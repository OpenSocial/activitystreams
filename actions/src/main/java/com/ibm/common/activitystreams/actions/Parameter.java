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

import static com.ibm.common.activitystreams.Makers.nlv;
import static com.ibm.common.activitystreams.util.Util.DEFAULT_LOCALE;

import java.io.Serializable;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.base.Objects;
import com.google.common.base.Supplier;
import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;
import com.ibm.common.activitystreams.Makers;
import com.ibm.common.activitystreams.NLV;
import com.ibm.common.activitystreams.ValueType;
import com.ibm.common.activitystreams.NLV.MapNLV;
import com.ibm.common.activitystreams.NLV.SimpleNLV;
import com.ibm.common.activitystreams.util.AbstractWritable;

@SuppressWarnings("unchecked")
public class Parameter 
  extends AbstractWritable
  implements ParameterValue {
    
  /**
   * Method makeParameter.
   * @return Builder
   */
  public static Builder makeParameter() {
    return new Builder();
  }

  public static class Builder
    extends AbstractWritable.AbstractWritableBuilder<Parameter,Builder> {

    protected Builder() {
      writeUsing(ActionMakers.io);
    }
    
    private final Map<String,Object> map = 
      Maps.newHashMap();
    
    public Builder language(String lang) {
      if (lang != null)
        map.put("language", lang);
      else 
        map.remove("language");
      return this;
    }
    
    protected Builder _nlv(String key, String value) {
      if (value != null)
         map.put(key, nlv(value));
      else 
         map.remove(key);
       return this;
    }
  
    protected Builder _nlv(String key, NLV nlv) {
      if (nlv != null)  
        map.put(key, nlv);
      else 
        map.remove(key);
      return this;
    }
  
    protected Builder _nlv(String key, Supplier<? extends NLV> nlv) {
      return _nlv(key,nlv.get());
    }
  
    protected Builder _nlv(String key, Map<String,String> map) {
      if (map != null)
        for (Map.Entry<String,String> entry : map.entrySet())
          _nlv(key,entry.getKey(),entry.getValue());
      else 
        this.map.remove(key);
      return this;
    }
    
    protected Builder _nlv(String key, String lang, String value) {
      if (map.containsKey(key)) {
        Object obj = map.get(key);
        if (obj instanceof NLV) {
          NLV nlv = (NLV) obj;
          switch(nlv.valueType()) {
          case SIMPLE:
            String l = (String) map.get("language");
            if (l == null)
              l = DEFAULT_LOCALE;
            NLV.MapNLV.Builder b = 
              Makers.nlv();
            if (lang.equals(l))
              b.set(lang, value);
            else
              b.set(l, ((NLV.SimpleNLV)obj).value())
               .set(lang, value);
            map.put(key, b);
            return this;
          case OBJECT: 
            map.put(key, 
              Makers.nlv()
                .from((NLV.MapNLV)obj, lang)
                .set(lang, value));
            return this;
          default:
            throw new IllegalArgumentException();
          }
        } else if (obj instanceof NLV.MapNLV.Builder) {
          ((NLV.MapNLV.Builder) obj).set(lang, value);
          return this;
        }
      }
      map.put(key, Makers.nlv().set(lang,value));      
      return this;
    }
    
    public Builder displayName(String val) {
      return _nlv("displayName",val);
    }
    
    public Builder displayName(NLV nlv) {
      return _nlv("displayName", nlv);
    }
    
    public Builder displayName(Supplier<? extends NLV> nlv) {
      return _nlv("displayName", nlv);
    }
    
    public Builder displayName(String lang, String val) {
      return _nlv("displayName", lang, val);
    }
    
    public Builder placeholder(String placeholder) {
      return _nlv("placeholder", placeholder);
    }
    
    public Builder placeholder(NLV placeholder) {
      return _nlv("placeholder", placeholder);
    }

    public Builder placeholder(Supplier<? extends NLV> nlv) {
      return _nlv("placeholder", nlv);
    }
    
    public Builder placeholder(String lang, String val) {
      return _nlv("placeholder", lang, val);
    }
    
    public Builder type(String type) {
      if (type != null)
        map.put("type", type);
      else 
        map.remove("type");
      return this;
    }
    
    public Builder required() {
      map.remove("required");
      return this;
    }
    
    public Builder optional() {
      map.put("required", false);
      return this;
    }

    public Builder repeated() {
      map.put("repeated", true);
      return this;
    }
    
    public Builder notRepeated() {
      map.remove("repeated");
      return this;
    }
    
    public Builder value(Object value) {
      if (value != null)
        map.put("value", value);
      else 
        map.remove("value");
      return this;
    }
    
    public Builder defaultValue(Object value) {
      if (value != null)
        map.put("default", value);
      else 
        map.remove("default");
      return this;
    }
        
    public Builder pattern(Pattern... pattern) {
      if (pattern != null && pattern.length > 0) {
        ImmutableSet.Builder<String> patterns = 
          ImmutableSet.builder();
        for (Pattern p : pattern)
          patterns.add(p.pattern());
        map.put("pattern",patterns.build());
      } else {
        map.remove("pattern");
      }
      return this;
    }

    public Builder pattern(String... pattern) {
      if (pattern != null && pattern.length > 0) {
        ImmutableSet<String> patterns = 
          ImmutableSet.copyOf(pattern);
        map.put("pattern",patterns);
      } else {
        map.remove("pattern");
      }
      return this;
    }
    
    public Builder pattern(Iterable<String> patterns) {
      if (patterns != null && Iterables.size(patterns) > 0) {
        map.put("pattern", ImmutableSet.copyOf(patterns));
      } else 
        map.remove("pattern");
      return this;
    }
    
    public Builder minInclusive(Object min) {
      if (min != null)
        map.put("minInclusive", min);
      else 
        map.remove("minInclusive");
      return this;
    }
    
    public Builder minExclusive(Object min) {
      if (min != null)
        map.put("minExclusive", min);
      else 
        map.remove("minExclusive");
      return this;
    }
    
    public Builder maxInclusive(Object max) {
      if (max != null)
        map.put("maxInclusive", max);
      else 
        map.remove("maxInclusive");
      return this;
    }
    
    public Builder maxExclusive(Object max) {
      if (max != null)
        map.put("maxExclusive", max);
      else
        map.remove("maxExclusive");
      return this;
    }
    
    public Builder bound(Range<?> range) {
      if (range != null) {
        if (range.hasLowerBound()) {
          switch(range.lowerBoundType()) {
          case CLOSED:
            minInclusive(range.lowerEndpoint());
            break;
          case OPEN:
            minExclusive(range.lowerEndpoint());
            break;
          default:
            break;
          }
        } else {
          minInclusive(null);
          minExclusive(null);
        }
        if (range.hasUpperBound()) {
          switch(range.upperBoundType()) {
          case CLOSED:
            maxInclusive(range.upperEndpoint());
            break;
          case OPEN:
            maxExclusive(range.upperEndpoint());
            break;
          default:
            break;
          }
        } else {
          maxInclusive(null);
          maxExclusive(null);
        }
      }
      return this;
    }
        
    public Builder step(Number step) {
      if (step != null)
        map.put("step", step);
      else
        map.remove("step");
      return this;
    }

    public Builder enumeration(Object... vals) {
      if (vals != null && vals.length > 0)
        map.put("enumeration", ImmutableList.copyOf(vals));
      else 
        map.remove("enumeration");
      return this;
    }
    
    public Builder maxLength(int length) {
      if (length > -1)
        map.put("maxLength", length);
      else
        map.remove("maxLength");
      return this;
    }
    
    public Builder minLength(int length) {
      if (length > -1)
        map.put("minLength", length);
      else
        map.remove("minLength");
      return this;
    }
    
    public Builder totalDigits(int num) {
      if (num > -1)
        map.put("totalDigits", num);
      else 
        map.remove("totalDigits");
      return this;
    }
    
    public Builder fractionDigits(int num) {
      if (num > -1) 
        map.put("fractionDigits", num);
      else 
        map.remove("fractionDigits");
      return this;
    }
    
    public Parameter get() {
      return new Parameter(this);
    }

  }
  
  private final ImmutableMap<String,Object> map;
  
  /**
   * Constructor for Parameter.
   * @param builder Builder
   */
  protected Parameter(Builder builder) {
    super(builder);
    this.map = ImmutableMap.copyOf(builder.map);
  }
  
  /**
   * Method required.
   * @return boolean
   */
  public boolean required() {
    return !has("required") ? 
      true : (Boolean)map.get("required");
  }
  
  /**
   * Method repeated.
   * @return boolean
   */
  public boolean repeated() {
    return !has("repeated") ?
      false : (Boolean)map.get("repeated");
  }
  
  /**
   * Method value.
   * @return O
   */
  public <O>O value() {
    return (O)map.get("value");
  }
  
  /**
   * Method value.
   * @param defaultValue O
   * @return O
   */
  public <O>O value(O defaultValue) {
    O val = value();
    return val != null ? val : defaultValue;
  }
  
  /**
   * Method defaultValue.
   * @return O
   */
  public <O>O defaultValue() {
    return (O)map.get("default");
  }
  
  /**
   * Method defaultValue.
   * @param defaultValue O
   * @return O
   */
  public <O>O defaultValue(O defaultValue) {
    O val = defaultValue();
    return val != null ? val : defaultValue;
  }
  
  public String type() {
    return (String)map.get("type");
  }
  
  /**
   * Method pattern.
   * @return String
   */
  public Iterable<String> pattern() {
    return (Iterable<String>)map.get("pattern");
  }
  
  public <O>O maxInclusive() {
    return (O)map.get("maxInclusive");
  }
  
  public <O>O maxExclusive() {
    return (O)map.get("maxExclusive");
  }
  
  public <O>O minInclusive() {
    return (O)map.get("minInclusive");
  }
  
  public <O>O minExclusive() {
    return (O)map.get("minExclusive");
  }
  
  public boolean has(String key) {
    return map.containsKey(key);
  }
  
  public boolean hasUpperBound() {
    return has("maxInclusive") || has("maxExclusive");
  }
  
  public <O extends Comparable<? super O>>Range<O> bounds() {
    O mini = minInclusive();
    O mine = minExclusive();
    O maxi = maxInclusive();
    O maxe = maxExclusive();
    Ordering<O> ordering = Ordering.<O>natural();
    O min = ordering.nullsLast().min(mini,mine);
    O max = ordering.nullsFirst().max(maxi,maxe);
    BoundType lower = 
      min == null ? null :
      min == mini ? BoundType.CLOSED :
        BoundType.OPEN;
    BoundType upper = 
      max == null ? null : 
      max == maxi ? BoundType.CLOSED :
        BoundType.OPEN;
    if (lower == null && upper == null)
      return Range.<O>all();
    else if (lower != null && upper == null) 
      return lower == BoundType.CLOSED ? 
        Range.atLeast(min) : 
        Range.greaterThan(min);
    else if (lower == null && upper != null)
      return upper == BoundType.CLOSED ?
        Range.atMost(max) :
        Range.lessThan(max);
    else {
      return Range.range(min, lower, max, upper);
    }
  }
  
  public <N extends Number>N step() {
    return (N)map.get("step");
  }
  
  public <N extends Number>N step(N defaultValue) {
    N n = (N)map.get("step");
    return n != null ? n : defaultValue;
  }
  
  /**
   * Method stepInt.
   * @return int
   */
  public int stepInt() {
    return step();
  }
  
  /**
   * Method stepInt.
   * @param defaultValue int
   * @return int
   */
  public int stepInt(int defaultValue) {
    return step(defaultValue);
  }
  
  /**
   * Method stepLong.
   * @return long
   */
  public long stepLong() {
    return step();
  }
  
  /**
   * Method getLong.
   * @param defaultValue long
   * @return long
   */
  public long stepLong(long defaultValue) {
    return step(defaultValue);
  }
  
  /**
   * Method stepShort.
   * @return short
   */
  public short stepShort() {
    return step();
  }
  
  /**
   * Method stepShort.
   * @param defaultValue short
   * @return short
   */
  public short stepShort(short defaultValue) {
    return step(defaultValue);
  }
  
  /**
   * Method stepDouble.
   * @return double
   */
  public double stepDouble() {
    return step();
  }
  
  /**
   * Method stepDouble.
   * @param defaultValue double
   * @return double
   */
  public double stepDouble(double defaultValue) {
    return step(defaultValue);
  }
  
  /**
   * Method stepFloat.
   * @return float
   */
  public float stepFloat() {
    return step();
  }
  
  /**
   * Method stepFloat.
   * @param defaultValue float
   * @return float
   */
  public float stepFloat(float defaultValue) {
    return step(defaultValue);
  }
  
  /**
   * Method enumVals.
   * @return Iterable<Object>
   */
  public Iterable<Object> enumeration() {
    if (has("enumeration"))
      return (Iterable<Object>)map.get("enumeration");
    else 
      return ImmutableSet.of();
  }
  
  /**
   * Method placeholder.
   * @return NLV
   */
  public NLV placeholder() {
    return (NLV)map.get("placeholder");
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
  
  public int maxLength() {
    Integer i = (Integer) map.get("maxLength");
    return i != null ? i : -1;
  }
  
  public int minLength() {
    Integer i = (Integer) map.get("minLength");
    return i != null ? i : -1;
  }
  
  public int totalDigits() {
    Integer i = (Integer) map.get("totalDigits");
    return i != null ? i : -1;
  }
  
  public int fractionDigits() {
    Integer i = (Integer) map.get("fractionDigits");
    return i != null ? i : -1;
  }
  
  /**
   * Method placeholder.
   * @return NLV
   */
  public NLV displayName() {
    return (NLV)map.get("displayName");
  }
  
  /**
   * Method placeholderString.
   * @return String
   */
  public String displayNameString() {
    return _nlv("displayName");
  }
  
  /**
   * Method placeholderString.
   * @param lang String
   * @return String
   */
  public String displayNameString(String lang) {
    return _nlv("displayName", lang);
  }
  
  public String language() {
    return (String)map.get("language");
  }
  
  /**
   * Method _nlv.
   * @param key String
   * @return String 
   **/
  protected String _nlv(String key) {
    String lang = language();
    return _nlv(key, lang != null ? lang : DEFAULT_LOCALE);
  }
  
  /**
   * Method _nlv.
   * @param key String
   * @param lang String
   * @return String 
   **/
  protected String _nlv(String key, String lang) {
    NLV nlv = 
      (NLV)map.get(key);
    switch(nlv.valueType()) {
    case SIMPLE:
      NLV.SimpleNLV sim = 
        (SimpleNLV) nlv;
      String l = language();
      return l == null || Objects.equal(l,lang) ? sim.value() : null;
    case OBJECT:
      NLV.MapNLV map = 
       (MapNLV) nlv;
      return map.value(lang);
    default:
      return null;
    } 
  }
  
  public ValueType valueType() {
    return ValueType.OBJECT;
  }
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
  implements Serializable {
    private static final long serialVersionUID = -1975376657749952999L;
    private ImmutableMap<String,Object> map;
    SerializedForm(Parameter obj) {
      this.map = obj.map;
    }
  
    Object readResolve() 
      throws java.io.ObjectStreamException {
        Parameter.Builder builder = new Parameter.Builder();
        builder.map.putAll(map);
        return builder.get();
    }

  }
}
