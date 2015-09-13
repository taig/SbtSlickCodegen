libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1202-jdbc42"

lazy val root = Project( "plugin", file( "." ) ) dependsOn ProjectRef( file( "../../../" ), "core" ) 