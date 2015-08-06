/* null */
/* auto generated constructor */
function org_shaolin_bmdp_workflow_form_WorkflowXMLEditor(json)
{
    var prefix = (typeof(json) == "string") ? json : json.prefix; 
    var entityNameUI = new UIMaster.ui.hidden
    ({
        ui: elementList[prefix + "entityNameUI"]
    });

    var xmlContentUI = new UIMaster.ui.textarea
    ({
        ui: elementList[prefix + "xmlContentUI"]
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
        ,subComponents: [prefix + "entityNameUI",prefix + "xmlContentUI"]
    });

    var Form = new UIMaster.ui.panel
    ({
        ui: elementList[prefix + "Form"]
        ,items: [entityNameUI,xmlContentUI,okbtn,cancelbtn,fieldPanel,actionPanel]
    });

    Form.entityNameUI=entityNameUI;

    Form.xmlContentUI=xmlContentUI;

    Form.okbtn=okbtn;

    Form.cancelbtn=cancelbtn;

    Form.fieldPanel=fieldPanel;

    Form.actionPanel=actionPanel;

    Form.user_constructor = function()
    {
        /* Construct_FIRST:org_shaolin_bmdp_workflow_form_WorkflowXMLEditor */
        /* Construct_LAST:org_shaolin_bmdp_workflow_form_WorkflowXMLEditor */
    };

    Form.Save = org_shaolin_bmdp_workflow_form_WorkflowXMLEditor_Save;

    Form.Cancel = org_shaolin_bmdp_workflow_form_WorkflowXMLEditor_Cancel;

    Form.__entityName="org.shaolin.bmdp.workflow.form.WorkflowXMLEditor";

    Form.init();
    return Form;
};

    /* EventHandler Functions */
/* Other_Func_FIRST:org_shaolin_bmdp_workflow_form_WorkflowXMLEditor */
/* Other_Func_LAST:org_shaolin_bmdp_workflow_form_WorkflowXMLEditor */

    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_WorkflowXMLEditor_Save(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_WorkflowXMLEditor_Save */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"saveDetail-20150801-235410",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_WorkflowXMLEditor_Save */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_WorkflowXMLEditor_Cancel(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_WorkflowXMLEditor_Cancel */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"cancelDetail-20150801-235410",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_WorkflowXMLEditor_Cancel */



