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

import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Iterables.size;

import static com.ibm.common.activitystreams.internal.ASObjectAdapter.primConverter;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.ibm.common.activitystreams.ASObject;

/**
 * @author james
 * @version $Revision: 1.0 $
 */
@SuppressWarnings({"rawtypes","unchecked"})
final class MultimapAdapter
  extends Adapter<Multimap> {
  
  /**
   * Method getAdaptedClass.
  
   * @return Class<Multimap> */
  public Class<Multimap> getAdaptedClass() {
    return Multimap.class;
  }

  /**
   * Method serialize.
   * @param src Multimap
   * @param typeOfSrc Type
   * @param context JsonSerializationContext
  
   * @return JsonElement */
  public JsonElement serialize(
    Multimap src, 
    Type typeOfSrc,
    JsonSerializationContext context) {
      JsonObject obj = new JsonObject();  
      for (Object key : src.keySet()) {
        Iterable<Object> vals = src.get(key);
        if (size(vals) == 1) {
          Object f = getFirst(vals, null);
          if (f != null)
            obj.add(key.toString(), context.serialize(f, f.getClass()));
        } else {
          obj.add(key.toString(), context.serialize(vals, Iterable.class));
        }
      }
      return obj;
  }

  /**
   * Method arraydes.
   * @param array JsonArray
   * @param context JsonDeserializationContext
  
   * @return ImmutableList<Object> */
  protected static ImmutableList<Object> arraydes(
    JsonArray array,
    JsonDeserializationContext context) {
    ImmutableList.Builder<Object> builder = 
      ImmutableList.builder();
    for (JsonElement child : array)
      if (child.isJsonArray())
        builder.add(arraydes(child.getAsJsonArray(),context));
      else if (child.isJsonObject())
        builder.add(context.deserialize(child, ASObject.class));
      else if (child.isJsonPrimitive())
        builder.add(primConverter.convert(child.getAsJsonPrimitive()));
    return builder.build();
  }
    
  /**
   * Method deserialize.
   * @param json JsonElement
   * @param typeOfT Type
   * @param context JsonDeserializationContext
  
  
  
   * @return Multimap * @throws JsonParseException * @see com.google.gson.JsonDeserializer#deserialize(JsonElement, Type, JsonDeserializationContext) */
  public Multimap deserialize(
    JsonElement json, 
    Type typeOfT,
    JsonDeserializationContext context) 
      throws JsonParseException {
    ImmutableMultimap.Builder mm = 
      ImmutableMultimap.builder();
    JsonObject obj = json.getAsJsonObject();
    for (Map.Entry<String,JsonElement> entry : obj.entrySet()) {
      String key = entry.getKey();
      JsonElement val = entry.getValue();
      if (val.isJsonArray()) {
        for (JsonElement el : val.getAsJsonArray()) {
          if (el.isJsonArray())
            mm.put(key, arraydes(el.getAsJsonArray(),context));
          else if (el.isJsonObject())
            mm.put(key, context.deserialize(el, ASObject.class));
          else if (el.isJsonPrimitive())
            mm.put(key,primConverter.convert(el.getAsJsonPrimitive()));
        }
      } else if (val.isJsonObject()) {
        mm.put(key, context.deserialize(val, ASObject.class));
      } else if (val.isJsonPrimitive()) {
        mm.put(key, primConverter.convert(val.getAsJsonPrimitive()));
      }
    }
    return mm.build();
  }  
  
}
