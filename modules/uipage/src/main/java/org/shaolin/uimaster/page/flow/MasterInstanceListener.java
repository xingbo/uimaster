package org.shaolin.uimaster.page.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.shaolin.bmdp.datamodel.common.DiagramType;
import org.shaolin.bmdp.datamodel.page.UIEntity;
import org.shaolin.bmdp.datamodel.page.UIPage;
import org.shaolin.bmdp.datamodel.pagediagram.WebChunk;
import org.shaolin.bmdp.runtime.Registry;
import org.shaolin.bmdp.runtime.cache.CacheManager;
import org.shaolin.bmdp.runtime.cache.ICache;
import org.shaolin.bmdp.runtime.entity.EntityAddedEvent;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.entity.EntityUpdatedEvent;
import org.shaolin.bmdp.runtime.entity.IEntityEventListener;
import org.shaolin.bmdp.runtime.internal.ServerServiceManagerImpl;
import org.shaolin.bmdp.runtime.spi.IEntityManager;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.WebConfig;
import org.shaolin.uimaster.page.cache.PageCacheManager;
import org.shaolin.uimaster.page.cache.UIFlowCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The master instance is responsible for reading all entities from the whole
 * system.
 * 
 * @author wushaol
 * 
 */
public class MasterInstanceListener implements ServletContextListener {

	private static final Logger logger = LoggerFactory.getLogger(MasterInstanceListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		((ServerServiceManagerImpl)IServerServiceManager.INSTANCE).setMasterNodeName(
				sce.getServletContext().getInitParameter("AppName"));
		
		// initial system configuration.
		Registry registry = Registry.getInstance();
		registry.initRegistry();
		
		List<String> cacheItems = registry.getNodeChildren("/System/caches");
    	for (String cacheName: cacheItems) {
    		Map<String, String> config = registry.getNodeItems("/System/caches/" + cacheName);
    		String maxSizeStr = config.get("maxSize");
    		String minutesStr = config.get("refreshTimeInMinutes");
    		String description = config.get("description");
    		int maxSize;
    		long minutes;
			try {
				maxSize = Integer.parseInt(maxSizeStr);
			} catch (NumberFormatException e) {
				maxSize = -1;
				logger.warn("maxSize format error, now use the default -1");
			}
			try {
				minutes = Long.parseLong(minutesStr);
			} catch (NumberFormatException e) {
				minutes = -1;
				logger.warn("refresh interval error, now use the default -1");
			}
    		ICache<String, ConcurrentHashMap> cache = CacheManager.getInstance().getCache(
    						cacheName, maxSize, false, String.class, ConcurrentHashMap.class);
    		cache.setRefreshInterval(minutes);
    		cache.setDescription(description);
    	}
    	WebConfig.setResourcePath(sce.getServletContext().getRealPath("/"));
		// load all entities from applications. only load once if there were many web instances.
		IEntityManager entityManager = IServerServiceManager.INSTANCE.getEntityManager();
		addEntityListeners(entityManager);
		((EntityManager)entityManager).initRuntime();

	}
	
	static void addEntityListeners(IEntityManager entityManager) {
		
		entityManager.addEventListener(new IEntityEventListener<WebChunk, DiagramType>() {
			@Override
			public void setEntityManager(EntityManager entityManager) {
			}

			@Override
			public void notify(
					EntityAddedEvent<WebChunk, DiagramType> event) {
				try {
					UIFlowCacheManager.getInstance().addChunk(
							event.getEntity());
				} catch (ParsingException e) {
					logger.error(
							"Parse ui flow error: " + e.getMessage(), e);
				}
			}

			@Override
			public void notify(
					EntityUpdatedEvent<WebChunk, DiagramType> event) {
				try {
					UIFlowCacheManager.getInstance().addChunk(
							event.getNewEntity());
				} catch (ParsingException e) {
					logger.error(
							"Parse ui flow error: " + e.getMessage(), e);
				}
			}

			@Override
			public void notifyLoadFinish(DiagramType diagram) {
			}

			@Override
			public void notifyAllLoadFinish() {
			}

			@Override
			public Class<WebChunk> getEventType() {
				return WebChunk.class;
			}

		});
		entityManager.addEventListener(new IEntityEventListener<UIPage, DiagramType>() {
			ArrayList<String> uipages = new ArrayList<String>();
			@Override
			public void setEntityManager(EntityManager entityManager) {
			}

			@Override
			public void notify(
					EntityAddedEvent<UIPage, DiagramType> event) {
				uipages.add(event.getEntity().getEntityName());
			}

			@Override
			public void notify(
					EntityUpdatedEvent<UIPage, DiagramType> event) {
			}

			@Override
			public void notifyLoadFinish(DiagramType diagram) {
			}

			@Override
			public void notifyAllLoadFinish() {
				for (String uipage: uipages) {
					PageCacheManager.removeUIPageCache(uipage);
					try {
						PageCacheManager.getODPageEntityObject(uipage);
						PageCacheManager.getUIPageObject(uipage);
					} catch (Exception e) {
						logger.error(
								"Parse ui page error: " + e.getMessage(), e);
					}
				}
				uipages.clear();
			}

			@Override
			public Class<UIPage> getEventType() {
				return UIPage.class;
			}
		});
		entityManager.addEventListener(new IEntityEventListener<UIEntity, DiagramType>() {
			ArrayList<String> uiforms = new ArrayList<String>();
			@Override
			public void setEntityManager(EntityManager entityManager) {
			}

			@Override
			public void notify(
					EntityAddedEvent<UIEntity, DiagramType> event) {
				uiforms.add(event.getEntity().getEntityName());
			}

			@Override
			public void notify(
					EntityUpdatedEvent<UIEntity, DiagramType> event) {
			}

			@Override
			public void notifyLoadFinish(DiagramType diagram) {
			}

			@Override
			public void notifyAllLoadFinish() {
				for (String uiform: uiforms) {
					PageCacheManager.removeUIFormCache(uiform);
					try {
						PageCacheManager.getODFormObject(uiform);
						PageCacheManager.getUIFormObject(uiform);
					} catch (Exception e) {
						logger.error(
								"Parse ui page error: " + e.getMessage(), e);
					}
				}
				uiforms.clear();
			}

			@Override
			public Class<UIEntity> getEventType() {
				return UIEntity.class;
			}
		});
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
