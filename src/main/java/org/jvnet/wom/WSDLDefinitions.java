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
 * Provides a model of a wsdl:definitions element. This will represent
 *
 * @author Vivek Pandey
 */
public abstract class WSDLDefinitions extends WSDLEntity {
    protected WSDLDefinitions(Locator locator, QName name) {
        super(locator, name);
    }

    public abstract String getTargetNamespace();

    /**
     * Gets {@link WSDLPortType} that models <code>wsdl:portType</code>
     *
     * @param name non-null quaified name of wsdl:message, where the localName is the value of <code>wsdl:portType@name</code> and
     *             the namespaceURI is the value of wsdl:definitions@targetNamespace
     * @return A {@link WSDLPortType} or null if no wsdl:portType found.
     */
    public abstract WSDLPortType getPortType(QName name);

    /**
     * Gets {@link WSDLBoundPortType} that models <code>wsdl:binding</code>
     *
     * @param name non-null quaified name of wsdl:binding, where the localName is the value of <code>wsdl:binding@name</code> and
     *             the namespaceURI is the value of wsdl:definitions@targetNamespace
     * @return A {@link WSDLBoundPortType} or null if no wsdl:binding found
     */
    public abstract WSDLBoundPortType getBinding(QName name);

    /**
     * Give a {@link WSDLBoundPortType} for the given wsdl:service and wsdl:port names.
     *
     * @param serviceName non-null service QName
     * @param portName    non-null port QName
     * @return A {@link WSDLBoundPortType}. null if the Binding for the given wsd:service and wsdl:port name are not
     *         found.
     */
    public abstract WSDLBoundPortType getBinding(QName serviceName, QName portName);

    /**
     * Gets {@link WSDLService} that models <code>wsdl:service</code>
     *
     * @param name non-null quaified name of wsdl:service, where the localName is the value of <code>wsdl:service@name</code> and
     *             the namespaceURI is the value of wsdl:definitions@targetNamespace
     * @return A {@link WSDLService} or null if no wsdl:service found.
     */
    public abstract WSDLService getService(QName name);


    /**
     * Gets {@link WSDLMessage} that models <code>wsdl:message</code>
     *
     * @param name non-null quaified name of wsdl:message, where the localName is the value of <code>wsdl:message@name</code> and
     *             the namespaceURI is the value of wsdl:definitions@targetNamespace
     * @return A {@link WSDLMessage} or null if no wsdl:service found.
     */
    public abstract WSDLMessage getMessage(QName name);


    /**
     * Gives a Iterator of all {@link WSDLMessage}
     *
     * @return empty iterator if the wsdl document has no wsdl:message. Always non-null.
     */
    public abstract Iterable<? extends WSDLMessage> getMessages();

    /**
     * Gives a Iterator of all {@link WSDLPortType}
     *
     * @return empty iterator if the wsdl document has no wsdl:portType. Always non-null.
     */
    public abstract Iterable<? extends WSDLPortType> getPortTypes();

    /**
     * Gives a Iterator of all {@link WSDLBoundPortType}
     *
     * @return empty iterator if the wsdl document has no wsdl:binding. Always non-null.
     */
    public abstract Iterable<? extends WSDLBoundPortType> getBindings();

    /**
     * Gives a Iterator of all {@link WSDLService}
     *
     * @return empty iterator if the wsdl document has no wsdl:service. Always non-null.
     */
    public abstract Iterable<? extends WSDLService> getServices();
}
