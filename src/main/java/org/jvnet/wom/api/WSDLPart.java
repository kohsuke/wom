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
package org.jvnet.wom.api;

import org.xml.sax.Locator;
import org.jvnet.wom.Schema;

import javax.xml.namespace.QName;

/**
 * Abstracts wsdl:part after applying binding information from wsdl:binding.
 *
 * @author Vivek Pandey
 */
public abstract class WSDLPart extends WSDLEntity {
    protected WSDLPart(Locator locator, QName name) {
        super(locator, name);
    }

    /**
     * Gets the wsdl:part binding as seen thru wsdl:binding
     */
//    ParameterBinding getBinding();

    /**
     * Index value is as the order in which the wsdl:part appears inside the input or output wsdl:message.
     *
     * @return n where n >= 0
     */
    public abstract int getIndex();

    /**
     * Gives the XML Schema descriptor referenced using either wsdl:part@element or wsdl:part@type.
     */
    public abstract WSDLPartDescriptor getDescriptor();

    /**
     * Abstracts wsdl:part descriptor that is defined using element or type attribute.
     *
     * @author Vivek Pandey
     */
    public abstract static class WSDLPartDescriptor {
        private final QName name;
        private final Kind type;

        public WSDLPartDescriptor(QName name, Kind kind) {
            this.name = name;
            this.type = kind;
        }

        /**
         * Gives Qualified name of the XML Schema element or type
         */

        public QName name() {
            return name;
        }

        /**
         * Gives whether wsdl:part references a schema type or a global element.
         */

        public Kind type() {
            return type;
        }

        public abstract Object getSchemaObject();


        /**
         * Enumeration that tells a wsdl:part that can be defined either using a type
         * attribute or an element attribute.
         *
         * @author Vivek Pandey
         */
        public enum Kind {
            /**
             * wsdl:part is defined using element attribute.
             * <p/>
             * <pre>
             * for exmaple,
             * &lt;wsdl:part name="foo" element="ns1:FooElement">
             * </pre>
             */
            ELEMENT,

            /**
             * wsdl:part is defined using type attribute.
             * <p/>
             * <pre>
             * for exmaple,
             * &lt;wsdl:part name="foo" element="ns1:FooType">
             * </pre>
             */
            TYPE
        }
    }

    /**
     * Tells different kind of known bindings a wsdl:part can be associated with
     *
     * @author Vivek Pandey
     */
    public enum Binding {
        Body, Header, Mime, None;
    }
}