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
package com.ibm.common.activitystreams.actions;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.util.AbstractDictionaryObject;

/**
 */
public final class Authentication 
  extends AbstractDictionaryObject<ASObject> {

  /**
   * Method make.
   * @return Builder
   */
  public static Builder make() {
    return new Builder();
  }
  
  /**
   */
  public static final class Builder
    extends AbstractDictionaryObject.AbstractBuilder<
      ASObject,Authentication,Builder> {

    /**
     * Method get.
     * @return Authentication
     * @see com.google.common.base.Supplier#get()
     */
    public Authentication get() {
      return new Authentication(this);
    }
      
  }
  
  /**
   * Constructor for Authentication.
   * @param builder Builder
   */
  protected Authentication(Builder builder) {
    super(builder);
  }

  /**
   * Method get.
   * @param key String
   * @return A
   */
  @SuppressWarnings("unchecked")
  public <A extends ASObject>A get(String key) {
    return (A)this.getSingle(key);
  }
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    implements Serializable {
    private static final long serialVersionUID = -1975376657749952999L;
    private ImmutableMap<String,Object> map;
    SerializedForm(Authentication obj) {
      ImmutableMap.Builder<String,Object> builder = 
        ImmutableMap.builder();
      for (String key : obj)
        builder.put(key, obj.get(key));
      this.map = builder.build();
    }

    Object readResolve() 
      throws java.io.ObjectStreamException {
        Authentication.Builder builder = 
          Authentication.make();
        for (Map.Entry<String,Object> entry : map.entrySet())
          builder.set(entry.getKey(), (ASObject) entry.getValue());
        return builder.get();
    }
  }
}
