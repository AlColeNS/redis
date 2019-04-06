# Redis Challenge

The goal of this challenge is to download the latest Redis release, build it locally, experiment with different cluster configurations and exercise the data structure features.


### OSS Community Distribution

The Redis 5.0.4 release was downloaded from [here](https://redis.io/download).  The following commands were executed to get it built on my MacBook Pro laptop.

```
$ setenv REDIS_HOME ~/GitHub/common-shared/dev-env/redis
$ cd $REDIS_HOME
$ tar -xf ~/Downloads/redis-5.0.4.tar.gz
$ ln -s redis-5.0.4 latest
$ cd latest
$ make
$ make test
$ mkdir cfg log bin
$ $ cp src/{redis-benchmark,redis-check-aof,redis-check-rdb,redis-cli,redis-server,redis-sentinel} bin
```

### Redis Cluster with 2 Master and 2 Replica Nodes

The next goal was to create a Redis Cluster with 2 Master and Replica nodes, require password for authentication and enable RDB and AOF persistence.

To accomplish this, I copied the default _redis.conf_ to create the following configuration files:

* _redis_m1.conf_ - Master 1 configuration file
* _redis_r1.conf_ - Replica 1 configuration file
* _redis_m2.conf_ - Master 2 configuration file
* _redis_r2.conf_ - Replica 2 configuration file

The files are available under the _cluster/cfg_ folder.

I also created three Bash scripts to automate the services starting, stopping and cleaning up of the cluster log and data files.

* start_redis_m2_r2_cluster.sh - Starts the Redis Server processes
* stop_redis_m2_r2_cluster.sh - Stops the Redis Server processes
* clean_redis_cluster.sh - Cleans up the log and data files

The files are available under the _cluster/bin_ folder.

**Starting and Validating the Multi-Master and Multi-Replica Cluster***

```
$ bin/start_redis_m2_r2_cluster.sh
Starting Redis Server - Master 1
Starting Redis Server - Master 2
Starting Redis Server - Replica 1
Starting Redis Server - Replica 2
  501  2060  2059   0  8:48AM ttys000    0:00.02 bin/redis-server 127.0.0.1:6379 
  501  2062  2059   0  8:48AM ttys000    0:00.01 bin/redis-server 127.0.0.1:6380 
  501  2064  2059   0  8:48AM ttys000    0:00.02 bin/redis-server 127.0.0.1:6381 
  501  2068  2059   0  8:48AM ttys000    0:00.00 bin/redis-server 127.0.0.1:6382 
-------------------------------------------------------------------------------
$ bin/redis-cli -a redis4cluster
127.0.0.1:6379> ping
PONG
127.0.0.1:6379> set country usa
OK
127.0.0.1:6379> get country
"usa"
127.0.0.1:6379> quit
-------------------------------------------------------------------------------
$ bin/stop_redis_m2_r2_cluster.sh
Active Redis Processess
  501  2105     1   0  9:01AM ttys000    0:00.02 bin/redis-server 127.0.0.1:6379 
  501  2107     1   0  9:01AM ttys000    0:00.03 bin/redis-server 127.0.0.1:6380 
  501  2109     1   0  9:01AM ttys000    0:00.03 bin/redis-server 127.0.0.1:6381 
  501  2113     1   0  9:01AM ttys000    0:00.03 bin/redis-server 

Stopping Redis Server - Replica 1
Starting Redis Server - Replica 2
Stopping Redis Server - Master 1
Stopping Redis Server - Master 2

Remaining Redis Processess
  501  2130  2118   0  9:01AM ttys000    0:00.00 grep redis-server
-------------------------------------------------------------------------------
$ cat log/redis_m1.log
2060:C 05 Apr 2019 08:48:33.347 # oO0OoO0OoO0Oo Redis is starting oO0OoO0OoO0Oo
2060:C 05 Apr 2019 08:48:33.347 # Redis version=5.0.4, bits=64, commit=00000000, modified=0, pid=2060, just started
2060:C 05 Apr 2019 08:48:33.348 # Configuration loaded
2060:M 05 Apr 2019 08:48:33.349 * Increased maximum number of open files to 10032 (it was originally set to 256).
                _._                                                  
           _.-``__ ''-._                                             
      _.-``    `.  `_.  ''-._           Redis 5.0.4 (00000000/0) 64 bit
  .-`` .-```.  ```\/    _.,_ ''-._                                   
 (    '      ,       .-`  | `,    )     Running in standalone mode
 |`-._`-...-` __...-.``-._|'` _.-'|     Port: 6379
 |    `-._   `._    /     _.-'    |     PID: 2060
  `-._    `-._  `-./  _.-'    _.-'                                   
 |`-._`-._    `-.__.-'    _.-'_.-'|                                  
 |    `-._`-._        _.-'_.-'    |           http://redis.io        
  `-._    `-._`-.__.-'_.-'    _.-'                                   
 |`-._`-._    `-.__.-'    _.-'_.-'|                                  
 |    `-._`-._        _.-'_.-'    |                                  
  `-._    `-._`-.__.-'_.-'    _.-'                                   
      `-._    `-.__.-'    _.-'                                       
          `-._        _.-'                                           
              `-.__.-'                                               

2060:M 05 Apr 2019 08:48:33.351 # Server initialized
2060:M 05 Apr 2019 08:48:33.351 * Ready to accept connections
2060:M 05 Apr 2019 08:48:35.373 * Replica 127.0.0.1:6381 asks for synchronization
2060:M 05 Apr 2019 08:48:35.373 * Full resync requested by replica 127.0.0.1:6381
2060:M 05 Apr 2019 08:48:35.373 * Starting BGSAVE for SYNC with target: disk
2060:M 05 Apr 2019 08:48:35.374 * Background saving started by pid 2066
2066:C 05 Apr 2019 08:48:35.375 * DB saved on disk
2060:M 05 Apr 2019 08:48:35.416 * Background saving terminated with success
2060:M 05 Apr 2019 08:48:35.417 * Synchronization with replica 127.0.0.1:6381 succeeded
-------------------------------------------------------------------------------
$ cat log/redis_m2.log
2062:C 05 Apr 2019 08:48:34.358 # oO0OoO0OoO0Oo Redis is starting oO0OoO0OoO0Oo
2062:C 05 Apr 2019 08:48:34.358 # Redis version=5.0.4, bits=64, commit=00000000, modified=0, pid=2062, just started
2062:C 05 Apr 2019 08:48:34.358 # Configuration loaded
2062:M 05 Apr 2019 08:48:34.360 * Increased maximum number of open files to 10032 (it was originally set to 256).
                _._                                                  
           _.-``__ ''-._                                             
      _.-``    `.  `_.  ''-._           Redis 5.0.4 (00000000/0) 64 bit
  .-`` .-```.  ```\/    _.,_ ''-._                                   
 (    '      ,       .-`  | `,    )     Running in standalone mode
 |`-._`-...-` __...-.``-._|'` _.-'|     Port: 6380
 |    `-._   `._    /     _.-'    |     PID: 2062
  `-._    `-._  `-./  _.-'    _.-'                                   
 |`-._`-._    `-.__.-'    _.-'_.-'|                                  
 |    `-._`-._        _.-'_.-'    |           http://redis.io        
  `-._    `-._`-.__.-'_.-'    _.-'                                   
 |`-._`-._    `-.__.-'    _.-'_.-'|                                  
 |    `-._`-._        _.-'_.-'    |                                  
  `-._    `-._`-.__.-'_.-'    _.-'                                   
      `-._    `-.__.-'    _.-'                                       
          `-._        _.-'                                           
              `-.__.-'                                               

2062:M 05 Apr 2019 08:48:34.361 # Server initialized
2062:M 05 Apr 2019 08:48:34.361 * Ready to accept connections
2062:M 05 Apr 2019 08:48:36.382 * Replica 127.0.0.1:6382 asks for synchronization
2062:M 05 Apr 2019 08:48:36.382 * Full resync requested by replica 127.0.0.1:6382
2062:M 05 Apr 2019 08:48:36.382 * Starting BGSAVE for SYNC with target: disk
2062:M 05 Apr 2019 08:48:36.382 * Background saving started by pid 2071
2071:C 05 Apr 2019 08:48:36.383 * DB saved on disk
2062:M 05 Apr 2019 08:48:36.415 * Background saving terminated with success
2062:M 05 Apr 2019 08:48:36.415 * Synchronization with replica 127.0.0.1:6382 succeeded
-------------------------------------------------------------------------------
$ cat log/redis_r1.log
2064:C 05 Apr 2019 08:48:35.367 # oO0OoO0OoO0Oo Redis is starting oO0OoO0OoO0Oo
2064:C 05 Apr 2019 08:48:35.367 # Redis version=5.0.4, bits=64, commit=00000000, modified=0, pid=2064, just started
2064:C 05 Apr 2019 08:48:35.368 # Configuration loaded
2064:S 05 Apr 2019 08:48:35.370 * Increased maximum number of open files to 10032 (it was originally set to 256).
                _._                                                  
           _.-``__ ''-._                                             
      _.-``    `.  `_.  ''-._           Redis 5.0.4 (00000000/0) 64 bit
  .-`` .-```.  ```\/    _.,_ ''-._                                   
 (    '      ,       .-`  | `,    )     Running in standalone mode
 |`-._`-...-` __...-.``-._|'` _.-'|     Port: 6381
 |    `-._   `._    /     _.-'    |     PID: 2064
  `-._    `-._  `-./  _.-'    _.-'                                   
 |`-._`-._    `-.__.-'    _.-'_.-'|                                  
 |    `-._`-._        _.-'_.-'    |           http://redis.io        
  `-._    `-._`-.__.-'_.-'    _.-'                                   
 |`-._`-._    `-.__.-'    _.-'_.-'|                                  
 |    `-._`-._        _.-'_.-'    |                                  
  `-._    `-._`-.__.-'_.-'    _.-'                                   
      `-._    `-.__.-'    _.-'                                       
          `-._        _.-'                                           
              `-.__.-'                                               

2064:S 05 Apr 2019 08:48:35.372 # Server initialized
2064:S 05 Apr 2019 08:48:35.372 * Ready to accept connections
2064:S 05 Apr 2019 08:48:35.372 * Connecting to MASTER 127.0.0.1:6379
2064:S 05 Apr 2019 08:48:35.372 * MASTER <-> REPLICA sync started
2064:S 05 Apr 2019 08:48:35.372 * Non blocking connect for SYNC fired the event.
2064:S 05 Apr 2019 08:48:35.373 * Master replied to PING, replication can continue...
2064:S 05 Apr 2019 08:48:35.373 * Partial resynchronization not possible (no cached master)
2064:S 05 Apr 2019 08:48:35.374 * Full resync from master: f0a46fa5768a2462ae3b4fb8d64c556cad6f4d52:0
2064:S 05 Apr 2019 08:48:35.417 * MASTER <-> REPLICA sync: receiving 175 bytes from master
2064:S 05 Apr 2019 08:48:35.417 * MASTER <-> REPLICA sync: Flushing old data
2064:S 05 Apr 2019 08:48:35.418 * MASTER <-> REPLICA sync: Loading DB in memory
2064:S 05 Apr 2019 08:48:35.418 * MASTER <-> REPLICA sync: Finished with success
2064:S 05 Apr 2019 08:48:35.419 * Background append only file rewriting started by pid 2067
2064:S 05 Apr 2019 08:48:35.443 * AOF rewrite child asks to stop sending diffs.
2067:C 05 Apr 2019 08:48:35.443 * Parent agreed to stop sending diffs. Finalizing AOF...
2067:C 05 Apr 2019 08:48:35.443 * Concatenating 0.00 MB of AOF diff received from parent.
2067:C 05 Apr 2019 08:48:35.444 * SYNC append only file rewrite performed
2064:S 05 Apr 2019 08:48:35.477 * Background AOF rewrite terminated with success
2064:S 05 Apr 2019 08:48:35.477 * Residual parent diff successfully flushed to the rewritten AOF (0.00 MB)
2064:S 05 Apr 2019 08:48:35.478 * Background AOF rewrite finished successfully
-------------------------------------------------------------------------------
$ cat log/redis_r2.log
2068:C 05 Apr 2019 08:48:36.376 # oO0OoO0OoO0Oo Redis is starting oO0OoO0OoO0Oo
2068:C 05 Apr 2019 08:48:36.376 # Redis version=5.0.4, bits=64, commit=00000000, modified=0, pid=2068, just started
2068:C 05 Apr 2019 08:48:36.377 # Configuration loaded
2068:S 05 Apr 2019 08:48:36.378 * Increased maximum number of open files to 10032 (it was originally set to 256).
                _._                                                  
           _.-``__ ''-._                                             
      _.-``    `.  `_.  ''-._           Redis 5.0.4 (00000000/0) 64 bit
  .-`` .-```.  ```\/    _.,_ ''-._                                   
 (    '      ,       .-`  | `,    )     Running in standalone mode
 |`-._`-...-` __...-.``-._|'` _.-'|     Port: 6382
 |    `-._   `._    /     _.-'    |     PID: 2068
  `-._    `-._  `-./  _.-'    _.-'                                   
 |`-._`-._    `-.__.-'    _.-'_.-'|                                  
 |    `-._`-._        _.-'_.-'    |           http://redis.io        
  `-._    `-._`-.__.-'_.-'    _.-'                                   
 |`-._`-._    `-.__.-'    _.-'_.-'|                                  
 |    `-._`-._        _.-'_.-'    |                                  
  `-._    `-._`-.__.-'_.-'    _.-'                                   
      `-._    `-.__.-'    _.-'                                       
          `-._        _.-'                                           
              `-.__.-'                                               

2068:S 05 Apr 2019 08:48:36.379 # Server initialized
2068:S 05 Apr 2019 08:48:36.379 * Ready to accept connections
2068:S 05 Apr 2019 08:48:36.381 * Connecting to MASTER 127.0.0.1:6380
2068:S 05 Apr 2019 08:48:36.381 * MASTER <-> REPLICA sync started
2068:S 05 Apr 2019 08:48:36.381 * Non blocking connect for SYNC fired the event.
2068:S 05 Apr 2019 08:48:36.381 * Master replied to PING, replication can continue...
2068:S 05 Apr 2019 08:48:36.382 * Partial resynchronization not possible (no cached master)
2068:S 05 Apr 2019 08:48:36.383 * Full resync from master: 534e9efe9c2c5a3d4bc640b5076775a108cbd8d0:0
2068:S 05 Apr 2019 08:48:36.415 * MASTER <-> REPLICA sync: receiving 175 bytes from master
2068:S 05 Apr 2019 08:48:36.416 * MASTER <-> REPLICA sync: Flushing old data
2068:S 05 Apr 2019 08:48:36.416 * MASTER <-> REPLICA sync: Loading DB in memory
2068:S 05 Apr 2019 08:48:36.416 * MASTER <-> REPLICA sync: Finished with success
2068:S 05 Apr 2019 08:48:36.416 * Background append only file rewriting started by pid 2072
2068:S 05 Apr 2019 08:48:36.440 * AOF rewrite child asks to stop sending diffs.
2072:C 05 Apr 2019 08:48:36.441 * Parent agreed to stop sending diffs. Finalizing AOF...
2072:C 05 Apr 2019 08:48:36.441 * Concatenating 0.00 MB of AOF diff received from parent.
2072:C 05 Apr 2019 08:48:36.442 * SYNC append only file rewrite performed
2068:S 05 Apr 2019 08:48:36.482 * Background AOF rewrite terminated with success
2068:S 05 Apr 2019 08:48:36.482 * Residual parent diff successfully flushed to the rewritten AOF (0.00 MB)
2068:S 05 Apr 2019 08:48:36.482 * Background AOF rewrite finished successfully
```

### Redis Cluster with 1 Master, 1 Replica Nodes and 3 Sentinels

The next goal was to create a Redis Cluster with 1 Master, 1 Replica node and 3 Sentinel services with the goal of supporting high availability.

To accomplish this, I copied the default _sentinel.conf_ to create the following configuration files:

* _redis_s1.conf_ - Sentinel 1 configuration file
* _redis_s2.conf_ - Sentinel 2 configuration file
* _redis_s3.conf_ - Sentinel 3 configuration file

The files are available under the _cluster/cfg_ folder.

I also created two Bash scripts to automate the starting and stopping of the services.

* start_redis_m1_r1_sentinel.sh - Starts the Redis Server processes
* stop_redis_m1_r1_sentinel.sh - Stops the Redis Server processes

The files are available under the _cluster/bin_ folder.

**Starting and Validating the Sentinel Cluster**

```
bin/start_redis_m1_r1_sentinel.sh
Starting Redis Server - Master 1
Starting Redis Server - Replica 1
Starting Redis Server - Sentinel 1
Starting Redis Server - Sentinel 2
Starting Redis Server - Sentinel 3
  501  2147  2146   0  9:13AM ttys000    0:00.02 bin/redis-server 127.0.0.1:6379 
  501  2149  2146   0  9:13AM ttys000    0:00.02 bin/redis-server 127.0.0.1:6381 
  501  2153  2146   0  9:13AM ttys000    0:00.01 bin/redis-server *:5000 [sentinel]  
  501  2155  2146   0  9:13AM ttys000    0:00.01 bin/redis-server *:5001 [sentinel]  
  501  2158  2146   0  9:13AM ttys000    0:00.00 bin/redis-server *:5002 [sentinel]
-------------------------------------------------------------------------------
$ bin/redis-cli -p 5000
127.0.0.1:5000> info
# Server
redis_version:5.0.4
redis_git_sha1:00000000
redis_git_dirty:0
redis_build_id:867a2aa9552fc3d1
redis_mode:sentinel
os:Darwin 18.5.0 x86_64
arch_bits:64
multiplexing_api:kqueue
atomicvar_api:atomic-builtin
gcc_version:4.2.1
process_id:2153
run_id:f5a5c380e4710a822681cc6ebfd7c56edf7d086f
tcp_port:5000
uptime_in_seconds:223
uptime_in_days:0
hz:14
configured_hz:10
lru_clock:10966334
executable:/Users/acole/GitHub/common-shared/dev-env/redis/redis-5.0.4/bin/redis-server
config_file:/Users/acole/GitHub/common-shared/dev-env/redis/redis-5.0.4/cfg/sentinel_s1.conf

# Clients
connected_clients:1
client_recent_max_input_buffer:2
client_recent_max_output_buffer:0
blocked_clients:0

# CPU
used_cpu_sys:0.219761
used_cpu_user:0.177366
used_cpu_sys_children:0.000000
used_cpu_user_children:0.000000

# Stats
total_connections_received:1
total_commands_processed:0
instantaneous_ops_per_sec:0
total_net_input_bytes:31
total_net_output_bytes:60
instantaneous_input_kbps:0.00
instantaneous_output_kbps:0.00
rejected_connections:0
sync_full:0
sync_partial_ok:0
sync_partial_err:0
expired_keys:0
expired_stale_perc:0.00
expired_time_cap_reached_count:0
evicted_keys:0
keyspace_hits:0
keyspace_misses:0
pubsub_channels:0
pubsub_patterns:0
latest_fork_usec:0
migrate_cached_sockets:0
slave_expires_tracked_keys:0
active_defrag_hits:0
active_defrag_misses:0
active_defrag_key_hits:0
active_defrag_key_misses:0

# Sentinel
sentinel_masters:1
sentinel_tilt:0
sentinel_running_scripts:0
sentinel_scripts_queue_length:0
sentinel_simulate_failure_flags:0
master0:name=master1,status=sdown,address=127.0.0.1:6379,slaves=0,sentinels=1

127.0.0.1:5000> sentinel masters
1)  1) "name"
    2) "master1"
    3) "ip"
    4) "127.0.0.1"
    5) "port"
    6) "6379"
    7) "runid"
    8) ""
    9) "flags"
   10) "s_down,master,disconnected"
   11) "link-pending-commands"
   12) "0"
   13) "link-refcount"
   14) "1"
   15) "last-ping-sent"
   16) "283757"
   17) "last-ok-ping-reply"
   18) "283757"
   19) "last-ping-reply"
   20) "3"
   21) "s-down-time"
   22) "253725"
   23) "down-after-milliseconds"
   24) "30000"
   25) "info-refresh"
   26) "1554470267490"
   27) "role-reported"
   28) "master"
   29) "role-reported-time"
   30) "283757"
   31) "config-epoch"
   32) "0"
   33) "num-slaves"
   34) "0"
   35) "num-other-sentinels"
   36) "0"
   37) "quorum"
   38) "2"
   39) "failover-timeout"
   40) "180000"
   41) "parallel-syncs"
   42) "1"
127.0.0.1:5000> quit
-------------------------------------------------------------------------------
$ bin/stop_redis_m1_r1_sentinel.sh
Active Redis Processess
  501  2147     1   0  9:13AM ttys000    0:01.13 bin/redis-server 127.0.0.1:6379 
  501  2149     1   0  9:13AM ttys000    0:00.62 bin/redis-server 127.0.0.1:6381 
  501  2153     1   0  9:13AM ttys000    0:01.19 bin/redis-server *:5000 [sentinel]  
  501  2155     1   0  9:13AM ttys000    0:01.17 bin/redis-server *:5001 [sentinel]  
  501  2158     1   0  9:13AM ttys000    0:01.18 bin/redis-server *:5002 [sentinel]  
  501  2178  2176   0  9:24AM ttys000    0:00.00 grep redis-server

Stopping Redis Server - Sentinel 3
Stopping Redis Server - Sentinel 2
Stopping Redis Server - Sentinel 1
Stopping Redis Server - Replica 1
Stopping Redis Server - Master 1

Remaining Redis Processess
  501  2190  2176   0  9:24AM ttys000    0:00.00 grep redis-server
```

### Publish and Subscribe Python Scripts

The goal of these scripts was to exercise the Redis publish/subscribe features using the Python client.  The script uses a public data set of HR records formatted as a CSV file.  The script files are described further below.

* _core/publish.py_ - Publishes the contents of the HR records
* _core/subscribe.py_ - Subscribes and consumes the HR records
* _core/cfg/properties.ini_ - Externalizes the script properties (e.g. Redis connection details)
* _core/data/hr-records.csv_ - Used by publisher script to feed the message channel

The execution of the scripts is captured below.

```
$ cd $APL/src/redis/core
$ python3 subscribe.py
This script will exercise the Redis subscribe features using Python client

Waiting up to 30.0 seconds for messages to arrive
Subscribed to channel 'Employees'

[1] Receieved on Employees : 671-48-9915 = Juliette Rojo
[2] Receieved on Employees : 527-99-6328 = Milan Krawczyk
[3] Receieved on Employees : 063-02-5994 = Elmer Jason
[4] Receieved on Employees : 421-67-5501 = Zelda Forest
[5] Receieved on Employees : 608-87-8674 = Rhett Wan
[6] Receieved on Employees : 661-22-0722 = Hal Farrow
[7] Receieved on Employees : 510-33-5541 = Del Fernandez
[8] Receieved on Employees : 651-62-1513 = Corey Jackman
[9] Receieved on Employees : 311-35-3566 = Bibi Paddock
[10] Receieved on Employees : 647-21-1863 = Eric Manning
[11] Receieved on Employees : 539-71-9884 = Renetta Hafner
[12] Receieved on Employees : 665-22-7253 = Paz Pearman
[13] Receieved on Employees : 449-99-5082 = Ardath Forman
[14] Receieved on Employees : 571-99-4191 = Nanci Osorio

Done
-------------------------------------------------------------------------------
$ python3 publish.py
This script will exercise the Redis publish features using Python client

CSV columns successfully read
[1] Published on Employees : 671-48-9915 = Juliette Rojo
[2] Published on Employees : 527-99-6328 = Milan Krawczyk
[3] Published on Employees : 063-02-5994 = Elmer Jason
[4] Published on Employees : 421-67-5501 = Zelda Forest
[5] Published on Employees : 608-87-8674 = Rhett Wan
[6] Published on Employees : 661-22-0722 = Hal Farrow
[7] Published on Employees : 510-33-5541 = Del Fernandez
[8] Published on Employees : 651-62-1513 = Corey Jackman
[9] Published on Employees : 311-35-3566 = Bibi Paddock
[10] Published on Employees : 647-21-1863 = Eric Manning
[11] Published on Employees : 539-71-9884 = Renetta Hafner
[12] Published on Employees : 665-22-7253 = Paz Pearman
[13] Published on Employees : 449-99-5082 = Ardath Forman
[14] Published on Employees : 571-99-4191 = Nanci Osorio
[15] Published on Employees : 157-23-8970 = Maricela Simard

Done
```


### Data Structure Python Scripts

The goal of these scripts was to exercise the Redis data structure features using the Python client.  The script uses a public data set of HR records formatted as a CSV file.  The script files are described further below.

* _core/datastructures.py_ - Exercises the Redis data structure functions using HR records
	* Simple strings
	* Simple strings using a pipeline
	* Strings with numbers that are incremented
	* String stored via push/pop methods
	* Hashes of records using a pipeline
	* Sets using a pipeline
	* Sorted sets displayed in ascending/descending order
* _core/cfg/properties.ini_ - Externalizes the script properties (e.g. Redis connection details)
* _core/data/hr-records.csv_ - Used to populate the different data structure functions

The execution of the scripts is captured below.

```
python3 datastructures.py 

This script will exercise the Redis data structure features using Python client.

Loading 100 records from data/hr-records.csv file
Done

This function will exercise the Redis simple string data structure features.
Successfully matched 100 key/values

This function will exercise the Redis simple string data structure features using a pipeline.
Successfully matched 100 key/values

This function will exercise the Redis increment string data structure features.
Successfully matched 100 key/values

This function will exercise the Redis push/pop string data structure features.
Successfully pushed/popped 100 full name strings.

This function will exercise the Redis hash data structure features using a pipeline.
Successfully matched 100 key/values

This function will exercise the Redis set data structure features using a pipeline.
Set cardinality is 100

This function will exercise the Redis sorted set data structure features.

Employee compensation in ascending order:
[(b'Tangela Woody', 41509.0), (b'Frankie Owings', 42901.0), (b'Kam Hazel', 43286.0),
 (b'Leone Buss', 44080.0), (b'Ruben Weissman', 48543.0), (b'Sherita Baugh', 49450.0)]

Employee compensation in descending order:
[(b'Jonathan Rosa', 198838.0), (b'Elden Ordonez', 197078.0), (b'Juliette Rojo',
 193912.0), (b'Lana Arbuckle', 193628.0), (b'Noe Stanger', 192182.0), 
 (b'Mariela Santoyo', 191952.0)]

```

### RediSearch Java Utility

The goal of this coding effort was to exercise the RedisSearch features using the Java client.  The utility uses a public data set of HR records formatted as a CSV file.

The RediSearch coding effort utilized the official Docker Hub release from [here](https://hub.docker.com/r/redislabs/redisearch/).  

The docker image was run and validated with the following commands:

```
$ docker run -p 6379:6379 --name redisearch -d redislabs/redisearch:latest
ec92d9a700f223fd9f79f49e2206c03dd6a7343d1bd9b33ea31639bd18354922
$ docker container ls
CONTAINER ID        IMAGE                         COMMAND                  CREATED             STATUS              PORTS                    NAMES
ec92d9a700f2        redislabs/redisearch:latest   "docker-entrypoint.s…"   29 seconds ago      Up 28 seconds       0.0.0.0:6379->6379/tcp   redisearch
$ docker logs redisearch
1:C 06 Apr 2019 15:06:49.905 # oO0OoO0OoO0Oo Redis is starting oO0OoO0OoO0Oo
1:C 06 Apr 2019 15:06:49.905 # Redis version=5.0.3, bits=64, commit=00000000, modified=0, pid=1, just started
1:C 06 Apr 2019 15:06:49.905 # Configuration loaded
1:M 06 Apr 2019 15:06:49.907 * Running mode=standalone, port=6379.
$ cd $REDIS_HOME
$ bin/redis-cli -p 6379 ping
PONG
```

The docker container was stopped with the following command:

```
$ docker container stop redisearch
```

The utility can be built and executed as follows:

```
$ cd $APL/src/redis/search
$ mvn compile assembly:single 
$ java -jar target/challenge-1.0-SNAPSHOT-jar-with-dependencies.jar

Exercising the RediSearch document indexing and search features.
Created search index in 47 milliseconds.
Added 10000 documents to the search index in 9621 milliseconds.
                                                                                                                                                                                                                                                                                        Results of Query for 'Johnny'

Employee Id   Name Prefix  First Name  Middle Initial  Last Name    Gender  E-Mail                       Father's Name       Mother's Name       Mother's Maiden Name  Date of Birth  Time of Birth  Age in Years  Weight in Kilograms  Date of Joining  Quarter of Joining  Half of Joining  Year of Joining  Month of Joining  Month Name of Joining  Short Month  Day of Joining  DOW of Joining  Short DOW  Age in Company Years  Salary    Last % Hike        Social Security Number  Phone Number  Place Name     County       City           State  Zip Code  Region     User Name      Password         
-----------   -----------  ----------  --------------  ---------    ------  ------                       -------------       -------------       --------------------  -------------  -------------  ------------  -------------------  ---------------  ------------------  ---------------  ---------------  ----------------  ---------------------  -----------  --------------  --------------  ---------  --------------------  ------    -----------        ----------------------  ------------  ----------     ------       ----           -----  --------  ------     ---------      --------         
158637        Mrs.         Johnny      Z               Waldo        F       johnny.waldo@gmail.com       Samuel Waldo        Kristi Waldo        Godfrey               7/18/1986      07:51:35 PM    31.05         60                   1/19/2015        Q1                  H1               2015             1                 January                Jan          19              Monday          Mon        2.52                  105627.0  26%                516-49-0552             231-362-5249  Lansing        Ingham       Lansing        MI     48950     Midwest    jzwaldo        zafd5G+;         

481295        Mr.          Johnny      S               Dangelo      M       johnny.dangelo@hotmail.com   Arron Dangelo       Britta Dangelo      Witte                 10/8/1980      12:37:49 AM    36.83         68                   8/31/2004        Q3                  H2               2004             8                 August                 Aug          31              Tuesday         Tue        12.92                 116587.0  19%                380-37-3144             219-533-1435  Cross Plains   Ripley       Cross Plains   IN     47017     Midwest    jsdangelo      W/krit~Oa8f      

554359        Mr.          Johnny      T               Chapple      M       johnny.chapple@aol.com       Jason Chapple       Cathie Chapple      Avery                 6/2/1984       11:17:57 AM    33.18         53                   4/4/2014         Q2                  H1               2014             4                 April                  Apr          4               Friday          Fri        3.32                  93176.0   6%                 177-86-0249             216-212-3227  Feesburg       Brown        Feesburg       OH     45119     Midwest    jtchapple      v\EuH$2%}5.{p    

806977        Mr.          Johnny      U               Stoffel      M       johnny.stoffel@yahoo.ca      Joshua Stoffel      Deja Stoffel        Speer                 7/4/1975       12:46:55 AM    42.1          89                   7/24/2013        Q3                  H2               2013             7                 July                   Jul          24              Wednesday       Wed        4.01                  154848.0  29%                550-99-1635             228-926-9711  Myrtle         Union        Myrtle         MS     38650     South      justoffel      wQL[BMMO         

171987        Mr.          Johnny      T               Uhl          M       johnny.uhl@aol.com           Russel Uhl          Marci Uhl           Lloyd                 5/13/1980      08:04:12 PM    37.23         67                   9/27/2014        Q3                  H2               2014             9                 September              Sep          27              Saturday        Sat        2.84                  185785.0  17%                384-37-9749             339-881-2303  Swansea        Bristol      Swansea        MA     2777      Northeast  jtuhl          GtyfT#2?CT@      

945311        Mr.          Johnny      Z               Gordon       M       johnny.gordon@gmail.com      Basil Gordon        Reita Gordon        Pacheco               6/27/1986      04:01:14 PM    31.11         58                   6/11/2015        Q2                  H1               2015             6                 June                   Jun          11              Thursday        Thu        2.13                  192425.0  4%                 519-87-1831             479-297-2080  Tillar         Desha        Tillar         AR     71670     South      jzgordon       pkU.$V]D0aY      

692151        Mrs.         Stasia      P               Matheny      F       stasia.matheny@gmail.com     Alvaro Matheny      Johnny Matheny      Durr                  1/14/1978      02:54:23 AM    39.56         53                   11/16/2005       Q4                  H2               2005             11                November               Nov          16              Wednesday       Wed        11.7                  178799.0  21%                658-36-0756             203-497-1322  East Windsor   Hartford     East Windsor   CT     6088      Northeast  spmatheny      6WP^:LW:$#[3NJT  

322639        Mr.          Riley       R               Bettencourt  M       riley.bettencourt@aol.com    Myron Bettencourt   Johnny Bettencourt  Ham                   11/15/1972     04:22:49 PM    44.73         63                   6/7/1995         Q2                  H1               1995             6                 June                   Jun          7               Wednesday       Wed        22.16                 129640.0  2%                 409-99-0475             307-462-4664  Lance Creek    Niobrara     Lance Creek    WY     82222     West       rrbettencourt  L>.&P6CgJW:y*    

197018        Mr.          Murray      A               Mccraw       M       murray.mccraw@gmail.com      Johnny Mccraw       Jin Mccraw          Markey                8/22/1979      05:46:38 PM    37.96         50                   1/5/2017         Q1                  H1               2017             1                 January                Jan          5               Thursday        Thu        0.56                  86081.0   24%                177-86-9873             215-907-4885  Clifford       Susquehanna  Clifford       PA     18413     Northeast  mamccraw       z&%nYCtQQe-uz    

154647        Ms.          Terresa     L               Witter       F       terresa.witter@aol.com       Johnny Witter       Jessica Witter      Bulger                2/17/1982      12:09:54 PM    35.47         46                   6/11/2006        Q2                  H1               2006             6                 June                   Jun          11              Sunday          Sun        11.14                 115856.0  19%                555-99-1458             212-913-6113  Warnerville    Schoharie    Warnerville    NY     12187     Northeast  tlwitter       2TsmXnQj1        

811306        Mr.          Rhett       P               Wan          M       rhett.wan@hotmail.com        Johnny Wan          Keva Wan            Gehring               7/14/1976      12:06:19 AM    41.07         71                   1/21/2009        Q1                  H1               2009             1                 January                Jan          21              Wednesday       Wed        8.52                  59406.0   25%                608-87-8674             209-984-3789  Selma          Fresno       Selma          CA     93662     West       rpwan          X\|4}dm%g~Z&A4q  

243263        Ms.          Johnnie     E               Lacasse      F       johnnie.lacasse@aol.com      Cody Lacasse        Marcie Lacasse      Weimer                9/22/1974      03:32:42 AM    42.88         49                   8/28/1998        Q3                  H2               1998             8                 August                 Aug          28              Friday          Fri        18.93                 199445.0  20%                303-37-0394             240-815-3345  Bethesda       Montgomery   Bethesda       MD     20892     South      jelacasse      c$ym?i4cPFrXRZ[  

580797        Ms.          Johnnie     R               Holman       F       johnnie.holman@yahoo.com     Stewart Holman      Pura Holman         Mcallister            8/21/1978      03:23:01 AM    38.96         58                   7/11/2006        Q3                  H2               2006             7                 July                   Jul          11              Tuesday         Tue        11.05                 153003.0  7%                 703-18-7850             231-885-7681  Allouez        Keweenaw     Allouez        MI     49805     Midwest    jrholman       LPItH!qU5        

872750        Mr.          Johnnie     J               Ibarra       M       johnnie.ibarra@aol.com       Abel Ibarra         Nenita Ibarra       Dunford               1/10/1972      06:04:09 PM    45.58         71                   4/14/2013        Q2                  H1               2013             4                 April                  Apr          14              Sunday          Sun        4.29                  181385.0  15%                439-99-8990             209-862-8601  San Diego      San Diego    San Diego      CA     92142     West       jjibarra       2sGw73*xxx\|     

395018        Mr.          Lane        G               Mair         M       lane.mair@yahoo.com          Johnnie Mair        Rosa Mair           Rainer                9/25/1985      02:12:57 AM    31.86         84                   6/13/2009        Q2                  H1               2009             6                 June                   Jun          13              Saturday        Sat        8.13                  45597.0   22%                405-73-1321             216-652-4256  Jewett         Harrison     Jewett         OH     43986     Midwest    lgmair         8>uT&8h<ZF^oS.   

256355        Mr.          Randy       P               Mcguinness   M       randy.mcguinness@gmail.com   Johnnie Mcguinness  Karie Mcguinness    Arroyo                6/30/1969      05:05:04 AM    48.11         66                   9/24/2016        Q3                  H2               2016             9                 September              Sep          24              Saturday        Sat        0.84                  167241.0  23%                569-99-9568             479-543-1195  Witter         Madison      Witter         AR     72776     South      rpmcguinness   X-}dY:2i?x       

767865        Ms.          Loria       U               Marte        F       loria.marte@gmail.com        Johnnie Marte       Fonda Marte         Nesmith               4/3/1980       11:38:09 AM    37.34         41                   4/12/2005        Q2                  H1               2005             4                 April                  Apr          12              Tuesday         Tue        12.3                  142222.0  2%                 265-99-6251             219-965-2622  Lowell         Lowell       Lowell         IN     46399     Midwest    lumarte        Gppy@ADHfd!#&/n  

480100        Mr.          Tracey      H               Davies       M       tracey.davies@gmail.com      Johnnie Davies      Mozella Davies      Kendall               6/26/1969      11:00:24 PM    48.12         54                   1/25/2008        Q1                  H1               2008             1                 January                Jan          25              Friday          Fri        9.51                  164526.0  20%                651-62-6591             316-697-8850  Topeka         Shawnee      Topeka         KS     66628     Midwest    thdavies       m[msp_vD9]}R     

693664        Mrs.         Lavinia     P               Laliberte    F       lavinia.laliberte@gmail.com  Johnnie Laliberte   Luis Laliberte      Marvel                10/24/1969     04:18:55 AM    47.79         48                   6/16/1999        Q2                  H1               1999             6                 June                   Jun          16              Wednesday       Wed        18.13                 139110.0  7%                 349-08-4429             218-694-4075  Stillwater     Washington   Stillwater     MN     55083     Midwest    lplaliberte    J0b!QS3i         

659851        Dr.          Leroy       P               Cedeno       M       leroy.cedeno@aol.com         Johnnie Cedeno      Dee Cedeno          Outlaw                9/5/1974       03:09:26 PM    42.92         60                   12/24/2015       Q4                  H2               2015             12                December               Dec          24              Thursday        Thu        1.59                  54225.0   14%                544-81-0354             209-673-6648  Moreno Valley  Riverside    Moreno Valley  CA     92557     West       lpcedeno       6ke<j_d.3        

Queried 20 documents from the search index in 93 milliseconds.

Suggestion query of 'Jo' returned 5 results in 6 milliseconds.
Suggestion{string='Joe Gowan', score=1.0, payload='null'}
Suggestion{string='Joe Shealy', score=1.0, payload='null'}
Suggestion{string='Josh Shook', score=1.0, payload='null'}
Suggestion{string='Jonas Dabbs', score=1.0, payload='null'}
Suggestion{string='Josef Howze', score=1.0, payload='null'}

```

### Java Foundation Classes

I included a collection of Java foundation classes that I often utilize when I am working with search-related content sources.  These foundation classes simplify the importing and exporting of data from a variety of content sources.

The foundation classes can be built as follows:

```
$ cd $APL/src/redis/foundation
$ mvn compile source:jar javadoc:jar install
```
