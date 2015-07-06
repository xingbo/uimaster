package org.shaolin.bmdp.runtime.entity;

import org.shaolin.bmdp.datamodel.common.DiagramType;
import org.shaolin.bmdp.datamodel.common.EntityType;

/**
 * Entity manager listener.
 * 
 * @author Administrator
 *
 * @param <T>
 */
public interface IEntityEventListener<T extends EntityType, D extends DiagramType> {

	void setEntityManager(EntityManager entityManager);

	/**
	 * this notification only happens after an entity being added.
	 * 
	 * @param event
	 */
	void notify(EntityAddedEvent<T, D> event);

	/**
	 * this notification only happens after an entity being replaced.
	 * 
	 * @param event
	 */
	void notify(EntityUpdatedEvent<T, D> event);

	/**
	 * this notification only happens after a diagram being loaded.
	 * 
	 * @param diagram
	 */
	void notifyLoadFinish(DiagramType diagram);
	
	/**
	 * this notification only happens when the overall loading action finished.
	 * this action is able to resolve the issue in the entity dependencies.
	 */
	void notifyAllLoadFinish();
	
	Class<T> getEventType();

}
