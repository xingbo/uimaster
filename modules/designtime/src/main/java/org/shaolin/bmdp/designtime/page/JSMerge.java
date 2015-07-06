package org.shaolin.bmdp.designtime.page;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSMerge {

	// for log4j
	private static Logger logger = LoggerFactory.getLogger(JSMerge.class);

	// private static String originalJSRootDir = null;

	static HashMap JSTextTable = null;

	public static final String JSMERGE_FUNC_HEAD = "Gen_First:";

	public static final String JSMERGE_FUNC_TAIL = "Gen_Last:";

	public static final String OTHER_HEAD = "OtherFunc_First:";

	public static final String OTHER_TAIL = "OtherFunc_Last:";

	public static final String Construct_HEAD = "Construct_FIRST";

	public static final String Construct_TAIL = "Construct_LAST";

	private static List jSTextList = null;

	/**
	 * Get file content of a file.
	 * 
	 * @param rootPath
	 *            js file root path
	 * @param jsName
	 *            javascript file name
	 * @return the text file lines stored in a list
	 */
	public static List getSingleText(String rootPath, String jsName) {
		List textList = getAllText(rootPath + "/" + jsName.replace('.', '/')
				+ ".js");
		if (textList != null) {
			List tempList = new ArrayList();
			for (int i = 0; i < textList.size(); i++) {
				String str = textList.get(i).toString();
				if (str.indexOf(JSMERGE_FUNC_HEAD) > -1) {
					tempList.add(getFuncName(str));
				}
			}
			if (jSTextList == null) {
				jSTextList = new ArrayList();
				jSTextList.addAll(tempList);
			}
		}
		return textList;
	}

	/**
	 * Get the file content of a file and put the jsName to file content mapping
	 * into the JSTextTable.
	 * 
	 * @param rootPath
	 *            the js file root path
	 * @param jsName
	 *            the js file name
	 */
	public static void addText(String rootPath, String jsName) {
		if (JSTextTable == null) {
			JSTextTable = new HashMap();
		}

		if (!JSTextTable.containsKey(jsName)) {
			List list = getAllText(rootPath + "/" + jsName.replace('.', '/')
					+ ".js");
			if (list != null) {
				JSTextTable.put(jsName, list);
			}
		}
	}

	/**
	 * Get Additional Function.
	 * 
	 * @param allText
	 *            list of the whole text
	 * @return list of the additional function lines
	 */
	public static List getOtherFunc(List allText) {
		return getPluginPP(OTHER_HEAD, OTHER_TAIL, allText);
	}

	/**
	 * Get constructor plug in text lines
	 * 
	 * @param allText
	 *            whole text lines stored in a list
	 * @return constructor plug in text lines
	 */
	public static List getConstructPlugin(List allText) {
		return getPluginPP(Construct_HEAD, Construct_TAIL, allText);
	}

	/**
	 * Get body content of some function.
	 * 
	 * @param funcName
	 *            Function to be read
	 * @param allText
	 *            whole text of some file
	 * @return String of the function content
	 */
	public static String getBody(String funcName, List allText) {
		String s1 = getFunction(funcName, allText, JSMERGE_FUNC_HEAD,
				JSMERGE_FUNC_TAIL);
		return s1;
	}

	/**
	 * Get body content of some function.
	 * 
	 * @param funcName
	 *            Function to be read
	 * @param allText
	 *            whole text of some file
	 * @return String of the function content
	 */
	public static String getPartBody(String funcName, List allText) {
		String s1 = getFunction(funcName, allText, JSMERGE_FUNC_HEAD,
				JSMERGE_FUNC_TAIL);
		return s1;
	}

	/**
	 * Get the function name in a line. The function name is after the tag.
	 * 
	 * @param line
	 *            the line text content
	 * @param tagName
	 *            the tag name
	 * @return the function name
	 */
	private static String getFuncName(String line, String tagName) {
		int index = line.indexOf(tagName);
		if (index < 0) {
			return "";
		}
		String tail = line.substring(line.indexOf(tagName) + tagName.length())
				.trim();
		int index2 = tail.indexOf("*/");
		if (index2 < 0) {
			return tail;
		} else {
			return tail.substring(0, index2).trim();
		}
	}

	/**
	 * Get plug in part text surrounded by headTag and endTag
	 * 
	 * @param headTag
	 *            the head tag to identify the start point
	 * @param endTag
	 *            the end tag to identify the end point
	 * @param allText
	 *            the whole line text stored in a list
	 * @return the plug in part text lines stored in a list.
	 */
	private static List getPluginPP(String headTag, String endTag, List allText) {
		List result = new ArrayList();
		int begin = 0;
		while (begin < allText.size()) {
			int i = begin;
			int start = 0;
			int end = 0;
			for (; i < allText.size(); i++) {
				String str = allText.get(i).toString();
				if (str.indexOf(headTag) > -1) {
					start = i;
					break;
				}
			}
			if (headTag.equals(endTag)) {
				i++;
			}
			for (; i < allText.size(); i++) {
				String str = allText.get(i).toString();
				if (str.indexOf(endTag) > -1) {
					end = i;
					break;
				}
			}
			begin = i;
			StringBuffer body = new StringBuffer();
			if (start < end) {
				for (i = start + 1; i < end; i++) {
					body.append(allText.get(i));
				}
				result.add(new String(body));
			}
		}

		return result;
	}

	/**
	 * Get the function text content.
	 * 
	 * @param funcName
	 *            the requested function name
	 * @param allText
	 *            the whole text file content which are stored in a list
	 * @param beforeTag
	 *            a tag to identify that the function start at this point
	 * @param endTag
	 *            a tag to identify that the function end at this point
	 * @return the function text content
	 */
	private static String getFunction(String funcName, List allText,
			String beforeTag, String endTag) {
		int start = 0;
		int end = 0;
		int i;
		String str;

		for (i = 0; i < allText.size(); i++) {
			str = allText.get(i).toString();
			if (str.indexOf(beforeTag) > -1
					&& getFuncName(str, beforeTag).equals(
							funcName.replace('.', '_'))) {
				start = i;
				break;
			}
		}
		if (beforeTag.equals(endTag)) {
			i = start + 1;
		} else {
			i = start;
		}
		for (; i < allText.size(); i++) {
			str = allText.get(i).toString();
			if (str.indexOf(endTag) > -1
					&& getFuncName(str, endTag).equals(
							funcName.replace('.', '_'))) {
				end = i;
				break;
			}
		}
		StringBuffer body = new StringBuffer();
		if (start < end) {
			String fName = funcName.replace('.', '_');
			if (jSTextList != null && jSTextList.contains(fName)) {
				jSTextList.remove(fName);
			}
			for (i = start + 1; i <= end; i++) {
				body.append(allText.get(i));
			}
			int tempIndex = body.lastIndexOf(endTag);
			int tempIndex2 = body.lastIndexOf("}", tempIndex);
			return body.substring(0, tempIndex2);
		}
		return new String(body);
	}

	/**
	 * Load all the js files in the HTMLGenConfig and get their body.
	 * 
	 * @param genConfig
	 *            HTMLGenConfig info to be read
	 * @return
	 */
	public static void init(String rootPath, Map mappingMap,
			Iterator componentNameIterator) {
		if (rootPath != null && rootPath.endsWith("/")) {
			rootPath = rootPath.substring(0, rootPath.length() - 1);
		}

		for (; componentNameIterator.hasNext();) {
			String entityName = (String) componentNameIterator.next();
			String jsName = (String) mappingMap.get(entityName);
			if (jsName == null) {
				jsName = UIPageUtil.getDefaultJspName(entityName);
			}
			addText(rootPath, jsName);
		}
	}

	/**
	 * Get Function Name from a line.
	 * 
	 * @param line
	 *            a line of String included the function Name
	 * @return Function Name
	 */
	private static String getFuncName(String line) {
		int index = line.indexOf(JSMERGE_FUNC_HEAD);
		if (index < 0)// if not found JSMERGE_FUNC_HEAD
		{
			return "";
		}
		String tail = line.substring(index + JSMERGE_FUNC_HEAD.length()).trim();
		int index2 = tail.indexOf("*/");
		if (index2 < 0) {
			return tail;
		} else {
			return tail.substring(0, index2).trim();
		}
	}

	/**
	 * Get all the text lines of a file.
	 * 
	 * @param fileName
	 *            file to be read
	 * @return list of file text lines
	 */
	protected static List getAllText(String fileName) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			List allText = new ArrayList();

			if (in != null) {
				String str = "";
				while ((str = in.readLine()) != null) {
					allText.add(str + "\r\n");
				}
			}
			in.close();
			return allText;
		} catch (Exception e) {
			// logger.error(fileName + "not found! ", e);
		}
		return null;
	}

	public static List getNotUsedJsFunc() {
		return jSTextList;
	}

	public static void clearFuncList() {
		if (jSTextList != null) {
			jSTextList.clear();
		}
		jSTextList = null;
	}

}
