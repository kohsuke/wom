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
package parsing;

import junit.framework.TestCase;
import org.jvnet.wom.WSDLDefinitions;
import org.jvnet.wom.WSDLMessage;
import org.jvnet.wom.WSDLPart;
import org.jvnet.wom.WSDLSet;
import org.jvnet.wom.parser.WOMParser;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import java.io.InputStream;

public class WSDLParsingTest extends TestCase {

    public void testWSDL() throws SAXException {
        InputStream is = getClass().getResourceAsStream("../simple.wsdl");
        WOMParser parser = new WOMParser();
        WSDLSet wsdls = parser.parse(is);
        assertTrue(wsdls.getWSDLs().iterator().hasNext());
        WSDLDefinitions def = wsdls.getWSDLs().iterator().next();
        assertEquals(def.getName(), new QName("http://example.com/wsdl", "Simple"));

        //test wsdl:messages
        WSDLMessage message1 = def.getMessage(new QName("http://example.com/wsdl", "echoRequest"));
        assertNotNull(message1);
        WSDLPart part1 = message1.getPart("reqBody");
        assertNotNull(part1);
        assertEquals(part1.getDescriptor().type(), WSDLPart.WSDLPartDescriptor.Kind.ELEMENT);
        assertEquals(part1.getDescriptor().name(), new QName("http://example.com/types", "echo"));
        assertEquals(part1.getIndex(), 0);


        WSDLMessage message2 = def.getMessage(new QName("http://example.com/wsdl", "echoResponse"));
        assertNotNull(message2);
        WSDLPart part2 = message2.getPart("respBody");
        assertNotNull(part2);
        assertEquals(part2.getDescriptor().type(), WSDLPart.WSDLPartDescriptor.Kind.ELEMENT);
        assertEquals(part2.getDescriptor().name(), new QName("http://example.com/types", "echoResponse"));
        assertEquals(part1.getIndex(), 0);

        assertNotNull(def.getMessage(new QName("http://example.com/wsdl", "echoResponse")));

        


    }
}
