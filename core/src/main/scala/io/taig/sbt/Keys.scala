package io.taig.sbt

import java.io.File

import sbt._
import slick.codegen.SourceCodeGenerator
import slick.driver.JdbcProfile
import slick.model.Model

trait Keys {
    object database {
        val driver = SettingKey[String]( "slick-codegen-database-driver", "Database driver" )

        val password = SettingKey[Option[String]]( "slick-codegen-database-password", "Database password" )

        val url = SettingKey[String]( "slick-codegen-database-url", "Database url" )

        val username = SettingKey[Option[String]]( "slick-codegen-database-username", "Database username" )
    }

    object profile {
        val slick = SettingKey[JdbcProfile]( "slick-codegen-profile-slick", "Slick database driver" )

        val user = SettingKey[Option[String]]( "slick-codegen-profile-user", "Custom slick database driver" )
    }

    val cache = SettingKey[Boolean](
        "slick-codegen-cache",
        "Continue with a previously generated file if the database is not running?"
    )

    val container = SettingKey[String](
        "slick-codegen-container",
        "Name of the class and file that contains the generated code"
    )

    val excludes = SettingKey[Seq[String]](
        "slick-codegen-excludes",
        "List of table names for which code generation will be skipped"
    )

    val generate = TaskKey[File](
        "slick-codegen-generate",
        "Run the code generator"
    )

    val generator = SettingKey[Model ⇒ SourceCodeGenerator](
        "slick-codegen-generator",
        "Actual SourceCodeGenerator implementation"
    )

    val identifier = SettingKey[Option[String]](
        "slick-codegen-identifier",
        "Package identifier of the generated file"
    )
}