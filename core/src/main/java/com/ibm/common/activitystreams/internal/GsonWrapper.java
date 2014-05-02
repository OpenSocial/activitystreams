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

import static com.google.gson.internal.bind.TypeAdapters.NUMBER;
import static com.ibm.common.activitystreams.internal.Adapters.DATE;
import static com.ibm.common.activitystreams.internal.Adapters.DATETIME;
import static com.ibm.common.activitystreams.internal.Adapters.NLV;
import static com.ibm.common.activitystreams.internal.Adapters.TABLE;
import static com.ibm.common.activitystreams.internal.Adapters.OPTIONAL;
import static com.ibm.common.activitystreams.internal.Adapters.ACTIONS;
import static com.ibm.common.activitystreams.internal.Adapters.DURATION;
import static com.ibm.common.activitystreams.internal.Adapters.INTERVAL;
import static com.ibm.common.activitystreams.internal.Adapters.ITERABLE;
import static com.ibm.common.activitystreams.internal.Adapters.MIMETYPE;
import static com.ibm.common.activitystreams.internal.Adapters.MULTIMAP;
import static com.ibm.common.activitystreams.internal.Adapters.RANGE;
import static com.ibm.common.activitystreams.internal.Adapters.PERIOD;
import static com.ibm.common.activitystreams.internal.Adapters.forEnum;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInterval;
import org.joda.time.ReadablePeriod;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import com.google.common.collect.Table;
import com.google.common.net.MediaType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LazilyParsedNumber;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.ActionsValue;
import com.ibm.common.activitystreams.Activity;
import com.ibm.common.activitystreams.Collection;
import com.ibm.common.activitystreams.LinkValue;
import com.ibm.common.activitystreams.NLV;
import com.ibm.common.activitystreams.TypeValue;
import com.ibm.common.activitystreams.Writable;

/**
 * @author james
 * @version $Revision: 1.0 $
 */
public final class GsonWrapper {

  /**
   * Method make.
  
   * @return Builder */
  public static final Builder make() {
    return new Builder();
  }
  
  /**
   * @author james
   * @version $Revision: 1.0 $
   */
  public static final class Builder 
    implements Supplier<GsonWrapper> {

    private String charset = "UTF-8";
    private boolean pretty;
    private Schema schema = null; // default
    private ImmutableList.Builder<AdapterEntry<?>> adapters =
      ImmutableList.builder();
    
    /**
     * Method charset.
     * @param charset String
    
     * @return Builder */
    public Builder charset(String charset) {
      this.charset = charset;
      return this;
    }
    
    /**
     * Method schema.
     * @param schema Schema
    
     * @return Builder */
    public Builder schema(Schema schema) {
      this.schema = schema;
      return this;
    }
    
    /**
     * Method adapter.
     * @param type Class<? extends T>
     * @param adapter Adapter<T>
    
     * @return Builder */
    public <T>Builder adapter(
      Class<? extends T> type, 
      Adapter<T> adapter) {
        return adapter(type,adapter,false);
    }
    
    /**
     * Method adapter.
     * @param type Class<? extends T>
     * @param adapter Adapter<T>
     * @param hier boolean
    
     * @return Builder */
    public <T>Builder adapter(
      Class<? extends T> type, 
      Adapter<T> adapter, 
      boolean hier) {
        adapters.add(new AdapterEntry<T>(type,adapter,hier));
        return this;
    }
    
    /**
     * Method prettyPrint.
     * @param on boolean
    
     * @return Builder */
    public Builder prettyPrint(boolean on) {
      this.pretty = on;
      return this;
    }
    
    /**
     * Method prettyPrint.
    
     * @return Builder */
    public Builder prettyPrint() {
      return prettyPrint(true);
    }
    
    /**
     * Method get.
    
    
     * @return GsonWrapper * @see com.google.common.base.Supplier#get() */
    public GsonWrapper get() {
      return new GsonWrapper(this);
    }
    
  }
  
  /**
   * @author james
   * @version $Revision: 1.0 $
   */
  private final static class AdapterEntry<T> {
    private final Class<? extends T> type;
    private final Adapter<T> adapter;
    private final boolean hier;
    /**
     * Constructor for AdapterEntry.
     * @param type Class<? extends T>
     * @param adapter Adapter<T>
     * @param hier boolean
     */
    AdapterEntry(
      Class<? extends T> type, 
      Adapter<T> adapter, 
      boolean hier) {
        this.type = type;
        this.adapter = adapter;
        this.hier = hier;
    }
  }
  
  private final Gson gson;
  private final String charset;
  
  /**
   * Constructor for GsonWrapper.
   * @param builder Builder
   */
  protected GsonWrapper(Builder builder) {
    Schema schema = 
      builder.schema != null ? 
        builder.schema : 
        Schema.make().get();
    ASObjectAdapter base = 
      new ASObjectAdapter(schema);
    GsonBuilder b = initGsonBuilder(
      builder,
      schema,
      base, 
      builder.adapters.build());
    if (builder.pretty)
      b.setPrettyPrinting();
    this.gson = b.create();
    this.charset = builder.charset;
  }
  
  /**
   * Method initGsonBuilder.
   * @param builder Builder
  
   * @return GsonBuilder */
  private static GsonBuilder initGsonBuilder(
    Builder builder, 
    Schema schema, 
    ASObjectAdapter base,
    Iterable<AdapterEntry<?>> adapters) {
    
    GsonBuilder gson = new GsonBuilder()
    .registerTypeHierarchyAdapter(TypeValue.class, new TypeValueAdapter(schema))
    .registerTypeHierarchyAdapter(LinkValue.class, new LinkValueAdapter(schema))
    .registerTypeHierarchyAdapter(Iterable.class, ITERABLE)
    .registerTypeHierarchyAdapter(ASObject.class, base)
    .registerTypeHierarchyAdapter(Collection.class, base)
    .registerTypeHierarchyAdapter(Activity.class, base)
    .registerTypeHierarchyAdapter(NLV.class, NLV)
    .registerTypeHierarchyAdapter(ActionsValue.class, ACTIONS)
    .registerTypeHierarchyAdapter(Optional.class, OPTIONAL)
    .registerTypeHierarchyAdapter(Range.class, RANGE)
    .registerTypeHierarchyAdapter(Table.class, TABLE)
    .registerTypeHierarchyAdapter(LazilyParsedNumber.class, NUMBER)
    .registerTypeHierarchyAdapter(LazilyParsedNumberComparable.class, NUMBER)
    .registerTypeHierarchyAdapter(ReadableDuration.class, DURATION)
    .registerTypeHierarchyAdapter(ReadablePeriod.class, PERIOD)
    .registerTypeHierarchyAdapter(ReadableInterval.class, INTERVAL)
    .registerTypeAdapter(
      Activity.Status.class, 
      forEnum(
        Activity.Status.class, 
        Activity.Status.OTHER))
    .registerTypeAdapter(Date.class, DATE)
    .registerTypeAdapter(DateTime.class, DATETIME)
    .registerTypeAdapter(MediaType.class, MIMETYPE)
    .registerTypeHierarchyAdapter(Multimap.class, MULTIMAP);
    
    for (AdapterEntry<?> entry : adapters) {
      if (entry.hier)
        gson.registerTypeHierarchyAdapter(
          entry.type, 
          entry.adapter!=null ?
            entry.adapter : base);
      else
        gson.registerTypeAdapter(
          entry.type, 
          entry.adapter!=null ? 
            entry.adapter:base);
    }
    
    return gson;

  }
  
  /**
   * Method write.
   * @param w Writable
   * @param out OutputStream
   */
  public void write(Writable w, OutputStream out) {
    try {
      OutputStreamWriter wout = 
        new OutputStreamWriter(out, charset);
      gson.toJson(w,wout);
      wout.flush();
    } catch (Throwable t) {
      throw Throwables.propagate(t);
    }
  }
  
  /**
   * Method write.
   * @param w Writable
   * @param out Writer
   */
  public void write(Writable w, Writer out) {
    gson.toJson(w,out);
  }
  
  /**
   * Method write.
   * @param w Writable
  
   * @return String */
  public String write(Writable w) {
    StringWriter sw = 
      new StringWriter();
    write(w,sw);
    return sw.toString();
  }
  
  /**
   * Method readAs.
   * @param in InputStream
   * @param type Class<? extends A>
  
   * @return A */
  public <A extends ASObject>A readAs(InputStream in, Class<? extends A> type) {
    try {
      return readAs(new InputStreamReader(in, charset), type);
    } catch (Throwable t) {
      throw Throwables.propagate(t);
    }
  }
  
  /**
   * Method readAs.
   * @param in Reader
   * @param type Class<? extends A>
  
   * @return A */
  public <A extends ASObject>A readAs(Reader in, Class<? extends A> type) {
    return (A)gson.fromJson(in, type);
  }
}
