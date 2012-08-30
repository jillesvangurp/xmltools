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
