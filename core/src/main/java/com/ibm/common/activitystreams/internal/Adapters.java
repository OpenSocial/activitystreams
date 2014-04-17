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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Iterables.size;

import static com.ibm.common.activitystreams.Makers.actions;
import static com.ibm.common.activitystreams.Makers.linkValues;
import static com.ibm.common.activitystreams.internal.ASObjectAdapter.primConverter;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Range;
import com.google.common.collect.Table;
import com.google.common.net.MediaType;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.internal.LazilyParsedNumber;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.ActionsValue;
import com.ibm.common.activitystreams.LinkValue;
import com.ibm.common.activitystreams.util.AbstractDictionaryObjectAdapter;

/**
 * @author james
 * @version $Revision: 1.0 $
 */
final class Adapters {

  private Adapters() {}

  /**
   * Method forEnum.
   * @param _enumClass Class<E>
   * @return EnumAdapter<E>
   */
  static <E extends Enum<E>>EnumAdapter<E> forEnum(Class<E> _enumClass) {
    return new EnumAdapter<E>(_enumClass);
  }
  
  /**
   * Method forEnum.
   * @param _enumClass Class<E>
   * @param or E
   * @return EnumAdapter<E>
   */
  static <E extends Enum<E>>EnumAdapter<E> forEnum(Class<E> _enumClass, E or) {
    return new EnumAdapter<E>(_enumClass,or);
  }
  
  static final Adapter<com.ibm.common.activitystreams.NLV> NLV = 
    new NaturalLanguageValueAdapter();
  
  static final Adapter<ActionsValue> ACTIONS =
    new AbstractDictionaryObjectAdapter
      <LinkValue,
       ActionsValue,
       ActionsValue.Builder>(LinkValue.class) {
    public JsonElement serialize(
      ActionsValue actions, 
      Type type,
      JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        for (String verb : actions) {
          Iterable<LinkValue> links = 
            actions.get(verb);
          obj.add(
            verb, 
            context.serialize(
              size(links) == 1 ? // if there's only one, serialize just 1
                getFirst(links,null) : // otherwise, serialize the list
                linkValues().add(links).get(),
              LinkValue.class));
        }
        return obj;
    }
    @Override
    protected ActionsValue.Builder builder() {
      return actions();
    }
  };
  
  static final Adapter<Iterable<?>> ITERABLE = 
    new Adapter<Iterable<?>>() {

      public JsonElement serialize(
        Iterable<?> i, 
        Type type,
        JsonSerializationContext context) {
        JsonArray ary = new JsonArray();
        for (Object obj : i)
          ary.add(context.serialize(obj, obj.getClass()));
        return ary;
      }

      public Iterable<?> deserialize(JsonElement arg0, Type arg1,
          JsonDeserializationContext arg2) throws JsonParseException {
        return null; // handled elsewhere
      }
    
  };
  
  static final Adapter<Date> DATE = 
    new SimpleAdapter<Date>() {
      protected String serialize(Date t) {
        return ISODateTimeFormat.dateTime().print(new DateTime(t));
      }
      public Date apply(String v) {
        return DateTime.parse(v).toDate();
      }
    };
  
  static final Adapter<DateTime> DATETIME =
    new SimpleAdapter<DateTime>() {
      protected String serialize(DateTime t) {
        return ISODateTimeFormat.dateTime().print(t);
      }
      public DateTime apply(String v) {
        return DateTime.parse(v);
      }
    };
  
  static final Adapter<Duration> DURATION =  
    new SimpleAdapter<Duration>() {
      public Duration apply(String v) {
        return Duration.parse(v);
      }
    };

  static final Adapter<Period> PERIOD =
    new SimpleAdapter<Period>() {
      public Period apply(String v) {
        return Period.parse(v);
      }
  };

  static final Adapter<Interval> INTERVAL =
    new SimpleAdapter<Interval>() {
      public Interval apply(String v) {
        return Interval.parse(v);
      }
    };
  
  static final Adapter<MediaType> MIMETYPE =
    new SimpleAdapter<MediaType>() {
      public MediaType apply(String v) {
        return MediaType.parse(v);
      }
    };

  static final MultimapAdapter MULTIMAP = 
    new MultimapAdapter();
  

  static final Adapter<Range<?>> RANGE = 
    new Adapter<Range<?>>() {

    public JsonElement serialize(
      Range<?> src, 
      Type typeOfSrc,
      JsonSerializationContext context) {
      JsonObject el = new JsonObject();
      el.add("lower", makeBound(src.lowerBoundType(),src.lowerEndpoint(),context));
      el.add("upper", makeBound(src.upperBoundType(),src.upperEndpoint(),context));
      return el;
    }
    
    private JsonElement makeBound(
      BoundType type, 
      Object val, 
      JsonSerializationContext context) {
      JsonObject obj = new JsonObject();
      obj.add("type", context.serialize(type.name().toLowerCase()));
      obj.add("endpoint", context.serialize(val));
      return obj;
    }

    @SuppressWarnings("rawtypes")
    public Range<?> deserialize(
      JsonElement json, 
      Type typeOfT,
      JsonDeserializationContext context) 
        throws JsonParseException {
      checkArgument(json.isJsonObject());
      try {
        JsonObject obj = json.getAsJsonObject();
        JsonObject upper = obj.getAsJsonObject("upper");
        JsonObject lower = obj.getAsJsonObject("lower");
        BoundType ubt = bt(upper.getAsJsonPrimitive("type"));
        BoundType lbt = bt(lower.getAsJsonPrimitive("type"));
        Object ub = des(upper.get("endpoint"),context);
        Object lb = des(lower.get("endpoint"),context);
        return Range.range((Comparable)lb, lbt, (Comparable)ub, ubt);
      } catch (Throwable t) {
        throw Throwables.propagate(t);
      }
    }
    
    private Object des(JsonElement val, JsonDeserializationContext context) {
      if (val.isJsonArray())
        return MultimapAdapter.arraydes(val.getAsJsonArray(), context);
      else if (val.isJsonObject())
        return context.deserialize(val, ASObject.class);
      else if (val.isJsonPrimitive()) {
        Object v = primConverter.convert(val.getAsJsonPrimitive());
        if (v instanceof LazilyParsedNumber) 
          v = new LazilyParsedNumberComparable((LazilyParsedNumber) v);
        return v;
      }
      else
        return null;
    }
    
    private BoundType bt(JsonPrimitive p) {
      try {
        return BoundType.valueOf(p.toString());
      } catch (Throwable t) {
        return BoundType.CLOSED;
      }
    }
  };
  
  static final Adapter<Optional<?>> OPTIONAL =
    new Adapter<Optional<?>>() {
      public JsonElement serialize(
        Optional<?> src, 
        Type typeOfSrc,
        JsonSerializationContext context) {
          return context.serialize(src.orNull());
      }
      public Optional<?> deserialize(
        JsonElement json, 
        Type typeOfT,
        JsonDeserializationContext context)
          throws JsonParseException {
        return null;
      }
  };
  
  static final Adapter<Table<?,?,?>> TABLE = 
    new Adapter<Table<?,?,?>>() {

    public JsonElement serialize(
      Table<?, ?, ?> src, 
      Type typeOfSrc,
      JsonSerializationContext context) {
      
      JsonObject obj = new JsonObject();
      for (Table.Cell<?, ?, ?> cell : src.cellSet()) {
        String r = cell.getRowKey().toString();
        String c = cell.getColumnKey().toString();
        JsonObject rowobj = null;
        if (!obj.has(r)) {
          rowobj = new JsonObject();
          obj.add(r, rowobj);
        } else {
          rowobj = obj.getAsJsonObject(r);
        }
        Object val = cell.getValue();
        if (val != null)
          rowobj.add(c, context.serialize(val,val.getClass()));
      }
      
      return obj;
    }

    public Table<?, ?, ?> deserialize(
      JsonElement json, 
      Type typeOfT,
      JsonDeserializationContext context) 
        throws JsonParseException {
      ImmutableTable.Builder<String,String,Object> table = 
        ImmutableTable.builder();
      checkArgument(json.isJsonObject());
      JsonObject obj = json.getAsJsonObject();
      for (Map.Entry<String,JsonElement> rowentry : obj.entrySet()) {
        String row = rowentry.getKey();
        JsonElement cell = rowentry.getValue();
        checkArgument(cell.isJsonObject());
        for (Map.Entry<String,JsonElement> cellentry : cell.getAsJsonObject().entrySet()) {
          String ckey = cellentry.getKey();
          JsonElement val = cellentry.getValue();
          Object desval = null;
          if (val.isJsonArray())
            desval = MultimapAdapter.arraydes(val.getAsJsonArray(),context);
          else if (val.isJsonObject())
            desval = context.deserialize(val, ASObject.class);
          else if (val.isJsonPrimitive())
            desval = primConverter.convert(val.getAsJsonPrimitive());
          if (desval != null)
            table.put(row,ckey,desval);
        }
      }
      return table.build();
    }

  };
}
