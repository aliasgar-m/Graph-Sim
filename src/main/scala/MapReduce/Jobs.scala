package MapReduce

import Utilities.LSCGSParams
import org.apache.hadoop.io.*
import org.apache.hadoop.fs.Path
import org.apache.hadoop.mapred.*
import scala.jdk.CollectionConverters.*
import Mappers.{NodeMapper, EdgeMapper}
import Reducers.{NodeReducer, EdgeReducer}


/** Object that allows to run the Map-Reduce jobs.
 */
object Jobs:
  /** Runs the Map-Reduce jobs corresponding to the Node Similarity and Edge Similarity Jobs.
   */
  def apply(): Unit =
    val NodeSim: JobConf = new JobConf(this.getClass)
    NodeSim.setJobName("Node Similarity")
    NodeSim.set("fs.defaultFS", "local")
    NodeSim.set("mapreduce.job.maps", "1")
    NodeSim.set("mapreduce.job.reduces", "1")
    NodeSim.setOutputKeyClass(classOf[IntWritable])
    NodeSim.setOutputValueClass(classOf[DoubleWritable])
    NodeSim.setMapperClass(classOf[NodeMapper])
    NodeSim.setReducerClass(classOf[NodeReducer])
    NodeSim.setInputFormat(classOf[TextInputFormat])
    NodeSim.setOutputFormat(classOf[TextOutputFormat[IntWritable, DoubleWritable]])
    FileInputFormat.setInputPaths(NodeSim, new Path(LSCGSParams.HDFSInputNodesFilePath))
    FileOutputFormat.setOutputPath(NodeSim, new Path(LSCGSParams.HDFSOutputNodesFilePath))
    JobClient.runJob(NodeSim)

    val EdgeSim: JobConf = new JobConf(this.getClass)
    EdgeSim.setJobName("Edge Similarity")
    EdgeSim.set("fs.defaultFS", "local")
    EdgeSim.set("mapreduce.job.maps", "1")
    EdgeSim.set("mapreduce.job.reduces", "1")
    EdgeSim.setOutputKeyClass(classOf[Text])
    EdgeSim.setOutputValueClass(classOf[DoubleWritable])
    EdgeSim.setMapperClass(classOf[EdgeMapper])
    EdgeSim.setReducerClass(classOf[EdgeReducer])
    EdgeSim.setInputFormat(classOf[TextInputFormat])
    EdgeSim.setOutputFormat(classOf[TextOutputFormat[Text, DoubleWritable]])
    FileInputFormat.setInputPaths(EdgeSim, new Path(LSCGSParams.HDFSInputEdgesFilePath))
    FileOutputFormat.setOutputPath(EdgeSim, new Path(LSCGSParams.HDFSOutputEdgesFilePath))
    JobClient.runJob(EdgeSim)
  end apply
end Jobs