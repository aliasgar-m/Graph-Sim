package MapReduce

import java.io.File
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DataManagerTest extends AnyFlatSpec with Matchers {
  val instanceObject : DataManager = new DataManager()

  "DataManager Class" should "create a new directory on local machine" in {
    val currDirectory = new java.io.File(".").getCanonicalPath
    instanceObject.createNewFolder(currDirectory + "/trial/")
    assert(File(currDirectory + "/trial/").exists() === true, "Directory not created.")
  }
  it should "delete the new directory from the local machine." in {
    val currDirectory = new java.io.File(".").getCanonicalPath
    instanceObject.deleteExistingLocalFolder(currDirectory + "/trial/")
    assert(File(currDirectory + "/trial/").exists() === false, "Directory not deleted.")
  }
  it should "create a new directory on HDFS" in {
    val dir = "/usr/local/Hadoop/hdfs/inputData/nodes"
    instanceObject.createNewFolder(dir)
    assert(File(dir).exists() === true, "Directory not created on HDFS.")
  }
  it should "delete the new directory from the HDFS folder." in {
    val dir = "/usr/local/Hadoop/hdfs/inputData/nodes"
    instanceObject.deleteExistingHDFSFolder(dir)
    assert(File(dir).exists() === false, "Directory not deleted from HDFS.")
  }
}