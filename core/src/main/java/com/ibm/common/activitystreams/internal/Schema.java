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
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.HashBiMap.create;
import static com.google.common.collect.Iterables.addAll;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.Set;

import org.joda.time.Duration;

import com.google.common.base.Converter;
import com.google.common.base.Supplier;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.MediaType;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.ActionsValue;
import com.ibm.common.activitystreams.Activity;
import com.ibm.common.activitystreams.Collection;

/**
 * @TODO: The Schema mechanism needs to be revisited and reworked
 *        to be much more efficient. 
 * @author james
 * @version $Revision: 1.0 $
 */
@SuppressWarnings("rawtypes")
public final class Schema {

  /**
   * @author james
   * @version $Revision: 1.0 $
   */
  public static final class Builder 
    implements Supplier<Schema> {

    final Map<String,Model> objectTypeMap =
      newHashMap();
    final Map<String,Class<? extends ASObject.AbstractBuilder>> builderMap =
      newHashMap();
    final Map<Class<? extends ASObject.AbstractBuilder>,Model> objectClassMap = 
      newHashMap();
    final BiMap<Class<? extends ASObject>, Class<? extends ASObject.AbstractBuilder>> classMap =
      create(100);
    final Set<Adapter<?>> adapters = 
      newHashSet();
    
    Builder() {}
    
    /**
     * Constructor for Builder.
     * @param template Schema
     */
    Builder(Schema template) {
      this.objectTypeMap.putAll(template.objectTypeMap);
      this.builderMap.putAll(template.builderMap);
      this.objectClassMap.putAll(template.objectClassMap);
      this.classMap.putAll(template.classMap);
      this.adapters.addAll(template.adapters);
    }
    
    /**
     * Method adapter.
     * @param _enumClass Class<E>
    
     * @return Builder */
    public <E extends Enum<E>>Builder adapter(
      Class<E> _enumClass) {
        return adapter(new EnumAdapter<E>(_enumClass));
    }
    
    /**
     * Method adapter.
     * @param _enumClass Class<E>
    
    
     * @param c Converter<String,E>
     * @return Builder */
    public <E extends Enum<E>>Builder adapter(
      Class<E> _enumClass, 
      Converter<String,E> c) {
        return adapter(new EnumAdapter<E>(_enumClass,c));
    }
    
    /**
     * Method adapter
     * @param _enumClass Class<E>
     * @param or E 
     * @return Builder
     */
    public <E extends Enum<E>>Builder adapter(
      Class<E> _enumClass, E or) {
        return adapter(new EnumAdapter<E>(_enumClass,or));
    }
    
    /**
     * Method adapter.
     * @param adapter Adapter<?>
    
     * @return Builder */
    public Builder adapter(Adapter<?> adapter) {
      this.adapters.add(adapter);
      return this;
    }
    
    /**
     * Method adapter.
     * @param adapters Adapter<?>[]
    
     * @return Builder */
    public Builder adapter(Adapter<?>... adapters) {
      if (adapters == null) return this;
      for (Adapter<?> a : adapters)
        adapter(a);
      return this;
    }
    
    /**
     * Method adapter.
     * @param adapters Iterable<Adapter<?>>
    
     * @return Builder */
    public Builder adapter(Iterable<Adapter<?>> adapters) {
      if (adapters == null) return this;
      addAll(this.adapters, adapters);
      return this;
    }
    
    /**
     * Method map.
     * @param objectType String
     * @param propertyMap Supplier<PropertyMap>
    
     * @return Builder */
    public Builder map(String objectType, Supplier<Model> propertyMap) {
      return map(objectType, propertyMap.get());
    }
    
    /**
     * Method map.
     * @param propertyMap Supplier<PropertyMap>
    
     * @return Builder */
    public Builder map(Supplier<Model> propertyMap) {
      return map(null,propertyMap);
    }
    
    /**
     * Method map.
     * @param propertyMap PropertyMap
    
     * @return Builder */
    public Builder map(Model propertyMap) {
      return map(null, propertyMap);
    }
    
    /**
     * Method map.
     * @param objectType String
     * @param propertyMap PropertyMap
    
     * @return Builder */
    public Builder map(String objectType, Model propertyMap) {
      checkNotNull(propertyMap);
      Class<? extends ASObject.AbstractBuilder<?,?>> _builder = propertyMap.builder();
      Class<? extends ASObject> _type = propertyMap.type();
      if (objectType != null) objectTypeMap.put(objectType, propertyMap);
      if (objectType != null && _builder != null)
        builderMap.put(objectType,_builder);
      if (_builder != null) objectClassMap.put(_builder, propertyMap);
      if (_builder != null && _type != null)
        classMap.put(_type,_builder);
      return this;
    }

    public Model model(String objectType) {
      return objectTypeMap.get(objectType);
    }
    
    public Model model() {
      return objectClassMap.get(ASObject.Builder.class);
    }
    
    /**
     * Method get.
     * @return Schema * @see com.google.common.base.Supplier#get() */
    public Schema get() {
      return new Schema(this);
    }
    
  }
  
  final ImmutableMap<String,Model> objectTypeMap;
  final ImmutableMap<Class<? extends ASObject.AbstractBuilder>, Model> objectClassMap;
  final ImmutableMap<String,Class<? extends ASObject.AbstractBuilder>> builderMap;
  final ImmutableBiMap<Class<? extends ASObject>, Class<? extends ASObject.AbstractBuilder>> classMap;
  final ImmutableSet<Adapter<?>> adapters;
  
  /**
   * Constructor for Schema.
   * @param builder Builder
   */
  Schema(Builder builder) {
    this.objectClassMap = ImmutableMap.copyOf(builder.objectClassMap);
    this.objectTypeMap = ImmutableMap.copyOf(builder.objectTypeMap);
    this.builderMap = ImmutableMap.copyOf(builder.builderMap);
    this.adapters = ImmutableSet.copyOf(builder.adapters);
    this.classMap = ImmutableBiMap.copyOf(builder.classMap);
    
    for (Model pmap : objectTypeMap.values())
      pmap.schema(this);
    for (Model pmap : objectClassMap.values())
      pmap.schema(this);

  }
  
  /**
   * Method adapters.
  
   * @return Iterable<Adapter<?>> */
  public Iterable<Adapter<?>> adapters() {
    return adapters;
  }
  
  /**
   * Method builderForObjectTypeOrClass.
   * @param ots String
   * @param _class Class<?>
  
   * @return Class<? extends ASObject.AbstractBuilder> */
  public Class<? extends ASObject.AbstractBuilder> builderForObjectTypeOrClass(String ots, Class<?> _class) {
    Class<? extends ASObject.AbstractBuilder> _builder = builderFor(ots);
    return _builder != null ?
      _builder : builderFor(_builder);
  }
  
  /**
   * Method builderFor.
   * @param _class Class<?>
  
   * @return Class<? extends ASObject.AbstractBuilder> */
  public Class<? extends ASObject.AbstractBuilder> builderFor(Class<?> _class) {
    if (_class == null) return null;
    return classMap.get(_class);
  }
  
  /**
   * Method classFor.
   * @param _builder Class<? extends ASObject.AbstractBuilder>
  
   * @return Class<? extends ASObject> */
  public Class<? extends ASObject> classFor(Class<? extends ASObject.AbstractBuilder> _builder) {
    if (_builder == null) return null;
    return classMap.inverse().get(_builder);
  }
  
  /**
   * Method builderFor.
   * @param ots String
  
   * @return Class<? extends ASObject.AbstractBuilder> */
  public Class<? extends ASObject.AbstractBuilder> builderFor(String ots) {
    if (ots == null) return null;
    return builderMap.get(ots);
  }
  
  /**
   * Method forObjectType.
   * @param objectType String
  
   * @return PropertyMap */
  public Model forObjectType(String objectType) {
    return objectTypeMap.get(objectType);
  }
  
  /**
   * Method forObjectClass.
   * @param _class Class<? extends ASObject.AbstractBuilder>
  
   * @return PropertyMap */
  public Model forObjectClass(Class<? extends ASObject.AbstractBuilder> _class) {
    return objectClassMap.get(_class);
  }
  
  /**
   * Method forObjectClassOrType.
   * @param _class Class<? extends ASObject.AbstractBuilder>
   * @param objectType String
  
   * @return PropertyMap */
  public Model forObjectClassOrType(Class<? extends ASObject.AbstractBuilder> _class, String objectType) {
    Model pm = forObjectClass(_class);
    return pm != null ? pm : forObjectType(objectType);
  }
  
  /**
   * Method template.
  
   * @return Builder */
  public Builder template() {
    return new Builder(this);
  }
  
  /**
   * Method toString.
  
   * @return String */
  public String toString() {
    return toStringHelper(Schema.class)
      .add("Object Types", objectTypeMap)
      .add("Object Classes", objectClassMap)
      .toString();
  }
  
  public final static Model object =
      Model
        .make()
        .type(
          ASObject.class, 
          ASObject.Builder.class)
        .typeValue(
          "objectType")
        .linkValue(
          "attachments", 
          "author",
          "duplicates",
          "icon",
          "image",
          "location",
          "inReplyTo",
          "tags",
          "url",
          "generator",
          "provider",
          "scope"
         )
         .dateTime(
           "published",
           "updated",
           "startTime",
           "endTime")
        .naturalLanguageValue(
          "summary",
          "title",
          "content",
          "displayName")
        .as("language", String.class)
        .as("actions", ActionsValue.class)
        .string("id", "rel", "alias")
        .doub("rating")
        .integer("height", "width")
        .as("mediaType", MediaType.class)
        .as("duration", Duration.class)
        .get();
    
  public final static Model activity =
      Model
        .make("object")
        .type(
          Activity.class, 
          Activity.Builder.class)
        .typeValue("verb")
        .linkValue(
          "actor", 
          "participant",
          "instrument",
          "object", 
          "target", 
          "result", 
          "to", 
          "bto", 
          "cc", 
          "bcc")
        .doub("priority")
        .as("status", Activity.Status.class)
        .get();

  public final static Model collection =
      Model
        .make("object")
        .type(
          Collection.class, 
          Collection.Builder.class)
        .dateTime(
          "itemsBefore", 
          "itemsAfter")
        .linkValue(
          "first", 
          "last", 
          "prev", 
          "next", 
          "current", 
          "self")
        .object("items")
        .integer(
          "startIndex", 
          "itemsPerPage",
          "totalItems")
        .get();

  /**
   * Method make.
  
   * @return Schema.Builder */
  public static Schema.Builder make() {
    return
      new Builder()
        .map(null, object)
        .map("activity", activity)
        .map("collection", collection);
  }
  
  public static final Schema DEFAULT_SCHEMA = make().get();
  
}
