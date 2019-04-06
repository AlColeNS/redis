#!/usr/local/bin/python3

# https://pypi.org/project/redis/

import csv
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

def load_csv_records(csv_file_name, max_records=100):
    """
    Loads a list of sample records from the CSV file up to the maximum record count.
    :param csv_file_name: Name of CSV file
    :param max_records: Maximum number of records to load
    :return: List of records
    """
    record_list = []
    print("Loading {} records from {} file".format(max_records, csv_file_name))
    with open(csv_file_name) as csv_file:
        csv_reader = csv.DictReader(csv_file)
        row_count = 0
        for row in csv_reader:
            if row_count == 0:
                pass
            else:
                record_list.append(row)
            row_count += 1
            if (row_count > max_records):
                break
    print("Done\n")
    return record_list

def exercise_strings_simple(redis_connection, record_list):
    """
    Exercise the Redis simple string data structure features.
    :param redis_connection: Redis connection instance
    :param record_list: List of records
    """
    print("This function will exercise the Redis simple string data structure features.")
    # Cache our strings in the Redis cluster
    record_number = 1
    for record in record_list:
        record_full_name = record['first_name'] + ' ' + record['last_name']
        key_name = 'full_name_' + str(record_number)
        redis_connection.set(key_name, record_full_name)
        record_number += 1
    # Validate and clean up our strings from the Redis cluster
    record_number = 1
    successful_matches = 0
    for record in record_list:
        record_full_name = record['first_name'] + ' ' + record['last_name']
        key_name = 'full_name_' + str(record_number)
        cache_full_name_binary = redis_connection.get(key_name)
        if (cache_full_name_binary is not None):
            cache_full_name_text = cache_full_name_binary.decode('utf-8')
            if (cache_full_name_text == record_full_name):
                redis_connection.delete(key_name)
                successful_matches += 1
            else:
                print("Key/value mismatch: {} expected {} but got {}".format(key_name, record_full_name, cache_full_name_text))
        else:
            print("Key/value mismatch: {} expected {}".format(key_name, record_full_name))
        record_number += 1
    print("Successfully matched {} key/values\n".format(successful_matches))

def exercise_strings_pipeline(redis_connection, record_list):
    """
    Exercise the Redis simple string data structure features using a pipeline.
    :param redis_connection: Redis connection instance
    :param record_list: List of records
    """
    print("This function will exercise the Redis simple string data structure features using a pipeline.")
    # Cache our strings in the Redis cluster using a pipeline
    redis_pipeline = redis_connection.pipeline()
    record_number = 1
    for record in record_list:
        record_full_name = record['first_name'] + ' ' + record['last_name']
        key_name = 'full_name_' + str(record_number)
        redis_pipeline.set(key_name, record_full_name)
        record_number += 1
    redis_responses = redis_pipeline.execute()
    for response in redis_responses:
        if (not response):
            print("Response indicates that one or more pipeline operations failed.")
            break
    # Validate and clean up our strings from the Redis cluster
    record_number = 1
    successful_matches = 0
    for record in record_list:
        record_full_name = record['first_name'] + ' ' + record['last_name']
        key_name = 'full_name_' + str(record_number)
        cache_full_name_binary = redis_connection.get(key_name)
        if (cache_full_name_binary is not None):
            cache_full_name_text = cache_full_name_binary.decode('utf-8')
            if (cache_full_name_text == record_full_name):
                redis_connection.delete(key_name)
                successful_matches += 1
            else:
                print("Key/value mismatch: {} expected {} but got {}".format(key_name, record_full_name, cache_full_name_text))
        else:
            print("Key/value mismatch: {} expected {}".format(key_name, record_full_name))
        record_number += 1
    print("Successfully matched {} key/values\n".format(successful_matches))

def exercise_strings_increment(redis_connection, record_list):
    """
    Exercise the Redis increment string data structure features.
    :param redis_connection: Redis connection instance
    :param record_list: List of records
    """
    print("This function will exercise the Redis increment string data structure features.")
    # Cache our strings in the Redis cluster
    record_number = 1
    for record in record_list:
        record_age_in_years = record['age_in_years']
        key_name = 'age_' + str(record_number)
        redis_connection.set(key_name, round(float(record_age_in_years)))
        record_number += 1
    # Increment all of the key values by 10 years
    record_number = 1
    increment_by_amount = 10
    for record in record_list:
        key_name = 'age_' + str(record_number)
        redis_connection.incrby(key_name, increment_by_amount)
        record_number += 1
    # Validate and clean up our strings from the Redis cluster
    record_number = 1
    successful_matches = 0
    for record in record_list:
        record_age_in_years = record['age_in_years']
        record_age_in_years_plus_10 = round(float(record_age_in_years)) + increment_by_amount
        key_name = 'age_' + str(record_number)
        cache_age_in_years_binary = redis_connection.get(key_name)
        if (cache_age_in_years_binary is not None):
            cache_cache_age_in_years_binary_text = cache_age_in_years_binary.decode('utf-8')
            if (int(cache_cache_age_in_years_binary_text) == record_age_in_years_plus_10):
                redis_connection.delete(key_name)
                successful_matches += 1
            else:
                print("Key/value mismatch: {} expected {} but got {}".format(key_name, record_age_in_years_plus_10, int(cache_cache_age_in_years_binary_text)))
        else:
            print("Key/value mismatch: {} expected {}".format(key_name, record_age_in_years_plus_10))
        record_number += 1
    print("Successfully matched {} key/values\n".format(successful_matches))

def exercise_strings_pushpop(redis_connection, record_list):
    """
    Exercise the Redis push/pop string data structure features.
    :param redis_connection: Redis connection instance
    :param record_list: List of records
    """
    print("This function will exercise the Redis push/pop string data structure features.")
    # Cache our strings in the Redis cluster
    key_name = 'full_name'
    for record in record_list:
        record_full_name = record['first_name'] + ' ' + record['last_name']
        redis_connection.rpush(key_name, record_full_name)
    # Validate and clean up our strings from the Redis cluster
    record_number = 0
    expected_full_name_count = len(record_list)
    cache_full_name_binary = redis_connection.lpop(key_name)
    while cache_full_name_binary is not None:
        record_number += 1
        cache_full_name_binary = redis_connection.lpop(key_name)
    if (record_number == expected_full_name_count):
        print("Successfully pushed/popped {} full name strings.\n".format(record_number))
    else:
        print("Failed to pop {} full name strings.\n".format(record_number))

def exercise_hashes_pipeline(redis_connection, record_list):
    """
    Exercise the Redis hash data structure features using a pipeline.
    :param redis_connection: Redis connection instance
    :param record_list: List of records
    """
    print("This function will exercise the Redis hash data structure features using a pipeline.")
    redis_pipeline = redis_connection.pipeline()
    # Cache our hashes in the Redis cluster
    for record in record_list:
        key_string = 'ssn:' + record['ssn']
        for name, value in record.items():
            redis_pipeline.hset(key_string, name, value)
    redis_responses = redis_pipeline.execute()
    for response in redis_responses:
        if (not response):
            print("Response indicates that one or more pipeline operations failed.")
            break
    # Validate and clean up our hashes from the Redis cluster
    successful_matches = 0
    for record in record_list:
        key_string = 'ssn:' + record['ssn']
        cache_record = redis_connection.hgetall(key_string)
        if (cache_record is not None):
            successful_matches += 1
        redis_connection.delete(key_string)
    print("Successfully matched {} key/values\n".format(successful_matches))

def exercise_sets_pipeline(redis_connection, record_list):
    """
    Exercise the Redis set data structure features using a pipeline.
    :param redis_connection: Redis connection instance
    :param record_list: List of records
    """
    print("This function will exercise the Redis set data structure features using a pipeline.")
    # Cache our sets in the Redis cluster using a pipeline
    set_name = 'full_name'
    redis_pipeline = redis_connection.pipeline()
    for record in record_list:
        record_full_name = record['first_name'] + ' ' + record['last_name']
        redis_pipeline.sadd(set_name, record_full_name)
    redis_responses = redis_pipeline.execute()
    for response in redis_responses:
        if (not response):
            print("Response indicates that one or more pipeline operations failed.")
            break
    # Validate and clean up our sets from the Redis cluster
    print("Set cardinality is {}\n".format(redis_connection.scard(set_name)))
    redis_connection.delete(set_name)

def exercise_sorted_sets_pipeline(redis_connection, record_list):
    """
    Exercise the Redis sorted set data structure features.
    :param redis_connection: Redis connection instance
    :param record_list: List of records
    """
    print("This function will exercise the Redis sorted set data structure features.")
    # Cache our sets in the Redis cluster using a pipeline
    set_name = 'compensation'
    for record in record_list:
        record_full_name = record['first_name'] + ' ' + record['last_name']
        name_value = {record_full_name : int(record['salary'])}
        redis_connection.zadd(set_name, name_value)
    # Sort, display and clean up our sets from the Redis cluster
    print("\nEmployee compensation in ascending order:")
    print(redis_connection.zrange(set_name, 0, 5, withscores=True))
    print("\nEmployee compensation in descending order:")
    print(redis_connection.zrange(set_name, 0, 5, withscores=True, desc=True))
    redis_connection.delete(set_name)

def main():
    print("\nThis script will exercise the Redis data structure features using Python client.\n")
    cfg_parser = configparser.ConfigParser()
    cfg_parser.read("cfg/properties.ini")
    host_name = cfg_parser['Redis']['host_name']
    port_number = int(cfg_parser['Redis']['port_number'])
    db_password = cfg_parser['Redis']['db_password']
    csv_file_name = cfg_parser['Redis']['csv_file_name']
    max_records = int(cfg_parser['Redis']['max_data_structure_records'])
    redis_connection = create_connection(host_name, port_number, db_password)
    if (redis_connection.ping()):
        record_list = load_csv_records(csv_file_name, max_records)
        exercise_strings_simple(redis_connection, record_list)
        exercise_strings_pipeline(redis_connection, record_list)
        exercise_strings_increment(redis_connection, record_list)
        exercise_strings_pushpop(redis_connection, record_list)
        exercise_hashes_pipeline(redis_connection, record_list)
        exercise_sets_pipeline(redis_connection, record_list)
        exercise_sorted_sets_pipeline(redis_connection, record_list)
    else:
        print("Redis service {}:{} is not responding to a ping request".format(host_name, port_number))

if __name__== "__main__":
    main()