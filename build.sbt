name := "xevepro"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "com.espertech" % "esper" % "5.2.0",
  "joda-time" % "joda-time" % "2.7",
  "org.joda" % "joda-convert" % "1.7"
)

lazy val xevepro = (project in file(".")).
  enablePlugins(BuildInfoPlugin).
  settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion),
    buildInfoPackage := "fi.aalto.xevepro.helper.buildinfo"
  )
    