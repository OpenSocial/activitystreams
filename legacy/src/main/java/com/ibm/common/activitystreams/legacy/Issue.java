package com.ibm.common.activitystreams.legacy;

import java.io.ObjectStreamException;

import com.google.common.collect.ImmutableList;
import com.ibm.common.activitystreams.ASObject;

public class Issue
  extends ASObject {

  public static final class Builder 
    extends ASObject.AbstractBuilder<Issue, Builder> {

    Builder() {
      objectType("issue");
    }
    
    public Builder types(String type, String... types) {
      ImmutableList.Builder<String> list = 
        ImmutableList.builder();
      if (type != null) 
        list.add(type);
      if (types != null)
        list.add(types);
      return types(list.build());
    }
    
    public Builder types(Iterable<String> types) {
      return set("types", types);
    }
    
    public Issue get() {
      return new Issue(this);
    }
    
  }
  
  private Issue(Builder builder) {
    super(builder);
  }
  
  public Iterable<String> types() {
    return this.<Iterable<String>>get("types");
  }
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<Issue> {
    private static final long serialVersionUID = -2060301713159936285L;
    protected SerializedForm(Issue obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return super.doReadResolve();
    }
    protected Issue.Builder builder() {
      return new Builder();
    }
  }
}
