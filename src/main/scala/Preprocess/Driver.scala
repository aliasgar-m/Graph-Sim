package Preprocess

import org.slf4j.Logger
import Utilities.CreateLogger
import scala.jdk.CollectionConverters.*
import com.google.common.graph.EndpointPair
import scala.collection.parallel.immutable.ParMap
import scala.collection.parallel.CollectionConverters.*
import NetGraphAlgebraDefs.{NetGraph, NetStateMachine, NodeObject, Action}

/** Class that contains functions to allow computations on Nodes.
 */
class Driver:
  /** Returns the edges correspondent to a selected node.
   *
   * - The function first generates the edges incident to and from the selected node.
   *
   * - The incident edges can be looped to obtain the edge values.
   *
   * @param selectNode : [[NodeObject]] : Represents the node whose edges need to be obtained.
   * @param stateMachine : [[NetStateMachine]] : Represents the state machine that gives the current state
   *                     of the graph.
   * @return Returns the edge values for the edges incident to and from the selected node.
   */
  def obtainNodeEdges(selectNode: NodeObject, stateMachine: NetStateMachine): Seq[Action] =
    val incidentEdges: Set[EndpointPair[NodeObject]] = stateMachine.incidentEdges(selectNode).asScala.toSet

    val incidentEdgesValues: Seq[Action] = incidentEdges.map(edge =>
      stateMachine.edgeValue(edge.nodeU(), edge.nodeV()).get()).toSeq
    incidentEdgesValues
  end obtainNodeEdges
end Driver


/** Object that provides an interface to generate node information from a NetGraph.
 */
object Driver:
  private val instanceObject: Driver = new Driver()
  private val logger: Logger = CreateLogger(Driver)

  /** Returns the Node Information containing the Node and Edge Information.
   *
   * - This function takes as input an Option[NetGraph] object and generates the node Information.
   *
   * - The function obtains the nodes from the NetGraph by using the '''NetStateMachine.nodes''' function.
   *
   * - The edges can be obtained by generating the incident edges and looping across each edge to obtain its value.
   *
   * @param graph : Option[NetGraph] : NetGraph that represents the graph that needs to be preprocessed.
   * @return the Node Information of type : ParMap[Int, (NodeObject, Seq[Action])] : containing the Node and
   *         Edge information corresponding to each node. The keys represent the node IDs while the values represent
   *         (Node Information, Edge Information).
   */
  def apply(graph: Option[NetGraph]): ParMap[Int, (NodeObject, Seq[Action])] =
    val graphSM: NetStateMachine = graph.get.sm
    val nodes: Seq[NodeObject] = graphSM.nodes().asScala.toSeq

    logger.info(s"Generating node information for use in similarity.")
    val nodeInformation: ParMap[Int, (NodeObject, Seq[Action])] = nodes.map(node => node.id -> (
      node,
      instanceObject.obtainNodeEdges(selectNode = node, stateMachine = graphSM)
    )).toMap.par
    nodeInformation
  end apply
end Driver