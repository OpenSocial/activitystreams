package com.ibm.common.activitystreams.actions;

import java.lang.reflect.Type;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.ibm.common.activitystreams.internal.Adapter;

public final class ParametersAdapter 
  extends Adapter<ParametersValue> {

  public static final ParametersAdapter instance = 
    new ParametersAdapter();
  
  @Override
  public JsonElement serialize(
    ParametersValue params, 
    Type type,
    JsonSerializationContext context) {
    if (params == null) return null;
    JsonObject obj = new JsonObject();
    for (String param : params) {
      obj.add(
        param, 
        context.serialize(
          params.get(param), 
          ParameterValue.class));
    }
    return obj;
  }

  @Override
  public ParametersValue deserialize(
    JsonElement json, 
    Type type,
    JsonDeserializationContext context) 
      throws JsonParseException {
    checkArgument(json.isJsonObject());
    JsonObject obj = json.getAsJsonObject();
    ParametersValue.Builder builder =
      ParametersValue.make();
    for (Map.Entry<String,JsonElement> entry : obj.entrySet())
      builder.param(
        entry.getKey(), 
        context.<ParameterValue>deserialize(
          entry.getValue(), 
          ParameterValue.class));
    return builder.get();
  }

}
