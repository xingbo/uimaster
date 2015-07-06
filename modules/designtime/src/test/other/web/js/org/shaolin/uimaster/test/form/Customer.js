/* null */
/* auto generated constructor */
function org_shaolin_uimaster_test_form_Customer(json)
{
    var prefix = (typeof(json) == "string") ? json : json.prefix; 
    var idLabel = new EBOS.ui.label
    ({
        ui: elementList[prefix + "idLabel"]
    });

    var id = new EBOS.ui.textfield
    ({
        ui: elementList[prefix + "id"]
    });

    var nameLabel = new EBOS.ui.label
    ({
        ui: elementList[prefix + "nameLabel"]
    });

    var name = new EBOS.ui.textfield
    ({
        ui: elementList[prefix + "name"]
    });

    var Form = new EBOS.ui.panel
    ({
        ui: elementList[prefix + "Form"]
        ,items: [idLabel,id,nameLabel,name]
    });

    Form.idLabel=idLabel;

    Form.id=id;

    Form.nameLabel=nameLabel;

    Form.name=name;

    Form.user_constructor = function()
    {
        /* Construct_FIRST:org_shaolin_uimaster_test_form_Customer */
        /* Construct_LAST:org_shaolin_uimaster_test_form_Customer */
    };

    Form.__entityName="org.shaolin.uimaster.test.form.Customer";

    Form.init();
    return Form;
};

    /* EventHandler Functions */
/* Other_Func_FIRST:org_shaolin_uimaster_test_form_Customer */
/* Other_Func_LAST:org_shaolin_uimaster_test_form_Customer */


