/* null */
/* auto generated constructor */
function org_shaolin_bmdp_workflow_form_VariableDefinitionTable(json)
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

    var fieldPanel = new UIMaster.ui.panel
    ({
        ui: elementList[prefix + "fieldPanel"]
        ,items: []
        ,subComponents: [prefix + "parentIdUI",prefix + "itemTable"]
    });

    var Form = new UIMaster.ui.panel
    ({
        ui: elementList[prefix + "Form"]
        ,items: [parentIdUI,itemTable,fieldPanel]
    });

    Form.parentIdUI=parentIdUI;

    Form.itemTable=itemTable;

    Form.fieldPanel=fieldPanel;

    Form.user_constructor = function()
    {
        /* Construct_FIRST:org_shaolin_bmdp_workflow_form_VariableDefinitionTable */
        /* Construct_LAST:org_shaolin_bmdp_workflow_form_VariableDefinitionTable */
    };

    Form.createItem = org_shaolin_bmdp_workflow_form_VariableDefinitionTable_createItem;

    Form.deleteItem = org_shaolin_bmdp_workflow_form_VariableDefinitionTable_deleteItem;

    Form.__entityName="org.shaolin.bmdp.workflow.form.VariableDefinitionTable";

    Form.init();
    return Form;
};

    /* EventHandler Functions */
/* Other_Func_FIRST:org_shaolin_bmdp_workflow_form_VariableDefinitionTable */
/* Other_Func_LAST:org_shaolin_bmdp_workflow_form_VariableDefinitionTable */

    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_VariableDefinitionTable_createItem(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_VariableDefinitionTable_createItem */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"createItem_20150807-230248",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_VariableDefinitionTable_createItem */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_VariableDefinitionTable_deleteItem(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_VariableDefinitionTable_deleteItem */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"deleteItem_20150807-230248",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_VariableDefinitionTable_deleteItem */



