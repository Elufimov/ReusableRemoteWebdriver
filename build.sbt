import scalariform.formatter.preferences._

name := "ReusableRemoteWebDriver"

version := "0.2"

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

organization := "com.github.elufimov"

sonatypeProfileName := "com.github.elufimov"

pomExtra in Global := {
  <url>https://github.com/Elufimov/ReusableRemoteWebdriver</url>
    <licenses>
      <license>
        <name>MIT</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      </license>
    </licenses>
    <scm>
      <connection>scm:git@github.com:Elufimov/ReusableRemoteWebdriver.git</connection>
      <developerConnection>scm:git:git@github.com:Elufimov/ReusableRemoteWebdriver.git</developerConnection>
      <url>github.com/Elufimov/ReusableRemoteWebdriver</url>
    </scm>
    <developers>
      <developer>
        <id>elufimov</id>
        <name>Michael Elufimov</name>
        <url>https://github.com/Elufimov/ReusableRemoteWebdriver</url>
      </developer>
    </developers>
}