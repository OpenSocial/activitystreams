package com.ibm.common.activitystreams.actions;

import java.lang.reflect.Type;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.Iterables;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.NLV;
import com.ibm.common.activitystreams.actions.ParameterValue.SimpleParameterValue;
import com.ibm.common.activitystreams.internal.Adapter;

public final class ParameterAdapter 
  extends Adapter<ParameterValue> {

  public static final ParameterAdapter instance = 
    new ParameterAdapter();

  @Override
  public JsonElement serialize(
    ParameterValue src, 
    Type typeOfSrc,
    JsonSerializationContext context) {
    
    if (src == null) return null;
    switch(src.valueType()) {
    case OBJECT:
      if (src instanceof Parameter) {
        Parameter parameter = (Parameter) src;
        JsonObject obj = new JsonObject();
        if (parameter.has("type")) {
          String type = parameter.type();
          if (type != null)
            obj.addProperty("type", type);
        }
        if (parameter.has("displayName")) {
          NLV nlv = parameter.displayName();
          if (nlv != null)
            obj.add("displayName", context.serialize(nlv, NLV.class));
        }
        if (parameter.has("placeholder")) {
          NLV nlv = parameter.placeholder();
          if (nlv != null)
            obj.add("placeholder", context.serialize(nlv, NLV.class));
        }
        if (parameter.has("enumeration")) {
          Iterable<Object> e = parameter.enumeration();
          if (e != null)
            obj.add("enumeration", context.serialize(e,e.getClass()));
        }
        if (parameter.has("language")) {
          String lang = parameter.language();
          if (lang != null)
            obj.addProperty("language", lang);
        }
        if (parameter.has("maxInclusive")) {
          Object max = parameter.maxInclusive();
          if (max != null)
            obj.add("maxInclusive", context.serialize(max, max.getClass()));
        }
        if (parameter.has("maxExclusive")) {
          Object max = parameter.maxExclusive();
          if (max != null)
            obj.add("maxExclusive", context.serialize(max, max.getClass()));
        }
        if (parameter.has("minInclusive")) {
          Object max = parameter.minInclusive();
          if (max != null)
            obj.add("minInclusive", context.serialize(max, max.getClass()));
        }
        if (parameter.has("minExclusive")) {
          Object max = parameter.minExclusive();
          if (max != null)
            obj.add("minExclusive", context.serialize(max, max.getClass()));
        }
        if (parameter.has("pattern")) {
          Iterable<String> pattern = parameter.pattern();
          if (pattern != null) {
            if (Iterables.size(pattern) == 0) {
              String first = Iterables.getFirst(pattern, null);
              if (first != null)
                obj.addProperty("pattern", first);
            } else 
              obj.add("pattern", context.serialize(pattern, pattern.getClass()));
          }
        }
        if (parameter.has("repeated")) {
          boolean repeated = parameter.repeated();
          if (repeated)
            obj.addProperty("repeated", repeated);
        }
        if (parameter.has("required")) {
          boolean required = parameter.required();
          if (!required)
            obj.addProperty("required", required);
        }
        if (parameter.has("step")) {
          Number n = parameter.step();
          if (n != null)
            obj.add("step", context.serialize(n,n.getClass()));
        }
        if (parameter.has("default")) {
          Object def = parameter.defaultValue();
          if (def != null) 
            obj.add("default",context.serialize(def, def.getClass()));
        }
        if (parameter.has("minLength")) {
          int len = parameter.minLength();
          if (len > -1)
            obj.addProperty("minLength", len);
        }
        if (parameter.has("maxLength")) {
          int len = parameter.maxLength();
          if (len > -1)
            obj.addProperty("maxLength", len);
        }
        if (parameter.has("totalDigits")) {
          int len = parameter.totalDigits();
          if (len > -1)
            obj.addProperty("totalDigits", len);
        }
        if (parameter.has("fractionDigits")) {
          int len = parameter.fractionDigits();
          if (len > -1)
            obj.addProperty("fractionDigits", len);
        }
        if (parameter.has("value")) {
          Object val = parameter.value();
          if (val != null)
            obj.add("value", context.serialize(val, val.getClass()));
        }
        return obj;
      } else if (src instanceof UrlTemplate) {
        return context.serialize(src, UrlTemplate.class);
      } else if (src instanceof TypedPayload) {
        return context.serialize(src, TypedPayload.class);
      } else if (src instanceof ASObject) {
        return context.serialize(src, ASObject.class);
      } else throw new IllegalArgumentException();
    case SIMPLE:
      SimpleParameterValue spv = (SimpleParameterValue) src;
      return context.serialize(spv.type(), String.class);
    default:
      throw new IllegalArgumentException();
    }
  }

  private Object deserialize(
    JsonDeserializationContext context,
    JsonElement el) {
      if (el.isJsonArray()) {
        return context.deserialize(el, Iterable.class);
      } else if (el.isJsonObject()) {
        return context.deserialize(el, ASObject.class);
      } else if (el.isJsonPrimitive()) {
        JsonPrimitive p = el.getAsJsonPrimitive();
        if (p.isBoolean())
          return p.getAsBoolean();
        else if (p.isNumber())
          return p.getAsNumber();
        else
          return p.getAsString();
      } else return null;
  }
  
  @Override
  public ParameterValue deserialize(
    JsonElement json, 
    Type typeOfT,
    JsonDeserializationContext context) 
      throws JsonParseException {

    checkArgument(json.isJsonPrimitive() || json.isJsonObject());
    
    if (json.isJsonPrimitive()) {
      JsonPrimitive jp = json.getAsJsonPrimitive();
      checkArgument(jp.isString());
      return ActionMakers.parameter(jp.getAsString());
    } else {
      JsonObject obj = json.getAsJsonObject();
      if (obj.has("objectType")) {
        ASObject as = context.deserialize(obj, ASObject.class);
        checkArgument(as instanceof ParameterValue);
        return (ParameterValue) as;
      } else {
        Parameter.Builder builder =
          ActionMakers.parameter();
        if (obj.has("default"))
          builder.defaultValue(
            deserialize(context, obj.get("default")));
        if (obj.has("displayName"))
          builder.displayName(
            context.<NLV>deserialize(
              obj.get("displayName"), 
              NLV.class));
        if (obj.has("enumeration"))
          builder.enumeration(
            context.<Iterable<?>>deserialize(
              obj.get("enumeration"), 
              Iterable.class));
        if (obj.has("fractionDigits"))
          builder.fractionDigits(
            obj.get("fractionDigits").getAsInt());
        if (obj.has("language"))
          builder.language(
            obj.get("language").getAsString());
        if (obj.has("maxExclusive"))
          builder.maxExclusive(
            deserialize(context, obj.get("maxExclusive")));
        if (obj.has("maxInclusive"))
          builder.maxInclusive(
              deserialize(context, obj.get("maxInclusive")));
        if (obj.has("minExclusive"))
          builder.minExclusive(
              deserialize(context, obj.get("minExclusive")));
        if (obj.has("minInclusive"))
          builder.minInclusive(
            deserialize(context, obj.get("minInclusive")));
        if (obj.has("maxLength"))
          builder.maxLength(
            obj.get("maxLength").getAsInt());
        if (obj.has("minLength"))
          builder.minLength(
            obj.get("minLength").getAsInt());
        if (obj.has("pattern"))
          builder.pattern(
            context.<Iterable<String>>deserialize(
              obj.get("pattern"), Iterable.class));
        if (obj.has("placeholder"))
          builder.placeholder(
            context.<NLV>deserialize(
              obj.get("placeholder"), NLV.class));
        if (obj.has("repeated") && obj.get("repeated").getAsBoolean())
            builder.repeated();
        if (obj.has("required") && !obj.get("required").getAsBoolean())
            builder.optional();
        if (obj.has("step")) 
          builder.step(
            obj.get("step").getAsNumber());
        if (obj.has("totalDigits")) 
          builder.totalDigits(
            obj.get("totalDigits").getAsInt());
        if (obj.has("type"))
          builder.type(
            obj.get("type").getAsString());
        if (obj.has("value"))
          builder.value(
              deserialize(context, obj.get("value")));
        return builder.get();
      }
    }
  }

}
