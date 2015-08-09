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
		
		var _treeObj = $(this).jstree({ 
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
		this._treeObj = $(_treeObj).jstree(true);
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
	},
	refresh:function(children){
		for (var i=0; i<children.length; i++){
			if(children[i].hasChildren && children[i].children.length==0) {
				children[i].children = true;
			}
		}
		$(this).jstree({"core":{"data": children, "check_callback" : true}});
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
