package com.imgix;

import java.util.Map;
import java.util.TreeMap;
import java.util.zip.CRC32;

public class URLBuilder {

	public static final String VERSION = "1.2.0";

	public enum ShardStrategy {
		CRC,
		CYCLE
	}

	private String[] domains;
	private boolean useHttps;
	private String signKey;
	private ShardStrategy shardStrategy;
	private boolean includeLibraryParam;

	private int shardCycleNextIndex = 0;

	/**
	 * @deprecated  As of 1.2.0, domain sharding has been deprecated
	 * 				and will be removed in the next major version.
	 */
	@Deprecated
	public URLBuilder(String[] domains, boolean useHttps, String signKey, ShardStrategy shardStrategy, boolean includeLibraryParam) {

		if (domains == null || domains.length == 0) {
			throw new IllegalArgumentException("At lease one domain must be passed to URLBuilder");
		}

		this.domains = domains;
		this.useHttps = useHttps;
		this.signKey = signKey;
		this.shardStrategy = shardStrategy;
		this.includeLibraryParam = includeLibraryParam;
	}

	public URLBuilder(String domain) {
		this(new String[] {domain}, false);
	}

	/**
	 * @deprecated  As of 1.2.0, domain sharding has been deprecated
	 * 				and will be removed in the next major version.
	 */
	@Deprecated
	public URLBuilder(String[] domain) {
		this(domain, false);
	}

	/**
	 * @deprecated  As of 1.2.0, domain sharding has been deprecated
	 * 				and will be removed in the next major version.
	 */
	@Deprecated
	public URLBuilder(String[] domain, boolean useHttps) {
		this(domain, useHttps, "");
	}

	public URLBuilder(String domain, boolean useHttps) {
		this(new String[] {domain}, useHttps, "");
	}

	/**
	 * @deprecated  As of 1.2.0, domain sharding has been deprecated
	 * 				and will be removed in the next major version.
	 */
	@Deprecated
	public URLBuilder(String[] domain, boolean useHttps, String signKey) {
		this(domain, useHttps, signKey, ShardStrategy.CRC, true);
	}

	public URLBuilder(String domain, boolean useHttps, String signKey) {
		this(new String[] {domain}, useHttps, signKey, ShardStrategy.CRC, true);
	}

	public URLBuilder(String domain, boolean useHttps, String signKey, ShardStrategy shardStrategy) {
		this(new String[] {domain}, useHttps, signKey, shardStrategy, true);
	}

	/**
	 * @deprecated  As of 1.2.0, domain sharding has been deprecated
	 * 				and will be removed in the next major version.
	 */
	@Deprecated
	public void setShardStratgy(ShardStrategy strat) {
		shardStrategy = strat;
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

		String domain;

		if (shardStrategy == ShardStrategy.CRC) {
			CRC32 c = new CRC32();
			c.update(path.getBytes());
			int index = (int) (c.getValue() % domains.length);
			domain = domains[index];
		} else if (shardStrategy == ShardStrategy.CYCLE) {
			shardCycleNextIndex = (shardCycleNextIndex + 1) % domains.length;
			domain = domains[shardCycleNextIndex];
		} else {
			domain = domains[0];
		}

		if (this.includeLibraryParam) {
			params.put("ixlib", "java-" + VERSION);
		}

		return new URLHelper(domain, path, scheme, signKey, params).getURL();
	}

	public static void main(String[] args) {
		System.out.println("Hello.");
	}

}
