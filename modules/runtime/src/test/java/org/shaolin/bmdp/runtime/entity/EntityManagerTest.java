package org.shaolin.bmdp.runtime.entity;

import java.io.StringWriter;
import java.util.Map;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.shaolin.bmdp.datamodel.bediagram.BusinessEntityType;
import org.shaolin.bmdp.datamodel.bediagram.ConstantEntityType;
import org.shaolin.bmdp.datamodel.registry.ItemConfigType;
import org.shaolin.bmdp.datamodel.registry.NodeConfigType;
import org.shaolin.bmdp.runtime.Registry;
import org.shaolin.bmdp.runtime.spi.IEntityManager;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;

public class EntityManagerTest {

	@Test
	public void test() {
		try {
			Registry registry = Registry.getInstance();
			registry.initRegistry();
			Assert.assertEquals("UTF-8", registry.getEncoding());
			Map<String, String> pairs = registry.getNodeItems(
					"/System/security/LDAPSecurity");
			Logger.getLogger(EntityManagerTest.class).debug(pairs.toString());
			Logger.getLogger(EntityManagerTest.class).debug(registry.getConfigNodePaths());
			
			
			IEntityManager entityManager = IServerServiceManager.INSTANCE.getEntityManager();
			((EntityManager)entityManager).initRuntime();
			
			BusinessEntityType be = entityManager.getEntity("org.shaolin.uimaster.be.Customer", BusinessEntityType.class);
			Assert.assertNotNull(be);
			ConstantEntityType ce = entityManager.getEntity("org.shaolin.uimaster.ce.Gender", ConstantEntityType.class);
			Assert.assertNotNull(ce);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRegistry() {
		try {
			org.shaolin.bmdp.datamodel.registry.Registry config = new org.shaolin.bmdp.datamodel.registry.Registry();
			
			NodeConfigType node0 = new NodeConfigType();
			node0.setName("System");
			config.getNodes().add(node0);
			
			NodeConfigType node00 = new NodeConfigType();
			node00.setName("webConstant");
			node0.getNodes().add(node00);
			
			ItemConfigType item0 = new ItemConfigType();
			item0.setName("contextRoot");
			item0.setValue("/uimaster");
			node00.getItems().add(item0);
			
			StringWriter writer = new StringWriter();
			EntityUtil.marshaller(config, writer);

			Logger.getLogger(EntityManagerTest.class).debug("Test composited a registry: \n" + writer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
