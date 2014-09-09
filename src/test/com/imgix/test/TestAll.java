package com.imgix.test;

import org.junit.Test;
import static org.junit.Assert.*;

import com.imgix.URLBuilder;
import com.imgix.URLHelper;

import java.util.Map;
import java.util.HashMap;

public class TestAll {

	@Test
	public void testHelperBuildSignedURLWithHashMapParams() {

		Map<String, String> params = new HashMap<String, String>();
		params.put("w", "500");

		URLHelper uh = new URLHelper("securejackangers.imgix.net", "chester.png", "http", "Q61NvXIy", params);

		assertEquals(uh.getURL(), "http://securejackangers.imgix.net/chester.png?w=500&s=0ddf97bf1a266a1da6c30c6ce327f917");
	}

	@Test
	public void testHelperBuildSignedURLWithHashSetterParams() {
		URLHelper uh = new URLHelper("securejackangers.imgix.net", "chester.png", "http", "Q61NvXIy");

		uh.setParameter("w", 500);

		assertEquals(uh.getURL(), "http://securejackangers.imgix.net/chester.png?w=500&s=0ddf97bf1a266a1da6c30c6ce327f917");
	}

	@Test
	public void test() {
		assertEquals(0,0);
	}

}
