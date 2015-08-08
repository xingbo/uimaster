/* null */
/* auto generated constructor */
function org_shaolin_bmdp_workflow_form_VariableDefinition(json)
{
    var prefix = (typeof(json) == "string") ? json : json.prefix; 
    var nameUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "nameUILabel"]
    });

    var nameUI = new UIMaster.ui.textfield
    ({
        ui: elementList[prefix + "nameUI"]
    });

    var varTypeUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "varTypeUILabel"]
    });

    var varTypeUI = new UIMaster.ui.textfield
    ({
        ui: elementList[prefix + "varTypeUI"]
    });

    var classTypeUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "classTypeUILabel"]
    });

    var classTypeUI = new UIMaster.ui.textfield
    ({
        ui: elementList[prefix + "classTypeUI"]
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
        ,subComponents: [prefix + "nameUILabel",prefix + "nameUI",prefix + "varTypeUILabel",prefix + "varTypeUI",prefix + "classTypeUILabel",prefix + "classTypeUI"]
    });

    var Form = new UIMaster.ui.panel
    ({
        ui: elementList[prefix + "Form"]
        ,items: [nameUILabel,nameUI,varTypeUILabel,varTypeUI,classTypeUILabel,classTypeUI,okbtn,cancelbtn,fieldPanel,actionPanel]
    });

    Form.nameUILabel=nameUILabel;

    Form.nameUI=nameUI;

    Form.varTypeUILabel=varTypeUILabel;

    Form.varTypeUI=varTypeUI;

    Form.classTypeUILabel=classTypeUILabel;

    Form.classTypeUI=classTypeUI;

    Form.okbtn=okbtn;

    Form.cancelbtn=cancelbtn;

    Form.fieldPanel=fieldPanel;

    Form.actionPanel=actionPanel;

    Form.user_constructor = function()
    {
        /* Construct_FIRST:org_shaolin_bmdp_workflow_form_VariableDefinition */
        /* Construct_LAST:org_shaolin_bmdp_workflow_form_VariableDefinition */
    };

    Form.Save = org_shaolin_bmdp_workflow_form_VariableDefinition_Save;

    Form.Cancel = org_shaolin_bmdp_workflow_form_VariableDefinition_Cancel;

    Form.__entityName="org.shaolin.bmdp.workflow.form.VariableDefinition";

    Form.init();
    return Form;
};

    /* EventHandler Functions */
/* Other_Func_FIRST:org_shaolin_bmdp_workflow_form_VariableDefinition */
/* Other_Func_LAST:org_shaolin_bmdp_workflow_form_VariableDefinition */

    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_VariableDefinition_Save(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_VariableDefinition_Save */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"saveDetail-20150808-104357",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_VariableDefinition_Save */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_VariableDefinition_Cancel(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_VariableDefinition_Cancel */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"cancelDetail-20150808-104357",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_VariableDefinition_Cancel */



