//$Revision: 1.13 $
// baserule_Length
function bmiasia_ebos_constraint_baserule_Length()
{
    if( ! this.ui.validationList)
    {
        this.ui.validationList = new Array();
    }

    var isExisted = false;
    for(var i = 0; i < this.ui.validationList.length; i++)
    {
        var action = this.ui.validationList[i][0];
        if (this.ui.validationList[i][1] == bmiasia_ebos_constraint_baserule_Length_internal
                && action.ui == this.ui && action.message == this.message && action.length == this.length && action.operator == this.operator)
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
        action.operator = this.operator;
        action.length = this.length;
        this.ui.validationList.push(new Array(action, bmiasia_ebos_constraint_baserule_Length_internal));
    }

    registerConstraint(this.ui);
    return bmiasia_ebos_constraint_baserule_Length_internal.call(this);
}

function bmiasia_ebos_constraint_baserule_Length_internal()
{

    if(this.ui.type == "text" | this.ui.type == "password")
    {
        var value = this.ui.value;
        if(">" == this.operator)
        {
            if(value.length > this.length)
            {
                clearConstraint(this.ui.name);
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
        else if(">=" == this.operator)
        {
            if(value.length >= this.length)
            {
                clearConstraint(this.ui.name);
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
        else if("<" == this.operator)
        {
            if(value.length < this.length)
            {
                clearConstraint(this.ui.name);
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
        else if("<=" == this.operator)
        {
            if(value.length <= this.length)
            {
                clearConstraint(this.ui.name);
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
        else if("==" == this.operator)
        {
            if(value.length == this.length)
            {
                clearConstraint(this.ui.name);
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
        else if("!=" == this.operator)
        {
            if(value.length != this.length)
            {
                clearConstraint(this.ui.name);
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

    }
    return true;
}
