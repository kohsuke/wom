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

package org.jvnet.wom.impl.parser.handler;

import org.jvnet.wom.api.parser.WSDLEventSource;
import org.jvnet.wom.api.parser.WSDLExtensionHandler;
import org.jvnet.wom.impl.parser.WSDLContentHandlerEx;
import org.jvnet.wom.impl.parser.WSDLTypesImpl;
import org.jvnet.wom.impl.extension.XMLSchemaParserImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;

import javax.xml.namespace.QName;

/**
 * @author Vivek Pandey
 */
public class Types extends AbstractHandler{
    WSDLTypesImpl types;
    private WSDLContentHandlerEx runtime;
    private String expectedNamespace;

    public Types(AbstractHandler parent, WSDLEventSource source, WSDLContentHandlerEx runtime, int cookie, String expectedNamespace) {
        super(source, parent, cookie);
        this.runtime = runtime;
        this.expectedNamespace = expectedNamespace;
    }
    protected Types(WSDLEventSource source, AbstractHandler parent, int parentCookie) {
        super(source, parent, parentCookie);
    }

    protected WSDLContentHandlerEx getRuntime() {
        return runtime;
    }

    protected void onChildCompleted(Object result, int cookie, boolean needAttCheck) throws SAXException {

    }

    @Override
    public void enterElement(String uri, String localName, String qname, Attributes atts) throws SAXException {
        if (uri.equals(WSDL_NS) && localName.equals("types")) {
            types = new WSDLTypesImpl(runtime.getLocator(), new QName(runtime.currentWSDL.getTargetNamespace(), ""), runtime.document);
        }else{//schema extensibility element
            boolean noCh = true;
            getRuntime().onEnterElementConsumed(uri, localName, qname, atts);
            for(WSDLExtensionHandler extensionHandler:getRuntime().parser.getWSDLExtensionHandlers()){
                ContentHandler ch = extensionHandler.getContentHandlerFor(uri, localName);
                if(ch == null)
                    continue;
                getRuntime().redirectSubtree(ch, uri, localName, qname);
                noCh = false;
                handlers.add(extensionHandler);
            }

            //Lets check if there is no XML schema handlers
            if(noCh && uri.equals("http://www.w3.org/2001/XMLSchema") &&
                    localName.equals("schema")){
                //let us try the default XSOM
                XMLSchemaParserImpl xsomParser = new XMLSchemaParserImpl(runtime.getErrorHandler(), runtime.parser.getEntityResolver());
                ContentHandler ch = xsomParser.getContentHandlerFor(uri, localName);
                assert ch != null;
                getRuntime().redirectSubtree(xsomParser.getContentHandlerFor(uri, localName), uri, localName, qname);
                handlers.add(xsomParser);
            }
        }
    }

    @Override
    public void leaveElement(String uri, String localName, String qname) throws SAXException {
        if (uri.equals(WSDL_NS) && localName.equals("types")) {
            endProcessingExtentionElement(types);
            revertToParentFromLeaveElement(types, _cookie, uri, localName, qname);
        }
    }
}
