logLevel := Level.Warn

resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.5.1")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.5.1")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")