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
package org.jvnet.wom;

import org.xml.sax.Locator;

import javax.xml.namespace.QName;

/**
 * Abstracts wsdl:binding/wsdl:operation. It can be used to determine the parts and their binding.
 *
 * @author Vivek Pandey
 */
public abstract class WSDLBoundOperation extends WSDLEntity {
    protected WSDLBoundOperation(Locator locator, QName name) {
        super(locator, name);
    }

    /**
     * Gives soapbinding:operation@soapAction value. soapbinding:operation@soapAction is optional attribute.
     * If not present an empty String is returned as per BP 1.1 R2745.
     */
    public abstract String getSOAPAction();

    /**
     * Gets the wsdl:portType/wsdl:operation model - {@link WSDLOperation},
     * associated with this binding operation.
     *
     * @return always same {@link WSDLOperation}
     */
    public abstract WSDLOperation getOperation();

//    /**
//     * Gets all inbound {@link WSDLPart} by its {@link WSDLPart#getName() name}.
//     */
//    public abstract Map<String, ? extends WSDLPart> getInParts();
//
//    /**
//     * Gets all outbound {@link WSDLPart} by its {@link WSDLPart#getName() name}.
//     */
//    public abstract Map<String, ? extends WSDLPart> getOutParts();

    /**
     * Gets the wsdl:input of this operation
     *
     * @return non-null {@link WSDLBoundInput}
     */
    public abstract WSDLBoundInput getInput();

    /**
     * Gets the wsdl:output of this operation.
     *
     * @return null if this is an one-way operation.
     */
    public abstract WSDLBoundOutput getOutput();

    /**
     * Gets all the {@link WSDLFault} bound to this operation.
     */
    public abstract Iterable<? extends WSDLBoundFault> getFaults();    
}
