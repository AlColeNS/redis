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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import com.nridge.foundation.std.StrUtl;
import org.apache.commons.lang3.StringUtils;

/**
 * A data item captures a field's type, name, title, features, values and transient properties.  Data items
 * can be used to describe schema meta data and manage the serialization of data values.  The Builder class
 * can be used to quickly construct new data item instances.
 *
 * @author Al Cole
 * @since 1.0
 */
public class DataItem
{
    private int mStoredSize;
    private int mDisplaySize;
    private boolean mIsStored;
    private boolean mIsRequired;
    private boolean mIsVisible = true;
    private String mName = StringUtils.EMPTY;
    private String mTitle = StringUtils.EMPTY;
    private Data.Type mType = Data.Type.Undefined;
    private String mDefaultValue = StringUtils.EMPTY;
    private ArrayList<String> mValues = new ArrayList<String>();
    private HashMap<String, String> mFeatures = new HashMap<String, String>();
    private transient HashMap<String,Object> mProperties = new HashMap<>();

    /**
     * Constructs a data item based on the type and name.
     *
     * @param aType Data type.
     * @param aName Item name.
     */
    public DataItem(Data.Type aType, String aName)
    {
        setType(aType);
        setName(aName);
    }

    /**
     * Constructs a Text data item based on the item name and title.
     *
     * @param aName Item name.
     * @param aTitle Item title.
     */
    public DataItem(String aName, String aTitle)
    {
        setName(aName);
        setTitle(aTitle);
    }

    /**
     * Clones an existing data item instance.
     *
     * @param aDataItem Data item instance.
     */
    public DataItem(final DataItem aDataItem)
    {
        mProperties = new HashMap<>();
        mValues = new ArrayList<String>();

        if (aDataItem != null)
        {
            setType(aDataItem.getType());
            setName(aDataItem.getName());
            setTitle(aDataItem.getTitle());
            setIsStored(aDataItem.isIsStored());
            setStoredSize(aDataItem.getStoredSize());
            setIsVisible(aDataItem.isIsVisible());
            setDisplaySize(aDataItem.getDisplaySize());
            setDefaultValue(aDataItem.getDefaultValue());
            setValues(aDataItem.getValues());

            this.mFeatures = new HashMap<String, String>(aDataItem.getFeatures());
            mProperties.forEach((k, v)-> { addProperty(k, v); });
        }
    }

    private DataItem(Builder aBuilder)
    {
        setType(aBuilder.mType);
        setName(aBuilder.mName);
        setTitle(aBuilder.mTitle);
        setValues(aBuilder.mValues);
        setIsStored(aBuilder.mIsStored);
        setIsVisible(aBuilder.mIsVisible);
        setIsRequired(aBuilder.mIsRequired);
        setStoredSize(aBuilder.mStoredSize);
        setDisplaySize(aBuilder.mDisplaySize);
        setDefaultValue(aBuilder.mDefaultValue);
    }

    /**
     * Returns a string representation of a data item.
     *
     * @return String summary representation of this data item.
     */
    @Override
    public String toString()
    {
        String diString = String.format("[%s] n = %s", Data.typeToString(mType), mName);
        if (isValueAssigned())
            diString += String.format(", v = %s", StrUtl.collapseToSingle(mValues, StrUtl.CHAR_PIPE));
        if (StringUtils.isNotEmpty(mDefaultValue))
            diString += String.format(", dv = %s", mDefaultValue);
        if (StringUtils.isNotEmpty(mTitle))
            diString += String.format(", t = %s", mTitle);

        return diString;
    }

    /**
     * Returns the stored size for the data item.
     *
     * @return Stored size.
     */
    public int getStoredSize()
    {
        return mStoredSize;
    }

    /**
     * Assigns the stored size for the data item.
     *
     * @param aStoredSize Stored size.
     */
    public void setStoredSize(int aStoredSize)
    {
        mStoredSize = aStoredSize;
    }

    /**
     * Returns the display size for the data item.
     *
     * @return Display size.
     */
    public int getDisplaySize()
    {
        return mDisplaySize;
    }

    /**
     * Assigns the display size for the data item.
     *
     * @param aDisplaySize Display size.
     */
    public void setDisplaySize(int aDisplaySize)
    {
        mDisplaySize = aDisplaySize;
    }

    /**
     * Identifies if the data item should be stored.
     *
     * @return <i>true</i> or <i>false</i>
     */
    public boolean isIsStored()
    {
        return mIsStored;
    }

    /**
     * Assigns the stored boolean flag.
     *
     * @param aIsStored Boolean flag.
     */
    public void setIsStored(boolean aIsStored)
    {
        mIsStored = aIsStored;
    }

    /**
     * Identifies if the data item should be required (e.g. not null).
     *
     * @return <i>true</i> or <i>false</i>
     */
    public boolean isIsRequired()
    {
        return this.mIsRequired;
    }

    /**
     * Assigns the required boolean flag.
     *
     * @param aIsRequired Boolean flag.
     */
    public void setIsRequired(boolean aIsRequired)
    {
        mIsRequired = aIsRequired;
    }

    /**
     * Returns the title of the data item.
     *
     * @return  Data item title.
     */
    public String getTitle()
    {
        return mTitle;
    }

    /**
     * Assigns a data item title.
     *
     * @param aTitle Data item title.
     */
    public void setTitle(String aTitle)
    {
        if (StringUtils.isNotEmpty(aTitle))
            mTitle = aTitle;
    }

    /**
     * Returns the name of the data item.
     *
     * @return  Data item name.
     */
    public String getName()
    {
        return this.mName;
    }

    /**
     * Assigns a data item name.
     *
     * @param aName Name of the data item.
     */
    public void setName(String aName)
    {
        if (StringUtils.isNotEmpty(aName))
            mName = aName;
    }

    /**
     * Identifies if the data item should be visible to the user.
     *
     * @return <i>true</i> or <i>false</i>
     */
    public boolean isIsVisible()
    {
        return mIsVisible;
    }

    /**
     * Assigns the visible boolean flag.
     *
     * @param aIsVisible Boolean flag.
     */
    public void setIsVisible(boolean aIsVisible)
    {
        mIsVisible = aIsVisible;
    }

    /**
     * Returns the data type of the item.
     *
     * @return Data type enumerated value.
     */
    public Data.Type getType()
    {
        return mType;
    }

    /**
     * Assigns the data type for the item.
     *
     * @param aType Data type.
     */
    public void setType(Data.Type aType)
    {
        mType = aType;
    }

    /**
     * Returns <i>true</i> if the data item represents a multi-value or
     * <i>false</i> otherwise.
     *
     * @return <i>true</i> or <i>false</i>
     */
    public boolean isMultiValue()
    {
        return mValues.size() > 1;
    }

    /**
     * Returns <i>true</i> if the data uten is empty or <i>false</i> otherwise.
     *
     * @return <i>true</i> or <i>false</i>
     */
    public boolean isValueEmpty()
    {
        return StringUtils.isEmpty(getValue());
    }

    /**
     * Returns <i>true</i> if the data uten is not empty or <i>false</i> otherwise.
     *
     * @return <i>true</i> or <i>false</i>
     */
    public boolean isValueNotEmpty()
    {
        return StringUtils.isNotEmpty(getValue());
    }

    /**
     * Assigns the value parameter to the data item.
     *
     * @param aValue Value to assign.
     */
    public void setValue(String aValue)
    {
        if (StringUtils.isNotEmpty(aValue))
        {
            mValues.clear();
            mValues.add(aValue);
            if (mType == Data.Type.Undefined)
                mType = Data.Type.Text;
        }
    }

    /**
     * Returns a <i>String</i> representation of the data item value.
     *
     * @return <i>String</i> representation of the data item value.
     */
    public String getValue()
    {
        if (mValues.size() > 0)
            return mValues.get(0);

        return StringUtils.EMPTY;
    }

    /**
     * Adds the value parameter to the data item.
     *
     * @param aValue A value that is formatted appropriately for
     *               the data type it represents.
     */
    public void addValue(String aValue)
    {
        if (StringUtils.isNotEmpty(aValue))
        {
            mValues.add(aValue);
            if (mType == Data.Type.Undefined)
                mType = Data.Type.Text;
        }
    }

    /**
     * Adds the value parameter to the data item if it is multi-value
     * and ensure that it is unique.
     *
     * @param aValue A value that is formatted appropriately for
     *               the data type it represents.
     */
    public void addValueUnique(String aValue)
    {
        if (StringUtils.isNotEmpty(aValue))
        {
            if (! mValues.contains(aValue))
                mValues.add(aValue);
            if (mType == Data.Type.Undefined)
                mType = Data.Type.Text;
        }
    }

    /**
     * Assigns the value parameter to the data item.
     *
     * @param aValue Value to assign.
     */
    public void setValue(Boolean aValue)
    {
        if (mType == Data.Type.Undefined)
            mType = Data.Type.Boolean;
        setValue(StrUtl.booleanToString(aValue));
    }

    /**
     * Adds the value parameter to the data item.
     *
     * @param aValue A value that is formatted appropriately for
     *               the data type it represents.
     */
    public void addValue(Boolean aValue)
    {
        if (mType == Data.Type.Undefined)
            mType = Data.Type.Boolean;
        addValue(aValue.toString());
    }

    public Boolean getValueAsBoolean()
    {
        return StrUtl.stringToBoolean(getValue());
    }

    /**
     * Returns the list of data values as a <i>Boolean</i> type.
     *
     * @return Values of the data item.
     */
    public ArrayList<Boolean> getValuesAsBoolean()
    {
        ArrayList<Boolean> booleanList = new ArrayList<>();
        getValues().forEach(v -> booleanList.add(StrUtl.stringToBoolean(v)));

        return booleanList;
    }

    /**
     * Returns <i>true</i> if the data value evaluates as true or
     * <i>false</i> otherwise.
     *
     * @return <i>true</i> or <i>false</i>
     */
    public boolean isValueTrue()
    {
        return Data.isValueTrue(getValue());
    }

    /**
     * Returns <i>false</i> if the data value evaluates as false or
     * <i>true</i> otherwise.
     *
     * @return <i>true</i> or <i>false</i>
     */
    public boolean isValueFalse()
    {
        return !Data.isValueTrue(getValue());
    }

    /**
     * Assigns the value parameter to the data item.
     *
     * @param aValue Value to assign.
     */
    public void setValue(Integer aValue)
    {
        if (mType == Data.Type.Undefined)
            mType = Data.Type.Integer;
        setValue(aValue.toString());
    }

    /**
     * Adds the value parameter to the data item.
     *
     * @param aValue A value that is formatted appropriately for
     *               the data type it represents.
     */
    public void addValue(Integer aValue)
    {
        if (mType == Data.Type.Undefined)
            mType = Data.Type.Integer;
        addValue(aValue.toString());
    }

    /**
     * Returns the data value as an <i>int</i> type.
     *
     * @return Value of the data item.
     */
    public Integer getValueAsInteger()
    {
        return Data.createIntegerObject(getValue());
    }

    /**
     * Returns the list of data values as a <i>Integer</i> type.
     *
     * @return Values of the data item.
     */
    public ArrayList<Integer> getValuesAsInteger()
    {
        ArrayList<Integer> integerList = new ArrayList<>();
        getValues().forEach(v -> integerList.add(Data.createIntegerObject(v)));

        return integerList;
    }

    /**
     * Assigns the value parameter to the data item.
     *
     * @param aValue Value to assign.
     */
    public void setValue(Long aValue)
    {
        if (mType == Data.Type.Undefined)
            mType = Data.Type.Long;
        setValue(aValue.toString());
    }

    /**
     * Adds the value parameter to the data item.
     *
     * @param aValue A value that is formatted appropriately for
     *               the data type it represents.
     */
    public void addValue(Long aValue)
    {
        if (mType == Data.Type.Undefined)
            mType = Data.Type.Long;
        addValue(aValue.toString());
    }

    /**
     * Returns the item value as a <i>Long</i> type.
     *
     * @return Value of the item.
     */
    public Long getValueAsLong()
    {
        return Data.createLongObject(getValue());
    }

    /**
     * Returns the list of data values as a <i>Long</i> type.
     *
     * @return Values of the data item.
     */
    public ArrayList<Long> getValuesAsLong()
    {
        ArrayList<Long> longList = new ArrayList<>();
        getValues().forEach(v -> longList.add(Data.createLongObject(v)));

        return longList;
    }

    /**
     * Assigns the value parameter to the data item.
     *
     * @param aValue Value to assign.
     */
    public void setValue(Double aValue)
    {
        if (mType == Data.Type.Undefined)
            mType = Data.Type.Double;
        setValue(aValue.toString());
    }

    /**
     * Adds the value parameter to the data item.
     *
     * @param aValue A value that is formatted appropriately for
     *               the data type it represents.
     */
    public void addValue(Double aValue)
    {
        if (mType == Data.Type.Undefined)
            mType = Data.Type.Double;
        addValue(aValue.toString());
    }

    /**
     * Returns the data value as a <i>Float</i> type.
     *
     * @return Value of the data item.
     */
    public Double getValueAsDouble()
    {
        return Data.createDoubleObject(getValue());
    }

    /**
     * Returns the list of data values as a <i>Float</i> type.
     *
     * @return Values of the data item.
     */
    public ArrayList<Double> getValuesAsDouble()
    {
        ArrayList<Double> doubleList = new ArrayList<>();
        getValues().forEach(v -> doubleList.add(Data.createDoubleObject(v)));

        return doubleList;
    }

    /**
     * Assigns the value parameter to the data item.
     *
     * @param aValue Value to assign.
     */
    public void setValue(Float aValue)
    {
        if (mType == Data.Type.Undefined)
            mType = Data.Type.Float;
        setValue(aValue.toString());
    }

    /**
     * Adds the value parameter to the list of data item values.
     *
     * @param aValue Value to assign.
     */
    public void addValue(Float aValue)
    {
        if (mType == Data.Type.Undefined)
            mType = Data.Type.Float;
        addValue(aValue.toString());
    }

    /**
     * Returns the data value as a <i>Float</i> type.
     *
     * @return Value of the data item.
     */
    public Float getValueAsFloat()
    {
        return Data.createFloatObject(getValue());
    }

    /**
     * Returns the list of data values as a <i>Float</i> type.
     *
     * @return Values of the data item.
     */
    public ArrayList<Float> getValuesAsFloat()
    {
        ArrayList<Float> floatList = new ArrayList<>();
        getValues().forEach(v -> floatList.add(Data.createFloatObject(v)));

        return floatList;
    }

    /**
     * Assigns a list of values to the data item.
     *
     * @param aValues List of string values.
     */
    public void setValues(ArrayList<String> aValues)
    {
        if (aValues != null)
        {
            mValues = aValues;
            if (mType == Data.Type.Undefined)
                mType = Data.Type.Text;
        }
    }

    /**
     * Returns a list of data item values.
     *
     * @return List of data item values.
     */
    public ArrayList<String> getValues()
    {
        return mValues;
    }

    /**
     * Returns the list of values as a collapsed string with the separator character
     * being used as a delimiter.
     *
     * @param aSeparator Separator character
     *
     * @return String of collapsed values
     */
    public String getCollapsedValues(char aSeparator)
    {
        return StrUtl.collapseToSingle(mValues, aSeparator);
    }

    /**
     * Returns the list of values as a collapsed string with a default separator character
     * being used as a delimiter.
     *
     * @return String of collapsed values
     */
    public String getCollapsedValues()
    {
        return getCollapsedValues(StrUtl.CHAR_PIPE);
    }

    /**
     * Return a read-only version of value array.
     *
     * @return Array of values.
     */
    public String[] getValuesAsArray()
    {
        String[] valueArray;

        ArrayList<String> valueList = getValues();
        int valueCount = valueList.size();
        if (valueCount > 0)
        {
            valueArray = new String[valueCount];
            valueList.toArray(valueArray);
        }
        else
            valueArray = new String[0];

        return valueArray;
    }

    /**
     * Identifies if there has been one or more values assigned to the data item.
     *
     * @return <i>true</i> or <i>false</i>
     */
    public boolean isValueAssigned()
    {
        return mValues.size() > 0;
    }

    /**
     * Clears all values from the data item.
     */
    public void clearValues()
    {
        mValues.clear();
    }

    /**
     * Assigns the default value parameter to the data item.
     *
     * @param aValue Value to assign.
     */
    public void setDefaultValue(String aValue)
    {
        mDefaultValue = aValue;
    }

    public String getDefaultValue()
    {
        return mDefaultValue;
    }

    /**
     * Assigns the default value parameter to the data item.
     *
     * @param aValue Value to assign.
     */
    public void setDefaultValue(Boolean aValue)
    {
        setDefaultValue(StrUtl.booleanToString(aValue));
    }

    /**
     * Assigns the default value parameter to the data item.
     *
     * @param aValue Value to assign.
     */
    public void setDefaultValue(Integer aValue)
    {
        setDefaultValue(aValue.toString());
    }

    /**
     * Assigns the default value parameter to the data item.
     *
     * @param aValue Value to assign.
     */
    public void setDefaultValue(Long aValue)
    {
        setDefaultValue(aValue.toString());
    }

    /**
     * Assigns the default value parameter to the data item.
     *
     * @param aValue Value to assign.
     */
    public void setDefaultValue(Double aValue)
    {
        setDefaultValue(aValue.toString());
    }

    /**
     * Assigns the default value parameter to the data item.
     *
     * @param aValue Value to assign.
     */
    public void setDefaultValue(Float aValue)
    {
        setDefaultValue(aValue.toString());
    }

    /**
     * Add a unique feature to this item.  A feature enhances the core
     * capability of the item.  Standard features are listed below.
     *
     * <ul>
     *     <li>Data.FEATURE_IS_PRIMARY_KEY</li>
     *     <li>Data.FEATURE_IS_VISIBLE</li>
     *     <li>Data.FEATURE_IS_REQUIRED</li>
     *     <li>Data.FEATURE_IS_UNIQUE</li>
     *     <li>Data.FEATURE_IS_INDEXED</li>
     *     <li>Data.FEATURE_IS_STORED</li>
     *     <li>Data.FEATURE_IS_SECRET</li>
     *     <li>Data.FEATURE_TYPE_ID</li>
     *     <li>Data.FEATURE_INDEX_TYPE</li>
     *     <li>Data.FEATURE_STORED_SIZE</li>
     *     <li>Data.FEATURE_INDEX_POLICY</li>
     *     <li>Data.FEATURE_FUNCTION_NAME</li>
     *     <li>Data.FEATURE_SEQUENCE_SEED</li>
     *     <li>Data.FEATURE_SEQUENCE_INCREMENT</li>
     *     <li>Data.FEATURE_SEQUENCE_MANAGEMENT</li>
     * </ul>
     *
     * @param aName Name of the feature.
     * @param aValue Value to associate with the feature.
     */
    public void addFeature(String aName, String aValue)
    {
        mFeatures.put(aName, aValue);
    }

    /**
     * Add a unique feature to this item.  A feature enhances the core
     * capability of the item.
     *
     * @param aName Name of the feature.
     * @param aValue Value to associate with the feature.
     */
    public void addFeature(String aName, int aValue)
    {
        addFeature(aName, Integer.toString(aValue));
    }

    /**
     * Enabling the feature will add the name and assign it a
     * value of <i>StrUtl.STRING_TRUE</i>.
     *
     * @param aName Name of the feature.
     */
    public void enableFeature(String aName)
    {
        mFeatures.put(aName, StrUtl.STRING_TRUE);
    }

    /**
     * Disabling a feature will remove its name and value
     * from the internal list.
     *
     * @param aName Name of feature.
     */
    public void disableFeature(String aName)
    {
        mFeatures.remove(aName);
    }

    /**
     * Returns <i>true</i> if the feature was previously
     * added and assigned a value.
     *
     * @param aName Name of feature.
     *
     * @return <i>true</i> or <i>false</i>
     */
    public boolean isFeatureAssigned(String aName)
    {
        return (getFeature(aName) != null);
    }

    /**
     * Returns <i>true</i> if the feature was previously
     * added and assigned a value of <i>StrUtl.STRING_TRUE</i>.
     *
     * @param aName Name of feature.
     *
     * @return <i>true</i> or <i>false</i>
     */
    public boolean isFeatureTrue(String aName)
    {
        return StrUtl.stringToBoolean(mFeatures.get(aName));
    }

    /**
     * Returns <i>true</i> if the feature was previously
     * added and not assigned a value of <i>StrUtl.STRING_TRUE</i>.
     *
     * @param aName Name of feature.
     *
     * @return <i>true</i> or <i>false</i>
     */
    public boolean isFeatureFalse(String aName)
    {
        return !StrUtl.stringToBoolean(mFeatures.get(aName));
    }

    /**
     * Returns <i>true</i> if the feature was previously
     * added and its value matches the one provided as a
     * parameter.
     *
     * @param aName Feature name.
     * @param aValue Feature value to match.
     *
     * @return <i>true</i> or <i>false</i>
     */
    public boolean isFeatureEqual(String aName, String aValue)
    {
        String featureValue = getFeature(aName);
        return StringUtils.equalsIgnoreCase(featureValue, aValue);
    }

    /**
     * Count of unique features assigned to this item.
     *
     * @return Feature count.
     */
    public int featureCount()
    {
        return mFeatures.size();
    }

    /**
     * Returns the String associated with the feature name or
     * <i>null</i> if the name could not be found.
     *
     * @param aName Feature name.
     *
     * @return Feature value or <i>null</i>
     */
    public String getFeature(String aName)
    {
        return mFeatures.get(aName);
    }

    /**
     * Returns the int associated with the feature name.
     *
     * @param aName Feature name.
     *
     * @return Feature value or <i>null</i>
     */
    public int getFeatureAsInt(String aName)
    {
        return Data.createInt(getFeature(aName));
    }

    /**
     * Removes all features assigned to this object instance.
     */
    public void clearFeatures()
    {
        mFeatures.clear();
    }

    /**
     * Returns a read-only copy of the internal map containing
     * feature list.
     *
     * @return Internal feature map instance.
     */
    public final HashMap<String, String> getFeatures()
    {
        return mFeatures;
    }

    /**
     * Add an application defined property to the data item.
     * <p>
     * <b>Notes:</b>
     * </p>
     * <ul>
     *     <li>The goal of the Dataitem is to strike a balance between
     *     providing enough properties to adequately model application
     *     related data without overloading it.</li>
     *     <li>This method offers a mechanism to capture additional
     *     (application specific) properties that may be needed.</li>
     *     <li>Properties added with this method are transient and
     *     will not be stored when saved.</li>
     * </ul>
     *
     * @param aName Property name (duplicates are not supported)
     * @param anObject Instance of an object
     */
    public void addProperty(String aName, Object anObject)
    {
        mProperties.put(aName, anObject);
    }

    /**
     * Returns an Optional for an object associated with the property name.
     *
     * @param aName Name of the property.
     * @return Optional instance of an object.
     */
    public Optional<Object> getProperty(String aName)
    {
        return Optional.ofNullable(mProperties.get(aName));
    }

    /**
     * Updates the property by name with the object instance.
     *
     * @param aName Name of the property
     * @param anObject Instance of an object
     */
    public void updateProperty(String aName, Object anObject)
    {
        mProperties.put(aName, anObject);
    }

    /**
     * Removes a property from the data item.
     *
     * @param aName
     */
    public void deleteProperty(String aName)
    {
        mProperties.remove(aName);
    }

    /**
     * Clears all property entries.
     */
    public void clearProperties()
    {
        mProperties.clear();
    }

    /**
     * Returns <i>true</i> if the value of the data item
     * parameter matches the current value of this field.  The
     * comparison is done via {@link StringUtils}.equals() method.
     * If the comparison fails, the it returns <i>false</i>.
     *
     * @param aDataItem Data item instance
     *
     * @return <i>true</i> or <i>false</i>
     */
    public boolean isValueEqual(DataItem aDataItem)
    {
        if (aDataItem != null)
        {
            String value1 = getCollapsedValues();
            String value2 = aDataItem.getCollapsedValues();
            if (StringUtils.equals(value1, value2))
                return true;
        }

        return false;
    }

    /**
     * Returns <i>true</i> if the name and value of the data item
     * parameter matches the current value of this data item.  The
     * comparison is done via {@link StringUtils}.equals() method.
     * If the comparison fails, the it returns <i>false</i>.
     *
     * @param aDataItem Data item instance
     *
     * @return <i>true</i> or <i>false</i>
     */
    public boolean isEqual(DataItem aDataItem)
    {
        return StringUtils.equals(aDataItem.getName(), getName()) && isValueEqual(aDataItem);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param anObject Object instance to compare
     *
     * @return <i>true</i> or <i>false</i>
     */
    @Override
    public boolean equals(Object anObject)
    {
        if (this == anObject)
            return true;
        if (anObject == null || getClass() != anObject.getClass())
            return false;

        DataItem dataItem = (DataItem) anObject;

        return isEqual(dataItem);
    }

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hash tables such as those provided by
     * {@link java.util.HashMap}.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode()
    {
        int result = mDisplaySize;
        result = 31 * result + (mValues != null ? mValues.hashCode() : 0);
        result = 31 * result + mName.hashCode();
        result = 31 * result + (mFeatures != null ? mFeatures.hashCode() : 0);
        result = 31 * result + mTitle.hashCode();
        result = 31 * result + (mType != null ? mType.hashCode() : 0);

        return result;
    }

    /***
     * The Builder class provides utility methods for constructing data items.
     */
    public static class Builder
    {
        private int mStoredSize;
        private int mDisplaySize;
        private boolean mIsStored;
        private boolean mIsRequired;
        private boolean mIsVisible = true;
        private Data.Type mType = Data.Type.Text;
        private String mName = StringUtils.EMPTY;
        private String mTitle = StringUtils.EMPTY;
        private String mDefaultValue = StringUtils.EMPTY;
        private ArrayList<String> mValues = new ArrayList<>();

        /**
         * Assigns a data type to a data item.
         *
         * @param aType Data type
         *
         * @return Builder instance
         */
        public Builder type(Data.Type aType)
        {
            mType = aType;
            return this;
        }

        /**
         * Assigns a name to a data item.
         *
         * @param aName Item name
         *
         * @return Builder instance
         */
        public Builder name(String aName)
        {
            mName = aName;
            return this;
        }

        /**
         * Assigns a title to a data item.
         *
         * @param aTitle Item title
         *
         * @return Builder instance
         */
        public Builder title(String aTitle)
        {
            mTitle = aTitle;
            return this;
        }

        /**
         * Assigns a stored size to a data item.
         *
         * @param aSize Item stored size
         *
         * @return Builder instance
         */
        public Builder storedSize(int aSize)
        {
            mStoredSize = aSize;
            return this;
        }

        /**
         * Assigns a display size to a data item.
         *
         * @param aSize Item display size
         *
         * @return Builder instance
         */
        public Builder displaySize(int aSize)
        {
            mDisplaySize = aSize;
            return this;
        }

        /**
         * Identifies if the data item should be stored.
         *
         * @param anIsStored Item stored flag
         *
         * @return Builder instance
         */
        public Builder isStored(boolean anIsStored)
        {
            mIsStored = anIsStored;
            return this;
        }

        /**
         * Identifies if the data item should be visible.
         *
         * @param anIsVisible Item visible flag
         *
         * @return Builder instance
         */
        public Builder isVisible(boolean anIsVisible)
        {
            mIsVisible = anIsVisible;
            return this;
        }

        /**
         * Identifies if the data item should be required.
         *
         * @param anIsRequired Item required flag
         *
         * @return Builder instance
         */
        public Builder isRequired(boolean anIsRequired)
        {
            mIsRequired = anIsRequired;
            return this;
        }

        /**
         * Assigns a default value to a data item.
         *
         * @param aValue Item default value
         *
         * @return Builder instance
         */
        public Builder defaultValue(String aValue)
        {
            if (StringUtils.isNotEmpty(aValue))
                mDefaultValue = aValue;
            return this;
        }

        /**
         * Assigns a value to a data item.
         *
         * @param aValue Item value
         *
         * @return Builder instance
         */
        public Builder value(String aValue)
        {
            if (StringUtils.isNotEmpty(aValue))
                mValues.add(aValue);
            if (mType == Data.Type.Undefined)
                mType = Data.Type.Text;
            return this;
        }

        /**
         * Assigns a value to a data item.
         *
         * @param aValue Item value
         *
         * @return Builder instance
         */
        public Builder value(Boolean aValue)
        {
            mValues.add(aValue.toString());
            if (mType == Data.Type.Undefined)
                mType = Data.Type.Boolean;
            return this;
        }

        /**
         * Assigns a value to a data item.
         *
         * @param aValue Item value
         *
         * @return Builder instance
         */
        public Builder value(Integer aValue)
        {
            mValues.add(aValue.toString());
            if (mType == Data.Type.Undefined)
                mType = Data.Type.Integer;
            return this;
        }

        /**
         * Assigns a value to a data item.
         *
         * @param aValue Item value
         *
         * @return Builder instance
         */
        public Builder value(Long aValue)
        {
            mValues.add(aValue.toString());
            if (mType == Data.Type.Undefined)
                mType = Data.Type.Long;
            return this;
        }

        /**
         * Assigns a value to a data item.
         *
         * @param aValue Item value
         *
         * @return Builder instance
         */
        public Builder value(Float aValue)
        {
            mValues.add(aValue.toString());
            if (mType == Data.Type.Undefined)
                mType = Data.Type.Float;
            return this;
        }

        /**
         * Assigns a value to a data item.
         *
         * @param aValue Item value
         *
         * @return Builder instance
         */
        public Builder value(Double aValue)
        {
            mValues.add(aValue.toString());
            if (mType == Data.Type.Undefined)
                mType = Data.Type.Double;
            return this;
        }

        /**
         * Assigns an array of values to a data item.
         *
         * @param aValues Item values
         *
         * @return Builder instance
         */
        public Builder values(String... aValues)
        {
            for (String value : aValues)
                if (StringUtils.isNotEmpty(value))
                    mValues.add(value);
            if (mType == Data.Type.Undefined)
                mType = Data.Type.Text;
            return this;
        }

        /**
         * Builds a data item instance.
         *
         * @return Data item instance
         */
        public DataItem build()
        {
            return new DataItem(this);
        }
    }
}
