//$Revision: 1.4 $
// baserule_TriggerUnedit

function bmiasia_ebos_constraint_baserule_TriggerUnedit() {
    for(var i = 0; i < this.targets.length; i++) {
        if(!this.targets[i].validationList) {
            this.targets[i].validationList = new Array();
            var action = new Object();
            action.message = this.message;
            action.sources = this.sources;
            action.targets = this.targets;
            action.sourceExpression = this.sourceExpression;
            action.targetExpression = this.targetExpression;
            this.targets[i].validationList.push(new Array(action, bmiasia_ebos_constraint_baserule_TriggerUnedit_internal));
        }
        bmiasia_ebos_constraint_baserule_TriggerUnedit_registerConstraint(this.targets[i]);
    }
    for(var i = 0; i < this.sources.length; i++) {
        if(!this.sources[i].validationList) {		
            this.sources[i].validationList = new Array();
            var action = new Object();
            action.message = this.message;
            action.sources = this.sources;
            action.targets = this.targets;
            action.sourceExpression = this.sourceExpression;
            action.targetExpression = this.targetExpression;
            this.sources[i].validationList.push(new Array(action, bmiasia_ebos_constraint_baserule_TriggerUnedit_internal));
        }
        bmiasia_ebos_constraint_baserule_TriggerUnedit_registerConstraint(this.sources[i]);
    }
    return bmiasia_ebos_constraint_baserule_TriggerUnedit_internal.call(this);
}

function bmiasia_ebos_constraint_baserule_TriggerUnedit_internal() {
    var sourceExpression = this.sourceExpression;
    var targetExpression = this.targetExpression;
    var sources = this.sources;
    var targets = this.targets;
    
    if(eval(sourceExpression)) {
        for(var i = 0; i < targets.length; i++) {
            targets[i].disabled = true;
            targets[i].style.backgroundImage = "url(" + WEB_CONTEXTPATH + READONLY_BACKGROUND_IMAGE + ")";
            targets[i].parentNode.disabled = true;
            targets[i].value = targets[i].defaultValue;
            bmiasia_ebos_constraint_baserule_TriggerUnedit_clearExpressionConstraint(targets[i].name);
        }
        return true;
    }
    else{
        for(var i = 0; i < targets.length; i++) {   
            targets[i].disabled = false;
            targets[i].style.backgroundImage = targets[i].defaultBackgroundImage;
            targets[i].parentNode.disabled = false;
            bmiasia_ebos_constraint_baserule_TriggerUnedit_clearExpressionConstraint(targets[i].name);
        }
        if(eval(targetExpression)) {
            return true;
        }
        else {
            for(var i = 0; i < targets.length; i++) {
                if (targets[i].type == "text" || targets[i].type == "password") {
                    bmiasia_ebos_constraint_baserule_TriggerUnedit_expressionConstraint(targets[i].name,this.message);
                }
            }
            return false;
        }
    }
}

function bmiasia_ebos_constraint_baserule_TriggerUnedit_registerConstraint(ui) {
    //memorize default background image
    if (!ui.defaultBackgroundImage)
    {
        if (EBOS.browser.msie)
        {
            //for ie
            ui.defaultBackgroundImage = ui.currentStyle.backgroundImage;
        }
        else
        {
            // for w3c dom
            ui.defaultBackgroundImage = document.defaultView.getComputedStyle(ui,null).getPropertyValue("background-image");
        }
    }
    
    if (ui.type == "select-one") {
		addEvent(ui, "change", bmiasia_ebos_constraint_baserule_TriggerUnedit_validateExpression)
    }
    else {
        try {
			removeEvent(ui, "mouseout", bmiasia_ebos_constraint_baserule_TriggerUnedit_validateExpression);
			addEvent(ui, "mouseout", bmiasia_ebos_constraint_baserule_TriggerUnedit_validateExpression);
        } catch (e) {}
        try {
			removeEvent(ui, "blur", bmiasia_ebos_constraint_baserule_TriggerUnedit_validateExpression);
			addEvent(ui, "blur", bmiasia_ebos_constraint_baserule_TriggerUnedit_validateExpression);
        } catch (e) {}
    }
}

function bmiasia_ebos_constraint_baserule_TriggerUnedit_validateExpression(event) {
    var component = EBOS.getObject(event);
    for(var i = 0; i < component.validationList.length; i++) {
        var param = component.validationList[i][0];
        param.currentComp = component;
        var validateFunc = component.validationList[i][1];
        if (!validateFunc.call(param)) {
            return true;
        }
    }
    return true;
}

function bmiasia_ebos_constraint_baserule_TriggerUnedit_clearExpressionConstraint(id) {
    clearConstraint(id);
    var t1 = document.getElementById(id);
    if(document.getElementById(t1.name+'div')) {
        document.getElementById(t1.name+'div').style.display = "none";
    }
}

function bmiasia_ebos_constraint_baserule_TriggerUnedit_expressionConstraint(id,message){
    if (message == null) {
        message = bmiasia_ebos_appbase_getI18NInfo("bmiasia.ebos.constraint.constraint.Bundle||VERIFY_FAIL");
    }
    var t1 = document.getElementById(id);
    var tp=t1.parentNode.firstChild;
    var newdiv;
    setConstraintStyle(t1);
    if(!document.getElementById(id+'div')) {
        newdiv =document.createElement("div");
        newdiv.innerHTML="<img src=\""+WEB_CONTEXTPATH+"/images/ebos_constraint.gif\" style=\"position:relative;left:0px;top:0px;\" alt=\""+message+"\" >";
        newdiv.id=id+'div';
        newdiv.style.display="";
        with(newdiv.style) {
            position = "absolute";
        }
        tp.parentNode.append(newdiv);
    }
    else {
        newdiv=document.getElementById(id+'div');
        newdiv.innerHTML="<img src=\""+WEB_CONTEXTPATH+"/images/ebos_constraint.gif\" style=\"position:relative;left:0px;top:0px;\" alt=\""+message+"\" >";		
        newdiv.style.display="";
    }
}