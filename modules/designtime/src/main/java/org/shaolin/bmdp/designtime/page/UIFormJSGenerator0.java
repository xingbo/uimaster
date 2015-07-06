
package org.shaolin.bmdp.designtime.page;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.shaolin.bmdp.datamodel.page.ArrayPropertyType;
import org.shaolin.bmdp.datamodel.page.BooleanPropertyType;
import org.shaolin.bmdp.datamodel.page.EntityPropertyType;
import org.shaolin.bmdp.datamodel.page.FunctionType;
import org.shaolin.bmdp.datamodel.page.OpCallAjaxType;
import org.shaolin.bmdp.datamodel.page.OpExecuteScriptType;
import org.shaolin.bmdp.datamodel.page.OpType;
import org.shaolin.bmdp.datamodel.page.PropertyValueType;
import org.shaolin.bmdp.datamodel.page.ReconfigurablePropertyType;
import org.shaolin.bmdp.datamodel.page.ReconfigurableType;
import org.shaolin.bmdp.datamodel.page.ResourceBundlePropertyType;
import org.shaolin.bmdp.datamodel.page.StringPropertyType;
import org.shaolin.bmdp.datamodel.page.UIBaseType;
import org.shaolin.bmdp.datamodel.page.UIButtonType;
import org.shaolin.bmdp.datamodel.page.UICheckBoxGroupType;
import org.shaolin.bmdp.datamodel.page.UICheckBoxType;
import org.shaolin.bmdp.datamodel.page.UIComboBoxType;
import org.shaolin.bmdp.datamodel.page.UIComponentType;
import org.shaolin.bmdp.datamodel.page.UIContainerType;
import org.shaolin.bmdp.datamodel.page.UIDateType;
import org.shaolin.bmdp.datamodel.page.UIEmptyType;
import org.shaolin.bmdp.datamodel.page.UIEntity;
import org.shaolin.bmdp.datamodel.page.UIFileType;
import org.shaolin.bmdp.datamodel.page.UIFlowDiagramType;
import org.shaolin.bmdp.datamodel.page.UIFrameType;
import org.shaolin.bmdp.datamodel.page.UIHiddenType;
import org.shaolin.bmdp.datamodel.page.UIImageType;
import org.shaolin.bmdp.datamodel.page.UILabelType;
import org.shaolin.bmdp.datamodel.page.UILinkType;
import org.shaolin.bmdp.datamodel.page.UIListType;
import org.shaolin.bmdp.datamodel.page.UIMultiChoiceType;
import org.shaolin.bmdp.datamodel.page.UIPanelType;
import org.shaolin.bmdp.datamodel.page.UIPasswordFieldType;
import org.shaolin.bmdp.datamodel.page.UIRadioButtonGroupType;
import org.shaolin.bmdp.datamodel.page.UIRadioButtonType;
import org.shaolin.bmdp.datamodel.page.UIReferenceEntityType;
import org.shaolin.bmdp.datamodel.page.UISelectComponentType;
import org.shaolin.bmdp.datamodel.page.UISingleChoiceType;
import org.shaolin.bmdp.datamodel.page.UISkinType;
import org.shaolin.bmdp.datamodel.page.UITabPaneItemType;
import org.shaolin.bmdp.datamodel.page.UITabPaneType;
import org.shaolin.bmdp.datamodel.page.UITableType;
import org.shaolin.bmdp.datamodel.page.UITextAreaType;
import org.shaolin.bmdp.datamodel.page.UITextComponentType;
import org.shaolin.bmdp.datamodel.page.UITextFieldType;
import org.shaolin.bmdp.datamodel.page.UIWebMenuType;
import org.shaolin.bmdp.datamodel.page.UIWebTreeType;
import org.shaolin.bmdp.datamodel.page.ValidatorPropertyType;
import org.shaolin.bmdp.datamodel.page.ValidatorsPropertyType;
import org.shaolin.bmdp.designtime.tools.GeneratorOptions;
import org.shaolin.bmdp.designtime.tools.PumpWriter;
import org.shaolin.bmdp.runtime.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UIFormJSGenerator0 {
    //for log4j
    private static Logger logger = LoggerFactory.getLogger(UIFormJSGenerator0.class);
    
    protected final PumpWriter out = new PumpWriter();
    
    protected String entityName = null;
    protected List jsText = null;
    protected ArrayList rootPanelValidators = new ArrayList();
    protected Map referenceMap = null;
    protected ArrayList refJS;
    protected GeneratorOptions option = null;

    public UIFormJSGenerator0() {}
    
    public UIFormJSGenerator0(GeneratorOptions option) {
		this.option = option;
		
		out.setOutputDir(option.getSrcDir());
		out.setEncoding(Registry.getInstance().getEncoding());
	}
    
    protected void divideContainer(UIContainerType container, List commonComponent, List reference,
            List containerList, boolean isObjectList)
    {
        List<UIComponentType> components = container.getComponents();
        for(UIComponentType component : components)
        {
			if (component instanceof UIEmptyType) {
				continue;
			}

			if (component instanceof UIReferenceEntityType) {
				reference.add(component);
			} else if (component instanceof UITabPaneType) {
				if (((UITabPaneType) component).isAjaxLoad()) {
					// only add the first tab.
					UITabPaneItemType tab = ((UITabPaneType) component).getTabs().get(0);
					if (tab.getRefEntity() != null) {
						reference.add(tab.getRefEntity());
					} else if (tab.getPanel() != null) {
						divideContainer((UIContainerType) tab.getPanel(), commonComponent,
								reference, containerList, isObjectList);
					}
				} else {
					// add all tabs if ajax loading is disabled.
					List<UITabPaneItemType> tabs = ((UITabPaneType) component).getTabs();
					for (UITabPaneItemType t : tabs) {
						if (t.getRefEntity() != null) {
							reference.add(t.getRefEntity());
						} else if (t.getPanel() != null) {
							divideContainer((UIContainerType) t.getPanel(), commonComponent,
									reference, containerList, isObjectList);
						}
					}
				}
				commonComponent.add(component);
			} else if (component instanceof UIContainerType) {
				containerList.add(component);
			} else {
				commonComponent.add(component);
			}

			if (component instanceof UIContainerType) {
				divideContainer((UIContainerType) component, commonComponent,
						reference, containerList, isObjectList);
			}
        }
    }
    
    protected void genComponentJS(PrintWriter out, List commonComponent, List reference,
            List container, UIEntity uiEntity)
    {
        for (int i = 0; i<commonComponent.size(); i++)
        {
            UIComponentType cType = (UIComponentType) commonComponent.get(i);
            genRefJS(out, cType);
            genCommonComponentJS(out, cType, uiEntity);
        }//end of common components
    
        for (int i=0; i<reference.size(); i++)
        {
            UIReferenceEntityType rType = (UIReferenceEntityType) reference.get(i);
            genRefComponentJS(out, rType, uiEntity);
        }//end of for (int i=0; i<reference.size(); i++)
    
    
        for (int i = container.size() - 1; i >= 0; i--)
        {
            UIContainerType tType = (UIContainerType) container.get(i);
            genContainerJS(out, tType, uiEntity);
        }//end of for (int i=0; i<container.size(); i++)
    }
    
    protected void genRootPanelJS(PrintWriter out, UIPanelType rootPanel,
        List commonComponent, List reference, List container, UIEntity uiEntity)
    {
        //root panel items
        int commonSize = commonComponent.size();
        int refSize = reference.size();
        int containerSize = container.size();
        int index = 0;
    
        UIComponentType[] components = new UIComponentType[commonSize + refSize + containerSize];
    
        for (int i = 0, n = commonSize; i < n; i++)
        {
            components[index] = (UIComponentType) commonComponent.get(i);
            index++;
        }
        for (int i = 0, n = refSize; i < n; i++)
        {
            components[index] = (UIComponentType) reference.get(i);
            index++;
        }
        for (int i = 0, n = containerSize; i < n; i++)
        {
            components[index] = (UIComponentType) container.get(i);
            index++;
        }
    
        //root panel validators
		if (rootPanel.getValidator() != null) {
			if (rootPanel.getValidator() instanceof ValidatorPropertyType) {
				rootPanelValidators.add((ValidatorPropertyType) rootPanel
						.getValidator());
			} else if (rootPanel.getValidator() instanceof ValidatorsPropertyType) {
				ValidatorsPropertyType validators = (ValidatorsPropertyType) rootPanel
						.getValidator();
				List<ValidatorPropertyType> validatorList = validators
						.getValidators();
				for (ValidatorPropertyType validator : validatorList) {
					rootPanelValidators.add(validator);
				}
			}
		}
    
        genRefJS(out, rootPanel);
        genUIContainerJS(out, rootPanel, true, components, uiEntity);
        genRelation(out, components);
    }
    
    protected void genUserConstructor(PrintWriter out) {
		if (jsText != null) {
			List result = JSMerge.getConstructPlugin(jsText);
			if (result != null) {
				for (int i = 0, n = result.size(); i < n; i++) {
					out.write("            ");
					out.print(result.get(i));
					out.write("\n");
				}
			}
		}
	}
    
	protected void genOtherFunc(PrintWriter out) {
		String jsEntityName = entityName.replace('.', '_');
		out.write("/* Other_Func_FIRST:");
		out.print(jsEntityName);
		out.write(" */\n");

		if (jsText != null) {
			List result = JSMerge.getOtherFunc(jsText);

			for (int i = 0; i < result.size(); i++) {

				out.write("\n    ");
				out.print(result.get(i));
				out.write("\n");

			}
		}
		out.write("/* Other_Func_LAST:");
		out.print(jsEntityName);
		out.write(" */\n");
	}
    
	protected void genFunctionHead(PrintWriter out, Map forOut, UIBaseType uiEntity, Map referenceMap)
    {
        String jsEntityName = entityName.replace('.', '_');
        List<FunctionType> functions = uiEntity.getEventHandlers();
        for (FunctionType fType: functions)
        {
            boolean isOut = false;
            if (fType.getFunctionName() == null || fType.getFunctionName().equals("") )
            {
                logger.error("EventHandler Function Name error! in entity: " + entityName, new Exception("Function Name can't be empty."));
            }
			if (forOut != null && forOut.containsKey(fType.getFunctionName())) {
				isOut = true;
			} else {
				isOut = false;
			}
            String functionName = fType.getFunctionName();
    
	        out.write("\n    /* auto generated eventlistener function declaration */\n    function ");
	        out.print(jsEntityName);
	        out.write("_");
	        out.print(functionName);
	        out.write("(eventsource,event) {/* Gen_First:");
	        out.print(jsEntityName);
	        out.write("_");
	        out.print(functionName);
	        out.write(" */\n");
    
            String body = null;
            if (jsText != null)
            {
                body = JSMerge.getBody(entityName + "." + functionName, jsText);
            }
            if (body != null && !body.equals(""))
            {
		        out.write("        ");
		        out.print(body);
		        out.write("\n");
            }
            else
            {
				if (referenceMap.containsKey(functionName)) {
					String funcName = (String) referenceMap.get(functionName);
					out.write("        ");
					out.print(funcName);
					out.write("(eventsource,event);\n");
				}
				List<OpType> ops = fType.getOps();
				for (OpType op : ops) {
					if (op instanceof OpExecuteScriptType) {
						out.write(((OpExecuteScriptType)op).getExpressionString());
					} else if (op instanceof OpCallAjaxType) {
						out.write("\n        // cal ajax function. \n");
						out.write("\n        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),\"");
						out.print(((OpCallAjaxType)op).getName());
				      	out.write("\",UIMaster.getValue(eventsource),this.__entityName);\n");
					}
				}
				out.write("\n        var UIEntity = this;\n");
				if (isOut) {
					out.write("\n        this.");
					out.print(forOut.get(fType.getFunctionName())
							+ "_OutFunctionName");
					out.write("(eventsource);\n");
				}
	        }
    
        out.write("    }/* Gen_Last:");
        out.print(jsEntityName);
        out.write("_");
        out.print(functionName);
        out.write(" */\n\n");
    
        }
    }
    
    protected void genReturnJS(PrintWriter out)
    {
        out.write("\n    Form.init();\n    return Form;\n};\n");
    }
    
    protected void genRefJS(PrintWriter out, UIComponentType component) {
		PropertyValueType validator = component.getValidator();

		if (validator != null) {
			if (validator instanceof ValidatorPropertyType) {
				processRefJS(out, (ValidatorPropertyType) validator);
			} else if (validator instanceof ValidatorsPropertyType) {
				for (int i = 0, n = rootPanelValidators.size(); i < n; i++) {
					processRefJS(out,
							(ValidatorPropertyType) rootPanelValidators.get(i));
				}
			}
		}
	}
    
	protected void processRefJS(PrintWriter out, ValidatorPropertyType validator) {
		if (validator.isIsFuncRef()) {
			String jsName = processTargetEntity(validator);
			if (jsName != null && !refJS.contains(jsName)) {
				refJS.add(jsName);
			}
		}
	}
    
    //For three divided list: CommonComponent, ReferenceComponent, ContainerComponent
	protected void genCommonComponentJS(PrintWriter out,
			UIComponentType component, UIEntity uiEntity) {
		if (component instanceof UITextComponentType) {
			genUITextComponentJS(out, (UITextComponentType) component, uiEntity);
		} else if (component instanceof UISingleChoiceType) {
			genUISingleChoiceJS(out, (UISingleChoiceType) component, uiEntity);
		} else if (component instanceof UIMultiChoiceType) {
			genUIMultiChoiceJS(out, (UIMultiChoiceType) component, uiEntity);
		} else if (component instanceof UISelectComponentType) {
			genUISelectComponentJS(out, (UISelectComponentType) component, uiEntity);
		} else {
			genOtherComponentJS(out, component, uiEntity);
		}
		out.write("\n");
	}
    
    protected void genRefComponentJS(PrintWriter out, UIReferenceEntityType refComponent, UIEntity uiEntity)
    {
    	String cID = refComponent.getUIID();
    	if (refComponent.getReferenceEntity() == null) {
    		out.write("// refered ui form["+ cID +"] is null.");
    		return;
    	}
    	
        String referenceClassName = refComponent.getReferenceEntity().getEntityName();
        boolean isReferenceInterface = refComponent.isIsReferenceInterface();
    
        out.write("    var ");
        out.print(cID);
    
        if (isReferenceInterface) {
	        out.write("= eval(\"new \" + elementList[prefix + \"");
	        out.print(cID);
	        out.write("\"].value.replace(/\\./g, \"_\") + \"({\\\"prefix\\\":prefix + \\\"");
	        out.print(cID);
	        out.write(".\\\"");
        } else {
	        out.write(" = new ");
	        out.print(referenceClassName.replace('.', '_'));
	        out.write("({\"prefix\":prefix + \"");
	        out.print(cID);
	        out.write(".\"");
        }
    
		List<ReconfigurableType> types = uiEntity.getReconfigurableProperties();
		for (ReconfigurableType type: types) {
			if (type instanceof ReconfigurablePropertyType) {
				ReconfigurablePropertyType rpt = (ReconfigurablePropertyType) type;
				String id = rpt.getComponentId();
				String propertyName = rpt.getPropertyName();
				String newPropertyName = rpt.getNewPropertyName();
				if (id.equals(cID)) {
					out.write(",\n    ");
					out.print(propertyName);
					out.write(":reconfiguration.");
					out.print(newPropertyName);
				}
			}
		}

		out.write("});");
		if (isReferenceInterface) {
			out.write("\");");
		}
		out.write("\n\n");
    
    }
    
    protected void genContainerJS(PrintWriter out, UIContainerType container, UIEntity uiEntity)
    {
        genUIContainerJS(out, container, false, null, uiEntity);
        out.write("\n");
    }
    
    //For five abstract type in UIBase schema: UIText, UISingleChoice, UIMultiChoice, UISelectComponent, UIContainer
    protected void genUITextComponentJS(PrintWriter out, UITextComponentType textComponent, UIEntity uiEntity)
    {
        String cID = textComponent.getUIID();
        out.write("    var ");
        out.print(cID);
		if (textComponent instanceof UIPasswordFieldType) {
			out.write(" = new ");
			out.print(JSConstants.PASSWORDFIELD);
			out.write("\n");
		} else if (textComponent instanceof UITextFieldType) {
			out.write(" = new ");
			out.print(JSConstants.TEXTFIELD);
			out.write("\n");
		} else if (textComponent instanceof UITextAreaType) {
			out.write(" = new ");
			out.print(JSConstants.TEXTAREA);
			out.write("\n");
		} else if (textComponent instanceof UILinkType) {
			out.write(" = new ");
			out.print(JSConstants.LINK);
			out.write("\n");
		} else if (textComponent instanceof UILabelType) {
			out.write(" = new ");
			out.print(JSConstants.LABEL);
			out.write("\n");
		} else if (textComponent instanceof UIHiddenType) {
			out.write(" = new ");
			out.print(JSConstants.HIDDEN);
			out.write("\n");
		} else if (textComponent instanceof UIButtonType) {
			out.write(" = new ");
			out.print(JSConstants.BUTTON);
			out.write("\n");
		} else if (textComponent instanceof UIDateType) {
			out.write(" = new ");
			out.print(JSConstants.CALENDAR);
			out.write("\n");
		}
        out.write("    ({\n");
    
        processCommonField(out, textComponent, uiEntity);
        if (textComponent instanceof UITextFieldType 
        		|| textComponent instanceof UITextAreaType
        		|| textComponent instanceof UIDateType)
        {
            processTextConstraint(out, textComponent);
        }
        //processReconfiguration(out,textComponent,inObjList,uiEntity);
    
        out.write("    });\n");
    }
    
    protected void genUISingleChoiceJS(PrintWriter out, UISingleChoiceType singleChoice, UIEntity uiEntity)
    {
		String cID = singleChoice.getUIID();
		out.write("    var ");
		out.print(cID);
		if (singleChoice instanceof UIRadioButtonGroupType) {

			out.write(" = new ");
			out.print(JSConstants.RADIOBUTTONGROUP);
			out.write("\n");

		} else if (singleChoice instanceof UIComboBoxType) {

			out.write(" = new ");
			out.print(JSConstants.COMBOBOX);
			out.write("\n");

		}
		out.write("    ({\n");

		processCommonField(out, singleChoice, uiEntity);
		if (singleChoice instanceof UIRadioButtonGroupType) {
			processChoiceConstraint(out, singleChoice);
		} else if (singleChoice instanceof UIComboBoxType) {
			processSelectConstraint(out, singleChoice);
		}
		// processReconfiguration(out,singleChoice,inObjList,uiEntity);

		out.write("    });\n");
    }
    
    protected void genUIMultiChoiceJS(PrintWriter out, UIMultiChoiceType multiChoice, UIEntity uiEntity)
    {
        String cID = multiChoice.getUIID();
        out.write("    var ");
        out.print(cID);
		if (multiChoice instanceof UIListType) {
			out.write(" = new ");
			out.print(JSConstants.LIST);
			out.write("\n");
		} else if (multiChoice instanceof UICheckBoxGroupType) {
			out.write(" = new ");
			out.print(JSConstants.CHECKBOXGROUP);
			out.write("\n");
		}
        out.write("    ({\n");
    
		processCommonField(out, multiChoice, uiEntity);
		if (multiChoice instanceof UICheckBoxGroupType) {
			processChoiceConstraint(out, multiChoice);
		} else if (multiChoice instanceof UIListType) {
			processSelectConstraint(out, multiChoice);
		}
        //processReconfiguration(out,multiChoice,inObjList,uiEntity);
        out.write("    });\n");
    }
    
    protected void genUISelectComponentJS(PrintWriter out, UISelectComponentType selectComponent, UIEntity uiEntity)
    {
        String cID = selectComponent.getUIID();
        if (selectComponent instanceof UICheckBoxType)
        {
	        out.write("    var ");
	        out.print(cID);
	        out.write(" = new ");
	        out.print(JSConstants.CHECKBOX);
	        out.write("\n    ({\n");
    
            processCommonField(out, selectComponent, uiEntity);
            processChoiceConstraint(out, selectComponent);
            //processReconfiguration(out,selectComponent,inObjList,uiEntity);
            out.write("    });\n");
        }
        else if (selectComponent instanceof UIRadioButtonType) //UIRadioButton in UIObjectList columns will be UIRadioButtonGroup
        {
	        out.write("    var ");
	        out.print(cID);
	        out.write(" = new ");
	        out.print(JSConstants.RADIOBUTTON);
	        out.write("\n    ({\n");
    
            processCommonField(out, selectComponent);
            processChoiceConstraint(out, selectComponent);
            //processReconfiguration(out,selectComponent,inObjList,uiEntity);
            out.write("    });\n");
        }
    }
    
    protected void genOtherComponentJS(PrintWriter out, UIComponentType component, UIEntity uiEntity)
    {
        String cID = component.getUIID();
        out.write("    var ");
        out.print(cID);
		if (component instanceof UIFrameType) {
			out.write(" = new ");
			out.print(JSConstants.FRAME);
			out.write("\n");
		} else if (component instanceof UIImageType) {
			out.write(" = new ");
			out.print(JSConstants.IMAGE);
			out.write("\n");
		} else if (component instanceof UIFileType) {
			out.write(" = new ");
			out.print(JSConstants.FILE);
			out.write("\n");
		} else if (component instanceof UITabPaneType) {
			out.write(" = new ");
			out.print(JSConstants.TAB);
			out.write("\n");
		} else if (component instanceof UIWebTreeType) {
			out.write(" = new ");
			out.print(JSConstants.WEBTREE);
			out.write("\n");
		} else if (component instanceof UIWebMenuType) {
			out.write(" = new ");
			out.print(JSConstants.WEBMENU);
			out.write("\n");
		} else if (component instanceof UITableType) {
			out.write(" = new ");
			out.print(JSConstants.TABLE);
			out.write("\n");
		}  else if (component instanceof UIFlowDiagramType) {
			out.write(" = new ");
			out.print(JSConstants.FLOW);
			out.write("\n");
		} 
    
        out.write("    ({\n");
        processCommonField(out, component, uiEntity);
        out.write("    });\n");
    
    }
    
    protected void genUIContainerJS(PrintWriter out, UIContainerType container,
        boolean isRootPanel, UIComponentType[] components, UIEntity uiEntity)
    {
        String cID = container.getUIID();
        out.write("    var ");
        out.print(cID);
    
		if (container instanceof UIPanelType) {
			out.write(" = new ");
			out.print(JSConstants.PANEL);
			out.write("\n");
		} 
    
        out.write("    ({\n");
    
        processCommonField(out, container, uiEntity);
        processContainerConstraint(out, container, isRootPanel, components);
        //processReconfiguration(out,container,inObjList,uiEntity);
    
        out.write("    });\n");
    }
    
    //For HTML element
    //HTML textfield, psswordfield, textarea
    protected void processTextConstraint(PrintWriter out, UITextComponentType textComponent)
    {
        processCommonValidator(out, textComponent);
    }
    
    //HTML radioButton, checkBox, radioButtonGroup, checkBoxGroup
    protected void processChoiceConstraint(PrintWriter out, UIComponentType choice)
    {
        PropertyValueType mustCheck = null;
        PropertyValueType mustCheckText = null;
        if (choice instanceof UISelectComponentType)
        {
            UISelectComponentType selectComponent = (UISelectComponentType) choice;
            mustCheck = (BooleanPropertyType) selectComponent.getCheckedValueConstraint();
            mustCheckText = selectComponent.getCheckedValueConstraintText();
        }
        else if (choice instanceof UIRadioButtonGroupType)
        {
            UIRadioButtonGroupType radioButtonGroup = (UIRadioButtonGroupType) choice;
            mustCheck = (ArrayPropertyType) radioButtonGroup.getSelectedValueConstraint();
            mustCheckText = radioButtonGroup.getSelectedValueConstraintText();
        }
        else if (choice instanceof UICheckBoxGroupType)
        {
            UICheckBoxGroupType checkBoxGroup = (UICheckBoxGroupType) choice;
            mustCheck = (ArrayPropertyType) checkBoxGroup.getSelectedValuesConstraint();
            mustCheckText = checkBoxGroup.getSelectedValuesConstraintText();
        }
    
        if (mustCheck != null)
        {
            if ((mustCheck instanceof BooleanPropertyType) && ((BooleanPropertyType) mustCheck).isValue())
            {
            	out.write("        ,mustCheck: true\n");
            }
            else if (mustCheck instanceof ArrayPropertyType)
            {
    
            	out.write("        ,mustCheck: [");
                processArray(out, ((ArrayPropertyType) mustCheck).getProperties());
                out.write("]\n");
    
            }
            if (mustCheckText == null)
            {
                mustCheckText = new ResourceBundlePropertyType();
                ((ResourceBundlePropertyType)mustCheckText).setBundle("Common");
                ((ResourceBundlePropertyType)mustCheckText).setKey("MUST_CHECK");
            }
    
            out.write("        ,mustCheckText:");
            processI18nValue(out, mustCheckText);
            out.write("\n");
    
        }
    
        processCommonValidator(out, choice);
    }
    
    //HTML comboBox(single), comboBox(multiple)
    protected void processSelectConstraint(PrintWriter out, UIComponentType select)
    {
        ArrayPropertyType value = null;
        PropertyValueType valueText = null;
        BooleanPropertyType allowBlank = null;
        StringPropertyType allowBlankText = null;
        
        if (select instanceof UIComboBoxType)
        {
            UIComboBoxType comboBox = (UIComboBoxType) select;
            value = (ArrayPropertyType) comboBox.getSelectedValueConstraint();
            valueText = comboBox.getSelectedValueConstraintText();
            PropertyValueType type1=comboBox.getAllowBlank();
            if(type1 instanceof BooleanPropertyType)allowBlank=(BooleanPropertyType)type1;
            PropertyValueType type2=comboBox.getAllowBlankText();
            if(type2 instanceof StringPropertyType)allowBlankText=(StringPropertyType)type2;
        }
        else if (select instanceof UIListType)
        {
            UIListType list = (UIListType) select;
            value = (ArrayPropertyType) list.getSelectedValuesConstraint();
            valueText = list.getSelectedValuesConstraintText();
            PropertyValueType type1=list.getAllowBlank();
            if(type1 instanceof BooleanPropertyType)allowBlank=(BooleanPropertyType)type1;
            PropertyValueType type2=list.getAllowBlankText();
            if(type2 instanceof StringPropertyType)allowBlankText=(StringPropertyType)type2;
        }
    
        if (value != null)
        {
            if (valueText == null)
            {
                valueText = new ResourceBundlePropertyType();
                ((ResourceBundlePropertyType)valueText).setBundle("Common");
                ((ResourceBundlePropertyType)valueText).setKey("SELECT_VALUE");
            }
    
            out.write("        ,selectValue:[");
            processArray(out, value.getProperties());
            out.write("]\n        ,selectValueText:");
            processI18nValue(out, valueText);
            out.write("\n");
    
    
        }
        
		if (allowBlank != null) {
			out.write("      ,allowBlank:");
			out.print(allowBlank.isValue());
			out.write("\n");
		}
		if (allowBlankText != null) {
			out.write("      ,allowBlankText:");
			processI18nValue(out, allowBlankText);
			out.write("\n");
		}
        
        processCommonValidator(out, select);
    }
    
    //HTML table
    protected void processContainerConstraint(PrintWriter out,
        UIContainerType container, boolean isRootPanel, UIComponentType[] rootComponents)
    {
		if (isRootPanel) {
			out.write("        ,items: [");
			for (int i = 0, n = rootComponents.length; i < n; i++) {
				out.print(((UIComponentType) rootComponents[i]).getUIID());
				if (i != n - 1) {
					out.write(",");
				}
			}
			out.write("]\n");
			ArrayPropertyType validationList = (ArrayPropertyType) container
					.getValidationList();
			if (validationList != null) {
				out.write("        ,validationList:[");
				processValidationList(out, validationList.getProperties());
				out.write("]\n");
			}
			processRootPanelValidators(out);
		} else {
			// sub panel validators added to root panel
			if (container.getValidator() != null) {
				if (container.getValidator() instanceof ValidatorPropertyType) {
					rootPanelValidators.add((ValidatorPropertyType) container
							.getValidator());
				} else if (container.getValidator() instanceof ValidatorsPropertyType) {
					ValidatorsPropertyType validators = (ValidatorsPropertyType) container
							.getValidator();
					List<ValidatorPropertyType> validatorList = validators
							.getValidators();
					for (ValidatorPropertyType validator : validatorList) {
						rootPanelValidators.add(validator);
					}
				}
			}
			out.write("        ,items: []\n        ,subComponents: [");
			List<UIComponentType> subComponents = container.getComponents();
			for (int i = 0, n = subComponents.size(); i < n; i++) {
				if (i > 0) {
					out.write(",");
				}
				out.write("prefix + \"");
				out.print(subComponents.get(i).getUIID());
				out.write("\"");
			}
			out.write("]\n");
		}
	}
    
    protected void processReconfiguration(PrintWriter out,UIComponentType component, UIEntity uiEntity)
    {
        List<ReconfigurableType> types = uiEntity.getReconfigurableProperties();
        for(ReconfigurableType type: types)
        {
            if(type instanceof ReconfigurablePropertyType)
            {
                ReconfigurablePropertyType rpt=(ReconfigurablePropertyType)type;
                String id=rpt.getComponentId();
                String propertyName=rpt.getPropertyName();
                String newPropertyName=rpt.getNewPropertyName();
                if(id.equals(component.getUIID()))
                {
                    if(propertyName.equals("allowBlank")||propertyName.equals("mustCheck"))//boolean values
                    {
				        out.write("        ,");
				        out.print(propertyName);
				        out.write(":(json.");
				        out.print(newPropertyName);
				        out.write(" || reconfiguration.");
				        out.print(newPropertyName);
				        out.write("=='true')\n");
                    }
                    else if(propertyName.equals("minLength")||propertyName.equals("maxLength"))//integer values
                    {
				        out.write("        ,");
				        out.print(propertyName);
				        out.write(":Number(json.");
				        out.print(newPropertyName);
				        out.write(" || reconfiguration.");
				        out.print(newPropertyName);
				        out.write(")\n");
                    }
                    else if(propertyName.equals("regex"))
                    {
				        out.write("        ,");
				        out.print(propertyName);
				        out.write(":new RegExp(json.");
				        out.print(newPropertyName);
				        out.write(" || reconfiguration.");
				        out.print(newPropertyName);
				        out.write(")\n");
                    }
                    else
                    {
				        out.write("        ,");
				        out.print(propertyName);
				        out.write(":json.");
				        out.print(newPropertyName);
				        out.write(" || reconfiguration.");
				        out.print(newPropertyName);
				        out.write("\n");
                    }
                }
            }
        }
    }
    
    protected void processCommonField(PrintWriter out, UIComponentType component, UIEntity uiEntity)
    {
        out.write("        ui: elementList[prefix + \"");
        out.print(component.getUIID());
        out.write("\"");
        out.write("]\n");
        
    
        BooleanPropertyType initValidation = (BooleanPropertyType) component.getInitValidation();
        if (initValidation != null && initValidation.isValue())
        {
        	out.write("        ,flag: true\n");
        }
        
        UISkinType uiskin = component.getUISkin();
        if(uiskin != null)
        {
	        out.write("        ,uiskin: \"");
	        out.print(uiskin.getSkinName());
	        out.write("\"\n");
        }
        
        if (component instanceof UITableType) {
        	UITableType table = (UITableType)component;
        	if (table.getSelectedRowAction() != null) {
        		out.write("        ,selectedRowAction: Form.\"");
    	        out.print(table.getSelectedRowAction());
    	        out.write("\"\n");
        	}
        	if (table.getEditable() != null 
        			&& table.getEditable() instanceof BooleanPropertyType) {
        		out.write("        ,editable: ");
        		out.print(((BooleanPropertyType)table.getEditable()).isValue());
        		out.write("\n");
        	}
        } else if (component instanceof UIDateType) {
        	UIDateType calendar = (UIDateType)component;
        	if (calendar.getIsDataOnly() != null) {
        		out.write("        ,isDataOnly: ");
    	        out.print(calendar.getIsDataOnly().isValue());
    	        out.write("\n");
        	}
        	if (calendar.getIsBiggerThan() != null) {
        		out.write("        ,isBiggerThan: ");
    	        out.print(calendar.getIsBiggerThan().isValue());
    	        out.write("\n");
        	}
        	if (calendar.getIsSmallerThan() != null) {
        		out.write("        ,isSmallerThan: ");
    	        out.print(calendar.getIsSmallerThan().isValue());
    	        out.write("\n");
        	}
        	if (calendar.getFormat() != null) {
        		out.write("        ,format: ");
        		out.print(calendar.getFormat().getValue());
        		out.write("\n");
        	}
        	if (calendar.getDateConstraint() != null) {
        		out.write("        ,dateConstraint: ");
    	        out.print(calendar.getDateConstraint().getValue());
    	        out.write("\n");
        	}
        }
    }
    
    protected void processCommonField(PrintWriter out, UIComponentType component)
    {
        out.write("        ui: elementList[prefix + \"");
        out.print(component.getUIID());
        out.write("\"");
    
        out.write("]\n");
    
        BooleanPropertyType initValidation = (BooleanPropertyType) component.getInitValidation();
        if (initValidation != null && initValidation.isValue())
        {
        	out.write("        ,flag: true\n");
        }
        
        UISkinType uiskin = component.getUISkin();
        if(uiskin != null)
        {
	        out.write("        ,uiskin: \"");
	        out.print(uiskin.getSkinName());
	        out.write("\"\n");
        }
    }
    
	protected void processCommonValidator(PrintWriter out,
			UIComponentType component) {
		if (component.getValidator() == null) {
			return;
		}
		if (component.getValidator() instanceof ValidatorPropertyType) {
			ValidatorPropertyType validator = (ValidatorPropertyType) component.getValidator();
			out.write("        ,validator:\n");
			processCustValidator(out, validator);
		} else if (component.getValidator() instanceof ValidatorsPropertyType) {
			ValidatorsPropertyType validators = (ValidatorsPropertyType) component.getValidator();
			out.write("        ,validators:[\n");
			int count = 0;
			for (ValidatorPropertyType validator: validators.getValidators()) {
				processCustValidator(out, validator);
				if (++count < validators.getValidators().size()) {
					out.write(",\n");
				}
			}
			out.write("]");
		}
	}
    
	protected void processRootPanelValidators(PrintWriter out) {
		if (!rootPanelValidators.isEmpty()) {
			out.write("        ,validator:[\n");
			for (int i = 0, n = rootPanelValidators.size(); i < n; i++) {
				processCustValidator(out,
						(ValidatorPropertyType) rootPanelValidators.get(i));
				if (i != n - 1) {
					out.write("        ,\n");
				}
			}
			out.write("        ]\n");
		}
	}
    
	protected void processCustValidator(PrintWriter out, ValidatorPropertyType validator)
    {
        out.write("        {\n");
        if (!validator.isIsFuncRef())
        {
	        out.write("            func: function() {\n                ");
	        out.print(validator.getFuncCode());
	        out.write("\n            }\n");
        }
        else
        {
	        out.write("            func: ");
	        out.print(processTargetEntity(validator).replace('.', '_'));
	        out.write("\n");
        }
    
        List<StringPropertyType> paramList = validator.getParams();
        if (paramList.size() > 0)
        {
        	out.write("            ,param: [");
            for (int i = 0, n = paramList.size(); i < n; i++)
            {
            	out.print(paramList.get(i).getValue());
                if (i != n - 1)
                {
                	out.write(",");
                }
            }
            out.write("]\n");
        }
    
        List<StringPropertyType> componentList = validator.getComponents();
        if (componentList.size() > 0)
        {
        	out.write("            ,component: [");
            for (int i = 0, n = componentList.size(); i < n; i++)
            {
            	out.print(componentList.get(i).getValue());
                if (i != n - 1)
                {
                	out.write(",");
                }
            }
            out.write("]\n");
    
        }
    
        ResourceBundlePropertyType i18nMsg = validator.getI18NMsg();
        if (i18nMsg != null)
        {
	        out.write("            ,msg: UIMaster_getI18NInfo(\"");
	        out.print(i18nMsg.getBundle());
	        out.write("||");
	        out.print(i18nMsg.getKey());
	        out.write("\")\n");
        }
        else if (validator.getErrMsg() != null)
        {
	        out.write("            ,msg: \"");
	        out.print(validator.getErrMsg());
	        out.write("\"\n");
        }
        else
        {
        	out.write("            ,msg: \"\"\n");
        }
    
        out.write("        }\n");
    
    }
    
	protected void processI18nValue(PrintWriter out, PropertyValueType property)
    {
        if (property instanceof StringPropertyType)
        {
            StringPropertyType sProperty = (StringPropertyType) property;
            String value = sProperty.getValue();
    
            if (value != null)
            {
		        out.write("\"");
		        out.print(value);
		        out.write("\"");
            }
            else
            {
            	out.write("\"\"");
            }
        }
        else if (property instanceof ResourceBundlePropertyType)
        {
            ResourceBundlePropertyType rProperty = (ResourceBundlePropertyType) property;
            String bundle = rProperty.getBundle();
            String key = rProperty.getKey();
    
            if (bundle != null && key != null)
            {
		        out.write("UIMaster_getI18NInfo(\"");
		        out.print(bundle);
		        out.write("||");
		        out.print(key);
		        out.write("\")");
            }
            else
            {
            	out.write("\"\"");
            }
        }
        else//default empty
        {
        	out.write("\"\"");
        }
    }
    
	protected String processTargetEntity(ValidatorPropertyType validator)
    {
        PropertyValueType targetJS = validator.getTargetEntity();
        String targetJSName = null;
        if (targetJS instanceof StringPropertyType)
        {
            targetJSName = ((StringPropertyType) targetJS).getValue();
        }
        else if (targetJS instanceof EntityPropertyType)
        {
            targetJSName = ((EntityPropertyType) targetJS).getValue().getEntityName();
        }
        return targetJSName;
    }
    
	protected void processArray(PrintWriter out, List<PropertyValueType> value)
    {
        for(int i = 0, n = value.size(); i < n; i++)
        {
            processI18nValue(out, value.get(i));
            if (i != n - 1)
            {
            	out.write(",");
            }
        }
    }
    
	protected void processValidationList(PrintWriter out, List<PropertyValueType> value)
    {
        for(int i = 0, n = value.size(); i < n; i++)
        {
            StringPropertyType sProperty = (StringPropertyType) value.get(i);
            String item = sProperty.getValue();
    
            if (item != null)
            {
            	out.print(item);
            }
            else
            {
            	out.write("\"\"");
            }
            if (i != n - 1)
            {
            	out.write(",");
            }
        }
    }
    
	protected void genRelation(PrintWriter out, UIComponentType[] components)
    {
        for (int i = 0, n = components.length; i < n; i++)
        {
	        out.write("\n    Form.");
	        out.print(((UIComponentType) components[i]).getUIID());
	        out.write("=");
	        out.print(((UIComponentType) components[i]).getUIID());
	        out.write(";\n");
        }
    }
    
	protected void genHandlerReference(PrintWriter out, UIBaseType uiBaseData)
    {
        String jsEntityName = entityName.replace('.', '_');
        List<FunctionType> functions = uiBaseData.getEventHandlers();
        for (FunctionType fType: functions)
        {
            if (fType.getFunctionName() == null || fType.getFunctionName().equals("") )
            {
                logger.error("EventHandler Function Name error! in entity: " + entityName, 
                		new Exception("Function Name can't be empty."));
            }
            String functionName = fType.getFunctionName();
    
	        out.write("\n    Form.");
	        out.print(functionName);
	        out.write(" = ");
	        out.print(jsEntityName);
	        out.write("_");
	        out.print(functionName);
	        out.write(";\n");
    
        }
    }
    

}
