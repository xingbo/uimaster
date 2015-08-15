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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.shaolin.uimaster.page.od.rules.UIChoiceOptionValue;
import org.shaolin.uimaster.page.od.rules.UIChoiceOptionValueAndCE;
import org.shaolin.uimaster.page.od.rules.UIMultipleChoice;
import org.shaolin.uimaster.page.od.rules.UIMultipleChoiceAndCE;
import org.shaolin.uimaster.page.od.rules.UISelect;
import org.shaolin.uimaster.page.od.rules.UISingleChoice;
import org.shaolin.uimaster.page.od.rules.UISingleChoiceAndCE;
import org.shaolin.uimaster.page.od.rules.UITable;
import org.shaolin.uimaster.page.od.rules.UIText;
import org.shaolin.uimaster.page.od.rules.UITextWithCE;
import org.shaolin.uimaster.page.od.rules.UITextWithCurrency;
import org.shaolin.uimaster.page.od.rules.UITextWithDate;
import org.shaolin.uimaster.page.od.rules.UITextWithFloatNumber;
import org.shaolin.uimaster.page.od.rules.UITextWithNumber;

public class BaseRulesHelper {
	
	public static final Map<String, Class<?>> BASE_RULES = 
    		new HashMap<String, Class<?>>();
	
	static {
    	BASE_RULES.put(UIChoiceOptionValueAndCE.class.getName(), UIChoiceOptionValueAndCE.class);
    	BASE_RULES.put(UIChoiceOptionValue.class.getName(), UIChoiceOptionValue.class);
    	BASE_RULES.put(UIMultipleChoiceAndCE.class.getName(), UIMultipleChoiceAndCE.class);
    	BASE_RULES.put(UIMultipleChoice.class.getName(), UIMultipleChoice.class);
    	BASE_RULES.put(UISelect.class.getName(), UISelect.class);
    	BASE_RULES.put(UISingleChoiceAndCE.class.getName(), UISingleChoiceAndCE.class);
    	BASE_RULES.put(UISingleChoice.class.getName(), UISingleChoice.class);
    	BASE_RULES.put(UITextWithCE.class.getName(), UITextWithCE.class);
    	BASE_RULES.put(UITextWithCurrency.class.getName(), UITextWithCurrency.class);
    	BASE_RULES.put(UITextWithDate.class.getName(), UITextWithDate.class);
    	BASE_RULES.put(UITextWithFloatNumber.class.getName(), UITextWithFloatNumber.class);
    	BASE_RULES.put(UITextWithNumber.class.getName(), UITextWithNumber.class);
    	BASE_RULES.put(UIText.class.getName(), UIText.class);
    	BASE_RULES.put(UITable.class.getName(), UITable.class);
    }
	
	public static IODMappingConverter createRule(String ruleName){
		String rule = getRuleName(ruleName);
		if (UITextWithNumber.class.getName().equals(rule)) {
			return UITextWithNumber.createRule();
		}
		if (UIText.class.getName().equals(rule)) {
			return UIText.createRule();
		}
		if (UITextWithCE.class.getName().equals(rule)) {
			return UITextWithCE.createRule();
		}
		if (UITextWithCurrency.class.getName().equals(rule)) {
			return UITextWithCurrency.createRule();
		}
		if (UITextWithDate.class.getName().equals(rule)) {
			return UITextWithDate.createRule();
		}
		if (UITextWithFloatNumber.class.getName().equals(rule)) {
			return UITextWithFloatNumber.createRule();
		}
		if (UIChoiceOptionValueAndCE.class.getName().equals(rule)) {
			return UIChoiceOptionValueAndCE.createRule();
		}
		if (UIChoiceOptionValue.class.getName().equals(rule)) {
			return UIChoiceOptionValue.createRule();
		}
		if (UIMultipleChoiceAndCE.class.getName().equals(rule)) {
			return UIMultipleChoiceAndCE.createRule();
		}
		if (UIMultipleChoice.class.getName().equals(rule)) {
			return UIMultipleChoice.createRule();
		}
		if (UISelect.class.getName().equals(rule)) {
			return UISelect.createRule();
		}
		if (UISingleChoiceAndCE.class.getName().equals(rule)) {
			return UISingleChoiceAndCE.createRule();
		}
		if (UISingleChoice.class.getName().equals(rule)) {
			return UISingleChoice.createRule();
		}
		if (UITable.class.getName().equals(rule)) {
			return UITable.createRule();
		}
		throw new IllegalArgumentException("This is not Base rule definition: " + rule);
	}
	
	public static Map<String, String> getRequiredDataParameter(String ruleName, 
			String parameter1){
		return getRequiredDataParameter(ruleName, parameter1, "");
	}
	
	public static Map<String, String> getRequiredDataParameter(String ruleName, 
			String parameter1, String parameter2){
		String rule = getRuleName(ruleName);
		
		if (UITextWithNumber.class.getName().equals(rule)) {
			return UITextWithNumber.getRequiredDataParameters(parameter1);
		}
		if (UIText.class.getName().equals(rule)) {
			return UIText.getRequiredDataParameters(parameter1);
		}
		if (UITextWithCE.class.getName().equals(rule)) {
			return UITextWithCE.getRequiredDataParameters(parameter1, parameter2);
		}
		if (UITextWithCurrency.class.getName().equals(rule)) {
			return UITextWithCurrency.getRequiredDataParameters(parameter1);
		}
		if (UITextWithDate.class.getName().equals(rule)) {
			return UITextWithDate.getRequiredDataParameters(parameter1);
		}
		if (UITextWithFloatNumber.class.getName().equals(rule)) {
			return UITextWithFloatNumber.getRequiredDataParameters(parameter1);
		}
		if (UIChoiceOptionValueAndCE.class.getName().equals(rule)) {
			return UIChoiceOptionValueAndCE.getRequiredDataParameters(parameter1);
		}
		if (UIChoiceOptionValue.class.getName().equals(rule)) {
			return UIChoiceOptionValue.getRequiredDataParameters(parameter1);
		}
		if (UIMultipleChoiceAndCE.class.getName().equals(rule)) {
			return UIMultipleChoiceAndCE.getRequiredDataParameters(parameter1, parameter2);
		}
		if (UIMultipleChoice.class.getName().equals(rule)) {
			return UIMultipleChoice.getRequiredDataParameters(parameter1, parameter2);
		}
		if (UISelect.class.getName().equals(rule)) {
			return UISelect.getRequiredDataParameters(parameter1);
		}
		if (UISingleChoiceAndCE.class.getName().equals(rule)) {
			return UISingleChoiceAndCE.getRequiredDataParameters(parameter1, parameter2);
		}
		if (UISingleChoice.class.getName().equals(rule)) {
			return UISingleChoice.getRequiredDataParameters(parameter1, parameter2);
		}
		return Collections.emptyMap();
		//throw new IllegalArgumentException("This is not Base rule definition: " + rule);
	}

	private static String getRuleName(String ruleName) {
		String rule = ruleName;
		if (!ruleName.startsWith("org.shaolin.uimaster.page.od.rules.")) {
			rule = "org.shaolin.uimaster.page.od.rules." + ruleName;
		}
		return rule;
	}
	
	public static String[] getBaseRuleNames() {
		String[] keys = new String[BASE_RULES.size()];
		BASE_RULES.keySet().toArray(keys);
		return keys;
	}
	
	public static Class<?> getBaseRuleClass(String ruleName) {
		return BASE_RULES.get(ruleName);
	}
	
	public static boolean isBaseRuleClass(String ruleName) {
		return BASE_RULES.containsKey(getRuleName(ruleName));
	}
	
	/**
	 * Get preferred rule by data type class.
	 * 
	 * TODO:
	 * 
	 * @param clazz
	 * @return
	 */
	public static String getPreferredRuleByDataType(Class<?> clazz) {
		if (String.class == clazz) {
			return UIText.class.getName();
		}
		if (int.class == clazz || Integer.class == clazz) {
			return UITextWithNumber.class.getName();
		}
		if (long.class == clazz || Long.class == clazz) {
			return UITextWithNumber.class.getName();
		}
		if (float.class == clazz || Float.class == clazz) {
			return UITextWithFloatNumber.class.getName();
		}
		return UIText.class.getName();
	}
	
}
