/* null */
/* auto generated constructor */
function org_shaolin_bmdp_workflow_form_CreateMissionNode(json)
{
    var prefix = (typeof(json) == "string") ? json : json.prefix; 
    var idUI = new UIMaster.ui.hidden
    ({
        ui: elementList[prefix + "idUI"]
    });

    var nameUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "nameUILabel"]
    });

    var nameUI = new UIMaster.ui.textfield
    ({
        ui: elementList[prefix + "nameUI"]
    });

    var expressionUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "expressionUILabel"]
    });

    var expressionUI = new UIMaster.ui.textarea
    ({
        ui: elementList[prefix + "expressionUI"]
    });

    var expiredDaysUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "expiredDaysUILabel"]
    });

    var expiredDaysUI = new UIMaster.ui.textfield
    ({
        ui: elementList[prefix + "expiredDaysUI"]
    });

    var expiredHoursUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "expiredHoursUILabel"]
    });

    var expiredHoursUI = new UIMaster.ui.textfield
    ({
        ui: elementList[prefix + "expiredHoursUI"]
    });

    var partyTypeUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "partyTypeUILabel"]
    });

    var partyTypeUI = new UIMaster.ui.textfield
    ({
        ui: elementList[prefix + "partyTypeUI"]
    });

    var actionPageUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "actionPageUILabel"]
    });

    var actionPageUI = new UIMaster.ui.textfield
    ({
        ui: elementList[prefix + "actionPageUI"]
    });

    var actionPositionUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "actionPositionUILabel"]
    });

    var actionPositionUI = new UIMaster.ui.textfield
    ({
        ui: elementList[prefix + "actionPositionUI"]
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
        ,subComponents: [prefix + "idUI",prefix + "nameUILabel",prefix + "nameUI",prefix + "expressionUILabel",prefix + "expressionUI",prefix + "expiredDaysUILabel",prefix + "expiredDaysUI",prefix + "expiredHoursUILabel",prefix + "expiredHoursUI",prefix + "partyTypeUILabel",prefix + "partyTypeUI",prefix + "actionPageUILabel",prefix + "actionPageUI",prefix + "actionPositionUILabel",prefix + "actionPositionUI"]
    });

    var Form = new UIMaster.ui.panel
    ({
        ui: elementList[prefix + "Form"]
        ,items: [idUI,nameUILabel,nameUI,expressionUILabel,expressionUI,expiredDaysUILabel,expiredDaysUI,expiredHoursUILabel,expiredHoursUI,partyTypeUILabel,partyTypeUI,actionPageUILabel,actionPageUI,actionPositionUILabel,actionPositionUI,okbtn,cancelbtn,fieldPanel,actionPanel]
    });

    Form.idUI=idUI;

    Form.nameUILabel=nameUILabel;

    Form.nameUI=nameUI;

    Form.expressionUILabel=expressionUILabel;

    Form.expressionUI=expressionUI;

    Form.expiredDaysUILabel=expiredDaysUILabel;

    Form.expiredDaysUI=expiredDaysUI;

    Form.expiredHoursUILabel=expiredHoursUILabel;

    Form.expiredHoursUI=expiredHoursUI;

    Form.partyTypeUILabel=partyTypeUILabel;

    Form.partyTypeUI=partyTypeUI;

    Form.actionPageUILabel=actionPageUILabel;

    Form.actionPageUI=actionPageUI;

    Form.actionPositionUILabel=actionPositionUILabel;

    Form.actionPositionUI=actionPositionUI;

    Form.okbtn=okbtn;

    Form.cancelbtn=cancelbtn;

    Form.fieldPanel=fieldPanel;

    Form.actionPanel=actionPanel;

    Form.user_constructor = function()
    {
        /* Construct_FIRST:org_shaolin_bmdp_workflow_form_CreateMissionNode */
        /* Construct_LAST:org_shaolin_bmdp_workflow_form_CreateMissionNode */
    };

    Form.Save = org_shaolin_bmdp_workflow_form_CreateMissionNode_Save;

    Form.Cancel = org_shaolin_bmdp_workflow_form_CreateMissionNode_Cancel;

    Form.__entityName="org.shaolin.bmdp.workflow.form.CreateMissionNode";

    Form.init();
    return Form;
};

    /* EventHandler Functions */
/* Other_Func_FIRST:org_shaolin_bmdp_workflow_form_CreateMissionNode */
/* Other_Func_LAST:org_shaolin_bmdp_workflow_form_CreateMissionNode */

    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_CreateMissionNode_Save(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_CreateMissionNode_Save */

        {   
            var constraint_result = this.Form.validate();
            if (constraint_result != true && constraint_result != null) {
                return false;
            }
        }
        
        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"saveDetail-20150807-230249",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_CreateMissionNode_Save */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_CreateMissionNode_Cancel(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_CreateMissionNode_Cancel */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"cancelDetail-20150807-230249",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_CreateMissionNode_Cancel */



