package com.jillesvangurp.xmltools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class iterates over string blobs in a stream that are clearly marked
 * with some begin and end token. This is useful for processing large files of
 * e.g. xml, json, or some other structured data and allows you to process the
 * blobs one by one instead of parsing the whole file, which over a certain size
 * might be very challenging.
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

		return new BlobIterator(br);
	}

	private final class BlobIterator implements Iterator<String> {
		private final BufferedReader br;
		StringBuilder current = new StringBuilder();

		String next;

		private BlobIterator(BufferedReader br) {
			this.br = br;
			readNext();
		}

		private void readNext() {
			current = new StringBuilder();
			next = null;
			try {
				int c;
				if(next == null) {
					while(next == null && (c = br.read()) != -1) {
						if(openTag.charAt(0) == c) {
							current.append((char)c);
							int o=1;
							while(o<openTag.length() && (c=br.read()) != -1) {
								current.append((char)c);
								o++;
							}
							if(openTag.equals(current.toString())) {

								while(!fastEndsWith(current, closeTag) && (c=br.read()) != -1) {
									current.append((char)c);
								}
								if(fastEndsWith(current, closeTag)) {
									next = current.toString();
									return;
								}
							}
							current = new StringBuilder();
						}
					}
				}
			} catch (IOException e) {
				throw new IllegalStateException("cannot read from stream",e);
			}
		}

		boolean fastEndsWith(StringBuilder buf, String postFix) {
			// String.endsWith is very slow
			if(buf.length()<postFix.length()) {
				return false;
			} else {
				boolean match = true;
				for(int i=1;i<=postFix.length();i++) {
					match = match && buf.charAt(buf.length()-i) == postFix.charAt(postFix.length()-i);
					if(!match) {
						return false;
					}
				}
				return match;
			}
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public String next() {
			String result = next;
			if(next != null) {
				readNext();
				return result;
			} else {
				throw new NoSuchElementException();
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("remove is not supported");
		}
	}
}
