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
import java.lang.reflect.Type;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInterval;
import org.joda.time.ReadablePeriod;

import com.google.common.base.Objects;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.ASObject.AbstractBuilder;
import com.ibm.common.activitystreams.LinkValue;
import com.ibm.common.activitystreams.NLV;
import com.ibm.common.activitystreams.TypeValue;

/**
 * @author james
 * @version $Revision: 1.0 $
 */
public final class Model {

  final String parent;
  final ImmutableMap<String,Type> properties;
  final Type _default;
  final Type _type;
  final Type _builder;
  private transient Schema schema;
  
  /**
   * Method schema.
   * @param schema Schema
   */
  protected void schema(Schema schema) {
    this.schema = schema;
  }
  
  /**
   * Method make.
  
   * @return Builder */
  public static Builder make() {
    return make(null);
  }
  
  /**
   * Method make.
   * @param parent String
  
   * @return Builder */
  public static Builder make(String parent) {
    return new Builder().parent(parent);
  }
  
  /**
   * @author james
   * @version $Revision: 1.0 $
   */
  public static final class Builder 
    implements Supplier<Model> {

    Type _type;
    Type _builder;
    Type _default = null;
    String parent = null;
    
    final Map<String,Type> properties =
      Maps.newHashMap();

    Builder() {}
    
    /**
     * Constructor for Builder.
     * @param template PropertyMap
     */
    Builder(Model template) {
      this.parent = template.parent;
      this._default = template._default;
      this._type = template._type;
      this._builder = template._builder;
      this.properties.putAll(template.properties);
    }

    /**
     * Method withDefault.
     * @param _default Class<?>
     * @return Builder 
     **/
    public Builder withDefault(Class<?> _default) {
      this._default = _default;
      return this;
    }
    
      /**
       * Method type.
       * @param _type Class<?>
       * @param _builder Class<?>
       * @return Builder 
       **/
      @SuppressWarnings("unchecked")
      public Builder type(
        Class<?> _type, Class<?> _builder) {
        this._type = (Class<? extends ASObject>) _type;
        this._builder = (Class<? extends ASObject.AbstractBuilder<?, ?>>) _builder;
        return this;
      }

    
    /**
     * Method parent.
     * @param parent String  
     * @return Builder 
     **/
    public Builder parent(String parent) {
      this.parent = parent;
      return this;
    }
    
    /**
     * Method naturalLanguageValue.
     * @param name String
     * @return Builder 
     **/
    public Builder naturalLanguageValue(String name) {
      return as(name, NLV.class);
    }
    
    /**
     * Method naturalLanguageValue.
     * @param names String[]
     * @return Builder 
     **/
    public Builder naturalLanguageValue(String... names) {
      for (String name : names)
        naturalLanguageValue(name);
      return this;
    }
    
    /**
     * Method object.
     * @param name String
     * @return Builder 
     **/
    public Builder object(String name) {
      return as(name, ASObject.class);
    }
    
    /**
     * Method object.
     * @param names String[]
     * @return Builder 
     **/
    public Builder object(String... names) {
      for (String name : names)
        object(name);
      return this;
    }
    
    /**
     * Method integer.
     * @param name String
     * @return Builder 
     **/
    public Builder integer(String name) {
      return as(name, Integer.class);
    }
    
    /**
     * Method integer.
     * @param names String[]
     * @return Builder 
     **/
    public Builder integer(String... names) {
      for (String name : names)
        integer(name);
      return this;
    }
    
    /**
     * Method doub.
     * @param name String
     * @return Builder 
     **/
    public Builder doub(String name) {
      return as(name, Double.class);
    }
    
    /**
     * Method doub.
     * @param names String[] 
     * @return Builder 
     **/
    public Builder doub(String... names) {
      for (String name : names)
        doub(name);
      return this;
    }
    
    /**
     * Method doub.
     * @param name String
     * @return Builder 
     **/
    public Builder floatValue(String name) {
      return as(name, Float.class);
    }
    
    /**
     * Method doub.
     * @param names String[] 
     * @return Builder 
     **/
    public Builder floatValue(String... names) {
      for (String name : names)
        floatValue(name);
      return this;
    }
    
    /**
     * Method string.
     * @param name String  
     * @return Builder 
     **/
    public Builder string(String name) {
      return as(name, String.class);
    }
    
    /**
     * Method string.
     * @param names String[]
     * @return Builder 
     **/
    public Builder string(String... names) {
      for (String name : names)
        string(name);
      return this;
    }
    
    /**
     * Method linkValue.
     * @param name String
     * @return Builder 
     **/
    public Builder linkValue(String name) {
      return as(name, LinkValue.class);
    }
    
    /**
     * Method linkValue.
     * @param names String[]
     * @return Builder 
     **/
    public Builder linkValue(String... names) {
      for (String name : names) 
        linkValue(name);
      return this;
    }
    
    /**
     * Method dateTime.
     * @param name String
     * @return Builder 
     **/
    public Builder dateTime(String name) {
      return as(name, DateTime.class);
    }
    
    /**
     * Method dateTime.
     * @param names String[]
     * @return Builder 
     **/
    public Builder dateTime(String... names) {
      for (String name : names)
        dateTime(name);
      return this;
    }
    
    /**
     * Method duration.
     * @param name String
     * @return Builder
     */
    public Builder duration(String name) {
      return as(name, ReadableDuration.class);
    }
    
    /**
     * Method duration.
     * @param names String[]
     * @return Builder
     */
    public Builder duration(String... names) {
      for (String name : names)
        duration(name);
      return this;
    }
    
    /**
     * Method period.
     * @param name String
     * @return Builder
     */
    public Builder period(String name) {
      return as (name, ReadablePeriod.class);
    }
    
    /**
     * Method period.
     * @param names String[]
     * @return Builder
     */
    public Builder period(String... names) {
      for (String name: names)
        period(name);
      return this;
    }
    
    /**
     * Method interval.
     * @param name String
     * @return Builder
     */
    public Builder interval(String name) {
      return as(name, ReadableInterval.class);
    }
    
    /**
     * Method interval.
     * @param names String[]
     * @return Builder
     */
    public Builder interval(String... names) {
      for (String name: names)
        interval(name);
      return this;
    }
    
    /**
     * Method typeValue.
     * @param name String
    
     * @return Builder */
    public Builder typeValue(String name) {
      return as(name, TypeValue.class);
    }
    
    /**
     * Method typeValue.
     * @param names String[]
    
     * @return Builder */
    public Builder typeValue(String... names) {
      for (String name : names)
        typeValue(name);
      return this;
    }
    
    /**
     * Method as.
     * @param name String
     * @param _class Class<?>
    
     * @return Builder */
    public Builder as(String name, Class<?> _class) {
      this.properties.put(name,_class);
      return this;
    }
    
    /**
     * Method get.
    
    
     * @return PropertyMap * @see com.google.common.base.Supplier#get() */
    public Model get() {
      return new Model(this);
    }
    
  }
  
  /**
   * Constructor for PropertyMap.
   * @param builder Builder
   */
  Model(Builder builder) {
    this.parent = builder.parent;
    this.properties = ImmutableMap.copyOf(builder.properties);
    this._default = builder._default;
    this._type = builder._type;
    this._builder = builder._builder;
  }
  
  /**
   * Method parentPropertyMap.
  
   * @return PropertyMap */
  protected Model parentPropertyMap() {
    if (schema == null) 
      return null;
    if (parent == null)
      return null;
    return schema.forObjectClassOrType(
      ASObject.Builder.class, 
      parent.equals("object") ? null : parent);
  }
  
  /**
   * Method get.
   * @param name String
  
   * @return Class<?> */
  @SuppressWarnings("unchecked")
  public <T extends Type>T get(String name) {
    Model parent = parentPropertyMap();
    return (T)(properties.containsKey(name) ?
      properties.get(name) :
      parent != null && _default == null ? 
        parent.get(name) : _default);
  }
  
  /**
   * Method containsKey.
   * @param name String
  
   * @return boolean */
  public boolean has(String name) {
    Model parent = parentPropertyMap();
    return properties.containsKey(name) ? 
      true : parent != null ? 
        parent.has(name) : false;
  }
  
  /**
   * Method type.
  
   * @return Class<? extends ASObject> */
  @SuppressWarnings("unchecked")
  public Class<? extends ASObject> type() {
    return (Class<? extends ASObject>) _type;
  }
  
  /**
   * Method builder.
  
   * @return Class<? extends ASObject.AbstractBuilder<?,?>> */
  @SuppressWarnings("unchecked")
  public Class<? extends ASObject.AbstractBuilder<?,?>> builder() {
    return (Class<? extends AbstractBuilder<?, ?>>) _builder;
  }
  
  /**
   * Method toString.
  
   * @return String */
  public String toString() {
    return Objects.toStringHelper(Model.class)
      .omitNullValues()
      .add("Parent", parent)
      .add("Properties", properties)
      .toString();
  }
  
  /**
   * Method template.
  
   * @return Builder */
  public Builder template() {
    return new Builder(this);
  }
  
  /**
   * Method template.
   * @param _type Class<? extends B>
   * @param _builder Class<? extends X>
  
   * @return PropertyMap */
  public <B extends ASObject, X extends ASObject.AbstractBuilder<B,X>> Model template(
    Class<? extends B> _type, Class<? extends X> _builder) {
      return template().type(_type,_builder).get();
  }
  
  /**
   * Method set.
   * @param name String
   * @param type Class<?>
  
   * @return PropertyMap */
  public Model set(String name, Class<?> type) {
    return template()
      .as(name, type)
      .get();
  }
  
  /**
   * Method set.
   * @param map Map<String,Class<?>>
  
   * @return PropertyMap */
  public Model set(Map<String,Class<?>> map) {
    Builder builder = template();
    for (Map.Entry<String,Class<?>> entry : map.entrySet())
      builder.as(entry.getKey(),entry.getValue());
    return builder.get();
  }
}
