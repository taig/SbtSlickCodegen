package io.taig.play

import com.typesafe.config.{ ConfigException, ConfigFactory, Config }
import io.taig.sbt._
import io.taig.sbt.writer.{Driver, Configuration}
import play.sbt.PlayScala
import sbt._
import sbt.plugins.JvmPlugin
import io.taig.sbt
import slick.driver.JdbcProfile
import scala.collection.JavaConversions._

import scala.language.implicitConversions
import scala.util.Try
import scala.reflect.runtime.universe

object SlickCodegenPlugin extends AutoPlugin {
    override def requires = JvmPlugin && PlayScala && sbt.SlickCodegenPlugin

    object autoImport {
        object Databases {
            def apply( config: Config ): Map[String, Configuration] = {
                val dbs = config.getConfig( "slick.dbs" )

                dbs.root().keys.map( key ⇒ {
                    val root = dbs.getConfig( key )
                    val db = root.getConfig( "db" )

                    val authentication = writer.Authentication(
                        db.getString( "url" ),
                        Try( db.getString( "username" ) ).toOption,
                        Try( db.getString( "password" ) ).toOption
                    )

                    val driver = Driver(
                        db.getString( "driver" ),
                        {
                            val mirror = universe.runtimeMirror( getClass.getClassLoader )
                            val module = mirror.staticModule( root.getString( "driver" ) )
                            mirror.reflectModule( module ).instance.asInstanceOf[JdbcProfile]
                        }
                    )

                    key → Configuration( authentication, driver )
                } ).toMap
            }

            def apply( file: File ): Map[String, Configuration] = {
                if ( !file.exists() ) {
                    sys.error( s"Configuration file ${file.getAbsolutePath} does not exist" )
                }

                Databases( ConfigFactory.parseFile( file ) )
            }
        }
    }
}