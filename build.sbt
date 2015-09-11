libraryDependencies ++=
    "com.typesafe.slick" %% "slick-codegen" % "3.0.3" ::
    Nil

name := "SbtSlickCodegen"

normalizedName := "slick-codegen"

organization := "io.taig.sbt"

sbtPlugin := true

scalacOptions ++=
    "-deprecation" ::
    "-feature" ::
    Nil

version := "0.0.3"