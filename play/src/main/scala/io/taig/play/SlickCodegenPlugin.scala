package io.taig.play

import sbt._
import com.typesafe.config.{ ConfigFactory, Config }
import io.taig.sbt.Database
import play.sbt.PlayScala
import sbt.plugins.JvmPlugin
import io.taig.sbt
import io.taig.sbt.SlickCodegenPlugin.autoImport._

object SlickCodegenPlugin extends AutoPlugin {
    override def requires = JvmPlugin && PlayScala && sbt.SlickCodegenPlugin

    object autoImport {
        val databaseConfigurations = SettingKey[Seq[Config]]( "database-configurations" )
    }

    import autoImport._

    override def projectSettings = inConfig( SlickCodegen )(
        Seq(
            databaseConfigurations := Seq( ConfigFactory.parseFile( file( "./conf/application.conf" ) ) ),
            databases ++= {
                val configs = databaseConfigurations.value.map( _.getConfig( "slick.dbs" ) )
                Map.empty[String, Database]
            }
        )
    )
}