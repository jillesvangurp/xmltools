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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Helper class for parsing xml documents in a way that reuses document builders. Uses a configurable cache for document builders.
 *
 * DocumentBuilder is NOT thread safe and creating them is somewhat expensive. So this helper class puts them in a nice guava class
 * and uses the thread id as the key. So each thread gets its own DocumentBuilder.
 */
public class PooledXmlParser {

    private LoadingCache<Long, DocumentBuilder> documentBuilderPool;

    public PooledXmlParser(int threads, int expirationMinutes) {
        // per thread cache of document builders
        documentBuilderPool = CacheBuilder.newBuilder()
            .maximumSize(threads)
            .build(new CacheLoader<Long, DocumentBuilder>() {

                @Override
                public DocumentBuilder load(Long id) throws Exception {
                    try {
                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        dbf.setValidating(false);
                        DocumentBuilder builder = dbf.newDocumentBuilder();
                        return builder;
                    } catch (ParserConfigurationException e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        );
    }

    public Document parseXml(final Reader r) throws SAXException, IOException {
        final InputSource inputSource = new InputSource(r);
        return parse(inputSource);
    }

	public Document parseXml(final InputStream inputStream, final String encoding) throws SAXException, IOException {
	    return parseXml(new BufferedReader(new InputStreamReader(inputStream, encoding)));
	}

    public Document parseXml(final String xmlBuffer) throws SAXException {
        try {
			return parseXml(new ByteArrayInputStream(xmlBuffer.getBytes("UTF-8")), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("utf-8 not supported", e);
		} catch (IOException e) {
			throw new IllegalStateException("ioerror parsing string", e);
		}
    }

    public Document parse(final InputSource inputSource) throws SAXException,
            IOException {
        return getDocumentBuilderForCurrentThread().parse(inputSource);
    }

    public DocumentBuilder getDocumentBuilderForCurrentThread()  {
        try {
            return documentBuilderPool.get(Thread.currentThread().getId());
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }
}
