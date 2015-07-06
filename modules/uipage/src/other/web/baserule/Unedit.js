//$Revision: 1.5 $
// baserule_Unedit
function bmiasia_ebos_constraint_baserule_Unedit() {
	bmiasia_ebos_constraint_baserule_UneditFunction(this.ui);
  	return true;
}

function bmiasia_ebos_constraint_baserule_UneditFunction(component)
{
	if(component != null && !(component instanceof Function))
	{
		if(component.length != null && component.type == null)
		{
			for(var i = 0, n = component.length; i < n; i++)
			{
				bmiasia_ebos_constraint_baserule_UneditFunction(component[i]);
			}
		}
		else
		{
			if(component.disabled != null)
			{
                                if (component.tagName == "INPUT" || component.tagName == "SELECT" || component.tagName == "TEXTAREA"
                                                || component.tagName == "BUTTON" || component.onclick != undefined)
                                {
				        component.disabled = true;
			        }
			}
			else
			{
				for(var subcomponent in component)
				{
					if(subcomponent != "parentEntity")
					{
						bmiasia_ebos_constraint_baserule_UneditFunction(component[subcomponent]);
					}
				}
			}
		}	
	}
}
