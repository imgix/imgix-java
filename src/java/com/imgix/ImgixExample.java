package com.imgix;

import com.imgix.URLBuilder;
import java.util.Map;
import java.util.HashMap;

public class ImgixExample {

	public static void readmeExample1() {
		heading("Plain Example");
		URLBuilder builder = new URLBuilder("demos.imgix.net");
		Map<String, String> params = new HashMap<String, String>();
		params.put("w", "100");
		params.put("h", "100");
		System.out.println(builder.createURL("bridge.png", params));
	}

	public static void readmeExample2() {
		heading("Example w/ https");
		URLBuilder builder = new URLBuilder("demos.imgix.net");
		builder.setUseHttps(true);
		Map<String, String> params = new HashMap<String, String>();
		params.put("w", "100");
		params.put("h", "100");
		System.out.println(builder.createURL("bridge.png", params));
	}

	public static void readmeExample3() {
		heading("Example w/ signing");
		URLBuilder builder = new URLBuilder("demos.imgix.net");
		builder.setSignKey("test1234");
		Map<String, String> params = new HashMap<String, String>();
		params.put("w", "100");
		params.put("h", "100");
		System.out.println(builder.createURL("bridge.png", params));
	}

	public static void readmeExample4() {
		heading("Example w/ default CRC domain sharding");
		String[] domains = new String[] { "demos-1.imgix.net", "demos-2.imgix.net", "demos-3.imgix.net"};
		URLBuilder builder = new URLBuilder(domains);

		Map<String, String> params = new HashMap<String, String>();
		params.put("w", "100");
		params.put("h", "100");
		System.out.println(builder.createURL("bridge.png", params));
		System.out.println(builder.createURL("flower.png", params));
	}

	public static void readmeExample5() {
		heading("Example w/ non-default CYCLE domain sharding");
		String[] domains = new String[] { "demos-1.imgix.net", "demos-2.imgix.net", "demos-3.imgix.net"};
		URLBuilder builder = new URLBuilder(domains);

		builder.setShardStratgy(URLBuilder.ShardStrategy.CYCLE);

		Map<String, String> params = new HashMap<String, String>();
		params.put("w", "100");
		params.put("h", "100");

		for (int i = 0; i < 4; i++) {
			System.out.println(builder.createURL("bridge.png", params));
		}
	}

	public static void main(String[] args) {
		readmeExample1();
		readmeExample2();
		readmeExample3();
		readmeExample4();
		readmeExample5();
	}

	private static void heading(String msg) {
		System.out.println("#################################");
		System.out.println("# " + msg);
		System.out.println("#################################");
	}

}
