/* null */
/* auto generated constructor */
function org_shaolin_bmdp_test_page_Administrator(json)
{
    var prefix = (typeof(json) == "string") ? json : json.prefix; 
    var beObject.field0 = new EBOS.ui.textfield
    ({
        ui: elementList[prefix + "beObject.field0"]
    });

    var beObjectUITable = new EBOS.ui.objectlist
    ({
        ui: elementList[prefix + "beObjectUITable"]
        ,items: []
        ,subComponents: []
    });

    var resultPanel = new EBOS.ui.panel
    ({
        ui: elementList[prefix + "resultPanel"]
        ,items: []
        ,subComponents: [prefix + "beObjectUITable"]
    });

    var searchPanel = new EBOS.ui.panel
    ({
        ui: elementList[prefix + "searchPanel"]
        ,items: []
        ,subComponents: [prefix + "beObject.field0"]
    });

    var Form = new EBOS.ui.panel
    ({
        ui: elementList[prefix + "Form"]
        ,items: [beObject.field0,searchPanel,resultPanel,beObjectUITable]
    });

    Form.beObject.field0=beObject.field0;

    Form.searchPanel=searchPanel;

    Form.resultPanel=resultPanel;

    Form.beObjectUITable=beObjectUITable;

    Form.user_constructor = function()
    {
        /* Construct_FIRST:org_shaolin_bmdp_test_page_Administrator */
        /* Construct_LAST:org_shaolin_bmdp_test_page_Administrator */
    };

    Form.Search = org_shaolin_bmdp_test_page_Administrator_Search;

    Form.initPageJs = org_shaolin_bmdp_test_page_Administrator_initPageJs;

    Form.finalizePageJs = org_shaolin_bmdp_test_page_Administrator_finalizePageJs;

    Form.DefaultOut_OutFunctionName = org_shaolin_bmdp_test_page_Administrator_DefaultOut_OutFunctionName;

    Form.__AJAXSubmit = false;
    
    Form.__entityName="org.shaolin.bmdp.test.page.Administrator";

    Form.init();
    return Form;
};

    /* EventHandler Functions */
/* Other_Func_FIRST:org_shaolin_bmdp_test_page_Administrator */
/* Other_Func_LAST:org_shaolin_bmdp_test_page_Administrator */

    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_test_page_Administrator_Search(eventsource,event) {/* Gen_First:org_shaolin_bmdp_test_page_Administrator_Search */

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_test_page_Administrator_Search */


    function org_shaolin_bmdp_test_page_Administrator_initPageJs(){/* Gen_First:org_shaolin_bmdp_test_page_Administrator_initPageJs */
        var constraint_result = true;
        var UIEntity = this;

    }/* Gen_Last:org_shaolin_bmdp_test_page_Administrator_initPageJs */


    function org_shaolin_bmdp_test_page_Administrator_finalizePageJs(){/* Gen_First:org_shaolin_bmdp_test_page_Administrator_finalizePageJs */

    }/* Gen_Last:org_shaolin_bmdp_test_page_Administrator_finalizePageJs */


    function org_shaolin_bmdp_test_page_Administrator_DefaultOut_OutFunctionName(eventsource) {/* Gen_First:org_shaolin_bmdp_test_page_Administrator_DefaultOut_OutFunctionName */
        var constraint_result = true;
        var myForm;
        if (this.formName != undefined)
        {
            myForm = document.forms[this.formName];
        }
        else
        {
            var p = this.Form.parentNode;
            while(p.tagName != "FORM")
                p = p.parentNode;
            myForm = p;//document.forms[0];
        }

        var UIEntity = this;
        
        myForm._outname.value = "DefaultOut";
        myForm.target = "_self";
             
        if ( (constraint_result == true || constraint_result == null) && (!ajax_execute_onerror) ) {
          myForm.submit();
        }
        return constraint_result;
    }/* Gen_Last:org_shaolin_bmdp_test_page_Administrator_DefaultOut_OutFunctionName */


