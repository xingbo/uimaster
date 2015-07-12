/*
* Copyright 2015 The UIMaster Project
*
* The UIMaster Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/
package org.shaolin.uimaster.page.widgets;

import java.io.IOException;

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.WebConfig;
import org.shaolin.uimaster.page.ajax.Layout;
import org.shaolin.uimaster.page.ajax.TextField;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLTextFieldType extends HTMLTextWidgetType
{
    private static Logger logger = LoggerFactory.getLogger(HTMLTextFieldType.class);

    public HTMLTextFieldType()
    {
    }

    public HTMLTextFieldType(HTMLSnapshotContext context)
    {
        super(context);
    }

    public HTMLTextFieldType(HTMLSnapshotContext context, String id)
    {
        super(context, id);
    }

    @Override
	public void generateBeginHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth) {
		
	}
    
    @Override
    public void generateEndHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth)
    {
        generateWidget(context);
        String currencySymbol = getCurrencySymbol();
        if ( currencySymbol == null || currencySymbol.equals("") )
        {
            generateContent(context);
        }
        else if ( getIsSymbolLeft() )
        {
            generateCurrencySymbol(context, currencySymbol);
            generateContent(context);
        }
        else
        {
            generateContent(context);
            generateCurrencySymbol(context, currencySymbol);
        }
        generateEndWidget(context);
    }

    public void generateCurrencySymbol(HTMLSnapshotContext context, String currencySymbol)
    {
        context.generateHTML("<span class=\"currencySymbolText\"");
        context.generateHTML(" id=\"" + getName() + "_currencySymbol\" >");
        context.generateHTML(currencySymbol);
        context.generateHTML("</span>");
    }

    private void generateContent(HTMLSnapshotContext context)
    {
        try
        {
            if (getIsCurrency())
            {
                context.generateHTML("\n<script type=\"text/javascript\"> ");
                context.generateHTML("\naddOnLoadEvent(EventExecutor('" + getName() + "'));");
                context.generateHTML("\n</script>");
            }
            if ( getReadOnly() != null && getReadOnly().booleanValue() )
            {
                addAttribute("allowBlank", "true");
                addAttribute("readOnly", "true");
            }
            context.generateHTML("<input type=\"text\" name=\"");
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
            context.generateHTML("\" />");
        }
        catch (Exception e)
        {
            logger.error("error. in entity: " + getUIEntityName(), e);
        }
    }

    public void generateAttribute(HTMLSnapshotContext context, String attributeName, Object attributeValue) throws IOException
    {
        if ("initValidation".equals(attributeName) || "validator".equals(attributeName))
        {
        	return;
        }
        if ("editable".equals(attributeName))
        {
            if ("false".equals(String.valueOf(attributeValue)))
            {
                context.generateHTML(" readOnly=\"true\"");
            }
        }
        else if ("maxLength".equals(attributeName))
        {
            context.generateHTML(" maxlength=\"");
            context.generateHTML((String)attributeValue);
            context.generateHTML("\"");
        }
        else if ("txtFieldLength".equals(attributeName))
        {
            context.generateHTML(" size=\"");
            context.generateHTML((String)attributeValue);
            context.generateHTML("\"");
        }
        else if ("prompt".equals(attributeName))
        {
            if ( attributeValue != null && !((String)attributeValue).trim().equals("") )
            {
                context.generateHTML(" title=\"");
                context.generateHTML((String)attributeValue);
                context.generateHTML("\"");
            }
        }
        else
        {
            super.generateAttribute(context, attributeName, attributeValue);
        }
    }

    public Widget createAjaxWidget(VariableEvaluator ee)
    {
        TextField textField = new TextField(getName(), Layout.NULL);

        textField.setReadOnly(getReadOnly());
        textField.setUIEntityName(getUIEntityName());

        // we don't expect to anything except the pure value 
        // what we really need in the backend.
        textField.setValue(getValue());
        if (this.getAttribute("secure") != null) {
        	textField.setIsSecure(Boolean.valueOf(this.getAttribute("secure").toString()));
        }
        // add necessary attribute especially the server side constraint check.
        setAJAXConstraints(textField);
        setAJAXAttributes(textField);
        
        textField.setListened(true);
        textField.setFrameInfo(getFrameInfo());

        return textField;
    }

    
    private static final long serialVersionUID = -5232602952223828765L;
}
