[![Build Status](https://travis-ci.com/joomcode/mongo-plan-checker.svg?branch=master)](https://travis-ci.com/joomcode/mongo-plan-checker)

Have you ever bring you mongo cluster to its knees rolling update with a query COLLSCAN-ing some collection?
This small library can prevent this. 

Use these wrappers for mongo-drivers in tests (you write tests for your 
code, aren't you?) and they will fail on collscans, sorts (for frequent queries it is preferred to have 
index ordering documents without need for separate heavy sort stage), excess reads (filtering too many 
documents read from disk and returning only a few) and broadcasts (if you have sharded mongodb cluster 
you don't want every your query go to all shards)

This is done using separate explain query before executing the real one. So this is not applicable for higload
production environment.

# Usage
Currently you can get it from [jitpack.io](https://jitpack.io/)
Depending on what driver you use you need 
- `com.github.joomcode.mongo-plan-checker:mongo-plan-checker-sync` that wraps `mongodb-driver-sync` (TODO sample)
- `com.github.joomcode.mongo-plan-checker:mongo-plan-checker-reactivestreams`
 that wraps `mongodb-driver-reactivestreams` (TODO sample)
- `com.github.joomcode.mongo-plan-checker:mongo-plan-checker-async` that wraps `mongodb-driver-async` (TODO sample)

Or you may use our extensions for spring-data
- `com.github.joomcode.mongo-plan-checker:mongo-plan-checker-sync-spring-data`

   and activate it similar to our sample [spring-boot-sample](samples/spring-boot-sync/src/test/java/com/github/joomcode/mongoplanchecker/sync/sample/PlanCheckerConfig.java) 
- `com.github.joomcode.mongo-plan-checker:mongo-plan-checker-reactivestreams-spring-data`

   and activate it similar to our sample [spring-boot-reactive-sample](samples/spring-boot-reactive/src/test/java/com/github/joomcode/mongoplanchecker/reactivestreams/sample/PlanCheckerConfig.java)

# Contributing
You are welcome!
### Building
- Ensure docker is installed and available for current user
- `./mvnw clean verify -Pdev`
- Code formatting is enforced with [google-java-format](https://github.com/google/google-java-format).
You may ensure code is properly formatted with `./mvnw com.coveo:fmt-maven-plugin:format`
### Tag new release
* `./mvnw release:prepare -Pdev`
* `git clean -f`

