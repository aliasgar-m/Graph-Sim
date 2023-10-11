package Sharder

import Driver.logger
import org.slf4j.Logger
import Utilities.{CreateLogger, LSCGSParams}
import NetGraphAlgebraDefs.{Action, NodeObject}
import scala.collection.parallel.immutable.ParMap

/** Class that contains functions to create shards of the data provided as input.
 */
class Driver():
  /** Returns boolean value to determine whether a value is greater than 0.
   *
   * The [[validityCheck]] allows to check if the configuration parameters
   * added by the user are valid and greater than 0.
   *
   * @param checkVal : Int : Int value that represents the parameters of the Sharder found in
   *                 ./src/main/resources/application.conf
   *
   * @return boolean value that represents the validity of the user entered input.
   */
  def validityCheck(checkVal: Int): Boolean =
    if checkVal > 0 then true else false

  /** Returns the minimum number of shards that need to be created.
   *
   * - The function performs a validity check on the user entered input in the
   * ./src/main/resources/application.conf file.
   *
   * - If the [[validityCheck]] function returns a boolean '''True''' then we the value for the [[minNoOfShards]]
   * is equal to the input provided by the user in '''LSCGSApp.Sharder''' in the config file otherwise a
   * preset '''DEFAULT value''' is used.
   *
   * @return the minimum number of shards that the data must be split into.
   */
  def getMinShards: Int =
    if validityCheck(LSCGSParams.minNoOfShards) then LSCGSParams.minNoOfShards else
      logger.info(s"Minimum number of shards set by user = ${LSCGSParams.minNoOfShards}")
      logger.info(s"Using Default Value = ${LSCGSParams.minNoOfShards_DEFAULT}")
      LSCGSParams.minNoOfShards_DEFAULT

  /** Returns the maximum number of shards that need to be created.
   *
   * - The function performs a validity check on the user entered input in the
   * ./src/main/resources/application.conf file.
   *
   * - If the [[validityCheck]] function returns a boolean '''True''' then we the value for the [[maxNoOfShards]]
   * is equal to the input provided by the user in '''LSCGSApp.Sharder''' in the config file otherwise a
   * preset '''DEFAULT value''' is used.
   *
   * @return the maximum number of shards that the data must be split into.
   */
  def getMaxShards: Int =
    if validityCheck(LSCGSParams.maxNoOfShards) then LSCGSParams.maxNoOfShards else
      logger.info(s"Maximum number of shards set by user = ${LSCGSParams.maxNoOfShards}")
      logger.info(s"Using Default Value = ${LSCGSParams.maxNoOfShards_DEFAULT}")
      LSCGSParams.maxNoOfShards_DEFAULT

  /** Returns the minimum length of the raw data.
   *
   * - The function performs a validity check on the user entered input in the
   * ./src/main/resources/application.conf file.
   *
   * - If the [[validityCheck]] function returns a boolean '''True''' then we the value for the [[minRawDataLength]]
   * is equal to the input provided by the user in '''LSCGSApp.Sharder''' in the config file otherwise a
   * preset '''DEFAULT value''' is used.
   *
   * @return the min length of the raw input data provided to the sharder.
   */
  def getMinRawDataLength: Int =
    if validityCheck(LSCGSParams.minRawDataLength) then LSCGSParams.minRawDataLength else
      logger.info(s"Minimum data length set by user = ${LSCGSParams.minRawDataLength}")
      logger.info(s"Using Default Value = ${LSCGSParams.minRawDataLength_DEFAULT}")
      LSCGSParams.minRawDataLength_DEFAULT

  /** Returns the step that must be used to shard the input data.
   *
   * - The function generates the sharding step based on the minRawDataLength, minShards, and maxShards.
   *
   * - If the input data length is less that the minimum raw data length provided in the application.conf
   * file, the sharding step is equal to the input data length divided by the minimum number of shards.
   *
   * - If the above criteria is not met, then the step size is taken as the input data length divide by
   * the maximum number of shards.
   *
   * @param dataLength : Int : Represents the length of the input data that needs to be sharded.
   * @return an Integer that represents the step that must be used to shard the data.
   */
  def generateShardingStep(dataLength: Int): Int =
    if dataLength <= getMinRawDataLength then
      dataLength / getMinShards
    else
      dataLength / getMaxShards
  end generateShardingStep
end Driver

/** Object that provides methods to shard the Nodes and Edges.
 */
object Driver:
  private val instanceObject: Driver = new Driver()
  private val logger: Logger = CreateLogger(Driver)

  /** Returns an iterator object over the Node Pairs that can be used to access the sharded data.
   *  
   *  The functions takes the Node Pairs that need to be sharded, generates the sharding step based on the
   *  size of the input data, and then shards the data.
   *
   * @param nodePairs : ParMap[(Int, Int), (NodeObject, NodeObject)] : A Parallel Map that represents the nodes
   *                  and their information. The Keys (Int, Int) represents (Perturbed graph node ID, Original 
   *                  graph node ID) while the values represents (Perturbed graph node, Original graph node).
   * @return An iterator that loops through shards of the Node Pairs.
   */
  def shardNodes(nodePairs: ParMap[(Int, Int), (NodeObject, NodeObject)]) =
    logger.info("Generating Shards for Node Pairs.")
    val rawDataLength: Int = nodePairs.size
    val shardingStep: Int = instanceObject.generateShardingStep(rawDataLength)
    val shards: Iterator[Map[(Int, Int), (NodeObject, NodeObject)]] = nodePairs.seq.grouped(shardingStep)
    shards
  end shardNodes

  /** Returns an iterator object over the Edge Pairs that can be used to access the sharded data.
   *
   * The functions takes the Edge Pairs that need to be sharded, generates the sharding step based on the
   * size of the input data, and then shards the data.
   *
   * @param nodePairs : ParMap[(Int, Int), Seq[(Action, Action)]] : A Parallel Map that represents the edges
   *                  and their information. The Keys (Int, Int) represents (Perturbed graph node ID, Original 
   *                  graph node ID) while the values represents Seq(Perturbed graph edge, Original graph edge).
   * @return An iterator that loops through shards of the Edge Pairs.
   */
  def shardEdges(edgePairs: ParMap[(Int, Int), Seq[(Action, Action)]]) =
    logger.info("Generating Shards for Edge Pairs.")
    val rawDataLength: Int = edgePairs.size
    val shardingStep: Int = instanceObject.generateShardingStep(rawDataLength)
    val shards: Iterator[Map[(Int, Int), Seq[(Action, Action)]]] = edgePairs.seq.grouped(shardingStep)
    shards
  end shardEdges
end Driver
