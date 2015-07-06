package org.shaolin.bmdp.designtime.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.shaolin.bmdp.datamodel.bediagram.BEListType;
import org.shaolin.bmdp.datamodel.bediagram.BEObjRefType;
import org.shaolin.bmdp.datamodel.bediagram.BEType;
import org.shaolin.bmdp.datamodel.bediagram.BinaryType;
import org.shaolin.bmdp.datamodel.bediagram.BooleanType;
import org.shaolin.bmdp.datamodel.bediagram.CEObjRefType;
import org.shaolin.bmdp.datamodel.bediagram.DateTimeType;
import org.shaolin.bmdp.datamodel.bediagram.DoubleType;
import org.shaolin.bmdp.datamodel.bediagram.FileType;
import org.shaolin.bmdp.datamodel.bediagram.IntType;
import org.shaolin.bmdp.datamodel.bediagram.LongType;
import org.shaolin.bmdp.datamodel.bediagram.StringType;
import org.shaolin.bmdp.datamodel.bediagram.TimeType;
import org.shaolin.bmdp.datamodel.common.ParamScopeType;
import org.shaolin.bmdp.datamodel.common.VariableCategoryType;
import org.shaolin.bmdp.datamodel.page.ComponentConstraintType;
import org.shaolin.bmdp.datamodel.page.TableLayoutConstraintType;
import org.shaolin.bmdp.datamodel.page.TableLayoutType;
import org.shaolin.bmdp.datamodel.page.UICheckBoxGroupType;
import org.shaolin.bmdp.datamodel.page.UICheckBoxType;
import org.shaolin.bmdp.datamodel.page.UIComboBoxType;
import org.shaolin.bmdp.datamodel.page.UIComponentType;
import org.shaolin.bmdp.datamodel.page.UIDateType;
import org.shaolin.bmdp.datamodel.page.UIFileType;
import org.shaolin.bmdp.datamodel.page.UIHiddenType;
import org.shaolin.bmdp.datamodel.page.UILabelType;
import org.shaolin.bmdp.datamodel.page.UIListType;
import org.shaolin.bmdp.datamodel.page.UIPanelType;
import org.shaolin.bmdp.datamodel.page.UIRadioButtonGroupType;
import org.shaolin.bmdp.datamodel.page.UIRadioButtonType;
import org.shaolin.bmdp.datamodel.page.UIReferenceEntityType;
import org.shaolin.bmdp.datamodel.page.UITableType;
import org.shaolin.bmdp.datamodel.page.UITextAreaType;
import org.shaolin.bmdp.datamodel.page.UITextFieldType;

public class UIPageUtil {

	public final static Map<String, Class<?>> BEFIELD_TYPES 
		= new LinkedHashMap<String, Class<?>>();
	
	public final static Map<String, Class<?>> ACTION_TYPES 
		= new LinkedHashMap<String, Class<?>>();
	
	public static final List<String> BASE_RULES 
		= new LinkedList<String>();
	
	private final static String[] CATEGORIES = new String[4];
	
	private final static String[] SCOPES = new String[4];
	
	static {
		BEFIELD_TYPES.put("UILabelType", UILabelType.class);
		BEFIELD_TYPES.put("UITextFieldType", UITextFieldType.class);
		BEFIELD_TYPES.put("UITextAreaType", UITextAreaType.class);
		BEFIELD_TYPES.put("UIHiddenType", UIHiddenType.class);
		BEFIELD_TYPES.put("UICheckBoxType", UICheckBoxType.class);
		BEFIELD_TYPES.put("UIRadioButtonType", UIRadioButtonType.class);
		BEFIELD_TYPES.put("UIComboBoxType", UIComboBoxType.class);
		BEFIELD_TYPES.put("UIListType", UIListType.class);
		BEFIELD_TYPES.put("UIRadioButtonGroupType", UIRadioButtonGroupType.class);
		BEFIELD_TYPES.put("UICheckBoxGroupType", UICheckBoxGroupType.class);
		BEFIELD_TYPES.put("UIFileType", UIFileType.class);
		BEFIELD_TYPES.put("UIDateType", UIDateType.class);
		BEFIELD_TYPES.put("UIEntityType", UIReferenceEntityType.class);
		BEFIELD_TYPES.put("UITableType", UITableType.class);
		
		CATEGORIES[0] = VariableCategoryType.BUSINESS_ENTITY.value();
		CATEGORIES[1] = VariableCategoryType.CONSTANT_ENTITY.value();
		CATEGORIES[2] = VariableCategoryType.UI_ENTITY.value();
		CATEGORIES[3] = VariableCategoryType.JAVA_CLASS.value();
		
		SCOPES[0] = ParamScopeType.IN.value();
		SCOPES[1] = ParamScopeType.OUT.value();
		SCOPES[2] = ParamScopeType.IN_OUT.value();
		SCOPES[3] = ParamScopeType.INTERNAL.value();
		
    	BASE_RULES.add("org.shaolin.uimaster.page.od.rules.UIFile");
    	BASE_RULES.add("org.shaolin.uimaster.page.od.rules.UIMultipleChoiceAndCE");
    	BASE_RULES.add("org.shaolin.uimaster.page.od.rules.UIMultipleChoice");
    	BASE_RULES.add("org.shaolin.uimaster.page.od.rules.UISelect");
    	BASE_RULES.add("org.shaolin.uimaster.page.od.rules.UISingleChoiceAndCE");
    	BASE_RULES.add("org.shaolin.uimaster.page.od.rules.UISingleChoice");
    	BASE_RULES.add("org.shaolin.uimaster.page.od.rules.UIText");
    	BASE_RULES.add("org.shaolin.uimaster.page.od.rules.UITextWithCE");
    	BASE_RULES.add("org.shaolin.uimaster.page.od.rules.UITextWithCurrency");
    	BASE_RULES.add("org.shaolin.uimaster.page.od.rules.UITextWithDate");
    	BASE_RULES.add("org.shaolin.uimaster.page.od.rules.UITextWithFloatNumber");
    	BASE_RULES.add("org.shaolin.uimaster.page.od.rules.UITextWithNumber");
    	BASE_RULES.add("org.shaolin.uimaster.page.od.rules.UITable");
	}
	
	public static void createLayout(UIPanelType parent, int rows, int columns) {
		TableLayoutType layout = new TableLayoutType();
		for (int i=0; i<rows; i++) {
			layout.getRowHeightWeights().add(new Double(0.0));// default length
		}
		for (int j=0; j<columns; j++) {
			layout.getColumnWidthWeights().add(new Double(1.0)); // 100% length
		}
		parent.setLayout(layout);
	}
	
	public static void createLayoutWithPanelEachRow(UIPanelType parent, int rows, int columns) {
		TableLayoutType layout = new TableLayoutType();
		for (int i=0; i<rows; i++) {
			layout.getRowHeightWeights().add(new Double(-1.0));
			
			String uiid = parent.getUIID() + "_uiPanel" + i;
			UIPanelType newPanel = new UIPanelType();
			newPanel.setUIID(uiid);
			newPanel.setLayout(new TableLayoutType());
			parent.getComponents().add(newPanel);
			
			ComponentConstraintType cc = new ComponentConstraintType();
			TableLayoutConstraintType c = new TableLayoutConstraintType();
			c.setX(0);
			c.setY(i);
			c.setAlign("FULL");
			cc.setConstraint(c);
			cc.setComponentId(uiid);
			parent.getLayoutConstraints().add(cc);
			
			((TableLayoutType)newPanel.getLayout())
				.getRowHeightWeights().add(new Double(-1.0));
			for (int j=0; j<columns; j++) {
				((TableLayoutType)newPanel.getLayout())
					.getColumnWidthWeights().add(new Double(-1.0));
			}
		}
		parent.setLayout(layout);
	}

	public static List<String> fetchPanelElementsUIID(UIPanelType parent) {
		List<UIComponentType> list = fetchPanelElements(parent);
		
		List<String> uiids = new ArrayList<String>(list.size());
		for (UIComponentType comp: list) {
			uiids.add(comp.getUIID());
		}
		
		return uiids;
	}
	
	public static List<UIComponentType> fetchPanelElements(UIPanelType parent) {
		List<UIComponentType> list = new ArrayList<UIComponentType>();
		fetchPanelElements0(parent, list);
		return list;
	}
	
	private static void fetchPanelElements0(UIPanelType parent, List<UIComponentType> list) {
		List<UIComponentType> subcomps = parent.getComponents();
		for (UIComponentType comp: subcomps) {
			if (comp instanceof UIPanelType) {
				fetchPanelElements0((UIPanelType)comp, list);
			} else {
				list.add(comp);
			}
		}
	}
	
	public static String getDefaultJspName(String entityName) {
		return entityName;
	}
	
	public static List<String> getAllRuleNames() {
		List<String> ruels = new ArrayList<String>();
		
		return ruels;
	}
	
	public static List<String> getRuleParameters() {
		return Collections.emptyList();
	}
	
	public static String[] getODBaseRules() {
		String[] rules = new String[BASE_RULES.size()];
		BASE_RULES.toArray(rules);
		return rules;
	}
	
	public static String getTypeByIndex(int i) {
		if (i == -1) {
			return "";
		}
		Set<String> typeNames = getBEFieldMappingTypes();
		String[] types = new String[typeNames.size()];
		typeNames.toArray(types);
		return types[i];
	}
	
	public static Set<String> getBEFieldMappingTypes() {
		return BEFIELD_TYPES.keySet();
	}
	
	public static Class<?> getMappingClass(String type) {
		return BEFIELD_TYPES.get(type);
	}
	
	public static String getDefaultODRuleType(BEType type) {
		if (type instanceof BinaryType) {
			return "org.shaolin.uimaster.page.od.rules.UIText";
		}
		if (type instanceof IntType) {
			return "org.shaolin.uimaster.page.od.rules.UITextWithNumber";
		}
		if (type instanceof LongType) {
			return "org.shaolin.uimaster.page.od.rules.UITextWithNumber";
		}
		if (type instanceof StringType) {
			return "org.shaolin.uimaster.page.od.rules.UIText";
		}
		if (type instanceof BooleanType) {
			return "org.shaolin.uimaster.page.od.rules.UISelect";
		}
		if (type instanceof DoubleType) {
			return "org.shaolin.uimaster.page.od.rules.UITextWithFloatNumber";
		}
		if (type instanceof FileType) {
			return "org.shaolin.uimaster.page.od.rules.UIFile";
		}
		if (type instanceof TimeType) {
			return "org.shaolin.uimaster.page.od.rules.UITextWithDate";
		}
		if (type instanceof DateTimeType) {
			return "org.shaolin.uimaster.page.od.rules.UITextWithDate";
		}
		if (type instanceof BEObjRefType) {
			String beName = ((BEObjRefType)type).getTargetEntity().getEntityName();
			return beName.replace("be", "form");
		}
		if (type instanceof CEObjRefType) {
			return "org.shaolin.uimaster.page.od.rules.UISingleChoiceAndCE";
		}
		if (type instanceof BEListType) {
			/**
			 * table is no needed a rule defintion.
			if (((BEListType)type).getElementType() instanceof BEObjRefType) {
				return "org.shaolin.uimaster.page.od.rules.UITable";
			}
			*/
			if (((BEListType)type).getElementType() instanceof CEObjRefType) {
				return "org.shaolin.uimaster.page.od.rules.UIMultipleChoiceAndCE";
			} 
			return "org.shaolin.uimaster.page.od.rules.UIMultipleChoice";
		}
		
		return "org.shaolin.uimaster.page.od.rules.UIText";
	}
	
	public static String getDefaultUIType(BEType type) {
		if (type instanceof BinaryType) {
			return "UILabelType";
		}
		if (type instanceof IntType) {
			return "UITextFieldType";
		}
		if (type instanceof LongType) {
			return "UITextFieldType";
		}
		if (type instanceof StringType) {
			return "UITextFieldType";
		}
		if (type instanceof BooleanType) {
			return "UICheckBoxType";
		}
		if (type instanceof DoubleType) {
			return "UITextFieldType";
		}
		if (type instanceof FileType) {
			return "UIFileType";
		}
		if (type instanceof TimeType || type instanceof DateTimeType) {
			return "UIDateType";
		}
		if (type instanceof BEObjRefType) {
			return "UIEntityType";
		}
		if (type instanceof CEObjRefType) {
			return "UIComboBoxType";
		}
		if (type instanceof BEListType) {
			if (((BEListType)type).getElementType() instanceof BEObjRefType) {
				return "UITableType";
			}
			if (((BEListType)type).getElementType() instanceof CEObjRefType) {
				return "UIComboBoxType";
			}
			return "UIComboBoxType";
		}
		
		return "UILabelType";
	}
	
	public static UIComponentType createUIComponent(String uitype) {
		if ("UILabelType".equals(uitype)) {
			return new UILabelType();
		}
		if ("UIHiddenType".equals(uitype)) {
			return new UIHiddenType();
		}
		if ("UITextAreaType".equals(uitype)) {
			return new UITextAreaType();
		}
		if ("UITextFieldType".equals(uitype)) {
			return new UITextFieldType();
		}
		if ("UIFileType".equals(uitype)) {
			return new UIFileType();
		}
		if ("UIComboBoxType".equals(uitype)) {
			return new UIComboBoxType();
		}
		if ("UICheckBoxType".equals(uitype)) {
			return new UICheckBoxType();
		}
		if ("UIDateType".equals(uitype)) {
			return new UIDateType();
		}
		if ("UITableType".equals(uitype)) {
			return new UITableType();
		}
		if ("UIEntityType".equals(uitype)) {
			return new UIReferenceEntityType();
		}
		return new UILabelType();
	}
	
	public static String[] getVarCategories() {
		return CATEGORIES;
	}
	
	public static String[] getVarScopes() {
		return SCOPES;
	}
	
	public static String upperCaseFristWord(String name)
    {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    } 
	
	public static String lowerCaseFristWord(String name)
    {
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    } 
}
