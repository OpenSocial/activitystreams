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

import com.google.common.net.MediaType;
import com.ibm.common.activitystreams.ASObject;

/**
 * The legacy "file" objectType
 * @author james
 */
public class File
  extends ASObject {

  public static final class Builder 
    extends ASObject.AbstractBuilder<File, Builder> {

    Builder() {
      objectType("file");
    }
    
    /**
     * Set the fileUrl property
     * @param url String
     * @return Builder
     */
    public Builder fileUrl(String url) {
      return set("fileUrl", url);
    }

    /**
     * Set the MIME mediaType using the legacy "mimeType" property 
     * name rather than the AS 2.0 "mediaType" property name
     */
    @Override
    public Builder mediaType(MediaType mt) {
      return set("mimeType", mt);
    }

    /**
     * Get the built File object
     */
    public File get() {
      return new File(this);
    }
    
  }
  
  private File(Builder builder) {
    super(builder);
  }

  /**
   * Get the MIME mediaType using the legacy "mimeType" property
   * name rather than the AS 2.0 "mediaType" property
   */
  @Override
  public MediaType mediaType() {
    return this.<MediaType>get("mimeType");
  }
  
  /**
   * Get the fileUrl property
   * @return
   */
  public String fileUrl() {
    return getString("fileUrl");
  }
  
  // Java Serialization Support
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<File> {
    private static final long serialVersionUID = -2060301713159936285L;
    protected SerializedForm(File obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return super.doReadResolve();
    }
    protected File.Builder builder() {
      return new Builder();
    }
  }
}
