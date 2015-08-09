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

    var tasksHistoryTable = new UIMaster.ui.objectlist
    ({
        ui: elementList[prefix + "tasksHistoryTable"]
    });

    var functionsTab = new UIMaster.ui.tab
    ({
        ui: elementList[prefix + "functionsTab"]
    });

    var Form = new UIMaster.ui.panel
    ({
        ui: elementList[prefix + "Form"]
        ,items: [CENameUI,tasksTable,tasksHistoryTable,functionsTab]
    });

    Form.CENameUI=CENameUI;

    Form.tasksTable=tasksTable;

    Form.tasksHistoryTable=tasksHistoryTable;

    Form.functionsTab=functionsTab;

    Form.user_constructor = function()
    {
        /* Construct_FIRST:org_shaolin_bmdp_workflow_page_TasksManager */
        /* Construct_LAST:org_shaolin_bmdp_workflow_page_TasksManager */
    };

    Form.createTask = org_shaolin_bmdp_workflow_page_TasksManager_createTask;

    Form.openTask = org_shaolin_bmdp_workflow_page_TasksManager_openTask;

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
    function org_shaolin_bmdp_workflow_page_TasksManager_createTask(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_page_TasksManager_createTask */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"showBlanktaskInfoPanel",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_page_TasksManager_createTask */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_page_TasksManager_openTask(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_page_TasksManager_openTask */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"openTask-20150809-2009",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_page_TasksManager_openTask */


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



