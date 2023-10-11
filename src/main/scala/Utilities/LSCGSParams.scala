package Utilities

import com.typesafe.config.{Config, ConfigFactory}

/** Entry point to access the configuration variables stored in
 *  ./src/main/resources/application.conf
 */
object LSCGSParams:
  private val config: Config = ConfigFactory.load()

  val outputDirectory: String = config.getString("LscgsApp.outputDirectory")
  val orgFileName: String = config.getString("LscgsApp.orgFileName")
  val perFileName: String = config.getString("LscgsApp.perturbedFileName")

  val minRawDataLength: Int = config.getInt("LscgsApp.Sharder.minRawDataLength")
  val minNoOfShards: Int = config.getInt("LscgsApp.Sharder.minNoOfShards")
  val maxNoOfShards: Int = config.getInt("LscgsApp.Sharder.maxNoOfShards")

  val minRawDataLength_DEFAULT: Int = config.getInt("LscgsApp.Sharder.minRawDataLength_DEFAULT")
  val minNoOfShards_DEFAULT: Int = config.getInt("LscgsApp.Sharder.minNoOfShards_DEFAULT")
  val maxNoOfShards_DEFAULT: Int = config.getInt("LscgsApp.Sharder.maxNoOfShards_DEFAULT")
  val shardingStep_DEFAULT: Int = config.getInt("LscgsApp.Sharder.shardingStep_DEFAULT")

  val HDFSInputNodesFilePath: String = config.getString("LscgsApp.MapReduce.HDFSInputNodesFilePath")
  val HDFSOutputNodesFilePath: String = config.getString("LscgsApp.MapReduce.HDFSOutputNodesFilePath")

  val LocalInputNodesFilePath: String = config.getString("LscgsApp.MapReduce.LocalInputNodesFilePath")
  val LocalOutputNodesFilePath: String = config.getString("LscgsApp.MapReduce.LocalOutputNodesFilePath")

  val HDFSInputEdgesFilePath: String = config.getString("LscgsApp.MapReduce.HDFSInputEdgesFilePath")
  val HDFSOutputEdgesFilePath: String = config.getString("LscgsApp.MapReduce.HDFSOutputEdgesFilePath")

  val LocalInputEdgesFilePath: String = config.getString("LscgsApp.MapReduce.LocalInputEdgesFilePath")
  val LocalOutputEdgesFilePath: String = config.getString("LscgsApp.MapReduce.LocalOutputEdgesFilePath")

  val noOfChildrenWeight: Double = config.getDouble("LscgsApp.SimilarityModel.noOfChildrenWeight")
  val nodePropertiesWeight: Double = config.getDouble("LscgsApp.SimilarityModel.nodePropertiesWeight")
  val maxBranchingFactorWeight: Double = config.getDouble("LscgsApp.SimilarityModel.maxBranchingFactorWeight")
  val currentDepthWeight: Double = config.getDouble("LscgsApp.SimilarityModel.currentDepthWeight")

  val edgeActionTypeWeight: Double = config.getDouble("LscgsApp.SimilarityModel.edgeActionTypeWeight")
  val edgeCostWeight: Double = config.getDouble("LscgsApp.SimilarityModel.edgeCostWeight")
  val edgeFromNodeWeight: Double = config.getDouble("LscgsApp.SimilarityModel.edgeFromNodeWeight")
  val edgeToNodeWeight: Double = config.getDouble("LscgsApp.SimilarityModel.edgeToNodeWeight ")
end LSCGSParams