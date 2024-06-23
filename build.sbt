ThisBuild / scalaVersion := "2.13.13"

val http4sVersion       = "0.23.18" //"1.0.0-M39"
val circeVersion        = "0.14.5"
val `munit-cats-effect` = "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % Test

val `http4s-munit` = "com.alejandrohdezma" %% "http4s-munit" % "0.15.1" % Test

val munit = "org.scalameta" %% "munit" % "0.7.29" % Test

val compilationCacheSettings: Seq[Def.Setting[_]] = Seq(
  Compile / remoteCacheId := "id" /* this will set the folder of the compiled artifacts to 0.0.0-id */,
  Compile / pushRemoteCacheConfiguration ~= (_.withOverwrite(true)),
  // Test / remoteCacheId := "id",
  Test / pushRemoteCacheConfiguration ~= (_.withOverwrite(true)),
  Test / packageCache / pushRemoteCacheArtifact := true, /* defaults to true */
  pushRemoteCacheTo := Some(
    "Nexus OSS 3 Remote Cache".at("http://localhost:8081/repository/sbt-build-cache/")
  )
  // ThisBuild / pushRemoteCacheTo := Some(MavenCache("local-cache", file("/tmp/remote-cache")))
  // setup remote cache
  // pushRemoteCacheTo := Some(MavenCache("local-cache", (ThisBuild / baseDirectory).value / "remote-cache")),
  //  Compile / pushRemoteCacheConfiguration := (Compile / pushRemoteCacheConfiguration).value.withOverwrite(true),
  //  Test / pushRemoteCacheConfiguration := (Compile / pushRemoteCacheConfiguration).value.withOverwrite(true),
  // ThisBuild / pushRemoteCacheTo := Some(
  //  MavenCache("local-cache", baseDirectory.value / "sbt-cache")
  // )
)

//packageOptions
// Run tests in a separate JVM to prevent resource leaks.
//ThisBuild / Test / fork := true
//usePipelining := true
lazy val root = (project in file(".")).settings(
  libraryDependencies ++= Seq(
    "org.typelevel"  %% "cats-core"           % "2.9.0",
    ("org.typelevel" %% "cats-effect"         % "3.5.0").withSources().withJavadoc(),
    "org.typelevel"  %% "cats-effect-cps"     % "0.4.0",
    "org.http4s"     %% "http4s-ember-client" % http4sVersion,
    "org.http4s"     %% "http4s-ember-server" % http4sVersion,
    "org.http4s"     %% "http4s-dsl"          % http4sVersion,
    "org.typelevel"  %% "log4cats-noop"       % "2.7.0",
    "org.http4s"     %% "http4s-circe"        % http4sVersion,
    "io.circe"       %% "circe-generic"       % circeVersion,
    "io.circe"       %% "circe-literal"       % circeVersion,
    `munit-cats-effect`,
    `http4s-munit`,
    munit
  ),
  // scalacOptions ++= Seq(
  //    "-feature",
  //    "-deprecation",
  //    "-unchecked",
  //    "-language:postfixOps",
  //    "-Xasync"
  //  ),
  compilationCacheSettings
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

//coverageEnabled := true

// unmanagedSources / includeFilter := ???

// Test / parallelExecution := true
// Test / fork              := true

//      Compile / packageCache / pushRemoteCacheArtifact := true

//      Test / packageCache / pushRemoteCacheArtifact := true

//  inThisBuild(
//    Seq(
//      version           := "0.1.0-SNAPSHOT",
//      scalaVersion      := "2.13.10",
//      semanticdbEnabled := true,
//      semanticdbVersion := scalafixSemanticdb.revision,
//      pushRemoteCacheTo := Some(MavenCache("local-cache", file("/tmp/sbt-remote-cache"))),
//    )
//  )

// val r = remoteCacheResolvers.value.head
//        val p = remoteCacheProjectId.value
//        val ids = remoteCacheIdCandidates.value
//        val is = (pushRemoteCache / ivySbt).value

// pushRemoteCacheConfiguration / publishMavenStyle := true
//    Compile / packageCache / pushRemoteCacheArtifact := true
//    Test / packageCache / pushRemoteCacheArtifact := true
//    Compile / packageCache / artifact := Artifact(moduleName.value, cachedCompileClassifier)
//    Test / packageCache / artifact := Artifact(moduleName.value, cachedTestClassifier)
//    remoteCachePom / pushRemoteCacheArtifact := true

//compile :=compile.dependsOn(pullRemoteCache).value

// ThisBuild / publishMavenStyle := true

//To disable checksum checking during update:

///update / checksums := Nil
//To disable checksum creation during artifact publishing:

//publishLocal / checksums := Nil

//publish / checksums := Nil
//The default value is:

//checksums := Seq("sha1", "md5")

//conflictManager := ConflictManager.strict
logLevel := Level.Debug
incOptions := incOptions
  .value
  .withIgnoredScalacOptions(
    incOptions.value.ignoredScalacOptions ++ Array(
      "-Xplugin:.*",
      "-Ybackend-parallelism [\\d]+"
    )
  )
  .withApiDebug(true)

//incOptions := incOptions.value.withLogRecompileOnMacro(true)

// incOptions := incOptions.value.withApiDebug(true)

addCommandAlias("ci", "clean; pullRemoteCache; test")

//externalResolvers := Resolver.withDefaultResolvers(resolvers.value, mavenCentral = false)

//resolvers += "Nexus" at "http://localhost:8081/nexus/content/groups/public"
//commands ++= Seq()
//addCommandAlias("runLinter", ";scalafixAll --rules OrganizeImports")

// (sources in Compile) :=
//   (managedSources in Compile).value ++
//   (unmanagedSources in Compile).value
