lazy val settings = Seq(
    normalizedName := "slick-codegen",
    organization := "io.taig",
    sbtPlugin := true,
    scalacOptions ++= (
        "-deprecation" ::
        "-feature" ::
        Nil
    ),
    scalaVersion := "2.10.5",
    version := "0.0.3"
)

lazy val root = project.in( file( "." ) ).aggregate( core, play )

lazy val core = project.in( file( "core" ) )
    .settings( settings )
    .settings(
        libraryDependencies ++= (
            "com.typesafe.slick" %% "slick-codegen" % "3.0.3" ::
            Nil
        ),
        organization += ".sbt"
    )

lazy val play = project.in( file( "play" ) )
    .settings( settings )
    .settings(
        addSbtPlugin( "com.typesafe.play" % "sbt-plugin" % "2.4.3" ),
        libraryDependencies ++= (
            "com.typesafe" % "config" % "1.3.0" ::
            Nil
        ),
        name := "PlaySlickCodegen",
        organization += ".play"
    )
    .dependsOn( core )