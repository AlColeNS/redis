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

package com.nridge.foundation.io;

import com.nridge.foundation.data.Data;
import com.nridge.foundation.data.DataDoc;
import com.nridge.foundation.data.DataGrid;
import com.nridge.foundation.data.DataItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class DataDocJSONTest
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
		dataSchema.add(new DataItem.Builder().name("name").title("Dish Name").build());
		dataSchema.add(new DataItem.Builder().type(Data.Type.Boolean).name("vegetarian").title("Is Vegetarian").build());
		dataSchema.add(new DataItem.Builder().type(Data.Type.Integer).name("calories").title("Calories").build());
		dataSchema.add(new DataItem.Builder().name("type").title("Meal Type").build());

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

	@Test
	public void saveDataDocAsJSON()
		throws IOException
	{
		DataDoc dataDoc;

		DataGrid dataGrid = createDataGrid();
		List<DataDoc> dataDocList = dataGrid.getRowsAsDocList();

		dataDoc = dataDocList.get(0);
		DataDocJSON dataDocJSON = new DataDocJSON();
		String pathFileName = "data/datadoc.json";
		dataDocJSON.save(pathFileName, dataDoc);
	}

	@Test
	public void loadBestBuyJSON()
		throws IOException
	{
		DataDocJSON dataDocJSON = new DataDocJSON();
		String pathFileName = "data/bestbuy-all.json";
		Optional<DataDoc> optDataDoc = dataDocJSON.load(pathFileName);
		DataDoc dataDoc = optDataDoc.get();
		dataDoc.count();
	}

	@Test
	public void loadAndSaveBestBuyJSON()
		throws IOException
	{
		DataDocJSON dataDocJSON = new DataDocJSON();
		String inPathFileName = "data/bestbuy-all.json";
		Optional<DataDoc> optDataDoc = dataDocJSON.load(inPathFileName);
		DataDoc dataDoc = optDataDoc.get();

		String outPathFileName = "data/bestbuy-test.json";
		dataDocJSON.save(outPathFileName, dataDoc);
	}

	@After
	public void cleanup()
	{
	}
}
