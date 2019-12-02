package com.imgix;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.URLDecoder;

import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.TreeMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Base64;

public class URLHelper {

	private String domain;
	private String path;
	private String scheme;
	private String signKey;
	private Map<String, String> parameters;

	public URLHelper(String domain, String path, String scheme, String signKey, Map<String, String> parameters) {
		this.domain = domain;
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
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
		byte[] stringBytes = new byte[0];
		String b64EncodedString = null;

		try {
			stringBytes = str.getBytes("UTF-8");
			b64EncodedString = new String(Base64.getEncoder().encode(stringBytes), "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}

		b64EncodedString = b64EncodedString.replace("=", "");
		b64EncodedString = b64EncodedString.replace('/', '_');
		b64EncodedString = b64EncodedString.replace('+', '-');

		return b64EncodedString;
	}

	public String getURL() {
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

		String decodedPath = URLHelper.decodeURIComponent(path.substring(1));
		if (decodedPath.startsWith("http://") || decodedPath.startsWith("https://")) {
			path = "/" + URLHelper.encodeURIComponent(decodedPath);
		}

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
		// do not use URI to build URL since it will do auto-encoding which can break our previous signing
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
			byte[] array = md.digest(md5.getBytes("UTF-8"));
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
			  sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
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
		for(String s: strings) {
			sb.append(sep).append(s);
			sep = separator;
		}
		return sb.toString();
	}

	public static String encodeURIComponent(String s) {
		String result = null;

		try {
		  result = URLEncoder.encode(s, "UTF-8")
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

	public static String decodeURIComponent(String s) {
		if (s == null) {
			return null;
		}

		String result = null;

		try {
			result = URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			result = s;
		}

		return result;
  }

}
