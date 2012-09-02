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

import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;


public class XPathExpressionCache {

	private final Cache<String, XPathExpression> cache;

    private final XPath xpath;

	public XPathExpressionCache(int cacheSize, int evictionOfUnusedInMinutes) {
		cache = CacheBuilder.newBuilder().concurrencyLevel(1).maximumSize(cacheSize).expireAfterAccess(evictionOfUnusedInMinutes, TimeUnit.MINUTES).build();
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

	public XPathExpression getExpression(final String expression) throws XPathExpressionException {
        try {
			return cache.get(expression, new Callable<XPathExpression>() {
				@Override
				public XPathExpression call() throws Exception {
					return xpath.compile(expression);
				}
			});
		} catch (ExecutionException e) {
			if(e.getCause() instanceof XPathExpressionException) {
				throw (XPathExpressionException)e.getCause();
			} else {
				throw new IllegalStateException(e.getCause());
			}
		}
    }
}
