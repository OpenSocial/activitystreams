
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