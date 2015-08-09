/* null */
/* auto generated constructor */
function org_shaolin_bmdp_workflow_form_TaskTable(json)
{
    var prefix = (typeof(json) == "string") ? json : json.prefix; 
    var parentIdUI = new UIMaster.ui.hidden
    ({
        ui: elementList[prefix + "parentIdUI"]
    });

    var itemTable = new UIMaster.ui.objectlist
    ({
        ui: elementList[prefix + "itemTable"]
        ,editable: true
    });

    var okbtn = new UIMaster.ui.button
    ({
        ui: elementList[prefix + "okbtn"]
    });

    var cancelbtn = new UIMaster.ui.button
    ({
        ui: elementList[prefix + "cancelbtn"]
    });

    var actionPanel = new UIMaster.ui.panel
    ({
        ui: elementList[prefix + "actionPanel"]
        ,items: []
        ,subComponents: [prefix + "okbtn",prefix + "cancelbtn"]
    });

    var fieldPanel = new UIMaster.ui.panel
    ({
        ui: elementList[prefix + "fieldPanel"]
        ,items: []
        ,subComponents: [prefix + "parentIdUI",prefix + "itemTable"]
    });

    var Form = new UIMaster.ui.panel
    ({
        ui: elementList[prefix + "Form"]
        ,items: [parentIdUI,itemTable,okbtn,cancelbtn,fieldPanel,actionPanel]
    });

    Form.parentIdUI=parentIdUI;

    Form.itemTable=itemTable;

    Form.okbtn=okbtn;

    Form.cancelbtn=cancelbtn;

    Form.fieldPanel=fieldPanel;

    Form.actionPanel=actionPanel;

    Form.user_constructor = function()
    {
        /* Construct_FIRST:org_shaolin_bmdp_workflow_form_TaskTable */
        /* Construct_LAST:org_shaolin_bmdp_workflow_form_TaskTable */
    };

    Form.createItem = org_shaolin_bmdp_workflow_form_TaskTable_createItem;

    Form.deleteItem = org_shaolin_bmdp_workflow_form_TaskTable_deleteItem;

    Form.Save = org_shaolin_bmdp_workflow_form_TaskTable_Save;

    Form.Cancel = org_shaolin_bmdp_workflow_form_TaskTable_Cancel;

    Form.__entityName="org.shaolin.bmdp.workflow.form.TaskTable";

    Form.init();
    return Form;
};

    /* EventHandler Functions */
/* Other_Func_FIRST:org_shaolin_bmdp_workflow_form_TaskTable */
/* Other_Func_LAST:org_shaolin_bmdp_workflow_form_TaskTable */

    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_TaskTable_createItem(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_TaskTable_createItem */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"createItem_20150809-191612",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_TaskTable_createItem */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_TaskTable_deleteItem(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_TaskTable_deleteItem */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"deleteItem_20150809-191612",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_TaskTable_deleteItem */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_TaskTable_Save(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_TaskTable_Save */
{this.itemTable.syncBodyDataToServer();}
        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"saveItem-20150809-191612",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_TaskTable_Save */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_TaskTable_Cancel(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_TaskTable_Cancel */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"cancelDetail-20150809-191612",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_TaskTable_Cancel */



