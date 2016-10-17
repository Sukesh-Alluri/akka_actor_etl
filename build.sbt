resolvers += "getty-releases" at "http://artifacts.amer.gettywan.com:8081/nexus/content/repositories/releases/"

val akkaVersion = "2.4.7"

scalaVersion := "2.11.8"

libraryDependencies += "com.gettyimages.dsa" %% "rmq" % "0.1.42"
libraryDependencies += "com.gettyimages.dsa" %% "persistencev2" % "0.192"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-remote" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % akkaVersion

scalacOptions ++= Seq(
  "-deprecation"
  ,"-unchecked"
  ,"-encoding", "UTF-8"
  ,"-Xlint"
  ,"-Yclosure-elim"
  ,"-Yinline"
  ,"-Xverify"
  ,"-feature"
  //,"-Xfatal-warnings"
  ,"-language:postfixOps"
)

// Deduplication of strings. Reduce heap used.
javaOptions += "-XX:+UseG1GC"
javaOptions += "-XX:+UseStringDeduplication"
