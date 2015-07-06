/*
 * This code is generated automatically, any change will be replaced after rebuild.
 * Generated on Sun Aug 17 10:56:31 CST 2014
 */

package org.shaolin.bmdp.test.ce;
import java.io.*;
import java.util.*;
import org.shaolin.bmdp.runtime.ce.IConstantEntity;
import org.shaolin.bmdp.runtime.ce.AbstractConstant;

/**
 * null
 * entityName: org.shaolin.bmdp.test.ce.FileType
 *
 */
public final class FileType extends AbstractConstant
{
    public static final String ENTITY_NAME = "org.shaolin.bmdp.test.ce.FileType";
    
    protected static final long serialVersionUID = 0x811b9115811b9115L;
    private static String i18nBundle = "org_shaolin_bmdp__designtime_i18n";

    private static final List<FileType> constantList = new ArrayList<FileType>(5);

    //User-defined constant define

    public static final FileType NOT_SPECIFIED = new FileType(CONSTANT_DEFAULT_VALUE, -1, null, null, null, null, false);


    public static final FileType SYSTEM = new FileType("System", 0, "org.shaolin.bmdp.test.ce.FileType.System", "This is Constant0 item.", null, null,false);

    public static final FileType WORD = new FileType("Word", 1, "org.shaolin.bmdp.test.ce.FileType.Word", "This is Constant1 item.", null, null,false);

    public static final FileType JAVA = new FileType("Java", 2, "org.shaolin.bmdp.test.ce.FileType.Java", "This is Constant2 item.", null, null,false);

    //End of constant define

    //Load constants
    public FileType()
    {
        constantList.add(NOT_SPECIFIED);
        constantList.add(SYSTEM);
        constantList.add(WORD);
        constantList.add(JAVA);
    }

    //Common constant define
    private FileType(String value, int intValue)
    {
        super(value, intValue);
    }

    private FileType(String value, int intValue, String i18nKey)
    {
        super(value, intValue, i18nKey);
    }
    
    private FileType(String value, int intValue, String i18nKey,
        String description, Date effTime, Date expTime)
    {
        super(value, intValue, i18nKey, description, effTime, expTime);
    }

    private FileType(String value, int intValue, String i18nKey,
            String description, Date effTime, Date expTime, boolean isPassivated)
    {
        super(value, intValue, i18nKey, description, effTime, expTime, isPassivated);
    }
    
    public String getI18nBundle()
    {
        return i18nBundle;
    }

    public void setI18nBundle(String bundle)
    {
        i18nBundle = bundle;
    }

    protected void __init()
    {
        //load constants in db
    }

    protected AbstractConstant __create(String value, int intValue, String i18nKey,
        String description, Date effTime, Date expTime)
    {
        return new FileType(value, intValue, i18nKey,
            description, effTime, expTime);
    }

    protected String getTypeEntityName()
    {
        return ENTITY_NAME;
    }

}

