//$Revision: 1.6 $
// baserule_NotHave

function bmiasia_ebos_constraint_baserule_NotHave()
{
	if(!this.ui.validationList)
	{		
		this.ui.validationList = new Array();
	}
	
	var isExisted = false;
	for(var i = 0; i < this.ui.validationList.length; i++)
	{
		var action = this.ui.validationList[i][0];
		if (this.ui.validationList[i][1] == bmiasia_ebos_constraint_baserule_NotHave_internal 
			&& action.ui == this.ui && action.message == this.message && action.chars == this.chars) {
			isExisted = true;
		}
	}
	//Not submit when not existed
	if(!isExisted)
	{
		var action = new Object();
		action.message = this.message;
		action.ui = this.ui;
		action.chars = this.chars;
		this.ui.validationList.push(new Array(action, bmiasia_ebos_constraint_baserule_NotHave_internal));
	}
		
	registerConstraint(this.ui);
	return bmiasia_ebos_constraint_baserule_NotHave_internal.call(this);	
}


function bmiasia_ebos_constraint_baserule_NotHave_internal() {
	
  var ind = this.chars.split(",");
  var abc = "";
  for(var i = 0; i < ind.length; i++) {
    if(ind[i].search("-")) {
     if(ind[i].indexOf('-') == 1 && ind[i].length == 3) {
      var start = ind[i].charAt(0);
      var end = ind[i].charAt(ind[i].length-1);
      for(var x = start; x <= end; x++) {
        abc += x;
      }
     }else {
      abc += ind[i];
     }
    }else {
      abc += ind[i];
    }
  }
  if(this.ui.type == "text" | this.ui.type == "password") {
    var value = this.ui.value;
    for(var i = 0; i < value.length; i++) {
      if(abc.indexOf(value.charAt(i)) != -1) {
	this.ui.oldInvalidF = true;
        constraint(this.ui.name,this.message);
        return false;
      }
    }
  }
  return true;
}
