package com.imgix;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class URLHelper {

  private static Boolean isEncoded = false;
  private static Boolean isProxy = false;
  private static final String UTF_8 = "UTF-8";
  private String domain;
  private String path;
  private String scheme;
  private String signKey;
  private Map<String, String> parameters;

  public URLHelper(
      String domain, String path, String scheme, String signKey, Map<String, String> parameters) {
    this.domain = domain;
    this.path = path;
    this.scheme = scheme;
    this.signKey = signKey;
    this.parameters = new TreeMap<String, String>(parameters);
  }

  public URLHelper(String domain, String path, String scheme, String signKey) {
    this(domain, path, scheme, signKey, new TreeMap<String, String>());
  }

  public URLHelper(String domain, String path, String scheme) {
    this(domain, path, scheme, "");
  }

  public URLHelper(String domain, String path) {
    this(domain, path, "http");
  }

  public void setParameter(String key, String value) {
    if (value != null && value.length() > 0) {
      parameters.put(key, value);
    } else {
      parameters.remove(key);
    }
  }

  public void setParameter(String key, Number value) {
    setParameter(key, String.valueOf(value));
  }

  public void deleteParameter(String key) {
    setParameter(key, "");
  }

  private String encodeBase64(String str) {
    String b64EncodedString = null;

    try {
      byte[] stringBytes = str.getBytes(UTF_8);
      b64EncodedString = new String(Base64.getEncoder().encode(stringBytes), UTF_8);
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException(e);
    }

    b64EncodedString = b64EncodedString.replace("=", "");
    b64EncodedString = b64EncodedString.replace('/', '_');
    b64EncodedString = b64EncodedString.replace('+', '-');

    return b64EncodedString;
  }

  public String getURL() {
    path = sanitizePath(path);
    List<String> queryPairs = new LinkedList<String>();

    for (Entry<String, String> entry : parameters.entrySet()) {
      String k = encodeURIComponent(entry.getKey());
      String v = entry.getValue();

      String encodedValue;

      if (k.endsWith("64")) {
        encodedValue = encodeBase64(v);
      } else {
        encodedValue = encodeURIComponent(v);
      }
      queryPairs.add(k + "=" + encodedValue);
    }

    String query = joinList(queryPairs, "&");

    if (signKey != null && signKey.length() > 0) {
      String delim = query.equals("") ? "" : "?";
      String toSign = signKey + path + delim + query;
      String signature = MD5(toSign);

      if (query.length() > 0) {
        query += "&s=" + signature;
      } else {
        query = "s=" + signature;
      }

      return buildURL(scheme, domain, path, query);
    }

    return buildURL(scheme, domain, path, query);
  }

  @Override
  public String toString() {
    return getURL();
  }

  ///////////// Static

  private static String buildURL(String scheme, String host, String path, String query) {
    // do not use URI to build URL since it will do auto-encoding which can break
    // our previous signing
    String url = String.format("%s://%s%s?%s", scheme, host, path, query);
    if (url.endsWith("#")) {
      url = url.substring(0, url.length() - 1);
    }

    if (url.endsWith("?")) {
      url = url.substring(0, url.length() - 1);
    }

    return url;
  }

  private static String MD5(String md5) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] array = md.digest(md5.getBytes(UTF_8));
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < array.length; ++i) {
        sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
      }
      return sb.toString();
    } catch (UnsupportedEncodingException e) {
    } catch (NoSuchAlgorithmException e) {
    }
    return null;
  }

  private static String joinList(List<String> strings, String separator) {
    StringBuilder sb = new StringBuilder();
    String sep = "";
    for (String s : strings) {
      sb.append(sep).append(s);
      sep = separator;
    }
    return sb.toString();
  }

  // checkProxyStatus checks if the path has one of the four possible
  // acceptable proxy prefixes. First we check if the path has the
  // correct ascii prefix. If it does then we know that it is a proxy,
  // but it's not percent encoded. Second, we check if the path is
  // prefixed by a percent-encoded prefix. If it is, we know that it's
  // a proxy and that it's percent-encoded. Finally, if the path isn't
  // prefixed by any of these four prefixes, it is not a valid proxy.
  // This might be "just enough validation," but if we run into issues
  // we can make this check smarter/more-robust.
  public static Map<String, Boolean> checkProxyStatus(String p) {
    Map<String, Boolean> status = new HashMap<String, Boolean>();
    String path = p;
    path.replaceAll("^/", "");

    String asciiHTTP = "http://";
    String asciiHTTPS = "https://";

    String encodedHTTP = "http%3A%2F%2F";
    String encodedHTTPS = "https%3A%2F%2F";

    String encodedHTTPLower = "http%3a%2f%2f";
    String encodedHTTPSLower = "https%3a%ff%2f";

    if (path.startsWith(asciiHTTP) || path.startsWith(asciiHTTPS)) {
      isProxy = true;
      isEncoded = false;

    } else if (path.startsWith(encodedHTTP)
        || path.startsWith(encodedHTTPS)
        || path.startsWith(encodedHTTPLower)
        || path.startsWith(encodedHTTPSLower)) {
      isProxy = true;
      isEncoded = true;

    } else {
      isProxy = false;
      isEncoded = false;
    }

    status.put("isProxy", isProxy);
    status.put("isEncoded", isEncoded);

    return status;
  }

  /**
   * isASCIIEncoded ensures that the string, p, only contains characters in the range x00-x7F and
   * not in the list ` ?$:+#`. This is a combination of typical ASCII regex validation with some
   * custom character replacement we do at imgix.
   */
  public static Boolean isASCIIEncoded(String p) {
    return p.matches("[\\x00-\\x7F]+") && p.matches("[^\\ ?$:+#]+");
  }

  public static String encodeURIComponent(String s) {
    String result = null;

    try {
      result =
          URLEncoder.encode(s, UTF_8)
              .replaceAll("\\+", "%20")
              .replaceAll("\\%21", "!")
              .replaceAll("\\%27", "'")
              .replaceAll("\\%28", "(")
              .replaceAll("\\%29", ")")
              .replaceAll("\\%7E", "~");
    } catch (UnsupportedEncodingException e) {
      result = s;
    }

    return result;
  }

  /**
   * Accepts a path and encodes it using UTF8 scheme. Also encodes `+$:? ` and decodes `!'()~`. This
   * method splits the s, or path, on "/" to avoid encoding them. It then encodes each character.
   * Finally, the method joins the encoded characters on "/" to restore the delimiter.
   */
  public static String encodeURI(String s) {
    String[] splitString = s.split("/");
    for (int i = 0; i < splitString.length; i++) {
      String str = splitString[i];
      try {
        splitString[i] =
            URLEncoder.encode(str, UTF_8)
                .replaceAll("\\+", "%20")
                .replaceAll("\\:", "%3A")
                .replaceAll("\\?", "%3F")
                .replaceAll("\\#", "%23")
                .replaceAll(" ", "%20")
                .replaceAll("\\%21", "!")
                .replaceAll("\\%27", "'")
                .replaceAll("\\%28", "(")
                .replaceAll("\\%29", ")")
                .replaceAll("\\%7E", "~");
      } catch (UnsupportedEncodingException e) {
      }
    }
    return String.join("/", splitString);
  }

  public static String sanitizePath(String path) {
    // Strip leading slash first (we'll re-add after encoding)
    path = path.replaceAll("^/", "");
    // Check if path is a proxy path and store type of proxy
    Map proxyStatus = checkProxyStatus(path);
    Object pathIsProxy = proxyStatus.get("isProxy");
    Object proxyIsEncoded = proxyStatus.get("isEncoded");

    if (pathIsProxy.equals(true) && proxyIsEncoded.equals(false)) {
      // Use encodeURIComponent to ensure *all* characters are handled,
      // since it's being used as a path
      return "/" + URLHelper.encodeURIComponent(path);
    } else if (path.startsWith("http%3A") || path.startsWith("https%3A")) {
      // To nothing to the URL being used as path since already encoded
      return "/" + path;
    } else {
      // Use encodeURI if we think the path is just a path,
      // so it leaves legal characters like '/' and '@' alone
      return "/" + encodeURI(path);
    }
  }
}
