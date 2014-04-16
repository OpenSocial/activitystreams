package com.ibm.common.activitystreams.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.ibm.common.activitystreams.Makers.type;

import java.lang.reflect.Type;

import com.google.common.base.Function;
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
  private final Function<TypeValue,TypeValue> resolver;
  
  /**
   * Constructor for TypeValueAdapter.
   * @param schema Schema
   */
  public TypeValueAdapter(
    Schema schema, 
    Function<TypeValue,TypeValue> resolver) {
    this.schema = schema;
    this.resolver = resolver;
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
      return resolver.apply(type(prim.getAsString()));
    } else {
      JsonObject obj = el.getAsJsonObject();
      if (obj.has("objectType")) {
        TypeValue tv = 
          context.deserialize(
            obj.get("objectType"), 
            TypeValue.class);
        Model pMap = 
          schema.forObjectType(tv.id());
        return resolver.apply(
          context.<ASObject>deserialize(
            el, 
            pMap.type() != null ? 
              pMap.type() : 
              ASObject.class));
      } else {
        return resolver.apply(
          context.<ASObject>deserialize(
            el, 
            ASObject.class));
      }
    }
  }

}
