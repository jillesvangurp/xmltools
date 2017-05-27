package io.inbot.xmltools;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XpathBrowserFactory {

    private final PooledXmlParser parser;
    private final XPathExpressionCache cache;

    public XpathBrowserFactory(PooledXmlParser parser, XPathExpressionCache cache) {
        this.parser = parser;
        this.cache = cache;
    }

    public XPathBrowser browse(Reader r) throws SAXException, IOException {
        return new XPathBrowser(cache, parser.parseXml(r));
    }

    public XPathBrowser browse(InputStream is, String encoding) throws SAXException, IOException {
        return new XPathBrowser(cache, parser.parseXml(is, encoding));
    }

    public XPathBrowser browse(String xml) throws SAXException {
        return new XPathBrowser(cache, parser.parseXml(xml));
    }

    public XPathBrowser browse(final Node node) {
        return new XPathBrowser(cache, node);
    }

}
