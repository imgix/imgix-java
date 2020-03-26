package com.imgix;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class URLBuilder {

	public static final String VERSION = "2.1.1";
	private static final String DOMAIN_REGEX = "^(?:[a-z\\d\\-_]{1,62}\\.){0,125}(?:[a-z\\d](?:\\-(?=\\-*[a-z\\d])|[a-z]|\\d){0,62}\\.)[a-z\\d]{1,63}$";

	private String domain;
	private boolean useHttps;
	private String signKey;
	private boolean includeLibraryParam;
	private final ArrayList<Integer> SRCSET_TARGET_WIDTHS = this.targetWidths();

	public URLBuilder(String domain, boolean useHttps, String signKey, boolean includeLibraryParam) {
		Pattern domainPattern = Pattern.compile(DOMAIN_REGEX);

		if (domain == null || domain.length() == 0) {
			throw new IllegalArgumentException("At lease one domain must be passed to URLBuilder");
		} else if (!domainPattern.matcher(domain).matches()) {
			throw new IllegalArgumentException("Domain must be passed in as a fully-qualified domain name and should not include a protocol or any path element, i.e. \"example.imgix.net\".");
		}

		this.domain = domain;
		this.useHttps = useHttps;
		this.signKey = signKey;
		this.includeLibraryParam = includeLibraryParam;
	}

	/**
	 * This single-parameter constructor, accepts a domain string and
	 * returns a `URLBuilder` with it's `useHttps` member variable set
	 * to `true` by default.
	 *
	 * @param domain - a valid domain string, e.g. `example.imgix.net`
	 */
	public URLBuilder(String domain) {
		this(domain, true);
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

	public String createSrcSet(String path) {
		return createSrcSet(path, new TreeMap<String, String>());
	}

	public String createSrcSet(String path, Map<String, String> params) {
		String width = params.get("w");
		String height = params.get("h");
		String aspectRatio = params.get("ar");

		/* builds a DPR srcset if either:
			   a width or
			   a height and an aspect ratio
		   are provided, otherwise builds a
		   srcset of width-pairs
		 */
		if (!(width == null || width.isEmpty())
				|| !((height == null || height.isEmpty())
				|| (aspectRatio == null || aspectRatio.isEmpty()))) {
			return createSrcSetDPR(path, params);
		} else {
			return createSrcSetPairs(path, params);
		}
	}

	private String createSrcSetPairs(String path, Map<String, String> params) {
		String srcset = "";

		for (Integer width: this.SRCSET_TARGET_WIDTHS) {
			params.put("w", width.toString());
			srcset += this.createURL(path, params) + " " + width + "w,\n";
		}

		return srcset.substring(0, srcset.length() - 2);
	}

	private String createSrcSetDPR(String path, Map<String, String> params) {
		String srcset = "";
		int[] srcsetTargetRatios = {1,2,3,4,5};

		for (int ratio: srcsetTargetRatios) {
			params.put("dpr", Integer.toString(ratio));
			srcset += this.createURL(path, params) + " " + ratio + "x,\n";
		}

		return srcset.substring(0, srcset.length() - 2);
	}

	private static ArrayList<Integer> targetWidths() {
		ArrayList<Integer> resolutions = new ArrayList<Integer>();
		int MAX_SIZE = 8192, roundedPrev;
		double SRCSET_INCREMENT_PERCENTAGE = 8;
		double prev = 100;

		while (prev < MAX_SIZE) {
			// ensures the added width is even
			roundedPrev = (int)(2 * Math.round(prev / 2));
			resolutions.add(roundedPrev);
			prev *= 1 + (SRCSET_INCREMENT_PERCENTAGE / 100) * 2;
		}
		resolutions.add(MAX_SIZE);

		return resolutions;
	}

	public static void main(String[] args) {
		System.out.println("Hello.");
	}

}
