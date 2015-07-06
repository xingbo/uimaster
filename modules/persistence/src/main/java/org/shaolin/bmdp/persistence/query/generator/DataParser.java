package org.shaolin.bmdp.persistence.query.generator;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.persistence.provider.DBMSProviderFactory;
import org.shaolin.bmdp.persistence.provider.IDBMSProvider;
import org.shaolin.bmdp.persistence.provider.OracleProvider;
import org.shaolin.bmdp.runtime.ce.IConstantEntity;
import org.shaolin.javacc.sql.SQLConstants;
import org.shaolin.javacc.sql.exception.QueryParsingException;

public class DataParser {

	private static final Logger logger = Logger.getLogger(DataParser.class
			.getName());

	private DataParser() {
	}

	public static Object parseParamValue(Object value,
			QueryParsingContext parsingContext, boolean isOperatorHasFunction)
			throws QueryParsingException {
		String pfName = parsingContext.getExpressionPFName();
		if (value instanceof Collection) {
			Collection valueCol = (Collection) value;
			if (pfName != null) {
				int n = 0;
				for (Iterator it = valueCol.iterator(); it.hasNext();) {
					n += ((Integer) parseParamValue(it.next(), parsingContext,
							isOperatorHasFunction)).intValue();
				}
				return new Integer(n);
			}
			StringBuffer sb = new StringBuffer(SQLConstants.OPEN_BRACKET);
			boolean first = true;
			for (Iterator it = valueCol.iterator(); it.hasNext();) {
				if (first) {
					first = false;
				} else {
					sb.append(SQLConstants.COMMA);
				}
				sb.append(parseParamValue(it.next(), parsingContext,
						isOperatorHasFunction));
			}
			sb.append(SQLConstants.CLOSE_BRACKET);
			return new String(sb);
		}
		if (pfName != null && (value instanceof String)) {
			String paramName = "_inp"
					+ parsingContext.getInternalParamGenerator()
							.getNewIndex();
			parsingContext.addExpressionParam(paramName);
			parsingContext.bindParam(
					paramName,
					valueToBoundParam((String)value,
							parsingContext.getExpressionDatabaseType(),
							parsingContext.getQueryDebugString()));
			parsingContext.addExpressionParam(paramName);
			parsingContext.bindParam(
					paramName,
					valueToBoundParam((String) value,
							parsingContext.getExpressionDatabaseType(),
							parsingContext.getQueryDebugString()));
			return new Integer(1);
		}
		String paramName = "_inp"
				+ parsingContext.getInternalParamGenerator().getNewIndex();
		parsingContext.addExpressionParam(paramName);
		parsingContext.bindParam(
				paramName,
				valueToBoundParam(value,
						parsingContext.getExpressionDatabaseType(),
						parsingContext.getQueryDebugString()));
		int exprDBType = parsingContext.getExpressionDatabaseType();
		if (DBMSProviderFactory.getProviderId() == DBMSProviderFactory.ORACLE
				&& (exprDBType == ExtendedSQLTypes.TIMESTAMP2 || exprDBType == Types.TIMESTAMP)) {
			if (value instanceof java.util.Date) {
				if (exprDBType == ExtendedSQLTypes.TIMESTAMP2) {
					return OracleProvider.TO_TIMESTAMP
							+ SQLConstants.QUESTION
							+ OracleProvider.TO_TIMESTAMP_FORMAT;
				}
				return OracleProvider.TO_DATE + SQLConstants.QUESTION
						+ OracleProvider.TO_DATE_FORMAT;
			}
		}
		return SQLConstants.QUESTION;
	}

	public static BoundParam valueToBoundParam(Object value,
			int exprDatabaseType, String queryString)
			throws QueryParsingException {
		if (value == null) {
			if (exprDatabaseType == ExtendedSQLTypes.UNKNOWN) {
				// Only when used in querying category field contains root
				// For other cases, use "is null" instead
				return new BoundParam(new Long(0), Types.BIGINT,
						exprDatabaseType, DBMSProviderFactory.getProviderId());
			}
			// throw new
			// QueryParsingException("Can't use null as value in search query."
			// +
			// " Use Operator.IS_NULL instead.");
			// For code compatibility, just log error now
			String debug = queryString == null ? "" : "\nQuery:" + queryString;
			logger.error("Can't use null as value in search query. "
					+ "Use Operator.IS_NULL instead." + debug);
			return new BoundParam(null, ExtendedSQLTypes.UNKNOWN,
					exprDatabaseType, DBMSProviderFactory.getProviderId());
		}
		if (value instanceof String) {
			return new BoundParam((String) value, Types.VARCHAR,
					exprDatabaseType, DBMSProviderFactory.getProviderId());
		}
		return new BoundParam(value, ExtendedSQLTypes.UNKNOWN,
				exprDatabaseType, DBMSProviderFactory.getProviderId());
	}

	public static Object parseValue(Object value, String pfName,
			int databaseType, String queryString) throws QueryParsingException {
		if (value == null) {
			if (databaseType == ExtendedSQLTypes.UNKNOWN) {
				// Only when used in querying category field contains root
				// For other cases, use "is null" instead
				return "0";
			}
			// throw new
			// QueryParsingException("Can't use null as value in search query."
			// +
			// " Use Operator.IS_NULL instead.");
			//
			// For code compatibility, just log error now
			String debug = queryString == null ? "" : "\nQuery:" + queryString;
			logger.error("Can't use null as value in search query. "
					+ "Use Operator.IS_NULL instead." + debug);
			return "null";
		}

		if (value instanceof Collection) {
			Collection valueCol = (Collection) value;
			if (pfName != null) {
				List valueList = new ArrayList();
				for (Iterator it = valueCol.iterator(); it.hasNext();) {
					valueList.addAll((List) parseValue(it.next(), pfName,
							databaseType, queryString));
				}
				return valueList;
			}
			StringBuffer sb = new StringBuffer(SQLConstants.OPEN_BRACKET);
			boolean first = true;
			for (Iterator it = valueCol.iterator(); it.hasNext();) {
				if (first) {
					first = false;
				} else {
					sb.append(SQLConstants.COMMA);
				}
				sb.append(parseValue(it.next(), pfName, databaseType,
						queryString));
			}
			sb.append(SQLConstants.CLOSE_BRACKET);
			return new String(sb);
		}
		if (pfName != null && value instanceof String) {
			List<String> valueList = new ArrayList<String>(1);
			valueList.add((String) value);
			return valueList;
		}
		if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue() ? "1" : "0";
		}
		if (value instanceof java.util.Date) {
			IDBMSProvider provider = DBMSProviderFactory.getProvider();
			// tricky: for history reasons,
			// ExtendedSQLTypes.TIMESTAMP2 maps to oracle TIMESTAMP, should use
			// TO_TIMESTAMP
			if (databaseType == ExtendedSQLTypes.TIMESTAMP2
					|| DBMSProviderFactory.getProviderId() != DBMSProviderFactory.ORACLE) {
				return provider.getToTimestampFunction((java.util.Date) value);
			}
			return provider.getToDateFunction((java.util.Date) value);

		}
		if (value instanceof IConstantEntity) {
			if (databaseType == Types.VARCHAR) {
				return SQLConstants.SINGLE_QUOTE
						+ ((IConstantEntity) value).getValue()
						+ SQLConstants.SINGLE_QUOTE;
			}
			// default is getIntValue()
			return String.valueOf(((IConstantEntity) value).getIntValue());
		}
		if (value instanceof String) {
			if (databaseType == Types.INTEGER || databaseType == Types.BIGINT) {
				return (String) value;
			}
			return SQLConstants.SINGLE_QUOTE
					+ (String) value
					+ SQLConstants.SINGLE_QUOTE;
		}

		return value.toString();
	}
}
