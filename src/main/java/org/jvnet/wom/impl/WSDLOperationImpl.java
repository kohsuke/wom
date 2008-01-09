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

import org.jvnet.wom.WSDLFault;
import org.jvnet.wom.WSDLInput;
import org.jvnet.wom.WSDLOperation;
import org.jvnet.wom.WSDLOutput;
import org.jvnet.wom.WSDLPart;
import org.jvnet.wom.WSDLPortType;
import org.jvnet.wom.WSDLVisitor;
import org.jvnet.wom.impl.parser.WSDLDocumentImpl;
import org.xml.sax.Locator;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Vivek Pandey
 */
public class WSDLOperationImpl extends WSDLOperation {
    private Set<WSDLFaultImpl> faults = new HashSet<WSDLFaultImpl>();
    private WSDLInputImpl input;
    private WSDLOutputImpl output;
    private WSDLPortType parent;
    private String doc;
    private List<String> paramOrder = new ArrayList<String>();
    
    public WSDLOperationImpl(Locator locator, QName name, WSDLDocumentImpl document) {
        super(locator, name);
        setOwnerWSDLDocument(document);
    }

    public WSDLInput getInput() {
        return input;
    }

    public WSDLOutput getOutput() {
        return output;
    }

    public boolean isOneWay() {
        return (output == null);
    }

    public final Iterable<WSDLFaultImpl> getFaults() {
        return faults;
    }

    public WSDLFault getFault(QName faultDetailName) {
        for (WSDLFaultImpl f : faults) {
            for (WSDLPart part : f.getMessage().parts()) {
                if (part.getDescriptor().name().equals(faultDetailName))
                    return f;
            }
        }
        return null;
    }

    public void addFault(WSDLFaultImpl fault) {
        faults.add(fault);
    }

    public WSDLPortType getPortType() {
        return parent;
    }

    public List<String> getParameterOrder() {
        return Collections.unmodifiableList(paramOrder);
    }

    public void setParameterOrder(String[] paramOrder) {
        assert this.paramOrder.isEmpty();
        for (String param : paramOrder) {
            this.paramOrder.add(param);
        }
    }

    public void setInput(WSDLInputImpl input) {
        this.input = input;
    }

    public void setOutput(WSDLOutputImpl output) {
        this.output = output;
    }

    public void setParent(WSDLPortType parent) {
        this.parent = parent;
    }

    public void visit(WSDLVisitor visitor) {
        visitor.operation(this);
    }

    public void setDocumentation(String doc) {
        this.doc = doc;
    }

    @Override
    public String getDocumentation() {
        return doc;
    }
}
