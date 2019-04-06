#!/bin/bash

echo "Starting Redis Server - Master 1"
bin/redis-server cfg/redis_m1.conf &
sleep 1
echo "Starting Redis Server - Master 2"
bin/redis-server cfg/redis_m2.conf &
sleep 1
echo "Starting Redis Server - Replica 1"
bin/redis-server cfg/redis_r1.conf &
sleep 1
echo "Starting Redis Server - Replica 2"
bin/redis-server cfg/redis_r2.conf &

ps -ef | grep redis-server

exit 0
