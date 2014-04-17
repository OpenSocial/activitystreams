/**
 * Copyright 2013 OpenSocial Foundation
 * Copyright 2013 International Business Machines Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Utility library for working with Activity Streams Actions
 * Requires underscorejs.
 *
 * @author James M Snell (jasnell@us.ibm.com)
 */
package com.ibm.common.activitystreams.internal;

import java.io.ObjectStreamException;

import com.google.common.primitives.Longs;
import com.google.gson.internal.LazilyParsedNumber;

/**
 */
final class LazilyParsedNumberComparable 
  extends Number
  implements Comparable<Number> {
  private static final long serialVersionUID = 761729254455440231L;
  private final LazilyParsedNumber inner;
  /**
   * Constructor for LazilyParsedNumberComparable.
   * @param inner LazilyParsedNumber
   */
  public LazilyParsedNumberComparable(LazilyParsedNumber inner) {
    this.inner = inner;
  }
  /**
   * Method compareTo.
   * @param o Number
   * @return int
   */
  public int compareTo(Number o) {
    return Longs.compare(inner.longValue(), o.longValue());
  }

  /**
   * Method doubleValue.
   * @return double
   */
  public double doubleValue() {
    return inner.doubleValue();
  }

  /**
   * Method floatValue.
   * @return float
   */
  public float floatValue() {
    return inner.floatValue();
  }

  /**
   * Method intValue.
   * @return int
   */
  public int intValue() {
    return inner.intValue();
  }

  /**
   * Method longValue.
   * @return long
   */
  public long longValue() {
    return inner.longValue();
  }
  /**
   * Method byteValue.
   * @return byte
   */
  public byte byteValue() {
    return inner.byteValue();
  }
  /**
   * Method shortValue.
   * @return short
   */
  public short shortValue() {
    return inner.shortValue();
  }
  /**
   * Method equals.
   * @param obj Object
   * @return boolean
   */
  public boolean equals(Object obj) {
    return inner.equals(obj);
  }
  /**
   * Method hashCode.
   * @return int
   */
  public int hashCode() {
    return inner.hashCode();
  }
  /**
   * Method toString.
   * @return String
   */
  public String toString() {
    return inner.toString();
  }
  
  /**
   * Method writeReplace.
   * @return Object
   * @throws ObjectStreamException
   */
  private Object writeReplace() throws ObjectStreamException {
    return inner;
  }
}