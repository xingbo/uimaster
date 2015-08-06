package org.shaolin.uimaster.page;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.common.ParamType;
import org.shaolin.bmdp.datamodel.common.TargetEntityType;
import org.shaolin.bmdp.datamodel.common.VariableCategoryType;
import org.shaolin.bmdp.datamodel.page.ComponentConstraintType;
import org.shaolin.bmdp.datamodel.page.ComponentParamType;
import org.shaolin.bmdp.datamodel.page.ODMappingType;
import org.shaolin.bmdp.datamodel.page.PageInType;
import org.shaolin.bmdp.datamodel.page.PageODMappingType;
import org.shaolin.bmdp.datamodel.page.PageOutType;
import org.shaolin.bmdp.datamodel.page.PropertyType;
import org.shaolin.bmdp.datamodel.page.SimpleComponentMappingType;
import org.shaolin.bmdp.datamodel.page.StringPropertyType;
import org.shaolin.bmdp.datamodel.page.TableLayoutConstraintType;
import org.shaolin.bmdp.datamodel.page.TableLayoutType;
import org.shaolin.bmdp.datamodel.page.UIComponentParamType;
import org.shaolin.bmdp.datamodel.page.UIEntity;
import org.shaolin.bmdp.datamodel.page.UIPage;
import org.shaolin.bmdp.datamodel.page.UIPanelType;
import org.shaolin.bmdp.datamodel.page.UISkinType;
import org.shaolin.bmdp.datamodel.page.UITextFieldType;
import org.shaolin.bmdp.i18n.LocaleContext;
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.Registry;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.entity.EntityUtil;
import org.shaolin.bmdp.runtime.internal.AppServiceManagerImpl;
import org.shaolin.bmdp.runtime.spi.IEntityManager;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.uimaster.page.ajax.Label;
import org.shaolin.uimaster.page.ajax.SelectWidget;
import org.shaolin.uimaster.page.ajax.SingleChoice;
import org.shaolin.uimaster.page.ajax.TextField;
import org.shaolin.uimaster.page.ajax.json.RequestData;
import org.shaolin.uimaster.page.cache.ODFormObject;
import org.shaolin.uimaster.page.cache.PageCacheManager;
import org.shaolin.uimaster.page.exception.ODProcessException;
import org.shaolin.uimaster.page.exception.UIConvertException;
import org.shaolin.uimaster.page.od.IODMappingConverter;
import org.shaolin.uimaster.page.od.ODProcessor;
import org.shaolin.uimaster.page.od.PageODProcessor;
import org.shaolin.uimaster.page.od.rules.UIMultipleChoice;
import org.shaolin.uimaster.page.od.rules.UIMultipleChoiceAndCE;
import org.shaolin.uimaster.page.od.rules.UISelect;
import org.shaolin.uimaster.page.od.rules.UISingleChoice;
import org.shaolin.uimaster.page.od.rules.UISingleChoiceAndCE;
import org.shaolin.uimaster.page.od.rules.UIText;
import org.shaolin.uimaster.page.od.rules.UITextWithCE;
import org.shaolin.uimaster.page.od.rules.UITextWithCurrency;
import org.shaolin.uimaster.page.od.rules.UITextWithDate;
import org.shaolin.uimaster.page.od.rules.UITextWithFloatNumber;
import org.shaolin.uimaster.page.od.rules.UITextWithNumber;
import org.shaolin.uimaster.page.security.UserContext;
import org.shaolin.uimaster.page.widgets.HTMLCheckBoxType;
import org.shaolin.uimaster.page.widgets.HTMLComboBoxType;
import org.shaolin.uimaster.page.widgets.HTMLDateType;
import org.shaolin.uimaster.page.widgets.HTMLLabelType;
import org.shaolin.uimaster.page.widgets.HTMLListType;
import org.shaolin.uimaster.page.widgets.HTMLMultiChoiceType;
import org.shaolin.uimaster.page.widgets.HTMLReferenceEntityType;
import org.shaolin.uimaster.page.widgets.HTMLSelectComponentType;
import org.shaolin.uimaster.page.widgets.HTMLTextFieldType;
import org.shaolin.uimaster.test.be.CustomerImpl;
import org.shaolin.uimaster.test.be.ICustomer;
import org.shaolin.uimaster.test.ce.Gender;

public class ODTest {

	@BeforeClass
	public static void setup() {
		LocaleContext.createLocaleContext("default");
		// initialize registry
		Registry.getInstance().initRegistry();
		String[] filters = new String[] {"/uipage/"};
		// initialize entity manager.
		IEntityManager entityManager = IServerServiceManager.INSTANCE.getEntityManager();
		((EntityManager)entityManager).init(new ArrayList(), filters);
		
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
	public void testUserContext() {
		System.out.println(UserContext.isMobileRequest());
	}
	
	@Test
	public void testTextComponentBaseRule() throws UIConvertException, EvaluationException {
		MockHttpRequest request = new MockHttpRequest();
        HTMLSnapshotContext htmlContext = new HTMLSnapshotContext(request);
        htmlContext.setIsDataToUI(true);
        htmlContext.setHTMLPrefix("");
        
        RequestData requestData = new RequestData();
        AjaxActionHelper.createAjaxContext(new AjaxContext(new HashMap(), requestData));
        AjaxActionHelper.getAjaxContext().initData();
        AjaxActionHelper.getAjaxContext().setRequest(request, null);
        
        Map ajaxWidgetMap = new HashMap();
        Map pageComponentMap = new HashMap();
        request.getSession(true).setAttribute(AjaxContext.AJAX_COMP_MAP, ajaxWidgetMap);
        ajaxWidgetMap.put(AjaxContext.GLOBAL_PAGE, pageComponentMap);
        htmlContext.setAjaxWidgetMap(pageComponentMap);
		
        HTMLLabelType label = new HTMLLabelType(htmlContext, "labelWidget");
		Map<String, Object> inputData = new HashMap<String, Object>();
		inputData.put(IODMappingConverter.UI_WIDGET_TYPE, label);
		inputData.put(IODMappingConverter.UI_WIDGET_ID, label.getUIID());
		inputData.put("StringData", "hello");
		inputData.put("DisplayStringData", "display hello");
		htmlContext.addAjaxWidget(label.getUIID(), label.createAjaxWidget(null));
		
		UIText uiTextRule = new UIText();
		uiTextRule.setInputData(inputData);
		uiTextRule.pushDataToWidget(htmlContext);
		
		Map<String, Object> output = uiTextRule.getOutputData();
		Assert.assertEquals("hello", ((HTMLLabelType)output.get(IODMappingConverter.UI_WIDGET_TYPE)).getValue());
		Assert.assertEquals("display hello", ((HTMLLabelType)output.get(IODMappingConverter.UI_WIDGET_TYPE)).getDisplayValue());
		((Label)AjaxActionHelper.getCachedAjaxWidget(label.getUIID(), htmlContext)).setValue("how are you?");
		uiTextRule.pullDataFromWidget(htmlContext);
		output = uiTextRule.getOutputData();
		Assert.assertEquals("how are you?", output.get("StringData"));
		
		inputData.clear();
		HTMLTextFieldType textField = new HTMLTextFieldType(htmlContext, "testWidget");
		inputData.put(IODMappingConverter.UI_WIDGET_TYPE, textField);
		inputData.put(IODMappingConverter.UI_WIDGET_ID, textField.getUIID());
		inputData.put("CEType", Gender.class.getName());
		inputData.put("CEValue", Gender.MALE);
		htmlContext.addAjaxWidget(textField.getUIID(), textField.createAjaxWidget(null));
		UITextWithCE uiTextCERule = new UITextWithCE();
		uiTextCERule.setInputData(inputData);
		uiTextCERule.pushDataToWidget(htmlContext);
		
		output = uiTextCERule.getOutputData();
		Assert.assertEquals("Male", ((HTMLTextFieldType)output.get(IODMappingConverter.UI_WIDGET_TYPE)).getValue());
		((TextField)AjaxActionHelper.getCachedAjaxWidget(textField.getUIID(), htmlContext)).setValue("Female");
		uiTextCERule.pullDataFromWidget(htmlContext);
		output = uiTextCERule.getOutputData();
		Assert.assertEquals(Gender.FEMALE, output.get("CEValue"));
		
		inputData.clear();
		inputData.put(IODMappingConverter.UI_WIDGET_TYPE, textField);
		inputData.put(IODMappingConverter.UI_WIDGET_ID, textField.getUIID());
		inputData.put("Currency", new Double(100d));
		UITextWithCurrency uiTextCurrencyRule = new UITextWithCurrency();
		uiTextCurrencyRule.setInputData(inputData);
		uiTextCurrencyRule.pushDataToWidget(htmlContext);
		
		output = uiTextCurrencyRule.getOutputData();
		Assert.assertEquals("100", ((HTMLTextFieldType)output.get(IODMappingConverter.UI_WIDGET_TYPE)).getValue());
		Assert.assertNotNull(((HTMLTextFieldType)output.get(IODMappingConverter.UI_WIDGET_TYPE)).getCurrencySymbol());
		((TextField)AjaxActionHelper.getCachedAjaxWidget(textField.getUIID(), htmlContext)).setValue("120");
		uiTextCurrencyRule.pullDataFromWidget(htmlContext);
		output = uiTextCurrencyRule.getOutputData();
		Assert.assertEquals(120d, output.get("Currency"));
		
		HTMLDateType date = new HTMLDateType(htmlContext, "dateWidget");
		inputData.clear();
		inputData.put(IODMappingConverter.UI_WIDGET_TYPE, date);
		inputData.put(IODMappingConverter.UI_WIDGET_ID, date.getUIID());
		inputData.put("Date", new Date(1403435848000L));
		UITextWithDate uiDateRule = new UITextWithDate();
		uiDateRule.setInputData(inputData);
		uiDateRule.pushDataToWidget(htmlContext);
		htmlContext.addAjaxWidget(date.getUIID(), date.createAjaxWidget(null));
		
		output = uiDateRule.getOutputData();
		Assert.assertEquals("2014-06-22/19:17:28", ((HTMLDateType)output.get(IODMappingConverter.UI_WIDGET_TYPE)).getValue());
		((TextField)AjaxActionHelper.getCachedAjaxWidget(date.getUIID(), htmlContext)).setValue("2014-06-22/19:17:28");
		uiDateRule.pullDataFromWidget(htmlContext);
		output = uiDateRule.getOutputData();
		Assert.assertEquals("Sun Jun 22 19:17:28 CST 2014", ((Date)output.get("Date")).toString());
		
		inputData.clear();
		inputData.put(IODMappingConverter.UI_WIDGET_TYPE, date);
		inputData.put(IODMappingConverter.UI_WIDGET_ID, date.getUIID());
		inputData.put("Date", new Date(1403435848796L));
		inputData.put("IsDateOnly", true);
		uiDateRule.setInputData(inputData);
		uiDateRule.pushDataToWidget(htmlContext);
		
		output = uiDateRule.getOutputData();
		Assert.assertEquals("2014-06-22", ((HTMLDateType)output.get(IODMappingConverter.UI_WIDGET_TYPE)).getValue());
		uiDateRule.pullDataFromWidget(htmlContext);
		output = uiDateRule.getOutputData();
		Assert.assertEquals(1403366400000L, ((Date)output.get("Date")).getTime());
		
		inputData.clear();
		inputData.put(IODMappingConverter.UI_WIDGET_TYPE, textField);
		inputData.put(IODMappingConverter.UI_WIDGET_ID, textField.getUIID());
		inputData.put("FloatNumber", new Double(100d));
		UITextWithFloatNumber uiTextFloatRule = new UITextWithFloatNumber();
		uiTextFloatRule.setInputData(inputData);
		uiTextFloatRule.pushDataToWidget(htmlContext);
		
		output = uiTextFloatRule.getOutputData();
		Assert.assertEquals("100", ((HTMLTextFieldType)output.get(IODMappingConverter.UI_WIDGET_TYPE)).getValue());
		((TextField)AjaxActionHelper.getCachedAjaxWidget(textField.getUIID(), htmlContext)).setValue("120");
		uiTextFloatRule.pullDataFromWidget(htmlContext);
		output = uiTextFloatRule.getOutputData();
		Assert.assertEquals(120.0d, output.get("FloatNumber"));
		
		inputData.clear();
		inputData.put(IODMappingConverter.UI_WIDGET_TYPE, textField);
		inputData.put(IODMappingConverter.UI_WIDGET_ID, textField.getUIID());
		inputData.put("Number", new Long(100L));
		UITextWithNumber uiTextNumberRule = new UITextWithNumber();
		uiTextNumberRule.setInputData(inputData);
		uiTextNumberRule.pushDataToWidget(htmlContext);
		
		output = uiTextNumberRule.getOutputData();
		Assert.assertEquals("100", ((HTMLTextFieldType)output.get(IODMappingConverter.UI_WIDGET_TYPE)).getValue());
		((TextField)AjaxActionHelper.getCachedAjaxWidget(textField.getUIID(), htmlContext)).setValue("120");
		uiTextNumberRule.pullDataFromWidget(htmlContext);
		output = uiTextNumberRule.getOutputData();
		Assert.assertEquals(120L, output.get("Number"));
	}
	
	@Test
	public void testChioceComponentBaseRule() throws UIConvertException, EvaluationException {
		MockHttpRequest request = new MockHttpRequest();
        HTMLSnapshotContext htmlContext = new HTMLSnapshotContext(request);
        htmlContext.setIsDataToUI(true);
        htmlContext.setHTMLPrefix("");
        
        RequestData requestData = new RequestData();
        AjaxActionHelper.createAjaxContext(new AjaxContext(new HashMap(), requestData));
        AjaxActionHelper.getAjaxContext().initData();
        AjaxActionHelper.getAjaxContext().setRequest(request, null);
        
        Map ajaxWidgetMap = new HashMap();
        Map pageComponentMap = new HashMap();
        request.getSession(true).setAttribute(AjaxContext.AJAX_COMP_MAP, ajaxWidgetMap);
        ajaxWidgetMap.put(AjaxContext.GLOBAL_PAGE, pageComponentMap);
        htmlContext.setAjaxWidgetMap(pageComponentMap);
        
        HTMLListType listWidget = new HTMLListType(htmlContext, "chioceWidget");
        List<String> values = new ArrayList<String>();
        values.add("item0");
        values.add("item1");
        List<String> options = new ArrayList<String>();
        options.add("item0");
        options.add("item1");
        options.add("item2");
		Map<String, Object> inputData = new HashMap<String, Object>();
		inputData.put(IODMappingConverter.UI_WIDGET_TYPE, listWidget);
		inputData.put(IODMappingConverter.UI_WIDGET_ID, listWidget.getUIID());
		inputData.put("Value", values);
		inputData.put("OptionValues", options);
		inputData.put("OptionDisplayValues", options);
		inputData.put("StringData", "hello");
		inputData.put("DisplayStringData", "display hello");
		
		UIMultipleChoice uiMultipleChoiceRule = new UIMultipleChoice();
		uiMultipleChoiceRule.setInputData(inputData);
		uiMultipleChoiceRule.pushDataToWidget(htmlContext);
		
		Map<String, Object> output = uiMultipleChoiceRule.getOutputData();
		Assert.assertEquals(2, ((HTMLMultiChoiceType)output.get(IODMappingConverter.UI_WIDGET_TYPE)).getValue().size());
		Assert.assertEquals(3, ((HTMLMultiChoiceType)output.get(IODMappingConverter.UI_WIDGET_TYPE)).getOptionValues().size());
		htmlContext.addAjaxWidget(listWidget.getUIID(), listWidget.createAjaxWidget(null));
		uiMultipleChoiceRule.pullDataFromWidget(htmlContext);
		output = uiMultipleChoiceRule.getOutputData();
		Assert.assertEquals(3, ((List)output.get("OptionValues")).size());
		Assert.assertEquals(2, ((List)output.get("Value")).size());
		
		
        inputData.clear();
        inputData.put(IODMappingConverter.UI_WIDGET_TYPE, listWidget);
		inputData.put(IODMappingConverter.UI_WIDGET_ID, listWidget.getUIID());
		inputData.put("CEType", Gender.class.getName());
		UIMultipleChoiceAndCE uiMultipleCERule = new UIMultipleChoiceAndCE();
		uiMultipleCERule.setInputData(inputData);
		uiMultipleCERule.pushDataToWidget(htmlContext);
		
		output = uiMultipleCERule.getOutputData();
		Assert.assertEquals(2, ((HTMLListType)output.get(IODMappingConverter.UI_WIDGET_TYPE)).getValue().size());
		Assert.assertEquals(3, ((HTMLListType)output.get(IODMappingConverter.UI_WIDGET_TYPE)).getOptionValues().size());
		List<Gender> options1 = new ArrayList<Gender>();
		options1.add(Gender.FEMALE);
		options1.add(Gender.MALE);
		options1.add(Gender.NOT_SPECIFIED);
		inputData.put("CEValues", options1);
		uiMultipleCERule.setInputData(inputData);
		uiMultipleCERule.pushDataToWidget(htmlContext);//write date to widget.
		
		//refresh data.
		htmlContext.addAjaxWidget(listWidget.getUIID(), listWidget.createAjaxWidget(null));
		uiMultipleCERule.pullDataFromWidget(htmlContext);
		output = uiMultipleCERule.getOutputData();
		Assert.assertEquals(3, ((List)output.get("CEValues")).size());
		
		HTMLComboBoxType combobox = new HTMLComboBoxType(htmlContext, "combobox");
        options = new ArrayList<String>();
        options.add("item0");
        options.add("item1");
        options.add("item2");
		inputData = new HashMap<String, Object>();
		inputData.put(IODMappingConverter.UI_WIDGET_TYPE, combobox);
		inputData.put(IODMappingConverter.UI_WIDGET_ID, combobox.getUIID());
		inputData.put("Value", "item1");
		inputData.put("OptionValues", options);
		inputData.put("OptionDisplayValues", options);
		UISingleChoice uiSingleChoiceRule = new UISingleChoice();
		uiSingleChoiceRule.setInputData(inputData);
		uiSingleChoiceRule.pushDataToWidget(htmlContext);
		htmlContext.addAjaxWidget(combobox.getUIID(), combobox.createAjaxWidget(null));
		
		output = uiSingleChoiceRule.getOutputData();
		Assert.assertEquals("item1", ((HTMLComboBoxType)output.get(IODMappingConverter.UI_WIDGET_TYPE)).getValue());
		Assert.assertEquals(3, ((HTMLComboBoxType)output.get(IODMappingConverter.UI_WIDGET_TYPE)).getOptionValues().size());
		((SingleChoice)AjaxActionHelper.getCachedAjaxWidget(combobox.getUIID(), htmlContext)).setValue("item0");
		uiSingleChoiceRule.pullDataFromWidget(htmlContext);
		output = uiSingleChoiceRule.getOutputData();
		Assert.assertEquals(3, ((List)output.get("OptionValues")).size());
		Assert.assertEquals("item0", (String)output.get("Value"));
		
		
		combobox = new HTMLComboBoxType(htmlContext, "combobox");
		inputData = new HashMap<String, Object>();
		inputData.put(IODMappingConverter.UI_WIDGET_TYPE, combobox);
		inputData.put(IODMappingConverter.UI_WIDGET_ID, combobox.getUIID());
		inputData.put("CEType", Gender.class.getName());
		inputData.put("CEValue", Gender.FEMALE);
		UISingleChoiceAndCE uiSingleCERule = new UISingleChoiceAndCE();
		uiSingleCERule.setInputData(inputData);
		uiSingleCERule.pushDataToWidget(htmlContext);
		
		output = uiSingleCERule.getOutputData();
		Assert.assertEquals(3, ((HTMLComboBoxType)output.get(IODMappingConverter.UI_WIDGET_TYPE)).getOptionValues().size());
		htmlContext.addAjaxWidget(combobox.getUIID(), combobox.createAjaxWidget(null));
		((SingleChoice)AjaxActionHelper.getCachedAjaxWidget(combobox.getUIID(), htmlContext)).setValue("Male");
		uiSingleCERule.pullDataFromWidget(htmlContext);
		output = uiSingleCERule.getOutputData();
		Assert.assertEquals(Gender.MALE, output.get("CEValue"));
		
		HTMLCheckBoxType checkBox = new HTMLCheckBoxType(htmlContext, "checkBox");
		inputData = new HashMap<String, Object>();
		inputData.put(IODMappingConverter.UI_WIDGET_TYPE, checkBox);
		inputData.put(IODMappingConverter.UI_WIDGET_ID, checkBox.getUIID());		
		inputData.put("Value", false);
		UISelect uiSelectRule = new UISelect();
		uiSelectRule.setInputData(inputData);
		uiSelectRule.pushDataToWidget(htmlContext);
		htmlContext.addAjaxWidget(checkBox.getUIID(), checkBox.createAjaxWidget(null));
		
		output = uiSelectRule.getOutputData();
		Assert.assertEquals(false, ((HTMLSelectComponentType)output.get(IODMappingConverter.UI_WIDGET_TYPE)).getValue());
		((SelectWidget)AjaxActionHelper.getCachedAjaxWidget(checkBox.getUIID(), htmlContext)).setSelected(true);
		uiSelectRule.pullDataFromWidget(htmlContext);
		output = uiSelectRule.getOutputData();
		Assert.assertEquals(true, ((Boolean)output.get("Value")).booleanValue());
		
	}
	
	@Test
	public void testGetODEntityObject() {
		
		ODMappingType odDescriptor = new ODMappingType();
		odDescriptor.setEntityName("org.shaolin.uimaster.form.Customer");
		
		ParamType customer = newCustomerBE();
		
		odDescriptor.getDataEntities().add(customer);
		
		TargetEntityType refEntity1 = new TargetEntityType();
		refEntity1.setEntityName("org.shaolin.uimaster.form.Customer");
		ParamType uicustomer = new ParamType();
		uicustomer.setName("customerUI");
		uicustomer.setType(refEntity1);
		uicustomer.setCategory(VariableCategoryType.UI_ENTITY);
		odDescriptor.getUIEntities().add(uicustomer);
		
		
		SimpleComponentMappingType id = new SimpleComponentMappingType();
		id.setName("idmapping");
		
		TargetEntityType rule1 = new TargetEntityType();
		rule1.setEntityName("org.shaolin.uimaster.page.od.rules.UIText");
		id.setMappingRule(rule1);
		odDescriptor.getComponentMappings().add(id);
		
		ComponentParamType compParameter = new ComponentParamType();
		compParameter.setParamName("Number");
		compParameter.setComponentPath("customer.id");
		id.getDataComponents().add(compParameter);
		
		UIComponentParamType uiParameter = new UIComponentParamType();
		uiParameter.setParamName("UIText");
		uiParameter.setComponentPath("customerUI.idText");
		id.getUIComponents().add(uiParameter);
		
		ExpressionType dataToUIExpr = new ExpressionType();
		dataToUIExpr.setExpressionString("{}");
		odDescriptor.setDataToUIMappingOperation(dataToUIExpr);
		
		ExpressionType uiToDataExpr = new ExpressionType();
		uiToDataExpr.setExpressionString("{}");
		odDescriptor.setUIToDataMappingOperation(uiToDataExpr);
		
		UIEntity customerEntity = new UIEntity();
		customerEntity.setEntityName("org.shaolin.uimaster.form.Customer");
		UIPanelType container = new UIPanelType();
		container.setUIID("Form");
		UISkinType skinType = new UISkinType();
		skinType.setSkinName("org.shaolin.uimaster.page.skin.TitlePanel");
		PropertyType p = new PropertyType();
		p.setName("text");
		skinType.getParams().add(p);
		container.setUISkin(skinType);
		customerEntity.setBody(container);
		
		UITextFieldType idText = new UITextFieldType();
		idText.setUIID("id");
		StringPropertyType value = new StringPropertyType();
		value.setValue("001");
		idText.setText(value);
		container.getComponents().add(idText);
		
		UITextFieldType nameText = new UITextFieldType();
		nameText.setUIID("name");
		container.getComponents().add(nameText);
		
		TableLayoutType layout = new TableLayoutType();
		container.setLayout(layout);
		
		layout.getColumnWidthWeights().add(0.0);
		layout.getColumnWidthWeights().add(0.0);
		layout.getRowHeightWeights().add(0.0);
		
		ComponentConstraintType idConstraint = new ComponentConstraintType();
		idConstraint.setComponentId("id");
		TableLayoutConstraintType idConstraintDetails = new TableLayoutConstraintType();
		idConstraintDetails.setX(0);
		idConstraintDetails.setY(0);
		idConstraintDetails.setVisible("true");
		idConstraint.setConstraint(idConstraintDetails);
		container.getLayoutConstraints().add(idConstraint);
		
		ComponentConstraintType nameConstraint = new ComponentConstraintType();
		nameConstraint.setComponentId("name");
		TableLayoutConstraintType nameConstraintDetails = new TableLayoutConstraintType();
		nameConstraintDetails.setX(0);
		nameConstraintDetails.setY(1);
		nameConstraintDetails.setVisible("true");
		nameConstraint.setConstraint(nameConstraintDetails);
		container.getLayoutConstraints().add(nameConstraint);
		
		customerEntity.setMapping(odDescriptor);
		
		try {
			StringWriter writer = new StringWriter();
			EntityUtil.marshaller(customerEntity, writer);
			
			System.out.println(writer.toString());
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		UIPage addCustomerPage = new UIPage();
		addCustomerPage.setEntityName("org.shaolin.uimaster.page.AddCustomer");
		
		PageInType in = new PageInType();
		addCustomerPage.setIn(in);
		
		PageOutType save = new PageOutType();
		addCustomerPage.getOuts().add(save);
		
		
		SimpleComponentMappingType customer1OD = new SimpleComponentMappingType();
		customer1OD.setName("UIEntity_customer1OD");
		
		TargetEntityType customerOdRule = new TargetEntityType();
		customerOdRule.setEntityName("org.shaolin.uimaster.form.Customer");
		customer1OD.setMappingRule(customerOdRule);
		
		ComponentParamType param = new ComponentParamType();
		param.setParamName("customer");
		param.setComponentPath("customer");
		customer1OD.getDataComponents().add(param);
		
		addCustomerPage.setODMapping(new PageODMappingType());
		addCustomerPage.getODMapping().getComponentMappings().add(customer1OD);
		
		UIEntity body = new UIEntity();
		addCustomerPage.setUIEntity(body);
		
		body.setBody(container);
		
		try {
			StringWriter writer = new StringWriter();
			EntityUtil.marshaller(addCustomerPage, writer);
			System.out.println(writer.toString());
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	private ParamType newCustomerBE() {
		TargetEntityType refEntity = new TargetEntityType();
		refEntity.setEntityName("org.shaolin.uimaster.be.Customer");
		ParamType customer = new ParamType();
		customer.setName("customer");
		customer.setCategory(VariableCategoryType.BUSINESS_ENTITY);
		customer.setType(refEntity);
		return customer;
	}
	
	@Test
	public void testODMapping() {
		try {
			MockHttpRequest request = new MockHttpRequest();
            HTMLSnapshotContext htmlContext = new HTMLSnapshotContext(request);
            htmlContext.setFormName("org.shaolin.uimaster.form.Customer");
            htmlContext.setIsDataToUI(true);
            htmlContext.setHTMLPrefix("");
			
            AjaxActionHelper.createAjaxContext(new AjaxContext(new HashMap(), new RequestData()));
            
			ODFormObject odEntityObject = PageCacheManager.getODFormObject(htmlContext.getFormName());
            HTMLReferenceEntityType newReferObject = new HTMLReferenceEntityType(htmlContext, "customer", htmlContext.getFormName());
            newReferObject.setPrefix("");
            
            CustomerImpl customerPojo = new CustomerImpl();
            customerPojo.setId(10);
            customerPojo.setName("John Prine");
            Map inputParams = new HashMap();
            inputParams.put("customer", customerPojo);
            inputParams.put(odEntityObject.getUiParamName(), newReferObject);
            
            htmlContext.setODMapperData(inputParams);
        	ODProcessor processor = new ODProcessor(htmlContext, htmlContext.getFormName(), -1);
            processor.process();

            Map referenceEntityMap = new HashMap();
            htmlContext.setRefEntityMap(referenceEntityMap);
            Map result = htmlContext.getODMapperData();
            System.out.println(result.toString());
            
            htmlContext.printHTMLAttributeValues();
            
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testPageODMapping() throws InterruptedException {
		MockHttpRequest request = new MockHttpRequest();
        HTMLSnapshotContext htmlContext = new HTMLSnapshotContext(request);
        htmlContext.setFormName("org.shaolin.uimaster.page.AddCustomer");
        htmlContext.setIsDataToUI(true);
        htmlContext.setHTMLPrefix("");
		
        ICustomer customer = new CustomerImpl();
        customer.setId(1101);
        customer.setName("Shaolin Wu");
        Map inputParams = new HashMap();
        inputParams.put("customer", customer);
        htmlContext.setODMapperData(inputParams);
        
        String page = "org.shaolin.uimaster.page.AddCustomer";
        
		PageODProcessor pageProcessor = new PageODProcessor(htmlContext, page);
		try {
			pageProcessor.process();
		} catch (ODProcessException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
