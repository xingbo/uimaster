/*
* Copyright 2015 The UIMaster Project
*
* The UIMaster Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/
package org.shaolin.uimaster.page.od;

import java.util.Map;

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.exception.UIConvertException;

public interface IODMappingConverter {

	/**
	 * data to ui
	 */
	public static final String UI_WIDGET_TYPE = "UIWidgetType";
	
	/**
	 *  ui to data (internal attribute)
	 */
	public static final String UI_WIDGET_ID = "UIWidgetID";
	
	/**
	 * Get the data defined variables info.
	 * 
	 * @return
	 */
	public Map<String, Class<?>> getDataEntityClassInfo();

	/**
	 * Get the ui defined entity info
	 * 
	 * @return
	 */
	public Map<String, Class<?>> getUIEntityClassInfo();

	/**
	 * set the input data before pushDataToWidget or pullDataFromWidget method
	 * 
	 * @param paramMap
	 * @throws UIConvertException
	 */
	public void setInputData(Map<String, Object> paramMap)
			throws UIConvertException;

	/**
	 * 
	 * @return
	 * @throws UIConvertException
	 */
	public Map<String, Object> getOutputData() throws UIConvertException;

	/**
	 * Get rule name.
	 * 
	 * @return
	 */
	public String getRuleName();

	/**
	 * convert the data model to ui widget.
	 * 
	 * @throws UIConvertException
	 */
	public void pushDataToWidget(HTMLSnapshotContext htmlContext) throws UIConvertException;

	/**
	 * convert UI values to Data model
	 * 
	 * @throws UIConvertException
	 */
	public void pullDataFromWidget(HTMLSnapshotContext htmlContext) throws UIConvertException;

}