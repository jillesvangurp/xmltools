package com.jillesvangurp.xmltools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class iterates over string blobs in a stream that are clearly marked
 * with some begin and end token. This is useful for processing large files of
 * e.g. xml, json, or some other structured data.
 *
 * Basically this class is an Iterable<String>, which means you can simply use a
 * for loop to loop over the content.
 */
public class BlobIterable implements Iterable<String> {

	private final Reader r;
	private final String openTag;
	private final String closeTag;


	public BlobIterable(Reader r, String openTag, String closeTag) {
		this.r = r;
		this.openTag = openTag;
		this.closeTag = closeTag;
	}

	@Override
	public Iterator<String> iterator() {
		final BufferedReader br = new BufferedReader(r);

		return new Iterator<String>() {
			StringBuilder buf = new StringBuilder(10000);
			String next;

			void readNext() throws IOException {
				int c;
				if(next == null) {
					while(next == null && (c = br.read()) != -1) {
						if(openTag.charAt(0) == c) {
							buf.append((char)c);
							int o=1;
							while(o<openTag.length() && (c=br.read()) != -1) {
								buf.append((char)c);
								o++;
							}
							if(openTag.equals(buf.toString())) {
								while(!buf.toString().endsWith(closeTag) && (c=br.read()) != -1) {
									buf.append((char)c);
								}
								if(buf.toString().endsWith(closeTag)) {
									next = buf.toString();
								}
							}
							// reuse the same buffer and save some memory
							buf.setLength(0);
						}
					}
				}
			}

			@Override
			public boolean hasNext() {
				if(next == null) {
					try {
						readNext();
					} catch (IOException e) {
						throw new IllegalStateException("cannot read from stream",e);
					}
				}
				return next != null;
			}

			@Override
			public String next() {
				String result = next;
				if(next != null) {
					next = null;
					return result;
				} else {
					throw new NoSuchElementException();
				}
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("remove is not supported");
			}
		};
	}

}
