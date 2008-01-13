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
package org.jvnet.wom.impl.parser;

import org.jvnet.wom.api.WSDLSet;
import org.jvnet.wom.api.parser.WOMParser;
import org.jvnet.wom.api.parser.WSDLExtensionHandler;
import org.jvnet.wom.api.parser.XMLParser;
import org.jvnet.wom.impl.extension.SOAPAddressExtensionHandler;
import org.jvnet.wom.impl.extension.SOAPBindingExtensionHandler;
import org.jvnet.wom.impl.extension.SOAPBodyExtensionHandler;
import org.jvnet.wom.impl.extension.SOAPFaultExtensionHandler;
import org.jvnet.wom.impl.extension.SOAPHeaderExtensionHandler;
import org.jvnet.wom.impl.extension.SOAPHeaderFaultExtensionHandler;
import org.jvnet.wom.impl.extension.SOAPOperationExtensionHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * Provides context information to be used by {@link WSDLContentHandlerEx}s.
 * <p/>
 * <p/>
 * This class does the actual processing for {@link WOMParser},
 * but to hide the details from the public API, this class in
 * a different package.
 *
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 * @author Vivek Pandey
 */
public class ParserContext {

    /**
     * SchemaSet to which a newly parsed schema is put in.
     */
    public final WSDLSetImpl wsdlSet = new WSDLSetImpl();

    private final WOMParser owner;

    final XMLParser parser;

    private final Set<WSDLExtensionHandler> extensionMap = new HashSet<WSDLExtensionHandler>();

    private void addKnownWSDLExtensionHandler(WSDLExtensionHandler... extensionHandlers){
        for(WSDLExtensionHandler extensionHandler:extensionHandlers){
            extensionMap.add(extensionHandler);
            //extensionMap.put(extensionHandler.extensibilityName(), extensionHandler);
        }
    }
    
    /* ContentHandler to parse schema inside the wsdl:types element */
    private ContentHandler schemaContentHandler = new DefaultHandler();


    private final Vector<Patch> patchers = new Vector<Patch>();
    private final Vector<Patch> errorCheckers = new Vector<Patch>();

    /**
     * Documents that are parsed already. Used to avoid cyclic inclusion/double
     * inclusion of schemas. Set of {@link WSDLDocumentImpl}s.
     * <p/>
     * The actual data structure is map from {@link WSDLDocumentImpl} to itself,
     * so that we can access the {@link WSDLDocumentImpl} itself.
     */
    public final Map<WSDLDocumentImpl, WSDLDocumentImpl> parsedDocuments = new HashMap<WSDLDocumentImpl, WSDLDocumentImpl>();


    public ParserContext(WOMParser owner, XMLParser parser) {
        this.owner = owner;
        this.parser = parser;
        addKnownWSDLExtensionHandler(
                new SOAPAddressExtensionHandler(errorHandler, owner.getEntityResolver()),
                new SOAPBindingExtensionHandler(errorHandler, owner.getEntityResolver()),
                new SOAPBodyExtensionHandler(errorHandler, owner.getEntityResolver()),
                new SOAPFaultExtensionHandler(errorHandler, owner.getEntityResolver()),
                new SOAPHeaderExtensionHandler(errorHandler, owner.getEntityResolver()),
                new SOAPHeaderFaultExtensionHandler(errorHandler, owner.getEntityResolver()),
                new SOAPOperationExtensionHandler(errorHandler, owner.getEntityResolver()));

    }

    public EntityResolver getEntityResolver() {
        return owner.getEntityResolver();
    }

    /**
     * Parses a new XML Schema document.
     */
    public void parse(InputSource source) throws SAXException {
        createWSDLContentHandler().parseEntity(source, null, null);
    }


    /**
     * Returns {@link WSDLSet}, null if there was any error.
     */
    public WSDLSet getResult() throws SAXException {
        // run all the patchers
        for (Patch patcher : patchers)
            patcher.run();

        patchers.clear();

        // run all the error checkers
        for (Patch patcher : errorCheckers)
            patcher.run();
        errorCheckers.clear();


        if (hadError) return null;
        else return wsdlSet;
    }

    public WSDLContentHandlerEx createWSDLContentHandler() {
        return new WSDLContentHandlerEx(this);
    }


    /**
     * Once an error is detected, this flag is set to true.
     */
    private boolean hadError = false;

    /**
     * Turns on the error flag.
     */
    void setErrorFlag() {
        hadError = true;
    }

    /**
     * PatchManager implementation, which is accessible only from
     * NGCCRuntimEx.
     */
    final PatcherManager patcherManager = new PatcherManager() {
        public void addPatcher(Patch patch) {
            patchers.add(patch);
        }

        public void addErrorChecker(Patch patch) {
            errorCheckers.add(patch);
        }

        public void reportError(String msg, Locator src) throws SAXException {
            // set a flag to true to avoid returning a corrupted object.
            setErrorFlag();

            SAXParseException e = new SAXParseException(msg, src);
            if (errorHandler == null)
                throw e;
            else
                errorHandler.error(e);
        }
    };

    public ContentHandler getSchemaContentHandler() {
        return schemaContentHandler;
    }

    public void setSchemaContentHandler(ContentHandler schemaContentHandler) {
        this.schemaContentHandler = schemaContentHandler;
    }

    /**
     * ErrorHandler proxy to turn on the hadError flag when an error
     * is found.
     */
    final ErrorHandler errorHandler = new ErrorHandler() {
        private ErrorHandler getErrorHandler() {
            if (owner.getErrorHandler() == null)
                return noopHandler;
            else
                return owner.getErrorHandler();
        }

        public void warning(SAXParseException e) throws SAXException {
            getErrorHandler().warning(e);
        }

        public void error(SAXParseException e) throws SAXException {
            setErrorFlag();
            getErrorHandler().error(e);
        }

        public void fatalError(SAXParseException e) throws SAXException {
            setErrorFlag();
            getErrorHandler().fatalError(e);
        }
    };

    /**
     * {@link ErrorHandler} that does nothing.
     */
    final ErrorHandler noopHandler = new ErrorHandler() {
        public void warning(SAXParseException e) {
        }

        public void error(SAXParseException e) {
        }

        public void fatalError(SAXParseException e) {
            setErrorFlag();
        }
    };

    public void addWSDLExtensionHandler(WSDLExtensionHandler extension) {
//        extensionMap.put(extension.extensibilityName(), extension);
        extensionMap.add(extension);
    }


//    public WSDLExtensionHandler getWSDLExtensionHandler(String uri, String localName){
//        return extensionMap.get(uri, localName);
//    }

    public Set<WSDLExtensionHandler> getWSDLExtensionHandlers(){
        return extensionMap;
    }
}
