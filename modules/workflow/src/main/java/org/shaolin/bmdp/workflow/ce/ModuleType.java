/*
 * This code is generated automatically, any change will be replaced after rebuild.
 * Generated on Sun Aug 09 12:57:27 CST 2015
 */

package org.shaolin.bmdp.workflow.ce;
import java.util.*;
import org.shaolin.bmdp.runtime.ce.IConstantEntity;
import org.shaolin.bmdp.runtime.ce.AbstractConstant;

/**
 * 
 * entityName: org.shaolin.bmdp.workflow.ce.ModuleType
 *
 */
public final class ModuleType extends AbstractConstant
{
    public static final String ENTITY_NAME = "org.shaolin.bmdp.workflow.ce.ModuleType";
    
    protected static final long serialVersionUID = 0x811b9115811b9115L;
    private static String i18nBundle = "org_shaolin_bmdp_workflow_i18n";

    //User-defined constant define

    public static final ModuleType NOT_SPECIFIED = new ModuleType(CONSTANT_DEFAULT_VALUE, -1, null, null, null, null, false);

    public static final ModuleType WORKFLOW = new ModuleType("Workflow", 1, "org.shaolin.bmdp.workflow.ce.ModuleType.WORKFLOW", "Workflow Entity", null, null,false);

    public static final ModuleType BUSINESSFUNCTION = new ModuleType("BusinessFunction", 2, "org.shaolin.bmdp.workflow.ce.ModuleType.BUSINESSFUNCTION", "Functionality Modules", null, null,false);

    //End of constant define

    //Common constant define
    public ModuleType()
    {
        constantList.add(NOT_SPECIFIED);
        constantList.add(WORKFLOW);
        constantList.add(BUSINESSFUNCTION);
    }

    private ModuleType(long id, String value, int intValue, String i18nKey, String description)
    {
        super(id, value, intValue, i18nKey, description);
    }
    
    private ModuleType(String value, int intValue, String i18nKey,
        String description, Date effTime, Date expTime)
    {
        super(value, intValue, i18nKey, description, effTime, expTime);
    }

    private ModuleType(String value, int intValue, String i18nKey,
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

    protected AbstractConstant __create(String value, int intValue, String i18nKey,
        String description, Date effTime, Date expTime)
    {
        return new ModuleType(value, intValue, i18nKey,
            description, effTime, expTime);
    }

    protected String getTypeEntityName()
    {
        return ENTITY_NAME;
    }

}

