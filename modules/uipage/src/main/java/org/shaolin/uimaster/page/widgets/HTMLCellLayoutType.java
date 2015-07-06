package org.shaolin.uimaster.page.widgets;

import java.io.IOException;

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HTMLCellLayoutType extends HTMLLayoutType
{
    private static final long serialVersionUID = -3130824264110176460L;
    
    private static final Logger logger = LoggerFactory.getLogger(HTMLCellLayoutType.class);
    
    private String cellUIStyle = "";
    
    private String cellUIClass = "";
    
    private int colSpan = 1;
    
    private int rowSpan = 1;
    
    private String container = "";
    
    private boolean isContainer = false;

    public HTMLCellLayoutType()
    {
    }
 
    public HTMLCellLayoutType(HTMLSnapshotContext context)
    {
        super(context);
    }

    public HTMLCellLayoutType(HTMLSnapshotContext context, String id)
    {
        super(context, id);
    }

    public void setContainer(String container)
    {
        this.container = container;
    }
    
    public void setIsContainer(boolean isContainer)
    {
        this.isContainer = isContainer;
    }

    @Override
    public void generateBeginHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth)
    {
        try
        {
            if ( !context.isLeftToRight() )
            {
                addStyle("float", "right");
                if ( !isContainer )
                {
                    addStyle("direction", "rtl");
                }
            }
            context.generateHTML("<div id=\"div-");
            context.generateHTML(context.getDIVPrefix());
            context.generateHTML(container + getX() + "_" + getY() + "\"");
            generateAttributes(context);
            context.generateHTML(" class=\"");
            if ( !isContainer )
            {
                context.generateHTML("uimaster_widget_cell ");
                context.generateHTML("w" + colSpan + " h" + rowSpan + " ");
            }
            else
            {
                context.generateHTML("uimaster_container_cell ");
            }
            context.generateHTML(cellUIClass);
            context.generateHTML("\"");
            if (cellUIStyle != null && !cellUIStyle.isEmpty()) {
	            context.generateHTML(" style=\"");
	            context.generateHTML(cellUIStyle);
	            context.generateHTML("\"");
            }
            context.generateHTML(" >");
        }
        catch (Exception e)
        {
            logger.error("error. in entity: " + getUIEntityName(), e);
        }
    }

    @Override
    public void generateEndHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth)
    {
        try
        {
            context.generateHTML("</div>");
        }
        catch (Exception e)
        {
            logger.error("error. in entity: " + getUIEntityName(), e);
        }
    }
    
    public void generateAttribute(HTMLSnapshotContext context, String attributeName, Object attributeValue) throws IOException
    {
        String attrValue = (String)attributeValue;
        if( "visible".equals(attributeName) )
		{
		    if( "false".equals(String.valueOf(attrValue)))
			{
			    addStyle("display", "none");
			}
		}
        else if ("cellUIStyle".equals(attributeName))
        {
            if (!"null".equals(attrValue))
            {
                cellUIStyle = attrValue;
            }
        }
        else  if ("cellUIClass".equals(attributeName))
        {
            if (!"null".equals(attrValue))
            {
            	cellUIClass = attrValue;
            }
        }
        else if ("align".equals(attributeName))
        {
            if(!"full".equals(attrValue.toLowerCase()))
            {
                addStyle("text-align", attrValue);
            }
        }
        else if ( "valign".equals(attributeName) || "rowUIStyle".equals(attributeName)
                || "x".equals(attributeName) || "y".equals(attributeName) )
        {
        }
        else if ("colSpan".equals(attributeName))
        {
            colSpan = Integer.parseInt(attrValue);
        }
        else if ("rowSpan".equals(attributeName))
        {
            rowSpan = Integer.parseInt(attrValue);
        }
        else if ("width".equals(attributeName))
        {
        	if (!"0px".equals(attrValue)) {
        		addStyle("min-width", attrValue);
        		//addStyle("_width", attrValue);
        	}
        }
        else if ("height".equals(attributeName))
        {
        	if (!"0px".equals(attrValue)) {
	            addStyle("min-height", attrValue);
	            //addStyle("_height", attrValue);
        	}
        }
        else
        {
            super.generateAttribute(context, attributeName, attributeValue);
        }
    }

}
