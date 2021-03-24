package com.imgix.test;

import org.junit.Test;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.*;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

import com.imgix.URLBuilder;
import com.imgix.URLHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.*;

@RunWith(JUnit4.class)
public class TestAll {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    public static ExpectedException urlException = ExpectedException.none();

    @Test
    public void testURLBuilderRaisesExceptionOnNoDomains() {
        exception.expect(IllegalArgumentException.class);
        new URLBuilder(new String());
    }

    @Test
    public void testURLBuilderUsesHttpsByDefault() {
        // Test `URLBuilder` uses https by default.
        // This test uses the single-parameter constructor.
        // The single parameter constructor is the only constructor
        // where "default" makes sense. E.g. calling
        // `URLBuilder("example.imgix.net", true)`
        // passes `true` to the constructor explicitly.
        URLBuilder ub = new URLBuilder("example.imgix.net");
        String expected = "https://example.imgix.net/image/file.png?ixlib=java-" + URLBuilder.VERSION;
        assertEquals(expected, ub.createURL("image/file.png"));
    }

    @Test
    public void testSetUseHttpsFalse() {
        // Test `setUseHttps` to `false`.
        URLBuilder ub = new URLBuilder("example.imgix.net");
        ub.setUseHttps(false);
        String expected = "http://example.imgix.net/image/file.png?ixlib=java-" + URLBuilder.VERSION;
        assertEquals(expected, ub.createURL("image/file.png"));
    }

    @Test
    public void testSetUseHttpsTrue() {
        // Test `setUseHttps` to `true`.
        URLBuilder ub = new URLBuilder("example.imgix.net");
        ub.setUseHttps(true);
        String expected = "https://example.imgix.net/image/file.png?ixlib=java-" + URLBuilder.VERSION;
        assertEquals(expected, ub.createURL("image/file.png"));
    }

    @Test
    public void testHelperBuildAbsolutePath() {
        URLHelper uh = new URLHelper("securejackangers.imgix.net", "/example/chester.png", "http");
        assertEquals("http://securejackangers.imgix.net/example/chester.png", uh.getURL());
    }

    @Test
    public void testHelperBuildRelativePath() {
        URLHelper uh = new URLHelper("securejackangers.imgix.net", "example/chester.png", "http");
        assertEquals("http://securejackangers.imgix.net/example/chester.png", uh.getURL());
    }

    @Test
    public void testHelperBuildNestedPath() {
        URLHelper uh = new URLHelper("securejackangers.imgix.net", "http://www.somedomain.com/example/chester.png",
                "http");
        assertEquals("http://securejackangers.imgix.net/http%3A%2F%2Fwww.somedomain.com%2Fexample%2Fchester.png",
                uh.getURL());
    }

    @RunWith(Parameterized.class)
    public static class TestHelperBuildPathWithParams {
        @Parameters(name = "''{0}' URL generated correctly'")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][] { { "Nested Path With Params",
                    "http://www.somedomain.com/example/chester.png",
                    "http://securejackangers.imgix.net/http%3A%2F%2Fwww.somedomain.com%2Fexample%2Fchester.png?w=500" },
                    { "Relative Path With Params", "example/chester.png",
                            "http://securejackangers.imgix.net/example/chester.png?w=500" },
                    { "Absolute Path With Params", "/example/chester.png",
                            "http://securejackangers.imgix.net/example/chester.png?w=500" }, });
        }

        @Parameter(0)
        public String tInputName;
        @Parameter(1)
        public String tInput;
        @Parameter(2)
        public String tExpected;

        public void testHelperBuildPathWithParams(String input, String expected) {
            tInput = input;
            tExpected = expected;
        }

        @Test
        public void test() {
            URLHelper uh = new URLHelper("securejackangers.imgix.net", tInput, "http");
            uh.setParameter("w", 500);
            assertThat("${tInputName} is not ${tExpected}", uh.getURL(), containsString(tExpected));
        }
    }

    @Test
    public void testHelperBuildSignedURLWithHashMapParams() {

        Map<String, String> params = new HashMap<String, String>();
        params.put("w", "500");

        URLHelper uh = new URLHelper("securejackangers.imgix.net", "example/chester.png", "http", "Q61NvXIy", params);
        assertEquals("http://securejackangers.imgix.net/example/chester.png?w=500&s=787b9057d5c077fe168b4849737d8a90",
                uh.getURL());
    }

    @Test
    public void testHelperBuildSignedURLWithHashSetterParams() {
        URLHelper uh = new URLHelper("securejackangers.imgix.net", "example/chester.png", "http", "Q61NvXIy");

        uh.setParameter("w", 500);

        assertEquals("http://securejackangers.imgix.net/example/chester.png?w=500&s=787b9057d5c077fe168b4849737d8a90",
                uh.getURL());
    }

    @Test
    public void testHelperBuildSignedURLWithWebProxyWithNoEncoding() {
        URLHelper uh = new URLHelper("jackttl2.imgix.net",
                "http://a.abcnews.com/assets/images/navigation/abc-logo.png?r=20", "http", "JHrM2ezd");
        assertEquals(
                "http://jackttl2.imgix.net/http%3A%2F%2Fa.abcnews.com%2Fassets%2Fimages%2Fnavigation%2Fabc-logo.png%3Fr%3D20?s=cf82defe3436a957262d0e64c21e72f9",
                uh.getURL());
    }

    @Test
    public void testHelperBuildSignedURLWithWebProxyWithEncoding() {
        URLHelper uh = new URLHelper("jackttl2.imgix.net",
                "http%3A%2F%2Fa.abcnews.com%2Fassets%2Fimages%2Fnavigation%2Fabc-logo.png%3Fr%3D20", "http",
                "JHrM2ezd");
        assertEquals(
                "http://jackttl2.imgix.net/http%3A%2F%2Fa.abcnews.com%2Fassets%2Fimages%2Fnavigation%2Fabc-logo.png%3Fr%3D20?s=cf82defe3436a957262d0e64c21e72f9",
                uh.getURL());
    }

    @Test
    public void testBuildSignedURLWithWebProxyWithUnencodedInput() {
        URLHelper uh = new URLHelper("imgix-library-web-proxy-test-source.imgix.net",
                "https://paulstraw.imgix.net/colon:test/benice.jpg", "https", "qN5VOqaLGQUFzETO");
        assertEquals(
                "https://imgix-library-web-proxy-test-source.imgix.net/https%3A%2F%2Fpaulstraw.imgix.net%2Fcolon%3Atest%2Fbenice.jpg?s=175a054524d75840735855b9263be591",
                uh.getURL());
    }

    @Test
    public void testBuilderWithFullyQualifiedURL() {
        URLBuilder ub = new URLBuilder("my-social-network.imgix.net", true, "FOO123bar", false);
        assertEquals(
                "https://my-social-network.imgix.net/http%3A%2F%2Favatars.com%2Fjohn-smith.png?s=493a52f008c91416351f8b33d4883135",
                ub.createURL("http://avatars.com/john-smith.png"));
    }

    @Test
    public void testBuilderWithFullyQualifiedURLAndParameters() {
        URLBuilder ub = new URLBuilder("my-social-network.imgix.net", true, "FOO123bar", false);
        Map<String, String> params = new HashMap<String, String>();
        params.put("w", "400");
        params.put("h", "300");
        assertEquals(
                "https://my-social-network.imgix.net/http%3A%2F%2Favatars.com%2Fjohn-smith.png?h=300&w=400&s=a201fe1a3caef4944dcb40f6ce99e746",
                ub.createURL("http://avatars.com/john-smith.png", params));
    }

    @Test
    public void testHelperBuildSignedUrlWithIxlibParam() {
        URLBuilder ub = new URLBuilder("assets.imgix.net", true, "", true);
        assertTrue(hasURLParameter(ub.createURL("/users/1.png"), "ixlib"));

        ub = new URLBuilder("assets.imgix.net", true, "", false);
        assertFalse(hasURLParameter(ub.createURL("/users/1.png"), "ixlib"));
    }

    @Test
    public void testTargetWidths() {
        ArrayList<Integer> actual = URLBuilder.targetWidths(100, 8192, 0.08);
        int[] targetWidths = { 100, 116, 135, 156, 181, 210, 244, 283, 328, 380, 441, 512, 594, 689, 799, 927, 1075,
                1247, 1446, 1678, 1946, 2257, 2619, 3038, 3524, 4087, 4741, 5500, 6380, 7401, 8192 };
        for (int i = 0; i < targetWidths.length; ++i) {
            assertEquals(targetWidths[i], (int) actual.get(i));
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

        assertEquals(
                "https://demo.imgix.net/demo.png?hello_world=%2Ffoo%22%3E%20%3Cscript%3Ealert(%22hacked%22)%3C%2Fscript%3E%3C",
                uh.getURL());
    }

    @Test
    public void testBase64ParamVariantsAreBase64Encoded() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("txt64", "I cannøt belîév∑ it wors! 😱");

        URLHelper uh = new URLHelper("demo.imgix.net", "~text", "https", null, params);

        assertEquals("https://demo.imgix.net/~text?txt64=SSBjYW5uw7h0IGJlbMOuw6l24oiRIGl0IHdvcu-jv3MhIPCfmLE",
                uh.getURL());
    }

    @Test
    public void testExtractDomain() {
        String url = "http://jackangers.imgix.net/chester.png";
        assertEquals("jackangers.imgix.net", extractDomain(url));
    }

    @Test
    public void testEncodeDecode() {
        String url = "http://a.abcnews.com/assets/images/navigation/abc-logo.png?r=20";
        String encodedUrl = "http%3A%2F%2Fa.abcnews.com%2Fassets%2Fimages%2Fnavigation%2Fabc-logo.png%3Fr%3D20";

        assertEquals(URLHelper.encodeURIComponent(url), encodedUrl);
        assertEquals(URLHelper.decodeURIComponent(encodedUrl), url);
        assertEquals(URLHelper.encodeURIComponent(URLHelper.decodeURIComponent(encodedUrl)), encodedUrl);
    }

    @RunWith(Parameterized.class)
    public static class TestInvalidDomain {
        @Parameters(name = "'domain with '{0}' throws exception'")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][] { { "Slash", "test.imgix.net/" },
                    { "Prepend Scheme", "https://test.imgix.net" }, { "Append Dash", "test.imgix.net-" }, });
        }

        @Parameter(0)
        public String tInputName;
        @Parameter(1)
        public String tInput;

        public void testInvalidDomain(String input, String expected) {
            tInput = input;
        }

        @Test(expected = IllegalArgumentException.class)
        public void test() {
            new URLBuilder(tInput);
        }
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
