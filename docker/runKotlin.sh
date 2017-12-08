#!/usr/bin/env bash
~/.sdkman/candidates/kotlin/current/bin/kotlinc $0 -include-runtime -d current.jar
java -jar current.jar