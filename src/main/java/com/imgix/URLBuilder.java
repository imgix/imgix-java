package com.imgix;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class URLBuilder {

	public static final String VERSION = "2.0.0";
	private static final String DOMAIN_REGEX = "^(?:[a-z\\d\\-_]{1,62}\\.){0,125}(?:[a-z\\d](?:\\-(?=\\-*[a-z\\d])|[a-z]|\\d){0,62}\\.)[a-z\\d]{1,63}$";

	private String domain;
	private boolean useHttps;
	private String signKey;
	private boolean includeLibraryParam;

	public URLBuilder(String domain, boolean useHttps, String signKey, boolean includeLibraryParam) {
		Pattern domainPattern = Pattern.compile(DOMAIN_REGEX);

		if (domain == null || domain.length() == 0) {
			throw new IllegalArgumentException("At lease one domain must be passed to URLBuilder");
		} else if (!domainPattern.matcher(domain).matches()) {
			throw new IllegalArgumentException("Domain must be passed in as fully-qualified domain name and should not include a protocol or any path element, i.e. \"example.imgix.net\".");
		}

		this.domain = domain;
		this.useHttps = useHttps;
		this.signKey = signKey;
		this.includeLibraryParam = includeLibraryParam;
	}

	public URLBuilder(String domain) {
		this(domain, false);
	}

	public URLBuilder(String domain, boolean useHttps) {
		this(domain, useHttps, "");
	}


	public URLBuilder(String domain, boolean useHttps, String signKey) {
		this(domain, useHttps, signKey, true);
	}

	public void setUseHttps(boolean useHttps) {
		this.useHttps = useHttps;
	}

	public void setSignKey(String signKey) {
		this.signKey = signKey;
	}

	public String createURL(String path) {
		return createURL(path, new TreeMap<String, String>());
	}

	public String createURL(String path, Map<String, String> params) {
		String scheme = this.useHttps ? "https": "http";

		if (this.includeLibraryParam) {
			params.put("ixlib", "java-" + VERSION);
		}

		return new URLHelper(domain, path, scheme, signKey, params).getURL();
	}

	public static void main(String[] args) {
		System.out.println("Hello.");
	}

}
