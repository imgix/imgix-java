package com.imgix.test;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.*;

import com.imgix.URLBuilder;

import java.util.*;

@RunWith(JUnit4.class)
public class TestReadMe {
    @Test
    public void testReadMeWidthTolerance() {
        URLBuilder ub = new URLBuilder("demo.imgix.net", true, "", false);
        HashMap<String, String>  params = new HashMap<String, String>();
        String actual = ub.createSrcSet("image.jpg", params, 100, 384, 20);
        String expected = "https://demo.imgix.net/image.jpg?w=100 100w,\n" +
            "https://demo.imgix.net/image.jpg?w=140 140w,\n" +
            "https://demo.imgix.net/image.jpg?w=196 196w,\n" +
            "https://demo.imgix.net/image.jpg?w=274 274w,\n" +
            "https://demo.imgix.net/image.jpg?w=384 384w";

        assertEquals(expected, actual);
    }

    @Test
    public void testReadMeMinMaxWidthRanges() {
        URLBuilder ub = new URLBuilder("demo.imgix.net", true, "", false);
        HashMap<String, String>  params = new HashMap<String, String>();
        String actual = ub.createSrcSet("image.jpg", params, 500, 2000);
        String expected = "https://demo.imgix.net/image.jpg?w=500 500w,\n" +
            "https://demo.imgix.net/image.jpg?w=580 580w,\n" +
            "https://demo.imgix.net/image.jpg?w=673 673w,\n" +
            "https://demo.imgix.net/image.jpg?w=780 780w,\n" +
            "https://demo.imgix.net/image.jpg?w=905 905w,\n" +
            "https://demo.imgix.net/image.jpg?w=1050 1050w,\n" +
            "https://demo.imgix.net/image.jpg?w=1218 1218w,\n" +
            "https://demo.imgix.net/image.jpg?w=1413 1413w,\n" +
            "https://demo.imgix.net/image.jpg?w=1639 1639w,\n" +
            "https://demo.imgix.net/image.jpg?w=1901 1901w,\n" +
            "https://demo.imgix.net/image.jpg?w=2000 2000w";

        assertEquals(expected, actual);
    }

    @Test
    public void testReadMeCustomWidths() {
        URLBuilder ub = new URLBuilder("demo.imgix.net", true, "", false);
        HashMap<String, String>  params = new HashMap<String, String>();
        String actual = ub.createSrcSet("image.jpg", params, new Integer[] {144, 240, 320, 446, 640});
        String expected = "https://demo.imgix.net/image.jpg?w=144 144w,\n" +
            "https://demo.imgix.net/image.jpg?w=240 240w,\n" +
            "https://demo.imgix.net/image.jpg?w=320 320w,\n" +
            "https://demo.imgix.net/image.jpg?w=446 446w,\n" +
            "https://demo.imgix.net/image.jpg?w=640 640w";

        assertEquals(expected, actual);
    }

    @Test
    public void testReadMeVariableQuality() {
        URLBuilder ub = new URLBuilder("demo.imgix.net", true, "", false);
        HashMap<String, String>  params = new HashMap<String, String>();
        params.put("w", "100");
        String actual = ub.createSrcSet("image.jpg", params, false);
        String expected = "https://demo.imgix.net/image.jpg?dpr=1&q=75&w=100 1x,\n" +
            "https://demo.imgix.net/image.jpg?dpr=2&q=50&w=100 2x,\n" +
            "https://demo.imgix.net/image.jpg?dpr=3&q=35&w=100 3x,\n" +
            "https://demo.imgix.net/image.jpg?dpr=4&q=23&w=100 4x,\n" +
            "https://demo.imgix.net/image.jpg?dpr=5&q=20&w=100 5x";

        assertEquals(expected, actual);
    }

    @Test
    public void testReadMeFixedWidthImages() {
        URLBuilder ub = new URLBuilder("demo.imgix.net", true, "", false);
        HashMap<String, String>  params = new HashMap<String, String>();
        params.put("h", "800");
        params.put("ar", "3:2");
        params.put("fit", "crop");
        String actual = ub.createSrcSet("image.jpg", params, false);
        String expected = "https://demo.imgix.net/image.jpg?ar=3%3A2&dpr=1&fit=crop&h=800&q=75 1x,\n" +
                "https://demo.imgix.net/image.jpg?ar=3%3A2&dpr=2&fit=crop&h=800&q=50 2x,\n" +
                "https://demo.imgix.net/image.jpg?ar=3%3A2&dpr=3&fit=crop&h=800&q=35 3x,\n" +
                "https://demo.imgix.net/image.jpg?ar=3%3A2&dpr=4&fit=crop&h=800&q=23 4x,\n" +
                "https://demo.imgix.net/image.jpg?ar=3%3A2&dpr=5&fit=crop&h=800&q=20 5x";

        assertEquals(expected, actual);
    }
}