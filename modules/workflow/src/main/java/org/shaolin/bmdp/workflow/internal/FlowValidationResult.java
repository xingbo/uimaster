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
package org.shaolin.bmdp.workflow.internal;

public class FlowValidationResult {
	
	private final String appName;
	private final String flowName;
	private final String nodeName;
	private final String message;

	public String getAppName() {
		return appName;
	}

	public String getFlowName() {
		return flowName;
	}

	public String getNodeName() {
		return nodeName;
	}

	public String getMessage() {
		return message;
	}

	public FlowValidationResult(String appName, String flowName,
			String nodeName, String message) {
		this.appName = appName;
		this.flowName = flowName;
		this.nodeName = nodeName;
		this.message = message;
	}

	@Override
	public String toString() {
		return "FlowValidationResult [appName=" + appName + ", flowName="
				+ flowName + ", nodeName=" + nodeName + ", message=" + message
				+ "]";
	}
}
