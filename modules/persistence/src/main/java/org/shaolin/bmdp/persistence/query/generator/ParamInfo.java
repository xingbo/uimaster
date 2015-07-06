package org.shaolin.bmdp.persistence.query.generator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.sql.exception.QueryParsingException;

public class ParamInfo implements Serializable {
	
	private String paramName = null;
	private int paramType = 0;
	private List paramIndexList = new ArrayList();


	private static final long serialVersionUID = -8602940704779142055L;
	
	public ParamInfo(String paramName, int paramType) {
		this.paramName = paramName;
		this.paramType = paramType;
	}

	public int getParamType() {
		return paramType;
	}

	public List getIndexList() {
		return paramIndexList;
	}

	public void addParamIndex(int index) {
		paramIndexList.add(new Integer(index));
	}

	public void copy(ParamInfo info) throws QueryParsingException {
		if (paramType == ExtendedSQLTypes.UNKNOWN) {
			paramType = info.paramType;
		} else if (paramType != info.paramType
				&& info.paramType != ExtendedSQLTypes.UNKNOWN) {
			throw new QueryParsingException("Incampatiable param, type:{0}, param name:{1}", new Object[] {
							new Integer(paramType), paramName });
		}

		paramIndexList.addAll(info.paramIndexList);
	}

}
