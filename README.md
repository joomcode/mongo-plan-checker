[![Build Status](https://travis-ci.com/joomcode/mongo-plan-checker.svg?branch=master)](https://travis-ci.com/joomcode/mongo-plan-checker)

WIP
# Building
* Ensure docker is installed and available for current user
* `./mvnw clean verify -Pdev`
# Tag new release
* `./mvnw release:prepare -Pdev`
* `git clean -f`
