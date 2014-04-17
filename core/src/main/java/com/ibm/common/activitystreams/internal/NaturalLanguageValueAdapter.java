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

import java.lang.reflect.Type;
import java.util.Map.Entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.ibm.common.activitystreams.NLV;
import com.ibm.common.activitystreams.NLV.MapNLV;

/**
 * @author james
 * @version $Revision: 1.0 $
 */
final class NaturalLanguageValueAdapter 
  extends Adapter<NLV> {

  /**
   * Method serialize.
   * @param nlv NLV
   * @param type Type
   * @param context JsonSerializationContext
  
   * @return JsonElement */
  public JsonElement serialize(
    NLV nlv, 
    Type type,
    JsonSerializationContext context) {
      JsonElement el = null;
      switch (nlv.valueType()) {
      case SIMPLE:
        el = context.serialize(((NLV.SimpleNLV)nlv).value());
        break;
      case OBJECT:
        NLV.MapNLV map = 
          (MapNLV) nlv;
        JsonObject obj = new JsonObject();
        for (String lang : map)
         obj.addProperty(
           lang.toString(), 
           map.value(lang));
        el = obj;
        break;
      default:
      }
      return el;
  }

  /**
   * Method deserialize.
   * @param element JsonElement
   * @param type1 Type
   * @param context JsonDeserializationContext
  
  
  
   * @return NLV * @throws JsonParseException * @see com.google.gson.JsonDeserializer#deserialize(JsonElement, Type, JsonDeserializationContext) */
  public NLV deserialize(
    JsonElement element, 
    Type type1,
    JsonDeserializationContext context)
      throws JsonParseException {
    checkArgument(
      element.isJsonPrimitive() || 
      element.isJsonObject());
    if (element.isJsonPrimitive()) {
      JsonPrimitive prim = 
        element.getAsJsonPrimitive();
      checkArgument(prim.isString());
      return NLV.SimpleNLV.make(
        prim.getAsString());
    } else {
      try {
        JsonObject obj = 
          element.getAsJsonObject();
        NLV.MapNLV.Builder builder = 
          NLV.MapNLV.make();
        for (Entry<String,JsonElement> entry : obj.entrySet())
          builder.set(
            entry.getKey(), 
            entry.getValue().getAsString());  
        return builder.get();
      } catch (Throwable t) {
        throw new IllegalArgumentException();
      }
    }
  }

}
