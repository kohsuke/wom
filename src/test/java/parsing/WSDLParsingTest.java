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

import com.sun.tools.xjc.api.S2JJAXBModel;
import junit.framework.TestCase;
import org.jvnet.wom.WSDLBoundInput;
import org.jvnet.wom.WSDLBoundOperation;
import org.jvnet.wom.WSDLBoundOutput;
import org.jvnet.wom.WSDLBoundPortType;
import org.jvnet.wom.WSDLDefinitions;
import org.jvnet.wom.WSDLInput;
import org.jvnet.wom.WSDLMessage;
import org.jvnet.wom.WSDLOperation;
import org.jvnet.wom.WSDLOutput;
import org.jvnet.wom.WSDLPart;
import org.jvnet.wom.WSDLPort;
import org.jvnet.wom.WSDLPortType;
import org.jvnet.wom.WSDLService;
import org.jvnet.wom.WSDLSet;
import org.jvnet.wom.binding.soap11.SOAPAddress;
import org.jvnet.wom.binding.soap11.SOAPBinding;
import org.jvnet.wom.binding.soap11.SOAPBody;
import org.jvnet.wom.binding.soap11.SOAPOperation;
import org.jvnet.wom.parser.WOMParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.InputStream;

public class WSDLParsingTest extends TestCase {

    public void testSimpleWSDL() throws SAXException {
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

        //portType
        WSDLPortType portType = def.getPortType(new QName("http://example.com/wsdl", "HelloWorld"));
        assertNotNull(portType);
        assertEquals(portType.getDocumentation(), "This is a simple HelloWorld service.");
        WSDLOperation operation = portType.get(new QName("http://example.com/wsdl", "echo"));
        assertNotNull(operation);
        assertEquals(operation.getDocumentation(), "This operation simply echoes back whatever it receives");

        WSDLInput input = operation.getInput();
        assertNotNull(input);
        assertEquals(input.getMessage(), message1);
        
        WSDLOutput output = operation.getOutput();
        assertNotNull(output);
        assertEquals(output.getMessage(), message2);

        //binding
        WSDLBoundPortType binding = def.getBinding(new QName("http://example.com/wsdl", "HelloWorldBinding"));
        assertNotNull(binding);
        assertEquals(binding.getPortType(), portType);

        //test soap:binding
        SOAPBinding sb = binding.getExtension(SOAPBinding.class);
        assertEquals(sb.getStyle(), SOAPBinding.Style.Document);
        assertEquals(sb.getTransport(), "http://schemas.xmlsoap.org/soap/http");

        WSDLBoundOperation boundOp = binding.get(new QName("http://example.com/wsdl", "echo"));
        assertNotNull(boundOp);
        assertEquals(boundOp.getOperation(), operation);

        //soap:operation
        SOAPOperation soapOp = boundOp.getExtension(SOAPOperation.class);
        assertNotNull(soapOp);
        assertEquals(soapOp.getSoapAction(), "http://example.com/wsdl/echo");
        assertNull(soapOp.getStyle());

        WSDLBoundInput boundInput = boundOp.getInput();
        assertNotNull(boundInput);

        //soap:body
        SOAPBody body = boundInput.getExtension(SOAPBody.class);
        assertNotNull(body);
        assertEquals(body.getUse(), SOAPBody.Use.literal);
        assertEquals(body.getParts().get(0), "reqBody");

        WSDLBoundOutput boundOutput = boundOp.getOutput();
        assertNotNull(boundOutput);

        //soap:body
        body = boundOutput.getExtension(SOAPBody.class);
        assertNotNull(body);
        assertEquals(body.getUse(), SOAPBody.Use.literal);
        assertEquals(body.getParts().get(0), "respBody");

        WSDLService service = def.getService(new QName("http://example.com/wsdl", "HelloService"));
        assertNotNull(service);
        assertEquals(service.getDocumentation(), "This is a simple HelloWorld service.");
        WSDLPort port = service.get(new QName("http://example.com/wsdl", "HelloWorldPort"));
        assertNotNull(port);
        assertEquals(port.getDocumentation(), "A SOAP 1.1 port");
        assertEquals(port.getBinding(), binding);

        SOAPAddress soapAdd = port.getExtension(SOAPAddress.class);
        assertNotNull(soapAdd);
        assertEquals(soapAdd.getLocation(), "http://localhost/HelloService");
    }

    public void testCyclicWsdl() throws IOException, SAXException {
        WOMParser parser = new WOMParser();
        WSDLSet wsdls = parser.parse(new InputSource("http://131.107.72.15/Security_WsSecurity_Service_Indigo/WsSecurity10.svc?wsdl"));
        assertTrue(wsdls.getWSDLs().iterator().hasNext());
        WSDLDefinitions def = wsdls.getWSDLs().iterator().next();
        assertNotNull(def);
    }

    public void testSchema() throws SAXException {
        InputStream is = getClass().getResourceAsStream("../simple.wsdl");
        WOMParser parser = new WOMParser();
        XMLSchemaExtensionHandler handler = new XMLSchemaExtensionHandler(parser.getErrorHandler(), parser.getEntityResolver());
        parser.addWSDLExtensionHandler(handler);
        WSDLSet wsdls = parser.parse(is);
        assertTrue(wsdls.getWSDLs().iterator().hasNext());
        WSDLDefinitions def = wsdls.getWSDLs().iterator().next();
        S2JJAXBModel jaxbModel = handler.bind();
        WSDLPortType pt = def.getPortTypes().iterator().next();
        WSDLOperation operation = pt.getOperations().iterator().next();
        WSDLPart part = operation.getInput().getMessage().parts().iterator().next();
        assertEquals(jaxbModel.get(part.getDescriptor().name()).getType().getTypeClass().fullName(), "com.example.types.EchoType");
    }
}
