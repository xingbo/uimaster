/*
 * Copyright 2001-2005 by BMIAsia, Inc.,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of BMIAsia, Inc.("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with BMIAsia.
 */

package org.shaolin.uimaster.page.skin;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.shaolin.bmdp.datamodel.page.UIPanelType;
import org.shaolin.uimaster.html.layout.IUISkin;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.WebConfig;
import org.shaolin.uimaster.page.widgets.HTMLLayoutType;
import org.shaolin.uimaster.page.widgets.HTMLPanelType;
import org.shaolin.uimaster.page.widgets.HTMLWidgetType;

/**
 * A foilding panel with title display table with lines,each line has 8 lines at
 * most. the even column(from 0) class=commontype ,odd column's class=typedr .
 * if you want to define the line and column, you can set the line number as 9.
 * 
 * @author qyfan
 */
public class FoldingPanel extends BaseSkin implements IUISkin {
	public boolean isOverwrite() {
		return false;
	}

	public Map<String, String> getAttributeMap(HTMLWidgetType component) {
		Map<String, String> attributeMap = null;
		if (component instanceof HTMLPanelType) {
			attributeMap = new HashMap<String, String>(5);
			attributeMap.put("width", "-1");
			attributeMap.put("border", "0");
			attributeMap.put("padding", "0");
			attributeMap.put("spacing", "0");
			attributeMap.put("UIStyle", "typetable");
		} else if (component instanceof HTMLLayoutType) {
			try {
				HTMLLayoutType layout = (HTMLLayoutType) component;
				attributeMap = new HashMap<String, String>(1);
				int x = layout.getX();
				int colSpan = layout.getColSpan();

				if (x + colSpan == layout.getTableColumnCount()) {
					attributeMap.put("cellUIStyle", "exttype");
				} else if (x % 2 == 0) {
					attributeMap.put("cellUIStyle", "commontype" + colSpan);
				} else {
					attributeMap.put("cellUIStyle", "typedr" + colSpan);
				}
			} catch (NumberFormatException e) {
			}
		}
		return attributeMap;
	}

	public void generatePreCode(HTMLWidgetType component)
			throws java.io.IOException {
		HTMLPanelType panel = (HTMLPanelType) component;
		HTMLSnapshotContext context = panel.getContext();
		HttpServletRequest request = context.getRequest();
		String webRoot = WebConfig.getResourceContextRoot();
		context.generateHTML("<table class=\"table-fp\">");
		context.generateHTML("<tr>");
		context.generateHTML("<td class=\"text-fp\">");
		context.generateHTML(getParam("title"));
		context.generateHTML("</td><td class=\"title-fp\"><img src=\"");
		context.generateHTML(webRoot);
		context.generateHTML("/images/table-");
		if ("true".equals(getParam("isHidden"))) {
			context.generateHTML("open");
		} else {
			context.generateHTML("close");
		}
		context.generateHTML(".jpg\" width=14 height=12 border=0 id=\""
				+ component.getName()
				+ ".arrowIcon\" onClick=\"bmiasia_slide('");
		context.generateHTML(component.getName());
		context.generateHTML("','");
		context.generateHTML(webRoot);
		context.generateHTML("/images/table-open.jpg','");
		context.generateHTML(webRoot);
		context.generateHTML("/images/table-close.jpg','"
				+ component.getName()
				+ ".arrowIcon',event);\"></td></tr><tr><td colspan=2 class=\"content-fp\">");
	}

	public void generatePostCode(HTMLWidgetType component)
			throws java.io.IOException {
		HTMLSnapshotContext context = component.getContext();
		if ("true".equals(getParam("isHidden"))) {
			context.generateHTML("<script>document.getElementById(\"");
			context.generateHTML(component.getName());
			context.generateHTML("\").style.display='none';</script>");
		}
		context.generateHTML("</td></tr></table>");
	}

	protected void initParam() {
		addParam("title", "");
		addParam("isHidden", "true");
	}

	public Class getUIComponentType() {
		return UIPanelType.class;
	}

}
