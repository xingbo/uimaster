/* null */
/* auto generated constructor */
function org_shaolin_bmdp_workflow_form_SessionVarDefinition(json)
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

    var sessionServiceClassUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "sessionServiceClassUILabel"]
    });

    var sessionServiceClassUI = new UIMaster.ui.textfield
    ({
        ui: elementList[prefix + "sessionServiceClassUI"]
    });

    var sessionClassUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "sessionClassUILabel"]
    });

    var sessionClassUI = new UIMaster.ui.textfield
    ({
        ui: elementList[prefix + "sessionClassUI"]
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
        ,subComponents: [prefix + "nameUILabel",prefix + "nameUI",prefix + "sessionServiceClassUILabel",prefix + "sessionServiceClassUI",prefix + "sessionClassUILabel",prefix + "sessionClassUI"]
    });

    var Form = new UIMaster.ui.panel
    ({
        ui: elementList[prefix + "Form"]
        ,items: [nameUILabel,nameUI,sessionServiceClassUILabel,sessionServiceClassUI,sessionClassUILabel,sessionClassUI,okbtn,cancelbtn,fieldPanel,actionPanel]
    });

    Form.nameUILabel=nameUILabel;

    Form.nameUI=nameUI;

    Form.sessionServiceClassUILabel=sessionServiceClassUILabel;

    Form.sessionServiceClassUI=sessionServiceClassUI;

    Form.sessionClassUILabel=sessionClassUILabel;

    Form.sessionClassUI=sessionClassUI;

    Form.okbtn=okbtn;

    Form.cancelbtn=cancelbtn;

    Form.fieldPanel=fieldPanel;

    Form.actionPanel=actionPanel;

    Form.user_constructor = function()
    {
        /* Construct_FIRST:org_shaolin_bmdp_workflow_form_SessionVarDefinition */
        /* Construct_LAST:org_shaolin_bmdp_workflow_form_SessionVarDefinition */
    };

    Form.Save = org_shaolin_bmdp_workflow_form_SessionVarDefinition_Save;

    Form.Cancel = org_shaolin_bmdp_workflow_form_SessionVarDefinition_Cancel;

    Form.__entityName="org.shaolin.bmdp.workflow.form.SessionVarDefinition";

    Form.init();
    return Form;
};

    /* EventHandler Functions */
/* Other_Func_FIRST:org_shaolin_bmdp_workflow_form_SessionVarDefinition */
/* Other_Func_LAST:org_shaolin_bmdp_workflow_form_SessionVarDefinition */

    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_SessionVarDefinition_Save(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_SessionVarDefinition_Save */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"saveDetail-20150808-104357",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_SessionVarDefinition_Save */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_SessionVarDefinition_Cancel(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_SessionVarDefinition_Cancel */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"cancelDetail-20150808-104357",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_SessionVarDefinition_Cancel */



