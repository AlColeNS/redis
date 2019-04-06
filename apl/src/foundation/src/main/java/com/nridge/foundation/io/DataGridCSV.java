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
import com.nridge.foundation.std.StrUtl;
import org.apache.commons.lang3.StringUtils;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.util.List;
import java.util.Optional;

/**
 * The DataGridCSV provides a collection of methods that can generate/load
 * a CSV representation of a <i>DataGrid</i> object.
 * <p>
 * This class utilizes the
 * <a href="http://supercsv.sourceforge.net">SuperCSV</a>
 * framework to manage these transformations.
 * </p>
 *
 * @author Al Cole
 * @since 1.0
 */
public class DataGridCSV
{
	private DataGrid mDataGrid;
	private boolean mIsFieldNamePreferred;

	/**
	 * Default constructor
	 */
	public DataGridCSV()
	{
		mDataGrid = new DataGrid("Data Grid from CSV");
	}

	/**
	 * Constructor that accepts a data grid instance.
	 *
	 * @param aDataGrid Data grid instance
	 */
	public DataGridCSV(DataGrid aDataGrid)
	{
		mDataGrid = aDataGrid;
	}

	/**
	 * Return an instance to the internally managed data grid.
	 *
	 * @return Data grid instance.
	 */
	public DataGrid getDataGrid()
	{
		return mDataGrid;
	}

	/**
	 * If assigned to <i>true</i>, then the field names will be used
	 * for the header row.
	 *
	 * @param aIsFieldNamePreferred Field name preference flag.
	 */
	public void setFieldNamePreferred(boolean aIsFieldNamePreferred)
	{
		mIsFieldNamePreferred = aIsFieldNamePreferred;
	}

	private DataItem dataTypeLabelToDataItem(String aDataTypeLabel, int aColumnOffset)
	{
		DataItem dataItem;
		Data.Type dataType = Data.Type.Text;

		if (StringUtils.isNotEmpty(aDataTypeLabel))
		{
			String columnName = aDataTypeLabel;
			String columnTitle = StringUtils.EMPTY;
			String typeName = Data.Type.Text.name();

			int typeOffsetStart = aDataTypeLabel.indexOf(StrUtl.CHAR_BRACKET_OPEN);
			int typeOffsetFinish = aDataTypeLabel.indexOf(StrUtl.CHAR_BRACKET_CLOSE);
			int labelOffsetStart = aDataTypeLabel.indexOf(StrUtl.CHAR_PAREN_OPEN);
			int labelOffsetFinish = aDataTypeLabel.indexOf(StrUtl.CHAR_PAREN_CLOSE);

			if ((typeOffsetStart > 0) && (typeOffsetFinish > 0))
			{
				columnName = aDataTypeLabel.substring(0, typeOffsetStart);
				typeName = aDataTypeLabel.substring(typeOffsetStart+1, typeOffsetFinish);
				dataType = Data.stringToType(typeName);
			}
			if ((labelOffsetStart > 0) && (labelOffsetFinish > 0))
			{
				if (typeOffsetStart == -1)
					columnName = aDataTypeLabel.substring(0, labelOffsetStart);
				columnTitle = aDataTypeLabel.substring(labelOffsetStart+1, labelOffsetFinish);
			}
			dataItem = new DataItem.Builder().type(dataType).name(columnName).title(columnTitle).build();
		}
		else
		{
			String columnName = String.format("column_name_%02d", aColumnOffset);
			dataItem = new DataItem(dataType, columnName);
		}

		return dataItem;
	}

	/**
	 * Loads an optional data grid instance from an input reader stream.
	 *
	 * @param aReader Input reader stream
	 * @param aWithHeaders If <i>true</i>, then the first row will be read to identify the column headers
	 *
	 * @return Optional data grid instance
	 *
	 * @throws IOException I/O exception
	 */
	public Optional<DataGrid> load(Reader aReader, boolean aWithHeaders)
		throws IOException
	{
		try (CsvListReader csvListReader = new CsvListReader(aReader, CsvPreference.EXCEL_PREFERENCE))
		{
			int colOffset;
			DataItem dataItem;
			List<String> rowCells;
			int colCount, adjColCount;
			String cellName, cellValue;
			String[] columnHeaders = null;

			if (aWithHeaders)
				columnHeaders = csvListReader.getHeader(aWithHeaders);
			colCount = mDataGrid.colCount();
			if ((columnHeaders != null) && (colCount == 0))
			{
				for (String columnName : columnHeaders)
				{
					colCount++;
					dataItem = dataTypeLabelToDataItem(columnName, colCount);
					mDataGrid.addCol(dataItem);
				}
			}

			do
			{
				rowCells = csvListReader.read();
				if (rowCells != null)
				{
					colOffset = 0;
					adjColCount = Math.min(rowCells.size(), colCount);
					mDataGrid.newRow();
					for (DataItem di : mDataGrid.getColumns().getItems())
					{
						if (colOffset < adjColCount)
						{
							cellValue = rowCells.get(colOffset++);
							mDataGrid.setValueByName(di.getName(), cellValue);
						}
						else
							break;
					}
					mDataGrid.addRow();
				}
			}
			while (rowCells != null);
		}
		catch (Exception e)
		{
			throw new IOException(e.getMessage());
		}

		return Optional.ofNullable(mDataGrid);
	}

	/**
	 * Loads an optional data grid instance from an input reader stream.
	 *
	 * @param aPathFileName Path file name identifying a CSV file
	 * @param aWithHeaders If <i>true</i>, then the first row will be read to identify the column headers
	 *
	 * @return Optional data grid instance
	 *
	 * @throws IOException I/O exception
	 */
	public Optional<DataGrid> load(String aPathFileName, boolean aWithHeaders)
		throws IOException
	{
		Optional<DataGrid> optDataGrid;

		File csvFile = new File(aPathFileName);
		if (!csvFile.exists())
			throw new IOException(aPathFileName + ": Does not exist.");

		try (FileReader fileReader = new FileReader(csvFile))
		{
			optDataGrid = load(fileReader, aWithHeaders);
		}
		catch (Exception e)
		{
			throw new IOException(aPathFileName + ": " + e.getMessage());
		}

		return optDataGrid;
	}

	private String dataItemToColumnName(DataItem aDataItem, boolean anIsTitleOnly)
	{
		String itemName = aDataItem.getName();
		String itemTitle = aDataItem.getTitle();

		if (anIsTitleOnly)
		{
			if (StringUtils.isEmpty(itemTitle))
				itemTitle = Data.nameToTitle(itemName);

			return itemTitle;
		}
		else
		{
			StringBuilder stringBuilder = new StringBuilder(itemName);
			stringBuilder.append(String.format("[%s]", aDataItem.getType().name()));
			if (StringUtils.isNotEmpty(itemTitle))
				stringBuilder.append(String.format("(%s)", itemTitle));

			return stringBuilder.toString();
		}
	}

	/**
	 * Saves the previous assigned table (e.g. via constructor or set method)
	 * to the <i>PrintWriter</i> output stream.
	 *
	 * @param aDataGrid Data grid instance.
	 * @param aPW Print writer output stream.
	 * @param aWithHeaders If <i>true</i>, then column headers will be stored
	 *                     in the CSV file.
	 * @param anIsTitleOnly Limit the column headers to just title strings.
	 *
	 * @throws IOException I/O related exception.
	 */
	public void save(DataGrid aDataGrid, PrintWriter aPW, boolean aWithHeaders, boolean anIsTitleOnly)
		throws IOException
	{
		if (aDataGrid == null)
			throw new IOException("Data Grid is null - cannot process");

		int colCount = aDataGrid.colCount();
		int rowCount = aDataGrid.rowCount();
		if ((rowCount > 0) && (colCount > 0))
		{
			DataDoc dataDoc;
			Optional<DataDoc> optDataDoc;

			try (CsvListWriter csvListWriter = new CsvListWriter(aPW, CsvPreference.EXCEL_PREFERENCE))
			{
				if (aWithHeaders)
				{
					int colOffset = 0;
					String headerName;

					String[] headerColumns = new String[colCount];
					for (DataItem dataItem : aDataGrid.getColumns().getItems())
					{
						if (mIsFieldNamePreferred)
							headerName = dataItem.getName();
						else
							headerName = dataItemToColumnName(dataItem, anIsTitleOnly);
						headerColumns[colOffset++] = headerName;
					}
					csvListWriter.writeHeader(headerColumns);
				}
				String[] rowCells = new String[colCount];
				for (int row = 0; row < rowCount; row++)
				{
					optDataDoc = aDataGrid.getRowAsDocOptional(row);
					if (optDataDoc.isPresent())
					{
						int colOffset = 0;
						dataDoc = optDataDoc.get();

						for (DataItem dataItem : dataDoc.getItems())
							rowCells[colOffset++] = dataItem.getCollapsedValues();
						csvListWriter.write(rowCells);
					}
				}
			}
			catch (Exception e)
			{
				throw new IOException(e.getMessage());
			}
		}
	}

	/**
	 * Saves the previous assigned table (e.g. via constructor or set method)
	 * to the <i>PrintWriter</i> output stream.
	 *
	 * @param aDataGrid Data grid instance.
	 * @param aPathFileName Absolute path/file name.
	 * @param aWithHeaders If <i>true</i>, then column headers will be stored
	 *                     in the CSV file.
	 *
	 * @throws IOException I/O related exception.
	 */
	public void save(DataGrid aDataGrid, String aPathFileName, boolean aWithHeaders)
		throws IOException
	{
		try (PrintWriter printWriter = new PrintWriter(aPathFileName, StrUtl.CHARSET_UTF_8))
		{
			save(aDataGrid, printWriter, aWithHeaders, false);
		}
		catch (Exception e)
		{
			throw new IOException(aPathFileName + ": " + e.getMessage());
		}
	}

	/**
	 * Saves the data grid instance to the <i>PrintWriter</i> output stream
	 * using the column titles in the header row.
	 *
	 * @param aDataGrid Data grid instance.
	 * @param aPathFileName Absolute path/file name.
	 *
	 * @throws IOException I/O related exception.
	 */
	public void saveWithTitleHeader(DataGrid aDataGrid, String aPathFileName)
		throws IOException
	{
		try (PrintWriter printWriter = new PrintWriter(aPathFileName, StrUtl.CHARSET_UTF_8))
		{
			save(aDataGrid, printWriter, true, true);
		}
		catch (Exception e)
		{
			throw new IOException(aPathFileName + ": " + e.getMessage());
		}
	}
}
