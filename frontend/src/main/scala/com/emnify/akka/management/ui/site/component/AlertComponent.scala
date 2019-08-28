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

import rx.{Rx, Var}
import scalatags.JsDom.all._

trait AlertComponent extends WebComponent {
  self: WebComponent =>

  lazy val alert: Var[Option[String]] = Var(None)

  lazy val renderAlert = Rx {
    div(
      alert().getOrElse[String](""),
      `class` := s"alert alert-danger ${alert().map(_ => "").getOrElse("in")}visible"
    ).render
  }
}
