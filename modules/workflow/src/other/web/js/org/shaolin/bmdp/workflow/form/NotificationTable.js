/* null */
/* auto generated constructor */
function org_shaolin_bmdp_workflow_form_NotificationTable(json)
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
        /* Construct_FIRST:org_shaolin_bmdp_workflow_form_NotificationTable */
        /* Construct_LAST:org_shaolin_bmdp_workflow_form_NotificationTable */
    };

    Form.createItem = org_shaolin_bmdp_workflow_form_NotificationTable_createItem;

    Form.deleteItem = org_shaolin_bmdp_workflow_form_NotificationTable_deleteItem;

    Form.Save = org_shaolin_bmdp_workflow_form_NotificationTable_Save;

    Form.Cancel = org_shaolin_bmdp_workflow_form_NotificationTable_Cancel;

    Form.__entityName="org.shaolin.bmdp.workflow.form.NotificationTable";

    Form.init();
    return Form;
};

    /* EventHandler Functions */
/* Other_Func_FIRST:org_shaolin_bmdp_workflow_form_NotificationTable */
/* Other_Func_LAST:org_shaolin_bmdp_workflow_form_NotificationTable */

    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_NotificationTable_createItem(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_NotificationTable_createItem */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"createItem_20150809-191613",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_NotificationTable_createItem */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_NotificationTable_deleteItem(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_NotificationTable_deleteItem */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"deleteItem_20150809-191613",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_NotificationTable_deleteItem */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_NotificationTable_Save(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_NotificationTable_Save */
{this.itemTable.syncBodyDataToServer();}
        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"saveItem-20150809-191613",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_NotificationTable_Save */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_NotificationTable_Cancel(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_NotificationTable_Cancel */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"cancelDetail-20150809-191613",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_NotificationTable_Cancel */



