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

import com.typesafe.config.{Config, ConfigValueFactory}

import scala.collection.JavaConverters._

package object example {

  implicit class AkkaConfig(conf: Config) {
    def withAkkaPortIncrement(i: Int) = {
      val port = conf.getInt("akka.remote.netty.tcp.port")
      conf.withValue("akka.remote.netty.tcp.port", ConfigValueFactory.fromAnyRef(port + i))
    }

    def withDc = {
      val dcs = Array("wuerzburg", "berlin")
      val port = conf.getInt("akka.remote.netty.tcp.port")
      conf.withValue(
        "akka.cluster.multi-data-center.self-data-center",
        ConfigValueFactory.fromAnyRef(dcs(port % dcs.length))
      )
    }

    def withRoles(roles: String*) = {
      conf.withValue("akka.cluster.roles", ConfigValueFactory.fromIterable(roles.asJava))
    }
  }

}
