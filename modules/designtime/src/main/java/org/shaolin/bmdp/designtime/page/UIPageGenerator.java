package org.shaolin.bmdp.designtime.page;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.shaolin.bmdp.datamodel.bediagram.BECollectionType;
import org.shaolin.bmdp.datamodel.bediagram.BEDiagram;
import org.shaolin.bmdp.datamodel.bediagram.BEObjRefType;
import org.shaolin.bmdp.datamodel.bediagram.BinaryType;
import org.shaolin.bmdp.datamodel.bediagram.BusinessEntityType;
import org.shaolin.bmdp.datamodel.bediagram.CEObjRefType;
import org.shaolin.bmdp.datamodel.bediagram.DateTimeType;
import org.shaolin.bmdp.datamodel.bediagram.FileType;
import org.shaolin.bmdp.datamodel.bediagram.MemberType;
import org.shaolin.bmdp.datamodel.common.DiagramType;
import org.shaolin.bmdp.datamodel.common.ExpressionType;
import org.shaolin.bmdp.datamodel.common.NameExpressionType;
import org.shaolin.bmdp.datamodel.common.ParamScopeType;
import org.shaolin.bmdp.datamodel.common.ParamType;
import org.shaolin.bmdp.datamodel.common.TargetEntityType;
import org.shaolin.bmdp.datamodel.common.VariableCategoryType;
import org.shaolin.bmdp.datamodel.page.BooleanPropertyType;
import org.shaolin.bmdp.datamodel.page.ClickListenerType;
import org.shaolin.bmdp.datamodel.page.ComponentConstraintType;
import org.shaolin.bmdp.datamodel.page.ComponentMappingType;
import org.shaolin.bmdp.datamodel.page.ComponentParamType;
import org.shaolin.bmdp.datamodel.page.ExpressionParamType;
import org.shaolin.bmdp.datamodel.page.ExpressionPropertyType;
import org.shaolin.bmdp.datamodel.page.FunctionCallType;
import org.shaolin.bmdp.datamodel.page.FunctionType;
import org.shaolin.bmdp.datamodel.page.ODMappingType;
import org.shaolin.bmdp.datamodel.page.OpCallAjaxType;
import org.shaolin.bmdp.datamodel.page.OpExecuteScriptType;
import org.shaolin.bmdp.datamodel.page.OpInvokeWorkflowType;
import org.shaolin.bmdp.datamodel.page.ResourceBundlePropertyType;
import org.shaolin.bmdp.datamodel.page.SimpleComponentMappingType;
import org.shaolin.bmdp.datamodel.page.StringPropertyType;
import org.shaolin.bmdp.datamodel.page.TableLayoutConstraintType;
import org.shaolin.bmdp.datamodel.page.UIButtonType;
import org.shaolin.bmdp.datamodel.page.UIComponentParamType;
import org.shaolin.bmdp.datamodel.page.UIComponentType;
import org.shaolin.bmdp.datamodel.page.UIEntity;
import org.shaolin.bmdp.datamodel.page.UIHiddenType;
import org.shaolin.bmdp.datamodel.page.UILabelType;
import org.shaolin.bmdp.datamodel.page.UIPage;
import org.shaolin.bmdp.datamodel.page.UIPanelType;
import org.shaolin.bmdp.datamodel.page.UIReferenceEntityType;
import org.shaolin.bmdp.datamodel.page.UISelectComponentType;
import org.shaolin.bmdp.datamodel.page.UITableColHTMLType;
import org.shaolin.bmdp.datamodel.page.UITableColumnType;
import org.shaolin.bmdp.datamodel.page.UITableDefaultActionType;
import org.shaolin.bmdp.datamodel.page.UITableType;
import org.shaolin.bmdp.designtime.tools.GeneratorOptions;
import org.shaolin.bmdp.runtime.be.BEUtil;
import org.shaolin.bmdp.runtime.entity.EntityAddedEvent;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.entity.EntityUpdatedEvent;
import org.shaolin.bmdp.runtime.entity.EntityUtil;
import org.shaolin.bmdp.runtime.entity.IEntityEventListener;
import org.shaolin.uimaster.page.od.BaseRulesHelper;
import org.shaolin.uimaster.page.od.IODMappingConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UIPageGenerator implements IEntityEventListener<BusinessEntityType, BEDiagram> {
	
	private static final Logger logger = LoggerFactory.getLogger(UIPageGenerator.class.getName());
	
	private GeneratorOptions option = null;
	
	private EntityManager entityManager;
	
	private boolean isSkip = true;
	
	public UIPageGenerator(GeneratorOptions option) {
		this.option = option;
		
	}
	
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	
	public void notify(EntityAddedEvent<BusinessEntityType, BEDiagram> event) {
		if (event.getDiagram().getBePackage().indexOf(option.getBundleName()) == -1) {
			return;
		}
		
		if (!event.getEntity().isNeedUIEntity()) {
			return;
		}
		
		File entityDir = option.getEntitiesDirectory();
		String entityName = event.getEntity().getEntityName();
		String bePackage = event.getDiagram().getBePackage();
		String pagePackage = entityName.substring(0, entityName.lastIndexOf("."));
		pagePackage = pagePackage.replace("be", "page");
		String formPackage = pagePackage.replace("page", "form");
		String name = entityName.substring(entityName.lastIndexOf(".") + 1);
		String pageName = name + ".page";
		String uiformName = name + ".form";
		String beImpl = BEUtil.getBEImplementClassName(entityName);
		String beImplName = BEUtil.getBEImplementOnlyName(entityName);
		
		File formDir = new File(entityDir, formPackage.replace('.', '/'));
		if (!formDir.exists()) {
			formDir.mkdirs();
		} 
		File formFile = new File(formDir, uiformName);
		if (formFile.exists()) {
			if (event.getEntity().isNeedUITableEditor()) {
				File tableFile = new File(formDir, name + "Table.form");
				if (tableFile.exists()) {
					return;
				}
				try {
					createTableEditorForm(formDir, event.getEntity().getMembers(), formPackage, name, beImpl, beImplName, entityName);
				} catch (Exception e) {
					logger.error("Error when generate form table for " + entityName,
							e);
				}
			}
			return;// already existed.
		}
		
//		File pageDir = new File(entityDir, pagePackage.replace('.', '/'));
//		if (!pageDir.exists()) {
//			pageDir.mkdirs();
//		} 
//		File pageFile = new File(pageDir, pageName);
//		if (pageFile.exists()) {
//			return;// already existed.
//		}
		
		isSkip = false;
		
		try {
			List<MemberType> members = event.getEntity().getMembers();
			if (event.getEntity().getParentObject() != null) {
				BEObjRefType be = (BEObjRefType)event.getEntity().getParentObject();
				String parentBe = be.getTargetEntity().getEntityName();
				if (parentBe == null || parentBe.isEmpty()) {
					logger.warn("the parent BE value is null from BE entity: " + entityName);
					return;
				}
				if (parentBe.indexOf(".") == -1) {
					parentBe = bePackage + "." + parentBe;
				}
				BusinessEntityType prentObject = this.entityManager.getEntity(parentBe, BusinessEntityType.class);
				members = prentObject.getMembers();
				members.addAll(event.getEntity().getMembers());
			}
			
			UIEntity uiform = new UIEntity();
			uiform.setEntityName(formPackage + "." + name);
			
			FunctionType saveFunc = new FunctionType();
			saveFunc.setFunctionName("Save");
			OpCallAjaxType ajaxCall = new OpCallAjaxType();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
			ajaxCall.setName("saveDetail-" + sdf.format(new Date()));
			ExpressionType expr = new ExpressionType();
			expr.setExpressionString(   "\n        import java.util.HashMap;" + 
										"\n        import org.shaolin.uimaster.page.AjaxContext;" +
								        "\n        import org.shaolin.uimaster.page.ajax.*;" +
								        "\n        import " + beImpl + ";" +
								        "\n        { " +
								        "\n            "+beImplName+" defaultUser = new " + beImplName + "();" +
								        "\n            HashMap input = new HashMap();" +
								        "\n            input.put(\"beObject\", defaultUser);" +
								        "\n            input.put(\"editable\", new Boolean(true));" +
								        "\n            RefForm form = (RefForm)@page.getElement(@page.getEntityUiid()); " +
								        "\n            form.ui2Data(input);\n" +
								        "\n            defaultUser = (" + beImplName + ")input.get(\"beObject\");" +
								        "\n            String v = @page.getHidden(\"idUI\").getValue();" +
								        "\n            if (v != null && v.length() > 0) {" +
								        "\n            	   Long objectId = Long.valueOf(v);" +
								        "\n            	   defaultUser.setId(objectId.longValue());" +
								        "\n            }" +
								        "\n            if (defaultUser.getId() == 0) {" +
								        "\n                System.out.println(\"created object: \" + defaultUser);" +
								        "\n            } else {" +
								        "\n                System.out.println(\"updated object: \" + defaultUser);" +
								        "\n            }\n" +
								        "\n            form.closeIfinWindows();" +
								        "\n            @page.removeForm(@page.getEntityUiid()); " +
								        "\n        }");
			
			ajaxCall.setExp(expr);
			saveFunc.getOps().add(ajaxCall);
			
			OpInvokeWorkflowType invokeWorkflow = new OpInvokeWorkflowType();
			invokeWorkflow.setEventProducer(uiform.getEntityName() + "Save");
			ExpressionType exprn = new ExpressionType();
			exprn.setExpressionString(  "\n        import java.util.HashMap;" + 
										"\n        import org.shaolin.uimaster.page.AjaxContext;" +
								        "\n        import org.shaolin.uimaster.page.ajax.*;" +
								        "\n        import " + beImpl + ";" +
								        "\n        { " +
								        "\n        }");
			invokeWorkflow.setCondition(exprn);
			NameExpressionType ne = new NameExpressionType();
			ne.setName("input1");
			invokeWorkflow.getOutDataMappings().add(ne);
			saveFunc.getOps().add(invokeWorkflow);
			
			uiform.getEventHandlers().add(saveFunc);
			
			FunctionType cancelFunc = new FunctionType();
			cancelFunc.setFunctionName("Cancel");
			OpCallAjaxType ajaxCall1 = new OpCallAjaxType();
			
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd-HHmmss");
			ajaxCall1.setName("cancelDetail-" + sdf1.format(new Date()));
			ExpressionType expr1 = new ExpressionType();
			expr1.setExpressionString(  "\n        import org.shaolin.uimaster.page.AjaxContext;" + 
								        "\n        import org.shaolin.uimaster.page.ajax.*;" +
								        "\n        { " +
								        "\n            RefForm form = (RefForm)@page.getElement(@page.getEntityUiid()); " +
								        "\n            form.closeIfinWindows(true);" +
								        "\n            @page.removeForm(@page.getEntityUiid()); " +
								        "\n        }");
			ajaxCall1.setExp(expr1);
			cancelFunc.getOps().add(ajaxCall1);
			uiform.getEventHandlers().add(cancelFunc);
			
			UIPanelType rootPanel = new UIPanelType();
    		rootPanel.setUIID("Form");
    		createUIEntityForm(entityName, members, rootPanel, formPackage);
    		uiform.setBody(rootPanel);
			
			ODMappingType odMapping = new ODMappingType();
			
			ParamType var0 = new ParamType();
			var0.setName("beObject");
			var0.setCategory(VariableCategoryType.BUSINESS_ENTITY);
			TargetEntityType refBE0 = new TargetEntityType();
			refBE0.setEntityName(event.getEntity().getEntityName());
			var0.setType(refBE0);
			var0.setScope(ParamScopeType.IN_OUT);
			odMapping.getDataEntities().add(var0);
			
			ParamType var1 = new ParamType();
			var1.setName("editable");
			var1.setCategory(VariableCategoryType.JAVA_PRIMITIVE);
			TargetEntityType primitive = new TargetEntityType();
			primitive.setEntityName("java.lang.Boolean");
			var1.setType(primitive);
			var1.setScope(ParamScopeType.IN_OUT);
			odMapping.getDataEntities().add(var1);
			//UI parameters?
			addODMapping(members, odMapping.getComponentMappings());
			
			odMapping.setDataToUIMappingOperation(new ExpressionType());
			odMapping.setUIToDataMappingOperation(new ExpressionType());
			odMapping.getDataToUIMappingOperation().setExpressionString("{\n@odContext.executeAllMappings();\n}");
			odMapping.getUIToDataMappingOperation().setExpressionString("{\n@odContext.executeAllMappings();\n}");
			
			uiform.setMapping(odMapping);

			EntityUtil.marshaller(uiform, new FileWriter(formFile));
			if (logger.isInfoEnabled()) {
				logger.info("UI form component {}, whose generated file is {}", 
						entityName, formFile.getAbsolutePath());
			}
			entityManager.reloadEntity(uiform);
			
			/**
			// uipage
			UIPageType uipage = new UIPageType();
			uipage.setEntityName(pagePackage + "." + name);
			
			createPageForm(event.getEntity(), uipage);
			
			PageODMappingType odMappingType = new PageODMappingType();
			uipage.setODMapping(odMappingType);
			ParamType var1 = new ParamType();
			var1.setName("beObject");
			var1.setCategory(VariableCategoryType.BUSINESS_ENTITY);
			TargetEntityType refBE1 = new TargetEntityType();
			refBE1.setEntityName(event.getEntity().getEntityName());
			var1.setType(refBE1);
			var1.setScope(ParamScopeType.IN_OUT);
			odMappingType.getDataEntity().add(var1);
			addODMapping(members, odMappingType.getComponentMapping());
			
			PageInType pageIn = new PageInType();
			pageIn.setServerOperation(new ExpressionType());
			pageIn.getServerOperation().setExpressionString("{\n\n}");
			uipage.setIn(pageIn);
			
			PageOutType defaultOut = new PageOutType();
			defaultOut.setName("DefaultOut");
			defaultOut.setServerOperation(new ExpressionType());
			defaultOut.getServerOperation().setExpressionString("{\n\n}");
			uipage.getOut().add(defaultOut);
			
			EntityUtil.marshaller(uipage, new FileWriter(pageFile));
			
			if (logger.isInfoEnabled()) {
				logger.info("UI page component {}, whose generated file is {}", 
						entityName, pageFile.getAbsolutePath());
			}
			*/
			
			if (!event.getEntity().isNeedUITableEditor()) {
				return;
			}
			
			createTableEditorForm(formDir, members, formPackage, name, beImpl, beImplName, entityName);
			
		} catch (Exception e) {
			logger.error("Error when generate uipage for " + entityName,
					e);
		}
		
	}

	private void createTableEditorForm(File formDir, List<MemberType> members, String formPackage,
			String name, String beImpl, String beImplName, String entityName) throws Exception {
		
		UIEntity uiform = new UIEntity();
		uiform.setEntityName(formPackage + "." + name + "Table");
		
		FunctionType createItemFunc = new FunctionType();
		createItemFunc.setFunctionName("createItem");
		OpCallAjaxType ajaxCall = new OpCallAjaxType();
		ajaxCall.setName("createItem_" + (new Random()).nextInt());
		ExpressionType expr = new ExpressionType();
		expr.setExpressionString(   "\n        import org.shaolin.uimaster.page.AjaxContext;" +
							        "\n        import org.shaolin.uimaster.page.ajax.*;" +
							        "\n        import " + beImpl + ";" +
							        "\n        { " +
							        "\n            Table tableWidget = (Table)@page.getElement(\"itemTable\");" +
		                            "\n            tableWidget.addRow(new " + beImplName + "());" +
							        "\n        }");
		
		ajaxCall.setExp(expr);
		createItemFunc.getOps().add(ajaxCall);
		uiform.getEventHandlers().add(createItemFunc);
		
		FunctionType deleteItemFunc = new FunctionType();
		deleteItemFunc.setFunctionName("deleteItem");
		OpCallAjaxType ajaxCall1 = new OpCallAjaxType();
		ajaxCall1.setName("deleteItem_" + (new Random()).nextInt());
		ExpressionType expr1 = new ExpressionType();
		expr1.setExpressionString(   "\n        import org.shaolin.uimaster.page.AjaxContext;" +
							        "\n        import org.shaolin.uimaster.page.ajax.*;" +
							        "\n        import " + beImpl + ";" +
							        "\n        { " +
							        "\n            Table tableWidget = (Table)@page.getElement(\"itemTable\");" +
		                            "\n            if (tableWidget.getSelectedRow() == null) {" +
		                            "\n                return;" +
		                            "\n            }" +
		                            "\n            tableWidget.deleteRow(tableWidget.getSelectedIndex());" +
							        "\n        }");
		
		ajaxCall1.setExp(expr1);
		deleteItemFunc.getOps().add(ajaxCall1);
		uiform.getEventHandlers().add(deleteItemFunc);
		
		FunctionType saveFunc = new FunctionType();
		saveFunc.setFunctionName("Save");
		
		OpExecuteScriptType scriptCall = new OpExecuteScriptType();
		scriptCall.setExpressionString("{this.itemTable.syncBodyDataToServer();}");
		saveFunc.getOps().add(scriptCall);
		
		OpCallAjaxType ajaxCall2 = new OpCallAjaxType();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
		ajaxCall2.setName("saveItem-" + sdf.format(new Date()));
		ExpressionType expr2 = new ExpressionType();
		expr2.setExpressionString(   "\n        import org.shaolin.uimaster.page.AjaxContext;" +
							        "\n        import org.shaolin.uimaster.page.ajax.*;" +
							        "\n        import " + beImpl + ";" +
							        "\n        import java.util.List;" +
							        "\n        { " +
							        "\n            long parentId = 0;" + 
							        "\n            String pid = @page.getHidden(\"parentIdUI\").getValue();" +
							        "\n            if (pid != null && pid.length() > 0) {" +
							        "\n                parentId = Long.valueOf(pid).longValue();" +
							        "\n            }" +
							        "\n            Table tableWidget = @page.getTable(\"itemTable\");" +
							        "\n            if (tableWidget.getAddItems() != null && tableWidget.getAddItems().size() > 0) {" +
							        "\n            	List items = tableWidget.getAddItems();" +
							        "\n            	for (int i=0;i<items.size();i++) {" +
							        "\n            		" + beImplName + " beObject = (" + beImplName + ")items.get(i);" +
							        "\n                   	beObject.setParentId(parentId);" +
							        "\n                    if (beObject.getId() == 0) {" +
							        "\n                        System.out.println(\"create item: \" + beObject);" +
							        "\n                    } else {" +
							        "\n                        System.out.println(\"update item: \" + beObject);" +
							        "\n                    }" +
							        "\n            	}" +
							        "\n            }" +
							        "\n            if (tableWidget.getDeleteItems() != null) {" +
							        "\n            List items = tableWidget.getDeleteItems();" +
							        "\n            for (int i=0;i<items.size();i++) {" +
							        "\n            		" + beImplName + " beObject = (" + beImplName + ")items.get(i);" +
							        "\n                   	beObject.setParentId(parentId);" +
							        "\n                    if (beObject.getId() > 0) {" +
							        "\n                        System.out.println(\"delete item: \" +beObject);" +
							        "\n                    }" +
							        "\n            	}" +
							        "\n            }" +
							        "\n            if (tableWidget.getUpdateItems() != null) {" +
							        "\n            	List items = tableWidget.getUpdateItems();" +
							        "\n            	for (int i=0;i<items.size();i++) {" +
							        "\n            		" + beImplName + " beObject = (" + beImplName + ")items.get(i);" +
							        "\n                   	beObject.setParentId(parentId);" +
							        "\n                    if (beObject.getId() == 0) {" +
							        "\n                        System.out.println(\"create item: \" +beObject);" +
							        "\n                    } else {" +
							        "\n                        System.out.println(\"update item: \" +beObject);" +
							        "\n                    }" +
							        "\n            	}" +
							        "\n            }" + 
							        "\n            RefForm form = (RefForm)@page.getElement(@page.getEntityUiid()); " +
							        "\n            form.closeIfinWindows();" +
							        "\n            @page.removeForm(@page.getEntityUiid()); " +
							        "\n        }");
		
		ajaxCall2.setExp(expr2);
		saveFunc.getOps().add(ajaxCall2);
		uiform.getEventHandlers().add(saveFunc);
		
		
		FunctionType cancelFunc = new FunctionType();
		cancelFunc.setFunctionName("Cancel");
		OpCallAjaxType ajaxCall5 = new OpCallAjaxType();
		ajaxCall5.setName("cancelDetail" + (new Random()).nextInt());
		ExpressionType expr5 = new ExpressionType();
		expr5.setExpressionString(  "\n        import org.shaolin.uimaster.page.AjaxContext;" + 
							        "\n        import org.shaolin.uimaster.page.ajax.*;" +
							        "\n        { " +
							        "\n            RefForm form = (RefForm)@page.getElement(@page.getEntityUiid()); " +
							        "\n            form.closeIfinWindows(true);" +
							        "\n            @page.removeForm(@page.getEntityUiid()); " +
							        "\n        }");
		ajaxCall5.setExp(expr5);
		cancelFunc.getOps().add(ajaxCall5);
		uiform.getEventHandlers().add(cancelFunc);
		
		UIPanelType rootPanel = new UIPanelType();
		rootPanel.setUIID("Form");
		createTableUIEntityForm(entityName, members, rootPanel, formPackage);
		uiform.setBody(rootPanel);
		
		ODMappingType odMapping = new ODMappingType();
		
		ParamType var0 = new ParamType();
		var0.setName("list");
		var0.setCategory(VariableCategoryType.JAVA_CLASS);
		TargetEntityType refBE0 = new TargetEntityType();
		refBE0.setEntityName("java.util.List");
		var0.setType(refBE0);
		var0.setScope(ParamScopeType.IN_OUT);
		odMapping.getDataEntities().add(var0);
		
		ParamType var1 = new ParamType();
		var1.setName("parentId");
		var1.setCategory(VariableCategoryType.JAVA_PRIMITIVE);
		TargetEntityType plong = new TargetEntityType();
		plong.setEntityName("java.lang.Long");
		var1.setType(plong);
		var1.setScope(ParamScopeType.IN_OUT);
		odMapping.getDataEntities().add(var1);
		
		
		ParamType var2 = new ParamType();
		var2.setName("editable");
		var2.setCategory(VariableCategoryType.JAVA_PRIMITIVE);
		TargetEntityType primitive = new TargetEntityType();
		primitive.setEntityName("java.lang.Boolean");
		var2.setType(primitive);
		var2.setScope(ParamScopeType.IN_OUT);
		odMapping.getDataEntities().add(var2);
		//UI parameters?
		
		SimpleComponentMappingType scm = new SimpleComponentMappingType();
		TargetEntityType t = new TargetEntityType();
		t.setEntityName("org.shaolin.uimaster.page.od.rules.UITextWithNumber");
		scm.setMappingRule(t);
		scm.setName("simpleMapping0");
		odMapping.getComponentMappings().add(scm);
		
		UIComponentParamType uiParam = new UIComponentParamType();
		uiParam.setParamName(IODMappingConverter.UI_WIDGET_TYPE);
		uiParam.setComponentPath("parentIdUI");
		scm.getUIComponents().add(uiParam);
		
		ComponentParamType dataParam = new ComponentParamType();
		dataParam.setParamName("Number");
		dataParam.setComponentPath("parentId");
		scm.getDataComponents().add(dataParam);
		
		odMapping.setDataToUIMappingOperation(new ExpressionType());
		odMapping.setUIToDataMappingOperation(new ExpressionType());
		odMapping.getDataToUIMappingOperation().setExpressionString("{\n@odContext.executeAllMappings();\n}");
		odMapping.getUIToDataMappingOperation().setExpressionString("{\n@odContext.executeAllMappings();\n}");
		
		uiform.setMapping(odMapping);

		String uiformName = name + "Table.form";
		File formFile = new File(formDir, uiformName);
		EntityUtil.marshaller(uiform, new FileWriter(formFile));
		if (logger.isInfoEnabled()) {
			logger.info("UI form component {}, whose generated file is {}", 
					entityName, formFile.getAbsolutePath());
		}
		entityManager.reloadEntity(uiform);
		
	}
	
	public void notify(EntityUpdatedEvent<BusinessEntityType, BEDiagram> event) {
		
	}

	public Class<BusinessEntityType> getEventType() {
		return BusinessEntityType.class;
	}
	
	@Override
	public void notifyLoadFinish(DiagramType diagram) {
		BEDiagram beDiagram = (BEDiagram)diagram;
		if (beDiagram.getBePackage().indexOf(option.getBundleName()) == -1) {
			return;
		}
		
		if (isSkip) {
			return;
		}
		
		File entityDir = option.getEntitiesDirectory();
		String bePackage = beDiagram.getBePackage();
		String pagePackage = bePackage.replace("be", "page");
		String formPackage = pagePackage.replace("page", "form");
		
		final File formDir = new File(entityDir, formPackage.replace('.', '/'));
		if (!formDir.exists()) {
			formDir.mkdirs();
		} 
		
		final File pageDir = new File(entityDir, pagePackage.replace('.', '/'));
		if (!pageDir.exists()) {
			pageDir.mkdirs();
		} 
		this.entityManager.addPostTask(new Runnable() {
			@Override
			public void run() {
				try {
					entityManager.reloadDir(formDir);
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					entityManager.reloadDir(pageDir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void notifyAllLoadFinish() {
	}

	
	private void createPageForm(BusinessEntityType beEntity, List<MemberType> members, UIPage uipage) {
		UIEntity uiEntity = new UIEntity();
		UIPanelType rootPanel = new UIPanelType();
		rootPanel.setUIID("Form");
		
		// add search criteria
		int row = 0;
		int col = 0;
		UIPanelType searchPanel = new UIPanelType();
		searchPanel.setUIID("searchPanel");
		for (MemberType member: members) {
			if (member.getType() instanceof BECollectionType
					|| member.getType() instanceof BinaryType
					|| member.getType() instanceof FileType
					|| member.getType() instanceof BEObjRefType
					|| member.isPk()) {
				continue;
			}
			
			String uiid = "beObject."+member.getName();
			UIComponentType uiComponent = UIPageUtil.createUIComponent(UIPageUtil.getDefaultUIType(member.getType()));
			uiComponent.setUIID(uiid);
			searchPanel.getComponents().add(uiComponent);
			
			ComponentConstraintType cc = new ComponentConstraintType();
			TableLayoutConstraintType c = new TableLayoutConstraintType();
			c.setX(row);
			c.setY(col);
			c.setAlign("FULL");
			cc.setConstraint(c);
			cc.setComponentId(uiid);
			searchPanel.getLayoutConstraints().add(cc);
			
			if (++col > 3) {
				row ++;
				col = 0;
			}
		}
		// add search button.
		FunctionType function = new FunctionType();
		FunctionCallType funcCall = new FunctionCallType();
		ClickListenerType click = new ClickListenerType();
		UIButtonType searchButton = new UIButtonType();
		StringPropertyType text = new StringPropertyType();
		text.setValue("Search");
		searchButton.setText(text);
		funcCall.setFunctionName("Search");
		click.setHandler(funcCall);
		function.setFunctionName("Search");
		function.getOps();//TODO:
		function.getVars();
		searchButton.getEventListeners().add(click);
		uiEntity.getEventHandlers().add(function);
		UIPageUtil.createLayout(searchPanel, row + 1, 4);
		
		// add result table.
		UIPanelType resultPanel = new UIPanelType();
		resultPanel.setUIID("resultPanel");
		UITableType uiTable = new UITableType();
		uiTable.setUIID("beUITable");
		//TODO: more details in table widget.
		resultPanel.getComponents().add(uiTable);
		UIPageUtil.createLayout(resultPanel, 1, 1);
		
		rootPanel.getComponents().add(searchPanel);
		rootPanel.getComponents().add(resultPanel);
		
		ComponentConstraintType ccSearchPanel = new ComponentConstraintType();
		TableLayoutConstraintType cSearchPanel = new TableLayoutConstraintType();
		cSearchPanel.setX(0);
		cSearchPanel.setY(0);
		cSearchPanel.setAlign("FULL");
		ccSearchPanel.setConstraint(cSearchPanel);
		ccSearchPanel.setComponentId(searchPanel.getUIID());
		rootPanel.getLayoutConstraints().add(ccSearchPanel);
		
		ComponentConstraintType ccResultPanel = new ComponentConstraintType();
		TableLayoutConstraintType cResultPanel = new TableLayoutConstraintType();
		cResultPanel.setX(0);
		cResultPanel.setY(1);
		cResultPanel.setAlign("FULL");
		ccResultPanel.setConstraint(cResultPanel);
		ccResultPanel.setComponentId(resultPanel.getUIID());
		rootPanel.getLayoutConstraints().add(ccResultPanel);
		
		UIPageUtil.createLayout(rootPanel, 2, 1);
		
		uiEntity.setBody(rootPanel);
		uipage.setUIEntity(uiEntity);
	}

	/**
	 * Create ui components n*2 matrix for entity view/update.
	 * 
	 * @param page
	 * @param parent
	 */
	private void createUIEntityForm(String entityName, List<MemberType> members, UIPanelType parent, String formPackage) {
		int row = 0;
		int col = 2;
		
		UIPageUtil.createLayout(parent, 2, 1);
		UIPanelType fieldPanel = new UIPanelType();
		fieldPanel.setUIID("fieldPanel");
		parent.getComponents().add(fieldPanel);
		
		for (MemberType m: members) {
			if (m.isPk()) {
				UIHiddenType idType = new UIHiddenType();
				idType.setUIID("idUI");
				idType.setSecure(new BooleanPropertyType());
				idType.getSecure().setValue(true);
				fieldPanel.getComponents().add(idType);
				continue;
			}
			
			// my field.
			String uiid = m.getName() + "UI";
			UILabelType uiLabel = new UILabelType();
			ResourceBundlePropertyType i18nKey = new ResourceBundlePropertyType();
			// directly reuse the be keys. this will reduce lots of work.
			i18nKey.setBundle(option.geti18nBundleName());
			i18nKey.setKey(entityName + "." +  m.getName());
			uiLabel.setText(i18nKey);
			uiLabel.setUIID(uiid + "Label");
			uiLabel.setUIStyle("uimaster_leftform_widget");
			fieldPanel.getComponents().add(uiLabel);
			
			UIComponentType uiComponent = UIPageUtil.createUIComponent(UIPageUtil.getDefaultUIType(m.getType()));
			uiComponent.setUIID(uiid);
			if (m.getType() instanceof BEObjRefType && uiComponent instanceof UIReferenceEntityType) {
				UIReferenceEntityType refEntity = (UIReferenceEntityType)uiComponent;
				String beName = ((BEObjRefType)m.getType()).getTargetEntity().getEntityName();
				if(beName.lastIndexOf('.') == -1) {
					TargetEntityType value = new TargetEntityType();
					value.setEntityName(formPackage + "." + beName);
					refEntity.setReferenceEntity(value);
				}
			}
			if (!(uiComponent instanceof UISelectComponentType)) {
				uiComponent.setUIStyle("uimaster_rightform_widget");
			}
			fieldPanel.getComponents().add(uiComponent);
			
			ComponentConstraintType ccLabel = new ComponentConstraintType();
			TableLayoutConstraintType cLabel = new TableLayoutConstraintType();
			cLabel.setX(0);
			cLabel.setY(row);
			cLabel.setAlign("FULL");
			cLabel.setCellUIClass("uimaster_leftform_cell");
			ccLabel.setConstraint(cLabel);
			ccLabel.setComponentId(uiid + "Label");
			fieldPanel.getLayoutConstraints().add(ccLabel);
			
			ComponentConstraintType ccValue = new ComponentConstraintType();
			TableLayoutConstraintType cValue = new TableLayoutConstraintType();
			cValue.setX(1);
			cValue.setY(row);
			cValue.setAlign("FULL");
			cValue.setCellUIClass("uimaster_rightform_cell");
			ccValue.setConstraint(cValue);
			ccValue.setComponentId(uiid);
			fieldPanel.getLayoutConstraints().add(ccValue);
			row ++;
		}
		// create layout
		UIPageUtil.createLayout(fieldPanel, row, col);
		
		UIPanelType actionPanel = new UIPanelType();
		actionPanel.setUIID("actionPanel");
		parent.getComponents().add(actionPanel);
		
		ComponentConstraintType fieldPanelLabel = new ComponentConstraintType();
		TableLayoutConstraintType fieldTLabel = new TableLayoutConstraintType();
		fieldTLabel.setX(0);
		fieldTLabel.setY(0);
		fieldTLabel.setAlign("FULL");
		fieldPanelLabel.setConstraint(fieldTLabel);
		fieldPanelLabel.setComponentId(fieldPanel.getUIID());
		parent.getLayoutConstraints().add(fieldPanelLabel);
		
		ComponentConstraintType actionPanelLabel = new ComponentConstraintType();
		TableLayoutConstraintType actionTLValue = new TableLayoutConstraintType();
		actionTLValue.setX(0);
		actionTLValue.setY(1);
		actionTLValue.setAlign("FULL");
		actionPanelLabel.setConstraint(actionTLValue);
		actionPanelLabel.setComponentId(actionPanel.getUIID());
		parent.getLayoutConstraints().add(actionPanelLabel);
		
		
		UIButtonType okbtn = new UIButtonType();
		okbtn.setUIID("okbtn");
		ResourceBundlePropertyType i18nKey = new ResourceBundlePropertyType();
		i18nKey.setBundle("Common");
		i18nKey.setKey("OKbtn");
		okbtn.setText(i18nKey);
		ClickListenerType clickListener = new ClickListenerType();
		FunctionCallType caller = new FunctionCallType();
		caller.setFunctionName("Save");
		clickListener.setHandler(caller);
		okbtn.getEventListeners().add(clickListener);
		
		ExpressionPropertyType exprType = new ExpressionPropertyType();
		ExpressionType expr = new ExpressionType();
		expr.setExpressionString("{return $editable;}");
		exprType.setExpression(expr);
		okbtn.setEditable(exprType);
		actionPanel.getComponents().add(okbtn);
		
		UIButtonType cancelbtn = new UIButtonType();
		cancelbtn.setUIID("cancelbtn");
		ResourceBundlePropertyType i18nKey1 = new ResourceBundlePropertyType();
		i18nKey1.setBundle("Common");
		i18nKey1.setKey("Cancelbtn");
		cancelbtn.setText(i18nKey1);
		ClickListenerType clickListener1 = new ClickListenerType();
		FunctionCallType caller1 = new FunctionCallType();
		caller1.setFunctionName("Cancel");
		clickListener1.setHandler(caller1);
		cancelbtn.getEventListeners().add(clickListener1);
		actionPanel.getComponents().add(cancelbtn);
		
		ComponentConstraintType okLabel = new ComponentConstraintType();
		TableLayoutConstraintType cLabel = new TableLayoutConstraintType();
		cLabel.setX(0);
		cLabel.setY(0);
		cLabel.setAlign("FULL");
		okLabel.setConstraint(cLabel);
		okLabel.setComponentId(okbtn.getUIID());
		actionPanel.getLayoutConstraints().add(okLabel);
		
		ComponentConstraintType cancelLabel = new ComponentConstraintType();
		TableLayoutConstraintType cValue = new TableLayoutConstraintType();
		cValue.setX(1);
		cValue.setY(0);
		cValue.setAlign("FULL");
		cancelLabel.setConstraint(cValue);
		cancelLabel.setComponentId(cancelbtn.getUIID());
		actionPanel.getLayoutConstraints().add(cancelLabel);
		
		// create layout
		UIPageUtil.createLayout(actionPanel, 1, 2);
	}
	
	private void createTableUIEntityForm(String entityName, List<MemberType> members, UIPanelType parent, String formPackage) {
		UIPageUtil.createLayout(parent, 2, 1);
		UIPanelType fieldPanel = new UIPanelType();
		fieldPanel.setUIID("fieldPanel");
		parent.getComponents().add(fieldPanel);
		
		UIHiddenType parentId = new UIHiddenType();
		parentId.setUIID("parentIdUI");
		parentId.setSecure(new BooleanPropertyType());
		parentId.getSecure().setValue(true);
		fieldPanel.getComponents().add(parentId);
		
		UITableType uitable = new UITableType();
		uitable.setUIID("itemTable");
		BooleanPropertyType bp = new BooleanPropertyType();
		bp.setValue(true);
		uitable.setEditable(bp);
		uitable.setBeElement(entityName);
		uitable.setDefaultRowSize(20);
		uitable.setShowFilter(false);
		uitable.setShowActionBar(true);
		uitable.setEditableCell(true);
		uitable.setInitQuery(new ExpressionPropertyType());
		uitable.getInitQuery().setExpression(new ExpressionType());
		uitable.getInitQuery().getExpression().setExpressionString(
				"\n        import java.util.List;" +
				"\n        {" +
				"\n            return $list;" +
				"\n        }");
		uitable.setQuery(new ExpressionPropertyType());
		uitable.getQuery().setExpression(new ExpressionType());
		uitable.getQuery().getExpression().setExpressionString(
				"\n        import java.util.List;" +
				"\n        {" +
				"\n            return $table.getListData();" +
				"\n        }");
		uitable.setTotalCount(new ExpressionPropertyType());
		uitable.getTotalCount().setExpression(new ExpressionType());
		uitable.getTotalCount().getExpression().setExpressionString(
				"\n        import java.util.List;" +
				"\n        {" +
				"\n            return $table.getListData().size();" +
				"\n        }");
		
		for (MemberType m: members) {
			if (m.isPk()) {
				continue;
			}
			
			UITableColumnType column = new UITableColumnType();
			column.setBeFieldId("rowBE." + m.getName());
			column.setUiType(new UITableColHTMLType());
			column.getUiType().setType("Text");
			ResourceBundlePropertyType i18nKey = new ResourceBundlePropertyType();
			i18nKey.setBundle(option.geti18nBundleName());
			i18nKey.setKey(entityName + "." +  m.getName());
			column.setTitle(i18nKey);
			uitable.getColumns().add(column);
		}
		uitable.setDefaultActions(new UITableDefaultActionType());
		uitable.getDefaultActions().setDefaultNewAction("createItem");
		uitable.getDefaultActions().setDefaultDeleteAction("deleteItem");
		fieldPanel.getComponents().add(uitable);
		
		ComponentConstraintType fieldPanelLabel0 = new ComponentConstraintType();
		TableLayoutConstraintType fieldTLabel0 = new TableLayoutConstraintType();
		fieldTLabel0.setX(0);
		fieldTLabel0.setY(0);
		fieldTLabel0.setAlign("FULL");
		fieldPanelLabel0.setConstraint(fieldTLabel0);
		fieldPanelLabel0.setComponentId(uitable.getUIID());
		fieldPanel.getLayoutConstraints().add(fieldPanelLabel0);
		
		
		// create layout
		UIPageUtil.createLayout(fieldPanel, 1, 1);
		
		UIPanelType actionPanel = new UIPanelType();
		actionPanel.setUIID("actionPanel");
		parent.getComponents().add(actionPanel);
		
		ComponentConstraintType fieldPanelLabel = new ComponentConstraintType();
		TableLayoutConstraintType fieldTLabel = new TableLayoutConstraintType();
		fieldTLabel.setX(0);
		fieldTLabel.setY(0);
		fieldTLabel.setAlign("FULL");
		fieldPanelLabel.setConstraint(fieldTLabel);
		fieldPanelLabel.setComponentId(fieldPanel.getUIID());
		parent.getLayoutConstraints().add(fieldPanelLabel);
		
		ComponentConstraintType actionPanelLabel = new ComponentConstraintType();
		TableLayoutConstraintType actionTLValue = new TableLayoutConstraintType();
		actionTLValue.setX(0);
		actionTLValue.setY(1);
		actionTLValue.setAlign("FULL");
		actionPanelLabel.setConstraint(actionTLValue);
		actionPanelLabel.setComponentId(actionPanel.getUIID());
		parent.getLayoutConstraints().add(actionPanelLabel);
		
		
		UIButtonType okbtn = new UIButtonType();
		okbtn.setUIID("okbtn");
		ResourceBundlePropertyType i18nKey = new ResourceBundlePropertyType();
		i18nKey.setBundle("Common");
		i18nKey.setKey("OKbtn");
		okbtn.setText(i18nKey);
		ClickListenerType clickListener = new ClickListenerType();
		FunctionCallType caller = new FunctionCallType();
		caller.setFunctionName("Save");
		clickListener.setHandler(caller);
		okbtn.getEventListeners().add(clickListener);
		
		ExpressionPropertyType exprType = new ExpressionPropertyType();
		ExpressionType expr = new ExpressionType();
		expr.setExpressionString("{return $editable;}");
		exprType.setExpression(expr);
		okbtn.setEditable(exprType);
		actionPanel.getComponents().add(okbtn);
		
		UIButtonType cancelbtn = new UIButtonType();
		cancelbtn.setUIID("cancelbtn");
		ResourceBundlePropertyType i18nKey1 = new ResourceBundlePropertyType();
		i18nKey1.setBundle("Common");
		i18nKey1.setKey("Cancelbtn");
		cancelbtn.setText(i18nKey1);
		ClickListenerType clickListener1 = new ClickListenerType();
		FunctionCallType caller1 = new FunctionCallType();
		caller1.setFunctionName("Cancel");
		clickListener1.setHandler(caller1);
		cancelbtn.getEventListeners().add(clickListener1);
		actionPanel.getComponents().add(cancelbtn);
		
		ComponentConstraintType okLabel = new ComponentConstraintType();
		TableLayoutConstraintType cLabel = new TableLayoutConstraintType();
		cLabel.setX(0);
		cLabel.setY(0);
		cLabel.setAlign("FULL");
		okLabel.setConstraint(cLabel);
		okLabel.setComponentId(okbtn.getUIID());
		actionPanel.getLayoutConstraints().add(okLabel);
		
		ComponentConstraintType cancelLabel = new ComponentConstraintType();
		TableLayoutConstraintType cValue = new TableLayoutConstraintType();
		cValue.setX(1);
		cValue.setY(0);
		cValue.setAlign("FULL");
		cancelLabel.setConstraint(cValue);
		cancelLabel.setComponentId(cancelbtn.getUIID());
		actionPanel.getLayoutConstraints().add(cancelLabel);
		
		// create layout
		UIPageUtil.createLayout(actionPanel, 1, 2);
	}
	
	private void addODMapping(List<MemberType> members, List<ComponentMappingType> list) {
		int count = 0;
		for (MemberType m: members) {
			SimpleComponentMappingType scm = new SimpleComponentMappingType();
			TargetEntityType t = new TargetEntityType();
			t.setEntityName(UIPageUtil.getDefaultODRuleType(m.getType()));
			scm.setMappingRule(t);
			scm.setName("simpleMapping"+ count++);
			list.add(scm);
			
			ParamType var = new ParamType();
			var.setName(m.getName());
			var.setCategory(BEUtil.covert(m.getType()));
			
			
			UIComponentParamType uiParam = new UIComponentParamType();
			uiParam.setParamName(IODMappingConverter.UI_WIDGET_TYPE);
			uiParam.setComponentPath(m.getName() + "UI");
			scm.getUIComponents().add(uiParam);
			
			if (m.isPk()) {
				ExpressionParamType exprParam = new ExpressionParamType();
				ExpressionType expr = new ExpressionType();
				expr.setExpressionString("$beObject.getId()");
				exprParam.setExpression(expr);
				exprParam.setParamName("Number");
				scm.getDataComponents().add(exprParam);
				continue;
			}
			
			String parameter12 = "";
			if (m.getType() instanceof CEObjRefType) {
				parameter12 = ((CEObjRefType) m.getType()).getTargetEntity().getEntityName();
			} 
			String parameter1 = "beObject." + m.getName();
			Map<String, String> params = BaseRulesHelper.getRequiredDataParameter(
					t.getEntityName(), parameter1, parameter12);
			
			Set<Entry<String, String>> entries = params.entrySet();
			for (Entry<String, String> entry : entries) {
				if (entry.getKey().equals("CEType")) {
					ExpressionParamType exprParam = new ExpressionParamType();
					ExpressionType expr = new ExpressionType();
					expr.setExpressionString("\"" + entry.getValue() + "\"");
					exprParam.setExpression(expr);
					exprParam.setParamName("CEType");
					scm.getDataComponents().add(exprParam);
				} else {
					ComponentParamType dataParam = new ComponentParamType();
					dataParam.setParamName(entry.getKey());
					dataParam.setComponentPath(entry.getValue());
					scm.getDataComponents().add(dataParam);
				}
			}
			if ("org.shaolin.uimaster.page.od.rules.UITextWithDate".equals(t.getEntityName())
					&& m.getType() instanceof DateTimeType) {
				ExpressionParamType exprParam = new ExpressionParamType();
				ExpressionType expr = new ExpressionType();
				expr.setExpressionString("true");
				exprParam.setExpression(expr);
				exprParam.setParamName("IsDateOnly");
				scm.getDataComponents().add(exprParam);
			}
		}
	}
	
}
