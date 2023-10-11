package Preprocess

import scala.jdk.CollectionConverters.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.collection.parallel.immutable.ParMap
import NetGraphAlgebraDefs.{Action, NetGraph, NodeObject}
import com.google.common.graph.{MutableValueGraph, ValueGraphBuilder}

class DriverTest extends AnyFlatSpec with Matchers {
  val node1: NodeObject = NodeObject(id = 1, children = 5, props = 10,
    propValueRange = 20, maxDepth = 5, maxBranchingFactor = 5, maxProperties = 10, 1)
  val node2: NodeObject = NodeObject(id = 2, children = 3, props = 5,
    propValueRange = 20, maxDepth = 10, maxBranchingFactor = 2, maxProperties = 4, 1)
  val edge12: Action = Action(actionType = 1, node1, node2, fromId = 1, toId = 2,
    resultingValue = Some(12), cost = 0.12)

  val graph1: MutableValueGraph[NodeObject, Action] = ValueGraphBuilder.directed().build()
  graph1.addNode(node1)
  graph1.addNode(node2)
  graph1.putEdgeValue(node1, node2, edge12)

  val graph2: MutableValueGraph[NodeObject, Action] = ValueGraphBuilder.directed().build()
  graph2.addNode(node1)

  val testGraph: Option[NetGraph] = Option(NetGraph(graph1, node1))
  val nodes: Seq[NodeObject] = testGraph.get.sm.nodes().asScala.toSeq
  val instanceObject: Driver = new Driver()

  "Driver Apply Method" should "return result of type ParMap[Int, (NodeObject, Seq[Action])]." in {
    val testNodeProps = Driver.apply(testGraph)
    assert(testNodeProps.getClass == ParMap(node1.id -> (node1, Seq(edge12))).getClass, "Unmatched Class.")
    assert(testNodeProps == ParMap(node1.id -> (node1, Seq(edge12)), node2.id -> (node2, Seq(edge12))),
      s"Incorrect Output: $testNodeProps")}

  "Driver Class" should "return the edges of a node." in {
    val edges = instanceObject.obtainNodeEdges(node1, testGraph.get.sm)
    assert(edges == Seq(edge12), s"Incorrect Output: $edges")

    val edges_ = instanceObject.obtainNodeEdges(node2, testGraph.get.sm)
    assert(edges_ == Seq(edge12), s"Incorrect Output: $edges")
  }

  "Driver Apply Method"  should "return a node and empty sequence of edges when only one node with no edge is present." in {
    val testGraph: Option[NetGraph] = Option(NetGraph(graph2, node1))
    val testNodeProps = Driver.apply(testGraph)
    assert(testNodeProps == ParMap(node1.id -> (node1, Seq())), s"Incorrect Output: $testNodeProps")
  }

  "Driver Class" should "return empty sequence of edges when only one node with no edges is present." in {
    val testGraph: Option[NetGraph] = Option(NetGraph(graph2, node1))
    val edges = instanceObject.obtainNodeEdges(node1, testGraph.get.sm)
    assert(edges == Seq(), s"Incorrect Output: $edges")
  }
}