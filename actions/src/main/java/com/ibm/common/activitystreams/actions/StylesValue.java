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

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.ibm.common.activitystreams.TypeValue;
import com.ibm.common.activitystreams.util.AbstractDictionaryObject;

/**
 * The value of the "parameters" property... 
 * @author james
 * @version $Revision: 1.0 $
 */
public final class StylesValue
  extends AbstractDictionaryObject<String>
  implements Serializable {

  /**
   * Method make.
  
   * @return Builder */
  public static Builder make() {
    return new Builder();
  }
  
 
  /**
   * @author james
   * @version $Revision: 1.0 $
   */
  public static final class Builder 
    extends AbstractDictionaryObject.AbstractBuilder
      <String, StylesValue, Builder> {

    /**
     * Method media.
     * @param query String
     * @return Builder
     */
    public Builder media(String query) {
      return super.set("media", query);
    }
    
    /**
     * Method set.
    
    
    
     * @param name String
     * @param value String
     * @return Builder */
    public Builder set(String name, String value) {
      checkArgument(!name.equalsIgnoreCase("query"));
      return super.set(name, value);
    }
    
    /**
     * Method get.
    
    
     * @return ParametersValue * @see com.google.common.base.Supplier#get() */
    public StylesValue get() {
      return new StylesValue(this);
    }
    
  }
  
  /**
   * Constructor for ParametersValue.
   * @param builder Builder
   */
  StylesValue(Builder builder) {
    super(builder);
  }

  /**
   * Method media.
   * @return String
   */
  public String media() {
    return getSingle("media");
  }

  /**
   * Method get.
   * @param key String
   * @return String
   */
  public String get(String key) {
    return super.getSingle(key);
  }

  /**
   * Method get.
   * @param key String
   * @param defaultValue String
   * @return String
   */
  public String get(String key, String defaultValue) {
    return super.getSingle(key, defaultValue);
  }
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    implements Serializable {
    private static final long serialVersionUID = -1975376657749952999L;
    private ImmutableMap<String,String> map;
    SerializedForm(StylesValue obj) {
      ImmutableMap.Builder<String,String> builder = 
        ImmutableMap.builder();
      for (String key : obj)
        builder.put(key, obj.get(key));
      this.map = builder.build();
    }
  
    Object readResolve() 
      throws java.io.ObjectStreamException {
        StylesValue.Builder builder = 
          StylesValue.make();
        for (Map.Entry<String,String> entry : map.entrySet())
          builder.set(entry.getKey(),  entry.getValue());
        return builder.get();
    }
  }
}
