/*
 * This code is generated automatically, any change will be replaced after rebuild.
 * Generated on Thu Jun 04 22:37:13 CST 2015
 */

package org.shaolin.bmdp.coordinator.ce;
import java.util.*;
import org.shaolin.bmdp.runtime.ce.IConstantEntity;
import org.shaolin.bmdp.runtime.ce.AbstractConstant;

/**
 * 
 * entityName: org.shaolin.bmdp.coordinator.ce.TaskPriorityType
 *
 */
public final class TaskPriorityType extends AbstractConstant
{
    public static final String ENTITY_NAME = "org.shaolin.bmdp.coordinator.ce.TaskPriorityType";
    
    protected static final long serialVersionUID = 0x811b9115811b9115L;
    private static String i18nBundle = "org_shaolin_bmdp_coordinator_i18n";

    //User-defined constant define

    public static final TaskPriorityType NOT_SPECIFIED = new TaskPriorityType(CONSTANT_DEFAULT_VALUE, -1, null, null, null, null, false);

    public static final TaskPriorityType LOW = new TaskPriorityType("Low", 0, "org.shaolin.bmdp.coordinator.ce.TaskPriorityType.LOW", "Low", null, null,false);

    public static final TaskPriorityType NORMAL = new TaskPriorityType("Normal", 1, "org.shaolin.bmdp.coordinator.ce.TaskPriorityType.NORMAL", "Normal", null, null,false);

    public static final TaskPriorityType HIGH = new TaskPriorityType("High", 2, "org.shaolin.bmdp.coordinator.ce.TaskPriorityType.HIGH", "High", null, null,false);

    public static final TaskPriorityType CRITICAL = new TaskPriorityType("Critical", 3, "org.shaolin.bmdp.coordinator.ce.TaskPriorityType.CRITICAL", "Critical", null, null,false);

    //End of constant define

    //Common constant define
    public TaskPriorityType()
    {
        constantList.add(NOT_SPECIFIED);
        constantList.add(LOW);
        constantList.add(NORMAL);
        constantList.add(HIGH);
        constantList.add(CRITICAL);
    }

    private TaskPriorityType(long id, String value, int intValue, String i18nKey, String description)
    {
        super(id, value, intValue, i18nKey, description);
    }
    
    private TaskPriorityType(String value, int intValue, String i18nKey,
        String description, Date effTime, Date expTime)
    {
        super(value, intValue, i18nKey, description, effTime, expTime);
    }

    private TaskPriorityType(String value, int intValue, String i18nKey,
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
        return new TaskPriorityType(value, intValue, i18nKey,
            description, effTime, expTime);
    }

    protected String getTypeEntityName()
    {
        return ENTITY_NAME;
    }

}

