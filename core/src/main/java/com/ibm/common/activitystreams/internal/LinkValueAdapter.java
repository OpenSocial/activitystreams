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
import static com.ibm.common.activitystreams.Makers.linkValue;
import static com.ibm.common.activitystreams.Makers.linkValues;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.LinkValue;
import com.ibm.common.activitystreams.LinkValue.SimpleLinkValue;
import com.ibm.common.activitystreams.TypeValue;

/**
 * @author james
 * @version $Revision: 1.0 $
 */
final class LinkValueAdapter 
  extends Adapter<LinkValue> {

  private final Schema schema;
  
  /**
   * Constructor for LinkValueAdapter.
   * @param schema Schema
   */
  public LinkValueAdapter(Schema schema) {
    this.schema = schema;
  }
  
  /**
   * Method serialize.
   * @param value LinkValue
   * @param type Type
   * @param context JsonSerializationContext
  
   * @return JsonElement */
  public JsonElement serialize(
    LinkValue value, 
    Type type,
    JsonSerializationContext context) {
      switch(value.valueType()) {
      case SIMPLE:
        LinkValue.SimpleLinkValue simple = (SimpleLinkValue) value;
        return context.serialize(simple.url(), String.class);
      case ARRAY:
        return context.serialize(value, Iterable.class);
      case OBJECT:
        return context.serialize(value, ASObject.class);
      default:
        throw new IllegalArgumentException();
      }
  }

  /**
   * Method deserialize.
   * @param el JsonElement
   * @param type Type
   * @param context JsonDeserializationContext
  
  
  
   * @return LinkValue * @throws JsonParseException * @see com.google.gson.JsonDeserializer#deserialize(JsonElement, Type, JsonDeserializationContext) */
  public LinkValue deserialize(
    JsonElement el, 
    Type type,
    JsonDeserializationContext context) 
      throws JsonParseException {
    checkArgument(
      el.isJsonArray() || 
      el.isJsonObject() || 
      el.isJsonPrimitive());
    if (el.isJsonArray()) {
      LinkValue.ArrayLinkValue.Builder builder = 
        linkValues();
      for (JsonElement aryel : el.getAsJsonArray())
        builder.add(
          context.<LinkValue>deserialize(
            aryel, 
            LinkValue.class));
      return builder.get();
    } else if (el.isJsonObject()) {
      JsonObject obj = el.getAsJsonObject();
      if (obj.has("objectType")) {
        TypeValue tv = 
          context.deserialize(
            obj.get("objectType"), 
            TypeValue.class);
        Model pMap = 
          schema.forObjectType(tv.id());
        return context.deserialize(
          el, 
          pMap != null && pMap.type() != null ? 
            pMap.type() : 
            ASObject.class);
      } else {
        return context.deserialize(
          el, 
          ASObject.class);
      }
    } else {
      JsonPrimitive prim = 
        el.getAsJsonPrimitive();
      checkArgument(prim.isString());
      return linkValue(prim.getAsString());
    } 
  }

}
