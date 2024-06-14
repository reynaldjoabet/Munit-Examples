# Munit-Examples


Test coverage is, without a doubt, one of the best ways to measure if we have enough tests and to detect if there are any untested areas in the application.


Code coverage is a very good metric for analyzing the coverage of the tests in the codebase. It helps to evaluate the effectiveness of the testing process. Moreover, it highlights the areas of code that aren’t covered with tests.

There are two main types of code coverage. Firstly, statement coverage checks the percentage of lines of code covered using the tests. Secondly, branch coverage calculates the test coverage for the different conditions within the codebase. By combining these two types, we can gain a comprehensive understanding of the total test coverage.


Some of the advantages of using scoverage are:

Support for statement and branch coverage
Integration with different build tools such as sbt, Maven, Gradle, and so on
HTML and XML-based coverage reports
Good customizable options
Works with all Scala testing libraries such as ScalaTest, uTest, MUnit, and so on, as it relies on bytecode instrumentation for code coverage checks.


## syntax
```scala
  import cats.instances.AllInstances
  import cats.syntax.all
  ````

  Let’s quickly review Scala 2’s imports and implicits! In Scala, imports are used for two purposes:

  To include names of values and types into the scope.
  To include implicits into the scope.
  Given some type A, implicit is a mechanism to ask the compiler for a specific (term) value for the type. This can be used for different purposes, for Cats, the 2 main usages are:

  instances; to provide typeclass instances.
  syntax; to inject methods and operators. (method extension)
  Implicits are selected in the following precedence:

  Values and converters accessible without prefix via local declaration, imports, outer scope, inheritance, and current package object. Inner scope can shadow values when they are named the same.
  Implicit scope. Values and converters declared in companion objects and package object of the type, its parts, or super types.

```scala
  package cats

  object implicits extends syntax.AllSyntax with instances.AllInstances
  ```
  This is quite a nice way of organizing the imports. implicits object itself doesn’t define anythig and it just mixes in the traits. We are going to look at each traits in detail, but they can also be imported a la carte, dim sum style


##### cats.instances.AllInstances 
Thus far, I have been intentionally conflating the concept of typeclass instances and method injection (aka enrich my library). But the fact that (Int, +) forms a Monoid and that Monoid introduces |+| operator are two different things.

One of the interesting design of Cats is that it rigorously separates the two concepts into “instance” and “syntax.”

AllInstances is a trait that mixes in all the typeclass instances for built-in datatypes such as Either[A, B] and Option[A]

#### cats.syntax.AllSyntax 
AllSyntax is a trait that mixes in all of the operators available in Cats.



#### typeclass instances 
As I mentioned above, after Cats 2.2.0, you typically don’t have to do anything to get the typeclass instances.
```scala
cats.Monad[Option].pure(0)
// res0: Option[Int] = Some(value = 0)
```
If you want to import typeclass instances for Option for some reason:
```scala
{
  import cats.instances.option._
  cats.Monad[Option].pure(0)
}
// res1: Option[Int] = Some(value = 0)
```

sbt plugins just define tasks and settings 
```scala
final class PublishConfiguration private (
  val publishMavenStyle: Boolean,
  val deliverIvyPattern: Option[String],
  val status: Option[String],
  val configurations: Option[scala.Vector[sbt.librarymanagement.ConfigRef]],
  val resolverName: Option[String],
  val artifacts: Vector[scala.Tuple2[sbt.librarymanagement.Artifact, java.io.File]],
  val checksums: scala.Vector[String],
  val logging: Option[sbt.librarymanagement.UpdateLogging],
  val overwrite: Boolean) extends Serializable {}

sbt:Munit-Examples> show Compile / pushRemoteCacheConfiguration
[warn] multiple main classes detected: run 'show discoveredMainClasses' to see the list
[info] PublishConfiguration(true, Some(~/downloads/munit-examples/target/scala-2.13/[artifact]-[revision](-[classifier]).[ext]), Some(release), Some(Vector(compile, runtime, test, provided, optional, compile-internal, runtime-internal, test-internal, plugin, pom, scala-tool, scala-doc-tool, sources, docs, scoveragePlugin)), Some(local-cache), Vector((Artifact(munit-examples, jar, jar, Some(cached-compile), Vector(), None, Map(), None, false),~/downloads/munit-examples/target/scala-2.13/munit-examples_2.13-1.0-cached-compile.jar)), Vector(sha1, md5), Some(Default), true)
```


  pushRemoteCacheArtifact            Enables publishing an artifact to remote cache.
```scala
[info]  Delegates:
[info]  pushRemoteCacheArtifact
[info]  ThisBuild / pushRemoteCacheArtifact
[info]  Global / pushRemoteCacheArtifact
[info] Related:
[info]  Test / packageCache / pushRemoteCacheArtifact
[info]  Compile / packageCache / pushRemoteCacheArtifact
[info]  remoteCachePom / pushRemoteCacheArtifact
```


```scala
[info]Dependencies:
[info]  Compile / pushRemoteCacheConfiguration / publishMavenStyle
[info]  Compile / crossTarget
[info]  Compile / pushRemoteCacheTo
[info]  Compile / isSnapshot
[info]  Compile / ivyLoggingLevel
[info]  Compile / ivyConfigurations
[info]  Compile / pushRemoteCacheConfiguration / checksums
[info]  Compile / pushRemoteCacheConfiguration / packagedArtifacts
[info] Delegates:
[info]  Compile / pushRemoteCacheConfiguration
[info]  pushRemoteCacheConfiguration
[info]  ThisBuild / Compile / pushRemoteCacheConfiguration
[info]  ThisBuild / pushRemoteCacheConfiguration
[info]  Zero / Compile / pushRemoteCacheConfiguration
[info]  Global / pushRemoteCacheConfiguration
[info] Related:
[info]  Test / pushRemoteCacheConfiguration
```
“Delegates”, all of the possible delegate candidates listed in the order of precedence!
```scala
[info] Dependencies:
[info]  Compile / sourceGenerators
[info]  Compile / managedFileStampCache
[info]  Compile / inputFileStamper
[info] Reverse dependencies:
[info]  Compile / bspBuildTargetSourcesItem
[info]  Compile / managedSourcePaths
[info]  Compile / sources
[info] Delegates:
[info]  Compile / managedSources
[info]  managedSources
[info]  ThisBuild / Compile / managedSources
[info]  ThisBuild / managedSources
[info]  Zero / Compile / managedSources
[info]  Global / managedSources
[info] Related:
[info]  Test / managedSources
```


The inspect tree command shows a whole tree of task dependencies for a particular task. If we inspect the tree for the unmanagedSources task, we can see it here:
```scala
sbt:Munit-Examples> inspect tree unmanagedSources
[info] Compile / unmanagedSources = Task[scala.collection.Seq[java.io.File]]
[info]   +-Compile / unmanagedSources / inputFileStamps = Task[scala.collection.Seq[s..
[info]     +-Global / state = Task[sbt.State]
[info]     +-Compile / unmanagedSources / allInputPathsAndAttributes = Task[scala.col..
[info]     | +-Global / state = Task[sbt.State]
[info]     | +-Global / dynamicInputs = Task[scala.Option[scala.collection.mutable.Se..
[info]     | +-Compile / unmanagedSources / fileInputs = List(~/downloads/..
[info]     | | +-baseDirectory = 
[info]     | | +-Global / sourcesInBase = true
[info]     | | +-Compile / unmanagedSourceDirectories = List(~/downloads/P..
[info]     | | | +-Global / crossPaths = true
[info]     | | | +-Compile / javaSource = src/main/java
[info]     | | | | +-Compile / sourceDirectory = src/main
[info]     | | | |   +-Compile / configuration = compile
[info]     | | | |   +-sourceDirectory = src
[info]     | | | |     +-baseDirectory = 
[info]     | | | |       +-thisProject = Project(id root, base: ~/Deskto..
[info]     | | | |       
[info]     | | | +-pluginCrossBuild / sbtBinaryVersion = 1.0
[info]     | | | +-Global / sbtPlugin = false
[info]     | | | +-ThisBuild / scalaBinaryVersion = 2.13
[info]     | | | +-Compile / scalaSource = src/main/scala
[info]     | | | | +-Compile / sourceDirectory = src/main
[info]     | | | |   +-Compile / configuration = compile
[info]     | | | |   +-sourceDirectory = src
[info]     | | | |     +-baseDirectory = 
[info]     | | | |       +-thisProject = Project(id root, base: ~/Deskto..
[info]     | | | |       
[info]     | | | +-ThisBuild / scalaVersion = 2.13.13
[info]     | | | 
[info]     | | +-Global / excludeFilter = HiddenFileFilter
[info]     | | +-Zero / unmanagedSources / includeFilter = ExtensionFilter(java,scala)
[info]     | | 
[info]     | +-Global / fileTreeView = Task[sbt.nio.file.FileTreeView[scala.Tuple2[ja..
[info]     | +-Global / inputFileStamper = Hash
[info]     | +-Compile / unmanagedSources / watchForceTriggerOnAnyChange = false
[info]     | 
[info]     +-Global / fileInputExcludeFilter = IsDirectory || HiddenFileFilter
[info]     +-Global / fileInputIncludeFilter = AllPass
[info]     +-Global / inputFileStamper = Hash
[info]     +-Global / unmanagedFileStampCache = Task[sbt.nio.FileStamp$Cache]
[info]     
```

unmanagedBase

```scala
info] ~/downloads/munit-examples/lib
```

To change the directory jars are stored in, change the unmanagedBase setting in your project definition. For example, to use custom_lib/:

```scala
unmanagedBase := baseDirectory.value / "custom_lib"
```


Declaring a dependency looks like:
```scala
libraryDependencies += groupID % artifactID % revision
or

libraryDependencies += groupID % artifactID % revision % configuration
```


Resolvers 
sbt uses the standard Maven2 repository by default.

Declare additional repositories with the form:

`resolvers += name at location`

sbt can search your local Maven repository if you add it as a repository:

`resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"`


he publish action is used to publish your project to a remote repository. To use publishing, you need to specify the repository to publish to and the credentials to use. Once these are set up, you can run publish.

The publishLocal action is used to publish your project to your Ivy local file repository, which is usually located at $HOME/.ivy2/local/. You can then use this project from other projects on the same machine.

To avoid publishing a project, add the following setting to the subprojects that you want to skip:

`publish / skip := true`



sbt:Munit-Examples> show remoteCacheId



 pullRemoteCache                           Retrieve remote cache.
  pushRemoteCache                           Push remote cache to the cache server.
  pushRemoteCacheConfiguration              
  remoteCacheArtifact                       The remote cache artifact definition.
  remoteCacheArtifacts                      Remote cache artifact definitions.
  remoteCacheId                             Unique identifier for the remote cache.
  remoteCacheIdCandidates                   Remote cache ids to pull.
  remoteCachePom                            Generates a pom for publishing when publishing Maven-style.
  remoteCacheProjectId

 Each component may be Zero (no specific value), This (current context), or Select (containing a specific value). sbt resolves This_ to either Zero or Select depending on the context.


 `jps -v | grep sbt-launch | cut -f1 -d ' ' | xargs kill -9` to shutdownall sbt servers

```scala
 sbt:Munit-Examples> inspect tree pushRemoteCache
[info] pushRemoteCache = Task[Unit]
[info]   +-pushRemoteCacheConfiguration / remoteCacheArtifacts = Task[scala.collectio..
[info]   | +-Compile / packageCache / pushRemoteCacheArtifact = true
[info]   | +-Compile / packageCache / remoteCacheArtifact = Task[sbt.internal.remotec..
[info]   | | +-Compile / classDirectory = target/scala-2.13/classes
[info]   | | +-Compile / compileAnalysisFile = Task[java.io.File]
[info]   | | +-moduleName = munit-examples
[info]   | | 
[info]   | +-Test / packageCache / pushRemoteCacheArtifact = false
[info]   | +-Test / packageCache / remoteCacheArtifact = Task[sbt.internal.remotecach..
[info]   |   +-Test / classDirectory = target/scala-2.13/test-classes
[info]   |   +-Test / compileAnalysisFile = Task[java.io.File]
[info]   |   +-moduleName = munit-examples
[info]   |   +-Test / test / streams = Task[sbt.std.TaskStreams[sbt.internal.util.Ini..
[info]   |     +-Global / streamsManager = Task[sbt.std.Streams[sbt.internal.util.Ini..
[info]   |     
[info]   +-Global / settingsData = Task[sbt.internal.util.Settings[sbt 
 ```

 `Test / packageCache / pushRemoteCacheArtifact`  defaults to true, set it to false to only push compiled artifacts and not the test artifacts


#### Aggregation 
Aggregation means that running a task on the aggregate project will also run it on the aggregated projects. For example,
```scala
lazy val root = (project in file("."))
  .aggregate(util, core)

lazy val util = (project in file("util"))

lazy val core = (project in file("core"))
```


#### Classpath dependencies 
A project may depend on code in another project. This is done by adding a dependsOn method call. For example, if `core` needed `util` on its classpath, you would define core as:

```scala
lazy val core = project.dependsOn(util)
```

Now code in `core` can use classes from `util`. This also creates an ordering between the projects when compiling them; `util` must be updated and compiled before `core` can be compiled

#### Per-configuration classpath dependencies

`core dependsOn(util)` means that the compile configuration in core depends on the compile configuration in util. You could write this explicitly as `dependsOn(util % "compile->compile")`.

The `->` in `"compile->compile"` means “depends on” so `"test->compile"` means the test configuration in core would depend on the compile configuration in `util`.

Omitting the `->config` part implies `->compile`, so `dependsOn(util % "test")` means that the `test` configuration in `core` depends on the `Compile` configuration in `util`.

A useful declaration is "test->test" which means test depends on test. This allows you to put utility code for testing in util/src/test/scala and then use that code in core/src/test/scala, for example.


```scala
final case class Contributor(
    login: String,
    contributions: Int,
    url: String,
    avatar: Option[String]
)

object Contributor {

  implicit val ContributorDecoder: Decoder[Contributor] = json =>
    for {
      login         <- json.get[String]("login")
      contributions <- json.get[Int]("contributions")
      url           <- json.get[String]("html_url")
      avatar        <- json.get[Option[String]]("avatar_url")
    } yield Contributor(login, contributions, url, avatar.filter(_.nonEmpty))

}
```

```scala

final case class Scope(
    project: ScopeAxis[Reference],
    config: ScopeAxis[ConfigKey],
    task: ScopeAxis[AttributeKey[_]],
    extra: ScopeAxis[AttributeMap]
)

object Scope {
  val ThisScope: Scope = Scope(This, This, This, This)
  val Global: Scope = Scope(Zero, Zero, Zero, Zero)
  val GlobalScope: Scope = Global
}

sealed trait ScopeAxis[+S] {}


/**
 * This is a scope component that represents not being
 * scoped by the user, which later could be further scoped automatically
 * by sbt.
 */
case object This extends ScopeAxis[Nothing]


/**
 * Zero is a scope component that represents not scoping.
 * It is a universal fallback component that is strictly weaker
 * than any other values on a scope axis.
 */
case object Zero extends ScopeAxis[Nothing]
/**
 * Select is a type constructor that is used to wrap type `S`
 * to make a scope component, equivalent of Some in Option.
 */
final case class Select[S](s: S) extends ScopeAxis[S]

```
These dependencies are provided, because they should not be packaged into the JAR file


A task can be scoped. When a task depends on another task, it can depend on that task in a particular scope.


Scoping by another task is incredibly useful

what it means is that the same task key can be used and explicitly configured for many tasks


```scala
sbt:Munit-Examples> inspect tree test:sources
[info] Test / sources = Task[scala.collection.Seq[java.io.File]]
[info]   +-Test / managedSources = Task[scala.collection.Seq[java.io.File]]
[info]   | +-Global / inputFileStamper = Hash
[info]   | +-Global / managedFileStampCache = Task[sbt.nio.FileStamp$Cache]
[info]   | +-Test / sourceGenerators = List()
[info]   | 
[info]   +-Test / unmanagedSources = Task[scala.collection.Seq[java.io.File]]
[info]     +-Test / unmanagedSources / inputFileStamps = Task[scala.collection.Seq[sc..
[info]       +-Global / state = Task[sbt.State]
[info]       +-Test / unmanagedSources / allInputPathsAndAttributes = Task[scala.coll..
[info]       | +-Global / state = Task[sbt.State]
[info]       | +-Global / dynamicInputs = Task[scala.Option[scala.collection.mutable...
[info]       | +-Test / unmanagedSources / fileInputs = List(/Users/locatiron/P..
[info]       | | +-baseDirectory = 
[info]       | | +-Global / sourcesInBase = true
[info]       | | +-Test / unmanagedSourceDirectories = List(~/location/Pr..
[info]       | | | +-Global / crossPaths = true
[info]       | | | +-Test / javaSource = src/test/java
[info]       | | | | +-Test / sourceDirectory = src/test
[info]       | | | |   +-Test / configuration = test
[info]       | | | |   +-sourceDirectory = src
[info]       | | | |     +-baseDirectory = 
[info]       | | | |       +-thisProject = Project(id root, base: /location/..
[info]       | | | |       
[info]       | | | +-pluginCrossBuild / sbtBinaryVersion = 1.0
[info]       | | | +-Global / sbtPlugin = false
[info]       | | | +-ThisBuild / scalaBinaryVersion = 2.13
[info]       | | | +-Test / scalaSource = src/test/scala
[info]       | | | | +-Test / sourceDirectory = src/test
[info]       | | | |   +-Test / configuration = test
[info]       | | | |   +-sourceDirectory = src
[info]       | | | |     +-baseDirectory = 
[info]       | | | |       +-thisProject = Project(id root, base: /location/..
[info]       | | | |       
[info]       | | | +-ThisBuild / scalaVersion = 2.13.13
[info]       | | | 
[info]       | | +-Global / excludeFilter = HiddenFileFilter
[info]       | | +-Zero / unmanagedSources / includeFilter = ExtensionFilter(java,sca..
[info]       | | 
[info]       | +-Global / fileTreeView = Task[sbt.nio.file.FileTreeView[scala.Tuple2[..
[info]       | +-Global / inputFileStamper = Hash
[info]       | +-Test / unmanagedSources / watchForceTriggerOnAnyChange = false
[info]       | 
[info]       +-Global / fileInputExcludeFilter = IsDirectory || HiddenFileFilter
[info]       +-Global / fileInputIncludeFilter = AllPass
[info]       +-Global / inputFileStamper = Hash
[info]       +-Global / unmanagedFileStampCache = Task[sbt.nio.FileStamp$Cache]
[info]       
```

 In the above tree we can see that `unmanagedSources` depends on `includeFilter` scoped to the `unmanagedSources` task. `includeFilter` may also be used elsewhere, for example, in discovering resources, in that case it will be scoped to the `unmanagedResources` task.


 To apply a scope to a setting, you can use the in method:

`sources in Compile += file("src/other/scala/Other.scala")`

Applying multiple scopes can be done by using multiple in calls, for example:

`excludeFilter in sbtFunProject in unmanagedSources in Compile := "_*"`

Or, they can also be done by passing multiple scopes to the in method, in the order project, configuration then task:

`excludeFilter in (sbtFunProject, Compile, unmanagedSources) := "_*"`

The same syntax can be used when depending on settings, though make sure you put parenthesis around the whole scoped setting in order to invoke the value method on it:

```scala
(sources in Compile) := 
  (managedSources in Compile).value ++ 
  (unmanagedSources in Compile).value```

  