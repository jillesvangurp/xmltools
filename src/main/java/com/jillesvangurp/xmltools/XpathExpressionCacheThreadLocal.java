package com.jillesvangurp.xmltools;

/**
 * This thread local ensures that each thread has its own cache for caching xpath expressions.
 */
class XpathExpressionCacheThreadLocal extends ThreadLocal<XPathExpressionCache> {
    /*
     * (non-Javadoc)
     * @see java.lang.ThreadLocal#initialValue()
     */
    @Override
    protected XPathExpressionCache initialValue() {
    	return new XPathExpressionCache(1000, 15);
    }
}