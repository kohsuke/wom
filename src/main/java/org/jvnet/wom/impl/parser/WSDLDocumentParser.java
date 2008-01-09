/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package org.jvnet.wom.impl.parser;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

/**
 * @author Vivek Pandey
 */
public class WSDLDocumentParser implements ContentHandler {


    private final TransformerHandler transformerHandler;
    private final ByteArrayOutputStream bos;

    boolean isDone = false;
    private Locator locator;

    private static final TransformerFactory transformerFactory = ((SAXTransformerFactory) TransformerFactory.newInstance());


    public String getDocumentation() {
        return bos.toString();
    }

    public WSDLDocumentParser() throws SAXParseException {
        this.bos = new ByteArrayOutputStream();
        try {
            transformerHandler = ((SAXTransformerFactory) transformerFactory).newTransformerHandler();
            Properties prop = new Properties();
            prop.put(OutputKeys.OMIT_XML_DECLARATION, "yes");
            this.transformerHandler.getTransformer().setOutputProperties(prop);
            this.transformerHandler.setResult(new StreamResult(bos));            
        } catch (TransformerConfigurationException e) {
            throw new SAXParseException(e.getMessage(), locator, e);
        }
    }


    public void setDocumentLocator(Locator locator) {
        locator = locator;
        transformerHandler.setDocumentLocator(locator);
    }

    public void startDocument() throws SAXException {
        transformerHandler.startDocument();
    }

    public void endDocument() throws SAXException {
        transformerHandler.endDocument();
        isDone = true;
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        transformerHandler.startPrefixMapping(prefix, uri);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        transformerHandler.endPrefixMapping(prefix);
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (uri.equals("http://schemas.xmlsoap.org/wsdl/") && localName.equals("documentation"))
            return;
        transformerHandler.startElement(uri, localName, qName, atts);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (uri.equals("http://schemas.xmlsoap.org/wsdl/") && localName.equals("documentation")){
            return;
        }
        transformerHandler.endElement(uri, localName, qName);
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        transformerHandler.characters(ch, start, length);
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        transformerHandler.ignorableWhitespace(ch, start, length);
    }

    public void processingInstruction(String target, String data) throws SAXException {
        //transformerHandler.processingInstruction(target, data);
    }

    public void skippedEntity(String name) throws SAXException {
        transformerHandler.skippedEntity(name);
    }
}
