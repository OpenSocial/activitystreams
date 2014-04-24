package com.ibm.common.activitystreams.ext;

import com.ibm.common.activitystreams.ASObject;

public final class Verb 
  extends ASObject {

  public static final class Builder 
    extends ASObject.AbstractBuilder<Verb, Builder> {

    Builder() {
      objectType("verb");
    }

    public Verb get() {
      return new Verb(this);
    }
    
  }
  
  private Verb(Builder builder) {
    super(builder);
  }
  
}
