//$Revision: 1.25 $
// baserule_NotNull
function bmiasia_ebos_constraint_baserule_NotNull()
{
    var validationList;
    
    if(this.ui.toSelectList && this.ui.showSelectedList && this.ui.hiddenSelectedList)
    {
        this.ui.showSelectedList.validationList = new Array();
        validationList = this.ui.showSelectedList.validationList;
    } else if( ! this.ui.validationList)
    {
        this.ui.validationList = new Array();
        validationList = this.ui.validationList;
    } else {
        validationList = this.ui.validationList;
    }

    var isExisted = false;
    for(var i = 0; i < validationList.length; i ++)
    {
        var action = validationList[i][0];
        if (validationList[i][1] == bmiasia_ebos_constraint_baserule_NotNull_internal
                && action.ui == this.ui && action.message == this.message)
        {
            isExisted = true;
        }
    }
    // Not submit when not existed
    if( ! isExisted)
    {
        var action = new Object();
        action.message = this.message;
        action.ui = this.ui;
        action.ui.markRequired && action.ui.markRequired();
        validationList.push(new Array(action, bmiasia_ebos_constraint_baserule_NotNull_internal));
    }

    if(this.ui.toSelectList && this.ui.showSelectedList && this.ui.hiddenSelectedList)
        registerConstraint(this.ui.showSelectedList);
    else
        registerConstraint(this.ui);
    return bmiasia_ebos_constraint_baserule_NotNull_internal.call(this);
}

function bmiasia_ebos_constraint_baserule_NotNull_internal()
{
    if(this.ui.type == "text" || this.ui.type == "textarea" || this.ui.type == "password" || this.ui.type == "hidden")
    {
        var value = this.ui.value;
        if(value.replace(/^\s+|\s+$/g,"").length > 0)
        {
            return true;
        }
        else
        {
            try
            {
                setConstraintStyle(this.ui);
            }
            catch(e)
            {
            }
            this.ui.oldInvalidF = true;
            constraint(this.ui.name, this.message);
            return false;
        }
    }
    else if(this.ui.type == "checkbox")
    {
        if(this.ui.checked == true)
        {
            return true;
        }
        else
        {
            try
            {
                setConstraintStyle(this.ui);
            }
            catch(e)
            {
            }
            this.ui.oldInvalidF = true;
            constraint(this.ui.name, this.message);
            return false;
        }
    }
    else if(this.ui.type == "select-one")
    {
        if(this.ui.value == "_NOT_SPECIFIED" || this.ui.value == "")
        {
            try
            {
                setConstraintStyle(this.ui);
            }
            catch(e)
            {
            }
            this.ui.oldInvalidF = true;
            constraint(this.ui.name, this.message);
            return false;
        }
        else
        {
            return true;
        }
    }
    else if(this.ui.type == "select-multiple")
    {
        if(this.ui.length > 0)
        {
            return true;
        }
        else
        {
            try
            {
                setConstraintStyle(this.ui);
            }
            catch(e)
            {
            }
            this.ui.oldInvalidF = true;
            constraint(this.ui.name, this.message);
            return false;
        }
    }
    else if(this.ui[0] != undefined)
    {
        if(this.ui[0].type == "radio" || this.ui[0].type == "checkbox")
        {
            for(var i = 0; i < this.ui.length; i ++ )
            {
                if(this.ui[i].checked == true)
                {
                    return true;
                }
            }
            try
            {
                setConstraintStyle(this.ui);
            }
            catch(e)
            {
            }
            this.ui.oldInvalidF = true;
            constraint(this.ui.name, this.message);
            return false;
        }
    }
    else if(this.ui.toSelectList && this.ui.showSelectedList && this.ui.hiddenSelectedList)
    {
        if (this.ui.hiddenSelectedList.length > 0)
            return true;
        try
        {
            setConstraintStyle(this.ui.showSelectedList);
        }
        catch(e)
        {}
        this.ui.showSelectedList.oldInvalidF = true;
        constraint(this.ui.showSelectedList.name, this.message);
        return false;
    }
    return true;
}