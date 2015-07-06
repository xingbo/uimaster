package org.shaolin.bmdp.persistence.query.operator;

import java.util.HashMap;
import java.util.Map;

import org.shaolin.bmdp.datamodel.rdbdiagram.CEEqualsOperatorType;
import org.shaolin.bmdp.datamodel.rdbdiagram.LogicOperatorType;
import org.shaolin.bmdp.datamodel.rdbdiagram.OperatorType;
import org.shaolin.bmdp.datamodel.rdbdiagram.StarLikeOperatorType;
import org.shaolin.bmdp.persistence.PersistenceRuntimeException;

public class OperatorGeneratorFactory {

	private static Map<Class<?>, IOperatorExpressionGenerator<?>> operatorMap 
		= new HashMap<Class<?>, IOperatorExpressionGenerator<?>>();

	static {
		operatorMap.put(StarLikeOperatorType.class, new StarLikeOperatorGenerator());
		operatorMap.put(LogicOperatorType.class, new LogicOperatorGenerator());
		operatorMap.put(CEEqualsOperatorType.class, new CEEqualsOperatorGenerator());
	}

	public static IOperatorExpressionGenerator<?> getOperatorExpressionGenerator(
			OperatorType operator) {
		if (!operatorMap.containsKey(operator.getClass())) {
			throw new PersistenceRuntimeException("operator generator for operator type {0} not found",
					new Object[] { operator.getClass().getName() });
		}
		IOperatorExpressionGenerator<?> generator = (IOperatorExpressionGenerator<?>) operatorMap
				.get(operator.getClass());

		return generator;
	}

}
