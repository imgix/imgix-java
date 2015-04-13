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

	public String getURL() {
		List<String> queryPairs = new LinkedList<String>();

		for (Entry<String, String> entry : parameters.entrySet()) {
			String k = entry.getKey();
			String v = entry.getValue();
			queryPairs.add(k + "=" + encodeURIComponent(v));
		}

		String query = joinList(queryPairs, "&");

		if (signKey != null && signKey.length() > 0) {
			String newPath = path;
			String restPath = path.substring(1);
			if (URLHelper.decodeURIComponent(restPath).equals(restPath)) {
				newPath = "/" + encodeURIComponent(newPath.substring(1));
			}
			String delim = query.equals("") ? "" : "?";
			String toSign = signKey + newPath + delim + query;
			String signature = MD5(toSign);

			if (query.length() > 0) {
				query += "&s=" + signature;
			} else {
				query = "s=" + signature;
			}

			return buildURL(scheme, domain, newPath, query);
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
