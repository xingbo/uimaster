package org.shaolin.bmdp.persistence.query.generator;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.runtime.be.IHistoryEntity;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.exception.EvaluationException;

public class SearchQueryEvaluationContext extends DefaultEvaluationContext {

	private static final Logger logger = Logger
			.getLogger(SearchQueryEvaluationContext.class);

	private String setVariableName;

	public SearchQueryEvaluationContext() {
		super();
	}

	public SearchQueryEvaluationContext(Map varValues) {
		super(varValues);
	}

	public void setTargetFieldValue(Object fieldValue)
			throws EvaluationException {
		super.setVariableValue(setVariableName, fieldValue);
	}

	public void setSetVariableName(String setVariableName) {
		this.setVariableName = setVariableName;
	}

	public void setFieldValue(Class ownerClass, Object ownerValue,
			String fieldName, Object fieldValue) throws EvaluationException {
		if (ownerClass != null) {
			PropertyDescriptor descriptor = SearchQueryParsingContext
					.getClassPropertyDescriptors(ownerClass, fieldName);

			if (descriptor != null) {
				Method writeMethod = descriptor.getWriteMethod();
				try {
					writeMethod.invoke(ownerValue, new Object[] { fieldValue });
					return;
				} catch (Throwable t) {
					throw new EvaluationException("Error while setting custom field {0} of class {1}. Object:{2}, fieldValue:{3}", t,
							new Object[] { fieldName, ownerClass, ownerValue,
									fieldValue });
				}
			} else if (IHistoryEntity.class.isAssignableFrom(ownerClass)
					&& IHistoryEntity.VERSION.equals(fieldName)) {
				try {
					IHistoryEntity historyEntity = (IHistoryEntity) ownerValue;
					int version = ((Number) fieldValue).intValue();
					historyEntity.setVersion(version);
					return;
				} catch (Throwable t) {
					throw new EvaluationException("Error while setting custom field {0} of class {1}. Object:{2}, fieldValue:{3}", t,
							new Object[] { fieldName, ownerClass, ownerValue,
									fieldValue });
				}
			}
		}

		throw new EvaluationException("Fail to find custom field:" + fieldName
				+ " for class:" + ownerClass);
	}

	public Object getFieldValue(Class ownerClass, Object ownerValue,
			String fieldName) throws EvaluationException {
		if (ownerClass != null) {
			PropertyDescriptor descriptor = SearchQueryParsingContext
					.getClassPropertyDescriptors(ownerClass, fieldName);

			if (descriptor != null) {
				Method readMethod = descriptor.getReadMethod();
				try {
					return readMethod.invoke(ownerValue, new Object[] {});
				} catch (Throwable t) {
					throw new EvaluationException("Error while getting custom field {0} of class {1}. Object:{2}", t,
							new Object[] { fieldName, ownerClass, ownerValue });
				}
			} else if (IHistoryEntity.class.isAssignableFrom(ownerClass)
					&& IHistoryEntity.VERSION.equals(fieldName)) {
				try {
					IHistoryEntity historyEntity = (IHistoryEntity) ownerValue;
					return new Integer(historyEntity.getVersion());
				} catch (Throwable t) {
					throw new EvaluationException("Error while getting custom field {0} of class {1}. Object:{2}", t,
							new Object[] { fieldName, ownerClass, ownerValue });
				}
			}
		}

		throw new EvaluationException("fail to find custom field {0} for class {1}",
				new Object[] { fieldName, ownerClass });
	}

}
