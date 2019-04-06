#!/bin/bash

# Redis Cluster Password
REDISPASS=redis4cluster

echo "Active Redis Processess"
ps -ef | grep redis-server
echo ""

echo "Stopping Redis Server - Sentinel 3"
bin/redis-cli -p 5002 shutdown
sleep 1
echo "Stopping Redis Server - Sentinel 2"
bin/redis-cli -p 5001 shutdown
sleep 1
echo "Stopping Redis Server - Sentinel 1"
bin/redis-cli -p 5000 shutdown
sleep 1
echo "Stopping Redis Server - Replica 1"
bin/redis-cli -p 6381 -a $REDISPASS shutdown
sleep 1
echo "Stopping Redis Server - Master 1"
bin/redis-cli -p 6379 -a $REDISPASS shutdown
sleep 1

echo "Remaining Redis Processess"
ps -ef | grep redis-server
echo ""

exit 0
