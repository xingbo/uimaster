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
package org.shaolin.uimaster.page.cache;

import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.uimaster.page.OpExecuteContext;
import org.shaolin.uimaster.page.od.ODContext;

public abstract class ODObject {

	protected String name = null;

	protected String uiEntityName = "";

	/**
	 * 'page od' op context.
	 * 
	 */
	protected transient OpExecuteContext opContext;

	public ODObject() {
		opContext = new OpExecuteContext();
	}

	protected void clearODObject() {
		uiEntityName = "";
		opContext = null;
		opContext = new OpExecuteContext();
	}

	public String getUIEntityName() {
		return uiEntityName;
	}

	/**
	 * Get local parsing context from this od entity.
	 * 
	 * od entity od page
	 * 
	 * @return
	 */
	public DefaultParsingContext getLocalPContext() {
		return (DefaultParsingContext) opContext
				.getParsingContextObject(ODContext.LOCAL_TAG);
	}

	/**
	 * Get global parsing context from this od entity.
	 * 
	 * od interface entity. od page
	 * 
	 * @return
	 */
	public DefaultParsingContext getGlobalPContext() {
		return (DefaultParsingContext) opContext
				.getParsingContextObject(ODContext.GLOBAL_TAG);
	}

}
