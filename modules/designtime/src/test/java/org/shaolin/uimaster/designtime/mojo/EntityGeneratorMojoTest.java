package org.shaolin.uimaster.designtime.mojo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.junit.Test;
import org.shaolin.bmdp.datamodel.common.EntityType;
import org.shaolin.bmdp.designtime.bediagram.BESourceGenerator;
import org.shaolin.bmdp.designtime.bediagram.CESourceGenerator;
import org.shaolin.bmdp.designtime.orm.DaoGenerator;
import org.shaolin.bmdp.designtime.orm.HibernateMappingGenerator;
import org.shaolin.bmdp.designtime.orm.MySQLSchemaGenerator;
import org.shaolin.bmdp.designtime.orm.RbdDiagramGenerator;
import org.shaolin.bmdp.designtime.page.UIFormJSGenerator;
import org.shaolin.bmdp.designtime.page.UIPageGenerator;
import org.shaolin.bmdp.designtime.page.UIPageJSGenerator;
import org.shaolin.bmdp.designtime.tools.GeneratorOptions;
import org.shaolin.bmdp.persistence.provider.DBMSProviderFactory;
import org.shaolin.bmdp.runtime.Registry;
import org.shaolin.bmdp.runtime.cache.CacheManager;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.entity.IEntityEventListener;
import org.shaolin.bmdp.runtime.spi.IEntityManager;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;
import org.shaolin.bmdp.utils.CloseUtil;
import org.shaolin.bmdp.utils.SerializeUtil;

public class EntityGeneratorMojoTest {

	@Test
	public void testRDB() {
		String root = "C:/uimaster/uimaster/modules/";
		
		File entitiesDirectory = new File(root + "designtime/src/test/resources/entities/");
		File resourcesDirectory = new File(root + "designtime/src/test/resources/");
		File srcDirectory = new File(root + "designtime\\src\\test\\java");
		File testDirectory = new File(root + "designtime\\src\\test\\java");
		File webDirectory = new File(root + "designtime\\src\\test\\other\\web");
		File hbmDirectory = new File(root + "designtime\\src\\test\\resources\\hbm");
		File sqlDirectory = new File(root + "designtime\\src\\test\\other\\sql");
		File targetClasses = new File(root + "designtime\\target\\classes");
		
		GeneratorOptions options = new GeneratorOptions(
				"org.shaolin.bmdp.", "designtime", 
				entitiesDirectory, resourcesDirectory,
				srcDirectory, testDirectory, webDirectory, hbmDirectory, 
				sqlDirectory, targetClasses, DBMSProviderFactory.MYSQL);
		Properties i18nProperty = new Properties();
		options.setI18nProperty(i18nProperty);
		
		List<IEntityEventListener<? extends EntityType, ?>> listeners 
		= new ArrayList<IEntityEventListener<? extends EntityType, ?>>();
		
		java.util.List<String> classpathElements = new java.util.ArrayList<String>();
		classpathElements.add(root + "common\\target\\org.shaolin.bmdp.common-3.1.0-SNAPSHOT.jar");
		classpathElements.add(root + "datamodel\\target\\org.shaolin.bmdp.datamodel-3.1.0-SNAPSHOT.jar");
		classpathElements.add(root + "javacc\\target\\org.shaolin.bmdp.javacc-3.1.0-SNAPSHOT.jar");
		classpathElements.add(root + "runtime\\target\\org.shaolin.bmdp.runtime-3.1.0-SNAPSHOT.jar");
		
		CESourceGenerator ceGenerator = new CESourceGenerator(options);
		listeners.add(ceGenerator);
		listeners.add(new BESourceGenerator(options, ceGenerator, classpathElements));
		
		listeners.add(new RbdDiagramGenerator(options));
		listeners.add(new MySQLSchemaGenerator(options));
		listeners.add(new HibernateMappingGenerator(options));
		listeners.add(new DaoGenerator(options));
		
		// ui parts.
		listeners.add(new UIPageGenerator(options));
		listeners.add(new UIFormJSGenerator(options));
		listeners.add(new UIPageJSGenerator(options));
		
		// initialize registry
		Registry.getInstance().initRegistry();
		
		String[] filters = new String[] {"/designtime/"};
		// initialize entity manager.
		IEntityManager entityManager = IServerServiceManager.INSTANCE.getEntityManager();
		((EntityManager)entityManager).init(listeners, filters, new File[]{entitiesDirectory});
		
		// check cache.
		CacheManager cacheManager = CacheManager.getInstance();
    	List<String> cacheItems = cacheManager.getCacheNames();
    	for (String item : cacheItems) {
    		try { 
	    		byte[] cacheObject = SerializeUtil.serializeData(cacheManager.getCache(item, null, null));
    		} catch (Throwable e) {
    			e.printStackTrace();
    			//fail
    		}
    	}
		storeProperties(resourcesDirectory, options.getI18nProperty());
	}
	
	private void storeProperties(File resourcesDir, Properties property) {
		File resource_en = new File(resourcesDir, "i18n_en_US.properties");
		OutputStream out = null;
		try {
			property = resort(property);
			out = new FileOutputStream(resource_en);
			property.store(out, "Updated: " + (new Date()).toString());
		} catch (IOException e) {
		} finally {
			CloseUtil.close(out);
		}
	}
	
	public Properties resort(Properties map) {
		SortedProperties newP = new SortedProperties();
		Set<Entry<Object,Object>> set = map.entrySet();
		for (Map.Entry<Object, Object> entry : set) {
			newP.setProperty(entry.getKey().toString(), entry.getValue().toString());
		}
		return newP;
	}

	class SortedProperties extends Properties {
		public Enumeration keys() {
			Enumeration keysEnum = super.keys();
			Vector<String> keyList = new Vector<String>();
			while (keysEnum.hasMoreElements()) {
				keyList.add((String) keysEnum.nextElement());
			}
			Collections.sort(keyList);
			return keyList.elements();
		}
	}
	
}
