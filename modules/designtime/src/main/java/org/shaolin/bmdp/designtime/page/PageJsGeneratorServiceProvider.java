package org.shaolin.bmdp.designtime.page;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.shaolin.bmdp.datamodel.page.FunctionReconfigurationType;
import org.shaolin.bmdp.datamodel.page.ReconfigurationType;
import org.shaolin.bmdp.datamodel.page.UIEntity;
import org.shaolin.bmdp.datamodel.page.UIPage;
import org.shaolin.bmdp.datamodel.page.UIPanelType;
import org.shaolin.bmdp.datamodel.page.UIReferenceEntityType;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;
import org.shaolin.bmdp.runtime.spi.IServiceProvider;
import org.shaolin.uimaster.page.spi.IJsGenerator;

/**
 * Dynamic js generator for ajax loading the static UI widget.
 * 
 * @author wushaol
 *
 */
public class PageJsGeneratorServiceProvider implements IServiceProvider,
		IJsGenerator {

	public String gen(String pageName, String prefix, UIPanelType panel) {
		StringWriter w = new StringWriter();
		PrintWriter writer = new PrintWriter(w);
		UIPage uipage = IServerServiceManager.INSTANCE.getEntityManager().getEntity(pageName, UIPage.class);
		UIPageJSGenerator generator = new UIPageJSGenerator();
		
	    List commonComponent = new ArrayList();
        List reference = new ArrayList();
        List container = new ArrayList();
        HashMap referenceMap = new HashMap();
    
        generator.divideContainer(panel, commonComponent, reference, container, false);
    
		for (int i = 0; i < reference.size(); i++) {
			UIReferenceEntityType rType = (UIReferenceEntityType) reference
					.get(i);
			List<ReconfigurationType> reconfigs = rType.getReconfigurations();
			for (ReconfigurationType reconfig : reconfigs) {
				if (reconfig instanceof FunctionReconfigurationType) {
					FunctionReconfigurationType ffType = (FunctionReconfigurationType) reconfig;
					String funcName = "this." + rType.getUIID() + "."
							+ ffType.getOriginFunctionName();
					referenceMap.put(ffType.getOverrideFunctionName(), funcName);
				}
			}
		}
		generator.genComponentJS(writer, commonComponent, reference, container, (UIEntity)uipage.getUIEntity());
		generator.genRootPanelJS(writer, panel, commonComponent, reference, container, (UIEntity)uipage.getUIEntity());
		
		String realPrefix = genPrefix(prefix, writer);
		writer.print(realPrefix);
		writer.print(panel.getUIID());
		writer.write(" = ");
		writer.print(panel.getUIID());
		writer.write(";\n");
		
		writer.print(realPrefix);
		writer.print(panel.getUIID());
		writer.write(".init();\n");
		
		return w.toString().replace("prefix + \"", "\"" + prefix).replace("Form.", realPrefix);
	}

	private String genPrefix(String prefix, PrintWriter writer) {
		String s = "defaultname.";
		if (prefix != null && prefix.length() > 0) {
			s = s + prefix + ".";
		}
		return s;
	}

	@Override
	public Class getServiceInterface() {
		return IJsGenerator.class;
	}

}
