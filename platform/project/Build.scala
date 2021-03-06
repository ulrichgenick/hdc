import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "hdc"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "org.mongodb" % "mongo-java-driver" % "2.11.3",
    "org.elasticsearch" % "elasticsearch" % "0.90.5"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
