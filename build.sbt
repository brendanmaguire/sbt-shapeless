version := "0.1-SNAPSHOT"

import sbt.ScriptedPlugin.autoImport.scriptedLaunchOpts

scalaVersion := "2.12.14"

lazy val root = Project("sbt-shapeless", file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    organization := "io.github.brendanmaguire",
    libraryDependencies ++= Seq(
      "commons-io" % "commons-io" % Versions.commonsIO,
      "org.scalameta" %% "munit" % Versions.munit % Test,
      "org.typelevel" %% "cats-core" % Versions.catsCore,
    ),
    scriptedLaunchOpts ++= Seq(
      "-Xmx1024M",
      "-Dplugin.version=" + version.value,
    ),
    scalacOptions ++= Seq("-Ypartial-unification"),
  )
