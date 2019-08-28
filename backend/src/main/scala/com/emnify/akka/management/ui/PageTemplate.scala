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

package com.emnify.akka.management.ui

import scalatags.Text.all._
import scalatags.Text.tags2.title

object PageTemplate {
  def apply() = {
    val stylesheets = List(
      "public/akka-management-ui-frontend.css"
    )
    val scripts = List(
      "public/akka-management-ui-frontend-jsdeps.min.js",
      "public/akka-management-ui-frontend-opt.js"
    )
    html(
      lang := "en",
      head(
        meta(charset := "UTF-8"),
        meta(name := "viewport", content := "width=device-width, initial-scale=1, shrink-to-fit=no"),
        title("Akka Management UI"),
        stylesheets.map(s => link(rel := "stylesheet", href := s))
      ),
      body(
        div(id := "app"),
        scripts.map(s => script(`type` := "text/javascript", src := s))
      )
    )
  }
}
