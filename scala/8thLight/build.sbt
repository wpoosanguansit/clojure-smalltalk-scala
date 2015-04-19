import AssemblyKeys._

name                := "8thLight"

version             := "1.0"

scalaVersion        := "2.11.5"

resolvers ++= Seq(
  "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
  "opennlp sourceforge repo" at "http://opennlp.sourceforge.net/maven2"
)

libraryDependencies ++= Seq(
  "org.scalaz" 		          %% "scalaz-core" 			  % "7.1.0",
  "org.scalaz.stream"       %% "scalaz-stream" 		  % "0.6a",
  "com.github.scopt"        %% "scopt"              % "3.3.0",
  "org.apache.opennlp"      % "opennlp-tools"       % "1.5.2-incubating"
)

scalacOptions       += "-feature"

initialCommands in console :=
  """import scalaz._, Scalaz._
    |import scalaz.stream._
    |import scalaz.concurrent.Task
    |import scala.collection.immutable.TreeMap
    |import java.util.concurrent.atomic.AtomicInteger
    |import scala.collection.mutable.{Set => MSet}
  """.stripMargin

jarName   in assembly := "solution.jar"

mainClass in assembly := Some("com.eighthlight.solution.Main")