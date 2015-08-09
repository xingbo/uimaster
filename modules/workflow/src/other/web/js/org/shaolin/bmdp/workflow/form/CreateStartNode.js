/* null */
/* auto generated constructor */
function org_shaolin_bmdp_workflow_form_CreateStartNode(json)
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
        ,subComponents: [prefix + "idUI",prefix + "nameUILabel",prefix + "nameUI",prefix + "expressionUILabel",prefix + "expressionUI"]
    });

    var Form = new UIMaster.ui.panel
    ({
        ui: elementList[prefix + "Form"]
        ,items: [idUI,nameUILabel,nameUI,expressionUILabel,expressionUI,okbtn,cancelbtn,fieldPanel,actionPanel]
    });

    Form.idUI=idUI;

    Form.nameUILabel=nameUILabel;

    Form.nameUI=nameUI;

    Form.expressionUILabel=expressionUILabel;

    Form.expressionUI=expressionUI;

    Form.okbtn=okbtn;

    Form.cancelbtn=cancelbtn;

    Form.fieldPanel=fieldPanel;

    Form.actionPanel=actionPanel;

    Form.user_constructor = function()
    {
        /* Construct_FIRST:org_shaolin_bmdp_workflow_form_CreateStartNode */
        /* Construct_LAST:org_shaolin_bmdp_workflow_form_CreateStartNode */
    };

    Form.Save = org_shaolin_bmdp_workflow_form_CreateStartNode_Save;

    Form.Cancel = org_shaolin_bmdp_workflow_form_CreateStartNode_Cancel;

    Form.__entityName="org.shaolin.bmdp.workflow.form.CreateStartNode";

    Form.init();
    return Form;
};

    /* EventHandler Functions */
/* Other_Func_FIRST:org_shaolin_bmdp_workflow_form_CreateStartNode */
/* Other_Func_LAST:org_shaolin_bmdp_workflow_form_CreateStartNode */

    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_CreateStartNode_Save(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_CreateStartNode_Save */

        {   
            var constraint_result = this.Form.validate();
            if (constraint_result != true && constraint_result != null) {
                return false;
            }
        }
        
        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"saveDetail-20150807-230249",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_CreateStartNode_Save */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_CreateStartNode_Cancel(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_CreateStartNode_Cancel */

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"cancelDetail-20150807-230249",UIMaster.getValue(eventsource),this.__entityName);

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_CreateStartNode_Cancel */



