package maven.plugin.javafmt;

/*
 * #%L
 * Java Formatter Maven Plugin
 * %%
 * Copyright (C) 2013 FocusSNS
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@SuppressWarnings("serial")
public class JavaCodeStyle extends HashMap<String, String> {
    //
    private XPathFactory xPathFactory = XPathFactory.newInstance();

    public JavaCodeStyle(Map<String, String> defaultOptions, InputStream inputStream) throws Exception {
        //
        super(defaultOptions);
        //
        loadStyle(inputStream);
    }

    private void loadStyle(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        XPath xPath = xPathFactory.newXPath();
        XPathExpression expression = xPath.compile("//profile/setting");
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
        NodeList nodeList = (NodeList) expression.evaluate(doc, XPathConstants.NODESET);
        //
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                String id = element.getAttribute("id");
                String value = element.getAttribute("value");
                //
                super.put(id, value);
            }
        }
    }

}
