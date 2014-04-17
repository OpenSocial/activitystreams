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

import com.google.common.collect.ImmutableList;
import com.ibm.common.activitystreams.ASObject;

/**
 * The legacy "issue" objectType.
 * @author james
 *
 */
public class Issue
  extends ASObject {

  public static final class Builder 
    extends ASObject.AbstractBuilder<Issue, Builder> {

    Builder() {
      objectType("issue");
    }
    
    /**
     * Set the "types" property
     * @param type String
     * @param types String... optional vararg of additional types to set
     * @return Builder
     */
    public Builder types(String type, String... types) {
      ImmutableList.Builder<String> list = 
        ImmutableList.builder();
      if (type != null) 
        list.add(type);
      if (types != null)
        list.add(types);
      return types(list.build());
    }
    
    /**
     * Set the "types" property
     * @param types Iterable&lt;String>
     * @return Builder
     */
    public Builder types(Iterable<String> types) {
      return set("types", types);
    }
    
    /**
     * Get the built Issue object
     */
    public Issue get() {
      return new Issue(this);
    }
    
  }
  
  private Issue(Builder builder) {
    super(builder);
  }
  
  /**
   * Get the listing of types
   * @return Iterable&lt;String>
   */
  public Iterable<String> types() {
    return this.<Iterable<String>>get("types");
  }
  
  // Java Serialization Support `
  
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
