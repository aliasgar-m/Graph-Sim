package Preprocess


import org.slf4j.Logger
import Utilities.CreateLogger
import NetGraphAlgebraDefs.{Action, NodeObject}
import scala.collection.parallel.immutable.ParMap

/** This class provides utility functions for generating pairs of edges from two sets of actions.
 */
class CrossPairs():
  /** Returns the edge pairs between sequence of edges of the Perturbed and Original graph.
   *
   * This function generates pairs of edges by combining every
   * edge from the first sequence with every edge from the second sequence.
   *
   * @param pV The first sequence of edges representing the edges of a particular node of the Perturbed graph.
   * @param oV The first sequence of edges representing the edges of a particular node of the Original graph.
   * @return A sequence of edge pairs, each consisting of a pair of edges (one from pV and one from oV).
   */
  def generateEdgePairs(pV: Seq[Action], oV: Seq[Action]): Seq[(Action, Action)] =
    val orgEdges = oV
    val perEdges = pV
    val edgePair = perEdges.flatMap(pE => orgEdges.map(oE => (pE, oE)))
    edgePair
  end generateEdgePairs
end CrossPairs


/** The CrossPairs object provides utility functions for generating node and edge pairs.
 */
object CrossPairs:
  private val logger: Logger = CreateLogger(CrossPairs)
  private val instanceObject: CrossPairs = new CrossPairs()

  /** Returns a ParMap consisting of node pairs with each pair representing one node from the Perturbed
   * and the other from the Original Node Information.
   *
   * This function generates pairs of nodes by combining every node from the perturbed graph with
   * every node from the original graph. The keys of the resultant data structure represent
   * the IDs of the original and perturbed nodes while the values gives us a pair of the node information.
   *
   * @param oNI ParMap of node information for the original graph.
   * @param pNI ParMap of node information for the perturbed graph.
   * @return A ParMap of node pairs, each representing a pair of nodes.
   */
  def nodePairs(oNI: ParMap[Int, (NodeObject, Seq[Action])], pNI: ParMap[Int, (NodeObject, Seq[Action])]) =
    logger.info("Generating Node Pairs for every node of the Perturbed with every other node of the Original Graph.")
    val crossNodePairs = pNI.flatMap((pK, pV) => oNI.map((oK, oV) => (pK, oK) -> (pV(0), oV(0))))
    crossNodePairs

  /** Returns a ParMap consisting of edge pairs with each pair representing one edge from the Perturbed
   * and the other from the Original Edge Information.
   *
   * This function generates pairs of edges by combining every edge of a node from the perturbed graph with
   * every other edge of a node from the original graph. The keys of the resultant data structure represent
   * the IDs of the original and perturbed nodes while the values gives us a list of combinations between
   * each of their edges.
   *
   * @param oNI ParMap of node information for the original graph.
   * @param pNI ParMap of node information for the perturbed graph.
   * @return A ParMap of edge pairs, each representing a pair of edges.
   */
  def edgePairs(oNI: ParMap[Int, (NodeObject, Seq[Action])], pNI: ParMap[Int, (NodeObject, Seq[Action])]) =
    logger.info("Generating Edge Pairs for every node of the Perturbed with every other node of the Original Graph.")
    val crossEdgePairs = pNI.flatMap((pK, pV) =>
      oNI.map((oK, oV) =>
        (pK, oK) -> instanceObject.generateEdgePairs(pV(1), oV(1))))
    crossEdgePairs
  end edgePairs
end CrossPairs


