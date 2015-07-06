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
