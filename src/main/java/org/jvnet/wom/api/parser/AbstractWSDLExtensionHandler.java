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
package org.jvnet.wom.api.parser;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;
import org.jvnet.wom.api.parser.WSDLExtensionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Any WSDL extensibility handler should extend from this abstract class.
 *
 * @author Vivek Pandey
 */
public abstract class AbstractWSDLExtensionHandler implements WSDLExtensionHandler {
    protected final ErrorHandler errorHandler;
    protected final EntityResolver entityResolver;

    protected AbstractWSDLExtensionHandler(ErrorHandler errorHandler, EntityResolver entityResolver) {
        this.errorHandler = errorHandler;
        this.entityResolver = entityResolver;
    }


    protected abstract class WSDLExtensibilityContentHandler implements ContentHandler{
        protected Locator locator = new LocatorImpl();
        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
        }

        public void startDocument() throws SAXException {

        }

        public void endDocument() throws SAXException {

        }

        List<String> namespaces = new ArrayList<String>();
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            namespaces.add(prefix);
            namespaces.add(uri);
        }

        public void endPrefixMapping(String prefix) throws SAXException {
            namespaces.remove(namespaces.size() - 1);
            namespaces.remove(namespaces.size() - 1);
        }

        public String resolveNamespacePrefix(String prefix) {
            for (int i = namespaces.size() - 2; i >= 0; i -= 2)
                if (namespaces.get(i).equals(prefix))
                    return namespaces.get(i + 1);

            // no binding was found.
            if (prefix.equals("")) return "";  // return the default no-namespace
            if (prefix.equals("xml"))    // pre-defined xml prefix
                return "http://www.w3.org/XML/1998/namespace";
            else return null;    // prefix undefined
        }

        

        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

        }

        public void endElement(String uri, String localName, String qName) throws SAXException {

        }

        public void characters(char ch[], int start, int length) throws SAXException {

        }

        public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {

        }

        public void processingInstruction(String target, String data) throws SAXException {

        }

        public void skippedEntity(String name) throws SAXException {

        }
    }

}
