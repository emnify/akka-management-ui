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

package com.emnify.akka.management.ui.site

import com.emnify.akka.management.ui._
import com.emnify.akka.management.ui.site.component._
import org.scalajs.dom.document
import scalatags.JsDom.all._
import scalatags.rx.all._

import scala.concurrent.ExecutionContext

case class AkkaManagementSite(c: ClusterHttpManagementUiConfig)(implicit val ec: ExecutionContext) extends Site {
  private val applicationContext = new AnyRef
    with AlertComponent
    with ExecutionContextComponent
    with ClusterMemberComponent
    with ClusterMemberTableComponent
    with ConfigurationComponent {
    override lazy val executionContext = ec

    override lazy val config = c
  }

  override def render() = {
    import applicationContext.owner
    document
      .getElementById("app")
      .appendChild(
        div(
          applicationContext.renderAlert,
          h2("Cluster Members"),
          applicationContext.renderMembersTable,
          h2("Unreachable Cluster Members"),
          applicationContext.renderUnreachableTable,
          `class` := "mt-1 container"
        ).render
      )
  }
}
