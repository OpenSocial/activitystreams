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
import static com.ibm.common.activitystreams.Makers.type;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.TypeValue;
import com.ibm.common.activitystreams.ValueType;

/**
 * @author james
 * @version $Revision: 1.0 $
 */
final class TypeValueAdapter 
  extends Adapter<TypeValue> {

  private final Schema schema;
  
  /**
   * Constructor for TypeValueAdapter.
   * @param schema Schema
   */
  public TypeValueAdapter(
    Schema schema) {
    this.schema = schema;
  }
  
  /**
   * Method serialize.
   * @param value TypeValue
   * @param type Type
   * @param context JsonSerializationContext
  
   * @return JsonElement */
  public JsonElement serialize(
    TypeValue value, 
    Type type,
    JsonSerializationContext context) {
      boolean simple = value.valueType() == ValueType.SIMPLE;
      return context.serialize(
        simple ? value.id() : value,
        simple ? String.class : ASObject.class
      );
  }

  /**
   * Method deserialize.
   * @param el JsonElement
   * @param type Type
   * @param context JsonDeserializationContext
  
  
  
   * @return TypeValue * @throws JsonParseException * @see com.google.gson.JsonDeserializer#deserialize(JsonElement, Type, JsonDeserializationContext) */
  public TypeValue deserialize(
    JsonElement el, 
    Type type,
    JsonDeserializationContext context) 
      throws JsonParseException {
    checkArgument(
      el.isJsonPrimitive() || 
      el.isJsonObject());
    if (el.isJsonPrimitive()) {
      JsonPrimitive prim = 
        el.getAsJsonPrimitive();
      checkArgument(prim.isString());
      return type(prim.getAsString());
    } else {
      JsonObject obj = el.getAsJsonObject();
      if (obj.has("objectType")) {
        TypeValue tv = 
          context.deserialize(
            obj.get("objectType"), 
            TypeValue.class);
        Model pMap = 
          schema.forObjectType(tv.id());
        return 
          context.<ASObject>deserialize(
            el, 
            pMap.type() != null ? 
              pMap.type() : 
              ASObject.class);
      } else {
        return 
          context.<ASObject>deserialize(
            el, 
            ASObject.class);
      }
    }
  }

}
