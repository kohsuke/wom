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
import org.jvnet.wom.api.*;
import org.jvnet.wom.api.parser.WOMParser;

import java.io.InputStream;

/**
 * @author Vivek Pandey
 */
public class WSDLVisitorTest extends TestCase {

    public void testVisitor(){
        WOMParser parser = new WOMParser();
        InputStream is = getClass().getResourceAsStream("../simple.wsdl");

    }

    private class TestVisitor implements WSDLVisitor<String, String>{

        public String definitions(WSDLDefinitions definitions, String param) {
            return null;
        }

        public String types(WSDLTypes types, String param) {
            return null;
        }

        public String messages(WSDLMessage message, String param) {
            return null;
        }

        public String part(WSDLPart part, String param) {
            return null;
        }

        public String portType(WSDLPortType portType, String param) {
            return null;
        }

        public String operation(WSDLOperation operation, String param) {
            return null;
        }

        public String input(WSDLInput input, String param) {
            return null;
        }

        public String output(WSDLOutput output, String param) {
            return null;
        }

        public String fault(WSDLFault fault, String param) {
            return null;
        }

        public String binding(WSDLBoundPortType binding, String param) {
            return null;
        }

        public String bindingOperation(WSDLBoundOperation boundOperation, String param) {
            return null;
        }

        public String boundInput(WSDLBoundInput boundInput, String param) {
            return null;
        }

        public String boundOutput(WSDLBoundOutput boundOutput, String param) {
            return null;
        }

        public String bindingFault(WSDLBoundFault fault, String param) {
            return null;
        }

        public String service(WSDLService service, String param) {
            return null;
        }

        public String port(WSDLPort port, String param) {
            return null;
        }
    }
}
