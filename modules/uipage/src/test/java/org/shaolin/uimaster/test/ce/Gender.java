/*
 * This code is generated automatically, any change will be replaced after rebuild.
 * Generated on Mon Jun 16 22:29:20 CST 2014
 */

package org.shaolin.uimaster.test.ce;
import java.io.*;
import java.util.*;
import org.shaolin.bmdp.runtime.ce.IConstantEntity;
import org.shaolin.bmdp.runtime.ce.AbstractConstant;

/**
 * null
 * entityName: org.shaolin.uimaster.test.ce.Gender
 *
 */
public final class Gender extends AbstractConstant
{
    public static final String ENTITY_NAME = "org.shaolin.uimaster.test.ce.Gender";
    
    protected static final long serialVersionUID = 0x811b9115811b9115L;
    private static String i18nBundle = null;

    //User-defined constant define

    public static final Gender NOT_SPECIFIED = new Gender(CONSTANT_DEFAULT_VALUE, -1, null, null, null, null, false);


    public static final Gender MALE = new Gender("Male", 1,null, null, null, null,false);

    public static final Gender FEMALE = new Gender("Female", 2,null, null, null, null,false);

    //End of constant define

    //Load constants
    public Gender()
    {
        constantList.add(NOT_SPECIFIED);
        constantList.add(MALE);
        constantList.add(FEMALE);
    }

    //Common constant define
    private Gender(String value, int intValue)
    {
        super(value, intValue);
    }

    private Gender(String value, int intValue, String i18nKey)
    {
        super(value, intValue, i18nKey);
    }
    
    private Gender(String value, int intValue, String i18nKey,
        String description, Date effTime, Date expTime)
    {
        super(value, intValue, i18nKey, description, effTime, expTime);
    }

    private Gender(String value, int intValue, String i18nKey,
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
        return new Gender(value, intValue, i18nKey,
            description, effTime, expTime);
    }

    protected String getTypeEntityName()
    {
        return ENTITY_NAME;
    }

}

