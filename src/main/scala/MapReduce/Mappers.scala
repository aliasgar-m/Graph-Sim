package MapReduce

import org.slf4j.Logger
import java.io.IOException
import org.apache.hadoop.io.*
import org.apache.hadoop.mapred.*
import com.fasterxml.jackson.databind.*
import scala.jdk.CollectionConverters.*
import Utilities.{CreateLogger, LSCGSParams}

/** Provides the interface for the Mapper classes corresponding to Node and Edge Similarity Jobs.
 */
object Mappers:
  private val logger: Logger = CreateLogger(Mappers)

  /** Returns an integer value specifying if a value is similar to another.
   *
   * - Result = 1 : the two values are similar.
   *
   * - Result = 0 : the two values are not similar.
   *
   * @param val1 : Any : Represents the first value that must be compared.
   * @param val2 : Any : Represents the second value that must be compared.
   * @return an integer value either (0/1) representing whether a value is similar to another.
   */
  private def validate(val1: Any, val2: Any): Int =
    if val1 == val2 then 1 else 0
  end validate

  /** Returns the similarity value between two nodes.
   *
   * The function takes as input two nodes of type json and generates the similarity between the nodes.
   *
   * The parameters of the node taken into consideration include :
   *
   *  - No of children : Number of children of the node.
   *  - No of Properties : Number of properties of the node.
   *  - Max Branching Factor: Represents the maximum branching factor of the node.
   *  - Current Depth : Represents the depth / extent of the node.
   *
   * @param node1 : JsonNode : Represents a list of properties for the first node.
   * @param node2 : JsonNode : Represents a list of properties for the second node.
   * @return the similarity value between two nodes.
   */
  private def calculateNodeSimilarity(node1: JsonNode, node2: JsonNode): Double =
    val perPropsList: List[Double] = node1.elements.asScala.map(_.asDouble()).toList
    val orgPropsList: List[Double] = node2.elements.asScala.map(_.asDouble()).toList
    
    val similarity: Double =
      LSCGSParams.noOfChildrenWeight * validate(perPropsList.head, orgPropsList.head) +
      LSCGSParams.nodePropertiesWeight * validate(perPropsList(1), orgPropsList(1)) +
      LSCGSParams.maxBranchingFactorWeight * validate(perPropsList(2), orgPropsList(2)) +
      LSCGSParams.currentDepthWeight * validate(perPropsList(3), orgPropsList(3))
    similarity
  end calculateNodeSimilarity

  /** Returns the similarity value between two edges.
   *
   * The function takes as input two edges of type json and generates the similarity between the nodes.
   *
   * The parameters of the edge taken into consideration include :
   *
   *  - Edge Action Type: Represents the value corresponding to the edge.
   *  - Edge Cost : Represents the cost of the edge.
   *  - EdgeFromNode: Represents the node from which the edge is generated.
   *  - EdgeToNode : Represents the node to which the edge is incident.
   *
   * @param edge1 : JsonNode : Represents a list of properties for the first edge.
   * @param edge2 : JsonNode : Represents a list of properties for the second edge.
   * @return the similarity value between two edges.
   */
  private def calculateEdgeSimilarity(edge1: JsonNode, edge2: JsonNode): Double =
    val perEdgeList = edge1.elements().asScala.toList
    val orgEdgeList = edge2.elements().asScala.toList

    val similarity: Double =
      LSCGSParams.edgeActionTypeWeight * validate(perEdgeList.head.asInt(), orgEdgeList.head.asInt()) +
      LSCGSParams.edgeCostWeight * validate(perEdgeList(1).asDouble(), orgEdgeList(1).asDouble())
      LSCGSParams.edgeFromNodeWeight * calculateNodeSimilarity(perEdgeList(2), orgEdgeList(2)) +
      LSCGSParams.edgeToNodeWeight * calculateNodeSimilarity(perEdgeList(3), orgEdgeList(3))
    similarity
  end calculateEdgeSimilarity

  /** Provides a class for performing the mapper function for the Node Similarity Job.
   *
   * - The NodeMapper takes as input the key value pairs and provides a new mapped key value pair.
   *
   * - The input to the mapper is a JSON input of the node information that needs to be compared.
   *
   * @param k1 : LongWritable : Represents the datatype of the key for the input data to the mapper.
   * @param v1 : Text : Represents the datatype of the value for the input data to the mapper.
   * @param k2 : Text : Represents the datatype of the key for the output data of the mapper.
   * @param v2 : DoubleWritable : Represents the datatype of the value for the output data of the mapper.
   * @return a key value pair representing the result of the Mapper algorithm where each key represents the
   *         Perturbed Node ID while the values represent the similarity of the node.
   */
  class NodeMapper extends MapReduceBase with Mapper[LongWritable, Text, IntWritable, DoubleWritable]:
    @throws[IOException]
    override def map(key: LongWritable, value: Text,
                     output: OutputCollector[IntWritable, DoubleWritable], reporter: Reporter): Unit =
      val jsonString = value.toString

      val objectMapper = new ObjectMapper()
      val rootNode = objectMapper.readTree(jsonString)

      val nodePer = rootNode.get("nodePer").asInt()
      val nodeOrg = rootNode.get("nodeOrg").asInt()

      val perPropsArray = rootNode.get("perProps")
      val orgPropsArray = rootNode.get("orgProps")

      val similarity = calculateNodeSimilarity(perPropsArray, orgPropsArray)
      output.collect(new IntWritable(nodePer), new DoubleWritable(similarity))
    end map
  end NodeMapper

  /** Provides a class for performing the mapper function for the Edge Similarity Job.
   *
   * - The NodeMapper takes as input the key value pairs and provides a new mapped key value pair.
   *
   * - The input to the mapper is a JSON input corresponding to the edges that are present in a shard.
   *
   * @param k1 : LongWritable : Represents the datatype of the key for the input data to the mapper.
   * @param v1 : Text : Represents the datatype of the value for the input data to the mapper.
   * @param k2 : Text : Represents the datatype of the key for the output data of the mapper.
   * @param v2 : DoubleWritable : Represents the datatype of the value for the output data of the mapper.
   * @return a key value pair representing the result of the Mapper algorithm where each key represents the
   *         "From Node ID -> To Node ID" of the perturbed graph while the values represent the similarity of the
   *         edge.
   */
  class EdgeMapper extends MapReduceBase with Mapper[LongWritable, Text, Text, DoubleWritable]:
    @throws[IOException]
    override def map(key: LongWritable, value: Text,
                     output: OutputCollector[Text, DoubleWritable], reporter: Reporter): Unit =
      val jsonString = value.toString

      val objectMapper = new ObjectMapper()
      val rootNode = objectMapper.readTree(jsonString)

      val edgeDir = rootNode.get("nodeFromToPer").asText()
      val perEdgeVal = rootNode.get("perEdge")
      val orgEdgeVal = rootNode.get("orgEdge")

      val similarity = calculateEdgeSimilarity(perEdgeVal, orgEdgeVal)
      output.collect(new Text(edgeDir), new DoubleWritable(similarity))
    end map
  end EdgeMapper
end Mappers