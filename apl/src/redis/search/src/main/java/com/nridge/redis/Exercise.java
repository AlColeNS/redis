package com.nridge.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.redisearch.Document;
import io.redisearch.Query;
import io.redisearch.Schema;
import io.redisearch.SearchResult;
import io.redisearch.client.Client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Exercise
{
	/**
	 * Exercises core hash set commands using the Java client.
	 */
	private static void exerciseCoreFeatures()
	{
		RedisClient redisClient = RedisClient.create("redis://localhost:6379/");
		StatefulRedisConnection<String, String> redisConnection = redisClient.connect();

		RedisCommands<String, String> syncCommands = redisConnection.sync();
		syncCommands.set("key", "Hello, Redis!");
		String keyValue = syncCommands.get("key");

		syncCommands.hset("recordName", "FirstName", "John");
		syncCommands.hset("recordName", "LastName", "Smith");
		Map<String, String> record = syncCommands.hgetall("recordName");

		redisConnection.close();
	}

	/**
	 * Exercises a simple search index create, document store and query operation.
	 */
	private static void exerciseSearchFeatures()
	{
		Client redisClient = new Client("testsearch", "localhost", 6379);

		Schema redisSchema = new Schema()
				.addTextField("title", 5.0)
				.addTextField("body", 1.0)
				.addNumericField("price");

		redisClient.createIndex(redisSchema, Client.IndexOptions.Default());

		Map<String, Object> fields = new HashMap<String, Object>();
		fields.put("title", "hello world");
		fields.put("body", "lorem ipsum");
		fields.put("price", 1337);

		redisClient.addDocument("doc1", fields);

// Creating a complex query
		Query redisQuery = new Query("hello world")
				.addFilter(new Query.NumericFilter("price", 0, 1500))
				.limit(0,5);

// Actual search
		SearchResult searchResult = redisClient.search(redisQuery);
		long totalResults = searchResult.totalResults;
		List<Document> searchDocuments = searchResult.docs;
		searchDocuments.size();

		redisClient.dropIndex();
	}

}
