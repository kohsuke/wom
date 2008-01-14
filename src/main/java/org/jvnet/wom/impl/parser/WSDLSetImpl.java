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

import org.jvnet.wom.api.WSDLDefinitions;
import org.jvnet.wom.api.WSDLSet;
import org.jvnet.wom.api.WSDLService;
import org.jvnet.wom.api.WSDLBoundPortType;
import org.jvnet.wom.api.WSDLPortType;
import org.jvnet.wom.api.WSDLMessage;
import org.jvnet.wom.api.WSDLTypes;
import org.jvnet.wom.impl.WSDLDefinitionsImpl;
import org.jvnet.wom.impl.util.Iterators;
import org.xml.sax.Locator;

import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class WSDLSetImpl implements WSDLSet {

    private final Map<String, WSDLDefinitionsImpl> wsdlMap = new HashMap<String, WSDLDefinitionsImpl>();
    private final List<WSDLDefinitionsImpl> wsdls = new ArrayList<WSDLDefinitionsImpl>();
    /**
     * Gets a reference to the existing schema or creates a new one
     * if none exists yet.
     */
    public WSDLDefinitionsImpl createWSDLDefinitions(String name, String targetNamespace, Locator location) {
        WSDLDefinitionsImpl wsdl = wsdlMap.get(targetNamespace);
        if (wsdl == null) {
            wsdl = new WSDLDefinitionsImpl(this, location, new QName(targetNamespace, name));
            wsdlMap.put(targetNamespace, wsdl);
            wsdls.add(wsdl);
        }
        return wsdl;
    }

    void add(WSDLDefinitionsImpl wsdl) {
        wsdlMap.put(wsdl.getWSDLDocument().getTargetNamespace(), wsdl);
    }

    public WSDLDefinitionsImpl getWSDL(String targetNamespace) {
        return wsdlMap.get(targetNamespace);
    }

    public Iterator<WSDLDefinitions> getWSDLs() {
        return Collections.<WSDLDefinitions>unmodifiableCollection(wsdlMap.values()).iterator();
    }

    public Iterator<WSDLService> services() {
        return new Iterators.Map<WSDLService,WSDLDefinitions>(getWSDLs()) {
            protected Iterator<WSDLService> apply(WSDLDefinitions u) {
                return u.getServices().iterator();
            }
        };
    }

    public Iterator<WSDLBoundPortType> boundPortTypes() {
        return new Iterators.Map<WSDLBoundPortType,WSDLDefinitions>(getWSDLs()) {
            protected Iterator<WSDLBoundPortType> apply(WSDLDefinitions u) {
                return u.getBindings().iterator();
            }
        };
    }

    public Iterator<WSDLPortType> portTypes() {
        return new Iterators.Map<WSDLPortType,WSDLDefinitions>(getWSDLs()) {
            protected Iterator<WSDLPortType> apply(WSDLDefinitions u) {
                return u.getPortTypes().iterator();
            }
        };

    }

    public Iterator<WSDLMessage> messages() {
        return new Iterators.Map<WSDLMessage,WSDLDefinitions>(getWSDLs()) {
            protected Iterator<WSDLMessage> apply(WSDLDefinitions u) {
                return u.getMessages().iterator();
            }
        };

    }

    private List<WSDLTypes> types = new ArrayList<WSDLTypes>();
    boolean alreadyComputed = false;
    public Iterator<WSDLTypes> types() {
        if(alreadyComputed)
            return types.iterator();

        for(WSDLDefinitions def:wsdls){
            types.add(def.getWSDLTypes());
        }
        alreadyComputed = true;
        return types.iterator();
    }

    public WSDLService service(QName serviceName) {
        WSDLDefinitions def = wsdlMap.get(serviceName.getNamespaceURI());
        return def.getService(serviceName);
    }

    public WSDLPortType portType(QName portTypeName) {
        WSDLDefinitions def = wsdlMap.get(portTypeName.getNamespaceURI());
        return def.getPortType(portTypeName);
    }

    public WSDLBoundPortType boundPortType(QName portType) {
        WSDLDefinitions def = wsdlMap.get(portType.getNamespaceURI());
        return def.getBinding(portType);
    }

    public WSDLMessage message(QName message) {
        WSDLDefinitions def = wsdlMap.get(message.getNamespaceURI());
        return def.getMessage(message);
    }

    public WSDLTypes types(String targetNamespace) {
        WSDLDefinitions def = wsdlMap.get(targetNamespace);
        return def.getWSDLTypes();
    }

    public Object resolveType(QName type) {
        for(WSDLTypes wsdlType : types){
            Object obj = wsdlType.getSchema().resolveType(type);
            if(obj != null)
                return obj;
        }
        return null;
    }

    public Object resolveElement(QName type) {
        while(types().hasNext()){
            WSDLTypes wsdlType = types().next();
            Object obj = wsdlType.getSchema().resolveElement(type);
            if(obj != null)
                return obj;

        }
        return null;
    }
}
