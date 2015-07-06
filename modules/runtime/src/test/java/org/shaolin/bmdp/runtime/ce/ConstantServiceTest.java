package org.shaolin.bmdp.runtime.ce;

import junit.framework.Assert;

import org.junit.Test;

public class ConstantServiceTest {

	@Test
	public void dynamicConstant() {
		
		DynamicConstant ce = new DynamicConstant("testce", 10);
		ce.addConstant(new DynamicConstant("Item0", 0, "i18nKey"));
		ce.addConstant(new DynamicConstant("Item1", 1, "i18nKey"));
		ce.addConstant(new DynamicConstant("Item2", 2, "i18nKey"));
		
		Assert.assertNotNull(ce.get("Item0"));
		Assert.assertEquals(ce.getEntityName(), "testce");
		Assert.assertEquals(ce.getRecordId(), 10);
		Assert.assertEquals(ce.getConstantList().size(), 4);
		
	}
	
}
