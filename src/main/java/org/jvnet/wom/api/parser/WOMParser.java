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

import org.jvnet.wom.api.WSDLSet;
import org.jvnet.wom.impl.parser.ParserContext;
import org.jvnet.wom.impl.parser.XMLParserImpl;
import org.jvnet.wom.impl.parser.WSDLContentHandlerEx;
import org.jvnet.wom.impl.parser.handler.Definitions;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import com.sun.xml.xsom.parser.XMLParser;

/**
 * Parses a WSDL and provides a model - {@link WSDLSet}. The interfaces provided by {@link WOMParser} are largely based
 * on XSOM.
 * <p/>
 * TODO: support for WSDL 2.0
 *
 * @author Vivek Pandey
 */
public final class WOMParser {

    private EntityResolver entityResolver;
    private ErrorHandler errorHandler;
    private final XMLParser parser;
    private final ParserContext context;

    /**
     * Creates a new {@link WOMParser} using SAX parser from the default implementation of {@link XMLParser}
     */
    public WOMParser() {
        this.parser = new XMLParserImpl();
        this.context = new ParserContext(this, this.parser);
    }


    /**
     * Creates a new {@link WOMParser}, the underlying XMLParser will use the factory to create SAX parser.
     *
     * @param factory Must be non-null. The factory must be configured correctly, such as
     *                <code>SAXParserFactory.setNamespaceAware(true) must be called</code> to create a namespace aware SAX parser.
     */
    public WOMParser(SAXParserFactory factory) {
        this.parser = new XMLParserImpl(factory);
        this.context = new ParserContext(this, this.parser);
    }

    /**
     * Creates a new WOMParser that will use the given XMLParser to parse the WSDL. The provided {@link XMLParser} will
     * send SAX events to the WOM ContentHandler.
     *
     * @param parser non null.
     */
    public WOMParser(XMLParser parser) {
        this.parser = parser;
        this.context = new ParserContext(this, this.parser);
    }


    /**
     * Parses a new XML Schema document.
     * <p/>
     * <p/>
     * Note that if the {@link InputSource} does not have a system ID,
     * WOM will fail to resolve them.
     *
     * @return Gives {@link WSDLSet}, null if there was any error.
     */
    public WSDLSet parse(InputSource wsdl) throws IOException, SAXException {
        context.parse(wsdl);
        return context.getResult();

    }

    /**
     * Parses a WSDL document from the given {@link File}.
     *
     * @param wsdl non-null
     * @return Gives {@link WSDLSet}, null if there was any error.
     */
    public WSDLSet parse(File wsdl) throws IOException, SAXException {
        return parse(wsdl.toURL());
    }

    /**
     * Parses a WSDL document from the given {@link InputStream}.
     * <p/>
     * <p/>
     * When using this method, WOM does not know the system ID of
     * this document, therefore, when this stream contains relative
     * references to other schemas, WOM will fail to resolve them.
     * To specify an system ID with a stream, use {@link InputSource}
     *
     * @return Gives {@link WSDLSet}, null if there was any error.
     */
    public WSDLSet parse(InputStream wsdl) throws SAXException {
        context.parse(new InputSource(wsdl));
        return context.getResult();
    }


    /**
     * Parses a WSDL document from the given {@link URL}.
     *
     * @param wsdl non-null
     * @return Gives {@link WSDLSet}, null if there was any error.
     */
    public WSDLSet parse(URL wsdl) throws SAXException {
        return parse(wsdl.toExternalForm());
    }

    /**
     * Parses a WSDL document from the given systemId
     *
     * @param systemId non-null
     * @return Gives {@link WSDLSet}, null if there was any error.
     */
    public WSDLSet parse(String systemId) throws SAXException {
        context.parse(new InputSource(systemId));
        return context.getResult();
    }

    /**
     * Parses a WSDL document from the given {@link Reader}.
     * <p/>
     * <p/>
     * When using this method, WOM does not know the system ID of
     * this document, therefore, when this reader contains relative
     * references to other schemas, WOM will fail to resolve them.
     * To specify an system ID with a reader, use {@link InputSource}
     *
     * @return Gives {@link WSDLSet}, null if there was any error.
     */
    public WSDLSet parse(Reader reader) throws SAXException {
        context.parse(new InputSource(reader));
        return context.getResult();
    }

    /**
     * Gets the parsed wsdl as {@link WSDLSet}. Call this method only after the parsing is done.
     */
    public WSDLSet getResult() throws SAXException {
        return context.getResult();
    }


    public EntityResolver getEntityResolver() {
        return entityResolver;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    /**
     * Set {@link EntityResolver} to resolve &lt;wsdl:import
     */
    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    /**
     * {@link ErrorHandler} to receive any error/warning that happens during parsing.
     */
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    /**
     * WOM will fire events for the schema under &lt;wsdl:types section to this ContentHandler. The idea is that
     * the schema inside a WSDL can be parsed and a schema object model can be created and used by for the purpose of
     * data binding.
     *
     * @param handler always non-null
     */
    public void setSchemaContentHandler(ContentHandler handler) {
        context.setSchemaContentHandler(handler);
    }

    public void addWSDLExtensionHandler(WSDLExtensionHandler extension){
        context.addWSDLExtensionHandler(extension);
    }

    /**
     * Gives the default XMLParser instance. This can be used when a custom XMLParser wants to use the default
     * as a fallback parser.
     */
    public XMLParser getDefaultXMLParserInstance(){
        return parser==null?new XMLParserImpl():parser;
    }

    /**
     * Gets the parser implemented as a ContentHandler.
     *
     * One can feed WSDL or XML Schema as SAX events to this interface to
     * parse a WSDL. To parse multiple WSDL files, feed multiple
     * sets of events.
     *
     * <p>
     * If you don't send a complete event sequence from a startDocument
     * event to an endDocument event, the state of WOMParser can become
     * unstable. This sometimes happen when you encounter an error while
     * generating SAX events. Don't call the getResult method in that case.
     *
     */
    public ContentHandler getParserHandler() {
        WSDLContentHandlerEx ch = context.createWSDLContentHandler();
        Definitions rootHandler = new Definitions(ch, null);
        ch.setRootHandler(rootHandler);
        return ch;
    }

}
