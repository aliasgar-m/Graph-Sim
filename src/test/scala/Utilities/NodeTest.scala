package Utilities

import Utilities.Node
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import NetGraphAlgebraDefs.{NodeObject, Action}

/**
 *
 *
 *
 * @return
 */
class NodeTest extends AnyFlatSpec with Matchers {
  val node1: NodeObject = NodeObject(id = 1, children = 5, props = 10,
    propValueRange = 20, maxDepth = 5, maxBranchingFactor = 5, maxProperties = 10, 1)
  val node2: NodeObject = NodeObject(id = 2, children = 3, props = 5,
    propValueRange = 20, maxDepth = 10, maxBranchingFactor = 2, maxProperties = 4, 1)
  val edge12: Action = Action(actionType = 1, node1, node2, fromId = 1, toId = 2,
    resultingValue = Some(12), cost = 0.12)

  "Node Class" should "have correct input types" in {}
  it should "assign empty values if necessary" in {}
  it should "not return an empty object" in {}
  it should "create an object of Node type" in {}
}