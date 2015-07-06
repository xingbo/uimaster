package org.shaolin.bmdp.persistence.query.generator;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.Map;

import org.shaolin.bmdp.runtime.VariableUtil;
import org.shaolin.bmdp.runtime.be.IHistoryEntity;
import org.shaolin.javacc.StatementParser;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.statement.CompilationUnit;

public class SearchQueryParsingContext extends DefaultParsingContext {
	
	private static final String SET_VARIABLE_PREFIX = "target";

	private String setVariableName;

	private boolean useParamBinding = true;
	
	public SearchQueryParsingContext() {
		super();
	}

	public SearchQueryParsingContext(Map variableTypes) {
		super(variableTypes);
	}

	public Class getFieldClass(Class ownerClass, String fieldName)
			throws ParsingException {
		if (ownerClass != null) {
			PropertyDescriptor descriptor = getClassPropertyDescriptors(
					ownerClass, fieldName);

			if (descriptor != null) {
				return descriptor.getPropertyType();
			} else
			// support _version field for history business entity
			if (IHistoryEntity.class.isAssignableFrom(ownerClass)
					&& IHistoryEntity.VERSION.equals(fieldName)) {
				return int.class;
			}
		}

		throw new ParsingException("Fail to find custom field:" + fieldName
				+ " for class:" + ownerClass);
	}

	public static PropertyDescriptor getClassPropertyDescriptors(
			Class ownerClass, String fieldName) {
		PropertyDescriptor descriptor = null;

		// Use Java Bean Introspector to resolve the Interface's Get/Set Method
		// as Field
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(ownerClass);
			PropertyDescriptor[] propertyDescriptors = beanInfo
					.getPropertyDescriptors();
			for (int i = 0, n = propertyDescriptors.length; i < n; i++) {
				if (propertyDescriptors[i].getName().equals(fieldName)
						|| VariableUtil.getVariableBeanName(
								propertyDescriptors[i].getName()).equals(
								fieldName)
						|| VariableUtil.getVariableBeanName(fieldName).equals(
								propertyDescriptors[i].getName())) {
					descriptor = propertyDescriptors[i];
					break;
				}
			}
		} catch (Exception ex) {
		}

		if (descriptor == null) {
			Class[] interfaceClasses = ownerClass.getInterfaces();
			for (int i = 0, n = interfaceClasses.length; i < n; i++) {
				descriptor = getClassPropertyDescriptors(interfaceClasses[i],
						fieldName);
				if (descriptor != null) {
					break;
				}
			}
		}

		return descriptor;
	}

	public void initSetVariable() {
		setVariableName = SET_VARIABLE_PREFIX;
		for (int suffix = 0;; suffix++) {
			boolean found = false;
			for (Iterator it = super.getAllVariableNames().iterator(); it
					.hasNext();) {
				String varName = (String) it.next();
				if (setVariableName.equals(varName)) {
					found = true;
					break;
				}
			}

			if (!found) {
				break;
			} else {
				setVariableName = SET_VARIABLE_PREFIX + suffix;
			}
		}
	}

	public String getSetVariableName() {
		return setVariableName;
	}

	public CompilationUnit buildSetFieldExpression(
			CompilationUnit fieldExpression) throws ParsingException {
		String expressionString = fieldExpression.getExpressionString();

		String setExpressionString = expressionString + " = " + setVariableName;

		super.setVariableClass(setVariableName, fieldExpression.getValueClass());

		CompilationUnit setUnit = StatementParser.parse(setExpressionString,
				this);

		return setUnit;
	}

	public void setUseParamBinding(boolean useParamBinding) {
		this.useParamBinding = useParamBinding;
	}

	public boolean getUseParamBinding() {
		return useParamBinding;
	}

}
