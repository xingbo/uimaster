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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.uimaster.page.UIPermissionManager;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.security.ComponentPermission;

public class CellLayout extends Layout implements Serializable
{
    private static final long serialVersionUID = -4318638788347246467L;

    private List compList;

    private CellLayout front;

    private CellLayout next;

    public CellLayout()
    {
        compList = new ArrayList();
    }

    public void setFront(CellLayout front)
    {
        this.front = front;
        if (front != null)
        {
            front.next = this;
        }
    }

    public CellLayout getFront()
    {
        return front;
    }

    public void setNext(CellLayout next)
    {
        this.next = next;
        if (next != null)
        {
            next.front = this;
        }
    }

    public CellLayout getNext()
    {
        return next;
    }

    public int getComponentIndex(Widget component)
    {
        return compList.indexOf(component);
    }

    public int getComponentSize()
    {
        return compList.size();
    }

    public void addComponent(Widget component)
    {
        compList.add(component);
        component.setHtmlLayout(this);
        this.setListened(true);
    }

    /**
     * @deprecated Should be prohibited to use this method.
     **/
    public void addComponent(int position, Widget component)
    {
        compList.add(position, component);
    }

    public Widget getComponent(int position)
    {
        Widget comp = null;
        if (position < getComponentSize())
        {
            comp = (Widget)compList.get(position);
        }
        return comp;
    }

    public void removeComponent(int position)
    {
        compList.remove(position);
    }

    /**
     * wraps component html in a div tag
     * 
     * @param htmlDiv
     */
    public String generateHTML()
    {
        StringBuffer html = new StringBuffer();
		
        //hasviewpermission will be used to indicate whether the value of the component needs to be hidden
        //by default without viewpermission configs, the hasviewpermission is true, which means,
        //if no viewpermission configured, the value will never be hidden.
        boolean hasViewPermission = true;
        ComponentPermission cp = null;
        if (isFLSEnabled())
        {
            cp = getContainedComponentPermission();
            String[] viewPermission = HTMLUtil.getViewPermission(cp, getContainedComponentViewPermission());
            if (viewPermission.length > 0)
            {
                hasViewPermission = HTMLUtil.checkViewPermission(viewPermission);
                if (!hasViewPermission)
                {
                    setVisible(false, false);
                }
            }
        }

        html.append("<div id=\"");
        html.append(getId());
        html.append("\"");
        generateAttributes(html);
        String className = (String)this.getAttribute("class");
        if(className == null)
        {
            generateAttribute("class", "uimaster_widget_cell w1 h1", html);
        }
        else if(className.indexOf("uimaster_container_cell") == -1 
                && className.indexOf("uimaster_widget_cell") == -1 )
        {
            generateAttribute("class", className + "uimaster_widget_cell w1 h1", html);
        }
        html.append(">");

        for (Iterator iterator = compList.iterator(); iterator.hasNext();)
        {
            Widget comp = (Widget)iterator.next();
            if (isFLSEnabled())
            {
                comp.setValueMask(!hasViewPermission);
                comp.addSecurityControls(cp);
            }
            html.append(comp.generateHTML());
        }

        html.append("</div>");

        return html.toString();
    }

    void append(CellLayout layout)
    {
        if (next != null)
        {
            next.append(layout);
        }
        else
        {
            setNext(layout);
        }
    }

    public void remove()
    {
        if (next != null)
        {
            next.setFront(front);
        }
        else if (front != null)
        {
            front.next = null;
        }
        front = next = null;
    }

    public void before(Widget comp)
    {
        CellLayout layout = (CellLayout)comp.getHtmlLayout();
        layout.setFront(front);
        layout.setNext(this);
        layout.setY(y);
        if(parent != null)
        {
            layout.setParent(parent);
            ((Panel)parent).setIDForNewLayout(layout);
        }
    }

    public void after(Widget comp)
    {
        CellLayout layout = (CellLayout)comp.getHtmlLayout();
        layout.setNext(next);
        layout.setFront(this);
        layout.setY(y);
        if(parent != null)
        {
            layout.setParent(parent);
            ((Panel)parent).setIDForNewLayout(layout);
        }
    }

    private ComponentPermission getContainedComponentPermission()
    {
        Widget comp = null;
        for (Iterator iterator = compList.iterator(); iterator.hasNext();)
        {
            comp = (Widget)iterator.next();
            break;
        }
        if (comp != null)
        {
            //split the compId as prefix and suffix
            //String[] splitedCompId = AjaxComponentSecurityUtil.splitComponentId(comp.getId());
            //return AjaxComponentSecurityUtil.getComponentPermission(splitedCompId[0],splitedCompId[1]);
        	UIPermissionManager permissionService = AppContext.get().getService(UIPermissionManager.class);
            return permissionService.getComponentPermission(comp.getUIEntityName(), comp.getId());
        }
        else
        {
            return null;
        }
    }
	
	private String[] getContainedComponentViewPermission()
    {
        Widget comp = null;
        for (Iterator iterator = compList.iterator(); iterator.hasNext();)
        {
            comp = (Widget)iterator.next();
            break;
        }
        if(comp != null)
        {
            return comp.getViewPermissions();
        }
        else
        {
            return null;
        }
    }

}
