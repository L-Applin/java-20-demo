#!/usr/bin/env bash

set -e


usage() {
    echo "demo.sh <class-to-run>"
    echo "    <class-to-run> name of the class to run in the ca.applin.demo package"
}

if [ -z "$1" ]; then
    usage
    exit 1
fi
GREEN='\033[0;32m'
RESET='\033[0m'
NAME="$0"
DEMO="$1"

[ -d out ] || mkdir out
rm -r out/


echo -e "    [${GREEN}$NAME${RESET}] Compiling ca.applin.demo.$1.java:"
javac --enable-preview --release 20 --add-modules jdk.incubator.concurrent -d out/ src/ca/applin/demo/$DEMO.java
echo -e "    [${GREEN}$NAME${RESET}] Running ca.applin.demo.$1.class:"
shift
java --enable-preview --add-modules jdk.incubator.concurrent -cp out ca.applin.demo.$DEMO "$@"
echo -e "    [${GREEN}$NAME${RESET}] Done"