/*
 * jsPlumb
 * 
 * Title:jsPlumb 1.6.5
 * 
 * Provides a way to visually connect elements on an HTML page, using SVG or VML.  
 * 
 * This file contains the code for Connections.
 *
 * Copyright (c) 2010 - 2014 Simon Porritt (simon@jsplumbtoolkit.com)
 * 
 * http://jsplumbtoolkit.com
 * http://github.com/sporritt/jsplumb
 * 
 * Dual licensed under the MIT and GPL2 licenses.
 */
;(function() {
    
    "use strict";

    var makeConnector = function(_jsPlumb, renderMode, connectorName, connectorArgs) {
            if (!_jsPlumb.Defaults.DoNotThrowErrors && jsPlumb.Connectors[renderMode][connectorName] == null)
                    throw { msg:"jsPlumb: unknown connector type '" + connectorName + "'" };

            return new jsPlumb.Connectors[renderMode][connectorName](connectorArgs);  
        },
        _makeAnchor = function(anchorParams, elementId, _jsPlumb) {
            return (anchorParams) ? _jsPlumb.makeAnchor(anchorParams, elementId, _jsPlumb) : null;
        };
    
    jsPlumb.Connection = function(params) {
        var _newConnection = params.newConnection,
            _newEndpoint = params.newEndpoint,
            _gel = jsPlumb.getElementObject,
            _ju = jsPlumbUtil;

        this.connector = null;
        this.idPrefix = "_jsplumb_c_";
        this.defaultLabelLocation = 0.5;
        this.defaultOverlayKeys = ["Overlays", "ConnectionOverlays"];
        // if a new connection is the result of moving some existing connection, params.previousConnection
        // will have that Connection in it. listeners for the jsPlumbConnection event can look for that
        // member and take action if they need to.
        this.previousConnection = params.previousConnection;
        this.source = jsPlumb.getDOMElement(params.source);
        this.target = jsPlumb.getDOMElement(params.target);
        // sourceEndpoint and targetEndpoint override source/target, if they are present. but 
        // source is not overridden if the Endpoint has declared it is not the final target of a connection;
        // instead we use the source that the Endpoint declares will be the final source element.
        if (params.sourceEndpoint) this.source = params.sourceEndpoint.endpointWillMoveTo || params.sourceEndpoint.getElement();            
        if (params.targetEndpoint) this.target = params.targetEndpoint.getElement();        

        OverlayCapableJsPlumbUIComponent.apply(this, arguments);

        this.sourceId = this._jsPlumb.instance.getId(this.source);
        this.targetId = this._jsPlumb.instance.getId(this.target);
        this.scope = params.scope; // scope may have been passed in to the connect call. if it wasn't, we will pull it from the source endpoint, after having initialised the endpoints.            
        this.endpoints = [];
        this.endpointStyles = [];
            
        var _jsPlumb = this._jsPlumb.instance;
        this._jsPlumb.visible = true;
        this._jsPlumb.editable = params.editable === true;    
        this._jsPlumb.params = {
            cssClass:params.cssClass,
            container:params.container,
            "pointer-events":params["pointer-events"],
            editorParams:params.editorParams
        };   
        this._jsPlumb.lastPaintedAt = null;
        this.getDefaultType = function() {
            return {
                parameters:{},
                scope:null,
                detachable:this._jsPlumb.instance.Defaults.ConnectionsDetachable,
                rettach:this._jsPlumb.instance.Defaults.ReattachConnections,
                paintStyle:this._jsPlumb.instance.Defaults.PaintStyle || jsPlumb.Defaults.PaintStyle,
                connector:this._jsPlumb.instance.Defaults.Connector || jsPlumb.Defaults.Connector,
                hoverPaintStyle:this._jsPlumb.instance.Defaults.HoverPaintStyle || jsPlumb.Defaults.HoverPaintStyle,
                overlays:this._jsPlumb.instance.Defaults.ConnectorOverlays || jsPlumb.Defaults.ConnectorOverlays
            };
        };
        
// INITIALISATION CODE			
                            
        // wrapped the main function to return null if no input given. this lets us cascade defaults properly.
        
        this.makeEndpoint = function(isSource, el, elId, ep) {
            elId = elId ||  this._jsPlumb.instance.getId(el);
            return this.prepareEndpoint(_jsPlumb, _newEndpoint, this, ep, isSource ? 0 : 1, params, el, elId);
        };
        
        var eS = this.makeEndpoint(true, this.source, this.sourceId, params.sourceEndpoint),
            eT = this.makeEndpoint(false, this.target, this.targetId, params.targetEndpoint);
        
        if (eS) _ju.addToList(params.endpointsByElement, this.sourceId, eS);
        if (eT) _ju.addToList(params.endpointsByElement, this.targetId, eT);
        // if scope not set, set it to be the scope for the source endpoint.
        if (!this.scope) this.scope = this.endpoints[0].scope;
                
        // if explicitly told to (or not to) delete endpoints on detach, override endpoint's preferences
        if (params.deleteEndpointsOnDetach != null) {
            this.endpoints[0]._deleteOnDetach = params.deleteEndpointsOnDetach;
            this.endpoints[1]._deleteOnDetach = params.deleteEndpointsOnDetach;
        }
        else {
            // otherwise, unless the endpoints say otherwise, mark them for deletion.
            if (!this.endpoints[0]._doNotDeleteOnDetach) this.endpoints[0]._deleteOnDetach = true;
            if (!this.endpoints[1]._doNotDeleteOnDetach) this.endpoints[1]._deleteOnDetach = true;
        }   
                    
        // TODO these could surely be refactored into some method that tries them one at a time until something exists
        this.setConnector(this.endpoints[0].connector || 
                          this.endpoints[1].connector || 
                          params.connector || 
                          _jsPlumb.Defaults.Connector || 
                          jsPlumb.Defaults.Connector, true, true);

        if (params.path)
            this.connector.setPath(params.path);
        
        this.setPaintStyle(this.endpoints[0].connectorStyle || 
                           this.endpoints[1].connectorStyle || 
                           params.paintStyle || 
                           _jsPlumb.Defaults.PaintStyle || 
                           jsPlumb.Defaults.PaintStyle, true);
                    
        this.setHoverPaintStyle(this.endpoints[0].connectorHoverStyle || 
                                this.endpoints[1].connectorHoverStyle || 
                                params.hoverPaintStyle || 
                                _jsPlumb.Defaults.HoverPaintStyle || 
                                jsPlumb.Defaults.HoverPaintStyle, true);
        
        this._jsPlumb.paintStyleInUse = this.getPaintStyle();
        
        var _suspendedAt = _jsPlumb.getSuspendedAt();
        _jsPlumb.updateOffset( { elId : this.sourceId, timestamp:_suspendedAt });
        _jsPlumb.updateOffset( { elId : this.targetId, timestamp:_suspendedAt });

//*
        if(!_jsPlumb.isSuspendDrawing()) {                    
            // paint the endpoints
            var myInfo = _jsPlumb.getCachedData(this.sourceId),
                myOffset = myInfo.o, myWH = myInfo.s,
                otherInfo = _jsPlumb.getCachedData(this.targetId),
                otherOffset = otherInfo.o,
                otherWH = otherInfo.s,
                initialTimestamp = _suspendedAt || _jsPlumb.timestamp(),
                anchorLoc = this.endpoints[0].anchor.compute( {
                    xy : [ myOffset.left, myOffset.top ], wh : myWH, element : this.endpoints[0],
                    elementId:this.endpoints[0].elementId,
                    txy : [ otherOffset.left, otherOffset.top ], twh : otherWH, tElement : this.endpoints[1],
                    timestamp:initialTimestamp
                });

            this.endpoints[0].paint( { anchorLoc : anchorLoc, timestamp:initialTimestamp });

            anchorLoc = this.endpoints[1].anchor.compute( {
                xy : [ otherOffset.left, otherOffset.top ], wh : otherWH, element : this.endpoints[1],
                elementId:this.endpoints[1].elementId,				
                txy : [ myOffset.left, myOffset.top ], twh : myWH, tElement : this.endpoints[0],
                timestamp:initialTimestamp				
            });
            this.endpoints[1].paint({ anchorLoc : anchorLoc, timestamp:initialTimestamp });
        }
        //*/
                                
// END INITIALISATION CODE			
        
// DETACHABLE 				
        this._jsPlumb.detachable = _jsPlumb.Defaults.ConnectionsDetachable;
        if (params.detachable === false) this._jsPlumb.detachable = false;
        if(this.endpoints[0].connectionsDetachable === false) this._jsPlumb.detachable = false;
        if(this.endpoints[1].connectionsDetachable === false) this._jsPlumb.detachable = false;                
// REATTACH
        this._jsPlumb.reattach = params.reattach || this.endpoints[0].reattachConnections || this.endpoints[1].reattachConnections || _jsPlumb.Defaults.ReattachConnections;
// COST + DIRECTIONALITY
        // if cost not supplied, try to inherit from source endpoint
        this._jsPlumb.cost = params.cost || this.endpoints[0].getConnectionCost();			        
        this._jsPlumb.directed = params.directed;
        // inherit directed flag if set no source endpoint
        if (params.directed == null) this._jsPlumb.directed = this.endpoints[0].areConnectionsDirected();        
// END COST + DIRECTIONALITY
                    
// PARAMETERS						
        // merge all the parameters objects into the connection.  parameters set
        // on the connection take precedence; then source endpoint params, then
        // finally target endpoint params.
        // TODO jsPlumb.extend could be made to take more than two args, and it would
        // apply the second through nth args in order.
        var _p = jsPlumb.extend({}, this.endpoints[1].getParameters());
        jsPlumb.extend(_p, this.endpoints[0].getParameters());
        jsPlumb.extend(_p, this.getParameters());
        this.setParameters(_p);
// END PARAMETERS

// PAINTING
                  
        // the very last thing we do is apply types, if there are any.
        var _types = [params.type, this.endpoints[0].connectionType, this.endpoints[1].connectionType ].join(" ");
        if (/[^\s]/.test(_types))
            this.addType(_types, params.data, true);        

        
// END PAINTING    
    };

    jsPlumbUtil.extend(jsPlumb.Connection, OverlayCapableJsPlumbUIComponent, {
        applyType : function(t, doNotRepaint) {            
            if (t.detachable != null) this.setDetachable(t.detachable);
            if (t.reattach != null) this.setReattach(t.reattach);
            if (t.scope) this.scope = t.scope;
            //editable = t.editable;  // TODO
            this.setConnector(t.connector, doNotRepaint);
            if (t.cssClass != null && this.canvas) this._jsPlumb.instance.addClass(this.canvas, t.cssClass);
            if (t.anchor) {
                this.endpoints[0].anchor = this._jsPlumb.instance.makeAnchor(t.anchor);
                this.endpoints[1].anchor = this._jsPlumb.instance.makeAnchor(t.anchor);
            }
            else if (t.anchors) {
                this.endpoints[0].anchor = this._jsPlumb.instance.makeAnchor(t.anchors[0]);
                this.endpoints[1].anchor = this._jsPlumb.instance.makeAnchor(t.anchors[1]);
            }
        },
        getTypeDescriptor : function() { return "connection"; },
        getAttachedElements : function() {
            return this.endpoints;
        },
        addClass : function(c, informEndpoints) {        
            if (informEndpoints) {
                this.endpoints[0].addClass(c);
                this.endpoints[1].addClass(c); 
                if (this.suspendedEndpoint) this.suspendedEndpoint.addClass(c);                   
            }
            if (this.connector) {
                this.connector.addClass(c);
            }
        },
        removeClass : function(c, informEndpoints) {            
            if (informEndpoints) {
                this.endpoints[0].removeClass(c);
                this.endpoints[1].removeClass(c);                    
                if (this.suspendedEndpoint) this.suspendedEndpoint.removeClass(c);
            }
            if (this.connector) {
                this.connector.removeClass(c);
            }
        },
        isVisible : function() { return this._jsPlumb.visible; },
        setVisible : function(v) {
            this._jsPlumb.visible = v;
            if (this.connector) 
                this.connector.setVisible(v);
            this.repaint();
        },
        cleanup:function() {
            this.endpoints = null;
            this.source = null;
            this.target = null;                    
            if (this.connector != null) {
                this.connector.cleanup();            
                this.connector.destroy();
            }
            this.connector = null;
        },
        isDetachable : function() {
            return this._jsPlumb.detachable === true;
        },
        setDetachable : function(detachable) {
          this._jsPlumb.detachable = detachable === true;
        },
        isReattach : function() {
            return this._jsPlumb.reattach === true;
        },        
        setReattach : function(reattach) {
          this._jsPlumb.reattach = reattach === true;
        },
        setHover : function(state) {
            if (this.connector && this._jsPlumb && !this._jsPlumb.instance.isConnectionBeingDragged()) {
                this.connector.setHover(state);
                jsPlumbAdapter[state ? "addClass" : "removeClass"](this.source, this._jsPlumb.instance.hoverSourceClass);
                jsPlumbAdapter[state ? "addClass" : "removeClass"](this.target, this._jsPlumb.instance.hoverTargetClass);
            }
        },
        getCost : function() { return this._jsPlumb.cost; },
        setCost : function(c) { this._jsPlumb.cost = c; },
        isDirected : function() { return this._jsPlumb.directed === true; },
        getConnector : function() { return this.connector; },
        setConnector : function(connectorSpec, doNotRepaint, doNotChangeListenerComponent) {
            var _ju = jsPlumbUtil;
            if (this.connector != null) {
                this.connector.cleanup();
                this.connector.destroy();
            }

            var connectorArgs = { 
                    _jsPlumb:this._jsPlumb.instance, 
                    cssClass:this._jsPlumb.params.cssClass, 
                    container:this._jsPlumb.params.container,                 
                    "pointer-events":this._jsPlumb.params["pointer-events"]
                },
                renderMode = this._jsPlumb.instance.getRenderMode();
            
            if (_ju.isString(connectorSpec)) 
                this.connector = makeConnector(this._jsPlumb.instance, renderMode, connectorSpec, connectorArgs); // lets you use a string as shorthand.
            else if (_ju.isArray(connectorSpec)) {
                if (connectorSpec.length == 1)
                    this.connector = makeConnector(this._jsPlumb.instance, renderMode, connectorSpec[0], connectorArgs);
                else
                    this.connector = makeConnector(this._jsPlumb.instance, renderMode, connectorSpec[0], _ju.merge(connectorSpec[1], connectorArgs));
            }
            // binds mouse listeners to the current connector.
            this.bindListeners(this.connector, this, function(state) {                
                this.setHover(state, false);                
            }.bind(this));
            
            this.canvas = this.connector.canvas;
            this.bgCanvas = this.connector.bgCanvas;

            if (!doNotChangeListenerComponent) this.setListenerComponent(this.connector);

            if (this._jsPlumb.editable && jsPlumb.ConnectorEditors != null && jsPlumb.ConnectorEditors[this.connector.type] && this.connector.isEditable()) {
                new jsPlumb.ConnectorEditors[this.connector.type]({
                    connector:this.connector,
                    connection:this,
                    params:this._jsPlumb.params.editorParams || { }
                });
            }
            else {                    
                this._jsPlumb.editable = false;
            }                
                
            if (!doNotRepaint) this.repaint();
        },
        paint : function(params) {
                    
            if (!this._jsPlumb.instance.isSuspendDrawing() && this._jsPlumb.visible) {
                    
                params = params || {};
                var elId = params.elId, ui = params.ui, recalc = params.recalc, timestamp = params.timestamp,
                    // if the moving object is not the source we must transpose the two references.
                    swap = false,
                    tId = swap ? this.sourceId : this.targetId, sId = swap ? this.targetId : this.sourceId,                    
                    tIdx = swap ? 0 : 1, sIdx = swap ? 1 : 0;

                if (timestamp == null || timestamp != this._jsPlumb.lastPaintedAt) {                        
                    var sourceInfo = this._jsPlumb.instance.updateOffset( { elId : sId, offset : ui, recalc : recalc, timestamp : timestamp }).o,
                        targetInfo = this._jsPlumb.instance.updateOffset( { elId : tId, timestamp : timestamp }).o, // update the target if this is a forced repaint. otherwise, only the source has been moved.
                        sE = this.endpoints[sIdx], tE = this.endpoints[tIdx];

                    if (params.clearEdits) {
                        this._jsPlumb.overlayPositions = null;
                        sE.anchor.clearUserDefinedLocation();
                        tE.anchor.clearUserDefinedLocation();
                        this.connector.setEdited(false);
                    }
                    
                    var sAnchorP = sE.anchor.getCurrentLocation({xy:[sourceInfo.left,sourceInfo.top], wh:[sourceInfo.width, sourceInfo.height], element:sE, timestamp:timestamp}),              
                        tAnchorP = tE.anchor.getCurrentLocation({xy:[targetInfo.left,targetInfo.top], wh:[targetInfo.width, targetInfo.height], element:tE, timestamp:timestamp});                                                 
                        
                    this.connector.resetBounds();

                    this.connector.compute({
                        sourcePos:sAnchorP,
                        targetPos:tAnchorP, 
                        sourceEndpoint:this.endpoints[sIdx],
                        targetEndpoint:this.endpoints[tIdx],
                        lineWidth:this._jsPlumb.paintStyleInUse.lineWidth,                                          
                        sourceInfo:sourceInfo,
                        targetInfo:targetInfo,
                        clearEdits:params.clearEdits === true
                    });                                                                                        

                    var overlayExtents = { minX:Infinity, minY:Infinity, maxX:-Infinity, maxY:-Infinity };
                                        
                    // compute overlays. we do this first so we can get their placements, and adjust the
                    // container if needs be (if an overlay would be clipped)
                    for ( var i = 0; i < this._jsPlumb.overlays.length; i++) {
                        var o = this._jsPlumb.overlays[i];
                        if (o.isVisible()) {                            
                            this._jsPlumb.overlayPlacements[i] = o.draw(this.connector, this._jsPlumb.paintStyleInUse, this.getAbsoluteOverlayPosition(o));
                            overlayExtents.minX = Math.min(overlayExtents.minX, this._jsPlumb.overlayPlacements[i].minX);
                            overlayExtents.maxX = Math.max(overlayExtents.maxX, this._jsPlumb.overlayPlacements[i].maxX);
                            overlayExtents.minY = Math.min(overlayExtents.minY, this._jsPlumb.overlayPlacements[i].minY);
                            overlayExtents.maxY = Math.max(overlayExtents.maxY, this._jsPlumb.overlayPlacements[i].maxY);
                        }
                    }

                    var lineWidth = parseFloat(this._jsPlumb.paintStyleInUse.lineWidth || 1) / 2,
                        outlineWidth = parseFloat(this._jsPlumb.paintStyleInUse.lineWidth || 0),
                        extents = {
                            xmin : Math.min(this.connector.bounds.minX - (lineWidth + outlineWidth), overlayExtents.minX),
                            ymin : Math.min(this.connector.bounds.minY - (lineWidth + outlineWidth), overlayExtents.minY),
                            xmax : Math.max(this.connector.bounds.maxX + (lineWidth + outlineWidth), overlayExtents.maxX),
                            ymax : Math.max(this.connector.bounds.maxY + (lineWidth + outlineWidth), overlayExtents.maxY)
                        };

                    // paint the connector.
                    this.connector.paint(this._jsPlumb.paintStyleInUse, null, extents);  
                    // and then the overlays
                    for ( var j = 0; j < this._jsPlumb.overlays.length; j++) {
                        var p = this._jsPlumb.overlays[j];
                        if (p.isVisible()) {
                            p.paint(this._jsPlumb.overlayPlacements[j], extents);    
                        }
                    }
                }
                this._jsPlumb.lastPaintedAt = timestamp;
            }
        },
        /*
         * Function: repaint
         * Repaints the Connection. No parameters exposed to public API.
         */
        repaint : function(params) {
            params = params || {};            
            this.paint({ elId : this.sourceId, recalc : !(params.recalc === false), timestamp:params.timestamp, clearEdits:params.clearEdits });
        },
        prepareEndpoint : function(_jsPlumb, _newEndpoint, conn, existing, index, params, element, elementId) {
            var e;
            if (existing) {
                conn.endpoints[index] = existing;
                existing.addConnection(conn);                   
            } else {
                if (!params.endpoints) params.endpoints = [ null, null ];
                var ep = params.endpoints[index]  || params.endpoint || _jsPlumb.Defaults.Endpoints[index] || jsPlumb.Defaults.Endpoints[index] || _jsPlumb.Defaults.Endpoint || jsPlumb.Defaults.Endpoint;
                if (!params.endpointStyles) params.endpointStyles = [ null, null ];
                if (!params.endpointHoverStyles) params.endpointHoverStyles = [ null, null ];
                var es = params.endpointStyles[index] || params.endpointStyle || _jsPlumb.Defaults.EndpointStyles[index] || jsPlumb.Defaults.EndpointStyles[index] || _jsPlumb.Defaults.EndpointStyle || jsPlumb.Defaults.EndpointStyle;
                // Endpoints derive their fillStyle from the connector's strokeStyle, if no fillStyle was specified.
                if (es.fillStyle == null && params.paintStyle != null)
                    es.fillStyle = params.paintStyle.strokeStyle;
                
                // TODO: decide if the endpoint should derive the connection's outline width and color.  currently it does:
                //*
                if (es.outlineColor == null && params.paintStyle != null) 
                    es.outlineColor = params.paintStyle.outlineColor;
                if (es.outlineWidth == null && params.paintStyle != null) 
                    es.outlineWidth = params.paintStyle.outlineWidth;
                //*/
                
                var ehs = params.endpointHoverStyles[index] || params.endpointHoverStyle || _jsPlumb.Defaults.EndpointHoverStyles[index] || jsPlumb.Defaults.EndpointHoverStyles[index] || _jsPlumb.Defaults.EndpointHoverStyle || jsPlumb.Defaults.EndpointHoverStyle;
                // endpoint hover fill style is derived from connector's hover stroke style.  TODO: do we want to do this by default? for sure?
                if (params.hoverPaintStyle != null) {
                    if (ehs == null) ehs = {};
                    if (ehs.fillStyle == null) {
                        ehs.fillStyle = params.hoverPaintStyle.strokeStyle;
                    }
                }
                var a = params.anchors ? params.anchors[index] : 
                        params.anchor ? params.anchor :
                        _makeAnchor(_jsPlumb.Defaults.Anchors[index], elementId, _jsPlumb) || 
                        _makeAnchor(jsPlumb.Defaults.Anchors[index], elementId,_jsPlumb) || 
                        _makeAnchor(_jsPlumb.Defaults.Anchor, elementId,_jsPlumb) || 
                        _makeAnchor(jsPlumb.Defaults.Anchor, elementId, _jsPlumb),                  
                    u = params.uuids ? params.uuids[index] : null;
                    
                e = _newEndpoint({ 
                    paintStyle : es,  hoverPaintStyle:ehs,  endpoint : ep,  connections : [ conn ], 
                    uuid : u,  anchor : a,  source : element, scope  : params.scope,
                    reattach:params.reattach || _jsPlumb.Defaults.ReattachConnections,
                    detachable:params.detachable || _jsPlumb.Defaults.ConnectionsDetachable
                });
                conn.endpoints[index] = e;
                
                if (params.drawEndpoints === false) e.setVisible(false, true, true);
                                    
            }
            return e;
        }
        
    }); // END Connection class            
})();

/*
 * jsPlumb
 * 
 * Title:jsPlumb 1.6.5
 * 
 * Provides a way to visually connect elements on an HTML page, using SVG or VML.  
 * 
 * This file contains the jsPlumb connector editors.  It is experimental.
 *
 * Copyright (c) 2010 - 2014 Simon Porritt (simon@jsplumbtoolkit.com)
 * 
 * http://jsplumbtoolkit.com
 * http://github.com/sporritt/jsplumb
 * 
 * Dual licensed under the MIT and GPL2 licenses.
 */
;(function() {
    
    var AbstractEditor = function(params) {
        var self = this;        
    };

    var isTouchDevice = "ontouchstart" in document.documentElement,
        downEvent = isTouchDevice ? "touchstart" : "mousedown",
        upEvent = isTouchDevice ? "touchend" : "mouseup",
        moveEvent = isTouchDevice ? "touchmove" : "mousemove";
    
    // TODO: this is for a Straight segment.it would be better to have these all available somewjere, keyed
    // by segment type
    var findClosestPointOnPath = function(seg, x, y, i, bounds) {
        var m = seg[0] == seg[2] ? Infinity : 0,
            m2 = -1 / m,
            out = { s:seg, m:m, i:i, x:-1, y:-1, d:Infinity };
        
        if (m == 0) {
            // a horizontal line. if x is in the range of this line then distance is delta y. otherwise we consider it to be
            // infinity.
            if ( (seg[0] <= x && x <= seg[2]) || (seg[2] <= x && x <= seg[0])) {
                out.x = x,
                out.y = seg[1];
                out.d = Math.abs(y - seg[1]);
            }
        }
        else if (m == Infinity || m == -Infinity) {
            // a vertical line. if y is in the range of this line then distance is delta x. otherwise we consider it to be
            // infinity.
            if ((seg[1] <= y && y <= seg[3]) || (seg[3] <= y && y <= seg[1])){
                out.x = seg[0];
                out.y = y;
                out.d = Math.abs(x - seg[0]);
            }                        
        }
        else {
            // closest point lies on normal from given point to this line.  
            var b = seg[1] - (m * seg[0]),
                b2 = y - (m2 * x),
            // now we know that
            // y1 = m.x1 + b   and   y1 = m2.x1 + b2
            // so:  m.x1 + b = m2.x1 + b2
            //      x1(m - m2) = b2 - b
            //      x1 = (b2 - b) / (m - m2)
                _x1 = (b2 -b) / (m - m2),
                _y1 = (m * _x1) + b,
                d = Biltong.lineLength([ x, y ], [ _x1, _y1 ]),
                fractionInSegment = Biltong.lineLength([ _x1, _y1 ], [ seg[0], seg[1] ]);
            
            out.d = d;
            out.x = _x1;
            out.y = _y1;
            out.l = fractionInSegment / length;
        }
        return out;
    };
    
    /**
    * @namespace jsPlumb.ConnectorEditors
    * @desc These are editors for the various connector types. They are not included in the
    * main jsPlumb release. To use them you have to build a custom version of jsPlumb - see
    * the Gruntfile for information on how to do that. 
    *
    * Currently there is only an editor for the Flowchart connector.
    */
    jsPlumb.ConnectorEditors = {
        /**
        * @name jsPlumb.ConnectorEditors.FlowchartConnectorEditor
        * @class
        * @classdesc Lets you drag the segments of a flowchart connection around. If you subsequently
        * drag an element, your edits are lost.
        */
        "Flowchart":function(params) {
            AbstractEditor.apply(this, arguments);            
            
            var clickConsumer = function(conn) {                     
                    conn._jsPlumb.afterEditClick = function() {
                        console.log("after edit click");
                        conn.unbind("click", conn._jsPlumb.afterEditClick);
                        conn._jsPlumb.afterEditClick = null;
                        return false;
                    }; 
                    conn.bind("click", conn._jsPlumb.afterEditClick, true);                    
                },
                documentMouseUp = function(e) {       

                    // an attempt at consuming the click that occurs after this mouseup
                    // it's not reliable though, as we dont always get a click fired, for some
                    // reason.
                    //if (editing)
                    //    clickConsumer(params.connection);

                    jsPlumbAdapter.removeClass(document.body, params.connection._jsPlumb.instance.dragSelectClass);
                    params.connection._jsPlumb.instance.setConnectionBeingDragged(false);
                    e.stopPropagation();
                    e.preventDefault();
                    jsPlumb.off(document, upEvent, documentMouseUp);
                    jsPlumb.off(document, moveEvent, documentMouseMove);                    
                    downAt = null;
                    currentSegments = null;
                    selectedSegment = null; 
                    segmentCoords = null;
                    params.connection.setHover(false);                    
                    params.connector.setSuspendEvents(false); 
                    params.connection.endpoints[0].setSuspendEvents(false);                
                    params.connection.endpoints[1].setSuspendEvents(false);
                    params.connection.editCompleted();
                    params.connector.justEdited = editing;
                    editing = false;            
                },
                downAt = null,
                currentSegments = null,
                selectedSegment = null,
                segmentCoords = null,
                editing = false,
                anchorsMoveable = true,//params.params.anchorsMoveable,
                sgn = function(p1, p2) {
                    if (p1[0] == p2[0])
                        return p1[1] < p2[1]  ? 1 : -1;
                    else
                        return p1[0] < p2[0]  ? 1 : -1;
                },
                // collapses currentSegments by joining subsequent segments that are in the
                // same axis. we do this because it doesn't matter about stubs any longer once a user
                // is editing a connector. so it is best to reduce the number of segments to the 
                // minimum.
                _collapseSegments = function() {                       
                    var _last = null, _lastAxis = null, s = [];
                    for (var i = 0; i < currentSegments.length; i++) {
                        var seg = currentSegments[i], axis = seg[4], axisIndex = (axis == "v" ? 3 : 2);
                        if (_last != null && _lastAxis === axis) {
                            _last[axisIndex] = seg[axisIndex];                            
                        }
                        else {
                            s.push(seg);
                            _last = seg;
                            _lastAxis = seg[4];
                        }
                    }
                    currentSegments = s;                   
                },
                // attempt to shift anchor
                _shiftAnchor = function(endpoint, horizontal, value) {                    
                    var elementSize = jsPlumb.getSize(endpoint.element),
                        sizeValue = elementSize[horizontal ? 1 : 0],
                        ee = jsPlumb.getElementObject(endpoint.element),
                        off = jsPlumb.getOffset(ee), 
                        cc = jsPlumb.getElementObject(params.connector.canvas.parentNode),
                        co = jsPlumb.getOffset(cc),
                        offValue = off[horizontal ? "top" : "left"] - co[horizontal ? "top" : "left"], 
                        ap = endpoint.anchor.getCurrentLocation({element:endpoint}),
                        desiredLoc = horizontal ? params.connector.y + value : params.connector.x + value;
                    
                    if (anchorsMoveable) {                        
                        
                        if (offValue < desiredLoc && desiredLoc < offValue + sizeValue) {
                            // if still on the element, okay to move.
                            var udl = [ ap[0], ap[1] ];
                            ap[horizontal ? 1 : 0] = desiredLoc;
                            endpoint.anchor.setUserDefinedLocation(ap);
                            return value;
                        }
                        else {                        
                            // otherwise, clamp to element edge
                            var edgeVal = desiredLoc < offValue ? offValue : offValue + sizeValue;
                            return edgeVal - (horizontal ? params.connector.y: params.connector.x);                         
                        }                    
                    }
                    else {
                        // otherwise, return the current anchor point.
                        return ap[horizontal ? 1 : 0] - params.connector[horizontal ? "y" : "x"];
                    }
                },
                _updateSegmentOrientation = function(seg) {
                    if (seg[0] != seg[2]) seg[5] = (seg[0] < seg[2]) ? 1 : -1;
                    if (seg[1] != seg[3]) seg[6] = (seg[1] < seg[3]) ? 1 : -1;
                },
                documentMouseMove = function(e) {
                    if (selectedSegment != null) {
                        // suspend events on first move.
                        if (!editing) {
                            params.connection.setHover(true);
                            params.connector.setSuspendEvents(true);
                            params.connection.endpoints[0].setSuspendEvents(true);                
                            params.connection.endpoints[1].setSuspendEvents(true);
                        }
                        editing = true;
                        var m = selectedSegment.m, s = selectedSegment.s,
                            x = (e.pageX || e.page.x), y = (e.pageY || e.page.y),
                            dx = m == 0 ? 0 : x - downAt[0], dy = m == 0 ? y - downAt[1] : 0,
                            newX1 = segmentCoords[0] + dx,
                            newY1 = segmentCoords[1] + dy,
                            newX2 = segmentCoords[2] + dx,
                            newY2 = segmentCoords[3] + dy,
                            horizontal = s[4] == "h";
                        
                        // so here we know the new x,y values we would like to set for the start
                        // and end of this segment. but we may not be able to set these values: if this
                        // is the first segment, for example, then we are constrained by how far the anchor
                        // can move (before it slides off its element). same thing goes if this is the last
                        // segment. if this is not the first or last segment then there are other considerations.
                        // we know, from having run collapse segments, that there will never be two
                        // consecutive segments that are not at right angles to each other, so what we need to
                        // know is whether we can adjust the endpoint of the previous segment to the values we
                        // want, and the same question for the start values of the next segment.  the answer to
                        // that is whether or not the segment in question would be rendered too small by such
                        // a change. if that is the case (and the same goes for anchors) then we want to know
                        // what an agreeable value is, and we use that.
                        
                        if (selectedSegment.i == 0) {
                                                        
                            var anchorLoc = _shiftAnchor(params.connection.endpoints[0], horizontal, horizontal ? newY1 : newX1);                            
                            if (horizontal) 
                                newY1 = newY2 = anchorLoc; 
                            else
                                newX1 = newX2 = anchorLoc;
                        
                            currentSegments[1][0] = newX2;
                            currentSegments[1][1] = newY2;
                            _updateSegmentOrientation(currentSegments[1]);                                                                                            
                        }
                        else if (selectedSegment.i == currentSegments.length - 1) {
                            var anchorLoc = _shiftAnchor(params.connection.endpoints[1], horizontal, horizontal ? newY1 : newX1);                          
                            if (horizontal) 
                                newY1 = newY2 = anchorLoc; 
                            else
                                newX1 = newX2 = anchorLoc;
                            
                            currentSegments[currentSegments.length - 2][2] = newX1;
                            currentSegments[currentSegments.length - 2][3] = newY1;
                            _updateSegmentOrientation(currentSegments[currentSegments.length - 2]);
                        }
                        else {
                            if (!horizontal) {
                                currentSegments[selectedSegment.i - 1][2] = newX1;
                                currentSegments[selectedSegment.i + 1][0] = newX2;                                                                
                            }
                            else {
                                currentSegments[selectedSegment.i - 1][3] = newY1;                            
                                currentSegments[selectedSegment.i + 1][1] = newY2;
                            }
                            _updateSegmentOrientation(currentSegments[selectedSegment.i + 1]);
                            _updateSegmentOrientation(currentSegments[selectedSegment.i - 1]);                            
                        }
                                                                                                
                        s[0] = newX1;
                        s[1] = newY1;
                        s[2] = newX2;
                        s[3] = newY2;                                              
                        
                        params.connector.setSegments(currentSegments);
                        params.connection.repaint();                        
                        params.connection.endpoints[0].repaint();
                        params.connection.endpoints[1].repaint();
                        params.connector.setEdited(true);                        
                    }
                    else
                        editing = false;
                };
                        
            //bind to mousedown and mouseup, for editing
            params.connector.bind(downEvent, function(c, e) {
                var x = (e.pageX || e.page.x),
                    y = (e.pageY || e.page.y),
                    oe = jsPlumb.getElementObject(params.connection.getConnector().canvas),
                    o = jsPlumbAdapter.getOffset(oe, params.connection._jsPlumb.instance),                    
                    minD = Infinity;

                // TODO this is really the way we want to go: get the segment from the connector.
                // for now it's just here to remind me what to change.
                var __seg = params.connector.findSegmentForPoint(x-o.left, y-o.top);
                console.log(__seg);
                
                currentSegments = params.connector.getOriginalSegments();
                _collapseSegments();
                for (var i = 0; i < currentSegments.length; i++) {                    
                    var _s = findClosestPointOnPath(currentSegments[i], (x - o.left) , (y - o.top), i, params.connector.bounds);
                    
                    //var _s = currentSegments[i].findClosestPointOnPath(x - o.left, y - o.top);
                    
                    if (_s.d < minD) {
                        selectedSegment = _s;
                        segmentCoords = [ _s.s[0], _s.s[1], _s.s[2], _s.s[3] ]; // copy the coords at mousedown
                        minD = _s.d;
                    }
                }
                
                downAt = [ x, y ];
                
                if (selectedSegment != null) {                    
                    jsPlumb.on(document, upEvent, documentMouseUp);
                    jsPlumb.on(document, moveEvent, documentMouseMove);                                      
                    jsPlumbAdapter.addClass(document.body, params.connection._jsPlumb.instance.dragSelectClass);
                    params.connection._jsPlumb.instance.setConnectionBeingDragged(true);
                    params.connection.editStarted();                
                    return false;
                }
            }, true);
        }
    };

    jsPlumb.Connectors.AbstractConnector.prototype.shouldFireEvent = function(type, value, originalEvent) {
        var out = !this.justEdited;
        if (type == "click") {            
            this.justEdited = false;
        }
        return out;
    };

// ------------------ augment the Connection prototype with the editing stuff --------------------------

    var EDIT_STARTED = "editStarted", EDIT_COMPLETED = "editCompleted", EDIT_CANCELED = "editCanceled";

    jsPlumb.Connection.prototype.setEditable = function(e) {
        if (this.connector && this.connector.isEditable())
            this._jsPlumb.editable = e;
        
        return this._jsPlumb.editable;
    };

    jsPlumb.Connection.prototype.isEditable = function() { return this._jsPlumb.editable; };

    jsPlumb.Connection.prototype.editStarted = function() {  
        this.setSuspendEvents(true);
        this.fire(EDIT_STARTED, {
            path:this.connector.getPath()
        });
        this._jsPlumb.instance.setHoverSuspended(true);
    };

    jsPlumb.Connection.prototype.editCompleted = function() {
        this.fire(EDIT_COMPLETED, {
            path:this.connector.getPath()
        });
        this.setSuspendEvents(false);
        this._jsPlumb.instance.setHoverSuspended(false);
        this.setHover(false);
    };

    jsPlumb.Connection.prototype.editCanceled = function() {
        this.fire(EDIT_CANCELED, {
            path:this.connector.getPath()
        });
        this._jsPlumb.instance.setHoverSuspended(false);
        this.setHover(false);
    };
        
})();

/*
 * jsPlumb
 * 
 * Title:jsPlumb 1.6.5
 * 
 * Provides a way to visually connect elements on an HTML page, using SVG or VML.  
 * 
 * This file contains the code for the Bezier connector type.
 *
 * Copyright (c) 2010 - 2014 Simon Porritt (simon@jsplumbtoolkit.com)
 * 
 * http://jsplumbtoolkit.com
 * http://github.com/sporritt/jsplumb
 * 
 * Dual licensed under the MIT and GPL2 licenses.
 */
;(function() {

	var Bezier = function(params) {
		params = params || {};

		var _super =  jsPlumb.Connectors.AbstractConnector.apply(this, arguments),
			stub = params.stub || 50,
			majorAnchor = params.curviness || 150,
			minorAnchor = 10;

		this.type = "Bezier";
		this.getCurviness = function() { return majorAnchor; };

		this._findControlPoint = function(point, sourceAnchorPosition, targetAnchorPosition, sourceEndpoint, targetEndpoint) {
			// determine if the two anchors are perpendicular to each other in their orientation.  we swap the control 
			// points around if so (code could be tightened up)
			var soo = sourceEndpoint.anchor.getOrientation(sourceEndpoint), 
				too = targetEndpoint.anchor.getOrientation(targetEndpoint),
				perpendicular = soo[0] != too[0] || soo[1] == too[1],
				p = [];

			if (!perpendicular) {
				if (soo[0] === 0) // X
					p.push(sourceAnchorPosition[0] < targetAnchorPosition[0] ? point[0] + minorAnchor : point[0] - minorAnchor);
				else p.push(point[0] - (majorAnchor * soo[0]));

				if (soo[1] === 0) // Y
					p.push(sourceAnchorPosition[1] < targetAnchorPosition[1] ? point[1] + minorAnchor : point[1] - minorAnchor);
				else p.push(point[1] + (majorAnchor * too[1]));
			}
			else {
				if (too[0] === 0) // X
					p.push(targetAnchorPosition[0] < sourceAnchorPosition[0] ? point[0] + minorAnchor : point[0] - minorAnchor);
				else p.push(point[0] + (majorAnchor * too[0]));

				if (too[1] === 0) // Y
					p.push(targetAnchorPosition[1] < sourceAnchorPosition[1] ? point[1] + minorAnchor : point[1] - minorAnchor);
				else p.push(point[1] + (majorAnchor * soo[1]));
			}

			return p;
		};

		this._compute = function(paintInfo, p) {
			var sp = p.sourcePos,
				tp = p.targetPos,
				_w = Math.abs(sp[0] - tp[0]),
				_h = Math.abs(sp[1] - tp[1]),
				_sx = sp[0] < tp[0] ? _w : 0,
				_sy = sp[1] < tp[1] ? _h : 0,
				_tx = sp[0] < tp[0] ? 0 : _w,
				_ty = sp[1] < tp[1] ? 0 : _h,
				_CP = this._findControlPoint([_sx, _sy], sp, tp, p.sourceEndpoint, p.targetEndpoint),
				_CP2 = this._findControlPoint([_tx, _ty], tp, sp, p.targetEndpoint, p.sourceEndpoint);

			_super.addSegment(this, "Bezier", {
				x1:_sx, y1:_sy, x2:_tx, y2:_ty,
				cp1x:_CP[0], cp1y:_CP[1], cp2x:_CP2[0], cp2y:_CP2[1]
			});
		};
	};

	jsPlumbUtil.extend(Bezier, jsPlumb.Connectors.AbstractConnector);
	jsPlumb.registerConnectorType(Bezier, "Bezier");

})();

/*
 * jsPlumb
 * 
 * Title:jsPlumb 1.6.5
 * 
 * Provides a way to visually connect elements on an HTML page, using SVG or VML.  
 * 
 * This file contains the 'flowchart' connectors, consisting of vertical and horizontal line segments.
 *
 * Copyright (c) 2010 - 2014 Simon Porritt (simon@jsplumbtoolkit.com)
 * 
 * http://jsplumbtoolkit.com
 * http://github.com/sporritt/jsplumb
 * 
 * Dual licensed under the MIT and GPL2 licenses.
 */
;(function() {
    
    "use strict";
   
    /**
     * Function: Constructor
     * 
     * Parameters:
     * 	stub - minimum length for the stub at each end of the connector. This can be an integer, giving a value for both ends of the connections, 
     * or an array of two integers, giving separate values for each end. The default is an integer with value 30 (pixels). 
     *  gap  - gap to leave between the end of the connector and the element on which the endpoint resides. if you make this larger than stub then you will see some odd looking behaviour.  
                Like stub, this can be an array or a single value. defaults to 0 pixels for each end.     
     * cornerRadius - optional, defines the radius of corners between segments. defaults to 0 (hard edged corners).
     * alwaysRespectStubs - defaults to false. whether or not the connectors should always draw the stub, or, if the two elements
                            are in close proximity to each other (closer than the sum of the two stubs), to adjust the stubs.
     */
    var Flowchart = function(params) {
        this.type = "Flowchart";
        params = params || {};
        params.stub = params.stub == null ? 30 : params.stub;
        var self = this,
            _super =  jsPlumb.Connectors.AbstractConnector.apply(this, arguments),		
            midpoint = params.midpoint == null ? 0.5 : params.midpoint,
            points = [], segments = [],
            grid = params.grid,
            alwaysRespectStubs = params.alwaysRespectStubs,
            userSuppliedSegments = null,
            lastx = null, lasty = null, lastOrientation,	
            cornerRadius = params.cornerRadius != null ? params.cornerRadius : 0,	
            sgn = function(n) { return n < 0 ? -1 : n === 0 ? 0 : 1; },            
            /**
             * helper method to add a segment.
             */
            addSegment = function(segments, x, y, paintInfo) {
                if (lastx == x && lasty == y) return;
                var lx = lastx == null ? paintInfo.sx : lastx,
                    ly = lasty == null ? paintInfo.sy : lasty,
                    o = lx == x ? "v" : "h",
                    sgnx = sgn(x - lx),
                    sgny = sgn(y - ly);
                    
                lastx = x;
                lasty = y;				    		                
                segments.push([lx, ly, x, y, o, sgnx, sgny]);
            },
            segLength = function(s) {
                return Math.sqrt(Math.pow(s[0] - s[2], 2) + Math.pow(s[1] - s[3], 2));    
            },
            _cloneArray = function(a) { var _a = []; _a.push.apply(_a, a); return _a;},
            updateMinMax = function(a1) {
                self.bounds.minX = Math.min(self.bounds.minX, a1[2]);
                self.bounds.maxX = Math.max(self.bounds.maxX, a1[2]);
                self.bounds.minY = Math.min(self.bounds.minY, a1[3]);
                self.bounds.maxY = Math.max(self.bounds.maxY, a1[3]);    
            },
            writeSegments = function(conn, segments, paintInfo) {
                var current, next;                
                for (var i = 0; i < segments.length - 1; i++) {
                    
                    current = current || _cloneArray(segments[i]);
                    next = _cloneArray(segments[i + 1]);
                    if (cornerRadius > 0 && current[4] != next[4]) {
                        var radiusToUse = Math.min(cornerRadius, segLength(current), segLength(next));
                        // right angle. adjust current segment's end point, and next segment's start point.
                        current[2] -= current[5] * radiusToUse;
                        current[3] -= current[6] * radiusToUse;
                        next[0] += next[5] * radiusToUse;
                        next[1] += next[6] * radiusToUse;														                         			
                        var ac = (current[6] == next[5] && next[5] == 1) ||
                                 ((current[6] == next[5] && next[5] === 0) && current[5] != next[6]) ||
                                 (current[6] == next[5] && next[5] == -1),
                            sgny = next[1] > current[3] ? 1 : -1,
                            sgnx = next[0] > current[2] ? 1 : -1,
                            sgnEqual = sgny == sgnx,
                            cx = (sgnEqual && ac || (!sgnEqual && !ac)) ? next[0] : current[2],
                            cy = (sgnEqual && ac || (!sgnEqual && !ac)) ? current[3] : next[1];                                                        
                        
                        _super.addSegment(conn, "Straight", {
                            x1:current[0], y1:current[1], x2:current[2], y2:current[3]
                        });
                            
                        _super.addSegment(conn, "Arc", {
                            r:radiusToUse, 
                            x1:current[2], 
                            y1:current[3], 
                            x2:next[0], 
                            y2:next[1],
                            cx:cx,
                            cy:cy,
                            ac:ac
                        });	                                            
                    }
                    else {                 
                        // dx + dy are used to adjust for line width.
                        var dx = (current[2] == current[0]) ? 0 : (current[2] > current[0]) ? (paintInfo.lw / 2) : -(paintInfo.lw / 2),
                            dy = (current[3] == current[1]) ? 0 : (current[3] > current[1]) ? (paintInfo.lw / 2) : -(paintInfo.lw / 2);
                        _super.addSegment(conn, "Straight", {
                            x1:current[0]- dx, y1:current[1]-dy, x2:current[2] + dx, y2:current[3] + dy
                        });
                    }                    
                    current = next;
                }
                if (next != null) {
                    // last segment
                    _super.addSegment(conn, "Straight", {
                        x1:next[0], y1:next[1], x2:next[2], y2:next[3]
                    });                             
                }
            };
        
        this.setSegments = function(s) {
            userSuppliedSegments = s;
        };
        
        this.isEditable = function() { return true; };
        
        /*
            Function: getOriginalSegments
            Gets the segments before the addition of rounded corners. This is used by the flowchart
            connector editor, since it only wants to concern itself with the original segments.
        */
        this.getOriginalSegments = function() {
            return userSuppliedSegments || segments;
        };
        
        this._compute = function(paintInfo, params) {
            
            if (params.clearEdits)
                userSuppliedSegments = null;
            
            if (userSuppliedSegments != null) {
                writeSegments(this, userSuppliedSegments, paintInfo);                
                return;
            }
            
            segments = [];
            lastx = null; lasty = null;
            lastOrientation = null;          
            
            var midx = paintInfo.startStubX + ((paintInfo.endStubX - paintInfo.startStubX) * midpoint),
                midy = paintInfo.startStubY + ((paintInfo.endStubY - paintInfo.startStubY) * midpoint);                                                                                                    
    
            var findClearedLine = function(start, mult, anchorPos, dimension) {
                    return start + (mult * (( 1 - anchorPos) * dimension) + _super.maxStub);
                },
                orientations = { x:[ 0, 1 ], y:[ 1, 0 ] },
                commonStubCalculator = function(axis) {
                    return [ paintInfo.startStubX, paintInfo.startStubY, paintInfo.endStubX, paintInfo.endStubY ];                    
                },
                stubCalculators = {
                    perpendicular:commonStubCalculator,
                    orthogonal:commonStubCalculator,
                    opposite:function(axis) {  
                        var pi = paintInfo,
                            idx = axis == "x" ? 0 : 1, 
                            areInProximity = {
                                "x":function() {                                    
                                    return ( (pi.so[idx] == 1 && ( 
                                        ( (pi.startStubX > pi.endStubX) && (pi.tx > pi.startStubX) ) ||
                                        ( (pi.sx > pi.endStubX) && (pi.tx > pi.sx))))) ||

                                        ( (pi.so[idx] == -1 && ( 
                                            ( (pi.startStubX < pi.endStubX) && (pi.tx < pi.startStubX) ) ||
                                            ( (pi.sx < pi.endStubX) && (pi.tx < pi.sx)))));
                                },
                                "y":function() {                                     
                                    return ( (pi.so[idx] == 1 && ( 
                                        ( (pi.startStubY > pi.endStubY) && (pi.ty > pi.startStubY) ) ||
                                        ( (pi.sy > pi.endStubY) && (pi.ty > pi.sy))))) ||

                                        ( (pi.so[idx] == -1 && ( 
                                        ( (pi.startStubY < pi.endStubY) && (pi.ty < pi.startStubY) ) ||
                                        ( (pi.sy < pi.endStubY) && (pi.ty < pi.sy)))));
                                }
                            };

                        if (!alwaysRespectStubs && areInProximity[axis]()) {                   
                            return {
                                "x":[(paintInfo.sx + paintInfo.tx) / 2, paintInfo.startStubY, (paintInfo.sx + paintInfo.tx) / 2, paintInfo.endStubY],
                                "y":[paintInfo.startStubX, (paintInfo.sy + paintInfo.ty) / 2, paintInfo.endStubX, (paintInfo.sy + paintInfo.ty) / 2]
                            }[axis];
                        }
                        else {
                            return [ paintInfo.startStubX, paintInfo.startStubY, paintInfo.endStubX, paintInfo.endStubY ];   
                        }
                    }
                },
                lineCalculators = {
                    perpendicular : function(axis, ss, oss, es, oes) {
                        var pi = paintInfo, 
                            sis = {
                                x:[ [ [ 1,2,3,4 ], null, [ 2,1,4,3 ] ], null, [ [ 4,3,2,1 ], null, [ 3,4,1,2 ] ] ],
                                y:[ [ [ 3,2,1,4 ], null, [ 2,3,4,1 ] ], null, [ [ 4,1,2,3 ], null, [ 1,4,3,2 ] ] ]
                            },
                            stubs = { 
                                x:[ [ pi.startStubX, pi.endStubX ] , null, [ pi.endStubX, pi.startStubX ] ],
                                y:[ [ pi.startStubY, pi.endStubY ] , null, [ pi.endStubY, pi.startStubY ] ]
                            },
                            midLines = {
                                x:[ [ midx, pi.startStubY ], [ midx, pi.endStubY ] ],
                                y:[ [ pi.startStubX, midy ], [ pi.endStubX, midy ] ]
                            },
                            linesToEnd = {
                                x:[ [ pi.endStubX, pi.startStubY ] ],
                                y:[ [ pi.startStubX, pi.endStubY ] ]
                            },
                            startToEnd = {
                                x:[ [ pi.startStubX, pi.endStubY ], [ pi.endStubX, pi.endStubY ] ],        
                                y:[ [ pi.endStubX, pi.startStubY ], [ pi.endStubX, pi.endStubY ] ]
                            },
                            startToMidToEnd = {
                                x:[ [ pi.startStubX, midy ], [ pi.endStubX, midy ], [ pi.endStubX, pi.endStubY ] ],
                                y:[ [ midx, pi.startStubY ], [ midx, pi.endStubY ], [ pi.endStubX, pi.endStubY ] ]
                            },
                            otherStubs = {
                                x:[ pi.startStubY, pi.endStubY ],
                                y:[ pi.startStubX, pi.endStubX ]                                    
                            },
                            soIdx = orientations[axis][0], toIdx = orientations[axis][1],
                            _so = pi.so[soIdx] + 1,
                            _to = pi.to[toIdx] + 1,
                            otherFlipped = (pi.to[toIdx] == -1 && (otherStubs[axis][1] < otherStubs[axis][0])) || (pi.to[toIdx] == 1 && (otherStubs[axis][1] > otherStubs[axis][0])),
                            stub1 = stubs[axis][_so][0],
                            stub2 = stubs[axis][_so][1],
                            segmentIndexes = sis[axis][_so][_to];

                        if (pi.segment == segmentIndexes[3] || (pi.segment == segmentIndexes[2] && otherFlipped)) {
                            return midLines[axis];       
                        }
                        else if (pi.segment == segmentIndexes[2] && stub2 < stub1) {
                            return linesToEnd[axis];
                        }
                        else if ((pi.segment == segmentIndexes[2] && stub2 >= stub1) || (pi.segment == segmentIndexes[1] && !otherFlipped)) {
                            return startToMidToEnd[axis];
                        }
                        else if (pi.segment == segmentIndexes[0] || (pi.segment == segmentIndexes[1] && otherFlipped)) {
                            return startToEnd[axis];  
                        }                                
                    },
                    orthogonal : function(axis, startStub, otherStartStub, endStub, otherEndStub) {                    
                        var pi = paintInfo,                                            
                            extent = {
                                "x":pi.so[0] == -1 ? Math.min(startStub, endStub) : Math.max(startStub, endStub),
                                "y":pi.so[1] == -1 ? Math.min(startStub, endStub) : Math.max(startStub, endStub)
                            }[axis];
                                                
                        return {
                            "x":[ [ extent, otherStartStub ],[ extent, otherEndStub ], [ endStub, otherEndStub ] ],
                            "y":[ [ otherStartStub, extent ], [ otherEndStub, extent ], [ otherEndStub, endStub ] ]
                        }[axis];                    
                    },
                    opposite : function(axis, ss, oss, es, oes) {                                                
                        var pi = paintInfo,
                            otherAxis = {"x":"y","y":"x"}[axis], 
                            dim = {"x":"height","y":"width"}[axis],
                            comparator = pi["is" + axis.toUpperCase() + "GreaterThanStubTimes2"];

                        if (params.sourceEndpoint.elementId == params.targetEndpoint.elementId) {
                            var _val = oss + ((1 - params.sourceEndpoint.anchor[otherAxis]) * params.sourceInfo[dim]) + _super.maxStub;
                            return {
                                "x":[ [ ss, _val ], [ es, _val ] ],
                                "y":[ [ _val, ss ], [ _val, es ] ]
                            }[axis];
                            
                        }                                                        
                        else if (!comparator || (pi.so[idx] == 1 && ss > es) || (pi.so[idx] == -1 && ss < es)) {                                            
                            return {
                                "x":[[ ss, midy ], [ es, midy ]],
                                "y":[[ midx, ss ], [ midx, es ]]
                            }[axis];
                        }
                        else if ((pi.so[idx] == 1 && ss < es) || (pi.so[idx] == -1 && ss > es)) {
                            return {
                                "x":[[ midx, pi.sy ], [ midx, pi.ty ]],
                                "y":[[ pi.sx, midy ], [ pi.tx, midy ]]
                            }[axis];
                        }                        
                    }
                };

            var stubs = stubCalculators[paintInfo.anchorOrientation](paintInfo.sourceAxis),
                idx = paintInfo.sourceAxis == "x" ? 0 : 1,
                oidx = paintInfo.sourceAxis == "x" ? 1 : 0,                            
                ss = stubs[idx],
                oss = stubs[oidx],
                es = stubs[idx + 2],
                oes = stubs[oidx + 2];

            // add the start stub segment.
            addSegment(segments, stubs[0], stubs[1], paintInfo);           

            // compute the rest of the line
            var p = lineCalculators[paintInfo.anchorOrientation](paintInfo.sourceAxis, ss, oss, es, oes);            
            if (p) {
                for (var i = 0; i < p.length; i++) {                	
                    addSegment(segments, p[i][0], p[i][1], paintInfo);
                }
            }          
            
            // line to end stub
            addSegment(segments, stubs[2], stubs[3], paintInfo);
    
            // end stub to end
            addSegment(segments, paintInfo.tx, paintInfo.ty, paintInfo);               
            
            writeSegments(this, segments, paintInfo);                            
        };	

        this.getPath = function() {
            var _last = null, _lastAxis = null, s = [], segs = userSuppliedSegments || segments;
            for (var i = 0; i < segs.length; i++) {
                var seg = segs[i], axis = seg[4], axisIndex = (axis == "v" ? 3 : 2);
                if (_last != null && _lastAxis === axis) {
                    _last[axisIndex] = seg[axisIndex];                            
                }
                else {
                    if (seg[0] != seg[2] || seg[1] != seg[3]) {
                        s.push({
                            start:[ seg[0], seg[1] ],
                            end:[ seg[2], seg[3] ]
                        });                    
                        _last = seg;
                        _lastAxis = seg[4];
                    }
                }
            }
            return s;
        };	

        this.setPath = function(path) {
            userSuppliedSegments = [];
            for (var i = 0; i < path.length; i++) {
                 var lx = path[i].start[0],
                    ly = path[i].start[1],
                    x = path[i].end[0],
                    y = path[i].end[1],
                    o = lx == x ? "v" : "h",
                    sgnx = sgn(x - lx),
                    sgny = sgn(y - ly);

                userSuppliedSegments.push([lx, ly, x, y, o, sgnx, sgny]);
            }
        };
    };

    jsPlumbUtil.extend(Flowchart, jsPlumb.Connectors.AbstractConnector);
    jsPlumb.registerConnectorType(Flowchart, "Flowchart");
})();

/*
 * jsPlumb
 * 
 * Title:jsPlumb 1.6.5
 * 
 * Provides a way to visually connect elements on an HTML page, using SVG or VML.  
 * 
 * This file contains the state machine connectors.
 *
 * Copyright (c) 2010 - 2014 Simon Porritt (simon@jsplumbtoolkit.com)
 * 
 * http://jsplumbtoolkit.com
 * http://github.com/sporritt/jsplumb
 * 
 * Dual licensed under the MIT and GPL2 licenses.
 */
 ;(function() {
	 
	"use strict";

	var Line = function(x1, y1, x2, y2) {

		this.m = (y2 - y1) / (x2 - x1);
		this.b = -1 * ((this.m * x1) - y1);
	
		this.rectIntersect = function(x,y,w,h) {
			var results = [], xInt, yInt;
		
			// 	try top face
			// 	the equation of the top face is y = (0 * x) + b; y = b.
			xInt = (y - this.b) / this.m;
			// test that the X value is in the line's range.
			if (xInt >= x && xInt <= (x + w)) results.push([ xInt, (this.m * xInt) + this.b ]);
		
			// try right face
			yInt = (this.m * (x + w)) + this.b;
			if (yInt >= y && yInt <= (y + h)) results.push([ (yInt - this.b) / this.m, yInt ]);
		
			// 	bottom face
			xInt = ((y + h) - this.b) / this.m;
			// test that the X value is in the line's range.
			if (xInt >= x && xInt <= (x + w)) results.push([ xInt, (this.m * xInt) + this.b ]);
		
			// try left face
			yInt = (this.m * x) + this.b;
			if (yInt >= y && yInt <= (y + h)) results.push([ (yInt - this.b) / this.m, yInt ]);

			if (results.length == 2) {
				var midx = (results[0][0] + results[1][0]) / 2, midy = (results[0][1] + results[1][1]) / 2;
				results.push([ midx,midy ]);
				// now calculate the segment inside the rectangle where the midpoint lies.
				var xseg = midx <= x + (w / 2) ? -1 : 1,
					yseg = midy <= y + (h / 2) ? -1 : 1;
				results.push([xseg, yseg]);
				return results;
			}
		
			return null;

		};
	},
	_segment = function(x1, y1, x2, y2) {
		if (x1 <= x2 && y2 <= y1) return 1;
		else if (x1 <= x2 && y1 <= y2) return 2;
		else if (x2 <= x1 && y2 >= y1) return 3;
		return 4;
	},
		
		// the control point we will use depends on the faces to which each end of the connection is assigned, specifically whether or not the
		// two faces are parallel or perpendicular.  if they are parallel then the control point lies on the midpoint of the axis in which they
		// are parellel and varies only in the other axis; this variation is proportional to the distance that the anchor points lie from the
		// center of that face.  if the two faces are perpendicular then the control point is at some distance from both the midpoints; the amount and
		// direction are dependent on the orientation of the two elements. 'seg', passed in to this method, tells you which segment the target element
		// lies in with respect to the source: 1 is top right, 2 is bottom right, 3 is bottom left, 4 is top left.
		//
		// sourcePos and targetPos are arrays of info about where on the source and target each anchor is located.  their contents are:
		//
		// 0 - absolute x
		// 1 - absolute y
		// 2 - proportional x in element (0 is left edge, 1 is right edge)
		// 3 - proportional y in element (0 is top edge, 1 is bottom edge)
		// 	
	_findControlPoint = function(midx, midy, segment, sourceEdge, targetEdge, dx, dy, distance, proximityLimit) {
        // TODO (maybe)
        // - if anchor pos is 0.5, make the control point take into account the relative position of the elements.
        if (distance <= proximityLimit) return [midx, midy];

        if (segment === 1) {
            if (sourceEdge[3] <= 0 && targetEdge[3] >= 1) return [ midx + (sourceEdge[2] < 0.5 ? -1 * dx : dx), midy ];
            else if (sourceEdge[2] >= 1 && targetEdge[2] <= 0) return [ midx, midy + (sourceEdge[3] < 0.5 ? -1 * dy : dy) ];
            else return [ midx + (-1 * dx) , midy + (-1 * dy) ];
        }
        else if (segment === 2) {
            if (sourceEdge[3] >= 1 && targetEdge[3] <= 0) return [ midx + (sourceEdge[2] < 0.5 ? -1 * dx : dx), midy ];
            else if (sourceEdge[2] >= 1 && targetEdge[2] <= 0) return [ midx, midy + (sourceEdge[3] < 0.5 ? -1 * dy : dy) ];
            else return [ midx + (1 * dx) , midy + (-1 * dy) ];
        }
        else if (segment === 3) {
            if (sourceEdge[3] >= 1 && targetEdge[3] <= 0) return [ midx + (sourceEdge[2] < 0.5 ? -1 * dx : dx), midy ];
            else if (sourceEdge[2] <= 0 && targetEdge[2] >= 1) return [ midx, midy + (sourceEdge[3] < 0.5 ? -1 * dy : dy) ];
            else return [ midx + (-1 * dx) , midy + (-1 * dy) ];
        }
        else if (segment === 4) {
            if (sourceEdge[3] <= 0 && targetEdge[3] >= 1) return [ midx + (sourceEdge[2] < 0.5 ? -1 * dx : dx), midy ];
            else if (sourceEdge[2] <= 0 && targetEdge[2] >= 1) return [ midx, midy + (sourceEdge[3] < 0.5 ? -1 * dy : dy) ];
            else return [ midx + (1 * dx) , midy + (-1 * dy) ];
        }

	};	
	
	/**
     * Class: Connectors.StateMachine
     * Provides 'state machine' connectors.
     */
	/*
	 * Function: Constructor
	 * 
	 * Parameters:
	 * curviness -	measure of how "curvy" the connectors will be.  this is translated as the distance that the
     *                Bezier curve's control point is from the midpoint of the straight line connecting the two
     *              endpoints, and does not mean that the connector is this wide.  The Bezier curve never reaches
     *              its control points; they act as gravitational masses. defaults to 10.
	 * margin	-	distance from element to start and end connectors, in pixels.  defaults to 5.
	 * proximityLimit  -   sets the distance beneath which the elements are consider too close together to bother
	 *						with fancy curves. by default this is 80 pixels.
	 * loopbackRadius	-	the radius of a loopback connector.  optional; defaults to 25.
	 * showLoopback   -   If set to false this tells the connector that it is ok to paint connections whose source and target is the same element with a connector running through the element. The default value for this is true; the connector always makes a loopback connection loop around the element rather than passing through it.
	*/
	var StateMachine = function(params) {
		params = params || {};
		this.type = "StateMachine";

		var self = this,
			_super =  jsPlumb.Connectors.AbstractConnector.apply(this, arguments),
			curviness = params.curviness || 10,
			margin = params.margin || 5,
			proximityLimit = params.proximityLimit || 80,
			clockwise = params.orientation && params.orientation === "clockwise",
			loopbackRadius = params.loopbackRadius || 25,
			showLoopback = params.showLoopback !== false;
		
		this._compute = function(paintInfo, params) {
			var w = Math.abs(params.sourcePos[0] - params.targetPos[0]),
				h = Math.abs(params.sourcePos[1] - params.targetPos[1]),
				x = Math.min(params.sourcePos[0], params.targetPos[0]),
				y = Math.min(params.sourcePos[1], params.targetPos[1]);				
		
			if (!showLoopback || (params.sourceEndpoint.elementId !== params.targetEndpoint.elementId)) {                            
				var _sx = params.sourcePos[0] < params.targetPos[0] ? 0  : w,
					_sy = params.sourcePos[1] < params.targetPos[1] ? 0:h,
					_tx = params.sourcePos[0] < params.targetPos[0] ? w : 0,
					_ty = params.sourcePos[1] < params.targetPos[1] ? h : 0;
            
				// now adjust for the margin
				if (params.sourcePos[2] === 0) _sx -= margin;
            	if (params.sourcePos[2] === 1) _sx += margin;
            	if (params.sourcePos[3] === 0) _sy -= margin;
            	if (params.sourcePos[3] === 1) _sy += margin;
            	if (params.targetPos[2] === 0) _tx -= margin;
            	if (params.targetPos[2] === 1) _tx += margin;
            	if (params.targetPos[3] === 0) _ty -= margin;
            	if (params.targetPos[3] === 1) _ty += margin;

            	//
	            // these connectors are quadratic bezier curves, having a single control point. if both anchors 
    	        // are located at 0.5 on their respective faces, the control point is set to the midpoint and you
        	    // get a straight line.  this is also the case if the two anchors are within 'proximityLimit', since
           	 	// it seems to make good aesthetic sense to do that. outside of that, the control point is positioned 
           	 	// at 'curviness' pixels away along the normal to the straight line connecting the two anchors.
	            // 
   	        	// there may be two improvements to this.  firstly, we might actually support the notion of avoiding nodes
            	// in the UI, or at least making a good effort at doing so.  if a connection would pass underneath some node,
            	// for example, we might increase the distance the control point is away from the midpoint in a bid to
            	// steer it around that node.  this will work within limits, but i think those limits would also be the likely
            	// limits for, once again, aesthetic good sense in the layout of a chart using these connectors.
            	//
            	// the second possible change is actually two possible changes: firstly, it is possible we should gradually
            	// decrease the 'curviness' as the distance between the anchors decreases; start tailing it off to 0 at some
            	// point (which should be configurable).  secondly, we might slightly increase the 'curviness' for connectors
            	// with respect to how far their anchor is from the center of its respective face. this could either look cool,
            	// or stupid, and may indeed work only in a way that is so subtle as to have been a waste of time.
            	//

				var _midx = (_sx + _tx) / 2, _midy = (_sy + _ty) / 2, 
            	    m2 = (-1 * _midx) / _midy, theta2 = Math.atan(m2),
            	    dy =  (m2 == Infinity || m2 == -Infinity) ? 0 : Math.abs(curviness / 2 * Math.sin(theta2)),
				    dx =  (m2 == Infinity || m2 == -Infinity) ? 0 : Math.abs(curviness / 2 * Math.cos(theta2)),
				    segment = _segment(_sx, _sy, _tx, _ty),
				    distance = Math.sqrt(Math.pow(_tx - _sx, 2) + Math.pow(_ty - _sy, 2)),			
	            	// calculate the control point.  this code will be where we'll put in a rudimentary element avoidance scheme; it
	            	// will work by extending the control point to force the curve to be, um, curvier.
					_controlPoint = _findControlPoint(_midx,
                                                  _midy,
                                                  segment,
                                                  params.sourcePos,
                                                  params.targetPos,
                                                  curviness, curviness,
                                                  distance,
                                                  proximityLimit);

				_super.addSegment(this, "Bezier", {
					x1:_tx, y1:_ty, x2:_sx, y2:_sy,
					cp1x:_controlPoint[0], cp1y:_controlPoint[1],
					cp2x:_controlPoint[0], cp2y:_controlPoint[1]
				});				
            }
            else {
            	// a loopback connector.  draw an arc from one anchor to the other.            	
        		var x1 = params.sourcePos[0], x2 = params.sourcePos[0], y1 = params.sourcePos[1] - margin, y2 = params.sourcePos[1] - margin, 				
					cx = x1, cy = y1 - loopbackRadius,				
					// canvas sizing stuff, to ensure the whole painted area is visible.
					_w = 2 * loopbackRadius, 
					_h = 2 * loopbackRadius,
					_x = cx - loopbackRadius, 
					_y = cy - loopbackRadius;

				paintInfo.points[0] = _x;
				paintInfo.points[1] = _y;
				paintInfo.points[2] = _w;
				paintInfo.points[3] = _h;
				
				// ADD AN ARC SEGMENT.
				_super.addSegment(this, "Arc", {
					loopback:true,
					x1:(x1 - _x) + 4,
					y1:y1 - _y,
					startAngle:0,
					endAngle: 2 * Math.PI,
					r:loopbackRadius,
					ac:!clockwise,
					x2:(x1 - _x) - 4,
					y2:y1 - _y,
					cx:cx - _x,
					cy:cy - _y
				});
            }                           
        };                        
	};
	jsPlumb.registerConnectorType(StateMachine, "StateMachine");
})();

/*
    	// a possible rudimentary avoidance scheme, old now, perhaps not useful.
        //      if (avoidSelector) {
		//		    var testLine = new Line(sourcePos[0] + _sx,sourcePos[1] + _sy,sourcePos[0] + _tx,sourcePos[1] + _ty);
		//		    var sel = jsPlumb.getSelector(avoidSelector);
		//		    for (var i = 0; i < sel.length; i++) {
		//			    var id = jsPlumb.getId(sel[i]);
		//			    if (id != sourceEndpoint.elementId && id != targetEndpoint.elementId) {
		//				    o = jsPlumb.getOffset(id), s = jsPlumb.getSize(id);
//
//						    if (o && s) {
//							    var collision = testLine.rectIntersect(o.left,o.top,s[0],s[1]);
//							    if (collision) {
								    // set the control point to be a certain distance from the midpoint of the two points that
								    // the line crosses on the rectangle.
								    // TODO where will this 75 number come from?
					//			    _controlX = collision[2][0] + (75 * collision[3][0]);
				//	/			    _controlY = collision[2][1] + (75 * collision[3][1]);
//							    }
//						    }
					//  }
	//			    }
              //}
    */