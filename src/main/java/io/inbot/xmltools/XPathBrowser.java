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
package io.inbot.xmltools;

import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
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
     * @throws XPathExpressionException if xpath incorrect
     */
    public Object eval(final String expr, final Node node, final QName resultType) throws XPathExpressionException {
        return expressionCache.getExpression(expr).evaluate(node, resultType);
    }

    /**
     * Evaluate expression to a boolean value.
     *
     * @param n
     *        node from which the (relative) expression is evaluated.
     * @param expr
     *        xpath expression.
     * @return result of the expression.
	 * @throws XPathExpressionException if xpath incorrect
     */
    public boolean getBoolean(final Node n, final String expr) throws XPathExpressionException {
        final String s = getString(n, expr);
        return Boolean.parseBoolean(s);
    }

    /**
     * Evaluate expression to a boolean value.
     *
     * @param expr
     *        xpath expression.
     * @return result of the expression.
	 * @throws XPathExpressionException if xpath incorrect
     */
    public boolean getBoolean(final String expr) throws XPathExpressionException {
        return getBoolean(currentNode(), expr);
    }

    /**
     * @return current node as a boolean
     */
    public boolean getBoolean() {
    	try {
			return getBoolean(".");
		} catch (XPathExpressionException e) {
			throw new IllegalStateException(". is a valid xpath expression", e);
		}
    }


    /**
     * Evaluate expression to a double value.
     *
     * @param n
     *        node from which the (relative) expression is evaluated.
     * @param expr
     *        xpath expression.
     * @return result of the expression.
	 * @throws XPathExpressionException if xpath incorrect
     */
    public double getDouble(final Node n, final String expr) throws XPathExpressionException {
        final String s = getString(n, expr);
        return Double.parseDouble(s);
    }

    /**
     * Evaluate expression to a double value.
     *
     * @param expr
     *        xpath expression.
     * @return result of the expression.
	 * @throws XPathExpressionException if xpath incorrect
     */
    public double getDouble(final String expr) throws XPathExpressionException {
        return getDouble(currentNode(), expr);
    }

    /**
     * @return current node as a double.
     */
    public double getDouble() {
    	try {
			return getDouble(".");
		} catch (XPathExpressionException e) {
			throw new IllegalStateException(". is a valid xpath expression", e);
		}
    }


    /**
     * Evaluate expression to a int value.
     *
     * @param n
     *        node from which the (relative) expression is evaluated.
     * @param expr
     *        xpath expression.
     * @return result of the expression.
	 * @throws XPathExpressionException if xpath incorrect
     */
    public int getInt(final Node n, final String expr) throws XPathExpressionException {
        final String s = getString(n, expr);
        return Integer.parseInt(s);
    }

    /**
     * Evaluate expression to a int value.
     *
     * @param expr
     *        xpath expression.
     * @return result of the expression.
	 * @throws XPathExpressionException if xpath incorrect
     */
    public int getInt(final String expr) throws XPathExpressionException {
        return getInt(currentNode(), expr);
    }

    /**
     * @return current node as an int
     */
    public int getInt() {
        try {
			return getInt(".");
		} catch (XPathExpressionException e) {
			throw new IllegalStateException(". is a valid xpath expression", e);
		}
    }


    /**
     * Evaluate expression to a long value.
     *
     * @param n
     *        node from which the (relative) expression is evaluated.
     * @param expr
     *        xpath expression.
     * @return result of the expression.
	 * @throws XPathExpressionException if xpath incorrect
     */
    public long getLong(final Node n, final String expr) throws XPathExpressionException {
        final String s = getString(n, expr);
        return Long.parseLong(s);
    }

    /**
     * Evaluate expression to a long value.
     *
     * @param expr
     *        xpath expression.
     * @return result of the expression.
	 * @throws XPathExpressionException if xpath incorrect
     */
    public long getLong(final String expr) throws XPathExpressionException {
        return getLong(currentNode(), expr);
    }

    /**
     * @return current node as a long
     */
    public long getLong() {
        try {
			return getLong(".");
		} catch (XPathExpressionException e) {
			throw new IllegalStateException(". is a valid xpath expression", e);
		}
    }

    /**
     * Evaluate an expression that should result in a String (relative to the provided node).
     *
     * @param n
     *        node from which the (relative) expression is evaluated.
     * @param expr
     *        xpath expression.
     * @return result of the expression.
	 * @throws XPathExpressionException if xpath incorrect
     */
    public String getString(final Node n, final String expr) throws XPathExpressionException {
        return ((String) eval(expr, n, XPathConstants.STRING)).trim();
    }

    /**
     * Evaluate an expression that should result in a String (relative to the root).
     *
     * @param expr
     *        xpath expression.
     * @return result of the expression.
	 * @throws XPathExpressionException if xpath incorrect
     */
    public String getString(final String expr) throws XPathExpressionException {
        return ((String) eval(expr, currentNode(), XPathConstants.STRING)).trim();
    }

    /**
     * @return current node as a String
     */
    public String getString() {
        try {
			return getString(".");
		} catch (XPathExpressionException e) {
			throw new IllegalStateException(". is a valid xpath expression", e);
		}
    }


    /**
	 * @param expr expression
	 * @return the first node that matches the expression
	 * @throws XPathExpressionException if xpath incorrect
	 * @throws IllegalArgumentException if the node does not exist
	 */
	public Node getFirstNode(String expr) throws XPathExpressionException {
		return getFirstNode(currentNode(), expr);
	}

	/**
	 * @param n node
	 * @param expr expression
	 * @return the first node that matches the expression
	 * @throws XPathExpressionException if xpath incorrect
	 * @throws IllegalArgumentException if the node does not exist
	 */
	public Node getFirstNode(Node n, String expr) throws XPathExpressionException {
		NodeList nodeList = getNodeList(n, expr);
		if(nodeList.getLength() == 0) {
			throw new IllegalArgumentException("no such node " + expr);
		} else {
			return nodeList.item(0);
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
	 * @throws XPathExpressionException if xpath incorrect
	 */
	public NodeList getNodeList(final Node n, final String expr) throws XPathExpressionException {
	    return (NodeList) eval(expr, n, XPathConstants.NODESET);
	}

	/**
	 * Evaluate an expression that should result in a Node set (relative to the root).
	 *
	 * @param expr
	 *        xpath expression.
	 * @return a list of nodes matching the expression
	 * @throws XPathExpressionException if xpath incorrect
	 */
	public NodeList getNodeList(final String expr) throws XPathExpressionException {
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
	 * @throws XPathExpressionException if xpath incorrect
     */
    public String[] getStringValues(final Node n, final String expr) throws XPathExpressionException {
        final NodeList nodes = getNodeList(n, expr);
        final String[] values = new String[nodes.getLength()];
        for (int i = 0; i < nodes.getLength(); i++) {
            values[i] = getString(nodes.item(i), ".");
        }
        return values;

    }

    /**
     * Get array of values that match specified expression.
     *
     * @param expr
     *        xpath expression.
     * @return array with matching values
	 * @throws XPathExpressionException if xpath incorrect
     */
    public String[] getStringValues(final String expr) throws XPathExpressionException {
        return getStringValues(currentNode(), expr);
    }

    /**
     * Get a named sub node from the parent.
     *
     * @param parent parent node
     * @param name name
     * @return a Node instance
	 * @throws XPathExpressionException if xpath incorrect
     */
    public Node getSubNode(final Node parent, final String name) throws XPathExpressionException {
        final Node node = (Node) eval(name, parent, XPathConstants.NODE);
        return node;
    }

    /**
     * @return the current node; expressions are evaluated relative to this node.
     */
    public Node currentNode() {
	    return currentNode;
	}

//	/**
//	 * @return the node with which this {@link XPathBrowser} was initialized.
//	 */
//	public Node root() {
//		return root;
//	}

//	/**
//     * Change the current node back to the root with which this browser was initialized.
//	 * @return the root node
//     */
//    public XPathBrowser cd() {
//    	return cd(root());
//    }

    public XPathBrowser browse(final Node node) {
        return new XPathBrowser(expressionCache, node);
    }

//    /**
//     * Change the current node to a the first node matching the expression.
//     * @param expression expression
//     * @return the node that matched
//	 * @throws XPathExpressionException if xpath incorrect
//	 * @throws IllegalArgumentException if the node does not exist
//     */
//    public XPathBrowser cd(String expression) throws XPathExpressionException {
//    	return cd(getFirstNode(expression));
//    }
//
//    /**
//     * Change the current node to a the first node matching the expression.
//     * @param n node
//     * @param expression expression
//     * @return the node that matched
//	 * @throws XPathExpressionException if xpath incorrect
//	 * @throws IllegalArgumentException if the node does not exist
//     */
//    public XPathBrowser cd(Node n, String expression) throws XPathExpressionException {
//    	return cd(getFirstNode(n, expression));
//    }

    public XPathBrowser browseFirst(String expression) throws XPathExpressionException {
        return new XPathBrowser(expressionCache, getFirstNode(expression));
    }

    public Iterable<XPathBrowser> browseSubNodes() throws XPathExpressionException {
    	return browseMatching("./*");
    }

    public Iterable<XPathBrowser> browseMatching(final String expr) throws XPathExpressionException {
    	final NodeList nodeList = getNodeList(currentNode(), expr);
    	final XPathBrowser parent = this;
    	return new Iterable<XPathBrowser>() {

			@Override
			public Iterator<XPathBrowser> iterator() {
				return new NodeIterator(nodeList, parent);
			}
		};
    }

    public Iterator<XPathBrowser> browseMatching(final Node n, final String expr) throws XPathExpressionException {
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
