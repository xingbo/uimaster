package org.shaolin.bmdp.workflow.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.common.ParamScopeType;
import org.shaolin.bmdp.datamodel.common.ParamType;
import org.shaolin.bmdp.datamodel.common.TargetEntityType;
import org.shaolin.bmdp.datamodel.common.VariableCategoryType;
import org.shaolin.bmdp.datamodel.workflow.ConditionNodeType;
import org.shaolin.bmdp.datamodel.workflow.ConfType;
import org.shaolin.bmdp.datamodel.workflow.DestType;
import org.shaolin.bmdp.datamodel.workflow.DestWithFilterType;
import org.shaolin.bmdp.datamodel.workflow.EndNodeType;
import org.shaolin.bmdp.datamodel.workflow.FlowConfType;
import org.shaolin.bmdp.datamodel.workflow.FlowType;
import org.shaolin.bmdp.datamodel.workflow.GeneralNodeType;
import org.shaolin.bmdp.datamodel.workflow.HandlerType;
import org.shaolin.bmdp.datamodel.workflow.SessionServiceType;
import org.shaolin.bmdp.datamodel.workflow.StartNodeType;
import org.shaolin.bmdp.datamodel.workflow.Workflow;
import org.shaolin.bmdp.runtime.entity.EntityUtil;
import org.shaolin.bmdp.utils.CloseUtil;
import org.shaolin.javacc.statement.BlockCompilationUnit;

public class GeneratorFlowTest {

	@Test
	public void testGenOneFlow() {
		Workflow entity = new Workflow();
		entity.setEntityName("testFlow");
		
		ConfType config = new ConfType();
		config.setBootable(true);
		entity.setConf(config);
		
		SessionServiceType session = new SessionServiceType();
		session.setName("defaultSession");
		config.setSessionService(session);
		
		FlowType flow = new FlowType();
		flow.setName("flow1");
		flow.setEventConsumer("org.shaolin.vogerp.herewego.page.Main.loginSuccess");
		entity.getFlows().add(flow);
		
		FlowConfType flowConfig = new FlowConfType();
		ParamType var1 = new ParamType();
		var1.setScope(ParamScopeType.IN_OUT);
		var1.setCategory(VariableCategoryType.BUSINESS_ENTITY);
		var1.setName("beObject");
		var1.setType(new TargetEntityType());
		var1.getType().setEntityName("org.shaolin.test.BE");
		flowConfig.getParams().add(var1);
		flow.setConf(flowConfig);
		
		StartNodeType start = new StartNodeType();
		start.setName("initSession");
		start.setFilter(new DestWithFilterType());
		start.getFilter().setName("filter1");
		start.getFilter().setExpression(new ExpressionType());
		start.getFilter().getExpression().setExpressionString("\"NodeTest\".equals(event.getAttribute(\"NodeName\"))");
		HandlerType handler = new HandlerType();
		handler.setExpression(new ExpressionType());
		handler.getExpression().setExpressionString("System.out.println(\"initial the workflow user data session in start node.\");");
		start.setProcess(handler);
		DestType dest = new DestType();
		dest.setName("logicNode1");
		start.setDest(dest);
		flow.getNodesAndConditionsAndSplits().add(start);
		
		GeneralNodeType logicNode = new GeneralNodeType();
		logicNode.setName("logicNode1");
		HandlerType handler1 = new HandlerType();
		handler1.setExpression(new ExpressionType());
		handler1.getExpression().setExpressionString("System.out.println(\"this is logic node.\");");
		ParamType var2 = new ParamType();
		var2.setScope(ParamScopeType.IN_OUT);
		var2.setCategory(VariableCategoryType.BUSINESS_ENTITY);
		var2.setName("beObject");
		var2.setType(new TargetEntityType());
		var2.getType().setEntityName("org.shaolin.test.BE");
		handler1.getVars().add(var2);
		
		logicNode.setProcess(handler1);
		DestType dest1 = new DestType();
		dest1.setName("conditionNode1");
		dest1.setNode(new BlockCompilationUnit(null,null,null,null));
		logicNode.setDest(dest1);
		flow.getNodesAndConditionsAndSplits().add(logicNode);
		
		ConditionNodeType conditionNode = new ConditionNodeType();
		conditionNode.setName("conditionNode1");
		HandlerType handler2 = new HandlerType();
		handler2.setExpression(new ExpressionType());
		handler2.getExpression().setExpressionString("System.out.println(\"this is logic node.\");");
		conditionNode.setProcess(handler2);
		
		DestWithFilterType conditionDest1 = new DestWithFilterType();
		conditionDest1.setName("conditionDest1");
		conditionDest1.setExpression(new ExpressionType());
		conditionDest1.getExpression().setExpressionString("return beObject != null;");
		conditionNode.getDests().add(conditionDest1);
		
		DestWithFilterType conditionDest2 = new DestWithFilterType();
		conditionDest2.setName("endNode");
		conditionDest2.setExpression(new ExpressionType());
		conditionDest2.getExpression().setExpressionString("return beObject == null;");
		conditionNode.getDests().add(conditionDest2);
		flow.getNodesAndConditionsAndSplits().add(conditionNode);
		
		EndNodeType endNode = new EndNodeType();
		endNode.setName("endNode");
		flow.getNodesAndConditionsAndSplits().add(endNode);
		
		try {
			StringWriter writer = new StringWriter();
			EntityUtil.marshaller(entity, writer);
			
			System.out.println(writer.toString());
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testMarshalUnmarshall() {
		URL url = GeneratorFlowTest.class.getClassLoader().getResource(
				"LoginFlows.workflow");
		InputStream in = null;
		try {
			in = new FileInputStream(new File(url.getFile()));
			Workflow wf = EntityUtil.unmarshaller(Workflow.class, in);
			StringWriter writer = new StringWriter();
			EntityUtil.marshaller(wf, writer);
		} catch (JAXBException | FileNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			CloseUtil.close(in);
		}
	}
	
}
