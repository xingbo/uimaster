//$Revision: 1.3 $
// baserule_Edit
function bmiasia_ebos_constraint_baserule_Visible() {
	bmiasia_ebos_constraint_baserule_VisibleFunction(this.ui);
  	return true;
}

function bmiasia_ebos_constraint_baserule_VisibleFunction(component)
{
	if(component != null && !(component instanceof Function))
	{
		if(component.length != null && component.type == null)
		{
			for(var i = 0, n = component.length; i < n; i++)
			{
				bmiasia_ebos_constraint_baserule_VisibleFunction(component[i]);
			}
		}
		else
		{
			if(component.style != null && component.style.display != null)
			{
				if((component.type == "hidden" || component.type == "radio" || component.type == "checkbox")&& component.parentNode != null)
				{
					component.parentNode.style.display = "inline";
				}
				else
				{				
					component.style.display = "inline";
				}
			}
			else
			{
				for(var subcomponent in component)
				{
					if(subcomponent != "parentEntity")
					{
						bmiasia_ebos_constraint_baserule_VisibleFunction(component[subcomponent]);
					}
				}
			}
		}	
	}
}
