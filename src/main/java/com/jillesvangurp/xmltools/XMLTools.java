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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Some helper methods for parsing xml documents.
 */
public class XMLTools {

    private static final class DocumentBuilderThreadLocal extends ThreadLocal<DocumentBuilder> {

        @Override
		protected DocumentBuilder initialValue() {
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setValidating(false);
                return dbf.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
		public DocumentBuilder get() {
            DocumentBuilder documentBuilder = super.get();
            // reset the documentBuilder before using it
            documentBuilder.reset();
            return documentBuilder;
        }
    }

    // use a thread local to cache the document builders (not documented as thread safe)
    private static ThreadLocal<DocumentBuilder> dbThreadLocal = new DocumentBuilderThreadLocal();

    private XMLTools() {
    }

    public static Document parseXml(final Reader r) throws SAXException, IOException {
        final InputSource inputSource = new InputSource(r);
        return XMLTools.parse(inputSource);
    }

	public static Document parseXml(final InputStream inputStream, final String encoding)
	        throws SAXException, IOException {
	    return parseXml(new BufferedReader(new InputStreamReader(inputStream, encoding)));
	}

    public static Document parseXml(final String xmlBuffer) throws SAXException {
        try {
			return XMLTools.parseXml(new ByteArrayInputStream(xmlBuffer.getBytes("UTF-8")), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("utf-8 not supported", e);
		} catch (IOException e) {
			throw new IllegalStateException("ioerror parsing string", e);
		}
    }

    private static Document parse(final InputSource inputSource) throws SAXException,
            IOException {
        return dbThreadLocal.get().parse(inputSource);
    }
}
