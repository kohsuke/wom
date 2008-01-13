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
import org.jvnet.wom.impl.WSDLDefinitionsImpl;
import org.xml.sax.Locator;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WSDLSetImpl implements WSDLSet {

    private final Map<String, WSDLDefinitionsImpl> wsdlMap = new HashMap<String, WSDLDefinitionsImpl>();

    /**
     * Gets a reference to the existing schema or creates a new one
     * if none exists yet.
     */
    public WSDLDefinitionsImpl createWSDLDefinitions(String name, String targetNamespace, Locator location) {
        WSDLDefinitionsImpl obj = wsdlMap.get(targetNamespace);
        if (obj == null) {
            obj = new WSDLDefinitionsImpl(this, location, new QName(targetNamespace, name));
            wsdlMap.put(targetNamespace, obj);
        }
        return obj;
    }

    void add(WSDLDefinitionsImpl wsdl) {
        wsdlMap.put(wsdl.getWSDLDocument().getTargetNamespace(), wsdl);
    }

    public WSDLDefinitionsImpl getWSDL(String targetNamespace) {
        return wsdlMap.get(targetNamespace);
    }

    public Collection<WSDLDefinitions> getWSDLs() {
        return Collections.<WSDLDefinitions>unmodifiableCollection(wsdlMap.values());
    }

}
