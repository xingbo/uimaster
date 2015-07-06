/* null */
/* auto generated constructor */
function org_shaolin_bmdp_test_form_Folder(json)
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

    var filesUILabel = new EBOS.ui.label
    ({
        ui: elementList[prefix + "filesUILabel"]
    });

    var filesUI = new EBOS.ui.objectlist
    ({
        ui: elementList[prefix + "filesUI"]
    });

    var foldersUILabel = new EBOS.ui.label
    ({
        ui: elementList[prefix + "foldersUILabel"]
    });

    var foldersUI = new EBOS.ui.objectlist
    ({
        ui: elementList[prefix + "foldersUI"]
    });

    var okbtn = new EBOS.ui.button
    ({
        ui: elementList[prefix + "okbtn"]
    });

    var cancelbtn = new EBOS.ui.button
    ({
        ui: elementList[prefix + "cancelbtn"]
    });

    var Form = new EBOS.ui.panel
    ({
        ui: elementList[prefix + "Form"]
        ,items: [idUILabel,idUI,nameUILabel,nameUI,authorUILabel,authorUI,filesUILabel,filesUI,foldersUILabel,foldersUI,okbtn,cancelbtn]
    });

    Form.idUILabel=idUILabel;

    Form.idUI=idUI;

    Form.nameUILabel=nameUILabel;

    Form.nameUI=nameUI;

    Form.authorUILabel=authorUILabel;

    Form.authorUI=authorUI;

    Form.filesUILabel=filesUILabel;

    Form.filesUI=filesUI;

    Form.foldersUILabel=foldersUILabel;

    Form.foldersUI=foldersUI;

    Form.okbtn=okbtn;

    Form.cancelbtn=cancelbtn;

    Form.user_constructor = function()
    {
        /* Construct_FIRST:org_shaolin_bmdp_test_form_Folder */
        /* Construct_LAST:org_shaolin_bmdp_test_form_Folder */
    };

    Form.__entityName="org.shaolin.bmdp.test.form.Folder";

    Form.init();
    return Form;
};

    /* EventHandler Functions */
/* Other_Func_FIRST:org_shaolin_bmdp_test_form_Folder */
/* Other_Func_LAST:org_shaolin_bmdp_test_form_Folder */


