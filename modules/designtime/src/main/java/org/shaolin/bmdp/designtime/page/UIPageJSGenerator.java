package org.shaolin.bmdp.designtime.page;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.shaolin.bmdp.datamodel.common.DiagramType;
import org.shaolin.bmdp.datamodel.page.FunctionReconfigurationType;
import org.shaolin.bmdp.datamodel.page.PageInType;
import org.shaolin.bmdp.datamodel.page.PageOutType;
import org.shaolin.bmdp.datamodel.page.ReconfigurationType;
import org.shaolin.bmdp.datamodel.page.UIBaseType;
import org.shaolin.bmdp.datamodel.page.UIContainerType;
import org.shaolin.bmdp.datamodel.page.UIEntity;
import org.shaolin.bmdp.datamodel.page.UIPage;
import org.shaolin.bmdp.datamodel.page.UIPanelType;
import org.shaolin.bmdp.datamodel.page.UIReferenceEntityType;
import org.shaolin.bmdp.designtime.tools.GeneratorOptions;
import org.shaolin.bmdp.designtime.tools.PumpWriter;
import org.shaolin.bmdp.runtime.entity.EntityAddedEvent;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.entity.EntityUpdatedEvent;
import org.shaolin.bmdp.runtime.entity.IEntityEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UIPageJSGenerator extends UIFormJSGenerator0 implements
		IEntityEventListener<UIPage, DiagramType> {
	
	private static Logger logger = LoggerFactory.getLogger(UIPageJSGenerator.class);

	private final PumpWriter out = new PumpWriter();

	private List<String> jsText = null;
	private boolean clear = false;
	private String jsName = null;
	private Map referenceMap = null;
	private ArrayList refJS;

	private EntityManager entityManager;

	public UIPageJSGenerator() {
		super();
	}

	public UIPageJSGenerator(GeneratorOptions option) {
		super(option);
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void notify(EntityAddedEvent<UIPage, DiagramType> event) {
		String entityName = event.getEntity().getEntityName();
		if (entityName.indexOf(option.getBundleName()) == -1) {
			return;
		}
		
		File jsRoot = new File(option.getWebDir(), "js");
		out.write("\n\n");
		try {

			String jsName = event.getEntity().getEntityName();
			File jsFile = getJsFile(jsRoot, jsName);
			out.write("!!!!file ");
			out.print(jsFile.getAbsolutePath());

			this.clear = false;
			this.jsName = UIPageUtil.getDefaultJspName(event.getEntity()
					.getEntityName());
			generateJs(jsRoot, event.getEntity());
		} catch (Exception ex) {
			logger.error("Error generating Java Class for UIPage entity "
					+ entityName, ex);
		} finally {
			out.write("\n\n");
			out.finish();
		}
	}

	public void notify(EntityUpdatedEvent<UIPage, DiagramType> event) {
	}

	public Class<UIPage> getEventType() {
		return UIPage.class;
	}

	@Override
	public void notifyLoadFinish(DiagramType diagram) {
		
	}
	
	public void notifyAllLoadFinish() {

	}

	private static File getJsFile(File jsRoot, String jsName) {
		return new File(jsRoot, jsName.replace('.', File.separatorChar) + ".js");
	}

	private void generateJs(File rootPath, UIPage uipageData) {
		try {
			refJS = new ArrayList();
			entityName = uipageData.getEntityName();
			Map pageOuts = null;
			UIBaseType uiEntity = (UIBaseType) uipageData.getUIEntity();
			pageOuts = new HashMap();

			List<PageOutType> outs = uipageData.getOuts();
			for (PageOutType pageOut : outs) {
				pageOuts.put(pageOut.getFunctionName(), pageOut.getName());
			}

			if (uiEntity != null) {
				// Don't need the user js at this moment.
				// jsText = JSMerge.getSingleText(rootPath.getAbsolutePath(), jsName);
				refJS = new ArrayList();
				generateJsFile(out, uiEntity);
				genHandlerReference(out, uiEntity);
				genOutReference(out, uipageData);
				out.write("\n    Form.__AJAXSubmit = false;\n    ");

				out.write("\n    Form.__entityName=\"");
				out.print(uipageData.getEntityName());
				out.write("\";\n");

				genReturnJS(out);
				out.write("\n    /* EventHandler Functions */\n");
				genOtherFunc(out);
				genFunctionHead(out, pageOuts, uiEntity, referenceMap);
				generateAppJS(out, uipageData);
				JSMerge.clearFuncList();
			}
		} catch (Exception ex) {
			logger.error("Error generating JavaScript for UIEntity/UIPage "
					+ entityName, ex);
		}
	}

	public void generateJsFile(PrintWriter out, UIBaseType uiEntity)
			throws Exception {
		String jsEntityName = entityName.replace('.', '_');

		List commonComponent = new ArrayList();
		List reference = new ArrayList();
		List container = new ArrayList();
		referenceMap = new HashMap();

		UIContainerType rootPanel = null;
		if (uiEntity instanceof UIEntity) {
			rootPanel = ((UIEntity) uiEntity).getBody();
			divideContainer(rootPanel, commonComponent, reference, container,
					false);
		}

		for (int i = 0; i < reference.size(); i++) {
			UIReferenceEntityType rType = (UIReferenceEntityType) reference
					.get(i);
			List<ReconfigurationType> reconfigs = rType.getReconfigurations();
			for (ReconfigurationType reconfig : reconfigs) {
				if (reconfig instanceof FunctionReconfigurationType) {
					FunctionReconfigurationType ffType = (FunctionReconfigurationType) reconfig;
					String funcName = "this." + rType.getUIID() + "."
							+ ffType.getOriginFunctionName();
					referenceMap
							.put(ffType.getOverrideFunctionName(), funcName);
				}
			}
		}

		out.write("\n/* ");
		out.print(uiEntity.getRevision());
		out.write(" */\n/* auto generated constructor */\nfunction ");
		out.print(jsEntityName);
		out.write("(json)\n{\n    var prefix = (typeof(json) == \"string\") ? json : json.prefix; \n");

		if (uiEntity instanceof UIEntity) {
			genComponentJS(out, commonComponent, reference, container,
					(UIEntity) uiEntity);
			genRootPanelJS(out, (UIPanelType) rootPanel, commonComponent,
					reference, container, (UIEntity) uiEntity);

			out.write("\n    ");
			out.print(((UIEntity) uiEntity).getBody().getUIID());
			out.write(".user_constructor = function()\n    {\n        /* Construct_FIRST:");
			out.print(jsEntityName);
			out.write(" */\n");

			List result = null;
			if (jsText != null) {
				result = JSMerge.getConstructPlugin(jsText);
			}
			if (result != null && result.size() > 0) {
				genUserConstructor(out);
			} else if (((UIPanelType) rootPanel).getConstructorCode() != null) {
				out.write("\n        ");
				out.print(((UIPanelType) rootPanel).getConstructorCode());
				out.write("\n    ");
			}
			out.write("        /* Construct_LAST:");
			out.print(jsEntityName);
			out.write(" */\n    };\n");
		} else {
			out.write("\n    var Form = {}\n    Form.name = prefix;\n    Form.init = function()\n    {\n        /* Construct_FIRST:");
			out.print(jsEntityName);
			out.write(" */\n");

			genUserConstructor(out);

			out.write("        /* Construct_LAST:");
			out.print(jsEntityName);
			out.write(" */\n    };\n");
		}
	}

	// BAJavaScriptGContext returned.
	public void  generateAppJS(PrintWriter out, UIPage uiPage)
			throws Exception {
		//BAJavaScriptGContext generationContext = new BAJavaScriptGContext();
		String jsEntityName = entityName.replace('.', '_');
		String body = null;
		if (jsText != null) {
			body = JSMerge.getPartBody(entityName + ".initPageJs", jsText);
		}

		out.write("\n    function ");
		out.print(jsEntityName);
		out.write("_initPageJs(){/* Gen_First:");
		out.print(jsEntityName);
		out.write("_initPageJs */\n        var constraint_result = true;\n        var UIEntity = this;\n");

		if (body != null && !body.equals("")) {
			out.write("\n        ");
			out.print(body);
			out.write("\n");
		} else {
			PageInType pageIn = uiPage.getIn();
			if (pageIn != null && pageIn.getClientAction() != null) {
				out.print(pageIn.getClientAction().getExpressionString());
			}
		}

		out.write("\n    }/* Gen_Last:");
		out.print(jsEntityName);
		out.write("_initPageJs */\n\n");

		body = null;
		if (jsText != null) {
			body = JSMerge.getPartBody(entityName + ".finalizePageJs", jsText);
		}

		out.write("\n    function ");
		out.print(jsEntityName);
		out.write("_finalizePageJs(){/* Gen_First:");
		out.print(jsEntityName);
		out.write("_finalizePageJs */\n");

		if (body != null && !body.equals("")) {

			out.write("\n        ");
			out.print(body);
			out.write("\n");

		} else {
			if (uiPage.getFinalize() != null) {
				out.print(uiPage.getFinalize().getExpressionString());
			}
		}

		out.write("\n    }/* Gen_Last:");
		out.print(jsEntityName);
		out.write("_finalizePageJs */\n\n");

		List<PageOutType> outs = uiPage.getOuts();
		for (PageOutType oType : outs) {
			String functionName = oType.getName() + "_OutFunctionName";

			out.write("\n    function ");
			out.print(jsEntityName);
			out.write("_");
			out.print(functionName);
			out.write("(eventsource) {/* Gen_First:");
			out.print(jsEntityName);
			out.write("_");
			out.print(functionName);
			out.write(" */\n        var constraint_result = true;\n        var myForm;\n        if (this.formName != undefined)\n        {\n            myForm = document.forms[this.formName];\n        }\n        else\n        {\n            var p = this.Form.parentNode;\n            while(p.tagName != \"FORM\")\n                p = p.parentNode;\n            myForm = p;//document.forms[0];\n        }\n");

			body = null;
			if (jsText != null) {
				body = JSMerge.getPartBody(entityName + "." + functionName,
						jsText);
			}
			if (body != null && !body.equals("")) {
				out.write("\n");
				out.print(body);
				out.write("\n");
			} else {
				out.write("\n        var UIEntity = this;\n");
				if (oType.isValidate()) {
					out.write("\n        constraint_result = this.Form.validate();\n");
				}
				
				if (oType.getClientAction() != null) {
					out.print(oType.getClientAction().getExpressionString());
				}

				String frameName = oType.getFrameName();
				if (frameName == null) {
					frameName = "_self";
				}

				out.write("        \n        myForm._outname.value = \"");
				out.print(oType.getName());
				out.write("\";\n        myForm.target = \"");
				out.print(frameName);
				out.write("\";\n");

			}
			out.write("             \n        if ( (constraint_result == true || constraint_result == null) && (!ajax_execute_onerror) ) {\n");

			if (uiPage.isIsAjaxHandlingAllowed()) {
				out.write("            UIMaster.trigger2PhaseSubmit(myForm.action,myForm._pagename.value,myForm._outname.value,myForm.getAttribute('_framePrefix'),myForm._portletId,myForm.target);\n");
			} else {
				out.write("          myForm.submit();\n");
			}

			out.write("        }\n        return constraint_result;\n    }/* Gen_Last:");
			out.print(jsEntityName);
			out.write("_");
			out.print(functionName);
			out.write(" */\n");

		}
		//return generationContext;
		/* importBA(out, clClient, generationContext); */
	}

	private void genOutReference(PrintWriter out, UIPage uiPage) {
		String jsEntityName = entityName.replace('.', '_');

		out.write("\n    Form.initPageJs = ");
		out.print(jsEntityName);
		out.write("_initPageJs;\n\n    Form.finalizePageJs = ");
		out.print(jsEntityName);
		out.write("_finalizePageJs;\n");

		List<PageOutType> outs = uiPage.getOuts();
		for (PageOutType oType : outs) {
			String functionName = oType.getName() + "_OutFunctionName";

			out.write("\n    Form.");
			out.print(functionName);
			out.write(" = ");
			out.print(jsEntityName);
			out.write("_");
			out.print(functionName);
			out.write(";\n");
		}
	}

}
