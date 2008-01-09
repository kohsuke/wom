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

import org.jvnet.wom.WSDLBoundFault;
import org.jvnet.wom.WSDLBoundInput;
import org.jvnet.wom.WSDLBoundOperation;
import org.jvnet.wom.WSDLBoundOutput;
import org.jvnet.wom.WSDLOperation;
import org.jvnet.wom.WSDLVisitor;
import org.jvnet.wom.impl.parser.WSDLDocumentImpl;
import org.xml.sax.Locator;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Vivek Pandey
 */
public class WSDLBoundOperationImpl extends WSDLBoundOperation {
    private String soapAction;
    private Map<String, WSDLPartImpl> inputParts = new HashMap<String, WSDLPartImpl>();
    private Map<String, WSDLPartImpl> outputParts = new HashMap<String, WSDLPartImpl>();
    private Set<WSDLBoundFaultImpl> faults = new HashSet<WSDLBoundFaultImpl>();
    private String doc;
    private WSDLBoundPortTypeImpl owner;
    private WSDLBoundInput input;
    private WSDLBoundOutput output;

    public WSDLBoundOperationImpl(Locator locator, QName name, WSDLDocumentImpl document) {
        super(locator, name);
        setOwnerWSDLDocument(document);
    }

    public String getSOAPAction() {
        return soapAction;
    }


    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public void addBoundFault(WSDLBoundFaultImpl fault){
        faults.add(fault);
    }

    public void setOwner(WSDLBoundPortTypeImpl owner) {
        this.owner = owner;
    }

    public WSDLOperation getOperation() {
        return owner.getPortType().get(getName());
    }

    public void setBoundInput(WSDLBoundInput input){
        this.input = input;
    }
    public void setBoundOutput(WSDLBoundOutput output){
        this.output = output;
    }
    public WSDLBoundInput getInput() {
        return input;
    }

    public WSDLBoundOutput getOutput() {
        return output;
    }

    public final Map<String, WSDLPartImpl> getInParts() {
        return inputParts;
    }

    public final Map<String, WSDLPartImpl> getOutParts() {
        return outputParts;
    }

    public final Iterable<? extends WSDLBoundFault> getFaults() {
        return faults;
    }

    //TODO
    public QName getReqPayloadName() {
        return null;
    }

    public void visit(WSDLVisitor visitor) {
        visitor.bindingOperation(this);
    }

    public void setDocumentation(String doc) {
        this.doc = doc;
    }

    @Override
    public String getDocumentation() {
        return doc;
    }
}
