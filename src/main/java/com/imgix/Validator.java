package com.imgix;

public class Validator {
    private static final double ONE_PERCENT = 0.01;

    /**
     * Validate `begin` width value is at least zero.
     *
     * @param begin Beginning width value of a width-range.
     * @throws RuntimeException If `begin` is less than zero.
     */
    public static void validateMinWidth(int begin) throws RuntimeException {
        if (begin < 0) {
            throw new RuntimeException("`begin` width value must be greater than zero");
        }
    }

    /**
     * Validate `end` width value is at least zero.
     *
     * @param end Ending width value of a width-range.
     * @throws RuntimeException If `end` is less than zero.
     */
    public static void validateMaxWidth(int end) throws RuntimeException {
        if (end < 0) {
            throw new RuntimeException("`end` width value must be greater than zero"); 
        }
    }

    /**
     * Validate `begin` and `end` represent a valid width-range.
     *
     * This validator is the composition of `validateMinWidth` and
     * `validateMaxWidth`. It also adds a final constraint that
     * `begin` be less than or equal to `end`.
     *
     * @param begin Beginning width value of a width-range.
     * @param end Ending width value of a width-range.
     * @throws RuntimeException If a width range `begin`s after it `end`s.
     */
    public static void validateRange(int begin, int end) throws RuntimeException {
        // Validate the minimum width, `begin`.
        validateMinWidth(begin);
        // Validate the maximum width, `end`.
        validateMaxWidth(end);

        // Ensure that the range is valid, ie. `begin <= end`.
        if (end < begin) {
            throw new RuntimeException("`begin` width value must be less than `end` width value"); 
        }
    }

    /**
     * Validate `tol`erance is at least `ONE_PERCENT`.
     *
     * @param tol Tolerable amount of image width variation.
     * @throws RuntimeException If `tol` is less than `ONE_PERCENT`.
     */
    public static void validateTolerance(double tol) throws RuntimeException {
        String msg = "`tol`erance value must be greater than, " +
            "or equal to one percent, ie. >= 0.01";

        if (tol < ONE_PERCENT) {
            throw new RuntimeException(msg);
        }
    }

    /**
     * Validate that `begin`, `end`, and `tol` represent a valid srcset range.
     *
     * @param begin Beginning width value of a width-range.
     * @param end Ending width value of a width-range.
     * @param tol Tolerable amount of image width variation.
     * @throws RuntimeException
     */
    public static void validateMinMaxTol(int begin, int end, double tol) throws RuntimeException {
        validateRange(begin, end);
        validateTolerance(tol);
    }

    /**
     * Validate `widths` array contains only positive values.
     *
     * @param widths Integer array of positive image width values.
     * @throws RuntimeException If `widths` contains a negative value.
     */
    public static void validateWidths(Integer[] widths) throws RuntimeException {
        if (widths == null) {
            throw new RuntimeException("`widths` array cannot be `null`");
        }

        if (widths.length == 0) {
            throw new RuntimeException("`widths` array cannot be empty");
        }

        for (Integer w: widths) {
            if (w < 0) {
                throw new RuntimeException("width values in `widths` cannot be negative");
            }
        }
    }
}