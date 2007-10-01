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

import org.jvnet.wom.WSDLOperation;
import org.jvnet.wom.WSDLPortType;
import org.jvnet.wom.WSDLVisitor;
import org.xml.sax.Locator;

import javax.xml.namespace.QName;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Vivek Pandey
 */
public class WSDLPortTypeImpl extends WSDLPortType {
    private Map<QName, WSDLOperationImpl> operations = new HashMap<QName, WSDLOperationImpl>();
    private WSDLDefinitionsImpl owner;
    
    public WSDLPortTypeImpl(Locator locator, QName name) {
        super(locator, name);
    }

    public WSDLOperation get(QName operationName) {
        return null;
    }

    public Iterable<WSDLOperationImpl> getOperations() {
        return operations.values();
    }

    public void visit(WSDLVisitor visitor) {
        visitor.portType(this);
    }
}
