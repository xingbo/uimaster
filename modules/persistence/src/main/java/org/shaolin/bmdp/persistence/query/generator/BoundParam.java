package org.shaolin.bmdp.persistence.query.generator;

import java.io.Serializable;
import java.sql.Types;
import java.util.Date;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.exceptions.I18NRuntimeException;
import org.shaolin.bmdp.persistence.provider.DBMSProviderFactory;
import org.shaolin.bmdp.runtime.ce.IConstantEntity;
import org.shaolin.bmdp.utils.DateParser;

public class BoundParam implements Serializable {

	private static final Logger logger = Logger.getLogger(BoundParam.class);

	public Object paramValue = null;
	public int paramType = 0;

	public BoundParam(Object pValue, int pType, int exprType, String vendorId) {
		paramValue = pValue;

		// In generally, pType should have the higher priority
		// BUT, to admit database type change without changing
		// application code, we use the opposite logic
		paramType = (exprType == ExtendedSQLTypes.UNKNOWN) ? pType : exprType;

		if (paramValue == null) {
			if (paramType == ExtendedSQLTypes.UNKNOWN) {
				// use string as default type
				paramType = Types.VARCHAR;
			}
			return;
		}

		if (pValue instanceof IConstantEntity) {
			IConstantEntity ce = (IConstantEntity) pValue;
			if (paramType == Types.VARCHAR) {
				paramValue = ce.getValue();
			} else // default is Types.INTEGER
			{
				paramValue = new Integer(ce.getIntValue());
				paramType = Types.INTEGER;
			}
			return;
		}
		if (vendorId == DBMSProviderFactory.ORACLE
				&& (exprType == ExtendedSQLTypes.TIMESTAMP2 || exprType == Types.TIMESTAMP)) {
			if (pValue instanceof Date) {
				DateParser parser = new DateParser((Date) pValue);
				if (paramType == ExtendedSQLTypes.TIMESTAMP2) {
					paramValue = parser.getTimestampString();
				} else {
					paramValue = parser.getTimeString();
				}
				paramType = Types.VARCHAR;
				return;
			}
		}

		if (pValue instanceof java.sql.Date) {
			// tricky: for history reasons,
			// Types.TIMESTAMP maps to oracle DATE, should use setDate()
			// ExtendedSQLTypes.TIMESTAMP2 maps to oracle TIMESTAMP, should use
			// setTimestamp()
			if (paramType == Types.TIMESTAMP && vendorId == DBMSProviderFactory.ORACLE) {
				paramType = Types.DATE;
			} else {
				paramValue = new Date(((java.sql.Date) pValue).getTime());
				paramType = Types.TIMESTAMP;
			}
			return;
		}
		if (pValue instanceof Date) {
			if (paramType == Types.TIMESTAMP && vendorId == DBMSProviderFactory.ORACLE) {
				paramValue = new java.sql.Date(((Date) pValue).getTime());
				paramType = Types.DATE;
			} else {
				paramType = Types.TIMESTAMP;
			}
			return;
		}

		if (paramType == ExtendedSQLTypes.UNKNOWN) {
			if (pValue instanceof Integer) {
				paramType = Types.INTEGER;
				return;
			}
			if (pValue instanceof Long) {
				paramType = Types.BIGINT;
				return;
			}
			if (pValue instanceof Double) {
				paramType = Types.DOUBLE;
				return;
			}
			if (pValue instanceof Boolean) {
				paramValue = ((Boolean) pValue).booleanValue() ? new Integer(1)
						: new Integer(0);
				paramType = Types.INTEGER;
				return;
			}
			if (pValue instanceof Byte) {
				paramType = Types.TINYINT;
				return;
			}
			if (pValue instanceof Float) {
				paramType = Types.FLOAT;
				return;
			}
			if (pValue instanceof Short) {
				paramType = Types.SMALLINT;
				return;
			}
			throw new I18NRuntimeException("Unsupported bind type :{0}", new Object[] { pValue
							.getClass().getName() });
			// throw new IllegalArgumentException("unsupported bind type:" +
			// pValue.getClass().getName());
		}

		convertIfNecessary();
	}

	private void convertIfNecessary() {
		switch (paramType) {
		case Types.VARCHAR:
			convertToString();
			break;
		case ExtendedSQLTypes.TIMESTAMP2:
			convertToTimestamp();
			break;
		case Types.TIMESTAMP:
		case Types.DATE:
			convertToSQLDate();
			break;
		case Types.INTEGER:
			convertToInt();
			break;
		case Types.BIGINT:
			convertToLong();
			break;
		case Types.DOUBLE:
			convertToDouble();
			break;
		case Types.TINYINT:
			convertToByte();
			break;
		case Types.FLOAT:
			convertToFloat();
			break;
		case Types.SMALLINT:
			convertToShort();
			break;
		case Types.BIT:
			convertToBoolean();
			break;
		default:
			throw new I18NRuntimeException("unsupported database type to bind :{0}",
					new Object[] { new Integer(paramType) });
		}
	}

	private void convertToTimestamp() {
		throw new I18NRuntimeException("cannot convert value to Timestamp {0}",
				new Object[] { paramValue });
	}

	private void convertToSQLDate() {
		// currently we only accept date type value
		// throw new IllegalArgumentException("Can't convert value:" +
		// paramValue +
		// " to date");
		throw new I18NRuntimeException("cannot convert value to Date:{0}",
				new Object[] { paramValue });
	}

	private void convertToString() {
		if (!(paramValue instanceof String)) {
			paramValue = (paramValue == null) ? "null" : paramValue.toString();
		}
	}

	private void convertToInt() {
		if (paramValue instanceof Integer) {
		} else if (paramValue instanceof Number) {
			paramValue = new Integer(((Number) paramValue).intValue());
		} else if (paramValue instanceof Boolean) {
			paramValue = ((Boolean) paramValue).booleanValue() ? new Integer(1)
					: new Integer(0);
		} else if (paramValue instanceof String) {
			paramValue = new Integer((String) paramValue);
		} else {
			throw new I18NRuntimeException("cannot convert value to int:{0}",
					new Object[] { paramValue });
		}
	}

	private void convertToLong() {
		if (paramValue instanceof Long) {
		} else if (paramValue instanceof Number) {
			paramValue = new Long(((Number) paramValue).longValue());
		} else if (paramValue instanceof String) {
			paramValue = new Long((String) paramValue);
		} else {
			throw new I18NRuntimeException("cannot convert value to long:{0}",
					new Object[] { paramValue });
		}
	}

	private void convertToDouble() {
		if (paramValue instanceof Double) {
		} else if (paramValue instanceof Number) {
			paramValue = new Double(((Number) paramValue).doubleValue());
		} else if (paramValue instanceof String) {
			paramValue = new Double((String) paramValue);
		} else {
			throw new I18NRuntimeException("cannot convert value to double:{0}",
					new Object[] { paramValue });
		}
	}

	private void convertToFloat() {
		if (paramValue instanceof Float) {
		} else if (paramValue instanceof Number) {
			paramValue = new Float(((Number) paramValue).floatValue());
		} else if (paramValue instanceof String) {
			paramValue = new Float((String) paramValue);
		} else {
			throw new I18NRuntimeException("cannot convert value to float:{0}",
					new Object[] { paramValue });
		}
	}

	private void convertToByte() {
		if (paramValue instanceof Byte) {
		} else if (paramValue instanceof Number) {
			paramValue = new Byte(((Number) paramValue).byteValue());
		} else if (paramValue instanceof String) {
			paramValue = new Byte((String) paramValue);
		} else {
			throw new I18NRuntimeException("cannot convert value to byte:{0}",
					new Object[] { paramValue });
		}
	}

	private void convertToShort() {
		if (paramValue instanceof Short) {
		} else if (paramValue instanceof Number) {
			paramValue = new Short(((Number) paramValue).shortValue());
		} else if (paramValue instanceof String) {
			paramValue = new Short((String) paramValue);
		} else {
			throw new I18NRuntimeException("cannot convert value to short:{0}",
					new Object[] { paramValue });
			// throw new IllegalArgumentException("Can't convert value:" +
			// paramValue +
			// " to short");
		}
	}

	private void convertToBoolean() {
		paramType = Types.INTEGER;
		if (paramValue instanceof Boolean) {
			paramValue = ((Boolean) paramValue).booleanValue() ? new Integer(1)
					: new Integer(0);
		} else if (paramValue instanceof Number) {
			int intValue = ((Number) paramValue).intValue();
			paramValue = (intValue == 0) ? new Integer(0) : new Integer(1);
		} else if (paramValue instanceof String) {
			paramValue = "false".equalsIgnoreCase((String) paramValue) ? new Integer(
					0) : new Integer(1);
		} else {
			throw new I18NRuntimeException("cannot convert value to boolean:{0}",
					new Object[] { paramValue });
		}
	}

	public String toString() {
		return String.valueOf(paramValue);
	}

}
