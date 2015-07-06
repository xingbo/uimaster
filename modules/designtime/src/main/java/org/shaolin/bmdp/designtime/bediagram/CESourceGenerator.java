package org.shaolin.bmdp.designtime.bediagram;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.shaolin.bmdp.datamodel.bediagram.BEDiagram;
import org.shaolin.bmdp.datamodel.bediagram.ConstantEntityType;
import org.shaolin.bmdp.datamodel.bediagram.ConstantValueType;
import org.shaolin.bmdp.datamodel.common.DiagramType;
import org.shaolin.bmdp.designtime.tools.GeneratorOptions;
import org.shaolin.bmdp.designtime.tools.PumpWriter;
import org.shaolin.bmdp.runtime.Registry;
import org.shaolin.bmdp.runtime.entity.EntityAddedEvent;
import org.shaolin.bmdp.runtime.entity.EntityManager;
import org.shaolin.bmdp.runtime.entity.EntityUpdatedEvent;
import org.shaolin.bmdp.runtime.entity.IEntityEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class CESourceGenerator implements IEntityEventListener<ConstantEntityType, BEDiagram>
{
    private static Logger logger = LoggerFactory.getLogger(CESourceGenerator.class);
    
    private final PumpWriter out = new PumpWriter();
    
    private GeneratorOptions option = null;
    
    private EntityManager entityManager = null;
    
    private final List<String> generatedCEs;
    
	public CESourceGenerator(GeneratorOptions option) {
		this.option = option;
		this.generatedCEs = new ArrayList<String>();
	}
	
	public List<String> getGeneratedCEs() {
		return generatedCEs;
	}
	
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;		
	}

	public void notify(EntityAddedEvent<ConstantEntityType, BEDiagram> entity) {
		if (entity.getDiagram().getBePackage().indexOf(option.getBundleName()) == -1) {
			return;
		}
		
		out.write("\n\n");

		out.setOutputDir(option.getSrcDir());
		out.setEncoding(Registry.getInstance().getEncoding());

		generateConstantEntity(entity.getEntity(), option.getSrcDir());

		out.write("\n");
		out.finish();
	}

	public void notify(EntityUpdatedEvent<ConstantEntityType, BEDiagram> event) {
		
	}

	public Class<ConstantEntityType> getEventType() {
		return ConstantEntityType.class;
	}
    
	public void notifyLoadFinish(DiagramType diagram) {
	}
	
	public void notifyAllLoadFinish() {
	}
	
    private void generateHeader(String packageName, String constantEntityName, File rootDir)
    {
        String fullName = getFullName(packageName, constantEntityName);
        if (logger.isInfoEnabled()) {
			logger.info("ConstantEntity is {}, whose generated file is {}", 
					fullName, rootDir.getAbsoluteFile());
		}
        generatedCEs.add(getJavaFileName(rootDir, fullName));
        
        if (packageName == null) {
			packageName = "";
		}
    
        out.write("\n\n!!!!javafile ");
        out.print(fullName);
        out.write("\n/*\n * This code is generated automatically, any change will be replaced after rebuild.\n * Generated on ");
        out.print(new Date());
        out.write("\n */\n\n");
    
		if (!packageName.equals("")) {
			out.write("package ");
			out.print(packageName);
			out.write(";");
		}
    }
    
    private void generateConstantEntity(ConstantEntityType constantEntity, File rootDir)
    {
		String constantEntityName = getPureName(constantEntity.getEntityName());
		String packageName = getPackageName(constantEntity.getEntityName());
		if (packageName == null) {
			packageName = "";
		}
		
		option.getI18nProperty().put(
         		packageName + "." + constantEntityName,
         		constantEntityName);
		
		generateHeader(packageName, constantEntityName, rootDir);
		List<ConstantValueType> constantValues = constantEntity.getConstantValues();
		boolean hasSetPriority = false;
    
        out.write("\nimport java.util.*;\n");
        out.write("import org.shaolin.bmdp.runtime.ce.IConstantEntity;\n");
        out.write("import org.shaolin.bmdp.runtime.ce.AbstractConstant;\n\n/**\n * ");
		out.print(constantEntity.getDescription() != null ? constantEntity
				.getDescription().trim() : "");
        out.write("\n * entityName: ");
        out.print(constantEntity.getEntityName());
        out.write("\n *\n */\npublic final class ");
        out.print(constantEntityName);
        out.write(" extends AbstractConstant\n{\n");
        out.write("    public static final String ENTITY_NAME = \"");
        out.print(constantEntity.getEntityName());
        out.write("\";\n    \n    ");
        out.write("protected static final long serialVersionUID = 0x811b9115811b9115L;\n    ");
        out.write("private static String i18nBundle = \"");
        out.print(option.geti18nBundleName());
        out.write("\";\n\n    ");
        out.write("//User-defined constant define\n");
        
        out.write("\n    public static final ");
        out.write(constantEntityName);
        out.write(" NOT_SPECIFIED = new ");
        out.write(constantEntityName);
        out.write("(CONSTANT_DEFAULT_VALUE, -1, null, null, null, null, false);\n");
        
        for(ConstantValueType constantValue: constantValues)
        {
        	String upperValue = getStringValue(constantValue.getValue()).replace('.', '_');
        	String value = getStringValue(constantValue.getValue());
            int intValue = constantValue.getIntValue();
            String i18nKey = packageName + "." + constantEntityName + '.' + value;
            option.getI18nProperty().put(i18nKey, constantValue.getDescription());
            i18nKey = getStringStr(i18nKey);
            String description = getStringStr(constantValue.getDescription());
            String effTime = getTimeStr(constantValue.getEffTime());
            String expTime = getTimeStr(constantValue.getExpTime());
            boolean isPassivated = constantValue.isPassivated() != null
            						? constantValue.isPassivated() : false;
            boolean hasPriority = constantValue.getPriority() != null
            						? constantValue.getPriority() > 0: false;
    
	        out.write("\n    public static final ");
	        out.print(constantEntityName);
	        out.write(" ");
	        out.print(upperValue);
	        out.write(" = ");
            if (!hasPriority)
            {
		        out.write("new ");
		        out.print(constantEntityName);
		        out.write("(\"");
		        out.print(constantValue.getValue().trim());
		        out.write("\", ");
		        out.print(intValue);
		        out.write(", ");
		        out.print(i18nKey);
		        out.write(", ");
		        out.print(description);
		        out.write(", ");
		        out.print(effTime);
		        out.write(", ");
		        out.print(expTime);
		        out.write(",");
		        out.print(isPassivated);
		        out.write(");\n");
            }
            else
            {
				if (!hasSetPriority) {
					hasSetPriority = true;
				}
				int priority = constantValue.getPriority();    
    
		        out.write("new ");
		        out.print(constantEntityName);
		        out.write("(");
		        out.print(value);
		        out.write(", ");
		        out.print(intValue);
		        out.write(", ");
		        out.print(i18nKey);
		        out.write(", ");
		        out.print(description);
		        out.write(", ");
		        out.print(effTime);
		        out.write(", ");
		        out.print(expTime);
		        out.write(",");
		        out.print(isPassivated);
		        out.write(", ");
		        out.print(priority);
		        out.write(");\n");
            }
        }
    
        out.write("\n    //End of constant define\n");
        out.write("\n    //Common constant define\n    ");
        out.print("public ");
        out.print(constantEntityName);
        out.write("()\n    ");
        out.print("{\n");
        out.write("        constantList.add(NOT_SPECIFIED);\n");
        for(ConstantValueType constantValue: constantValues)
        {
        	String upperValue = getStringValue(constantValue.getValue()).replace('.', '_');
	        out.write("        constantList.add(");
	        out.print(upperValue);
	        out.write(");\n");
        }
        out.print("    }\n\n    ");
        out.print("private "); // for dynamic reflecting.
        out.print(constantEntityName);
        out.write("(long id, String value, int intValue, String i18nKey, String description)\n    {\n        ");
        out.print("super(id, value, intValue, i18nKey, description);\n    ");
        out.print("}\n    \n    ");
        out.print("private ");
        out.print(constantEntityName);
        out.write("(String value, int intValue, String i18nKey,\n        ");
        out.print("String description, Date effTime, Date expTime)\n    ");
        out.print("{\n        ");
        out.print("super(value, intValue, i18nKey, description, effTime, expTime);\n    ");
        out.print("}\n\n    private ");
        out.print(constantEntityName);
        out.write("(String value, int intValue, String i18nKey,\n            ");
        out.print("String description, Date effTime, Date expTime, boolean isPassivated)\n    ");
        out.print("{\n        ");
        out.print("super(value, intValue, i18nKey, description, effTime, expTime, isPassivated);\n    ");
        out.print("}\n    \n");
    
        if (hasSetPriority)
        {
	        out.write("\n    private ");
	        out.print(constantEntityName);
	        out.write("(String value, int intValue, String i18nKey,\n        ");
	        out.print("String description, Date effTime, Date expTime, \n        ");
	        out.print("boolean isPassivated, int priority)\n    {\n        ");
	        out.print("super(value, intValue, i18nKey, description, effTime, expTime, \n            ");
	        out.print("isPassivated, priority);\n    }\n");
        }
    
        /**
        out.write("\n");
        out.print("    public static ");
        out.print(constantEntityName);
        out.write(" get(String _value)\n    {");
        
        for(ConstantValueType constantValue: constantValues)
        {
        	String upperValue = getStringValue(constantValue.getValue()).replace('.', '_');
	        out.write("\n    	if(");
	        out.print(upperValue);
	        out.write(".getValue().equals(_value)) {\n");
	        out.write("    		return ");
	        out.print(upperValue);
	        out.write(";\n    	}\n");
        }
        out.write("    	return NOT_SPECIFIED;\n");
        out.print("    }\n\n");
        
        out.print("    public static ");
        out.print(constantEntityName);
        out.write(" getByIntValue(int _intValue)\n    {");
        
        for(ConstantValueType constantValue: constantValues)
        {
        	String upperValue = getStringValue(constantValue.getValue()).replace('.', '_');
	        out.write("\n    	if(");
	        out.print(upperValue);
	        out.write(".getIntValue() == _intValue) {\n");
	        out.write("    		return ");
	        out.print(upperValue);
	        out.write(";\n    	}\n");
        }
        out.write("    	return NOT_SPECIFIED;\n");
        out.print("    }\n\n");
        */
        
        out.print("    public String getI18nBundle()\n    {\n");
        out.print("        return i18nBundle;\n");
        out.print("    }\n\n");
        out.print("    public void setI18nBundle(String bundle)\n    {\n");
        out.print("        i18nBundle = bundle;\n");
        out.print("    }\n\n");
        out.print("    protected AbstractConstant __create(String value, int intValue, String i18nKey,\n");
        out.print("        String description, Date effTime, Date expTime)\n    {\n");
        out.print("        return new ");
        out.print(constantEntityName);
        out.write("(value, intValue, i18nKey,\n            description, effTime, expTime);\n");
        out.print("    }\n\n");
        out.print("    protected String getTypeEntityName()\n    {\n");
        out.print("        return ENTITY_NAME;\n");
        out.print("    }\n\n}\n");
    
    }
    
    private String getTimeStr(XMLGregorianCalendar time)
    {
		if (time == null) {
			return "null";
		} else {
			return "new Date(" + time.toGregorianCalendar().getTime().getTime() + "L)";
		}
    }
    
	private String getStringStr(String str) {
		if (str == null)
			return "null";
		StringBuffer sb = new StringBuffer("\"");
		char[] chars = str.trim().toCharArray();
		for (int j = 0, m = chars.length; j < m; j++) {
			switch (chars[j]) {
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			default:
				sb.append(chars[j]);
			}
		}
		sb.append("\"");
		return sb.toString();
	}
	
	private String getStringValue(String value) {
		return value.trim().toUpperCase().replace(' ', '_');
	}
    
	private String getJavaFileName(File rootDir, String fullClassName) {
		return new File(rootDir, fullClassName.replace('.', File.separatorChar)
				+ ".java").getAbsolutePath();
	}
	
	private String getFullName(String packageName, String name) {
		String fullName;

		if (packageName == null) {
			packageName = "";
		}

		if (packageName.equals("")) {
			fullName = name;
		} else {
			fullName = packageName + "." + name;
		}

		return fullName;
	}
    
	private String getPackageName(String fullName) {
		int i = fullName.lastIndexOf(".");
		String tempStr = null;

		if (i > 0) {
			tempStr = fullName.substring(0, i);
		}

		if (tempStr == null)
			tempStr = "";

		return tempStr;
	}

    //get name exclude packagename
	private String getPureName(String fullName) {
		int i = fullName.lastIndexOf(".");
		String tempStr = null;

		if (i == -1) {
			tempStr = fullName;
		} else {
			tempStr = fullName.substring(i + 1);
		}

		if (tempStr == null)
			tempStr = "";

		return tempStr;
	}

}
