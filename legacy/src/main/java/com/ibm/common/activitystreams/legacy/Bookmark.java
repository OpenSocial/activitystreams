package com.ibm.common.activitystreams.legacy;

import java.io.ObjectStreamException;

import com.ibm.common.activitystreams.ASObject;

public class Bookmark
  extends ASObject {

  public static final class Builder 
    extends ASObject.AbstractBuilder<Bookmark, Builder> {

    Builder() {
      objectType("bookmark");
    }
    
    public Builder targetUrl(String url) {
      return set("targetUrl", url);
    }
    
    public Bookmark get() {
      return new Bookmark(this);
    }
    
  }
  
  private Bookmark(Builder builder) {
    super(builder);
  }
  
  public String targetUrl() {
    return getString("targetUrl");
  }
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<Bookmark> {
    private static final long serialVersionUID = -2060301713159936285L;
    protected SerializedForm(Bookmark obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return super.doReadResolve();
    }
    protected Bookmark.Builder builder() {
      return new Builder();
    }
  }
}
