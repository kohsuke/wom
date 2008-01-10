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

package parsing;

import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.api.XJC;
import org.jvnet.wom.WSDLExtension;
import org.jvnet.wom.parser.AbstractWSDLExtensionHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;

/**
 * @author Vivek Pandey
 */
public class XMLSchemaExtensionHandler extends AbstractWSDLExtensionHandler {
    private XMLSchema schema = new XMLSchema();

    private final ContentHandler delegatingHandler = new XMLSchemaCH();

    /**
     * Obtained from SchemaCompiler,latter on during parsing
     */
    private ContentHandler schemaHandler;

    private final SchemaCompiler schemaCompiler = XJC.createSchemaCompiler();

    public XMLSchemaExtensionHandler(ErrorHandler errorHandler, EntityResolver entityResolver) {
        super(errorHandler, entityResolver);
    }

    public WSDLExtension getExtension() {
        return schema;
    }

    public S2JJAXBModel bind(){
        return schemaCompiler.bind();
    }
    public QName extensibilityName() {
        return XMLSchema.XMLSCHEMA_NAME;
    }

    public ContentHandler getContentHandler() {
        return delegatingHandler;
    }

    public ContentHandler getContentHandler(String systemId) {
        return schemaCompiler.getParserHandler(systemId);
    }

    private class XMLSchemaCH implements ContentHandler{
        private int counter=0;

        /** This method must be called first by WOM parser otherwise there is no way to get ContentHandler from
         *  SchemaCompiler
         */
        public void setDocumentLocator(Locator locator) {
            schemaHandler = schemaCompiler.getParserHandler(locator.getSystemId()+"#types?schema"+counter++);
            schemaHandler.setDocumentLocator(locator);
        }

        public void startDocument() throws SAXException {
            schemaHandler.startDocument();
        }

        public void endDocument() throws SAXException {
            schemaHandler.endDocument();
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            schemaHandler.startPrefixMapping(prefix, uri);
        }

        public void endPrefixMapping(String prefix) throws SAXException {
            schemaHandler.endPrefixMapping(prefix);
        }

        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            schemaHandler.startElement(uri, localName, qName, atts);
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            schemaHandler.endElement(uri, localName, qName);
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            schemaHandler.characters(ch, start, length);
        }

        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            schemaHandler.ignorableWhitespace(ch, start, length);
        }

        public void processingInstruction(String target, String data) throws SAXException {
            schemaHandler.processingInstruction(target, data);
        }

        public void skippedEntity(String name) throws SAXException {
            schemaHandler.skippedEntity(name);
        }

    }

}
