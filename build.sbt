name := "s3mock"

version := "0.2.7"

organization := "thecoderatekid"

scalaVersion in ThisBuild := "2.13.2"
crossScalaVersions in ThisBuild := Seq("2.11.12", "2.12.10", "2.13.2")

val akkaVersion = "2.5.31"
val akkaHttpVersion = "10.1.12"

licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
homepage := Some(url("https://github.com/findify/s3mock"))

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"                   % akkaVersion,
  "com.typesafe.akka" %% "akka-stream"                  % akkaVersion,
  "com.typesafe.akka" %% "akka-http"                    % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-core"               % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-xml"                % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-parsing"                 % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream-testkit"          % akkaVersion    % Test,
  "org.scala-lang.modules" %% "scala-xml"               % "1.3.0",
  "org.scala-lang.modules" %% "scala-collection-compat" % "2.1.6",
  "com.github.pathikrit" %% "better-files"              % "3.9.1",
  "com.typesafe.scala-logging" %% "scala-logging"       % "3.9.2",
  "com.amazonaws" % "aws-java-sdk-s3"                   % "1.11.294",
  "com.sun.activation" % "javax.activation" % "1.2.0",
  "org.scalatest" %% "scalatest"                        % "3.0.8"        % Test,
  "ch.qos.logback" % "logback-classic"                  % "1.2.3"        % Test,
  "org.iq80.leveldb" % "leveldb"                        % "0.12",
  "com.lightbend.akka" %% "akka-stream-alpakka-s3"      % "1.1.2"        % Test,
  "javax.xml.bind" % "jaxb-api"                         % "2.3.0",
  "com.sun.xml.bind" % "jaxb-core"                      % "2.3.0",
  "com.sun.xml.bind" % "jaxb-impl"                      % "2.3.0"
)

libraryDependencies ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, major)) if major >= 13 =>
      Seq("org.scala-lang.modules" %% "scala-parallel-collections" % "0.2.0" % Test)
    case _ => Seq()
  }
}

dependencyOverrides ++= Seq(
  "com.typesafe.akka" %% "akka-http"      % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-xml"  % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-parsing"   % akkaHttpVersion
)

parallelExecution in Test := false

publishMavenStyle := true

publishTo := sonatypePublishToBundle.value

pomExtra := (
  <scm>
    <url>git@github.com:findify/s3mock.git</url>
    <connection>scm:git:git@github.com:findify/s3mock.git</connection>
  </scm>
  <developers>
    <developer>
      <id>romangrebennikov</id>
      <name>Roman Grebennikov</name>
      <url>http://www.dfdx.me</url>
    </developer>
  </developers>
)

enablePlugins(DockerPlugin)

assemblyJarName in assembly := "s3mock.jar"
mainClass in assembly := Some("io.findify.s3mock.Main")
test in assembly := {}

dockerfile in docker := new Dockerfile {
  from("adoptopenjdk/openjdk11:jre-11.0.7_10-debian")
  expose(8001)
  add(assembly.value, "/app/s3mock.jar")
  entryPoint(
    "java",
    "-Xmx128m",
    "-jar",
    "--add-opens",
    "java.base/jdk.internal.ref=ALL-UNNAMED",
    "/app/s3mock.jar"
  )
}

imageNames in docker := Seq(
  ImageName(s"findify/s3mock:${version.value.replaceAll("\\+", "_")}"),
  ImageName(s"findify/s3mock:latest")
)
