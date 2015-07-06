
package org.shaolin.bmdp.designtime.page;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.shaolin.bmdp.datamodel.common.DiagramType;
import org.shaolin.bmdp.datamodel.page.FunctionReconfigurationType;
import org.shaolin.bmdp.datamodel.page.ReconfigurationType;
import org.shaolin.bmdp.datamodel.page.UIContainerType;
import org.shaolin.bmdp.datamodel.page.UIEntity;
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

public final class UIFormJSGenerator extends UIFormJSGenerator0 implements IEntityEventListener<UIEntity, DiagramType> {
    //for log4j
    private static Logger logger = LoggerFactory.getLogger(UIFormJSGenerator.class);
    
    private final PumpWriter out = new PumpWriter();
    
    private List jsText = null;
    private boolean clear = false;
    private String jsName = null;
    private Map referenceMap = null;
    private ArrayList refJS;

    private EntityManager entityManager;

    public UIFormJSGenerator(GeneratorOptions option) {
		super(option);
	}
    
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
    
	public void notify(EntityAddedEvent<UIEntity, DiagramType> event) {
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
        	this.entityName = event.getEntity().getEntityName();
            this.jsName = UIPageUtil.getDefaultJspName(event.getEntity().getEntityName());
            generateJs(jsRoot, event.getEntity());
		} catch(Exception ex) {
          logger.error("Error generating Java Class for UIPage entity " + entityName, ex);
        } finally {
        	out.write("\n\n");
        	out.finish();
		}
	}

	public void notify(EntityUpdatedEvent<UIEntity, DiagramType> event) {
	}

	public Class<UIEntity> getEventType() {
		return UIEntity.class;
	}
    
	@Override
	public void notifyLoadFinish(DiagramType diagram) {
		
	}
	
	public void notifyAllLoadFinish() {

	}

	private static File getJsFile(File jsRoot, String jsName) {
		return new File(jsRoot, jsName.replace('.', File.separatorChar) + ".js");
	}
    
    private void generateJs(File rootPath, UIEntity uiEntity)
    {
		try {
			rootPanelValidators = new ArrayList();
			refJS = new ArrayList();
			entityName = uiEntity.getEntityName();
			Map pageOuts = null;
			// Don't need
			// jsText = JSMerge.getSingleText(rootPath.getAbsolutePath(), jsName);
			rootPanelValidators = new ArrayList();
			refJS = new ArrayList();
			generateJsFile(out, uiEntity);
			genHandlerReference(out, uiEntity);

			out.write("\n    Form.__entityName=\"");
			out.print(uiEntity.getEntityName());
			out.write("\";\n");

			genReturnJS(out);
			out.write("\n    /* EventHandler Functions */\n");
			genOtherFunc(out);
			genFunctionHead(out, pageOuts, uiEntity, referenceMap);
			JSMerge.clearFuncList();
		} catch (Exception ex) {
			logger.error("Error generating JavaScript for UIEntity/UIPage "
					+ entityName, ex);
		}
    }
    
    public void generateJsFile(PrintWriter out, UIEntity uiEntity) throws Exception
    {
        entityName = uiEntity.getEntityName();
        String jsEntityName = entityName.replace('.', '_');
    
        List commonComponent = new ArrayList();
        List reference = new ArrayList();
        List container = new ArrayList();
        referenceMap = new HashMap();
    
        UIContainerType rootPanel = null;
        rootPanel = uiEntity.getBody();
        divideContainer(rootPanel, commonComponent, reference, container, false);
    
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
    
        genComponentJS(out, commonComponent, reference, container, (UIEntity)uiEntity);
        genRootPanelJS(out, (UIPanelType) rootPanel, commonComponent, reference, container,(UIEntity)uiEntity);
    
        out.write("\n    ");
        out.print(((UIEntity)uiEntity).getBody().getUIID());
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

    }

}
