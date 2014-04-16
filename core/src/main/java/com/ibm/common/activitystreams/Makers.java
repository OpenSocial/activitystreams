package com.ibm.common.activitystreams;

import java.util.Map;

import static com.google.common.collect.ImmutableMap.copyOf;
import com.ibm.common.activitystreams.internal.Schema;

/**
 * Utility class for creating Makers for all of the various objects.
 */
public final class Makers {

  private Makers() {}
  
  /**
   * Make a new ActionsValue.Builder
   * @return ParametersValue.Builder 
   **/
  public static ActionsValue.Builder actions() {
    return new ActionsValue.Builder();
  }
  
  /**
   * Make a new Schema.Builder
   * @return Schema.Builder 
   */
  public static Schema.Builder schema() {
    return Schema.make();
  }
  
  /**
   * Make a new IO.builder
   * @return IO.Builder 
   **/
  public static IO.Builder io() {
    return IO.make();
  }
  
  /**
   * Make a new Activity.Builder
   * @return Activity.Builder 
   **/
  public static Activity.Builder activity() {
    return new Activity.Builder();
  }
  
  /**
   * Make a new Collection.Builder
   * @return Collection.Builder 
   **/
  public static Collection.Builder collection() {
    return new Collection.Builder();
  }
  
  /**
   * Make a new ASObject.Builder
   * @return ASObject.Builder 
   **/
  public static ASObject.Builder object() {
    return new ASObject.Builder();
  }
  
  /**
   * Make a new ASObject.Builder with a specific objectType
   * @param type String
   * @return ASObject.Builder 
   **/
  public static ASObject.Builder object(String type) {
    return object().objectType(type);
  }

  /**
   * Return an Object from the given Map
   * @param map
   * @return ASObject
   */
  public static ASObject objectFrom(Map<String,?> map) {
    ASObject.Builder builder = object();
    for (Map.Entry<String,?> entry : copyOf(map).entrySet())
      builder.set(entry.getKey(), entry.getValue());
    return builder.get();
  }
  
  /**
   * Make a new ASObject.Builder
   * @param type TypeValue
   * @return ASObject.Builder 
   **/
  public static ASObject.Builder object(TypeValue type) {
    return object().objectType(type);
  }
  
  /**
   * Make a new TypeValue
   * @param iri String
   * @return TypeValue 
   **/
  public static TypeValue type(String iri) {
    return TypeValue.SimpleTypeValue.make(iri);
  }
  
  /**
   * Make a new LinkValue
   * @param iri String
   * @return LinkValue 
   **/
  public static LinkValue linkValue(String iri) {
    return LinkValue.SimpleLinkValue.make(iri);
  }
  
  /**
   * Make a new ArrayLinkValue.Builder
   * @return LinkValue.ArrayLinkValue.Builder 
   **/
  public static LinkValue.ArrayLinkValue.Builder linkValues() {
    return LinkValue.ArrayLinkValue.make();
  }
  
  /**
   * Make a new MapNLV.Builder
   * @return NLV.MapNLV.Builder 
   **/
  public static NLV.MapNLV.Builder nlv() {
    return NLV.MapNLV.make();
  }
  
  /**
   * Make a new SimpleNLV value
   * @param val String
   * @return NLV.SimpleNLV 
   **/
  public static NLV.SimpleNLV nlv(String val) {
    return NLV.SimpleNLV.make(val);
  }
  
  /**
   * Make a new verb ASObject.Builder
   * @param id String
   * @return ASObject.Builder
   */
  public static ASObject.Builder verb(String id) {
    return object("verb").id(id);
  }
  
  /**
   * Make a new objectType ASObject.Builder
   * @param id String
   * @return ASObject.Builder
   */
  public static ASObject.Builder objectType(String id) {
    return object("objectType").id(id);
  }
}
