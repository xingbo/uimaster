    
package org.shaolin.uimaster.page.ajax.json;

public interface IRequestData extends java.io.Serializable
{
    public final static String ENTITY_NAME = "org.shaolin.uimaster.page.ajax.json.RequestData";
    
    // getter methods block
    
    /**
     *  get uiid
     *
     *  @return uiid
     */
    public java.lang.String getUiid();

    
    public java.lang.String getEntityName();
            
    /**
     *  get entityUiid
     *
     *  @return entityUiid
     */
    public java.lang.String getEntityUiid();

            
    /**
     *  get data
     *
     *  @return data
     */
    public java.util.Map getData();

    /**
     *  get frameId
     *  
     *  @return frameId
     */
    public String getFrameId();
    
    /**
     *  get bAName
     *  
     *  @return bAName
     */
    public String getBAName();

    // setter methods block
    
    /**
     *  set uiid
     */
    public void setUiid(java.lang.String uiid);

            
    /**
     *  set entityUiid
     */
    public void setEntityUiid(java.lang.String entityUiid);

    /**
     *  set entityName
     */
    public void setEntityName(java.lang.String entityName);
            
    /**
     *  set data
     */
    public void setData(java.util.Map data);

    /**
     *  set frameId
     */
    public void setFrameId(String frameId);
    
    /**
     *  set bAName
     */
    public void setBAName(String bAName);
}

        