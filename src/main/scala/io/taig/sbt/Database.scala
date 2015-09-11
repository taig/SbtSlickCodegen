package io.taig.sbt

import slick.codegen.SourceCodeGenerator
import slick.driver.JdbcProfile

/**
 * Database configuration
 *
 * @param authentication Authentication configuration
 * @param driver Driver configuration
 * @param container Name of the generated file and class that contains the table definitions
 * @param identifier Package identifier of the generated code
 * @param generator Sourcecode generator
 * @param excludes List of tables for which no code shall be generated
 * @param cache Allow to continue the code generation task with a cached file if the database is not available
 */
case class Database(
    authentication: Authentication,
    driver:         Driver,
    container:      String                                  = "Tables",
    identifier:     Option[String]                          = None,
    generator:      slick.model.Model ⇒ SourceCodeGenerator = new SourceCodeGenerator( _ ),
    excludes:       Seq[String]                             = Seq.empty,
    cache:          Boolean                                 = true
)

/**
 * Authentication configuration
 *
 * @param url URL for database login
 * @param username Username for database login
 * @param password Password for database login
 */
case class Authentication(
    url:      String,
    username: Option[String] = None,
    password: Option[String] = None
)

/**
 * Driver configuration
 *
 * @param jdbc Jdbc driver used to read the database schema
 * @param slick Slick driver used to read the database schema
 * @param user User space driver that should be referenced in the generated code (rather than the slick driver)
 */
case class Driver(
    jdbc:  String,
    slick: JdbcProfile,
    user:  Option[String] = None
)