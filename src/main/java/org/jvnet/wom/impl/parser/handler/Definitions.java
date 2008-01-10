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

import org.jvnet.wom.impl.WSDLBoundPortTypeImpl;
import org.jvnet.wom.impl.WSDLMessageImpl;
import org.jvnet.wom.impl.WSDLPortTypeImpl;
import org.jvnet.wom.impl.WSDLServiceImpl;
import org.jvnet.wom.impl.parser.Messages;
import org.jvnet.wom.impl.parser.WSDLContentHandlerEx;
import org.jvnet.wom.impl.parser.WSDLTypesImpl;
import org.jvnet.wom.impl.util.XmlUtil;
import org.jvnet.wom.parser.WSDLEventSource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class Definitions extends AbstractHandler {
    private int state;
    private final WSDLContentHandlerEx runtime;
    private final String expectedNamespace;


    public Definitions(AbstractHandler parent, WSDLEventSource source, WSDLContentHandlerEx runtime, int cookie, String expectedNamespace) {
        super(source, parent, cookie);
        this.runtime = runtime;
        this.expectedNamespace = expectedNamespace;
        state = 1;
    }

    public Definitions(WSDLContentHandlerEx runtime, String _expectedNamespace) {
        this(null, runtime, runtime, -1, _expectedNamespace);
    }

    protected WSDLContentHandlerEx getRuntime() {
        return runtime;
    }

    protected void onChildCompleted(Object result, int cookie, boolean needAttCheck) throws SAXException {
        switch (cookie) {
            case 50:
                WSDLMessageImpl message = (WSDLMessageImpl) result;
                runtime.currentWSDL.addMessage(message);
                break;
            case 60:
                runtime.currentWSDL.addPortType((WSDLPortTypeImpl) result);
                break;
            case 70:
                runtime.currentWSDL.addBoundPortType((WSDLBoundPortTypeImpl) result);
                break;
            case 80:
                runtime.currentWSDL.addService((WSDLServiceImpl) result);
                break;
            case 90:
                runtime.currentWSDL.setTypes((WSDLTypesImpl) result);
                break;
        }
    }

    private void readDefinitionQName() throws SAXException {
        Attributes test = runtime.getCurrentAttributes();
        String tns = XmlUtil.fixNull(test.getValue("targetNamespace"));
        String name = XmlUtil.fixNull(test.getValue("name"));

        // importing
        runtime.currentWSDL = runtime.parser.wsdlSet.createWSDLDefinitions(name, tns, runtime.copyLocator());
        if (expectedNamespace != null && !expectedNamespace.equals(tns)) {
            runtime.reportError(
                    Messages.format("UnexpectedTargetnamespace.Import", tns, expectedNamespace, tns),
                    runtime.getLocator());
        }

        // multiple inclusion test.
        if (runtime.hasAlreadyBeenRead()) {
            // skip this document
            runtime.redirectSubtree(new DefaultHandler(), "", "", "");
            return;
        }
    }


    public void enterElement(String uri, String localName, String qname, Attributes atts) throws SAXException {

        switch (state) {
            case 1://definitions
                if (uri.equals(WSDL_NS) && localName.equals("definitions")) {
                    runtime.onEnterElementConsumed(uri, localName, qname, atts);
                    readDefinitionQName();
                    state = 2;
                }
                break;
            case 2: //children of wsdl:definitions
                 if (uri.equals(WSDL_NS) && localName.equals("types")) {
                    Types types = new Types(this, _source, runtime, 90, expectedNamespace);
                    spawnChildFromEnterElement(types, uri, localName, qname, atts);
                } else if (uri.equals(WSDL_NS) && localName.equals("message")) {
                    Message message = new Message(this, _source, runtime, 50, expectedNamespace);
                    spawnChildFromEnterElement(message, uri, localName, qname, atts);
                } else if (uri.equals(WSDL_NS) && localName.equals("portType")) {
                    PortType portType = new PortType(this, _source, runtime, 60, expectedNamespace);
                    spawnChildFromEnterElement(portType, uri, localName, qname, atts);
                } else if (uri.equals(WSDL_NS) && localName.equals("binding")) {
                    BoundPortType boundPortType = new BoundPortType(this, _source, runtime, 70, expectedNamespace);
                    spawnChildFromEnterElement(boundPortType, uri, localName, qname, atts);
                } else if (uri.equals(WSDL_NS) && localName.equals("service")) {
                    Service service = new Service(this, _source, runtime, 80, expectedNamespace);
                    spawnChildFromEnterElement(service, uri, localName, qname, atts);
                } else if (uri.equals(WSDL_NS) && localName.equals("import")) {
                    String namespace = atts.getValue("namespace");
                    if(namespace == null){
                        runtime.getErrorHandler().error(new SAXParseException(org.jvnet.wom.impl.parser.handler.Messages.format(org.jvnet.wom.impl.parser.handler.Messages.MISSING_ATTRIBUTE, "namespace", "wsdl:import"), runtime.getLocator()));
                    }
                    String location = atts.getValue("location");
                    if(location == null){
                        runtime.getErrorHandler().error(new SAXParseException(org.jvnet.wom.impl.parser.handler.Messages.format(org.jvnet.wom.impl.parser.handler.Messages.MISSING_ATTRIBUTE, "location", "wsdl:import"), runtime.getLocator()));
                    }
                    if(location != null && namespace != null){                        
                        runtime.importWSDL(namespace, location);
                    }

                }else{
                    super.enterElement(uri, localName, qname, atts);
                }
                break;
            default: //unknown, it might be extensibility element
                //TODO: log a message
                break;


        }

    }

    public void leaveElement(String uri, String localName, String qname) throws SAXException {
        if (uri.equals(WSDL_NS) && localName.equals("definitions")) {
            endProcessingExtentionElement(runtime.currentWSDL);
            runtime.currentWSDL.setDocumentation(getWSDLDocumentation());
        }
    }

    public void text(String value) throws SAXException {

    }

    public void enterAttribute(String uri, String localName, String qname) throws SAXException {

    }

    public void leaveAttribute(String uri, String localName, String qname) throws SAXException {

    }
}
