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

import org.jvnet.wom.api.WSDLBoundOutput;
import org.jvnet.wom.api.WSDLOutput;
import org.jvnet.wom.api.WSDLPart;
import org.jvnet.wom.api.WSDLVisitor;
import org.jvnet.wom.api.binding.wsdl11.mime.MimeContent;
import org.jvnet.wom.api.binding.wsdl11.mime.MimeMultipart;
import org.jvnet.wom.api.binding.wsdl11.mime.MimePart;
import org.jvnet.wom.api.binding.wsdl11.soap.SOAPBody;
import org.jvnet.wom.api.binding.wsdl11.soap.SOAPHeader;
import org.jvnet.wom.impl.parser.WSDLDocumentImpl;
import org.xml.sax.Locator;

import javax.xml.namespace.QName;
import java.util.Collection;

/**
 * @author Vivek Pandey
 */
public class WSDLBoundOutputImpl extends WSDLBoundOutput {
    private WSDLBoundOperationImpl parent;
    private String doc;

    public WSDLBoundOutputImpl(Locator locator, QName name, WSDLDocumentImpl document) {
        super(locator, name);
        setOwnerWSDLDocument(document);
    }

    public void setParent(WSDLBoundOperationImpl parent) {
        this.parent = parent;
    }

    @Override
    public String getDocumentation() {
        return doc;
    }

    public <V, P> V visit(WSDLVisitor<V, P> visitor, P param) {
        return visitor.boundOutput(this, param);
    }

    public void setDocumentation(String doc) {
        this.doc = doc;
    }

    public void setOwner(WSDLBoundOperationImpl boundOp){
        this.parent = boundOp;
    }


    public WSDLPart.Binding getPartBinding(String partName) {
        int numOfParts = parent.getOperation().getOutput().getMessage().parts().size();
        Collection<SOAPHeader> headers = getExtension(SOAPHeader.class);
        if(headers != null && headers.size() > 0){
            for(SOAPHeader header:headers){
                if(header.getPart().equals(partName))
                    return WSDLPart.Binding.Header;

            }
        }
        Collection<SOAPBody> soapBodies = getExtension(SOAPBody.class);
        if(soapBodies != null){
            for(SOAPBody body: soapBodies){
                if(body.getParts().size() > 0){
                    if(body.getParts().contains(partName))
                        return WSDLPart.Binding.Body;
                    else
                        return WSDLPart.Binding.None;
                }
            }

        }

        Collection<MimeMultipart> mimeMultiparts = getExtension(MimeMultipart.class);
        if(mimeMultiparts != null){
            for(MimeMultipart multipart: mimeMultiparts){
                for(MimePart mimepart:multipart.getMimeParts()){
                    for(MimeContent content:mimepart.getMimeContentParts()){
                        if(content.getPartName().equals(partName))
                            return WSDLPart.Binding.Mime;
                        else if((content.getPartName() == null || content.getPartName().equals("")) && numOfParts==1)
                            return WSDLPart.Binding.Mime;
                    }
                    SOAPBody body = mimepart.getBodyPart();
                    if(body != null){
                        if(body.getParts() == null || body.getParts().contains(partName)){
                            return WSDLPart.Binding.Body;
                        }
                    }
                }
            }
        }

        return WSDLPart.Binding.None;
    }


    public WSDLOutput getOutput() {
        return parent.getOperation().getOutput();
    }
}
