package org.shaolin.uimaster.designtime.entity;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.junit.Test;
import org.shaolin.bmdp.datamodel.rdbdiagram.CompositeConditionMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.ConditionFieldMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.LogicOperatorType;
import org.shaolin.bmdp.datamodel.rdbdiagram.RDBDiagram;
import org.shaolin.bmdp.datamodel.rdbdiagram.SearchConditionMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.SearchQueryType;
import org.shaolin.bmdp.datamodel.rdbdiagram.SubQueryMappingType;
import org.shaolin.bmdp.datamodel.rdbdiagram.SubQueryMappingType.FromData;

public class RBDDiagramTypeTest {

	@Test
	public void testSQ() {
		RDBDiagram diagram = new RDBDiagram();
		diagram.setDaoPackage("org.shaolin.uimaster.dao");
		diagram.setDiagramPackage("org.shaolin.uimaster.dao.Test");
		SearchQueryType sq = new SearchQueryType();

		sq.setSearchConditionMapping(new SearchConditionMappingType());
		
		CompositeConditionMappingType ccMapping = new CompositeConditionMappingType();
		ccMapping.setLogicalOperator("AND");
		ccMapping.setToDataFieldPath("Entity.field1");
		ccMapping.getFieldMappings().add(new ConditionFieldMappingType());
		sq.getSearchConditionMapping().getFieldMappings().add(ccMapping);
		sq.getSearchConditionMapping().getFieldMappings().add(new ConditionFieldMappingType());
		
		SubQueryMappingType subSQMapping = new SubQueryMappingType();
		sq.getSearchConditionMapping().getFieldMappings().add(subSQMapping);
		
		LogicOperatorType logicOperator = new LogicOperatorType();
		logicOperator.setType("=");
		subSQMapping.setOperator(logicOperator);
		subSQMapping.setIsDistinct(true);
		subSQMapping.getFromDatas().add(new FromData());
		subSQMapping.getFieldMappings().add(new ConditionFieldMappingType());
		
		diagram.getQueries().add(sq);

		StringWriter writer = new StringWriter();
		try {
			EntityUtil.marshaller(diagram, writer);
			System.out.println(writer.toString());
			
			StringReader reader = new StringReader(writer.toString());
			RDBDiagram a = EntityUtil.unmarshaller(RDBDiagram.class, reader);
			Assert.assertNotNull(a);
		} catch (JAXBException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
