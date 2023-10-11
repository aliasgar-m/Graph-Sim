package Preprocess

import Preprocess.Processor
import scala.jdk.CollectionConverters.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import NetGraphAlgebraDefs.{NodeObject, Action, NetGraph}
import com.google.common.graph.{MutableValueGraph, ValueGraphBuilder}

/**
 *
 * @param
 * @param
 * @return
 */
class ProcessorTest extends AnyFlatSpec with Matchers {
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
  val instanceObject: Processor = new Processor()

  "Processor Apply Method" should "return result of type Map[Int, Node]." in {}
  "Processor Class" should "return the properties of a node." in {}
  it should "return the neighbors of a node." in {}
  it should "return the edges incident to/from a node." in {}

  "Processor Apply Method"  should "return () when only one node is present." in {}
  "Processor Class" should "return () neighbors when only one node is present" in {}
  it should "return () edges when only one node is present" in {}
}