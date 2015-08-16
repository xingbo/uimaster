/* null */
/* auto generated constructor */
function org_shaolin_bmdp_workflow_page_FlowManagement(json)
{
    var prefix = (typeof(json) == "string") ? json : json.prefix; 
    var flowDiagram = new UIMaster.ui.flow
    ({
        ui: elementList[prefix + "flowDiagram"]
    });

    var functionTree = new UIMaster.ui.webtree
    ({
        ui: elementList[prefix + "functionTree"]
    });

    var propertiesPanel = new UIMaster.ui.panel
    ({
        ui: elementList[prefix + "propertiesPanel"]
        ,items: []
        ,subComponents: [prefix + "functionTree"]
    });

    var topPanel = new UIMaster.ui.panel
    ({
        ui: elementList[prefix + "topPanel"]
        ,items: []
        ,subComponents: [prefix + "flowDiagram",prefix + "propertiesPanel"]
    });

    var Form = new UIMaster.ui.panel
    ({
        ui: elementList[prefix + "Form"]
        ,items: [flowDiagram,functionTree,topPanel,propertiesPanel]
    });

    Form.flowDiagram=flowDiagram;

    Form.functionTree=functionTree;

    Form.topPanel=topPanel;

    Form.propertiesPanel=propertiesPanel;

    Form.user_constructor = function()
    {
        /* Construct_FIRST:org_shaolin_bmdp_workflow_page_FlowManagement */
        /* Construct_LAST:org_shaolin_bmdp_workflow_page_FlowManagement */
    };

    Form.newWorkflow = org_shaolin_bmdp_workflow_page_FlowManagement_newWorkflow;

    Form.openWorkflow = org_shaolin_bmdp_workflow_page_FlowManagement_openWorkflow;

    Form.addFlow = org_shaolin_bmdp_workflow_page_FlowManagement_addFlow;

    Form.editFlow = org_shaolin_bmdp_workflow_page_FlowManagement_editFlow;

    Form.removeFlow = org_shaolin_bmdp_workflow_page_FlowManagement_removeFlow;

    Form.refreshFlow = org_shaolin_bmdp_workflow_page_FlowManagement_refreshFlow;

    Form.addMissionNode = org_shaolin_bmdp_workflow_page_FlowManagement_addMissionNode;

    Form.addStartNode = org_shaolin_bmdp_workflow_page_FlowManagement_addStartNode;

    Form.addEndNode = org_shaolin_bmdp_workflow_page_FlowManagement_addEndNode;

    Form.saveWorkflow = org_shaolin_bmdp_workflow_page_FlowManagement_saveWorkflow;

    Form.refreshModuleGroup = org_shaolin_bmdp_workflow_page_FlowManagement_refreshModuleGroup;

    Form.showXmlEditor = org_shaolin_bmdp_workflow_page_FlowManagement_showXmlEditor;

    Form.initPageJs = org_shaolin_bmdp_workflow_page_FlowManagement_initPageJs;

    Form.finalizePageJs = org_shaolin_bmdp_workflow_page_FlowManagement_finalizePageJs;

    Form.__AJAXSubmit = false;
    
    Form.__entityName="org.shaolin.bmdp.workflow.page.FlowManagement";

    Form.init();
    return Form;
};

    /* EventHandler Functions */
/* Other_Func_FIRST:org_shaolin_bmdp_workflow_page_FlowManagement */
/* Other_Func_LAST:org_shaolin_bmdp_workflow_page_FlowManagement */

    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_page_FlowManagement_newWorkflow(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_page_FlowManagement_newWorkflow */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"newWorkflow35433",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_page_FlowManagement_newWorkflow */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_page_FlowManagement_openWorkflow(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_page_FlowManagement_openWorkflow */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"openWorkflow543543",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_page_FlowManagement_openWorkflow */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_page_FlowManagement_addFlow(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_page_FlowManagement_addFlow */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"addFlow543543",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_page_FlowManagement_addFlow */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_page_FlowManagement_editFlow(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_page_FlowManagement_editFlow */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"editFlow-20150816-1118",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_page_FlowManagement_editFlow */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_page_FlowManagement_removeFlow(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_page_FlowManagement_removeFlow */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"removeFlow-20150809-1101",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_page_FlowManagement_removeFlow */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_page_FlowManagement_refreshFlow(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_page_FlowManagement_refreshFlow */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"refreshFlow-20150809-1101",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_page_FlowManagement_refreshFlow */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_page_FlowManagement_addMissionNode(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_page_FlowManagement_addMissionNode */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"addMissionNode_20150808-104357",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_page_FlowManagement_addMissionNode */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_page_FlowManagement_addStartNode(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_page_FlowManagement_addStartNode */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"addStartNode_20150808-104357",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_page_FlowManagement_addStartNode */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_page_FlowManagement_addEndNode(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_page_FlowManagement_addEndNode */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"addEndNode_20150808-104357",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_page_FlowManagement_addEndNode */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_page_FlowManagement_saveWorkflow(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_page_FlowManagement_saveWorkflow */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"saveWorkflow432423",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_page_FlowManagement_saveWorkflow */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_page_FlowManagement_refreshModuleGroup(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_page_FlowManagement_refreshModuleGroup */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"refreshModuleGroup-201506182322",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_page_FlowManagement_refreshModuleGroup */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_page_FlowManagement_showXmlEditor(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_page_FlowManagement_showXmlEditor */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"showXmlEditor-201508081054",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_page_FlowManagement_showXmlEditor */


    function org_shaolin_bmdp_workflow_page_FlowManagement_initPageJs(){/* Gen_First:org_shaolin_bmdp_workflow_page_FlowManagement_initPageJs */
        var constraint_result = true;
        var UIEntity = this;

    }/* Gen_Last:org_shaolin_bmdp_workflow_page_FlowManagement_initPageJs */


    function org_shaolin_bmdp_workflow_page_FlowManagement_finalizePageJs(){/* Gen_First:org_shaolin_bmdp_workflow_page_FlowManagement_finalizePageJs */

    }/* Gen_Last:org_shaolin_bmdp_workflow_page_FlowManagement_finalizePageJs */



