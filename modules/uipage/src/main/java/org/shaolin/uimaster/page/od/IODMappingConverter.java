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