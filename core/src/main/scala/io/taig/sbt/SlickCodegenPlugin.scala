package io.taig.sbt

import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin
import slick.codegen.SourceCodeGenerator
import slick.driver.JdbcProfile

import scala.collection.mutable
import scala.reflect.runtime._

object SlickCodegenPlugin extends AutoPlugin {
    val configurations = mutable.Map[String, sbt.Configuration]()

    object autoImport {
        object slickCodegen extends Keys {
            val Database: Configuration = config( "database" ) extend Compile

            def Database( name: String ): Configuration = {
                configurations.getOrElseUpdate( name, config( s"database-$name" ) extend Database )
            }

            def addDatabase = Command.args(
                "slick-codegen-add-database",
                "name, jdbc driver, url, slick profile"
            ) { ( state, arguments ) ⇒
                    require( arguments.length == 4, "Need exactly 4 arguments" )
                    val name = arguments( 0 )
                    val driver = arguments( 1 )
                    val url = arguments( 2 )
                    val slick = {
                        val mirror = universe.runtimeMirror( getClass.getClassLoader )
                        val module = mirror.staticModule( arguments( 3 ) )
                        mirror.reflectModule( module ).instance.asInstanceOf[JdbcProfile]
                    }

                    val configuration = Database( name )

                    val extracted = Project extract state

                    extracted.append(
                        inConfig( configuration ) {
                            Seq(
                                database.driver := driver,
                                database.url := url,
                                profile.slick := slick,
                                database.username := ( database.username in Database ).value,
                                database.password := ( database.password in Database ).value,
                                profile.user := ( profile.user in Database ).value,
                                cache := ( cache in Database ).value,
                                container := ( container in Database ).value,
                                excludes := ( excludes in Database ).value,
                                generator := ( generator in Database ).value,
                                identifier := ( identifier in Database ).value,
                                sourceManaged := ( sourceManaged in Database ).value
                            )
                        } ++ Settings.create( name, configuration ),
                        state
                    )
                }
        }
    }

    import autoImport.slickCodegen._

    override def requires = JvmPlugin

    override lazy val projectConfigurations = Database +: configurations.values.toSeq

    object Settings {
        val default = inConfig( Database ) {
            Seq(
                database.username := None,
                database.password := None,
                profile.user := None,
                cache := true,
                container := "Tables",
                excludes := Seq.empty,
                generator := { new SourceCodeGenerator( _ ) },
                identifier := None,
                sourceManaged := ( sourceManaged in Compile ).value
            )
        } ++ Seq(
            commands += addDatabase
        )

        val configurations = SlickCodegenPlugin
            .configurations
            .flatMap{ case ( name, configuration ) ⇒ create( name, configuration ) }

        def create( name: String, configuration: Configuration ) = inConfig( configuration ) {
            import writer._

            Seq(
                container := name.capitalize + container.value,
                generate := Writer(
                    sourceManaged.value,
                    Configuration(
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
                    ),
                    streams.value.log
                ),
                sourceGenerators in Compile <+= generate.map( Seq( _ ) )
            )
        }
    }

    override lazy val projectSettings = Settings.default ++ Settings.configurations
}