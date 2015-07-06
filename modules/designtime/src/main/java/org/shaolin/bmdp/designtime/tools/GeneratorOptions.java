package org.shaolin.bmdp.designtime.tools;

import java.io.File;
import java.util.Properties;

import org.shaolin.bmdp.designtime.immocompiler.MemoryClassLoader;


public class GeneratorOptions {
	
	private final String groupId;
	
	private final String projectName;
	
	private final String i18nFileName;
	
	private final String i18nBundleName;
	
	private final File entitiesDirectory;
	
	private final File resourcesDir;
	
	private final File srcDir;

	private final File testDir;
	
	private final File webDir;
	
	private final File hbmDirectory;
	
	private final File sqlDir;
	
	private final File targetClasses;

	private final String sqlVendorType;
	
	private Properties i18nProperty;

	private MemoryClassLoader memoryCL;
	
	public GeneratorOptions(String groupId, String projectName, 
			File entitiesDirectory,
			File resourcesDir,
			File srcDir, 
			File testDir,
			File webDir, 
			File hbmDirectory, 
			File sqlDir, 
			File targetClasses, 
			String vendor) {
		this.groupId = groupId;
		this.projectName = projectName;
		this.i18nFileName = groupId.replace('.', '_') + "_" + projectName + "_i18n_en_US.properties";
		this.i18nBundleName = groupId.replace('.', '_') + "_" + projectName + "_i18n";
		this.entitiesDirectory = entitiesDirectory;
		this.resourcesDir = resourcesDir;
		this.srcDir = srcDir;
		this.testDir = testDir;
		this.webDir = webDir;
		this.hbmDirectory = hbmDirectory;
		this.sqlDir = sqlDir;
		this.targetClasses = targetClasses;
		this.sqlVendorType = vendor;
	}

	public String getGroupId() {
		return groupId;
	}
	
	public String getProjectName() {
		return projectName;
	}
	
	public String getBundleName() {
		return groupId + "." + projectName;
	}
	
	public String geti18nFileName() {
		return i18nFileName;
	}
	
	public String geti18nBundleName() {
		return i18nBundleName;
	}
	
	public File getEntitiesDirectory() {
		return entitiesDirectory;
	}

	public String getSqlVendorType() {
		return sqlVendorType;
	}
	
	public File getHBMDirectory() {
		return hbmDirectory;
	}

	public File getSrcDir() {
		return srcDir;
	}

	public File getTestDir() {
		return testDir;
	}
	
	public File getWebDir() {
		return webDir;
	}
	
	public File getSqlDir() {
		return sqlDir;
	}
	
	public File getTargetClassesDir() {
		return targetClasses;
	}
	
	public File getResourcesDir() {
		return resourcesDir;
	}

	public Properties getI18nProperty() {
		return i18nProperty;
	}

	public void setI18nProperty(Properties i18nProperty) {
		this.i18nProperty = i18nProperty;
	}

	public MemoryClassLoader getBEMemoryClassLoader() {
		return memoryCL;
	}
	
	public void setBEMemoryClassLoader(MemoryClassLoader memoryCL) {
		this.memoryCL = memoryCL;
	}
}
