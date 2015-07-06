package org.shaolin.bmdp.runtime.entity;

import org.shaolin.bmdp.datamodel.common.EntityType;


public class EntityAddedEvent<T extends EntityType, D> {

	private final D diagram;
	
	private final T entity;
	
	public EntityAddedEvent(D diagram, T entity) {
		this.diagram = diagram;
		this.entity = entity;
	}
	
	public T getEntity() {
		return entity;
	}

	public D getDiagram() {
		return diagram;
	}
	
	public boolean hasDiagram() {
		return diagram != null;
	}
}
