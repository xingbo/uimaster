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
 *  hard code for dynamic page links.
 */
function dPageLink(link){
	var t = parent.defaultname.functionTree._treeObj; t.deselect_node(t.get_selected()); t.select_node(link);
};
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
    if (data.name == undefined) {
       e.removeAttr(data.data);
    } else {
	   e.removeAttr(data.name);
    }
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
    tree.refresh(children);
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
