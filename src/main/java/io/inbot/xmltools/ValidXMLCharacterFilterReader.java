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

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Remove characters from an imput stream that are illegal in XML. Useful when parsing content from an unreliable source
 * if you don't want to break on every little error. This merely drops any characters that don't pass the filter.
 */
public class ValidXMLCharacterFilterReader extends FilterReader {

    /**
     * Create a new {@link ValidXMLCharacterFilterReader}.
     *
     * @param in reader
     */
    public ValidXMLCharacterFilterReader(final Reader in) {
        super(in);
    }

    @Override
    public int read(final char[] buf, final int off, final int len) throws IOException {
        int length = len;
        int offSet = off;
        if (length <= 0) {
            return 0;
        }
        final int start = offSet;
        int c;

        while (length > 0 && (c = super.in.read()) != -1) {
            if (ValidXMLCharacterFilterReader.isAllowedInXml(c)) {
                buf[offSet++] = (char) c;
                --length;
            }
        }
        final int count = offSet - start;
        return count > 0 ? count : -1;
    }

    /**
     * The xml specification defines these character hex codes as allowed: #x9 | #xA | #xD | [#x20-#xD7FF] |
     * [#xE000-#xFFFD] | [#x10000-#x10FFFF] Characters outside this range will cause parsers to reject the xml as not
     * well formed.
     *
     * @param c
     *        a character
     * @return true if character is allowed in an XML document
     */
    public static boolean isAllowedInXml(final int c) {
        boolean ok = false;
        if (c >= 0x10000 && c <= 0x10FFFF) {
            ok = true;
        } else if (c >= 0xE000 && c <= 0xFFFD) {
            ok = true;
        } else if (c >= 0x20 && c <= 0xD7FF) {
            ok = true;
        } else if (c == 0x9 || c == 0xA || c == 0xD) {
            ok = true;
        }
        return ok;
    }
}
