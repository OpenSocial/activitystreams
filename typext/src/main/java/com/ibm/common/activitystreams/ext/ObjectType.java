package com.ibm.common.activitystreams.ext;

import com.ibm.common.activitystreams.ASObject;

public final class ObjectType 
  extends ASObject {

  public static final class Builder 
    extends ASObject.AbstractBuilder<ObjectType, Builder> {

    Builder() {
      objectType("objectType");
    }
    
    public ObjectType get() {
      return new ObjectType(this);
    }
    
  }
  
  private ObjectType(Builder builder) {
    super(builder);
  }
}
