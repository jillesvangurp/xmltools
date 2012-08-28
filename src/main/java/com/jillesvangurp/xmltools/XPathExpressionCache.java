/**
 * Copyright (c) 2012, Jilles van Gurp
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.jillesvangurp.xmltools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * This class may be used to cache compiled {@link XPathExpression} instances. XPathExpression is NOT threadsafe so use
 * a ThreadLocal like in the {@link XPathBrowser} class. method to evaluate xpath expressions.
 */
class XPathExpressionCache {

    private final XPath xpath;

    private final Map<String, XPathExpression> cache = new HashMap<String, XPathExpression>();

    /**
     * Use a simple XPath instance with a simple NamespaceContext that resolves every prefix to http://domain.com/[prefix].
     */
    XPathExpressionCache() {
        final XPathFactory xpf = XPathFactory.newInstance();
        xpath = xpf.newXPath();
        xpath.setNamespaceContext(new NamespaceContext() {

            @Override
			public String getNamespaceURI(String prefix) {
                // hack to keep xpath happy
                return "http://domain.com/" + prefix;
            }

            @Override
			@SuppressWarnings("rawtypes")
            public Iterator getPrefixes(String val) {
                // Dummy implementation - not used!
                return null;
            }

            @Override
			public String getPrefix(String uri) {
                // Dummy implementation - not used!
                return null;
            }
        });
    }


    /**
     * Provide your own custom XPath.
     * @param xpath
     */
    XPathExpressionCache(XPath xpath) {
		this.xpath = xpath;

    }

    /**
     * If the expression has been compiled already, return the cached instance. Otherwise compile a new one and cache it.
     *
     * @param expression
     * @return
     * @throws XPathExpressionException
     */
    XPathExpression getInstance(final String expression) throws XPathExpressionException {
        XPathExpression expr = cache.get(expression);
        if (expr == null) {
            expr = xpath.compile(expression);
            cache.put(expression, expr);
        }
        return expr;
    }
}
