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

import com.google.common.base.Function;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

/**
 * @author james
 * @version $Revision: 1.0 $
 */
public abstract class SimpleAdapter<T>
  extends Adapter<T>
  implements Function<String,T> {

  /**
   * Method serialize.
   * @param t T
   * @param type Type
   * @param context JsonSerializationContext
  
  
   * @return JsonElement * @see com.google.gson.JsonSerializer#serialize(T, Type, JsonSerializationContext) */
  public JsonElement serialize(
    T t, 
    Type type, 
    JsonSerializationContext context) {
      return context.serialize(serialize(t));
  }

  /**
   * Method serialize.
   * @param t T
  
   * @return String */
  protected String serialize(T t) {
    return t != null ? t.toString() : null;
  }
  
  /**
   * Method deserialize.
   * @param v String
  
   * @return T */
  protected T deserialize(String v) {
    return apply(v);
  }
  
  /**
   * Method deserialize.
   * @param json JsonElement
   * @param type Type
   * @param context JsonDeserializationContext
  
  
  
   * @return T * @throws JsonParseException * @see com.google.gson.JsonDeserializer#deserialize(JsonElement, Type, JsonDeserializationContext) */
  public T deserialize(
    JsonElement json, 
    Type type,
    JsonDeserializationContext context) 
      throws JsonParseException {
    return deserialize(json.getAsJsonPrimitive().getAsString());
  }
}