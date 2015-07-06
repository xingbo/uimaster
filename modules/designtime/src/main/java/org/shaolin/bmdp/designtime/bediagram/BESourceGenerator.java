package org.shaolin.bmdp.designtime.bediagram;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.shaolin.bmdp.datamodel.bediagram.BEComplexType;
import org.shaolin.bmdp.datamodel.bediagram.BEDiagram;
import org.shaolin.bmdp.datamodel.bediagram.BEListType;
import org.shaolin.bmdp.datamodel.bediagram.BEMapType;
import org.shaolin.bmdp.datamodel.bediagram.BEObjRefType;
import org.shaolin.bmdp.datamodel.bediagram.BEPrimitiveType;
import org.shaolin.bmdp.datamodel.bediagram.BESetType;
import org.shaolin.bmdp.datamodel.bediagram.BEType;
import org.shaolin.bmdp.datamodel.bediagram.BinaryType;
import org.shaolin.bmdp.datamodel.bediagram.BooleanType;
import org.shaolin.bmdp.datamodel.bediagram.BusinessEntityType;
import org.shaolin.bmdp.datamodel.bediagram.CEObjRefType;
import org.shaolin.bmdp.datamodel.bediagram.ConstantEntityType;
import org.shaolin.bmdp.datamodel.bediagram.DateTimeType;
import org.shaolin.bmdp.datamodel.bediagram.DoubleType;
import org.shaolin.bmdp.datamodel.bediagram.FileType;
import org.shaolin.bmdp.datamodel.bediagram.IntType;
import org.shaolin.bmdp.datamodel.bediagram.JavaObjRefType;
import org.shaolin.bmdp.datamodel.bediagram.LongType;
import org.shaolin.bmdp.datamodel.bediagram.MemberType;
import org.shaolin.bmdp.datamodel.bediagram.ObjectRefType;
import org.shaolin.bmdp.datamodel.bediagram.PersistenceTypeType;
import org.shaolin.bmdp.datamodel.bediagram.PersistentConfigType;
import org.shaolin.bmdp.datamodel.bediagram.StringType;
import org.shaolin.bmdp.datamodel.bediagram.TimeType;
import org.shaolin.bmdp.datamodel.common.DiagramType;
import org.shaolin.bmdp.datamodel.common.TargetEntityType;
import org.shaolin.bmdp.datamodel.common.TargetJavaType;
import org.shaolin.bmdp.datamodel.common.VariableType;
import org.shaolin.bmdp.designtime.immocompiler.MemoryCompiler;
import org.shaolin.bmdp.designtime.tools.GeneratorOptions;
import org.shaolin.bmdp.designtime.tools.PumpWriter;
import org.shaolin.bmdp.runtime.Registry;
import org.shaolin.bmdp.runtime.VariableUtil;
import org.shaolin.bmdp.runtime.be.BEUtil;
import org.shaolin.bmdp.runtime.entity.EntityAddedEvent;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.entity.EntityUpdatedEvent;
import org.shaolin.bmdp.runtime.entity.IEntityEventListener;
import org.shaolin.bmdp.utils.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BESourceGenerator implements IEntityEventListener<BusinessEntityType, BEDiagram> {
	
	private static final Logger logger = LoggerFactory.getLogger(BESourceGenerator.class.getName());

	private static Map<Class<?>, String> dataTypeToJavaTypeMap 
		= new HashMap<Class<?>, String>();
	
	private final PumpWriter out = new PumpWriter();
	
	private GeneratorOptions option = null;
	
	private final CESourceGenerator ceGenerator;
	
	private List<String> classpathElements = null;
	
	private EntityManager entityManager;
	
	private List<String> generatedBEs = new ArrayList<String>();

	private boolean genExtension = false;

	static {
		dataTypeToJavaTypeMap.put(BooleanType.class, "boolean");
		dataTypeToJavaTypeMap.put(IntType.class, "int");
		dataTypeToJavaTypeMap.put(LongType.class, "long");
		dataTypeToJavaTypeMap.put(DoubleType.class, "double");
		dataTypeToJavaTypeMap.put(BinaryType.class, "byte[]");
		dataTypeToJavaTypeMap.put(StringType.class, "java.lang.String");
		// needed to update!!!
		dataTypeToJavaTypeMap.put(FileType.class, "java.lang.String");
		dataTypeToJavaTypeMap.put(DateTimeType.class, "java.util.Date");
		dataTypeToJavaTypeMap.put(TimeType.class, "java.sql.Time");
	}
	
	public BESourceGenerator(GeneratorOptions option, CESourceGenerator ceGenerator, List<String> classpathElements) {
		this.option = option;
		this.ceGenerator = ceGenerator;
		this.classpathElements = classpathElements;
		
		out.setOutputDir(option.getSrcDir());
		out.setEncoding(Registry.getInstance().getEncoding());
	}
	
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	// temporary global variables.
	private static String cePackage;
	private static String bePackage;
	
	public void notify(EntityAddedEvent<BusinessEntityType, BEDiagram> event) {
		if (event.getDiagram().getBePackage().indexOf(option.getBundleName()) == -1) {
			return;
		}
		
		out.write("\n\n");
		
		File interfaceRoot = option.getSrcDir();
		File implementRoot = option.getSrcDir();
		String entityName = event.getEntity().getEntityName();
		cePackage = event.getDiagram().getCePackage();
		bePackage = event.getDiagram().getBePackage();
		String packageName = entityName.substring(0, entityName.lastIndexOf("."));
		String name = entityName.substring(entityName.lastIndexOf(".") + 1);
		String interfaceJava = packageName + ".I" + name;
		String implementJava = entityName + "Impl";
		
		try {
			generateBEInterface(event.getEntity(), interfaceRoot, interfaceJava, out);
		} catch (Exception e) {
			logger.error("Error when generate interface for " + entityName,
					e);
		}
		try {
			generateBEImplement(event.getEntity(), implementRoot, implementJava,
					interfaceJava, out);
		} catch (Exception e) {
			logger.error("Error when generate implement for " + entityName,
					e);
		}
		
		out.write("\n\n");
		out.finish();
	}

	public void notify(EntityUpdatedEvent<BusinessEntityType, BEDiagram> event) {
		
	}

	public Class<BusinessEntityType> getEventType() {
		return BusinessEntityType.class;
	}
	
	public void notifyLoadFinish(DiagramType diagram) {
		BEDiagram bediagram = (BEDiagram)diagram;
		if (bediagram.getBePackage().indexOf(option.getBundleName()) == -1) {
			return;
		}
		
		File ceDir = new File(option.getSrcDir(), bediagram.getCePackage().replace('.', '/'));
		if (!ceDir.exists()) {
			ceDir.mkdirs();
			
			// generate a fake class for compiling process.
			ConstantEntityType entity = new ConstantEntityType();
			entity.setEntityName(bediagram.getCePackage() + ".FakeCE");
			EntityAddedEvent<ConstantEntityType, BEDiagram> event = 
					new EntityAddedEvent(diagram, entity);
			ceGenerator.notify(event);
		}
	}
	
	public void notifyAllLoadFinish() {
		if (generatedBEs.isEmpty() && ceGenerator.getGeneratedCEs().isEmpty()) {
			return;
		}
		
		String sep = System.getProperty("path.separator");
		StringBuffer cp = new StringBuffer();
		if (Thread.currentThread().getContextClassLoader() instanceof URLClassLoader) {
			URLClassLoader urlCL = ((URLClassLoader) (Thread.currentThread().getContextClassLoader()));
			ClassLoader parent = urlCL.getParent();
			ClassLoader temp = parent;
			while (temp != null && temp instanceof URLClassLoader) {
				java.net.URL[] ulrs = ((URLClassLoader) (temp)).getURLs();
				for (java.net.URL url : ulrs) {
					cp.append(new java.io.File(url.getPath()));
					cp.append(sep);
				}
				temp = temp.getParent();
			}
			java.net.URL[] ulrs = urlCL.getURLs();
			for (java.net.URL url : ulrs) {
				cp.append(new java.io.File(url.getPath()));
				cp.append(sep);
			}
		}
		if (classpathElements != null && !classpathElements.isEmpty()) {
			// load into current class loader.
			for (String p : classpathElements) {
				cp.append(p).append(sep);
			}
		}
		// add ce classes.
		cp.append(option.getTargetClassesDir().getAbsolutePath());
		String classpath = cp.toString();
		
		logger.info("Pre-compile BE source code...");
		logger.debug("Built class path: " + classpath);
		logger.info("sourcepath: " + option.getSrcDir().getAbsolutePath());
		logger.info("targetpath: " + option.getTargetClassesDir().getAbsolutePath());
		
		if (!option.getTargetClassesDir().exists()) {
			option.getTargetClassesDir().mkdirs();
		}
		
		MemoryCompiler compiler = new MemoryCompiler();
		try {
			// compiling CEs from disk to momery.
			List<String> generatedCEs = ceGenerator.getGeneratedCEs();
			if (generatedCEs.size() > 0) {
				File[] files = new File[generatedCEs.size()];
				for (int i=0 ; i<generatedCEs.size(); i++) {
					files[i] = new File(generatedCEs.get(i));
				}
				compiler.compileToDisk(classpath, option.getTargetClassesDir().getAbsolutePath(), files);
				compiler.compile(classpath, option.getTargetClassesDir().getAbsolutePath(), files);
			}
			
			// compiling BEs.
			if (generatedBEs.size() > 0) {
				File[] files = new File[generatedBEs.size()];
				for (int i=0 ; i<generatedBEs.size(); i++) {
					files[i] = new File(generatedBEs.get(i));
				}
				compiler.compile(classpath, option.getTargetClassesDir().getAbsolutePath(), files);
			}
			// get MemoryClassLoader
			option.setBEMemoryClassLoader(compiler.getClassLoader());
			
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		} finally {
			ceGenerator.getGeneratedCEs().clear();
			generatedBEs.clear();
			compiler.close();
		}
	}

	private void generateBEInterface(BusinessEntityType businessEntity, File interfaceRoot,
			String interfaceJava, PumpWriter out) {
		if (interfaceJava != null) {
			String interfaceName = ClassUtil.getClassName(interfaceJava);
			String packageName = ClassUtil.getPackageName(interfaceJava);
			generateHeader(businessEntity, packageName, interfaceJava,
					interfaceRoot, true, out);

			out.write("\n\n/**\n * ");
			out.print(businessEntity.getDescription());
			out.write("\n * ");
			out.write("\n * This code is generated automatically, any change will be replaced after rebuild.");
			out.write("\n * ");
			out.write("\n *\n */\n\npublic interface ");
			out.print(interfaceName);
			out.write(" ");
			generateInterfaceExtends(businessEntity, out);
			out.write("\n{\n    public final static String ENTITY_NAME = \"");
			out.print(businessEntity.getEntityName());
			out.write("\";\n    \n ");
			generateGetDeclaration(businessEntity, out);
			generateSetDeclaration(businessEntity, out);

			if (genExtension) {
				// TODO:
			}

			out.write("\n\n}\n\n        ");

		}
	}

	private void generateBEImplement(BusinessEntityType businessEntity, File implementRoot,
			String implementJava, String interfaceJava, PumpWriter out) {
		if (implementJava != null) {
			String implClsName = ClassUtil.getClassName(implementJava);
			String implPkgName = ClassUtil.getPackageName(implementJava);
			String intfClsName = ClassUtil.getClassName(interfaceJava);
			String intfPkgName = ClassUtil.getPackageName(interfaceJava);
			generateHeader(businessEntity, implPkgName, implementJava,
					implementRoot, false, out);

			out.write("\n\n/**\n * ");
			out.print(businessEntity.getDescription());
			out.write("\n * ");
			out.write("\n * This code is generated automatically, any change will be replaced after rebuild.");
			out.write("\n * ");
			out.write("\n *\n */\n\npublic class ");
			out.print(implClsName);
			out.write(" ");
			generateImplementExtends(businessEntity, out);
			out.write(" implements ");
			out.print(interfaceJava);
			out.write("\n{\n    private static final long serialVersionUID = 0x90B1123CE87B50FFL;");
			out.write("\n\n    private final IConstantService ceService = AppContext.get().getConstantService();");
			out.write("\n\n    protected String getBusinessEntityName()\n    {\n        return \"");
			out.print(businessEntity.getEntityName());
			out.write("\";\n    }\n\n    public ");
			out.print(implClsName);
			out.write("()\n    {\n        ");
			generateConstructor(businessEntity, out);
			out.write("\n    }\n    \n    ");

			if (genExtension) {
				// TODO:
			}

			out.write("\n    ");
			generateAttrDeclaration(businessEntity, out);
			out.write("\n    ");
			generateGetDefinition(businessEntity, out);
			out.write("\n    ");
			generateSetDefinition(businessEntity, out);
			out.write("\n    ");
			generateEqualsMethod(businessEntity, out);
			out.write("\n    ");
			generateToStringMethod(businessEntity, out);
			out.write("\n    ");
			generateGetMemberListMethod(businessEntity, out);
			out.write("\n    public ");
			out.print(intfClsName);
			out.write(" createEntity ()\n    {\n");
			out.write("        return new ");
			out.print(implClsName);
			out.write("();\n    }\n    ");

			if (genExtension) {
				// TODO:
			}
			out.write("\n}\n\n        ");

		}
	}

	// //////////////////////////////////////////////////////////////////////
	private void generateHeader(BusinessEntityType businessEntity,
			String packageName, String fullClassName, File rootDir,
			boolean isInterface, PumpWriter out) {
		String fileName = getJavaFileName(rootDir, fullClassName);
		if (logger.isInfoEnabled()) {
			logger.info("BusinessEntity is {}, whose generated file is {}", 
					businessEntity.getEntityName(), fileName);
		}
		option.getI18nProperty().put(businessEntity.getEntityName(), businessEntity.getEntityName());
		generatedBEs.add(fileName);
		
		out.write("\n!!!!file ");
		out.print(fileName);
		out.write("\n/*\n *\n * This file is automatically generated on ");
		out.print(new Date());
		out.write("\n */\n\n    ");

		if ((packageName != null) && (!packageName.equals(""))) {
			out.write("\npackage ");
			out.print(packageName);
			out.write(";\n        ");
		}

		out.write("\nimport java.util.Collections;\n");
		out.write("import java.util.List;\n");
		out.write("import java.util.ArrayList;\n");

		out.write("\nimport org.shaolin.bmdp.datamodel.bediagram.*;\n");
		out.write("import org.shaolin.bmdp.datamodel.common.*;\n");
		out.write("import org.shaolin.bmdp.runtime.be.IBusinessEntity;\n");
		out.write("import org.shaolin.bmdp.runtime.be.IExtensibleEntity;\n");
		out.write("import org.shaolin.bmdp.runtime.be.IPersistentEntity;\n");
		out.write("import org.shaolin.bmdp.runtime.be.IHistoryEntity;\n");
		out.write("import org.shaolin.bmdp.runtime.be.BEExtensionInfo;\n\n");
		out.write("import org.shaolin.bmdp.runtime.spi.IConstantService;\n\n");
		out.write("import org.shaolin.bmdp.runtime.AppContext;\n\n");
		out.write("import org.shaolin.bmdp.runtime.ce.CEUtil;\n\n");
		List<MemberType> members = businessEntity.getMembers();
		for (MemberType m : members) {
			if (m.getType().getClass() == CEObjRefType.class) {
				out.write("import " + cePackage + ".*;\n");
				break;
			} else if (m.getType().getClass() == BEListType.class 
					&& ((BEListType)m.getType()).getElementType().getClass() == CEObjRefType.class) {
				out.write("import " + cePackage + ".*;\n");
				break;
			} else if (m.getType().getClass() == BESetType.class 
					&& ((BESetType)m.getType()).getElementType().getClass() == CEObjRefType.class) {
				out.write("import " + cePackage + ".*;\n");
				break;
			}
		}

	}

	private void generateInterfaceExtends(BusinessEntityType businessEntity, PumpWriter out) {
		String parentInterface = null;
		if (businessEntity.isNeedPersist()) {
			parentInterface = "IPersistentEntity";
		} else if (businessEntity.isNeedHistory()) {
			parentInterface = "IHistoryEntity";
		} else {
			parentInterface = "IBusinessEntity";
		}

		ObjectRefType parentObj = businessEntity.getParentObject();
		if (parentObj != null) {
			if (parentObj instanceof BEObjRefType) {
				parentInterface += ", "
						+ getBEInterfaceClassName(((BEObjRefType) parentObj)
								.getTargetEntity().getEntityName());
			} else {
				/**
				String parentImplement = getJavaClassName(((JavaObjRefType) parentObj)
						.getTargetJava());
				try {
					String parentBE = getBENameByImplementClassName(parentImplement);
					parentInterface += ", " + getBEInterfaceClassName(parentBE);
				} catch (EntityNotFoundException e) {
					logger.info("Can't get BusinessEntity for implement: "
							+ parentImplement);
				}
				*/
			}
		}

		if (isSelfExtensible(businessEntity)) {
			parentInterface += ", IExtensibleEntity";
		}

		out.write("\n    extends ");
		out.print(parentInterface);

	}

	private void generateImplementExtends(BusinessEntityType businessEntity, PumpWriter out) {
		ObjectRefType parentObj = businessEntity.getParentObject();
		if (parentObj != null) {
			String parentImplement = null;
			if (parentObj instanceof BEObjRefType)
				parentImplement = getBEImplementClassName(((BEObjRefType) parentObj)
						.getTargetEntity().getEntityName());
			else
				parentImplement = getJavaClassName(((JavaObjRefType) parentObj)
						.getTargetJava());

			out.write("\n    extends ");
			out.print(parentImplement);
			out.write("\n        ");

		}
	}

	private void generateConstructor(BusinessEntityType businessEntity, PumpWriter out) {
		List<MemberType> members = businessEntity.getMembers();
		for (MemberType member: members) {
			String attrName = member.getName();
			
			option.getI18nProperty().put(businessEntity.getEntityName() + '.' + attrName, attrName);
			
			BEType attrType = member.getType();
			if (attrType instanceof BESetType) {
				out.write("\n        ");
				out.print(attrName);
				out.write(" = new java.util.HashSet");
				out.print(BEUtil.getGenericType(((BESetType)attrType).getElementType()));
				out.write("();\n            ");
			} else if (attrType instanceof BEListType) {
				out.write("\n        ");
				out.print(attrName);
				out.write(" = new java.util.ArrayList");
				out.print(BEUtil.getGenericType(((BEListType)attrType).getElementType()));
				out.write("();\n            ");
			} else if (attrType instanceof BEMapType) {
				BEMapType beMapType = (BEMapType)attrType;
				out.write("\n        ");
				out.print(attrName);
				out.write(" = new java.util.HashMap");
				out.print(BEUtil.getGenericTypes(beMapType.getKeyType(), 
												 beMapType.getElementType()));
				out.write("();\n            ");
			}
		}
		if (isSelfExtensible(businessEntity)) {
			out.write("\n        _extField = new BEExtensionInfo();\n        ");
		}
	}

	private void generateGetDeclaration(BusinessEntityType beEntity, PumpWriter out) {
		List<MemberType> members = beEntity.getMembers();
		for (MemberType member: members) {
			if (member.isAccessible()) {
				String attrName = member.getName();
				BEType attrType = member.getType();
				String beanName = getBeanName(attrName);
				String attrTypeString = getDataTypeClassName(beEntity.isNeedPersist(), attrType);

				out.write("\n    /**\n     *  get ");
				out.print(attrName);
				out.write("\n     *\n     *  @return ");
				out.print(attrName);
				out.write("\n     */\n    public ");
				out.print(attrTypeString);
				out.write(" get");
				out.print(beanName);
				out.write("();\n");

			}
		}
	}

	private void generateSetDeclaration(BusinessEntityType beEntity, PumpWriter out) {
		List<MemberType> members = beEntity.getMembers();
		for (MemberType member: members) {

			if (member.isModifiable()) {
				String attrName = member.getName();
				BEType attrType = member.getType();
				String beanName = getBeanName(attrName);
				String attrTypeString = getDataTypeClassName(beEntity.isNeedPersist(), attrType);

				out.write("\n    /**\n     *  set ");
				out.print(attrName);
				out.write("\n     */\n    public void set");
				out.print(beanName);
				out.write("(");
				out.print(attrTypeString);
				out.write(" ");
				out.print(attrName);
				out.write(");\n");
			}
		}
	}

	private void generateGetDefinition(BusinessEntityType beEntity, PumpWriter out) {
		if (isSelfNeedPersist(beEntity) || isSelfNeedHistory(beEntity)) {
			out.write("    /**\n");
			out.write("     *  Is enable\n");
			out.write("     *\n");
			out.write("     *  @return boolean\n");
			out.write("     */\n");
			out.write("    public boolean isEnabled() {\n");
			out.write("        return _enable;\n");
			out.write("    }\n        ");
			out.write("    /**\n");
			out.write("     *  Is enable\n");
			out.write("     *\n");
			out.write("     *  @return boolean\n");
			out.write("     */\n");
			out.write("    private boolean get_enable() {\n");
			out.write("        return _enable;\n");
			out.write("    }\n        ");
		}
		
		if (isSelfNeedHistory(beEntity)) {
			out.write("\n    /**\n");
			out.write("     *  get version\n");
			out.write("     *\n");
			out.write("     *  @return version\n");
			out.write("     */\n");
			out.write("    public int getVersion() {\n");
			out.write("        return _version;\n");
			out.write("    }\n\n");
			out.write("    private int get_version() {\n");
			out.write("        return _version;\n");
			out.write("    }\n\n");
			out.write("    /**\n");
			out.write("     *  get operator user id\n");
			out.write("     *\n");
			out.write("     *  @return user id\n");
			out.write("     */\n");
			out.write("    public long getOptUserId() {\n");
			out.write("        return _optuserid;\n");
			out.write("    }\n\n");
			out.write("    /**\n");
			out.write("     *  get operator user id\n");
			out.write("     *\n");
			out.write("     *  @return user id\n");
			out.write("     */\n");
			out.write("    private long get_optuserid() {\n");
			out.write("        return _optuserid;\n");
			out.write("    }\n\n");
			out.write("    /**\n");
			out.write("     *  get start time\n");
			out.write("     *\n");
			out.write("     *  @return start time\n");
			out.write("     */\n");
			out.write("    public java.util.Date getStarttime() {\n");
			out.write("        return _starttime;\n");
			out.write("    }\n\n");
			out.write("    private java.util.Date get_starttime() {\n");
			out.write("        return _starttime;\n");
			out.write("    }\n\n");
			out.write("    /**\n");
			out.write("     *  get endtime\n");
			out.write("     *\n");
			out.write("     *  @return endtime\n");
			out.write("     */\n");
			out.write("    public java.util.Date getEndtime() {\n");
			out.write("        return _endtime;\n");
			out.write("    }\n\n");
			out.write("    private java.util.Date get_endtime() {\n");
			out.write("        return _endtime;\n");
			out.write("    }\n\n");
		}

		if (isSelfExtensible(beEntity)) {
			out.write("\n    /**\n");
			out.write("     *  get _extType\n");
			out.write("     *\n");
			out.write("     *  @return _extType\n");
			out.write("     */\n");
			out.write("    public String get_extType() {\n");
			out.write("        return _extType;\n");
			out.write("    }\n\n");
			out.write("    /**\n");
			out.write("     *  get _extField\n");
			out.write("     *\n");
			out.write("     *  @return _extField\n");
			out.write("     */\n");
			out.write("    public BEExtensionInfo get_extField() {\n");
			out.write("        return _extField;\n");
			out.write("    }\n        ");
		}

		List<MemberType> members = beEntity.getMembers();
		for (MemberType member: members) {
			if (member.isAccessible()) {
				String attrName = member.getName();
				BEType attrType = member.getType();
				String beanName = getBeanName(attrName);
				String attrTypeString = getDataTypeClassName(beEntity.isNeedPersist(), attrType);

				out.write("\n    /**\n     *  get ");
				out.print(attrName);
				out.write("\n     *\n     *  @return ");
				out.print(attrName);
				out.write("\n     */\n    public ");
				out.print(attrTypeString);
				out.write(" get");
				out.print(beanName);
				out.write("() {\n        return ");
				out.print(attrName);
				out.write(";\n    }\n");
				
				if (member.getType() instanceof CEObjRefType)  {
					String intAttrName = attrName + "Int";
					out.write("\n    /**\n     *  get ");
					out.print(intAttrName);
					out.write("\n     *\n     *  @return ");
					out.print(intAttrName);
					out.write("\n     */\n    private int get");
					out.print(beanName);
					out.write("Int() {\n        return ");
					out.print(intAttrName);
					out.write(";\n    }\n");
				} else if (member.getType().getClass() == BEListType.class 
						&& ((BEListType)member.getType()).getElementType().getClass() == CEObjRefType.class) {
					String intAttrName = attrName + "IntValues";
					out.write("\n    /**\n     *  get ");
					out.print(intAttrName);
					out.write("\n     *\n     *  @return ");
					out.print(intAttrName);
					out.write("\n     */\n    public String get");
					out.print(beanName);
					out.write("IntValues() {\n        return ");
					out.print(intAttrName);
					out.write(";\n    }\n");
				}
			}
		}
	}

	private void generateSetDefinition(BusinessEntityType beEntity, PumpWriter out) {
		if (isSelfNeedPersist(beEntity) || isSelfNeedHistory(beEntity)) {
			out.write("    /**\n");
			out.write("     *  set enable\n");
			out.write("     *  @parameter true or false.\n");
			out.write("     */\n");
			out.write("    public void setEnabled(boolean enable) {\n");
			out.write("        _enable = enable;\n");
			out.write("    }\n\n    ");
			out.write("    /**\n");
			out.write("     *  set enable\n");
			out.write("     *  @parameter true or false.\n");
			out.write("     */\n");
			out.write("    private void set_enable(boolean enable) {\n");
			out.write("        _enable = enable;\n");
			out.write("    }\n\n    ");
		}
			
		if (isSelfNeedHistory(beEntity)) {
			out.write("\n    /**\n");
			out.write("     *  set version\n");
			out.write("     */\n");
			out.write("    public void setVersion(int version) {\n");
			out.write("        _version = version;\n");
			out.write("    }\n\n");
			out.write("    private void set_version(int version) {\n");
			out.write("        _version = version;\n");
			out.write("    }\n\n");
			out.write("    /**\n");
			out.write("     *  set operator user id\n");
			out.write("     *  @parameter user id.\n");
			out.write("     */\n");
			out.write("    public void setOptUserId(long optUserId) {\n");
			out.write("        _optuserid = optUserId;\n");
			out.write("    }\n\n");
			out.write("    /**\n");
			out.write("     *  set operator user id\n");
			out.write("     *  @parameter user id.\n");
			out.write("     */\n");
			out.write("    private void set_optuserid(long optUserId) {\n");
			out.write("        _optuserid = optUserId;\n");
			out.write("    }\n\n");
			out.write("    /**\n");
			out.write("     *  set start time\n");
			out.write("     *  @parameter start time which is the start time of object.\n");
			out.write("     */\n");
			out.write("    public void setStarttime(java.util.Date starttime) {\n");
			out.write("        _starttime = starttime;\n");
			out.write("    }\n\n");
			out.write("    private void set_starttime(java.util.Date starttime) {\n");
			out.write("        _starttime = starttime;\n");
			out.write("    }\n\n");
			out.write("    /**\n");
			out.write("     *  set endtime\n");
			out.write("     *  @parameter endtime which is the end time of object.\n");
			out.write("     */\n");
			out.write("    public void setEndtime(java.util.Date endtime) {\n");
			out.write("        _endtime = endtime;\n");
			out.write("    }\n\n");
			out.write("    private void set_endtime(java.util.Date endtime) {\n");
			out.write("        _endtime = endtime;\n");
			out.write("    }\n\n");
		}

		if (isSelfExtensible(beEntity)) {
			out.write("/**\n");
			out.write("     *  set _extType\n");
			out.write("     *  @param _extType which is the extension type of be object.\n");
			out.write("     */\n");
			out.write("    public void set_extType(java.lang.String _extType) {\n");
			out.write("        this._extType = _extType;\n");
			out.write("    }\n        ");
		}

		List<MemberType> members = beEntity.getMembers();
		for (MemberType member: members) {

			if (member.isModifiable()) {
				String attrName = member.getName();
				BEType attrType = member.getType();
				String beanName = getBeanName(attrName);
				String attrTypeString = getDataTypeClassName(beEntity.isNeedPersist(), attrType);

				out.write("\n    /**\n     *  set ");
				out.print(attrName);
				out.write("\n     */\n    public void set");
				out.print(beanName);
				out.write("(");
				out.print(attrTypeString);
				out.write(" ");
				out.print(attrName);
				out.write(") {");
				generateSetBody(beEntity, attrTypeString, attrName, out);
				if (member.getType() instanceof CEObjRefType)  {
					String intAttrName = attrName + "Int";
					
					out.write("    if (");
					out.print(intAttrName);
					out.write(" != ");
					out.print(attrName);
					out.write(".getIntValue()) {\n");
					out.write("            ");
					out.print(intAttrName);
					out.write(" = ");
					out.print(attrName);
					out.write(".getIntValue();\n");
					out.write("        }\n");
					
				} else if (member.getType().getClass() == BEListType.class 
						&& ((BEListType)member.getType()).getElementType().getClass() == CEObjRefType.class) {
					out.write("        this.");
					out.print(attrName);
					out.write("IntValues = CEUtil.parseCEIntValues2String("+attrName+");\n");
				}
				out.write("    }\n");

				if (member.getType() instanceof CEObjRefType)  {
					String intAttrName = attrName + "Int";
					out.write("\n    /**\n     *  set int ");
					out.print(attrName);
					out.write("\n     */\n    private void set");
					out.print(beanName);
					out.write("Int(int intValue) {\n");
					out.write("        this.");
					out.print(intAttrName);
					out.write(" = intValue;\n");
					out.write("        if (");
					out.print(intAttrName);
					out.write(" != ");
					out.print(attrName);
					out.write(".getIntValue()) {\n");
		        	out.write("            ");
					out.print(attrName);
					out.write(" = (");
					out.print(attrTypeString);
					out.write(")ceService.getConstantEntity("+attrTypeString+".ENTITY_NAME).getByIntValue(");
					out.print(intAttrName);
					out.write(");\n");
		        	out.write("        }\n");
					out.write("    }\n");
				} else if (member.getType().getClass() == BEListType.class 
						&& ((BEListType)member.getType()).getElementType().getClass() == CEObjRefType.class) {
					String elementType = ((CEObjRefType)((BEListType)member.getType()).getElementType()).getTargetEntity().getEntityName();
					String intAttrName = attrName + "IntValues";
					out.write("\n    /**\n     *  set String ");
					out.print(attrName);
					out.write("\n     */\n    private void set");
					out.print(beanName);
					out.write("IntValues(String intValues) {\n");
					out.write("        this.");
					out.print(intAttrName);
					out.write(" = intValues;\n");
					out.write("        this.");
					out.print(attrName);
					out.write(" = CEUtil.parseCEIntValues("+elementType+".class, intValues);\n");
					out.write("    }\n");
				}
			}
		}
	}

	private void generateSetBody(BusinessEntityType businessEntity,
			String attrTypeString, String attrName, PumpWriter out) {
		out.write("\n        this.");
		out.print(attrName);
		out.write(" = ");
		out.print(attrName);
		out.write(";\n");
	}

	private void generateAttrDeclaration(BusinessEntityType beEntity, PumpWriter out) {
		if (isSelfNeedPersist(beEntity) || isSelfNeedHistory(beEntity)) {
			out.write("    /**\n");
			out.write("     * Enable record\n");
			out.write("     */\n");
			out.write("    private boolean _enable = true;\n\n");
		}

		if (isSelfNeedHistory(beEntity)) {
			out.write("\n    /**\n");
			out.write("     * History version\n");
			out.write("     */\n");
			out.write("    protected int _version = 0;\n\n");
			out.write("    /**\n");
			out.write("     * History starttime\n");
			out.write("     */\n");
			out.write("    private java.util.Date _starttime = new java.util.Date();\n\n");
			out.write("    /**\n");
			out.write("     * History endtime\n");
			out.write("     */\n");
			out.write("    private java.util.Date _endtime = null;\n\n");
			out.write("    /**\n");
			out.write("     * History _optuserid\n");
			out.write("     */\n");
			out.write("    private long _optuserid = 0L;\n\n");
		}
		
		if (isSelfExtensible(beEntity)) {
			out.write("\n    /**\n");
			out.write("     *  BEExtension _extType\n");
			out.write("     */\n");
			out.write("    protected String _extType;\n\n");
			out.write("    /**\n");
			out.write("     *  BEExtension _extField\n");
			out.write("     */\n");
			out.write("    protected BEExtensionInfo _extField;\n");
			out.write("        ");
		}

		List<MemberType> members = beEntity.getMembers();
		for (MemberType member: members) {
			String description = member.getDescription();
			if (description == null) {
				description = "help is not available";
			}
			String attrName = member.getName();
			BEType attrType = member.getType();
			String attrTypeString = getDataTypeClassName(beEntity.isNeedPersist(), attrType);
			String attrInitString = member.getDefaultValue();
			String isTransient = member.isTransient() ? "transient " : "";

			out.write("\n   /**\n     *  ");
			out.print(description);
			out.write("\n     */    ");

			if (attrInitString != null) {
				out.write("\n    protected ");
				out.print(isTransient);
				out.print(attrTypeString);
				out.write(" ");
				out.print(attrName);
				out.write(" = ");
				out.print(attrInitString);
				out.write(";\n    ");
			} else if (attrType instanceof CEObjRefType) {
				out.write("\n    protected ");
				out.print(isTransient);
				out.print(attrTypeString);
				out.write(" ");
				out.print(attrName);
				out.write(" = ");
				out.print(attrTypeString);
				out.write(".NOT_SPECIFIED;\n    ");
				
				out.write("\n    protected int ");
				out.print(attrName);
				out.write("Int = ");
				out.print(attrTypeString);
				out.write(".NOT_SPECIFIED.getIntValue();\n    ");
				
			} else {
				out.write("\n    protected ");
				out.print(isTransient);
				out.print(attrTypeString);
				out.write(" ");
				out.print(attrName);
				out.write(";\n    ");
				if (member.getType().getClass() == BEListType.class 
						&& ((BEListType)member.getType()).getElementType().getClass() == CEObjRefType.class) {
					out.write("\n    protected String ");
					out.print(attrName);
					out.write("IntValues = null;\n    ");
				}
			}
		}
	}


	private void generateEqualsMethod(BusinessEntityType businessEntity, PumpWriter out) {
		if (businessEntity.isNeedEquals()) {
			String implCls = getBEImplementClassName(businessEntity.getEntityName());

			out.write("\n    /**\n");
			out.write("     * Check different according to primary key.\n");
			out.write("     */\n");
			out.write("    public boolean equals(Object obj) {\n");
			out.write("        if (obj == this)\n");
			out.write("            return true;\n");
			out.write("        if (!(obj instanceof ");
			out.print(implCls);
			out.write("))\n            return false;\n        ");
			out.print(implCls);
			out.write(" o = (");
			out.print(implCls);
			out.write(")obj;\n        ");

			out.write("\n        boolean result = super.equals(obj);\n");
			out.write("\n        boolean eq = true;\n        ");

			out.write("\n        return result;\n");
			out.write("    }\n\n");
			out.write("    /**\n");
			out.write("     * Generate hashCode according to primary key.\n");
			out.write("     */\n");
			out.write("    public int hashCode() {\n        ");

			out.write("\n        int result = super.hashCode();\n");
			out.write("\n        return result;\n    }\n        ");

		}
	}

	private void generateToStringMethod(BusinessEntityType businessEntity, PumpWriter out) {

		out.write("\n     /**\n");
		out.write("     * Gets the String format of the business entity.\n");
		out.write("     *\n");
		out.write("     * @return String the business entity in String format.\n");
		out.write("     */\n");
		out.write("    public  String  toString() {\n");
		out.write("        StringBuffer aBuf = new StringBuffer();\n");
		out.write("        aBuf.append(\"");
		out.print(businessEntity.getEntityName());
		out.write("\");\n    ");

		ObjectRefType parentObj = businessEntity.getParentObject();
		if (parentObj != null) {

			out.write("\n        aBuf.append(\" extends \");\n");
			out.write("        aBuf.append(super.toString());\n        ");

		} else {

			out.write("\n        aBuf.append(\" : \");\n        ");

		}
		if (isSelfNeedPersist(businessEntity) || isSelfNeedHistory(businessEntity)) {
			out.write("\n        aBuf.append(\"enable=\").append(_enable).append(\", \");\n        ");
		}
		List<MemberType> members = businessEntity.getMembers();
		for (MemberType member: members) {
			String fieldValue = fieldToString(member);
			if (fieldValue != null) {

				out.write("\n        aBuf.append(\"");
				out.print(member.getName());
				out.write("\");\n        aBuf.append(\"=\");\n        aBuf.append(");
				out.print(fieldValue);
				out.write(");\n        aBuf.append(\", \");\n        ");

			}
		}

		out.write("\n        return aBuf.toString();\n    }\n    ");

	}

	private void generateGetMemberListMethod(BusinessEntityType businessEntity, PumpWriter out) {

		out.write("\n     /**\n");
		out.write("     * Gets list of MemberType.\n");
		out.write("     *\n");
		out.write("     * @return List     the list of MemberType.\n");
		out.write("     */\n");
		out.write("    public List<MemberType> getMemberList() {\n");
		out.write("        List<MemberType> memberTypeList = new ArrayList<MemberType>();\n        ");

		if (getParentBusinessEntityName(businessEntity) != null) {
			out.write("\n        List<MemberType> parentList = super.getMemberList();\n");
			out.write("        for(int i=0, n=parentList.size(); i<n; i++)\n");
			out.write("            memberTypeList.add(parentList.get(i));\n            ");
		}

		out.write("\n        MemberType member = null;\n        ");

		List<MemberType> members = businessEntity.getMembers();
		for (MemberType member: members) {
			String description = member.getDescription();
			BEType beType = member.getType();
			String beTypeVar = generateBEType(beType, member.getName(), out);

			out.write("\n        //MemberType Define for ");
			out.print(member.getName());
			out.write("\n        member = new MemberType();\n");
			out.write("        member.setName(\"");
			out.print(member.getName());
			out.write("\");\n        member.setDescription(\"");
			out.print(description);
			out.write("\");\n        member.setType(");
			out.print(beTypeVar);
			out.write(");\n        memberTypeList.add(member);\n            ");
		}
		out.write("\n        return memberTypeList;\n    }\n    ");

	}

	private String generateBEType(BEType beType, String name, PumpWriter out) {
		String beTypeVar = name + "BEType";

		out.write("\n        ");
		out.print(beType.getClass().getName());
		out.write(" ");
		out.print(beTypeVar);
		out.write(" = new ");
		out.print(beType.getClass().getName());
		out.write("();\n    ");

		if (beType instanceof BEComplexType) {
			if (beType instanceof BEObjRefType) {
				String targetEntityVar = name + "TargetEntity";
				TargetEntityType be = ((BEObjRefType) beType).getTargetEntity();
				if (be.getEntityName().indexOf('.') == -1) {
					be.setEntityName(bePackage + "." + be.getEntityName());
				}
				
				out.write("\n        TargetEntityType ");
				out.print(targetEntityVar);
				out.write(" = new TargetEntityType();\n        ");
				out.print(targetEntityVar);
				out.write(".setEntityName(\"");
				out.print(be.getEntityName());
				out.write("\");\n        ");
				out.print(beTypeVar);
				out.write(".setTargetEntity(");
				out.print(targetEntityVar);
				out.write(");\n            ");

			} else if (beType instanceof CEObjRefType) {
				String targetEntityVar = name + "TargetEntity";
				TargetEntityType ce = ((CEObjRefType) beType).getTargetEntity();
				if (ce.getEntityName().indexOf('.') == -1) {
					ce.setEntityName(cePackage + "." + ce.getEntityName());
				}
				
				out.write("\n        TargetEntityType ");
				out.print(targetEntityVar);
				out.write(" = new TargetEntityType();\n        ");
				out.print(beTypeVar);
				out.write(".setTargetEntity(");
				out.print(targetEntityVar);
				out.write(");\n        ");
				out.print(targetEntityVar);
				out.write(".setEntityName(\"");
				out.print(ce.getEntityName());
				out.write("\");\n            ");

			} else if (beType instanceof JavaObjRefType) {
				String targetJavaVar = name + "TargetJava";
				TargetJavaType targetJava = ((JavaObjRefType) beType)
						.getTargetJava();

				out.write("\n        TargetJavaType ");
				out.print(targetJavaVar);
				out.write(" = new TargetJavaType();\n        ");
				out.print(targetJavaVar);
				out.write(".setName(\"");
				out.print(targetJava.getName());
				out.write("\");\n        ");
				out.print(targetJavaVar);
				out.write(".setPackageName(\"");
				out.print(targetJava.getPackageName());
				out.write("\");\n        ");
				out.print(beTypeVar);
				out.write(".setTargetJava(");
				out.print(targetJavaVar);
				out.write(");\n            ");

			} else if (beType instanceof BESetType) {
				BEType elementBEType = ((BESetType) beType).getElementType();
				String elementBETypeVar = generateBEType(elementBEType, name
						+ "Element", out);

				out.write("\n        ");
				out.print(beTypeVar);
				out.write(".setElementType(");
				out.print(elementBETypeVar);
				out.write(");\n            ");

			} else if (beType instanceof BEListType) {
				BEType elementBEType = ((BEListType) beType).getElementType();
				String elementBETypeVar = generateBEType(elementBEType, name
						+ "Element", out);

				out.write("\n        ");
				out.print(beTypeVar);
				out.write(".setElementType(");
				out.print(elementBETypeVar);
				out.write(");\n            ");

			} else if (beType instanceof BEMapType) {
				BEType keyBEType = ((BEMapType) beType).getKeyType();
				String keyBETypeVar = generateBEType(keyBEType, name + "Key", out);
				BEType elementBEType = ((BEMapType) beType).getElementType();
				String elementBETypeVar = generateBEType(elementBEType, name
						+ "Element", out);

				out.write("\n        ");
				out.print(beTypeVar);
				out.write(".setKeyType(");
				out.print(keyBETypeVar);
				out.write(");\n        ");
				out.print(beTypeVar);
				out.write(".setElementType(");
				out.print(elementBETypeVar);
				out.write(");\n            ");
			}
		}
		return beTypeVar;
	}

	// ///////////////////////////Util
	// method////////////////////////////////////////////

	private String getBeanName(String attrName) {
		return Character.toUpperCase(attrName.charAt(0))
				+ attrName.substring(1);
	}

	static boolean isPersistentByBE(BusinessEntityType be, PersistenceTypeType rdbType) {
		for (PersistentConfigType pct : be.getPersistentConfigs()) {
			PersistenceTypeType pt = pct.getSupportedPersistenceType();
			if (pt == rdbType) {
				return true;
			}
		}
		return false;
	}
	
	static boolean isSelfNeedPersist(BusinessEntityType businessEntity) {
		return businessEntity.isNeedPersist();
	}
	
	static boolean isSelfNeedHistory(BusinessEntityType businessEntity) {
		return businessEntity.isNeedHistory();
	}

	static boolean isSelfExtensible(BusinessEntityType businessEntity) {
		return businessEntity.isExtensible();
	}

	private String getParentBusinessEntityName(BusinessEntityType businessEntity) {
		ObjectRefType parentObj = businessEntity.getParentObject();
		if (parentObj != null) {
			if (parentObj instanceof BEObjRefType) {
				return ((BEObjRefType) parentObj).getTargetEntity()
						.getEntityName();
			}
			/**
			String javaClass = getJavaClassName(((JavaObjRefType) parentObj)
					.getTargetJava());
			return javaClass;
			*/
		}
		return null;
	}

	static String getJavaClassName(TargetJavaType targetJava) {
		String className = targetJava.getName();
		String packageName = targetJava.getPackageName();
		return ClassUtil.getFullClassName(packageName, className);
	}

	private String getJavaFileName(File rootDir, String fullClassName) {
		return new File(rootDir, fullClassName.replace('.', File.separatorChar)
				+ ".java").getAbsolutePath();
	}

	static String getDataTypeClassName(boolean isPersistent, BEType dataType) {
		String typeClass = null;

		if (dataType instanceof BEPrimitiveType) {
			typeClass = (String) dataTypeToJavaTypeMap.get(dataType.getClass());
		} else if (dataType instanceof BEComplexType) {
			if (dataType instanceof BEObjRefType) {
				TargetEntityType be = ((BEObjRefType) dataType)
						.getTargetEntity();
				if (be != null) {
					//due to hibernate engine needs the concrete class mapping.
					//the interface won't work for that mapping.
					if (isPersistent) {
						typeClass = getBEImplementClassName(be.getEntityName());
					} else {
						typeClass = getBEInterfaceClassName(be.getEntityName());
					}
				}
			} else if (dataType instanceof CEObjRefType) {
				TargetEntityType ce = ((CEObjRefType) dataType)
						.getTargetEntity();
				if (ce != null)
					typeClass = getConstantEntityClassName(ce.getEntityName());
			} else if (dataType instanceof JavaObjRefType) {
				TargetJavaType targetJava = ((JavaObjRefType) dataType)
						.getTargetJava();
				if (targetJava != null)
					typeClass = getJavaClassName(targetJava);
			} else if (dataType instanceof BESetType) {
				typeClass = "java.util.Set" + 
						BEUtil.getGenericType(((BESetType)dataType).getElementType());
			} else if (dataType instanceof BEListType) {
				typeClass = "java.util.List" + 
						BEUtil.getGenericType(((BEListType)dataType).getElementType());
			} else if (dataType instanceof BEMapType) {
				BEMapType beMapType = (BEMapType)dataType;
				typeClass = "java.util.Map" + BEUtil.getGenericTypes(beMapType.getKeyType(), 
						 					 beMapType.getElementType());
			}
		}
		return typeClass;
	}

	static String getWrappedDataTypeClass(boolean isPersistent, BEType dataType) {
		String typeClass = null;
		if (dataType instanceof BooleanType) {
			typeClass = "Boolean";
		} else if (dataType instanceof IntType) {
			typeClass = "Integer";
		} else if (dataType instanceof LongType) {
			typeClass = "Long";
		} else if (dataType instanceof DoubleType) {
			typeClass = "Double";
		} else {
			typeClass = getDataTypeClassName(isPersistent, dataType);
		}
		return typeClass;
	}

	private String getWrapperDataTypeMethod(BEType dataType) {
		String valueMethod = "";
		if (dataType instanceof BooleanType) {
			valueMethod = ".booleanValue()";
		} else if (dataType instanceof IntType) {
			valueMethod = ".intValue()";
		} else if (dataType instanceof LongType) {
			valueMethod = ".longValue()";
		} else if (dataType instanceof DoubleType) {
			valueMethod = ".doubleValue()";
		}
		return valueMethod;
	}

	static String getWrapperDataTypeObject(BEType dataType, String value) {
		String wrapper = value;
		if (dataType instanceof BooleanType) {
			wrapper = value + " ? Boolean.TRUE : Boolean.FALSE";
			;
		} else if (dataType instanceof IntType) {
			wrapper = "new Integer(" + value + ")";
		} else if (dataType instanceof LongType) {
			wrapper = "new Long(" + value + ")";
		} else if (dataType instanceof DoubleType) {
			wrapper = "new Double(" + value + ")";
		}
		return wrapper;
	}

	private static String getBENameByImplementClassName(String implementClassName) {
		if (implementClassName.indexOf('.') == -1) {
			return bePackage + "." + implementClassName;
		}
		
		return implementClassName.substring(0, implementClassName.length()-4);
	}

	static String getBEInterfaceClassName(String beName) {
		if (beName.indexOf('.') == -1) {
			beName = bePackage + "." + beName;
		}
		String packageName = beName.substring(0, beName.lastIndexOf("."));
		String name = beName.substring(beName.lastIndexOf(".") + 1);
		return packageName + ".I" + name;
	}

	private static String getBEImplementClassName(String beName) {
		String clsName = beName + "Impl";
		return clsName;
	}

	static String getConstantEntityClassName(String ceName) {
		String clsName = ceName;
		return clsName;
	}

	private static String getVarWrapper(VariableType var) {
		return getWrapper(var.getName(), VariableUtil.getVariableClassName(var));
	}

	private static String getWrapper(String varName, String varType) {
		if ("int".equals(varType)) {
			return "new Integer(" + varName + ")";
		}
		if ("long".equals(varType)) {
			return "new Long(" + varName + ")";
		}
		if ("boolean".equals(varType)) {
			return varName + " ? Boolean.TRUE : Boolean.FALSE";
		}
		if ("double".equals(varType)) {
			return "new Double(" + varName + ")";
		}
		if ("char".equals(varType)) {
			return "new Character(" + varName + ")";
		}
		if ("float".equals(varType)) {
			return "new Float(" + varName + ")";
		}
		if ("short".equals(varType)) {
			return "new Short(" + varName + ")";
		}
		if ("byte".equals(varType)) {
			return "new Byte(" + varName + ")";
		}
		return varName;
	}

	private static String fieldToString(MemberType field) {
		String fieldName = field.getName();
		BEType fieldType = field.getType();
		if (fieldType instanceof BEPrimitiveType
				|| fieldType instanceof CEObjRefType) {
			return fieldName;
		}
		if (fieldType instanceof BEObjRefType) {
			return fieldName + "==null? \"\" : " + fieldName + ".toString()";
		}
		return null;
	}
	
	private void createI18NProperties(String projectRoot, StringBuffer keys) {
		File resourcePath = new File(projectRoot + "/src/main/resources");
		if (!resourcePath.exists()) {
			resourcePath.mkdirs();
		}
		
		File resource_en = new File(resourcePath, "i18n_en_US.properties");
		File resource_zh = new File(resourcePath, "i18n_zh_CN.properties");
		
		
		
	}
	

}
