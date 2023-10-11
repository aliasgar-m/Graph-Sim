Global / excludeLintKeys += test / fork
Global / excludeLintKeys += run / mainClass

val scala3Version = "3.3.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "PROJECT1",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.4.7",
      "com.typesafe" % "config" % "1.4.2",
      "org.scalactic" %% "scalactic" % "3.2.17",
      "org.scalatest" %% "scalatest" % "3.2.17" % "test",
      "com.lihaoyi" %% "upickle" % "3.1.3"
    ),

    libraryDependencies ++= Seq(
      "org.apache.hadoop" % "hadoop-common" % "3.3.6",
      "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "3.3.6",
      "org.apache.hadoop" % "hadoop-mapreduce-client-jobclient" % "3.3.6"
    )
  )

compileOrder := CompileOrder.JavaThenScala
test / fork := true
run / fork := true
//run / javaOptions ++= Seq(
//  "-Xms8G",
//  "-Xmx100G",
//  "-XX:+UseG1GC"
//)

Compile / mainClass := Some("com.lsc.Main")
test / mainClass := Some("com.lsc.Main")
run / mainClass := Some("com.lsc.Main")