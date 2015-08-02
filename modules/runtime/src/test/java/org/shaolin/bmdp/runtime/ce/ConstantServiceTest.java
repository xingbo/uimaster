package org.shaolin.bmdp.runtime.ce;

import junit.framework.Assert;

import org.junit.Test;

public class ConstantServiceTest {

	@Test
	public void dynamicConstant() {
		
		DynamicConstant ce = new DynamicConstant("testce", 10);
		ce.addConstant(new DynamicConstant(0, "Item0", 0, "i18nkey", "hello"));
		ce.addConstant(new DynamicConstant(1, "Item1", 1, "i18nkey", "hello"));
		ce.addConstant(new DynamicConstant(2, "Item2", 2, "i18nkey", "hello"));
		
		Assert.assertNotNull(ce.get("Item0"));
		Assert.assertEquals(ce.getEntityName(), "testce");
		Assert.assertEquals(ce.getConstantList().size(), 4);
		
	}
	
}
