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

import org.jvnet.wom.impl.WSDLDefinitionsImpl;
import org.jvnet.wom.impl.parser.WSDLDocumentImpl;
import org.jvnet.wom.parser.WSDLDocument;
import org.xml.sax.Locator;

import javax.xml.namespace.QName;
import java.util.HashSet;
import java.util.Set;

/**
 * Base class for the wsdl model. It represents WSDL entities such as wsdl:definitions,
 * wsdl:portType, wsdl:port, wsdl:message etc.
 *
 * @author Vivek Pandey
 */
public abstract class WSDLEntity implements WSDLExtensible {    
    private WSDLDefinitionsImpl model;
    private WSDLDocumentImpl ownerDoc;
    private String comment="";

    private final Set<WSDLExtension> extensions = new HashSet<WSDLExtension>();
    private final Locator locator;
    private final QName name;

    protected WSDLEntity(Locator locator, QName name) {
        this.locator = locator;
        this.name = name;
    }

    public QName getName() {
        return name;
    }

    /**
     * Gets the source location information in the parsed WSDL.
     *
     * This is useful when producing error messages.
     */
    public Locator getLocation() {
        return locator;
    }

    /**
     * Gives {@link WSDLDefinitions} owning this WSDL entity.
     * @return
     */
    public WSDLDefinitions getOwnerWSDLModel() {
        return model;
    }

    /**
     * The {@link WSDLDocument} associated with this WSDL entity.
     *
     * @return Always non-null.
     */
    public WSDLDocument getWSDLDocument() {
        return ownerDoc;
    }

    /**
     * Gives the Comment on a WSDL component.
     * @return "" if there is no comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * WSDL visitor
     */
    public abstract void visit(WSDLVisitor visitor);


    public final Iterable<WSDLExtension> getExtensions() {
        return extensions;
    }

    public <T extends WSDLExtension> T getExtension(Class<T> type) {
        for(WSDLExtension ex : extensions){
            if(type.isInstance(ex))
                return type.cast(ex);
        }
        return null;
    }

    public void addExtension(WSDLExtension extension) {
        if(extension == null)
            throw new IllegalArgumentException();
        extensions.add(extension);
    }

    protected void setModel(WSDLDefinitionsImpl model) {
        this.model = model;
    }

    protected void setOwnerDoc(WSDLDocumentImpl ownerDoc) {
        this.ownerDoc = ownerDoc;
    }

    protected void setComment(String comment) {
        this.comment = comment;
    }
}
