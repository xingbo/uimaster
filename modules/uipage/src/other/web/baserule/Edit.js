//$Revision: 1.3 $
// baserule_Edit
function bmiasia_ebos_constraint_baserule_Edit() {
	bmiasia_ebos_constraint_baserule_EditFunction(this.ui);
  	return true;
}

function bmiasia_ebos_constraint_baserule_EditFunction(component)
{
	if(component != null && !(component instanceof Function))
	{
		if(component.length != null && component.type == null)
		{
			for(var i = 0, n = component.length; i < n; i++)
			{
				bmiasia_ebos_constraint_baserule_EditFunction(component[i]);
			}
		}
		else
		{
			if(component.disabled != null)
			{
				component.disabled = false;
			}
			else
			{
				for(var subcomponent in component)
				{
					if(subcomponent != "parentEntity")
					{
						bmiasia_ebos_constraint_baserule_EditFunction(component[subcomponent]);
					}
				}
			}
		}	
	}
}
