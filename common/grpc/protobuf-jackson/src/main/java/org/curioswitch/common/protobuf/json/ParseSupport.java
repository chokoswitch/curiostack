/*
 * MIT License
 *
 * Copyright (c) 2019 Choko (choko@curioswitch.org)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.curioswitch.common.protobuf.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.exc.InputCoercionException;
import com.fasterxml.jackson.core.io.NumberInput;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Internal.EnumLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.NullValue;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Base64;

/**
 * Static methods for parsing various protobuf types. Parsing code, including generated bytecode,
 * calls these methods to actually read and normalize data from input. Many of these methods are
 * only called from generated bytecode, but it is still worth extracting into static methods to
 * minimize the amount of generated bytecode managed and optimized by the VM and reduce the
 * complexity of code generation.
 *
 * <p>All values may be quoted, and integer values may be represented by the equivalent floating
 * point value. Numeric values are all checked to be in their valid range or throw an {@link
 * InvalidProtocolBufferException}.
 */
public final class ParseSupport {

  // Visible for testing
  static final int RECURSION_LIMIT = 100;

  /**
   * Checks the current token is '{' and advances past it. For parsing the beginning of JSON objects
   * ({@link Message}s and {@link java.util.Map}s).
   */
  public static void parseObjectStart(JsonParser parser) throws IOException {
    JsonToken json = parser.currentToken();
    if (json != JsonToken.START_OBJECT) {
      throw new InvalidProtocolBufferException("Expected an object but found: " + json);
    }
    parser.nextToken();
  }

  /**
   * Returns whether the {@link JsonToken} is '}'. This is the terminating condition when iterating
   * over fields in a JSON object. Does not advance the token as if it's not the end, it means it's
   * a field and needs to be parsed, not skipped.
   */
  public static boolean checkObjectEnd(JsonToken token) {
    return token == JsonToken.END_OBJECT;
  }

  /** Checks whether the current token is '[' and advances past it. */
  public static void parseArrayStart(JsonParser parser) throws IOException {
    JsonToken json = parser.currentToken();
    if (json != JsonToken.START_ARRAY) {
      throw new InvalidProtocolBufferException("Expected an array but found: " + json);
    }
    parser.nextToken();
  }

  /**
   * Returns whether the current token is ']'. This is the terminating condition when iterating over
   * elements in a JSON array. Does not advance the token as if it's not the end, it means it's an
   * element and needs to be parsed, not skipped.
   */
  public static boolean checkArrayEnd(JsonParser parser) {
    JsonToken json = parser.currentToken();
    return json == JsonToken.END_ARRAY;
  }

  /**
   * Returns whether the current token is 'null'. This is used to skip over fields that are set to
   * null, which we treat as defaults.
   */
  public static boolean checkNull(JsonParser parser) {
    return parser.currentToken() == JsonToken.VALUE_NULL;
  }

  /**
   * Throws an exception if the current token is 'null'. This is used to prevent repeated elements
   * from being set to 'null', which is not allowed.
   */
  public static void throwIfRepeatedValueNull(JsonParser parser)
      throws InvalidProtocolBufferException {
    if (parser.currentToken() == JsonToken.VALUE_NULL) {
      throw new InvalidProtocolBufferException("Repeated field elements cannot be null");
    }
  }

  /** Parsers an int32 value out of the input. */
  public static int parseInt32(JsonParser parser) throws IOException {
    JsonToken token = parser.currentToken();
    if (token == JsonToken.VALUE_NUMBER_INT) {
      // Use optimized code path for integral primitives, the normal case.
      return parser.getIntValue();
    }
    // JSON doesn't distinguish between integer values and floating point values so "1" and
    // "1.000" are treated as equal in JSON. For this reason we accept floating point values for
    // integer fields as well as long as it actually is an integer (i.e., round(value) == value).
    try {
      BigDecimal value =
          new BigDecimal(
              parser.getTextCharacters(), parser.getTextOffset(), parser.getTextLength());
      return value.intValueExact();
    } catch (Exception e) {
      throw new InvalidProtocolBufferException("Not an int32 value: " + parser.getText());
    }
  }

  /** Parsers an int64 value out of the input. */
  public static long parseInt64(JsonParser parser) throws IOException {
    try {
      return parseLong(parser);
    } catch (JsonParseException | NumberFormatException e) {
      // fall through
    }

    // JSON doesn't distinguish between integer values and floating point values so "1" and
    // "1.000" are treated as equal in JSON. For this reason we accept floating point values for
    // integer fields as well as long as it actually is an integer (i.e., round(value) == value).
    try {
      BigDecimal value =
          new BigDecimal(
              parser.getTextCharacters(), parser.getTextOffset(), parser.getTextLength());
      return value.longValueExact();
    } catch (ArithmeticException e) {
      throw new InvalidProtocolBufferException("Not an int64 value: " + parser.getText());
    }
  }

  private static final BigInteger MAX_UINT32 = new BigInteger("FFFFFFFF", 16);

  /** Parsers a uint32 value out of the input. */
  public static int parseUInt32(JsonParser parser) throws IOException {
    try {
      long result = parseLong(parser);
      if (result < 0 || result > 0xFFFFFFFFL) {
        throw new InvalidProtocolBufferException("Out of range uint32 value: " + parser.getText());
      }
      return (int) result;
    } catch (JsonParseException | NumberFormatException e) {
      // fall through
    }

    // JSON doesn't distinguish between integer values and floating point values so "1" and
    // "1.000" are treated as equal in JSON. For this reason we accept floating point values for
    // integer fields as well as long as it actually is an integer (i.e., round(value) == value).
    try {
      BigDecimal decimalValue =
          new BigDecimal(
              parser.getTextCharacters(), parser.getTextOffset(), parser.getTextLength());
      BigInteger value = decimalValue.toBigIntegerExact();
      if (value.signum() < 0 || value.compareTo(MAX_UINT32) > 0) {
        throw new InvalidProtocolBufferException("Out of range uint32 value: " + parser.getText());
      }
      return value.intValue();
    } catch (ArithmeticException | NumberFormatException e) {
      throw new InvalidProtocolBufferException("Not an uint32 value: " + parser.getText());
    }
  }

  private static final BigInteger MAX_UINT64 = new BigInteger("FFFFFFFFFFFFFFFF", 16);

  /** Parsers a uint64 value out of the input. */
  public static long parseUInt64(JsonParser parser) throws IOException {
    // Try to optimistically handle non-huge unsigned longs through fast code path. This should
    // cover the vast majority of cases.
    try {
      long result = parseLong(parser);
      if (result >= 0) {
        // Only need to check the uint32 range if the parsed long is negative.
        return result;
      }
    } catch (JsonParseException | InputCoercionException | NumberFormatException e) {
      // Fall through.
    }

    final BigInteger value;
    try {
      BigDecimal decimal =
          new BigDecimal(
              parser.getTextCharacters(), parser.getTextOffset(), parser.getTextLength());
      value = decimal.toBigIntegerExact();
    } catch (ArithmeticException | NumberFormatException e) {
      throw new InvalidProtocolBufferException("Not an uint64 value: " + parser.getText());
    }

    if (value.compareTo(BigInteger.ZERO) < 0 || value.compareTo(MAX_UINT64) > 0) {
      throw new InvalidProtocolBufferException("Out of range uint64 value: " + parser.getText());
    }
    return value.longValue();
  }

  /** Parsers a bool value out of the input. */
  public static boolean parseBool(JsonParser parser) throws IOException {
    JsonToken token = parser.currentToken();
    if (token.isBoolean()) {
      return parser.getBooleanValue();
    }
    String json = parser.getText();
    if (json.equals("true")) {
      return true;
    } else if (json.equals("false")) {
      return false;
    }
    throw new InvalidProtocolBufferException("Invalid bool value: " + json);
  }

  private static final double EPSILON = 1e-6;

  /** Parsers a float value out of the input. */
  public static float parseFloat(JsonParser parser) throws IOException {
    JsonToken current = parser.currentToken();
    if (!current.isNumeric()) {
      String json = parser.getText();
      if (json.equals("NaN")) {
        return Float.NaN;
      } else if (json.equals("Infinity")) {
        return Float.POSITIVE_INFINITY;
      } else if (json.equals("-Infinity")) {
        return Float.NEGATIVE_INFINITY;
      }
    }
    String json = parser.getText();
    try {
      // We don't use Float.parseFloat() here because that function simply
      // accepts all double values. Here we readValue the value into a Double
      // and do explicit range check on it.
      double value = Double.parseDouble(json);
      // When a float value is printed, the printed value might be a little
      // larger or smaller due to precision loss. Here we need to add a bit
      // of tolerance when checking whether the float value is in range.
      if (value > Float.MAX_VALUE * (1.0 + EPSILON) || value < -Float.MAX_VALUE * (1.0 + EPSILON)) {
        throw new InvalidProtocolBufferException("Out of range float value: " + json);
      }
      return (float) value;
    } catch (NumberFormatException e) {
      throw new InvalidProtocolBufferException("Not a float value: " + json);
    }
  }

  private static final BigDecimal MORE_THAN_ONE = new BigDecimal(String.valueOf(1.0 + EPSILON));
  // When a float value is printed, the printed value might be a little
  // larger or smaller due to precision loss. Here we need to add a bit
  // of tolerance when checking whether the float value is in range.
  private static final BigDecimal MAX_DOUBLE =
      new BigDecimal(String.valueOf(Double.MAX_VALUE)).multiply(MORE_THAN_ONE);
  private static final BigDecimal MIN_DOUBLE =
      new BigDecimal(String.valueOf(-Double.MAX_VALUE)).multiply(MORE_THAN_ONE);

  /** Parsers a double value out of the input. */
  public static double parseDouble(JsonParser parser) throws IOException {
    JsonToken current = parser.currentToken();
    if (!current.isNumeric()) {
      String json = parser.getText();
      if (json.equals("NaN")) {
        return Double.NaN;
      } else if (json.equals("Infinity")) {
        return Double.POSITIVE_INFINITY;
      } else if (json.equals("-Infinity")) {
        return Double.NEGATIVE_INFINITY;
      }
    }
    try {
      // We don't use Double.parseDouble() here because that function simply
      // accepts all values. Here we readValue the value into a BigDecimal and do
      // explicit range check on it.
      BigDecimal value =
          new BigDecimal(
              parser.getTextCharacters(), parser.getTextOffset(), parser.getTextLength());
      if (value.compareTo(MAX_DOUBLE) > 0 || value.compareTo(MIN_DOUBLE) < 0) {
        throw new InvalidProtocolBufferException("Out of range double value: " + parser.getText());
      }
      return value.doubleValue();
    } catch (NumberFormatException e) {
      throw new InvalidProtocolBufferException("Not an double value: " + parser.getText());
    }
  }

  /** Parsers a string value out of the input. */
  public static String parseString(JsonParser parser) throws IOException {
    JsonToken json = parser.currentToken();
    String result = null;
    try {
      result = parser.getValueAsString();
    } catch (IOException e) {
      // Fall through
    }
    if (result == null) {
      throw new InvalidProtocolBufferException("Not a string value: " + json);
    }
    return result;
  }

  /** Parsers a bytes value out of the input. */
  public static ByteString parseBytes(JsonParser parser) throws IOException {
    JsonToken json = parser.currentToken();
    byte[] result = null;
    try {
      // Use JDK to decode base64, which can handle more variants than Jackson.
      result = Base64.getDecoder().decode(parser.getText());
    } catch (IllegalArgumentException e) {
      // Fall through
    }
    if (result == null) {
      throw new InvalidProtocolBufferException("Not a bytes value: " + json);
    }
    return ByteString.copyFrom(result);
  }

  /** Parsers an enum value out of the input. Supports both numeric and string representations. */
  public static int parseEnum(
      JsonParser parser, EnumDescriptor descriptor, boolean ignoringUnknownFields)
      throws IOException {
    JsonToken json = parser.currentToken();
    if (json == JsonToken.VALUE_NULL) {
      if (descriptor == NullValue.getDescriptor()) {
        return NullValue.NULL_VALUE_VALUE;
      } else {
        // A null value in a map with enum value type, we always ignore such nulls.
        return -1;
      }
    }
    if (json.isNumeric()) {
      try {
        return parser.getIntValue();
      } catch (IOException e) {
        // Fall through.
      }
    } else {
      try {
        String value = parser.getValueAsString();
        EnumValueDescriptor enumValue = descriptor.findValueByName(value);
        if (enumValue != null) {
          return enumValue.getNumber();
        }
      } catch (IOException e) {
        // Fall through.
      }
    }
    if (!ignoringUnknownFields) {
      throw new InvalidProtocolBufferException(
          "Invalid enum value: " + json + " for enum type: " + descriptor.getFullName());
    }
    return -1;
  }

  /** Returns the default value for an enum if it was read as an unknown, for singular fields. */
  public static int mapUnknownEnumValue(int value) {
    return value == -1 ? 0 : value;
  }

  /** Parsers a {@link Message} value out of the input. */
  public static <T extends Message> T parseMessage(
      JsonParser parser, TypeSpecificMarshaller<T> marshaller, int currentDepth)
      throws IOException {
    return marshaller.readValue(parser, currentDepth + 1);
  }

  /**
   * Checks the field presence of the field with number {@code fieldNumber} and variableName {@code
   * fullName}. If the field has already been set, an {@link InvalidProtocolBufferException} is
   * thrown.
   */
  public static int throwIfFieldAlreadyWritten(int setFieldsBits, int fieldBitMask, String fullName)
      throws InvalidProtocolBufferException {
    if ((setFieldsBits & fieldBitMask) != 0) {
      throw new InvalidProtocolBufferException("Field " + fullName + " has already been set.");
    }
    return setFieldsBits | fieldBitMask;
  }

  /**
   * Checks whether the oneof whose {@code oneofCase} has already been set. If so, an {@link
   * InvalidProtocolBufferException} is thrown.
   */
  public static void throwIfOneofAlreadyWritten(
      JsonParser parser, Object oneofCase, String fieldName, boolean ignoreNull)
      throws InvalidProtocolBufferException {
    if (ignoreNull && parser.currentToken() == JsonToken.VALUE_NULL) {
      // If the value is null, we skip it and don't need to throw any error..
      return;
    }
    if (((EnumLite) oneofCase).getNumber() != 0) {
      // TODO: Add the actual variableName of the offending field to the error message like
      // upstream, not
      // too hard but just a little boring for the expected return.
      throw new InvalidProtocolBufferException(
          "Cannot set field "
              + fieldName
              + " because another field "
              + oneofCase
              + " belonging to the same oneof has already been set.");
    }
  }

  /**
   * Throws an {@link InvalidProtocolBufferException} indicating the field with variableName {@code
   * fieldName} is not part of {@link Message} with variableName {@code messageName}. Called from
   * code after determining a field is unknown.
   */
  public static void throwIfUnknownField(String fieldName, String messageName)
      throws InvalidProtocolBufferException {
    throw new InvalidProtocolBufferException(
        "Cannot find field: " + fieldName + " in message " + messageName);
  }

  /**
   * Checks whether the {@code currentDepth} of nested message parsing is higher than the limit, and
   * throws {@link InvalidProtocolBufferException} if so. This is used to prevent stack exhaustion
   * attacks with extremely nested recursive messages.
   */
  public static void checkRecursionLimit(int currentDepth) throws InvalidProtocolBufferException {
    if (currentDepth >= RECURSION_LIMIT) {
      throw new InvalidProtocolBufferException("Hit recursion limit.");
    }
  }

  /** Parses a long out of the input, using the optimized path when the value is not quoted. */
  private static long parseLong(JsonParser parser) throws IOException {
    if (parser.currentToken() == JsonToken.VALUE_NUMBER_INT) {
      return parser.getLongValue();
    }
    return NumberInput.parseLong(parser.getText());
  }

  private ParseSupport() {}
}
