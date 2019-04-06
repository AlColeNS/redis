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

import com.nridge.foundation.std.StrUtl;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * The Data class captures the constants, enumerated types and utility methods for the data package.
 * Specifically, it defines the following:
 * <ul>
 *     <li>Field data types</li>
 *     <li>Date/Time and currency formatting constants</li>
 *     <li>Data type conversion utility methods</li>
 * </ul>
 *
 * @since 1.0
 * @author Al Cole
 */
@SuppressWarnings({"UnusedDeclaration"})
public class Data
{
	public static final String VALUE_DATETIME_TODAY = "DateTimeToday";
	public static final String FORMAT_DATETIME_DEFAULT = "MMM-dd-yyyy HH:mm:ss";

// Date and time related constants.

	public static final String FORMAT_DATE_DEFAULT = "MMM-dd-yyyy";
	public static final String FORMAT_TIME_AMPM = "HH:mm:ss a";
	public static final String FORMAT_TIME_PLAIN = "HH:mm:ss";
	public static final String FORMAT_TIME_DEFAULT = FORMAT_TIME_PLAIN;
	public static final String FORMAT_TIMESTAMP_PACKED = "yyMMddHHmmss";
	public static final String FORMAT_SQLISODATE_DEFAULT = "yyyy-MM-dd";
	public static final String FORMAT_SQLISOTIME_DEFAULT = FORMAT_TIME_DEFAULT;
	public static final String FORMAT_SQLISODATETIME_DEFAULT = "yyyy-MM-dd HH:mm:ss";
	public static final String FORMAT_SQLORACLEDATE_DEFAULT = FORMAT_DATETIME_DEFAULT;
	public static final String FORMAT_ISO8601DATETIME_DEFAULT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String FORMAT_ISO8601DATETIME_MILLI2D = "yyyy-MM-dd'T'HH:mm:ss.SS'Z'";
	public static final String FORMAT_ISO8601DATETIME_MILLI3D = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	public static final String FORMAT_RFC1123_DATE_TIME = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";

// Number related constants.

	public static final String FORMAT_INTEGER_PLAIN = "#";
	public static final String FORMAT_DOUBLE_COMMA = "###,###";
	public static final String FORMAT_DOUBLE_POINT = "###.####";
	public static final String FORMAT_DOUBLE_COMMA_POINT = "###,###.####";
	public static final String FORMAT_DOUBLE_PERCENT = "%##";
	public static final String FORMAT_DOUBLE_CURRENCY_COMMA_POINT = "$###,###.##";

// Document action constants.

	public static final String ACTION_ADD_DOCUMENT = "Add";
	public static final String ACTION_UPD_DOCUMENT = "Update";
	public static final String ACTION_DEL_DOCUMENT = "Delete";

// Data item feature constants.

	public static final String FEATURE_IS_SECRET = "isSecret";
	public static final String FEATURE_IS_STORED = "isStored";
	public static final String FEATURE_IS_UNIQUE = "isUnique";
	public static final String FEATURE_IS_HIDDEN = "isHidden";
	public static final String FEATURE_IS_VISIBLE = "isVisible";
	public static final String FEATURE_IS_INDEXED = "isIndexed";
	public static final String FEATURE_IS_CONTENT = "isContent";
	public static final String FEATURE_IS_REQUIRED = "isRequired";
	public static final String FEATURE_MV_DELIMITER = "delimiterChar";
	public static final String FEATURE_IS_PRIMARY_KEY = "isPrimaryKey";
	public static final String FEATURE_DESCRIPTION = "fieldDescription";
	public static final String FEATURE_INDEX_FIELD_TYPE = "indexFieldType";

// Data types.

	public static enum Type
	{
		Text, Integer, Long, Float, Double, Boolean, DateTime, Undefined
	}

	private Data()
	{
	}

	/**
	 * Returns a string representation of a data type.
	 *
	 * @param aType Data type.
	 *
	 * @return String representation of a data type.
	 */
	public static String typeToString(Type aType)
	{
		return aType.name();
	}

	/**
	 * Returns the data type matching the string representation.
	 *
	 * @param aString String representation of a data type.
	 *
	 * @return Data type.
	 */
	public static Type stringToType(String aString)
	{
		return Type.valueOf(aString);
	}

	/**
	 * Returns <i>true</i> if the data type represents a numeric
	 * type.
	 *
	 * @param aType Data type.
	 *
	 * @return <i>true</i> or <i>false</i>
	 */
	public static boolean isNumber(Data.Type aType)
	{
		switch (aType)
		{
			case Integer:
			case Long:
			case Float:
			case Double:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Returns <i>true</i> if the data type represents a boolean type.
	 *
	 * @param aType Data type.
	 *
	 * @return <i>true</i> or <i>false</i>
	 */
	public static boolean isBoolean(Data.Type aType)
	{
		switch (aType)
		{
			case Boolean:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Returns <i>true</i> if the data type represents a text type.
	 *
	 * @param aType Data type.
	 *
	 * @return <i>true</i> or <i>false</i>
	 */
	public static boolean isText(Data.Type aType)
	{
		switch (aType)
		{
			case Text:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Returns <i>true</i> if the data type represents a date or time type.
	 *
	 * @param aType Field type.
	 *
	 * @return <i>true</i> or <i>false</i>
	 */
	public static boolean isDateOrTime(Data.Type aType)
	{
		switch (aType)
		{
			case DateTime:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Returns a title that has been derived from the name of the
	 * data item.  This method will handle the conversion as follows:
	 *
	 * <ul>
	 *     <li>id becomes Id</li>
	 *     <li>employee.name becomes Employee Name</li>
	 *     <li>employee_name becomes Employee Name</li>
	 *     <li>federatedName becomes Federated Name</li>
	 * </ul>
	 *
	 * The logic will ignore any other conventions and simply pass
	 * the original character forward.
	 *
	 * @param aName Name of the data item to convert.
	 *
	 * @return Title for the data item name.
	 */
	public static String nameToTitle(String aName)
	{
		if (StringUtils.isNotEmpty(aName))
		{
			char curChar;
			boolean isLastSpace = true;
			boolean isLastLower = false;

			StringBuilder stringBuilder = new StringBuilder();
			int strLength = aName.length();
			for (int i = 0; i < strLength; i++)
			{
				curChar = aName.charAt(i);

				if ((curChar == StrUtl.CHAR_UNDERLINE) || (curChar == StrUtl.CHAR_DOT))
				{
					curChar = StrUtl.CHAR_SPACE;
					stringBuilder.append(curChar);
				}
				else if (isLastSpace)
					stringBuilder.append(Character.toUpperCase(curChar));
				else if ((Character.isUpperCase(curChar)) && (isLastLower))
				{
					stringBuilder.append(StrUtl.CHAR_SPACE);
					stringBuilder.append(curChar);
				}
				else
					stringBuilder.append(curChar);

				isLastSpace = (curChar == StrUtl.CHAR_SPACE);
				isLastLower = Character.isLowerCase(curChar);
			}

			return stringBuilder.toString();
		}
		else
			return aName;
	}

	/**
	 * Returns a data item name that has been derived from the title.  This
	 * method will handle the conversion as follows:
	 *
	 * <ul>
	 *     <li>Id becomes id</li>
	 *     <li>Employee Name becomes employee_name</li>
	 *     <li>Federated Name becomes federated_name</li>
	 * </ul>
	 *
	 * The logic will ignore any other conventions and simply pass
	 * the original character forward.
	 *
	 * @param aTitle Title string to convert.
	 *
	 * @return Data item name.
	 */
	public static String titleToName(String aTitle)
	{
		if (StringUtils.isNotEmpty(aTitle))
		{
			String itemName = StringUtils.replaceChars(aTitle.toLowerCase(), StrUtl.CHAR_SPACE, StrUtl.CHAR_UNDERLINE);
			itemName = StringUtils.replaceChars(itemName, StrUtl.CHAR_HYPHEN ,StrUtl.CHAR_UNDERLINE);
			itemName = StringUtils.replaceChars(itemName, StrUtl.CHAR_PAREN_OPEN ,StrUtl.CHAR_UNDERLINE);
			itemName = StringUtils.replaceChars(itemName, StrUtl.CHAR_PAREN_CLOSE ,StrUtl.CHAR_UNDERLINE);
			itemName = StringUtils.replaceChars(itemName, StrUtl.CHAR_BRACKET_OPEN ,StrUtl.CHAR_UNDERLINE);
			itemName = StringUtils.replaceChars(itemName, StrUtl.CHAR_BRACKET_CLOSE ,StrUtl.CHAR_UNDERLINE);
			itemName = StrUtl.removeDuplicateChar(itemName, StrUtl.CHAR_UNDERLINE);
			if (StrUtl.endsWithChar(itemName, StrUtl.CHAR_UNDERLINE))
				itemName = StrUtl.trimLastChar(itemName);

			return itemName;
		}
		else
			return aTitle;
	}

	/**
	 * Returns a Java data type class representing the data type.
	 *
	 * @param aType Data type.
	 *
	 * @return Java data type class.
	 */
	public static Class<?> getTypeClass(Data.Type aType)
	{
		switch (aType)
		{
			case Integer:
				return Integer.class;
			case Long:
				return Long.class;
			case Float:
				return Float.class;
			case Double:
				return Double.class;
			case Boolean:
				return Boolean.class;
			case DateTime:
				return Calendar.class;
			default:
				return String.class;
		}
	}

	/**
	 * Return a data type representing the object type.
	 *
	 * @param anObject Object instance.
	 *
	 * @return Data type.
	 */
	public static Data.Type getTypeByObject(Object anObject)
	{
		if (anObject != null)
		{
			if (anObject instanceof Integer)
				return Type.Integer;
			else if (anObject instanceof Long)
				return Type.Long;
			else if (anObject instanceof Float)
				return Type.Float;
			else if (anObject instanceof Double)
				return Type.Double;
			else if (anObject instanceof Boolean)
				return Type.Boolean;
			else if (anObject instanceof Date)
				return Type.DateTime;
			else if (anObject instanceof Calendar)
				return Type.DateTime;
		}

		return Type.Text;
	}

	/**
	 * Returns a hidden string value (e.g. a simple Caesar-cypher
	 * encryption) based on the value parameter.  This method is
	 * only intended to obscure a field value.  The developer
	 * should use other encryption methods to achieve the goal
	 * of strong encryption.
	 *
	 * @param aValue String to hide.
	 *
	 * @return Hidden string value.
	 */
	public static String hideValue(String aValue)
	{
		if (StrUtl.isHidden(aValue))
			return aValue;
		else
			return StrUtl.hidePassword(aValue);
	}

	/**
	 * Returns a previously hidden (e.g. Caesar-cypher encrypted)
	 * string to its original form.
	 *
	 * @param aValue Hidden string value.
	 *
	 * @return Decrypted string value.
	 */
	public static String recoverValue(String aValue)
	{
		if (StrUtl.isHidden(aValue))
			return StrUtl.recoverPassword(aValue);
		else
			return aValue;
	}

	/**
	 * Returns an <i>int</i> representation of the data item
	 * value string.
	 *
	 * @param aValue Numeric string value.
	 *
	 * @return Converted value.
	 */
	public static int createInt(String aValue)
	{
		if (NumberUtils.isDigits(aValue))
			return Integer.parseInt(aValue);
		else
			return Integer.MIN_VALUE;
	}

	/**
	 * Returns an <i>Integer</i> representation of the data item
	 * value string.
	 *
	 * @param aValue Numeric string value.
	 *
	 * @return Converted value.
	 */
	public static Integer createIntegerObject(String aValue)
	{
		if (NumberUtils.isDigits(aValue))
			return Integer.valueOf(aValue);
		else
			return Integer.MIN_VALUE;
	}

	/**
	 * Returns a <i>long</i> representation of the data item
	 * value string.
	 *
	 * @param aValue Numeric string value.
	 *
	 * @return Converted value.
	 */
	public static long createLong(String aValue)
	{
		if (NumberUtils.isDigits(aValue))
			return Long.parseLong(aValue);
		else
			return Long.MIN_VALUE;
	}

	/**
	 * Returns a <i>Long</i> representation of the data item
	 * value string.
	 *
	 * @param aValue Numeric string value.
	 *
	 * @return Converted value.
	 */
	public static Long createLongObject(String aValue)
	{
		if (NumberUtils.isDigits(aValue))
			return Long.valueOf(aValue);
		else
			return Long.MIN_VALUE;
	}

	/**
	 * Returns a <i>float</i> representation of the data item
	 * value string.
	 *
	 * @param aValue Numeric string value.
	 *
	 * @return Converted value.
	 */
	public static float createFloat(String aValue)
	{
		if (StringUtils.isNotEmpty(aValue))
			return Float.parseFloat(aValue);
		else
			return Float.MIN_VALUE;
	}

	/**
	 * Returns a <i>Float</i> representation of the data item
	 * value string.
	 *
	 * @param aValue Numeric string value.
	 *
	 * @return Converted value.
	 */
	public static Float createFloatObject(String aValue)
	{
		if (StringUtils.isNotEmpty(aValue))
			return Float.valueOf(aValue);
		else
			return Float.MIN_VALUE;
	}

	/**
	 * Returns a <i>double</i> representation of the data item
	 * value string.
	 *
	 * @param aValue Numeric string value.
	 *
	 * @return Converted value.
	 */
	public static double createDouble(String aValue)
	{
		if (StringUtils.isNotEmpty(aValue))
			return Double.parseDouble(aValue);
		else
			return Double.MIN_VALUE;
	}

	/**
	 * Returns a <i>Double</i> representation of the data item
	 * value string.
	 *
	 * @param aValue Numeric string value.
	 *
	 * @return Converted value.
	 */
	public static Double createDoubleObject(String aValue)
	{
		if (StringUtils.isNotEmpty(aValue))
			return Double.valueOf(aValue);
		else
			return Double.MIN_VALUE;
	}

	/**
	 * Returns <i>true</i> if the data item value represents a boolean
	 * true string (e.g. yes, true) or <i>false</i> otherwise.
	 *
	 * @param aValue Boolean string value.
	 *
	 * @return <i>true</i> or <i>false</i>
	 */
	public static boolean isValueTrue(String aValue)
	{
		return StrUtl.stringToBoolean(aValue);
	}

	/**
	 * Returns a <i>Date</i> representation of the data item value
	 * string based on the format mask property.
	 *
	 * @param aValue Date/Time string value.
	 * @param aFormatMask SimpleDateFormat mask.
	 *
	 * @return Converted value.
	 */
	public static Date createDate(String aValue, String aFormatMask)
	{
		if (StringUtils.isNotEmpty(aValue))
		{
			ParsePosition parsePosition = new ParsePosition(0);
			SimpleDateFormat simpleDateFormat;
			if (StringUtils.isNotEmpty(aFormatMask))
				simpleDateFormat = new SimpleDateFormat(aFormatMask);
			else
				simpleDateFormat = new SimpleDateFormat(Data.FORMAT_DATETIME_DEFAULT);
			return simpleDateFormat.parse(aValue, parsePosition);
		}
		else
			return new Date();
	}

	/**
	 * Returns a <i>Date</i> representation of the data item value
	 * string based on the FORMAT_DATETIME_DEFAULT format mask
	 * property.
	 *
	 * @param aValue Date/Time string value.
	 *
	 * @return Converted value.
	 */
	public static Date createDate(String aValue)
	{
		if (StringUtils.isNotEmpty(aValue))
		{
			if (aValue.equals(Data.VALUE_DATETIME_TODAY))
				return new Date();
			else
			{
				ParsePosition parsePosition = new ParsePosition(0);
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Data.FORMAT_DATETIME_DEFAULT);
				return simpleDateFormat.parse(aValue, parsePosition);
			}
		}
		else
			return new Date();
	}

	/**
	 * Returns a formatted <i>String</i> representation of the date
	 * parameter based on the format mask parameter.  If the format
	 * mask is <i>null</i>, then <code>Field.FORMAT_DATETIME_DEFAULT</code>
	 * will be used.
	 *
	 * @param aDate Date/Time to convert.
	 * @param aFormatMask Format mask string.
	 *
	 * @return String representation of the date/time parameter.
	 */
	public static String dateValueFormatted(Date aDate, String aFormatMask)
	{
		if (StringUtils.isEmpty(aFormatMask))
			aFormatMask = Data.FORMAT_DATETIME_DEFAULT;

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(aFormatMask);

		return simpleDateFormat.format(aDate.getTime());
	}

	/**
	 * Returns the time representation of data item value
	 * string based on the FORMAT_DATETIME_DEFAULT format mask
	 * property.
	 *
	 * @param aValue Date/Time string value.
	 *
	 * @return The number of milliseconds since January 1, 1970, 00:00:00 GMT
	 * represented by this value.
	 */
	public static long createDateLong(String aValue)
	{
		Date dateValue = createDate(aValue);
		return dateValue.getTime();
	}
}
