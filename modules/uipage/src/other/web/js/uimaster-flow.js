UIMaster.ui.flow = function(conf){
    conf = conf || {};
    UIMaster.apply(this, conf);
    UIMaster.apply(this.ui, this,true);
    return this.ui;
};
UIMaster.ui.flow = UIMaster.extend(UIMaster.ui, {
	containerId:null,
	_selectedNodeId:null,
	_selectedNode:null,
	connectionTypes: {
		stateMachine:{
			connector:"StateMachine",
			paintStyle:{lineWidth:3,strokeStyle:"#056"},
			hoverPaintStyle:{strokeStyle:"#dbe300"},
			overlays:[ ["PlainArrow", {location:1, width:10, length:8} ]],
			endpoint:"Blank",
			anchor:"Continuous"
		},
		flowchart: {
			connector:["Flowchart", { stub:[20, 40], gap:2, cornerRadius:5, alwaysRespectStubs:true }],	
			paintStyle:{strokeStyle:"#7AB02C",fillStyle:"transparent",radius:7,lineWidth:3},
			connectorStyle:{lineWidth:4,strokeStyle:"#61B7CF",joinstyle:"round",outlineColor:"white",outlineWidth:2},
			hoverPaintStyle:{fillStyle:"#216477",strokeStyle:"#216477"},
			connectorHoverStyle:{lineWidth:4,strokeStyle:"#216477",outlineWidth:2,outlineColor:"white"},
			overlays:[["Label",{location:[0.5, 1.5],label:"",cssClass:"endpointSourceLabel"}]],
			endpoint:"Dot"
		}
	},
	endpoint: {
		// the definition of source endpoints (the small blue ones)
		sourceEndpoint: {
			endpoint:"Dot",
			paintStyle:{strokeStyle:"#7AB02C",fillStyle:"transparent",radius:5,lineWidth:3},				
			isSource:true,
			connector:["Flowchart", { stub:[20, 40], gap:2, cornerRadius:2, alwaysRespectStubs:true }],								                
			connectorStyle:{lineWidth:3,strokeStyle:"#61B7CF",joinstyle:"round",outlineColor:"white",outlineWidth:2},
			hoverPaintStyle:{fillStyle:"#216477",strokeStyle:"#216477"},
			connectorHoverStyle:{lineWidth:3,strokeStyle:"#216477",outlineWidth:2,outlineColor:"white"},
	        dragOptions:{},
	        overlays:[["Label",{location:[0.5, 1.5],label:"",cssClass:"endpointSourceLabel"}]]
		},		
		// the definition of target endpoints (will appear when the user drags a connection) 
		targetEndpoint: {
			endpoint:"Dot",
			paintStyle:{fillStyle:"#7AB02C",radius:5},
			hoverPaintStyle:{fillStyle:"#216477",strokeStyle:"#216477"},
			maxConnections:-1,
			dropOptions:{hoverClass:"hover", activeClass:"active"},
			isTarget:true,
	        overlays:[["Label",{location:[0.5, -0.5],label:"",cssClass:"endpointTargetLabel" }]]
		}
	},
	/**
	 * add endpoint to the specific element with the anchors positions at top, bottom, left and right boundaries.
	 */
	addEndpoints: function(toId, endpoint, sourceAnchors, targetAnchors) {
		for (var i = 0; i < sourceAnchors.length; i++) {
			var sourceUUID = toId + sourceAnchors[i];
			this.instance.addEndpoint(toId, endpoint.sourceEndpoint, { anchor:sourceAnchors[i], uuid:sourceUUID });						
		}
		for (var j = 0; j < targetAnchors.length; j++) {
			var targetUUID = toId + targetAnchors[j];
			this.instance.addEndpoint(toId, endpoint.targetEndpoint, { anchor:targetAnchors[j], uuid:targetUUID });						
		}
	},
	init: function(){},
	postInit: function() {
		this.containerId = $(this).attr("id");
		this.instance = jsPlumb.getInstance({
			// default drag options
			DragOptions : { cursor: 'pointer', zIndex:2000 },
			// the overlays to decorate each connection with.  note that the label overlay uses a function to generate the label text; in this
			// case it returns the 'labelText' member that we set on each connection in the 'init' method below.
			ConnectionOverlays : [
				[ "Arrow", { location:1 } ],
				[ "Label", { 
					location:0.1,
					id:"label",
					cssClass:"aLabel"
				}]
			],
			Container:this.containerId
		});
		
		var blocks = $(this).children(".window");
		//this.instance.makeSource(blocks, this.endpoint.sourceEndpoint);
		//this.instance.makeTarget(blocks, this.endpoint.targetEndpoint);
		
		var t = this;
		blocks.each(function(){
			t._addNode(this);
		});
		
		this.instance.bind("connection", function(info, originalEvent) { 
			//info.connection.getOverlay("label").setLabel(info.connection.sourceId.substring(15) + "-" + info.connection.targetId.substring(15));
			info.connection.bind("editCompleted", function(o) {
				//if (typeof console != "undefined")
					//console.log("connection edited. path is now ", o.path);
			});
		});
		this.actionBar = $('#'+this.id+'ActionBar');
		this.originalBackGround = this.actionBar.css("background");
		this.actionBar.children().each(function() {
			$(this).buttonset();
			$(this).children().each(function(){
				if ($(this).attr("icon") != undefined && $(this).attr("icon") != null) {
					$(this).button({text:false,icons:{primary:$(this).attr("icon")}});
				}
			});
		});
		$($(this).children("div[id='connectionInfo']")[0]).children().each(function(){
			t.instance.connect({
				uuids:[$(this).attr("srcAnchor"), $(this).attr("tarAnchor")], 
				editable:true, 
				overlays: [
					["Label", {													   					
						cssClass:"l1 component label",
						label : "", 
						location:0.7, id:"label",
						events:{
							"click":function(label, evt) {
								//alert("clicked on label for connection " + label.component.id);
							}
						}
					}]
				]
			});
		});
		this.instance.bind("click", function(conn, originalEvent) {
			if (confirm("Delete connection from " + conn.sourceId + " to " + conn.targetId + "?")) {
				jsPlumb.detach(conn); 
			}
		});
		this.instance.bind("connectionDrag", function(connection) {
			//console.log("connection " + connection.id + " is being dragged. suspendedElement is ", connection.suspendedElement, 
			//" of type ", connection.suspendedElementType);
		});
		this.instance.bind("connectionDragStop", function(connection) {
			t.nodeChangeHint();
			//connection.sourceId, connection.targetId;
		});
		this.instance.bind("connectionMoved", function(params) {
			//console.log("connection " + params.connection.id + " was moved");
		});
		
		jsPlumb.fire("jsPlumbDemoLoaded", this.instance);
	},
	sync:function() {
		var obj = UIMaster.getObject(this);
        UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"selectedNode",_value:this._selectedNodeId,_framePrefix:UIMaster.getFramePrefix(obj)});
        var nodes = [];
        $(this).children(".window").each(function(){
        	var offset = $(this).offset();
        	nodes.push({id: $(this).attr("id"), top:offset.top, left:offset.left});
        });
        UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"nodelocations",_value:JSON.stringify(nodes),_framePrefix:UIMaster.getFramePrefix(obj)});
        var connections = [], conns = this.instance.getAllConnections();
        $(conns).each(function() {
        	var sourceAnchor = this.endpoints[0].anchor.type;
        	var targetAnchor = this.endpoints[1].anchor.type;
			connections.push({connectId:this.id, source:this.sourceId, srcAnchor:sourceAnchor, target:this.targetId, tarAnchor:targetAnchor});
		});
		UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"connections",_value:JSON.stringify(connections),_framePrefix:UIMaster.getFramePrefix(obj)});
	},
	_addNode: function(node) {
		var t = this;
		this.addEndpoints($(node).attr("id"), this.endpoint, ["TopCenter", "LeftMiddle", "RightMiddle"], ["BottomCenter"]);
		this.instance.draggable($(node), { grid: [20, 20], stop:function(event, ui){t.nodeChangeHint();} });
		$(node).click(function() {
			if (t._selectedNode != null) {t._selectedNode.removeClass("selected");}
			t._selectedNode = $(this);
			t._selectedNodeId = $(this).attr("id");
			$(this).addClass("selected");
		})
	},
	refreshModel: function(model) {
		var blocks = $(this).children(".window");
		for(var i=0;i<blocks.length;i++) {
		    var node = $(blocks[i]);
			jsPlumb.detachAllConnections(node);
			jsPlumb.removeAllEndpoints(node.attr("id"));
			node.remove();
		}
		for(var i=0;i<model.length;i++) {
			if (model[i].sourceAnchor) {//connection
				this.addConnection(model[i]);
			} else {//node
				this.addNode(model[i]);
			}
		}
	},
	addConnection: function(connInfo) {
		this.instance.connect({
			source:connInfo.sourceAnchor,
			target:connInfo.targetAnchor, 
			editable:true, 
			overlays: [
				["Label", {													   					
					cssClass:"l1 component label",
					label : "", 
					location:0.7, id:"label",
					events:{
						"click":function(label, evt) {
							alert("clicked on label for connection " + label.component.id);
						}
					}
				}]
			]
		});
	},
	removeConnection: function(connInfo) {
		
	},
    addNode: function(nodeInfo) { 
		$(this).append("<div id='"+nodeInfo.id+"' style='top:"+nodeInfo.y+"px;left:"+nodeInfo.x+"px' title='"+nodeInfo.name+"' class='window'>"+nodeInfo.name+"</div>");
		var blocks = $(this).children(".window");
		this._addNode(blocks[blocks.length-1]);
	},
	removeNode: function(nodeId) {
		this._selectedNodeId == null;
		var node;
		$(this).children(".window").each(function(){
			if ($(this).attr("id") == nodeId) {node = $(this); return false;}
		});
		jsPlumb.removeAllEndpoints(node.attr("id"));
		node.remove();
		this.nodeChangeHint();
	}, 
	checkSelection:function() {
		if (this._selectedNodeId == null) {alert("Please select a node!");return false;}
		return true;
	},
	nodeChangeHint:function() {
		this.actionBar.css({"background":"none"});
		this.actionBar.animate({backgroundColor: "#EEEE00"}, "slow");
	},
	saveSuccessHint:function() {
		//this.actionBar.animate
		//this.originalBackGround
		//css({"background":"lightgreen"})
		var t = this;
		this.actionBar.css({"background":"none"});
		this.actionBar.animate({backgroundColor: "#66CD00"}, "slow", function() {
			t.actionBar.css({"background":t.originalBackGround});
		});
	},
	saveFailureHint:function() {
		var t = this;
		this.actionBar.css({"background":"none"});
		this.actionBar.animate({backgroundColor: "#FF3030"}, "slow");
	}
});
