package Shard

//import Preprocess.Processor
import scala.jdk.CollectionConverters.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import NetGraphAlgebraDefs.{NodeObject, Action, NetGraph}
import com.google.common.graph.{MutableValueGraph, ValueGraphBuilder}

class SharderTest extends AnyFlatSpec with Matchers {
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

  "" should "" in {}

}

// wrong input values from user in application conf
// 0 step size
// proper step size generated
// iterators created for nodes and edges.