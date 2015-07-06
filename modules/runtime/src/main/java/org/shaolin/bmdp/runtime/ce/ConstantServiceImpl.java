package org.shaolin.bmdp.runtime.ce;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.shaolin.bmdp.datamodel.bediagram.BEDiagram;
import org.shaolin.bmdp.datamodel.bediagram.ConstantEntityType;
import org.shaolin.bmdp.datamodel.common.DiagramType;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.bmdp.runtime.cache.CacheManager;
import org.shaolin.bmdp.runtime.cache.ICache;
import org.shaolin.bmdp.runtime.entity.EntityAddedEvent;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.entity.EntityNotFoundException;
import org.shaolin.bmdp.runtime.entity.EntityUpdatedEvent;
import org.shaolin.bmdp.runtime.entity.IEntityEventListener;
import org.shaolin.bmdp.runtime.spi.IConstantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Constant Runtime Service for every single application.
 * 
 * @author wushaol
 *
 */
public class ConstantServiceImpl implements Serializable, IConstantService, IEntityEventListener<ConstantEntityType, BEDiagram> {

	private static final long serialVersionUID = 7967098596510060235L;

	private static final Logger logger = LoggerFactory.getLogger(ConstantServiceImpl.class);
	
	private final ICache<String, IConstantEntity> serverConstantMap;
	
	public ConstantServiceImpl() {
		serverConstantMap = CacheManager.getInstance().getCache(
				"__sys_constants_cache", String.class, IConstantEntity.class);
	}
	
	/**
	 * Import data from data base, all constant entities can be overrided.
	 * 
	 * @param constants
	 */
	public void importData(IConstantEntity[] constants) {
		for (IConstantEntity ce: constants) {
			logger.info("Load the constant entity: " + ce.getEntityName() + " from DB data.");
			serverConstantMap.put(ce.getEntityName(), ce);
		}
	}
	
	public void reloadData(IConstantEntity constant) {
		logger.info("Reload the constant: " + constant.getEntityName());
		this.serverConstantMap.put(constant.getEntityName(), constant);
	}
	
	public List<IConstantEntity> getAppConstants(IConstantEntity condition, int offset, int count) {
		List<IConstantEntity> temp = new ArrayList<IConstantEntity>(serverConstantMap.getValues());
		
		int start = offset * count;
		int end = start + count;
		List<IConstantEntity> result = new ArrayList<IConstantEntity>(count);
		for (; (start < temp.size() && start < end); start++) {
			result.add(temp.get(start));
		}
		return result;
	}
	
	public List<IConstantEntity> getServerConstants(IConstantEntity condition, int offset, int count) {
		List<IConstantEntity> temp = new ArrayList<IConstantEntity>(serverConstantMap.getValues());
		
		int start = offset * count;
		int end = start + count;
		List<IConstantEntity> result = new ArrayList<IConstantEntity>(count);
		for (; (start < temp.size() && start < end); start++) {
			result.add(temp.get(start));
		}
		return result;
	}
	
	public int getServerConstantCount(IConstantEntity condition) {
		return serverConstantMap.size();
	}
	
	public IConstantEntity getConstantEntity(String ceName) {
		return this.loadFromCache(ceName, true);
	}
	
	public boolean hasConstantEntity(String ceName) {
		return this.loadFromCache(ceName, false) != null;
	}

	@Override
	public void setEntityManager(EntityManager entityManager) {
	}

	@Override
	public void notify(EntityAddedEvent<ConstantEntityType, BEDiagram> event) {
		loadFromCache(event.getEntity().getEntityName(), true);
	}

	@Override
	public void notify(EntityUpdatedEvent<ConstantEntityType, BEDiagram> event) {
	}

	@Override
	public void notifyLoadFinish(DiagramType diagram) {
	}

	@Override
	public void notifyAllLoadFinish() {
	}

	@Override
	public Class<ConstantEntityType> getEventType() {
		return ConstantEntityType.class;
	}
	
	private IConstantEntity loadFromCache(String ceName, boolean needException) {
		if (serverConstantMap.containsKey(ceName)) {
			return serverConstantMap.get(ceName);
		} else {
			try {
				Class<?> clazz = Class.forName(ceName, false, Thread.currentThread().getContextClassLoader());
				IConstantEntity ceEntity = (IConstantEntity)clazz.newInstance();
				serverConstantMap.put(ceEntity.getEntityName(), ceEntity);
				return ceEntity;
			} catch (Exception e) {
				if (needException) {
					throw new EntityNotFoundException(
							ExceptionConstants.EBOS_COMMON_002, e,
							new Object[] { ceName });
				}
			}
			return null;
		}
	}
}
