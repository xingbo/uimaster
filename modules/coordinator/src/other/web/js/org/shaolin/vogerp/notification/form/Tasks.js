/* null */
/* auto generated constructor */
function org_shaolin_vogerp_notification_form_Tasks(json)
{
    var prefix = (typeof(json) == "string") ? json : json.prefix; 
    var partyIdUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "partyIdUILabel"]
    });

    var partyIdUI = new UIMaster.ui.textfield
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

    var descriptionUI = new UIMaster.ui.textfield
    ({
        ui: elementList[prefix + "descriptionUI"]
    });

    var triggerTimeUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "triggerTimeUILabel"]
    });

    var triggerTimeUI = new UIMaster.ui.calendar
    ({
        ui: elementList[prefix + "triggerTimeUI"]
    });

    var triggerTimeStartUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "triggerTimeStartUILabel"]
    });

    var triggerTimeStartUI = new UIMaster.ui.calendar
    ({
        ui: elementList[prefix + "triggerTimeStartUI"]
    });

    var triggerTimeEndUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "triggerTimeEndUILabel"]
    });

    var triggerTimeEndUI = new UIMaster.ui.calendar
    ({
        ui: elementList[prefix + "triggerTimeEndUI"]
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
        ,subComponents: [prefix + "partyIdUILabel",prefix + "partyIdUI",prefix + "subjectUILabel",prefix + "subjectUI",prefix + "descriptionUILabel",prefix + "descriptionUI",prefix + "triggerTimeUILabel",prefix + "triggerTimeUI",prefix + "triggerTimeStartUILabel",prefix + "triggerTimeStartUI",prefix + "triggerTimeEndUILabel",prefix + "triggerTimeEndUI",prefix + "sendSMSUILabel",prefix + "sendSMSUI",prefix + "sendEmailUILabel",prefix + "sendEmailUI",prefix + "statusUILabel",prefix + "statusUI",prefix + "completeRateUILabel",prefix + "completeRateUI",prefix + "priorityUILabel",prefix + "priorityUI"]
    });

    var Form = new UIMaster.ui.panel
    ({
        ui: elementList[prefix + "Form"]
        ,items: [partyIdUILabel,partyIdUI,subjectUILabel,subjectUI,descriptionUILabel,descriptionUI,triggerTimeUILabel,triggerTimeUI,triggerTimeStartUILabel,triggerTimeStartUI,triggerTimeEndUILabel,triggerTimeEndUI,sendSMSUILabel,sendSMSUI,sendEmailUILabel,sendEmailUI,statusUILabel,statusUI,completeRateUILabel,completeRateUI,priorityUILabel,priorityUI,okbtn,cancelbtn,fieldPanel,actionPanel]
    });

    Form.partyIdUILabel=partyIdUILabel;

    Form.partyIdUI=partyIdUI;

    Form.subjectUILabel=subjectUILabel;

    Form.subjectUI=subjectUI;

    Form.descriptionUILabel=descriptionUILabel;

    Form.descriptionUI=descriptionUI;

    Form.triggerTimeUILabel=triggerTimeUILabel;

    Form.triggerTimeUI=triggerTimeUI;

    Form.triggerTimeStartUILabel=triggerTimeStartUILabel;

    Form.triggerTimeStartUI=triggerTimeStartUI;

    Form.triggerTimeEndUILabel=triggerTimeEndUILabel;

    Form.triggerTimeEndUI=triggerTimeEndUI;

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
        /* Construct_FIRST:org_shaolin_vogerp_notification_form_Tasks */
        /* Construct_LAST:org_shaolin_vogerp_notification_form_Tasks */
    };

    Form.Save = org_shaolin_vogerp_notification_form_Tasks_Save;

    Form.Cancel = org_shaolin_vogerp_notification_form_Tasks_Cancel;

    Form.__entityName="org.shaolin.vogerp.notification.form.Tasks";

    Form.init();
    return Form;
};

    /* EventHandler Functions */
/* Other_Func_FIRST:org_shaolin_vogerp_notification_form_Tasks */
/* Other_Func_LAST:org_shaolin_vogerp_notification_form_Tasks */

    /* auto generated eventlistener function declaration */
    function org_shaolin_vogerp_notification_form_Tasks_Save(eventsource,event) {/* Gen_First:org_shaolin_vogerp_notification_form_Tasks_Save */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"saveDetail-167785158",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_vogerp_notification_form_Tasks_Save */


    /* auto generated eventlistener function declaration */
    function org_shaolin_vogerp_notification_form_Tasks_Cancel(eventsource,event) {/* Gen_First:org_shaolin_vogerp_notification_form_Tasks_Cancel */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"cancelDetail850677409",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_vogerp_notification_form_Tasks_Cancel */



