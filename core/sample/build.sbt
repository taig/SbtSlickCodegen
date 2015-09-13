import slickCodegen._

val project1 = Project( "project-1", file( "./module/p1/" ) )

val project2 = Project( "project-2", file( "./module/p2/" ) )
    .enablePlugins( SlickCodegenPlugin )
    .settings(
        database.driver in Database( "default" ) := "org.postgresql.Driver",
        database.url in Database( "default" ) := "jdbc:postgresql://localhost:5432/Taig",
        profile.slick in Database( "default" ) := slick.driver.PostgresDriver
    )

val root = project.in( file( "." ) ).dependsOn( project1, project2 )