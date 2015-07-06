package org.shaolin.bmdp.designtime.orm;

import java.io.File;

public class BuilderUtil {
	
	public static String getFullName(String packageName, String name) {
		if (packageName == null || packageName.length() == 0) {
			return name;
		}
		return packageName + "." + name;
	}

	public static File getJavaFile(File rootDir, String fullClassName) {
		return new File(rootDir, fullClassName.replace('.', File.separatorChar)
				+ ".java");
	}

	public static String getStringValue(String str) {
		if (str == null) {
			return str;
		}
		return "\"" + str + "\"";
	}

	public static String getSqlFileName(String sqlFileName) {
		if (sqlFileName.endsWith(".sql")) {
			return sqlFileName.substring(0, sqlFileName.length() - 4);
		}
		return sqlFileName;
	}

	private BuilderUtil() {
	}
}
