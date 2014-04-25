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
package com.ibm.common.activitystreams;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.ibm.common.activitystreams.internal.Adapter;
import com.ibm.common.activitystreams.internal.GsonWrapper;
import com.ibm.common.activitystreams.internal.Schema;
import com.ibm.common.activitystreams.util.Module;

/**
 * The IO object is responsible for serializing and deserializing 
 * Activity Stream objects. Instances of IO should be created and
 * defined statically. IO instances are threadsafe and immutable
 * once created.
 * 
 * <p>You can choose to use one of the default IO instances:</p>
 * 
 * <pre>
 *   public static final IO io = IO.makeDefault();
 *   
 *   public static final IO prettyIo = IO.makeDefaultPrettyPrint();
 * </pre>
 * 
 * <p>Or you can use the IO.Builder to construct and configure your
 * own IO instance with custom adapters, object properties and 
 * type mappings:</p>
 * 
 * <pre>
 *   import static 
 * 
 *   public static final IO io = 
 *   IO.make()
 *     .schema(
 *       Makers.makeSchema().map(
 *         Schema.object.template()
 *           .as("foo", Foo.class)))
 *     .adapter(Foo.class, new MyFooAdapter())
 *     .get();
 * </pre>
 * 
 * <p>Once created, you can use IO instances to parse Activity Streams
 * documents:</p>
 * 
 * <pre>
 *  InputStream in = ...
 *  Activity activity = io.readAsActivity(in);
 * </pre>
 * 
 * <p>Or can use the IO instance to serialize:</p>
 * 
 * <pre>
 *   OutputStream out = ...
 *   Activity activity = ...
 *   activity.writeTo(out, io);
 * </pre>
 * 
 * @author james
 * @version $Revision: 1.0 $
 */
public final class IO {
  
  /**
   * Create a new IO.Builder
   * @return Builder */
  public static Builder make() {
    return new Builder();
  }
  
  /**
   * Create a new IO.Builder that uses the given schema
   * @param schema Schema
   * @return IO
   */
  public static IO makeWithSchema(Schema schema) {
    return make().schema(schema).get();
  }
  
  /**
   * Create a new IO.Builder that uses the given schema
   * @param schema Supplier<? extends Schema>
   * @return IO
   */
  public static IO makeWithSchema(Supplier<? extends Schema> schema) {
    return makeWithSchema(schema.get());
  }
  
  /**
   * Make or return the default IO instance
   * @return IO 
   **/
  public static IO makeDefault(Module... modules) {
    IO.Builder builder = make();
    if (modules != null)
      for (Module mod : modules)
        builder.using(mod);
    return builder.get();
  }
  
  /**
   * Make or return a default IO instance with Pretty Print enabled
   * @return IO
   */
  public static IO makeDefaultPrettyPrint(Module... modules) {
    IO.Builder builder = make().prettyPrint();
    if (modules != null)
      for (Module mod : modules)
        builder.using(mod);
    return builder.get();
  }
  
  public static class Builder 
    implements Supplier<IO> {

    private final GsonWrapper.Builder inner = 
      GsonWrapper.make();
    private Schema schema;
    private final ImmutableSet.Builder<Module> modules = 
      ImmutableSet.builder();
    
    public Builder using(Module module) {
      modules.add(module);
      return this;
    }
    
    /**
     * Turn pretty print on or off
     * @param on boolean
     * @return Builder 
     **/
    public Builder prettyPrint(boolean on) {
      inner.prettyPrint(on);
      return this;
    }
    
    /**
     * Turn pretty print on
     * @return Builder 
     **/
    public Builder prettyPrint() {
      return prettyPrint(true);
    }
    
    /**
     * Add an adapter
     * @param type Class<? extends T>
     * @param adapter Adapter<T>
     * @return Builder 
     **/
    public <T>Builder adapter(
      Class<? extends T> type, 
      Adapter<T> adapter) {
      inner.adapter(type, adapter);
      return this;
    }
    
    /** 
     * Add an adapter
     * @param type Class&lt;? extends T>
     * @return Builder
     */
    public <T>Builder adapter(
      Class<? extends T> type) {
      return adapter(type,null);
    }
    
    /** 
     * Add an adapter
     * @param type Class&lt;? extends T>
     * @return Builder
     */
    public <T>Builder hierarchicalAdapter(
      Class<? extends T> type) {
      return hierarchicalAdapter(type,null);
    }
    
    /**
     * Add an adapter.
     * @param type Class<? extends T>
     * @param adapter Adapter<T>
     * @param hier boolean
     * @return Builder 
     **/
    public <T>Builder hierarchicalAdapter(
      Class<? extends T> type, 
      Adapter<T> adapter) {
      inner.adapter(type, adapter, true);
      return this;
    }
    
    /**
     * Set the schema
     * @param schema Schema
     * @return Builder 
     **/
    public Builder schema(Schema schema) {
      //inner.schema(schema);
      this.schema = schema;
      return this;
    }
    
    /**
     * Set the schema.
     * @param schema Supplier<Schema>
     * @return Builder 
     **/
    public Builder schema(Supplier<Schema> schema) {
      return schema(schema.get());
    }
    
    public IO get() {
      Iterable<Module> mods = modules.build();
      Schema schema = this.schema;
      if (schema == null) {
        Schema.Builder builder = Schema.make();
        for (Module mod : mods) 
          mod.apply(builder);
        schema = builder.get();
      }
      inner.schema(schema);
      for (Module module : modules.build())
        module.apply(this, schema);
      return new IO(this);
    }
  }
  
  private final GsonWrapper gson;
  
  protected IO(Builder builder) {
    this.gson = 
      builder.inner.get();
  }
 
  /**
   * Write the given object
   * @param w Writable
   * @return String
   */
  public String write(Writable w) {
    StringWriter sw = new StringWriter();
    w.writeTo(sw,this);
    return sw.toString();
  }
  
  /**
   * Asynchronously write the given object
   * @param w
   * @param executor
   * @return java.util.concurrent.Future&lt;String>
   */
  public Future<String> write(
    final Writable w, 
    ExecutorService executor) {
    return executor.submit(
      new Callable<String>() {
        public String call() throws Exception {
          return write(w);
        }        
      }
    );
  }
  
  /**
   * Write the object to the given outputstream
   * @param w Writable
   * @param out OutputStream
   */
  public void write(Writable w, OutputStream out) {
    gson.write(w,out);
  }
  
  /**
   * Asychronously write the object to the given output stream
   * @param w
   * @param out
   * @param executor
   * @return java.util.concurrent.Future&lt;?>
   */
  public Future<?> write(
    final Writable w, 
    final OutputStream out,
    ExecutorService executor) {
    return executor.submit(
      new Runnable() {
        public void run() {
          write(w, out);
        }        
      }
    );
  }
  
  /**
   * Asychronously write the object to the given writer
   * @param w
   * @param out
   * @param executor
   * @return java.util.concurrent.Future&lt;?>
   */
  public Future<?> write(
    final Writable w, 
    final Writer out,
    ExecutorService executor) {
    return executor.submit(
      new Runnable() {
        public void run() {
          write(w, out);
        }        
      }
    );
  }
  
  /**
   * Write the object to the given writer
   * @param w Writable
   * @param out Writer
   */
  public void write(Writable w, Writer out) {
    gson.write(w,out);
  }
  
  /**
   * Asynchronously read the given input stream and 
   * return a parsed object of the given type
   * @param in
   * @param type
   * @param executor
   * @return java.util.concurrent.Future&lt;A extends ASObject>
   */
  public <A extends ASObject>Future<A> readAs(
    final InputStream in,
    final Class<? extends A> type,
    ExecutorService executor) {
      return executor.submit(
        new Callable<A>() {
          public A call() throws Exception {
            return readAs(in, type);
          }          
        }
      );
  }
  
  /**
   * Read the given input stream and return a parsed object
   * of the given type
   * @param in InputStream
   * @param type Class<? extends A>
   * @return A */
  public <A extends ASObject>A readAs(
    InputStream in, 
    Class<? extends A> type) {
      return gson.<A>readAs(in, type);
  }
  
  /**
   * Asynchronously read the given reader and return a parsed
   * object of the given type
   * @param in
   * @param type
   * @param executor
   * @return java.util.concurrent.Future&lt;A extends ASObject>
   */
  public <A extends ASObject>Future<A> readAs(
    final Reader in,
    final Class<? extends A> type,
    ExecutorService executor) {
      return executor.submit(
        new Callable<A>() {
          public A call() throws Exception {
            return readAs(in, type);
          }          
        }
      );
  }
  
  /**
   * Read the given reader and return a parsed object of the given type
   * @param in Reader
   * @param type Class<? extends A>
   * @return A */
  public <A extends ASObject>A readAs(
    Reader in, 
    Class<? extends A> type) {
      return gson.<A>readAs(in, type);
  }
  
  /**
   * Asynchronously read the given string and return a parsed object of 
   * the given type
   * @param in
   * @param type
   * @param executor
   * @return java.util.concurrent.Future&lt;A extends ASObject>
   */
  public <A extends ASObject>Future<A> readAs(
    final String in,
    final Class<? extends A> type,
    ExecutorService executor) {
      return executor.submit(
        new Callable<A>() {
          public A call() throws Exception {
            return readAs(in, type);
          }          
        }
      );
  }
  
  /**
   * Read the given string and return a parsed object of the given type
   * @param in String
   * @param type Class<? extends A>
   * @return A
   */
  public <A extends ASObject>A readAs(
    String in,
    Class<? extends A> type) {
      return readAs(new StringReader(in),type);
  }
  
  /**
   * Asynchronously read the given string 
   * @param in
   * @param executor
   * @return java.util.concurrent.Future&lt;ASObject>
   */
  public Future<ASObject> read(String in, ExecutorService executor) {
    return read(new StringReader(in), executor);
  }
  
  /**
   * Read the given string
   * @param in String
   * @return ASObject
   */
  public ASObject read(String in) {
    return read(new StringReader(in));
  }
  
  /**
   * Asynchronously read the given inputstream
   * @param in
   * @param executor
   * @return java.util.concurrent.Future&lt;ASObject>
   */
  public Future<ASObject> read(InputStream in, ExecutorService executor) {
    return readAs(in, ASObject.class, executor);
  }
  
  /**
   * Asynchronously read the given reader
   * @param in
   * @param executor
   * @return java.util.concurrent.Future&lt;ASObject>
   */
  public Future<ASObject> read(Reader in, ExecutorService executor) {
    return readAs(in, ASObject.class, executor);
  }
  
  /**
   * Read the given input stream.
   * @param in InputStream
   * @return ASObject 
   **/
  public ASObject read(InputStream in) {
    return readAs(in, ASObject.class);
  }
  
  /**
   * Return the given input stream
   * @param in InputStream
   * @return A
   */
  @SuppressWarnings("unchecked")
  public <A extends ASObject>A readAs(InputStream in) {
    return (A)read(in);
  }
  
  /**
   * Read the given string as an Activity object
   * @param in String
   * @return Activity
   */
  public Activity readAsActivity(String in) {
    return readAsActivity(new StringReader(in));
  }
  
  /**
   * Asynchronously read the given string as an Activity object
   * @param in
   * @param executor
   * @return java.util.concurrent.Future&lt;Activity>
   */
  public Future<Activity> readAsActivity(String in, ExecutorService executor) {
    return readAsActivity(new StringReader(in), executor);
  }
  
  /**
   * Asynchronously read the given inputstream as an Activity object
   * @param in
   * @param executor
   * @return java.util.concurrent.Future&lt;Activity>
   */
  public Future<Activity> readAsActivity(InputStream in, ExecutorService executor) {
    return readAs(in, Activity.class, executor);
  }
  
  /**
   * Asynchronously read the given reader as an Activity object
   * @param in
   * @param executor
   * @return java.util.concurrent.Future&lt;Activity>
   */
  public Future<Activity> readAsActivity(Reader in, ExecutorService executor) {
    return readAs(in, Activity.class, executor);
  }
  
  /**
   * Asynchronously read the given string as a Collection object
   * @param in
   * @param executor
   * @return java.util.concurrent.Future&lt;Collection>
   */
  public Future<Collection> readAsCollection(String in, ExecutorService executor) {
    return readAsCollection(new StringReader(in), executor);
  }
  
  /**
   * Asynchronously read the given input stream as a Collection object
   * @param in
   * @param executor
   * @return java.util.concurrent.Future&lt;Collection>
   */
  public Future<Collection> readAsCollection(InputStream in, ExecutorService executor) {
    return readAs(in, Collection.class, executor);
  }
  
  /**
   * Asynchronously read the given reader as a Collection object
   * @param in
   * @param executor
   * @return java.util.concurrent.Future&lt;Collection>
   */
  public Future<Collection> readAsCollection(Reader in, ExecutorService executor) {
    return readAs(in, Collection.class, executor);
  }
  
  /**
   * Read the given inputstream as an Activity.
   * @param in InputStream
   * @return Activity 
   **/
  public Activity readAsActivity(InputStream in) {
    return readAs(in, Activity.class);
  }
  
  /**
   * Read the given string as a Collection.
   * @param in InputStream
   * @return Collection 
   **/
  public Collection readAsCollection(String in) {
    return readAsCollection(new StringReader(in));
  }
  
  /**
   * Read the given inputstream as a Collection.
   * @param in InputStream
   * @return Collection 
   **/
  public Collection readAsCollection(InputStream in) {
    return readAs(in, Collection.class);
  }
  
  /**
   * Read the given reader
   * @param in
   * @return ASObject
   */
  public ASObject read(Reader in) {
    return readAs(in, ASObject.class);
  }
  
  /**
   * Read the given reader as an Activity
   * @param in Reader
   * @return Activity 
   **/
  public Activity readAsActivity(Reader in) {
    return readAs(in, Activity.class);
  }
  
  /**
   * Read the given reader as a Collection
   * @param in Reader
   * @return Collection 
   **/
  public Collection readAsCollection(Reader in) {
    return readAs(in, Collection.class);
  }
}
