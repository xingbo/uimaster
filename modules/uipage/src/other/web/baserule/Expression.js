//$Revision: 1.17 $
// baserule_Expression
function bmiasia_ebos_constraint_baserule_Expression()
{
	for(var i = 0; i < this.components.length; i++)
	{
		if(!this.components[i].validationList)
		{		
			this.components[i].validationList = new Array();
		}
		var isExisted = false;
		for(var j = 0; j < this.components[i].validationList.length; j++)
		{
			var action = this.components[i].validationList[j][0];
			if (this.components[i].validationList[j][1] == bmiasia_ebos_constraint_baserule_Expression_internal 
				&& action.expression == this.expression && action.message == this.message
				&& isArrayEquals(action.components,this.components)) {
				isExisted = true;
			}
		}
		//Not submit when not existed
		if(!isExisted)
		{
			var action = new Object();
			action.message = this.message;
			action.components = this.components;
			action.expression = this.expression;
			if(this.variables)
				action.variables = this.variables;
			this.components[i].validationList.push(new Array(action, bmiasia_ebos_constraint_baserule_Expression_internal));
		}
		registerConstraint(this.components[i]);
	}
	return bmiasia_ebos_constraint_baserule_Expression_internal.call(this);
}


function bmiasia_ebos_constraint_baserule_Expression_internal() {    
  var expression = this.expression;
  var newExp = expression;
  if (this.components != undefined) {
    var components = this.components;
    for(var i = 0; i < components.length; i++) {
      var component = components[i];
      if(component.type == "text" || component.type == "password") {
      	newExp = eval("newExp.replace(/\\{" + (i+1) + "\\}/g, \"\\\"" + component.value.replace(/(^\s*)|(\s*$)/g, "") + "\\\"\")");
      }else if(component.type == "checkbox") {
        if(component.checked) {
          newExp = eval("newExp.replace(/\\{" + (i+1) + "\\}/g, \"true\")");
        }else {
          newExp = eval("newExp.replace(/\\{" + (i+1) + "\\}/g, \"false\")");
        }
      }else if(component[0] != undefined && component[0].type == "radio") {
        var flag = false;
        for(var j = 0; j < component.length; j++) {
          if(component[j].checked == true) {
          	newExp = eval("newExp.replace(/\\{" + (i+1) + "\\}/g, \"\\\"" + component[j].value + "\\\"\")");
           // newExp = newExp.replace("{" + (i + 1) + "}", "\"" + component[j].value + "\"");
            flag = true;
            break;
          }
        }
        if(!flag) {
        	newExp = eval("newExp.replace(/\\{" + (i+1) + "\\}/g, \"undefined\"\")");
        //  newExp = newExp.replace("{" + (i + 1) + "}", "undefined");
        }
      }else {
      	newExp = eval("newExp.replace(/\\{" + (i+1) + "\\}/g, \"\\\"" + component.value + "\\\"\")");
      //  newExp = newExp.replace("{" + (i + 1) + "}", "\"" + component.value + "\"");
      }
    }
  }
  
  if(this.variables)
  {
    newFunc = eval("tempFunc = function(variables){ return "+ newExp+";};tempFunc");
    var result = newFunc(this.variables); 
    if (result) {
    	return true;
    }
    else {
    	if(!this.currentComp)
	  	{
	  	    var defaultComp = this.components[0].name;
            for(var i = 0; i < this.components.length; i++)
	        {
	            if (this.components[i].type == "text" || this.components[i].type == "password" || this.components[i].tagName == "SELECT")
	            {
	                defaultComp = this.components[i].name;
                        setConstraintStyle(this.components[i]);
	                //break;
	            }
	        }
			this.components.oldInvalidF = true;
	  		constraint(defaultComp,this.message);
	  	}
	  	else {
		        setConstraintStyle(this.currentComp);
		        this.currentComp.oldInvalidF = true;
	  		constraint(this.currentComp.name,this.message);
	  	}
	  	return false;
	}
  }
  else {
	  if(eval(newExp))
	  {
	  	return true;
	  }
	  else
	  {
	  	//if(!this.currentComp)
	  	//{
            for(var i = 0; i < this.components.length; i++)
	        {
	            var defaultComp = this.components[i].name;
	            if (this.components[i].type == "text" || this.components[i].type == "password" || this.components[i].tagName == "SELECT")
	            {
	                defaultComp = this.components[i].name;
                        setConstraintStyle(this.components[i]);
                        this.components[i].oldInvalidF = true;
	                constraint(defaultComp,this.message);
	                //break;
	            }
	        }
	  	//}
	  	//else { 
	  	//    setConstraintStyle(this.currentComp);
	  	//	constraint(this.currentComp.name,this.message);
        //}
	  	return false;
	  }
  }
}