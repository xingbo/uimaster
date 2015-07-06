package org.shaolin.uimaster.designtime.entity;

import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;

public final class EntityUtil {

	public static <T> T unmarshaller(Class<T> entityType, InputStream in)
			throws JAXBException {
		Unmarshaller unmarshaller = createUnmarshaller(entityType);
		return (T) unmarshaller.unmarshal(in);
	}
	
	public static <T> T unmarshaller(Class<T> entityType, Reader reader)
			throws JAXBException {
		Unmarshaller unmarshaller = createUnmarshaller(entityType);
		return (T) unmarshaller.unmarshal(reader);
	}

	public static void marshaller(Object entity, Writer writer)
			throws JAXBException {
//		marshaller.marshal(entity, writer);
		
	    Marshaller marshaller = createMarshaller(entity.getClass());
		XMLOutputFactory xof = XMLOutputFactory.newInstance();
		CDataXMLStreamWriter cdataStreamWriter = null;
		try {
			XMLStreamWriter streamWriter = new IndentingXMLStreamWriter(xof.createXMLStreamWriter(writer));
			cdataStreamWriter = new CDataXMLStreamWriter(streamWriter);
			marshaller.marshal(entity, cdataStreamWriter);
		} catch (XMLStreamException e) {
			throw new JAXBException(e.getMessage(), e);
		} finally {
			if (cdataStreamWriter != null) {
				try {
					cdataStreamWriter.flush();
					cdataStreamWriter.close();
				} catch (XMLStreamException e) {
				}
			}
		}
		
	}

	public static Unmarshaller createUnmarshaller(Class<?> clazz)
			throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		return jaxbContext.createUnmarshaller();
	}

	public static Marshaller createMarshaller(Class<?> clazz)
			throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "");
		return marshaller;
	}
	
}
