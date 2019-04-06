#!/bin/bash

echo "Starting Redis Server - Master 1"
bin/redis-server cfg/redis_m1.conf &
sleep 1
echo "Starting Redis Server - Replica 1"
bin/redis-server cfg/redis_r1.conf &
sleep 1
echo "Starting Redis Server - Sentinel 1"
bin/redis-server cfg/sentinel_s1.conf --sentinel &
sleep 1
echo "Starting Redis Server - Sentinel 2"
bin/redis-server cfg/sentinel_s2.conf --sentinel &
sleep 1
echo "Starting Redis Server - Sentinel 3"
bin/redis-server cfg/sentinel_s3.conf --sentinel &

ps -ef | grep redis-server

exit 0
