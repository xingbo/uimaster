/**
 * @description Background color of the combobox.
 */
var CONSTRAINT_BACKGROUNDCOLOR = typeof(UIMaster_CONSTRAINT_BACKGROUND_COLOR) == "undefined" ? "#C4E1FF" : UIMaster_CONSTRAINT_BACKGROUND_COLOR;
var CONSTRAINT_BGCOLOR = "#E7F3FF";
var CONSTRAINT_STYLE_SUFFIX = "_constraint";
/**
 * @description Required asterisk indicator.
 */
var CONSTRAINT_BACKGROUND_IMAGE = typeof(UIMaster_CONSTRAINT_BACKGROUND_IMAGE) == "undefined" ? "/images/widget_required_highlight.gif" : UIMaster_CONSTRAINT_BACKGROUND_IMAGE;
var READONLY_BACKGROUND_IMAGE = "/images/widget_readonly.gif";
/*
 * @description Some short names for common static string
 */
var userAgent = navigator.userAgent.toLowerCase(),
    D="defaultname.",
    C="Common";
function setListener(component, funcname){
    if (component != null)
        //for array
        if (component.length != null && component.type == null)
            for (var i = 0, n = component.length; i < n; i++)
                setSingleListener(component[i], funcname);
        else
            setSingleListener(component, funcname);
}

function setSingleListener(component, funcname){
    if (component == undefined)
        return;
    if (component.type == "select-one") {
        if (component.onchange != null && component.onchange != undefined) {
            var oldfunc = component.onchange;
            component.onchange = function(){
                oldfunc();
                funcname();
            };
        }
        else
            component.onchange = funcname;
    }
    else //if(component.type == "checkbox")
    {
        if (component.onclick != null && component.onclick != undefined) {
            var oldfunc = component.onclick;
            component.onclick = function(){
                oldfunc();
                funcname();
            };
        }
        else
            component.onclick = funcname;
    }
}

function Expression(components, expression){
    var newExp = expression;
    for (var i = 0; i < components.length; i++) {
        var component = components[i];
        if (component.type == "text" | component.type == "password")
            newExp = newExp.replace("{" + (i + 1) + "}", component.value);
        else
            if (component.type == "checkbox")
                if (component.checked == true)
                    newExp = newExp.replace("{" + (i + 1) + "}", "true");
                else
                    newExp = newExp.replace("{" + (i + 1) + "}", "false");
            else
                newExp = newExp.replace("{" + (i + 1) + "}", component.value);
    }
    return eval(newExp);
}
/**
 * @description Set the required indicator for a widget.
 * @param {String} id Widget's id.
 */
function setRequiredStyle(id) {
    if (!document.getElementById(id + 'div')) {
        var t1 = document.getElementById(id + "_widgetLabel"), newdiv = $('<span><img src=\"' + RESOURCE_CONTEXTPATH + USER_CONSTRAINT_IMG + '\"</span>').attr('id',id+'div').css("verticalAlign","top");
        t1 ? (USER_CONSTRAINT_LEFT ? $(t1).prepend(newdiv) : $(t1).append(newdiv)) : (t1 = document.getElementById(id) || document.getElementsByName(id)[0],USER_CONSTRAINT_LEFT ? $(t1).before(newdiv) : $(t1).after(newdiv));
    }
}
/**
 * @description Set the blue background for a textfield.
 * @param {Node} ui Widget's node.
 */
function setConstraintStyle(ui){
    var uiClassName = ui.className;
    if (uiClassName.lastIndexOf(CONSTRAINT_STYLE_SUFFIX) != -1 || ui.style.backgroundImage == "url(" + RESOURCE_CONTEXTPATH + CONSTRAINT_BACKGROUND_IMAGE + ")")
        return;

    if ($.trim(uiClassName) != "") {
        ui.className += " " + uiClassName + CONSTRAINT_STYLE_SUFFIX;
        var noBg = (UIMaster.browser.msie ? ui.currentStyle.backgroundImage == "none" : document.defaultView.getComputedStyle(ui, null).getPropertyValue("background-image") == "none")
        if (noBg) {
            ui.className = uiClassName;
            ui.style.backgroundImage = "url(" + RESOURCE_CONTEXTPATH + CONSTRAINT_BACKGROUND_IMAGE + ")";
        }
    }
    else {
        ui.style.backgroundImage = "url(" + RESOURCE_CONTEXTPATH + CONSTRAINT_BACKGROUND_IMAGE + ")";
    }
}
/**
 * @description Set constraint display for a widget.
 * @param {String} id Widget's id.
 * @param {String} message Error message to set.
 */
function constraint(id, message){
    message = message || UIMaster.i18nmsg(C+"||VERIFY_FAIL");
    var ui = document.getElementById(id) || document.getElementsByName(id)[0], p;
    if ((ui && ui.parentDiv) || (ui && !ui.parentDiv && ui.parentNode.nodeName=='P')) {
        p=$(ui.parentDiv!=null?ui.parentDiv:ui.parentNode.parentNode);
        p.children('.err-field-warn, #_pholder').remove();
        var pw=(p.width()>0)?p.width():175;   //175 is the default width of class "w1", and 18 is the default width of icon
        var errors=message.split(" * "), msg=$('<span style=color:red; >' + (errors.length > 1 ? UIMaster.i18nmsg(C+"||MULT_ERRORS") : errors[0]) + '</span>'), box=$('<div class="err-field-warn clearfix"></div>');
        if (p.attr("nodeName")=="TD") box.addClass("err-field-warn-table");
        p.append('<div id="_pholder"></div>').append(box.attr('title',errors.join("\n")).width(pw).append(msg.width(pw-18)).append('<span class="err-icon err-icon-warn"></span>'));
        UIMaster.browser.mozilla && msg.textOverflow("...");
    }
}
/**
 * @description Clear the widget's constraint display.
 * @param {String} id Widget's id.
 */
function clearConstraint(id){
    var t1 = document.getElementById(id) || document.getElementsByName(id)[0];
    if (t1) {
        var t1ClassName = t1.className;
        var suffixIndex = t1ClassName.lastIndexOf(CONSTRAINT_STYLE_SUFFIX);
        var index = t1ClassName.substring(0,suffixIndex).lastIndexOf(" ");
        if (suffixIndex != -1)
            t1.className = t1ClassName.substring(index, suffixIndex) + " " + t1ClassName.substring(suffixIndex+CONSTRAINT_STYLE_SUFFIX.length+1);
        else
            if ((t1.style.backgroundImage).replace(/"/g, "") == "url(" + RESOURCE_CONTEXTPATH + CONSTRAINT_BACKGROUND_IMAGE + ")")
                t1.style.backgroundImage = t1.defaultBackgroundImage?t1.defaultBackgroundImage:"none";
        if (t1.oldInvalidF == true)
            t1.oldInvalidF = false;
        if ((t1.parentDiv) || (!t1.parentDiv && t1.parentNode.nodeName=='P')) $(t1.parentDiv!=null?t1.parentDiv:t1.parentNode.parentNode).children('.err-field-warn').remove();
    }
}

function validateAll(event){
    var component = UIMaster.getObject(event);
    for (var i = 0; i < component.validationList.length; i++) {
        var param = component.validationList[i][0];
        param.currentComp = component;
        var validateFunc = component.validationList[i][1];
        //
        var components = param.components;
        if (components == undefined) {
            if (!validateFunc.call(param))
                return false;
            clearConstraint(component.name);
        }
        else {
            for (var j = 0, n = components.length; j < n; j++) {
                if (!validateFunc.call(param))
                    return false;
                clearConstraint(components[j].name);
            }
        }
    }
    return true;
}
Array.prototype.remove=function(dx){
    if(isNaN(dx)||dx>this.length){return false;}
    for(var i=0,n=0;i<this.length;i++)
    	if(this[i]!=this[dx])
    		this[n++]=this[i]
    this.length-=1;
}
function isArrayEquals(a, b){
    if (a == b)
        return true;
    if (typeof(a) != typeof(b))
        return false;
    if (typeof(a) == "function")
        return a.toString() == b.toString();
    if (typeof(a) == "object") {
        if (a.constructor == Array && b.constructor == Array) {
            if (a.length == b.length) {
                for (var i = 0; i < a.length; i++)
                    if (!isArrayEquals(a[i], b[i]))
                        return false;
                return true;
            }
            return false;
        }
        if (a.constructor != Array && b.constructor != Array) {
            if (typeof(a) == "object" && typeof(b) == "object") {
                return a.equals(b);
            }
            else
                if (typeof(a) != "object" && typeof(b) != "object")
                    return a == b;
            return false;
        }
    }
    return false;
}

function registerConstraint(ui){
    try {
        //memorize default background image
        ui.defaultBackgroundImage = ui.defaultBackgroundImage || (UIMaster.browser.msie ? ui.currentStyle.backgroundImage : document.defaultView.getComputedStyle(ui, null).getPropertyValue("background-image"));
    }
    catch (e) {
    }

    try{
        ui.removeListener("mouseout",validateAll,false);
        ui.addListener("mouseout",validateAll,false);
        ui.removeListener("blur",validateAll,false);
        ui.addListener("blur",validateAll,false);
    }catch(e){}
}

function getInputUI(uientity){
    for (var el in uientity) {
        if (el == "parentEntity" || el == "Form" || el == "name" || el == "validators")
            continue;
        var e = eval("uientity." + el);

        if (e == undefined || (e.style != undefined && e.style.display == "none") || typeof(e) != "object")
            continue;
        if (e.tagName == undefined) {
            var e2 = getInputUI(e);
            if (e2 != null) {
                return e2;
            }
        }
        if (e.type == "text" || e.type == 'password' || e.type == "checkbox" || e.type == 'radio' || e.type == 'textArea' || e.type == 'select-one' || e.type == 'select-multiple')
            return e;
    }
    return null;
}

function getUIValue(uientity){
    if (uientity.type == "checkbox" || uientity.type == 'radio' || uientity.type == 'select-multiple') {
        //to do
    }
    else
        return uientity.type != undefined ? uientity.value : null;
}

function setUIValue(uientity, value){
    if (uientity.type == "checkbox" || uientity.type == 'radio' || uientity.type == 'select-multiple') {
    }
    else
        (uientity.type != undefined) && (uientity.value = value);
}

function addEvent(obj,type,fn) {
    if (obj.addEventListener)
        obj.addEventListener(type,fn,false);
    else if (obj.attachEvent)
        obj.attachEvent("on"+type, fn);
}

function removeEvent(obj,type,fn) {
    if (obj.removeEventListener)
        obj.removeEventListener(type,fn,false);
    else if (obj.detachEvent)
        obj.detachEvent("on"+type, fn);
}

function retrieveFormValue(){
    var map = [], action = "", arr;

    if (arguments.length == 1) {    // example: retrieveFormValue(frameid), for multi-frame
        action = $("#"+arguments[0]).attr("action");
        map["_frameId"] = arguments[0];     // first reserved value
    } else if (arguments.length == 0) {    // default: retrieveFormValue(), for non-frame
        action = $("form").eq(0).attr("action");
        map["_frameId"] = "";
    }

    map["_actionUrl"] = action;    // second reserved value
    arr = (action.substring(action.indexOf('?')+1)).split("&");
    for (var i=0; i<arr.length; i++) {
        var s = arr[i].split('=');
        map[s[0]]=s[1];
    }
    return map;
}

function containXSS(s) {
    var reg = /(<\s*script\s*>)|(javascript:)|(eval\()|(&#\d+)|(&amp;)/i;
    return reg.test(s);
}

/**
 * @description Precision addition due to the javascript's float operation bug.
 * @param {Number} arg1 First number to add.
 * @param {Number} arg2 Second number to add.
 * @returns {Number} Result.
 */
function accAdd(arg1,arg2) {
    var r1,r2,m;
    try{r1=arg1.toString().split(".")[1].length}catch(e){r1=0}
    try{r2=arg2.toString().split(".")[1].length}catch(e){r2=0}
    m=Math.pow(10,Math.max(r1,r2));
    return Math.round(arg1*m+arg2*m)/m;
}
/**
 * @description Precision subtraction due to the javascript's float operation bug.
 * @param {Number} arg1 First number, it's subtrahend.
 * @param {Number} arg2 Second number, it's minuend.
 * @returns {Number} Result.
 */
function accSub(arg1,arg2) {
    return accAdd(arg1,-arg2);
}
/**
 * @description Precision multiplication due to the javascript's float operation bug.
 * @param {Number} arg1 First number to multiply.
 * @param {Number} arg2 Second number to multiply.
 * @returns {Number} Result.
 */
function accMul(arg1,arg2) {
    var m=0,s1=arg1.toString(),s2=arg2.toString();
    try{m+=s1.split(".")[1].length}catch(e){}
    try{m+=s2.split(".")[1].length}catch(e){}
    return Number(s1.replace(".",""))*Number(s2.replace(".",""))/Math.pow(10,m);
}
/**
 * @description Precision division due to the javascript's float operation bug.
 * @param {Number} arg1 First number, it's divisor.
 * @param {Number} arg2 Second number, it's dividend.
 * @returns {Number} Result.
 */
function accDiv(arg1,arg2) {
    var t1=0,t2=0,r1,r2;
    try{t1=arg1.toString().split(".")[1].length}catch(e){}
    try{t2=arg2.toString().split(".")[1].length}catch(e){}
    r1=Number(arg1.toString().replace(".",""));
    r2=Number(arg2.toString().replace(".",""));
    return accMul((r1/r2), Math.pow(10,t2-t1));
}
function sideBar(parentPanel, leftPanel, rightPanel) {
	var children = $("#"+parentPanel).children();
	if (children.length < 2) {
		if (elementList[parentPanel]) {
			children = $(elementList[parentPanel]).children();
		}
		if (children.length < 2) {
			alert("Side bar requires two panels defined.");
			return;
		}
	}
	var setwidth = function(parentPanel) {
		var p = $(elementList[parentPanel]);
		var rp = $(elementList[rightPanel]);
		var lp = $(elementList[leftPanel]);
		
		var leftPanelCell = $(children[0]);
		var rightPanelCell = $(children[1]);
		var readWidth = p.width() - leftPanelCell.width() - 18;
		if (readWidth > 0) { 
			rightPanelCell.css("width", readWidth); 
		} 
		
		var b = p.parents("body:first");
		//while(p[0].tagName.toLowerCase()!="form") {
			//p = $(p).parent();
		//}
		var bodyheight = $(b).height();
		var complement = $(window).height() - bodyheight - 20;
		var realHeight =  rp.height() + complement;
		if (realHeight > parseInt(rp.css("min-height"))) {
			lp.css("height", realHeight);
			rp.css("height", realHeight);
		}
		var frames = rightPanelCell.find("iframe");
		frames.each(function(){
			$(this).css({"height":(rightPanelCell.height() - 60) + "px"}); 
		});
	}
	setwidth(parentPanel);//do it first normally.
	var trick = function(parentPanel) {
		return function() {setwidth(parentPanel);};
	}
	setTimeout(trick(parentPanel), 500);//do the second for correcting the wrong layout.
	// the initial height settle down to the parent cell.
	var height = $("#"+parentPanel).parent().css("min-height");
	$("#"+leftPanel).css("min-height", height);
	$("#"+rightPanel).css("min-height", height);
};
/**
 * @description Parsing the string to one number.
 * @function
 * @param {String} str String to parse.
 * @returns {Number} Parsed number.
 * @example
 * 00,900.099e+2 -> 90009.9
 */
function parseNumber(str)
{
    var int_part, float_part, e_part, e_part_full, e_isExist, result, reg_result, is_positive_int, is_positive_e, regexp_str = "([-|+]?)([0-9,]*)([\.]?)([0-9,]*)(([e|E])([-|+]?)(([0]*)([1-9]?[0-9]*)))?", regexp = new RegExp(regexp_str);;
    /*
     * Parsing the string to three parts:
     * 1. int_part: contains 0-9 and ","
     * 2. float_part: after the "." and contains 0-9
     * 3. e_part: after the "e" or "E" and contains 0-9
     */
    reg_result = regexp.exec(str);
    if(reg_result == null)
        return null;
    if(reg_result[0].length != str.length )
        return null;
    int_part = reg_result[2];
    float_part = reg_result[4];
    e_isExist = reg_result[6];
    e_part_full = reg_result[8];
    e_part = reg_result[10];
    //Detecting the positive or negtive of the int part and e part
    if(reg_result[1] == "-")
        is_positive_int = 0;
    else
        is_positive_int = 1;
    if(reg_result[7] == "-")
        is_positive_e = 0;
    else
        is_positive_e = 1;
    /*
     * Parsing the int_part
     * 1. Get all 0-9 and remove ","
     * 2. Remove the top "0"s
     * 3. Using parseInt parsing it
     */
    int_part = int_part.replace(/,/g, "");
    var regexp_int = new RegExp("([0]*)([0-9]*)"), reg_result_int = regexp_int.exec(int_part);
    if(reg_result_int[1].length == 0 && reg_result_int[2].length == 0)
        return null;
    else if(reg_result_int[2].length == 0)
        int_part = 0;
    else
        int_part = parseInt(reg_result_int[2]);
    /*
     * Parsing the float_part
     * Get all numbers
     * 1. Remove ","
     * Notice: If no float_part and exist ".", the float_part is 0. i.e. 123.e2 -> 12300
     */
    float_part = reg_result[4].replace(/,/g, "");

    /*e_isExist.length == 0 ||
     * Parsing the e_part
     * 1. Remove the top "0"s
     * Notice: If only exist "0", the e_part is 0. i.e. 123e0 ->123
     *         If exist "e" and no other number after it, Illegal input. 123e+ -> null
     */
    if(e_isExist == null)
        e_part = 0;
    else if(e_isExist.length == 0)
        e_part = 0;
    else if(e_part_full.length == e_part.length && e_part.length == 0)
        return null;
    else if(e_part.length == 0 )
        e_part = 0;
    /*
     * Get the final result
     */
    result = "" + int_part + "." + float_part;
    result = parseFloat(result);
    if(isNaN(result))
        return null;
    if(is_positive_e == 0)
        e_part *= -1;
    result = accMul(result, Math.pow(10, e_part));
    if(is_positive_int == 0)
        result *= -1;

    return result;
}
(function(){
/**
 * @description UIMaster core utilities and functions.
 * @namespace Holds all functions and variables.
 */
var UIMaster = {
    emptyFn:function(){},
    funclist:[],
    csslist:[],
    framelist:[],
    handler:[],
    syncList:[],
    initList:[],
    /**
     * @description Util methods.
     * @namespace Hold some util method.
     */
    util:{}};
if (window.UIMaster == undefined) {
    window.UIMaster = UIMaster;
    //identify if any error happens when ajax operation
    window.ajax_execute_onerror = false;
} else
    return;
undefined;

/**
 * @description Provides some browser related constants to current page.
 * @namespace Defines some browser features.
 */
UIMaster.browser = {
    /**
     * @description Describe the version of current browser.
     * @type String
     */
    version: (userAgent.match(/.*?(?:rv|it|ra|ie)[\/: ]([\d.]+)/) || [])[1],
    /**
     * @description True if this is a Webkit based browser.
     * @type Boolean
     */
    safari: /webkit/.test(userAgent),
    /**
     * @description True if this is a Opera browser.
     * @type Boolean
     */
    opera: /opera/.test(userAgent),
    /**
     * @description True if this is a Trident(IE) based browser.
     * @type Boolean
     */
    msie: /msie/.test(userAgent) && !/opera/.test(userAgent),
    /**
     * @description True if this is a Gecko based browser.
     * @type Boolean
     */
    mozilla: /mozilla/.test(userAgent) && !/(compatible|webkit)/.test(userAgent)
};
/**
 * @description Initialize some variables and functions in the system.
 * @ignore
 */
UIMaster.init = function(){
    UIMaster.getAllScript();
    $('input[name="__resourcebundle"]').each(function(){
        UIMaster.i18nmsg.put($(this).val(),g(this,'msg'));
    });
};
/**
 * @description Acquire a JavaScript file from the server. This method is synchronized.
 * @param {String} jsName The JavaScript file names which need to be downloaded.
 * @param {Boolean} [nocheck] If set to true, this will not apply a timestamp to the request url. Otherwise, it will force to download the latest script.
 */
UIMaster.require = function(_jsName, _nocheck){
    _jsName = (_jsName.indexOf('/') == 0 ? '' : '/') + _jsName;
    if (_jsName.search(RESOURCE_CONTEXTPATH) != 0) {
        _jsName = RESOURCE_CONTEXTPATH + _jsName;
    }
    if (!UIMaster.funclist[_jsName]){
        var head = document.getElementsByTagName("head")[0] || document.documentElement, script = document.createElement("script"), data = new bmiasia_UIMaster_appbase_AjaxClient(_jsName + (_nocheck ? "" : ("?_timestamp=" + new Date().getTime()))).submitAsString();
        script.type = "text/javascript";
        UIMaster.browser.msie ? (script.text = data) : script.appendChild( document.createTextNode( data ) );
        head.insertBefore( script, head.firstChild );
        head.removeChild( script );
        UIMaster.funclist[_jsName] = 'finished';
    }
};
/**
 * @description Make a list of all scripts loaded in current page.
 * @ignore
 */
UIMaster.getAllScript = function(){
    var scripts = document.getElementsByTagName("script"), pattern = /^https?\:\/\/[^\/]*([\/\S*]*)$/;
    for (var i=0;i<scripts.length;i++){
        scripts[i].src && (UIMaster.funclist[scripts[i].src] = 'finished');
    } // UIMaster.browser.msie?scripts[i].src:pattern.exec(scripts[i].src)[1]
	return UIMaster;
};
UIMaster.addResource = function(pageName){
	/**
    $(document).ready(function(){
    var form = $('<form></form>')
            .css({"marginBottom":"0px",'width':'95px'})
            .attr('action',WEB_CONTEXTPATH+"/jsp/Resource.jsp")
            .append($('<div></div>')
                .css({'border-bottom':'1px solid'})
                .append($('<span>Export Resource</span>').attr('title',pageName).css({'cursor':'pointer'}))
                .append($('<input type="hidden"/>').attr({'name':'page','value':pageName}))
                .click(function(){form.submit()}));
    if ($("div[name=resource]").length > 0) {
        $("div[name=resource]").append(form);
    } else {
        $('<div name="resource"></div>').css({'position':'absolute','top':'5px','left':$(document.body).width()-120,'zIndex':'500','background':'#3399cc','opacity':'.85','filter':'alpha(opacity=85)','color':'white','padding':'5px'}).append(form).appendTo($(document.body));
    }
    UIMaster.require('js/controls/dragdrop.js');
    $("div[name=resource]").draggable();
    });
    */
};
/**
 * @description Get an element from the page.
 * @param {String|Node} id Id of the element or the DOM node.
 * @returns {UIMaster.El} An UIMaster.El element which represents the DOM element.
 * @class Represents an element.
 * @constructor
 */
UIMaster.El = function(id){
    return new UIMaster.El.fn.init(id);
};
/**
  * @description Prototype of the UIMaster.El. Providing function definitions.
  */
UIMaster.El.fn = {
    jqObj: null,
    /**
     * @constructs
     * @lends UIMaster.El
     */
    init: function(id){
        if (typeof id == "string")
            this.jqObj = $(document.getElementById(id) || document.getElementsByName(id));
        else
            if (id && id.nodeType)
                this.jqObj = jQuery(id);
    },
    /**
     * @description Get the parent of current element.
     * @returns {UIMaster.El} An UIMaster.El element which represents the parent node of current element.
     */
    getParent: function(){
        var p = new UIMaster.El();
        p.jqObj = this.jqObj.parent();
        return p;
    },
    /**
     * @description Get a DOM node of current node.
     * @param {Number} [i] Index of the node which wants to get.
     * @returns {Node} An UIMaster.El element which represents the parent node of current element.
     */
    get: function(i){
        return i ? this.jqObj.get(i) : this.jqObj.get(0);
    },
    /**
     * @description Get the children of current element.
     * @returns {UIMaster.El} An UIMaster.El element which represents the children of current element.
     */
    getChildren: function(){
        var p = new UIMaster.El();
        p.jqObj = this.jqObj.children();
        return p;
    },
    /**
     * @description Get the children nodes of current element.
     * @returns {Array} An array of children nodes of current element.
     */
    getArray: function(){
        return this.jqObj.get();
    },
    /**
     * @description Get position of current element.
     * @example
     * var pos = element.getPosition();
     * var top = pos.top;
     * var left = pos.left;
     * var width = pos.width;
     * var height = post.height;
     * @returns {Object} An object contains position information of current element.
     */
    getPosition: function(){
        var obj = this.jqObj.offset();
        obj.top = obj.top-$(document).scrollTop();
        obj.left = obj.left-$(document).scrollLeft();
        obj.width = this.jqObj.width();
        obj.height = this.jqObj.height();
        return obj;
    },
    /**
     * @description Add a listener to the element.
     * @param {String} type An event type.
     * @param {Function} fn A reference to a JavaScript function.
     */
    addListener: function(t, fn){
        this.jqObj.bind(t, fn);
    },
    /**
     * @description Remove a listener from the element.
     * @param {String} type An event type.
     * @param {Function} fn A reference to a JavaScript function.
     */
    removeListener: function(t, fn){
        this.jqObj.unbind(t, fn);
    },
    /**
     * @description Trigger an event on the element.
     * @param {String} type An event type.
     */
    trigger: function(t){
        this.jqObj.trigger(t);
    },
    /**
     * @description Set a single property to a value.
     * @param {String} key The name of the property to set.
     * @param {Object} value The value to set the property to.
     */
    setAttr: function(k, v){
        this.jqObj.attr(k, v);
    },
    /**
     * @description Remove an attribute from the element.
     * @param {String} key The name of the property to remove.
     */
    removeAttr: function(k){
        this.jqObj.removeAttr(k);
    },
    /**
     * @description Access a property from the element.
     * @param {String} key The name of the property to access.
     */
    getAttr: function(k){
        return this.jqObj.attr(k);
    },
    /**
     * @description Insert the content after the element.
     * @param {String|Node} content Content to insert after the element.
     */
    after: function(v){
        var p = new UIMaster.El();
        p.jqObj = this.jqObj.after(v);
        return p;
    },
    /**
     * @description Insert the content before the element.
     * @param {String|Node} content Content to insert before the element.
     */
    before: function(v){
        var p = new UIMaster.El();
        p.jqObj = this.jqObj.before(v);
        return p;
    },
    /**
     * @description Remove current element.
     */
    remove: function(){
        this.jqObj.remove();
    },
    /**
     * @description Append the content inside the element.
     * @param {String|Node} content Content to append to the element.
     */
    append: function(v){
        var p = new UIMaster.El();
        p.jqObj = this.jqObj.append(v);
        return p;
    },
    /**
     * @description Prepend the content inside the element.
     * @param {String|Node} content Content to prepend to the element.
     */
    prepend: function(v){
        var p = new UIMaster.El();
        p.jqObj = this.jqObj.prepend(v);
        return p;
    },
    /**
     * @description Replace this element with the content
     * @param {String|Node} content Content to replace the element with.
     */
    update: function(v){
        var p = new UIMaster.El();
        p.jqObj = this.jqObj.replaceWith(v);
        return p;
    }
};
UIMaster.El.fn.init.prototype = UIMaster.El.fn;
/**
 * @description Copies all the properties of config to obj.
 * @param {Object} obj The receiver of the properties
 * @param {Object} config The source of the properties
 * @param {Boolean} valid If true, will only copy undefined members or functions in the receiver.
 */
UIMaster.apply = function(o, c, v){
    if (o && c && typeof c == 'object')
        for (var m in c)
            if (v)
                (o[m]==undefined || typeof o[m] == "function") && (o[m] = c[m]);
            else
                o[m] = c[m];
};
/**
 * @description Assign attributes with same value to an object.
 * @param {Object} object The receiver of the properties.
 * @param {Array} values The values array.
 * @example
 * var options = [[['var1','var2','var3'],0],['var4','var5','var6'],1];
 * var target = {};
 * UIMaster.groupAssign(target, options);
 * //target.var1 -> 0
 * //target.var4 -> 1
 */
UIMaster.groupAssign = function(obj, nv){
    for (var i=0; i < nv.length; i++)
        for (var n=0; n < nv[i][0].length; n++)
            obj[nv[i][0][n]] = nv[i][1];
};
/**
 * @description An alias to UIMaster_getI18NInfo.
 * @function
 * @param {String} keyInfo Key of the bundle.
 * @param {Array} param Params applied to the bundle.
 * @param {String} languageType Language info needs to transfer.
 * @returns {String} Internationalized message.
 */
UIMaster.i18nmsg = UIMaster_getI18NInfo;
/**
 * @description Synchronize widget attribute to the server. This method is synchronized.
 * @param {String} uiid ID of the element.
 * @param {String} name Name of the attribute.
 * @param {String} value Value of the attribute.
 */
UIMaster.synAttr = function(uiid, name, value){
    $.ajax({url:AJAX_SERVICE_URL,
            asnyc:false,
            data:{_ajaxUserEvent:false,_uiid:uiid,_valueName:name,_value:value,_framePrefix:UIMaster.getFramePrefix()}});
};
/**
 * @description Extend a class with configurations.
 * @param {Function} sp Super Class.
 * @param {Object} c Configuration items.
 * @example
 * var a = function(){};
 * var b = UIMaster.extend(a, {
 *         b: function(){
 *                alert('My item');
 *            };
 * });
 * (new a()).b();   // Object doesn't support this property or method.
 * (new b()).b();   // My item.
 * @returns {Function} Sub-class.
 */
UIMaster.extend = function(sp, c){
    var sb = function(conf){
        return sp.apply(this, arguments);
    }, F = function(){};
    F.prototype = sp.prototype;
    sb.prototype = new F();
    sb.prototype.constructor = sb;
    sb.superclass = sp.prototype;
    UIMaster.apply(sb.prototype, c);
    return sb;
};
/**
 * @description Get name from an UIMaster object. This method is for UIMaster specifically.
 * @param {Object} obj HTML element.
 * @returns {String} Name of the element.
 */
UIMaster.getName = function(obj){
    var name = (typeof obj.name == "string") ? obj.name : obj.id;
    if (name) {
        var n = name.split('.'), i = n[n.length - 1] == "Form" ? 2 : 1;
        return n[n.length - i];
    }
    return null;
};
/**
 * @description Get object from the event. It is the source element of event, and from element of the mouseover and mouseout event.
 * @param {Event} event Event object.
 * @returns {Object} Get the event source object from the event.
 */
UIMaster.getObject = function(event){
    event = event || window.event;
    if (!event) return;
    if (!(event.srcElement || event.target)) return event;
    if (event.type == "mouseover" || event.type == "mouseout")
        return event.fromElement ? event.fromElement : event.target;
    else
        return event.srcElement ? event.srcElement : event.target;
};
/**
 * @description Whether the the target array is contained in the source array.
 * @param {Array} tgtArray Target Array.
 * @param {Array} srcArray Source Array.
 * @returns {Boolean} The target array is contained in the source array or not.
 */
UIMaster.arrayContain = function(tgtArray, srcArray){
    if (tgtArray instanceof Array && srcArray instanceof Array) {
        var str = srcArray.join(),i;
        if (str == tgtArray.join()) return true;
        for (i = 0; i < tgtArray.length; i++)
            if (str.indexOf(tgtArray[i]) == -1 || tgtArray[i] == "")
                return false;
        return true;
    }
    return false;
};
/**
 * @description Telling the page float should be left to right.
 * @returns {Boolean} The page float is left to right.
 */
UIMaster.util.isLeftToRight = function(){
	var ltr = document.getElementById('isLeftToRight');
	if(ltr)return (ltr.value == 'true'? true : false);
	return true;
};
/**
 * @description Extract a multi-dimension error messages array to an one dimension array.
 * @ignore
 * @returns {Array} A one dimension array including the error messages.
 */
UIMaster.util.retrieveErrMsg = function(constraint){
	var msg = [];
	var recursive = function(o){
		if (typeof o[o.length-1] == "string")
			msg[msg.length] = o.join(". ");
        else
			for(var i=0; i<o.length; i++) {
				if ((i==0)&&(typeof o[0] == "string"))
					continue;
				recursive(o[i]);
			}
	};

	if(constraint instanceof Array)
		recursive(constraint);
	else
		msg[0] = constraint;
	return msg;
};
/**
 * @description Register an AJAX handler to handle UIMaster AJAX operations.
 * @param {String} name Handler's name.
 * @param {Function} handler Function handler. <br/> There will be two parameters passed to the handler. First one is the data passed from the server, the second one is the window object of that operation.
 * @returns {UIMaster} UIMaster object.
 */
UIMaster.registerHandler = function(name,handler){
    UIMaster.handler[name]=handler;
    return UIMaster;
};
UIMaster.registerHandler("fadeOut", function(data,win){
    UIMaster.El(data.uiid).jqObj.fadeOut( (data.speed && typeof data.speed=="number") ? data.speed : 5000 );
}).registerHandler("pageReSubmit",function(data,win){
    UIMaster.ui.mask.close();
    var sync = $('<input type="hidden" />').attr({'id':"_sync",'value':data.data});
    var _form = (data.parent=="null" || data.parent=="") ? $("form:first") : $("form[_frameprefix='"+data.parent+"']");
    _form.prepend(sync).submit();
}).registerHandler("permitSubmit",function(data,win){
    UIMaster.ui.mask.close();
    var _form = $("form:first"), _target = ((!data.parent||data.parent=="null") ? "_self" : data.parent);
    // data.frameInfo stores source form/frame name, and data.parent stores target form/frame name
    var act = _form.attr("action") + "&_htmlkey=" + data.data;
    _form.attr("target",_target).attr("action", act).submit();
}).registerHandler("appendError",function(data,win){
    // if any error found, update mask
    UIMaster.ui.mask.close();
    UIMaster.clearErrMsg(); 
    if (data instanceof Object) {
        var box, bw, m, t, le, p;
        m = $('<div class="err-page-warn clearfix"></div>'), tip = $('<div></div>');
        (data.image) ? tip.append('<div style="float: left;"><img src="'+RESOURCE_CONTEXTPATH+'/images/Error.png" /></div>') : tip.append('<div class="err-icon" style="float: left;"></div>');
        if (data.errorMsgTitle) tip.append('<div class="err-title">'+data.errorMsgTitle+'</div>');
        if (data.errorMsgBody) tip.append('<div class="err-body">'+data.errorMsgBody+'</div>');
        t = $("#"+data.uiid);
        if ( t.length == 0 ) {
            t = $("[name='"+data.uiid+"']");
        }
        le = data.uiid.lastIndexOf(".");
        if ( le == -1) {
            p = $("#Form");
        } else {
            p = $("#" + data.uiid.substring(0, le + 1).replace(".","\\.") + "Form");
        }
        p.prepend(m.append(tip));
        if (data.exceptionTrace) {
            tip.append($('<span id="showTraceImg" style="float:right;cursor:pointer;" class="ui-button-icon-primary ui-icon ui-icon-arrow-1-s" alt="Show trace"></span>').bind('click', function(){
                var t = $(this);
                if (t.hasClass("ui-icon-arrowthick-1-n")) {
                	t.removeClass("ui-icon-arrowthick-1-n");
                	t.addClass("ui-icon-arrowthick-1-s");
                } else {
                	t.addClass("ui-icon-arrowthick-1-n");
                	t.removeClass("ui-icon-arrowthick-1-s");
                }
                $('textarea[id=traceArea]').toggle();
            }));
            m.append('<textarea id="traceArea" style="display:none;clear:both;width:98%;" rows="20">'+data.exceptionTrace+'</textarea>');
        }
        if (data.uiid) constraint(data.uiid, data.errorMsgTitle);
    }
}).registerHandler("append",function(data,win){
    if (data.data){
        var n = $(win.eval(D+data.parent)).children(),k = data.parent.split(".");
        if (win.eval(D+data.parent)){
            win.getElementListSingle($(n.get(n.length-1)).before(data.data).get(0).previousSibling);
            win.eval(data.js);
            k[k.length-1]="Form";
            win.eval(D+k.join(".")+".parentEntity").addComponent(win.eval(D+data.uiid),true);
        }
    }else{
        var n = $(win.eval(D+data.parent)).children();
        $(n.get(n.length-1)).before(win.eval(D+data.uiid).parentDiv);
    }
}).registerHandler("prepend",function(data,win){
    if (data.data){
        if (win.eval(D+data.parent)){
            win.getElementListSingle(win.eval("UIMaster.El(defaultname."+data.parent+")").prepend(data.data).get(0));
            win.eval(data.js);
            var k = data.parent.split(".");
            k[k.length-1]="Form";
            win.eval(D+k.join(".")+".parentEntity").addComponent(win.eval(D+data.uiid),true);
        }
    }else{
        $(win.eval(D+data.parent)).prepend(win.eval(D+data.uiid).parentDiv);
    }
}).registerHandler("before",function(data,win){
    if (data.data){
        if (win.eval(D+data.sibling+".parentDiv")){
            win.getElementListSingle(win.eval("UIMaster.El(defaultname."+data.sibling+".parentDiv)").before(data.data).get(0).previousSibling);
            win.eval(data.js);
            win.eval(D+data.sibling+".parentEntity").addComponent(win.eval(D+data.uiid),true);
        }
    }else{
        $(win.eval(D+data.sibling+".parentDiv")).before(win.eval(D+data.uiid+".parentDiv"));
    }
}).registerHandler("after",function(data,win){
    if (data.data){
        if (win.eval(D+data.sibling+".parentDiv")){
            win.getElementListSingle(win.eval("UIMaster.El(defaultname."+data.sibling+".parentDiv)").after(data.data).get(0).nextSibling);
            win.eval(data.js);
            win.eval(D+data.sibling+".parentEntity").addComponent(win.eval(D+data.uiid),true);
        }
    }else{
        $(win.eval(D+data.sibling+".parentDiv")).after(win.eval(D+data.uiid+".parentDiv"));
    }
}).registerHandler("remove",function(data,win){
    var node = win.eval(D+data.uiid), i;
    for (i in win.elementList)
        if (i.indexOf(data.uiid) == 0 && (i == data.uiid || i.indexOf(data.uiid + '.') == 0))
            delete win.elementList[i];
    if (node) {
	    node.parentEntity.removeComponent(node);
	    win.UIMaster.El(node.parentDiv).remove();
	    node.parentDiv = node.parentEntity = null;
	    delete node;
    }
}).registerHandler("openwindow",function(data,win){
    var config = win.eval('('+data.sibling+')');
    win.UIMaster.apply(config,{
        js:data.js,
        data:data.data,
        uiid:data.uiid,
        parent:data.parent,
        frameInfo:data.frameInfo});
    new win.UIMaster.ui.window(config).open();
}).registerHandler("closewindow",function(data,win){
    win.UIMaster.ui.window.getWindow(data.uiid).close();
}).registerHandler("opendialog",function(data,win){
    new win.UIMaster.ui.dialog(eval('('+data.data+')')).open();
}).registerHandler("update_attr",function(data,win){
    var e,i;
    for (i in win.elementList)
        if (i.indexOf(data.uiid) == 0)
        	e = win.elementList[i];
    if(!e)
    	 return;
	var attribute = win.eval("("+data.data+")");
	e.addAttr(attribute);
}).registerHandler("remove_attr",function(data,win){
    var e,i;
    for (i in win.elementList)
        if (i.indexOf(data.uiid) == 0)
        	e = win.elementList[i];
    if(!e)
    	 return;
	e.removeAttr(data.name);
}).registerHandler("update_event",function(data,win){
	var attribute = win.eval("("+data.data+")");
	win.$('#'+data.uiid).bind(attribute.name, attribute.value);
}).registerHandler("remove_event",function(data,win){
	win.$('#'+data.uiid).unbind(data.name);
}).registerHandler("update_style",function(data,win){
	var attribute = win.eval("("+data.data+")");
	win.$('#'+data.uiid).addClass(attribute.name, attribute.value);
}).registerHandler("remove_style",function(data,win){
	win.$('#'+data.uiid).removeClass(data.name);
}).registerHandler("update_const",function(data,win){
	if (data.readonly == "true") {
		return;
	}
	var attribute = win.eval("("+data.data+")");
	win.$('#'+data.uiid).validator=attribute.validator;
	win.$('#'+data.uiid).validators=attribute.validators;
}).registerHandler("remove_const",function(data,win){
	if (data.readonly == "true") {
		return;
	}
	var attribute = win.eval("("+data.data+")");
	win.$('#'+data.uiid).validators=attribute.validators;
}).registerHandler("update_readonly",function(data,win){
	var attribute = win.eval("("+data.data+")");
	UIMaster.handler["remove"](data, win);
	UIMaster.handler["append"](data, win);
}).registerHandler("hardreturn",function(data,win){
    win.$('#'+data.uiid).append(data.data);
}).registerHandler("javaobject",function(data,win){
    return data.data;
}).registerHandler("sessiontimeout",function(data){
    location.replace(location.protocol + '//' + location.host + WEB_CONTEXTPATH + data.data);
}).registerHandler("js",function(data,win){
    win.eval(data.js);
}).registerHandler("updateSingle",function(data,win){
    //D + data.uiid + style? + data.attr = data.value
}).registerHandler("table_update",function(data,win){
	var tdata = win.eval("("+data.data+")"),id = data.uiid,uitable,i;
    for (i in win.elementList)
        if (i.indexOf(id) == 0)
        	uitable = win.elementList[i];
    if(!uitable)
    	 return;
    uitable.refreshFromServer(tdata);
}).registerHandler("tabPaneHandler",function(data,win){
    var tabdata = win.eval("("+data.data+")"),id = data.uiid,uitab,i;
    for (i in win.elementList)
        if (i.indexOf(id) == 0)
            uitab = win.elementList[i];
    if(!uitab)
        return;
    switch(tabdata.cmd){
        case "addTab":
            uitab.addTab(tabdata.entity, tabdata.title, tabdata.index);
            break;
        case "removeTab":
            uitab.removeTab(tabdata.index);
            break;
        case "setBody":
            uitab.setTabAt(tabdata.entity, tabdata.index);
            break;
        case "setTitle":
            uitab.setTitleAt(tabdata.title, tabdata.index);
            break;
        case "setSelectedIndex":
            uitab.setSelectedTab(tabdata.index);
            break;
    }
    win.eval(data.js);
}).registerHandler("tab_append",function(data,win){
	var id = data.parent,uitab,i;
    for (i in win.elementList)
        if (i.indexOf(id) == 0)
            uitab = win.elementList[i];
    if(!uitab)
        return;
    var c = uitab.addTabByLazyLoading(data.data, data.uiid);
    c!=null? win.getElementListSingle(c):"";
    win.eval(data.js);
}).registerHandler("uiflowhandler",function(data,win){
    var flowdata = win.eval("("+data.data+")"),id = data.uiid,uiflow,i;
    for (i in win.elementList)
        if (i.indexOf(id) == 0)
            uiflow = win.elementList[i];
    if(!uiflow)
        return;
    switch(flowdata.cmd){
		case "refreshModel":
	    	uiflow.refreshModel(win.eval("("+flowdata.data+")"));
	    	break;
	    case "addNode":
	    	uiflow.addNode(win.eval("("+flowdata.data+")"));
	    	break;
	    case "removeNode":
	    	uiflow.removeNode(flowdata.nodeId);
	    	break;
        case "saveSuccess":
        	uiflow.saveSuccessHint();
            break;
        case "saveFailure":
        	uiflow.saveFailureHint();
            break;
    }
}).registerHandler("tree_refresh",function(data,win){
    var children = win.eval("("+data.data+")"),id = data.uiid,tree,i;
    for (i in win.elementList)
        if (i.indexOf(id) == 0)
            tree = win.elementList[i];
    if(!tree)
        return;
});
/**
 * @description AJAX callback handler.
 * @ignore
 * @param {Object} data AJAX response text.
 * @param {String} status Text Status.
 * @param {String} result Variables passed to the callback handler.
 */
UIMaster.cmdHandler = function(json,status,result){
    function getW(f){
        var fs, w, i;
        w = FRAMEWRAP!="${FRAME_WRAP}"?(function(f){
            var s=f.split('.'),w=window.top,i;
            for (i=0;i<s.length;i++)
                w = w.frames[s[i]];
            return (w && w.window)?w:window;})(FRAMEWRAP):window.top;
        if (f == "")
            return w;
        if (!f)
            return window;
        fs = f.split('.');
        for (i=0;i<fs.length;i++)
        {
            try{w.name}catch(e){return window;}
            w = w.frames[fs[i]];
        }
        return (w && w.window)?w:window;
    }
    var cmds, win, i;
    cmds = json;
    for (i=0;i<cmds.length;i++){
        win = getW(cmds[i].frameInfo);
        if (win.UIMaster.handler[cmds[i].jsHandler])
            win.UIMaster.handler[cmds[i].jsHandler](cmds[i],win);
    }
    if ((cmds.length<=0||cmds[cmds.length-1].jsHandler!="appendError") && arguments.callee.caller.toString().indexOf('postInit')==-1) {
        UIMaster.ui.mask.close();
    }
};
/**
 * @description Append error messages to the panel.
 * @ignore
 * @param {String} sid Failed validation widget name/id.
 * @param {Array} res Error message array.
 */
UIMaster.appendPanelErr = function(sid,res) {
    var tp, s = UIMaster.El(sid).jqObj;
    // panel id, the error message will append to its first children
    var pid = s.parent(".uimaster_panel").attr("id");
    // expand the panel at first
    tp = document.getElementById(pid+".wrapperPanel");
    if (tp && tp.style.display=="none") {
        tp.style.display="", tp.open="true";
        document.getElementById(pid+'.arrowIcon').src=RESOURCE_CONTEXTPATH+'/images/table-open.gif';
    }

    // move focus on the first failed textfield
    $(".err-field-warn:first").siblings("input[type=text],input[type=checkbox],input[type=radio],textarea,select").eq(0).focus();
};
/**
 * @description Clear page error message.
 * @ignore
 */
UIMaster.clearErrMsg = function() {
    $(".err-page-warn").remove();
};
function syncAll() {
    if (defaultname.sync)
        defaultname.sync();
    else
        for (var i in defaultname)
            defaultname[i].sync && defaultname[i].sync();
};
/**
 * @description Trigger an UIMaster AJAX call.
 * @param {String} uiid An id of the triggered object. It should be an event's source object, or some objects on the same panel.
 * @param {String} actionName Server side registered AJAX function name.
 * @param {String} data Data sent to the server. Not in use now.
 * @param {String} entityName The UIEntity name of the action.
 */
UIMaster.triggerServerEvent = function(uiid,actionName,data,entityName,action){
    syncAll();
    
    var opt = {
            async: false,
            url: AJAX_SERVICE_URL,
            type: 'POST',
            data:{_ajaxUserEvent: action==undefined?true:action,
                _uiid: uiid,
                _actionName: actionName,
                _framePrefix: UIMaster.getFramePrefix(UIMaster.El(uiid).get(0)),
                _actionPage: entityName,
                _sync: UIMaster.ui.sync()},
            beforeSend: UIMaster.ui.mask.open(),
            success: UIMaster.cmdHandler
        };

    var opt2 = {};
    if (typeof data.asyncAttr != "undefined") {
        opt2.async = data.asyncAttr; 
    }

    if (typeof data.completeHandler != "undefined") {
        opt2.complete = data.completeHandler; 
    }
    $.ajax(jQuery.extend({}, opt, opt2));
};
/**
 * @description Trigger a two phase submit.
 * @ignore
 * @param {String} destUrl Destination URL.
 * @param {String} pageName Current UIPage name.
 * @param {String} outName Out name of the action.
 * @param {String} framePrefix Frame's prefix of current window.
 * @param {String} portlet Current portlet's ID.
 * @param {String} target The submit's target.
 */
UIMaster.trigger2PhaseSubmit = function(destUrl,pageName,outName,framePrefix,portlet,target){
    //UIMaster.ui.mask.openA(UIMaster.i18nmsg(
    //    "Common||AJAX_PROCESSING"));

    try {
        syncAll();
        var o = new Object();
        o._pagename = pageName;
        o._outname = outName;
        o._framePrefix = framePrefix;
        o._sync = UIMaster.ui.sync();
        o._ajaxSubmit="true";
        // set targetId
        if (target!="_self")
            if (target=="_parent")
                o._frameTarget = window.parent.document.forms[0].getAttribute('_framePrefix') || '';
            else
                o._frameTarget = target;
        

        // set portletId
        if (portlet)
            o._portletId = portlet.value;

        $.ajax({
            async: true,
            type: "POST",
            url: destUrl,
            data: o,
            beforeSend: UIMaster.ui.mask.open(),
            success: UIMaster.cmdHandler
        });
    } catch (e) {
        new UIMaster.ui.dialog({
            message:'An error happens when invoke \nmethod "trigger2PhaseSubmit"',
            messageType:0,
            title:'Javascript Execute Error',
            error:true,
            height:150,
            width:300
        }).open();
        UIMaster.ui.mask.close();
    }
};
/**
 * @description Get the frame's target according to the element.
 * @param {Node} Element node.
 * @returns {String} Frame target.
 */
UIMaster.getFrameTarget = function(elm){
    return frameInternal(elm,"_frameTarget");
};
/**
 * @description Get the frame's prefix according to the element.
 * @param {Node} Element node.
 * @returns {String} Frame prefix.
 */
UIMaster.getFramePrefix = function(elm){
    return frameInternal(elm,"_framePrefix");
};
function frameInternal(elm, name){
    if(elm && elm.form)
        return elm.form.getAttribute(name);
    while (elm && elm.tagName && elm.tagName.toLowerCase()!="form" && elm.getAttribute(name)==null)
        elm = elm.parentNode;
    if (!elm || elm==document || !elm.getAttribute(name))
        return document.forms[0].getAttribute(name);
    return elm.getAttribute(name);
};
/**
 * @description Get the element's ID.
 * @param {Node} Element node.
 * @returns {String} Element's ID.
 */
UIMaster.getUIID = function(widget){
	if (typeof(widget) == 'string') return widget;
    return widget.nodeType ? (widget.id ? widget.id : widget.name) : widget[0].name;
};
/**
 * @description Get the element's value.
 * @param {Node} Element node.
 * @returns {String|Array} Element's value.
 */
UIMaster.getValue = function(widget){
    return widget;
    //return widget.getValue?widget.getValue():'';
};
/**
 * @description Parse a currency string to its number.
 * @param {String} locale Locale string defined in the i18n_data_runconfig.xml.
 * @param {String} format Format name defined in the i18n_data_runconfig.xml.
 * @param {String} text Formatted currency string.
 * @returns {Number} Parsed number.
 */
UIMaster.parseCurrency = function(locale, format, text){
    var r;
    $.ajax({
        async:false,
        url:AJAX_SERVICE_URL,
        data:{serviceName:'CurrencyFormatService',_locale:locale,_format:format,_text:text},
        success:function(data){
            r=data;
        }
    });
    return Number(r);
};
/**
 * @description Format a number to some currency string.
 * @param {String} locale Locale string defined in the i18n_data_runconfig.xml.
 * @param {String} format Format name defined in the i18n_data_runconfig.xml.
 * @param {Number} number Number to format.
 * @returns {String} Formatted currency string.
 */
UIMaster.formatCurrency = function(locale, format, number){
    var r;
    $.ajax({
        async:false,
        url:AJAX_SERVICE_URL,
        data:{serviceName:'CurrencyFormatService',_locale:locale,_format:format,_number:number},
        success:function(data){
            r=data;
        }
    });
    return r;
};
/**
 * @description Trigger an event on a object.
 * @param {Node} obj HTML element.
 * @param {String} evt Event name, e.g, 'click', 'blur'.
 */
UIMaster.runEvent = function(obj, evt){
    if (obj.fireEvent)
        obj.fireEvent('on' + evt);
    else
        if (obj.dispatchEvent) {
            var evnt = document.createEvent('HTMLEvents');
            evnt.initEvent(evt, true, true);
            obj.dispatchEvent(evnt);
        }
};
UIMaster.apply(Array.prototype, {
    validate: function(init){
        var result = [], i;
        for (i = 0; i < this.length; i++) {
            var res = this[i].validate && this[i].validate(init);
            if (res != null)
                result = result.concat(res);
        }
        return result.length == 0 ? null : result;
    },
    init: function(){
        for (var i = 0; i < this.length; i++) {
            this[i].parentEntity = this.parentEntity;
            this[i].arrayIndex = i;
            (this[i].Form || (this[i].init && this[i].ui)) && !this[i].initialized && this[i].init();
        }
    },
    sync: function(){
        for (var i = 0; i < this.length; i++)
            (this[i].Form || (this[i].sync && this[i].ui)) && this[i].sync();
    }
});
function g(t,v){
    return UIMaster.browser.mozilla?t.getAttribute(v):$(t).attr(v);
}
/**
 * @description UI base class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui} An UIMaster.ui element.
 * @class UIMaster UI object.
 * @constructor
 */
UIMaster.ui = function(conf){
    conf = conf || {};
    UIMaster.apply(this, conf);
    UIMaster.apply(this.ui, this,true);
    return this.ui;
};
UIMaster.ui = UIMaster.extend(UIMaster.ui, /** @lends UIMaster.ui */{
    /**
     * @description UI object, a standard HTML element.
     */
    ui: null,
    /**
     * @description Set the widget's visible.
     * @param {Boolean} v
     */
    setVisible: function(v){
        v ? $(this.parentDiv).show() : $(this.parentDiv).hide();
    },
    /**
     * @description Enable the widget.
     * @param {Boolean} v
     */
    setEnabled: function(v){
        this.disabled = !v;
        this.parentDiv && (this.parentDiv.disabled = !v);
        if (v) {
            this.grayWidgetLabelStyle(this.name, false);
        } else {
            this.grayWidgetLabelStyle(this.name, true);
        }
    },
    /**
     * @description Set the widget's label text.
     * @param {String} text Label text.
     */
    setLabelText: function(text){
        var n = this.previousSibling ? (this.previousSibling.nodeType == 1?this.previousSibling:this.previousSibling.previousSibling):null;
        n ? n.childNodes[0].nodeValue = text : $(this).before($('<label></label>').attr({'id':this.name+'_widgetLabel','for':this.name}).addClass("uimaster_widgetLabel").css('display','block').text(text));
    },
    /**
     * @description Add a listener to the widget.
     * @param {String} E Event name.
     * @param {Function} fn Event handler.
     */
    addListener: function(E, fn){
        $(this).bind(E,fn);
    },
    /**
     * @description Remove a listener from the widget.
     * @param {String} E Event name.
     * @param {Function} fn Event handler.
     */
    removeListener: function(E, fn){
        $(this).unbind(E,fn);
    },
    init: function(){
        this.parentDiv = this.parentNode;
    },
    /**
     * @description light/gray the widget label and icon
     */
    grayWidgetLabelStyle: function(name, gray){
        var el = document.getElementById(name) || document.getElementsByName(name)[0];
        if (el) {
            var img = RESOURCE_CONTEXTPATH + USER_CONSTRAINT_IMG;
            if (gray) {
                img = img.substring(0, img.indexOf('.')) + '_gray.gif';
                $(el).parent().find('label[class*=uimaster_widgetLabel]').addClass('uimaster_widgetLabel_gray')
                    .end().find('img[src$="uimaster_constraint.gif"]').attr('src', img);
            } else {
                $(el).parent().find('label[class*=uimaster_widgetLabel]').removeClass('uimaster_widgetLabel_gray')
                    .end().find('img[src$="uimaster_constraint_gray.gif"]').attr('src', img);
            }
        }
    },
	addAttr: function(a){
		$(this).attr(a.name, a.value);
	},
	removeAttr: function(name){
	    $(this).removeAttr(name);
	}
});
/**
 * @description Synchronize the data to the server.
 */
UIMaster.ui.sync = function(){
    var escapable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
         meta = {'\b': '\\b','\t': '\\t','\n': '\\n','\f': '\\f','\r': '\\r','"' : '\\"','\\': '\\\\'},r,i,p=[],v;
    function getR(frame){
        var rl=[],i;
        for (i=0;i<frame.frames.length;i++){
            if (frame.frames[i].UIMaster){
                rl=rl.concat(frame.frames[i].UIMaster.syncList);
                frame.frames[i].UIMaster.syncList=[];
            }
            rl=rl.concat(getR(frame.frames[i]));
        }
        return rl;
    }
    function quote(string) {
        escapable.lastIndex = 0;
        return escapable.test(string) ? '"' + string.replace(escapable, function (a) {
                return typeof meta[a] === 'string' ? meta[a] : '\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
            }) + '"' : '"' + string + '"';
    }
    function str(v){
        var k,a=[];
        for (k in v)
            a.push(quote(k)+':'+quote(typeof v[k] == "string" ? v[k] :String(v[k])));
        return '{'+a.join(',')+'}';
    }
    try{
        r = window.top.UIMaster ? window.top.UIMaster.syncList : [];
        window.top.UIMaster && (window.top.UIMaster.syncList = []);
    }catch(e){r = [];};
    var set = getR(window.top);
    r = r.length ? r.concat(set) : (set.length ? set.concat(r) : []);
    for (i = 0; i < r.length; i++)
        p[i] = str(r[i]) || 'null';
    v = p.length === 0 ? '[]' : '[' + p.join(',') + ']';
    return v;
};
/**
 * @description Set the data to the synchronized queue.
 * @param {Object} data Data to be synchronized.
 */
UIMaster.ui.sync.set = function(data){
    UIMaster.syncList.push(data);
};
function postInit(){
    while(UIMaster.initList.length > 0) {
        var root = UIMaster.initList.shift();
		var items = root.Form.items;
		for (var i = 0; i < items.length; i++) {if (items[i]) {items[i].postInit && items[i].postInit();}}
		root.user_constructor();
	}
};
/**
 * @description UI Field class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.field} An UIMaster.ui.field element.
 * @class UIMaster UI Field.
 * @extends UIMaster.ui
 * @constructor
 */
UIMaster.ui.field = UIMaster.extend(UIMaster.ui, /** @lends UIMaster.ui.field */{
    flag: false,
    validate: null,
    oldInvalidF: false,
    validator: null,
    //private
    validators: null,
    invalidText: null,
    /**
     * @description Disable the validation for this field.
     */
    disableValid: function(){
        this.flag = null;
        clearConstraint(this.name);
    },
    /**
     * @description Enable the validation for this field.
     */
    enableValid: function(){
        this.flag = false;
    },
    /**
     * @description Mark this field as invalid.
     */
    markInvalid: function(){
        constraint(this.name, this.invalidText);
    },
    /**
     * @description Clear this field's invalid mark.
     */
    clearInvalid: function(){
        clearConstraint(this.name);
    },
    /**
     * @description Remove the asterisk mark for this widget.
     */
    removeIndicator: function(){
        $('#' + this.name + 'div').remove();
    },
    /**
     * @description Mark this field as required.
     */
    markRequired: function(){
        setRequiredStyle(UIMaster.getUIID(this));
    },
    /**
     * @description Add a validator for this field.
     * @param {Function} fn Validator function.
     * @param {String} message Error message if the validator fails.
     */
    addValidator:function(fn,message){
        this.validators.push({func:fn,msg:message});
    },
    /**
     * @description Synchronize the field's value.
     */
    sync: function(){
        this.notifyChange(this);
    },
    //private
    validateEvent: function(evnt){
        var obj = UIMaster.getObject(evnt);
        return obj.validate ? obj.validate() : false;
    },
    validateCustConstraint: function(v){
        var r;
        if (v.param) {
            var arr = '', i;
            for (i = 0; i < v.param.length; i++)
                arr += (i == 0 ? '' : ',') + 'v.param[' + i + ']'
            r = new Function('v','c','return v.func.call(c,'+arr+');')(v,this);
        }
        else
            r = v.func.call(this);
        if(typeof r == "string"){
            v.msg = r;
            return !r;
        }
        return r;
    },
    init: function(){
        this.parentDiv = this.parentNode;
        this.addListener('blur', this.validateEvent);
        this.validators = this.validators || [];
        if (this.validator)
            this.validators.push(this.validator);
    }
});
/**
 * @description Textfield class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.textfield} An UIMaster.ui.textfield element.
 * @class UIMaster UI Textfield.
 * @extends UIMaster.ui.field
 * @constructor
 */
UIMaster.ui.textfield = UIMaster.extend(UIMaster.ui.field, /** @lends UIMaster.ui.textfield */{
    /**
     * @description The field is required or not. If required, this field's value should be false.
     * @type Boolean
     * @default true - This field is not required.
     */
    allowBlank: true,
    /**
     * @description Text message for the mandatory validation.
     * @type String
     */
    allowBlankText: '',
    /**
     * @description Regular expression pattern for the field.
     * @type RegExp
     * @default null - No pattern for this field.
     */
    regex: null,
    /**
     * @description Text message for the pattern validation.
     * @type String
     */
    regexText: '',
    /**
     * @description Minimum length required for this field.
     * @type Number
     * @default 0 - No entry is allowed.
     */
    minLength: 0,
    /**
     * @description Maximum length allowed for this field.
     * @type Number
     * @default -1 - No limitation.
     */
    maxLength: -1,
    /**
     * @description Text message for the minimum length validation.
     * @type String
     */
    lengthText: '',
    //private
    needErrMsg : true,
    /**
     * @description Set the textfield's value.
     * @param {String} v Value to set.
     */
    setValue: function(v){
        this.value = v;
        this.notifyChange(this);
    },
    /**
     * @description Get the field's value.
     * @returns {String} Field's value.
     */
    getValue: function(){
        return this.value;
    },
    checkMaxLength: function(e){
        if((this.maxLength>0)&&(this.value.length>=this.maxLength)){
            var keyCode = window.event ? e.keyCode : e.which;
            return ((keyCode == 8)||((keyCode >= 35) && (keyCode <= 40))||(keyCode == 46))
        }
        return true;
    },
    checkMaxAfterInput:function(e){
        if(this.maxLength>0 && this.value.length>this.maxLength){
            this.value=this.value.substring(0,this.maxLength);
        }
    },
    /**
     * @description Validate the field.
     * @returns {Array} An error message array if the validation fails.
     */
    validate: function(init){
        var result = [];
        if (this.flag != null) {
            if (!this.oldInvalidF)
                clearConstraint(this.name);
            if (!this.allowBlank && $.trim(this.value).length < 1) {    //not-blank is the first level check
                result.push(this.allowBlankText);
            } else if (containXSS(this.value) ){    //escape possible executable illegal character
                result.push('Illegal character.');
            } else {    //do the business logic validation
                if ((this.minLength>0)&&(this.value.length<this.minLength))
                    result.push(this.lengthText);
                if ((this.maxLength>0)&&(this.value.length>this.maxLength))
                    result.push(this.lengthText);
                if (this.regex && !this.regex.test(this.ui.value))
                    result.push(this.regexText);
                if (this.validators.length != 0)
                    for (var i = 0; i < this.validators.length; i++)
                        this.validateCustConstraint(this.validators[i]) || result.push(this.validators[i].msg || '');
            }
            if (result.length > 0){
                if (this.needErrMsg === true)
                    constraint(this.name, result.join(" * "));
                setConstraintStyle(this);
            }
        }
        return result.length > 0 ? result : null;
    },
    /**
     * @description Parse the text to a currency number.
     * @see UIMaster.parseCurrency
     * @param {String} message Error message if the validator fails.
     */
    parseCurrency: function() {
        return UIMaster.parseCurrency(g(this,"locale"),g(this,"currencyformat"),this.value);
    },
    notifyChange: function(e){
        var obj = UIMaster.getObject(e);
        this.needErrMsg = true;
        if (obj._v == obj.value) return;
        if (!(obj.validate ? obj.validate() : false)){
            UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"value",_value:obj.value,_framePrefix:UIMaster.getFramePrefix(obj)});
            obj._v = obj.value;
        }
    },
    init: function(){
        UIMaster.ui.textfield.superclass.init.call(this);
        this.removeListener('blur', this.validateEvent);
        this._v = this.value;
        this.regex && (this.regex = new RegExp(this.regex));
        if (this.ui && !this.defaultBackgroundImage)
            UIMaster.browser.msie?this.defaultBackgroundImage = this.currentStyle.backgroundImage:this.defaultBackgroundImage = document.defaultView.getComputedStyle(this, null).getPropertyValue("background-image");
        var f = this.onblur;
        this.onblur = null;
        $(this).bind('blur', this.notifyChange).bind('keyup', function(e) {
            this.needErrMsg = false;
            //key should not be tab || return || shift || ctrl || alt ||  Caps lock
            if ((e.which==9) || (e.which==13) || (e.which==16) || (e.which==17) || (e.which==18) || (e.which==20))
                return;
            else
                this.validateEvent(e);
        });
        f && $(this).bind('blur', f);

        var v0=g(this, "validationFlag");
        var v1=eval(g(this,"allowBlank"));
        var v2=g(this,"allowBlankText");
        var v3=g(this,"regex");
        var v4=g(this,"regexText");
        var v5=g(this,"minLength");
        var v6=g(this,"lengthText");
        var v7=g(this,"maxLength");

        if(v0)this.flag=null;
        this.allowBlank= v1 == null ? this.allowBlank : v1;
        if(!this.allowBlank && (this.value == ''))
            setConstraintStyle(this);
        if(v2)this.allowBlankText=v2;
        !this.allowBlank && this.allowBlankText === '' && (this.allowBlankText = UIMaster.i18nmsg(C+"||ALLOW_BLANK"));
        if(v3)this.regex=new RegExp(v3);
        if(v4)this.regexText=v4;
        this.regex && this.regexText === '' && (this.regexText = UIMaster.i18nmsg(C+"||REGULAR_EXPRESSION"));
        if(v5)this.minLength=Number(v5);
        if(v6)this.lengthText=v6;
        v5 && this.lengthText === '' && (this.lengthText = UIMaster.i18nmsg(C+"||MINIMUM_LENGTH"));
        $(this).bind('keydown',this.checkMaxLength);
        $(this).bind('keyup',this.checkMaxAfterInput)
        if(v7)this.maxLength=Number(v7);
    }
});
/**
 * @description Textarea class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.textarea} An UIMaster.ui.textarea element.
 * @class UIMaster UI Textarea.
 * @extends UIMaster.ui.textfield
 * @constructor
 */
UIMaster.ui.textarea = UIMaster.extend(UIMaster.ui.textfield, /** @lends UIMaster.ui.textarea */{
});
/**
 * @description Calender fiels class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.passwordfield} An UIMaster.ui.passwordfield element.
 * @class UIMaster UI Passwordfield.
 * @extends UIMaster.ui.textfield
 * @constructor
 */
UIMaster.ui.calendar = UIMaster.extend(UIMaster.ui.textfield, /** @lends UIMaster.ui.calendar */{
	isDataOnly: false,
	isBiggerThan: false,
	isSmallerThan: false,
	editableFlag: true,
	dateConstraint: "",
	format: "",
	dateType: "date",
	init: function(){
		UIMaster.ui.calendar.superclass.init.call(this);
		this.selectDate = this.ui.nextElementSibling;
        if (this.className.toLowerCase().indexOf("readonly")>0) {
        	this.editableFlag = false;
        } else {
        	this.editableFlag = true;
        }
        if (this.editableFlag == false) {
            this.disableValid();
            this.selectDate.style.display = "none";
        }
        if (!this.allowBlank)
        {
            this.allowBlank = false;
            if (this.editableFlag == true)
            {
                setRequiredStyle(this.name);
                if (this.value == '') {
                    setConstraintStyle(this);
                }
            }
        }
        this.allowBlank || (this.allowBlankText = UIMaster_getI18NInfo("Common||ALLOW_BLANK"));
        this.date = this.parseDate(this, this.format, this.dateType);
        this.getDate = function(){
            return this.date;
        };
        this.setDate = function(date){
            var dStr = UIMaster_getFormattedDate(this.format, this.dateType, date, '');
            this.value = dStr;
            this.date = date;
            this.validate();
            if(!this.readOnly && !this.disabled && ($(this).css('display') != 'none'))
            	this.focus();
        };
        this.setEditable = function(f){
        	this.editableFlag = f;
            if (f) {
            	this.enableValid();
            	this.selectDate.style.display = "inline";
            	this.validate();
            } else {
            	this.disableValid();
            	this.selectDate.style.display = "none";
                this.clearInvalid();
            }
        };
        this.setValue = function(v){
            if (typeof v == "string"){
            	this.value = v;
            	this.date = this.parseDate(this, this.format, this.dateType);
                this.notifyChange(this);
            }
            else if (typeof v == "date"){
            	this.setDate(v);
            }
        };
        /* Date Constraint and Datepicker callback function */
        this.isDateValid = function(cdate) {
            var date = new Date(cdate.getTime());
            date.setHours(0,0,0,0);
            var dateConstraint = this.dateConstraint, entry, d = date.getDate(), m = date.getMonth()+1;
            for (var i = 0; i < dateConstraint.length; i++) {
                entry = dateConstraint[i];
                if (entry[0] == "R") {
                    var dateFormat3 = /(\d{4})\/(\d{1,2})\/(\d{1,2})/,  result = null,
                    leftRange = entry[1] == "null" || (result = entry[1].match(dateFormat3)) != null && date >= new Date(parseInt(result[1],10), parseInt(result[2],10)-1, parseInt(result[3],10)),
                    rightRange = entry[2] == "null" || (result = entry[2].match(dateFormat3)) != null && date <= new Date(parseInt(result[1],10), parseInt(result[2],10)-1, parseInt(result[3],10));
                    if (leftRange && rightRange) {
                        return false;
                    }
                } else if (entry[0] == "W") {
                    if (date.getDay() == parseInt(entry[1],10)) {
                        return false;
                    }
                } else if (entry[0] == "M") {
                    if ((d >= parseInt(entry[1],10)) && (d <= parseInt(entry[2],10))) {
                        return false;
                    }
                } else if (entry[0] == "Y") {
                    var dateFormat2 = /(\d{1,2})\/(\d{1,2})/, result = null, leftRange = false, rightRange = false;
                    if ((result = entry[1].match(dateFormat2)) != null) {
                        var lm = parseInt(result[1],10), ld = parseInt(result[2],10);
                        leftRange = m > lm || (m == lm && d >= ld);
                    }
                    if ((result = entry[2].match(dateFormat2)) != null) {
                        var rm = parseInt(result[1],10), rd = parseInt(result[2],10);
                        rightRange = m < rm || (m == rm && d <= rd);
                    }
                    if (leftRange && rightRange) {
                        return false;
                    }
                }
            }
            return true;
        };
        this.beforeShowDay = function(date) {
            // if a parent InputDateRange contains this InputDate, delegate call to parent
            if ((this.parentEntity == undefined) || (this.parentEntity.beforeShowDay == undefined)) {
                return [this.isDateValid(date), ''];
            } else {
                return [this.isDateValid(date) && this.parentEntity.beforeShowDay(this, date), ''];
            }
        };
	},
	parseDate: function(src, tformat, datetype){
	    if (src.value.length){
	       // var d = UIMaster_getFormattedDate(tformat, datetype, null, src.value);
	        return isNaN(parseInt(src.value))? null : new Date(parseInt(src.value));
	    } else
	        return null;
	},
	open: function() {
	    if (this.editableFlag) {
	    	LANG == "en" || UIMaster.require("/js/controls/i18n/ui.datepicker-" + LANG + ".js", true);
	        this.showCalendar(this, this.format, this.dateType);
	    }
	},
	showCalendar: function(src, tformat, datetype) {
	    var btn = $(src), date = src.getDate(), todayD = new Date(CURTIME), beforeShowDayFunc = src.beforeShowDay, node = src, dateObj;
	    todayD = new Date(CURTIME+todayD.getTimezoneOffset()*60000+TZOFFSET);
	    $.datepicker.regional[LANG];
	    dateObj = date ? date : todayD;
	    btn.datepicker({
	    	defaultDate: dateObj,
	        dateFormat: "yy-mm-dd",
	        duration: "fast",
	        today:todayD,
	        changeMonth: true,
	        changeYear: true,
	        constrainInput:false,
	        yearRange: '-80:+20'
	    });
	    if (jQuery.datepicker._datepickerShowing && jQuery.datepicker._lastInput == src) 
	       return;
	    jQuery.datepicker._showDatepicker(src);
	},
	notifyChange: function(e){
        var obj = UIMaster.getObject(e);
        this.needErrMsg = true;
        if (obj._v == obj.value) return;
        if (!(obj.validate ? obj.validate() : false)){
            UIMaster.ui.sync.set({_uiid:this.name,_valueName:"value",_value:obj.value,_framePrefix:UIMaster.getFramePrefix(obj)});
            obj._v = obj.value;
        }
    },
	validator: {
		msg: "Calender value format is wrong.",
		func: function() {
			var big = this.isBiggerThan, small = this.isSmallerThan;
			var date = this.parseDate(this, this.format, this.dateType), cDate = new Date(CURTIME);
			//adjust the value according TimeZoneOffset
			cDate = new Date(CURTIME+cDate.getTimezoneOffset()*60000+TZOFFSET);
			if (this.dateType == "date") {
			    cDate.setHours(0,0,0,0);
			}
			if (this.value.length) {
			    if (big && small && (!date || date.getTime() != cDate.getTime()))
			        return UIMaster_getI18NInfo("Common||INPUT_DATE_CONSTRAINT_TODAY");
			    else if (date <= cDate && big)
			        return UIMaster_getI18NInfo("Common||INPUT_DATE_CONSTRAINT_GREATER");
			    else if (date >= cDate && small)
			        return UIMaster_getI18NInfo("Common||INPUT_DATE_CONSTRAINT_SMALLER");
	
			        if ((this.lastInputDateValue == undefined) || (this.lastInputDateValue != this.value)) {
			        this.lastInputDateValue = this.value;
			        //first time or the input value has changed
			        if (!this.allowBlank || !(/^\s*$/.test(this.value))) {
			            //the input is not a legal date
			            if ((date == null) || (this.isDateValid(date) == false)) {
			                this.date = undefined;
			                this.lastInputDateValid = false;
			                return UIMaster_getI18NInfo("Common||INPUT_DATE_CONSTRAINT_INVALID");
			            }
			            if (date != null) {
			                var cYear = cDate.getFullYear(), dYear = date.getFullYear();
			                //the input is legal but the year range exceed (-80,+20) range
			                if ( ( (dYear>=cYear) && (dYear-cYear<=20)) || ( (dYear<cYear) && (dYear-cYear>-80) ) ) {
			                    this.date = date;
			                    this.lastInputDateValid = true;
			                } else {
			                    this.date = undefined;
			                    this.lastInputDateValid = false;
			                    return UIMaster_getI18NInfo("Common||INPUT_DATE_CONSTRAINT_INVALID");
			                }
			            }
			        }
			    } else {
			        // in case of value not changed
			        if (this.lastInputDateValid == false) {
			            return UIMaster_getI18NInfo("Common||INPUT_DATE_CONSTRAINT_INVALID");
			        }
			    }
			}
			else
			{
				this.date = undefined;
			}
			return true;
		}
	}
});
/**
 * @description Password fiels class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.passwordfield} An UIMaster.ui.passwordfield element.
 * @class UIMaster UI Passwordfield.
 * @extends UIMaster.ui.textfield
 * @constructor
 */
UIMaster.ui.passwordfield = UIMaster.extend(UIMaster.ui.textfield);
//SingleChoice
/**
 * @description Checkbox class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.checkbox} An UIMaster.ui.checkbox element.
 * @class UIMaster UI Checkbox.
 * @extends UIMaster.ui.field
 * @constructor
 */
UIMaster.ui.checkbox = UIMaster.extend(UIMaster.ui.field, {
    mustCheck: false,
    mustCheckText: '',
    /**
     * @description Set the checkbox's value.
     * @param {Boolean} c Value to set.
     */
    setValue: function(c){
        this.checked = c;
        this.notifyChange(this);
    },
    /**
     * @description Get the checkbox's value.
     * @returns {Boolean} Checkbox's value.
     */
    getValue: function(){
        return this.checked ? this.value : "";
    },
    validate: function(init){
        var result = [];
        if (this.flag != null) {
            if (!this.oldInvalidF)
                clearConstraint(this.name);
            if (this.mustCheck && this.checked != this.mustCheck)
                result.push(this.mustCheckText);
            if (this.validators.length != 0)
                for (var i = 0; i < this.validators.length; i++)
                    this.validateCustConstraint(this.validators[i]) || result.push(this.validators[i].msg || '');
            if (result.length > 0)
                constraint(this.name, result.join(" * "));
        }
        return result.length > 0 ? result : null;
    },
    notifyChange: function(e){
        var obj = UIMaster.getObject(e);
        if(!!obj._v == !!obj.checked)return;
        if (!(obj.validate ? obj.validate() : false)){
            UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"selected",_value:obj.checked,_framePrefix:UIMaster.getFramePrefix(obj)});
            obj._v = obj.checked;
        }
    },
    init: function(){
        UIMaster.ui.checkbox.superclass.init.call(this);
        this._v = this.checked;
        var f = this.onclick;
        this.onclick = null;
        $(this).bind('click',this.notifyChange);
        f && $(this).bind('click',f);

        var v1=eval(g(this,"mustCheck"));
        var v2=g(this,"mustCheckText");
        this.mustCheck=v1!=null?v1:this.mustCheck;
        if(v2)this.mustCheckText=v2;
    }
});
/**
 * @description Radiobutton class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.radiobutton} An UIMaster.ui.radiobutton element.
 * @class UIMaster UI Radiobutton.
 * @extends UIMaster.ui.checkbox
 * @constructor
 */
UIMaster.ui.radiobutton = UIMaster.extend(UIMaster.ui.checkbox,{
    notifyChange: function(e){
        var obj = UIMaster.getObject(e);
        if(obj._v == obj.checked)return;
        var uiid = obj.inList?obj.name+"["+obj.value+"]":obj.name;
        if (!(obj.validate ? obj.validate() : false)){
            UIMaster.ui.sync.set({_uiid:uiid,_valueName:"selected",_value:obj.checked,_framePrefix:UIMaster.getFramePrefix(obj)});
            obj._v = obj.checked;
        }
    }
});
//MultiChoice
/**
 * @description Checkboxgroup class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.checkboxgroup} An UIMaster.ui.checkboxgroup element.
 * @class UIMaster UI Checkboxgroup.
 * @extends UIMaster.ui.field
 * @constructor
 */
UIMaster.ui.checkboxgroup = UIMaster.extend(UIMaster.ui.field, /** @lends UIMaster.ui.checkboxgroup */{
    mustCheck: null,
    mustCheckText: '',
    /**
     * @description Enable/disable the checkboxgroup.
     * @param {Boolean} v Value to set.
     */
    setEnabled: function(v){
        if (this.length)
            for (var i = 0; i < this.length; i++)
                v ? this[i].disabled = false : this[i].disabled = true;
    },
    /**
     * @description Set the checkboxgroup's value.
     * @param {Array} c Values to set.
     */
    setValue: function(c){
        var arr = c || [];
        if (this.length){
            for (var i = 0; i < arr.length; i++) {
                for (var j = 0; j < this.length; j++)
                    if (this[j].value == arr[i]) {
                        this[j].checked = true;
                        break;
                    }
                    else
                        this[j].checked = false;
            }
        }
        else
            this.checked = c;
        this.notifyChange(this.nodeType?this:this[0]);
    },
    /**
     * @description Get the checkboxgroup's value.
     * @returns {Array} Checkboxgroup's value.
     */
    getValue: function(){
        var arr = [];
        if (this.length)
            for (var i = 0; i < this.length; i++)
                if (this[i].checked)
                    arr.push(this[i].value);
        else
            if(this.checked)
                arr.push(this.value);
        return arr;
    },
    validateEvent: function(evnt){
        var obj = UIMaster.getObject(evnt);
        return eval(D+ obj.name).validate();
    },
    notifyChange: function(e){
        var obj = eval(D + UIMaster.getObject(e).name), v = obj.getValue().join(",")
        if(obj._v == v)return;
        if (!(obj.validate ? obj.validate() : false)){
            UIMaster.ui.sync.set({_uiid:UIMaster.getObject(e).name,_valueName:"values",_value:obj.getValue().join(","),_framePrefix:UIMaster.getFramePrefix(UIMaster.getObject(e))});
            obj._v = v;
        }
    },
    validate: function(init){
        var result = [];
        if (this.length && this.flag != null) {
            if (!this.oldInvalidF)
                clearConstraint(this.nodeType ? this.name : this[0].name);
            if (this.mustCheck)
                if (!UIMaster.arrayContain(this.mustCheck, this.getValue()))
                    result.push(this.mustCheckText);
            if (this.validators.length != 0)
                for (var i = 0; i < this.validators.length; i++)
                    this.validateCustConstraint(this.validators[i]) || result.push(this.validators[i].msg || '');
            if (result.length > 0)
                constraint(this.nodeType ? this.name : this[0].name, result.join(" * "));
        }
        return result.length > 0 ? result : null;
    },
    sync:function(){
        this.notifyChange(this.nodeType?this:this[0]);
    },
    getDefaultValue: function(){
        var arr=[];
        if(this.length)
            for(var i=0;i<this.length;i++)
                if(this[i].defaultSelected)
                    arr.push(this[i].value);
        else
            if(this.defaultSelected)
                arr.push(this.value);
        return arr;
    },
    setLabelText: function(text){
        var n = this.nodeType?this:this[0];
        n.previousSibling ? n.previousSibling.childNodes[0].nodeValue = text : $(n).before($('<label></label>').attr({'id':n.name+'_widgetLabel','for':n.name}).addClass("uimaster_widgetLabel").css('display','block').text(text));
    },
    removeIndicator: function(){
        $('#' + (this.nodeType ? this.name : this[0].name) + 'div').remove();
    },
    addListener: function(E, fn){
        if (this.length)
            for (var i = 0; i < this.length; i++)
                $(this[i]).bind(E,fn);
        else
            $(this).bind(E,fn)
    },
    removeListener: function(E, fn, u){
        if (this.length)
            for (var i = 0; i < this.length; i++)
                $(this[i]).unbind(E,fn);
        else
            $(this).unbind(E,fn);
    },
    initV:function(){
        this._v = this.getDefaultValue().join(",");
    },
    init: function(){
        this.parentDiv = this.nodeType?this.parentNode.parentNode:this[0].parentNode.parentNode;
        this.initV();
        this.validators = [];
        if (this.validator)
            this.validators.push(this.validator);
        if (this.length)
            for (var i = 0; i < this.length; i++) {
                this[i].parentEntity = this.parentEntity;
                this[i].arrayIndex = i;
                this[i].setVisible = UIMaster.ui.checkboxgroup.superclass.setVisible;
            }
        this.addListener('click', this.notifyChange, false);
        this.addListener('blur', this.validateEvent, false);
        if (this.nodeType){
            var f = this.onclick;
            this.onclick = null;
            f && $(this).bind('click',f);
        }else{
            for(var i=0;i<this.length;i++){
                var f = this[i].onclick;
                this[i].onclick = null;
                f && $(this[i]).bind('click',f);
            }
        }

        //var v1=eval(g(this,"mustCheck"));
        //var v2=g(this,"mustCheckText");
        //if(v1)this.mustCheck=(v1=="true");
        //if(v2)this.mustCheckText=v2;
    }
});
/**
 * @description Radiobuttongroup class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.radiobuttongroup} An UIMaster.ui.radiobuttongroup element.
 * @class UIMaster UI Radiobuttongroup.
 * @extends UIMaster.ui.checkboxgroup
 * @constructor
 */
UIMaster.ui.radiobuttongroup = UIMaster.extend(UIMaster.ui.checkboxgroup, /** @lends UIMaster.ui.radiobuttongroup*/{
    /**
     * @description Set the radiobuttongroup's value.
     * @param {String} c Value to set.
     */
    setValue: function(c){
        if (this.length)
            for (var i = 0; i < this.length; i++)
                this[i].checked = (this[i].value == c ? true : false);
        else
            this.checked = (this.value == c ? true : false);
        this.notifyChange(this.nodeType?this:this[0]);
    },
    /**
     * @description Get the radiobuttongroup's value.
     * @returns {String} Radiobuttongroup's value.
     */
    getValue: function(){
        if (this.length)
            for (var i = 0; i < this.length; i++)
                if (this[i].checked)
                    return arguments.callee.caller == this.validate ? [this[i].value] : this[i].value;
    },
    getDefaultValue: function(){
        if (this.length)
            for (var i = 0; i < this.length; i++)
                if (!!this[i].defaultChecked)
                    return this[i].value;
    },
    notifyChange: function(e){
        var rBtn = UIMaster.getObject(e), obj = eval(D + rBtn.name);
        if(e == undefined || obj._v == obj.getValue())return;
        var uiid = obj.inList?rBtn.name.lastIndexOf(']')!=rBtn.name.length-1 ? rBtn.value == "on"?rBtn.name:rBtn.name+"["+rBtn.value+"]" : rBtn.name:rBtn.name;
        if (!(obj.validate ? obj.validate() : false)){
            UIMaster.ui.sync.set({_uiid:uiid,_valueName:"value",_value:obj.getValue(),_framePrefix:UIMaster.getFramePrefix(rBtn)});
            obj._v = obj.getValue();
        }
    },
    initV:function(){
        this._v = this.getDefaultValue();
    }
});
//SelectComponent
/**
 * @description Combobox class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.combobox} An UIMaster.ui.combobox element.
 * @class UIMaster UI Combobox.
 * @extends UIMaster.ui.field
 * @constructor
 */
UIMaster.ui.combobox = UIMaster.extend(UIMaster.ui.field, /** @lends UIMaster.ui.combobox*/{
    /**
     * @description The field is required or not. If required, this field's value should be false.
     * @type Boolean
     * @default true - This field is not required.
     */
    allowBlank: true,
    /**
     * @description Text message for the mandatory validation.
     * @type String
     */
    allowBlankText:'',
    selectValue: null,
    selectValueText: '',
    /**
     * @description Set the combobox's value.
     * @param {String} v Value to set.
     */
    setValue: function(v,b){
        this.value=v;
		if(b) {
		   for(var i=0;i<this.options.length;i++)
            if(this.options[i].value==v) {
                this.options[i].selected=true;
				break;
			}
		}
        this.notifyChange(this);
    },
    /**
     * @description Get the field's value.
     * @returns {String} Field's value.
     */
    getValue: function(){
        return arguments.callee.caller == this.validate ? [this.value] : this.value;
    },
    getDefaultValue: function(){
        for(var i=0;i<this.options.length;i++)
            if(this.options[i].defaultSelected)
                return this.options[i].value;
    },
	addAttr: function(a){
	    if (a.item) {
		   $(this).append($("<option></option>").attr("value",a.value).text(a.name));
		} else {
		   this.setValue(a.value, true);
		}
	},
	removeAttr: function(v) {
		this.clearOptions();
	},
	addOption: function(item){
	   $(this).append($("<option></option>").attr("value",item.value).text(item.name));
	},
	clearOptions: function(){
	   $(this).children().remove();
	},
    /**
     * @description Validate the field.
     * @returns {Array} An error message array if the validation fails.
     */
    validate: function(init){
        var result = [];
        if (this.options && this.flag != null) {
            if (!this.oldInvalidF)
                clearConstraint(this.name);
            if (!this.allowBlank && (this.value == "" || this.value == "_NOT_SPECIFIED"))
                result.push(this.allowBlankText);
            if (this.selectValue) {
                var select = this.getValue();
                if (!UIMaster.arrayContain(this.selectValue, select))
                    result.push(this.selectValueText);
            }
            if (this.validators.length != 0)
                for (var i = 0; i < this.validators.length; i++)
                    this.validateCustConstraint(this.validators[i]) || result.push(this.validators[i].msg || '');
            if (result.length > 0)
                constraint(this.name, result.join(" * "));
        }
        this.style.backgroundColor=result.length>0?CONSTRAINT_BACKGROUNDCOLOR:"#FFFFFF";
        return result.length > 0 ? result : null;
    },
    notifyChange: function(e,t){
        var obj = UIMaster.getObject(e);
        if (!(obj.validate ? obj.validate() : false)||t) {
            UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"value",_value:obj.getValue(),_framePrefix:UIMaster.getFramePrefix(obj)});
        }
    },
    init: function(){
        UIMaster.ui.combobox.superclass.init.call(this);
        var f = this.onchange;
        this.onchange = null;
        $(this).bind('change', this.notifyChange);
        f && $(this).bind('change', f);

        var v1=eval(g(this,"allowBlank"));
        var v2=g(this,"allowBlankText");
        //var v3=g(this,"selectValue");
        //var v4=g(this,"selectValueText");
        this.allowBlank= v1 == null ? this.allowBlank : v1;
        if(v2)this.allowBlankText=v2;
        !this.allowBlank && this.allowBlankText === '' && (this.allowBlankText = UIMaster.i18nmsg(C+"||ALLOW_BLANK"));
        //if(v3)this.selectValue=v3;
        //if(v4)this.selectValueText=v4;

        //begin comboBox tool tip
        $('option', $(this)).each(function(i){
            $(this).attr('title', $(this).text());
        });
        //end comboBox tool tip
    }
});
/**
 * @description List class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.list} An UIMaster.ui.list element.
 * @class UIMaster UI List.
 * @extends UIMaster.ui.combobox
 * @constructor
 */
UIMaster.ui.list = UIMaster.extend(UIMaster.ui.combobox, {
    /**
     * @description Set the list's value.
     * @param {Array} v Values to set.
     */
	syncedValue:null,
    setValue: function(arr){
        for (var i = 0; i < arr.length; i++) {
            for (var j = 0; j < this.options.length; j++)
                if (this.options[j].value == arr[i]) {
                    this.options[j].selected = true;
                    break;
                }
        }
        this.notifyChange(this);
    },
    /**
     * @description Get the list's value.
     * @returns {Array} List's value.
     */
    getValue: function(){
        var arr = [];
        for (var i=0; i < this.options.length; i++)
            if (this.options[i].selected)
                arr.push(this.options[i].value);
        return arr;
    },
    getDefaultValue: function(){
        var arr=[];
        for(var i=0;i<this.options.length;i++)
            if(this.options[i].defaultSelected)
                arr.push(this.options[i].value);
        return arr;
    },
    notifyChange: function(e,t){
        var obj = UIMaster.getObject(e);
        if (!(obj.validate ? obj.validate() : false)||t) {
		    if (this.syncedValue != null) {
			   var i = -1, l = UIMaster.syncList.length;
			   for (var j=0;j<l;j++) {
			     if (UIMaster.syncList[j] == this.syncedValue) {
				    i = j; break;
				 }
			   }
			   if (i > -1) {
			      UIMaster.syncList.splice(i,1);
			   }
			}
		    this.syncedValue = {_uiid:UIMaster.getUIID(obj),_valueName:"values",_value:obj.getValue().join(";"),_framePrefix:UIMaster.getFramePrefix(obj)};    
            UIMaster.ui.sync.set(this.syncedValue);
	    }
    }
});
//ContainerComponent
/**
 * @description Container class. This is the panel container.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.container} An UIMaster.ui.container element.
 * @class UIMaster UI Container.
 * @extends UIMaster.ui.field
 * @constructor
 */
UIMaster.ui.container = UIMaster.extend(UIMaster.ui.field, /** @lends UIMaster.ui.container */{
    /**
     * @description Items in this container.
     * @type Array
     */
    items: null,
    validationList: null,
    flag: true,
    /**
     * @description Set the container's visibility.
     * @param {Boolean} v Visibility to set.
     */
    setVisible: function(v){
        for (var i = 0; i < this.items.length; i++)
            this.items[i].setVisible && this.items[i].setVisible(v);
    },
    /**
     * @description Enable the container.
     */
    enable: function(){
        for (var i = 0; i < this.items.length; i++)
            this.items[i].setEnabled && this.items[i].setEnabled(true);
    },
    /**
     * @description Disable the container.
     */
    disable: function(){
        for (var i = 0; i < this.items.length; i++)
            this.items[i].setEnabled && this.items[i].setEnabled(false);
    },
    /**
     * @description Disable the container's validation.
     */
    disableValid: function(){
        for (var i = 0; i < this.validationList.length; i++)
            this.validationList[i].disableValid && this.validationList[i].disableValid();
    },
    /**
     * @description Enable the container's validation.
     */
    enableValid: function(){
        for (var i = 0; i < this.validationList.length; i++)
            this.validationList[i].enableValid && this.validationList[i].enableValid();
    },
    /**
     * @description Validate the container.
     * @returns {Array} An error message array if the validation fails.
     */
    validate: function(init){
        var result = [];
        if (this.flag != null)
            // clear panel err message only in page-level's validation. this ensure the clearErrMsg execute only one time
            if (this.parentEntity.initPageJs) UIMaster.clearErrMsg();
            for (var i = 0; i < this.validationList.length; i++) {
                var res=null;
                if (init == undefined)
                    res = this.validationList[i].validate();
                // add star to field that must not blank
                if (this.validationList[i].allowBlank==false)
                    setRequiredStyle(this.validationList[i].name);
                if (res != null)
                    for (var obj in this.parentEntity)
                        if (this.parentEntity[obj] == this.validationList[i]) {
                            result.push([obj,res]);
                            // when validation find errs, show them in panel
                            this.validationList[i].parentDiv && UIMaster.appendPanelErr(this.validationList[i].parentDiv.id, res);
                            break;
                        }
            }
        return result.length > 0 ? result : null;
    },
    /**
     * @description Synchronize the values in the container.
     */
    sync:function(){
        for (var i = 0; i < this.items.length; i++)
            this.items[i].sync && (this.items[i].ui || this.items[i].Form || this.items[i] instanceof Array)&& this.items[i].sync();
    },
    addComponentValidate: function(v){
        if (v instanceof Array)
            for (var i = 0; i < v.length; i++)
                if (v[i].component instanceof Array)
                    for (var j = 0; j < v[i].component.length; j++)
                        v[i].component[j].validators.push(v[i]);
                else
                    this.parentEntity.validators.push(v[i]);
    },
    init: function(){
        UIMaster.ui.container.superclass.init.call(this);
        this.addComponentValidate(this.validator);
        if (!this.validationList) {
            this.validationList = [];
            for (var i = 0; i < this.items.length; i++)
                if (this.items[i] && this.items[i].validate)
                    this.validationList.push(this.items[i]);
        }
        this.removeListener('blur', this.validateEvent, false);
        this.validate(this.flag);
        this.parentEntity.initialized = true;
    }
});
/**
 * @description Panel class. This is the abstract object of UIEntity or UIPage.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.panel} An UIMaster.ui.panel element.
 * @class UIMaster UI Panel.
 * @constructor
 */
UIMaster.ui.panel = function(conf){
    function parseInitPageJs(){
        if (this.initPageJs) {
            UIMaster.ui.mask.open();
            var oIPJS = this.initPageJs, comment, nodes=this.sync?$(this.Form):$(this);
            this.initPageJs = function(){
                if (!jQuery.isReady) jQuery.ready();
                postInit();
                //comment && UIMaster.cmdHandler(comment);
                oIPJS.call(this);
                focusFirstTextField();
            }
            UIMaster.ui.mask.close();
            if ($(this.parentDiv).is("form")) {
            	$(this.parentDiv).css("display","block");
            } else {
            	var p = $(this.parentDiv); while(!(p=p.parent()).is("form")){};
            	p.css("display","block");
            }
        }
    }
    if (conf.items && conf.items.length > 0) {
        UIMaster.apply(this, {
            /**
             * @description Container object.
             * @see UIMaster.ui.container
             */
            Form: new UIMaster.ui.container(conf),
            /**
             * @see UIMaster.ui.container.disableValid
             */
            disableValid:function(){
                this.Form.disableValid();
            },
            /**
             * @see UIMaster.ui.container.enableValid
             */
            enableValid:function(){
                this.Form.enableValid();
            },
            /**
             * @see UIMaster.ui.container.disable
             */
            disable:function(){
                this.Form.disable();
            },
            /**
             * @see UIMaster.ui.container.enable
             */
            enable:function(){
                this.Form.enable();
            },
            /**
             * @see UIMaster.ui.container.sync
             */
            sync:function(){
                this.Form.sync();
            },
            /**
             * @description Validate the panel.
             * @returns {Array} An error message array if the validation fails.
             */
            validate: function(){
                var r = this.Form.validate();
                r = r || [];
                var a = [];
                clearConstraint(this.Form.id);
                for (var i=0;i<this.validators.length;i++)
                    if(!this.validators[i].func.call(this))
                        a.push(this.validators[i].msg);
                if(a.length){
                    constraint(this.Form.id, a.join(" * "));
                    if (!this.name.nodeType)
                        r.push([this.name,a.join(" * ")]);
                    else
                        r.push(["panel",a.join(" * ")]);
                }
                return r.length>0?r:null;
            },
            validators: [],
            addComponent: function(c,v,n){
                n && (this[n] = c);
                c.parentEntity = this;
                !c.initialized && c.init();
                this.Form.items.push(c);
                v && c.validate && this.Form.validationList.push(c);
                if (c.allowBlank == false)
                    setRequiredStyle(c.name);
                if (c.subComponents)
                {
                    for (var i = 0; i < c.subComponents.length; i++)
                    {
                        this.addComponent(eval(D+c.subComponents[i]),true);
                    }
                }
            },
            removeComponent: function(c){
            	if (c == undefined)
            		return;
                if (c.subComponents != undefined)
                    for (var i = 0; i < c.subComponents.length; i++)
                        this.removeComponent(eval(D+c.subComponents[i]));
                for(var i in this)
                    if(this[i] == c){
                        delete this[i];
                        for(var k=0;k<this.Form.validationList.length;k++)
                            if(this.Form.validationList[k]==c){
                                this.Form.validationList.splice(k,1);
                                break;
                            }
                        for(var k=0;k<this.Form.items.length;k++)
                            if(this.Form.items[k]==c){
                                this.Form.items.splice(k,1);
                                break;
                            }
                        break;
                    }
            },
            releaseFormObject: function() {
            	var formName = this.Form.id.substring(0,this.Form.id.lastIndexOf('.'));
            	for (i in elementList) {
            		if (i.substr(0,formName.length) == formName) {
            			delete elementList[i];
            		}
            	}
            	//var k=this.Form.items.length;
            	//while(k-->0) {
                    //var item = this.Form.items.splice(k,1)[0];
            	//}
            	this.Form.items = null;
            	delete this.Form;
            },
            /**
             * @see UIMaster.ui.setLabelText
             */
            setLabelText:function(txt){
                this.Form.setLabelText(txt);
            },
            init: function(){
                var ui = this.Form.ui;
                if (ui && !this.name) {
                    var name = (typeof ui.name == "string") ? ui.name : ui.id;
                    if (name) {
                        var n = name.split('.');
                        n.pop();
                        this.name = n.join('.') + '.';
                    }
                    else
                        this.name = "";
                }
                var items = this.Form.items;
                for (var i = 0; i < items.length; i++)
                    if (items[i]) {
                        items[i].parentEntity = this;
                        if (!items[i].initialized)
                            items[i].init && (items[i].ui || items[i].Form || items[i] instanceof Array) && items[i].init();
                    }
                this.Form.parentEntity = this;
                this.parentDiv = this.Form.parentNode;
                if("TitlePanel" == this.Form.uiskin)
                {
                    this.parentDiv=this.parentDiv.parentNode.parentNode.parentNode.parentNode;
                }
                else if("FoldingPanel" == this.Form.uiskin)
                {
                    this.parentDiv=this.parentDiv.parentNode.parentNode.parentNode.parentNode.parentNode;
                }
                this.Form.init();
                this.user_constructor && (defaultname && defaultname.Form || defaultname && defaultname[this.Form.id.split('.')[0]] ? this.user_constructor() : UIMaster.initList.push(this));
                parseInitPageJs.apply(this);
            },
            user_constructor: null
        });
    }else{
        UIMaster.apply(this, conf);
        UIMaster.apply(this, {
            validate:function(){},
            init:function(){
                this.parentDiv=this.parentNode;
                this.user_constructor && (defaultname && defaultname.Form || defaultname && defaultname[this.Form.id.split('.')[0]] ? this.user_constructor() : UIMaster.initList.push(this));
                this.initialized=true;
                if("TitlePanUIMaster" == this.uiskin)
                    this.parentDiv=this.parentDiv.parentNode.parentNode.parentNode.parentNode;
                else if("FoldingPanel" == this.uiskin)
                    this.parentDiv=this.parentDiv.parentNode.parentNode.parentNode.parentNode.parentNode;
                parseInitPageJs.apply(this);
            }
        });
        UIMaster.apply(this.ui, this);
        return this.ui;
    }
};

//OtherComponent UI
/**
 * @description UI Button class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.button} An UIMaster.ui.button element.
 * @class UIMaster UI Button.
 * @extends UIMaster.ui
 * @constructor
 */
UIMaster.ui.button = UIMaster.extend(UIMaster.ui,{
    init:function(){
        this.parentDiv = this.parentNode;
    }
});
/**
 * @description UI Hidden class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.hidden} An UIMaster.ui.hidden element.
 * @class UIMaster UI Hidden.
 * @extends UIMaster.ui
 * @constructor
 */
UIMaster.ui.hidden = UIMaster.extend(UIMaster.ui, /** @lends UIMaster.ui.hidden*/{
    sync:function(){
        if(this.value!=this._v)
            this.notifyChange(this);
    },
    notifyChange:function(e){
        var obj = UIMaster.getObject(e);
        UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"value",_value:obj.value,_framePrefix:UIMaster.getFramePrefix(obj)});
        obj._v = obj.value;
    },
    n:function(e){
        if(e.originalEvent.propertyName=="value" || e.originalEvent.attrName=="value")
            this.notifyChange(e.originalEvent);
    },
    /**
     * @description Set the hidden's value.
     * @param {String} v Value to set.
     */
    setValue:function(v){
        this.value = v;
    },
    init:function(){
        $(this).bind('propertychange', this.n).bind('DOMAttrModified',this.n);
        this.parentDiv = this;
        this._v = this.value;
    }
});
/**
 * @description UI Label class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.label} An UIMaster.ui.label element.
 * @class UIMaster UI Label.
 * @extends UIMaster.ui.hidden
 * @constructor
 */
UIMaster.ui.label = UIMaster.extend(UIMaster.ui.hidden, /** @lends UIMaster.ui.label*/{
    /**
     * @description Set the widget's label text.
     * @param {String} text Label text to set.
     */
    setLabelText: function(text){
        var n = this.parentNode.parentNode, k = n.previousSibling ? (n.previousSibling.nodeType == 1?n.previousSibling:n.previousSibling.previousSibling):null;
        k ? k.childNodes[0].nodeValue = text : $(n).before($('<label></label>').attr({'id':this.name+'_widgetLabel','for':this.name}).addClass("uimaster_widgetLabel").css('display','block').text(text));
    },
    /**
     * @description Set the widget's text.
     * @param {String} text Text to set.
     */
    setText: function(text){
        this.previousSibling?this.previousSibling.nodeValue = text:$(this).before(text);
    },
    init:function(){
        UIMaster.ui.label.superclass.init.call(this);
        this.parentDiv = this.parentNode.parentNode.parentNode;
    }
});
/**
 * @description UI Link class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.link} An UIMaster.ui.link element.
 * @class UIMaster UI Link.
 * @extends UIMaster.ui.hidden
 * @constructor
 */
UIMaster.ui.link = UIMaster.extend(UIMaster.ui.hidden,{
	init:function(){
        UIMaster.ui.link.superclass.init.call(this);
        this.parentDiv = this.parentNode;
    }
});
UIMaster.ui.frame = UIMaster.extend(UIMaster.ui);
UIMaster.ui.image = UIMaster.extend(UIMaster.ui, {
	init:function(){
		if (this.tagName.toLowerCase() == "div") {
			$(this).jGallery();
		}
	}
});
UIMaster.ui.file = UIMaster.extend(UIMaster.ui, {
	init:function(){
		var fileUI = this;
		var uploadBtn = this.nextElementSibling;
		var progressbox = this.nextElementSibling.nextElementSibling;
		var messagebox = progressbox.nextElementSibling;
		var c = $(progressbox).children();
		var progressbar = c[0];
		var percent = c[1];

		var options = {
			beforeSend : function() {
				$(progressbox).show();
				// clear everything
				$(progressbar).width('0%');
				$(messagebox).empty();
				$(percent).html("0%").css("background-color","lightblue");
			},
			uploadProgress : function(event, position, total, percentComplete) {
				$(progressbox).width(percentComplete + '%');
				$(percent).html(percentComplete + '%');

				// change message text to red after 50%
				if (percentComplete > 50) {
				$(messagebox).html("<font color='red'>File Upload is in progress</font>");
				}
			},
			success : function() {
				$(uploadBtn).before($(fileUI));
				$(progressbar).width('100%');
				$(percent).html('100%');
			},
			complete : function(response) {
				$(messagebox).html("<font color='blue'>Your file has been uploaded!</font>");
			},
			error : function() {
				$(messagebox).html("<font color='red'> ERROR: unable to upload files</font>");
			}
		};
		$(uploadBtn).click(function() {
			var suffix = $(fileUI).attr("suffix");
			if (suffix == "undefined" || suffix == "") {
				alert("Wrong file widget with the empty suffix!");
				return;
			}
			if (fileUI.value == "") {
				alert("Please choose a file!");
				return;
			}
			var fileName = fileUI.value;
			var ldot = fileName.lastIndexOf(".");
			if (ldot == -1) {
				alert("Please choose a file with this suffix: "+suffix);
				return;
			}
			var type = fileName.substring(ldot + 1).toLowerCase();
			if (suffix.toLowerCase().indexOf(type) == -1) {
				alert("Please choose a file with this suffix: "+suffix);
				return;
			}
			
			var _framePrefix=UIMaster.getFramePrefix(UIMaster.El(fileUI).get(0));
			var form = $('<form action='+WEB_CONTEXTPATH+'/uploadFile?_uiid='+fileUI.name+'&_framePrefix='+_framePrefix+' method=post enctype=multipart/form-data></form>');
			//encodeURI(fileUI.uploadName || fileUI.name)  
			$(form).append($(fileUI));
			$(form).ajaxSubmit(options);
			return;
		});
	}
});
UIMaster.ui.objectlist = function(conf){
	UIMaster.apply(this, conf);
};
UIMaster.ui.objectlist = UIMaster.extend(UIMaster.ui, {
	initialized:false,
	dtable:null,
	isSingleSelection:true,
	isMultipleSelection:false,
	editablecell:false,
	selectedIndex:-1,
	selectNotify:[],
	columnIds:[],
	tbody:null,
	tfoot:null,
	init:function(){
		if (this.initialized)
			return;
		this.initialized = true;
					
		$(this).prev().children().each(function() { //#'+this.id+'ActionBar'
			$(this).buttonset();
			$(this).children().each(function(){
				if ($(this).attr("icon") != undefined && $(this).attr("icon") != null) {
					$(this).button({text:false,icons:{primary:$(this).attr("icon")}});
				}
			});
		});
		var othis = this;
		var table = $(this).dataTable({
			"paging": (this.editable == undefined?true:this.editable),
			"filter": false,
			"recordsFiltered": $(this).attr("recordsFiltered"),
			"recordsTotal": $(this).attr("recordsTotal"),
			"deferLoading": 10,
			"processing": true,
			"serverSide": true,
			"ajax": {
					async: false,
		            url: AJAX_SERVICE_URL+"?r="+Math.random(),
		            type: 'POST',
		            data:{_ajaxUserEvent: "table",
		                _uiid: this.id,
		                _actionName: "pull",
		                _framePrefix: UIMaster.getFramePrefix(UIMaster.El(this.id).get(0)),
		                _actionPage: this.parentEntity.__entityName
		                }
					}
		});//this method will reinit the constructor again. weird!
		this.dtable = table;
		this.dtable.api().settings()[0].oFeatures.bServerSide=true;
		//attach the event after initializing the table.
		$(this).on("order.dt", function () { });
		$(this).on("page.dt", function (event,settings) {});
		$(this).on("length.dt", function (event,settings,integer) {});
		var columnIds = new Array();
		var coli = 0;
		$(elementList[this.id]).find('thead th').each(function(){
			columnIds[coli++] = $(this).attr('id');
		});
		this.columnIds = columnIds;
		var body = $(elementList[this.id]).children('tbody');
		this.tbody = body;
		if (this.isSingleSelection) {
			this.refreshBodyEvents(body, true);
		} else if (this.isMultipleSelection) {
			//TODO:
		}
		if(this.rowEmpty()) {
			this.syncButtonGroup(false);
		}
		var t = this;
		this.tfoot = $(elementList[this.id]).find('tfoot');
		if (this.tfoot.length == 0) {
			return;
		}
		this.tfoot = $(this.tfoot[0]);
		if(typeof(this.tfoot.attr('editablecell'))!="undefined") {
			this.editablecell = true;
			this.syncCellEvent(body);
			return;
		} 
		var filters = this.tfoot.find('th');
		filters.each(function(){
			var c = $(this).children();
			for (var i=0;i<c.length;i++) {
				if(c[i].tagName.toLowerCase() == "input") {
					$(c[i]).keydown(function(e){
						if(e.keyCode == 13) {
							t.sync();
							t.refresh(0);
						}
					});
				} else if(c[i].tagName.toLowerCase() == "select") {
					$(c[i]).change(function(){
						t.sync()
						t.refresh(0);
					});
				} else if(c[i].tagName.toLowerCase() == "img" && c[i].src.indexOf("calendar") != -1) {
					//TODO: support data range
					c[i].calendar = new UIMaster.ui.calendar({ui: $(c[i]).prev()[0]});
					c[i].calendar.init();
					$(c[i]).click(function(){
					    this.calendar.open();
					});
					$(c[i]).prev().change(function(e){
						t.sync()
						t.refresh(0);
					});
				}
			} 
		});
	},
	syncCellEvent:function(body){
		var othis = this;
		$(body).find('td').each(function(){
			$(this).hover(
			function(){othis.showEditorOnCell(othis.getTDIndex(this),this);}, 
			function(){othis.hideEditorOnCell(othis.getTDIndex(this),this);});
		});
	},
	rowEmpty:function(){
		return this.tbody.find("td[class='dataTables_empty']").length == 1;
	},
	getTDIndex:function(td){
		var i=0;
		$(td).parent().children().each(function(){
			if(this == td) {
				return false;
			}
			i++;
		});
		return i;
	},
	showEditorOnCell:function(i, td){
		if (this.rowEmpty()) {return;}
		
		var value = $(td).text();
		this.tempCellValue = value;
		var widget = this.tfoot.find('th');
		var wd = $(widget[i]).children()[0];
		var tagName = wd.tagName.toUpperCase();
		if(tagName == "INPUT") {
			wd.value = value;
		} else if(tagName == "SELECT"){
			$(wd).children().each(function(){
				if($(this).text() == value) {
					$(this).attr("selected",true);
					return false;
				}
			});
			
		}
		$(td).text("");
		$(td).append(wd);
	},
	hideEditorOnCell:function(i, td){
		if (this.rowEmpty()) {return;}
		var wd = $(td).children()[0];
		var tagName = wd.tagName.toUpperCase();
		if(tagName == "INPUT") {
			$(td).text(wd.value);
			if(this.tempCellValue != wd.value) {
				$(td).parent().attr("updated",true);
			}
		} else if(tagName == "SELECT"){
			var v = $(wd).find("option:selected").text();
			$(td).text(v);
			wd.selectedIndex = -1; 
			if(this.tempCellValue != v) {
				$(td).parent().attr("updated",true);
			}
		}
		var widget = this.tfoot.find('th');
		$(widget[i]).append(wd);
	},
	syncButtonGroup:function(isselected){
		var id = this.id.replace(/\./g,"_");
		if (this.rowEmpty()) {
			isselected = false;
		}
		if (isselected){
			$('#'+id+"_newItem").button({disabled:true});
			$('#'+id+"_openItem").button({disabled:false});
			$('#'+id+"_disableItem").button({disabled:false});
			$('#'+id+"_enableItem").button({disabled:false});
			$('#'+id+"_deleteItem").button({disabled:false});
		}else{
			$('#'+id+"_newItem").button({disabled:false});
			$('#'+id+"_openItem").button({disabled:true});
			$('#'+id+"_disableItem").button({disabled:true});
			$('#'+id+"_enableItem").button({disabled:true});
			$('#'+id+"_deleteItem").button({disabled:true});
		}
	},
	sync:function(){
		var obj = UIMaster.getObject(this);
        if (obj._selectedIndex != this.selectedIndex) {
            UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"selectedIndex",_value:this.selectedIndex,_framePrefix:UIMaster.getFramePrefix(obj)});
            obj._selectedIndex = this.selectedIndex;
        }
		if (this.editablecell) {
			return;//the editable cell and the filters are mutual exclusive.
		}
        var filters = $(elementList[this.id]).find('tfoot th');
		var conditions = new Array();
		filters.each(function(){
			var c = $(this).children();
			for (var i=0;i<c.length;i++) {
				if(c[i].tagName.toLowerCase() == "input") {
					conditions.push({name:c[i].name, value:c[i].value});
				} else if(c[i].tagName.toLowerCase() == "select") {
					conditions.push({name:c[i].name, value:c[i].value});
				} 
			}
		});
		UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"conditions",_value:JSON.stringify(conditions),_framePrefix:UIMaster.getFramePrefix(obj)});
	},
	syncBodyDataToServer:function(){
		if (this.editablecell) {
			var o = this;
			var bodydata = new Array();
		    //convert body cells to json.
			this.tbody.find('tr').each(function(){
				var row = new Object();
				var i=0;
				$(this).children().each(function(){
					row[o.columnIds[i++]]=$(this).text();
				});
				if ($(this).attr("updated") == "true") {
					row["updated"] = true;
				}
				bodydata.push(row);
			});
			var obj = UIMaster.getObject(this);
			UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"bodyJson",_value:JSON.stringify(bodydata),_framePrefix:UIMaster.getFramePrefix(obj)});
			return;
		}
	},
	refreshBodyEvents:function(body, selectedByDefault) {
		var othis = this;
		body.children().each(function(){
		 $(this).bind('click', function(){
			var tr = $(this);
			var isselected=false;
			if (tr.hasClass('selected')){
				tr.removeClass('selected');
				othis.selectedIndex = -1;
	        } else {
	        	othis.dtable.$('tr.selected').removeClass('selected');
	            tr.addClass('selected');
	            othis.selectedIndex = tr[0]._DT_RowIndex;
	            isselected=true;
	        }
			othis.syncButtonGroup(isselected);
	    });
		});
		if (othis.editablecell) {
			othis.syncCellEvent(body);
		}
		if (selectedByDefault == undefined || !selectedByDefault) {
			return;
		}
		var c = body.children();
		if (c.length == 1) {
			if (this.rowEmpty()) {
				this.syncButtonGroup(false);
			} else {
				$(c[0]).addClass('selected');
	            this.selectedIndex = 0;
	            othis.syncButtonGroup(true);
			}
		} else if (c.length > 0) {
			if (this.selectedIndex != -1) {
				$(c[this.selectedIndex]).addClass('selected');
			} else {
				$(c[0]).addClass('selected');
				this.selectedIndex = 0;
			}
            othis.syncButtonGroup(true);
		}
	},
	refreshFromServer:function(json){
		var trs = $(elementList[this.id]).find('tbody tr');		
		trs.each(function(){
			$(this).unbind('click');
		});
		var tds = $(elementList[this.id]).find('tbody td');
		tds.each(function(){
			$(this).unbind('click');
		});
		this.dtable._fnAjaxUpdateDraw(json);
		this.refreshBodyEvents($(elementList[this.id]).children('tbody'), true);
	},
	refresh:function(pageNumber){
		var s = this.dtable.api().settings()[0];
		s.ajax.data={_ajaxUserEvent: "table", _uiid: this.id, _actionName: "pull", _framePrefix: UIMaster.getFramePrefix(UIMaster.El(this.id).get(0)),
            _actionPage: this.parentEntity.__entityName, _selectedIndex: this.selectedIndex, _sync: UIMaster.ui.sync()};
		if (pageNumber != undefined) {
			this.dtable.fnPageChange(pageNumber, true);
		} else {
			var curr = s._iDisplayStart/s._iDisplayLength;
			this.dtable.fnPageChange(curr, true);
		}
	},
	importData:function(){
				  
	},
	exportData:function(){
		var s = this.dtable.api().settings()[0];
		var data = "_ajaxUserEvent=table&_uiid="+this.id+"&_actionName=exportTable&_framePrefix="
					 +UIMaster.getFramePrefix(UIMaster.El(this.id).get(0))+"&_actionPage="+this.parentEntity.__entityName;
		var form = $('<form action='+AJAX_SERVICE_URL+"?r="+Math.random()+'&'+data+' method=post target=_blank></form>');
		$(form).submit(); 
	}
});
UIMaster.ui.webtree = function(conf){
	UIMaster.apply(this, conf);
	
};
UIMaster.ui.webtree = UIMaster.extend(UIMaster.ui, {
	_selectedNodeId:null,
	_selectedNodeName:null,
	_selectedParentNodeId:null,
	_treeObj:null,
	init:function(){
		var t = this;
		var config = $($(this).children()[0]);
		var d = eval("("+config.text()+")");
		var children = d[0].children;
		for (var i=0; i<children.length; i++){
			if(children[i].hasChildren && children[i].children.length==0) {
				children[i].children = true;
			}
		}
		var clickEvent = config.attr("clickevent");
		var expandEvent = config.attr("expendevent");
		var expandEvent0 = config.attr("expendevent0");
		this._addnodeevent = config.attr("addnodeevent");
		this._addnodeevent0 = config.attr("addnodeevent0");
		this._deletenodeevent = config.attr("deletenodeevent");
		this._deletenodeevent0 = config.attr("deletenodeevent0");
		this._refreshnodeevent = config.attr("refreshnodeevent");
		this._refreshnodeevent0 = config.attr("refreshnodeevent0");
		
		this._treeObj = $(this).jstree({ 
			"core":{"data": d, "check_callback" : true}, 
			"plugins":["contextmenu", "dnd"], 
			"contextmenu":{"items": this.createMenu},
			"types": {"#": {"max_children": 1, "max_depth": 10, "valid_children": []}}
		}).bind("loaded.jstree", function(node,tree_obj,e){
			//triggered after the root node is loaded for the first time
		}).bind("ready.jstree", function(node,tree_obj,e){
			//triggered after all nodes are finished loading
			tree_obj.instance.settings.core.data = {
			  'url': function(node) { //node.id === '#' ?
				return AJAX_SERVICE_URL+"?r="+Math.random();
			  },
			  'data': function(node) {
				return {'nodeid' : node.id, 
					'_ajaxUserEvent': true,
	                '_uiid': t.id,
	                '_actionName': expandEvent0,
	                '_framePrefix': UIMaster.getFramePrefix(UIMaster.El(t.id).get(0)),
	                '_actionPage': t.parentEntity.__entityName,
	                '_sync': UIMaster.ui.sync()};
			}};
			$(this).bind("select_node.jstree", function(node,tree_obj,e){
				if (t._selectedNodeId == tree_obj.node.id) {return;}
				t._selectedNodeId=tree_obj.node.id;
				t._selectedParentNodeId=tree_obj.node.parent;
				var tree = t;
				eval(clickEvent);
			}).bind("open_node.jstree", function(node,tree_obj,e){
				t._selectedNodeId=tree_obj.node.id;
				t._selectedParentNodeId=tree_obj.node.parent;
				var ref = $(t).jstree(true);
				ref.select_node(tree_obj.node.id);
			}).bind("create_node.jstree", function(node,tree_obj,position){
				t._selectedNodeId=tree_obj.node.id;
				t._selectedParentNodeId=tree_obj.node.parent;
				var ref = $(t).jstree(true);
				ref.deselect_node(t._selectedParentNodeId);
				ref.select_node(t._selectedNodeId);
			}).bind("rename_node.jstree", function(node,tree_obj,old){
				t._selectedNodeId=tree_obj.node.id;
				t._selectedNodeName=tree_obj.text;
				t._selectedParentNodeId=tree_obj.node.parent;
				var tree = t;
				eval(t._addnodeevent);
			}).bind("delete_node.jstree", function(node,parent){
				t._selectedNodeId=parent.node.id;
				t._selectedParentNodeId=parent.node.parent;
			}).bind("move_node.jstree", function(node,parent,position,old_parent,old_position,is_multi,old_instance,new_instance){
				alert("move_node");
			});
		});
	},
	createMenu:function(node){
		var items = {};
		var t = this.element[0];
		if (t._addnodeevent0 && t._addnodeevent0 != "") {
			items.addItem = {label: "Add...", action: function (e) {
				var ref = $(t).jstree(true);
				t._sel = ref.get_selected();
				if(!t._sel.length) { return false; }
				t._sel = t._sel[0];
				var index = ref.get_node(t._sel).children.length;
				var id = t._sel + "00" + (index+1);
				t._sel = ref.create_node(t._sel, {"id":id, "type":"#"});
				if(t._sel) {
					ref.edit(t._sel);
					var tree = t;
				}
			}};
		}
		if (t._deletenodeevent0 && t._deletenodeevent0 != "") { 
			items.deleteItem = {label: "Delete", action: function (e) {
				var ref = $(t).jstree(true);
				t._sel = ref.get_selected();
				if(!t._sel.length) { return false; }
				ref.delete_node(t._sel);
				var tree = t;
				eval(t._deletenodeevent);
			}};
		}
		if (t._refreshnodeevent0 && t._refreshnodeevent0 != "") {
			items.refreshItem = {label: "Refresh", action: function (e) {
				var ref = $(t).jstree(true);
				t._sel = ref.get_selected();
				if(!t._sel.length) { return false; }
				ref.refresh_node(t._sel);
			}};
		}
		return items;
	},
	sync:function(){
		var obj = UIMaster.getObject(this);
		if (this._selectedNodeId && this._selectedNodeId != "") {
			UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"selectedNode",
								  _value:this._selectedNodeId,_framePrefix:UIMaster.getFramePrefix(obj)});
			UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"selectedParentNode",
				  				  _value:this._selectedParentNodeId,_framePrefix:UIMaster.getFramePrefix(obj)});
			UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"selectedNodeName",
				                  _value:this._selectedNodeName,_framePrefix:UIMaster.getFramePrefix(obj)});
		}
	}
});
var options = [[['YES_NO_OPTION','YES_OPTION','MESSAGE_DIALOG','Error'],0],
                [['YES_NO_CANCEL_OPTION','NO_OPTION','INPUT_DIALOG','Information'],1],
                [['OK_CANCEL_OPTION','OK_OPTION','OPTION_DIALOG','Warning'],2],
                [['OK_ONLY_OPTION','CANCEL_OPTION','CONFIRM_DIALOG','Question'],3],
                [['CLOSE_OPTION'],4]];
/**
 * @description UI Dialog class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.dialog} An UIMaster.ui.dialog element.
 * @class UIMaster UI Dialog.
 * @constructor
 */
UIMaster.ui.dialog= function(conf){
    //UIMaster.require("/js/compound.js",true);
    conf = conf || {};
    UIMaster.apply(this, conf);
    UIMaster.groupAssign(this, options);
    this.title = this.title || UIMaster.i18nmsg("Common||DIALOG_TITLE");
    this.message = this.message || UIMaster.i18nmsg("Common||DIALOG_TITLE_MESSAGE");
    return this;
};
UIMaster.ui.dialog=UIMaster.extend(UIMaster.ui.dialog, /** @lends UIMaster.ui.dialog*/{
    /**
     * @description Dialog title.
     * @type String
     * @default Dialog Title, varies by the locale.
     */
    title : null,
    /**
     * @description Dialog message.
     * @type String
     * @default Message, varies by the locale.
     */
    message : null,
    /**
     * @description Dialog's option type. Candidates are<br/>
     * UIMaster.ui.dialog.YES_NO_OPTION<br/>
     * UIMaster.ui.dialog.YES_NO_CANCEL_OPTION<br/>
     * UIMaster.ui.dialog.OK_CANCEL_OPTION<br/>
     * UIMaster.ui.dialog.OK_ONLY_OPTION
     * @type Number
     * @default UIMaster.ui.dialog.OK_ONLY_OPTION
     */
    optionType : 3,
    options : [],
    initialValue : 0,
    /**
     * @description Dialog's type. Candidates are<br/>
     * UIMaster.ui.dialog.MESSAGE_DIALOG<br/>
     * UIMaster.ui.dialog.INPUT_DIALOG<br/>
     * UIMaster.ui.dialog.OPTION_DIALOG<br/>
     * UIMaster.ui.dialog.CONFIRM_DIALOG
     * @type Number
     * @default UIMaster.ui.dialog.MESSAGE_DIALOG
     */
    dialogType : 0,
    /**
     * @description Dialog's message type. Candidates are<br/>
     * UIMaster.ui.dialog.Error<br/>
     * UIMaster.ui.dialog.Information<br/>
     * UIMaster.ui.dialog.Warning<br/>
     * UIMaster.ui.dialog.Question
     * @type Number
     * @default UIMaster.ui.dialog.Information
     */
    messageType : 1,
    isOpen:false,
    /**
     * @description Dialog's id.
     * @type String
     * @default ''.
     */
    uiid:'',
    /**
     * @description Error message dialog.
     * @type Boolean
     * @default false - Standard dialog.
     */
    error:false,
    /**
     * @description Dialog's height.
     * @type Number
     * @default 115px.
     */
    height: 115,
    /**
     * @description Dialog's width.
     * @type Number
     * @default 300px.
     */
    width: 300,
    /**
     * @description Dialog's x-position.
     * @type Number
     * @default 400px.
     */
    x:-1,
    /**
     * @description Dialog's y-position.
     * @type Number
     * @default screen center.
     */
    y:-1,
    parent:"",
    handler: UIMaster.emptyFn,
    /**
     * @description Open the dialog.
     */
    open:function(){
        if(!this.isOpen){
            this.paint();
            this.isOpen = true;
        }
    },
    paint:function(){
        function closeBtn(e){
            dialog.close();
            dialog.destroy();
        }
        function eventHandler(e){
            diaObj.returnValue = new Number(this.getAttribute("returnType"));
            diaObj.value = $(content).find("td").eq(1).children('[name="returnType"]').val();
            var rV = diaObj.handler.call(diaObj,e);
            if (rV == false)
                return;
            dialog.close();
            dialog.destroy();
        }
        function createBtn(text,returnType,id){
            return $('<input type="button" value="'+text+'" id="'+id+'">')
                        .attr("returnType",returnType)
                        .click(this.error ? closeBtn : eventHandler)
                        .get(0);
        }
        var msgIconText = [UIMaster.i18nmsg("Common||DIALOG_TYPE_ERROR"),
                           UIMaster.i18nmsg("Common||DIALOG_TYPE_INFORMATION"),
                           UIMaster.i18nmsg("Common||DIALOG_TYPE_WARNING"),
                           UIMaster.i18nmsg("Common||DIALOG_TYPE_QUESTION")];
        var msgIcon = ["Error","Information","Warning","Question"];
        var diaObj = this;
        var dialog = $("<div><div>"); 

        var content;
        if ((typeof(this.messageType.length) != 'undefined') && (typeof(this.message.length) != 'undefiend')) {     //multi-line messages
                var p = document.createElement("p");
                for(var i = 0; i < this.messageType.length; i++) {
                    $(p).append('<tr><td class="dialog-image"><img width="48px" height="48px" src="'+RESOURCE_CONTEXTPATH + "/images/" + msgIcon[this.messageType[i]]+'.png" alt="'+msgIconText[this.messageType[i]]+'" title="'+msgIconText[this.messageType[i]]+'"></td><td><div>'+this.message[i]+'</div></td></tr>');
                }
                content = $($(p).html());
        } else {    //single message
            content = document.createElement("tr");
            $(content).append('<td class="dialog-image"><img width="48px" height="48px" src="'+RESOURCE_CONTEXTPATH + "/images/" + msgIcon[this.messageType]+'.png" alt="'+msgIconText[this.messageType]+'" title="'+msgIconText[this.messageType]+'"></td>');
            var msg = document.createElement("td");
            if (this.error) {
                $(msg).append('<pre class="dialog-error-msg">'+this.parent+'</pre>');
                if (this.message != UIMaster.i18nmsg("Common||DIALOG_TITLE_MESSAGE")) {
                    $('<div id="tracebutton" />').addClass('dialog-trace-button-hide').toggle(
                        function(){$('#trace').show();$('#tracebutton').addClass('dialog-trace-button-show');$('#tracetext').text('Hide Detail');$(dialog).height(250);},
                        function(){$('#trace').hide();$('#tracebutton').removeClass('dialog-trace-button-show');$('#tracetext').text('Show Detail');$(dialog).height(100);}).appendTo(msg);
					$(msg).append('<div id="tracetext">'+UIMaster.i18nmsg("Common||DIALOG_ERROR_SHOWDETAIL")+'</div>').append('<pre id="trace" style="display:none; padding-left:10px">'+this.message+'</pre>');
					$(content).append($(msg));
                }
            } else {
                $(msg).css('text-align','center').append('<div>'+this.message+'</div>').appendTo(content);
            }
        }
		
        //append input or select controls to first td
        if(this.dialogType==this.INPUT_DIALOG)
            $(content).find("td").eq(1).append('<input name="returnType" type="text" />');
        else if(this.dialogType==this.OPTION_DIALOG){
            var select = $('<select name="returnType"></select>');
            $.each(this.options,function(key, value){
                $(select).append($("<option></option>")
                    .attr("value",key)
                    .text(value));
                });
            $(content).find("td").eq(1).append(select);
        }
        var contextDiv = document.createElement("table");
        $(contextDiv).append("<tbody></tbody>").width('100%').append(content);
        //append buttons panel
        var optionDiv = document.createElement("div");
        $(optionDiv).addClass("dialog-option");
        if (this.optionType==this.YES_NO_OPTION || this.optionType==this.YES_NO_CANCEL_OPTION)
            $(optionDiv).append(createBtn.call(this,UIMaster.i18nmsg("Common||DIALOG_BTN_YES"),this.YES_OPTION,'yes')).append(createBtn.call(this,UIMaster.i18nmsg("Common||DIALOG_BTN_NO"),this.NO_OPTION,'no'));
        else if (this.optionType==this.CLOSE_OPTION)
            optionDiv.appendChild(createBtn.call(this,UIMaster.i18nmsg("Common||DIALOG_BTN_CLOSE"),this.CLOSE_OPTION,'close'));
        else
            optionDiv.appendChild(createBtn.call(this,UIMaster.i18nmsg("Common||DIALOG_BTN_OK"),this.OK_OPTION,'ok'));
        if (this.optionType==this.YES_NO_CANCEL_OPTION||this.optionType==this.OK_CANCEL_OPTION)
            optionDiv.appendChild(createBtn.call(this,UIMaster.i18nmsg("Common||DIALOG_BTN_CANCEL"),this.CANCEL_OPTION,'cancel'));
        //adjust the space between buttons
        $(optionDiv).find(":button").css("margin-left", "50px").eq(0).css("margin-left", "10px");
		
		dialog.append(contextDiv).append(optionDiv);
        dialog.dialog({
        	title: this.title,
        	width: this.width,
        	height: this.height,
        	modal: true
        });
    }
});
UIMaster.groupAssign(UIMaster.ui.dialog, options);
/**
 * @description Provide UI Mask functions.
 * @namespace Provide a UI mask interface.
 */
UIMaster.ui.mask={
    init:function(){
        if (typeof this._count == 'undefined') 
        {
            this._count = 1;    
        } else {
            this._count = this._count + 1;   
        }
        //if (!document.getElementById('ui-mask-shadow') && !document.getElementById('ui-mask-content'))
        if (this._count == 1)
        {
            var divMask = document.createElement('div');
            divMask.id="ui-mask-shadow";
            divMask.className="ui-overlay";
            divMask.innerHTML = '<div class="ui-widget-overlay"></div>' +
                                '<div id="ui-mask-content" class="outer">' +
                                '<div class="inner"><p></p></div></div>';
            divMask.style.display="none";

            document.body.insertBefore(divMask, document.body.firstChild);
        }
    },
    /**
     * @description Open the mask.
     */
    open:function(msg){
        if (this._isOpen) { return; }
        $('#ui-mask-shadow').show();
        /**
        $('#ui-mask-content').removeClass("ui-info-fail-border ui-info-suc-border")
            .html('<div class="inner"><p class="ui-info-msg"><span></span>'
            + (msg == undefined ? 'Processing... Please wait.': msg) +'</p></div>').show();
        $('#ui-mask-content').find('.inner').parent().css('padding', '6px');
        */
        this._isOpen = true;
    },
    /**
     * @description Close the mask.
     */
    close:function(flag, msg){
        this._count = this._count ? this._count-1 : 0;
        if (this._count != 0 || !this._isOpen) { return; }
        if (typeof flag === 'undefined') {
            $('#ui-mask-shadow').delay(1000).hide();
            $('#ui-mask-content').delay(1000).hide(); 
        } else if (typeof flag === 'boolean') {
            if (flag === false) {
                $('#ui-mask-content').html('<div class="inner"><p class="ui-fail-msg"><span></span>'
                    + (msg == undefined ? 'Error Found.': msg) +'</p></div>').addClass("ui-info-fail-border");
                $('#ui-mask-content').fadeOut(1500);
                $('#ui-mask-shadow').delay(1000).fadeOut(200);
            } else if (flag === true) {
                $('#ui-mask-content').html('<div class="inner"><p class="ui-suc-msg"><span></span>'
                    + (msg == undefined ? 'Operation Successful.': msg) +'</p></div>').addClass("ui-info-suc-border");
                $('#ui-mask-content').fadeOut(1500);
                $('#ui-mask-shadow').delay(1000).fadeOut(200);
            }
        }
        this._isOpen = false;
    },
    /**
     *  @deprecated Open a mask.
     */
    openA:function(msg){
        this.open();
    },
    /**
     *  @deprecated Show failure prompt message before close.
     */
    updateA:function(msg){
        this.close(false, msg);
    }
};
/**
 * @description UI Window class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.window} An UIMaster.ui.window element.
 * @class UIMaster UI Window.
 * @extends UIMaster.ui.dialog
 * @constructor
 */
UIMaster.ui.window=UIMaster.extend(UIMaster.ui.dialog,{
    open:function(){
        if(!this.isOpen){
        	var w = this.width == 0 ? 500: this.width;
        	var h = this.height == 0 ? 300: this.height;
        	var thisObj = this;
            this.content = $("<div><div>").html(this.data).attr("id", this.id);
            
            var buttonset = [];
            if (this.isOnlyShowCloseBtn=="true") {
            	buttonset = [{text:"Close", click:function(){thisObj.close();}}];
            } else {
            	var p = this.content.find("div[id$='actionPanel']");
            	if(p.length > 0) {
            		$(p[0]).css("display", "none");
            		var buttons = $(p[0]).find("input[type='button']");
            		for (var i=0;i<buttons.length;i++) {
            			var b = buttons[i];
            			buttonset[i] = { text:b.value, 
            					click:function(){var e=$(event.srcElement).text();for (var i=0;i<buttons.length;i++){ 
            						if(e==buttons[i].value){$(buttons[i]).click();break;}} } };
            		}
            	}
            }
            this.content.dialog({
            	title: thisObj.title,
            	height: h,
                width: w,
                modal: true,
                show: {
                  effect: "blind",
                  duration: 500
                },
                close: function() {
				  //todo: notify the server side.
                  thisObj.close();
                },
                buttons: buttonset
            });
            $($("#"+this.id).children().get(0)).attr("_framePrefix",this.frameInfo);
            getElementListSingle(this.content);
            eval(this.js);
            defaultname.addComponent(eval(D+this.uiid),true);
            UIMaster.ui.window.addWindow(this.id,this);
            this.isOpen = true;
        }
    },
    close:function(e){
    	if (elementList[this.uiid+".Form"]) 
    		elementList[this.uiid+".Form"].parentEntity.releaseFormObject();
    	this.content.dialog("close");
    	this.content.parent().remove();
    	defaultname.removeComponent(eval(D+this.uiid));
        UIMaster.ui.window.removeWindow(this.id,this);
    }
});
UIMaster.apply(UIMaster.ui.window,{
    wList:[],
    addWindow:function(id,win){this.wList[id]=win},
    removeWindow:function(id){delete this.wList[id]},
    getWindow:function(id){return this.wList[id]}
});
function iframeAutoFitHeight(parent, iframe) {
	iframe.height = $("#"+parent).height() + "px"; 
}
/**
 * @description UI Tab class. Need more information.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.tab} An UIMaster.ui.tab element.
 * @class UIMaster UI Tab
 * @extends UIMaster.ui
 * @constructor
 */
UIMaster.ui.tab=UIMaster.extend(UIMaster.ui,{
    links:[],
    index:0,
    init:function(){
        var othis = this, s = this.childNodes[0].nodeType == 1 ? this.childNodes[0] : this.childNodes[1], n = s.childNodes[0].nodeType == 1 ? s.childNodes[0] : s.childNodes[1];
        for (var i in n.childNodes) {
            if (n.childNodes[i].nodeType == 1) {
            	$(n.childNodes[i]).hover(function(){
                	 var all = $(this).parent().children();
                	 for(var i=0;i<all.length;i++){$(all[i]).removeClass("ui-state-hover");};
                	 $(this).addClass("ui-state-hover");
                }).click(function(){othis.setTab(this)});
            }
        }
        var bodies = $("#bodies-container-" + this.id).children();
        bodies.each(function(){
			if(typeof($(this).attr("uipanelid"))!="undefined"){
        		$(this).append($(elementList[$(this).attr("uipanelid")]).parent());
			}
		});
    },
    setTab:function(e){
        var id = UIMaster.getObject(e).getAttribute("id");
        this._setTab(id);
    },
    _setTab:function(tabObj){
        var currTitle=$("#"+tabObj), titleContainer=currTitle.parent(), titles=titleContainer.children(), bodies=titleContainer.next().children();
        for(var i=0;i<titles.length;i++){$(titles[i]).removeClass("ui-tabs-active").removeClass("ui-state-active").attr("style",null);};
        for(var i=0;i<bodies.length;i++){$(bodies[i]).removeClass("tab-selected-body").addClass("tab-unselected-body");};
        currTitle.addClass("ui-tabs-active").addClass("ui-state-active").attr("style","border-bottom: 1px solid white");
        $("#"+currTitle.attr("id").replace("titles","body")).removeClass("tab-unselected-body").addClass("tab-selected-body");
        titleContainer.attr("selectedIndex",currTitle.attr("index"));
        //$.ajax({url:AJAX_SERVICE_URL,async:false,data:{_ajaxUserEvent:false,_uiid:this.id,_valueName:"selectedIndex",_value:currTitle.attr("index"),_framePrefix:UIMaster.getFramePrefix()}});
        if (currTitle.attr("ajaxload") != null && currTitle.attr("ajaxload") == "true") {
        	currTitle.attr("ajaxload", null);
        	$.ajax({url:AJAX_SERVICE_URL,async:false,success: UIMaster.cmdHandler,data:{_ajaxUserEvent:"tabpane",_uiid:this.id,_valueName:"selectedIndex",_value:currTitle.attr("index"),_framePrefix:UIMaster.getFramePrefix()}});
        }
    },
    addFrameTab:function(title,url){
    	if (this.links.length == 0) {
    		this.addTab(url, title, this.index++, true);
    	} else {
			for(var i=0;i<this.links.length;i++) {
			    if(this.links[i].src==url) {
			    	this._setTab(this.links[i].tabId);
				    return;
			    }
			}
    		this.addTab(url, title, this.index++, true);
    	}
    },
    addTab:function(html, title, index, isUrl){
        var titles = $("#titles-container-" + this.id).children(), bodies = $("#bodies-container-" + this.id).children(), length = titles.length;
        if (index === undefined || index == ""){index = this.index++;}
        var newId = "tab-" + this.id + "-titles-" + index, newBodyId = newId.replace("titles","body"), titleHtml = $("<div id=\""+ newId + "\" class =\"ui-state-default ui-corner-top\" index=\""+ 
        		index +"\"><span style=\"float:left;\">" + title + "&nbsp;&nbsp;</span><span class=\"ui-icon ui-icon-circle-close\" i=\""+index+"\"></span></div>");
        this.links.push({src: html, tabId: newId, i: index});
        var bodyHtml;
        if (isUrl != undefined) {
        	var frameId = html.substring(html.indexOf("_framename=") + "_framename=".length);
        	frameId = frameId.substring(0, frameId.indexOf("&"));
        	bodyHtml = $("<div id=\"" + newBodyId + "\" class=\"tab-unselected-body\" index=\""+ index +"\"><iframe id=\""+frameId+"\" name=\""+frameId+"\" src=\""+html+"\" needsrc=\"true\" frameborder=\"0\" style=\"min-width:100%;min-height:100%;\"></iframe></div>");
			var t = this,f=bodyHtml.children()[0];
        } else {
        	bodyHtml = $("<div id=\"" + newBodyId + "\" class=\"tab-unselected-body\" index=\""+ index +"\">" + html + "</div>");
        }
        titleHtml.appendTo($("#titles-container-" + this.id));
        bodyHtml.appendTo($("#bodies-container-" + this.id));
        
        var othis = this;
        titleHtml.click(function(){if(!$(this).attr("removed")) {othis.setTab(this)} });
        titleHtml.children(".ui-icon-circle-close").click(function(){othis.removeTab($(this).attr("i"));});
        //getElementListSingle(document.getElementById("bodies-container-" + this.id));
        this._setTab(newId);
    },
    addTabByLazyLoading:function(html, tabUiid) {
    	var updateContent=null;
    	var bodies = $("#bodies-container-" + this.id).children();
    	bodies.each(function(){
			if($(this).attr("uiid") == tabUiid){
				$(html).appendTo($(this));
				updateContent = $(this);
        		return false;
			}
		});
    	return updateContent;
    },
    removeTab:function(index){
    	var othis = this;
        var titles = $("#titles-container-" + this.id).children(), bodies = $("#bodies-container-" + this.id).children();
        titles.each(function(){
        	if($(this).attr("index") == index){
        		$(this).attr("removed","true");
        		$(this).fadeOut(500, function(){
        			this.remove();
        			titles = $("#titles-container-" + othis.id).children();
        			if(titles.length != 0)
        				othis._setTab($(titles[0]).attr("id"));
        		});
        		bodies.each(function(){
        			if($(this).attr("index") == index){
        				var c = $(this).children();
        				if (c.length > 0 && c[0].tagName.toLowerCase() == "iframe") { 
        					var obj = othis.ui;
        					$.ajax({url:AJAX_SERVICE_URL,async:true,data:{_ajaxUserEvent:"tabpane",_uiid:othis.id,_valueName:"remveTabId",_value:$(c[0]).attr("name"),_framePrefix:UIMaster.getFramePrefix()}});
        				}
                		$(this).remove();
                		return false;
        			}
        		});
        		for(var i=0;i<othis.links.length;i++){
        			if (othis.links[i].i == index) {
        				othis.links.remove(i);
        				break;
        			}
        		}
        		return false;
        	}
        });
    },
    setTabAt:function(html, index){
        var bodies = $("#bodies-container-" + this.id).children();
        if (index === undefined || index >= bodies.length || index < 0)
            return;
        bodies.slice(index,index+1).html(html);
    },
    setTitleAt:function(title, index){
        var titles = $("#titles-container-" + this.id).children();
        if (index === undefined || index >= titles.length || index < 0)
            return;
        titles.slice(index,index+1).html(title);
    },
    setSelectedTab:function(index){
        var titles = $("#titles-container-" + this.id).children();
        if (index === undefined || index >= titles.length || index < 0)
            return;
        this._setTab(titles[index].getAttribute("id"));
    },
    getTabLength:function(){
    	return $("#titles-container-" + this.id).children().length;
    }
});
UIMaster.ajaxDefaultSettings = {
    async: false,
    type: "POST",
    url: AJAX_SERVICE_URL,
    success: UIMaster.cmdHandler
};
/**
 * @description UIMaster ajax method. Usually used to send value to 
 *              server side.
 * @param       {url}       the server url
 *              {data}      the data need to pass to server
 *              {callback}  callback function when ajax executed successfully
 *              {type}      the format of response text
 * @return                   none
 *
 */ 
UIMaster.ajaxSend = function(url, data, callback, type) {
    callback = callback || null;
    var args = jQuery.extend({}, UIMaster.ajaxDefaultSettings, {
                    url: url, 
                    data: data,
                    success: callback,
                    dataType: type
                });
    jQuery.ajax( args );
};
/**
 * @description UIMaster ajax method. Usually used to get a value from
 *              server side.
 * @param       {url}       the server url
 *              {data}      the data need to pass to server
 *              {type}      the format of repsonse text
 * @return                  none.
 *
 */ 
UIMaster.ajaxReceive = function(url, data, type) {
    var args = jQuery.extend({}, UIMaster.ajaxDefaultSettings, {
                    url: url, 
                    data: data,
                    success: function(msg){
                        res = msg;
                    },
                    dataType: type
                }), res = null;
    jQuery.ajax( args );
    return res;
};
/**
 * @description Set UIMaster Ajax synchronization mode to true.
 *
 * @param       {target}    the object will be used to
 *                          hold the sync value.
 *
 */
UIMaster.asyncAjax = function(target) {
    if (target) {
        target["asyncAttr"] = true;
    }
    return target;
};
/**
 * @description Set UIMaster Ajax "complete" callback handler,
 *              this function will be called after "success" handler.
 *              
 * @param       {target}    the object will be used to
 *                          hold the "complete" function.
 *              {fn}        the "complete" function body.
 *
 */
UIMaster.ajaxCompleteHandler = function(target, fn) {
    if (target) {
        target["completeHandler"] = fn;
    }
    return target;
};
})();
if (window.jQuery == undefined) UIMaster.require("/js/jquery.js", true);
UIMaster.ready = $(document).ready;


var bmiasia_UIMaster_appbase_Util = {
    checkBrowser: function(){return UIMaster.browser.msie?'IE':(UIMaster.browser.opera?'Opera':(UIMaster.browser.mozilla?'Mozilla':'Unknown'));},
    elmPosition: function(elm){return UIMaster.El(elm).getPosition();},
    findParent: function(elm, tag){
        while (elm && elm.tagName && elm.tagName.toLowerCase() != tag.toLowerCase())
            elm = elm.parentNode;
        return elm;
    },
    getEventElement: function(e){return UIMaster.getObject(e);}
};

var UIMaster_dialog_container = {
    CONTEXT_DIV_ID : "UIMaster_dialog_container",
    isExistContainer: function( containerType ){
        return document.getElementById(UIMaster_dialog_container.CONTEXT_DIV_ID) && document.getElementById(containerType);
    },
    getDefaultContainer: function( containerType, pnode ){
        function findParent(pnode){
            var node = pnode.parentNode;
            if (node != document)
                while (node.getAttribute("type") != "frame")
                    if (node != document.body)
                        node = node.parentNode;
                    else
                        break;
            return node;
        }
        var container = document.getElementById(UIMaster_dialog_container.CONTEXT_DIV_ID);
        if(container == null){
            container = document.createElement("DIV");
            container.setAttribute("id", UIMaster_dialog_container.CONTEXT_DIV_ID );
            if (pnode){
                var node = findParent(pnode);
                if (node == document.body || node == document)
                    document.getElementsByTagName("body")[0].appendChild( container );
                else
                    node.appendChild( container );
            } else
                document.getElementsByTagName("body")[0].appendChild( container );
        } else {
            if (pnode){
                var node = findParent(pnode);
                if (!(node == document.body || node == document)){
                    if(node.childNodes.length > 1)
                        container = node.childNodes[1];
                    else{
                        $(container).remove();
                        container = document.createElement("DIV");
                        container.setAttribute("id", UIMaster_dialog_container.CONTEXT_DIV_ID );
                        node.appendChild( container );
                    }
                }
            }
        }
        var subContainer = document.getElementById(containerType);
        if(subContainer == null){
            subContainer = document.createElement("DIV");
            subContainer.childIndex = 0;
            subContainer.setAttribute("id", containerType );
            container.appendChild( subContainer );
        }
        return subContainer;
    }
};

function bmiasia_UIMaster_appbase_getDomDocumentPrefix(){
    if (bmiasia_UIMaster_appbase_getDomDocumentPrefix.prefix)
        return bmiasia_UIMaster_appbase_getDomDocumentPrefix.prefix;

    var prefixes = ["MSXML2", "Microsoft", "MSXML", "MSXML3"],o,i;
    for (i = 0; i < prefixes.length; i++)
        try {
            o = new ActiveXObject(prefixes[i] + ".DomDocument");
            return bmiasia_UIMaster_appbase_getDomDocumentPrefix.prefix = prefixes[i];
        }
        catch (ex){}
    throw new Error("Could not find an installed XML parser");
}

function bmiasia_UIMaster_appbase_getXmlHttpPrefix(){
    if (bmiasia_UIMaster_appbase_getXmlHttpPrefix.prefix)
        return bmiasia_UIMaster_appbase_getXmlHttpPrefix.prefix;

    var prefixes = ["MSXML2", "Microsoft", "MSXML", "MSXML3"],o,i;
    for (i = 0; i < prefixes.length; i++)
        try {
            o = new ActiveXObject(prefixes[i] + ".XmlHttp");
            return bmiasia_UIMaster_appbase_getXmlHttpPrefix.prefix = prefixes[i];
        }
        catch (ex) {};
    throw new Error("Could not find an installed XMLHttp object");
}

function bmiasia_UIMaster_appbase_XmlHttp() {}

bmiasia_UIMaster_appbase_XmlHttp.create = function () {
    try {
        // NS & MOZ
        if (window.XMLHttpRequest) {
            var req = new XMLHttpRequest();

            // some versions of Moz do not support the readyState property
            // and the onreadystate event so we patch it!
            if (req.readyState == null) {
                req.readyState = 1;
                req.addEventListener("load", function () {
                    req.readyState = 4;
                    if (typeof req.onreadystatechange == "function")
                        req.onreadystatechange();
                }, false);
            }

            return req;
        }
        if (window.ActiveXObject)
            return new ActiveXObject(bmiasia_UIMaster_appbase_getXmlHttpPrefix() + ".XmlHttp");
    }
    catch (ex) {}
    throw new Error("Your browser does not support XmlHttp objects");
};


function bmiasia_UIMaster_appbase_XmlDocument() {}

bmiasia_UIMaster_appbase_XmlDocument.create = function (){
    try{
        if (document.implementation && document.implementation.createDocument){
            var doc = document.implementation.createDocument("", "", null);
            if (doc.readyState == null){
                doc.readyState = 1;
                doc.addEventListener("load", function(){
                    doc.readyState = 4;
                    if (typeof doc.onreadystatechange == "function")
                        doc.onreadystatechange();
                }, false);
            }
            return doc;
        }
        if (window.ActiveXObject)
            return new ActiveXObject(bmiasia_UIMaster_appbase_getDomDocumentPrefix() + ".DomDocument");
    }
    catch (ex){}
    throw new Error("Your browser does not support XmlDocument objects");
};

if (window.DOMParser &&
    window.XMLSerializer &&
    window.Node && Node.prototype && Node.prototype.__defineGetter__) {

    Document.prototype.loadXML = function(s){		
        var doc2 = (new DOMParser()).parseFromString(s, "text/xml");

        while (this.hasChildNodes())
            this.removeChild(this.lastChild);
        for (var i = 0; i < doc2.childNodes.length; i++)
            this.appendChild(this.importNode(doc2.childNodes[i], true));
    };

    Document.prototype.__defineGetter__("xml", function (){
        return (new XMLSerializer()).serializeToString(this);
    });
}

function bmiasia_UIMaster_appbase_AjaxClient(url, method, aysn, callBack, contentType){
    this._url = url || "";
    this._param = "";
    this._method = method || "GET";
    this._aysn = (aysn == null) ? false : aysn;
    this.callBack = callBack;
    this.contentType = contentType || this.CONTENT_TYPE;
    this._xHttp = bmiasia_UIMaster_appbase_XmlHttp.create();
}
bmiasia_UIMaster_appbase_AjaxClient.prototype = {
    CONTENT_TYPE: "application/x-www-form-urlencoded",

    setUrl : function(url) {
        this._url = url;
    },
    setMethod : function(method) {
        this._method = method;
    },
    setAysn : function(aysn) {
        this._aysn = aysn;
    },
    callBackObj : function(callBack) {
        this.callBack = callBack;
    },
    setContentType: function(contentType) {
        this.contentType = contentType;
    },
    append : function(key,value) {
        if(key == null)
            return;
        value = (value == null || value == undefined) ? "" : value;

        this._param += "&" + encodeURIComponent(key) + "=" + encodeURIComponent(value);
        return this;
    },
    appendAllForms:function(formObj){
        var result = false;
        try{
            if ( typeof(formObj) == undefined )
                throw UIMaster_getI18NInfo("Common||FORM_NOTFIND");
            var _tags = ["input","select","textarea"];
            for ( var j = 0; j < _tags.length; j++ ){
                var _elements = formObj.all.tags(_tags[j]);
                for ( var i = 0; i < _elements.length; i++ )
                    if ( j==0 && _elements[i].type=="radio" ){
                        if ( _elements[i].checked )
                            this.append(_elements[i].name,_elements[i].value);
                    }
                    else
                        this.append(_elements[i].name,_elements[i].value);
            }
            result = true;
        }
        catch(ex){
            alert(ex.description);
        }
        return result;
    },
    submit:function(){
        this.requestData();
        if( !this._aysn )
            return this.xmlParse(this._xHttp.responseText);
    },
    submitAsString:function(){
        this.requestData();
        if( !this._aysn )
            return this._xHttp.responseText;
    },
    xmlParse:function(str){
        var xmlDoc = bmiasia_UIMaster_appbase_XmlDocument.create();
        xmlDoc.async = false;
        xmlDoc.loadXML(str);
        if ( xmlDoc.parseError && xmlDoc.parseError.errorCode != 0 ){
            var myErr = xmlDoc.parseError;
            throw myErr;
        }
        else if (xmlDoc.documentElement == null)
            throw "Parse error";
        else
            return xmlDoc.documentElement;
    },
    /**
     *  readyState:
     *  <br> 0-UNINITIALIZED??
     *  <br> 1-LOADING??
     *  <br> 2-LOADED??
     *  <br> 3-INTERACTIVE??
     *  <br> 4-COMPLETED??
     */
    requestData:function(){
        if ( this._xHttp.readyState != 0){
            bmiasia_UIMaster_appbase_StatusBar.showInfo("Request Waiting...");//$NOI18N.
            window.setTimeout("bmiasia_UIMaster_appbase_StatusBar.setStatusShow(false)",2000);	
            return;
        }

        if( this._aysn ){
            // regist call-back event, standby call-back.
            var aysnObjHttp = this._xHttp;
            var callBackFuc = this.callBack;
            var xmlParseFuc = this.xmlParse;
            aysnObjHttp.onreadystatechange = function(){
                if ( aysnObjHttp.readyState == 4 ){
                    if ( aysnObjHttp.status == 200 ){
                        if (callBackFuc != null){
                            var cmds = null; var resultText = aysnObjHttp.responseText;
                            try{cmds = eval("("+resultText+")");}catch(e){}
                            if (cmds && cmds.length > 0 && UIMaster.handler[cmds[0].jsHandler])
                                UIMaster.cmdHandler(resultText);
                            else if (callBackFuc instanceof bmiasia_UIMaster_appbase_CallBack)
                                resultText.indexOf("[ajax_error]") == 0 ? callBackFuc.callExceptionMethod(resultText.substring("[ajax_error]".length, resultText.length)) : callBackFuc.isXML ? callBackFuc.callMethod(xmlParseFuc(resultText)) : callBackFuc.callMethod(resultText);
                            else if (typeof(callBackFuc) == "function")
                                callBackFuc(resultText);
                        }
                    }
                    else{
                        bmiasia_UIMaster_appbase_StatusBar.showInfo("aysnObjHttp.status: "+aysnObjHttp.status
                                    +" "+UIMaster_getI18NInfo("Common||AJAX_EXCEPTION_INTERNAL"));
                        window.setTimeout("bmiasia_UIMaster_appbase_StatusBar.setStatusShow(false)",2000);	
                    }
                }
            };
        }
        else
            this._xHttp.onreadystatechange = function(){};

        if( this._method.toUpperCase() == "POST" ){
            this._xHttp.open("POST",this._url,this._aysn);
            this._xHttp.setRequestHeader("content-length",this._param.length);
            this._xHttp.setRequestHeader("content-type",this.contentType);
            this._xHttp.send(this._param);
        }
        else{
            var data = this._url+(this._url.indexOf('?') > 0 ? '&' : '?')+"SIGNATURE=AjaxClient"+ this._param;
            this._xHttp.open("GET",data,this._aysn);
            this._xHttp.send(null);
        }
    }
};

function bmiasia_UIMaster_appbase_CallBack(method, exceptionMethod, arguments, isXML){
    this.method = method;
    this.exceptionMethod = exceptionMethod;
    this.arguments = (arguments == null) ? new Array(): arguments;
    this.isXML = ((isXML == null) ? false : isXML);
    this.exceptionMsg = "";
}
bmiasia_UIMaster_appbase_CallBack.prototype = {
    /**
     * optional.
     * call-back method
     * @param {Object} method
     */
    method : null,
    /**
     * optional.
     * if call-back( status != 200 ) exception, call exceptionMethod.
     */
    exceptionMethod: null,
    /**
     * call-back method parameters
     */
    arguments : null,
    /**
     * whether responseText convert to xml object or not.
     * @param {Object} method
     */
    isXML: false,
    isXMLType:function(bool){
        this.isXML = bool;
    },
    setMethod:function(method){
        this.method = method;
    },
    setExceptionMethod:function(exceptionMethod){
        this.exceptionMethod = exceptionMethod;
    },
    setArgument:function(arguments){
        this.arguments = arguments || [];
    },
    getArgument:function(){
        return this.arguments;
    },
    callMethod:function(responseObj){
        if( this.method != null && typeof(this.method) == "function" ){
            this.arguments.push(responseObj);	
            this.method.call( this );
        }
    },
    callExceptionMethod:function(msg){
        this.exceptionMsg = msg;
        if( this.exceptionMethod != null && typeof(this.exceptionMethod) == "function" )
            this.exceptionMethod( this );
    }
};

function UIMaster_getI18NInfo(keyInfo, param, languageType){
    var v = UIMaster_getI18NInfo.get(keyInfo);
    if (param == undefined && v)
        return v;
    var object = new bmiasia_UIMaster_appbase_AjaxClient(AJAX_SERVICE_URL);
    object.append('serviceName','I18NService').append('KEYINFO',keyInfo);
    if(param != null && param != "")
        if( param.length > 0 ){
            var str = "";
            for(var i = 0 ; i < param.length ; i++)
                str += param[i]+"||";
            //param1||param2||param3||param n||
            object.append('ARGUMENTS',str);
        }
    languageType && object.append('LANGUAGE',languageType);

    var m = object.submitAsString().replace(/^\s+|\s+$/g,"");

    try {
        var c = eval("("+m+")");
        if (c.length==1) UIMaster.cmdHandler(m);
    } catch(e) {
        return UIMaster_getI18NInfo.put(keyInfo, m);
    }
};
UIMaster_getI18NInfo.cache=[];
UIMaster_getI18NInfo.get=function(key){
    return this.cache[key];
};
UIMaster_getI18NInfo.put=function(key,value){
    this.cache[key]=value;
    return value;
};

function UIMaster_getFormattedDate(format, datetype, date, datestring){
    var object = new bmiasia_UIMaster_appbase_AjaxClient(AJAX_SERVICE_URL);
    object.append('serviceName','DateFormatService');
    if (date && date instanceof Date) object.append('DATE',date.getTime());
    return object.append('FORMAT',format).append('DATETYPE',datetype).append('DATESTRING',datestring).append('OFFSET', date?date.getTimezoneOffset():new Date().getTimezoneOffset()).submitAsString().replace(/^\s+|\s+$/g,"");
}

function bmiasia_UIMaster_appbase_setTimezoneOffset(){
    new bmiasia_UIMaster_appbase_AjaxClient(AJAX_SERVICE_URL).append('serviceName','SetTimezoneOffset').append('OFFSET',new Date().getTimezoneOffset()).submitAsString();
}

var bmiasia_UIMaster_appbase_StatusBar = new function(){
    this.statusDiv = null;
    this.isOff = false;

    this.init = function(){
        if (this.statusDiv !== null)
            return;
        var body = document.getElementsByTagName("body")[0];
        $('<div></div>').css({'position':'absolute','top':'0','left':'0','cursor':'wait'}).width('100%').height('100%').attr('id','bmiasia_UIMaster_appbase_div_status').append($('<div></div>').css({'position':'absolute','top':'50%','left':'40%','cursor':'wait','backgroundColor':'#e4e7ef','margin':'-50px 0 0 -100px','padding':'15px','border':'1px solid #0a246a','color':'#2b2e36','fontSize':'14px','textAlign':'center'}).width(280).attr('id','bmiasia_UIMaster_appbase_div_statustext')).hide().appendTo(body);
        this.statusDiv = document.getElementById("bmiasia_UIMaster_appbase_div_status");
        this.statusDiv.statusText = document.getElementById("bmiasia_UIMaster_appbase_div_statustext");
    };
    this.turnOn = function(){this.isOff = false;};
    this.turnOff = function(){this.isOff = true;};
    this.showInfo = function(_message){
        if (this.isOff)
            return;

        if (this.statusDiv === null)
            this.init();
        this.setStatusShow(true);
        this.statusDiv.statusText.innerHTML = _message;
    };

    this.setStatusShow = function(_show){
        if (this.isOff)
            return;

        if (this.statusDiv === null)
            this.init();
        if (_show)
            this.statusDiv.style.display = "";
        else{
            this.statusDiv.statusText.innerHTML = "";
            this.statusDiv.style.display = "none";
            this.statusDiv.style.cursor  = "pointer";
        }
    };
};

var DATA_FORMAT_SERVICE_URL = "/jsp/common/DataFormatService.jsp";

function bmiasia_UIMaster_appbase_getDataFormat(dataType, localeConfig, formatName){
    var ajaxClient = new bmiasia_UIMaster_appbase_AjaxClient(WEB_CONTEXTPATH + DATA_FORMAT_SERVICE_URL);
    ajaxClient.append("datatype", dataType);
    localeConfig && ajaxClient.append("localeconfig", localeConfig);
    formatName && ajaxClient.append("formatname", formatName);
    return ajaxClient.submitAsString();
}

function bmiasia_UIMaster_appbase_getDateTimeFormat(localeConfig, formatName){
    return bmiasia_UIMaster_appbase_getDataFormat("dateTime", localeConfig, formatName);
}

function bmiasia_UIMaster_appbase_getDateFormat(localeConfig, formatName){
    return bmiasia_UIMaster_appbase_getDataFormat("date", localeConfig, formatName);
}

function bmiasia_UIMaster_appbase_getFloatNumberFormat(localeConfig, formatName){
    return bmiasia_UIMaster_appbase_getDataFormat("floatNumber", localeConfig, formatName);
}

function bmiasia_UIMaster_appbase_getNumberFormat(localeConfig, formatName){
    return bmiasia_UIMaster_appbase_getDataFormat("number", localeConfig, formatName);
}

function bmiasia_UIMaster_appbase_getCurrencyFormat(localeConfig, formatName){
    return bmiasia_UIMaster_appbase_getDataFormat("currency", localeConfig, formatName);
}

var elementList = new Array();

function getFormElementList(formName){
    getElementListSingle(document.forms[formName]);
    disableFormDoubleSubmit(formName);
    UIMaster.init();
}
function getIframeElementList(formNode){
    getElementListSingle(formNode);
    disableFormDoubleSubmitInternal(formNode);
}
function getElementList(){
    getElementListSingle(document.forms[0]);
    disableDoubleSubmit();
    UIMaster.init();
    focusFirstTextField();
}
/*
 * Place focus on the first editable, non-defaulted control on the page so the user can begin typing immediately.
 */
function focusFirstTextField(){
    for (var key in elementList) {
        if (elementList[key].type == "text" && elementList[key].disabled == false &&
        elementList[key].readOnly == false &&
        elementList[key].style.display != "none") {
            try {
                elementList[key].focus();
                break;
            }
            catch (e) {
            }
        }
    }
}
function getElementListSingle(obj){//div[id!=''],
    var el = $(obj).find("div[id!=''],input[name!='__resourcebundle'][name!='isLeftToRight'],select,textarea,button,table,iframe");
    for (var i = 0; i < el.length; i++) {
        var e = el[i], t = elementList[e.name];
        if (e && e.type=="")
            continue;
        e.id && e.id.indexOf('div-') < 0 && !elementList[e.id] && (elementList[e.id] = e);
        if (e.name)
            if (!t)
                elementList[e.name] = e;
            else {
                if (t.length == undefined || t.type == "select-one" || t.type == "select-multiple")
                    elementList[e.name] = [t,e];
                else
                    t[t.length] = e;
            }
    }
}
function disableDoubleSubmit(){
    disableFormDoubleSubmitInternal(document.forms[0]);
}
function disableFormDoubleSubmit(formName){
    disableFormDoubleSubmitInternal(document.forms[formName]);
}
function disableFormDoubleSubmitInternal(formNode){
    function appendHidden(key,value){
        return $('<input type="hidden" />').attr({'value':value,'name':key});
    }
    function formSubmit(form,target){
        var f,i,tf,s;
        for(i=0;i<form.elements.length;i++)
            if(form.elements[i].parentEntity){
                f=form.elements[i].parentEntity;break;
            }
        while(f.parentEntity)
            f=f.parentEntity;
        f.sync();
        tf = $('<form></form>').attr({'action':form.action,'method':'POST','target':form.target,'encoding':form.encoding}).append(appendHidden('_pagename',form._pagename.value)).append(appendHidden('_outname',form._outname.value)).append(appendHidden('_framePrefix',form.getAttribute('_framePrefix'))).append($(myForm).find('input[type=file]'));
        if (form._portletId)
            tf.append(appendHidden('_portletId',form._portletId.value));
        if (target)
            tf.append(appendHidden('_frameTarget',target));
            
        var k = $("#_htmlkey");
        if(k.length>0)	// if this request just for fetch result
            tf.append(appendHidden('_htmlkey',k.attr("value")));
        else {
            s = $("#_sync");
            if (s.length>0) // if this request is re-submit. Be careful that the tf form is not the original form
                tf.append(appendHidden('_sync',s.attr("value")));
            else {
                s=UIMaster.ui.sync();
                if (s) tf.append(appendHidden('_sync',s));
            }  
        }
        
        tf.appendTo(form.parentNode);
        tf.submit();
    }
    var myForm = formNode;
    myForm._submit = myForm.submit;
    myForm.submit = function(){
        if (myForm.target == "_self") {
            if (myForm.enableSubmited != false) {
                myForm.enableSubmited = false;
                formSubmit(myForm);
            }
            else
                alert(UIMaster_getI18NInfo("Common||AJAX_EXCEPTION_REQUEST_WAIT"));
        }
        else
            if (myForm.target == "_parent") {
                if (parent.document.enableSubmited != false) {
                    parent.document.enableSubmited = false;
                    var _frameTarget = window.parent.document.forms[0].getAttribute('_framePrefix') || '';
                    formSubmit(myForm,_frameTarget)
                }
                else
                    alert(UIMaster_getI18NInfo("Common||AJAX_EXCEPTION_REQUEST_WAIT"));
            }
            else {
                frame = lookupFrame(myForm.target, window.top.frames);
                if (frame != undefined) {
                    if (frame.document.enableSubmited != false) {
                        frame.document.enableSubmited = false;
                        formSubmit(myForm,myForm.target);
                    }
                    else
                        alert(UIMaster_getI18NInfo("Common||AJAX_EXCEPTION_REQUEST_WAIT"));
                }
                else
                    formSubmit(myForm,myForm.target);
            }
    }
}

function attachWindowEvent(){
    window.onunload == null && (window.onunload = releaseMem);
}

function releaseMem(){
    var objs;
    if (document.forms[0]) {
        objs = document.forms[0].elements;
        for (var i = objs.length - 1; i >= 0; i--)
            releaseMemSingle(objs[i]);
        objs = null;
    }

    objs = document.links;
    for (var i = objs.length - 1; i >= 0; i--)
        releaseMemSingle(objs[i]);
    objs = null;

    delete defaultname;
    defaultname = null;
    delete elementList;
    elementList = null;
    window.onload = null;
    window.onunload = null;
}
function releaseMem4Frame(frame, frameName){
    var objs = frame.elements;
    if (objs)
        for (var i = objs.length - 1; i >= 0; i--)
            releaseMemSingle(objs[i]);
    objs = null;
    for (var i in elementList)
        if (i.indexOf(frameName) == 0)
            delete elementList[i];
    var frame = eval("defaultname."+frameName);
    for (var i in frame.parentEntity)
        if(frame.parentEntity[i].id == frameName){
            delete frame.parentEntity[i];
            break;
        }
    frame = null;
}
function releaseMemSingle(obj){
    if (obj == null)
        return;
    if ("clearAttributes" in obj)
        obj.clearAttributes();
}
