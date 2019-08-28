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
import akka.http.scaladsl.model.{StatusCodes, Uri}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.management.scaladsl.ManagementRouteProviderSettings
import org.scalatest.FunSpec

class ClusterHttpManagementUiRouteProviderSpec extends FunSpec with ScalatestRouteTest with JsonSupport {
  def routes(readOnly: Boolean = false) = new ClusterHttpManagementUiRouteProvider(system.asInstanceOf[ExtendedActorSystem]).routes(ManagementRouteProviderSettings(Uri("http://localhost"), readOnly))

  describe("ClusterHttpManagementUiRouteProvider") {
    it("should redirect without ending slash") {
      Get("/ui") ~> routes() ~> check {
        assert(status === StatusCodes.PermanentRedirect)
        assert(header("Location").get.value() === s"http://${DefaultHostInfo.defaultHost.host.host.address()}/ui/")
      }
    }

    it("should return html with ending slash") {
      Get("/ui/") ~> routes() ~> check {
        assert(status === StatusCodes.OK)
        assert(responseAs[String] === "<!DOCTYPE html>" + PageTemplate().render)
      }
    }

    it("should return the config for read-only http management") {
      Get("/ui/config") ~> routes(true) ~> check {
        assert(responseAs[ClusterHttpManagementUiConfig] === ClusterHttpManagementUiConfig(1000, true))
      }
    }

    it("should return the config for http management") {
      Get("/ui/config") ~> routes(false) ~> check {
        assert(responseAs[ClusterHttpManagementUiConfig] === ClusterHttpManagementUiConfig(1000, false))
      }
    }

    it("should not return listing of public resources") {
      List("/ui/public", "/ui/public/").foreach { path =>
        Get(path) ~> Route.seal(routes()) ~> check {
          assert(status === StatusCodes.NotFound)
        }
      }
    }

    it("should return public resources") {
      val resources = List(
        "akka-management-ui-frontend.css",
        "akka-management-ui-frontend-jsdeps.min.js",
        "akka-management-ui-frontend-opt.js"
      )
      resources.foreach { resource =>
        Get(s"/ui/public/${resource}") ~> routes() ~> check {
          assert(status === StatusCodes.OK)
          assert(response.entity.contentLengthOption.get > 0)
        }
      }
    }
  }
}
