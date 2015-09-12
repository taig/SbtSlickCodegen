package io.taig.sbt

import java.io.File

import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin
import slick.codegen.SourceCodeGenerator
import slick.model.Model

object SlickCodegenPlugin extends AutoPlugin {
    object autoImport {
        val SlickCodegen = config( "slick-codegen" ) extend Compile

        val Authentication = io.taig.sbt.Authentication
        val Database = io.taig.sbt.Database
        val Driver = io.taig.sbt.Driver

        val databases = SettingKey[Map[String, Database]](
            "databases",
            "Database configurations for code generation"
        )

        val generate = TaskKey[Seq[File]]( "generate", "Run the code generator" )
    }

    import autoImport._

    override def requires = JvmPlugin

    override lazy val projectSettings = inConfig( SlickCodegen )(
        Seq(
            databases := Map.empty,
            generate := {
                databases.value.map {
                    case ( name, database ) ⇒ Writer(
                        ( sourceManaged in SlickCodegen ).value,
                        database,
                        ( streams in Compile ).value.log
                    )
                }.toSeq
            }
        )
    ) ++ Seq( sourceGenerators in Compile <+= ( generate in SlickCodegen ) )
}