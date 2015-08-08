/* null */
/* auto generated constructor */
function org_shaolin_bmdp_workflow_form_CreateFlow(json)
{
    var prefix = (typeof(json) == "string") ? json : json.prefix; 
    var idUI = new UIMaster.ui.hidden
    ({
        ui: elementList[prefix + "idUI"]
    });

    var flowNameUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "flowNameUILabel"]
    });

    var flowNameUI = new UIMaster.ui.textfield
    ({
        ui: elementList[prefix + "flowNameUI"]
    });

    var variableLabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "variableLabel"]
    });

    var okbtn = new UIMaster.ui.button
    ({
        ui: elementList[prefix + "okbtn"]
    });

    var cancelbtn = new UIMaster.ui.button
    ({
        ui: elementList[prefix + "cancelbtn"]
    });

    var variableTable = new org_shaolin_bmdp_workflow_form_VariableDefinitionTable({"prefix":prefix + "variableTable."});

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
        ,subComponents: [prefix + "idUI",prefix + "flowNameUILabel",prefix + "flowNameUI",prefix + "variableLabel",prefix + "variableTable"]
    });

    var Form = new UIMaster.ui.panel
    ({
        ui: elementList[prefix + "Form"]
        ,items: [idUI,flowNameUILabel,flowNameUI,variableLabel,okbtn,cancelbtn,variableTable,fieldPanel,actionPanel]
    });

    Form.idUI=idUI;

    Form.flowNameUILabel=flowNameUILabel;

    Form.flowNameUI=flowNameUI;

    Form.variableLabel=variableLabel;

    Form.okbtn=okbtn;

    Form.cancelbtn=cancelbtn;

    Form.variableTable=variableTable;

    Form.fieldPanel=fieldPanel;

    Form.actionPanel=actionPanel;

    Form.user_constructor = function()
    {
        /* Construct_FIRST:org_shaolin_bmdp_workflow_form_CreateFlow */
        /* Construct_LAST:org_shaolin_bmdp_workflow_form_CreateFlow */
    };

    Form.Save = org_shaolin_bmdp_workflow_form_CreateFlow_Save;

    Form.Cancel = org_shaolin_bmdp_workflow_form_CreateFlow_Cancel;

    Form.__entityName="org.shaolin.bmdp.workflow.form.CreateFlow";

    Form.init();
    return Form;
};

    /* EventHandler Functions */
/* Other_Func_FIRST:org_shaolin_bmdp_workflow_form_CreateFlow */
/* Other_Func_LAST:org_shaolin_bmdp_workflow_form_CreateFlow */

    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_CreateFlow_Save(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_CreateFlow_Save */

        {   
            var constraint_result = this.Form.validate();
            if (constraint_result != true && constraint_result != null) {
                return false;
            }
        }
        {this.variableTable.itemTable.syncBodyDataToServer();}
        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"saveDetail-20150807-230249",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_CreateFlow_Save */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_CreateFlow_Cancel(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_CreateFlow_Cancel */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"cancelDetail-20150807-230249",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_CreateFlow_Cancel */



