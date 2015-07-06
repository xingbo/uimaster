package org.shaolin.bmdp.runtime;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.bind.JAXBException;

import org.shaolin.bmdp.datamodel.registry.ItemConfigType;
import org.shaolin.bmdp.datamodel.registry.NodeConfigType;
import org.shaolin.bmdp.i18n.ResourceUtil;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.entity.EntityUtil;
import org.shaolin.bmdp.runtime.spi.IRegistry;
import org.shaolin.bmdp.utils.CloseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Here is only one registry manager while running multiple applications.
 * To reduce our cost of memory consuming.
 * 
 */
public final class Registry implements IRegistry, Serializable {
	
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(Registry.class);

	private final List<org.shaolin.bmdp.datamodel.registry.Registry> registryItems = 
			new ArrayList<org.shaolin.bmdp.datamodel.registry.Registry>();
	
	private final ConcurrentHashMap<String, String> itemsMap = 
			new ConcurrentHashMap<String, String>();
	
	private final ConcurrentHashMap<String, String> expressionMap = 
			new ConcurrentHashMap<String, String>();

	private final ConcurrentHashMap<String, Map<String, String>> nodesMap = 
			new ConcurrentHashMap<String, Map<String, String>>();
	
	private final ConcurrentHashMap<String, List<String>> nodeNamesMap = 
			new ConcurrentHashMap<String, List<String>>();

	/**
	 * Optimize the frequently reading operation.
	 * if the registry has any update, this cache
	 * will be refreshed as well.
	 */
	private transient final ConcurrentHashMap<String, Object> fastreadCacheMap = 
			new ConcurrentHashMap<String, Object>();
	
	private static final Registry INSTANCE = new Registry();
	
	// if the registry is the centre server mode, 
	// it will read the configuration from the 
	// centre server database.
	private boolean isCentralMode = false;
	
	private boolean initialized = false;

	public static Registry getInstance() {
		return INSTANCE;
	}

	private void loadConfiguration() {
		try {
			Enumeration<URL> urls1 = EntityManager.class.getClassLoader()
					.getResources("runconfig.registry");
			while (urls1.hasMoreElements()) {
				URL url = urls1.nextElement();
				String path = url.toString();
				if (logger.isInfoEnabled()) {
					logger.info("Registry file directory: " + path);
				}
				if (path.startsWith("jar:")) {
					/**
					 * jar:file:/E:/projects/uimaster/components1.jar!/runconfig.registry
					 */
					String file = path.substring("jar:file:".length(),
												 path.indexOf(".jar") + 4);
					loadFromJar(file);
				} else {
					/**
					 * file:/E:/projects/uimaster/runconfig.registry
					 */
					try {
						loadFromDir(new File(url.toURI()), false);
					} catch (URISyntaxException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
			init();
		} catch (IOException e1) {
			logger.error("Failed to build up the entity cache. Cause: "
							+ e1.getMessage(), e1);
		}
	}
	
	public void initRegistry() {
		if (initialized) {
			logger.warn("The registry has already initialized!");
		}
		this.initialized = true;
		
		this.isCentralMode = true;
		loadConfiguration();
	}
	
	/**
	 * The client node will received a registry from the central node.
	 * 
	 * @param centralRegistry
	 */
	public void initRegistry(Registry centralRegistry) {
		this.itemsMap.clear();
		this.itemsMap.putAll(centralRegistry.itemsMap);
		this.expressionMap.clear();
		this.expressionMap.putAll(centralRegistry.expressionMap);
		this.nodesMap.clear();
		this.nodesMap.putAll(centralRegistry.nodesMap);
		this.nodeNamesMap.clear();
		this.nodeNamesMap.putAll(centralRegistry.nodeNamesMap);
	}
	
	private void loadFromJar(final String file) throws IOException {
		JarFile jar = new JarFile(new File(file));
		final Enumeration<JarEntry> e = jar.entries();
		while (e.hasMoreElements()) {
			final JarEntry entry = e.nextElement();
			if (!entry.isDirectory() && entry.getName().startsWith("runconfig.registry")) {
				InputStream in = null;
				try {
					in = jar.getInputStream(entry);

					String entityType = entry.getName();
					entityType = entityType.substring(entityType.lastIndexOf('.') + 1);
					
					addItem(entry.getName(), in, entityType, false);
					
				} catch (JAXBException e1) {
					logger.error("Failed to load runconfig.registry " + entry.getName() 
							+ ". Cause: " + e1.getMessage(), e1);
				} finally {
					CloseUtil.close(in);
				}
			}
		}
	}

	private void loadFromDir(final File path, boolean reload) throws IOException {
		if (path.isDirectory()) {
			final String files[] = path.list();
			for (final String file : files) {
				loadFromDir(new File(path, file), reload);
			}
		} else {
			InputStream in = null;
			try {
				in = new FileInputStream(path);

				String entityType = path.getName();
				entityType = entityType
						.substring(entityType.lastIndexOf('.') + 1);
				
				addItem(path.getName(), in, entityType, reload);
			} catch (JAXBException e1) {
				logger.error("Failed to load runconfig.registry " + path.getName() 
						+ ". Cause: " + e1.getMessage(), e1);
			} finally {
				CloseUtil.close(in);
			}
		}
	}
	
	private void addItem(final String name, InputStream in,
			String entityType, boolean reload) throws JAXBException {
		if (logger.isDebugEnabled()) {
			logger.debug("Added registry item: " + name);
		}
		org.shaolin.bmdp.datamodel.registry.Registry config = EntityUtil.unmarshaller(org.shaolin.bmdp.datamodel.registry.Registry.class, in);
		registryItems.add(config);
	}
	
	private void init() {
		for (org.shaolin.bmdp.datamodel.registry.Registry config : registryItems) {
			StringBuffer sb = new StringBuffer();
			init0(config.getNodes(), sb);
		}
		
		Registry registry = Registry.getInstance();
		
		List<String> i18ns = registry.getNodeChildren("/System/i18n");
		Map<String, String> localeConfigMap = new HashMap<String, String>();
		for (String name : i18ns) {
			String locale = registry.getValue("/System/i18n/" + name + "/locale");
			localeConfigMap.put(name, locale);
		}
		String defaultLocale = registry.getValue("/System/i18n/default/locale");
		
		ResourceUtil.init(defaultLocale, localeConfigMap);
	}

	private void init0(List<NodeConfigType> nodes, StringBuffer parent) {
		List<String> nodeChildren;
		if (nodeNamesMap.get(parent.toString()) != null) {
			nodeChildren = nodeNamesMap.get(parent.toString());
		} else {
			nodeChildren = new LinkedList<String>();
		}
		for (NodeConfigType config : nodes) {
			StringBuffer sb = new StringBuffer(parent);
			sb.append("/").append(config.getName());
			
			if (config.getExpression() != null) {
				expressionMap.put(sb.toString(), config.getExpression());
				if (logger.isDebugEnabled()) {
					logger.debug("Add expression: " + sb.toString() + "="
							+ config.getExpression());
				}
			}
			
			Map<String, String> pairs = new LinkedHashMap<String, String>();
			List<ItemConfigType> items = config.getItems();
			for (ItemConfigType item : items) {
				StringBuffer itemsb = new StringBuffer();
				itemsb.append(sb);
				itemsb.append("/");
				itemsb.append(item.getName());
				itemsMap.put(itemsb.toString(), item.getValue());
				pairs.put(item.getName(), item.getValue());
				
				if (logger.isDebugEnabled()) {
					logger.debug("Add item: " + itemsb.toString() + "="
							+ item.getValue());
				}
			}
			nodesMap.put(sb.toString(), pairs);
			if (!nodeChildren.contains(config.getName())) {
				nodeChildren.add(config.getName());
			}
			
			init0(config.getNodes(), sb);
		}
		nodeNamesMap.put(parent.toString(), nodeChildren);
	}

	public String getEncoding() {
		return itemsMap.get("/System/encoding");
	}

	public Set<String> getConfigItemPaths() {
		return itemsMap.keySet();
	}
	
	public Set<String> getConfigNodePaths() {
		return nodesMap.keySet();
	}

	public String getValue(String path) {
		return itemsMap.get(path);
	}

	public String getExpression(String path) {
		return expressionMap.get(path);
	}
	
	public Map<String, String> getNodeItems(String path) {
		if (nodesMap.containsKey(path)) {
			return nodesMap.get(path);
		}
		return Collections.emptyMap();
	}
	
	public List<String> getNodeChildren(String path) {
		if (nodeNamesMap.containsKey(path)) {
			return nodeNamesMap.get(path);
		}
		return Collections.emptyList();
	}
	
	public boolean exists(String path) {
		return itemsMap.containsKey(path);
	}

	public Object readFromFastCache(String key) {
		return fastreadCacheMap.get(key);
	}
	
	public boolean existInFastCache(String key) {
		return fastreadCacheMap.containsKey(key);
	}
	
	public void putInFastCache(String key, Object value) {
		fastreadCacheMap.put(key, value);
	}
}
