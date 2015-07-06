package org.shaolin.uimaster.page.widgets;

import java.util.List;

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.ajax.ComboBox;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLComboBoxType extends HTMLSingleChoiceType
{
    private static final Logger logger = LoggerFactory.getLogger(HTMLComboBoxType.class);

    public HTMLComboBoxType()
    {
    }

    public HTMLComboBoxType(HTMLSnapshotContext context)
    {
        super(context);
    }

    public HTMLComboBoxType(HTMLSnapshotContext context, String id)
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
            List<String> displayOptions =  getOptionDisplayValues();
            List<String> options = getOptionValues();
            String value = getValue();
            if(displayOptions == null)
            {
                displayOptions = options;
            }
            generateWidget(context);
            if (displayOptions != null && options != null)
            {
                if (displayOptions.size() == 0)
                {
                    displayOptions = options;
                }
                if (displayOptions.size() != options.size())
                {
                    logger.error("display size && output size not equal!", new Exception());
                    displayOptions = options;
                }
                if (getReadOnly() != null && getReadOnly().booleanValue())
                {
                    addAttribute("allowBlank", "true");
                    String UIStyle = (String)getAllAttribute("UIStyle");
                    if (UIStyle != null && !UIStyle.trim().equals("null"))
                    {
                        UIStyle = "uimaster_comboBox_readOnly " + UIStyle;
                    }
                    else
                    {
                        UIStyle = "uimaster_comboBox_readOnly";
                    }
                    if ( value == null && displayOptions.size() != 0 )
                    {
                        context.generateHTML("<input type=text readOnly=\"true\" class=\"" + UIStyle + "\" value=\"");
                        context.generateHTML(HTMLUtil.formatHtmlValue(displayOptions.get(0) == null ? "_null" : String.valueOf(displayOptions.get(0))));
                        context.generateHTML("\" />");
                        context.generateHTML("<input type=hidden name=\"");
                        context.generateHTML(getName());
                        context.generateHTML("\"");
                        generateAttributes(context);
                        generateEventListeners(context);
                        context.generateHTML(" value=\"");;
                        context.generateHTML(HTMLUtil.formatHtmlValue((options.get(0) == null) ? "_null" : String.valueOf(options.get(0))));
                        context.generateHTML("\" />");
                    }
                    else
                    {
                        boolean isMatch = true;
                        for (int i = 0; i < displayOptions.size(); i++)
                        {
                            String optionValue = (options.get(i) == null) ? "_null" : String.valueOf(options.get(i));
                            if (value != null && value.equalsIgnoreCase(optionValue))
                            {
                                context.generateHTML("<input type=text readOnly=\"true\" class=\"" + UIStyle + "\" value=\"");
                                context.generateHTML(HTMLUtil.formatHtmlValue(displayOptions.get(i) == null ? "_null" : String.valueOf(displayOptions.get(i))));
                                context.generateHTML("\" />");
                                context.generateHTML("<input type=hidden name=\"");
                                context.generateHTML(getName());
                                context.generateHTML("\"");
                                generateAttributes(context);
                                generateEventListeners(context);
                                context.generateHTML(" value=\"");
                                context.generateHTML(HTMLUtil.formatHtmlValue(optionValue));
                                context.generateHTML("\" />");
                                isMatch = true;
                                break;
                            }
                            isMatch = false;
                        }
                        if ( !isMatch )
                        {
                            context.generateHTML("<input type=text readOnly=\"true\" class=\"" + UIStyle + "\" value=\"");
                            context.generateHTML(HTMLUtil.formatHtmlValue(displayOptions.get(0) == null ? "_null" : String.valueOf(displayOptions.get(0))));
                            context.generateHTML("\" />");
                            context.generateHTML("<input type=hidden name=\"");
                            context.generateHTML(getName());
                            context.generateHTML("\"");
                            generateAttributes(context);
                            generateEventListeners(context);
                            context.generateHTML(" value=\"");;
                            context.generateHTML(HTMLUtil.formatHtmlValue((options.get(0) == null) ? "_null" : String.valueOf(options.get(0))));
                            context.generateHTML("\" />");
                        }
                    }
                }
                else
                {
                    context.generateHTML("<select name=\"");
                    context.generateHTML(getName());
                    context.generateHTML("\"");
                    generateAttributes(context);
                    generateEventListeners(context);
                    context.generateHTML(">");
                    for (int i = 0; i < displayOptions.size(); i++)
                    {
                        context.generateHTML("<option value=\"");
                        String optionValue = (options.get(i) == null) ? "_null" : String.valueOf(options.get(i));
                        context.generateHTML(HTMLUtil.formatHtmlValue(optionValue));
                        context.generateHTML("\"");
                        if (value != null && value.equalsIgnoreCase(optionValue))
                        {
                            context.generateHTML(" selected");
                        }
                        context.generateHTML(">");
                        context.generateHTML(HTMLUtil.formatHtmlValue(displayOptions.get(i) == null ? "_null" : String.valueOf(displayOptions.get(i))));
                        context.generateHTML("</option>");
                    }
                    context.generateHTML("</select>");
                }
            }
            else
            {
                if (getReadOnly() != null && getReadOnly().booleanValue())
                {
                    addAttribute("allowBlank", "true");
                    addAttribute("readOnly", "true");
                    context.generateHTML("<input type=text name=\"");
                    context.generateHTML(getName());
                    context.generateHTML("\"");
                    generateAttributes(context);
                    generateEventListeners(context);
                    context.generateHTML(" value=\"\" />");
                }
                else
                {
                    context.generateHTML("<select name=\"");
                    context.generateHTML(getName());
                    context.generateHTML("\"");
                    generateAttributes(context);
                    generateEventListeners(context);
                    context.generateHTML(">");
                    context.generateHTML("</select>");
                }
            }
        }
        catch (Exception e)
        {
            logger.error("error. in entity: " + getUIEntityName(), e);
        }
    }
    
    public Widget createAjaxWidget(VariableEvaluator ee)
    {
        ComboBox comboBox = new ComboBox(getName(), null);

        comboBox.setReadOnly(getReadOnly());
        comboBox.setUIEntityName(getUIEntityName());

        comboBox.setOptions(getOptionDisplayValues(), getOptionValues());
        comboBox.setValue(getValue());

        setAJAXConstraints(comboBox);
        setAJAXAttributes(comboBox);
        
        comboBox.setListened(true);
        comboBox.setFrameInfo(getFrameInfo());

        return comboBox;
    }
    
    private static final long serialVersionUID = -7717716729284638113L;
}
