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

import io.inbot.xmltools.exceptions.RethrownException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Simple file system abstraction over XML documents that allows you to browse the XML document using xpath expressions.
 *
 *
 * It's reuses xpath expressions using the {@link XPathExpressionCache}. This is a lot faster than recompiling the expressions every time.
 *
 * Note, you should use the XPathBrowserFactory for creating instances.
 *
 * Note, this class does not support namespaces currently. TODO: check here for potential solution
 * http://blog.davber.com/2006/09/17/xpath-with-namespaces-in-java/
 */
public class XPathBrowser {

    private final Node currentNode;
    private final XPathExpressionCache expressionCache;

    XPathBrowser(XPathExpressionCache expressionCache, Node node) {
        this.expressionCache = expressionCache;
        currentNode=node;
    }

    /**
     * Efficient xpath expression evaluator that uses the {@link XPathExpressionCache}.
     * Use this if none of the other methods do what you need.
     *
     * @param expr expr
     * @param node node
     * @param resultType type
     * @return DOM object of the specified type or null.
     */
    public Object eval(final String expr, final Node node, final QName resultType) {
        try {
            return expressionCache.getExpression(expr).evaluate(node, resultType);
        } catch (XPathExpressionException e) {
            throw RethrownException.rethrow(e);
        }
    }

    /**
     * Evaluate expression to a boolean value.
     *
     * @param n
     *        node from which the (relative) expression is evaluated.
     * @param expr
     *        xpath expression.
     * @return result of the expression.
     */
    public boolean getBoolean(final Node n, final String expr) {
        return getString(n, expr).map(s->Boolean.valueOf(s)).orElse(false);
    }

    /**
     * Evaluate expression to a boolean value.
     *
     * @param expr
     *        xpath expression.
     * @return result of the expression.
     */
    public boolean getBoolean(final String expr) {
        return getBoolean(currentNode(), expr);
    }

    /**
     * @return current node as a boolean
     */
    public boolean getBoolean() {
		return getBoolean(".");
    }


    /**
     * Evaluate expression to a double value.
     *
     * @param n
     *        node from which the (relative) expression is evaluated.
     * @param expr
     *        xpath expression.
     * @return result of the expression.
     */
    public Optional<Double> getDouble(final Node n, final String expr) {
        return getString(n, expr).map(s -> Double.valueOf(s));
    }

    /**
     * Evaluate expression to a double value.
     *
     * @param expr
     *        xpath expression.
     * @return result of the expression.
     */
    public Optional<Double> getDouble(final String expr) {
        return getDouble(currentNode(), expr);
    }

    /**
     * @return current node as a double.
     */
    public Optional<Double> getDouble() {
		return getDouble(".");
    }


    /**
     * Evaluate expression to a int value.
     *
     * @param n
     *        node from which the (relative) expression is evaluated.
     * @param expr
     *        xpath expression.
     * @return result of the expression.
     */
    public Optional<Integer> getInt(final Node n, final String expr) {
        return getString(n, expr).map(s -> Integer.valueOf(s));
    }

    /**
     * Evaluate expression to a int value.
     *
     * @param expr
     *        xpath expression.
     * @return result of the expression.
     */
    public Optional<Integer> getInt(final String expr) {
        return getInt(currentNode(), expr);
    }

    /**
     * @return current node as an int
     */
    public Optional<Integer> getInt() {
		return getInt(".");
    }

    public Optional<Number> getNumber(Locale locale, final Node n, final String expr) {
            return getString(n, expr).map(s -> {
                try {
                    return NumberFormat.getInstance(locale).parse(s);
                } catch (ParseException e) {
                    throw RethrownException.rethrow(e);
                }
            });
    }

    /**
     * Evaluate expression to a long value.
     *
     * @param n
     *        node from which the (relative) expression is evaluated.
     * @param expr
     *        xpath expression.
     * @return result of the expression.
     */
    public Optional<Long> getLong(final Node n, final String expr) {
        return getString(n, expr).map(s -> Long.valueOf(s));
    }

    /**
     * Evaluate expression to a long value.
     *
     * @param expr
     *        xpath expression.
     * @return result of the expression.
     */
    public Optional<Long> getLong(final String expr) {
        return getLong(currentNode(), expr);
    }

    /**
     * @return current node as a long
     */
    public Optional<Long> getLong() {
		return getLong(".");
    }

    /**
     * Evaluate an expression that should result in a String (relative to the provided node).
     *
     * @param n
     *        node from which the (relative) expression is evaluated.
     * @param expr
     *        xpath expression.
     * @return result of the expression.
     */
    public Optional<String> getString(final Node n, final String expr) {
        String result = ((String) eval(expr, n, XPathConstants.STRING)).trim();
        if(StringUtils.isBlank(result)) {
            return Optional.empty();
        } else {
            return Optional.of(result);
        }
    }

    /**
     * Evaluate an expression that should result in a String (relative to the root).
     *
     * @param expr
     *        xpath expression.
     * @return result of the expression.
     */
    public Optional<String> getString(final String expr) {
        String s = ((String) eval(expr, currentNode(), XPathConstants.STRING)).trim();
        if(StringUtils.isBlank(s)) {
            return Optional.empty();
        } else {
            return Optional.of(s);
        }
    }

    /**
     * @return current node as a String
     */
    public Optional<String> getString() {
		return getString(".");
    }


    /**
	 * @param expr expression
	 * @return the first node that matches the expression
	 * @throws IllegalArgumentException if the node does not exist
	 */
	public Optional<Node> getFirstNode(String expr) {
		return getFirstNode(currentNode(), expr);
	}

	/**
	 * @param n node
	 * @param expr expression
	 * @return the first node that matches the expression
	 * @throws IllegalArgumentException if the node does not exist
	 */
	public Optional<Node> getFirstNode(Node n, String expr) {
		NodeList nodeList = getNodeList(n, expr);
		if(nodeList.getLength() == 0) {
			return Optional.empty();
		} else {
			return Optional.of(nodeList.item(0));
		}
	}

	/**
	 * Evaluate an expression that should result in a Node set (relative to the provided node).
	 *
	 * @param n
	 *        node from which the (relative) expression is evaluated.
	 * @param expr
	 *        expr xpath expression.
	 * @return a list of Nodes matching the expression.
	 */
	public NodeList getNodeList(final Node n, final String expr) {
	    return (NodeList) eval(expr, n, XPathConstants.NODESET);
	}

	/**
	 * Evaluate an expression that should result in a Node set (relative to the root).
	 *
	 * @param expr
	 *        xpath expression.
	 * @return a list of nodes matching the expression
	 */
	public NodeList getNodeList(final String expr) {
	    return (NodeList) eval(expr, currentNode(), XPathConstants.NODESET);
	}

	/**
     * Get array of values that match specified expression.
     *
     * @param n
     *        node from which the (relative) expression is evaluated.
     * @param expr
     *        xpath expression.
     * @return array with matching values
     */
    public String[] getStringValues(final Node n, final String expr) {
        final NodeList nodes = getNodeList(n, expr);
        final String[] values = new String[nodes.getLength()];
        for (int i = 0; i < nodes.getLength(); i++) {
            values[i] = getString(nodes.item(i), ".").orElse("");
        }
        return values;

    }

    /**
     * Get array of values that match specified expression.
     *
     * @param expr
     *        xpath expression.
     * @return array with matching values
     */
    public String[] getStringValues(final String expr) {
        return getStringValues(currentNode(), expr);
    }

    /**
     * Get a named sub node from the parent.
     *
     * @param parent parent node
     * @param name name
     * @return a Node instance
     */
    public Node getSubNode(final Node parent, final String name) {
        final Node node = (Node) eval(name, parent, XPathConstants.NODE);
        return node;
    }

    /**
     * @return the current node; expressions are evaluated relative to this node.
     */
    public Node currentNode() {
	    return currentNode;
	}

    public XPathBrowser browse(final Node node) {
        return new XPathBrowser(expressionCache, node);
    }

    public XPathBrowser browseFirst(String expression) {
        return new XPathBrowser(expressionCache, getFirstNode(expression).orElseThrow(() -> new NoSuchElementException("node does not exist for " + expression)));
    }

    public Iterable<XPathBrowser> browseSubNodes() {
    	return browseMatching("./*");
    }

    public Iterable<XPathBrowser> browseMatching(final String expr) {
    	final NodeList nodeList = getNodeList(currentNode(), expr);
    	final XPathBrowser parent = this;
    	return new Iterable<XPathBrowser>() {

			@Override
			public Iterator<XPathBrowser> iterator() {
				return new NodeIterator(nodeList, parent);
			}
		};
    }

    public Iterator<XPathBrowser> browseMatching(final Node n, final String expr) {
    	NodeList nodeList = getNodeList(n, expr);
    	return new NodeIterator(nodeList, this);
    }

	private final class NodeIterator implements Iterator<XPathBrowser> {
		private final NodeList nodeList;
		int i=0;

		private NodeIterator(NodeList nodeList, XPathBrowser browser) {
			this.nodeList = nodeList;
		}

		@Override
		public boolean hasNext() {
			boolean hasNext = i<nodeList.getLength();

			return hasNext;
		}

		@Override
		public XPathBrowser next() {
		    return new XPathBrowser(expressionCache, nodeList.item(i++));
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("remove is not supported");
		}
	}
}
