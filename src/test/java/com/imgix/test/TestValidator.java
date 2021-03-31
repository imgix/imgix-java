package com.imgix.test;

import com.imgix.Validator;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestValidator {
  private static final int LESS_THAN_ZERO = -1;
  /** Test `validateMinWidth` throws if passed a value less than zero. */
  @Test(expected = RuntimeException.class)
  public void testValidateMinWidth() {
    Validator.validateMinWidth(LESS_THAN_ZERO);
  }

  /** Test `validateMaxWidth` throws if passed a value less than zero. */
  @Test(expected = RuntimeException.class)
  public void testValidateMaxWidth() {
    Validator.validateMaxWidth(LESS_THAN_ZERO);
  }

  /** Test `validateRange` throws if passed an invalid range, ie. if `BEGIN > END`. */
  @Test(expected = RuntimeException.class)
  public void testValidateRange() {
    final int BEGIN = 100;
    final int END = 99;
    Validator.validateRange(BEGIN, END);
  }

  /** Test `validateTolerance` throws if passed a `tol`erance that is less than one percent. */
  @Test(expected = RuntimeException.class)
  public void testValidateTolerance() {
    final double LESS_THAN_ONE_PERCENT = 0.001;
    Validator.validateTolerance(LESS_THAN_ONE_PERCENT);
  }

  /** Test `validateWidths` throws if a negative width value has been found. */
  @Test(expected = RuntimeException.class)
  public void testValidateWidths() {
    Integer[] widths = new Integer[] {100, 200, 300, -400};
    Validator.validateWidths(widths);
  }

  /** Test `validateWidths` throws if passed a `null` array. */
  @Test(expected = RuntimeException.class)
  public void testValidateWidthsNullArray() {
    Validator.validateWidths(null);
  }

  /** Test `validateWidths` throws if passed an empty array. */
  @Test(expected = RuntimeException.class)
  public void testValidateWidthsEmptyArray() {
    Validator.validateWidths(new Integer[] {});
  }
}