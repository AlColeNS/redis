#!/usr/local/bin/python3

import json
import redis
import configparser

def create_connection(host_name, port_number, db_password):
    """ Creates a Redis client connection to the cluster.
    :param host_name: Host name of a Redis node
    :param port_number: Port number Redis service is listening on
    :param db_password: Redis authentication password
    :return: Redis connection instance
    """
    redis_connection = redis.Redis(host=host_name, port=port_number, password=db_password)
    return redis_connection

def consume_messages(redis_connection, topic_name='Employees', max_messages=-1, subscribe_timeout=0.0):
    """
    Consumes messages from a Redis topic channel and outputs them to the console.
    :param redis_connection: redis_connection: Redis connection instance
    :param topic_name: Name of the messaging topic
    :param max_messages: If non-negative, it limits the number of messages processed
    :param subscribe_timeout: Interrupts blocking read (in seconds)
    """
    message_count = 0
    publish_stream = redis_connection.pubsub()
    publish_stream.subscribe(topic_name)
    is_done = False
    print("Waiting up to {} seconds for messages to arrive".format(subscribe_timeout))
    while not is_done:
        message = publish_stream.get_message(timeout=subscribe_timeout)
        if message is None:
            break
        elif (message_count == 0):
            print("Subscribed to channel '{}'\n".format(message['channel'].decode('ascii')))
        else:
            employee_row = json.loads(message['data'])
            print("[{}] Receieved on {} : {} = {} {}".format(message_count, topic_name, employee_row['ssn'], employee_row['first_name'], employee_row['last_name']))
        message_count += 1
        if (max_messages > 0) and (message_count >= max_messages):
            is_done = True
    print("\nDone\n")

def main():
    print("This script will exercise the Redis subscribe features using Python client\n")
    cfg_parser = configparser.ConfigParser()
    cfg_parser.read("cfg/properties.ini")
    host_name = cfg_parser['Redis']['host_name']
    port_number = int(cfg_parser['Redis']['port_number'])
    db_password = cfg_parser['Redis']['db_password']
    topic_name = cfg_parser['Redis']['publish_subscribe_topic']
    subscribe_timeout = float(cfg_parser['Redis']['subscribe_timeout'])
    max_pubsub_messages = int(cfg_parser['Redis']['max_pubsub_messages'])
    redis_connection = create_connection(host_name, port_number, db_password)
    if (redis_connection.ping()):
        consume_messages(redis_connection, topic_name=topic_name, max_messages=max_pubsub_messages, subscribe_timeout=subscribe_timeout)
    else:
        print("Redis service {}:{} is not responding to a ping request".format(host_name, port_number))

if __name__== "__main__":
    main()

