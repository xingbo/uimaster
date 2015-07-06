package org.shaolin.bmdp.persistence.query.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.shaolin.bmdp.datamodel.common.VariableCategoryType;
import org.shaolin.bmdp.persistence.JoinTableInstance;
import org.shaolin.bmdp.persistence.PersistentUtil;
import org.shaolin.bmdp.runtime.be.BEUtil;
import org.shaolin.javacc.sql.SQLConstants;
import org.shaolin.javacc.sql.exception.QueryParsingException;

public class QueryParsingContext {
	private Map aliasToTypeInfo = new HashMap();

	private List<TableInstance> fromTableList = new ArrayList<TableInstance>();
	private List<TableInstance> tableInstances = null;
	private List<String> joinClauses = null;
	private Map params = null;
	private Map<String, BoundParam> boundParams = null;
	private boolean valueAsParamConfiged = false;
	private boolean valueAsParam = false;
	private boolean exprValueAsParam = false;
	private int exprParamType = ExtendedSQLTypes.UNKNOWN;
	// only affect value expression
	private String exprPFName = null;

	private Map exprParams = null;
	private ArrayList<String> exprJoinClauses = null;

	private AliasGenerator aliasGenerator = null;
	private AliasGenerator paramIndexGenerator = null;
	private AliasGenerator internalParamGenerator = null;

	private QueryParsingContext superContext = null;

	private Class resolvedClass = null;

	private boolean encodedFieldDetected = false;

	private Map startWithMap = new HashMap();
	private String join = null;
	private String connectBy = null;
	private List startValue = new ArrayList();

	private String queryDebugString = null;

	public QueryParsingContext(AliasGenerator aliasGenerator,
			AliasGenerator paramIndexGenerator,
			AliasGenerator internalParamGenerator, Map params, Map<String, BoundParam> boundParams,
			boolean valueAsParamConfiged, boolean valueAsParam) {
		this.aliasGenerator = aliasGenerator;
		this.paramIndexGenerator = paramIndexGenerator;
		this.internalParamGenerator = internalParamGenerator;
		this.params = params;
		this.boundParams = boundParams;
		this.valueAsParamConfiged = valueAsParamConfiged;
		this.valueAsParam = valueAsParam;
	}

	public void setSuperParsingContext(QueryParsingContext superContext) {
		this.superContext = superContext;
	}

	public Map getParams() {
		return params;
	}

	public AliasGenerator getAliasGenerator() {
		return aliasGenerator;
	}

	public AliasGenerator getParamIndexGenerator() {
		return paramIndexGenerator;
	}

	public AliasGenerator getInternalParamGenerator() {
		return internalParamGenerator;
	}

	// initial methods
	public void parseFrom(List fromList) throws QueryParsingException {
		if (fromList == null || fromList.size() == 0) {
			 throw new QueryParsingException(
			 "Invalid query: Source of the query is not specified.");
		}
		for (int i = 0, n = fromList.size(); i < n; i++) {
			String from = (String) fromList.get(i);
			parseSource(from);
		}
	}

	public void parseFrom(String from) throws QueryParsingException {
		parseSource(from);
	}

	private void parseSource(String from) throws QueryParsingException {
		StringTokenizer st = new StringTokenizer(from);
		int tokenCount = st.countTokens();
		String category =  st.nextToken();
		String beName = null;
		String alias = null;
		switch (tokenCount) {
		case 2:
			beName = st.nextToken();
			break;
		case 4:
			beName = st.nextToken();
			String as = st.nextToken();
			if (!SQLConstants.AS.equalsIgnoreCase(as)) {
				throw new QueryParsingException("Invalid source:{0}",
						new Object[] { from });
			}
			alias = st.nextToken();
			break;
		default:
			throw new QueryParsingException("Invalid source:{0}", new Object[] { from });
		}

		if (category.equals(VariableCategoryType.JOIN_TABLE.toString())) {
			TableInstance tableInstance = new TableInstance(beName, alias, aliasGenerator, this);
			fromTableList.add(tableInstance);
			// build type info
			TypeInfo thisTypeInfo = new TypeInfo(beName, JoinTableInstance.class.getName(), 
					JoinTableInstance.class, tableInstance, false, null);
			TypeInfo typeInfo = findTypeInfo(beName);
			if (typeInfo != null) {
				typeInfo.setUnconflict(false);
				thisTypeInfo.setUnconflict(false);
			}
			aliasToTypeInfo.put(alias, thisTypeInfo);
			return;
		}
		
		// parse collection field
		String typeName = beName;
		String colFieldName = null;
		int index = beName.indexOf(SQLConstants.COLON);
		if (index != -1) {
			colFieldName = beName.substring(index + 1);
			beName = beName.substring(0, index);
		}

		// parse customer rdb
		String custRDBName = null;
		if (beName.endsWith("]")) {
			index = beName.indexOf("[");
			if (index == -1) {
				throw new QueryParsingException("Invalid source:{0}",
						new Object[] { from });
			}
			custRDBName = beName.substring(index + 1, beName.length() - 1);
			beName = beName.substring(0, index);
		}

		// resolve table name
		String tableName = custRDBName;
		String colFieldDesc = null;
		Class beClass;
		try {
			beClass = BEUtil.getBEImplementClass(beName);
		} catch (ClassNotFoundException e) {
			throw new QueryParsingException(e.getMessage());
		}
		tableName = PersistentUtil.getTableName(beName);
		if (tableName == null) {
			throw new QueryParsingException("CustomerRDB {0} not found in {1}", new Object[] {
							custRDBName, beClass.getName() });
		}
		
		// build table instance
		TableInstance tableInstance = null;
		boolean hasAlias = false;
		if (alias == null) {
			tableInstance = new TableInstance(tableName, aliasGenerator, this);
			alias = tableInstance.getAlias();
		} else {
			tableInstance = new TableInstance(tableName, alias, aliasGenerator,
					this);
			hasAlias = true;
		}
//		if (colFieldDesc != null) {
//			tableInstance.setCollectionFieldDescriptor(colFieldDesc);
//		}
		fromTableList.add(tableInstance);

		// build type info
		TypeInfo typeInfo = findTypeInfo(typeName);
		if (typeInfo != null && (!typeInfo.hasAlias() || !hasAlias)) {
			throw new QueryParsingException("Ambiguous type :{0}",
					new Object[] { typeName });
		}
		TypeInfo thisTypeInfo = new TypeInfo(typeName, beName, beClass,
				tableInstance, hasAlias, colFieldName);
		if (typeInfo != null) {
			typeInfo.setUnconflict(false);
			thisTypeInfo.setUnconflict(false);
		}
		aliasToTypeInfo.put(alias, thisTypeInfo);
	}

	private TypeInfo findTypeInfo(String typeName) {
		for (Iterator it = aliasToTypeInfo.values().iterator(); it.hasNext();) {
			TypeInfo typeInfo = (TypeInfo) it.next();
			if (typeInfo.getTypeName().equals(typeName)) {
				return typeInfo;
			}
		}
		return null;
	}

	// get initial data
	public List getFrom() {
		return fromTableList;
	}

	public Class getClassByAlias(String alias) throws QueryParsingException {
		TypeInfo typeInfo = (TypeInfo) aliasToTypeInfo.get(alias);
		if (typeInfo != null) {
			return typeInfo.getClazz();
		}
		throw new QueryParsingException("Alias{0} not found",
				new Object[] { alias });
	}

	// operations
	public boolean addTableInstance(TableInstance tableInstance) {
		if (tableInstances == null) {
			tableInstances = new ArrayList<TableInstance>();
		} else if (tableInstances.contains(tableInstance)) {
			return false;
		}
		tableInstances.add(tableInstance);
		return true;
	}

	public void addJoinClauses(List<String> clauses) {
		if (clauses != null) {
			if (joinClauses == null) {
				joinClauses = new ArrayList<String>();
			}
			for (String clause: clauses) {
				if (!joinClauses.contains(clause)) {
					joinClauses.add(clause);
				}
			}
		}
	}

	public void addExpressionJoinClause(String clause) {
		if (exprJoinClauses == null) {
			exprJoinClauses = new ArrayList<String>();
		}
		if (!exprJoinClauses.contains(clause)) {
			exprJoinClauses.add(clause);
		}
	}

	public ArrayList getExpressionJoinClauses() {
		ArrayList l = exprJoinClauses;
		exprJoinClauses = null;
		return l;
	}

	public void addExpressionParam(String paramName) {
		ParamInfo paramInfo = null;
		if (exprParams == null) {
			exprParams = new HashMap();
		} else {
			paramInfo = (ParamInfo) exprParams.get(paramName);
		}
		if (paramInfo == null) {
			paramInfo = new ParamInfo(paramName, exprParamType);
			exprParams.put(paramName, paramInfo);
		}
		paramInfo.addParamIndex(paramIndexGenerator.getNewIndex());
	}

	public void addExpressionParams(Map paramMap) throws QueryParsingException {
		for (Iterator it = paramMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String paramName = (String) entry.getKey();
			ParamInfo paramInfo = (ParamInfo) entry.getValue();
			ParamInfo info = null;
			if (exprParams == null) {
				exprParams = new HashMap();
			} else {
				info = (ParamInfo) exprParams.get(paramName);
			}
			if (info == null) {
				exprParams.put(paramName, paramInfo);
			} else {
				info.copy(paramInfo);
			}
		}
	}

	public void bindParam(String paramName, BoundParam param) {
		if (boundParams == null) {
			boundParams = new HashMap();
		}
		boundParams.put(paramName, param);
	}

	public void addBoundParams(Map paramMap) {
		if (paramMap != null) {
			if (boundParams == null) {
				boundParams = new HashMap();
			}
			boundParams.putAll(paramMap);
		}
	}

	public void collectExpressionParams() throws QueryParsingException {
		if (exprParams != null) {
			for (Iterator it = exprParams.keySet().iterator(); it.hasNext();) {
				String paramName = (String) it.next();
				ParamInfo paramInfo = (ParamInfo) exprParams.get(paramName);
				ParamInfo info = (ParamInfo) params.get(paramName);
				if (info == null) {
					params.put(paramName, paramInfo);
				} else {
					info.copy(paramInfo);
				}
			}
			exprParams = null;
		}
		exprParamType = ExtendedSQLTypes.UNKNOWN;
	}

	public void copyExpressionParams(int count) {
		if (exprParams != null && count > 0) {
			int paramCount = 0;
			// calculate param count first
			for (Iterator it = exprParams.values().iterator(); it.hasNext();) {
				ParamInfo info = (ParamInfo) it.next();
				paramCount += info.getIndexList().size();
			}

			for (Iterator it = exprParams.values().iterator(); it.hasNext();) {
				ParamInfo info = (ParamInfo) it.next();
				List l = (List) ((ArrayList) info.getIndexList()).clone();
				for (int i = 0, n = l.size(); i < n; i++) {
					int index = ((Integer) l.get(i)).intValue();
					for (int j = 1; j <= count; j++) {
						info.addParamIndex(index + j * paramCount);
					}
				}
			}
			paramIndexGenerator.increase(paramCount * count);
		}
	}

	public void setExpressionValueAsParam(QueryExpression expr) {
		if (valueAsParamConfiged) {
			exprValueAsParam = valueAsParam;
		} else {
			exprValueAsParam = expr.getValueAsParam();
		}
	}

	public boolean getExpressionValueAsParam() {
		return exprValueAsParam;
	}

	public void setExpressionPFName(String pfName) {
		exprPFName = pfName;
	}

	public String getExpressionPFName() {
		return exprPFName;
	}

	public void setExpressionParamType(int exprParamType) {
		this.exprParamType = exprParamType;
	}

	public int getExpressionParamType() {
		return exprParamType;
	}

	// results
	public List<TableInstance> getTableInstances() {
		List<TableInstance> l = new ArrayList<TableInstance>();
		l.addAll(fromTableList);
		if (tableInstances != null) {
			l.addAll(tableInstances);
		}
		return l;
	}

	public List<String> getJoinClauses() {
		return joinClauses;
	}

	public TableInstance resolveByTypeName(String typeName)
			throws QueryParsingException {
		TableInstance tblInstance = resolveByAlias(typeName);
		if (tblInstance != null) {
			return tblInstance;
		}
		return resolveByBEName(typeName);
	}

	public TableInstance resolveByFieldName(String fieldName)
			throws QueryParsingException {
		resolvedClass = null;
		String resolvedAlias = null;

		for (Iterator it = aliasToTypeInfo.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String alias = (String) entry.getKey();
			TypeInfo typeInfo = (TypeInfo) entry.getValue();
			if (typeInfo.getColFieldName() != null) {
				continue;
			}
		}
		if (superContext != null) {
			TableInstance tblInstance = superContext
					.resolveByFieldName(fieldName);
			resolvedClass = superContext.getResolvedClass();
			return tblInstance;
		}
		throw new QueryParsingException("field {0} not found",
				new Object[] { fieldName });
	}

	public Class getResolvedClass() {
		return resolvedClass;
	}

	public void reset() {
		tableInstances = null;
		joinClauses = null;
		exprDatabaseType = ExtendedSQLTypes.UNKNOWN;
	}

	public TableInstance resolveByAlias(String alias) {
		resolvedClass = null;
		TypeInfo typeInfo = (TypeInfo) aliasToTypeInfo.get(alias);
		if (typeInfo != null) {
			resolvedClass = typeInfo.getClazz();
			return typeInfo.getTableInstance();
		}

		TableInstance tblInstance = null;
		if (superContext != null) {
			tblInstance = superContext.resolveByAlias(alias);
			resolvedClass = superContext.getResolvedClass();
		}
		return tblInstance;
	}

	public TableInstance resolveByBEName(String beName)
			throws QueryParsingException {
		TypeInfo typeInfo = findTypeInfo(beName);
		if (typeInfo != null) {
			if (typeInfo.isUnconflict()) {
				resolvedClass = typeInfo.getClazz();
				return typeInfo.getTableInstance();
			}
			throw new QueryParsingException("{0}is an ambiguous type reference",
					new Object[] { beName });
		}
		if (superContext != null) {
			TableInstance tableInstance = superContext.resolveByBEName(beName);
			resolvedClass = superContext.getResolvedClass();
			return tableInstance;
		}
		throw new QueryParsingException("type {0} not found",
				new Object[] { beName });
	}

	public boolean getEncodedFieldDetected() {
		boolean tmpFlag = encodedFieldDetected;
		encodedFieldDetected = false;
		return tmpFlag;
	}

	public void setEncodedFieldDetected(boolean flag) {
		encodedFieldDetected = flag;
	}

	public void addStartWith(QueryExpression expr, String startWith)
			throws QueryParsingException {
		this.startWithMap.put(new Integer(System.identityHashCode(expr)),
				startWith);
	}

	public Map getStartWith() {
		return startWithMap;
	}

	public void setJoin(String join) throws QueryParsingException {
		if (this.join != null && !this.join.equals(join)) {
			throw new QueryParsingException("Join is not null:{0}",
					new Object[] { this.join });
		}
		this.join = join;
	}

	public String getJoin() {
		return join;
	}

	public void setConnectBy(String connectBy) throws QueryParsingException {
		if (this.connectBy != null && !this.connectBy.equals(connectBy)) {
			throw new QueryParsingException("ConnectBy is not null:{0}",
					new Object[] { this.connectBy });
		}
		this.connectBy = connectBy;
	}

	public String getConnectBy() {
		return connectBy;
	}

	public void addStartWithValue(Object[] value) throws QueryParsingException {
		this.startValue.add(value);
	}

	public List getStartWithValue() {
		return startValue;
	}

	public String getQueryDebugString() {
		return queryDebugString;
	}

	public void setQueryDebugString(String queryDebugString) {
		this.queryDebugString = queryDebugString;
	}
	
	private int exprDatabaseType = ExtendedSQLTypes.UNKNOWN;    //result of parsing expression
	
	public void setExpressionDatabaseType(int databaseType)
    {
        exprDatabaseType = databaseType;
    }

    public int getExpressionDatabaseType()
    {
        return exprDatabaseType;
    }

}
