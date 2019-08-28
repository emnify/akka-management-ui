# Akka (Cluster) Management UI

[![Download](https://api.bintray.com/packages/mkroli/maven/akka-management-ui/images/download.svg)](https://bintray.com/mkroli/maven/akka-management-ui/_latestVersion)

## Usage
Adding akka-cluster-management-ui as dependency will automatically register its routes with [Akka Cluster HTTP Management](https://doc.akka.io/docs/akka-management/current/cluster-http-management.html). You can reach it like Akka Cluster HTTP Management by default at http://HOST:8558/ui.

### sbt
```
resolvers += "bintray-mkroli" at "https://dl.bintray.com/mkroli/maven"

libraryDependencies += "com.emnify.akka.management.ui" %% "akka-management-ui" % VERSION
```

### Maven
```xml
<repositories>
  <repository>
    <id>bintray-mkroli</id>
    <url>https://dl.bintray.com/mkroli/maven</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.emnify.akka.management.ui</groupId>
  <artifactId>akka-management-ui_2.13</artifactId>
  <version>VERSION</version>
</dependency>
```

### Configuration
The following settings can be configured (```src/main/resources/application.conf```):
```hocon
akka.management.ui {
  update-interval = 1s
}
```

## Running example
```
$ sbt "example / run"
...
[INFO] [08/26/2019 12:34:56.789] [example-akka.actor.default-dispatcher-1] [AkkaManagement(akka://example)] Bound Akka Management (HTTP) endpoint to: 1.2.3.4:8558
```

## License
Copyright (c) 2019 EMnify GmbH

Published under the [Apache License 2.0](LICENSE)
