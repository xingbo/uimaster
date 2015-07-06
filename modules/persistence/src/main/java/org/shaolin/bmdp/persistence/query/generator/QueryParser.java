package org.shaolin.bmdp.persistence.query.generator;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.bmdp.persistence.provider.DBMSProviderFactory;
import org.shaolin.bmdp.persistence.provider.IDBMSProvider;
import org.shaolin.bmdp.persistence.query.operator.ExpressionHandlers;
import org.shaolin.bmdp.persistence.query.operator.IExpressionHandler;
import org.shaolin.bmdp.persistence.query.operator.LogicalOperator;
import org.shaolin.bmdp.persistence.query.operator.Operator;
import org.shaolin.javacc.sql.SQLConstants;
import org.shaolin.javacc.sql.exception.OQLException;
import org.shaolin.javacc.sql.exception.QueryParsingException;
import org.shaolin.javacc.sql.node.OQLBinaryExpression;
import org.shaolin.javacc.sql.node.OQLExpression;

public class QueryParser {
	private static final Logger logger = Logger
			.getLogger("bmiasia.ebos.ormapper.search.QueryParser");

	private SearchQuery query = null;
	private ParsedQuery parsedQuery = null;

	private String selectAndHintClause = null;
	private List<String> selectClause = null;
	private String fromClause = null;
	private String whereClause = null;
	private String joinClause = null;
	private String startWithClause = null;
	private String connectByClause = null;
	private String groupByClause = null;
	private String havingClause = null;
	private String orderByClause = null;

	private List<String> selectClauseFromOrder = null;

	private AliasGenerator aliasGenerator = null;
	private AliasGenerator paramIndexGenerator = null;
	private AliasGenerator internalParamGenerator = null;
	private QueryParsingContext parsingContext = null;
	private QueryParsingContext superContext = null;

	private Map params = null;
	private Map boundParams = null;

	public QueryParser(SearchQuery query) {
		this.query = query;
		this.aliasGenerator = new AliasGenerator();
		this.paramIndexGenerator = new AliasGenerator();
		this.internalParamGenerator = new AliasGenerator();
		this.params = new HashMap();
		this.boundParams = new HashMap();
	}

	public QueryParser(SearchQuery query, QueryParsingContext superContext) {
		this.query = query;
		this.superContext = superContext;
		this.aliasGenerator = superContext.getAliasGenerator();
		this.paramIndexGenerator = superContext.getParamIndexGenerator();
		this.internalParamGenerator = superContext.getInternalParamGenerator();
		this.params = new HashMap();
		this.boundParams = new HashMap();
	}

	public void parse() throws QueryParsingException {
		parsingContext = new QueryParsingContext(aliasGenerator,
				paramIndexGenerator, internalParamGenerator, params,
				boundParams, true, false);
		parsingContext.setQueryDebugString(query.toString());
		if (superContext != null) {
			parsingContext.setSuperParsingContext(superContext);
		}

		// parse from first
		parseFrom();

		parseHint();
		parseSelect();
		parseWhere();
		buildConnectByClause();
		parseGroupBy();
		parseHaving();
		parseOrderBy();
		buildSql();

		if (superContext != null) {
			superContext.addExpressionParams(params);
			superContext.addBoundParams(boundParams);
		}
	}

	public ParsedQuery getParsedQuery() {
		return parsedQuery;
	}

	public String getSql() {
		return parsedQuery.buildSql();
	}

	public String getCountSql() {
		return parsedQuery.buildCountSql();
	}
	
	public Map getParams() {
		return params;
	}

	public Map getBoundParams() {
		return boundParams;
	}

	private void parseFrom() throws QueryParsingException {
		parsingContext.parseFrom(query.getFrom());
	}

	private void parseHint() {
		if (logger.isInfoEnabled()) {
			logger.info("Hints " + DBMSProviderFactory.getProviderId()
					+ " data source.");
		}
		StringBuffer sb = new StringBuffer(SQLConstants.SELECT
				+ SQLConstants.SPACE);
		List hintList = query.getHint();
		if (hintList != null && !hintList.isEmpty()) {
			sb.append(SQLConstants.SLASH + SQLConstants.STAR
					+ SQLConstants.PLUS);
			for (int i = 0, n = hintList.size(); i < n; i++) {
				sb.append(SQLConstants.SPACE);
				sb.append((String) hintList.get(i));
			}
			sb.append(SQLConstants.SPACE + SQLConstants.STAR
					+ SQLConstants.SLASH + SQLConstants.SPACE);
		}
		selectAndHintClause = new String(sb);
	}

	private void parseSelect() throws QueryParsingException {
		List<String> selection = query.getSelection();
		if (selection == null || selection.size() == 0) {
			throw new QueryParsingException("Invalid query.The selection of query is null");
		}
		selectClause = (List<String>) parseStringList(selection, true);
	}

	private void parseWhere() throws QueryParsingException {
		QueryExpressionNodeList filter = query.getJoinFilter();
		if (filter != null) {
			ParsedFilter where = parseExpressionNodeList(filter, false);
			joinClause = where.getSql();
		}
		
		filter = query.getFilter();
		if (filter != null) {
			ParsedFilter where = parseExpressionNodeList(filter, true);
			whereClause = where.getSql();
		}
	}
	
	private void parseGroupBy() throws QueryParsingException {
		List<String> groupBy = query.getGroupBy();
		if (groupBy == null || groupBy.size() == 0) {
			return;
		}

		groupByClause = (String) parseStringList(groupBy, false);
	}

	private void parseHaving() throws QueryParsingException {
		QueryExpressionNodeList having = query.getHaving();
		if (having != null) {
			ParsedFilter havingFilter = parseExpressionNodeList(having, true);
			havingClause = havingFilter.getSql();
			parsingContext.addJoinClauses(havingFilter.getJoinList());
		}
	}

	private void parseOrderBy() throws QueryParsingException {
		List<Ordering> orderByList = query.getOrderBy();
		if (orderByList == null || orderByList.size() == 0) {
			return;
		}

		StringBuffer orderByBuffer = new StringBuffer(SQLConstants.SPACE + //
				SQLConstants.ORDER_BY + // order by
				SQLConstants.SPACE); //
		selectClauseFromOrder = new ArrayList<String>(orderByList.size());
		for (int i = 0, n = orderByList.size(); i < n; i++) {
			if (i > 0) {
				orderByBuffer.append(SQLConstants.COMMA); // ,
			}
			Ordering order = (Ordering) orderByList.get(i);
			String orderExpr = order.getExpression();
			parsingContext.collectExpressionParams();
			parsingContext.addJoinClauses(parsingContext
					.getExpressionJoinClauses());

			orderByBuffer.append(orderExpr); // ,
			selectClauseFromOrder.add(orderExpr);

			if (order.isAscending()) {
				orderByBuffer.append(Ordering.ASC);
			} else {
				orderByBuffer.append(Ordering.DESC);
			}
		}
		orderByClause = new String(orderByBuffer);
	}

	public ParsedFilter parseExpressionNodeList(
			QueryExpressionNodeList filter, boolean isTopNode)
			throws QueryParsingException {
		ArrayList<Object> filters = new ArrayList<Object>();
		ArrayList<ArrayList<?>> joins = new ArrayList<ArrayList<?>>();
		for (Iterator<?> it = filter.list(); it.hasNext();) {
			Object item = it.next();
			if (item instanceof LogicalOperator) {
				filters.add(item);
			} else if (item instanceof QueryExpression) {
				ParsedFilter expFilter = parseExpression((QueryExpression) item);
				filters.add(expFilter);
				joins.add(expFilter.getJoinList());
			} else if (item instanceof QueryExpressionNodeList) {
				ParsedFilter expListFilter = parseExpressionNodeList(
						(QueryExpressionNodeList) item, false);
				filters.add(expListFilter);
				joins.add(expListFilter.getJoinList());
			}
		}
		ArrayList<String> commonJoin = getCommonJoin(joins);
		StringBuffer sb = new StringBuffer();
		if (!isTopNode && filters.size() > 1) {
			sb.append(SQLConstants.OPEN_BRACKET);
		}
		for (int i = 0, n = filters.size(); i < n; i++) {
			if (i > 0) {
				sb.append(SQLConstants.SPACE);
			}
			sb.append(filters.get(i));
		}
		if (!isTopNode && filters.size() > 1) {
			sb.append(SQLConstants.CLOSE_BRACKET);
		}
		return new ParsedFilter(new String(sb), commonJoin, filter.isReversed());
	}

	private ParsedFilter parseExpression(QueryExpression expr)
			throws QueryParsingException {
		StringBuffer sb = new StringBuffer();

		String attr = expr.getAttribute();
		Operator op = expr.getOperator();

		parsingContext.setExpressionValueAsParam(expr);
		parsingContext.setExpressionDatabaseType(ExtendedSQLTypes.UNKNOWN);
		parsingContext.setExpressionPFName(null);

		OQLExpression oqlExpr = null;
        String leftColumn = null;
        if (op != Operator.EXISTS &&
            op != Operator.EXISTS.reverse())
        {
            if (attr == null)
            {
            	throw new QueryParsingException("The attribute of expression is null");
            }
            try {
            oqlExpr = QueryUtil.parseExpression(attr);
            leftColumn = QueryUtil.buildExpressionSql(oqlExpr,
                parsingContext, oqlExpr.isName(),
                attr, op, expr.getValue(), expr);
            } catch (OQLException e) {
            	throw new QueryParsingException("The attribute of expression is null", e, e.getMessage());
            }
            //If oqlExpr.isName(), we need the expression database type for parsing value.
            //Else we clear that value
            if (!oqlExpr.isName())
            {
                parsingContext.setExpressionDatabaseType(ExtendedSQLTypes.UNKNOWN);
                parsingContext.setExpressionPFName(null);
            }

            if (QueryUtil.isStringOperator(op) &&
                    parsingContext.getExpressionDatabaseType() != Types.VARCHAR)
            {
                parsingContext.setExpressionDatabaseType(Types.VARCHAR);
                //leftColumn = DBMSProviderFactory.getProvider().getToCharFunction(leftColumn);
            }
        }
        else
        {
            if (attr != null)
            {
                throw new QueryParsingException(
                    "The attribute of an 'exists' expression must be null");
            }
            if (!(expr.getValue() instanceof SearchQuery))
            {
                throw new QueryParsingException("The value of an 'exists' " +
                    "expression should be a sub query");
            }
        }
        
        Object rightColumn = null;

        //do some check
        if (op == null)
        {
            if (oqlExpr instanceof OQLBinaryExpression)
            {
                throw new QueryParsingException(attr,
                    "This expression's operator should not be null");
            }
            sb.append(leftColumn);
        }
        else
        {
            if (oqlExpr != null && !(oqlExpr instanceof OQLBinaryExpression))
            {
                throw new QueryParsingException(expr.getAttribute(),
                    "This expression's operator should be null");
            }
            try {
				rightColumn = QueryUtil.buildValueSql(expr, parsingContext);
			} catch (OQLException e) {
				throw new QueryParsingException(ExceptionConstants.EBOS_000, e, e.getMessage());
			}
            IExpressionHandler expHandler = ExpressionHandlers.getHandler(op);
            String expStr = expHandler.parse(leftColumn, rightColumn,
                op, expr.getValue(), parsingContext);
            sb.append(expStr);
            
            parsingContext.copyExpressionParams(expHandler.getParamCopyCount());
        }
        parsingContext.collectExpressionParams();
        return new ParsedFilter(new String(sb),
            parsingContext.getExpressionJoinClauses(), expr.isReversed());	
    }

	// returns String/List when isSelect is false/true
	private Object parseStringList(List<String> list, boolean isSelect)
			throws QueryParsingException {
		StringBuffer sb = isSelect ? (StringBuffer) null : new StringBuffer();
		List selectList = isSelect ? new ArrayList(list.size()) : (List) null;
		for (int i = 0, n = list.size(); i < n; i++) {
			if (!isSelect && i > 0) {
				sb.append(SQLConstants.COMMA); // ,
			}
			String s = (String) list.get(i);
			if (isSelect) {
				selectList.add(s);
			} else {
				sb.append(s);
			}
		}
		return isSelect ? (Object) selectList : (Object) new String(sb);
	}

	private void buildSql() throws QueryParsingException {
		List<TableInstance> ts = parsingContext.getTableInstances();
		buildFromClause(ts);

		// build startWith and GroupBy and Having Clause
		StringBuffer sb = new StringBuffer();
		if (startWithClause != null) {
			sb.append(SQLConstants.SPACE + //
					"START WITH" + // START WITH
					SQLConstants.SPACE); //
			sb.append(startWithClause);
		}
		if (connectByClause != null) {
			sb.append(connectByClause);
		}
		if (groupByClause != null) {
			sb.append(SQLConstants.SPACE + //
					SQLConstants.GROUP_BY + // group by
					SQLConstants.SPACE); //
			sb.append(groupByClause);
		}
		if (havingClause != null) {
			sb.append(SQLConstants.SPACE + //
					SQLConstants.HAVING + // having
					SQLConstants.SPACE); //
			sb.append(havingClause);
		}
		String startWithAndGroupByAndHavingClause = sb.length() == 0 ? null
				: new String(sb);

		IDBMSProvider provider = DBMSProviderFactory.getProvider();
		boolean isDistinct = query.isDistinct();
		boolean isObjectQuery = query.isObjectQuery();
		boolean singleFrom = (ts.size() == 1);
		boolean canOptimizeCount = (groupByClause == null)
				&& (selectClause.size() == 1);
		boolean canOptimizeRowNum = provider.canOptimizeRowNum()
				&& (groupByClause == null) && (orderByClause == null)
				&& !isDistinct && (!isObjectQuery || singleFrom);

		int[] columnIndex = new int[] { 0 };
		
		
		parsedQuery = new ParsedQuery(selectAndHintClause,
				QueryUtil.buildMultiClause(selectClause, SQLConstants.COMMA,
						null), QueryUtil.buildMultiClause(
						selectClauseFromOrder, SQLConstants.COMMA,
						selectClause, columnIndex), fromClause, joinClause, whereClause,
				startWithAndGroupByAndHavingClause, orderByClause, 
				singleFrom, isDistinct);
	}

	private void buildFromClause(List<TableInstance> ts) {
		fromClause = QueryUtil.buildFrom(ts);
	}

	private void buildConnectByClause() throws QueryParsingException {
		Map startWithMap = parsingContext.getStartWith();
		if (!startWithMap.isEmpty()) {
			startWithClause = buildStartWithClause(query.getFilter(),
					startWithMap);
		}

		connectByClause = parsingContext.getConnectBy();

		List startWithValueList = parsingContext.getStartWithValue();
		if (!startWithValueList.isEmpty()) {
			for (int j = 0, k = startWithValueList.size(); j < k; j++) {
				Object[] startWithValue = (Object[]) startWithValueList.get(j);
				for (int i = 0, n = startWithValue.length; i < n; i++) {
					String paramName = "_inp"
							+ parsingContext.getInternalParamGenerator()
									.getNewIndex();
					parsingContext.addExpressionParam(paramName);
					parsingContext.bindParam(paramName, DataParser
							.valueToBoundParam(startWithValue[i],
									ExtendedSQLTypes.UNKNOWN,
									parsingContext.getQueryDebugString()));
				}
			}
			parsingContext.collectExpressionParams();
		}
	}

	private String buildStartWithClause(QueryExpressionNodeList filter,
			Map startWithMap) {
		StringBuffer sb = new StringBuffer();
		boolean isFirst = true;
		boolean isAnd = true;
		for (Iterator<?> it = filter.list(); it.hasNext();) {
			Object item = it.next();
			if (item instanceof LogicalOperator) {
				if (!isFirst && item == LogicalOperator.OR) {
					isAnd = false;
				}
			} else {
				String startWith = null;
				if (item instanceof QueryExpression) {
					startWith = (String) startWithMap.get(new Integer(System
							.identityHashCode(item)));
				} else if (item instanceof QueryExpressionNodeList) {
					startWith = buildStartWithClause(
							(QueryExpressionNodeList) item, startWithMap);
					if (startWith != null) {
						startWith = "(" + startWith + ")";
					}
				}
				if (startWith != null) {
					append(sb, startWith, isFirst, isAnd);
					isFirst = false;
					isAnd = true;
				}
			}
		}
		return new String(sb);
	}

	private void append(StringBuffer sb, String startWith, boolean isFirst,
			boolean isAnd) {
		if (!isFirst) {
			sb.append(isAnd ? " AND " : " OR ");
		}
		sb.append(startWith);
	}

	public static String concatFilter(String s1, String s2) {
		if (s1 == null) {
			return s2;
		}
		if (s2 == null) {
			return s1;
		}
		return s1 + SQLConstants.SPACE + SQLConstants.AND
				+ SQLConstants.SPACE + s2;
	}

	private static ArrayList<String> getCommonJoin(List<ArrayList<?>> joins) {
		ArrayList<String> l = null;
		for (int i = 0, n = joins.size(); i < n; i++) {
			ArrayList<?> join = joins.get(i);
			if (join == null) {
				return null;
			}
			if (i == 0) {
				l = (ArrayList) join.clone();
			} else {
				l.retainAll(join);
			}
		}
		if (l != null && l.size() == 0) {
			return null;
		}
		for (int i = 0, n = joins.size(); i < n; i++) {
			ArrayList<?> join = joins.get(i);
			join.removeAll(l);
		}
		return l;
	}

	public class ParsedQuery implements Serializable
	{
		private static final long serialVersionUID = 1L;
		
		private String selectAndHintClause = null;
	    private String selectClause = null;
	    private String selectFromOrderClause = null;
	    private String fromClause = null;
	    private String joinClause = null;
	    private String conditionClause = null;
	    private String startWithAndGroupByAndHavingClause = null;
	    private String orderByClause = null;
	    
	    private boolean canOptimizeRowNum = false;
	    private boolean singleFrom = false;
	    private boolean isDistinct = false;
	    private boolean isObjectQuery = false;
	    
	    ParsedQuery(String selectAndHintClause, String selectClause, String selectFromOrderClause,
	            String fromClause, String joinClause, String conditionClause, String startWithAndGroupByAndHavingClause,
	            String orderByClause, boolean singleFrom, boolean isDistinct)
	    {
	        this.selectAndHintClause = selectAndHintClause;
	        this.selectClause = selectClause;
	        this.selectFromOrderClause = selectFromOrderClause;
	        this.fromClause = fromClause;
	        this.joinClause = joinClause;
	        this.conditionClause = conditionClause;
	        this.startWithAndGroupByAndHavingClause = startWithAndGroupByAndHavingClause;
	        this.orderByClause = orderByClause;
	        
	        this.singleFrom = singleFrom;
	        this.isDistinct = isDistinct;
	    }
	    
	    boolean needBindRequestedCount()
	    {
	        return !canOptimizeRowNum ||
	                startWithAndGroupByAndHavingClause == null;
	    }
	    
	    String buildCountSql() {
	    	StringBuffer sb = new StringBuffer(selectAndHintClause);
	        if (isDistinct || (isObjectQuery && !singleFrom))
	        {
	            sb.append("DISTINCT ");
	        }
	        sb.append("count(1)");
	        if (isObjectQuery && selectFromOrderClause != null)
	        {
	            sb.append(",");
	            sb.append(selectFromOrderClause);
	        }
	        
	        // build fromAndWhere clause
	        sb.append(SQLConstants.SPACE);
	        sb.append(SQLConstants.FROM);
	        sb.append(SQLConstants.SPACE); 
			sb.append(fromClause);
			if (query.getStringFrom() != null) {
				sb.append(SQLConstants.COMMA);
				sb.append(query.getStringFrom());
			}
			String where = concatFilter(joinClause, conditionClause);
			where = concatFilter(where, query.getStringFilter());
			where = concatFilter(where, parsingContext.getJoin()); 
			boolean hasWhereClause = (where != null);
			if (hasWhereClause) {
				// where
				sb.append(SQLConstants.SPACE); 
				sb.append(SQLConstants.WHERE);
				sb.append(SQLConstants.SPACE); 
				sb.append(where);
			}
			
	        if (startWithAndGroupByAndHavingClause != null)
	        {
	            sb.append(startWithAndGroupByAndHavingClause);
	        }
	        // no need order by in count SQL.
	        return new String(sb);
	    }
	    
	    String buildSql()
	    {
	        StringBuffer sb = new StringBuffer(selectAndHintClause);
	        if (isDistinct || (isObjectQuery && !singleFrom))
	        {
	            sb.append("DISTINCT ");
	        }
	        sb.append(selectClause);
	        if (isObjectQuery && selectFromOrderClause != null)
	        {
	            sb.append(",");
	            sb.append(selectFromOrderClause);
	        }
	        
	        // build fromAndWhere clause
	        sb.append(SQLConstants.SPACE);
	        sb.append(SQLConstants.FROM);
	        sb.append(SQLConstants.SPACE); 
			sb.append(fromClause);
			if (query.getStringFrom() != null) {
				sb.append(SQLConstants.COMMA);
				sb.append(query.getStringFrom());
			}
			String where = concatFilter(joinClause, conditionClause);
			where = concatFilter(where, query.getStringFilter());
			where = concatFilter(where, parsingContext.getJoin()); 
			boolean hasWhereClause = (where != null);
			if (hasWhereClause) {
				// where
				sb.append(SQLConstants.SPACE); 
				sb.append(SQLConstants.WHERE);
				sb.append(SQLConstants.SPACE); 
				sb.append(where);
			}
			
	        if (startWithAndGroupByAndHavingClause != null)
	        {
	            sb.append(startWithAndGroupByAndHavingClause);
	        }
	        if (orderByClause != null)
	        {
	            sb.append(orderByClause);
	        }
	        return new String(sb);
	    }
	    
	    public String toString()
	    {
	        return buildSql();
	    }
	}

	public class ParsedFilter {

		private ArrayList<String> commonJoin = null;
		private String sql = null;
		private boolean isReversed = false;

		public ParsedFilter(String sql, ArrayList<String> commonJoin, boolean isReversed) {
			this.commonJoin = commonJoin;
			this.sql = sql;
			this.isReversed = isReversed;
		}

		public ArrayList<String> getJoinList() {
			return commonJoin;
		}

		public String getSql() {
			return sql;
		}

		public boolean isReversed() {
			return isReversed;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();

			if (isReversed) {
				sb.append(SQLConstants.NOT);
			}
			if (isReversed || (commonJoin != null && commonJoin.size() > 0)) {
				sb.append(SQLConstants.OPEN_BRACKET);
			}

			if (commonJoin != null && commonJoin.size() > 0) {
				for (int i = 0, n = commonJoin.size(); i < n; i++) {
					if (i > 0) {
						sb.append(SQLConstants.SPACE);
						sb.append(SQLConstants.AND);
						sb.append(SQLConstants.SPACE);
					}
					sb.append(commonJoin.get(i));
				}
				sb.append(SQLConstants.SPACE);
				sb.append(SQLConstants.AND);
				sb.append(SQLConstants.SPACE);
			}
			sb.append(sql);

			if (isReversed || (commonJoin != null && commonJoin.size() > 0)) {
				sb.append(SQLConstants.CLOSE_BRACKET);
			}
			return new String(sb);
		}

	}
}
