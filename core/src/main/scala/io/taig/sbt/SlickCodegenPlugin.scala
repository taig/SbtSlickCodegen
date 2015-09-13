package io.taig.sbt

import java.io.File

import io.taig.sbt.writer.{ Driver, Writer }
import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin
import slick.codegen.SourceCodeGenerator
import slick.driver.JdbcProfile
import slick.model.Model

import scala.collection.mutable

object SlickCodegenPlugin extends AutoPlugin {
    val configurations = mutable.Map[String, sbt.Configuration]()

    object autoImport {
        object slickCodegen {
            def Database( name: String ) = {
                configurations.getOrElseUpdate( name, config( s"database-$name" ) extend Compile )
            }

            object database {
                val driver = SettingKey[String]( "database-driver", "Database driver" )

                val password = SettingKey[Option[String]]( "database-password", "Database password" )

                val url = SettingKey[String]( "database-url", "Database url" )

                val username = SettingKey[Option[String]]( "database-username", "Database username" )
            }

            object profile {
                val slick = SettingKey[JdbcProfile]( "profile-slick", "Slick database driver" )

                val user = SettingKey[Option[String]]( "profile-user", "Custom slick database driver" )
            }

            val cache = SettingKey[Boolean](
                "cache",
                "Continue with a previously generated file if the database is not running?"
            )

            val container = SettingKey[String](
                "container",
                "Name of the class and file that contains the generated code"
            )

            val excludes = SettingKey[Seq[String]](
                "excludes",
                "List of table names for which code generation will be skipped"
            )

            val generate = TaskKey[File](
                "generate",
                "Run the code generator"
            )

            val generator = SettingKey[Model ⇒ SourceCodeGenerator](
                "generator",
                "Actual SourceCodeGenerator implementation"
            )

            val identifier = SettingKey[Option[String]](
                "identifier",
                "Package identifier of the generated file"
            )
        }
    }

    import autoImport.slickCodegen._

    override def requires = JvmPlugin

    override def projectConfigurations = configurations.values.toSeq

    override lazy val projectSettings = {
        import writer._

        configurations.flatMap {
            case ( name, configuration ) ⇒ inConfig( configuration ) {
                Seq(
                    database.username := None,
                    database.password := None,
                    profile.user := None,
                    cache := true,
                    container := name.capitalize + "Tables",
                    excludes := Seq.empty,
                    generate := {
                        val configuration = Configuration(
                            Authentication(
                                database.url.value,
                                database.username.value,
                                database.password.value
                            ),
                            Driver(
                                database.driver.value,
                                profile.slick.value,
                                profile.user.value
                            ),
                            container.value,
                            identifier.value,
                            generator.value,
                            excludes.value,
                            cache.value
                        )

                        Writer( sourceManaged.value, configuration, streams.value.log )
                    },
                    generator := { new SourceCodeGenerator( _ ) },
                    identifier := None,

                    sourceGenerators in Compile <+= generate.map( Seq( _ ) )
                )
            }
        }.toSeq
    }
}