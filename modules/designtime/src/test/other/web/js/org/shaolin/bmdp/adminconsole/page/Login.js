/* null */
/* auto generated constructor */
function org_shaolin_bmdp_adminconsole_page_Login(json)
{
    var prefix = (typeof(json) == "string") ? json : json.prefix; 
    var userName = new EBOS.ui.textfield
    ({
        ui: elementList[prefix + "userName"]
    });

    var password = new EBOS.ui.passwordfield
    ({
        ui: elementList[prefix + "password"]
    });

    var submit = new EBOS.ui.button
    ({
        ui: elementList[prefix + "submit"]
    });

    var bottomPanel = new EBOS.ui.panel
    ({
        ui: elementList[prefix + "bottomPanel"]
        ,uiskin: "org.shaolin.uimaster.page.skin.TitlePanel"
        ,items: []
        ,subComponents: []
    });

    var loginPanel = new EBOS.ui.panel
    ({
        ui: elementList[prefix + "loginPanel"]
        ,uiskin: "org.shaolin.uimaster.page.skin.TitlePanel"
        ,items: []
        ,subComponents: [prefix + "userName",prefix + "password",prefix + "submit"]
    });

    var topPanel = new EBOS.ui.panel
    ({
        ui: elementList[prefix + "topPanel"]
        ,uiskin: "org.shaolin.uimaster.page.skin.TitlePanel"
        ,items: []
        ,subComponents: []
    });

    var Form = new EBOS.ui.panel
    ({
        ui: elementList[prefix + "Form"]
        ,items: [userName,password,submit,topPanel,loginPanel,bottomPanel]
    });

    Form.userName=userName;

    Form.password=password;

    Form.submit=submit;

    Form.topPanel=topPanel;

    Form.loginPanel=loginPanel;

    Form.bottomPanel=bottomPanel;

    Form.user_constructor = function()
    {
        /* Construct_FIRST:org_shaolin_bmdp_adminconsole_page_Login */
        /* Construct_LAST:org_shaolin_bmdp_adminconsole_page_Login */
    };

    Form.Login = org_shaolin_bmdp_adminconsole_page_Login_Login;

    Form.initPageJs = org_shaolin_bmdp_adminconsole_page_Login_initPageJs;

    Form.finalizePageJs = org_shaolin_bmdp_adminconsole_page_Login_finalizePageJs;

    Form.Login_OutFunctionName = org_shaolin_bmdp_adminconsole_page_Login_Login_OutFunctionName;

    Form.__AJAXSubmit = false;
    
    Form.__entityName="org.shaolin.bmdp.adminconsole.page.Login";

    Form.init();
    return Form;
};

    /* EventHandler Functions */
/* Other_Func_FIRST:org_shaolin_bmdp_adminconsole_page_Login */
/* Other_Func_LAST:org_shaolin_bmdp_adminconsole_page_Login */

    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_adminconsole_page_Login_Login(eventsource,event) {/* Gen_First:org_shaolin_bmdp_adminconsole_page_Login_Login */

        var UIEntity = this;
    }/* Gen_Last:org_shaolin_bmdp_adminconsole_page_Login_Login */


    function org_shaolin_bmdp_adminconsole_page_Login_initPageJs(){/* Gen_First:org_shaolin_bmdp_adminconsole_page_Login_initPageJs */
        var constraint_result = true;
        var UIEntity = this;

    }/* Gen_Last:org_shaolin_bmdp_adminconsole_page_Login_initPageJs */


    function org_shaolin_bmdp_adminconsole_page_Login_finalizePageJs(){/* Gen_First:org_shaolin_bmdp_adminconsole_page_Login_finalizePageJs */

    }/* Gen_Last:org_shaolin_bmdp_adminconsole_page_Login_finalizePageJs */


    function org_shaolin_bmdp_adminconsole_page_Login_Login_OutFunctionName(eventsource) {/* Gen_First:org_shaolin_bmdp_adminconsole_page_Login_Login_OutFunctionName */
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
        
        myForm._outname.value = "Login";
        myForm.target = "_self";
             
        if ( (constraint_result == true || constraint_result == null) && (!ajax_execute_onerror) ) {
          myForm.submit();
        }
        return constraint_result;
    }/* Gen_Last:org_shaolin_bmdp_adminconsole_page_Login_Login_OutFunctionName */


