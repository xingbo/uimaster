/* null */
/* auto generated constructor */
function org_shaolin_bmdp_workflow_form_UIFlows(json)
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

    var flowUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "flowUILabel"]
    });

    var flowUI = new UIMaster.ui.textfield
    ({
        ui: elementList[prefix + "flowUI"]
    });

    var moduleItemIdUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "moduleItemIdUILabel"]
    });

    var moduleItemIdUI = new UIMaster.ui.textfield
    ({
        ui: elementList[prefix + "moduleItemIdUI"]
    });

    var moduleTypeUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "moduleTypeUILabel"]
    });

    var moduleTypeUI = new UIMaster.ui.combobox
    ({
        ui: elementList[prefix + "moduleTypeUI"]
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
        ,subComponents: [prefix + "idUI",prefix + "nameUILabel",prefix + "nameUI",prefix + "flowUILabel",prefix + "flowUI",prefix + "moduleItemIdUILabel",prefix + "moduleItemIdUI",prefix + "moduleTypeUILabel",prefix + "moduleTypeUI"]
    });

    var Form = new UIMaster.ui.panel
    ({
        ui: elementList[prefix + "Form"]
        ,items: [idUI,nameUILabel,nameUI,flowUILabel,flowUI,moduleItemIdUILabel,moduleItemIdUI,moduleTypeUILabel,moduleTypeUI,okbtn,cancelbtn,fieldPanel,actionPanel]
    });

    Form.idUI=idUI;

    Form.nameUILabel=nameUILabel;

    Form.nameUI=nameUI;

    Form.flowUILabel=flowUILabel;

    Form.flowUI=flowUI;

    Form.moduleItemIdUILabel=moduleItemIdUILabel;

    Form.moduleItemIdUI=moduleItemIdUI;

    Form.moduleTypeUILabel=moduleTypeUILabel;

    Form.moduleTypeUI=moduleTypeUI;

    Form.okbtn=okbtn;

    Form.cancelbtn=cancelbtn;

    Form.fieldPanel=fieldPanel;

    Form.actionPanel=actionPanel;

    Form.user_constructor = function()
    {
        /* Construct_FIRST:org_shaolin_bmdp_workflow_form_UIFlows */
        /* Construct_LAST:org_shaolin_bmdp_workflow_form_UIFlows */
    };

    Form.Save = org_shaolin_bmdp_workflow_form_UIFlows_Save;

    Form.Cancel = org_shaolin_bmdp_workflow_form_UIFlows_Cancel;

    Form.__entityName="org.shaolin.bmdp.workflow.form.UIFlows";

    Form.init();
    return Form;
};

    /* EventHandler Functions */
/* Other_Func_FIRST:org_shaolin_bmdp_workflow_form_UIFlows */
/* Other_Func_LAST:org_shaolin_bmdp_workflow_form_UIFlows */

    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_UIFlows_Save(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_UIFlows_Save */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"saveDetail217605319",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_UIFlows_Save */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_UIFlows_Cancel(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_UIFlows_Cancel */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"cancelDetail1051864807",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_UIFlows_Cancel */



