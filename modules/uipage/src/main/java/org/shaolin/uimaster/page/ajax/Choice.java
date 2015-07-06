package org.shaolin.uimaster.page.ajax;

import java.util.ArrayList;
import java.util.List;

import org.shaolin.uimaster.page.AjaxContext;
import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.ajax.json.IDataItem;

abstract public class Choice extends Widget
{
    private static final long serialVersionUID = -2155496455366137360L;

    private static final String CLEAR_ALL_ITEMS = "CLEAR_ALL_ITEMS_2155496455366137360L";
    
    protected List<String> optionValues = new ArrayList<String>();

    protected List<String> optionDisplayValues = new ArrayList<String>();
    
    public Choice(String id, Layout layout)
    {
        super(id, layout);
        this._setWidgetLabel(id);
    }

    /**
     * 
     * @param itemName
     * @param itemValue
     */
    public void addOption(String itemName, String itemValue)
    {
        if(this._isReadOnly())
        {
            return;
        }
        if(!optionValues.contains(itemValue))
        {
            optionValues.add(itemValue);
            optionDisplayValues.add(itemName);
            
            if (!this.isListened())
            {
                return;
            }
            AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
            if (ajaxContext == null || !ajaxContext.existElement(this))
            {
                return;
            }
            
            String str = "{'item':'true','name':'"+ itemName +"','value':'"+ HTMLUtil.handleEscape(String.valueOf(itemValue)) +"'}";
            IDataItem dataItem = AjaxActionHelper.updateAttrItem(this.getId(), str);
            dataItem.setFrameInfo(getFrameInfo());
            ajaxContext.addDataItem(dataItem);
        }
    }
    
    /**
     * 
     * Be noticed that the parameter is itemValue!
     * 
     * @param itemValue
     */
    public void removeOption(String itemValue)
    {
        if(this._isReadOnly())
        {
            return;
        }
        if(optionValues.contains(itemValue))
        {
            int index = optionValues.indexOf(itemValue);
            optionValues.remove(index);
            optionDisplayValues.remove(index);
            
            _removeAttribute(itemValue);
        }
    }

    public void setOptions(List<String> displayValues, List<String> optionValues)
    {
        if(displayValues == null && optionValues == null)
        {
            return;
        }
        if(displayValues == null)
        {
            displayValues = new ArrayList<String>(optionValues);
        }
        if(displayValues.size() != optionValues.size())
        {
            throw new IllegalArgumentException("Value's size and Display's size are mismatched!");
        }
        if (!this.isListened())
        {
            // create ajax object initially.
            this.optionValues = new ArrayList<String>(optionValues);
            //to cut the reference between optionValues and displayValues. If both are the same object, it will be a potential problem.
            this.optionDisplayValues = new ArrayList<String>(displayValues);//
            
            return;
        }
        if(this._isReadOnly())
        {
            return;
        }
        
        this.optionValues = new ArrayList<String>(optionValues);
        this.optionDisplayValues = new ArrayList<String>(displayValues);
        
        updateOptions();
    }
    
    private void updateOptions()
    {
        if(optionDisplayValues.size() != optionValues.size())
        {
            throw new IllegalArgumentException("The size of Value option and the size of Display option are mismatched!");
        }
        if (!this.isListened())
        {
            return;
        }
        AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        if (ajaxContext == null || !ajaxContext.existElement(this))
        {
            return;
        }
        if(this._isReadOnly())
        {
            return;
        }
        
        _removeAttribute(CLEAR_ALL_ITEMS);
        for(int i=0; i<this.optionValues.size(); i++)
        {
            String name = this.optionDisplayValues.get(i).toString();
            String value = this.optionValues.get(i).toString();
            
            String str = "{'item':'true','name':'"+ name +"','value':'"+ HTMLUtil.handleEscape(String.valueOf(value)) +"'}";
            IDataItem dataItem = AjaxActionHelper.updateAttrItem(this.getId(), str);
            dataItem.setFrameInfo(getFrameInfo());
            ajaxContext.addDataItem(dataItem);
        }
    }
    
    public List<String> getOptionValues()
    {
        return optionValues;
    }

    public List<String> getOptionDisplayValues()
    {
        return optionDisplayValues;
    }

}
