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
package org.jvnet.wom.impl.extension.wsdl11.mime;

import org.jvnet.wom.api.binding.wsdl11.mime.MimeContent;
import org.jvnet.wom.api.binding.wsdl11.mime.MimePart;
import org.jvnet.wom.impl.extension.wsdl11.soap.SOAPBodyImpl;
import org.xml.sax.Locator;

import java.util.*;

/**
 * @author Vivek Pandey
 */
public class MimePartImpl implements MimePart {
    private final Locator locator;
    private final String partName;
    private final List<MimeContent> contents = new ArrayList<MimeContent>();
    private final Map<String, List<String>> contentMap = new HashMap<String, List<String>>();
    private SOAPBodyImpl bodyPart;

    public MimePartImpl(String partName, Locator locator) {
        this.partName = partName;
        this.locator = locator;
    }

    public String getPartName() {
        return partName;
    }

    public void setBodyPart(SOAPBodyImpl part){
        this.bodyPart = part;
    }

    public SOAPBodyImpl getBodyPart() {
        return bodyPart;
    }

    public Collection<MimeContent> getMimeContentParts() {
        return contents;
    }

    public void addMimeContent(MimeContent mimeContent){
        if(contentMap.get(mimeContent.getPartName())==null){
            List<String> contentTypes = new ArrayList<String>();
            contentTypes.add(mimeContent.getType());
            contentMap.put(mimeContent.getPartName(), contentTypes);
        }else{
            contentMap.get(mimeContent.getPartName()).add(mimeContent.getType());
        }
        contents.add(mimeContent);
    }

    public Collection<String> getMimeContentType(String mimePart) {
        return contentMap.get(mimePart);
    }
}
