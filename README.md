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


coverage                        coverageExcludedPackages          coverageMinimumBranchTotal        coverageOn                        coverageOutputXML
coverageAggregate                 coverageFailOnMinimum             coverageMinimumStmtPerFile        coverageOutputCobertura           coverageReport
coverageDataDir                   coverageHighlighting              coverageMinimumStmtPerPackage     coverageOutputDebug               coverageScalacPluginVersion
coverageEnabled                   coverageMinimumBranchPerFile      coverageMinimumStmtTotal          coverageOutputHTML   



sbt plugins just define tasks and settings 