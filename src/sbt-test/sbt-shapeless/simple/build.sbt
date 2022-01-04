version := "0.1"
scalaVersion := "2.13.5"

shapelessCirceCodecsEnabled := true
shapelessDoobieMetasEnabled := true

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.3.7",
  "io.circe" %% "circe-core" % "0.14.1",
  "io.circe" %% "circe-parser" % "0.14.1",
  "io.circe" %% "circe-generic" % "0.14.1",
  "org.tpolecat" %% "doobie-core" % "1.0.0-RC1",
)
