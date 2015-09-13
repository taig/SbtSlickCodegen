package io.taig.play

import _root_.sbt.Keys._
import com.typesafe.config.{ ConfigException, ConfigFactory, Config }
import io.taig.sbt._
import io.taig.sbt.writer.{ Driver, Configuration }
import play.sbt.PlayScala
import sbt._
import sbt.plugins.JvmPlugin
import io.taig.sbt
import io.taig.sbt.SlickCodegenPlugin.autoImport.slickCodegen
import slick.driver.JdbcProfile
import scala.collection.JavaConversions._

import scala.language.implicitConversions
import scala.util.Try
import scala.reflect.runtime.universe

object SlickCodegenPlugin extends AutoPlugin {
    override def requires = JvmPlugin && PlayScala && sbt.SlickCodegenPlugin

    object autoImport {
        implicit def `File -> Config`( file: File ): Config = ConfigFactory.parseFile( file )

        object playSlickCodegen {
            val configurations = SettingKey[Seq[Config]](
                "play-slick-codegen-configurations",
                ""
            )

//            def resolve = Command.command( "play-slick-codegen-resolve" ) { state ⇒
//                val extracted = Project extract state
//
//                val settings = extracted.get( configurations ).flatMap( configuration ⇒ {
//                    val databases = configuration.getConfig( "slick.dbs" )
//
//                    databases.root().keys.flatMap( key ⇒ {
//                        val driver = databases.getConfig( key )
//                        val database = driver.getConfig( "db" )
//
//                        inConfig( slickCodegen.Database( key ) ) {
//                            Seq(
//                                slickCodegen.database.driver := database.getString( "driver" ),
//                                slickCodegen.database.password := Try( database.getString( "password" ) ).toOption,
//                                slickCodegen.database.url := database.getString( "url" ),
//                                slickCodegen.database.username := Try( database.getString( "username" ) ).toOption,
//                                slickCodegen.profile.slick := {
//                                    val mirror = universe.runtimeMirror( getClass.getClassLoader )
//                                    val module = mirror.staticModule( driver.getString( "driver" ) )
//                                    mirror.reflectModule( module ).instance.asInstanceOf[JdbcProfile]
//                                }
//                            )
//                        }
//                    } )
//                } )
//
//                extracted.append( settings, state )
//            }
        }
    }

    import autoImport.playSlickCodegen._

    override lazy val projectSettings = Seq(
        configurations := Seq.empty
    )
}