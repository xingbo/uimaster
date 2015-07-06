package org.shaolin.bmdp.designtime.bediagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.shaolin.bmdp.datamodel.bediagram.BEObjRefType;
import org.shaolin.bmdp.datamodel.bediagram.BusinessEntityType;
import org.shaolin.bmdp.datamodel.bediagram.IntType;
import org.shaolin.bmdp.datamodel.bediagram.LongType;
import org.shaolin.bmdp.datamodel.bediagram.MemberType;
import org.shaolin.bmdp.datamodel.bediagram.ObjectRefType;
import org.shaolin.bmdp.datamodel.bediagram.PersistenceTypeType;
import org.shaolin.bmdp.datamodel.bediagram.PersistentConfigType;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.bmdp.runtime.entity.InvalidEntityException;

class BusinessEntityInfo {
	
	final BusinessEntityType beType;
	final Map<String, MemberType> fieldMap = new LinkedHashMap<String, MemberType>();
	final Map<PersistenceTypeType, List> pkMap = new HashMap<PersistenceTypeType, List>();
	List equalsConfig = null;

	BusinessEntityInfo(BusinessEntityType beType) {
		this.beType = beType;

		if (BESourceGenerator.isPersistentByBE(beType, PersistenceTypeType.RDB)) {
			MemberType rdbObjectId = new MemberType();
			rdbObjectId.setName("_rdbObjectId");
			rdbObjectId.setType(new LongType());
			rdbObjectId.setAccessible(true);
			rdbObjectId.setModifiable(true);
			fieldMap.put("_rdbObjectId", rdbObjectId);
		}

		for (MemberType m : beType.getMembers()) {
			fieldMap.put(m.getName(), m);
		}

		if (BESourceGenerator.isSelfNeedHistory(beType)) {
			MemberType version = new MemberType();
			version.setName("_version");
			version.setType(new IntType());
			version.setAccessible(true);
			version.setModifiable(true);
			fieldMap.put("_version", version);

			MemberType starttime = new MemberType();
			starttime.setName("_starttime");
			starttime.setType(new LongType());
			starttime.setAccessible(true);
			starttime.setModifiable(true);
			fieldMap.put("_starttime", starttime);

			MemberType endtime = new MemberType();
			endtime.setName("_endtime");
			endtime.setType(new LongType());
			endtime.setAccessible(true);
			endtime.setModifiable(true);
			fieldMap.put("_endtime", endtime);
		}

		for (PersistentConfigType pct : beType.getPersistentConfigs()) {
			List pkList = new ArrayList();
			for (String pkFieldName : pct.getPrimaryKeyMembers()) {
				MemberType pkField = (MemberType) fieldMap.get(pkFieldName);
				if (pkField == null) {
					pkField = getBEFieldByName(beType, pkFieldName);
				}
				PrimaryKeyInfo pkInfo = new PrimaryKeyInfo();
				pkInfo.name = pkFieldName;
				pkInfo.type = BESourceGenerator.getDataTypeClassName(beType.isNeedPersist(), pkField.getType());
				pkInfo.wrapperType = BESourceGenerator.getWrappedDataTypeClass(beType.isNeedPersist(), pkField.getType());
				pkInfo.value = BESourceGenerator.getWrapperDataTypeObject(pkField.getType(), pkFieldName);
				pkList.add(pkInfo);
			}

			if (beType.isNeedHistory() && pkList.size() > 0) {
				PrimaryKeyInfo pkInfo = new PrimaryKeyInfo();
				pkInfo.name = "_version";
				pkInfo.type = "int";
				pkInfo.wrapperType = "Integer";
				pkInfo.value = "new Integer(_version)";
				pkList.add(pkInfo);
			}
			PersistenceTypeType pt = pct.getSupportedPersistenceType();
			pkMap.put(pt, pkList);
		}
	}

	private MemberType getBEFieldByName(BusinessEntityType businessEntity,
			String fieldName) {
		while (true) {
			ObjectRefType parentObject = businessEntity.getParentObject();
			if (parentObject != null && parentObject instanceof BEObjRefType) {
				String name = ((BEObjRefType) parentObject).getTargetEntity()
						.getEntityName();
				//TODO:
				//businessEntity = BESourceGenerator.getBusinessEntityByName(name);
			} else {
				break;
			}
			for (MemberType field : businessEntity.getMembers()) {
				if (fieldName.equals(field.getName())) {
					return field;
				}
			}
		}
		throw new InvalidEntityException(ExceptionConstants.EBOS_BE_021,
				new Object[] { businessEntity.getEntityName() });
		// throw new InvalidEntityException("Primary key field:" + fieldName +
		// " not found in entity:" + businessEntity.getEntityName());
	}
	
	public class PrimaryKeyInfo
	{
	    String name;
	    String type;
	    String wrapperType;
	    String value;
	}


}
