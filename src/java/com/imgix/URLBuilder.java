package com.imgix;

import java.util.Map;


public class URLBuilder {

	// TODO: add shard strategy	with enum (okay on andriod?)

	private String[] domains;
	private boolean useHttps;
	private String signKey;

	public URLBuilder(String[] domains, boolean useHttps, String signKey) {
		this.domains = domains;
		this.useHttps = useHttps;
		this.signKey = signKey;
	}

	public URLBuilder(String domain) {
		this(new String[] { domain }, false, "");
	}

	public URLBuilder(String domain, boolean useHttps) {
		this(new String[] { domain }, useHttps, "");
	}

	public URLBuilder(String domain, boolean useHttps, String signKey) {
		this(new String[] { domain }, useHttps, signKey);
	}


	public String createURL(String path, Map<String, String> params) {
		String scheme = this.useHttps ? "https": "http";


		// TODO: shard strategy
		return new URLHelper(domains[0], path, scheme, signKey, params).getURL();
	}

	public static void main(String[] args) {
		System.out.println("Hello.");
	}

}
