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
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;

public abstract class AbstractHandler implements WSDLEventReceiver {
    protected static final String WSDL_NS = "http://schemas.xmlsoap.org/wsdl/";

    protected AbstractHandler(WSDLEventSource source, AbstractHandler parent, int parentCookie) {

        _parent = parent;
        _source = source;
        _cookie = parentCookie;
    }

    /**
     * Parent AbstractHandler, if any.
     * If this is the root handler, this field will be null.
     */
    protected final AbstractHandler _parent;

    /**
     * Event source.
     */
    protected final WSDLEventSource _source;

    /**
     * This method will be implemented by the generated code
     * and returns a reference to the current runtime.
     */
    protected abstract WSDLContentHandler getRuntime();

    /**
     * Cookie assigned by the parent.
     * <p/>
     * This value will be passed to the onChildCompleted handler
     * of the parent.
     */
    protected final int _cookie;

    // used to copy parameters to (enter|leave)(Element|Attribute) events.
    //protected String localName,uri,qname;


    /**
     * Notifies the completion of a child object.
     *
     * @param result       The parsing result of the child state.
     * @param cookie       The cookie value passed to the child object
     *                     when it is created.
     * @param needAttCheck This flag is true when the callee needs to call the
     *                     processAttribute method to check attribute transitions.
     *                     This flag is set to false when this method is triggered by
     *                     attribute transition.
     */
    protected abstract void onChildCompleted(Object result, int cookie, boolean needAttCheck) throws SAXException;

//
//
// spawns a new child object from event handlers.
//

    //

    public void spawnChildFromEnterElement(WSDLEventReceiver child,
                                           String uri, String localname, String qname, Attributes atts) throws SAXException {

        int id = _source.replace(this, child);
        _source.sendEnterElement(id, uri, localname, qname, atts);
    }

    public void spawnChildFromEnterAttribute(WSDLEventReceiver child,
                                             String uri, String localname, String qname) throws SAXException {

        int id = _source.replace(this, child);
        _source.sendEnterAttribute(id, uri, localname, qname);
    }

    public void spawnChildFromLeaveElement(WSDLEventReceiver child,
                                           String uri, String localname, String qname) throws SAXException {

        int id = _source.replace(this, child);
        _source.sendLeaveElement(id, uri, localname, qname);
    }

    public void spawnChildFromLeaveAttribute(WSDLEventReceiver child,
                                             String uri, String localname, String qname) throws SAXException {

        int id = _source.replace(this, child);
        _source.sendLeaveAttribute(id, uri, localname, qname);
    }

    public void spawnChildFromText(WSDLEventReceiver child,
                                   String value) throws SAXException {

        int id = _source.replace(this, child);
        _source.sendText(id, value);
    }

//
//
// reverts to the parent object from the child handler
//

    //

    public void revertToParentFromEnterElement(Object result, int cookie,
                                               String uri, String local, String qname, Attributes atts) throws SAXException {

        int id = _source.replace(this, _parent);
        _parent.onChildCompleted(result, cookie, true);
        _source.sendEnterElement(id, uri, local, qname, atts);
    }

    public void revertToParentFromLeaveElement(Object result, int cookie,
                                               String uri, String local, String qname) throws SAXException {

        if (uri == WSDLContentHandler.IMPOSSIBLE && uri == local && uri == qname && _parent == null)
            // all the handlers are properly finalized.
            // quit now, because we don't have any more NGCCHandler.
            // see the endDocument handler for detail
            return;

        int id = _source.replace(this, _parent);
        _parent.onChildCompleted(result, cookie, true);
        _source.sendLeaveElement(id, uri, local, qname);
    }

    public void revertToParentFromEnterAttribute(Object result, int cookie,
                                                 String uri, String local, String qname) throws SAXException {

        int id = _source.replace(this, _parent);
        _parent.onChildCompleted(result, cookie, true);
        _source.sendEnterAttribute(id, uri, local, qname);
    }

    public void revertToParentFromLeaveAttribute(Object result, int cookie,
                                                 String uri, String local, String qname) throws SAXException {

        int id = _source.replace(this, _parent);
        _parent.onChildCompleted(result, cookie, true);
        _source.sendLeaveAttribute(id, uri, local, qname);
    }

    public void revertToParentFromText(Object result, int cookie,
                                       String text) throws SAXException {

        int id = _source.replace(this, _parent);
        _parent.onChildCompleted(result, cookie, true);
        _source.sendText(id, text);
    }

    protected void validateAttribute(ErrorHandler handler, Attributes attributes, int... understoodAtts) throws SAXException {
        for (int i = 0; i < understoodAtts.length; i++) {
            if (understoodAtts[i] == 1)
                continue;
            handler.warning(new SAXParseException(Messages.format(Messages.UNKNOWN_ATTRIBUTE, attributes.getQName(i)), getRuntime().getLocator()));
        }
    }

    protected String processDocumentation(String uri, String localName, String qname) throws SAXException {
        TransformerHandler handler;
        try {
            handler = ((SAXTransformerFactory) TransformerFactory.newInstance()).newTransformerHandler();
        } catch (TransformerConfigurationException e) {
            throw new SAXParseException(e.getMessage(), getRuntime().getLocator(), e);
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(os);
        handler.setResult(result);
        getRuntime().redirectSubtree(handler, uri, localName, qname);
        return os.toString();
    }

//
//
// error handler
//

    //

    public void unexpectedEnterElement(String qname) throws SAXException {
        getRuntime().unexpectedX('<' + qname + '>');
    }

    public void unexpectedLeaveElement(String qname) throws SAXException {
        getRuntime().unexpectedX("</" + qname + '>');
    }

    public void unexpectedEnterAttribute(String qname) throws SAXException {
        getRuntime().unexpectedX('@' + qname);
    }

    public void unexpectedLeaveAttribute(String qname) throws SAXException {
        getRuntime().unexpectedX("/@" + qname);
    }

}




