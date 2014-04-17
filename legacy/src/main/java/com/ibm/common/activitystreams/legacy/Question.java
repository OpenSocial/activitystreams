/**
 * Copyright 2013 OpenSocial Foundation
 * Copyright 2013 International Business Machines Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Utility library for working with Activity Streams Actions
 * Requires underscorejs.
 *
 * @author James M Snell (jasnell@us.ibm.com)
 */
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
