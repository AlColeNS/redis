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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DataDocTest
{
	@Before
	public void setup()
	{
	}

	@Test
	public void exercise()
	{
		DataDoc dataDoc = new DataDoc("Data Doc Test");
		dataDoc.add(new DataItem.Builder().name("name1").value("value1").build());
		dataDoc.add(new DataItem.Builder().name("name2").value("value2").build());
		dataDoc.add(new DataItem.Builder().name("name3").value("value3").build());
		for (DataItem dataItem : dataDoc.getItems())
			dataItem.toString();
	}

	@After
	public void cleanup()
	{
	}
}
