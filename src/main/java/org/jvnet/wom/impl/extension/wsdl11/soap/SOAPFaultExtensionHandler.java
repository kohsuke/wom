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

package org.jvnet.wom.impl.extension.wsdl11.soap;

import org.jvnet.wom.api.WSDLExtension;
import org.jvnet.wom.api.binding.wsdl11.soap.SOAP11Constants;
import org.jvnet.wom.api.binding.wsdl11.soap.SOAP12Constants;
import org.jvnet.wom.api.binding.wsdl11.soap.SOAPBody;
import org.jvnet.wom.impl.extension.Messages;
import org.jvnet.wom.impl.extension.wsdl11.AbstractWSDLExtensionHandler;
import org.jvnet.wom.impl.util.XmlUtil;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Vivek Pandey
 */
public class SOAPFaultExtensionHandler extends AbstractWSDLExtensionHandler {
    SOAPFaultImpl fault;
    private final ContentHandler contentHandler = new SOAPFaultCH();
    private final QName names[] = new QName[]{SOAP11Constants.SOAPFAULT_NAME, SOAP12Constants.SOAPFAULT_NAME};

    public SOAPFaultExtensionHandler(ErrorHandler errorHandler, EntityResolver entityResolver) {
        super(errorHandler, entityResolver);
    }

    public Collection<WSDLExtension> getExtensions() {
        return Collections.<WSDLExtension>singleton(fault);
    }

    protected QName[] getExtensionNames() {
        return names;
    }

    public Collection<WSDLExtension> parseAttribute(Attributes atts) {
        return null;
    }

    public ContentHandler getContentHandlerFor(String nsUri, String localName) {
        if(canHandle(nsUri, localName))
            return contentHandler;
        return null;
    }

    private class SOAPFaultCH extends WSDLExtensibilityContentHandler{
        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            if(!canHandle(uri, localName))
                return;

            String encodingStyleAtt = atts.getValue("encodingStyle");
            String[] encodingStyle = null;
            if(encodingStyleAtt != null){
                encodingStyle = encodingStyleAtt.split("\\s");
            }

            String namespace = atts.getValue("namespace");
            String useatt = XmlUtil.fixNull(atts.getValue("use")).trim();
            SOAPBody.Use use = SOAPBody.Use.literal;
            if(useatt.equals("encoded")){
                use = SOAPBody.Use.encoded;
            }else if(!useatt.equals("literal")){
                errorHandler.error(new SAXParseException(Messages.format(Messages.INVALID_ATTR, "use", useatt, "literal or encoded"), locator));
            }

            String name = atts.getValue("name");
            if(name == null){
                errorHandler.error(new SAXParseException(Messages.format(Messages.MISSING_ATTR, "name", "soap:fault"), locator));
            }
            fault = new SOAPFaultImpl(new QName(uri, localName));
            fault.setEncodingStyle(encodingStyle);
            fault.setNamespace(namespace);
            fault.setName(name);
            fault.setUse(use);
        }
    }
}
