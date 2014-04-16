package com.ibm.common.activitystreams.legacy;

import java.io.ObjectStreamException;

import com.google.common.base.Supplier;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.Collection;

public class Membership extends ASObject {

  public static final class Builder 
    extends ASObject.AbstractBuilder<Membership, Builder> {

    public Builder members(Collection collection) {
      return set("members", collection);
    }
    
    public Builder members(Supplier<? extends Collection> collection) {
      return members(collection.get());
    }
    
    public Membership get() {
      return new Membership(this);
    }
    
  }
  
  private Membership(Builder builder) {
    super(builder);
  }
  
  public Collection members() {
    return this.<Collection>get("members");
  }
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<Membership> {
    private static final long serialVersionUID = -2060301713159936285L;
    protected SerializedForm(Membership obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return super.doReadResolve();
    }
    protected Membership.Builder builder() {
      return new Builder();
    }
  }
}
