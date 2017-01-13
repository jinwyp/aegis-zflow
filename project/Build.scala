import com.trueaccord.scalapb.{ScalaPbPlugin => PB}
import sbt.Keys._
import sbt._
import spray.revolver.RevolverPlugin.autoImport.Revolver

object Resolvers {
}

object Dependencies {

  private val akka = "2.4.16"
  private val akkaHttp = "10.0.1"
  private val swaggerAkkaHttp = "0.7.2"
  private val akkaHttpSession = "0.3.0"

  private val leveldb = "0.7"
  private val leveldbjniAll = "1.8"
  private val akkaPersistenceRedis = "0.6.0"

  private val slick = "3.1.1"
  private val mysqlConnectorJava = "6.0.5"
  private val hikaricp = "2.4.5"

  private val accordCore = "0.6"
  private val flywayCore = "3.2.1"

  private val freemarker = "2.3.23"
  private val thymeleaf = "3.0.2.RELEASE"

  private val scalazCore = "7.2.8"
  private val commonsCompress = "1.2"
  private val commonsIO = "2.5"
  private val quicklens = "1.4.8"

  private val camelJetty = "2.16.4"
  private val camelQuartz = "2.16.4"

  private val scalapbRuntime = "0.5.34"

  private val logbackClassic = "1.1.3"

  private val scalaTest = "3.0.0"

  val appDependencies = Seq(
    // akka and cluster
    "com.typesafe.akka" %% "akka-actor" % akka,
    "com.typesafe.akka" %% "akka-persistence" % akka,
    "com.typesafe.akka" %% "akka-cluster" % akka,
    "com.typesafe.akka" %% "akka-cluster-tools" % akka,
    "com.typesafe.akka" %% "akka-cluster-sharding" % akka,
    "com.typesafe.akka" %% "akka-multi-node-testkit" % akka % "test",
    "com.typesafe.akka" %% "akka-contrib" % akka,
    "com.typesafe.akka" %% "akka-remote" % akka,
    "com.typesafe.akka" %% "akka-stream" % akka,
    "com.typesafe.akka" %% "akka-stream-testkit" % akka % "test",
    "com.typesafe.akka" %% "akka-slf4j" % akka,
    "com.typesafe.akka" %% "akka-camel" % akka,

    // camel integration
    "org.apache.camel" % "camel-jetty" % camelJetty,
    "org.apache.camel" % "camel-quartz" % camelQuartz,

    // akka http now is production-ready
    "com.typesafe.akka" %% "akka-http-core" % akkaHttp,
    "com.typesafe.akka" %% "akka-http" % akkaHttp,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttp % "test",
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttp,
    "com.typesafe.akka" %% "akka-http-jackson" % akkaHttp,
    "com.typesafe.akka" %% "akka-http-xml" % akkaHttp,
    "com.github.swagger-akka-http" %% "swagger-akka-http" % swaggerAkkaHttp,
    "com.softwaremill.akka-http-session" %% "core" % akkaHttpSession,

    // akka-persistence
    "com.hootsuite" %% "akka-persistence-redis" % akkaPersistenceRedis,
    "org.iq80.leveldb" % "leveldb" % leveldb,
    "org.fusesource.leveldbjni" % "leveldbjni-all" % leveldbjniAll,

    // compiler
    "org.scala-lang" % "scala-reflect" % "2.11.8",
    "org.scala-lang" % "scala-compiler" % "2.11.8",

    // database: slick and flyway
    "com.typesafe.slick" %% "slick" % slick,
    "org.flywaydb" % "flyway-core" % flywayCore,
    "com.zaxxer" % "HikariCP" % hikaricp,
    "mysql" % "mysql-connector-java" % mysqlConnectorJava,

    // test
    "org.scalatest" %% "scalatest" % scalaTest % "test",

    // validation
    "com.wix" %% "accord-core" % accordCore,

    // lens
    "com.softwaremill.quicklens" %% "quicklens" % quicklens,

    // logger
    "ch.qos.logback" % "logback-classic" % logbackClassic,
    "org.slf4j" % "slf4j-nop" % "1.6.4",

    // neo4j-scala
    //"eu.fakod"  %% "neo4j-scala" % "0.3.3",

    // scala-pb
    // "com.trueaccord.scalapb"  %% "scalapb-runtime"  % "0.5.34"  % PB.protobufConfig,

    // scalaz
    "org.scalaz" %% "scalaz-core" % scalazCore,

    //files tar.gz
    "org.apache.commons" % "commons-compress" % commonsCompress,
    "commons-io" % "commons-io" % commonsIO,

    // backend template
    "org.freemarker" % "freemarker" % freemarker,
    "org.thymeleaf" % "thymeleaf" % thymeleaf

  )
}

object BuildSettings {

  val buildOrganization = "com.yimei"
  val appName = "aegis-zflow"
  val buildVersion = "0.0.5"
  val buildScalaVersion = "2.11.8"
  val buildScalaOptions = Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8")

  import Dependencies._

  val buildSettings = Defaults.coreDefaultSettings ++ Seq(
    organization := buildOrganization,
    version := buildVersion,
    scalaVersion := buildScalaVersion,
    libraryDependencies ++= appDependencies,
    scalacOptions := buildScalaOptions,
    fork in Test := true,
    javaOptions += "-Ddev=true"
  ) ++ Revolver.settings
}


object PublishSettings {

  // publish settings
  val publishSettings = Seq(
    credentials += Credentials("Sonatype Nexus Repository Manager", "maven.yimei180.com", "admin", "admin123"),
    publishMavenStyle := true,
    isSnapshot := false,
    publishArtifact in Test := false,
    pomIncludeRepository := { (repo: MavenRepository) => false },
    pomExtra := pomXml,
    publishTo := {
      if (isSnapshot.value)
        Some("Sonatype Nexus Repository Manager" at "http://maven.yimei180.com/content/repositories/snapshots")
      else
        Some("Sonatype Nexus Repository Manager" at "http://maven.yimei180.com/content/repositories/releases")
    }
  )

  lazy val pomXml = {
    <url>https://github.com/epiphyllum/zflow</url>
      <licenses>
        <license>
          <name>Apache License 2.0</name>
          <url>http://www.apache.org/licenses/</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:epiphyllum/zflow.git</url>
        <connection>scm:git:git@github.com:epiphyllum/zflow.git</connection>
      </scm>
      <developers>
        <developer>
          <id>hary</id>
          <name>hary</name>
          <url>http://github.com/epiphyllum</url>
        </developer>
      </developers>
  }
}

object ApplicationBuild extends Build {

  import BuildSettings._
  import PublishSettings._


  lazy val zflowUtil = Project("util", file("zflow-util"), settings = buildSettings ++ publishSettings)
  lazy val zflowEngine = Project("engine", file("zflow-engine"), settings = buildSettings ++ publishSettings).dependsOn(zflowUtil, zflowUtil)

  lazy val zflowCluster = Project("cluster", file("zflow-cluster"), settings = buildSettings ++ publishSettings).dependsOn(zflowEngine)
  lazy val zflowSingle = Project("single", file("zflow-single"), settings = buildSettings ++ publishSettings).dependsOn(zflowEngine)
  lazy val zflowMoney = Project("money", file("zflow-money"), settings = buildSettings ++ publishSettings).dependsOn(zflowEngine, zflowSingle)

  //  val singleSettings = libraryDependencies ++= Seq("com.softwaremill.akka-http-session" %% "core" %  akkaHttpSession)
  //  lazy val zflowSingle  = Project("single", file("zflow-single"),
  //    settings = buildSettings ++ publishSettings ++ singleSettings).dependsOn(zflowEngine)

  lazy val root = Project(appName, file(".")).aggregate(zflowEngine, zflowUtil, zflowSingle, zflowCluster, zflowMoney)

}
