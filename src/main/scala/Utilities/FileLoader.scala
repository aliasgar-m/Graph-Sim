package Utilities

import org.slf4j.Logger
import NetGraphAlgebraDefs.NetGraph

/** Accessor for the NetGraphs.
 *
 */
object FileLoader:
  private val logger: Logger = CreateLogger(FileLoader)

  /** Returns NetGraph of type Option{[[NetGraph]]} that can be used for graph similarity computation.
   *
   * @param currDir : String : The current directory of the user.
   * @param outDir : String : The output directory where the outputs of the NetGraph Module are stored.
   * @param fName : String : The file that contains the NetGraph that needs to be loaded.
   * @return Option{[[NetGraph]]} type which contains the Graph that has to be used for computation.
   */
  def apply(currDir: String, outDir: String, fName: String): Option[NetGraph] =
    val graphFound = java.io.File(currDir + outDir + fName).exists

    val graphRawData = if graphFound then
      NetGraph.load(fileName = fName, dir = currDir + outDir)
    else
      logger.error(s"\"$fName\" not found. Please check the directory / filename again")
      None
    graphRawData
  end apply
end FileLoader