logLevel := Level.Warn

// Informative Scala compiler errors
addSbtPlugin("com.softwaremill.clippy" % "plugin-sbt" % "0.3.0")

// Scala Protocol Buffers Compiler
addSbtPlugin("com.trueaccord.scalapb" % "sbt-scalapb" % "0.5.34")

addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.5.0")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.4")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.5")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "1.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-pgp" % "0.8.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.4")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.4.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.5")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.0")

addSbtPlugin("io.spray" % "sbt-revolver" % "0.8.0")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

// Protoc-jar so we don't need the Protoc compiler
libraryDependencies += "com.github.os72" % "protoc-jar" % "3.0.0-b3"
