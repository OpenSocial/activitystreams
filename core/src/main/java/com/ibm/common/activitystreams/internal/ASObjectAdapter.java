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


import static com.google.common.collect.ImmutableList.builder;
import static com.ibm.common.activitystreams.Makers.activity;
import static com.ibm.common.activitystreams.Makers.collection;
import static com.ibm.common.activitystreams.Makers.object;

import java.lang.reflect.Type;
import java.util.Map.Entry;

import com.google.common.base.Converter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.Activity;
import com.ibm.common.activitystreams.Collection;
import com.ibm.common.activitystreams.LinkValue;
import com.ibm.common.activitystreams.Makers;
import com.ibm.common.activitystreams.TypeValue;

/**
 * @author james
 * @version $Revision: 1.0 $
 */
public class ASObjectAdapter 
  extends Adapter<ASObject> {

  private final Schema schema;
  
  protected Schema schema() {
    return schema;
  }
  
  /**
   * Constructor for ASObjectAdapter.
   * @param schema Schema
   */
  protected ASObjectAdapter(Schema schema) {
    this.schema = schema;
  }
  
  /**
   * Method serialize.
   * @param obj ASObject
   * @param type Type
   * @param context JsonSerializationContext
  
   * @return JsonElement */
  public final JsonElement serialize(
    ASObject obj, 
    Type type,
    JsonSerializationContext context) {
    JsonObject el = 
      new JsonObject();
    for (String key : obj) {
      Object val = obj.get(key);
      if (val != null) {
        el.add(
          key, 
          context.serialize(
            val, 
            val.getClass()));
      }
    }
    return el;
    
  }

  private static final ImmutableSet<? extends Type> knownTypes = 
    ImmutableSet.of(
      Collection.class,
      Activity.class);
  
  protected boolean knowsType(Type type) {
    return knownTypes.contains(type);
  }
  
  protected ASObject.AbstractBuilder<?,?> builderFor(Type type) {
    if (type == Collection.class)
      return collection();
    else if (type == Activity.class)
      return activity();
    else return null;
  }
  
  protected Model modelFor(Type type) {
    if (type == Collection.class)
      return schema.forObjectClassOrType(
        Collection.Builder.class, 
        "collection");
    else if (type == Activity.class)
      return schema.forObjectClassOrType(
        Activity.Builder.class, 
        "activity");
    else return null;
  }
  
  /**
   * Method deserialize.
   * @param element JsonElement
   * @param type Type
   * @param context JsonDeserializationContext
   * @return ASObject 
   * @throws JsonParseException
   * @see com.google.gson.JsonDeserializer#deserialize(JsonElement, Type, JsonDeserializationContext) 
   **/
  public final ASObject deserialize(
    JsonElement element, 
    Type type,
    JsonDeserializationContext context) 
      throws JsonParseException {
    
    JsonObject obj = (JsonObject)element;
    ASObject.AbstractBuilder<?,?> builder = null;
    Model propMap = null;
    TypeValue tv = null;
    
    if (knowsType(type)) {
      builder = builderFor(type);
      propMap = modelFor(type);
    } else {
      if (obj.has("objectType")) {
        tv = context.deserialize(
          obj.get("objectType"), 
          TypeValue.class);
        @SuppressWarnings("rawtypes")
        Class<? extends ASObject.AbstractBuilder> _class = 
          schema.builderForObjectTypeOrClass(tv.id(), (Class)type);
        if (_class != null) {
          propMap = schema.forObjectClassOrType(_class, tv.id());
          if (!_class.isInterface()) {
            try {
              builder = _class.getConstructor(String.class).newInstance(tv.id());
            } catch (Throwable t) {
              try {
                builder = _class.newInstance();
                builder.set("objectType", tv);
              } catch (Throwable t2) {
                builder = Makers.object(tv);
              }
            }
          } else
            builder = Makers.object(tv);
        } else {
          builder = Makers.object(tv);
          propMap = schema.forObjectClassOrType(
            ASObject.Builder.class, tv.id());
        }
      } else {
        if (obj.has("verb") && 
            (obj.has("actor") || 
             obj.has("object") || 
             obj.has("target"))) {
           builder = activity();
           propMap = schema.forObjectClassOrType(
             Activity.Builder.class, "activity");
         } else if (obj.has("items")) {
           builder = collection();
           propMap = schema.forObjectClassOrType(
             Collection.Builder.class, 
             "collection");
         } else {
           @SuppressWarnings("rawtypes")
          Class<? extends ASObject.AbstractBuilder> _class = 
             schema.builderFor((Class)type);
           if (_class != null) {
             if (!_class.isInterface()) {
               try {
                 builder = _class.newInstance();
               } catch (Throwable t) {
                 builder = object();
               }
             } else builder = object();
           }
           if (builder == null)
             builder = object(); // anonymous
           propMap = schema.forObjectClass(builder.getClass());
           propMap = propMap != null ? 
             propMap : 
             schema.forObjectClass(
               ASObject.Builder.class);
         }
      }
    }
    
    for (Entry<String,JsonElement> entry : obj.entrySet()) {
      String name = entry.getKey();
      if (name.equalsIgnoreCase("objectType")) continue;
      Class<?> _class = propMap.get(name);
      JsonElement val = entry.getValue();
      if (val.isJsonPrimitive())
        builder.set(
          name,
          _class != null ?
            context.deserialize(val,_class) :
            primConverter.convert(val.getAsJsonPrimitive()));
      else if (val.isJsonArray()) { 
        builder.set(
          name,
          LinkValue.class.isAssignableFrom(_class!=null?_class:Object.class) ?
            context.deserialize(val, LinkValue.class) :
            convert(
              val.getAsJsonArray(),
              _class,
              context,
              builder()));
      } else if (val.isJsonObject())
        builder.set(
          name, 
          context.deserialize(
            val, 
            propMap.has(name) ? 
              propMap.get(name):
              ASObject.class));
    }
    return builder.get();
    
  }
  
  /**
   * Method convert.
   * @param arr JsonArray
   * @param _class Class<?>
   * @param context JsonDeserializationContext
   * @param list ImmutableList.Builder<Object>
   * @return ImmutableList<Object>
   */
  private ImmutableList<Object> convert(
    JsonArray arr, 
    Class<?> _class, 
    JsonDeserializationContext context,
    ImmutableList.Builder<Object> list) {
    processArray(arr, _class, context, list);
    return list.build();
  }

  /**
   * Method processArray.
   * @param arr JsonArray
   * @param _class Class<?>
   * @param context JsonDeserializationContext
   * @param list ImmutableList.Builder<Object>
   */
  private void processArray(
    JsonArray arr, 
    Class<?> _class, 
    JsonDeserializationContext context, 
    ImmutableList.Builder<Object> list) {
    for (JsonElement mem : arr) {
      if (mem.isJsonPrimitive())
        list.add(
          _class != null ? 
            context.deserialize(mem,_class) :
            primConverter.convert(
              mem.getAsJsonPrimitive()));
      else if (mem.isJsonObject())
        list.add(
          context.deserialize(
            mem, 
            _class != null ? 
              _class : 
              ASObject.class));
      else if (mem.isJsonArray())
        list.add(
          convert(
            mem.getAsJsonArray(),
            _class,
            context,
            builder()));
    }
  }
  
  public static final Converter<JsonPrimitive,Object> primConverter = 
    new Converter<JsonPrimitive,Object>() {
      @Override
      protected JsonPrimitive doBackward(Object a) {
        if (a instanceof Boolean)
          return new JsonPrimitive((Boolean)a);
        else if (a instanceof Number)
          return new JsonPrimitive((Number)a);
        else 
          return new JsonPrimitive(a.toString());
      }
      @Override
      protected Object doForward(JsonPrimitive b) {
        if (b.isBoolean())
          return b.getAsBoolean();
        else if (b.isNumber())
          return b.getAsNumber();
        else 
          return b.getAsString();
      }
  };
}
