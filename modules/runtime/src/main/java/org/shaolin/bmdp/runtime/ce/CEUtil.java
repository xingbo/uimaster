package org.shaolin.bmdp.runtime.ce;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.shaolin.bmdp.exceptions.I18NRuntimeException;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;

public class CEUtil {

	@SuppressWarnings("unchecked")
	public static <T> List<T> parseCEIntValues(Class<T> clazz, String intValues) {
		if (intValues != null && intValues.length() > 0) { 
			String[] values = intValues.split(",");
			int[] intvalues = new int[values.length];
			for (int i = 0; i < values.length; i++) {
				intvalues[i] = Integer.valueOf(values[i]);
			}
			return (List<T>) getConstantEntities(clazz.getName(), intvalues);
		}
		return (List<T>) getConstantEntities(clazz.getName(), Collections.EMPTY_LIST);
	}
	
	public static String parseCEIntValues2String(@SuppressWarnings("rawtypes") List values) {
		StringBuffer sb = new StringBuffer();
		for (Object v : values) {
			sb.append(((IConstantEntity) v).getIntValue()).append(",");
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}
	
	public static List<String> listCEValues(String ceType) {
		return listCEValues(ceType, true);
	}

	public static List<String> listCEValues(String ceType, boolean containsNotSpecified) {
		return listCEValues(ceType, containsNotSpecified, null);
	}

	public static List<String> listCEValues(String ceType,
			boolean containsNotSpecified,
			List<String> excludeValue) {
		List<IConstantEntity> constantEntities = getConstantEntities(ceType,
				containsNotSpecified, excludeValue);

		return listCEValues(constantEntities);
	}

	public static List<String> listCEDisplayValues(String ceType) {
		return listCEDisplayValues(ceType, true, true);
	}

	public static List<String> listCEDisplayValues(String ceType,
			boolean containsNotSpecified) {
		return listCEDisplayValues(ceType, containsNotSpecified, null);
	}

	public static List<String> listCEDisplayValues(String ceType,
			boolean containsNotSpecified, boolean containsUnknown) {
		return listCEDisplayValues(ceType, containsNotSpecified, null);
	}

	public static List<String> listCEDisplayValues(String ceType,
			boolean containsNotSpecified, List<String> excludeValue) {
		return listCEDisplayValues(ceType, containsNotSpecified,
				excludeValue, null);
	}

	public static List<String> listCEDisplayValues(String ceType,
			boolean containsNotSpecified, List<String> excludeValue, 
			String notSpecifiedDisplayValue) {
		List<IConstantEntity> constantEntities = getConstantEntities(ceType,
				containsNotSpecified, excludeValue);

		return listCEDisplayValues(constantEntities, notSpecifiedDisplayValue);
	}

	public static List<IConstantEntity> getConstantEntities(String ceType,
			boolean containsNotSpecified, List<String> excludeValue) {

		IConstantEntity ceEntity = IServerServiceManager.INSTANCE.getConstantService().getConstantEntity(ceType);

		List<IConstantEntity> result = ceEntity.getConstantList();
		if (!containsNotSpecified) {
			for (IConstantEntity item : result) {
				if (item.getValue().equals(
						IConstantEntity.CONSTANT_DEFAULT_VALUE)) {
					result.remove(item);
					break;
				}
			}
		}

		return result;
	}
	
	public static List<IConstantEntity> getConstantEntities(String ceType) {
		return getConstantEntities(ceType, new int[0]);
	}
	
	public static List<IConstantEntity> getConstantEntities(String ceType, List<String> values) {
		List<IConstantEntity> items = getConstantEntities(ceType, true, null);
		if (values.size() == 0) {
			return items;
		}
		List<IConstantEntity> realItems = new ArrayList<IConstantEntity>();
		for (IConstantEntity item: items) {
			String value = item.getValue();
			for (String v: values) {
				if (v.equals(value)) {
					realItems.add(item);
				}
			}
			
		}
		return realItems;
	}
	
	public static List<IConstantEntity> getConstantEntities(String ceType, int[] values) {
		List<IConstantEntity> items = getConstantEntities(ceType, true, null);
		if (values.length == 0 ) {
			return items;
		}
		List<IConstantEntity> realItems = new ArrayList<IConstantEntity>();
		for (IConstantEntity item: items) {
			for (int v: values) {
				if (v == item.getIntValue()) {
					realItems.add(item);
				}
			}
		}
		return realItems;
	}

	public static Map<Integer, String> getAllConstants(Class ce) throws Exception {
		Map<Integer, String> m = new HashMap<Integer, String>();
		List<IConstantEntity> items = getConstantEntities(ce.getName(), true, null);
		for (IConstantEntity item: items) {
			m.put(item.getIntValue(), item.getValue());
		}
		return m;
	}

	public static List<String> listCEValues(
			List<IConstantEntity> constantEntities) {
		List<String> constantEntityValues = new ArrayList<String>();
		for (IConstantEntity constantEntity : constantEntities) {
			constantEntityValues.add(constantEntity.getValue());
		}

		return constantEntityValues;
	}

	public static List<String> listCEDisplayValues(
			List<IConstantEntity> constantEntities) {
		return listCEDisplayValues(constantEntities, null);
	}

	public static List<String> listCEDisplayValues(
			List<IConstantEntity> constantEntities,
			String notSpecifiedDisplayValue) {
		List<String> constantEntityDisplayValues = new ArrayList<String>();

		boolean hasNotSpecifiedDisplayValue = notSpecifiedDisplayValue != null;

		for (IConstantEntity constantEntity : constantEntities) {
			if (hasNotSpecifiedDisplayValue
					&& IConstantEntity.CONSTANT_DEFAULT_VALUE
							.equals(constantEntity.getValue())) {
				constantEntityDisplayValues.add(notSpecifiedDisplayValue);
			} else {
				constantEntityDisplayValues
						.add(constantEntity.getDisplayName());
			}
		}

		return constantEntityDisplayValues;
	}

	public static IConstantEntity getConstantEntity(String ceType) {
		return getConstantEntity(IConstantEntity.CONSTANT_DEFAULT_VALUE, ceType);
	}
	
	public static IConstantEntity getConstantEntity(String ceType, int intValue) {
		IConstantEntity ceEntity = IServerServiceManager.INSTANCE.getConstantService().getConstantEntity(ceType);

		List<IConstantEntity> result = ceEntity.getConstantList();
		for (IConstantEntity item : result) {
			if (item.getIntValue() == intValue) {
				return item;
			}
		}
	
		throw new IllegalArgumentException("CE int value " + intValue
				+ " can't be matched from CE entity: " + ceType);
	}
	
	public static IConstantEntity getConstantEntity(String ceValue,
			String ceType) {
		if (ceValue != null) {
			ceValue = ceValue.trim();
		}

		if (ceValue == null || "".equals(ceValue)) {
			ceValue = IConstantEntity.CONSTANT_DEFAULT_VALUE;
		}

		IConstantEntity ceEntity = IServerServiceManager.INSTANCE.getConstantService().getConstantEntity(ceType);

		List<IConstantEntity> result = ceEntity.getConstantList();
		for (IConstantEntity item : result) {
			if (item.getValue().equals(ceValue)) {
				return item;
			}
		}

		throw new IllegalArgumentException("CE value " + ceValue
				+ " can't be matched from CE entity: " + ceType);
	}

	/**
	 * 
	 * @param allList
	 *            the list of Objects
	 * @param selectedList
	 *            the list of Boolean
	 * @return List
	 */
	public static List<?> getSelectedValues(List<?> allList,
			List<Boolean> selectedList) {
		if (allList.size() != selectedList.size()) {
			throw new I18NRuntimeException(
					ExceptionConstants.EBOS_ODMAPPER_010, new Object[] {
							new Integer(allList.size()),
							new Integer(selectedList.size()) });

		}
		List result = new ArrayList();

		for (int i = 0, n = allList.size(); i < n; i++) {
			if (selectedList.get(i).booleanValue()) {
				result.add(allList.get(i));
			}
		}

		return result;
	}
	
	public static String getValue(String pattern) {
		int i = pattern.indexOf(",") ;
		if (i!= -1) {
			IConstantEntity constant = IServerServiceManager.INSTANCE.getConstantService().getConstantEntity(pattern.substring(0, i));
			return constant.getByIntValue(Integer.valueOf(pattern.substring(i+1))).getDisplayName();
		}
		return pattern;
	}
	
	public static String getValue(IConstantEntity item) {
		return item.getEntityName() + "," + item.getIntValue();
	}
	
}
