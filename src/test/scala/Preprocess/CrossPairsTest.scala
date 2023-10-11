package Preprocess

import Preprocess.{Driver, CrossPairs}
import scala.jdk.CollectionConverters.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.collection.parallel.immutable.ParMap
import NetGraphAlgebraDefs.{NodeObject, Action, NetGraph}
import com.google.common.graph.{MutableValueGraph, ValueGraphBuilder}

class CrossPairsTest extends AnyFlatSpec with Matchers {
  val node1: NodeObject = NodeObject(id = 1, children = 5, props = 10,
    propValueRange = 20, maxDepth = 5, maxBranchingFactor = 5, maxProperties = 10, 1)
  val node2: NodeObject = NodeObject(id = 2, children = 3, props = 5,
    propValueRange = 20, maxDepth = 10, maxBranchingFactor = 2, maxProperties = 4, 1)
  val node3: NodeObject = NodeObject(id = 3, children = 4, props = 3,
    propValueRange = 20, maxDepth = 4, maxBranchingFactor = 7, maxProperties = 15,1)
  val edge12: Action = Action(actionType = 1, node1, node2, fromId = 1, toId = 2,
    resultingValue = Some(12), cost = 0.12)
  val edge23: Action = Action(actionType = 2, node2, node3, fromId = 2, toId = 3,
    resultingValue = Some(23), cost = 0.23)

  val graph1: MutableValueGraph[NodeObject, Action] = ValueGraphBuilder.directed().build()
  graph1.addNode(node1)
  graph1.addNode(node2)
  graph1.putEdgeValue(node1, node2, edge12)

  val testGraph: Option[NetGraph] = Option(NetGraph(graph1, node1))
  val nodes: Seq[NodeObject] = testGraph.get.sm.nodes().asScala.toSeq
  val instanceObject: Driver = new Driver()
  val nodeProperties: ParMap[Int, (NodeObject, Seq[Action])] = Driver(testGraph)

  "Cross Pairs object" should "return Cross Node Pairs." in {
    val nodePairs = CrossPairs.nodePairs(nodeProperties, nodeProperties)
    assert(nodePairs == ParMap((node1.id, node1.id) -> (node1, node1), (node1.id, node2.id) -> (node1, node2),
                               (node2.id, node1.id) -> (node2, node1), (node2.id, node2.id) -> (node2, node2)),
          s"Incorrect Output. $nodePairs")
  }
  "Cross Pairs object" should "return Cross Edge Pairs." in {
    val edgePairs = CrossPairs.edgePairs(nodeProperties, nodeProperties)
    assert(edgePairs == ParMap((node1.id, node1.id) -> List((edge12, edge12)),
                               (node1.id, node2.id) -> List((edge12, edge12)),
                               (node2.id, node1.id) -> List((edge12, edge12)),
                               (node2.id, node2.id) -> List((edge12, edge12))),
      s"Incorrect Output. $edgePairs")
  }
  "Cross Pairs class" should "return Edge Pairs." in {
    val edgePairs = CrossPairs().generateEdgePairs(Seq(edge12), Seq(edge12))
    assert(edgePairs == Seq((edge12, edge12)), s"Incorrect Output. $edgePairs")
  }
}