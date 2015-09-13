lazy val root = Project( "sample", file( "." ) ) dependsOn ProjectRef( file( "../../../" ), "play" )

libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1202-jdbc42"

addSbtPlugin( "com.typesafe.play" % "sbt-plugin" % "2.4.3" )