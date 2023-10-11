package com.lsc

import org.slf4j.Logger
import Preprocess.CrossPairs
import MapReduce.{DataManager, Jobs}
import scala.collection.parallel.immutable.ParMap
import Utilities.{CreateLogger, FileLoader, LSCGSParams}
import NetGraphAlgebraDefs.{Action, NetGraph, NodeObject}

/** Entry point for the project */
object Main:
  private val logger: Logger = CreateLogger(Main)

  /** Returns accuracy of the Large Scale Computation program for Graph Similarity.
   * ==Overview==
   * The code runs in the following pattern:
   *
   * 1. Function loads the Original and Perturbed NetGraphs from the [[outputs]] folder located in the root folder.
   *
   * 2. Graphs are processed to generate the node and edge information.
   *
   * 3. Cross Pairs are created between the Original and Perturbed graphs particularly each Node of the Perturbed is
   * compared with every other node of the Original and similarly each Edge of the Perturbed is compared with that of
   * the Original.
   *
   * 4. Shards of the cross-node pairs and cross-edge pairs are created.
   *
   * 5. Any existing Output folder located in the HDFS folder is deleted.
   *
   * 6. Each shard is then converted into a JSON file that is saved in the HDFS folders and on the Local Machine
   * whose paths can be found from the ./src/main/resources/application.conf file.
   *
   * 7. After the shards have been saved as JSON files, the MapReduce job is started.
   *
   * 8.
   *
   * 9.
   *
   */
  def main(args: Array[String]): Unit =
    println("--------------------------------------------------------")
    println("--------Running the Large Scale Computation code--------")
    println("--------------------------------------------------------")

    logger.info("Obtaining the current directory in order to load the original and perturbed graphs.")
    val currDirectory = new java.io.File(".").getCanonicalPath
    logger.info(s"The current directory is: $currDirectory")

    logger.info(s"Loading Original Graph from $currDirectory${LSCGSParams.outputDirectory}${LSCGSParams.orgFileName}")
    val orgGraph: Option[NetGraph] = FileLoader(currDir = currDirectory,
      outDir = LSCGSParams.outputDirectory, fName = LSCGSParams.orgFileName)

    logger.info(s"Loading Perturbed Graph from $currDirectory${LSCGSParams.outputDirectory}${LSCGSParams.perFileName}")
    val perGraph: Option[NetGraph] = FileLoader(currDir = currDirectory,
      outDir = LSCGSParams.outputDirectory, fName = LSCGSParams.perFileName)

    if orgGraph.nonEmpty && perGraph.nonEmpty then
      logger.info("Original and Perturbed Graphs have been loaded.")
      logger.info("Pre-Processing the Original and Perturbed graphs to obtain node and edge information.")

      val orgNodeInformation: ParMap[Int, (NodeObject, Seq[Action])] = Preprocess.Driver(graph = orgGraph)
      logger.info(s"Obtained Node and Edge information for the Original Graph with ${orgNodeInformation.size} nodes.")

      val perNodeInformation: ParMap[Int, (NodeObject, Seq[Action])] = Preprocess.Driver(graph = perGraph)
      logger.info(s"Obtained Node and Edge information for the Perturbed Graph with ${perNodeInformation.size} nodes.")

      logger.info("Creating Node and Edge pairs between the Original and Perturbed Graphs.")
      val nodePairs: ParMap[(Int, Int), (NodeObject, NodeObject)] =
        CrossPairs.nodePairs(oNI = orgNodeInformation, pNI = perNodeInformation)
      logger.info(s"Obtained ${nodePairs.size} Node Pairs.")

      val edgePairs: ParMap[(Int, Int), Seq[(Action, Action)]] =
        CrossPairs.edgePairs(oNI = orgNodeInformation, pNI = perNodeInformation)
      logger.info(s"Obtained ${edgePairs.size} Edge Pairs.")

      logger.info("Creating shards of the Node and Edge Pairs.")
      val shardedNodePairs: Iterator[Map[(Int, Int), (NodeObject, NodeObject)]] = Sharder.Driver.shardNodes(nodePairs)
      val shardedEdgePairs: Iterator[Map[(Int, Int), Seq[(Action, Action)]]] = Sharder.Driver.shardEdges(edgePairs)
      logger.info("Shards created and received.")

      DataManager().deleteExistingHDFSFolder(LSCGSParams.HDFSOutputNodesFilePath)
      DataManager().deleteExistingHDFSFolder(LSCGSParams.HDFSOutputEdgesFilePath)
      logger.info(s"Deleting the Output Folder on the HDFS folder located at " +
        s"${LSCGSParams.HDFSOutputNodesFilePath} and ${LSCGSParams.HDFSOutputEdgesFilePath}")

      logger.info("Converting the nodes data to MapReduce compatible input and saving the data in JSON Files.")
      DataManager.storeNodeInformation(shardedNodePairs)
      logger.info("Converting the edges data to MapReduce compatible input and saving the data in JSON Files.")
      DataManager.storeEdgeInformation(shardedEdgePairs)

      logger.info("Now using these sharded data files with the MapReduce Algorithm to produce similarity pairs.")
      MapReduce.Jobs()

      logger.info("Now loading and rendering the results of the MapReduce algorithm to YAML files.")
//      val results =

      logger.info("Comparing the results of the Large Scale Computation YAML file and NetGameSim YAML file.")
//      val accuracy =

    else
      logger.error("Either the Original or Perturbed Graph is empty.")
      logger.error("Since either of the graphs are empty, the similarity cannot be computed.")
      logger.error("Exiting.")
  end main
end Main
