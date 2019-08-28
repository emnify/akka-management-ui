/*
 * Copyright 2019 EMnify GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}
import ReleaseTransformations._

lazy val commonSettings = Seq(
  organization := "com.emnify.akka.management.ui",
  organizationName := "EMnify GmbH",
  organizationHomepage := Some(url("https://www.emnify.com/")),
  startYear := Some(2019),
  licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/EMnify/akka-management-ui"),
      "scm:git@github.com:EMnify/akka-management-ui.git"
    )
  ),
  developers := List(
    Developer("mkroli", "Michael Krolikowski", "michael.krolikowski@emnify.com", url("https://github.com/mkroli"))
  ),
  publishMavenStyle := true,
  bintrayPackage := "akka-management-ui"
)

lazy val commonJVMSettings = Seq(
  scalaVersion := "2.12.8",
  crossScalaVersions := List(
    "2.13.0",
    "2.12.8",
    "2.11.12"
  ),
  scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation") ++ (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 10 | 11)) => Seq("-target:jvm-1.6")
    case _ => Seq("-target:jvm-1.8")
  })
)

lazy val commonJSSettings = Seq(
  scalaVersion := "2.12.8",
  crossScalaVersions := Nil,
  publish / skip := true
)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .settings(commonSettings)
  .settings(
    name := "akka-management-ui-shared"
  )
  .jvmSettings(commonJVMSettings)
  .jsSettings(commonJSSettings)

lazy val sharedJS = shared.js

lazy val sharedJVM = shared.jvm

lazy val frontend = project.in(file("frontend"))
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb)
  .settings(commonSettings)
  .settings(commonJSSettings)
  .settings(
    name := "akka-management-ui-frontend",
    crossScalaVersions := Nil,
    publish / skip := true,
    scalaJSUseMainModuleInitializer := true,
    scalaJSStage in Global := FullOptStage,
    scalaJSLinkerConfig in(Compile, fullOptJS) ~= {
      _.withSourceMap(false)
    },
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "scalarx" % "0.4.0",
      "com.lihaoyi" %%% "scalatags" % "0.7.0",
      "com.timushev" %%% "scalatags-rx" % "0.4.0",
      "io.circe" %%% "circe-core" % "0.11.1",
      "io.circe" %%% "circe-generic" % "0.11.1",
      "io.circe" %%% "circe-parser" % "0.11.1"
    ),
    jsDependencies ++= Seq(
      "org.webjars" % "jquery" % "3.4.1" / "jquery.slim.min.js",
      "org.webjars" % "popper.js" % "1.15.0" / "umd/popper.min.js",
      "org.webjars" % "bootstrap" % "4.3.1" / "bootstrap.min.js"
    ),
    skip in packageJSDependencies := true
  )
  .dependsOn(sharedJS)

lazy val backend = project.in(file("backend"))
  .enablePlugins(SbtWeb)
  .settings(commonSettings)
  .settings(commonJVMSettings)
  .settings(
    name := "akka-management-ui",
    libraryDependencies ++= Seq(
      "com.lightbend.akka.management" %% "akka-management-cluster-http" % "1.0.1",
      "org.webjars" % "webjars-locator-core" % "0.38",
      "com.lihaoyi" %% "scalatags" % (CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 11)) => "0.6.8"
        case _ => "0.7.0"
      }),
      "org.webjars" % "bootstrap" % "4.3.1" % "compile-internal"
    ),
    scalaJSProjects := Seq(frontend),
    Concat.groups := Seq(
      "akka-management-ui-frontend.css" -> group(
        (WebKeys.webJarsDirectory in Assets).value / (WebKeys.webModulesLib in Assets).value / "bootstrap" / "css" * "bootstrap.min.css"
      )
    ),
    pipelineStages in Assets := Seq(scalaJSPipeline, concat)
  )
  .dependsOn(sharedJVM)

lazy val example = project.in(file("example"))
  .settings(commonSettings)
  .settings(commonJVMSettings)
  .settings(
    name := "akka-management-ui-example",
    crossScalaVersions := Nil,
    publish / skip := true
  )
  .dependsOn(backend)

lazy val root = project.in(file("."))
  .aggregate(sharedJS, sharedJVM, frontend, backend, example)
  .settings(commonSettings)
  .settings(commonJVMSettings)
  .settings(
    name := "akka-management-ui-root",
    crossScalaVersions := Nil,
    publish / skip := true,
    releaseProcess := Seq(
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      releaseStepCommandAndRemaining("+ publishSigned"), // workaround for https://github.com/sbt/sbt-release/issues/214
      setNextVersion,
      commitNextVersion,
      pushChanges
    )
  )
