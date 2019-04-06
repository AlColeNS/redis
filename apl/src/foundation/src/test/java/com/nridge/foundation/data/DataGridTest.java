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

package com.nridge.foundation.data;

import com.nridge.foundation.std.Sleep;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class DataGridTest
{
	@Before
	public void setup()
	{
	}

	private void addMenuItem(DataGrid aGrid, String aName, boolean aIsVegetarian, int aCalories, String aType)
	{
		aGrid.newRow();
		aGrid.setValueByName("name", aName);
		aGrid.setValueByName("vegetarian", aIsVegetarian);
		aGrid.setValueByName("calories", aCalories);
		aGrid.setValueByName("type", aType);
		aGrid.addRow();
	}

	// Modern Java - page 85
	private DataGrid createDataGrid()
	{
		DataDoc dataSchema = new DataDoc("Menu Dish Schema");
		dataSchema.add(new DataItem.Builder().type(Data.Type.Text).name("name").title("Dish Name").build());
		dataSchema.add(new DataItem.Builder().type(Data.Type.Boolean).name("vegetarian").title("Is Vegetarian").build());
		dataSchema.add(new DataItem.Builder().type(Data.Type.Integer).name("calories").title("Calories").build());
		dataSchema.add(new DataItem.Builder().type(Data.Type.Text).name("type").title("Meal Type").build());

		DataGrid dataGrid = new DataGrid("Menu Dishes", dataSchema);
		addMenuItem(dataGrid, "pork", false, 800, "MEAT");
		addMenuItem(dataGrid, "beef", false, 700, "MEAT");
		addMenuItem(dataGrid, "chicken", false, 400, "MEAT");
		addMenuItem(dataGrid, "french fries", true, 530, "OTHER");
		addMenuItem(dataGrid, "rice", true, 350, "OTHER");
		addMenuItem(dataGrid, "season fruit", true, 120, "OTHER");
		addMenuItem(dataGrid, "pizza", true, 550, "OTHER");
		addMenuItem(dataGrid, "prawns", false, 300, "FISH");
		addMenuItem(dataGrid, "salmon", false, 450, "FISH");

		return dataGrid;
	}

	private void printDataItem(DataItem anItem)
	{
		System.out.printf("[%s] %s = %s%n", Data.typeToString(anItem.getType()), anItem.getName(), anItem.getCollapsedValues());
	}

	private void printDataDoc(DataDoc aDoc)
	{
		System.out.printf("%n%s%n", aDoc.getName());
		aDoc.getItems().forEach(this::printDataItem);
	}

	@Test
	public void dataGridCreationValidation()
	{
		Optional<DataDoc> optDataDoc;

		DataGrid dataGrid = createDataGrid();
		int rowCount = dataGrid.rowCount();
		for (int row = 0; row < rowCount; row++)
		{
			optDataDoc = dataGrid.getRowAsDocOptional(row);
			optDataDoc.ifPresent(this::printDataDoc);

//			optDataDoc.ifPresent(dd -> {
//				dd.getItems().forEach(di -> {
//					System.out.printf("[%s] %s = %s%n", Data.typeToString(di.getType()), di.getName(), di.getCollapsedValues());
//				});
//			});
		}
	}

	private String getType(DataDoc aDoc)
	{
		return aDoc.getItemByName("type").getValue();
	}

	private Integer getCalories(DataDoc aDoc)
	{
		return aDoc.getItemByName("calories").getValueAsInteger();
	}

	private DataDoc dropCalories(DataDoc aDoc)
	{
		DataDoc dataDoc = new DataDoc(aDoc);

		dataDoc.remove("calories");

		return dataDoc;
	}

	// https://www.baeldung.com/java-8-streams
	@Test
	public void dataGridStreamValidation()
		throws NoSuchElementException
	{
		DataGrid dataGrid = createDataGrid();
		List<String> threeHighCalorieDishNames = dataGrid.stream()
				.filter(dd -> dd.getItemByName("calories").getValueAsInteger() > 300)
				.map(dd -> dd.getItemByName("name").getValue())
				.limit(3)
				.collect(toList());
		System.out.println("--- threeHighCalorieDishNames ---");
		threeHighCalorieDishNames.forEach(System.out::println);

		// https://www.concretepage.com/java/jdk-8/java-8-stream-sorted-example
		List<DataDoc> listDataDocSorted = dataGrid.stream()
			   .sorted(Comparator.comparing(dd -> dd.getItemByName("calories").getValueAsInteger()))
			   .collect(toList());
		listDataDocSorted.size();
		System.out.println("--- listDataDocSorted ---");
		listDataDocSorted.forEach(this::printDataDoc);

		// https://howtodoinjava.com/sort/sort-on-multiple-fields/
		List<DataDoc> listDataDocSortedTwoColumns = dataGrid.stream()
				.sorted(Comparator.comparing(this::getType).thenComparing(this::getCalories))
			  	.collect(toList());
		System.out.println("--- listDataDocSortedTwoColumns ---");
		listDataDocSortedTwoColumns.forEach(this::printDataDoc);

		// Drop fields from document using map()
		List<DataDoc> listWithoutCalories = dataGrid.stream()
				.map(this::dropCalories)
				.collect(toList());
		listWithoutCalories.size();
		System.out.println("--- listWithoutCalories ---");
		listWithoutCalories.forEach(this::printDataDoc);

		// https://www.baeldung.com/apache-commons-math
		DescriptiveStatistics descriptiveStatistics = dataGrid.getDescriptiveStatistics("calories");
		double min = descriptiveStatistics.getMin();
		double max = descriptiveStatistics.getMax();
		double mean = descriptiveStatistics.getMean();
		double median = descriptiveStatistics.getPercentile(50);
		double standardDeviation = descriptiveStatistics.getStandardDeviation();
		System.out.println("--- DescriptiveStatistics ---");
		System.out.printf("min = %.2f, max = %.2f, mean = %.2f, median = %.2f, std = %.2f%n", min, max, mean, median, standardDeviation);
	}

	@After
	public void cleanup()
	{
	}
}
