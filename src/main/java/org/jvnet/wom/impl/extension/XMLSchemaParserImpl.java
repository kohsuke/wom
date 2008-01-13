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

import org.jvnet.wom.api.parser.XMLSchemaParser;
import org.jvnet.wom.api.WSDLExtension;
import org.jvnet.wom.Schema;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.XMLFilterImpl;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.Collections;
import java.io.IOException;

import com.sun.xml.xsom.parser.XSOMParser;
import com.sun.xml.xsom.parser.XMLParser;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.XSSchemaSet;

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

    public XMLSchemaParserImpl(ErrorHandler errorHandler, EntityResolver entityResolver) throws SAXException {
        this.errorHandler = errorHandler;
        this.parser = new XSOMParser();
        parser.setErrorHandler(errorHandler);
        parser.setEntityResolver(entityResolver);
        xsomParser = parser.getParserHandler();
    }
   

    public Schema getSchema() {
        return schemaExtension;
    }

    public void freeze() throws SAXException {
        schemas = parser.getResult();
        schemaExtension = new XMLSchema();
    }

    public Collection<WSDLExtension> getExtension() {
        return Collections.<WSDLExtension>singleton(schemaExtension);
    }

    public Collection<WSDLExtension> parseAttribute(Attributes atts) {
        return null;
    }

    public ContentHandler getContentHandlerFor(String nsUri, String localName) {
        return xsomParser;
    }
}
