package MapReduce

import ujson.*
import java.io.*
import org.slf4j.Logger
import DataManager.logger
import org.apache.hadoop.conf.*
import org.apache.commons.io.FileUtils
import scala.jdk.CollectionConverters.*
import Utilities.{CreateLogger, LSCGSParams}
import org.apache.hadoop.fs.{FileSystem, Path}
import NetGraphAlgebraDefs.{Action, NodeObject}


/** Class that provides utility functions to make the node and edge information compatible for sending to
 * the Map-Reduce algorithm.
 */
class DataManager():
  /** Returns a json Array of the Node Properties that will be used for comparison.
   *
   * @param node : [[NodeObject]] : Node whose properties will be generated as a json Array.
   * @return returns the properties of the node that will be used for similarity generation.
   */
  private def readNodeObject(node: NodeObject): ujson.Arr =
    ujson.Arr(node.children, node.props, node.maxBranchingFactor, node.currentDepth)
  end readNodeObject

  /** Returns a json Array of the Edge Properties that will be used for comparison.
   *
   * @param action : [[Action]] : Edge whose properties will be generated as a json Array.
   * @return returns the properties of the edge that will be used for similarity generation.
   */
  private def readEdgeObject(action: Action): ujson.Arr =
    ujson.Arr(action.actionType, action.cost, readNodeObject(action.fromNode), readNodeObject(action.toNode))
  end readEdgeObject

  /** Function that allows to delete a folder on the local system.
   *
   * @param dirPath : String : The directory path that needs to be deleted.
   * @return None
   */
  def deleteExistingLocalFolder(dirPath: String): Unit =
    val directoryObj = new File(dirPath)
    FileUtils.deleteDirectory(directoryObj)
    logger.info(s"Deleted existing Directories at $dirPath")
  end deleteExistingLocalFolder

  /** Function that allows to delete a folder on the HDFS system.
   *
   * @param dirPath : String : The directory path that needs to be deleted.
   * @return None
   */
  def deleteExistingHDFSFolder(dirPath: String): Unit =
    val config = new Configuration()
    val fs = FileSystem.get(config)
    val outPutPath = new Path(dirPath)
    fs.delete(outPutPath, true)
  end deleteExistingHDFSFolder

  /** Function that allows to create a folder on the local / HDFS system.
   *
   * @param dirPath : String : The directory path that needs to be created.
   * @return None
   */
  def createNewFolder(dirPath: String): Unit =
    val directoryObj = new File(dirPath)
    FileUtils.forceMkdir(directoryObj)
    logger.info(s"Created new Directory at $dirPath.")
  end createNewFolder

  /** Function that generates the JSON Equivalents of the shard of the Node data.
   *
   * @param shard : Map[(Int, Int), (NodeObject, NodeObject)] : The shard of data that need to
   * be converted into JSON Equivalents.
   * @return a JSON Equivalent of a shard.
   */
  def generateJSONEquivalentNode(shard: Map[(Int, Int), (NodeObject, NodeObject)]) =
    val jSONEquivalent = shard.map((key, value) =>
      ujson.Obj(
      "nodePer" -> ujson.Num(key(0)),
      "nodeOrg" -> ujson.Num(key(1)),
      "perProps" -> readNodeObject(value(0)),
      "orgProps" -> readNodeObject(value(1))
    ))
    jSONEquivalent
  end generateJSONEquivalentNode

  /** Function that generates the JSON Equivalents of the shard of the Edge data.
   *
   * @param shard : Map[(Int, Int), Seq[(Action, Action)]] : The shard of data that need to
   *              be converted into JSON Equivalents.
   * @return a JSON Equivalent of a shard of edge data.
   */
  def generateJSONEquivalentEdge(shard: Map[(Int, Int), Seq[(Action, Action)]]) =
    val jSONEquivalent = shard.map((key, value: Seq[(Action, Action)]) =>
      value.map(edgePair => ujson.Obj(
          "nodePer" -> ujson.Num(key(0)),
          "nodeOrg" -> ujson.Num(key(1)),
          "nodeFromToPer" -> ujson.Str(s"${edgePair(0).fromNode.id}->${edgePair(0).toNode.id}"),
          "nodeFromToOrg" -> ujson.Str(s"${edgePair(1).fromNode.id}->${edgePair(1).toNode.id}"),
          "perEdge" -> readEdgeObject(edgePair(0)),
          "orgEdge" -> readEdgeObject(edgePair(1))
        )))
      jSONEquivalent
  end generateJSONEquivalentEdge

  /** Function that allows to store the shards on the local system.
   *
   * @param shards : Iterable[ujson.Obj] : An Iterator across the JSON Equivalents of the shards.
   * @param index : Int : Integer value representing the shard number.
   * @param fPath : String : Represents the path where the shards will be saved.
   * @return None
   */
  def storeDataLocal(shards: Iterable[ujson.Obj], index: Int, fPath: String): Unit =
    val writer = new PrintWriter(fPath)
    shards.foreach(entry => {
      writer.print(entry)
      writer.print("\n")
    })
    writer.close()
  end storeDataLocal

  /** Function that allows to store the shards on the HDFS system.
   *
   * @param shards : Iterable[ujson.Obj] : An Iterator across the JSON Equivalents of the shards.
   * @param index  : Int : Integer value representing the shard number.
   * @param fPath  : String : Represents the path where the shards will be saved.
   * @return None
   */
  def storeDataHDFS(shards: Iterable[ujson.Obj], index: Int, fPath: String): Unit =
    val config = new Configuration()
    val fs = FileSystem.get(config)
    val output = fs.create(new Path(fPath))
    val writer = new PrintWriter(output)

    shards.foreach(entry => {
      writer.print(entry)
      writer.print("\n")
    })
    writer.close()
  end storeDataHDFS


/** Object that provides an interface to run the DataManager that will store the Node and Edge
 * Information in JSON Equivalent that can sent to the Map-Reduce algorithm.
 */
object DataManager:
  private val logger: Logger = CreateLogger(DataManager)
  private val instanceObject: DataManager = new DataManager()

  /** Function that takes steps to store th sharded node data into JSON Equivalents on the
   * local and HDFS System.
   *
   * @param pairs : Iterator[Map[(Int, Int), (NodeObject, NodeObject)]] : An iterator
   *              over the Cross Node Pairs which can be used to access the shards.
   * @return None
   */
  def storeNodeInformation(pairs: Iterator[Map[(Int, Int), (NodeObject, NodeObject)]]): Unit =
    val currDir = new java.io.File(".").getCanonicalPath

    instanceObject.deleteExistingLocalFolder(currDir + LSCGSParams.LocalInputNodesFilePath)
    instanceObject.createNewFolder(currDir + LSCGSParams.LocalInputNodesFilePath)

    instanceObject.deleteExistingLocalFolder(currDir + LSCGSParams.LocalOutputNodesFilePath)
    instanceObject.createNewFolder(currDir + LSCGSParams.LocalOutputNodesFilePath)

    instanceObject.deleteExistingHDFSFolder(LSCGSParams.HDFSInputNodesFilePath)
    instanceObject.createNewFolder(LSCGSParams.HDFSInputNodesFilePath)

    val data = pairs.zipWithIndex.map((shard, index) => {instanceObject.generateJSONEquivalentNode(shard)})

    data.zipWithIndex.foreach((shard, index) => {
      instanceObject.storeDataLocal(shard, index, currDir + LSCGSParams.LocalInputNodesFilePath +
        s"/shard_${index}.json")
      logger.info(s"Created File containing the sharded data on the local machine at " +
        s"${currDir + LSCGSParams.LocalInputNodesFilePath}")

      instanceObject.storeDataHDFS(shard, index, LSCGSParams.HDFSInputNodesFilePath + s"/shard_${index}.json")
      logger.info(s"Created File containing the sharded data on HDFS at " +
        s"${currDir + LSCGSParams.HDFSInputNodesFilePath}")
    })
  end storeNodeInformation

  /** Function that takes steps to store th sharded edge data into JSON Equivalents on the
   * local and HDFS System.
   *
   * @param pairs : Iterator[Map[(Int, Int), Seq[(Action, Action)]]] : An iterator
   *              over the Cross Edge Pairs which can be used to access the shards.
   * @return None
   */
  def storeEdgeInformation(pairs: Iterator[Map[(Int, Int), Seq[(Action, Action)]]]): Unit =
    val currDir = new java.io.File(".").getCanonicalPath

    instanceObject.deleteExistingLocalFolder(currDir + LSCGSParams.LocalInputEdgesFilePath)
    instanceObject.createNewFolder(currDir + LSCGSParams.LocalInputEdgesFilePath)

    instanceObject.deleteExistingLocalFolder(currDir + LSCGSParams.LocalOutputEdgesFilePath)
    instanceObject.createNewFolder(currDir + LSCGSParams.LocalOutputEdgesFilePath)

    instanceObject.deleteExistingHDFSFolder(LSCGSParams.HDFSInputEdgesFilePath)
    instanceObject.createNewFolder(LSCGSParams.HDFSInputEdgesFilePath)

    val data = pairs.map(shard => instanceObject.generateJSONEquivalentEdge(shard))

    data.zipWithIndex.foreach((shard, index) => {
      instanceObject.storeDataLocal(shard.flatten, index, currDir + LSCGSParams.LocalInputEdgesFilePath +
        s"/shard_${index}.json")
      logger.info(s"Created File containing the sharded data on the local machine at " +
        s"${currDir + LSCGSParams.LocalInputEdgesFilePath}")

      instanceObject.storeDataHDFS(shard.flatten, index, LSCGSParams.HDFSInputEdgesFilePath +
        s"/shard_${index}.json")
      logger.info(s"Created File containing the sharded data on HDFS at " +
        s"${currDir + LSCGSParams.HDFSInputEdgesFilePath}")
    })
  end storeEdgeInformation
end DataManager