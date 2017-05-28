# Introduction

XMLTools includes some useful functionality for Java that I've accumulated over the years dealing with XML and XPath on various projects.

In short, XML is tedious in Java. The APIs are primitive and throw checked exceptions left right and center. This is intended to take away that pain and wrap the standard java classes with some convenience.

# Get xmltools from Maven Central

```
<dependency>
    <groupId>io.inbot</groupId>
    <artifactId>xmltools</artifactId>
    <version>2.1</version>
</dependency>
```

# Overview

## PooledXmlParser.

Caches DocumentBuilder instances so you can repeatedly parse documents in Java. Also traps all the checked exceptions and rethrows them as run time exceptions.

Each thread looks up their DocumentBuilder from a guava cache using the thread id. If it is not there, a new one is created.


```
// creates a cache for 100 threads that each keep their DocumentBuilder for 10 minutes.
// this uses a default non validating DocumentBuilder, you can also provide your own Supplier<DocumentBuilder> if you need this customized.
PooledXmlParser parser = new PooledXmlParser(100,10);
Document doc = parser.parse("<root>Hi Wrld!</root>");
```

## XPathBrowser

Xpath is a convenient way to extract information from XML and in Java it is quite fast if you reuse your xpath expressions instead of recompiling them everytime.

Of course xpath expressions are not thread safe; which makes doing this hard somewhat tedious. Especially in multithreaded web applications.

XPathBrowser uses a simple Guava cache of caches for expressions. Each thread gets its own cache from the top level cache by thread id and can then get a compiled expression from its own cache with the expression as the key. It uses a Guava loading cache here.

`XPathBrowser` makes xpath easy by wrapping Dom nodes with a browser abstraction. You browse DOM nodes and use xpath expressions to navigate to browsers for sub nodes in the tree. Also there are nice methods for getting information out of the nodes.

A few examples:

```
<root>
    <string>foo</string>
    <double>0.42</double>
    <long>42</long>
    <bool>True</bool>
    <list>
        <item>1</item>
        <item>2</item>
    </list>
    <attrnode foo="bar" bar="foo" />
</root>


XpathBrowserFactory xpbf = new XpathBrowserFactory(new PooledXmlParser(20, 20), new XPathExpressionCache(20,10000, 1000, 20));
XPathBrowser browser = xpbf.browse(abovexml);

// attributes are easy
Map<String, String> attributeMap = browser.browseFirst("/root/attrnode").nodeAttributes();
assertThat(attributeMap.size(),equalTo(2));
assertThat(attributeMap.get("foo"),equalTo("bar"));
assertThat(attributeMap.get("bar"),equalTo("foo"));

// get a string if it is there and not empty
assertThat(browser.getString("/root/string").get(), equalTo("foo"));

// get an int if it is there
assertThat(browser.getInt("/root/long").get(), equalTo(42));


assertThat(browser.streamMatching("/root/list/item").count(),equalTo(2l));
browser.streamMatching("/root/list/item")
    // browse each of the item nodes and extract the string value
    .map(itemNode -> itemNode.getString().get())
    .forEach(s -> assertThat("not empty",StringUtils.isNotBlank(s)));

```


h1. Changelog
* 2.1
** Some minor fixes
** `PooledXmlParser` constructor now takes a `Supplier<DocumentFactory>`
* 2.0
** Big compatibility breaking refactor to add some support for java 8
** No more checked exceptions.
** No more ThreadLocal, uses guava caches only
** Optionals and some streams.
** browse instead of ls and cd.
* 1.3
** First maven central release; refactored to have io.inbot package
* 1.2
** Mostly documentation fixes
* 1.1
** Added BlobIterable, replaced the caching for xpath expressions with a guava based LRU cache
* 1.0
** First release of XMLTools
