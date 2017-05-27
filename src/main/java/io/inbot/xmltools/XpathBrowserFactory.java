package io.inbot.xmltools;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import org.w3c.dom.Node;

public class XpathBrowserFactory {

    private final PooledXmlParser parser;
    private final XPathExpressionCache cache;

    public XpathBrowserFactory(PooledXmlParser parser, XPathExpressionCache cache) {
        this.parser = parser;
        this.cache = cache;
    }

    public XPathBrowser browse(Reader r) {
        return new XPathBrowser(cache, parser.parseXml(r));
    }

    public XPathBrowser browse(InputStream is, Charset encoding) {
        return new XPathBrowser(cache, parser.parseXml(is, encoding));
    }

    public XPathBrowser browse(String xml) {
        return new XPathBrowser(cache, parser.parseXml(xml));
    }

    public XPathBrowser browse(final Node node) {
        return new XPathBrowser(cache, node);
    }

}
