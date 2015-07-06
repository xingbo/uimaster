/*
 * Copyright 2000-2003 by BraveMinds, Inc.,
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of BraveMinds, Inc.("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with BraveMinds.
 */
 
//package
package org.shaolin.javacc.symbol;

//imports
import java.util.ArrayList;
import java.util.List;

import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.javacc.context.OOEEEvaluationContext;
import org.shaolin.javacc.context.OOEEParsingContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.javacc.util.ExpressionStringBuffer;
import org.shaolin.javacc.util.ExpressionUtil;



/**
 * The class for field node
 *
 * @author Xiao Yi
 */

public class FieldExpression extends ExpressionNode
{
    public FieldExpression()
    {
        super("FieldExpression");
        isClass = false;
    }
    
    public void setCanBeClass(boolean aCanBeClass)
    {
        canBeClass = aCanBeClass;
    }
    
    public Class checkType(OOEEParsingContext context) throws ParsingException
    {
        int childNum = getChildNum();

        //process fieldName* prefix
        int fieldNamePrefixIndex = 0;
        List nameList = new ArrayList();
        StringBuffer sb = new StringBuffer();
        for(; fieldNamePrefixIndex < childNum; fieldNamePrefixIndex++)
        {
            ExpressionNode child = getChild(fieldNamePrefixIndex);
            if (!(child instanceof FieldName))
            {
                break;
            }
            FieldName fName = (FieldName)child;
            if (fieldNamePrefixIndex > 0)
            {
                sb.append(".");
            }
            sb.append(fName.getFieldName());
            nameList.add(new String(sb));
        }
        int resolvedIndex = 0;
        if (fieldNamePrefixIndex > 0)
        {
            resolvedIndex = resolve(fieldNamePrefixIndex, nameList, context);
        }
        
        for(int i = resolvedIndex; i < childNum; i++)
        {
            ExpressionNode child = getChild(i);

            setValueClass(child.checkType(context));
            
            super.isVariable = child.isVariable();
        }
        
        if(valueClass == null)
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_036,new Object[]{toString()});
        }

        return valueClass;
    }
    
    private int resolve(int fieldNamePrefixIndex, List nameList,
            OOEEParsingContext context) throws ParsingException
    {
        //
        //if all field names are lower case, resolve as variable firstly,
        //or resolve as class from likeClassNameIndex
        //
        //likeClassNameIndex:
        //      canBeClass(true):
        //              a.b.C.D
        //                    ^
        //              a.b.C.D.x
        //                    ^
        //      canBeClass(false):
        //              a.b.C.D
        //                  ^
        //              a.b.C.D.x
        //                    ^
        int likeClassNameIndex = getLikeClassNameIndex(fieldNamePrefixIndex);
        int ret = 0;
        if (likeClassNameIndex == -1 )
        {
            ret = resolveAsVariable(fieldNamePrefixIndex, nameList, context);
            if (ret == 0)
            {
                ret = resolveAsClass(fieldNamePrefixIndex, nameList, context, likeClassNameIndex);
            }
        }
        else
        {
            ret = resolveAsClass(fieldNamePrefixIndex, nameList, context, likeClassNameIndex);
            if (ret == 0)
            {
                ret = resolveAsVariable(fieldNamePrefixIndex, nameList, context);
            }
        }
        if (ret == 0)
        {
        	throw new ParsingException(ExceptionConstants.EBOS_OOEE_036,new Object[]{toString()});
           // throw new ParsingException("Can't resolve symbol " + toString());
        }
        return ret;
    }
    
    private int resolveAsVariable(int fieldNamePrefixIndex, List nameList, OOEEParsingContext context)
    {
        for (int i = 0; i < fieldNamePrefixIndex; i++)
        {
            String vName = (String)nameList.get(i);
            try
            {
                Class clazz = context.getVariableClass(vName);
                if (clazz != null)
                {
                    setValueClass(clazz);
                    isVariable = true;
                    FieldName fName = (FieldName)getChild(i);
                    fName.setVariableName(vName);
                    return i + 1;
                }
            }
            catch(ParsingException e1)
            {
                setValueClass(null);
            }
        }
        return 0;
    }
    
    private int resolveAsClass(int fieldNamePrefixIndex, List nameList,
            OOEEParsingContext context, int likeClassNameIndex)
    {
        if (!canBeClass)
        {
            fieldNamePrefixIndex--;
        }
        if (likeClassNameIndex == -1)
        {
            likeClassNameIndex = fieldNamePrefixIndex - 1;
        }
        for (int i = likeClassNameIndex; i >= 0; i--)
        {
            String cName = (String)nameList.get(i);
            try
            {
                setValueClass(ExpressionUtil.findClass(cName, context));
                isClass = true;
                return i + 1;
            }
            catch(ParsingException e1)
            {
                setValueClass(null);
            }
        }
        for (int i = likeClassNameIndex + 1; i < fieldNamePrefixIndex; i++)
        {
            String cName = (String)nameList.get(i);
            try
            {
                setValueClass(ExpressionUtil.findClass(cName, context));
                isClass = true;
                return i + 1;
            }
            catch(ParsingException e1)
            {
                setValueClass(null);
            }
        }
        return 0;
    }
    
    private int getLikeClassNameIndex(int fieldNamePrefixIndex)
    {
        if (!canBeClass)
        {
            fieldNamePrefixIndex--;
        }
        for (fieldNamePrefixIndex--; fieldNamePrefixIndex >= 0; fieldNamePrefixIndex--)
        {
            FieldName fName = (FieldName)getChild(fieldNamePrefixIndex);
            if (fName.isLikeClassName())
            {
                return fieldNamePrefixIndex;
            }
        }
        return -1;
    }
    
    public boolean isClass()
    {
        return isClass;
    }
    
    public void setIsClass(boolean isClass)
    {
        this.isClass = isClass;
    }

    protected void evaluateNode(OOEEEvaluationContext context) throws EvaluationException
    {
        try
        {
            getValueClass();
        }
        catch(ParsingException e)
        {
        	throw new EvaluationException(ExceptionConstants.EBOS_000,e);
        }

        for(int i = 0, childNum = getChildNum(); i < childNum; i++)
        {
            ExpressionNode child = getChild(i);
           
            child.evaluate(context);
        }
    }
    
    public void setIsValueAssignee(boolean isValueAssignee)
    {
        ExpressionNode child = getChild(getChildNum() - 1);
        child.setIsValueAssignee(isValueAssignee);
    }

    protected void setVariableValue(OOEEEvaluationContext context, Object variableValue) throws EvaluationException
    {
        ExpressionNode child = getChild(getChildNum() - 1);
        child.setVariableValue(context, variableValue);
    }

    public void appendToBuffer(ExpressionStringBuffer buffer)
    {
        int childNum = getChildNum();
        for(int i = 0; i < childNum; i++)
        {
            ExpressionNode node = getChild(i);
            buffer.appendExpressionNode(node);
            if(i != childNum - 1)
            {
                buffer.appendSeperator(this, ".");
            }
        }
    }
    
    /*
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        
        int childNum = getChildNum();
        for(int i = 0; i < childNum; i++)
        {
            ExpressionNode node = getChild(i);
            buffer.append(node);
            if(i != childNum - 1)
            {
                buffer.append(".");
            }
        }
        
        return buffer.toString();           
    }
    */
    
    /* indicate whether this field node can be a name of a class 
       combining this value with valueClass can indicate the following situations:
       1. isClass == true, parentClass == null : still try to resolve this node as a name of a class
       2. isClass == true, parentClass != null : has successfullly resolved this node as a name of a class
       3. isClass == false, parentClass != null: has resolved this node as a field, a function or a variable
       4. isClass == false, parentClass == null: fail to resolve this node, must be error
    */
    
    private boolean isClass;

    private boolean canBeClass = true;
    
    static final long serialVersionUID = 0x84FF998A4F2C60CCL;
 
    public static final String ___REVISION___ = "$Revision: 1.10 $";
}
