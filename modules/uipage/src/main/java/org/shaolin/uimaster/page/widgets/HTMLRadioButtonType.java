package org.shaolin.uimaster.page.widgets;

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.WebConfig;
import org.shaolin.uimaster.page.ajax.Layout;
import org.shaolin.uimaster.page.ajax.RadioButton;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLRadioButtonType extends HTMLSelectComponentType
{
    private static Logger logger = LoggerFactory.getLogger(HTMLRadioButtonType.class);

    public HTMLRadioButtonType()
    {
    }

    public HTMLRadioButtonType(HTMLSnapshotContext context)
    {
        super(context);
    }

    public HTMLRadioButtonType(HTMLSnapshotContext context, String id)
    {
        super(context, id);
    }

    private String _getName2()
    {
        String prefix = getPrefix() == null ? "" : getPrefix();
        return prefix + getId();
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
            context.generateHTML("<input type=radio name=\"");
            context.generateHTML(_getName2());
            context.generateHTML("\"");
            context.generateHTML(" id=\"");
            context.generateHTML(_getName2());
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
            context.generateHTML("<label for=\"");
            context.generateHTML(_getName2());
            context.generateHTML("\">");
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
        RadioButton radioButton = new RadioButton(getName(), Layout.NULL);

        radioButton.setReadOnly(getReadOnly());
        radioButton.setUIEntityName(getUIEntityName());

        radioButton.setLabel(getLabel());

        radioButton.setListened(true);
        radioButton.setSelected(getValue());
        radioButton.setFrameInfo(getFrameInfo());

        return radioButton;
    }
    
    private static final long serialVersionUID = -4405215152580918889L;
}
