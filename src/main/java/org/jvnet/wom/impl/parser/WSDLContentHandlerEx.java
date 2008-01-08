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

import org.jvnet.wom.impl.WSDLDefinitionsImpl;
import org.jvnet.wom.impl.parser.handler.Definitions;
import org.jvnet.wom.impl.parser.handler.WSDLContentHandler;
import org.jvnet.wom.impl.util.Uri;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Stack;

public class WSDLContentHandlerEx extends WSDLContentHandler {

    /**
     * The schema currently being parsed.
     */
    public WSDLDefinitionsImpl currentWSDL;

    /**
     * URI that identifies the schema document.
     * Maybe null if the system ID is not available.
     */
    private String documentSystemId;

    /**
     * Keep the local name of elements encountered so far.
     * This information is passed to AnnotationParser as
     * context information
     */
    private final Stack<String> elementNames = new Stack<String>();

    /**
     * Points to the schema document (the parser of it) that included/imported
     * this schema.
     */
    private final WSDLContentHandlerEx referer;

    /**
     * Points to the {@link WSDLDocumentImpl} that represents the
     * schema document being parsed.
     */
    public WSDLDocumentImpl document;

    public ParserContext parser;

    public WSDLContentHandlerEx(ParserContext parser) {
        this(parser, null);
    }

    private WSDLContentHandlerEx(ParserContext parser, WSDLContentHandlerEx referer) {
        this.referer = referer;
        this.parser = parser;
    }


    /* registers a patcher that will run after all the parsing has finished. */
    public void addPatcher(Patch patcher) {
        parser.patcherManager.addPatcher(patcher);
    }

    public void addErrorChecker(Patch patcher) {
        parser.patcherManager.addErrorChecker(patcher);
    }

    public void reportError(String msg, Locator loc) throws SAXException {
        parser.patcherManager.reportError(msg, loc);
    }

    public void reportError(String msg) throws SAXException {
        reportError(msg, getLocator());
    }


    /**
     * Resolves relative URI found in the document.
     *
     * @param namespaceURI passed to the entity resolver.
     * @param relativeUri  value of the schemaLocation attribute. Can be null.
     * @return non-null if {@link org.xml.sax.EntityResolver} returned an {@link InputSource},
     *         or if the relativeUri parameter seems to be pointing to something.
     *         Otherwise it returns null, in which case import/include should be abandoned.
     */
    private InputSource resolveRelativeURL(String namespaceURI, String relativeUri) throws SAXException {
        try {
            String baseUri = getLocator().getSystemId();
            if (baseUri == null)
                // if the base URI is not available, the document system ID is
                // better than nothing.
                baseUri = documentSystemId;

            String systemId = null;
            if (relativeUri != null)
                systemId = Uri.resolve(baseUri, relativeUri);

            EntityResolver er = parser.getEntityResolver();
            if (er != null) {
                InputSource is = er.resolveEntity(namespaceURI, systemId);
                if (is != null)
                    return is;
            }

            if (systemId != null)
                return new InputSource(systemId);
            else
                return null;
        } catch (IOException e) {
            SAXParseException se = new SAXParseException(e.getMessage(), getLocator(), e);
            parser.errorHandler.error(se);
            return null;
        }
    }

    /**
     * Imports the specified WSDL.
     */
    public void importWSDL(String ns, String wsdlLocation) throws SAXException {
        WSDLContentHandlerEx newRuntime = new WSDLContentHandlerEx(parser, this);
        InputSource source = resolveRelativeURL(ns, wsdlLocation);
        if (source != null)
            newRuntime.parseEntity(source, ns, getLocator());
        // if source == null,
        // we can't locate this document. Let's just hope that
        // we already have the wsdl components for this wsdl
        // or we will receive them in the future.
    }


    /**
     * Called when a new document is being parsed and checks
     * if the document has already been parsed before.
     * <p/>
     * <p/>
     * Used to avoid recursive inclusion. Note that the same
     * document will be parsed multiple times if they are for different
     * target namespaces.
     * <p/>
     * <h2>Document Graph Model</h2>
     * <p/>
     * The challenge we are facing here is that you have a graph of
     * documents that reference each other. Each document has an unique
     * URI to identify themselves, and references are done by using those.
     * The graph may contain cycles.
     * <p/>
     * <p/>
     * Our goal here is to parse all the documents in the graph, without
     * parsing the same document twice. This method implements this check.
     * <p/>
     * <p/>
     * One complication is the chameleon schema; a document can be parsed
     * multiple times if they are under different target namespaces.
     * <p/>
     * <p/>
     * Also, note that when you resolve relative URIs in the @schemaLocation,
     * their base URI is *NOT* the URI of the document.
     *
     * @return true if the document has already been processed and thus
     *         needs to be skipped.
     */
    public boolean hasAlreadyBeenRead() {
        if (documentSystemId != null) {
            if (documentSystemId.startsWith("file:///"))
                // change file:///abc to file:/abc
                // JDK File.toURL method produces the latter, but according to RFC
                // I don't think that's a valid URL. Since two different ways of
                // producing URLs could produce those two different forms,
                // we need to canonicalize one to the other.
                documentSystemId = "file:/" + documentSystemId.substring(8);
        } else {
            // if the system Id is not provided, we can't test the identity,
            // so we have no choice but to read it.
            // the newly created SchemaDocumentImpl will be unique one
        }

        assert document == null;
        document = new WSDLDocumentImpl(currentWSDL, documentSystemId);

        WSDLDocumentImpl existing = parser.parsedDocuments.get(document);
        if (existing == null) {
            parser.parsedDocuments.put(document, document);

            //set the reference to owning document in the current WSDL
            currentWSDL.setOwnerDocument(document);
        } else {
            document = existing;
        }

        assert document != null;

        if (referer != null) {
            assert referer.document != null : "referer " + referer.documentSystemId + " has docIdentity==null";
            referer.document.references.add(this.document);
            this.document.referers.add(referer.document);
        }

        return existing != null;
    }

    /**
     * Parses the specified entity.
     *
     * @param importLocation The source location of the import/include statement.
     *                       Used for reporting errors.
     */
    public void parseEntity(InputSource source, String expectedNamespace, Locator importLocation)
            throws SAXException {

        documentSystemId = source.getSystemId();
//        System.out.println("parsing "+baseUri);


        try {
            Definitions s = new Definitions(this, expectedNamespace);
            setRootHandler(s);

            try {
                parser.parser.parse(source, this,
                        parser.getEntityResolver(), getErrorHandler());
            } catch (IOException e) {
                SAXParseException se = new SAXParseException(
                        e.toString(), importLocation, e);
                parser.errorHandler.fatalError(se);
                throw se;
            }
        } catch (SAXException e) {
            parser.setErrorFlag();
            throw e;
        }
    }


    /**
     * Gets the element name that contains the annotation element.
     * This method works correctly only when called by the annotation handler.
     */
    public String getAnnotationContextElementName() {
        return elementNames.get(elementNames.size() - 2);
    }

    /**
     * Creates a copy of the current locator object.
     */
    public Locator copyLocator() {
        return new LocatorImpl(getLocator());
    }

    public ErrorHandler getErrorHandler() {
        return parser.errorHandler;
    }

    public void onEnterElementConsumed(String uri, String localName, String qname, Attributes atts)
            throws SAXException {
        super.onEnterElementConsumed(uri, localName, qname, atts);
        elementNames.push(localName);
    }

    public void onLeaveElementConsumed(String uri, String localName, String qname) throws SAXException {
        super.onLeaveElementConsumed(uri, localName, qname);
        elementNames.pop();
    }


    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        super.startPrefixMapping(prefix, uri);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        super.endPrefixMapping(prefix);
    }

//
//
// Utility functions
//
//


    public boolean parseBoolean(String v) {
        if (v == null) return false;
        v = v.trim();
        return v.equals("true") || v.equals("1");
    }


    protected void unexpectedX(String token) throws SAXException {
        SAXParseException e = new SAXParseException(MessageFormat.format(
                "Unexpected {0} appears at line {1} column {2}",
                token,
                getLocator().getLineNumber(),
                getLocator().getColumnNumber()),
                getLocator());

        parser.errorHandler.fatalError(e);
        throw e;    // we will abort anyway
    }
}
