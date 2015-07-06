package org.shaolin.uimaster.page.widgets;

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.WebConfig;
import org.shaolin.uimaster.page.ajax.Button;
import org.shaolin.uimaster.page.ajax.Layout;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLButtonType extends HTMLTextWidgetType 
{
    private static final Logger logger = LoggerFactory.getLogger(HTMLButtonType.class);

    private String buttonType;

    public HTMLButtonType()
    {
    }

    public HTMLButtonType(HTMLSnapshotContext context)
    {
        super(context);
    }

    public HTMLButtonType(HTMLSnapshotContext context, String id)
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
            context.generateHTML("<input type=\"");
            context.generateHTML(getButtonType());
            context.generateHTML("\" name=\"");
            context.generateHTML(getName());
            context.generateHTML("\"");
            generateAttributes(context);
            generateEventListeners(context);
            context.generateHTML(" value=\"");
            if (context.isValueMask())
            {
                context.generateHTML(WebConfig.getHiddenValueMask());
            }
            else
            {
                context.generateHTML(HTMLUtil.formatHtmlValue(getValue()));
            }
            context.generateHTML("\"");
            if (getReadOnly() != null && getReadOnly().booleanValue())
            {
                context.generateHTML(" disabled=\"true\"");
            }
            context.generateHTML(" />");
        }
        catch (Exception e)
        {
            logger.error("error. in entity: " + getUIEntityName(), e);
        }
    }

    private String getButtonType()
    {
        if ( buttonType == null )
        {
            buttonType = (String)removeAttribute("buttonType");
            if ( buttonType == null || ( !buttonType.trim().equals("reset") && !buttonType.trim().equals("submit") ) )
            {
                buttonType = "button";
            }
        }

        return buttonType;
    }
    
    public Widget createAjaxWidget(VariableEvaluator ee)
    {
      Button button = new Button(getName(), Layout.NULL);

      button.setButtonType(getButtonType());

      button.setReadOnly(getReadOnly());
      button.setUIEntityName(getUIEntityName());

      button.setValue(getValue());

      setAJAXAttributes(button);

      button.setListened(true);
      button.setFrameInfo(getFrameInfo());

      return button;
    }

}
