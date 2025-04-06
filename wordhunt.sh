#!/bin/bash

# Use the fat JAR with all dependencies included
java -jar $(dirname "$0")/target/wordhunt-1.0-SNAPSHOT-full.jar "$@"
