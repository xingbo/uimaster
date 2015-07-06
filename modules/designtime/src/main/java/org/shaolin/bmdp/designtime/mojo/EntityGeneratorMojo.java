package org.shaolin.bmdp.designtime.mojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
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
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.Registry;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.entity.IEntityEventListener;
import org.shaolin.bmdp.runtime.internal.AppServiceManagerImpl;
import org.shaolin.bmdp.runtime.spi.IEntityManager;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;
import org.shaolin.bmdp.utils.CloseUtil;

/**
 * @goal generate-entity
 * @phase generate-sources
 * @author Shaolin
 *
 */
public class EntityGeneratorMojo extends AbstractMojo {
	
	// read-only parameters ---------------------------------------------------
    /**
     * The maven project.
     * 
     * @parameter expression="${project}"
     * @readonly
     */
    private MavenProject project;

	/**
     * project/target/classes
     * 
     * @parameter expression="${project.build.outputDirectory}"
     */
    private File targetClasses;
	
    /**
     * project/src/main/java
     * 
     * @parameter expression="${project.build.sourceDirectory}"
     */
    private File srcDirectory;
    
    /**
     * project/src/test/java
     * 
     * @parameter expression="${basedir}/src/test/java"
     */
    private File testDirectory;
    
    /**
     * 
     * @parameter expression="${basedir}/src/main/resources/entities"
     */
    private File entitiesDirectory;
    
    /**
     * 
     * @parameter expression="${generate-entity.systemEntityPath}"
     */
    private String systemEntityPath;
    
    /**
     * 
     * @parameter expression="${basedir}/src/main/resources"
     */
    private File resourcesDir;
    
    /**
     * 
     * @parameter expression="${basedir}/src/other/web"
     */
    private File webDirectory;
    
    /**
     * 
     * @parameter expression="${basedir}/src/other/sql"
     */
    private File sqlDirectory;
    
    /**
     * 
     * @parameter expression="${basedir}/src/other/hbm"
     */
    private File hbmDirectory;

    /**
     * 
     * @parameter expression="${generate-entity.genUIComponents}" default-value="true"
     */
    private boolean genUIComponents = true;
    
    // Mojo methods -----------------------------------------------------------
    /**
     * The project's classpath.
     * 
     * @parameter expression="${project.compileClasspathElements}"
     * @readonly
     */
    private List<String> classpathElements;

    // AbstractAptMojo methods ------------------------------------------------

    protected List<String> getClasspathElements() {
        return classpathElements;
    }
	
    /**
     * Gets the Maven project.
     * 
     * @return the project
     */
    protected MavenProject getProject() {
        return project;
    }
    
    public void execute() throws MojoExecutionException, MojoFailureException  {
    	if (!entitiesDirectory.exists()) {
    		return;
    	}
    	if (!srcDirectory.exists()) {
    		srcDirectory.mkdirs();
    	}
    	if (!webDirectory.exists()) {
    		webDirectory.mkdirs();
    	}
    	if (!sqlDirectory.exists()) {
    		sqlDirectory.mkdirs();
    	}
    	
    	this.getLog().info("SrcDirectory: " + srcDirectory.getAbsolutePath());
    	this.getLog().info("EntitiesDirectory: " + entitiesDirectory.getAbsolutePath());
    	this.getLog().info("WebDirectory: " + webDirectory.getAbsolutePath());
    	this.getLog().info("SqlDirectory: " + sqlDirectory.getAbsolutePath());
    	
    	// initialize registry
		Registry.getInstance().initRegistry();
    	
		GeneratorOptions options = new GeneratorOptions(project.getGroupId(),
				project.getArtifactId(), entitiesDirectory, resourcesDir,
				srcDirectory, testDirectory, webDirectory, 
				hbmDirectory, sqlDirectory, targetClasses, 
				DBMSProviderFactory.MYSQL);
		options.setI18nProperty(readProperties(options)); 
		
		if (classpathElements.size() == 1 && 
				(Thread.currentThread().getContextClassLoader() instanceof URLClassLoader)) {
			//TODO: the dependent jar will not be existed in while generating source phrase.
			// we manually add the jar with the same group id here. this is just a workaround.
			// looking for a better solution.
			try {
				String projectGroupId = this.getProject().getGroupId();
				String projectArtifactId = this.getProject().getArtifactId();
				List<String> findjars = new ArrayList<String>();
				List<Dependency> dependencies = this.getProject().getDependencies();
				for (Dependency d : dependencies) {
					if (projectGroupId.equals(d.getGroupId())) {
						String newpath = classpathElements.get(0).replace(projectArtifactId, d.getArtifactId());
						newpath = newpath.substring(0,newpath.lastIndexOf(File.separatorChar));
						File folder = new File(newpath);
						String findjar = null;
						String[] files = folder.list();
						for (String f : files) {
							if (f.endsWith(".jar")) {
								findjar = f;
								break;
							}
						}
						if (findjar != null) {
							String jar = "file:///" + newpath + File.separatorChar + findjar;
							findjars.add(jar);
						}
					}
				}
				if (!findjars.isEmpty()) {
					URL[] urls = new URL[findjars.size()];
					for (int i = 0; i < findjars.size(); i++) {
						urls[i] = new URL(findjars.get(i));
					}
					URLClassLoader urlCL = ((URLClassLoader) (Thread.currentThread().getContextClassLoader()));
					URLClassLoader newCL = URLClassLoader.newInstance(urls, urlCL);
					Thread.currentThread().setContextClassLoader(newCL);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		List<IEntityEventListener<? extends EntityType, ?>> listeners 
			= new ArrayList<IEntityEventListener<? extends EntityType, ?>>();
		// core.
		CESourceGenerator ceGenerator = new CESourceGenerator(options);
		BESourceGenerator beGenerator = new BESourceGenerator(options, ceGenerator, classpathElements);
		listeners.add(ceGenerator);
		listeners.add(beGenerator);
		
		// add more DB schemas support.
		listeners.add(new RbdDiagramGenerator(options));
		listeners.add(new MySQLSchemaGenerator(options));
		listeners.add(new HibernateMappingGenerator(options));
		listeners.add(new DaoGenerator(options));
		
		// ui parts.
		if (genUIComponents) { // easy for manual dev.
			listeners.add(new UIPageGenerator(options));
			listeners.add(new UIFormJSGenerator(options));
			listeners.add(new UIPageJSGenerator(options));
		}
		
		// initialize entity manager.
		String[] filters = new String[] {project.getName() + "/"};
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
	    	AppContext.register(new AppServiceManagerImpl("build_app", loader));
			
			// Classloader will be switched in designtime
			IEntityManager entityManager = IServerServiceManager.INSTANCE.getEntityManager();
			if (systemEntityPath != null) {
				ArrayList<File> files = new ArrayList<File>();
				if (systemEntityPath.indexOf(";") != -1) {
					String[] paths = systemEntityPath.split(";");
					for (String p : paths) {
						files.add(new File(p));
					}
				} else {
					files.add(new File(systemEntityPath));
				}
				files.add(entitiesDirectory);
				((EntityManager)entityManager).init(listeners, filters, files.toArray(new File[files.size()]));
			} else {
				((EntityManager)entityManager).init(listeners, filters, new File[]{entitiesDirectory});
			}
		} finally {
			storeProperties(options.getI18nProperty(), options);
			Thread.currentThread().setContextClassLoader(loader);
		}
	}
	
	private Properties readProperties(GeneratorOptions options) {
		if (!resourcesDir.exists()) {
			resourcesDir.mkdirs();
		}
		File resource_en = new File(resourcesDir, options.geti18nFileName());
		Properties property = new Properties();
		if (resource_en.exists()) {
			InputStream in = null;
			try {
				in = new FileInputStream(resource_en);
				property.load(in);
			} catch (IOException e) {
			} finally {
				CloseUtil.close(in);
			}
		}
		return property;
	}
	
	private void storeProperties(Properties property, GeneratorOptions options) {
		File resource_en = new File(resourcesDir, options.geti18nFileName());
		OutputStream out = null;
		try {
			property = resort(property);
			out = new FileOutputStream(resource_en);
			property.store(out, "English localization file!");
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
