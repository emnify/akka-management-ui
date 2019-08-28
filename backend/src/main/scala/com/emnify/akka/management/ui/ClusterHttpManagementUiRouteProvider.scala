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

import akka.actor.ExtendedActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.Marshaller.StringMarshaller
import akka.http.scaladsl.marshalling.ToEntityMarshaller
import akka.http.scaladsl.model.MediaTypes.`text/html`
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.management.scaladsl.{ManagementRouteProvider, ManagementRouteProviderSettings}
import org.webjars.WebJarAssetLocator
import scalatags.Text.all._
import spray.json.DefaultJsonProtocol

import scala.util.Try

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val configFormat = jsonFormat2(ClusterHttpManagementUiConfig)
}

class ClusterHttpManagementUiRouteProvider(system: ExtendedActorSystem) extends ManagementRouteProvider with JsonSupport {
  implicit val ScalaTagsMarshaller: ToEntityMarshaller[Tag] = (StringMarshaller wrap `text/html`) {
    case html if html.tag == "html" => s"<!DOCTYPE html>${html.render}"
    case tag => tag.render
  }

  val webjarLocator = new WebJarAssetLocator

  override def routes(settings: ManagementRouteProviderSettings): Route = {
    val conf = system.settings.config
    val configuration = ClusterHttpManagementUiConfig(
      updateInterval = conf.getDuration("akka.management.ui.update-interval").toMillis.toInt,
      readOnly = settings.readOnly
    )

    pathPrefix("ui") {
      (get & pathEndOrSingleSlash & redirectToTrailingSlashIfMissing(StatusCodes.PermanentRedirect)) {
        complete(PageTemplate())
      } ~ (get & path("config")) {
        complete(configuration)
      } ~ (get & path("public" / Segment)) { p =>
        Try(webjarLocator.getFullPath(p))
          .map(getFromResource)
          .getOrElse(complete(StatusCodes.NotFound))
      }
    }
  }
}
