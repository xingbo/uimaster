/* null */
/* auto generated constructor */
function org_shaolin_bmdp_test_form_Administrator(json)
{
    var prefix = (typeof(json) == "string") ? json : json.prefix; 
    var field0Label = new EBOS.ui.label
    ({
        ui: elementList[prefix + "field0Label"]
    });

    var field0 = new EBOS.ui.textfield
    ({
        ui: elementList[prefix + "field0"]
    });

    var Form = new EBOS.ui.panel
    ({
        ui: elementList[prefix + "Form"]
        ,items: [field0Label,field0]
    });

    Form.field0Label=field0Label;

    Form.field0=field0;

    Form.user_constructor = function()
    {
        /* Construct_FIRST:org_shaolin_bmdp_test_form_Administrator */
        /* Construct_LAST:org_shaolin_bmdp_test_form_Administrator */
    };

    Form.__entityName="org.shaolin.bmdp.test.form.Administrator";

    Form.init();
    return Form;
};

    /* EventHandler Functions */
/* Other_Func_FIRST:org_shaolin_bmdp_test_form_Administrator */
/* Other_Func_LAST:org_shaolin_bmdp_test_form_Administrator */


