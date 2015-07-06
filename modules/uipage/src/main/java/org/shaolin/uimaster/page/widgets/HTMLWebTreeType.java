package org.shaolin.uimaster.page.widgets;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.datamodel.page.UITableActionType;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.ajax.Layout;
import org.shaolin.uimaster.page.ajax.Tree;
import org.shaolin.uimaster.page.ajax.TreeItem;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.ajax.json.JSONArray;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;

public class HTMLWebTreeType extends HTMLWidgetType {
	
	private static final long serialVersionUID = 1587046878874940935L;

	private static final Logger logger = Logger.getLogger(HTMLWebTreeType.class);

	public HTMLWebTreeType() {
	}

	public HTMLWebTreeType(HTMLSnapshotContext context) {
		super(context);
	}

	public HTMLWebTreeType(HTMLSnapshotContext context, String id) {
		super(context, id);
	}

	@Override
	public void generateBeginHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth) {
		
	}
	
	
	@Override
	public void generateEndHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth) {
		try {
			String selectedNodeEvent = (String)this.getAttribute("selectedNode");
			String expendTreeEvent = (String)this.getAttribute("expendTree");
			String deleteNodeEvent = (String)this.getAttribute("deleteNode");
			String addNodeEvent = (String)this.getAttribute("addNode");
			String refreshNodeEvent = (String)this.getAttribute("refreshNode");
			List<UITableActionType> actions = (List<UITableActionType>)this.getAttribute("actions");
			String nodeIcon = (String)this.getAttribute("nodeIcon");
			String itemIcon = (String)this.getAttribute("itemIcon");
			Boolean isopened= (Boolean)this.getAttribute("opened");
			
			HTMLUtil.generateTab(context, depth);
			context.generateHTML("<div id=\"");
			context.generateHTML(getName());
			context.generateHTML("\" class=\"uimaster_tree\">");
			
			List<TreeItem> result = (List<TreeItem>)this.removeAttribute("initExpr");
			JSONArray jsonArray = new JSONArray(result);
			
			context.generateHTML("<div style='display:none;' clickevent=\"defaultname.");
			context.generateHTML(this.getPrefix() + selectedNodeEvent);
			context.generateHTML("(tree, e)\" expendevent=\"defaultname.");
			context.generateHTML(this.getPrefix() + expendTreeEvent);
			context.generateHTML("(tree, e)\" expendevent0=\"");
			context.generateHTML(expendTreeEvent);
			context.generateHTML("\" clickevent0=\"");
			context.generateHTML(selectedNodeEvent);
			context.generateHTML("\"");
			if (addNodeEvent != null) {
				context.generateHTML(" addnodeevent0=\"");
				context.generateHTML(addNodeEvent);
				context.generateHTML("\" addnodeevent=\"defaultname.");
				context.generateHTML(this.getPrefix() + addNodeEvent);
				context.generateHTML("(tree, e)\"");
			}
			if (deleteNodeEvent != null) {
				context.generateHTML(" deletenodeevent0=\"");
				context.generateHTML(deleteNodeEvent);
				context.generateHTML("\" deletenodeevent=\"defaultname.");
				context.generateHTML(this.getPrefix() + deleteNodeEvent);
				context.generateHTML("(tree, e)\"");
			}
			if (refreshNodeEvent != null) {
				context.generateHTML(" refreshnodeevent0=\"");
				context.generateHTML(refreshNodeEvent);
				//TODO: add more actions
				context.generateHTML("\" refreshnodeevent=\"defaultname.");
				context.generateHTML(this.getPrefix() + refreshNodeEvent);
				context.generateHTML("(tree, e)\"");
			}
			context.generateHTML("\">");
			context.generateHTML(jsonArray.toString());
			context.generateHTML("</div></div>");
		} catch (Exception e) {
			logger.error("error in entity: " + getUIEntityName(), e);
		}
	}
	
	public Widget createAjaxWidget(VariableEvaluator ee)
    {
        Tree tree = new Tree(getName(), Layout.NULL);

        tree.setReadOnly(getReadOnly());
        tree.setUIEntityName(getUIEntityName());

        tree.setListened(true);
        tree.setFrameInfo(getFrameInfo());

        List result = (List)this.getAttribute("initExpr");
        Object lastObject = result.get(result.size()-1);
        if (lastObject instanceof Map) {
        	tree.setDataModel((Map)lastObject);
        	result.remove(result.size()-1);
        }
        
        return tree;
    }

	
	public static Map sortByValue(Map map) {
		List list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				Object k1 = ((Map.Entry) (o1)).getKey();
				Object v1 = ((Map.Entry) (o1)).getValue();
				if (v1 instanceof Comparable) {
					// compare the items
					return ((Comparable) v1).compareTo(((Map.Entry) (o2)).getValue());
				} else {
					// compare the nodes for map object.
					return ((Comparable) k1).compareTo(((Map.Entry) (o2)).getKey());
				}
			}
		});
		Map result = new LinkedHashMap();

		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}
