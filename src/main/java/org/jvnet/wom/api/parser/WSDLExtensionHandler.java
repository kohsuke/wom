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
package org.jvnet.wom.api.parser;

import org.jvnet.wom.api.WSDLExtension;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;

import java.util.Collection;

/**
 * Abstraction to allow processing and populating WSDL extensibility elements in to the model.
 *
 * @author Vivek Pandey
 */
public interface WSDLExtensionHandler {

    /**
     * This method should be called only after the parsing of extensibility element is complete.
     * Caller will know this when the extension's ContentHandler returns.
     *
     * @return Immutable collection of  {@link WSDLExtension}
     */
    public Collection<WSDLExtension> getExtension();

//    public void addExtensions(ExtensionReceiver receiver);
//
//    /**
//     * Gives the qualified name of wsdl extensibility element or attribute
//     */
//    public boolean extensibilityName(QName name);

    /**
     * Gives the {@link ContentHandler} which will receive SAX events for the extensibility element
     */
//    public ContentHandler getContentHandler();

    /**
     * With this the extension handlers get a chance to process the extensibility attributes.
     *
     * @return Immutable collection of {@link org.jvnet.wom.api.WSDLExtension}s
     */
    public Collection<WSDLExtension> parseAttribute(Attributes atts);

    /**
     * Gives a {@link ContentHandler} if this extension handler can process the extensibility elements
     * idetified by the nsUri and localName
     *
     * @param nsUri namespace of the extensiblity element, always non-null
     * @param localName localName of the extensibility element, always non-null
     */
    public ContentHandler getContentHandlerFor(String nsUri, String localName);
}
