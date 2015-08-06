package org.shaolin.uimaster.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.shaolin.bmdp.datamodel.flowdiagram.RectangleNodeType;
import org.shaolin.bmdp.i18n.LocaleContext;
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.Registry;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.internal.AppServiceManagerImpl;
import org.shaolin.bmdp.runtime.spi.IEntityManager;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.uimaster.page.ajax.AList;
import org.shaolin.uimaster.page.ajax.CheckBox;
import org.shaolin.uimaster.page.ajax.ComboBox;
import org.shaolin.uimaster.page.ajax.TextArea;
import org.shaolin.uimaster.page.ajax.TextField;
import org.shaolin.uimaster.page.ajax.TreeItem;
import org.shaolin.uimaster.page.ajax.TreeItem.LinkAttribute;
import org.shaolin.uimaster.page.ajax.json.JSONArray;
import org.shaolin.uimaster.page.ajax.json.JSONException;
import org.shaolin.uimaster.page.ajax.json.JSONObject;
import org.shaolin.uimaster.page.ajax.json.RequestData;
import org.shaolin.uimaster.page.cache.UIPageObject;
import org.shaolin.uimaster.page.exception.ODProcessException;
import org.shaolin.uimaster.page.flow.WebflowConstants;
import org.shaolin.uimaster.page.od.ODPageContext;
import org.shaolin.uimaster.page.od.PageODProcessor;
import org.shaolin.uimaster.test.be.CustomerImpl;
import org.shaolin.uimaster.test.be.ICustomer;

public class PageTest {

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
	
	@AfterClass
	public static void teardown() {
		
	}
	
	@Test
	public void testConstraint() throws EvaluationException {
		MockHttpRequest request = new MockHttpRequest();
		MockHttpResponse response = new MockHttpResponse();
		
        HTMLSnapshotContext htmlContext = new HTMLSnapshotContext(request, response);
        htmlContext.setFormName("test");
        htmlContext.setIsDataToUI(true);
        htmlContext.setHTMLPrefix("");
		
        Map ajaxWidgetMap = new HashMap();
        Map pageComponentMap = new HashMap();
        request.getSession(true).setAttribute(AjaxContext.AJAX_COMP_MAP, ajaxWidgetMap);
        ajaxWidgetMap.put(AjaxContext.GLOBAL_PAGE, pageComponentMap);
        htmlContext.setAjaxWidgetMap(pageComponentMap);
        
		RequestData requestData = new RequestData();
        AjaxActionHelper.createAjaxContext(new AjaxContext(new HashMap(), requestData));
        AjaxActionHelper.getAjaxContext().initData();
        AjaxActionHelper.getAjaxContext().setRequest(request, null);
		
		TextField textField = new TextField("textField");
		try {
			textField.addConstraint("allowBlank", false, "The field is not allowed blank!");
			textField.getValue();
			Assert.fail();
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
			textField.setValue("hello");
			textField.getValue();
		}
		TextArea textArea = new TextArea("textArea");
		try {
			textArea.addConstraint("maxLength", 5, "The max length must less than 5!");
			textArea.setValue("1000000");
			textArea.getValue();
			Assert.fail();
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
			textArea.setValue("1000");
			textArea.getValue();
		}
		CheckBox checkbo = new CheckBox("checkBox");
		try {
			checkbo.addConstraint("mustCheck", true, "the checkbox must be selected!");
			checkbo.isSelected();
			Assert.fail();
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
			checkbo.setSelected(true);
			checkbo.isSelected();
		}
		AList list = new AList("list");
		try {
			List<String> optionValues = new ArrayList<String>();
			optionValues.add("a");
			optionValues.add("b");
			list.setOptions(optionValues, optionValues);
			list.addConstraint("selectedValuesConstraint", new String[]{"a"}, "the a value must be selected in list!");
			list.getValues();
			Assert.fail();
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
			List<String> values = new ArrayList<String>();
			values.add("a");
			list.setValues(values);
			list.getValues();
		}
		
		ComboBox combobox = new ComboBox("comboBox");
		try {
			List<String> optionValues = new ArrayList<String>();
			optionValues.add("a");
			optionValues.add("b");
			combobox.setOptions(optionValues, optionValues);
			combobox.addConstraint("selectedValueConstraint", "b", "the a value must be selected in combobox!");
			combobox.getValue();
			Assert.fail();
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
			combobox.setValue("b");
			combobox.getValue();
		}
	}
	
	@Test
	public void testDataToUI() throws IllegalStateException, IllegalArgumentException, IOException {
		String page = "org.shaolin.uimaster.page.AddCustomer";
		
		MockHttpRequest request = new MockHttpRequest();
		MockHttpResponse response = new MockHttpResponse();
		
        HTMLSnapshotContext htmlContext = new HTMLSnapshotContext(request, response);
        htmlContext.setFormName(page);
        htmlContext.setIsDataToUI(true);
        htmlContext.setHTMLPrefix("");
		
        Map ajaxWidgetMap = new HashMap();
        Map pageComponentMap = new HashMap();
        request.getSession(true).setAttribute(AjaxContext.AJAX_COMP_MAP, ajaxWidgetMap);
        ajaxWidgetMap.put(AjaxContext.GLOBAL_PAGE, pageComponentMap);
        htmlContext.setAjaxWidgetMap(pageComponentMap);
        
        ICustomer customer = new CustomerImpl();
        customer.setId(1101);
        customer.setName("Shaolin Wu");
        
        Map inputParams = new HashMap();
        inputParams.put("customer", customer);
        htmlContext.setODMapperData(inputParams);
		
		try {
			UIPageObject pageObject = HTMLUtil.parseUIPage(page);
			PageDispatcher dispatcher = new PageDispatcher(pageObject);
			dispatcher.forwardPage(htmlContext);
			
			System.out.println("HTML Code: \n" + response.getHtmlCode());
		} catch (JspException e) {
			e.printStackTrace();
			Assert.fail();
		}
		
        inputParams.put("customer", customer);
		
        htmlContext.resetRepository();
        htmlContext.setFormName(page);
        htmlContext.setODMapperData(inputParams);
        htmlContext.setIsDataToUI(false);
        request.setAttribute(ODPageContext.OUT_NAME, "Save");
        try
        {
        	PageODProcessor pageODProcessor = new PageODProcessor(htmlContext, page);
            pageODProcessor.process();
            
            System.out.println("UI To Data outcomes: \n" + htmlContext.getODMapperData());
        } catch (ODProcessException e) {
        	e.printStackTrace();
		}
	}
	
	@Test
	public void testFrameDataToUI() throws IllegalStateException, IllegalArgumentException, IOException {
		
		String page = "org.shaolin.uimaster.page.AddCustomer";
		
		MockHttpRequest request = new MockHttpRequest();
		request.setAttribute(WebflowConstants.FRAME_NAME, "frame1");
		request.setAttribute("_framePrefix", "frame1");
		request.setAttribute("_frameTarget", "frame1");
		
		
		MockHttpResponse response = new MockHttpResponse();
		
        HTMLSnapshotContext htmlContext = new HTMLSnapshotContext(request, response);
        htmlContext.setFormName(page);
        htmlContext.setIsDataToUI(true);
        htmlContext.setHTMLPrefix("");
		
        ICustomer customer = new CustomerImpl();
        customer.setId(1101);
        customer.setName("Shaolin Wu");
        
        Map inputParams = new HashMap();
        inputParams.put("customer", customer);
        htmlContext.setODMapperData(inputParams);
		
		try {
			UIPageObject pageObject = HTMLUtil.parseUIPage(page);
			PageDispatcher dispatcher = new PageDispatcher(pageObject);
			dispatcher.forwardPage(htmlContext);
			
			System.out.println("HTML Code: \n" + response.getHtmlCode());
		} catch (JspException e) {
			e.printStackTrace();
			Assert.fail();
		}
		
	}
	
	@Test
	public void testJSON() throws JSONException {
		JSONArray array = new JSONArray("[{\"id\": \"General Setup\",\"top\": \"200px\",\"left\": \"980px\"},{\"id\": \"Master Data Management\",\"top\": \"220px\",\"left\": \"680px\"}]");
		Assert.assertEquals(array.length(), 2);
		Assert.assertEquals(array.getJSONObject(0).getString("id"), "General Setup");
		
		RectangleNodeType node = new RectangleNodeType();
		node.setId("node1");
		node.setName("Hello");
		node.setX(0);
		node.setY(0);
		
		System.out.println((new JSONObject(node)).toString());
		
		Map data = new HashMap();
        data.put("cmd","addNode");
        data.put("data", (new JSONObject(node)).toString());
        
        System.out.println((new JSONObject(data)).toString());
        
        ArrayList result = new ArrayList();
        for (int i=0;i<1;i++) {
            TreeItem item = new TreeItem();
            item.setId("id" + i);
            item.setText("Node" + i);
            item.setA_attr(new LinkAttribute("#"));
            item.setIcon(null);
            
            TreeItem child = new TreeItem();
            child.setId("id" + i);
            child.setText("Node" + i);
            child.setA_attr(new LinkAttribute("#"));
            child.setIcon(null);
            
            TreeItem child1 = new TreeItem();
            child1.setId("id" + i);
            child1.setText("Node" + i);
            child1.setA_attr(new LinkAttribute("#"));
            child1.setIcon(null);
            
            item.getChildren().add(child);
            item.getChildren().add(child1);
            
            result.add(item);
        } 
        JSONArray jsonArray = new JSONArray(result);
        System.out.println(jsonArray.toString());
	}
	
	@Test
	public void testSplit() {
		String s = "Wu Shaolin";
		String[] items = s.split(" ");
		System.out.println(items[0]);
		System.out.println(items[1]);
	}
}
