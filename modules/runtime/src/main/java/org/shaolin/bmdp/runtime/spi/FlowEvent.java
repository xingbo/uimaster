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
package org.shaolin.bmdp.runtime.spi;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class FlowEvent implements Event {

	public static final String FLOW_CONTEXT = "FLOW_CONTEXT";
	
	private Map<String, Object> content_;

	private String id_;
	
	private final String eventProducer_;
	
	public FlowEvent(String eventProducer) {
		this.eventProducer_ = eventProducer;
	}
	
	public String getEventConsumer() {
		return this.eventProducer_;
	}

	@Override
	public Collection<Entry<String, Object>> getAllAttributes() {
		if (content_ == null) {
			return Collections.emptyList();
		}
		return content_.entrySet();
	}

	@Override
	public Object getAttribute(String key) {
		if (content_ == null) {
			return null;
		}
		return content_.get(key);
	}

	@Override
	public Object removeAttribute(String key) {
		if (content_ == null) {
			return null;
		}
		return content_.remove(key);
	}

	@Override
	public void setAttribute(String key, Object value) {
		if (content_ == null) {
			content_ = new HashMap<String, Object>();
		}
		content_.put(key, value);
	}

	public Object getFlowContext() {
		return content_.get(FLOW_CONTEXT);
	}

	public void setFlowContext(Object flowContext) {
		setAttribute(FLOW_CONTEXT, flowContext);
	}

	@Override
	public String getId() {
		return this.id_;
	}

	@Override
	public void setId(String id) {
		this.id_ = id;
	}

}
