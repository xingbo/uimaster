package org.shaolin.bmdp.persistence;

import java.util.ArrayList;
import java.util.List;

import org.shaolin.javacc.sql.SQLConstants;

public class SQLConditionUtil {

	/** Condition handles */
	private static final String WHERE_WORD = SQLConstants.SPACE
			+ SQLConstants.WHERE + SQLConstants.SPACE;
	private static final String WHERE_WTIH_OPEN_WORD = SQLConstants.SPACE
			+ SQLConstants.WHERE + SQLConstants.SPACE
			+ SQLConstants.OPEN_BRACKET;
	private static final String IN_WORD = SQLConstants.SPACE + SQLConstants.IN
			+ SQLConstants.SPACE + SQLConstants.QUESTION;
	private static final String AND_WORD = SQLConstants.SPACE
			+ SQLConstants.AND + SQLConstants.SPACE;
	private static final int WHERE_LENGTH = WHERE_WORD.length();
	private static final int WHERE_WTIH_OPEN_LENGTH = WHERE_WTIH_OPEN_WORD
			.length();
	private static final int AND_LENGTH = AND_WORD.length();
	private static final int IN_LENGH = IN_WORD.length();

	public static void cutCondition(StringBuffer sql, String condition) {
		int start = sql.indexOf(condition);
		int end = start + condition.length();
		if (sql.length() >  end + AND_LENGTH && AND_WORD.equals(sql.substring(end, end + AND_LENGTH))) {
			// ? And
			end += AND_LENGTH;
		} else if ((start - AND_LENGTH) >= 0 && AND_WORD.equals(sql.substring(start - AND_LENGTH, start))) {
			// And ?
			start -= AND_LENGTH;
		}

		if ((start - WHERE_LENGTH) >= 0 && WHERE_WORD.equals(sql.substring(start - WHERE_LENGTH, start))) {
			// Where ?
			start -= WHERE_LENGTH;
		} else if ((start - WHERE_WTIH_OPEN_LENGTH) >= 0 && WHERE_WTIH_OPEN_WORD.equals(sql.substring(start
				- WHERE_WTIH_OPEN_LENGTH, start))
				&& SQLConstants.CLOSE_BRACKET.equals(sql
						.substring(end, end + 1))) {
			// Where (?)
			start -= WHERE_WTIH_OPEN_LENGTH;
			end += 1;
		}
		sql.delete(start, end);
	}

	public static void handleINCondition(StringBuffer sql,
			final String conditionExpression, List<?> list,
			ArrayList<Object> parameters) {
		int n = list.size();
		if (n == 0) {
			throw new IllegalArgumentException("IN operator["
					+ conditionExpression
					+ "] must have at least one parameter.");
		}
		
		StringBuffer sb = new StringBuffer(conditionExpression);
		int start = sb.indexOf(IN_WORD);
		if (start == -1) {
			throw new IllegalArgumentException("IN operator["
					+ conditionExpression + "] can be detected.");
		}
		start = +IN_LENGH;
		sb.deleteCharAt(start);
		sb.append("(");
		for (Object value : list) {
			if (value instanceof Integer || value instanceof Long) {
				sb.append(value);
			} else {
				// TODO: what about the date type?
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}
			sb.append(",");
		}
		sb.deleteCharAt(sb.length());
		sb.append(")");

		int cstart = sql.indexOf(conditionExpression);
		int cend = cstart + conditionExpression.length();
		sql.replace(cstart, cend, sb.toString());
	}

	public static void handleBetweenCondition(List<?> list,
			ArrayList<Object> parameters) {
		int n = list.size();
		if (n < 2) {
			throw new IllegalArgumentException(
					"Between operator must have two parameters");
		}
		Object value0 = list.get(0);
		Object value1 = list.get(1);
//		if (value0 instanceof Integer || value0 instanceof Long) {
			parameters.add(value0);
			parameters.add(value1);
//		} else {
//			// TODO: hand date.
//			parameters.add("'" + value0 + "'");
//			parameters.add("'" + value1 + "'");
//		}
	}
}
