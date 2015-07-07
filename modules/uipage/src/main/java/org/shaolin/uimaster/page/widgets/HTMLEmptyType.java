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
