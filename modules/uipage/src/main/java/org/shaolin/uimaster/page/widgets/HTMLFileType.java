package org.shaolin.uimaster.page.widgets;

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.WebConfig;
import org.shaolin.uimaster.page.ajax.AFile;
import org.shaolin.uimaster.page.ajax.Layout;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLFileType extends HTMLTextWidgetType 
{
    private static final Logger logger = LoggerFactory.getLogger(HTMLFileType.class);

    public HTMLFileType()
    {
    }

    public HTMLFileType(HTMLSnapshotContext context)
    {
        super(context);
    }

    public HTMLFileType(HTMLSnapshotContext context, String id)
    {
        super(context, id);
    }

    @Override
	public void generateBeginHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth) {
		
	}
    
    @Override
    public void generateEndHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth)
    {
        try
        {
            generateWidget(context);
            String root = WebConfig.getWebRoot();
            context.generateHTML("<script type=\"text/javascript\" src=\""+root+"/js/jquery-form.js\"></script>");
            HTMLUtil.generateTab(context, depth + 2);
            context.generateHTML("<input type=\"file\" name=\"");
            context.generateHTML(getName());
            context.generateHTML("\"");
            generateAttributes(context);
            generateEventListeners(context);
            context.generateHTML(" value=\"\" ");
            if (this.getAttribute("isMultiple") != null && "true".equals(this.getAttribute("isMultiple"))) {
            	context.generateHTML("multiple=\"multiple\" ");
            }
        	context.generateHTML("suffix=\"");
        	context.generateHTML(this.getAttribute("suffix").toString());
        	context.generateHTML("\" ");
            context.generateHTML("/>");
            HTMLUtil.generateTab(context, depth + 2);
            context.generateHTML("<input type=\"button\" value=\"Upload\" />");
            HTMLUtil.generateTab(context, depth + 2);
            context.generateHTML("<div name=\"progressbox\" style=\"display:none;\"><div name=\"progressbar\"></div><div name=\"percent\">0%</div></div>");
            context.generateHTML("<div name=\"message\"></div>");
            HTMLUtil.generateTab(context, depth);
        }
        catch (Exception e)
        {
            logger.error("error. in entity: " + getUIEntityName(), e);
        }
    }
    
    /**
     *  Whether this component can have editPermission.
     */
    @Override
    public boolean isEditPermissionEnabled()
    {
        return false;
    }
    
    public Widget createAjaxWidget(VariableEvaluator ee)
    {
        AFile file = new AFile(getName(), Layout.NULL);

        file.setReadOnly(getReadOnly());
        file.setUIEntityName(getUIEntityName());
        file.setStoredPath(this.getAttribute("storedPath").toString());
        if (file.getStoredPath().trim().isEmpty()) {
        	throw new IllegalArgumentException("The file stored path can't be empty!");
        }
        file.setSuffix(this.getAttribute("suffix").toString());
        file.setListened(true);
        file.setFrameInfo(getFrameInfo());

        return file;
    }

}
