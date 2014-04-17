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
package com.ibm.common.activitystreams.util;

import static com.google.common.base.Enums.getIfPresent;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Optional.of;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.joda.time.Duration.millis;
import static org.joda.time.Duration.standardDays;
import static org.joda.time.Duration.standardHours;
import static org.joda.time.Duration.standardMinutes;
import static org.joda.time.Duration.standardSeconds;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.Period;

import com.google.common.base.Converter;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

public final class Converters {

  private Converters() {}

  /**
   * Method toDuration.
   * @param v long
   * @param unit TimeUnit
   * @return Duration
   */
  public static Duration toDuration(long v, TimeUnit unit) {
    switch(unit) {
    case NANOSECONDS:
      return millis(v / 1000 / 1000);
    case MICROSECONDS:
      return millis(v / 1000);
    case MILLISECONDS:
      return millis(v);
    case SECONDS:
      return standardSeconds(v);
    case MINUTES:
      return standardMinutes(v);
    case HOURS:
      return standardHours(v);
    case DAYS:
      return standardDays(v);
    default:
      throw new IllegalArgumentException();
    }
  }
  
  /**
   * Method tryParseShort.
   * @param input String
   * @return Short
   */
  private static Short tryParseShort(String input) {
    try {
      return Short.parseShort(input);
    } catch (Throwable t) {
      return null;
    }
  }
  
  /**
   * Method tryParseBoolean.
   * @param input String
   * @return Boolean
   */
  private static Boolean tryParseBoolean(String input) {
    try {
      return Boolean.parseBoolean(input);
    } catch (Throwable t) {
      return null;
    }
  }
  
  /**
   * Method tryParseDateTime.
   * @param input String
   * @return DateTime
   */
  private static DateTime tryParseDateTime(String input) {
    try {
      return DateTime.parse(input);
    } catch (Throwable t) {
      return null;
    }
  }
  
  /**
   * Method tryParseDuration.
   * @param input String
   * @return Duration
   */
  private static Duration tryParseDuration(String input) {
    try {
      return Period.parse(input).toDurationFrom(DateTime.now());
    } catch (Throwable t) {
      return null;
    }
  }
  
  /**
   * Method tryParsePeriod.
   * @param input String
   * @return Period
   */
  private static Period tryParsePeriod(String input) {
    try {
      return Period.parse(input);
    } catch (Throwable t) {
      return null;
    }
  }
  
  /**
   * Method tryParseInterval.
   * @param input String
   * @return Interval
   */
  private static Interval tryParseInterval(String input) {
    try {
      return Interval.parse(input);
    } catch (Throwable t) {
      return null;
    }
  }

  public static final Function<Object,Optional<Interval>> toInterval =
      new Function<Object,Optional<Interval>>() {
        public Optional<Interval> apply(Object input) {
          Optional<Interval> ret = absent();
          if (input != null) 
            ret = input instanceof Interval ?
              of((Interval)input) :
                  fromNullable(tryParseInterval(input.toString()));
          return ret;
        }
      
    };
  
  public static final Function<Object,Optional<Duration>> toDuration =
    new Function<Object,Optional<Duration>>() {

      public Optional<Duration> apply(Object input) {
        Optional<Duration> ret = absent();
        if (input != null) 
          ret = input instanceof Duration ?
            of((Duration)input) :
              input instanceof Number ?
                of(standardSeconds(((Number)input).longValue())) :
                fromNullable(tryParseDuration(input.toString()));
        return ret;
      }
    
  };
  
  public static final Function<Object,Optional<Period>> toPeriod = 
    new Function<Object,Optional<Period>>() {
    public Optional<Period> apply(Object input) {
      Optional<Period> ret = absent();
      if (input != null) 
        ret = input instanceof Period ?
          of((Period)input) :
          fromNullable(tryParsePeriod(input.toString()));
      return ret;
    }
    };
  
  public static final Function<Object,Optional<DateTime>> toDateTime =
    new Function<Object,Optional<DateTime>>() {
      public Optional<DateTime> apply(Object input) {
        Optional<DateTime> ret = absent();
        if (input != null)
          ret = input instanceof DateTime ?
            of((DateTime)input) :
            input instanceof Date ?
              of(new DateTime((Date)input)) :
                input instanceof Number ?
                  of(new DateTime(((Number)input).longValue())) :
                  fromNullable(tryParseDateTime(input.toString()));
        return ret;
      }
    
    };
  
  public static final Function<Object,Optional<Boolean>> toBoolean =
    new Function<Object,Optional<Boolean>>() {
      public Optional<Boolean> apply(Object input) {
        Optional<Boolean> ret = absent();
        if (input != null)
          ret = input instanceof Boolean ?
            of((Boolean)input) :
              input instanceof Number ?
                of(((Number)input).intValue() != 0) :
                fromNullable(tryParseBoolean(input.toString()));
        return ret;
      }
  };
  
  public static final Function<Object,Optional<Short>> toShort = 
    new Function<Object,Optional<Short>>() {
      public Optional<Short> apply(Object input) {
        Optional<Short> ret = absent();
        if (input != null)
          ret = input instanceof Number ?
            of(((Number) input).shortValue()) :
            fromNullable(tryParseShort(input.toString()));
        return ret;
      }
  };
  
  public static final Function<Object,Optional<Integer>> toInt = 
    new Function<Object,Optional<Integer>>() {
      public Optional<Integer> apply(Object input) {
        Optional<Integer> ret = absent();
        if (input != null)
          ret = input instanceof Number ?
            of(((Number) input).intValue()) :
            fromNullable(Ints.tryParse(input.toString()));
        return ret;
      }
  };
  
  public static final Function<Object,Optional<Long>> toLong = 
    new Function<Object,Optional<Long>>() {
      public Optional<Long> apply(Object input) {
        Optional<Long> ret = absent();
        if (input != null)
          ret = input instanceof Number ?
            of(((Number) input).longValue()) :
            fromNullable(Longs.tryParse(input.toString()));
        return ret;
      }
  };
  
  public static final Function<Object,Optional<Double>> toDouble = 
    new Function<Object,Optional<Double>>() {
      public Optional<Double> apply(Object input) {
        Optional<Double> ret = absent();
        if (input != null)
          ret = input instanceof Number ?
            of(((Number) input).doubleValue()) :
            fromNullable(Doubles.tryParse(input.toString()));
        return ret;
      }
  };
  
  public static final Function<Object,Optional<Float>> toFloat = 
    new Function<Object,Optional<Float>>() {
      public Optional<Float> apply(Object input) {
        Optional<Float> ret = absent();
        if (input != null)
          ret = input instanceof Number ?
            of(((Number) input).floatValue()) :
            fromNullable(Floats.tryParse(input.toString()));
        return ret;
      }
  };
  
  /**
   * Method stringConverter.
   * @param enumClass Class<E>
   * @param or E
   * @return Converter<String,E>
   */
  public static <E extends Enum<E>> Converter<String,E> stringConverter(
    final Class<E> enumClass, 
    final E or) {
      return new Converter<String,E>() {

        @Override
        protected String doBackward(E e) {
          return checkNotNull(e).name();
        }

        @Override
        protected E doForward(String s) {
          return getIfPresent(enumClass, s).or(or);
        }
        
      };
  }
  
  /**
   * Method toUpperConverter.
   * @return Converter<String,String>
   */
  public static Converter<String,String> toUpperConverter() {
    return toLowerConverter().reverse();
  }
  
  /**
   * Method toUpperConverter.
   * @param locale Locale
   * @return Converter<String,String>
   */
  public static Converter<String,String> toUpperConverter(Locale locale) {
    return toLowerConverter(locale).reverse();
  }
  
  /**
   * Method toLowerConverter.
   * @return Converter<String,String>
   */
  public static Converter<String,String> toLowerConverter() {
    return toLowerConverter(Locale.getDefault());
  }
  
  /**
   * Method toLowerConverter.
   * @param locale Locale
   * @return Converter<String,String>
   */
  public static Converter<String,String> toLowerConverter(final Locale locale) {
    return new Converter<String,String>() {

      @Override
      protected String doForward(String a) {
        return a.toLowerCase(locale);
      }

      @Override
      protected String doBackward(String b) {
        return b.toUpperCase(locale);
      }
      
    };
  }
}
