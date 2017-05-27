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

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import javax.xml.parsers.ParserConfigurationException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

@Test
public class PooledXmlParserTest {

    private PooledXmlParser pooledXmlParser;

    @BeforeMethod
    public void before() {
        pooledXmlParser = new PooledXmlParser(20,20);
    }

    public void shouldParseFile() throws SAXException, IOException {
        final Reader inputStream = new FileReader("src/test/resources/test.xml");
        pooledXmlParser.parseXml(inputStream);
    }

    public void shouldParseString() throws SAXException {
        pooledXmlParser.parseXml("<xml />");
    }

    @Test(expectedExceptions = SAXParseException.class)
    public void shouldNotParseInvalidXml() throws ParserConfigurationException, SAXException, IOException {
        pooledXmlParser.parseXml("<noxml>");
    }
}
