package com.ibm.common.activitystreams.internal;
import static com.google.common.base.Enums.stringConverter;
import static com.google.common.base.Preconditions.checkArgument;
import static com.ibm.common.activitystreams.util.Converters.stringConverter;
import static com.ibm.common.activitystreams.util.Converters.toLowerConverter;
import static com.ibm.common.activitystreams.util.Converters.toUpperConverter;

import java.lang.reflect.Type;

import com.google.common.base.Converter;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

/**
 * @author james
 * @version $Revision: 1.0 $
 */
public final class EnumAdapter<E extends Enum<E>> 
  extends Adapter<E> {
  
  protected static final Converter<String,String> toLower = 
    toLowerConverter();
  protected static final Converter<String,String> toUpper = 
    toUpperConverter();
  
  private final Converter<String,E> des;
  private final Converter<E,String> ser;
  /**
   * Constructor for EnumAdapter.
   * @param _enumClass Class<E>
   */
  public EnumAdapter(Class<E> _enumClass) {
    this(_enumClass, stringConverter(_enumClass));
  }
  
  /**
   * Constructor for EnumAdapter
  
  
   * @param _enumClass Class<E>
   * @param or E
   */
  public EnumAdapter(Class<E> _enumClass, E or) {
    this(_enumClass, stringConverter(_enumClass,or));
  }
  
  /**
   * Constructor for EnumAdapter.
   * @param _enumClass Class<E>
  
   * @param c Converter<String,E>
   */
  public EnumAdapter(
    Class<E> _enumClass, 
    Converter<String,E> c) {
    super();
    this.des = toUpper.andThen(c);
    this.ser = c.reverse().andThen(toLower);
  }
    
  /**
   * Method serialize.
   * @param src E
   * @param typeOfSrc Type
   * @param context JsonSerializationContext
   * @return JsonElement
   */
  public JsonElement serialize(
    E src, 
    Type typeOfSrc,
    JsonSerializationContext context) {
    return context.serialize(ser.convert(src));
  }

  /**
   * Method deserialize.
   * @param json JsonElement
   * @param typeOfT Type
   * @param context JsonDeserializationContext
   * @return E
   * @throws JsonParseException
   * @see com.google.gson.JsonDeserializer#deserialize(JsonElement, Type, JsonDeserializationContext)
   */
  public E deserialize(
    JsonElement json, 
    Type typeOfT,
    JsonDeserializationContext context) 
      throws JsonParseException {
    checkArgument(json.isJsonPrimitive());
    JsonPrimitive jp = json.getAsJsonPrimitive();
    checkArgument(jp.isString());
    return des.convert(jp.getAsString());
  }
  
  /**
   * Method convert.
   * @param s String
   * @return E
   */
  protected E convert(String s) {
    return des.convert(s);
  }
  
}