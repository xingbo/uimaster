package org.shaolin.bmdp.runtime.be;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The extensible entity purposes on the extensibility of each single DB query mapping with the multiple tables.
 * 
 * The multiple object mapping is more preferred as the object update/remove cascading. However, the query operation
 * might not be good idea since the object mapping is not flexible to be extended, also the query result is not flat.
 * 
 * Here come up with the solution of dynamic flat column mappings for the multiple table query. It brings lot of convenience
 * while mapping the dynamic items to UI table.
 * 
 * @author Shaolin
 */
public class BEExtensionInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public Map<String, Object> items;
	
	public BEExtensionInfo() {
	}
	
	public void put(String key, String value) {
		if (items == null) {
			items = new HashMap<String, Object>();
		}
		items.put(key, value);
	}

	public Object get(String key) {
		if (items != null) {
			return items.get(key);
		}
		return null;
	}
	
	public String  getStr(String key) {
		if (items != null) {
			return String.valueOf(items.get(key));
		}
		return "";
	}
}
