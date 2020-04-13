#!/bin/bash

SCRIPT_DIR=`dirname "$0"`
SCRIPT_DIR="`( cd \"$SCRIPT_DIR\" && pwd )`"

rm -rf ./target
mkdir ./target

javac -Xlint:unchecked -d $SCRIPT_DIR/target $SCRIPT_DIR/src/main/java/wordhunt/*.java

cd ./target
jar cfm wordhunt.jar $SCRIPT_DIR/src/main/resources/META-INF/MANIFEST.MF wordhunt/*.class

rm -rf ./wordhunt

