package org.shaolin.bmdp.runtime.entity;

import org.shaolin.bmdp.datamodel.common.EntityType;


public class EntityUpdatedEvent<T extends EntityType, D>{

	private final D diagram;

	private final T oldEntity;
	
	private final T newEntity;
	
	public EntityUpdatedEvent(D diagram, T oldEntity, T newEntity) {
		this.diagram = diagram;
		this.oldEntity = oldEntity;
		this.newEntity = newEntity;
	}
	
	public T getNewEntity() {
		return newEntity;
	}

	public T getOldEntity() {
		return oldEntity;
	}
	
	public D getDiagram() {
		return diagram;
	}
	
	public boolean hasDiagram() {
		return diagram != null;
	}
}
