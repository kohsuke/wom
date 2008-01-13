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

import org.jvnet.wom.api.parser.WSDLDocument;
import org.jvnet.wom.impl.WSDLDefinitionsImpl;

import java.util.HashSet;
import java.util.Set;

public class WSDLDocumentImpl implements WSDLDocument {
    private final WSDLDefinitionsImpl wsdl;
    private final String systemId;


    /**
     * WSDLs that are referenced from this document
     */
    final Set<WSDLDocumentImpl> references = new HashSet<WSDLDocumentImpl>();

    /**
     * {@link WSDLDocumentImpl}s that are referencing this document.
     */
    final Set<WSDLDocumentImpl> referers = new HashSet<WSDLDocumentImpl>();


    public WSDLDocumentImpl(WSDLDefinitionsImpl wsdl, String systemId) {
        this.wsdl = wsdl;
        this.systemId = systemId;
    }

    public String getSystemId() {
        return systemId;
    }

    public String getTargetNamespace() {
        return wsdl.getTargetNamespace();
    }

    public WSDLDefinitionsImpl getWSDLModel() {
        return wsdl;
    }

    public Set<WSDLDocumentImpl> getImportedWSDLs() {
        return references;
    }

    public boolean equals(Object o) {
        WSDLDocumentImpl rhs = (WSDLDocumentImpl) o;

        if( this.systemId==null || rhs.systemId==null)
            return this==rhs;
        if(!systemId.equals(rhs.systemId) )
            return false;
        return this.wsdl==rhs.wsdl;
    }
    public int hashCode() {
        if(systemId==null)
            return super.hashCode();
        return systemId.hashCode()^this.systemId.hashCode();
    }
}
