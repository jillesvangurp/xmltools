package com.jillesvangurp.xmltools;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.hamcrest.CoreMatchers;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
public class BlobIterableTest {

	@DataProvider
	public String[][] input() {
		return new String[][] {
				{"<i>1</i><i>2</i><i>3</i>", "<i>", "</i>"},
				{"<list> <i>\n\t1</i>\n<i>2</i><i>3</i><list>", "<i>", "</i>"},
				{"[[]]]    [[    ]]] [[[]] ","[[","]]"}
		};
	}

	@Test(dataProvider="input")
	public void shouldIterateOverBlobs(String xml, String begin, String end) {
		ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes(Charset.forName("utf-8")));
		BlobIterable xmlBlobIterable = new BlobIterable(new InputStreamReader(is), begin, end);

		int count = 0;
		for(String blob: xmlBlobIterable) {
			assertThat("Should start with", blob.startsWith(begin));
			assertThat("Should end with", blob.endsWith(end));
			count++;
		}
		assertThat(count, CoreMatchers.is(3));
	}
}
