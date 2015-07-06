/* null */
/* auto generated constructor */
function org_shaolin_bmdp_test_form_File(json)
{
    var prefix = (typeof(json) == "string") ? json : json.prefix; 
    var idUILabel = new EBOS.ui.label
    ({
        ui: elementList[prefix + "idUILabel"]
    });

    var idUI = new EBOS.ui.textfield
    ({
        ui: elementList[prefix + "idUI"]
    });

    var nameUILabel = new EBOS.ui.label
    ({
        ui: elementList[prefix + "nameUILabel"]
    });

    var nameUI = new EBOS.ui.textfield
    ({
        ui: elementList[prefix + "nameUI"]
    });

    var authorUILabel = new EBOS.ui.label
    ({
        ui: elementList[prefix + "authorUILabel"]
    });

    var authorUI = new EBOS.ui.textfield
    ({
        ui: elementList[prefix + "authorUI"]
    });

    var typeUILabel = new EBOS.ui.label
    ({
        ui: elementList[prefix + "typeUILabel"]
    });

    var typeUI = new EBOS.ui.combobox
    ({
        ui: elementList[prefix + "typeUI"]
    });

    var ownerUILabel = new EBOS.ui.label
    ({
        ui: elementList[prefix + "ownerUILabel"]
    });

    var sizeUILabel = new EBOS.ui.label
    ({
        ui: elementList[prefix + "sizeUILabel"]
    });

    var sizeUI = new EBOS.ui.textfield
    ({
        ui: elementList[prefix + "sizeUI"]
    });

    var filesUILabel = new EBOS.ui.label
    ({
        ui: elementList[prefix + "filesUILabel"]
    });

    var filesUI = new EBOS.ui.objectlist
    ({
        ui: elementList[prefix + "filesUI"]
    });

    var modifiedUILabel = new EBOS.ui.label
    ({
        ui: elementList[prefix + "modifiedUILabel"]
    });

    var modifiedUI    ({
        ui: elementList[prefix + "modifiedUI"]
    });

    var okbtn = new EBOS.ui.button
    ({
        ui: elementList[prefix + "okbtn"]
    });

    var cancelbtn = new EBOS.ui.button
    ({
        ui: elementList[prefix + "cancelbtn"]
    });

// refered ui form[ownerUI] is null.    var Form = new EBOS.ui.panel
    ({
        ui: elementList[prefix + "Form"]
        ,items: [idUILabel,idUI,nameUILabel,nameUI,authorUILabel,authorUI,typeUILabel,typeUI,ownerUILabel,sizeUILabel,sizeUI,filesUILabel,filesUI,modifiedUILabel,modifiedUI,okbtn,cancelbtn,ownerUI]
    });

    Form.idUILabel=idUILabel;

    Form.idUI=idUI;

    Form.nameUILabel=nameUILabel;

    Form.nameUI=nameUI;

    Form.authorUILabel=authorUILabel;

    Form.authorUI=authorUI;

    Form.typeUILabel=typeUILabel;

    Form.typeUI=typeUI;

    Form.ownerUILabel=ownerUILabel;

    Form.sizeUILabel=sizeUILabel;

    Form.sizeUI=sizeUI;

    Form.filesUILabel=filesUILabel;

    Form.filesUI=filesUI;

    Form.modifiedUILabel=modifiedUILabel;

    Form.modifiedUI=modifiedUI;

    Form.okbtn=okbtn;

    Form.cancelbtn=cancelbtn;

    Form.ownerUI=ownerUI;

    Form.user_constructor = function()
    {
        /* Construct_FIRST:org_shaolin_bmdp_test_form_File */
        /* Construct_LAST:org_shaolin_bmdp_test_form_File */
    };

    Form.__entityName="org.shaolin.bmdp.test.form.File";

    Form.init();
    return Form;
};

    /* EventHandler Functions */
/* Other_Func_FIRST:org_shaolin_bmdp_test_form_File */
/* Other_Func_LAST:org_shaolin_bmdp_test_form_File */


