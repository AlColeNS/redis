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

import java.util.*;
import java.util.stream.Stream;

/**
 * A DataDoc manages a collection of data items representing a document.
 */
public class DataDoc
{
	private String mName = StringUtils.EMPTY;
	private String mTitle = StringUtils.EMPTY;
	private String mAction = StringUtils.EMPTY;
	private LinkedHashMap<String, DataItem> mItems = new LinkedHashMap<>();
	private LinkedHashMap<String, ArrayList<DataDoc>> mChildDocs = new LinkedHashMap<>();

	/**
	 * Constructs a data document based on the name.
	 *
	 * @param aName Name of document
	 */
	public DataDoc(String aName)
	{
		setName(aName);
	}

	/**
	 * Constructs a data document based on the name and title.
	 *
	 * @param aName Name of document
	 * @param aTitle Title of document
	 */
	public DataDoc(String aName, String aTitle)
	{
		setName(aName);
		setTitle(aTitle);
	}

	/**
	 * Clones and existing data document instance.
	 *
	 * @param aDataDoc Data document instance
	 */
	public DataDoc(DataDoc aDataDoc)
	{
		if (aDataDoc != null)
		{
			setName(aDataDoc.getName());
			setTitle(aDataDoc.getTitle());
			aDataDoc.getItems().forEach(di -> {
				DataItem ndi = new DataItem(di);
				add(ndi);
			});
			aDataDoc.getChildDocsAsCollection().forEach(dd -> {
				DataDoc ndd = new DataDoc(dd);
				addChild(ndd);
			 });
		}
	}

	/**
	 * Returns a string representation of a data item.
	 *
	 * @return String summary representation of this data item.
	 */
	@Override
	public String toString()
	{
		String ddString = String.format("%s [%d items]", mName, mItems.size());
		if (mChildDocs.size() > 0)
			ddString += String.format("[%d children]", mChildDocs.size());
		if (StringUtils.isNotEmpty(mTitle))
			ddString += String.format(", t = %s", mTitle);

		return ddString;
	}

	/**
	 * Assigns a name to the data document instance.
	 *
	 * @param aName Name of document
	 */
	public void setName(String aName)
	{
		if (StringUtils.isNotEmpty(aName))
			mName = aName;
	}

	/**
	 * Returns the name of the data document.
	 *
	 * @return Name of document
	 */
	public String getName()
	{
		return mName;
	}

	/**
	 * Assigns a title to the data document instance.
	 *
	 * @param aTitle Title of the document
	 */
	public void setTitle(String aTitle)
	{
		if (StringUtils.isNotEmpty(aTitle))
			mTitle = aTitle;
	}

	/**
	 * Returns a title of the data document.
	 *
	 * @return Title of the document
	 */
	public String getTitle()
	{
		return mTitle;
	}

	/**
	 * Returns the action previously assigned to the data document instance.
	 *
	 * @return Action string
	 */
	public String getAction()
	{
		return mAction;
	}

	/**
	 * Assigns an action to the document.
	 *
	 * @param anAction Action string (e.g. "add", "update", "delete")
	 */
	public void setAction(String anAction)
	{
		if (StringUtils.isNotEmpty(anAction))
			mAction = anAction;
	}

	/**
	 * Returns a data item matching the name parameter.
	 *
	 * @param aName Name of the data item
	 *
	 * @return Data item instance
	 *
	 * @throws NoSuchElementException If the name cannot be matched in the document
	 */
	public DataItem getItemByName(String aName)
		throws NoSuchElementException
	{
		DataItem dataItem = null;

		if (StringUtils.isNotEmpty(aName))
			dataItem = mItems.get(aName);

		if (dataItem == null)
			throw new NoSuchElementException(aName);
		else
			return dataItem;
	}

	/**
	 * Returns an optional data item matching the name parameter.
	 *
	 * @param aName Name of the data item
	 *
	 * @return Optional data item instance
	 */
	public Optional<DataItem> getItemByNameOptional(String aName)
	{
		DataItem dataItem = null;

		if (StringUtils.isNotEmpty(aName))
			dataItem = mItems.get(aName);

		return Optional.ofNullable(dataItem);
	}

	/**
	 * Assigns a value to the data item identified by the name.
	 *
	 * @param aName Name of the data item
	 * @param aValue Value to assign to the data item
	 */
	public void setValueByName(String aName, String aValue)
	{
		Optional<DataItem> optDataItem = getItemByNameOptional(aName);
		optDataItem.ifPresent(di -> di.setValue(aValue));
	}

	/**
	 * Adds a value to the data item identified by the name.
	 *
	 * @param aName Name of the data item
	 * @param aValue Value to assign to the data item
	 */
	public void addValueByName(String aName, String aValue)
	{
		Optional<DataItem> optDataItem = getItemByNameOptional(aName);
		optDataItem.ifPresent(di -> di.addValue(aValue));
	}

	/**
	 * Returns a data item value identified by a name.
	 *
	 * @param aName Name of the data item
	 *
	 * @return Data item value
	 */
	public String getValueByName(String aName)
	{
		Optional<DataItem> optDataItem = getItemByNameOptional(aName);
		if (optDataItem.isPresent())
			return optDataItem.get().getValue();
		else
			return StringUtils.EMPTY;
	}

	/**
	 * Assigns a value to the data item identified by the name.
	 *
	 * @param aName Name of the data item
	 * @param aValue Value to assign to the data item
	 */
	public void setValueByName(String aName, Boolean aValue)
	{
		Optional<DataItem> optDataItem = getItemByNameOptional(aName);
		optDataItem.ifPresent(di -> di.setValue(aValue));
	}

	/**
	 * Adds a value to the data item identified by the name.
	 *
	 * @param aName Name of the data item
	 * @param aValue Value to assign to the data item
	 */
	public void addValueByName(String aName, Boolean aValue)
	{
		Optional<DataItem> optDataItem = getItemByNameOptional(aName);
		optDataItem.ifPresent(di -> di.addValue(aValue));
	}

	/**
	 * Returns a data item value identified by a name.
	 *
	 * @param aName Name of the data item
	 *
	 * @return Data item value
	 */
	public Boolean getValueAsBoolean(String aName)
	{
		DataItem dataItem = getItemByName(aName);
		return dataItem.getValueAsBoolean();
	}

	/**
	 * Assigns a value to the data item identified by the name.
	 *
	 * @param aName Name of the data item
	 * @param aValue Value to assign to the data item
	 */
	public void setValueByName(String aName, Integer aValue)
	{
		Optional<DataItem> optDataItem = getItemByNameOptional(aName);
		optDataItem.ifPresent(di -> di.setValue(aValue));
	}

	/**
	 * Adds a value to the data item identified by the name.
	 *
	 * @param aName Name of the data item
	 * @param aValue Value to assign to the data item
	 */
	public void addValueByName(String aName, Integer aValue)
	{
		Optional<DataItem> optDataItem = getItemByNameOptional(aName);
		optDataItem.ifPresent(di -> di.addValue(aValue));
	}

	/**
	 * Returns a data item value identified by a name.
	 *
	 * @param aName Name of the data item
	 *
	 * @return Data item value
	 */
	public Integer getValueAsInteger(String aName)
	{
		DataItem dataItem = getItemByName(aName);
		return dataItem.getValueAsInteger();
	}

	/**
	 * Assigns a value to the data item identified by the name.
	 *
	 * @param aName Name of the data item
	 * @param aValue Value to assign to the data item
	 */
	public void setValueByName(String aName, Long aValue)
	{
		Optional<DataItem> optDataItem = getItemByNameOptional(aName);
		optDataItem.ifPresent(di -> di.setValue(aValue));
	}

	/**
	 * Adds a value to the data item identified by the name.
	 *
	 * @param aName Name of the data item
	 * @param aValue Value to assign to the data item
	 */
	public void addValueByName(String aName, Long aValue)
	{
		Optional<DataItem> optDataItem = getItemByNameOptional(aName);
		optDataItem.ifPresent(di -> di.addValue(aValue));
	}

	/**
	 * Returns a data item value identified by a name.
	 *
	 * @param aName Name of the data item
	 *
	 * @return Data item value
	 */
	public Long getValueAsLong(String aName)
	{
		DataItem dataItem = getItemByName(aName);
		return dataItem.getValueAsLong();
	}

	/**
	 * Assigns a value to the data item identified by the name.
	 *
	 * @param aName Name of the data item
	 * @param aValue Value to assign to the data item
	 */
	public void setValueByName(String aName, Float aValue)
	{
		Optional<DataItem> optDataItem = getItemByNameOptional(aName);
		optDataItem.ifPresent(di -> di.setValue(aValue));
	}

	/**
	 * Adds a value to the data item identified by the name.
	 *
	 * @param aName Name of the data item
	 * @param aValue Value to assign to the data item
	 */
	public void addValueByName(String aName, Float aValue)
	{
		Optional<DataItem> optDataItem = getItemByNameOptional(aName);
		optDataItem.ifPresent(di -> di.addValue(aValue));
	}

	/**
	 * Returns a data item value identified by a name.
	 *
	 * @param aName Name of the data item
	 *
	 * @return Data item value
	 */
	public Float getValueAsFloat(String aName)
	{
		DataItem dataItem = getItemByName(aName);
		return dataItem.getValueAsFloat();
	}

	/**
	 * Assigns a value to the data item identified by the name.
	 *
	 * @param aName Name of the data item
	 * @param aValue Value to assign to the data item
	 */
	public void setValueByName(String aName, Double aValue)
	{
		Optional<DataItem> optDataItem = getItemByNameOptional(aName);
		optDataItem.ifPresent(di -> di.setValue(aValue));
	}

	/**
	 * Adds a value to the data item identified by the name.
	 *
	 * @param aName Name of the data item
	 * @param aValue Value to assign to the data item
	 */
	public void addValueByName(String aName, Double aValue)
	{
		Optional<DataItem> optDataItem = getItemByNameOptional(aName);
		optDataItem.ifPresent(di -> di.addValue(aValue));
	}

	/**
	 * Returns a data item value identified by a name.
	 *
	 * @param aName Name of the data item
	 *
	 * @return Data item value
	 */
	public Double getValueAsDouble(String aName)
	{
		DataItem dataItem = getItemByName(aName);
		return dataItem.getValueAsDouble();
	}

	/**
	 * Add an item to the data document.
	 *
	 * @param anItem Data item instance
	 */
	public void add(DataItem anItem)
	{
		mItems.put(anItem.getName(), anItem);
	}

	/**
	 * Update an item in the data document.
	 *
	 * @param anItem Data item instance
	 */
	public void update(DataItem anItem)
	{
		mItems.put(anItem.getName(), anItem);
	}

	/**
	 * Remove the data item identified by the name.
	 *
	 * @param aName Name of data item
	 */
	public void remove(String aName)
	{
		mItems.remove(aName);
	}

	/**
	 * Count of items being managed by the data document instance.
	 *
	 * @return Count of data items
	 */
	public int count()
	{
		return mItems.size();
	}

	/**
	 * Returns the list of data items being managed by the data document instance.
	 *
	 * @return Collection of data items.
	 */
	public Collection<DataItem> getItems()
	{
		return mItems.values();
	}

	/**
	 * Adds the data document instance as a child document identified by the name.
	 *
	 * @param aName Name of the child document
	 * @param aDoc Data document instance
	 */
	public void addChild(String aName, DataDoc aDoc)
	{
		if ((StringUtils.isNotEmpty(aName)) && (aDoc != null))
		{
			ArrayList<DataDoc> childDocs = mChildDocs.get(aDoc.getName());
			if (childDocs == null)
				childDocs = new ArrayList<>();
			childDocs.add(aDoc);
			mChildDocs.put(aDoc.getName(), childDocs);
		}
	}

	/**
	 * Adds the data document instance as a child document.
	 *
	 * @param aDoc Data document instance
	 */
	public void addChild(DataDoc aDoc)
	{
		if (aDoc != null)
			addChild(aDoc.getName(), aDoc);
	}

	/**
	 * Delete the child document identified by the name parameter.
	 *
	 * @param aName Name of child document
	 */
	public void deleteChild(String aName)
	{
		if (StringUtils.isNotEmpty(aName))
			mChildDocs.remove(aName);
	}

	/**
	 * Returns the count of child documents.
	 *
	 * @return Count of child documents
	 */
	public int childrenCount()
	{
		return mChildDocs.size();
	}

	/**
	 * Returns the list of child documents as a collection.
	 *
	 * @return Collection of data document instances
	 */
	public Collection<DataDoc> getChildDocsAsCollection()
	{
		Collection<DataDoc> dataDocList = new ArrayList<>();
		mChildDocs.entrySet().stream()
				  .forEach(e -> {
				  	for (DataDoc dataDoc : e.getValue())
				  		dataDocList.add(dataDoc);
				  });

		return dataDocList;
	}

	/**
	 * Returns a linked hash map of child documents.
	 *
	 * @return a linked hash map of child documents
	 */
	public LinkedHashMap<String, ArrayList<DataDoc>> getChildDocs()
	{
		return mChildDocs;
	}

	/**
	 * Retuns the data items as a stream.
	 *
	 * @return Data item stream
	 */
	public Stream<DataItem> stream()
	{
		List<DataItem> dataItemList = new ArrayList<DataItem>();
		mItems.forEach((s, dataItem) -> dataItemList.add(dataItem));

		return dataItemList.stream();
	}
}
