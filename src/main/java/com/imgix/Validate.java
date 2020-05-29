package com.imgix;

public class Validate {
    /**
     * Validates the width range specified by `begin` and `end`.
     *
     * A width range is valid if `begin` is less than or equal to
     * `end` _and_ if `begin` is greater than zero.
     *
     * The maximum value for `end` is generally 8192. However, this
     * is only the general case. In the future, values larger than
     * 8192 will become valid.
     *
     * @param begin beginning or minimum width value
     * @param end ending or maximum width value
     * @return true if the width range is valid, otherwise false
     */
    public static boolean isValidWidthRange(double begin, double end) {
        return (begin <= end) && (begin > 0);
    }

    /**
     * Validate the width tolerance specified by `tol`.
     *
     * A width `tol`erance is valid if it is greater than or equal to
     * `0.01` (one percent).
     *
     * The upper limit of `tol` is not checked. Generally, this value
     * should range between [0.01, 1.0] or from one percent to one
     * hundred percent.
     *
     * In the case where a value larger than `1.0` is passed to
     * `targetWidths`, say `100000`, then one of two "smallest possible
     * srcsets will be generated (depending on `begin` and `end`)––either
     * `[begin, end]` or `[begin]` (when `begin == end`).
     *
     * @param tol width tolerance to be validated
     * @return true if `tol` is greater than `0.01`, otherwise false.
     */
    public static boolean isValidTolerance(double tol) {
        return tol >= 0.01;
    }

    /**
     * Validate `widths` array to ensure it is _not_ empty.
     *
     * @param widths array of integer width values
     * @return true if `widths` is non-empty, otherwise false
     */
    public static boolean isValidWidths(Integer[] widths) {
        return widths.length > 0;
    }
}
