package com.ibm.common.activitystreams.util;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Type;
import java.util.Map.Entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.ibm.common.activitystreams.internal.Adapter;

/**
 * Abstract base GSON Serializer for AbstractDictionaryObject
 * instances.
 * @author james
 * @version $Revision: 1.0 $
 */
public abstract class AbstractDictionaryObjectAdapter
  <Y,X extends AbstractDictionaryObject<Y>,
   B extends AbstractDictionaryObject.AbstractBuilder<Y, X, B>>
  extends Adapter<X> {

  private final Class<Y> klass;
  
  /**
   * Constructor for AbstractDictionaryObjectAdapter.
   * @param klass Class<Y>
   */
  protected AbstractDictionaryObjectAdapter(Class<Y> klass) {
    this.klass = klass;
  }
  
  public JsonElement serialize(
    X x, 
    Type type,
    JsonSerializationContext context) {
      JsonObject obj = new JsonObject();
      for (String key : x)
        obj.add(
          key, 
          context.serialize(
            x.getSingle(key),
            klass));
      return obj;
  }

  public X deserialize(
    JsonElement element, 
    Type type1,
    JsonDeserializationContext context)
      throws JsonParseException {
    checkArgument(element.isJsonObject());
    try {
      JsonObject obj = 
        element.getAsJsonObject();
      B builder = builder();
      for (Entry<String,JsonElement> entry : obj.entrySet())
        builder.set(
          entry.getKey(), 
          context.<Y>deserialize(
            entry.getValue(), 
            klass));  
      return builder.get();
    } catch (Throwable t) {
      t.printStackTrace();
      throw new IllegalArgumentException();
    }
  }

  protected abstract B builder();
}
