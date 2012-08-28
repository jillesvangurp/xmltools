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

import java.io.IOException;
import java.io.StringReader;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

@Test
public class ValidXMLCharacterFilterReaderTest {

    public void testUnicode() {
        AssertJUnit.assertFalse("Is not a valid character", ValidXMLCharacterFilterReader.isAllowedInXml(0x1a));
    }

    public void testDropBadChars() throws IOException {
        final String test = "OK" + (char) 0x1a;
        final ValidXMLCharacterFilterReader r = new ValidXMLCharacterFilterReader(new StringReader(test));
        final int length = r.read(new char[test.length()], 0, test.length());
        r.close();
        AssertJUnit.assertEquals(2, length);
    }

    public void testReadString() throws IOException {
        final String test = "_OK_" + (char) 0x1a + "_OK_foo_OK__OK__OK__OK__OK_"; // 32 chars
        final ValidXMLCharacterFilterReader r = new ValidXMLCharacterFilterReader(new StringReader(test));
        final StringBuilder builder = new StringBuilder();
        final char[] buf = new char[4];
        int length;
        while ((length = r.read(buf, 0, 4)) != -1) {
            for (int i = 0; i < length; i++) {
                builder.append(buf[i]);
            }
        }
        r.close();
        AssertJUnit.assertEquals(31, builder.length());
    }

    public void testEmpty() throws IOException {
        final String test = ""; // 0 chars
        final ValidXMLCharacterFilterReader r = new ValidXMLCharacterFilterReader(new StringReader(test));
        final int length = r.read(new char[50], 0, 50);
        r.close();
        AssertJUnit.assertEquals(-1, length);
    }
}
