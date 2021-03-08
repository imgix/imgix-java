package com.imgix;

import java.lang.Math;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class URLBuilder {

    public static final String VERSION = "2.3.0";
    private static final String DOMAIN_REGEX = "^(?:[a-z\\d\\-_]{1,62}\\.){0,125}(?:[a-z\\d](?:\\-(?=\\-*[a-z\\d])|[a-z]|\\d){0,62}\\.)[a-z\\d]{1,63}$";

    private String domain;
    private boolean useHttps;
    private String signKey;
    private boolean includeLibraryParam;

    private static final Integer[] SRCSET_TARGET_WIDTHS = {100, 116, 135, 156, 181, 210, 244, 283,
            328, 380, 441, 512, 594, 689, 799, 927,
            1075, 1247, 1446, 1678, 1946, 2257, 2619,
            3038, 3524, 4087, 4741, 5500, 6380, 7401, 8192};

    private static final double SRCSET_WIDTH_TOLERANCE = 0.08;
    private static final int MIN_WIDTH = 100;
    private static final int MAX_WIDTH = 8192;
    private static final Integer[] DPR_QUALITIES = {75, 50, 35, 23, 20};
    private static final Integer[] TARGET_RATIOS = {1, 2, 3, 4, 5};

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

    /**
     * Create a srcset given a `path` and a map of `params`.
     *
     * This function creates a dpr based srcset if `params`
     * contain either:
     * - a width "w" param _or_ a height "h" param.
     *
     * Otherwise, a srcset of width-pairs is created.
     *
     * @param path - path to the image, i.e. "image/file.png"
     * @param params - map of query parameters
     * @return srcset attribute string
     */
    public String createSrcSet(String path, Map<String, String> params) {
        return createSrcSet(path, params, MIN_WIDTH, MAX_WIDTH, SRCSET_WIDTH_TOLERANCE, false);
    }

    public String createSrcSet(String path) {
        return createSrcSet(path, new TreeMap<String, String>());
    }

    /**
     * Create a srcset given a `path`, map of `params`, and `tol`.
     *
     * This function provides the ability to vary `tol` while
     * generating a srcset over the default range.
     *
     * @param path - path to the image, i.e. "image/file.png"
     * @param params - map of query parameters
     * @param tol - tolerable amount of width value variation
     * @return srcset attribute string
     */
    public String createSrcSet(String path, Map<String, String> params, double tol) {
        return createSrcSet(path, params, MIN_WIDTH, MAX_WIDTH, tol, false);
    }

    /**
     * Create a srcset given a `path`, map of `params`, `begin` and
     * `end`.
     *
     * This function provides the ability to create a srcset attribute
     * over a custom range between `begin` and `end` (inclusively)
     * while using the default srcset width tolerance.
     *
     * @param path - path to the image, i.e. "image/file.png"
     * @param params - map of query parameters
     * @param begin - beginning image width value
     * @param end - ending image width value
     * @return srcset attribute string
     */
    public String createSrcSet(String path, Map<String, String> params, int begin, int end) {
        return createSrcSet(path, params, begin, end, SRCSET_WIDTH_TOLERANCE, false);
    }

    /**
     * Create a srcset given a `path`, map of `params`, `begin`, `end`,
     * and `tol`.
     *
     * This function provides the ability to create a completely custom
     * srcset attribute. The srcset width value range will `begin` and `end`
     * on the specified values. The specified `tol` determines the tolerable
     * amount of width value variation.
     *
     * @param path - path to the image, i.e. "image/file.png"
     * @param params - map of query parameters
     * @param begin - beginning image width value
     * @param end - ending image width value
     * @param tol - tolerable amount of width value variation, from 0.01 to 1.0
     * @return srcset attribute string
     */
    public String createSrcSet(String path, Map<String, String> params, int begin, int end, double tol) {
        return createSrcSet(path, params, begin, end, tol, false);
    }

    /**
     * Create a srcset given a `path`, map of `params`, and the
     * `disableVariableQuality` flag.
     *
     * If `disableVariableQuality` is `false` then variable output
     * is turned _on_. If `disableVariableQuality` is `true` then
     * variable quality output is turned _off_.
     *
     * @param path - path to the image, i.e. "image/file.png"
     * @param params - map of query parameters
     * @param disableVariableQuality - flag to toggle variable image
     * output quality.
     * @return srcset attribute string
     */
    public String createSrcSet(String path, Map<String, String> params, boolean disableVariableQuality) {
        return createSrcSet(path, params, MIN_WIDTH, MAX_WIDTH, SRCSET_WIDTH_TOLERANCE, disableVariableQuality);
    }

    public String createSrcSet(String path, Map<String, String> params, int begin, int end, double tol, boolean disableVariableQuality) {
        if (isDpr(params)) {
            return createSrcSetDPR(path, params, disableVariableQuality);
        } else {
            Integer[] targets = targetWidths(begin, end, tol).toArray(new Integer[0]);
            return createSrcSetPairs(path, params, targets);
        }
    }


    public String createSrcSet(String path, HashMap<String, String> params, Integer[] widths) {
        return createSrcSetPairs(path, params, widths);
    }

    private String createSrcSetPairs(String path, Map<String, String> params) {
        return createSrcSetPairs(path, params, SRCSET_TARGET_WIDTHS);
    }

    private String createSrcSetPairs(String path, Map<String, String> params, Integer[] widths) {
        Validator.validateWidths(widths);

        StringBuilder srcset = new StringBuilder();

        for (Integer width: widths) {
            params.put("w", width.toString());
            srcset.append(this.createURL(path, params)).append(" ").append(width).append("w,\n");
        }

        return srcset.substring(0, srcset.length() - 2);
    }

    private String createSrcSetDPR(String path, Map<String, String> params) {
        return createSrcSetDPR(path, params, false);
    }

    private String createSrcSetDPR(String path, Map<String, String> params, boolean disableVariableQuality) {
        StringBuilder srcset = new StringBuilder();
        Map<String, String> srcsetParams = new HashMap<String, String>(params);

        boolean has_quality = params.get("q") != null;

        for (int ratio: TARGET_RATIOS) {
            srcsetParams.put("dpr", Integer.toString(ratio));

            if (!disableVariableQuality && !has_quality) {
                srcsetParams.put("q", DPR_QUALITIES[ratio - 1].toString());
            }
            srcset.append(this.createURL(path, srcsetParams)).append(" ").append(ratio).append("x,\n");
        }
        return srcset.substring(0, srcset.length() - 2);
    }

    /**
     * Create an `ArrayList` of integer target widths.
     *
     * If `begin`, `end`, and `tol` are the default values, then the
     * targets are not custom, in which case the default widths are
     * returned.
     *
     * A target width list of length one is valid: if `begin` == `end`
     * then the list begins where it ends.
     *
     * When the `while` loop terminates, `begin` is greater than `end`
     * (where `end` less than or equal to `MAX_WIDTH`). This means that
     * the most recently appended value may, or may not, be the `end`
     * value.
     *
     * To be inclusive of the ending value, we check for this case and the
     * value is added if necessary.
     *
     * @param begin - beginning image width value
     * @param end - ending image width value
     * @param tol - tolerable amount of width value variation, from 0.01 to 1.0
     * @return array list of image width values
     */
    public static ArrayList<Integer> targetWidths(int begin, int end, double tol) {
        Validator.validateMinMaxTol(begin, end, tol);
        return computeTargetWidths(begin, end, tol);
    }

    public static ArrayList<Integer> targetWidths() {
        return new ArrayList<Integer>(Arrays.asList(SRCSET_TARGET_WIDTHS));
    }

    /**
     * Create an `ArrayList` of integer target widths.
     *
     * This function is the implementation details of `targetWidths`.
     * This function exists to provide a consistent interface for
     * callers of `targetWidths`.
     *
     * This function implements the syntax that fulfills the semantics
     * of `targetWidths`. Meaning, `begin`, `end`, and `tol` are
     * to be whole integers, but computation requires `double`s. This
     * function hides this detail from callers.
     */
    private static ArrayList<Integer> computeTargetWidths(double begin, double end, double tol) {
        if (notCustom(begin, end, tol)) {
            return new ArrayList<Integer>(Arrays.asList(SRCSET_TARGET_WIDTHS));
        }

        ArrayList<Integer> resolutions = new ArrayList<Integer>();
        if (begin == end) {
            // `begin` has not been mutated; cast back to `int`.
            resolutions.add((int) begin);
            return resolutions;
        }

        while (begin < end && begin < MAX_WIDTH) {
            // Round values so that the resulting `int` is truer
            // to expectations (i.e. 115.99999 --> 116).
            resolutions.add((int) Math.round(begin));
            begin *= 1 + tol * 2;
        }

        int lastIndex = resolutions.size() - 1;
        if (resolutions.get(lastIndex) < end) {
            // `end` has not been mutated; cast back to `int`.
            resolutions.add((int) end);
        }

        return resolutions;
    }

    private boolean isDpr(Map<String, String> params) {
        String width = params.get("w");
        boolean hasWidth = (width != null) && !width.isEmpty();

        String height = params.get("h");
        boolean hasHeight = (height != null) && !height.isEmpty();

        String aspectRatio = params.get("ar");
        boolean hasAspectRatio = (aspectRatio != null) && !aspectRatio.isEmpty();

        // If `params` have a width param or height parameters
        // then the srcset to be constructed with these params
        // _is dpr based_.
        return hasWidth || hasHeight;
    }

    private static boolean notCustom(double begin, double end, double tol) {
        boolean defaultBegin = (begin == MIN_WIDTH);
        boolean defaultEnd = (end == MAX_WIDTH);
        boolean defaultTol = (tol == SRCSET_WIDTH_TOLERANCE);

        // A list of target widths is _NOT_ custom if `begin`, `end`,
        // and `tol` are equal to their default values.
        return defaultBegin && defaultEnd && defaultTol;
    }
}
