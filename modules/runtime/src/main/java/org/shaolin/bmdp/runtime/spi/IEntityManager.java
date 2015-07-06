package org.shaolin.bmdp.runtime.spi;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.shaolin.bmdp.datamodel.common.DiagramType;
import org.shaolin.bmdp.datamodel.common.EntityType;
import org.shaolin.bmdp.runtime.entity.EntityNotFoundException;
import org.shaolin.bmdp.runtime.entity.IEntityEventListener;

/**
 * 
 * @author Shaolin
 *
 */
public interface IEntityManager {

	public static final String[] loadSequence = new String[] {
		"bediagram", "rdbdiagram", "form", "flow", "page", "pageflow", "workflow"
	};
	
	/**
	 * register the extension entity type.
	 * 
	 * @param suffix
	 * @param entityType
	 */
	void registerEntityType(String suffix, Class<?> entityType);

	/**
	 * Specify the load sequence of this entity type.
	 * 
	 * @param entityType
	 */
	void addLoadSequence(String entityType);

	/**
	 * pre-initial entity manager listener.
	 * 
	 * @param listener
	 */
	public void addListener(IEntityEventListener<? extends EntityType, ?> listener);
	
	/**
	 * pre-initial entity manager listener.
	 * 
	 * @param listeners
	 */
	void addListeners(List<IEntityEventListener<? extends EntityType, ?>> listeners);
	
	/**
	 * post-initial entity manager listener. this listener is not stored to the entity manager.
	 * 
	 * @param listeners
	 */
	void executeListener(IEntityEventListener<? extends EntityType, ?> listener);
	
	/**
	 * This is a design time method. it accepts the intermediate generated entity 
	 * and append to the generation process.
	 * 
	 * @param type entity type
	 * @param file the entity path
	 */
	void appendEntity(String type, File file);
	
	/**
	 * For the customization entity.
	 * 
	 * @param entity
	 */
	void appendEntity(EntityType entity);

	/**
	 * For the customization diagram.
	 * 
	 * @param diagram
	 */
	void appendEntity(DiagramType diagram);
	
	/**
	 * Load a directory.
	 * 
	 * @param path
	 * @throws IOException
	 */
	void reloadDir(File path) throws IOException;

	/**
	 * Load a directory with the file filters applied.
	 * 
	 * @param path
	 * @param fileFilters
	 * @throws IOException
	 */
	void reloadDir(File path, String[] fileFilters) throws IOException;
	
	/**
	 * 
	 * @param entity
	 * @throws IOException
	 */
	void reloadEntity(EntityType entity) throws IOException;

	/**
	 * 
	 * @param name
	 * @param type
	 * @return
	 * @throws EntityNotFoundException
	 */
	<T> T getEntity(String name, Class<T> type) throws EntityNotFoundException;

	/**
	 * 
	 * @param listener
	 */
	void addEventListener(IEntityEventListener<? extends EntityType, ?> listener);

	/**
	 * 
	 * @param listener
	 */
	void removeEventListener(IEntityEventListener<? extends EntityType, ?> listener);
}
