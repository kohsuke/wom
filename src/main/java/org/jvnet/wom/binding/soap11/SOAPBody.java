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

package org.jvnet.wom.binding.soap11;

import org.jvnet.wom.WSDLExtension;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Vivek Pandey
 */
public final class SOAPBody implements WSDLExtension {
    private final List<String> encodingStyle;
    private final String namespace;
    private final Use use;
    private final List<String> parts;

    public SOAPBody(String[] parts, List<String> encodingStyle, String namespace, Use use) {
        //The soap:body parts attribute could be an empty string, which means no part is
        // bound to body. Otherwise if present would represent space delimited parts (NMTOKENS)
        if(parts != null){
            List<String> tmpList = new ArrayList<String>();
            for(String part:parts){
                tmpList.add(part);
            }
            this.parts = Collections.unmodifiableList(tmpList);
        }else{
            this.parts = null;
        }

        this.encodingStyle = encodingStyle;
        this.namespace = namespace;
        this.use = (use==null)?Use.literal:null;
    }

    /**
     * Maybe null. It is supposed to be non-null only for the encoded case. 
     */
    public List<String> getEncodingStyle() {
        return encodingStyle;
    }

    /**
     *  null when {@link SOAPBinding.Style} is Document, otherwise will be non-null.
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * If not specified on &lt;soap:body> the default value is literal.
     */
    public Use getUse() {
        return use;
    }

    public QName getName() {
        return new QName(SOAPBinding.SOAP_NS, "body");
    }

    /**
     * null if parts attribute is not defined on &lt;soap:body> element, otherwise non-null. 
     */
    public List<String> getParts() {
        return parts;
    }

    public enum Use{literal,encoded}
}
