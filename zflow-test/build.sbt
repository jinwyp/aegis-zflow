name := "akka-cluster-sharding-scala"

version := "1.0"

scalaVersion := "2.11.8"

val akka = "2.4.11"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-cluster" % akka,
  "com.typesafe.akka" %% "akka-cluster-tools" % akka,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akka,
  "com.typesafe.akka" %% "akka-persistence" % akka,
  "com.typesafe.akka" %% "akka-multi-node-testkit" % akka,
  "com.hootsuite" %% "akka-persistence-redis" % "0.6.0",
  "org.iq80.leveldb" % "leveldb" % "0.7",
  "com.typesafe.akka" %% "akka-http-core" % akka,
  "com.typesafe.akka" %% "akka-http-experimental" % akka,
  "com.typesafe.akka" %% "akka-http-testkit" % akka % "test",
  "com.typesafe.akka" %% "akka-slf4j" % akka,
  "com.typesafe.akka" %% "akka-camel"   % akka,
  "com.typesafe.akka" %  "akka-http-spray-json-experimental_2.11" %  akka,

  // compiler
  "org.scala-lang" % "scala-reflect" % "2.11.8",
  "org.scala-lang" % "scala-compiler" % "2.11.8",

  // persistence
  "com.github.swagger-akka-http" %% "swagger-akka-http" % "0.7.2",
//  "com.softwaremill.akka-http-session" %% "core" %  "0.3.0",


  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8"

 // "org.scalatest" %% "scalatest" % "2.1.6" % "test",
 // "commons-io" % "commons-io" % "2.4" % "test"
)

