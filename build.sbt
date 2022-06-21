lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """json-validation-service""",
    organization := "com.snowplow",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.8",
    libraryDependencies ++= Seq(
      guice,
      "org.mongodb.scala" %% "mongo-scala-driver" % "4.6.0",
      "com.github.java-json-tools" % "json-schema-validator" % "2.2.14",
      "com.typesafe.play" %% "play-json" % "2.8.2",
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )
