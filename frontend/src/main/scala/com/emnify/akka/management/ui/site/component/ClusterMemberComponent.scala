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

package com.emnify.akka.management.ui.site.component

import io.circe.generic.auto._
import io.circe.parser._
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw.FormData
import org.scalajs.dom.window
import rx.Var

import scala.util.{Failure, Success}

trait ClusterMemberComponent {
  self: AlertComponent with ExecutionContextComponent with ConfigurationComponent =>

  lazy val members = Var(ClusterMembers("", Set.empty, Seq.empty, None, None, Map.empty))

  def updateMembers(): Unit = {
    Ajax
      .get("../cluster/members")
      .map(_.responseText)
      .map(decode[ClusterMembers])
      .onComplete {
        case Failure(_) =>
          alert() = Some("Error loading cluster status")
        case Success(Left(_)) =>
          alert() = Some("Error reading response")
        case Success(Right(m)) =>
          alert() = None
          members() = m
      }
  }

  private def updateMember(member: String, operation: String): Unit = {
    val data = new FormData()
    data.append("operation", operation)
    Ajax
      .put(s"../cluster/members/${member}", data)
      .foreach(_ => updateMembers())
  }

  def memberLeave(member: String) = updateMember(member, "Leave")

  def memberDown(member: String) = updateMember(member, "Down")

  window.setInterval(() => updateMembers(), config.updateInterval)
  updateMembers()
}
