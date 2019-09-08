#!/bin/bash

if [ $# != 1 ]; then
echo "USAGE: $0 bc_server_jar_path"
exit 1
fi

if [ ! -e "$1" ]; then
echo "$1 does not exist"
exit 1
fi

MAIN_PATH=`pwd`/`dirname $0`;
JAR_PATH=$1
BEECOM_PATH=..;
TARGET_PATH=target;

pushd $MAIN_PATH;
mkdir -p $TARGET_PATH;
rm -rf $TARGET_PATH/*
popd

cp $JAR_PATH $MAIN_PATH/$TARGET_PATH;

cd $MAIN_PATH;
cp -r $BEECOM_PATH/beecom_server/beecom_server/config $TARGET_PATH/
cp restart.sh $TARGET_PATH/
