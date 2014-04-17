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
