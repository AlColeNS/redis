/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.nridge.redis;

import com.nridge.foundation.data.Data;
import com.nridge.foundation.data.DataDoc;
import com.nridge.foundation.data.DataGrid;
import com.nridge.foundation.io.DataGridCSV;
import com.nridge.foundation.io.DataGridConsole;
import io.redisearch.*;
import io.redisearch.client.Client;
import io.redisearch.client.SuggestionOptions;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * This Main class will exercise the features of the RediSearch module.
 *
 * @author Al Cole
 * @since 1.0
 */
public class Main
{
	/**
	 * Load a DataGrid from a CSV file.  This method assumes that the CSV
	 * file contains a header row identifying the column name, data type
	 * and title.
	 *
	 * @param aPathFileName Path/file name of the CSV file
	 *
	 * @return DataGrid instance populated with the CSV rows
	 *
	 * @throws IOException I/O Exception
	 */
	private static DataGrid loadCSVFile(String aPathFileName)
		throws IOException
	{
		DataGridCSV dataGridCSV = new DataGridCSV();
		Optional<DataGrid> optDataGrid = dataGridCSV.load(aPathFileName, true);
		if (optDataGrid.isPresent())
			return optDataGrid.get();
		else
			return new DataGrid("CSV Empty Data Grid");
	}

	/**
	 * Maps a DataDoc to a RediSearch document field map.
	 *
	 * @param aDataDoc DataDoc instance
	 *
	 * @return RediSearch document field map
	 */
	private static Map<String, Object> dataDocToRedisDoc(DataDoc aDataDoc)
	{
		Map<String, Object> docFields = new HashMap<String, Object>();
		aDataDoc.getItems().forEach(di -> {
			switch (di.getType())
			{
				case Integer:
					docFields.put(di.getName(), di.getValueAsInteger());
					break;
				case Float:
				case Double:
					docFields.put(di.getName(), di.getValueAsDouble());
					break;
				default:
					docFields.put(di.getName(), di.getValue());
					break;
			}
		});

		return docFields;
	}

	/**
	 * Maps a list of RediSearch documents to a DataGrid instance.
	 *
	 * @param aName Name of the DataGrid
	 * @param aColumns DataDoc identifying the grid columns
	 * @param aResults RediSearch list of results
	 *
	 * @return DataGrid instance
	 */
	private static DataGrid searchResultsToDataGrid(String aName, DataDoc aColumns, List<Document> aResults)
	{
		DataGrid resultGrid = new DataGrid(aName, aColumns);
		for (Document searchDocument : aResults)
		{
			resultGrid.newRow();
			for (Map.Entry<String, Object> entry : searchDocument.getProperties())
				resultGrid.setValueByName(entry.getKey(), entry.getValue().toString());
			resultGrid.addRow();
		}

		return resultGrid;
	}

	/**
	 * Exercises the RediSearch features.
	 *
	 * @throws IOException I/O exception
	 */
	private static void exerciseSearchFeatures()
		throws IOException
	{
		DataDoc dataDoc;
		String fullName;
		Suggestion redisSuggestion;
		Optional<DataDoc> optDataDoc;
		Map<String, Object> docFields;

		System.out.printf("Exercising the RediSearch document indexing and search features.%n");

// Load our HR records from the CSV file.

		DataGrid dataGrid = loadCSVFile("data/hr-records.csv");

// Create our Redis Java client instance.

		Client redisClient = new Client("hrsearch", "localhost", 6379);

// Drop an existing index from a prior run of this test method.

		redisClient.dropIndex(true);

// Create a search schema based on the DataGrid columns.

		Schema redisSchema = new Schema();
		dataGrid.getColumns().getItems().forEach(di -> {
			String itemName = di.getName();
			if (Data.isNumber(di.getType()))
				redisSchema.addNumericField(di.getName());
			else
			{
				if (itemName.endsWith("_name"))
					redisSchema.addTextField(itemName, 2.0);	// Weight columns referring to a name more
				else
					redisSchema.addTextField(itemName, 1.0);
			}
		});

// Create the search index.

		StopWatch stopWatch = StopWatch.createStarted();
		redisClient.createIndex(redisSchema, Client.IndexOptions.Default());
		System.out.printf("Created search index in %d milliseconds.%n", stopWatch.getTime(TimeUnit.MILLISECONDS));

		stopWatch = StopWatch.createStarted();
		int rowCount = dataGrid.rowCount();
		for (int row = 0; row < rowCount; row++)
		{
			optDataDoc = dataGrid.getRowAsDocOptional(row);
			if (optDataDoc.isPresent())
			{
				dataDoc = optDataDoc.get();
				docFields = dataDocToRedisDoc(dataDoc);
				redisClient.addDocument(String.format("Document_%04d", row), docFields);
				if (row < 1000)
				{
					fullName = dataDoc.getItemByName("first_name").getValue() + " " + dataDoc.getItemByName("last_name").getValue();
					redisSuggestion = Suggestion.builder().str(fullName).build();
					redisClient.addSuggestion(redisSuggestion, false);
				}
			}
		}
		System.out.printf("Added %d documents to the search index in %d milliseconds.%n", rowCount,
						  stopWatch.getTime(TimeUnit.MILLISECONDS));

// Build a complex query criteria.

		stopWatch = StopWatch.createStarted();
		String queryTerm = "Johnny";
		Query redisQuery = new Query(queryTerm)
				.addFilter(new Query.NumericFilter("age_in_years", 30, 50))
				.limit(0,50);

// Execute the search criteria.

		SearchResult searchResult = redisClient.search(redisQuery);
		long totalResults = searchResult.totalResults;
		if (totalResults > 0)
		{
			String resultTitle = String.format("Results of Query for '%s'", queryTerm);
			DataGrid resultsGrid = searchResultsToDataGrid(resultTitle, dataGrid.getColumns(), searchResult.docs);
			PrintWriter printWriter = new PrintWriter(System.out, true);
			DataGridConsole dataGridConsole = new DataGridConsole();
			dataGridConsole.write(resultsGrid, printWriter, resultTitle);
		}
		System.out.printf("Queried %d documents from the search index in %d milliseconds.%n", totalResults,
						  stopWatch.getTime(TimeUnit.MILLISECONDS));

// List some suggestions.

		stopWatch = StopWatch.createStarted();
		String suggestionQuery = "Jo";
		SuggestionOptions suggestionOptions = SuggestionOptions.builder().build();
		List<Suggestion> suggestionList = redisClient.getSuggestion(suggestionQuery, suggestionOptions);
		System.out.printf("Suggestion query of '%s' returned %d results in %d milliseconds.%n", suggestionQuery,
						  suggestionList.size(), stopWatch.getTime(TimeUnit.MILLISECONDS));
		for (Suggestion suggestion : suggestionList)
			System.out.printf("%s%n", suggestion.toString());
	}

	public static void main(String[] anArgs)
	{
		try
		{
			exerciseSearchFeatures();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		System.exit(0);
	}
}
