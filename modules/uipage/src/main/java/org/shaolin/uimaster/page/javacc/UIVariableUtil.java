package org.shaolin.uimaster.page.javacc;

import org.shaolin.bmdp.datamodel.common.VariableCategoryType;
import org.shaolin.bmdp.datamodel.common.VariableType;
import org.shaolin.bmdp.datamodel.page.PropertyValueType;
import org.shaolin.bmdp.datamodel.page.ResourceBundlePropertyType;
import org.shaolin.bmdp.datamodel.page.StringPropertyType;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.bmdp.i18n.LocaleContext;
import org.shaolin.bmdp.i18n.ResourceUtil;
import org.shaolin.bmdp.runtime.be.BEUtil;
import org.shaolin.bmdp.runtime.ce.CEUtil;
import org.shaolin.bmdp.runtime.entity.EntityNotFoundException;
import org.shaolin.uimaster.page.widgets.HTMLReferenceEntityType;

public final class UIVariableUtil {
	
	public static Object createObject(String entityName, VariableCategoryType categoryType)
			throws EntityNotFoundException {
		if (VariableCategoryType.BUSINESS_ENTITY == categoryType) {
			return BEUtil.createBEObject(entityName);
		} else if (VariableCategoryType.CONSTANT_ENTITY == categoryType) {
			return CEUtil.getConstantEntity(entityName);
		} else if (VariableCategoryType.UI_ENTITY == categoryType) {
			return new HTMLReferenceEntityType(entityName);
		} else if (VariableCategoryType.JAVA_CLASS == categoryType 
				|| VariableCategoryType.JAVA_PRIMITIVE == categoryType) {
			try {
				Class<?> objectClass = Class.forName(entityName);
				if (objectClass.isInterface()) {
					return null;
				}

				if (objectClass == boolean.class) {
					return Boolean.FALSE;
				} else if (objectClass == byte.class) {
					return new Byte((byte) 0);
				} else if (objectClass == short.class) {
					return new Short((short) 0);
				} else if (objectClass == int.class) {
					return new Integer(0);
				} else if (objectClass == long.class) {
					return new Long(0);
				} else if (objectClass == float.class) {
					return new Float(0);
				} else if (objectClass == double.class) {
					return new Double(0);
				} else if (objectClass == char.class) {
					return new Character((char) 0);
				} else {
					return objectClass.newInstance();
				}
			} catch (Exception ex) {
				throw new EntityNotFoundException(
						ExceptionConstants.EBOS_COMMON_003, ex,
						new Object[] { entityName });
			}
		} else {
			throw new EntityNotFoundException(
					ExceptionConstants.EBOS_COMMON_008, new Object[] { categoryType.value(),
							entityName });
		}
	}

	public static String getVariableClassName(VariableType variable)
			throws EntityNotFoundException {
		VariableCategoryType categoryType = variable.getCategory();
		String variableType = variable.getType().getEntityName();

		String variableClassName = null;

		if (categoryType == null) {
			categoryType = VariableCategoryType.JAVA_CLASS;
		}
		if (categoryType == VariableCategoryType.BUSINESS_ENTITY) {
//			String packageName = variableType.substring(0, variableType.length() - variableType.lastIndexOf('.') - 1);
//			String name = variableType.substring(variableType.length() - variableType.lastIndexOf('.'));
			variableClassName = variableType + "Impl";
		} else if (categoryType == VariableCategoryType.CONSTANT_ENTITY) {
			variableClassName = variableType;
		} else if (categoryType == VariableCategoryType.UI_ENTITY) {
			variableClassName = HTMLReferenceEntityType.class.getName();
		} else if (categoryType == VariableCategoryType.JAVA_CLASS
				|| VariableCategoryType.JAVA_PRIMITIVE == categoryType) {
			variableClassName = variableType;
		} 

		return variableClassName;
	}
	
	public static String getI18NProperty(PropertyValueType pvalue) {
		if (pvalue instanceof StringPropertyType) {
			return ((StringPropertyType) pvalue).getValue();
		} else if (pvalue instanceof ResourceBundlePropertyType) {
			String userLocale = LocaleContext.getUserLocale();
			String bundle = ((ResourceBundlePropertyType) pvalue).getBundle();
			String key = ((ResourceBundlePropertyType) pvalue).getKey();
			String value = ResourceUtil.getResource(bundle, key);
			return value;
		}
		return "";
	}

}
