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

    Form.addFlowNode = org_shaolin_bmdp_workflow_page_FlowManagement_addFlowNode;

    Form.saveWorkflow = org_shaolin_bmdp_workflow_page_FlowManagement_saveWorkflow;

    Form.refreshModuleGroup = org_shaolin_bmdp_workflow_page_FlowManagement_refreshModuleGroup;

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
    function org_shaolin_bmdp_workflow_page_FlowManagement_addFlowNode(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_page_FlowManagement_addFlowNode */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"addFlowNode543543",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_page_FlowManagement_addFlowNode */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_page_FlowManagement_saveWorkflow(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_page_FlowManagement_saveWorkflow */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"saveWorkflow432423",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_page_FlowManagement_saveWorkflow */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_page_FlowManagement_refreshModuleGroup(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_page_FlowManagement_refreshModuleGroup */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"refreshModuleGroup432423",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_page_FlowManagement_refreshModuleGroup */


    function org_shaolin_bmdp_workflow_page_FlowManagement_initPageJs(){/* Gen_First:org_shaolin_bmdp_workflow_page_FlowManagement_initPageJs */
        var constraint_result = true;
        var UIEntity = this;

    }/* Gen_Last:org_shaolin_bmdp_workflow_page_FlowManagement_initPageJs */


    function org_shaolin_bmdp_workflow_page_FlowManagement_finalizePageJs(){/* Gen_First:org_shaolin_bmdp_workflow_page_FlowManagement_finalizePageJs */

    }/* Gen_Last:org_shaolin_bmdp_workflow_page_FlowManagement_finalizePageJs */



