package org.shaolin.bmdp.persistence.query.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.exceptions.I18NRuntimeException;
import org.shaolin.bmdp.persistence.query.generator.QueryParser.ParsedQuery;
import org.shaolin.javacc.sql.exception.QueryParsingException;

/**
 * An object query frame work.
 * <p>
 * A query contains six important components
 * 
 * <UL type=SQUARE>
 * <LI>what sort of result is needed (selection)
 * <LI>where to select (source)
 * <LI>subset to select (where filter)
 * <LI>how to group(grouping)
 * <LI>sub group to select (having)
 * <LI>how to present the result (presentation)
 * </UL>
 * <p>
 * The source is specified by passing it as the argument to constructor.
 * Currently this framework supports two kinds of data sources.
 * 
 * <UL>
 * <LI>object type
 * <LI>native database views or tables
 * </UL>
 * 
 * Note the usage of database views or tables in search is discouraged as
 * application using such queries are not guaranteed to work with schema
 * changes.
 * <p>
 * The selection is typically instances of the source specified. If the source
 * is an object type, it scans the instances for the object type.
 * <p>
 * Report generator like applications that demands non object expressions as
 * selection need to specify their selection by <CODE>#setSelection()</code>
 * calls. You can select any database expressions as argument here.
 * <p>
 * Result filtering is done by specifying an <CODE>#ExpressionList</CODE>. An
 * ExpressionList can contain list of <CODE>Expression</CODE> or one or more of
 * <CODE>ExpressionList</CODE> items. ie, SubQueries are handled by means of
 * ExpressionList containing other ExpressionList items.
 * <p>
 * Result grouping is done by <code>#addGrouping()</code>
 * <p>
 * Group filtering is done by specifying an <CODE>#ExpressionList</CODE> as a
 * 'having filter'. see <code>#setHaving()</code>
 * <p>
 * Finally the result can be presented in an ordered manner by specifying
 * ascending or descending values of the expression specified.
 * 
 */
public final class SearchQuery {
	
	private static final Logger logger = Logger
			.getLogger("bmiasia.ebos.ormapper.search.SearchQuery");
	
	private String targetType = null;
	private String sql = null;
	private List selectList = null;
	private List hintList = null;
	private boolean distinct = false;
	private List<String> fromList = null;
	private String stringFrom = null;
	private QueryExpressionNodeList conditionFilter = null;
	private QueryExpressionNodeList joinFilter = null;
	private String stringFilter = null;
	private List<String> groupBy = null;
	private QueryExpressionNodeList havingFilter = null;
	private List<Ordering> orderBy = null;
	private boolean isObjectQuery = false;
	private boolean isResultSet = false;
	private boolean isDirectSql = false;
	private boolean needCount = false;
	private int offset = 0;
	private int count = -1;
	private int timeoutSeconds = -1;

	private boolean parsed = false;
	// parsed result
	private ParsedQuery parsedQuery = null;
	private Map parsedParams = null;
	private Map parsedBoundParams = null;

	private List targetMemberList = null;
	
	private QueryParser parser = new QueryParser(this);

	/**
	 * Construct a search query against a specific data type and returns object
	 * results.
	 * 
	 * @param anInstanceType
	 *            the data type to scan.
	 */
	public SearchQuery(String beName, boolean isObjectQuery, boolean isResultSet) {
		if (isObjectQuery) {
			this.targetType = beName;
			this.isResultSet = isResultSet;
		}
		fromList = new ArrayList<String>();
		fromList.add(beName);
		this.isObjectQuery = isObjectQuery;
	}

	/**
	 * Construct a search query against a specific data type.
	 * 
	 * @param anInstanceType
	 *            the data type to scan.
	 * @param anIsObject
	 *            <code>true</code> if object results are needed. If
	 *            <CODE>false</CODE>, the query result set is returned as
	 *            <CODE>ResultSet</CODE> rather than objects.
	 */
	public SearchQuery(String beName, String sql, boolean isResultSet) {
		this.targetType = beName;
		isObjectQuery = true;
		isResultSet = true;
		this.sql = sql;
		isDirectSql = true;
	}

	/**
	 * Construct a search query against a specific database view and returns
	 * <CODE>ResultSet</CODE> rather than objects.
	 * 
	 * @param aDatabaseView
	 *            the database view to scan.
	 */
	public SearchQuery(String sql) {
		this.sql = sql;
		isDirectSql = true;
	}

	public void addFrom(String beName) {
		if (fromList == null) {
			fromList = new ArrayList<String>();
		}
		fromList.add(beName);
	}

	public List<String> getAllFrom() {
		return fromList;
	}

	public void removeAllFrom() {
		fromList.clear();
	}

	/**
	 * To select partially fetched object instances.
	 * <P>
	 * This option is provided for applications like report generator that needs
	 * to pull out column values from different tables in single query.
	 * <P>
	 * Items in the select list can be field names described in Expression
	 * <P>
	 * example.
	 * 
	 * // select just the name and list price of the product <BR>
	 * ArrayList aList = new ArrayList(); <BR>
	 * aList.add("name"); <BR>
	 * aList.add("listPrice"); <BR>
	 * aQuery.setSelection(aList);
	 * 
	 * <P>
	 * <B>NOTE:</B> if the source is an object type, search engine will parse
	 * the elements in the list to provide ORMapping; if the source is a
	 * database view, no parsing will be performed.
	 * 
	 * @param aSelectList
	 *            the list of fields to be fetched for this object.
	 */
	public void setSelection(List aSelectList) {
		this.selectList = aSelectList;
	}

	/**
	 * To select partially fetched object instances.
	 * 
	 * @param aSelect
	 *            the field to select for this query
	 */
	public void setSelection(String aSelect) {
		setSelection(Collections.singletonList(aSelect));
	}

	/**
	 * Set whether unique query results are requested
	 * 
	 * @param anIsDistinct
	 *            <code>true</code> unique, <code>false</code> otherwise
	 */
	public void setDistinct(boolean anIsDistinct) {
		distinct = anIsDistinct;
	}

	/**
	 * Set whether query result count are needed
	 * 
	 * @param aNeedCount
	 *            <code>true</code> need count, <code>false</code> otherwise
	 */
	public void setNeedCount(boolean aNeedCount) {
		needCount = aNeedCount;
	}

	public void setOffset(int anOffset) {
		if (anOffset < 0) {
			throw new I18NRuntimeException("offset is less than zero");
		}
		offset = anOffset;
	}

	public void setCount(int aCount) {
		if (aCount < -1) {
			throw new I18NRuntimeException("count is less than -1");
		}
		count = aCount;
	}

	/**
	 * Add a join filter.
	 * 
	 * @param aRootNode
	 *            the expression node list containing the search filter.
	 */
	public void setJoinFilter(QueryExpressionNodeList aRootNode) {
		joinFilter = aRootNode;
	}

	
	/**
	 * Add a search filter.
	 * 
	 * @param aRootNode
	 *            the expression node list containing the search filter.
	 */
	public void setFilter(QueryExpressionNodeList aRootNode) {
		conditionFilter = aRootNode;
	}

	/**
	 * Add a search filter.
	 * 
	 * @param anExpression
	 *            the expression containing the search filter.
	 */
	public void setFilter(QueryExpression anExpression) {
		setFilter(new QueryExpressionNodeList(anExpression));
	}

	/**
	 * Add an group by clause.
	 * <p>
	 * group by expression can be field names described in Expression
	 * <p>
	 * Group by expressions are executed in the order they are added to the
	 * query.
	 * 
	 * @param aGroupBy
	 *            a group by.
	 */
	public void addGrouping(String aGroupBy) {
		if (null == groupBy) {
			groupBy = new ArrayList<String>();
		}
		groupBy.add(aGroupBy);
	}

	/**
	 * Remove all added grouping for this query.
	 */
	public void removeAllGrouping() {
		if (null != groupBy) {
			groupBy.clear();
		}
	}

	/**
	 * Add a having filter.
	 * 
	 * @param aRootNode
	 *            the expression node list containing the having filter.
	 */
	public void setHaving(QueryExpressionNodeList aRootNode) {
		havingFilter = aRootNode;
	}

	/**
	 * Add a having filter.
	 * 
	 * @param anExpression
	 *            the expression containing the having filter.
	 */
	public void setHaving(QueryExpression anExpression) {
		setHaving(new QueryExpressionNodeList(anExpression));
	}

	/**
	 * Add an order by clause.
	 * <p>
	 * order by expression can be field names described in Expression
	 * <p>
	 * Order by expressions are executed in the order they are added to the
	 * query.
	 * 
	 * @param anOrderBy
	 *            an ordering.
	 */
	public void addOrdering(Ordering anOrderBy) {
		if (null == orderBy) {
			orderBy = new ArrayList<Ordering>();
		}
		orderBy.add(anOrderBy);
	}

	/**
	 * Whether contains an ordering.
	 * 
	 * @param orderById
	 * @return <code>true</code> if true, the list contains ordering of orderBy
	 *         id.
	 */
	public boolean containsOrdering(String orderById) {
		if (null == orderBy || orderBy.isEmpty()) {
			return false;
		}

		for (int i = 0; i < orderBy.size(); i++) {
			Ordering ordering = (Ordering) orderBy.get(i);
			if (ordering.getExpression().equals(orderById)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * if orderById was not exist in order list, return null.
	 * 
	 * @param orderById
	 * @return return Ordering object.
	 */
	public Ordering getOrdering(String orderById) {
		if (null != orderBy && !orderBy.isEmpty()) {
			for (int i = 0; i < orderBy.size(); i++) {
				Ordering ordering = (Ordering) orderBy.get(i);
				if (ordering.getExpression().equals(orderById)) {
					return ordering;
				}
			}
		}
		return null;
	}

	/**
	 * Remove all added ordering for this query.
	 */
	public void removeAllOrdering() {
		if (null != orderBy) {
			orderBy.clear();
		}
	}

	/**
	 * Add from clause to this query.
	 * 
	 * @param from
	 *            string from clause
	 */
	public void addStringFrom(String from) {
		stringFrom = from;
	}

	public void removeStringFrom() {
		stringFrom = null;
	}

	/**
	 * Add filter to this query.
	 * 
	 * @param filter
	 *            string filter
	 */
	public void addStringFilter(String filter) {
		stringFilter = filter;
	}

	public void removeStringFilter() {
		stringFilter = null;
	}

	public void setHint(String hint) {
		setHint(Collections.singletonList(hint));
	}

	public void setHint(List hintList) {
		this.hintList = hintList;
	}

	public void setTimeoutSeconds(int timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public void setTargetMemberList(List targetMemberList) {
		this.targetMemberList = targetMemberList;
	}

	public void parse() throws QueryParsingException {
		if (parsed) {
			return;
		}
		parsed = true;
		
		parser.parse();
		// store result
		parsedQuery = parser.getParsedQuery();
		parsedParams = parser.getParams();
		parsedBoundParams = parser.getBoundParams();
	}

	public QueryParser getParser() {
		return parser;
	}
	
	public void setParser(QueryParser parser) {
		this.parser = parser;
	}
	
	/**
	 * Returns whether this query is an object query.
	 * 
	 * @return <code>true</code> if this query returns object results,
	 *         <code>false</code> if this query returns java.sql.Result
	 */
	public boolean isObjectQuery() {
		return isObjectQuery;
	}

	public boolean isResultSet() {
		return isResultSet;
	}

	boolean isDirectSql() {
		return isDirectSql;
	}

	String getTargetType() {
		return targetType;
	}

	public String getSql() {
		return parser.getSql();
	}

	public String getCountSql() {
		return parser.getCountSql();
	}

	boolean isDistinct() {
		return distinct;
	}

	boolean isNeedCount() {
		return needCount;
	}

	int getOffset() {
		return offset;
	}

	int getCount() {
		return count;
	}

	public List getSelection() {
		return selectList;
	}

	List getHint() {
		return hintList;
	}

	List<String> getFrom() {
		return fromList;
	}

	public QueryExpressionNodeList getFilter() {
		return conditionFilter;
	}
	
	public QueryExpressionNodeList getJoinFilter() {
		return joinFilter;
	}

	List<String> getGroupBy() {
		return groupBy;
	}

	QueryExpressionNodeList getHaving() {
		return havingFilter;
	}

	List<Ordering> getOrderBy() {
		return orderBy;
	}

	String getStringFrom() {
		return stringFrom;
	}

	String getStringFilter() {
		return stringFilter;
	}

	int getTimeoutSeconds() {
		return timeoutSeconds;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("targetType:" + targetType + "\n");
		buf.append("targetMemberList:" + targetMemberList + "\n");
		buf.append("sql:" + sql + "\n");
		buf.append("selectList:" + selectList + "\n");
		buf.append("hintList:" + hintList + "\n");
		buf.append("distinct:" + distinct + "\n");
		buf.append("fromList:" + fromList + "\n");
		buf.append("stringFrom:" + stringFrom + "\n");
		buf.append("stringFilter:" + stringFilter + "\n");
		buf.append("groupBy:" + groupBy + "\n");
		buf.append("orderBy:" + orderBy + "\n");
		buf.append("isObjectQuery:" + isObjectQuery + "\n");
		buf.append("isDirectSql:" + isDirectSql + "\n");
		buf.append("needCount:" + needCount + "\n");
		buf.append("offset:" + offset + "\n");
		buf.append("count:" + count + "\n");
		buf.append("timeoutSeconds:" + timeoutSeconds + "\n");
		buf.append("parsed:" + parsed + "\n");
		buf.append("parsedQuery:" + parsedQuery + "\n");
		buf.append("parsedParams:" + parsedParams + "\n");
		buf.append("parsedBoundParams:" + parsedBoundParams + "\n");
		buf.append("havingFilter:" + havingFilter + "\n");
		buf.append("join filter:" + joinFilter + "\n");
		buf.append("condition filter:" + conditionFilter + "\n");

		return buf.toString();
	}

}
