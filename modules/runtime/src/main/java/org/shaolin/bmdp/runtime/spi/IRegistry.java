package org.shaolin.bmdp.runtime.spi;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IRegistry {

	public String getEncoding();

	public Set<String> getConfigItemPaths();

	public Set<String> getConfigNodePaths();

	public String getValue(String path);

	public Map<String, String> getNodeItems(String path);

	public List<String> getNodeChildren(String path);

	public boolean exists(String path);

}
