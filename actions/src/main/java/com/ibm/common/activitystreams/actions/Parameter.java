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
package com.ibm.common.activitystreams.actions;

import java.io.ObjectStreamException;
import java.util.regex.Pattern;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.NLV;
import com.ibm.common.activitystreams.TypeValue;

/**
 */
public class Parameter 
  extends ASObject {
  
  /**
   */
  public static enum Format {
    OTHER(null),
    BOOLEAN("boolean"),
    INT32("int32"),
    INT64("int64"),
    UINT32("uint32"),
    UINT64("uint64"),
    DOUBLE("double"),
    FLOAT("float"),
    BYTE("byte"),
    DATE("date"),
    DATETIME("date-time"),
    DURATION("duration"),
    LANG("lang"),
    URI("uri"),
    IRI("iri")
    ;
    
    private final String label;
    
    /**
     * Constructor for Format.
     * @param label String
     */
    Format(String label) {
      this.label = label;
    }
    
    /**
     * Method select.
     * @param label String
     * @return Format
     */
    private static Format select(String label) {
      try {
        if (label == null)
          return Format.OTHER;
        label = label.toUpperCase().replaceAll("-", "");
        return valueOf(label);
      } catch (Throwable t) {
        return Format.OTHER;
      }
    }
  }
  
  /**
   * Method makeParameter.
   * @return Builder
   */
  public static Builder makeParameter() {
    return new Builder();
  }
  
  /**
   */
  public static final class Builder 
    extends AbstractBuilder<Parameter,Builder> {

    /**
     * Method get.
     * @return Parameter
     * @see com.google.common.base.Supplier#get()
     */
    public Parameter get() {
      return new Parameter(this);
    }
    
  }
  
  /**
   */
  public static abstract class AbstractBuilder<P extends Parameter, B extends AbstractBuilder<P,B>>
    extends ASObject.AbstractBuilder<P,B> {

    protected AbstractBuilder() {
      objectType("parameter");
    }
    
    /**
     * Method placeholder.
     * @param val String
     * @return B
     */
    public B placeholder(String val) {
      return this._nlv("placeholder", val);
    }

    /**
     * Method placeholder.
     * @param nlv NLV
     * @return B
     */
    public B placeholder(NLV nlv) {
      return this._nlv("placeholder", nlv);
    }
    
    /**
     * Method placeholder.
     * @param nlv Supplier<? extends NLV>
     * @return B
     */
    public B placeholder(Supplier<? extends NLV> nlv) {
      return this._nlv("placeholder", nlv);
    }
    
    /**
     * Method placeholder.
     * @param lang String
     * @param val String
     * @return B
     */
    public B placeholder(String lang, String val) {
      return this._nlv("placeholder", lang, val);
    }
    
    /**
     * Method type.
     * @param iri String
     * @return B
     */
    public B type(String iri) {
      return type(TypeValue.SimpleTypeValue.make(iri));
    }
    
    /**
     * Method type.
     * @param tv TypeValue
     * @return B
     */
    public B type(TypeValue tv) {
      return set("type", tv);
    }
    
    /**
     * Method type.
     * @param tv Supplier<? extends TypeValue>
     * @return B
     */
    public B type(Supplier<? extends TypeValue> tv) {
      return type(tv.get());
    }
    
    /**
     * Method required.
     * @param on boolean
     * @return B
     */
    public B required(boolean on) {
      return set("required", on);
    }
    
    /**
     * Method repeated.
     * @param on boolean
     * @return B
     */
    public B repeated(boolean on) {
      return set("repeated", on);
    }
    
    /**
     * Method required.
     * @return B
     */
    public B required() {
      return required(true);
    }
    
    /**
     * Method repeated.
     * @return B
     */
    public B repeated() {
      return repeated(true);
    }
    
    /**
     * Method value.
     * @param value Object
     * @return B
     */
    public B value(Object value) {
      return set("value", value);
    }
    
    /**
     * Method defaultValue.
     * @param value Object
     * @return B
     */
    public B defaultValue(Object value) {
      return set("default", value);
    }
    
    /**
     * Method format.
     * @param format Format
     * @return B
     */
    @SuppressWarnings("unchecked")
    public B format(Format format) {
      if (format == Format.OTHER)
        return (B)this;
      return format(format.label);
    }
    
    /**
     * Method formatInt32.
     * @return B
     */
    public B formatInt32() {
      return format(Format.INT32);
    }
    
    /**
     * Method formatInt64.
     * @return B
     */
    public B formatInt64() {
      return format(Format.INT64);
    }
    
    /**
     * Method formatUint32.
     * @return B
     */
    public B formatUint32() {
      return format(Format.UINT32);
    }
    
    /**
     * Method formatUint64.
     * @return B
     */
    public B formatUint64() {
      return format(Format.UINT64);
    }
    
    /**
     * Method formatDouble.
     * @return B
     */
    public B formatDouble() {
      return format(Format.DOUBLE);
    }
    
    /**
     * Method formatFloat.
     * @return B
     */
    public B formatFloat() {
      return format(Format.FLOAT);
    }
    
    /**
     * Method formatByte.
     * @return B
     */
    public B formatByte() {
      return format(Format.BYTE);
    }
    
    /**
     * Method formatDate.
     * @return B
     */
    public B formatDate() {
      return format(Format.DATE);
    }
    
    /**
     * Method formatDateTime.
     * @return B
     */
    public B formatDateTime() {
      return format(Format.DATETIME);
    }
    
    /**
     * Method formatDuration.
     * @return B
     */
    public B formatDuration() {
      return format(Format.DURATION);
    }
    
    /**
     * Method formatLang.
     * @return B
     */
    public B formatLang() {
      return format(Format.LANG);
    }
    
    /**
     * Method formatUri.
     * @return B
     */
    public B formatUri() {
      return format(Format.URI);
    }
    
    /**
     * Method formatIri.
     * @return B
     */
    public B formatIri() {
      return format(Format.IRI);
    }
    
    /**
     * Method format.
     * @param format String
     * @return B
     */
    public B format(String format) {
      return set("format", format);
    }
    
    /**
     * Method pattern.
     * @param pattern Pattern
     * @return B
     */
    public B pattern(Pattern pattern) {
      return pattern(pattern.pattern());
    }
    
    /**
     * Method pattern.
     * @param pattern String
     * @return B
     */
    public B pattern(String pattern) {
      return set("pattern", pattern);
    }
    
    /**
     * Method minimum.
     * @param min String
     * @return B
     */
    public B minimum(String min) {
      return set("minimum", min);
    }
    
    /**
     * Method maximum.
     * @param max String
     * @return B
     */
    public B maximum(String max) {
      return set("maximum", max);
    }
    
    /**
     * Method minimum.
     * @param min int
     * @return B
     */
    public B minimum(int min) {
      return set("minimum", min);
    }
    
    /**
     * Method maximum.
     * @param max int
     * @return B
     */
    public B maximum(int max) {
      return set("maximum", max);
    }
    
    /**
     * Method minimum.
     * @param min long
     * @return B
     */
    public B minimum(long min) {
      return set("minimum", min);
    }
    
    /**
     * Method maximum.
     * @param max long
     * @return B
     */
    public B maximum(long max) {
      return set("maximum", max);
    }
    
    /**
     * Method minimum.
     * @param min short
     * @return B
     */
    public B minimum(short min) {
      return set("minimum", min);
    }
    
    /**
     * Method maximum.
     * @param max short
     * @return B
     */
    public B maximum(short max) {
      return set("maximum", max);
    }
    
    /**
     * Method minimum.
     * @param min double
     * @return B
     */
    public B minimum(double min) {
      return set("minimum", min);
    }
    
    /**
     * Method maximum.
     * @param max double
     * @return B
     */
    public B maximum(double max) {
      return set("maximum", max);
    }
    
    /**
     * Method minimum.
     * @param min float
     * @return B
     */
    public B minimum(float min) {
      return set("minimum", min);
    }
    
    /**
     * Method maximum.
     * @param max float
     * @return B
     */
    public B maximum(float max) {
      return set("maximum", max);
    }
    
    /**
     * Method step.
     * @param step int
     * @return B
     */
    public B step(int step) {
      return set("step", step);
    }
    
    /**
     * Method step.
     * @param step long
     * @return B
     */
    public B step(long step) {
      return set("step", step);
    }
    
    /**
     * Method step.
     * @param step short
     * @return B
     */
    public B step(short step) {
      return set("step", step);
    }
    
    /**
     * Method step.
     * @param step double
     * @return B
     */
    public B step(double step) {
      return set("step", step);
    }
    
    /**
     * Method step.
     * @param step float
     * @return B
     */
    public B step(float step) {
      return set("step", step);
    }
    
    /**
     * Method enumVals.
     * @param vals Object[]
     * @return B
     */
    public B enumVals(Object... vals) {
      return set("enum", ImmutableList.copyOf(vals));
    }

  }
  
  /**
   * Constructor for Parameter.
   * @param builder Builder
   */
  protected Parameter(Builder builder) {
    super(builder);
  }
  
  /**
   * Method required.
   * @return boolean
   */
  public boolean required() {
    return getBoolean("required");
  }
  
  /**
   * Method repeated.
   * @return boolean
   */
  public boolean repeated() {
    return getBoolean("repeated");
  }
  
  /**
   * Method value.
   * @return O
   */
  public <O>O value() {
    return this.<O>get("value");
  }
  
  /**
   * Method value.
   * @param defaultValue O
   * @return O
   */
  public <O>O value(O defaultValue) {
    return this.<O>get("value", defaultValue);
  }
  
  /**
   * Method defaultValue.
   * @return O
   */
  public <O>O defaultValue() {
    return this.<O>get("default");
  }
  
  /**
   * Method defaultValue.
   * @param defaultValue O
   * @return O
   */
  public <O>O defaultValue(O defaultValue) {
    return this.<O>get("default", defaultValue);
  }
  
  /**
   * Method formatString.
   * @return String
   */
  public String formatString() {
    return getString("format");
  }
  
  /**
   * Method format.
   * @return Format
   */
  public Format format() {
    return Format.select(getString("format"));
  }
  
  /**
   * Method pattern.
   * @return String
   */
  public String pattern() {
    return getString("pattern");
  }
  
  /**
   * Method maximum.
   * @return String
   */
  public String maximum() {
    return getString("maximum");
  }
  
  /**
   * Method minimum.
   * @return String
   */
  public String minimum() {
    return getString("minimum");
  }
  
  /**
   * Method maximumInt.
   * @return int
   */
  public int maximumInt() {
    return getInt("maximum");
  }
  
  /**
   * Method minimumInt.
   * @return int
   */
  public int minimumInt() {
    return getInt("minimum");
  }
  
  /**
   * Method maximumLong.
   * @return long
   */
  public long maximumLong() {
    return getLong("maximum");
  }
  
  /**
   * Method minimumLong.
   * @return long
   */
  public long minimumLong() {
    return getLong("minimum");
  }
  
  /**
   * Method maximumShort.
   * @return short
   */
  public short maximumShort() {
    return getShort("maximum");
  }
  
  /**
   * Method minimumShort.
   * @return short
   */
  public short minimumShort() {
    return getShort("minimum");
  }
  
  /**
   * Method maximumDouble.
   * @return double
   */
  public double maximumDouble() {
    return getDouble("maximum");
  }
  
  /**
   * Method minimumDouble.
   * @return double
   */
  public double minimumDouble() {
    return getDouble("minimum");
  }
  
  /**
   * Method maximumFloat.
   * @return float
   */
  public float maximumFloat() {
    return getFloat("maximum");
  }
  
  /**
   * Method minimumFloat.
   * @return float
   */
  public float minimumFloat() {
    return getFloat("minimum");
  }
  
  /**
   * Method maximum.
   * @param defaultValue String
   * @return String
   */
  public String maximum(String defaultValue) {
    return getString("maximum", defaultValue);
  }
  
  /**
   * Method minimum.
   * @param defaultValue String
   * @return String
   */
  public String minimum(String defaultValue) {
    return getString("minimum", defaultValue);
  }
  
  /**
   * Method maximumInt.
   * @param defaultValue int
   * @return int
   */
  public int maximumInt(int defaultValue) {
    return getInt("maximum", defaultValue);
  }
  
  /**
   * Method minimumInt.
   * @param defaultValue int
   * @return int
   */
  public int minimumInt(int defaultValue) {
    return getInt("minimum", defaultValue);
  }
  
  /**
   * Method maximumLong.
   * @param defaultValue long
   * @return long
   */
  public long maximumLong(long defaultValue) {
    return getLong("maximum", defaultValue);
  }
  
  /**
   * Method minimumLong.
   * @param defaultValue long
   * @return long
   */
  public long minimumLong(long defaultValue) {
    return getLong("minimum", defaultValue);
  }
  
  /**
   * Method maximumShort.
   * @param defaultValue short
   * @return short
   */
  public short maximumShort(short defaultValue) {
    return getShort("maximum", defaultValue);
  }
  
  /**
   * Method minimumShort.
   * @param defaultValue short
   * @return short
   */
  public short minimumShort(short defaultValue) {
    return getShort("minimum", defaultValue);
  }
  
  /**
   * Method maximumDouble.
   * @param defaultValue double
   * @return double
   */
  public double maximumDouble(double defaultValue) {
    return getDouble("maximum", defaultValue);
  }
  
  /**
   * Method minimumDouble.
   * @param defaultValue double
   * @return double
   */
  public double minimumDouble(double defaultValue) {
    return getDouble("minimum", defaultValue);
  }
  
  /**
   * Method maximumFloat.
   * @param defaultValue float
   * @return float
   */
  public float maximumFloat(float defaultValue) {
    return getFloat("maximum", defaultValue);
  }
  
  /**
   * Method minimumFloat.
   * @param defaultValue float
   * @return float
   */
  public float minimumFloat(float defaultValue) {
    return getFloat("minimum", defaultValue);
  }
  
  /**
   * Method stepInt.
   * @return int
   */
  public int stepInt() {
    return getInt("step");
  }
  
  /**
   * Method stepInt.
   * @param defaultValue int
   * @return int
   */
  public int stepInt(int defaultValue) {
    return getInt("step", defaultValue);
  }
  
  /**
   * Method stepLong.
   * @return long
   */
  public long stepLong() {
    return getLong("step");
  }
  
  /**
   * Method getLong.
   * @param defaultValue long
   * @return long
   */
  public long getLong(long defaultValue) {
    return getLong("step", defaultValue);
  }
  
  /**
   * Method stepShort.
   * @return short
   */
  public short stepShort() {
    return getShort("step");
  }
  
  /**
   * Method stepShort.
   * @param defaultValue short
   * @return short
   */
  public short stepShort(short defaultValue) {
    return getShort("step", defaultValue);
  }
  
  /**
   * Method stepDouble.
   * @return double
   */
  public double stepDouble() {
    return getDouble("step");
  }
  
  /**
   * Method stepDouble.
   * @param defaultValue double
   * @return double
   */
  public double stepDouble(double defaultValue) {
    return getDouble("step", defaultValue);
  }
  
  /**
   * Method stepFloat.
   * @return float
   */
  public float stepFloat() {
    return getFloat("step");
  }
  
  /**
   * Method stepFloat.
   * @param defaultValue float
   * @return float
   */
  public float stepFloat(float defaultValue) {
    return getFloat("step", defaultValue);
  }
  
  /**
   * Method enumVals.
   * @return Iterable<Object>
   */
  public Iterable<Object> enumVals() {
    return this.<Iterable<Object>>get("enum");
  }
  
  /**
   * Method type.
   * @return T
   */
  public <T extends TypeValue>T type() {
    return this.<T>get("type");
  }
  
  /**
   * Method placeholder.
   * @return NLV
   */
  public NLV placeholder() {
    return this.<NLV>get("placeholder");
  }
  
  /**
   * Method placeholderString.
   * @return String
   */
  public String placeholderString() {
    return _nlv("placeholder");
  }
  
  /**
   * Method placeholderString.
   * @param lang String
   * @return String
   */
  public String placeholderString(String lang) {
    return _nlv("placeholder", lang);
  }
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<Parameter> {
    private static final long serialVersionUID = -2060301713159936285L;
    protected SerializedForm(Parameter obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return super.doReadResolve();
    }
    protected Parameter.Builder builder() {
      return ActionMakers.parameter();
    }
  }
}
