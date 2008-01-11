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
package org.jvnet.wom.impl;

import org.jvnet.wom.api.WSDLBoundPortType;
import org.jvnet.wom.api.WSDLDefinitions;
import org.jvnet.wom.api.WSDLMessage;
import org.jvnet.wom.api.WSDLService;
import org.jvnet.wom.api.WSDLVisitor;
import org.jvnet.wom.impl.parser.WSDLDocumentImpl;
import org.jvnet.wom.impl.parser.WSDLSetImpl;
import org.jvnet.wom.impl.parser.WSDLTypesImpl;
import org.jvnet.wom.impl.util.QNameMap;
import org.xml.sax.Locator;

import javax.xml.namespace.QName;

public class WSDLDefinitionsImpl extends WSDLDefinitions {

    private final WSDLSetImpl parent;

    private String documentation = "";
    private final QNameMap<WSDLMessageImpl> messages = new QNameMap<WSDLMessageImpl>();
    private final QNameMap<WSDLPortTypeImpl> portTypes = new QNameMap<WSDLPortTypeImpl>();
    private final QNameMap<WSDLBoundPortTypeImpl> bindings = new QNameMap<WSDLBoundPortTypeImpl>();
    private final QNameMap<WSDLServiceImpl> services = new QNameMap<WSDLServiceImpl>();
    private WSDLTypesImpl types;


    public WSDLDefinitionsImpl(WSDLSetImpl parent, Locator locator, QName name) {
        super(locator, name);
        this.parent = parent;
    }

    public String getTargetNamespace() {
        return getName().getNamespaceURI();
    }

    public WSDLPortTypeImpl getPortType(QName name) {
        return portTypes.get(name);
    }

    public void addPortType(WSDLPortTypeImpl portType) {
        portTypes.put(portType.getName(), portType);
    }

    public WSDLBoundPortType getBinding(QName name) {
        return bindings.get(name);
    }

    public void addBoundPortType(WSDLBoundPortTypeImpl binding) {
        bindings.put(binding.getName(), binding);
    }

    public WSDLBoundPortType getBinding(QName serviceName, QName portName) {
        WSDLServiceImpl service = services.get(serviceName);
        if (service == null)
            return null;
        WSDLPortImpl port = service.get(portName);
        return port.getBinding();
    }

    public void addService(WSDLServiceImpl service){
        services.put(service.getName(), service);
    }

    public WSDLService getService(QName name) {
        return services.get(name);
    }

    public WSDLMessageImpl getMessage(QName name) {
        return messages.get(name);
    }

    public Iterable<? extends WSDLMessage> getMessages() {
        return messages.values();
    }

    public Iterable<WSDLPortTypeImpl> getPortTypes() {
        return portTypes.values();
    }

    public Iterable<WSDLBoundPortTypeImpl> getBindings() {
        return bindings.values();
    }

    public Iterable<WSDLServiceImpl> getServices() {
        return services.values();
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public void addMessage(WSDLMessageImpl message) {
        messages.put(message.getName(), message);
    }

    public void setOwnerDocument(WSDLDocumentImpl owner) {
        setOwnerWSDLDocument(owner);
    }

    public <V> V visit(WSDLVisitor<V> visitor) {
        return visitor.definitions(this);
    }

    public void setTypes(WSDLTypesImpl wsdlTypes) {
        this.types = types;
    }
}
