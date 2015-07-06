package org.shaolin.bmdp.persistence.query.generator;

import org.shaolin.javacc.sql.SQLConstants;

/**
 * Table alias generator.
 */
public class AliasGenerator {
	
	// alias seed
	private int aliasId = 1;

	/**
	 * Default constructor
	 */
	public AliasGenerator() {
	}

	/**
	 * Get new alias unique from this generator.
	 * 
	 * @return new table alias
	 */
	public String getNewAlias() {
		return SQLConstants.TABLE_ALIAS + (aliasId++);
	}

	public int getNewIndex() {
		return aliasId++;
	}

	public void increase(int count) {
		aliasId += count;
	}

}
