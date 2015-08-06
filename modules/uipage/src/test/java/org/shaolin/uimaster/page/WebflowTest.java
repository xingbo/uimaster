package org.shaolin.uimaster.page;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.common.NameExpressionType;
import org.shaolin.bmdp.datamodel.common.ParamType;
import org.shaolin.bmdp.datamodel.common.TargetEntityType;
import org.shaolin.bmdp.datamodel.common.VariableCategoryType;
import org.shaolin.bmdp.datamodel.flowdiagram.Connection;
import org.shaolin.bmdp.datamodel.flowdiagram.FlowChunk;
import org.shaolin.bmdp.datamodel.flowdiagram.RectangleNodeType;
import org.shaolin.bmdp.datamodel.pagediagram.DisplayOutType;
import org.shaolin.bmdp.datamodel.pagediagram.LogicNodeType;
import org.shaolin.bmdp.datamodel.pagediagram.NextType;
import org.shaolin.bmdp.datamodel.pagediagram.PageNodeType;
import org.shaolin.bmdp.i18n.LocaleContext;
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.Registry;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.entity.EntityUtil;
import org.shaolin.bmdp.runtime.internal.AppServiceManagerImpl;
import org.shaolin.bmdp.runtime.spi.IEntityManager;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.exception.WebFlowException;
import org.shaolin.uimaster.page.flow.WebflowConstants;
import org.shaolin.uimaster.page.flow.nodes.UIPageNode;
import org.shaolin.uimaster.page.flow.nodes.WebChunk;
import org.shaolin.uimaster.test.be.ICustomer;

public class WebflowTest {

	@BeforeClass
	public static void setup() {
		LocaleContext.createLocaleContext("default");
		// initialize registry
		Registry.getInstance().initRegistry();
		String[] filters = new String[] {"/uipage/"};
		// initialize entity manager.
		IEntityManager entityManager = IServerServiceManager.INSTANCE.getEntityManager();
		((EntityManager)entityManager).init(new ArrayList(), filters);
		WebConfig.setServletContextPath("E:/test/web/");
		AppContext.register(new AppServiceManagerImpl("test", ODTest.class.getClassLoader()));
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	@Test
	public void testCreateWebflow() {
		System.out.println("File.separator:" + File.separator);
	}
	
	@Test
	public void testLogicNodeOrStartNode() {
		MockHttpRequest request = new MockHttpRequest();
		MockHttpResponse response = new MockHttpResponse();
		
		org.shaolin.bmdp.datamodel.pagediagram.WebChunk flowType = new org.shaolin.bmdp.datamodel.pagediagram.WebChunk();
		ParamType gVar = new ParamType();
		gVar.setCategory(VariableCategoryType.JAVA_CLASS);
		gVar.setName("gStr");
		ExpressionType expression = new ExpressionType();
		expression.setExpressionString("\"this is global var!\"");
		gVar.setDefault(expression);
		createRefEntity(gVar, "java.lang.String");
		flowType.getGlobalVariables().add(gVar);
		
		flowType.setEntityName("TestWebFlow");
		
		LogicNodeType type = new LogicNodeType();
		flowType.getWebNodes().add(type);
		type.setName("TestLogicNode");
		
		ParamType var1 = new ParamType();
		var1.setCategory(VariableCategoryType.JAVA_CLASS);
		var1.setName("str");
		createRefEntity(var1, "java.lang.String");
		type.getVariables().add(var1);
		
		ExpressionType expression1 = new ExpressionType();
		expression1.setExpressionString("{$str = \"hello, this is logic node!\";System.out.println($str);System.out.println(@gStr);}");
		type.setOperation(expression1);
		
		try {
			StringWriter writer = new StringWriter();
			EntityUtil.marshaller(flowType, writer);
			
			System.out.println(writer.toString());
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		try {
			WebChunk chunk = new WebChunk(flowType);
			chunk.initChunk();
			chunk.getWebNodes().get(0).execute(request, response);		
		} catch (ParsingException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (WebFlowException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	private void createRefEntity(ParamType var1, String entityName) {
		TargetEntityType value1 = new TargetEntityType();
		value1.setEntityName(entityName);
		var1.setType(value1);
	}
	
	private TargetEntityType createRefEntity(String entityName) {
		TargetEntityType value1 = new TargetEntityType();
		value1.setEntityName(entityName);
		return value1;
	}
	
	@Test
	public void testFlow() {
		FlowChunk flow = new FlowChunk();
		
		RectangleNodeType node1 = new RectangleNodeType();
		node1.setName("node1");
		node1.setX(40);
		node1.setY(50);
		flow.getNodes().add(node1);
		
		RectangleNodeType node2 = new RectangleNodeType();
		node2.setName("node2");
		node2.setX(40);
		node2.setY(50);
		flow.getNodes().add(node2);
		
		Connection connection1 = new Connection();
		connection1.setName("connection1");
		connection1.setSourceAnchor("node1RightMiddle");
		connection1.setTargetAnchor("node2LeftMiddle");
		flow.getConnections().add(connection1);
		
		try {
			StringWriter writer = new StringWriter();
			EntityUtil.marshaller(flow, writer);
			
			System.out.println(writer.toString());
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testPageNode() {
		Registry.getInstance().initRegistry();
		LocaleContext.createLocaleContext("default");
		
		MockHttpRequest request = new MockHttpRequest();
		MockHttpResponse response = new MockHttpResponse();
		
		Map ajaxWidgetMap = new HashMap();
        Map pageComponentMap = new HashMap();
        request.getSession(true).setAttribute(AjaxContext.AJAX_COMP_MAP, ajaxWidgetMap);
        ajaxWidgetMap.put(AjaxContext.GLOBAL_PAGE, pageComponentMap);
		
		
        org.shaolin.bmdp.datamodel.pagediagram.WebChunk flowType = new org.shaolin.bmdp.datamodel.pagediagram.WebChunk();
		flowType.setEntityName("TestWebFlow");
		
		PageNodeType type = new PageNodeType();
		flowType.getWebNodes().add(type);
		type.setSourceEntity(createRefEntity("org.shaolin.uimaster.page.SearchCustomer"));
		ParamType inVar = new ParamType();
		inVar.setCategory(VariableCategoryType.BUSINESS_ENTITY);
		inVar.setName("customer");
		ExpressionType expression = new ExpressionType();
		expression.setExpressionString("import org.shaolin.uimaster.test.be.CustomerImpl;" +
				"{CustomerImpl customer = new CustomerImpl(); \n" +
				"customer.setId(1234); \n" +
				"customer.setName(\"Shaolin\"); \n" +
				"return customer;}");
		inVar.setDefault(expression);
		TargetEntityType value = new TargetEntityType();
		value.setEntityName("org.shaolin.uimaster.test.be.Customer");
		inVar.setType(value);
		type.getVariables().add(inVar);
		DisplayOutType displayout = new DisplayOutType();
		displayout.setName("out1");
		NextType nextType = new NextType();
		nextType.setDestNode("TestLogicNode");
		displayout.setNext(nextType);
		
		NameExpressionType nameExpr = new NameExpressionType();
		nameExpr.setName("customer");
		ExpressionType expr = new ExpressionType();
		expr.setExpressionString("$customer");
		nameExpr.setExpression(expr);
		nextType.getOutDataMappingToNodes().add(nameExpr);
		
		type.getOuts().add(displayout);
		type.setName("TestPageNode");
		
		//add the next page.
		LogicNodeType logicType = new LogicNodeType();
		flowType.getWebNodes().add(logicType);
		logicType.setName("TestLogicNode");
		
		ParamType var1 = new ParamType();
		var1.setCategory(VariableCategoryType.BUSINESS_ENTITY);
		var1.setName("customer");
		TargetEntityType value1 = new TargetEntityType();
		value1.setEntityName("org.shaolin.uimaster.test.be.Customer");
		var1.setType(value1);
		logicType.getVariables().add(var1);
		
		ExpressionType expression1 = new ExpressionType();
		expression1.setExpressionString("{System.out.println(\"Customer info: \" + $customer);}");
		logicType.setOperation(expression1);
		
		try {
			WebChunk chunk = new WebChunk(flowType);
			chunk.initChunk();
			UIPageNode pageNode = (UIPageNode)chunk.getWebNodes().get(0);
			pageNode.execute(request, response);
			
			System.out.println("HTML Code: \n" + response.getHtmlCode());
			
			Assert.assertEquals(10, ((Map)AjaxActionHelper.getAjaxWidgetMap(
					request.getSession()).get(AjaxContext.GLOBAL_PAGE)).size());
			ICustomer customer = (ICustomer)pageNode.getWebFlowContext().
					getEvaluationContextObject("$").getVariableValue("customer");
			Assert.assertEquals(1234, customer.getId());
			Assert.assertEquals("Shaolin", customer.getName());
		} catch (ParsingException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (WebFlowException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (EvaluationException e) {
			e.printStackTrace();
			Assert.fail();
		}
		
	}
	
}
