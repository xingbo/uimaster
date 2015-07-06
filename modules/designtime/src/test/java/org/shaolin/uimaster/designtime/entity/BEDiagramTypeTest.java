package org.shaolin.uimaster.designtime.entity;

import java.io.StringWriter;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.junit.Test;
import org.shaolin.bmdp.datamodel.bediagram.BEDiagram;
import org.shaolin.bmdp.datamodel.bediagram.BusinessEntityType;
import org.shaolin.bmdp.datamodel.bediagram.LongType;
import org.shaolin.bmdp.datamodel.bediagram.MemberType;

public class BEDiagramTypeTest {

	@Test
	public void test() {
		BEDiagram diagram = new BEDiagram();
		diagram.setBePackage("org.shaolin.uimaster.be");
		BusinessEntityType be = new BusinessEntityType();
		be.setEntityName("Customer");
		be.setAuthor("Shaolin");
		MemberType id = new MemberType();
		id.setName("id");
		id.setType(new LongType());
		be.getMembers().add(id);

		diagram.getBeEntities().add(be);

		StringWriter writer = new StringWriter();
		try {
			EntityUtil.marshaller(diagram, writer);
			System.out.println(writer.toString());
		} catch (JAXBException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
