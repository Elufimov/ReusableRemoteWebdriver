import scalariform.formatter.preferences._

name := "ReusableRemoteWebDriver"

version := "0.1"

scalaVersion := "2.11.7"

scalariformPreferences := scalariformPreferences.value
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(RewriteArrowSymbols, true)
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(IndentPackageBlocks, true)

libraryDependencies ++= Seq(
  "org.seleniumhq.selenium" % "selenium-java" % "2.47.1",
  "com.github.pathikrit" %% "better-files" % "2.8.1"
)