package com.ibm.common.activitystreams.legacy;

import java.io.ObjectStreamException;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.LinkValue;

public class Question extends ASObject {

  public static final class Builder 
    extends ASObject.AbstractBuilder<Question, Builder> {

    Builder() {
      objectType("question");
    }
    
    public Builder option(String url, String... urls) {
      if (url != null)
        link("options", url);
      if (urls != null)
        for (String u : urls)
          link("options", u);
      return this;
    }
    
    public Builder option(LinkValue link, LinkValue... links) {
      if (link != null)
        link("options", link);
      if (links != null)
        for (LinkValue l : links)
          link("options", l);
      return this;
    }
    
    public Builder option(Supplier<? extends LinkValue> link) {
      return option(link.get());
    }
    
    public Question get() {
      return new Question(this);
    }
    
  }
  
  public Question(Builder builder) {
    super(builder);
  }

  public Iterable<LinkValue> options() {
    return links("options");
  }
  
  public Iterable<LinkValue> options(Predicate<? super LinkValue> filter) {
    return links("options", filter);
  }
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<Question> {
    private static final long serialVersionUID = -2060301713159936285L;
    protected SerializedForm(Question obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return super.doReadResolve();
    }
    protected Question.Builder builder() {
      return new Builder();
    }
  }
}
