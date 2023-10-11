package Utilities

import NetGraphAlgebraDefs.NetGraph
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/**
 *
 *
 *
 * @return
 */
class FileLoaderTest extends AnyFlatSpec with Matchers {
  val fileName: String = "NetGraph_22-09-23-18-10-37.ngs"
  val currentDirectory: String = System.getProperty("user.dir")
  val outputDirectory: String = "/outputs/5_nodes/"

  val graph: Option[NetGraph] = FileLoader(currentDirectory, outputDirectory, fileName)

  "FileLoader" should "not be sent an empty input" in {
    assert(fileName.isEmpty === false, ": Input sent is empty.")
  }

  it should "return None when the file is not found." in {
    val fileName = "NetGraph_22-09-23-18-10-38.ngs"
    val graph = FileLoader(currentDirectory, outputDirectory, fileName)
    assert(graph.getClass == None.getClass, ": None type not returned despite file not being found.")
    assert(graph.isEmpty === true, ": Empty Graph not sent.")
  }

  it should "not return an empty NetGraph." in {
    assert(graph.getClass == Some(1).getClass, ": Graph not loaded. None type returned.")
    assert(graph.nonEmpty === true, ": NetGraph is empty.")
  }

//  solve this problem. None.get does not exists. Tests should fail not give an error of type.
//  it should "return a NetGraph when the file is found." in {
//    assert(graph.get.getClass == classOf[NetGraph], ": NetGraph not returned.")
//  }
}