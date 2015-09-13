enablePlugins( PlayScala, io.taig.play.SlickCodegenPlugin )

databases in SlickCodegen ++= Databases( file( "./conf/application.conf" ) )