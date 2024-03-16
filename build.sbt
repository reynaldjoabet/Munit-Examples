

scalaVersion := "2.13.13"


name := "Munit-Examples"

version := "1.0"

val `munit-cats-effect`="org.typelevel" %% "munit-cats-effect-3" % "1.0.7"

val `http4s-munit`= "com.alejandrohdezma" %% "http4s-munit" % "0.15.1" % Test

val munit= "org.scalameta" %% "munit" % "0.7.29" % Test
lazy val root=(project in file("."))
.settings(libraryDependencies ++= Seq(munit,`http4s-munit`,`munit-cats-effect`))