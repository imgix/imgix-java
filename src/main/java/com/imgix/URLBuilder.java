package com.imgix;

import java.util.Map;
import java.util.TreeMap;

public class URLBuilder {

	public static final String VERSION = "1.2.0";

	private String domain;
	private boolean useHttps;
	private String signKey;
	private boolean signWithLibraryParameter;

	public URLBuilder(String domain, boolean useHttps, String signKey, boolean signWithLibraryParameter) {

		if (domain == null || domain.length() == 0) {
			throw new IllegalArgumentException("At lease one domain must be passed to URLBuilder");
		}

		this.domain = domain;
		this.useHttps = useHttps;
		this.signKey = signKey;
		this.signWithLibraryParameter = signWithLibraryParameter;
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

		if (this.signWithLibraryParameter) {
			params.put("ixlib", "java-" + VERSION);
		}

		return new URLHelper(domain, path, scheme, signKey, params).getURL();
	}

	public static void main(String[] args) {
		System.out.println("Hello.");
	}

}
