package com.ibm.common.activitystreams.legacy;

import java.io.ObjectStreamException;

import com.google.common.base.Supplier;
import com.ibm.common.activitystreams.ASObject;

public class WithImage extends ASObject {

  public static final class Builder 
    extends ASObject.AbstractBuilder<WithImage, Builder> {

    public Builder fullImage(MediaLink link) {
      return set("fullImage", link);
    }
    
    public Builder fullImage(Supplier<? extends MediaLink> link) {
      return fullImage(link.get());
    }
    
    public WithImage get() {
      return new WithImage(this);
    }
    
  }
  
  private WithImage(Builder builder) {
    super(builder);
  }
  
  public MediaLink fullImage() {
    return this.<MediaLink>get("fullImage");
  }
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<WithImage> {
    private static final long serialVersionUID = -2060301713159936285L;
    protected SerializedForm(WithImage obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return super.doReadResolve();
    }
    protected WithImage.Builder builder() {
      return new Builder();
    }
  }
}
