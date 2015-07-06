/*-------------------------------------------------------------------- 
Scripts for creating and manipulating custom menus based on standard <ul> markup
Version: 3.0, 03.31.2009

By: Maggie Costello Wachs (maggie@filamentgroup.com) and Scott Jehl (scott@filamentgroup.com)
	http://www.filamentgroup.com
	* reference articles: http://www.filamentgroup.com/lab/jquery_ipod_style_drilldown_menu/
		
Copyright (c) 2009 Filament Group
Dual licensed under the MIT (filamentgroup.com/examples/mit-license.txt) and GPL (filamentgroup.com/examples/gpl-license.txt) licenses.

Customized by Shaolin
--------------------------------------------------------------------*/

var allUIMenus = [];

$.fn.menu = function(options){
	var caller = this;
	var options = options;
	var uiMenus = allUIMenus[options.groupId];
	$.each(uiMenus, function(i){
		if( uiMenus.length > i){
			if (options.menuId == uiMenus[i].menuId) {
				uiMenus[i].destroy();
				uiMenus.splice(i,1);
			}
		}
	});
	var m = new Menu(caller, options);	
	m.menuId = options.menuId;
	m.groupId = options.groupId;
	uiMenus.push(m);
};
$.fn.menu.applyAGroup = function(uiid){ allUIMenus[uiid]=[]; };
$.fn.menu.removeGroup = function(uiid){
	var uiMenus = allUIMenus[uiid];
	if(!uiMenus){ return;}
	$.each(uiMenus, function(i){
		if (uiMenus[i].menuOpen) { uiMenus[i].kill(); }	
		uiMenus[i].destroy();
	});
	allUIMenus[uiid] = [];
};
$.fn.menu.removeMenu = function(uiid, menuId){
	var uiMenus = allUIMenus[uiid];
	if(!uiMenus){ return;}
	$.each(uiMenus, function(i){
		if (i >= uiMenus.length){return;}
		if (uiMenus[i].menuId == menuId) { 
			if(uiMenus[i].menuOpen){uiMenus[i].kill();} 
			uiMenus[i].destroy(); 
			uiMenus.splice(i,1);
		}	
	});
};
$.fn.menu.killAll = function(uiid){
	var uiMenus = allUIMenus[uiid];
	if(!uiMenus){ return;}
	$.each(uiMenus, function(i){
		if (uiMenus[i].menuOpen) { uiMenus[i].kill(); }	
	});
};
$.fn.menu.kill = function(uiid, menuId){
	var uiMenus = allUIMenus[uiid];
	if(!uiMenus){ return;}
	$.each(uiMenus, function(i){
		if (uiMenus[i].menuId == menuId && uiMenus[i].menuOpen)
			uiMenus[i].kill();
	});
};
$.fn.menu.isMe = function(uiid, menuId){
	var uiMenus = allUIMenus[uiid]; var flag;
	if(!uiMenus){ return true;}
	$.each(uiMenus, function(i){
		if (uiMenus[i].menuId == menuId )
			flag = true;
	});
	return flag;
};

function Menu(caller, options){
	var menu = this;
	var caller = $(caller);
	// the options.content will copy as a DOM object to the menu area.
	var container = $('<div class="fg-menu-container ui-widget ui-widget-content ui-corner-all"></div>').append($(options.content).clone(true));

	this.menuOpen = false;
	this.menuExists = false;
	
	var options = jQuery.extend({
		menuId: null, // required!
		groupId: null, // required!
		content: null, // required! the target content that all items will start with this content.
		width: 180, // width of menu container, must be set or passed in to calculate widths of child menus
		maxHeight: 180, // max height of menu (if a drilldown: height does not include breadcrumb)
		loadItemLink: "/jsp/webmenu/loadmenu.jsp", // get the subitems from service side.
		positionOpts: {
			posX: 'left', 
			posY: 'bottom',
			offsetX: 0,
			offsetY: 0,
			directionH: 'right',
			directionV: 'down', 
			detectH: true, // do horizontal collision detection  
			detectV: true, // do vertical collision detection
			linkToFront: false
		},
		showSpeed: 200, // show/hide speed in milliseconds
		callerOnState: 'ui-state-active', // class to change the appearance of the link/button when the menu is showing
		loadingState: 'ui-state-loading', // class added to the link/button while the menu is created
		linkHover: 'ui-state-hover', // class for menu option hover state
		linkHoverSecondary: 'li-hover', // alternate class, may be used for multi-level menus
		singleIcon: 'ui-icon-bullet', // class to the style of single item
		multipleIcon: 'ui-icon-arrow-1-ne', // class to the style of multiple item
		singleChoice: false, // single choice mode
		multipleChoice: false, // multiple choice mode
		readOnly: false, // if this attribute is true, the current menu cannot be changed by client action. 
		// ----- multi-level menu defaults -----
		crossSpeed: 200, // cross-fade speed for multi-level menus
		crumbDefaultText: 'Choose an option:',
		backLink: true, // in the ipod-style menu: instead of breadcrumbs, show only a 'back' link
		backLinkText: 'Back',
		flyOut: true, // flyout style is default. otherwise, this is will be ipod-style.
		flyOutOnState: 'ui-state-default',
		nextMenuLink: 'ui-icon-triangle-1-e', // class to style the link (specifically, a span within the link) used in the multi-level menu to show the next level
		topLinkText: 'All',
		nextCrumbLink: 'ui-icon-carat-1-e',
		needHover: true // if hover is ture, the mouse moves to the container will show menu.
	}, options);

	this.showLoading = function(){
		caller.addClass(options.loadingState);
	};

	this.showMenu = function(){
		killAllMenus();
		if (!menu.menuExists) { menu.create() };
		caller.addClass('fg-menu-open').addClass(options.callerOnState);
		container.parent().show().click(function(){ menu.kill(); return false; });
		container.hide().slideDown(options.showSpeed).find('.fg-menu:eq(0)');
		menu.setPosition(container, caller, options);//refresh position
		menu.menuOpen = true;
		caller.removeClass(options.loadingState);
		$(document).click(killAllMenus);
		
		// assign key events
		$(document).keydown(function(event){
			var e;
			if (event.which !="") { e = event.which; }
			else if (event.charCode != "") { e = event.charCode; }
			else if (event.keyCode != "") { e = event.keyCode; }
			
			var menuType = ($(event.target).parents('div').is('.fg-menu-flyout')) ? 'flyout' : 'ipod' ;
			
			switch(e) {
				case 37: // left arrow 
					if (menuType == 'flyout') {
						$(event.target).trigger('mouseout');
						if ($('.'+options.flyOutOnState).size() > 0) { $('.'+options.flyOutOnState).trigger('mouseover'); };
					};
					
					if (menuType == 'ipod') {
						$(event.target).trigger('mouseout');
						if ($('.fg-menu-footer').find('a').size() > 0) { $('.fg-menu-footer').find('a').trigger('click'); };
						if ($('.fg-menu-header').find('a').size() > 0) { $('.fg-menu-current-crumb').prev().find('a').trigger('click'); };
						if ($('.fg-menu-current').prev().is('.fg-menu-indicator')) {
							$('.fg-menu-current').prev().trigger('mouseover');							
						};						
					};
					return false;
					break;
					
				case 38: // up arrow 
					if ($(event.target).is('.' + options.linkHover)) {	
						var prevLink = $(event.target).parent().prev().find('a:eq(0)');						
						if (prevLink.size() > 0) {
							$(event.target).trigger('mouseout');
							prevLink.trigger('mouseover');
						};						
					}
					else { container.find('a:eq(0)').trigger('mouseover'); }
					return false;
					break;
					
				case 39: // right arrow 
					if ($(event.target).is('.fg-menu-indicator')) {						
						if (menuType == 'flyout') {
							$(event.target).next().find('a:eq(0)').trigger('mouseover');
						}
						else if (menuType == 'ipod') {
							$(event.target).trigger('click');						
							setTimeout(function(){
								$(event.target).next().find('a:eq(0)').trigger('mouseover');
							}, options.crossSpeed);
						};				
					}; 
					return false;
					break;
					
				case 40: // down arrow 
					if ($(event.target).is('.' + options.linkHover)) {
						var nextLink = $(event.target).parent().next().find('a:eq(0)');						
						if (nextLink.size() > 0) {							
							$(event.target).trigger('mouseout');
							nextLink.trigger('mouseover');
						};				
					}
					else { container.find('a:eq(0)').trigger('mouseover'); }		
					return false;						
					break;
					
				case 27: // escape
					killAllMenus();
					break;
					
				case 13: // enter
					if ($(event.target).is('.fg-menu-indicator') && menuType == 'ipod') {							
						$(event.target).trigger('click');						
						setTimeout(function(){
							$(event.target).next().find('a:eq(0)').trigger('mouseover');
						}, options.crossSpeed);					
					}; 
					break;
			};			
		});
	};
	var initSelection = function(){
		if(options.singleChoice || options.multipleChoice){//none selection will be ignored.
			container.find('a[selected=false]').each(function(){
				var spans = $(this).find('span');
				if(spans.length > 0){
					$(spans[0]).before($('<span class="ui-icon-blank"></span>'));
				}else{
					 $(this).html('<span class="ui-icon-blank"></span><span>' + $(this).text() + '</span>');
				}
			});
			var hasSelected = container.find('a[selected=true]');
			if(hasSelected.length > 0){
				container.attr("hasSelected",true);
				hasSelected.each(function(){
					var spans = $(this).find('span');
					if(options.singleChoice){
						if(spans.length > 0){$(spans[0]).before($('<span class="ui-icon '+options.singleIcon+'"></span>'));}
						else{$(this).html('<span class="ui-icon '+options.singleIcon+'"></span><span>'+$(this).text()+'</span>');}
						return;
					}else if(options.multipleChoice){
						if(spans.length > 0){$(spans[0]).before($('<span class="ui-icon '+options.multipleIcon+'"></span>'));}
						else{$(this).html('<span class="ui-icon '+options.multipleIcon+'"></span><span>'+$(this).text()+'</span>');}
					}
				});
			}
		} 
	};
	
	this.create = function(){	
		container.css({ width: options.width }).appendTo('body').find('ul:first').not('.fg-menu-breadcrumb').addClass('fg-menu');
		container.find('ul, li a').addClass('ui-corner-all');
		// aria roles & attributes
		container.find('ul').attr('role', 'menu').eq(0).attr('aria-activedescendant','active-menuitem').attr('aria-labelledby', caller.attr('id'));
		container.find('li').attr('role', 'menuitem');
		container.find('li:has(ul)').attr('aria-haspopup', 'true').find('ul').attr('aria-expanded', 'false');
		container.find('a').attr('tabindex', '-1');
		
		// when there are multiple levels of hierarchy, create flyout or drilldown menu
		if (container.find('ul').size() > 1) {
			if (options.flyOut) { menu.flyout(container, options); }
			else { menu.drilldown(container, options); }	
		}
		else {
			container.find('a').bind("click",function(e){
				menu.chooseItem(this,e);
				return false;
			});
		};	
		initSelection();
		if (options.linkHover) {
			var allLinks = container.find('.fg-menu li[disabled!=true][seperateline!=true] a');
			allLinks.hover(
				function(){
					var menuitem = $(this);
					$('.'+options.linkHover).removeClass(options.linkHover).blur().parent().removeAttr('id');
					$(this).addClass(options.linkHover).focus().parent().attr('id','active-menuitem');
				},
				function(){
					$(this).removeClass(options.linkHover).blur().parent().removeAttr('id');
				}
			);
		};
		if (options.linkHoverSecondary) {
			container.find('.fg-menu li[disabled!=true][seperateline!=true]').hover(
				function(){
					var t = $(this); t.siblings('li').removeClass(options.linkHoverSecondary);
					if (options.flyOutOnState) { t.siblings('li').find('a').removeClass(options.flyOutOnState); }
					t.addClass(options.linkHoverSecondary);
					if(t.attr('hasChild')=='true'){
						//var destUrl = WEB_CONTEXTPATH+options.loadItemLink;
						//var o = {menuId: options.groupId, currentItem: t.attr('itemId'), _framePrefix: EBOS.getFramePrefix(options.content[0])};
						//$.ajax({ url:destUrl, data:o, success:function(e){alert("hello:"+e);}});
						// TODO: support dynamically add sub items.
					}
				},
				function(){ $(this).removeClass(options.linkHoverSecondary); }
			);
		};	
		/**
		TODO: supprt seperate lines.
		container.find('li[seperateline=true]').each(function(){
			$(this).addClass('fg-menu-seperate-line');
		});
		*/
		menu.setPosition(container, caller, options);
		caller.helper = container.parent();
		menu.menuExists = true;
	};
	
	this.chooseItem = function(item,e){
		var jItem = $(item);
		if(jItem.parent().attr('disabled')){ return; }
		menu.kill();
		if(options.singleChoice){
			var selectedObject = container.find('span[class*='+options.singleIcon+']');
			if(selectedObject.parent().text() == jItem.text())
				return;
			selectedObject.removeClass(options.singleIcon).removeClass('ui-icon').addClass('ui-icon-blank');
			jItem.find('span[class=ui-icon-blank]').removeClass('ui-icon-blank').addClass('ui-icon').addClass(options.singleIcon);
		}else if(options.multipleChoice){
			var blank = jItem.find('span[class=ui-icon-blank]');
			if(blank.length > 0){blank.removeClass('ui-icon-blank').addClass('ui-icon').addClass(options.multipleIcon);}
			else{jItem.find('span[class*='+options.multipleIcon+']').removeClass('ui-icon').removeClass(options.multipleIcon).addClass('ui-icon-blank');}
		}
	};
	
	var killAllMenus = function(){
		$().menu.killAll(options.groupId);
	};
	
	this.kill = function(){
		caller.removeClass(options.loadingState).removeClass('fg-menu-open').removeClass(options.callerOnState);	
		container.find('li').removeClass(options.linkHoverSecondary).find('a').removeClass(options.linkHover);		
		if (options.flyOutOnState) { container.find('li a').removeClass(options.flyOutOnState); };	
		if (options.callerOnState) { caller.removeClass(options.callerOnState); };			
		if (container.is('.fg-menu-ipod')) { menu.resetDrilldownMenu(); };
		if (container.is('.fg-menu-flyout')) { menu.resetFlyoutMenu(); };	
		container.parent().hide();	
		menu.menuOpen = false;
		$(document).unbind('click', killAllMenus);
		$(document).unbind('keydown');
	};
	
	var m = this;
	var load = function(){ m.showLoading();};
	var loadShow = function(){
		if (!m.menuOpen) { load(); m.showMenu(); }
		else {m.kill();}
	};
	var show = function(){
		if (!m.menuOpen) { m.showMenu(); }
		else {m.kill();}
		return false;
	};
	
	var a = caller.find('a[class=webmenu-tab-a]'); var tabA;
	if(a.length > 0){
		tabA = $(a[0]);
		if(options.needHover){tabA.bind('mouseover',loadShow);}
		tabA.bind('mousedown',load).bind('click',show);	
	}else{
		if(options.needHover){caller.bind('mouseover',loadShow);}
		caller.bind('mousedown',load).bind('click',show);	
	}
	this.destroy = function(){
		if (menu.menuExists){
			this.kill();
			container.parent().remove();
		}
		if(a.length > 0){
			if(options.needHover){tabA.unbind('mouseover', loadShow);}
			tabA.unbind('mousedown', load).unbind('click', show);
		}else{
			if(options.needHover){caller.unbind('mouseover', loadShow);}
			caller.unbind('mousedown', load).unbind('click', show);
		}
	};
};

Menu.prototype.flyout = function(container, options) {
	var menu = this;
	
	this.resetFlyoutMenu = function(){
		var allLists = container.find('ul ul');
		allLists.removeClass('ui-widget-content').hide();	
	};

	container.addClass('fg-menu-flyout').find('li:has(ul)[disabled!=true][seperateline!=true]').each(function(){
		var linkWidth = container.width();
		var showTimer, hideTimer;
		var allSubLists = $(this).find('ul');		
		allSubLists.css({ left: linkWidth, width: linkWidth }).hide();
		
		$(this).find('a:eq(0)').addClass('fg-menu-indicator').html('<span>' + $(this).find('a:eq(0)').text() + '</span><span class="ui-icon '+options.nextMenuLink+'"></span>').hover(function(){
				clearTimeout(hideTimer);
				var subList = $(this).next();
				if (!fitVertical(subList, $(this).offset().top)) { subList.css({ top: 'auto', bottom: 0 }); };
				if (!fitHorizontal(subList, $(this).offset().left + 100)) { subList.css({ left: 'auto', right: linkWidth, 'z-index': 999 }); };
				showTimer = setTimeout(function(){
					subList.addClass('ui-widget-content').show(options.showSpeed).attr('aria-expanded', 'true');	
				}, 300);	
			},
			function(){
				clearTimeout(showTimer);
				var subList = $(this).next();
				hideTimer = setTimeout(function(){
					subList.removeClass('ui-widget-content').hide(options.showSpeed).attr('aria-expanded', 'false');
				}, 400);	
			}
		);

		$(this).find('ul a').hover(
			function(){
				clearTimeout(hideTimer);
				if ($(this).parents('ul').prev().is('a.fg-menu-indicator')) {
					$(this).parents('ul').prev().addClass(options.flyOutOnState);
				}
			},
			function(){
				hideTimer = setTimeout(function(){
					allSubLists.hide(options.showSpeed);
					container.find(options.flyOutOnState).removeClass(options.flyOutOnState);
				}, 500);	
			}
		);	
	});
	
	container.find('a').bind("click",function(e){
		menu.chooseItem(this,e);
		return false;
	});
};


Menu.prototype.drilldown = function(container, options) {
	var menu = this;	
	var topList = container.find('.fg-menu');	
	var breadcrumb = $('<ul class="fg-menu-breadcrumb ui-widget-header ui-corner-all ui-helper-clearfix"></ul>');
	var crumbDefaultHeader = $('<li class="fg-menu-breadcrumb-text">'+options.crumbDefaultText+'</li>');
	var firstCrumbText = (options.backLink) ? options.backLinkText : options.topLinkText;
	var firstCrumbClass = (options.backLink) ? 'fg-menu-prev-list' : 'fg-menu-all-lists';
	var firstCrumbLinkClass = (options.backLink) ? 'ui-state-default ui-corner-all' : '';
	var firstCrumbIcon = (options.backLink) ? '<span class="ui-icon ui-icon-triangle-1-w"></span>' : '';
	var firstCrumb = $('<li class="'+firstCrumbClass+'"><a href="#" class="'+firstCrumbLinkClass+'">'+firstCrumbIcon+firstCrumbText+'</a></li>');
	
	container.addClass('fg-menu-ipod');
	
	if (options.backLink) { breadcrumb.addClass('fg-menu-footer').appendTo(container).hide(); }
	else { breadcrumb.addClass('fg-menu-header').prependTo(container); };
	breadcrumb.append(crumbDefaultHeader);
	
	var checkMenuHeight = function(el){
		if (el.height() > options.maxHeight) { el.addClass('fg-menu-scroll') };	
		el.css({ height: options.maxHeight });
	};
	
	var resetChildMenu = function(el){ el.removeClass('fg-menu-scroll').removeClass('fg-menu-current').height('auto'); };
	
	this.resetDrilldownMenu = function(){
		$('.fg-menu-current').removeClass('fg-menu-current');
		topList.animate({ left: 0 }, options.crossSpeed, function(){
			$(this).find('ul').each(function(){
				$(this).hide();
				resetChildMenu($(this));				
			});
			topList.addClass('fg-menu-current');			
		});		
		$('.fg-menu-all-lists').find('span').remove();	
		breadcrumb.empty().append(crumbDefaultHeader);		
		$('.fg-menu-footer').empty().hide();	
		checkMenuHeight(topList);		
	};
	
	topList.addClass('fg-menu-content fg-menu-current ui-widget-content ui-helper-clearfix').css({ width: container.width() })
	.find('ul').css({ width: container.width(), left: container.width() }).addClass('ui-widget-content').hide();		
	checkMenuHeight(topList);	
	
	topList.find('a').each(function(){
		// if the link opens a child menu:
		if ($(this).next().is('ul')) {
			$(this).addClass('fg-menu-indicator')
				.each(function(){ $(this).html('<span>' + $(this).text() + '</span><span class="ui-icon '+options.nextMenuLink+'"></span>'); })
				.click(function(){ // ----- show the next menu			
					var nextList = $(this).next();
		    		var parentUl = $(this).parents('ul:eq(0)');   		
		    		var parentLeft = (parentUl.is('.fg-menu-content')) ? 0 : parseFloat(topList.css('left'));    		
		    		var nextLeftVal = Math.round(parentLeft - parseFloat(container.width()));
		    		var footer = $('.fg-menu-footer');
		    		
		    		// show next menu   		
		    		resetChildMenu(parentUl);
		    		checkMenuHeight(nextList);
					topList.animate({ left: nextLeftVal }, options.crossSpeed);						
		    		nextList.show().addClass('fg-menu-current').attr('aria-expanded', 'true');    
		    		
		    		var setPrevMenu = function(backlink){
		    			var b = backlink;
		    			var c = $('.fg-menu-current');
			    		var prevList = c.parents('ul:eq(0)');
			    		c.hide().attr('aria-expanded', 'false');
		    			resetChildMenu(c);
		    			checkMenuHeight(prevList);
			    		prevList.addClass('fg-menu-current').attr('aria-expanded', 'true');
			    		if (prevList.hasClass('fg-menu-content')) { b.remove(); footer.hide(); };
		    		};		
		
					// initialize "back" link
					if (options.backLink) {
						if (footer.find('a').size() == 0) {
							footer.show();
							$('<a href="#"><span class="ui-icon ui-icon-triangle-1-w"></span> <span>Back</span></a>')
								.appendTo(footer)
								.click(function(){ // ----- show the previous menu
									var b = $(this);
						    		var prevLeftVal = parseFloat(topList.css('left')) + container.width();		    						    		
						    		topList.animate({ left: prevLeftVal },  options.crossSpeed, function(){
						    			setPrevMenu(b);
						    		});			
									return false;
								});
						}
					}
					// or initialize top breadcrumb
		    		else { 
		    			if (breadcrumb.find('li').size() == 1){				
							breadcrumb.empty().append(firstCrumb);
							firstCrumb.find('a').click(function(){
								menu.resetDrilldownMenu();
								return false;
							});
						}
						$('.fg-menu-current-crumb').removeClass('fg-menu-current-crumb');
						var crumbText = $(this).find('span:eq(0)').text();
						var newCrumb = $('<li class="fg-menu-current-crumb"><a href="javascript://" class="fg-menu-crumb">'+crumbText+'</a></li>');	
						newCrumb.appendTo(breadcrumb)
							.find('a').click(function(e){
								if ($(this).parent().is('.fg-menu-current-crumb')){
									menu.chooseItem(this,e);
								}
								else {
									var newLeftVal = - ($('.fg-menu-current').parents('ul').size() - 1) * 180;
									topList.animate({ left: newLeftVal }, options.crossSpeed, function(){
										setPrevMenu();
									});
								
									// make this the current crumb, delete all breadcrumbs after this one, and navigate to the relevant menu
									$(this).parent().addClass('fg-menu-current-crumb').find('span').remove();
									$(this).parent().nextAll().remove();									
								};
								return false;
							});
						newCrumb.prev().append(' <span class="ui-icon '+options.nextCrumbLink+'"></span>');
		    		};			
		    		return false;    		
    			});
		}
		// if the link is a leaf node (doesn't open a child menu)
		else {
			$(this).click(function(e){
				menu.chooseItem(this,e);
				return false;
			});
		};
	});
};


/* Menu.prototype.setPosition parameters (defaults noted with *):
	referrer = the link (or other element) used to show the overlaid object 
	settings = can override the defaults:
		- posX/Y: where the top left corner of the object should be positioned in relation to its referrer.
				X: left*, center, right
				Y: top, center, bottom*
		- offsetX/Y: the number of pixels to be offset from the x or y position.  Can be a positive or negative number.
		- directionH/V: where the entire menu should appear in relation to its referrer.
				Horizontal: left*, right
				Vertical: up, down*
		- detectH/V: detect the viewport horizontally / vertically
		- linkToFront: copy the menu link and place it on top of the menu (visual effect to make it look like it overlaps the object) */

Menu.prototype.setPosition = function(widget, caller, options) { 
	var el = widget;
	var referrer = caller;
	var dims = {
		refX: referrer.offset().left,
		refY: referrer.offset().top,
		refW: referrer.getTotalWidth(),
		refH: referrer.getTotalHeight()
	};	

	if(caller.helper){
		caller.helper.css({left: dims.refX, top: dims.refY});
		return;	
	}
	var helper = $('<div class="positionHelper"></div>');
	helper.css({ position: 'absolute', left: dims.refX, top: dims.refY, width: dims.refW, height: dims.refH });
	el.wrap(helper);
	
	var options = options;
	var xVal, yVal;
	// get X pos
	switch(options.positionOpts.posX) {
		case 'left': 	xVal = 0; 
			break;				
		case 'center': xVal = dims.refW / 2;
			break;				
		case 'right': xVal = dims.refW;
			break;
	};
	
	// get Y pos
	switch(options.positionOpts.posY) {
		case 'top': 	yVal = 0;
			break;				
		case 'center': yVal = dims.refH / 2;
			break;				
		case 'bottom': yVal = dims.refH;
			break;
	};
	
	// add the offsets (zero by default)
	xVal += options.positionOpts.offsetX;
	yVal += options.positionOpts.offsetY;
	
	// position the object vertically
	if (options.positionOpts.directionV == 'up') {
		el.css({ top: 'auto', bottom: yVal });
		if (options.positionOpts.detectV && !fitVertical(el)) {
			el.css({ bottom: 'auto', top: yVal });
		}
	} 
	else {
		el.css({ bottom: 'auto', top: yVal });
		if (options.positionOpts.detectV && !fitVertical(el)) {
			el.css({ top: 'auto', bottom: yVal });
		}
	};
	
	// and horizontally
	if (options.positionOpts.directionH == 'left') {
		el.css({ left: 'auto', right: xVal });
		if (options.positionOpts.detectH && !fitHorizontal(el)) {
			el.css({ right: 'auto', left: xVal });
		}
	} 
	else {
		el.css({ right: 'auto', left: xVal });
		if (options.positionOpts.detectH && !fitHorizontal(el)) {
			el.css({ left: 'auto', right: xVal });
		}
	};
	
	// if specified, clone the referring element and position it so that it appears on top of the menu
	if (options.positionOpts.linkToFront) {
		referrer.clone().addClass('linkClone').css({
			position: 'absolute', 
			top: 0, 
			right: 'auto', 
			bottom: 'auto', 
			left: 0, 
			width: referrer.width(), 
			height: referrer.height()
		}).insertAfter(el);
	};
};


/* Utilities to sort and find viewport dimensions */

function sortBigToSmall(a, b) { return b - a; };

jQuery.fn.getTotalWidth = function(){
	var brw = parseInt($(this).css('borderRightWidth'));
	var blw = parseInt($(this).css('borderLeftWidth'));
	var bw = 0; bw = ((isNaN(brw))?0:(bw+brw))+((isNaN(blw))?0:(bw+blw))
	return $(this).width() + parseInt($(this).css('paddingRight')) + parseInt($(this).css('paddingLeft')) + bw;
};

jQuery.fn.getTotalHeight = function(){
	var btw = parseInt($(this).css('borderTopWidth'));
	var bbw = parseInt($(this).css('borderBottomWidth'));
	var bw = 0; bw = ((isNaN(btw))?0:btw)+((isNaN(bbw))?0:bbw)
	return $(this).height() + parseInt($(this).css('paddingTop')) + parseInt($(this).css('paddingBottom')) + bw;
};

function getScrollTop(){
	return self.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop;
};

function getScrollLeft(){
	return self.pageXOffset || document.documentElement.scrollLeft || document.body.scrollLeft;
};

function getWindowHeight(){
	var de = document.documentElement;
	return self.innerHeight || (de && de.clientHeight) || document.body.clientHeight;
};

function getWindowWidth(){
	var de = document.documentElement;
	return self.innerWidth || (de && de.clientWidth) || document.body.clientWidth;
};

/* Utilities to test whether an element will fit in the viewport
	Parameters:
	el = element to position, required
	leftOffset / topOffset = optional parameter if the offset cannot be calculated (i.e., if the object is in the DOM but is set to display: 'none') */
	
function fitHorizontal(el, leftOffset){
	var leftVal = parseInt(leftOffset) || $(el).offset().left;
	return (leftVal + $(el).width() <= getWindowWidth() + getScrollLeft() && leftVal - getScrollLeft() >= 0);
};

function fitVertical(el, topOffset){
	var topVal = parseInt(topOffset) || $(el).offset().top;
	return (topVal + $(el).height() <= getWindowHeight() + getScrollTop() && topVal - getScrollTop() >= 0);
};

/*-------------------------------------------------------------------- 
 * javascript method: "pxToEm"
 * by:
   Scott Jehl (scott@filamentgroup.com) 
   Maggie Wachs (maggie@filamentgroup.com)
   http://www.filamentgroup.com
 *
 * Copyright (c) 2008 Filament Group
 * Dual licensed under the MIT (filamentgroup.com/examples/mit-license.txt) and GPL (filamentgroup.com/examples/gpl-license.txt) licenses.
 *
 * Description: Extends the native Number and String objects with pxToEm method. pxToEm converts a pixel value to ems depending on inherited font size.  
 * Article: http://www.filamentgroup.com/lab/retaining_scalable_interfaces_with_pixel_to_em_conversion/
 * Demo: http://www.filamentgroup.com/examples/pxToEm/	 	
 *							
 * Options:  	 								
 		scope: string or jQuery selector for font-size scoping
 		reverse: Boolean, true reverses the conversion to em-px
 * Dependencies: jQuery library						  
 * Usage Example: myPixelValue.pxToEm(); or myPixelValue.pxToEm({'scope':'#navigation', reverse: true});
 *
 * Version: 2.0, 08.01.2008 
 * Changelog:
 *		08.02.2007 initial Version 1.0
 *		08.01.2008 - fixed font-size calculation for IE
--------------------------------------------------------------------*/

Number.prototype.pxToEm = String.prototype.pxToEm = function(settings){
	//set defaults
	settings = jQuery.extend({
		scope: 'body',
		reverse: false
	}, settings);
	
	var pxVal = (this == '') ? 0 : parseFloat(this);
	var scopeVal;
	var getWindowWidth = function(){
		var de = document.documentElement;
		return self.innerWidth || (de && de.clientWidth) || document.body.clientWidth;
	};	
	
	/* When a percentage-based font-size is set on the body, IE returns that percent of the window width as the font-size. 
		For example, if the body font-size is 62.5% and the window width is 1000px, IE will return 625px as the font-size. 	
		When this happens, we calculate the correct body font-size (%) and multiply it by 16 (the standard browser font size) 
		to get an accurate em value. */
				
	if (settings.scope == 'body' && $.browser.msie && (parseFloat($('body').css('font-size')) / getWindowWidth()).toFixed(1) > 0.0) {
		var calcFontSize = function(){		
			return (parseFloat($('body').css('font-size'))/getWindowWidth()).toFixed(3) * 16;
		};
		scopeVal = calcFontSize();
	}
	else { scopeVal = parseFloat(jQuery(settings.scope).css("font-size")); };
			
	var result = (settings.reverse == true) ? (pxVal * scopeVal).toFixed(2) + 'px' : (pxVal / scopeVal).toFixed(2) + 'em';
	return result;
};