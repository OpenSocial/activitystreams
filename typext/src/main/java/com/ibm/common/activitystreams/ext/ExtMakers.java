package com.ibm.common.activitystreams.ext;

public final class ExtMakers {

  private ExtMakers() {}

  public static Verb.Builder verb() {
    return new Verb.Builder();
  }
  
  public static Verb.Builder verb(String id) {
    return verb().id(id);
  }
  
  public static ObjectType.Builder objectType() {
    return new ObjectType.Builder();
  }
  
  public static ObjectType.Builder objectType(String id) {
    return objectType().id(id);
  }
  
}
