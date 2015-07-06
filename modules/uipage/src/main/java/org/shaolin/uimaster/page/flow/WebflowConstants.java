package org.shaolin.uimaster.page.flow;

/**
 * Constants for webflow
 *
 */
public interface WebflowConstants
{

    /**
     * The request attribute/parameter name under which the name of our
     * source webchunk  is normally stored.
     * The value of this constant is "_chunkname".
     */
    public static final String SOURCE_CHUNK_NAME =
            "_chunkname";

    /**
     * The request attribute/parameter name under which the name of our
     * source WebNode  is normally stored.
     * The value of this constant is "_nodename".
     */
    public static final String SOURCE_NODE_NAME =
            "_nodename";

    /**
     * The request parameter/attribute name under which the name of out
     *  is normally stored, for Logic Node and Display Node.
     * The value of this constant is "_outname".
     */
    public static final String OUT_NAME =
            "_outname";

    /**
     * The request parameter name under which the name of frame
     *  is normally stored, for UIPage Display Node which contains frames.
     * The value of this constant is "_framename".
     */
    public static final String FRAME_NAME =
            "_framename";

        /**
         * the self target
         */
        public static final String TARGET_SELF =
                "_self";


    /**
     * The request parameter/attribute name under which the name of our
     * source entity corresponding to the Display Node  is normally stored.
     * for Display Node.
     * The value of this constant is "_pagename".
     */
    public static final String SOURCE_ENTITY_NAME =
            "_pagename";

    /**
     * The request parameter/attribute name under which the name of our
     * dest webchunk  is normally stored
     * The value of this constant is "_destchunkname".
     */
    public static final String DEST_CHUNK_NAME =
            "_destchunkname";

    /**
     * The request parameter/attribute name under which the name of our
     * dest WebNode  is normally stored.
     * The value of this constant is "_destnodename".
     */
    public static final String DEST_NODE_NAME =
            "_destnodename";

        /**
         * The request attribute name under which the name of our
         * dest url  is normally stored
         * The value of this constant is "_desturl".
         */
   public static final String DEST_URL =
                "_desturl";

   public static final String FORM_URL = 
		   		"_formaction";
   
   /**
    * Last menu requested.
    * The value of this constant is "_lastmenu_".
    */
   public static final String LAST_MENU =
                "_lastmenu_";

   public static final String AJAX_COMP_MAP = "_ajax_page_component_map";
   
    /**
     * The request attribute name under which the flag of attribute
     *  is normally stored. this flag indicates whether the source node info is stored in request attribute scope
     * The value of this constant is "_attributeFlag".
     */
    public static final String ATTRIBUTE_FLAG =
            "_attributeFlag";

    /**
     * The request attributes key under which your servlet should store an
     * <code>bmiasia.ebos.webflow.exception.WebflowErrors</code>
     * object
     * The value of this constant is "bmiasia.ebos.webflow.engine.ERROR".
     */
    public static final String ERROR_KEY =
            "org.shaolin.uimaster.page.flow.ERROR";


    public static final String USER_SESSION_KEY = "CurrentUserSession";

    public static final String USER_LOCALE_KEY = "CurrentUserLocale";
    
    public static final String USER_ROLE_KEY = "CurrentUserRole";
    
    public static final String INDEX_PAGE_VISITED = "indexPageVisited";

    /*
    * stores the current of server name in UserContext
    */
    public static final String SERVER_NAME_KEY = "CurrentServerName";
    
	public static final String PASSWORD_KEY = "PASSWORD";

    /**
     * The session context attributes key under which our
     * initiated webchunks List <code>java.lang.ArrayList</code> object
     * is normally stored.
     *
     * The value of this constant is "CurrentUser".
     */
    public static final String INITIATED_CHUNKS_KEY = "_initedChunksList";

    
    public static final String QUIT_ACTION_FLAG_KEY = "QuitActionFlag";
    
    /**
     *  the separator for files in web.xml, used by the init parameter "config"
     *
     *  it's not used.
     */
    public static final String SEPARATOR = ",";

    /**
     *  the prefix tag of request context
     * The value of this constant is "$".
     */
    public static final String REQUEST_PARSING_CONTEXT_PREFIX = "$";

    /**
     *  the prefix tag of session context
     * The value of this constant is "@".
     */
    public static final String SESSION_PARSING_CONTEXT_PREFIX = "@";

    /**
     *  the prefix tag of session context for dest chunk in OpGetChunkGlobalVariable
     * The value of this constant is "#".
     */
    public static final String DESTCHUNK_SESSION_PARSING_CONTEXT_PREFIX = "#";


    /**
     *  the jndi name of user transaction
     *
     *  The value of this constant is "java:comp/UserTransaction".
     */
    public static final String JNDI_USERTRANSACTION = "java:comp/UserTransaction";

    /**
     *  the separator of the logicname resourcename: chunkname + separator + nodename
     *  The value of this constant is ".".
     */
    public static final String SEPARATOR_RESOURCENAME = ".";


    //constant for webflow manager servlet
    /**
     *  the key of webflow manager servlet url in client.properties
     *  The value of this constant is "webflowManagerUrl".
     */
    public static final String URL_WEBFLOWMANAGER_SERVLET = "webflowManagerUrl";
    /**
     *  the name of parameter "command" for webflow manager servlet
     *  The value of this constant is "command".
     */
    public static final String PARAMETER_COMMAND = "command";
    /**
     *  the "add" value of parameter command for webflow manager servlet
     *  The value of this constant is "add".
     */
    public static final String COMMAND_ADD = "add";
    /**
     *  the "remove" value of parameter command for webflow manager servlet
     *  The value of this constant is "remove".
     */
    public static final String COMMAND_REMOVE = "remove";
    /**
     *  the "reloadAll" value of parameter command for webflow manager servlet
     *  The value of this constant is "reloadAll".
     */
    public static final String COMMAND_RELOADALL = "reloadAll";
    /**
     *  the name of parameter "entityname" for webflow manager servlet
     *  The value of this constant is "entityname".
     */
    public static final String PARAMETER_ENTITYNAME = "entityname";
    /**
     *  the name of parameter "xmldata" for webflow manager servlet
     *  The value of this constant is "xmldata".
     */
    public static final String PARAMETER_XMLDATA = "xmldata";

    /**
     * The request attributes key under which the outdata MappingToNode of previous node should store an
     * <code>java.util.Map</code> object. Map: key-variable name value-object
     * The value of this constant is "_outDataMapping2Node".
     */
    public static final String OUTDATA_MAPPING2NODE_KEY = "_outDataMapping2Node";

    /**
     * The session attributes key under which the outdata MappingToChunk of previous chunk should store an
     * <code>java.util.Map</code> object. Map: key-variable name value-object
     * The value of this constant is "_outDataMapping2Chunk".
     */
    public static final String OUTDATA_MAPPING2CHUNK_KEY = "_outDataMapping2Chunk";

    /**
     * The request attributes key under which the user transaction should store an
     * <code>javax.transaction.UserTransaction</code> object. used when transaction spans multi-logicnode
     * The value of this constant is "_userTransaction".
     */
    public static final String USERTRANSACTION_KEY = "_userTransaction";
    
    /**
    * The timezone offset of the client.
    * <code>java.lang.String</code> object.
    * The value of this constant is "_clientTimeZoneOffset".
    */
    public static final String CLIENT_TIMEZONE_OFFSET = "_clientTimeZoneOffset";
    
    
    /**
     * The request parameter name under which the
     * identifier for whether this request is submitted by
     * ajax method or not.
     * 
     * The value of this constant is "_htmlkey".
     */
    public static final String AJAX_SUBMIT_FLAG = "_ajaxSubmit";

    public static final String AJAX_PRESUBMIT_DATA = "_ajaxPresubmitData";
    
    /**
     * The <code>Session</code> attribute name under which the
     * next html page's content is normally stored.
     * 
     * The value of this constant is "_htmlkey".
     */
    public static final String TEMP_RESPONSE_DATA = "_htmlData";
    
    /**
     * The <code>Session</code> attribute name under which the
     * next html page's generated time is normally stored. It will
     * be used for check the validity for the request that just for
     * fetching html page's content.
     * 
     * The value of this constant is "_htmlkey".
     */
    public static final String TEMP_RESPONSE_KEY = "_htmlkey";
    
    /**
     * The <code>Request</code> attribute name under which the
     * prompt information after a successful operation is normally stored.
     * 
     * The value of this constant is "_postCommitTip".
     */
    public static final String POST_COMMIT_TIP = "_postCommitTip";
    
    
    public static final String ERROR_DATA_FIELD_SCOPE = "field";
    
    public static final String ERROR_DATA_SECTION_SCOPE = "section";
    
    public static final String ERROR_DATA_PAGE_SCOPE = "page";
    
    
    public static final String ERROR_DATA_FIELD_ATTACHNODE_DEFAULT = "field-show";
    
    public static final String ERROR_DATA_SECTION_ATTACHNODE_DEFAULT = "section-show";
    
    public static final String ERROR_DATA_PAGE_ATTACHNODE_DEFAULT = "page-show";
    
    
    public static final String ERROR_DATA_FIELD_STYLE_DEFAULT = "ui-field-err";
    
    public static final String ERROR_DATA_SECTION_STYLE_DEFAULT = "ui-section-err";
    
    public static final String ERROR_DATA_PAGE_STYLE_DEFAULT = "ui-page-err";
    
    
    public static final String DEFAULT_ERRORHANDLER_PAGE = "/jsp/common/Failure.jsp";

	public static final String BACK_CHUNK_NAME = "_backChunkName";
	public static final String BACK_CHUNK_NODE = "_backChunkNode";
	public static final String START_WEBFLOW_NODE_CALL_FLAG = "_startWebflowNodeCallFlag";
	public static final String KEY_AJAXSUBMIT_LOCAL_VARIABLE = "_ajaxSubmit_mappingData_to_JSPPage";
	public static final String KEY_WEBFLOW_CONTAINER = "_webflowMode";
	public static final String WEBFLOW_CONTAINER_PORTLET = "Portlet";
	public static final String WEBFLOW_CONTAINER_SERVLET = "Servlet";
    public static final String KEY_PORTLETURL = "_PortletURL";
    public static final String KEY_UIPAGE_PATH = "_UIPageJSPPath";
    public static final String KEY_PORTLET_ID = "_PortletId";
    public static final String KEY_PORTLET_RESTART_FLAG = "_NeedRestart";
    public static final String KEY_FORWARD_TO_SERVLET = "_ForwardToServlet";
    public static final String KEY_UIPAGE_ENTITY_NAME = "uipangeEntityName";
    public static final String KEY_RESOURCE_MAP = "_resourceMap";

}
