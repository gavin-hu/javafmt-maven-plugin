package maven.plugin.javafmt;

/*
 * #%L
 * Java Formatter Maven Plugin
 * %%
 * Copyright (C) 2013 FocusSNS
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    private void loadStyle(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException,
            XPathExpressionException {
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
