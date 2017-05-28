/**
 * Copyright (c) 2012-2017, Jilles van Gurp
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
package io.inbot.xmltools;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Per thread caching of xpath expressions. Compiling expressions is way more expensive than reusing them. Unfortunately, they are
 * not thread safe. So this class provides a per thread cache of XPathExpression. The thread cache uses the thread ids for storing the
 * per thread expresssion cache. Each expression is stored with the uncompiled string as its key.
 *
 */
public class XPathExpressionCache {

	private final LoadingCache<Long,Cache<String, XPathExpression>> perThreadCaches;

    private final XPath xpath;

	public XPathExpressionCache(int threadCacheSize, int threadCacheExpireMinutes, final int cacheSize, final int evictionOfUnusedInMinutes) {
	    // return a cache of caches
	    perThreadCaches=CacheBuilder.newBuilder()
	        .maximumSize(threadCacheSize)
            .expireAfterAccess(threadCacheExpireMinutes, TimeUnit.MINUTES)
	        .build(new CacheLoader<Long, Cache<String, XPathExpression>>() {

                @Override
                public Cache<String, XPathExpression> load(Long id) throws Exception {
                    return CacheBuilder.newBuilder()
                        .maximumSize(cacheSize)
                        .expireAfterAccess(evictionOfUnusedInMinutes, TimeUnit.MINUTES)
                        .<String, XPathExpression>build();
                }
            }
	        );

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
            return perThreadCaches.get(Thread.currentThread().getId()).get(expression, () ->xpath.compile(expression));
		} catch (ExecutionException e) {
			if(e.getCause() instanceof XPathExpressionException) {
				throw (XPathExpressionException)e.getCause();
			} else {
				throw new IllegalStateException(e.getCause());
			}
		}
    }
}
