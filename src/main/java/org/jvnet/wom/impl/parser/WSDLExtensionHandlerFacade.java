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

import org.jvnet.wom.api.WSDLExtension;
import org.jvnet.wom.api.WSDLEntity;
import org.jvnet.wom.api.parser.WSDLExtensionHandler;
import org.jvnet.wom.api.parser.Receiver;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;


/**
 * @author Vivek Pandey
 */
public final class WSDLExtensionHandlerFacade implements WSDLExtensionHandler {

    private final WSDLExtensionHandler[] extensionHandlers;

    private final Set<WSDLExtension> extensions
    public WSDLExtensionHandlerFacade(WSDLExtensionHandler[] extensionHandlers) {
        this.extensionHandlers = extensionHandlers;
    }

    public boolean isSupported(QName extensibilityElement) {
        return true;
    }

    public Collection<WSDLExtension> getExtension() {
        return null;
    }

    public QName extensibilityName() {
        return null;
    }

    public ContentHandler getContentHandler() {
        return facade;
    }

    public Collection<WSDLExtension> parseAttribute(Attributes atts, Receiver<WSDLExtension> receiver) {
        List<WSDLExtension> extns = new ArrayList<WSDLExtension>();
        for(WSDLExtensionHandler wsdlExtensionHandler: extensionHandlers){
            for(WSDLExtension ext : wsdlExtensionHandler.parseAttribute(atts)){
                receiver.add(ext);
            }
            extns.addAll(wsdlExtensionHandler.parseAttribute(atts));
        }

        return Collections.unmodifiableCollection(extns);
    }

    public ContentHandler getContentHandlerFor(String nsUri, String localName) {
        return null;
    }

    private final ContentHandler facade = new WSDLContentHandlerFacade();
    private class WSDLContentHandlerFacade implements ContentHandler{
        public void setDocumentLocator(Locator locator) {
        for(WSDLExtensionHandler wsdlExtensionHandler: extensionHandlers){
            wsdlExtensionHandler.getContentHandler().setDocumentLocator(locator);
        }
    }

    public void startDocument() throws SAXException {
        for(WSDLExtensionHandler wsdlExtensionHandler: extensionHandlers){
            wsdlExtensionHandler.getContentHandler().startDocument();
        }
    }

    public void endDocument() throws SAXException {
        for(WSDLExtensionHandler wsdlExtensionHandler: extensionHandlers){
            wsdlExtensionHandler.getContentHandler().endDocument();
        }

    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        for(WSDLExtensionHandler wsdlExtensionHandler: extensionHandlers){
            wsdlExtensionHandler.getContentHandler().startPrefixMapping(prefix, uri);
        }
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        for(WSDLExtensionHandler wsdlExtensionHandler: extensionHandlers){
            wsdlExtensionHandler.getContentHandler().endPrefixMapping(prefix);
        }
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        for(WSDLExtensionHandler wsdlExtensionHandler: extensionHandlers){
            wsdlExtensionHandler.getContentHandler().startElement(uri, localName, qName, atts);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        for(WSDLExtensionHandler wsdlExtensionHandler: extensionHandlers){
            wsdlExtensionHandler.getContentHandler().endElement(uri, localName, qName);
        }
    }

    public void characters(char ch[], int start, int length) throws SAXException {
        for(WSDLExtensionHandler wsdlExtensionHandler: extensionHandlers){
            wsdlExtensionHandler.getContentHandler().characters(ch, start, length);
        }
    }

    public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {
        for(WSDLExtensionHandler wsdlExtensionHandler: extensionHandlers){
            wsdlExtensionHandler.getContentHandler().ignorableWhitespace(ch, start, length);
        }
    }

    public void processingInstruction(String target, String data) throws SAXException {
        //NOOP
//        for(WSDLExtensionHandler wsdlExtensionHandler:extensionHandlers){
//            wsdlExtensionHandler.getContentHandler().processingInstruction(target, data);
//        }
    }

    public void skippedEntity(String name) throws SAXException {
        for(WSDLExtensionHandler wsdlExtensionHandler: extensionHandlers){
            wsdlExtensionHandler.getContentHandler().skippedEntity(name);
        }
    }
    }
}
