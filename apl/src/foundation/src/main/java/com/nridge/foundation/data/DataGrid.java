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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.*;
import java.util.stream.Stream;

/**
 * A DataGrid manages a collection of columns and rows of data items.
 */
public class DataGrid
{
	private String mName = StringUtils.EMPTY;
	private DataDoc mColumns = new DataDoc("Data Grid");
	private Optional<HashMap<String, ArrayList<String>>> mNewRowMap = Optional.empty();
	private ArrayList<HashMap<String, ArrayList<String>>> mRows = new ArrayList<HashMap<String, ArrayList<String>>>();

	/**
	 * Constructor that accepts a name for the data grid.
	 *
	 * @param aName Name of the data grid
	 */
	public DataGrid(String aName)
	{
		mName = aName;
	}

	/**
	 * Constructor that accepts a name and a data document instance representing
	 * the columns.
	 *
	 * @param aName Name of the grid
	 * @param aColumns Data document instance
	 */
	public DataGrid(String aName, DataDoc aColumns)
	{
		mName = aName;
		mColumns = aColumns;
	}

	/**
	 * Returns a string summary representation of a DataGrid.
	 *
	 * @return String summary representation of this DataGrid.
	 */
	@Override
	public String toString()
	{
		String idName;

		if (StringUtils.isEmpty(mName))
			idName = "Data Table";
		else
			idName = mName;

		return String.format("%s [%d cols x %d rows]", idName, mColumns.count(), mRows.size());
	}

	/**
	 * Adds a data item to the document instance.
	 *
	 * @param anItem Data item instance
	 */
	public void addCol(DataItem anItem)
	{
		if (rowCount() == 0)
			mColumns.add(anItem);
	}

	/**
	 * Count of data grid columns.
	 *
	 * @return Count of data grid columns
	 */
	public int colCount()
	{
		return mColumns.count();
	}

	/**
	 * Returns the columns of the data grid as a data doucument instance
	 *
	 * @return Data document instance
	 */
	public DataDoc getColumns()
	{
		return mColumns;
	}

	private ArrayList<String> cellValues(String aValue)
	{
		ArrayList<String> cellValues = new ArrayList<>();
		if (StringUtils.isNotEmpty(aValue))
			cellValues.add(aValue);

		return cellValues;
	}

	/**
	 * Returns a map representing a new row of cell values.
	 *
	 * @return Map representing a row of cell values
	 */
	public Map<String,ArrayList<String>> newRow()
	{
		LinkedHashMap<String, ArrayList<String>> newRowMap = new LinkedHashMap<String, ArrayList<String>>();
		for (DataItem dataItem : mColumns.getItems())
			newRowMap.put(dataItem.getName(), cellValues(dataItem.getDefaultValue()));
		mNewRowMap = Optional.of(newRowMap);

		return newRowMap;
	}

	/**
	 * Adds the data document instance as a new row to the data grid instance.
	 *
	 * @param aDataDoc Data document instance
	 *
	 * @return <i>true</i> if the add was successful or <i>false</i> otherwise
	 */
	public boolean addRow(final DataDoc aDataDoc)
	{
		if ((aDataDoc != null) && (aDataDoc.count() == mColumns.count()))
		{
			LinkedHashMap<String, ArrayList<String>> rowMap = new LinkedHashMap<String, ArrayList<String>>();
			for (DataItem dataItem : aDataDoc.getItems())
				rowMap.put(dataItem.getName(), dataItem.getValues());
			mRows.add(rowMap);
			return true;
		}
		return false;
	}

	/**
	 * Assigns a cell value by name to a previously staged row instance.  You should call
	 * the newRow() method prior to using this row.
	 *
	 * @param aName Name of the cell to assign the value to
	 * @param aValue Value to assign to the cell
	 */
	public void setValueByName(String aName, String aValue)
	{
		mNewRowMap.ifPresent(r -> r.put(aName, cellValues(aValue)));
	}

	/**
	 * Assigns a cell values by name to a previously staged row instance.  You should call
	 * the newRow() method prior to using this row.
	 *
	 * @param aName Name of the cell to assign the value to
	 * @param aValues Values to assign to the cell
	 */
	public void setValuesByName(String aName, ArrayList<String> aValues)
	{
		mNewRowMap.ifPresent(r -> r.put(aName, aValues));
	}

	/**
	 * Assigns a cell value by name to a previously staged row instance.  You should call
	 * the newRow() method prior to using this row.
	 *
	 * @param aName Name of the cell to assign the value to
	 * @param aValue Value to assign to the cell
	 */
	public void setValueByName(String aName, Boolean aValue)
	{
		mNewRowMap.ifPresent(r -> r.put(aName, cellValues(aValue.toString())));
	}

	/**
	 * Assigns a cell value by name to a previously staged row instance.  You should call
	 * the newRow() method prior to using this row.
	 *
	 * @param aName Name of the cell to assign the value to
	 * @param aValue Value to assign to the cell
	 */
	public void setValueByName(String aName, Integer aValue)
	{
		mNewRowMap.ifPresent(r -> r.put(aName, cellValues(aValue.toString())));
	}

	/**
	 * Adds a row of cell values to the data grid.  You should have called the newRow()
	 * and setValueByName() methods prior to calling this method.
	 *
	 * @return <i>true</i> if the add was successful or <i>false</i> otherwise
	 */
	public boolean addRow()
	{
		if (mNewRowMap.isPresent())
		{
			mRows.add(mNewRowMap.get());
			mNewRowMap = Optional.empty();
			return true;
		}
		return false;
	}

	/**
	 * Count of rows in the data grid.
	 *
	 * @return Row count
	 */
	public int rowCount()
	{
		return mRows.size();
	}

	/**
	 * Returns an list of maps that represent the cell values in the data grid.
	 *
	 * @return List of all rows from the data grid
	 */
	public ArrayList<HashMap<String, ArrayList<String>>> getRows()
	{
		return mRows;
	}

	/**
	 * Returns a data item representing a cell from the data grid.
	 *
	 * @param anEntry Representing a cell in the data grid row
	 *
	 * @return Data item instance
	 *
	 * @throws NoSuchElementException If the name cannot be matched to a cell
	 */
	public DataItem rowEntryToDataItem(Map.Entry<String, ArrayList<String>> anEntry)
		throws NoSuchElementException
	{
		DataItem curDataItem = mColumns.getItemByName(anEntry.getKey());
		DataItem newDataItem = new DataItem(curDataItem);
		newDataItem.setValues(anEntry.getValue());

		return newDataItem;
	}

	/**
	 * Returns a data item representing a cell from the data grid.
	 *
	 * @param anEntry Representing a cell in the data grid row
	 *
	 * @return Optional data item instance
	 */
	public Optional<DataItem> rowEntryToDataItemOptional(Map.Entry<String, ArrayList<String>> anEntry)
	{
		Optional<DataItem> optDataItem = mColumns.getItemByNameOptional(anEntry.getKey());
		if (optDataItem.isPresent())
		{
			DataItem dataItem = new DataItem(optDataItem.get());
			dataItem.setValues(anEntry.getValue());
			return Optional.of(dataItem);
		}
		else
			return optDataItem;
	}

	/**
	 * Returns a data document representing a row from the data grid.
	 *
	 * @param aRowOffset Row offset in the data grid
	 *
	 * @return Optional data document instance
	 */
	public Optional<DataDoc> getRowAsDocOptional(int aRowOffset)
	{
		DataDoc dataDoc = null;

		if (aRowOffset < mRows.size())
		{
			dataDoc = new DataDoc(mColumns);
			for (Map.Entry<String, ArrayList<String>> entry : mRows.get(aRowOffset).entrySet())
			{
				Optional<DataItem> optDataItem = rowEntryToDataItemOptional(entry);
				if (optDataItem.isPresent())
					dataDoc.add(optDataItem.get());
			}
		}

		return Optional.ofNullable(dataDoc);
	}

	/**
	 * Returns a data document representing a row from the data grid.
	 *
	 * @param aRowOffset Row offset in the data grid
	 *
	 * @return Data document instance
	 */
	public DataDoc getRowAsDoc(int aRowOffset)
	{
		DataDoc dataDoc = null;

		if (aRowOffset < mRows.size())
		{
			dataDoc = new DataDoc(mColumns);
			for (Map.Entry<String, ArrayList<String>> entry : mRows.get(aRowOffset).entrySet())
			{
				Optional<DataItem> optDataItem = rowEntryToDataItemOptional(entry);
				if (optDataItem.isPresent())
					dataDoc.add(optDataItem.get());
			}
			return dataDoc;
		}
		else
			throw new NoSuchElementException(String.format("Row offset %d is out of range.", aRowOffset));
	}

	/**
	 * Returns a list of data document instances representing the rows in the data grid.
	 *
	 * @return List of data document instances
	 */
	public List<DataDoc> getRowsAsDocList()
	{
		Optional<DataDoc> optDataDoc;

		List<DataDoc> dataDocList = new ArrayList<>();
		int rowCount = rowCount();
		for (int row = 0; row < rowCount; row++)
		{
			optDataDoc = getRowAsDocOptional(row);
			optDataDoc.ifPresent(dataDocList::add);
		}

		return dataDocList;
	}

	/**
	 * Returns a stream of data documents representing the data grid.
	 *
	 * @return Stream of data documents
	 */
	public Stream<DataDoc> stream()
	{
		return getRowsAsDocList().stream();
	}

	/**
	 * Returns a descriptive statistics instance for the column of values identified
	 * by the name.
	 *
	 * @param aName Name of the column
	 *
	 * @return Descriptive statistics instance
	 *
	 * @throws NoSuchElementException If the name cannot be matched to a column
	 */
	public DescriptiveStatistics getDescriptiveStatistics(String aName)
		throws NoSuchElementException
	{
		DataDoc dataDoc;
		DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();

		int rowCount = rowCount();
		DataItem dataItem = mColumns.getItemByName(aName);
		if (Data.isNumber(dataItem.getType()))
		{
			for (int row = 0; row < rowCount; row++)
			{
				dataDoc = getRowAsDoc(row);
				descriptiveStatistics.addValue(dataDoc.getItemByName(aName).getValueAsDouble());
			}
		}

		return descriptiveStatistics;
	}
}
