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

import org.jvnet.wom.impl.WSDLOperationImpl;
import org.jvnet.wom.impl.WSDLPortTypeImpl;
import org.jvnet.wom.impl.parser.WSDLContentHandlerEx;
import org.jvnet.wom.parser.WSDLEventSource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.namespace.QName;

/**
 * @author Vivek Pandey
 */
public class PortType extends AbstractHandler {
    private WSDLPortTypeImpl portType;
    private WSDLContentHandlerEx runtime;
    private String expectedNS;
    private int state;

    protected PortType(WSDLEventSource source, AbstractHandler parent, int parentCookie) {
        super(source, parent, parentCookie);
    }

    public PortType(AbstractHandler parent, WSDLEventSource source, WSDLContentHandlerEx runtime, int cookie, String expectedNamespace) {
        super(source, parent, cookie);
        this.runtime = runtime;
        this.expectedNS = expectedNamespace;
        state = 1;
    }


    protected WSDLContentHandler getRuntime() {
        return runtime;
    }

    protected void onChildCompleted(Object result, int cookie, boolean needAttCheck) throws SAXException {
        switch (cookie) {
            case 61:
                portType.addOperation((WSDLOperationImpl) result);
                break;
        }
    }

    public void enterElement(String uri, String localName, String qname, Attributes atts) throws SAXException {
        switch (state) {
            case 1:
                if (WSDL_NS.equals(uri) && localName.equals("portType")) {
                    runtime.onEnterElementConsumed(uri, localName, qname, atts);
                    Attributes test = runtime.getCurrentAttributes();
                    processAttributes(test);
                } else if (WSDL_NS.equals(uri) && localName.equals("documentation")) {
                    String doc = processDocumentation(uri, localName, qname);
                    runtime.onEnterElementConsumed(uri, localName, qname, atts);
                    portType.setDocumentation(doc);
                } else if (WSDL_NS.equals(uri) && localName.equals("operation")) {
                    Operation operation = new Operation(this, _source, runtime, 61, expectedNS);
                    spawnChildFromEnterElement(operation, uri, localName, qname, atts);
                }
                break;
        }
    }

    public void leaveElement(String uri, String localName, String qname) throws SAXException {
        switch (state) {
            case 1:
                if (WSDL_NS.equals(uri) && localName.equals("portType")) {
                    revertToParentFromLeaveElement(portType, _cookie, uri, localName, qname);
                }
        }
    }

    public void text(String value) throws SAXException {

    }

    public void enterAttribute(String uri, String localName, String qname) throws SAXException {

    }

    public void leaveAttribute(String uri, String localName, String qname) throws SAXException {

    }

    private void processAttributes(Attributes test) throws SAXException {
        int[] validattrs = new int[test.getLength()];
        String name = fixNull(test.getValue("name"));
        if (name.equals("")) {
            runtime.getErrorHandler().warning(new SAXParseException(Messages.format(Messages.MISSING_NAME, "wsdl:portType", name), runtime.getLocator()));
        }
        int index = test.getIndex("name");
        if (index >= 0)
            validattrs[index] = 1;

        portType = new WSDLPortTypeImpl(runtime.getLocator(), new QName(runtime.currentWSDL.getName().getNamespaceURI(), name));
        portType.setOwner(runtime.currentWSDL);

        validateAttribute(runtime.getErrorHandler(), test, validattrs);
    }

}
