name := """mirror-coding-challenge"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.8"

javacOptions ++= Seq(
  "-encoding", "UTF-8"
)

javaOptions in Universal ++= Seq(
  "-Dpidfile.path=/dev/null"
)

crossScalaVersions := Seq("2.11.12", "2.12.7")

libraryDependencies += guice
libraryDependencies += "com.auth0" % "java-jwt" % "3.8.0"
libraryDependencies += "com.pubnub" % "pubnub-gson" % "4.22.0-beta"

// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))
