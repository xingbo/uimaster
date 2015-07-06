package org.shaolin.uimaster.page.skin;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.datamodel.page.UIPanelType;
import org.shaolin.uimaster.html.layout.IUISkin;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.widgets.HTMLWidgetType;

public class GridLineSkin extends BaseSkin implements IUISkin {
	private static final Logger logger = Logger.getLogger(GridLineSkin.class);

	protected void initParam() {
		addParam("lineFileName", "");
	}

	public boolean isOverwrite() {
		return false;
	}

	public java.util.Map getAttributeMap(HTMLWidgetType component) {
		return null;
	}

	public void generatePreCode(HTMLWidgetType component)
			throws java.io.IOException {
		HTMLSnapshotContext context = component.getContext();
		String lineFileName = getParam("lineFileName");
		lineFileName = "/" + lineFileName.replace('.', '/') + ".properties";
		InputStream in = this.getClass().getClassLoader()
				.getResourceAsStream(lineFileName);
		Properties properties = new Properties();
		properties.load(in);
		Enumeration keys = properties.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = properties.getProperty(key);
			String[] valueArray = value.split(",");
			if (valueArray.length == 4) {
				context.generateHTML("<input type=hidden name=\"");
				context.generateHTML(key);
				context.generateHTML("\" printType=\"line\" printX=\"");
				context.generateHTML(valueArray[0]);
				context.generateHTML("\" printY=\"");
				context.generateHTML(valueArray[1]);
				context.generateHTML("\" printXOffSet=\"");
				context.generateHTML(valueArray[2]);
				context.generateHTML("\" printYOffSet=\"");
				context.generateHTML(valueArray[3]);
				context.generateHTML("\" value=\" \">");
			} else {
				logger.error("GridLineSkin's property file " + lineFileName
						+ " is error, at the line: " + key + "=" + value,
						new Exception());
			}
		}
	}

	public void generatePostCode(HTMLWidgetType component)
			throws java.io.IOException {
	}

	public Class getUIComponentType() {
		return UIPanelType.class;
	}

}
