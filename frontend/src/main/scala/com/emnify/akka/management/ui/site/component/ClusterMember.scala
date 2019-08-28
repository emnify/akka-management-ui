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

trait ClusterMember {
  val node: String

  def classes: String
}

case class ClusterUnreachableMember(node: String, observedBy: Seq[String]) extends ClusterMember {
  override def classes: String = "table-danger"
}

case class ClusterReachableMember(node: String, nodeUid: String, status: String, roles: Set[String]) extends ClusterMember {
  override def classes = status match {
    case "Up" => "table-success"
    case "Joining" | "WeaklyUp" => "table-info"
    case "Leaving" | "Exiting" => "table-warning"
    case "Down" | "Removed" => "table-danger"
    case _ => ""
  }
}

case class ClusterMembers(selfNode: String,
                          members: Set[ClusterReachableMember],
                          unreachable: Seq[ClusterUnreachableMember],
                          leader: Option[String],
                          oldest: Option[String],
                          oldestPerRole: Map[String, String])
