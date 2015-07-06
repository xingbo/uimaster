package org.shaolin.uimaster.page.ajax;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.shaolin.bmdp.datamodel.bediagram.MemberType;
import org.shaolin.bmdp.runtime.be.IBusinessEntity;

public class TreeItem implements IBusinessEntity, Serializable {

	private static final long serialVersionUID = 1L;

	private String id;// required
	private String text; // node text
	private String icon; // string for custom
	private State state = new State(); // opened : boolean is the node open
							// disabled :boolean is the node disabled
							// selected : boolean s the node selecte
	private LiAttribute li_attr; // : {} // attributes for the generated LI node
	private LinkAttribute a_attr; // : {} // attributes for the generated A node
	private List<TreeItem> children;
	private boolean hasChildren = false;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public LiAttribute getLi_attr() {
		return li_attr;
	}

	public void setLi_attr(LiAttribute li_attr) {
		this.li_attr = li_attr;
	}

	public LinkAttribute getA_attr() {
		return a_attr;
	}

	public void setA_attr(LinkAttribute a_attr) {
		this.a_attr = a_attr;
	}

	public List<TreeItem> getChildren() {
		if (children == null) {
			children = new ArrayList<TreeItem>();
		}
		return children;
	}

	public void setHasChildren(boolean flag) {
		this.hasChildren = flag;
	}
	
	public void setChildren(List<TreeItem> children) {
		this.children = children;
	}

	public boolean isHasChildren() {
		return hasChildren;
	}
	
	@Override
	public List<MemberType> getMemberList() {
		return null;
	}

	@Override
	public IBusinessEntity createEntity() {
		return new TreeItem();
	}

	public static class LinkAttribute implements Serializable {
		private static final long serialVersionUID = 1L;
		
		String href ;

		public LinkAttribute(String href) {
			this.href = href;
		}
		
		public String getHref() {
			return href;
		}

		public void setHref(String href) {
			this.href = href;
		}
		
	}
	
	public static class LiAttribute implements Serializable {
		private static final long serialVersionUID = 1L;
		
	}
	
	public static class State implements Serializable {

		private boolean opened = true;
		private boolean disabled = false;
		private boolean selected = false;
		
		public boolean isOpened() {
			return opened;
		}
		public void setOpened(boolean opened) {
			this.opened = opened;
		}
		public boolean isDisabled() {
			return disabled;
		}
		public void setDisabled(boolean disabled) {
			this.disabled = disabled;
		}
		public boolean isSelected() {
			return selected;
		}
		public void setSelected(boolean selected) {
			this.selected = selected;
		}
		
	}
}
