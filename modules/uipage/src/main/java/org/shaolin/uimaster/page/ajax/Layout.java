/*
* Copyright 2015 The UIMaster Project
*
* The UIMaster Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
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
