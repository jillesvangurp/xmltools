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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.xml.xpath.XPathExpressionException;
import org.apache.commons.lang3.StringUtils;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

@Test
public class XPathBrowserTest {

    private XPathBrowser browser;

    private Node root;

    private XpathBrowserFactory xpbf;

    @BeforeMethod
	public void before() throws Exception {
        xpbf = new XpathBrowserFactory(new PooledXmlParser(20, 20), new XPathExpressionCache(20,10000, 1000, 20));

        browser = xpbf.browse(this.getClass().getResourceAsStream("/test.xml"), StandardCharsets.UTF_8);
        root = browser.getNodeList("/root").item(0);
    }

    public void shouldExtractFromNamespacedXml() throws SAXException, IOException, XPathExpressionException {
        browser = xpbf.browse(this.getClass().getResourceAsStream("/test-with-ns.xml"), StandardCharsets.UTF_8);
        AssertJUnit.assertEquals(0.42,browser.getDouble("/root/double").get());
    }

    public void shouldHandleBooleans() throws XPathExpressionException {
    	assertThat("should be true", browser.getBoolean("/root/bool"));
    	assertThat("should be true", browser.getBoolean(root, "bool"));
    	browser = browser.browseFirst("/root/bool");
        assertThat("should get value from current node", browser.getBoolean());
        assertThat("should be false", !browser.getBoolean(root, "string"));
    }

    public void shouldHandleStrings() throws XPathExpressionException {
    	assertThat(browser.getString("/root/string").get(), equalTo("foo"));
    	assertThat(browser.getString(root, "string").get(), equalTo("foo"));
        browser = browser.browseFirst("/root/string");
        assertThat(browser.getString().get(), equalTo("foo"));

    }

    @Test(expectedExceptions = NumberFormatException.class)
    public void shouldHandleLongs() throws XPathExpressionException {
    	assertThat(browser.getLong("/root/long").get(), equalTo(42l));
    	assertThat(browser.getLong(root, "long").get(), equalTo(42l));
    	browser = browser.browseFirst("/root/long");
        assertThat(browser.getLong().get(), equalTo(42l));
        browser.getLong("/root/string");
    }

    @Test(expectedExceptions = NumberFormatException.class)
    public void shouldHandleDoubles() throws XPathExpressionException {
    	assertThat(browser.getDouble("/root/double").get(), equalTo(0.42));
    	assertThat(browser.getDouble(root, "double").get(), equalTo(0.42));
    	browser = browser.browseFirst("/root/double");
        assertThat(browser.getDouble().get(), equalTo(0.42));
        browser.getDouble("/root/string");
    }

    @Test(expectedExceptions = NumberFormatException.class)
    public void shouldHandleInts() throws XPathExpressionException {
    	assertThat(browser.getInt("/root/long").get(), equalTo(42));
    	assertThat(browser.getInt(root, "long").get(), equalTo(42));
    	browser = browser.browseFirst("/root/long");
        assertThat(browser.getInt().get(), equalTo(42));
        browser.getInt("/root/string");
    }

    public void shouldHandleNodeLists() throws XPathExpressionException {
    	assertThat(browser.getNodeList("/root/list/item").getLength(),equalTo(2));
    	assertThat(browser.getNodeList(root, "list/item").getLength(), equalTo(2));
    }

    public void shouldGetSubNode() throws XPathExpressionException {
    	assertThat(browser.getSubNode(root, "string"), notNullValue());
    }

    public void shouldGetStringValuesFromNodeList() throws XPathExpressionException {
        String[] stringValues = browser.getStringValues("/root/list/item");
        assertThat(stringValues.length, equalTo(2));
        assertThat(stringValues[0], equalTo("1"));
        stringValues = browser.getStringValues(root, "list/item");
        assertThat(stringValues.length, equalTo(2));
        assertThat(stringValues[0], equalTo("1"));
    }

    public void shouldFindMatchingNodes() throws XPathExpressionException {
		int count = 0;
		Node originalRoot = browser.rootNode();
		for( XPathBrowser b: browser.browseMatching("/root/list/item")) {
			count++;
	    	assertThat("browser should have different node", originalRoot != b.rootNode());
		}
		assertThat(count, equalTo(2));
		assertThat("original browser should still have root", originalRoot == browser.rootNode());
	}

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void shouldNotAllowRemoveOnBrowserIterator() throws XPathExpressionException {
    	browser.browseMatching("/root/list/item").iterator().remove();
    }

	public void shouldBrowseItemsWithoutExpression() throws XPathExpressionException {
	    browser = browser.browseFirst("/root/list");
    	int count = 0;
    	Node originalRoot = browser.rootNode();
    	for( XPathBrowser b: browser.browseSubNodes()) {
    		count++;
        	assertThat("browser should cd to node", originalRoot != b.rootNode());
    	}
    	assertThat(count, equalTo(2));
        assertThat("original browser should still have root", originalRoot == browser.rootNode());
    }

    public void shouldThrowExceptionOnGetFirstNodeThatDoesNotExist() throws XPathExpressionException {
    	assertThat(browser.getFirstNode("/idontexist").isPresent(),equalTo(false));
    }

    public void shouldStreamSubNodes() {
        browser.streamMatching("/root/list").map(itemNode -> itemNode.getString().get()).forEach(s -> StringUtils.isNotBlank(s));;
    }
}
