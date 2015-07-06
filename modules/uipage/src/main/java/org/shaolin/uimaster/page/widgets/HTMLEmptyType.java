package org.shaolin.uimaster.page.widgets;

import org.apache.log4j.Logger;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.cache.UIFormObject;

public class HTMLEmptyType extends HTMLWidgetType {
	
	private static final long serialVersionUID = 1587046878874940935L;

	private static final Logger logger = Logger.getLogger(HTMLEmptyType.class);

	public HTMLEmptyType() {
	}

	public HTMLEmptyType(HTMLSnapshotContext context) {
		super(context);
	}

	public HTMLEmptyType(HTMLSnapshotContext context, String id) {
		super(context, id);
	}

	@Override
	public void generateBeginHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth) {
		
	}
	
	@Override
	public void generateEndHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth) {
		try {
			context.generateHTML("&nbsp;");
		} catch (Exception e) {
			logger.error("error. in entity: " + getUIEntityName(), e);
		}
	}

}
