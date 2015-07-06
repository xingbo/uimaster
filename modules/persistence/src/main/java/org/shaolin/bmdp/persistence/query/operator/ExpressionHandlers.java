package org.shaolin.bmdp.persistence.query.operator;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.shaolin.bmdp.persistence.query.generator.DataParser;
import org.shaolin.bmdp.persistence.query.generator.QueryParsingContext;
import org.shaolin.bmdp.persistence.query.generator.QueryUtil;
import org.shaolin.bmdp.persistence.query.generator.SearchQuery;
import org.shaolin.javacc.sql.SQLConstants;
import org.shaolin.javacc.sql.exception.QueryParsingException;

/**
 * A registry of expression handlers.
 * <P>
 * Each operator can have associated handlers registered to create database
 * specific query generators.
 * 
 */
public class ExpressionHandlers {
	/**
	 * expression handlers currently registered.
	 */
	private static HashMap handlers = new HashMap(32);

	/**
	 * the default handler for BETWEEN operator
	 */
	static final BetweenHandler BETWEEN_HANDLER = new BetweenHandler();

	/**
	 * the default handler for START_WITH operator
	 */
	static final StartWithHandler START_WITH_HANDLER = new StartWithHandler();

	/**
	 * the default handler for START_WITH_IGNORE_CASE operator
	 */
	static final StartWithIgnoreCaseHandler START_WITH_IC_HANDLER = new StartWithIgnoreCaseHandler();

	/**
	 * the default handler for EQUALS_IGNORE_CASE operator
	 */
	static final EqualsIgnoreCaseHandler EQUALS_IC_HANDLER = new EqualsIgnoreCaseHandler();

	/**
	 * the default handler for EQUALS_IGNORE_CASE operator
	 */
	static final NotEqualsHandler NOT_EQUALS_HANDLER = new NotEqualsHandler();

	/**
	 * the default handler for NOT_EQUALS_IGNORE_CASE operator
	 */
	static final NotEqualsIgnoreCaseHandler NOT_EQUALS_IC_HANDLER = new NotEqualsIgnoreCaseHandler();

	/**
	 * the default handler for NOT_NULL operator
	 */
	static final NullHandler NULL_HANDLER = new NullHandler();
	/**
	 * the default handler for CONTAINS_WORD operator
	 */
	static final ContainsWordHandler CONTAINS_WORD_HANDLER = new ContainsWordHandler();
	/**
	 * the default handler for CONTAINS_PARTIAL operator
	 */
	static final ContainsPartialHandler CONTAINS_PARTIAL_HANDLER = new ContainsPartialHandler();

	/**
	 * the default handler for CONTAINS_WORD_IGNORE_CASE operator
	 */
	static final ContainsWordIgnoreCaseHandler CONTAINS_WORD_IC_HANDLER = new ContainsWordIgnoreCaseHandler();
	/**
	 * the default handler for CONTAINS_PARTIAL_IGNORE_CASE operator
	 */
	static final ContainsPartialIgnoreCaseHandler CONTAINS_PARTIAL_IC_HANDLER = new ContainsPartialIgnoreCaseHandler();

	/**
	 * the default handler for COLLECTION_CONTAINS operator
	 */
	static final CollectionContainsHandler COLLECTION_CONTAINS_HANDLER = new CollectionContainsHandler();

	static final ExistsHandler EXISTS_HANDLER = new ExistsHandler();

	static final PhoneticMatchHandler PHONETIC_MATCH_HANDLER = new PhoneticMatchHandler();

	static final NotPhoneticMatchHandler NOT_PHONETCI_MATCH_HANDLER = new NotPhoneticMatchHandler();

	static {
		register(Operator.START_WITH, START_WITH_HANDLER);
		register(Operator.START_WITH.reverse(), START_WITH_HANDLER);
		
		register(Operator.START_WITH_LEFT, START_WITH_HANDLER);
		register(Operator.START_WITH_LEFT.reverse(), START_WITH_HANDLER);

		register(Operator.START_WITH_RIGHT, START_WITH_HANDLER);
		register(Operator.START_WITH_RIGHT.reverse(), START_WITH_HANDLER);
		
		register(Operator.START_WITH_IGNORE_CASE, START_WITH_IC_HANDLER);
		register(Operator.START_WITH_IGNORE_CASE.reverse(),
				START_WITH_IC_HANDLER);

		register(Operator.EQUALS, new DefaultHandler(Operator.EQUALS));
		register(Operator.EQUALS.reverse(), NOT_EQUALS_HANDLER);

		register(Operator.EQUALS_IGNORE_CASE, EQUALS_IC_HANDLER);
		register(Operator.EQUALS_IGNORE_CASE.reverse(), NOT_EQUALS_IC_HANDLER);

		register(Operator.BETWEEN, BETWEEN_HANDLER);
		register(Operator.BETWEEN.reverse(), BETWEEN_HANDLER);

		register(Operator.CONTAINS_WORD, CONTAINS_WORD_HANDLER);
		register(Operator.CONTAINS_WORD.reverse(), CONTAINS_WORD_HANDLER);

		register(Operator.CONTAINS_WORD_IGNORE_CASE, CONTAINS_WORD_IC_HANDLER);
		register(Operator.CONTAINS_WORD_IGNORE_CASE.reverse(),
				CONTAINS_WORD_IC_HANDLER);

		register(Operator.CONTAINS_PARTIAL, CONTAINS_PARTIAL_HANDLER);
		register(Operator.CONTAINS_PARTIAL.reverse(), CONTAINS_PARTIAL_HANDLER);

		register(Operator.CONTAINS_PARTIAL_IGNORE_CASE,
				CONTAINS_PARTIAL_IC_HANDLER);
		register(Operator.CONTAINS_PARTIAL_IGNORE_CASE.reverse(),
				CONTAINS_PARTIAL_IC_HANDLER);

		register(Operator.IS_NULL, NULL_HANDLER);
		register(Operator.IS_NULL.reverse(), NULL_HANDLER);

		register(Operator.IN, new DefaultHandler(Operator.IN));
		register(Operator.IN.reverse(),
				new DefaultHandler(Operator.IN.reverse()));

		register(Operator.LESS_THAN, new DefaultHandler(Operator.LESS_THAN));
		register(Operator.LESS_THAN.reverse(), new DefaultHandler(
				Operator.GREATER_THAN_OR_EQUALS));

		register(Operator.LESS_THAN_OR_EQUALS, new DefaultHandler(
				Operator.LESS_THAN_OR_EQUALS));
		register(Operator.LESS_THAN_OR_EQUALS.reverse(), new DefaultHandler(
				Operator.GREATER_THAN));

		register(Operator.GREATER_THAN, new DefaultHandler(
				Operator.GREATER_THAN));
		register(Operator.GREATER_THAN.reverse(), new DefaultHandler(
				Operator.LESS_THAN_OR_EQUALS));

		register(Operator.GREATER_THAN_OR_EQUALS, new DefaultHandler(
				Operator.GREATER_THAN_OR_EQUALS));
		register(Operator.GREATER_THAN_OR_EQUALS.reverse(), new DefaultHandler(
				Operator.LESS_THAN));

		register(Operator.COLLECTION_CONTAINS, COLLECTION_CONTAINS_HANDLER);
		register(Operator.COLLECTION_CONTAINS.reverse(),
				COLLECTION_CONTAINS_HANDLER);

		register(Operator.EXISTS, EXISTS_HANDLER);
		register(Operator.EXISTS.reverse(), EXISTS_HANDLER);

	}

	static final String START_WITH_FORMAT = "{0} LIKE {1}";

	static final String NOT_START_WITH_FORMAT = "{0} NOT LIKE {1}";

	static final String START_WITH_IGNORE_CASE_FORMAT = "UPPER({0}) LIKE UPPER({1})";

	static final String NOT_START_WITH_IGNORE_CASE_FORMAT = "UPPER({0}) NOT LIKE UPPER({1})";

	static final String CONTAINS_WORD_FORMAT = "{0} LIKE {1} || ' %' OR "
			+ "{0} LIKE '% ' || {1} OR {0} LIKE '% ' || {1} || ' %'";

	static final String NOT_CONTAINS_WORD_FORMAT = "{0} NOT LIKE {1} || ' %' AND "
			+ "{0} NOT LIKE '% ' || {1} AND {0} NOT LIKE '% ' || {1} || ' %'";

	static final String CONTAINS_WORD_IGNORE_CASE_FORMAT = "UPPER({0}) LIKE UPPER({1}) || ' %' OR "
			+ "UPPER({0}) LIKE '% ' || UPPER({1}) OR UPPER({0}) LIKE '% ' || UPPER({1}) || ' %'";

	static final String NOT_CONTAINS_WORD_IGNORE_CASE_FORMAT = "UPPER({0}) NOT LIKE UPPER({1}) || ' %' AND "
			+ "UPPER({0}) NOT LIKE '% ' || UPPER({1}) AND UPPER({0}) NOT LIKE '% ' || UPPER({1}) || ' %'";

	static final String CONTAINS_PARTIAL_FORMAT = "{0} LIKE '%' || {1} || '%'";

	static final String NOT_CONTAINS_PARTIAL_FORMAT = "{0} NOT LIKE '%' || {1} || '%'";

	static final String CONTAINS_PARTIAL_IGNORE_CASE_FORMAT = "UPPER({0}) LIKE '%' || UPPER({1}) || '%'";

	static final String NOT_CONTAINS_PARTIAL_IGNORE_CASE_FORMAT = "UPPER({0}) NOT LIKE '%' || UPPER({1}) || '%'";

	static final String BINARY_IGNORE_CASE_FORMAT = "UPPER({0}) {1} UPPER({2})";

	/**
	 * Register an expression handler for a search operator.
	 * 
	 * @param anOpr
	 *            the operator.,
	 * @param aHandler
	 *            the handler produces sql from the expression
	 */
	public static void register(Operator anOpr, IExpressionHandler aHandler) {
		handlers.put(anOpr, aHandler);
	}

	/**
	 * Get a handler registered against an operator.
	 * 
	 * @param anOpr
	 *            the operator to get the handler for.
	 * @return the handler registered against this operator.
	 */
	public static IExpressionHandler getHandler(Operator anOpr) {
		IExpressionHandler aHandler = (IExpressionHandler) handlers.get(anOpr);
		if (null == aHandler) {
			throw new IllegalArgumentException("Unknown operator:" + anOpr);
		}
		return aHandler;
	}

	private static abstract class AbstractExpressionHandler implements
			IExpressionHandler {
		public String parseUpdate(String leftColumn, Object rightColumn,
				Operator anOpr, Object value, QueryParsingContext parsingContext)
				throws QueryParsingException {
			String filterString = parse(leftColumn, rightColumn, anOpr, value,
					parsingContext);
			return QueryUtil.parseUpdate(parsingContext, filterString);
		}

		public int getParamCopyCount() {
			return 0;
		}
	}

	/**
	 * handler for <code>BETWEEN</code> operator.
	 * 
	 * @author Tom Joseph
	 */
	private static class BetweenHandler extends AbstractExpressionHandler {
		public String parse(String leftColumn, Object rightColumn,
				Operator anOpr, Object value, QueryParsingContext parsingContext)
				throws QueryParsingException {
			// rightColumn must be null

			Object aFrom = "?";
			Object aTo = "?";
			
			int databaseType = parsingContext.getExpressionDatabaseType();
			String from = parsingContext.getExpressionValueAsParam() ? (String) DataParser
					.parseParamValue(aFrom, parsingContext, false)
					: (String) DataParser.parseValue(aFrom,
							parsingContext.getExpressionPFName(), databaseType,
							parsingContext.getQueryDebugString());
			String to = parsingContext.getExpressionValueAsParam() ? (String) DataParser
					.parseParamValue(aTo, parsingContext, false)
					: (String) DataParser.parseValue(aTo,
							parsingContext.getExpressionPFName(), databaseType,
							parsingContext.getQueryDebugString());
					
			return leftColumn + SQLConstants.SPACE + anOpr.toString()
					+ SQLConstants.SPACE + SQLConstants.OPEN_BRACKET + from + SQLConstants.SPACE
					+ SQLConstants.AND + SQLConstants.SPACE + to+ SQLConstants.CLOSE_BRACKET ;
		}
	}

	/**
	 * handler for <code>IS_NULL.reverse()</code> operator.
	 * 
	 * @author Tom Joseph
	 */
	private static class NullHandler extends AbstractExpressionHandler {
		public String parse(String leftColumn, Object rightColumn,
				Operator anOpr, Object value, QueryParsingContext parsingContext)
				throws QueryParsingException {
			String opStr = (anOpr == Operator.IS_NULL) ? SQLConstants.IS_NULL
					: SQLConstants.IS_NOT_NULL;
			return leftColumn + SQLConstants.SPACE + opStr;
		}
	}// NotNullHandler

	/**
	 * handler for <code>START_WITH()</code> operator.
	 * 
	 * @author Bu FeiMing
	 */
	private static class StartWithHandler extends AbstractExpressionHandler {
		public String parse(String leftColumn, Object rightColumn,
				Operator anOpr, Object value, QueryParsingContext parsingContext)
				throws QueryParsingException {
			QueryFormat aFormat = new QueryFormat(START_WITH_FORMAT);
			return aFormat.format(new Object[] { leftColumn, rightColumn });
		}
	}

	/**
	 * handler for <code>START_WITH_IGNORE_CASE()</code> operator.
	 * 
	 */
	private static class StartWithIgnoreCaseHandler extends
			AbstractExpressionHandler {
		public String parse(String leftColumn, Object rightColumn,
				Operator anOpr, Object value, QueryParsingContext parsingContext)
				throws QueryParsingException {
			QueryFormat aFormat = new QueryFormat(
					Operator.START_WITH_IGNORE_CASE.equals(anOpr) ? START_WITH_IGNORE_CASE_FORMAT
							: NOT_START_WITH_IGNORE_CASE_FORMAT);
			return aFormat.format(new Object[] { leftColumn, rightColumn });
		}
	}

	/**
	 * handler for <code>CONTAINS_WORD()</code> operator.
	 * 
	 * @author Tom Joseph
	 */
	private static class ContainsWordHandler extends AbstractExpressionHandler {
		public String parse(String leftColumn, Object rightColumn,
				Operator anOpr, Object value, QueryParsingContext parsingContext)
				throws QueryParsingException {
			QueryFormat aFormat = new QueryFormat(
					Operator.CONTAINS_WORD.equals(anOpr) ? CONTAINS_WORD_FORMAT
							: NOT_CONTAINS_WORD_FORMAT);
			return aFormat.format(new Object[] { leftColumn, rightColumn });
		}

		public int getParamCopyCount() {
			return 2;
		}
	}// ContainsWordHandler

	/**
	 * handler for <code>CONTAINS_PARTIAL()</code> operator.
	 * 
	 * @author Tom Joseph
	 */
	private static class ContainsPartialHandler extends
			AbstractExpressionHandler {
		public String parse(String leftColumn, Object rightColumn,
				Operator anOpr, Object value, QueryParsingContext parsingContext)
				throws QueryParsingException {
			QueryFormat aFormat = new QueryFormat(
					Operator.CONTAINS_PARTIAL.equals(anOpr) ? CONTAINS_PARTIAL_FORMAT
							: NOT_CONTAINS_PARTIAL_FORMAT);
			return aFormat.format(new Object[] { leftColumn, rightColumn });
		}
	}// ContainsWordHandler

	/**
	 * handler for <code>CONTAINS_WORD_IGNORE_CASE()</code> operator.
	 * 
	 * @author Tom Joseph
	 */
	private static class ContainsWordIgnoreCaseHandler extends
			AbstractExpressionHandler {
		public String parse(String leftColumn, Object rightColumn,
				Operator anOpr, Object value, QueryParsingContext parsingContext)
				throws QueryParsingException {
			QueryFormat aFormat = new QueryFormat(
					Operator.CONTAINS_WORD_IGNORE_CASE.equals(anOpr) ? CONTAINS_WORD_IGNORE_CASE_FORMAT
							: NOT_CONTAINS_WORD_IGNORE_CASE_FORMAT);
			return aFormat.format(new Object[] { leftColumn, rightColumn });
		}

		public int getParamCopyCount() {
			return 2;
		}
	}// ContainsWordIgnoreCaseHandler

	/**
	 * handler for <code>CONTAINS_PARTIAL_IGNORE_CASE()</code> operator.
	 * 
	 * @author Tom Joseph
	 */
	private static class ContainsPartialIgnoreCaseHandler extends
			AbstractExpressionHandler {
		public String parse(String leftColumn, Object rightColumn,
				Operator anOpr, Object value, QueryParsingContext parsingContext)
				throws QueryParsingException {
			QueryFormat aFormat = new QueryFormat(
					Operator.CONTAINS_PARTIAL_IGNORE_CASE.equals(anOpr) ? CONTAINS_PARTIAL_IGNORE_CASE_FORMAT
							: NOT_CONTAINS_PARTIAL_IGNORE_CASE_FORMAT);

			return aFormat.format(new Object[] { leftColumn, rightColumn });
		}
	}// ContainsPartialIgnoreCaseHandler

	/**
	 * handler for <code>EQUALS_IGNORE_CASE()</code> operator.
	 * 
	 * @author Tom Joseph
	 */
	private static class EqualsIgnoreCaseHandler extends
			AbstractExpressionHandler {
		public String parse(String leftColumn, Object rightColumn,
				Operator anOpr, Object value, QueryParsingContext parsingContext)
				throws QueryParsingException {
			QueryFormat aFormat = new QueryFormat(BINARY_IGNORE_CASE_FORMAT);
			return aFormat.format(new Object[] { leftColumn,
					SQLConstants.EQUAL, rightColumn });
		}
	}// EqualsIgnoreCaseHandler

	/**
	 * handler for <code>COLLECTION_CONTAINS</code> operator.
	 * 
	 * @author Tom Joseph
	 */
	private static class CollectionContainsHandler extends
			AbstractExpressionHandler {
		public String parse(String leftColumn, Object rightColumn,
				Operator anOpr, Object value, QueryParsingContext parsingContext)
				throws QueryParsingException {
			String opString = null;
			if (value instanceof Collection || value instanceof SearchQuery) {
				opString = (anOpr == Operator.COLLECTION_CONTAINS) ? SQLConstants.IN
						: SQLConstants.NOT + SQLConstants.SPACE
								+ SQLConstants.IN;
			} else {
				opString = (anOpr == Operator.COLLECTION_CONTAINS) ? SQLConstants.EQUAL
						: SQLConstants.NOT_EQUALS;
			}

			return leftColumn + SQLConstants.SPACE + opString
					+ SQLConstants.SPACE + rightColumn;
		}
	}// EqualsIgnoreCaseHandler

	/**
	 * handler for <code>NOT_EQUALS_IGNORE_CASE()</code> operator.
	 * 
	 * @author Tom Joseph
	 */
	private static class NotEqualsIgnoreCaseHandler extends
			AbstractExpressionHandler {
		public String parse(String leftColumn, Object rightColumn,
				Operator anOpr, Object value, QueryParsingContext parsingContext)
				throws QueryParsingException {
			QueryFormat aFormat = new QueryFormat(BINARY_IGNORE_CASE_FORMAT);
			return aFormat.format(new Object[] { leftColumn,
					SQLConstants.NOT_EQUALS, rightColumn });
		}
	}// EqualsIgnoreCaseHandler

	/**
	 * handler for <code>EQUALS.reverse()</code> operator.
	 * 
	 * @author Tom Joseph
	 */
	private static class NotEqualsHandler extends AbstractExpressionHandler {
		public String parse(String leftColumn, Object rightColumn,
				Operator anOpr, Object value, QueryParsingContext parsingContext)
				throws QueryParsingException {
			return leftColumn + SQLConstants.SPACE + SQLConstants.NOT_EQUALS
					+ SQLConstants.SPACE + rightColumn;
		}
	}// EqualsIgnoreCaseHandler

	private static class ExistsHandler extends AbstractExpressionHandler {
		public String parse(String leftColumn, Object rightColumn,
				Operator anOpr, Object value, QueryParsingContext parsingContext)
				throws QueryParsingException {
			return anOpr + (String) rightColumn;
		}
	}

	private static class PhoneticMatchHandler extends AbstractExpressionHandler {
		public String parse(String leftColumn, Object rightColumn,
				Operator anOpr, Object value, QueryParsingContext parsingContext)
				throws QueryParsingException {
			return SQLConstants.SOUNDEX + SQLConstants.OPEN_BRACKET
					+ leftColumn + SQLConstants.CLOSE_BRACKET
					+ SQLConstants.EQUAL + SQLConstants.SOUNDEX
					+ SQLConstants.OPEN_BRACKET + rightColumn
					+ SQLConstants.CLOSE_BRACKET;
		}
	}

	private static class NotPhoneticMatchHandler extends
			AbstractExpressionHandler {
		public String parse(String leftColumn, Object rightColumn,
				Operator anOpr, Object value, QueryParsingContext parsingContext)
				throws QueryParsingException {
			return SQLConstants.SOUNDEX + SQLConstants.OPEN_BRACKET
					+ leftColumn + SQLConstants.CLOSE_BRACKET
					+ SQLConstants.NOT_EQUALS + SQLConstants.SOUNDEX
					+ SQLConstants.OPEN_BRACKET + rightColumn
					+ SQLConstants.CLOSE_BRACKET;
		}
	}

	/**
	 * Default handler for an operator.
	 * 
	 * @author Tom Joseph
	 */
	private static class DefaultHandler extends AbstractExpressionHandler {
		private Operator opr;

		/**
		 * Construct a default handler for the specified operator.
		 * 
		 * @param anOpr
		 *            the operator
		 */
		public DefaultHandler(Operator anOpr) {
			opr = anOpr;
		}

		public String parse(String leftColumn, Object rightColumn,
				Operator anOpr, Object value, QueryParsingContext parsingContext)
				throws QueryParsingException {
			if (anOpr == Operator.IN) {
				if (rightColumn != null && !"'?'".equals(rightColumn)) {
					// for sub query
					return leftColumn + SQLConstants.SPACE + opr + SQLConstants.SPACE
							+ rightColumn;
				}
				return leftColumn + SQLConstants.SPACE + opr + SQLConstants.SPACE
						+ SQLConstants.QUESTION;
			}
			if (rightColumn instanceof Integer) {
				int n = ((Integer) rightColumn).intValue();
				String exprStr = leftColumn + SQLConstants.SPACE + opr
						+ SQLConstants.SPACE + SQLConstants.QUESTION;
				StringBuffer sb = new StringBuffer("(");
				for (int i = 0; i < n; i++) {
					if (i > 0) {
						sb.append(SQLConstants.SPACE);
						sb.append(SQLConstants.OR);
						sb.append(SQLConstants.SPACE);
					}
					sb.append(exprStr);
				}
				sb.append(")");
				return new String(sb);
			}
			if (rightColumn instanceof List) {
				List valueList = (List) rightColumn;
				StringBuffer sb = new StringBuffer("(");
				for (int i = 0, n = valueList.size(); i < n; i++) {
					if (i > 0) {
						sb.append(SQLConstants.SPACE);
						sb.append(SQLConstants.OR);
						sb.append(SQLConstants.SPACE);
					}
					sb.append(leftColumn);
					sb.append(SQLConstants.SPACE);
					sb.append(opr);
					sb.append(SQLConstants.SPACE);
					sb.append("'");
					sb.append(valueList.get(i));
					sb.append("'");
				}
				sb.append(")");
				return new String(sb);
			}
			return leftColumn + SQLConstants.SPACE + opr + SQLConstants.SPACE
					+ rightColumn;
		}
	}

}

