import sbtscalaxb.ScalaxbPlugin.autoImport._

lazy val commonSettings = Seq(
  organization  := "com.yourspraveen",
  scalaVersion  := "2.11.8"
)

lazy val scalaXml = "org.scala-lang.modules" %% "scala-xml" % "1.0.6"
lazy val scalaParser = "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.5"
lazy val dispatchV = "0.11.3"
lazy val dispatch = "net.databinder.dispatch" %% "dispatch-core" % dispatchV

//logLevel := Level.Debug

dependencyOverrides += "org.scala-lang" % "scala-library" % scalaVersion.value

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  enablePlugins(ScalaxbPlugin).
  settings(
    name := "scalaxb-Example",
    libraryDependencies ++= Seq(dispatch),
    libraryDependencies ++= {
      if (scalaVersion.value startsWith "2.11") Seq()
      else Seq(scalaXml, scalaParser)
    }).
  settings(
    scalaxbDispatchVersion in(Compile, scalaxb) := dispatchV,
    scalaxbPackageName in(Compile, scalaxb) := "com.yourspraveen",
    // scalaxbPackageNames in (Compile, scalaxb)    := Map(uri("http://schemas.microsoft.com/2003/10/Serialization/") -> "microsoft.serialization"),
    logLevel in(Compile, scalaxb) := Level.Debug
  )