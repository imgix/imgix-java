package com.imgix.test;

import org.junit.Test;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

import com.imgix.URLBuilder;
import com.imgix.URLHelper;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import java.net.URI;
import java.net.URISyntaxException;

@RunWith(JUnit4.class)
public class TestAll {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testURLBuilderRaisesExceptionOnNoDomains() {
		exception.expect(IllegalArgumentException.class);
		URLBuilder ub = new URLBuilder(new String[] {});
	}

	@Test
	public void testHelperBuildSignedURLWithHashMapParams() {

		Map<String, String> params = new HashMap<String, String>();
		params.put("w", "500");

		URLHelper uh = new URLHelper("securejackangers.imgix.net", "chester.png", "http", "Q61NvXIy", params);

		assertEquals(uh.getURL(), "http://securejackangers.imgix.net/chester.png?w=500&s=0ddf97bf1a266a1da6c30c6ce327f917");
	}

	@Test
	public void testHelperBuildSignedURLWithHashSetterParams() {
		URLHelper uh = new URLHelper("securejackangers.imgix.net", "chester.png", "http", "Q61NvXIy");

		uh.setParameter("w", 500);

		assertEquals(uh.getURL(), "http://securejackangers.imgix.net/chester.png?w=500&s=0ddf97bf1a266a1da6c30c6ce327f917");
	}

	@Test
	public void testHelperBuildSignedURLWithWebProxyWithNoEncoding() {
		URLHelper uh = new URLHelper("jackttl2.imgix.net", "http://a.abcnews.com/assets/images/navigation/abc-logo.png?r=20", "http", "JHrM2ezd");
		assertEquals(uh.getURL(), "http://jackttl2.imgix.net/http%3A%2F%2Fa.abcnews.com%2Fassets%2Fimages%2Fnavigation%2Fabc-logo.png%3Fr%3D20?s=cf82defe3436a957262d0e64c21e72f9");
	}

	@Test
	public void testHelperBuildSignedURLWithWebProxyWithEncoding() {
		URLHelper uh = new URLHelper("jackttl2.imgix.net", "http%3A%2F%2Fa.abcnews.com%2Fassets%2Fimages%2Fnavigation%2Fabc-logo.png%3Fr%3D20", "http", "JHrM2ezd");
		assertEquals(uh.getURL(), "http://jackttl2.imgix.net/http%3A%2F%2Fa.abcnews.com%2Fassets%2Fimages%2Fnavigation%2Fabc-logo.png%3Fr%3D20?s=cf82defe3436a957262d0e64c21e72f9");
	}

	@Test
	public void testUrlBuilderCycleShard() {
		// generate a url for the number of domains in use ensure they're cycled through...
		String[] domains = new String[] { "jackangers.imgix.net", "jackangers2.imgix.net", "jackangers3.imgix.net" };

		URLBuilder ub = new URLBuilder(domains);
		ub.setShardStratgy(URLBuilder.ShardStrategy.CYCLE);// uses crc by default so manually set cycle

		List<String> used = new ArrayList<String>();
		for (int i = 0; i < domains.length; i++) {

			String url = ub.createURL("chester.png");

			String curDomain = extractDomain(url);
			assertFalse(used.contains(curDomain));

			used.add(curDomain);
		}

	}

	@Test
	public void testUrlBuilderCRCShard() {
		String[] domains = new String[] { "jackangers.imgix.net", "jackangers2.imgix.net", "jackangers3.imgix.net" };

		URLBuilder ub = new URLBuilder(domains);

		String[] paths = new String[] {"chester.png", "chester1.png", "chester2.png"};

		for (String path: paths) {
			String testDomain = extractDomain(ub.createURL(path));

			// ensure we get that we keep getting the same domain...
			for (int i = 0; i < 20; i++) {
				assertEquals(testDomain, extractDomain(ub.createURL(path)));
			}
		}
	}


	@Test
	public void testExtractDomain() {
		String url = "http://jackangers.imgix.net/chester.png";
		assertEquals(extractDomain(url), "jackangers.imgix.net");
	}

	@Test
	public void testEncodeDecode() {
		String url = "http://a.abcnews.com/assets/images/navigation/abc-logo.png?r=20";
		String encodedUrl = "http%3A%2F%2Fa.abcnews.com%2Fassets%2Fimages%2Fnavigation%2Fabc-logo.png%3Fr%3D20";

		assertEquals(URLHelper.encodeURIComponent(url), encodedUrl);
		assertEquals(URLHelper.decodeURIComponent(encodedUrl), url);
		assertEquals(URLHelper.encodeURIComponent(URLHelper.decodeURIComponent(encodedUrl)), encodedUrl);
	}

	private static String extractDomain(String url) {
		try {
			URI parsed = new URI(url);
			String curDomain = parsed.getAuthority();

			return curDomain;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return "";
	}

}
