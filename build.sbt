name := "Labs"

version := "0.1"

scalaVersion := "2.11.2"

val elastic4sVersion = "5.4.15"

//resolvers += "MavenRepository" at "https://mvnrepository.com/"
//resolvers  += "MavenRepository" at "http://central.maven.org/maven2"

libraryDependencies ++= Seq(
  "ch.qos.logback"  %  "logback-classic"    % "1.2.+",
  "mysql" % "mysql-connector-java" % "5.1.24",
  "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion
)
libraryDependencies += "org.scalikejdbc" %% "scalikejdbc" % "3.2.1"

libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-http" % elastic4sVersion

libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.5.3"

libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % "2.3.0"
