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
package org.jvnet.wom.impl.parser.handler;

import org.jvnet.wom.parser.WSDLEventReceiver;
import org.jvnet.wom.parser.WSDLEventSource;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.AttributesImpl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;

public class WSDLContentHandler implements ContentHandler, WSDLEventSource {
    private Locator locator;

    /**
     * Stack of {@link Attributes}
     */
    private final Stack<AttributesImpl> attStack = new Stack<AttributesImpl>();

    /**
     * Current attribute set, always equal to attStack.peek()
     */
    private AttributesImpl currentAtts;

    /**
     * Accumulated text
     */
    StringBuffer text = new StringBuffer();

    WSDLEventReceiver currentHandler;


    /**
     * Get the source location of the current event
     */
    public Locator getLocator() {
        return locator;
    }

    /**
     * Gets the attributes on the current element
     */
    public Attributes getCurrentAttributes() {
        return currentAtts;
    }

    private void reset() {
        attStack.clear();
        currentAtts = null;
        currentHandler = null;
        indent = 0;
        locator = null;
        namespaces.clear();
        needIndent = true;
        redirect = null;
        redirectionDepth = 0;
        text = new StringBuffer();

        // add a dummy attributes at the bottom as a "centinel."
        attStack.push(new AttributesImpl());
    }


    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    public void startDocument() throws SAXException {

    }

    /**
     * Impossible token. This value can never be a valid XML name.
     */
    static final String IMPOSSIBLE = "\u0000";

    public void endDocument() throws SAXException {
        // consume the special "end document" token so that all the handler
        // currently at the stack will revert to their respective parents.
        //
        // this is necessary to handle a grammar like
        // <start><ref name="X"/></start>
        // <define name="X">
        //   <element name="root"><empty/></element>
        // </define>
        //
        // With this grammar, when the endElement event is consumed, two handler
        // are on the stack (because a child object won't revert to its parent
        // unless it sees a next event.)

        // pass around an "impossible" token.
        currentHandler.leaveElement(IMPOSSIBLE, IMPOSSIBLE, IMPOSSIBLE);

        reset();
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if (redirect != null)
            redirect.startPrefixMapping(prefix, uri);
        else {
            namespaces.add(prefix);
            namespaces.add(uri);
        }
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        if (redirect != null)
            redirect.endPrefixMapping(prefix);
        else {
            namespaces.remove(namespaces.size() - 1);
            namespaces.remove(namespaces.size() - 1);
        }
    }

    public void startElement(String uri, String localname, String qname, Attributes atts) throws SAXException {
        if (redirect != null) {
            redirect.startElement(uri, localname, qname, atts);
            redirectionDepth++;
        } else {
            processPendingText(true);
            //        System.out.println("startElement:"+localname+"->"+_attrStack.size());
            currentHandler.enterElement(uri, localname, qname, atts);
        }
    }

    /**
     * Called by the generated handler code when an enter element
     * event is consumed.
     * <p/>
     * <p/>
     * Pushes a new attribute set.
     * <p/>
     * <p/>
     * Note that attributes are NOT pushed at the startElement method,
     * because the processing of the enterElement event can trigger
     * other attribute events and etc.
     * <p/>
     * This method will be called from one of handler when it truely
     * consumes the enterElement event.
     */
    public void onEnterElementConsumed(
            String uri, String localName, String qname, Attributes atts) throws SAXException {
        attStack.push(currentAtts = new AttributesImpl(atts));
        nsEffectiveStack.push(nsEffectivePtr);
        nsEffectivePtr = namespaces.size();
    }

    public void onLeaveElementConsumed(String uri, String localName, String qname) throws SAXException {
        attStack.pop();
        if (attStack.isEmpty())
            currentAtts = null;
        else
            currentAtts = attStack.peek();
        nsEffectivePtr = nsEffectiveStack.pop();
    }

    public void endElement(String uri, String localname, String qname) throws SAXException {
        if (redirect != null) {
            redirect.endElement(uri, localname, qname);
            redirectionDepth--;

            if (redirectionDepth != 0)
                return;

            // finished redirection.
            for (int i = 0; i < namespaces.size(); i += 2)
                redirect.endPrefixMapping((String) namespaces.get(i));
            redirect.endDocument();

            redirect = null;
            // then process this element normally
        }
    }

    public void characters(char ch[], int start, int length) throws SAXException {
        if (redirect != null)
            redirect.characters(ch, start, length);
        else
            text.append(ch, start, length);
    }

    public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {
        if (redirect != null)
            redirect.ignorableWhitespace(ch, start, length);
        else
            text.append(ch, start, length);
    }

    public int getAttributeIndex(String uri, String localname) {
        return currentAtts.getIndex(uri, localname);
    }

    public void consumeAttribute(int index) throws SAXException {
        final String uri = currentAtts.getURI(index);
        final String local = currentAtts.getLocalName(index);
        final String qname = currentAtts.getQName(index);
        final String value = currentAtts.getValue(index);
        currentAtts.removeAttribute(index);

        currentHandler.enterAttribute(uri, local, qname);
        currentHandler.text(value);
        currentHandler.leaveAttribute(uri, local, qname);
    }


    public void processingInstruction(String target, String data) throws SAXException {
        if (redirect != null)
            redirect.processingInstruction(target, data);
    }

    public void skippedEntity(String name) throws SAXException {
        if (redirect != null)
            redirect.skippedEntity(name);
    }

    public int replace(WSDLEventReceiver _old, WSDLEventReceiver _new) {
        if (_old != currentHandler)
            throw new IllegalStateException();
        currentHandler = _new;

        return 0;
    }

    public void sendEnterElement(int receiverThreadId, String uri, String localName, String qname, Attributes atts) throws SAXException {
        currentHandler.enterElement(uri, localName, qname, atts);
    }

    public void sendLeaveElement(int receiverThreadId, String uri, String localName, String qname) throws SAXException {
        currentHandler.leaveElement(uri, localName, qname);
    }

    public void sendText(int receiverThreadId, String value) throws SAXException {
        currentHandler.text(value);
    }

    public void sendEnterAttribute(int receiverThreadId, String uri, String localName, String qname) throws SAXException {
        currentHandler.enterAttribute(uri, localName, qname);
    }

    public void sendLeaveAttribute(int receiverThreadId, String uri, String localName, String qname) throws SAXException {
        currentHandler.leaveAttribute(uri, localName, qname);
    }

    private void processPendingText(boolean ignorable) throws SAXException {
        if (!ignorable || text.toString().trim().length() != 0) {
            currentHandler.text(text.toString());
        }

        //truncate StringBuffer but avoid excessive allocation
        if (text.length() > 1024)
            text = new StringBuffer();
        else
            text.setLength(0);

    }

    public void processList(String str) throws SAXException {
        StringTokenizer t = new StringTokenizer(str, " \t\r\n");
        while (t.hasMoreTokens())
            currentHandler.text(t.nextToken());
    }

    //
    //
    // redirection of SAX2 events.
    //
    //

    /**
     * When redirecting a sub-tree, this value will be non-null.
     */
    private ContentHandler redirect = null;

    /**
     * Counts the depth of the elements when we are re-directing
     * a sub-tree to another ContentHandler.
     */
    private int redirectionDepth = 0;

    /**
     * This method can be called only from the enterElement handler.
     * The sub-tree rooted at the new element will be redirected
     * to the specified ContentHandler.
     * <p/>
     * <p/>
     * Currently active ContentHandler will only receive the leaveElement
     * event of the newly started element.
     *
     * @param uri,local,qname Parameters passed to the enter element event. Used to
     *                        simulate the startElement event for the new ContentHandler.
     */
    public void redirectSubtree(ContentHandler child,
                                String uri, String local, String qname) throws SAXException {

        redirect = child;
        redirect.setDocumentLocator(locator);
        redirect.startDocument();

        // TODO: when a prefix is re-bound to something else,
        // the following code is potentially dangerous. It should be
        // modified to report active bindings only.
        for (int i = 0; i < namespaces.size(); i += 2)
            redirect.startPrefixMapping(
                    (String) namespaces.get(i),
                    (String) namespaces.get(i + 1)
            );

        redirect.startElement(uri, local, qname, currentAtts);
        redirectionDepth = 1;
    }

    //
//
    // validation context implementation
    //
    //
    /**
     * in-scope namespace mapping.
     * namespaces[2n  ] := prefix
     * namespaces[2n+1] := namespace URI
     */
    private final ArrayList namespaces = new ArrayList();
    /**
     * Index on the namespaces array, which points to
     * the top of the effective bindings. Because of the
     * timing difference between the startPrefixMapping method
     * and the execution of the corresponding actions,
     * this value can be different from <code>namespaces.size()</code>.
     * <p/>
     * For example, consider the following schema:
     * <pre><xmp>
     *  <oneOrMore>
     *   <element name="foo"><empty/></element>
     *  </oneOrMore>
     *  code fragment X
     *  <element name="bob"/>
     * </xmp></pre>
     * Code fragment X is executed after we see a startElement event,
     * but at this time the namespaces variable already include new
     * namespace bindings declared on "bob".
     */
    private int nsEffectivePtr = 0;

    /**
     * Stack to preserve old nsEffectivePtr values.
     */
    private final Stack<Integer> nsEffectiveStack = new Stack<Integer>();

    public String resolveNamespacePrefix(String prefix) {
        for (int i = nsEffectivePtr - 2; i >= 0; i -= 2)
            if (namespaces.get(i).equals(prefix))
                return (String) namespaces.get(i + 1);

        // no binding was found.
        if (prefix.equals("")) return "";  // return the default no-namespace
        if (prefix.equals("xml"))    // pre-defined xml prefix
            return "http://www.w3.org/XML/1998/namespace";
        else return null;    // prefix undefined
    }


    // error reporting
    protected void unexpectedX(String token) throws SAXException {
        throw new SAXParseException(MessageFormat.format(
                "Unexpected {0} appears at line {1} column {2}",
                token,
                getLocator().getLineNumber(),
                getLocator().getColumnNumber()),
                getLocator());
    }

    //
    //
    // trace functions
    //
    //
    private int indent = 0;
    private boolean needIndent = true;

    private void printIndent() {
        for (int i = 0; i < indent; i++)
            System.out.print("  ");
    }

    public void trace(String s) {
        if (needIndent) {
            needIndent = false;
            printIndent();
        }
        System.out.print(s);
    }

    public void traceln(String s) {
        trace(s);
        trace("\n");
        needIndent = true;
    }
}
