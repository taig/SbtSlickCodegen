lazy val root = Project( "plugin", file( "." ) ) dependsOn file( "../" ).getAbsoluteFile.toURI

addSbtPlugin( "com.typesafe.play" % "sbt-plugin" % "2.4.2" )