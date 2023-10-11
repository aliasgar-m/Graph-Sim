package Sharder

import Sharder.Driver
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import NetGraphAlgebraDefs.{Action, NetGraph, NodeObject}
import Utilities.LSCGSParams
import com.google.common.graph.{MutableValueGraph, ValueGraphBuilder}

class DriverTest extends AnyFlatSpec with Matchers {
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

  val instanceObject : Driver = new Driver()
  
  "Driver Class" should "perform a validity check when required." in {
    assert(instanceObject.validityCheck(-1) === false)
    assert(instanceObject.validityCheck(50) === true)
  }
  it should "use user provided minimum number of shards when proper input provided." in {
    assert(instanceObject.getMinShards == 5)
  }
  it should "use user provided maximum number of shards when proper input provided." in {
    assert(instanceObject.getMaxShards == 10)
  }
  it should "use user provided raw data length when proper input provided." in {
    assert(instanceObject.getMinRawDataLength == 500)
  }
  it should "generate sharding step when the data length = 50." in {
    assert(instanceObject.generateShardingStep(50) == 10)
  }
}