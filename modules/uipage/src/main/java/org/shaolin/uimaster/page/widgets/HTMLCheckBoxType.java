package org.shaolin.uimaster.page.widgets;

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.WebConfig;
import org.shaolin.uimaster.page.ajax.CheckBox;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HTMLCheckBoxType extends HTMLSelectComponentType
{
    private static final Logger logger = LoggerFactory.getLogger(HTMLCheckBoxType.class);

    public HTMLCheckBoxType()
    {
    }

    public HTMLCheckBoxType(HTMLSnapshotContext context)
    {
        super(context);
    }

    public HTMLCheckBoxType(HTMLSnapshotContext context, String id)
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
            context.generateHTML("<input type=\"checkbox\" name=\"");
            context.generateHTML(getName());
            context.generateHTML("\"");
            context.generateHTML(" id=\"");
            context.generateHTML(getName());
            context.generateHTML("\"");
            generateAttributes(context);
            generateEventListeners(context);
            if (getReadOnly() != null && getReadOnly().booleanValue())
            {
                context.generateHTML(" disabled=\"true\"");
            }
            context.generateHTML(" />");
            if (!this.isVisible())
                context.generateHTML("<span style=\"display:none\">");
            if (getName() == null || "null".equals(getName())) {
	            context.generateHTML("<label for=\"");
	            context.generateHTML(getName());
	            context.generateHTML("\">");
            }
            if (context.isValueMask())
            {
                context.generateHTML(WebConfig.getHiddenValueMask());
            }
            else
            {
                context.generateHTML(HTMLUtil.htmlEncode(getLabel()));
            }
            context.generateHTML("</label>");
            if (!this.isVisible())
            {
                context.generateHTML("</span>");
            }
        }
        catch (Exception e)
        {
            logger.error("error. in entity: " + getUIEntityName(), e);
        }
    }

    public Widget createAjaxWidget(VariableEvaluator ee)
    {
        CheckBox checkBox = new CheckBox(getName(), null);

        checkBox.setReadOnly(getReadOnly());
        checkBox.setUIEntityName(getUIEntityName());

        checkBox.setLabel(getLabel());
        checkBox.setSelected(getValue());
        setAJAXConstraints(checkBox);
        setAJAXAttributes(checkBox);
        
        checkBox.setListened(true);
        checkBox.setFrameInfo(getFrameInfo());

        return checkBox;
    }
    
    private static final long serialVersionUID = -6662073300811877694L;
}
