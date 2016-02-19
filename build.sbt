resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

lazy val root = (project in file("."))
  .settings(
    name := "XingApi",
    scalaVersion := "2.11.7",
    libraryDependencies ++= Seq(
        "com.netflix.rxjava" % "rxjava-scala" % "0.19.1",
        "com.ning" % "async-http-client" % "1.9.33",
        "org.slf4j" % "slf4j-log4j12" % "1.7.5",
        "com.google.code.gson" % "gson" % "2.6.1",
        "net.liftweb" %% "lift-json" % "2.6",
        "com.typesafe.play" %% "play-json" % "2.4.1",
        "junit" % "junit" % "4.11" % "test",
        "org.scalatest" %% "scalatest" % "2.2.6" % "test"
    )
  )