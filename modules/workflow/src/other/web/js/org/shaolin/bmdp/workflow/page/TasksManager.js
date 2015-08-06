/* null */
/* auto generated constructor */
function org_shaolin_bmdp_workflow_page_TasksManager(json)
{
    var prefix = (typeof(json) == "string") ? json : json.prefix; 
    var CENameUI = new UIMaster.ui.hidden
    ({
        ui: elementList[prefix + "CENameUI"]
    });

    var tasksTable = new UIMaster.ui.objectlist
    ({
        ui: elementList[prefix + "tasksTable"]
    });

    var constantInfoPanel = new UIMaster.ui.panel
    ({
        ui: elementList[prefix + "constantInfoPanel"]
        ,items: []
        ,subComponents: [prefix + "tasksTable"]
    });

    var Form = new UIMaster.ui.panel
    ({
        ui: elementList[prefix + "Form"]
        ,items: [CENameUI,tasksTable,constantInfoPanel]
    });

    Form.CENameUI=CENameUI;

    Form.tasksTable=tasksTable;

    Form.constantInfoPanel=constantInfoPanel;

    Form.user_constructor = function()
    {
        /* Construct_FIRST:org_shaolin_bmdp_workflow_page_TasksManager */
        /* Construct_LAST:org_shaolin_bmdp_workflow_page_TasksManager */
    };

    Form.createCE = org_shaolin_bmdp_workflow_page_TasksManager_createCE;

    Form.openCEDetail = org_shaolin_bmdp_workflow_page_TasksManager_openCEDetail;

    Form.deleteUser = org_shaolin_bmdp_workflow_page_TasksManager_deleteUser;

    Form.initPageJs = org_shaolin_bmdp_workflow_page_TasksManager_initPageJs;

    Form.finalizePageJs = org_shaolin_bmdp_workflow_page_TasksManager_finalizePageJs;

    Form.__AJAXSubmit = false;
    
    Form.__entityName="org.shaolin.bmdp.workflow.page.TasksManager";

    Form.init();
    return Form;
};

    /* EventHandler Functions */
/* Other_Func_FIRST:org_shaolin_bmdp_workflow_page_TasksManager */
/* Other_Func_LAST:org_shaolin_bmdp_workflow_page_TasksManager */

    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_page_TasksManager_createCE(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_page_TasksManager_createCE */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"showBlankconstantInfoPanel",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_page_TasksManager_createCE */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_page_TasksManager_openCEDetail(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_page_TasksManager_openCEDetail */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"showconstantInfoPanel",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_page_TasksManager_openCEDetail */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_page_TasksManager_deleteUser(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_page_TasksManager_deleteUser */

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_page_TasksManager_deleteUser */


    function org_shaolin_bmdp_workflow_page_TasksManager_initPageJs(){/* Gen_First:org_shaolin_bmdp_workflow_page_TasksManager_initPageJs */
        var constraint_result = true;
        var UIEntity = this;

    }/* Gen_Last:org_shaolin_bmdp_workflow_page_TasksManager_initPageJs */


    function org_shaolin_bmdp_workflow_page_TasksManager_finalizePageJs(){/* Gen_First:org_shaolin_bmdp_workflow_page_TasksManager_finalizePageJs */

    }/* Gen_Last:org_shaolin_bmdp_workflow_page_TasksManager_finalizePageJs */



