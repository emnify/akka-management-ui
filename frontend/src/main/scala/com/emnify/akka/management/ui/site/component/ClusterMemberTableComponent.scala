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

import org.scalajs.dom.window
import rx.Rx
import scalatags.JsDom.all._

trait ClusterMemberTableComponent {
  self: ClusterMemberComponent with WebComponent with ConfigurationComponent =>

  private def confirm(s: String)(f: => Unit): Unit = {
    if (window.confirm(s)) {
      f
    } else {
      ()
    }
  }

  private def confirm(action: String, node: String)(f: => Unit): Unit = confirm(s"""Please confirm triggering "$action" on $node.""")(f)

  private def membersButton(member: String) = {
    val leaveButtonBase = button(
      "Leave",
      `class` := "btn btn-sm btn-warning",
      `type` := "button",
      onclick := (() => confirm("leave", member) {
        memberLeave(member)
      })
    )
    val downButtonBase = button(
      "Down",
      `class` := "btn btn-sm btn-danger",
      `type` := "button",
      onclick := (() => confirm("down", member) {
        memberDown(member)
      })
    )

    val (leaveButton, downButton) = if (config.readOnly) {
      (leaveButtonBase(disabled), downButtonBase(disabled))
    } else {
      (leaveButtonBase, downButtonBase)
    }

    td(
      width := "1px",
      whiteSpace := "nowrap",
      leaveButton,
      raw("&nbsp;"),
      downButton
    )
  }

  lazy val renderMembersTable = Rx {
    def optionalBadge(c: Boolean)(s: String) = {
      if (c) {
        Some(span(s, `class` := "badge badge-primary ml-1"))
      } else {
        None
      }
    }

    val rows = members().members.toList.sortBy(_.node).map { member =>
      val selfBadge = optionalBadge(members().selfNode == member.node)("Self")
      val leaderBadge = optionalBadge(members().leader == Some(member.node))("Leader")
      val oldestBadge = optionalBadge(members().oldest == Some(member.node))("Oldest")
      val badges = List(selfBadge, oldestBadge, leaderBadge).flatten
      val roles = member.roles.toList.sorted.mkString(", ")
      tr(
        td(span(member.node), badges),
        td(member.status),
        td(roles),
        membersButton(member.node),
        `class` := member.classes
      )
    }

    table(
      thead(tr(th("Name"), th("Status"), th("Roles"), th("Actions", width := "1px", whiteSpace := "nowrap"))),
      tbody(rows.toList),
      `class` := "table table-striped table-bordered table-hover"
    ).render
  }

  lazy val renderUnreachableTable = Rx {
    val rows = members().unreachable.toList.sortBy(_.node).map { unreachable =>
      tr(
        td(unreachable.node),
        td(
          span(s"${unreachable.observedBy.size} / ${members().members.size}", `class` := "badge badge-primary mr-1"),
          span(unreachable.observedBy.toList.sorted.mkString(", "))
        ),
        membersButton(unreachable.node),
        `class` := "table-danger"
      )
    }

    table(
      thead(tr(th("Name"), th("Observers"), th("Actions", width := "1px", whiteSpace := "nowrap"))),
      tbody(rows.toList),
      `class` := "table table-striped table-bordered table-hover"
    ).render
  }
}
