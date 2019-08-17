#!/usr/bin/env bash

PRG="$0"

currentpath=`dirname $(readlink -f $0)`
frontdir=$(dirname $currentpath)
cd $frontdir
if [ ! -e logs ]; then
  mkdir logs
fi

source /etc/profile
if [ -n "$JAVA_HOME" ]; then
    for java in "$JAVA_HOME"/bin/amd64/java "$JAVA_HOME"/bin/java; do
        if [ -x "$java" ]; then
            JAVA="$java"
            break
        fi
    done
else
    JAVA=java
    echo 'please set JAVA_HOME'
    exit
fi

CLASSPATH=.:etc:bin
for i in lib/*.jar
do
  CLASSPATH=$CLASSPATH:$i
done
echo $CLASSPATH
CLASSNAME="mars.springboot.WebApplicationUserSystem"

nohup java -classpath $CLASSPATH -Xmx500m -Dconfig.dir=$dir/etc $CLASSNAME  > logs/nohup 2>&1 &

