ThisBuild / scalaVersion := "2.13.13"

val http4sVersion       = "0.23.18" //"1.0.0-M39"
val circeVersion        = "0.14.5"
val `munit-cats-effect` = "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % Test

val `http4s-munit` = "com.alejandrohdezma" %% "http4s-munit" % "0.15.1" % Test

val munit = "org.scalameta" %% "munit" % "0.7.29" % Test

lazy val root = (project in file(".")).settings(
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core"           % "2.9.0",
    "org.typelevel" %% "cats-effect"         % "3.5.0" withSources () withJavadoc (),
    "org.typelevel" %% "cats-effect-cps"     % "0.4.0",
    "org.http4s"    %% "http4s-ember-client" % http4sVersion,
    "org.http4s"    %% "http4s-ember-server" % http4sVersion,
    "org.http4s"    %% "http4s-dsl"          % http4sVersion,
    "org.typelevel" %% "log4cats-noop"       % "2.6.0",
    "org.http4s"    %% "http4s-circe"        % http4sVersion,
    "io.circe"      %% "circe-generic"       % circeVersion,
    "io.circe"      %% "circe-literal"       % circeVersion,
    `munit-cats-effect`,
    `http4s-munit`,
    munit
  ),
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-unchecked",
    "-language:postfixOps",
    "-Xasync"
  )
)

name := "Munit-Examples"

version := "1.0"

val ScalaCheck = TestFramework("org.scalacheck.ScalaCheckFramework")
val ScalaTest =
  TestFramework("org.scalatest.tools.Framework", "org.scalatest.tools.ScalaTestFramework")
val Specs = TestFramework("org.specs.runner.SpecsFramework")
val Specs2 =
  TestFramework("org.specs2.runner.Specs2Framework", "org.specs2.runner.SpecsFramework")
val JUnit          = TestFramework("com.novocode.junit.JUnitFramework")
val MUnit          = TestFramework("munit.Framework")
val ZIOTest        = TestFramework("zio.test.sbt.ZTestFramework")
val WeaverTestCats = TestFramework("weaver.framework.CatsEffect")
val Hedgehog       = TestFramework("hedgehog.sbt.Framework")

// TestFrameworks.All
// TestFrameworks.Hedgehog
// TestFrameworks.WeaverTestCats
// TestFrameworks.ZIOTest
// TestFrameworks.MUnit
// TestFrameworks.Specs2
// TestFrameworks.Specs
// TestFrameworks.ScalaTest
// TestFrameworks.ScalaCheck
// TestFrameworks.JUnit

//coverageEnabled := true
ThisBuild / pushRemoteCacheTo := Some("Nexus OSS 3 Remote Cache" at "http://localhost:8081/repository/sbt-build-cache/")


//publishConfiguration := publishConfiguration.value.withOverwrite(true)
 //publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)

 //Compile/pushRemoteCacheConfiguration := pushRemoteCacheConfiguration.value.withOverwrite(true)



