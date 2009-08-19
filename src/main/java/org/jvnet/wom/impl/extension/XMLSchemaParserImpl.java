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

package org.jvnet.wom.impl.extension;

import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.parser.XSOMParser;
import org.jvnet.wom.Schema;
import org.jvnet.wom.api.WSDLExtension;
import org.jvnet.wom.api.parser.XMLSchemaParser;
import org.xml.sax.*;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Vivek Pandey
 */
public class XMLSchemaParserImpl implements XMLSchemaParser {
    private final ErrorHandler errorHandler;
    private final ContentHandler xsomParser;
    private final XSOMParser parser;
    private XSSchemaSet schemas;
    private XMLSchema schemaExtension;


    private class XMLSchema implements Schema<XSElementDecl, XSType>{

        public QName getName() {
            return new QName("http://www.w3.org/2001/XMLSchema", "schema");
        }

        public XSElementDecl resolveElement(QName elementName) {
            return schemas.getElementDecl(elementName.getNamespaceURI(), elementName.getLocalPart());
        }

        public XSType resolveType(QName typeName) {
            return schemas.getType(typeName.getNamespaceURI(), typeName.getLocalPart());
        }
    }

    private final Set<ContentHandler> contentHandlers = new HashSet<ContentHandler>();

    public XMLSchemaParserImpl(ErrorHandler errorHandler, EntityResolver entityResolver, ContentHandler ch) throws SAXException {
        this.errorHandler = errorHandler;
        this.parser = new XSOMParser();
        if(ch != null){
            this.contentHandlers.add(ch);
        }
        contentHandlers.add(parser.getParserHandler());
        parser.setErrorHandler(errorHandler);
        parser.setEntityResolver(entityResolver);
        this.xsomParser = new ContentHandlerExt();
    }

    public Schema getSchema() {
        return schemaExtension;
    }

    public void freeze() throws SAXException {
        schemas = parser.getResult();
        schemaExtension = new XMLSchema();
    }

    public Collection<WSDLExtension> getExtensions() {
        return Collections.<WSDLExtension>singleton(schemaExtension);
    }

    public Collection<WSDLExtension> parseAttribute(Attributes atts) {
        return null;
    }

    public ContentHandler getContentHandlerFor(String nsUri, String localName) {
        return xsomParser;
    }

    private class ContentHandlerExt implements ContentHandler{

        public void setDocumentLocator(Locator locator) {
            for(ContentHandler contentHandler:contentHandlers){
                contentHandler.setDocumentLocator(locator);
            }
        }

        public void startDocument() throws SAXException {
            for(ContentHandler contentHandler:contentHandlers){
                contentHandler.startDocument();
            }
        }

        public void endDocument() throws SAXException {
            for(ContentHandler contentHandler:contentHandlers){
                contentHandler.endDocument();
            }
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            for(ContentHandler contentHandler:contentHandlers){
                contentHandler.startPrefixMapping(prefix, uri);
            }
        }

        public void endPrefixMapping(String prefix) throws SAXException {
            for(ContentHandler contentHandler:contentHandlers){
                contentHandler.endPrefixMapping(prefix);
            }
        }

        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            for(ContentHandler contentHandler:contentHandlers){
                contentHandler.startElement(uri, localName, qName, atts);
            }
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            for(ContentHandler contentHandler:contentHandlers){
                contentHandler.endElement(uri, localName, qName);
            }
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            for(ContentHandler contentHandler:contentHandlers){
                contentHandler.characters(ch, start, length);
            }
        }

        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            for(ContentHandler contentHandler:contentHandlers){
                contentHandler.ignorableWhitespace(ch, start, length);
            }
        }

        public void processingInstruction(String target, String data) throws SAXException {
            for(ContentHandler contentHandler:contentHandlers){
                contentHandler.processingInstruction(target, data);
            }
        }

        public void skippedEntity(String name) throws SAXException {
            for(ContentHandler contentHandler:contentHandlers){
                contentHandler.skippedEntity(name);
            }
        }
    }
}
