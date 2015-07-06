package org.shaolin.uimaster.page.widgets;

import java.io.IOException;

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLChartPolarPie extends HTMLWidgetType
{
    private static Logger logger = LoggerFactory.getLogger(HTMLChartPolarPie.class);

    public HTMLChartPolarPie()
    {
    }

    public HTMLChartPolarPie(HTMLSnapshotContext context)
    {
        super(context);
    }

    public HTMLChartPolarPie(HTMLSnapshotContext context, String id)
    {
        super(context, id);
    }

    @Override
	public void generateBeginHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth) {
		
	}
    
    @Override
    public void generateEndHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth)
    {
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
    }

    public void generateAttribute(HTMLSnapshotContext context, String attributeName, Object attributeValue) throws IOException
    {
    }

    public Widget createAjaxWidget(VariableEvaluator ee)
    {
        return null;
    }
    
    private static final long serialVersionUID = -5232602952223828765L;
}
