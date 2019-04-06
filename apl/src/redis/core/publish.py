#!/usr/local/bin/python3

import csv
import json
import redis
import configparser

def create_connection(host_name, port_number, db_password):
    """
    Creates a Redis client connection to the cluster.
    :param host_name: Host name of a Redis node
    :param port_number: Port number Redis service is listening on
    :param db_password: Redis authentication password
    :return: Redis connection instance
    """
    redis_connection = redis.Redis(host=host_name, port=port_number, password=db_password)
    return redis_connection

def publish_messages(redis_connection, csv_file_name, topic_name='Employees', max_messages=-1):
    """
    Publishes the rows of a CSV to a Redis cluster - writing each record to the console.
    :param redis_connection: Redis connection instance
    :param csv_file_name: Name of CSV file
    :param topic_name: Name of the messaging topic
    :param max_messages: If non-negative, it limits the number of messages processed
    """
    with open(csv_file_name) as csv_file:
        csv_reader = csv.DictReader(csv_file)
        row_count = 0
        for row in csv_reader:
            if row_count == 0:
                print("CSV columns successfully read")
            else:
                row_json = json.dumps(row)
                redis_connection.publish(topic_name, row_json)
                print("[{}] Published on {} : {} = {} {}".format(row_count, topic_name, row['ssn'], row['first_name'], row['last_name']))
            row_count += 1
            if (max_messages > 0) and (row_count > max_messages):
                break
    print("\nDone\n")

def main():
    print("This script will exercise the Redis publish features using Python client\n")
    cfg_parser = configparser.ConfigParser()
    cfg_parser.read("cfg/properties.ini")
    host_name = cfg_parser['Redis']['host_name']
    port_number = int(cfg_parser['Redis']['port_number'])
    db_password = cfg_parser['Redis']['db_password']
    csv_file_name = cfg_parser['Redis']['csv_file_name']
    topic_name = cfg_parser['Redis']['publish_subscribe_topic']
    max_pubsub_messages = int(cfg_parser['Redis']['max_pubsub_messages'])
    redis_connection = create_connection(host_name, port_number, db_password)
    if (redis_connection.ping()):
        publish_messages(redis_connection, csv_file_name, topic_name=topic_name, max_messages=max_pubsub_messages)
    else:
        print("Redis service {}:{} is not responding to a ping request".format(host_name, port_number))

if __name__== "__main__":
    main()

