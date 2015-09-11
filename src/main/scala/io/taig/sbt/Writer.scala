package io.taig.sbt

import java.io.File._
import java.io.{ BufferedWriter, File, FileWriter }
import java.sql.SQLException

import sbt.Logger
import slick.codegen.SourceCodeGenerator
import slick.model.Model

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration._

object Writer {
    def error( message: String, cause: Throwable = null ) = throw new RuntimeException( message, cause )

    def apply(
        target:        File,
        configuration: Database,
        logger:        Logger
    ): File = {
        import configuration._

        if ( target.exists() ) {
            if ( !target.isDirectory ) {
                error( s"$target is not a directory" )
            }
        } else {
            if ( !target.mkdirs() ) {
                error( s"$target could not be created" )
            }
        }

        val database = {
            try {
                driver.slick.api.Database.forURL(
                    authentication.url,
                    authentication.username.orNull,
                    authentication.password.orNull,
                    null,
                    driver.jdbc
                )
            } catch {
                case exception: ClassNotFoundException if exception.getMessage == driver.jdbc ⇒
                    error(
                        s"Could not load driver '${driver.jdbc}', did you add the dependency into " +
                            "project/plugins.sbt?",
                        exception
                    )
                case exception: SQLException if exception.getMessage == "No suitable driver" ⇒
                    error( s"Invalid database url '${authentication.url}'", exception )
            }
        }

        val root = new File(
            target,
            identifier.map( _.replaceAllLiterally( ".", separator ) ).getOrElse( "" )
        )
        root.mkdirs()

        val file = new File( root, container + ".scala" )

        try {
            database.source.createConnection().close()
        } catch {
            case exception: Throwable ⇒
                if ( cache && file.exists() ) {
                    logger.warn(
                        s"Could not establish a database connection, continuing with cached file " +
                            s"(${file.getAbsolutePath})"
                    )

                    return file
                } else {
                    error( "Could not establish a database connection. Is the database running?", exception )
                }
        }

        val tables = driver.slick
            .defaultTables
            .map( _.filterNot( table ⇒ excludes.contains( table.name.name ) ) )

        val profile = driver.user.getOrElse( "slick.driver." + driver.slick )

        val action = driver.slick.createModel( Some( tables ) ).map( generator ).map( generator ⇒ {
            import generator._

            val content = s"""
                |${identifier.map( "package " + _ ).getOrElse( "" )}
                |
                |/**
                | * Stand-alone Slick data model for immediate use
                | */
                |object $container extends { val profile = $profile } with $container
                |
                |/**
                | * Slick data model trait for extension, choice of backend or usage in the cake pattern.
                | * (Make sure to initialize this late.)
                | */
                |trait $container${parentType.map( " extends " + _ ).getOrElse( "" )} {
                |    val profile: slick.driver.JdbcProfile
                |    import profile.api._
                |    ${indent( code )}
                |}
            """.stripMargin

            val writer = new BufferedWriter( new FileWriter( file ) )
            writer.write( content )
            writer.close()
        } )

        Await.result( database.run( action ), Inf )
        logger.info( "Generated slick code: " + file.getAbsolutePath )

        file
    }
}
