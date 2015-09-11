# SbtSlickCodegen

> sbt AutoPlugin for slick code generation

[![Circle CI](https://circleci.com/gh/Taig/SbtSlickCodegen.svg?style=svg)](https://circleci.com/gh/Taig/SbtSlickCodegen)

## Installation

Add the plugin and the required database drivers to `project/plugins.sbt`:

````
libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1202-jdbc42"

addSbtPlugin( "io.taig.sbt" % "slick-codegen" % "0.0.3" )
````

## Usage

The plugin needs to be enabled explicitly in your project definition:

````
val root = Project( "root", file( "." ) ).enablePlugins( SlickCodegenPlugin )
````

You then need to add database configurations, and the plugin will automatically trigger on compile or via `slick-codegen:generate`.

````
val root = Project( "root", file( "." ) )
    .enablePlugins( SlickCodegenPlugin )
    .settings(
        databases in SlickCodegen ++= Map(
            "default" -> Database(
                Authentication(
                    url = "jdbc:postgresql://localhost:5432/Taig",
                    username = Some( "db_user_123" ),           // optional, default: None
                    password = Some( "p@assw0rd" )              // optional, default: None
                ),
                Driver(
                    jdbc = "org.postgresql.Driver",
                    slick = slick.driver.PostgresDriver,
                    user = Some( "com.example.MySlickProfile" ) // optional, default: None
                ),
                container = "Tables",                           // optional, default: "Tables"
                generator = new SourceCodeGenerator( _ ),       // optional, default: new SourceCodeGenerator( _ )
                identifier = Some( "com.example" ),             // optional, default: None
                excludes = Seq( "play_evolutions" ),            // optional, default: Seq.empty
                cache = true                                    // optional, default: true
            ),
            "user_db" -> Database( ... )
        )
    )
````

The generated files are automatically written to `sourceManaged in Compile`, but the path may be altered via `sourceManaged in SlickCodegen`.