package org.openmrs.module;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ModuleConfigEntityResolver implements EntityResolver {

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		System.out.println("public: " + publicId);
		System.out.println("system: " + systemId);
		Reader r = new FileReader(getClass().getResource("dtd/config-1.1.dtd").getFile());
		return new InputSource(r);
	}
}
