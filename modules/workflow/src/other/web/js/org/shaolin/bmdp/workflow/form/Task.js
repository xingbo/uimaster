/* null */
/* auto generated constructor */
function org_shaolin_bmdp_workflow_form_Task(json)
{
    var prefix = (typeof(json) == "string") ? json : json.prefix; 
    var partyIdUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "partyIdUILabel"]
    });

    var partyIdUI = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "partyIdUI"]
    });

    var subjectUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "subjectUILabel"]
    });

    var subjectUI = new UIMaster.ui.textfield
    ({
        ui: elementList[prefix + "subjectUI"]
    });

    var descriptionUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "descriptionUILabel"]
    });

    var descriptionUI = new UIMaster.ui.textarea
    ({
        ui: elementList[prefix + "descriptionUI"]
    });

    var expiredTimeUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "expiredTimeUILabel"]
    });

    var expiredTimeUI = new UIMaster.ui.calendar
    ({
        ui: elementList[prefix + "expiredTimeUI"]
        ,isDataOnly: false
    });

    var sendSMSUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "sendSMSUILabel"]
    });

    var sendSMSUI = new UIMaster.ui.checkbox
    ({
        ui: elementList[prefix + "sendSMSUI"]
    });

    var sendEmailUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "sendEmailUILabel"]
    });

    var sendEmailUI = new UIMaster.ui.checkbox
    ({
        ui: elementList[prefix + "sendEmailUI"]
    });

    var statusUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "statusUILabel"]
    });

    var statusUI = new UIMaster.ui.combobox
    ({
        ui: elementList[prefix + "statusUI"]
    });

    var completeRateUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "completeRateUILabel"]
    });

    var completeRateUI = new UIMaster.ui.textfield
    ({
        ui: elementList[prefix + "completeRateUI"]
    });

    var priorityUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "priorityUILabel"]
    });

    var priorityUI = new UIMaster.ui.combobox
    ({
        ui: elementList[prefix + "priorityUI"]
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
        ,subComponents: [prefix + "partyIdUILabel",prefix + "partyIdUI",prefix + "subjectUILabel",prefix + "subjectUI",prefix + "descriptionUILabel",prefix + "descriptionUI",prefix + "expiredTimeUILabel",prefix + "expiredTimeUI",prefix + "sendSMSUILabel",prefix + "sendSMSUI",prefix + "sendEmailUILabel",prefix + "sendEmailUI",prefix + "statusUILabel",prefix + "statusUI",prefix + "completeRateUILabel",prefix + "completeRateUI",prefix + "priorityUILabel",prefix + "priorityUI"]
    });

    var Form = new UIMaster.ui.panel
    ({
        ui: elementList[prefix + "Form"]
        ,items: [partyIdUILabel,partyIdUI,subjectUILabel,subjectUI,descriptionUILabel,descriptionUI,expiredTimeUILabel,expiredTimeUI,sendSMSUILabel,sendSMSUI,sendEmailUILabel,sendEmailUI,statusUILabel,statusUI,completeRateUILabel,completeRateUI,priorityUILabel,priorityUI,okbtn,cancelbtn,fieldPanel,actionPanel]
    });

    Form.partyIdUILabel=partyIdUILabel;

    Form.partyIdUI=partyIdUI;

    Form.subjectUILabel=subjectUILabel;

    Form.subjectUI=subjectUI;

    Form.descriptionUILabel=descriptionUILabel;

    Form.descriptionUI=descriptionUI;

    Form.expiredTimeUILabel=expiredTimeUILabel;

    Form.expiredTimeUI=expiredTimeUI;

    Form.sendSMSUILabel=sendSMSUILabel;

    Form.sendSMSUI=sendSMSUI;

    Form.sendEmailUILabel=sendEmailUILabel;

    Form.sendEmailUI=sendEmailUI;

    Form.statusUILabel=statusUILabel;

    Form.statusUI=statusUI;

    Form.completeRateUILabel=completeRateUILabel;

    Form.completeRateUI=completeRateUI;

    Form.priorityUILabel=priorityUILabel;

    Form.priorityUI=priorityUI;

    Form.okbtn=okbtn;

    Form.cancelbtn=cancelbtn;

    Form.fieldPanel=fieldPanel;

    Form.actionPanel=actionPanel;

    Form.user_constructor = function()
    {
        /* Construct_FIRST:org_shaolin_bmdp_workflow_form_Task */
        /* Construct_LAST:org_shaolin_bmdp_workflow_form_Task */
    };

    Form.Save = org_shaolin_bmdp_workflow_form_Task_Save;

    Form.Cancel = org_shaolin_bmdp_workflow_form_Task_Cancel;

    Form.__entityName="org.shaolin.bmdp.workflow.form.Task";

    Form.init();
    return Form;
};

    /* EventHandler Functions */
/* Other_Func_FIRST:org_shaolin_bmdp_workflow_form_Task */
/* Other_Func_LAST:org_shaolin_bmdp_workflow_form_Task */

    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_Task_Save(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_Task_Save */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"saveDetail-20150801-235410",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_Task_Save */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_Task_Cancel(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_Task_Cancel */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"cancelDetail-20150801-235410",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_Task_Cancel */



