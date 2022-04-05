[![Github Actions Status](https://github.com/joomcode/mongo-plan-checker/workflows/CI/badge.svg)](https://github.com/joomcode/mongo-plan-checker/actions)

Have you ever bring you mongo cluster to its knees rolling update with a query COLLSCAN-ing some collection?
This small library can prevent this. 

Use these wrappers for mongo-drivers in tests (you write tests for your 
code, aren't you?) and they will fail on collscans, sorts (for frequent queries it is preferred to have 
index ordering documents without need for separate heavy sort stage), excess reads (filtering too many 
documents read from disk and returning only a few) and broadcasts (if you have sharded mongodb cluster 
you don't want every your query to hit all shards)

This is done using separate explain query before executing the real one. So this is not applicable for higload
production environment.

# Usage
Currently you can get it from [jitpack.io](https://jitpack.io/)

Depending on what driver you use you need 
- `com.joom.mongo-plan-checker:mongo-plan-checker-sync42` that wraps `mongodb-driver-sync` version 4.2.x-4.x (latest supported)
- `com.joom.mongo-plan-checker:mongo-plan-checker-sync4` that wraps `mongodb-driver-sync` version 4.0.x-4.1.x
- `com.joom.mongo-plan-checker:mongo-plan-checker-sync` that wraps `mongodb-driver-sync` version 3.x
- `com.joom.mongo-plan-checker:mongo-plan-checker-reactivestreams42` that wraps `mongodb-driver-reactivestreams` version 4.2.x-4.x (latest supported)
- `com.joom.mongo-plan-checker:mongo-plan-checker-reactivestreams4` that wraps `mongodb-driver-reactivestreams` version 4.0.x-4.1.x
- `com.joom.mongo-plan-checker:mongo-plan-checker-reactivestreams` that wraps `mongodb-driver-reactivestreams` version 1.x
- `com.joom.mongo-plan-checker:mongo-plan-checker-async` that wraps `mongodb-driver-async` (that is an old driver deprecated in favor of reactivestreams)

Or you may use our extensions for spring-data
- `com.joom.mongo-plan-checker:mongo-plan-checker-sync-spring-data2` for spring-data version 2.x (used in spring-boot 2.2.x)

   and activate it similar to our sample [spring-boot22-sample](samples/spring-boot22-sync/src/test/java/com/github/joomcode/mongoplanchecker/sync/sample/PlanCheckerConfig.java) 
- `com.joom.mongo-plan-checker:mongo-plan-checker-sync-spring-data3` for spring-data version 3.0.x and 3.1.x (used in spring-boot 2.3.x and 2.4.x)

   and activate it similar to our sample [spring-boot23-sample](samples/spring-boot23-sync/src/test/java/com/github/joomcode/mongoplanchecker/sync/sample/PlanCheckerConfig.java) 
- `com.joom.mongo-plan-checker:mongo-plan-checker-sync-spring-data32` for spring-data version 3.2.x and onwards (used in spring-boot 2.5.x and 2.6.x)

  and activate it similar to our sample [spring-boot25-sample](samples/spring-boot25-sync/src/test/java/com/github/joomcode/mongoplanchecker/sync/sample/PlanCheckerConfig.java)
- `com.joom.mongo-plan-checker:mongo-plan-checker-reactivestreams-spring-data2` for spring-data version 2.x (used in spring-boot 2.2.x)

   and activate it similar to our sample [spring-boot22-reactive-sample](samples/spring-boot22-reactive/src/test/java/com/github/joomcode/mongoplanchecker/reactivestreams/sample/PlanCheckerConfig.java)
- `com.joom.mongo-plan-checker:mongo-plan-checker-reactivestreams-spring-data3` for spring-data version 3.0.x and 3.1.x (used in spring-boot 2.3.x and 2.4.x)

   and activate it similar to our sample [spring-boot23-reactive-sample](samples/spring-boot23-reactive/src/test/java/com/github/joomcode/mongoplanchecker/reactivestreams/sample/PlanCheckerConfig.java)
- `com.joom.mongo-plan-checker:mongo-plan-checker-reactivestreams-spring-data32` for spring-data version 3.2.x and onwards (used in spring-boot 2.5.x and 2.6.x)

  and activate it similar to our sample [spring-boot25-reactive-sample](samples/spring-boot25-reactive/src/test/java/com/github/joomcode/mongoplanchecker/reactivestreams/sample/PlanCheckerConfig.java)

# Contributing
You are welcome!
### Building
- Ensure docker is installed and available for current user
- `./mvnw clean verify`
- Code formatting is enforced with [google-java-format](https://github.com/google/google-java-format).
You may ensure code is properly formatted with `./mvnw com.coveo:fmt-maven-plugin:format`
### Tag new release
* `./mvnw release:prepare`
* `git clean -f`

