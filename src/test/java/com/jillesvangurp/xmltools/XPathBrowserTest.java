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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import javax.xml.xpath.XPathExpressionException;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

@Test
public class XPathBrowserTest {

    private XPathBrowser xpath;

    private Node root;

    @BeforeMethod
	public void before() throws Exception {
        xpath = XPathBrowser.browse(this.getClass().getResourceAsStream("/test.xml"), "utf-8");
        root = xpath.getNodeList("/root").item(0);
    }

    public void shouldExtractFromNamespacedXml() throws SAXException, IOException, XPathExpressionException {
        xpath = XPathBrowser.browse(this.getClass().getResourceAsStream("/test-with-ns.xml"), "utf-8");
        AssertJUnit.assertEquals(0.42,xpath.getDouble("/root/double"));
    }

    public void shouldHandleBooleans() throws XPathExpressionException {
    	assertThat("should be true", xpath.getBoolean("/root/bool"));
    	assertThat("should be true", xpath.getBoolean(root, "bool"));
        xpath.cd("/root/bool");
        assertThat("should get value from current node", xpath.getBoolean());
        assertThat("should be false", !xpath.getBoolean(root, "string"));
    }

    public void shouldHandleStrings() throws XPathExpressionException {
    	assertThat(xpath.getString("/root/string"), is("foo"));
    	assertThat(xpath.getString(root, "string"), is("foo"));
        xpath.cd("/root/string");
        assertThat(xpath.getString(), is("foo"));

    }

    @Test(expectedExceptions = NumberFormatException.class)
    public void shouldHandleLongs() throws XPathExpressionException {
    	assertThat(xpath.getLong("/root/long"), is(42l));
    	assertThat(xpath.getLong(root, "long"), is(42l));
        xpath.cd("/root/long");
        assertThat(xpath.getLong(), is(42l));
        xpath.getLong("/root/string");
    }

    @Test(expectedExceptions = NumberFormatException.class)
    public void shouldHandleDoubles() throws XPathExpressionException {
    	assertThat(xpath.getDouble("/root/double"), is(0.42));
    	assertThat(xpath.getDouble(root, "double"), is(0.42));
        xpath.cd("/root/double");
        assertThat(xpath.getDouble(), is(0.42));
        xpath.getDouble("/root/string");
    }

    @Test(expectedExceptions = NumberFormatException.class)
    public void shouldHandleInts() throws XPathExpressionException {
    	assertThat(xpath.getInt("/root/long"), is(42));
    	assertThat(xpath.getInt(root, "long"), is(42));
        xpath.cd("/root/long");
        assertThat(xpath.getInt(), is(42));
        xpath.getInt("/root/string");
    }

    public void shouldHandleNodeLists() throws XPathExpressionException {
    	assertThat(xpath.getNodeList("/root/list/item").getLength(),is(2));
    	assertThat(xpath.getNodeList(root, "list/item").getLength(), is(2));
    }

    public void shouldGetSubNode() throws XPathExpressionException {
    	assertThat(xpath.getSubNode(root, "string"), notNullValue());
    }

    public void shouldGetStringValuesFromNodeList() throws XPathExpressionException {
        String[] stringValues = xpath.getStringValues("/root/list/item");
        assertThat(stringValues.length, is(2));
        assertThat(stringValues[0], is("1"));
        stringValues = xpath.getStringValues(root, "list/item");
        assertThat(stringValues.length, is(2));
        assertThat(stringValues[0], is("1"));
    }

    public void shouldCd() throws XPathExpressionException {
    	Node cd = xpath.cd().currentNode();
    	assertThat(cd, is(xpath.root()));
    	cd = xpath.cd("root").currentNode();
    	assertThat(cd, is(xpath.getFirstNode(xpath.root(), "/root")));
    	cd = xpath.cd("list").currentNode();
    	assertThat(cd, is(xpath.getFirstNode(xpath.root(), "/root/list")));
    	cd = xpath.cd().currentNode();
    	assertThat(cd, is(xpath.root()));
    }

    public void shouldLs() throws XPathExpressionException {
		int count = 0;
		Node originalRoot = xpath.currentNode();
		for( XPathBrowser b: xpath.ls("/root/list/item")) {
			count++;
	    	assertThat("browser should cd to node", originalRoot != b.currentNode());
		}
		assertThat(count, is(2));
		assertThat("browser should revert back to original node", originalRoot == xpath.currentNode());
	}

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void shouldNotAllowRemoveOnLsIterator() throws XPathExpressionException {
    	xpath.ls("/root/list/item").iterator().remove();
    }

	public void shouldLsItemsWithoutExpression() throws XPathExpressionException {
    	xpath.cd("/root/list");
    	int count = 0;
    	Node originalRoot = xpath.currentNode();
    	for( XPathBrowser b: xpath.ls()) {
    		count++;
        	assertThat("browser should cd to node", originalRoot != b.currentNode());
    	}
    	assertThat(count, is(2));
    	assertThat("browser should revert back to original node", originalRoot == xpath.currentNode());
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void shouldThrowExceptionOnGetFirstNodeThatDoesNotExist() throws XPathExpressionException {
    	xpath.getFirstNode("/idontexist");
    }
}
