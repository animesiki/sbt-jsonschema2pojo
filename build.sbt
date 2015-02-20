import bintray.Keys._

sbtPlugin := true

version := "0.1"

name := "sbt-jsonschema2pojo"

organization := "com.github.catap"

publishMavenStyle := false

libraryDependencies += "org.jsonschema2pojo" % "jsonschema2pojo-core" % "0.4.7"

bintrayPublishSettings

repository in bintray := "sbt-plugins"

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))

bintrayOrganization in bintray := None
