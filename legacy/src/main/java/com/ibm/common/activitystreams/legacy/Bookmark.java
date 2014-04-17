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

import com.ibm.common.activitystreams.ASObject;

/**
 * The legacy "bookmark" objectType
 * @author james
 */
public class Bookmark
  extends ASObject {

  public static final class Builder 
    extends ASObject.AbstractBuilder<Bookmark, Builder> {

    Builder() {
      objectType("bookmark");
    }
    
    /**
     * Set the targetUrl property
     * @param url String
     * @return Builder
     */
    public Builder targetUrl(String url) {
      return set("targetUrl", url);
    }

    /**
     * Get the built Bookmark object
     */
    public Bookmark get() {
      return new Bookmark(this);
    }
    
  }
  
  private Bookmark(Builder builder) {
    super(builder);
  }
  
  /**
   * Get the targetUrl property
   * @return String
   */
  public String targetUrl() {
    return getString("targetUrl");
  }
  
  // Java Serialization Support
  
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
