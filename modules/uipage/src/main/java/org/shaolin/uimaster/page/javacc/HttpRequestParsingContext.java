package org.shaolin.uimaster.page.javacc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.runtime.be.BEUtil;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.exception.ParsingException;

public class HttpRequestParsingContext extends DefaultParsingContext {

	private static Logger logger = Logger
			.getLogger(HttpSessionParsingContext.class);

	public HttpRequestParsingContext() {
		super();
		initBuildInVariable();
	}

	public HttpRequestParsingContext(HashMap variableTypes) {
		super(variableTypes);
		initBuildInVariable();
	}

	private void initBuildInVariable() {
		super.setVariableClass("QuitActionFlag",
				BEUtil.getPrimitiveImplementClass(BEUtil.BOOLEAN));
	}

	public HttpServletRequest getRequest() {
		return null;
	}
	
	@Override
	public Method findMethod(String name, List argClasses)
			throws ParsingException {
		if (logger.isDebugEnabled())
			logger.debug("findMethod: " + name);
		try {
			if ("getRequest".equals(name)) {
				return HttpRequestParsingContext.class.getMethod("getRequest", new Class[0]);
			}
			
			List argClassList = new ArrayList();
			argClassList.add(HttpSessionEvaluationContext.class);
			argClassList.addAll(argClasses);

			return WebflowContextFunction.class.getMethod(name,
					(Class[]) argClassList.toArray(new Class[argClassList
							.size()]));
		} catch (NoSuchMethodException ex) {
			throw new ParsingException("plugin method " + name
					+ " not support in HttpSessionEvaluationContext", ex);
		}
	}
}
