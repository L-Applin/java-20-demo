#!/usr/bin/env bash

usage() {
    echo "demo.sh <class-to-run>"
    echo "    <class-to-run> name of the class to run in the ca.applin.demo package"
}

if [ -z "$1" ]; then
    usage
    exit 1
fi
DEMO="$1"

[ -d out ] || mkdir out
rm -r out/

echo "    [$0] Compiling ca.applin.demo.$1.java:"
javac --enable-preview --release 20 --add-modules jdk.incubator.concurrent -d out/ src/ca/applin/demo/$DEMO.java
echo "    [$0] Running ca.applin.demo.$1.class:"
java --enable-preview --add-modules jdk.incubator.concurrent -cp out ca.applin.demo.$DEMO
echo "    [$0] Done"