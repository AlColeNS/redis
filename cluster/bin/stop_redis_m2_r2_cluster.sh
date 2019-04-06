#!/bin/bash

# Redis Cluster Password
REDISPASS=redis4cluster

echo "Active Redis Processess"
ps -ef | grep redis-server
echo ""

echo "Stopping Redis Server - Replica 1"
bin/redis-cli -p 6381 -a $REDISPASS shutdown
sleep 1
echo "Starting Redis Server - Replica 2"
bin/redis-cli -p 6382 -a $REDISPASS shutdown
sleep 1
echo "Stopping Redis Server - Master 1"
bin/redis-cli -p 6379 -a $REDISPASS shutdown
sleep 1
echo "Stopping Redis Server - Master 2"
bin/redis-cli -p 6380 -a $REDISPASS shutdown
sleep 1

echo ""
echo "Remaining Redis Processess"
ps -ef | grep redis-server
echo ""

exit 0
