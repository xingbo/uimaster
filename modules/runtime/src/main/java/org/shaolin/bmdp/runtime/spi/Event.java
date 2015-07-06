package org.shaolin.bmdp.runtime.spi;

import java.util.Collection;
import java.util.Map.Entry;

public interface Event {

	public String getEventConsumer();
	
	public Collection<Entry<String, Object>> getAllAttributes();

	public Object getAttribute(String key);

	public void setAttribute(String key, Object value);

	public Object removeAttribute(String key);

	public String getId();

	public void setId(String id);

	public Object getFlowContext();

	public void setFlowContext(Object flowContext);

}
