/*
 * Copyright 2000-2009 by BMI Asia, Inc.,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of BMI Asia, Inc.("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with BMI Asia.
 */

package org.shaolin.uimaster.page.ajax;

import java.io.Serializable;

/**
 * Layout object will not be added in UI map, usually this object is attached as a field of a widget.
 * 
 * @author swu
 *
 */
abstract public class Layout extends Widget implements Serializable
{
    private static final long serialVersionUID = -2271538279502552196L;

    public static final Layout.EmptyLayout NULL = new Layout.EmptyLayout();
    
    protected Widget parent;
    
    protected int x;
    
    protected int y;
    
    public Layout()
    {
        super(null, NULL);
    }

    public void setParent(Widget parent)
    {
        this.parent = parent;
    }

    public Widget getParent()
    {
        return parent;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getX()
    {
        return x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public int getY()
    {
        return y;
    }

    static class EmptyLayout extends Layout
    {
        public EmptyLayout(){}
        
        public String generateHTML()
        {
            return "";
        }
    }
    
}
