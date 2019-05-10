package com.imgix.test;

import org.junit.Test;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

import com.imgix.URLBuilder;
import com.imgix.URLHelper;

import java.lang.annotation.Annotation;
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
	public void testHelperBuildAbsolutePath() {
		URLHelper uh = new URLHelper("securejackangers.imgix.net", "/example/chester.png", "http");
		assertEquals(uh.getURL(), "http://securejackangers.imgix.net/example/chester.png");
	}

	@Test
	public void testHelperBuildRelativePath() {
		URLHelper uh = new URLHelper("securejackangers.imgix.net", "example/chester.png", "http");
		assertEquals(uh.getURL(), "http://securejackangers.imgix.net/example/chester.png");
	}

	@Test
	public void testHelperBuildNestedPath() {
		URLHelper uh = new URLHelper("securejackangers.imgix.net", "http://www.somedomain.com/example/chester.png", "http");
		System.out.println(uh.getURL());
		assertEquals(uh.getURL(), "http://securejackangers.imgix.net/http%3A%2F%2Fwww.somedomain.com%2Fexample%2Fchester.png");
	}

	@Test
	public void testHelperBuildAbsolutePathWithParams() {
		URLHelper uh = new URLHelper("securejackangers.imgix.net", "/example/chester.png", "http");
		uh.setParameter("w", 500);
		assertEquals(uh.getURL(), "http://securejackangers.imgix.net/example/chester.png?w=500");
	}

	@Test
	public void testHelperBuildRelativePathWithParams() {
		URLHelper uh = new URLHelper("securejackangers.imgix.net", "example/chester.png", "http");
		uh.setParameter("w", 500);
		assertEquals(uh.getURL(), "http://securejackangers.imgix.net/example/chester.png?w=500");
	}

	@Test
	public void testHelperBuildNestedPathWithParams() {
		URLHelper uh = new URLHelper("securejackangers.imgix.net", "http://www.somedomain.com/example/chester.png", "http");
		uh.setParameter("w", 500);
		System.out.println(uh.getURL());
		assertEquals(uh.getURL(), "http://securejackangers.imgix.net/http%3A%2F%2Fwww.somedomain.com%2Fexample%2Fchester.png?w=500");
	}

	@Test
	public void testHelperBuildSignedURLWithHashMapParams() {

		Map<String, String> params = new HashMap<String, String>();
		params.put("w", "500");

		URLHelper uh = new URLHelper("securejackangers.imgix.net", "example/chester.png", "http", "Q61NvXIy", params);
		assertEquals(uh.getURL(), "http://securejackangers.imgix.net/example/chester.png?w=500&s=787b9057d5c077fe168b4849737d8a90");
	}

	@Test
	public void testHelperBuildSignedURLWithHashSetterParams() {
		URLHelper uh = new URLHelper("securejackangers.imgix.net", "example/chester.png", "http", "Q61NvXIy");

		uh.setParameter("w", 500);

		assertEquals(uh.getURL(), "http://securejackangers.imgix.net/example/chester.png?w=500&s=787b9057d5c077fe168b4849737d8a90");
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
	public void testBuildSignedURLWithWebProxyWithUnencodedInput() {
		URLHelper uh = new URLHelper("imgix-library-web-proxy-test-source.imgix.net", "https://paulstraw.imgix.net/colon:test/benice.jpg", "https", "qN5VOqaLGQUFzETO");
		assertEquals(uh.getURL(), "https://imgix-library-web-proxy-test-source.imgix.net/https%3A%2F%2Fpaulstraw.imgix.net%2Fcolon%3Atest%2Fbenice.jpg?s=175a054524d75840735855b9263be591");
	}

	@Test
	public void testBuilderWithFullyQualifiedURL() {
		URLBuilder ub = new URLBuilder(new String[] { "my-social-network.imgix.net" }, true, "FOO123bar", URLBuilder.ShardStrategy.CRC, false);
		assertEquals(ub.createURL("http://avatars.com/john-smith.png"), "https://my-social-network.imgix.net/http%3A%2F%2Favatars.com%2Fjohn-smith.png?s=493a52f008c91416351f8b33d4883135");
	}

	@Test
	public void testBuilderWithFullyQualifiedURLAndParameters() {
		URLBuilder ub = new URLBuilder(new String[] { "my-social-network.imgix.net" }, true, "FOO123bar", URLBuilder.ShardStrategy.CRC, false);
		Map<String, String> params = new HashMap<String, String>();
		params.put("w", "400");
		params.put("h", "300");
		assertEquals(ub.createURL("http://avatars.com/john-smith.png", params), "https://my-social-network.imgix.net/http%3A%2F%2Favatars.com%2Fjohn-smith.png?h=300&w=400&s=a201fe1a3caef4944dcb40f6ce99e746");
	}

	@Test
	public void testHelperBuildSignedUrlWithIxlibParam() {
		String[] domains = new String[] { "assets.imgix.net" };
		URLBuilder ub = new URLBuilder(domains, true, "", URLBuilder.ShardStrategy.CRC, true);
		assertTrue(hasURLParameter(ub.createURL("/users/1.png"), "ixlib"));

		ub = new URLBuilder(domains, true, "", URLBuilder.ShardStrategy.CRC, false);
		assertFalse(hasURLParameter(ub.createURL("/users/1.png"), "ixlib"));
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
	public void testParamKeysAreEscaped() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("hello world", "interesting");

		URLHelper uh = new URLHelper("demo.imgix.net", "demo.png", "https", null, params);

		assertEquals("https://demo.imgix.net/demo.png?hello%20world=interesting", uh.getURL());
	}

	@Test
	public void testParamValuesAreEscaped() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("hello_world", "/foo\"> <script>alert(\"hacked\")</script><");

		URLHelper uh = new URLHelper("demo.imgix.net", "demo.png", "https", null, params);

		assertEquals("https://demo.imgix.net/demo.png?hello_world=%2Ffoo%22%3E%20%3Cscript%3Ealert(%22hacked%22)%3C%2Fscript%3E%3C", uh.getURL());
	}

	@Test
	public void testBase64ParamVariantsAreBase64Encoded() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("txt64", "I cannøt belîév∑ it wors! 😱");

		URLHelper uh = new URLHelper("demo.imgix.net", "~text", "https", null, params);

		assertEquals("https://demo.imgix.net/~text?txt64=SSBjYW5uw7h0IGJlbMOuw6l24oiRIGl0IHdvcu-jv3MhIPCfmLE", uh.getURL());
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

	@Test
	public void testDomainShardingConstructorDeprecated1() {
		Class[] classes = new Class[]{
				String[].class, boolean.class, String.class,
				URLBuilder.ShardStrategy.class, boolean.class
		};
		assertTrue(hasDeprecationAnnotationConstructor(classes));
	}

	@Test
	public void testDomainShardingConstructorDeprecated2() {
		Class[] classes = new Class[]{
				String[].class
		};
		assertTrue(hasDeprecationAnnotationConstructor(classes));
	}

	@Test
	public void testDomainShardingConstructorDeprecated3() {
		Class[] classes = new Class[]{
				String[].class, boolean.class
		};
		assertTrue(hasDeprecationAnnotationConstructor(classes));
	}

	@Test
	public void testDomainShardingConstructorDeprecated4() {
		Class[] classes = new Class[]{
				String[].class, boolean.class, String.class
		};
		assertTrue(hasDeprecationAnnotationConstructor(classes));
	}

	@Test
	public void testSetShardStrategyDeprecated() {
		assertTrue(hasDeprecationAnnotationMethod("setShardStratgy", URLBuilder.ShardStrategy.class));
	}

	private boolean hasDeprecationAnnotationConstructor(Class[] classes) {
		Annotation[] annotations = new Annotation[0];

		try {
			annotations = URLBuilder.class.getConstructor(classes).getAnnotations();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		for (Annotation annotation : annotations){
			if(annotation.annotationType().equals(Deprecated.class)) {
				return true;
			}
		}

		return false;
	}

	private boolean hasDeprecationAnnotationMethod(String name, Class type) {
		Annotation[] annotations = new Annotation[0];

		try {
			annotations = URLBuilder.class.getDeclaredMethod(name,type).getAnnotations();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		for (Annotation annotation : annotations){
			if(annotation.annotationType().equals(Deprecated.class)) {
				return true;
			}
		}

		return false;

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

	private static boolean hasURLParameter(String url, String param) {
		try {
			URI parsed = new URI(url);
			String query = parsed.getQuery();
			return query != null && query.contains(param);
		} catch (URISyntaxException e) {
			return false;
		}
	}
}
