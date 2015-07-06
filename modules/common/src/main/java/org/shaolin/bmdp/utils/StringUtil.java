package org.shaolin.bmdp.utils;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;

public class StringUtil
{
    /**
     * Returns a new string resulting from replacing all
     * occurrences of oldStr in a string with newStr. 
     * <p>
     * If the string oldStr does not occur in the specified
     * string then a reference to that String object is returned.
     * Otherwise, a new String object is created that
     * represents a character sequence identical to the
     * character sequence represented by the specified String
     * object, except that every occurrence of oldStr is
     * replaced by an occurrence of newStr. 
     *
     * @param       str         string to be replaced
     * @param       oldStr      old string
     * @param       newStr      new string
     * @return      a string derived from the specified string
     *              by replacing every occurrence of oldStr with newStr
     * @exception   NullPointerException if str or oldStr is <code>null</code>
     */
    public static String replaceString(String str, String oldStr, String newStr)
    {
        if (str == null)
        {
            throw new NullPointerException("String to be replaced is null");
        }
        int index = str.indexOf(oldStr);
        if (index == -1)
        {
            return str;
        }
        
        int oldStrLen = oldStr.length();
        StringBuffer sb = new StringBuffer();
        do
        {
            sb.append(str.substring(0, index));
            sb.append(newStr);
            str = str.substring(index + oldStrLen);
        }
        while ((index = str.indexOf(oldStr)) != -1);

        sb.append(str);
        return new String(sb);
    }

    public static String getSizeString(long size)
    {
        if (size < MSIZE)
        {
            DecimalFormat format = new DecimalFormat("0.0KB");
            return format.format((double)size / KSIZE);
        }
        if (size < GSIZE)
        {
            DecimalFormat format = new DecimalFormat("0.0MB");
            return format.format((double)size / MSIZE);
        }
        if (size < TSIZE)
        {
            DecimalFormat format = new DecimalFormat("0.0GB");
            return format.format((double)size / GSIZE);
        }
        DecimalFormat format = new DecimalFormat("0.0TB");
        return format.format((double)size / TSIZE);
    }

    public static long parseSizeString(String str)
    {
        int n = str.length() - 1;
        char c = str.charAt(n);
        switch(c)
        {
            case 'k':
            case 'K':
                return Integer.parseInt(str.substring(0, n)) * KSIZE;
            case 'm':
            case 'M':
                return Integer.parseInt(str.substring(0, n)) * MSIZE;
            case 'g':
            case 'G':
                return Integer.parseInt(str.substring(0, n)) * GSIZE;
            case 't':
            case 'T':
                return Integer.parseInt(str.substring(0, n)) * TSIZE;
            default:
                return Integer.parseInt(str);
        }
    }
    
    public static String getTrace()
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        new Throwable("Trace debug...").printStackTrace(pw);
        pw.close();
        return sw.toString();
    }

    public static String escape(String line)
    {
        if (line == null)
        {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0, n = line.length(); i < n; i++)
        {
            char c = line.charAt(i);
            switch (c)
            {
                case '\b':
                    sb.append("\\b");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                default:
                    sb.append(c);
            }
        }
        return new String(sb);
    }
    
    public static String unescape(String line)
    {
        if (line == null)
        {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0, n = line.length(); i < n; i++)
        {
            char c = line.charAt(i);
            if (c == '\\')
            {
                if (i == n - 1)
                {
                    throw new IllegalArgumentException("Invalid string to unescape:" + line);
                }
                char d = line.charAt(i + 1);
                switch (d)
                {
                    case 'b':
                        sb.append("\b");
                        break;
                    case 't':
                        sb.append("\t");
                        break;
                    case 'n':
                        sb.append("\n");
                        break;
                    case 'f':
                        sb.append("\f");
                        break;
                    case 'r':
                        sb.append("\r");
                        break;
                    case '"':
                        sb.append("\"");
                        break;
                    case '\\':
                        sb.append("\\");
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid string to unescape:" + line);
                }
                i++;
            }
            else
            {
                sb.append(c);
            }
        }
        return new String(sb);
    }
    
    public static String getBeanName(String name)
    {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
    
    public static String detectXMLEncoding(InputStream is) throws IOException
    {
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));
            String line = reader.readLine();
            return detectXMLEncodingInLine(line);
        }
        finally
        {
            CloseUtil.close(is);
        }
    }
    
    public static String replaceXMLEncoding(String xml, String encoding)
    {
        int index = 0;
        for (int n = xml.length(); index < n; index++)
        {
            char c = xml.charAt(index);
            if (c == '\r' || c == '\n')
            {
                break;
            }
        }
        return "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>" +
                xml.substring(index);
    }
    
    private static String detectXMLEncodingInLine(String line)
    {
        if (line == null)
        {
            return null;
        }
        int index1 = line.indexOf("encoding=\"");
        if (index1 == -1)
        {
            return null;
        }
        int index2 = line.indexOf("\"", index1 + 10);
        if (index2 == -1)
        {
            return null;
        }
        return line.substring(index1 + 10, index2);
    }

    /**
     * Convert the initial of given string <code>word</code> to upper case.
     * @param word  the string to be transfered
     * @return if given string is a valid string , then return string with
     * upper case initial character.
     */
    public static String capitalizeWord(String word)
    {
        String result = null;

        if ((word != null) && (word.trim().length() > 0))
        {
            result = word.substring(0, 1).toUpperCase() + word.substring(1);
        }
        else
        {
            result = word;
        }

        return result;
    }

    /**
     * Convert new lines, "\n" or "\r\n" of normal text to new line tag 
     * &lt;BR /&gt; in HTML format.
     * @param input  string to convert
     * @return  if given string is valid, then return translated string, 
     * otherwise return itself.
     */
    public static String convert2HtmlNewlines(String input)
    {
        if (isValid(input))
        {
            input = replace(input, "\r\n", "\n");
            input = replace(input, "\n", "<BR />");
        }

        return input;
    }


    /**
     * Break the given string <code>input</code> into words whose length less
     * than given <code>maxLength</code>.
     * @param input - string to be operated
     * @param maxLength - maximum length of a word
     * @return if given string <code>input</code> is not a valid string, then
     * return <code>input</code> itself, otherwise return string with words
     * delimited by space and every word's length less than <code>maxLength</code>.
     */
    public static String createBreaks(String input, int maxLength)
    {
        if (!isValid(input))
        {
            return input;
        }

        char chars[] = input.toCharArray();
        int len = chars.length;
        StringBuffer buf = new StringBuffer(len + input.length() / maxLength);
        int count = 0;
        int cur = 0;

        for (int i = 0; i < len; i++)
        {
            if (Character.isWhitespace(chars[i]))
            {
                count = 0;
                continue;
            }
            
            if (count >= maxLength)
            {
                buf.append(chars, cur, i - cur).append(" ");
                count = 1;
                cur = i;
                continue;
            }
            
            count++;
        }

        buf.append(chars, cur, len - cur);
        return buf.toString();
    }

    /**
     * Escape HTML tags which can display in browser.
     * @param input string to replace
     * @return string
     */
    public static String escapeHtmlTags(String input)
    {
        if ((input == null) || (input.length() == 0))
        {
            return input;
        }

        StringBuffer buf = new StringBuffer();
        char ch = ' ';

        for (int i = 0; i < input.length(); i++)
        {
            ch = input.charAt(i);

            if (ch == '<')
            {
                buf.append("&lt;");
            }
            else if (ch == '>')
            {
                buf.append("&gt;");
            }
            else if (ch == '&')
            {
                buf.append("&amp;");
            }
            else if (ch == '"')
            {
                buf.append("&quot;");
            }
            else if (ch == ' ')
            {
                buf.append("&nbsp;");
            }
            else
            {
                buf.append(ch);
            }
        }

        return buf.toString();
    }

    /**
     * Escape SQL tags, ' to ''; \ to \\.
     *
     * @param input
     *            string to replace
     * @return string
     */
    public static String escapeSQLTags(String input)
    {
        if ((input == null) || (input.length() == 0))
        {
            return input;
        }

        StringBuffer buf = new StringBuffer();
        char ch = ' ';

        for (int i = 0; i < input.length(); i++)
        {
            ch = input.charAt(i);

            if (ch == '\\')
            {
                buf.append("\\\\");
            }
            else if (ch == '\'')
            {
                buf.append("\'\'");
            }
            else
            {
                buf.append(ch);
            }
        }

        return buf.toString();
    }

    /**
     * Escape XML tags.
     *
     * @param input
     *            string to replace
     * @return string
     */
    public static String escapeXMLTags(String input)
    {
        if ((input == null) || (input.length() == 0))
        {
            return input;
        }

        StringBuffer buf = new StringBuffer();
        char ch = ' ';

        for (int i = 0; i < input.length(); i++)
        {
            ch = input.charAt(i);

            if (ch == '<')
            {
                buf.append("&lt;");
            }
            else if (ch == '>')
            {
                buf.append("&gt;");
            }
            else if (ch == '&')
            {
                buf.append("&amp;");
            }
            else if (ch == '"')
            {
                buf.append("&quot;");
            }
            else
            {
                buf.append(ch);
            }
        }

        return buf.toString();
    }

    /**
     * Filter all characters that are not allowed in file name from the given
     * <p>Examples:
     * <blockquote><pre>
     * StringUtil.filterFileName("test/.txt") return "test.txt"
     * StringUtil.filterFileName("test?.txt") return "test.txt"
     * StringUtil.filterFileName("")
     * </pre></blockquote>
     * file name <code>fileName</code>.
     * @param fileName file name to filter
     * @return return filtered file name that accord with windows file system
     */
    public static String filterFileName(String fileName)
    {
        String filtedName = fileName;
        String punctation = "*?/\\:<>|\"";
        List<String> list = split(fileName, punctation);
        Iterator<String> iter = null;

        if ((list != null) &&!list.isEmpty())
        {
            iter = list.iterator();

            StringBuffer result = new StringBuffer(fileName.length());

            while (iter.hasNext())
            {
                result.append(iter.next());
            }

            filtedName = result.toString();
        }

        return filtedName;
    }

    /**
     * Get string after the last given delimiter <code>delim</code> in the given
     * string <code>str</code>. If there is no given delimiter in the string, then
     * null will be returned. If given delimiter is null or empty, then null returned.
     * <p>Examples:
     * <blockquote><pre>
     * StringUtil.getLastPart("bmiasia.ebos.util.string.StringUtil", ".") return "StringUtil"
     * StringUtil.getLastPart("StringUtil", ".") return null
     * StringUtil.getLastPart("d:/test.txt", ".") return "txt"
     * StringUtil.getLastPart("d:/test.txt", "/") return "test.txt"
     * </pre></blockquote>
     * @param str string to process
     * @param delim delimiter in the string
     * @return last token delimit by given delimiter <code>delim</code> in the
     * input string.
     */
    public static String getLastPart(String str, String delim)
    {
        String part = "";
        if(null==delim || delim.equals(""))
        {
            return null;
        }
        
        int index = str.lastIndexOf(delim);

        if (index == -1)
        {
            part = str;
        }
        else
        {
            part = str.substring(index + 1);
        }

        return part;
    }

    /**
     * Truncate the given string <code>oldStr</code> to given length
     * <code>outLen</code> and append ellipsis to the output string if the
     * length of given string is larger than given length.
     * <p>Examples:
     * <blockquote><pre>
     * StringUtil.getAbbreviatoryString("This is Michael Jackson", 10) return "This is Mi..."
     * StringUtil.getAbbreviatoryString("This is Michael Jackson", 15) return "This is Michael..."
     * StringUtil.getAbbreviatoryString("This is Michael Jackson", 20) return "This is Michael Jack"
     * StringUtil.getAbbreviatoryString("This is Michael Jachson", 25) return "This is Michael Jackson"
     * </pre></blockquote>
     * @param oldStr string to process
     * @param outLen the length the ouput string should less than excluding ellipsis sign
     * @return abbreviated string whose length less than given length
     * <code>outLen</code>, if the length of original string larger than
     * given string, then ellipsis sign will be append to output string.
     * If input string is null, then null return.
     */
    public static String getAbbreviatoryString(String oldStr, int outLen)
    {
        return truncateString(oldStr, outLen, "...");
    }

    /**
     * Get string before the last given delimiter <code>delim</code> in the given
     * string <code>str</code>. If there is no given delimiter in the string, then
     * null will be returned. If given delimiter is null or empty, then null returned.
     * <p>Examples:
     * <blockquote><pre>
     * StringUtil.getPartBeforeLast("bmiasia.ebos.util.string.StringUtil", ".") return "bmiasia.ebos.util.string"
     * StringUtil.getPartBeforeLast("StringUtil", ".") return null
     * StringUtil.getPartBeforeLast("d:/test.txt", ".") return "d:/test"
     * StringUtil.getPartBeforeLast("d:/test.txt", "/") return "d:"
     * </pre></blockquote>
     * @param str string to process
     * @param delim delimiter in the string
     * @return last token delimit by given delimiter <code>delim</code> in the
     * input string.
     */
    public static String getPartBeforeLast(String str, String delim)
    {
        String part = null;
        
        if(null == delim || "".equals(delim))
        {
            return null;
        }
        
        int index = str.lastIndexOf(delim);

        if (index == -1)
        {
            part = str;
        }
        else
        {
            part = str.substring(0, index);
        }

        return part;
    }

    /**
     * Get simple name of given Class <code>clazz</code>. Please call getSimpleName() 
     * method on {@link Class} if use JDK 1.5 or above instead.
     * <p>Examples:
     * <blockquote><pre>
     * StringUtil.getSimpleClassName(StringUtil.class) return StringUtil
     * StringUtil.getSimpleClassName(null) return null
     * </pre></blockquote>
     * @param clazz instance of Class type
     * @return simple name of given class
     */
    public static String getSimpleClassName(Class<?> clazz)
    {
        if (null == clazz)
        {
            return null;
        }

        String className = clazz.getName();      
        className = getSimpleClassName(className);        
        return className;
    }

    /**
     * Get simple name of given class name <code>className</code>.
     * <p>Note:This method will method will not verify whether the given name
     * is a valid name for class or not.
     * <p>Examples:
     * <blockquote><pre>
     * StringUtil.getSimpleClassName("bmiasia.ebos.util.StringUtil") return "StringUtil"
     * StringUtil.getSimpleClassName("bmiasia.ebos.util") return "util"
     * StringUtil.getSimpleClassName("") return ""
     * StringUtil.getSimpleClassName(null) return null
     * </pre></blockquote>
     * @param className class name to operate
     * @return a simple class name 
     */
    public static String getSimpleClassName(String className)
    {
        if (!isValid(className))
        {
            return className;
        }

        String simpleName = className;
        int index = className.lastIndexOf(".");

        if (index != -1)
        {
            simpleName = className.substring(index + 1);
        }

        return simpleName;
    }

    /**
     * Verify the given string is a valid array type of primitive type.
     * @param javaType string represents java type
     * @return true if the given string represents a primitive array,
     * otherwise return false.
     */
    public static boolean isPrimitiveArrayType(String javaType)
    {
        boolean isPrimitive = false;
        String primitivePattern = "(\\[)*[IZBCDFIJS]$";
        isPrimitive = Pattern.matches(primitivePattern, javaType);
        return isPrimitive;
    }
    
    /**
     * Verify the given string is a valid Object array type.
     * @param javaType string represents java type
     * @return true if the given string represents an Object array type,
     * otherwise return false.
     */
    public static boolean isObjectArrayType(String javaType)
    {
        boolean isArray = false;
        String objArrayPttern = "(\\[)+L(([_$a-zA-Z])*([_$0-9a-zA-Z])*\\.)*([_$a-zA-Z]([_$0-9a-zA-Z])*);$";
        isArray = Pattern.matches(objArrayPttern, javaType);
        return isArray;
    }
    
    /**
     * Verify whether the given string <code>str</code> is valid. If the given
     * string is not null and contains any alphabet or digit, then the string
     * is think as valid, otherwise invalid.
     * @param str string to verify
     * @return false if the given string is null or contains only empty character,
     * otherwise return true.
     */
    public static boolean isValid(String str)
    {
        boolean valid = true;

        if ((str == null) || (str.trim().length() <= 0))
        {
            valid = false;
        }

        return valid;
    }
    
    /**
     * Tell whether the given string <code>className</code> represents a valid
     * class name. Please reference the java specification for right class name.
     * @param className given string
     * @return if the given string is valid then return true, otherwise return false
     */
    public static boolean isValidClassName(String className)
    {
        boolean valid = false;
        
        //boolean  Z,  byte  B ,char  C ,class or interface  Lclassname;   
        //double  D , float  F,int  I, long  J ,short  S  
        String primitivePattern = "(\\[)*[IZBCDFIJS]$";
        String objArrayPttern = "(\\[)+L(([_$a-zA-Z])*([_$0-9a-zA-Z])*\\.)*([_$a-zA-Z]([_$0-9a-zA-Z])*);$";
        String classPattern = "([_$a-zA-Z]([_$0-9a-zA-Z])*\\.)*([_$a-zA-Z]([0-9_$a-zA-Z])*)$";
        
        valid = Pattern.matches(primitivePattern, className);
        
        if(!valid)
        {
            valid = Pattern.matches(objArrayPttern, className);            
        }
        if(!valid)
        {
            valid = Pattern.matches(classPattern, className);
        }
        
        return valid;
    }

    /**
     * Join each string in the string list <code>list</code> to one string
     * that delimiter by given delimiter <code>delim</code>.
     * <p>Examples:
     * <blockquote><pre>
     * List strList = new ArrayList();
     * strList.add("hello");
     * strList.add("world");
     * strList.add(null);
     * StringUtil.join(strList, " ");
     * 
     * return "hello world null"
     * </pre></blockquote>
     * @param list a string list, null in the list will be replace with "null"
     * @param delim delimiter to delimit each string from the string list
     * @return return joined string
     */
    public static String join(List<String> list, String delim)
    {
        if ((list == null) || (list.size() < 1))
        {
            return null;
        }

        StringBuffer buf = new StringBuffer();
        Iterator<String> i = list.iterator();

        while (i.hasNext())
        {
            buf.append((String) i.next());

            if (i.hasNext())
            {
                buf.append(delim);
            }
        }

        return buf.toString();
    }

    /**
     * Get string value of given Object <code>param</code>.
     * This method will invoke toString() method on given object if not null.
     * @param param object instance
     * @return string represent given object if it's not null, otherwise return null.
     */
    public static String toString(Object param)
    {
        if (param == null)
        {
            return null;
        }

        return param.toString().trim();
    }

    /**
     * Check a string null or blank.
     * @param param  string to check
     * @return true if the given string is null or contains only space character
     */
    public static boolean nullOrBlank(String param)
    {
        return !isValid(param);
    }

    /**
     * Parse a string to boolean.
     * @param param  string to parse
     * @return boolean value, if param is(y,Y,t,T) or positive digital return true, on
     *         exception return false.
     */
    public static boolean parseBoolean(String param)
    {
        if (nullOrBlank(param))
        {
            return false;
        }

        String pattern = "([yYtT])|([1-9]\\d*)";
        boolean isTrue = false;
        
        isTrue = Pattern.matches(pattern, param);

        if(!isTrue)
        {
            if(param.equalsIgnoreCase("yes") || param.equalsIgnoreCase("true"))
            {
                isTrue = true;
            }
        }

        return isTrue;
    }

    /**
     * Parse the given string <code>param</code> to a double value.
     * @param param string to parse
     * @return return a double value if the given string represents a valid
     * double value.
     * @throws NumberFormatException if the given string does not represent
     * a valid double value
     */
    public static double parseDouble(String param)
    {
        double d = 0;

        d = Double.parseDouble(param);

        return d;
    }

    /**
     * Parse the given string <code>param</code> to a float value.
     * 
     * @param param the string to parse
     * @return return a float value if the given string represents a valid
     * float or double value
     * @throws NumberFormatException 
     */
    public static float parseFloat(String param)
    {
        float f = 0;

        try
        {
            f = Float.parseFloat(param);
        }
        catch (Exception e)
        {
            f = (float)parseDouble(param);
        }

        return f;
    }

    /**
     * Parse a string to int value.
     * @param param string to parse
     * @return return a int value if the given string represent a int value
     * @throws NumberFormatException if the given string does not represent a
     * valid int value
     */
    public static int parseInt(String param)
    {
        int i = 0;

        try
        {
            i = Integer.parseInt(param);
        }
        catch (Exception e)
        {
            i = (int) parseFloat(param);
        }

        return i;
    }

    /**
     * Parse a string <code>param</code> to a long vlaue.
     * @param param string to parse
     * @return return a long value if the given string represents a valid
     * long value or float/double value.
     */
    public static long parseLong(String param)
    {
        long l = 0;

        try
        {
            l = Long.parseLong(param);
        }
        catch (Exception e)
        {
            l = (long) parseDouble(param);
        }

        return l;
    }

    /**
     * Wrap all string in the list with single quote.
     * @param list a string list
     * @return string list with each string wrapped by single quote if the
     * element is not null.
     */
    public static List<String> quoteStrList(List<String> list)
    {
        List<String> tmpList = list;

        list = new ArrayList<String>();

        Iterator<String> i = tmpList.iterator();

        while (i.hasNext())
        {
            String str = (String) i.next();

            str = quoteString(str);
            list.add(str);
        }

        return list;
    }

    /**
     * Wrap the given string with single quote.
     * @param str string to wrap
     * @return wrapped string with single quote if the given string is not null
     */
    public static String quoteString(String str)
    {
        if(null == str)
        {
            return str;
        }
        StringBuffer dstStr = new StringBuffer(str.length() + 2);

        if (null != str)
        {
            dstStr.append("'").append(str).append("'");
        }

        return dstStr.toString();
    }

    /**
     * Wrap the string with single quote according to the <code>emptyWrap</code> flag.
     * <p>Examples:
     * <blockquote><pre>
     * StringUtil.quoteString("hello", true) return "'hello'"
     * StringUtil.quoteString("hello", false) return "'hello'"
     * StringUtil.quoteString("", true) return "''"
     * StringUtil.quoteString("", false) return ""
     * StringUtil.quoteString(null, true) return null
     * </pre></blockquote>
     * @param str string to wrap
     * @param emptyWrap flag tell whether wrap with single quote if the given
     * string is empty, true for wrap, false for not wrap
     * @return if the given string is null then return null, otherwise if given
     * string is not empty or the given <code>emptyWrap</code> is true, then
     * return wrapped string with single quote;if string is empty and 
     * <code>emptyWrap</code> is false, then return empty string.
     */
    public static String quoteString(String str, boolean emptyWrap)
    {
        if(str == null)
        {
            return null;
        }
        
        StringBuffer dstStr = new StringBuffer(str.length()+2);

        if (str != null)
        {
            if (emptyWrap || (str.trim().length() > 0))
            {
                dstStr.append("'").append( str).append("'");
            }
            else
            {
                return str;
            }
        }

        return dstStr.toString();
    }

    /**
     * Replace all matched token <code>oldString</code> with new token
     * <code>newString</code> in the given string <code>mainString</code>.
     * @param mainString string to operate
     * @param oldString token to remove
     * @param newString token to substitute the old token
     * @return new string with all <code>oldString</code> substituted by 
     * <code>newString</code>
     */
    public static String replace(String mainString, String oldString, String newString)
    {
        if (mainString == null)
        {
            return null;
        }

        int i = mainString.lastIndexOf(oldString);

        if (i < 0)
        {
            return mainString;
        }
        
        StringBuffer mainSb = new StringBuffer(mainString);

        while (i >= 0)
        {
            mainSb.replace(i, i + oldString.length(), newString);
            i = mainString.lastIndexOf(oldString, i - 1);
        }

        return mainSb.toString();
    }

    /**
     * Split the given string <code>str/code> delimited by given delimiter 
     * <code>delim</code> into string list.
     * <p>Examples:
     * <blockquote><pre>
     * StringUtil.split("2005-05-01", "-") return list L[2005,05,01]
     * StringUtil.split("2005 05 01", " ") return list L[2005,05,01]
     * </pre></blockquote>
     * @param str string to split
     * @param delim delimiter set which delimit the token in the string, if it's
     * null then use default value : space characters(" \t\r\n\f")
     * @return string list contains tokens splitted by given delimiter
     */
    public static List<String> split(String str, String delim)
    {
        List<String> splitList = null;
        StringTokenizer st = null;

        if (str == null)
        {
            return splitList;
        }

        if (isValid(delim))
        {
            st = new StringTokenizer(str, delim);
        }
        else
        {
            st = new StringTokenizer(str);
        }

        if ((st != null) && st.hasMoreTokens())
        {
            splitList = new ArrayList<String>();

            while (st.hasMoreTokens())
            {
                splitList.add(st.nextToken());
            }
        }

        return splitList;
    }

    /**
     * Remove all matched prefix <code>prefix</code> at the head of given
     * string <code>str</code>.
     * <p>Examples:
     * <blockquote><pre>
     * StringUtil.trimHeader("/hello", "/") return "hello"
     * StringUtil.trimHeader("///hello", "/") return "hello"
     * StringUtil.trimHeader("hello.txt", "hello") return ".txt"
     * StringUtil.trimHeader("hellohello.txt", "hello") return ".txt"
     * </pre></blockquote>
     * @param str string to operate
     * @param prefix the prefix to remove
     * @return new string with matched <code>prefix</code> remove
     */
    public static String trimHeader(String str, String prefix)
    {
        if(null == str)
        {
            return str;
        }
        
        String newStr = str;

        if (str.startsWith(prefix))
        {
            newStr = str.substring(prefix.length());
            newStr = trimHeader(newStr, prefix);
        }

        return newStr;
    }

    /**
     * Remove all matched suffix <code>suffix</code> at the tail of the given
     * string <code>str</code>.
     * <p>Examples:
     * <blockquote><pre>
     * StringUtil.trimTail("hello/", "/") return "hello"
     * StringUtil.trimTail("hello///", "/") return "hello"
     * StringUtil.trimTail("hello.txt", ".txt") return "hello"
     * StringUtil.trimTail("hello.txt.txt", ".txt") return "hello"
     * </pre></blockquote>
     * @param str the string to operate
     * @param suffix the suffix to remove
     * @return string that with matched suffix removed
     */
    public static String trimTail(String str, String suffix)
    {
        if(null == str)
        {
            return str;
        }
        
        String newStr = str;
        int position = 0;

        if (str.endsWith(suffix))
        {
            position = str.lastIndexOf(suffix);
            newStr = str.substring(0, position);
            newStr = trimTail(newStr, suffix);
        }

        return newStr;
    }

    /**
     * Truncate a given string <code>str</code> to given length <code>toLen</code>.
     * <p>
     * Examples:
     * <blockquote><pre>
     *  StringUtil.truncateString("Hello world", 5) return "Hello"
     *  StringUtil.truncateString("") return ""
     *  StringUtil.truncateString("Hello world", 20) return "Hello world"
     *  StringUtil.truncateString("Hello world", -1) return ""
     * </pre></blockquote>
     * @param str string to truncate
     * @param toLen which the length of output string should be less than,
     * negative or zero mean clear the string
     * @return return truncated string if the given string is valid ,otherwise
     * return input string itself; if the input string is valid and the 
     * <code>toLen</code> is negative then return empty string.
     */
    public static String truncateString(String str, int toLen)
    {
        String newStr = null;
        
        if(null == str)
        {
            return str;
        }
        
        if(toLen <=0)
        {
            return "";
        }
        
        if (str.length() > toLen)
        {
            newStr = str.substring(0, toLen);
        }
        else
        {
            return str;
        }

        return newStr;
    }

    /**
     * Truncate the given string to  given length <code>toLen</code> and append
     * given suffix <code>suffix</code> to it. 
     * <br>Note: the <code>toLen</code> not include the length of <code>suffix</code>
     * <p>
     * <blockquote><pre>
     *  StringUtil.truncateString("Hello world", 5) return "Hello..."
     *  StringUtil.truncateString("") return ""
     *  StringUtil.truncateString("Hello world", 20) return "Hello world..."
     *  StringUtil.truncateString("Hello world", -1) return "..."
     * </pre></blockquote>
     * @param str string to truncate
     * @param toLen which the length of output string should less than exclude suffix     
     * @param suffix the suffix append to the output string, default value is "..."
     * @return return truncated string if the given string is valid ,otherwise
     * return input string itself; if the input string is valid and the 
     * <code>toLen</code> is negative then return empty string.
     */
    public static String truncateString(String str, int toLen, String suffix)
    {
        if(null == str)
        {
            return str;
        }
        
        if(null == suffix)
        {
            suffix = "...";
        }
        
        StringBuffer newStr = new StringBuffer(toLen + suffix.length());
        
        newStr.append(truncateString(str, toLen));
        
        if(toLen < str.length())
        {
            newStr.append(suffix);
        }
        
        return newStr.toString();
    }

    /**
     * Convert the initial of given string <code>word</code> to lower case.
     * <p>
     * Examples:
     * <blockquote><pre>
     * StringUtil.uncapitalizeWord("hello") return "Hello"
     * StringUtil.uncapitalizeWord("World") return "World"
     * StringUtil.uncapitalizeWord("  ") return "  "
     * StringUtil.uncapitalizeWord(null) return null
     * </pre></blockquote>
     * @param word the string to convert
     * @return if given string is a valid string, then  return string with
     * lower case initial character, otherwise return the input string.
     */
    public static String uncapitalizeWord(String word)
    {
        String result = null;

        if (isValid(word))
        {
            result = word.substring(0, 1).toLowerCase() + word.substring(1);
        }
        else
        {
            return word;
        }

        return result;
    }

    private StringUtil()
    {
    }

	public static final String LINE_SEPARATOR   = System.getProperty("line.separator");
	public static final String DEFAULT_ENCODING = System.getProperty("file.encoding");

    private static final long KSIZE             = 1024L;
    private static final long MSIZE             = KSIZE * 1024L;
    private static final long GSIZE             = MSIZE * 1024L;
    private static final long TSIZE             = GSIZE * 1024L;
    
}
