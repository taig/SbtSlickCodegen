val project1 = Project( "project-1", file( "./module/p1/" ) )

val project2 = Project( "project-2", file( "./module/p2/" ) )
    .enablePlugins( SlickCodegenPlugin )
    .settings(
        databases in SlickCodegen ++= Map(
            "default" -> Database(
                Authentication( "jdbc:postgresql://localhost:5432/Taig" ),
                Driver(
                    "org.postgresql.Driver",
                    slick.driver.PostgresDriver
                )
            )
        )
    )

val root = project.in( file( "." ) ).dependsOn( project1, project2 )