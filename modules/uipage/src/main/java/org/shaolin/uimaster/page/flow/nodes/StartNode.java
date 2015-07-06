package org.shaolin.uimaster.page.flow.nodes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.shaolin.bmdp.datamodel.pagediagram.LogicNodeType;
import org.shaolin.uimaster.page.exception.WebFlowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartNode extends LogicNode {

	private static Logger logger = LoggerFactory.getLogger(StartNode.class);
	
	public StartNode(LogicNodeType type) {
		super(type);
	}

	public WebNode execute(HttpServletRequest request,
			HttpServletResponse response) throws WebFlowException {
		if (logger.isInfoEnabled())
			logger.info("execute() start node {}", toString());

		return super.execute(request, response);
	}

}
