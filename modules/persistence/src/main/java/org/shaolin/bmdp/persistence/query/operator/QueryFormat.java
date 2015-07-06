package org.shaolin.bmdp.persistence.query.operator;



/**
 * <code>QueryFormat</code> provides a means to produce concatenated
 * queries. Use this to construct both SQL and OQL queries.
 * 
 * <p>
 * <code>QueryFormat</code> takes a set of objects, inserts them
 * at the appropriate places.
 * 
 * <p>
 * <strong>Note:</strong>
 * <code>QueryFormat</code> differs from the other <code>Format</code>
 * classes in that you create a <code>QueryFormat</code> object with one
 * of its constructors (not with a <code>getInstance</code> style factory
 * method). The factory methods aren't necessary because <code>QueryFormat</code>
 * doesn't require any complex setup. In fact, <code>QueryFormat</code> doesn't
 * implement any locale specific behavior at all.
 * 
 * <p>
 * Here are some examples of usage:
 * <blockquote>
 * <pre>
 * 
 * String anEmployeeId = "JohnDoe";                   
 * String anSql = QueryFormat.format("select salary from employee where id = '{0}'", 
 *      new Object[]{anEmployeeId});
 * 
 * <em>output</em>: select salary from employee where id ='JohnDoe'
 * 
 * </pre>
 * <pre>
 * 
 * String anSql = QueryFormat.format("select {0} from {1} where id = '{2}'", 
 *      new Object[]{"salary", "employee","JohnDoe"});
 * 
 * <em>output</em>: select salary from employee where id ='JohnDoe'
 * 
 * </pre>
 * </blockquote>
 * <p>
 * 
 * @author Tom Joseph
 */
public class QueryFormat
{
    /**
     * Constructs a query format with the specified pattern.
     * @see QueryFormat#applyPattern
     */
    public QueryFormat(String aPattern)
    {
        applyPattern(aPattern);
    }

    /**
     * Apply the pattern string to be formatted with the specified 
     * argument list. 
     */
    public void applyPattern(String aPattern)
    {
        StringBuffer aBuf = new StringBuffer();
        int aBraceStack = 0;
        int anArgIdx = 0, anInsertIdx = 0, aCurrentSize = 0;

        // initialize total number of tags
        totalPatterns = 0;

        StringBuffer    anIdxBuf = new StringBuffer();
        for (int i = 0; i < aPattern.length(); i++)
        {
            char aCh = aPattern.charAt(i);
            if (aCh == '{')
            {
                ++aBraceStack;

                // clear
                anIdxBuf.setLength(0);
            }
            else if (aCh == '}')
            {
                --aBraceStack;

                // set the offset
                offsets[totalPatterns] = new Position();
                offsets[totalPatterns].argIndex = getArgIndex(anIdxBuf.toString());
                offsets[totalPatterns ++].offset = aCurrentSize;
            }
            else if (0 != aBraceStack)
            {
                anIdxBuf.append(aCh);        
            }
            else
            {
                aBuf.append(aCh);
                aCurrentSize ++;
            }
        }
        if (aBraceStack != 0)
        {
            throw new IllegalArgumentException("Unmatched braces in the pattern.");
        }
        pattern = aBuf.toString();
    }


    /**
     * Gets the formatted string with the specified argument 
     * list.
     * 
     * @param anArgs the arguments to insert at appropriate locations.
     * @return the formatted String.
     */
    public  String  format(Object[] anArgs)
    {
        int aLength = pattern.length();
        StringBuffer aBuffer = new StringBuffer(aLength);
        int aStartIdx = 0;
        for (int i = 0; i < totalPatterns; i ++)
        {
            aBuffer.append(pattern.substring(aStartIdx, offsets[i].offset));
            aBuffer.append(null != anArgs[offsets[i].argIndex] ? anArgs[offsets[i].argIndex].toString() : "null");
            aStartIdx = offsets[i].offset;
        }
        if (aStartIdx <= aLength)
        {
            aBuffer.append(pattern.substring(aStartIdx, aLength));
        }
        return aBuffer.toString();
    }

    /**
     * Create a QueryFormat and use it to format a query string
     * with specified argument list.
     * Avoids explicit creation of QueryFormat,
     * but doesn't allow future optimizations.
     * 
     * @param aPattern the query string containing argument place holders.
     * @param anArgs the argument list.
     * @return the formatted query string.
     */
    public static String format(String aPattern, Object[] anArgs) 
    {
        QueryFormat aTemp = new QueryFormat(aPattern);
        return aTemp.format(anArgs);
    }
    

    /**
     * Get the index from the buffer.
     * 
     * @param aBuf   index buffer.
     * @return index/
     */
    private int getArgIndex(String aBuf)
    {
        int anIdx = new Integer(aBuf).intValue();
        if (anIdx < 0 || anIdx >= MAX_ARGS)
        {
            throw new IllegalArgumentException("Invalid argument index " + aBuf);
        }
        return anIdx;
    }

    
    /**
     * The string that the formatted values are to be plugged into.  In other words, this
     * is the pattern supplied on construction with all of the {} expressions taken out.
     * @serial
     */
    private String pattern = "";

    /**
     * The positions where the results of formatting each argument are to be inserted
     * into the pattern.
     * @serial
     */
    private Position[] offsets = new Position[MAX_ARGS];

    private int totalPatterns = 0;

    static  final   int MAX_ARGS = 10;


    /**
     * A position where to insert the argument.
     * 
     * @author Tom Joseph
     */
    private static  class   Position
    {
        public  int argIndex;
        public  int offset;

        public String   toString()
        {
            return "index = " + argIndex + ", offset = " + offset;
        }
    }

}
