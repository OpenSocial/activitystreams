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
package com.ibm.common.activitystreams.legacy;

import java.lang.reflect.Type;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.ibm.common.activitystreams.ASObject;
import static com.ibm.common.activitystreams.internal.ASObjectAdapter.primConverter;
import com.ibm.common.activitystreams.internal.Adapter;

public final class MediaLinkAdapter
  extends Adapter<MediaLink> {

  public JsonElement serialize(
    MediaLink src, 
    Type typeOfSrc,
    JsonSerializationContext context) {
    JsonObject el = new JsonObject();
    for (String key : src) {
      Object val = src.get(key);
      if (val != null) 
        el.add(key, context.serialize(val, val.getClass()));
    }
    return el;
  }

  public MediaLink deserialize(
    JsonElement json, 
    Type typeOfT,
    JsonDeserializationContext context) 
      throws JsonParseException {
    
    checkArgument(json.isJsonObject());
    JsonObject obj = (JsonObject) json;
    MediaLink.Builder builder = 
      LegacyMakers.mediaLink();
    for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
      String name = entry.getKey();
      JsonElement val = entry.getValue();
      if (val.isJsonPrimitive())
        builder.set(
          name,
          primConverter.convert(val.getAsJsonPrimitive()));
      else if (val.isJsonArray())        
        builder.set(
          name, 
          context.deserialize(val, Iterable.class));
      else if (val.isJsonObject())
        builder.set(
          name, 
          context.deserialize(
            val, 
            ASObject.class));
    }
    return builder.get();
  }

}
