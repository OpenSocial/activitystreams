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
import com.ibm.common.activitystreams.TypeValue;
import com.ibm.common.activitystreams.ValueType;
import com.ibm.common.activitystreams.util.AbstractDictionaryObject;

/**
 * The value of the "parameters" property... 
 * @author james
 * @version $Revision: 1.0 $
 */
public final class ParametersValue
  extends AbstractDictionaryObject<TypeValue>
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
      <TypeValue,ParametersValue,Builder> {

    /**
     * Method set.
     * @param param String
     * @param iri String
    
     * @return Builder */
    public Builder set(String param, String iri) {
      return super.set(
        param, 
        TypeValue.SimpleTypeValue.make(iri));
    }
    
    /**
     * Method get.
    
    
     * @return ParametersValue * @see com.google.common.base.Supplier#get() */
    public ParametersValue get() {
      return new ParametersValue(this);
    }
    
  }
  
  /**
   * Constructor for ParametersValue.
   * @param builder Builder
   */
  ParametersValue(Builder builder) {
    super(builder);
  }
  
  /**
   * Method get.
   * @param param String
  
   * @return TypeValue */
  @SuppressWarnings("unchecked")
  public <T extends TypeValue>T get(String param) {
    return (T)super.getSingle(param);
  }
  
  /**
   * Method id.
   * @param param String
   * @return String
   */
  public String id(String param) {
    TypeValue tv = get(param);
    return tv != null ? tv.id() : null;
  }
  
  /**
   * Method required.
   * @param param String
   * @return boolean
   */
  public boolean required(String param) {
    TypeValue tv = get(param);
    if (tv == null) 
      return false;
    if (tv.valueType() == ValueType.SIMPLE)
      return true;
    ASObject obj = (ASObject) tv;
    return obj.getBoolean("required", true);
  }
  
  /**
   * Method value.
   * @param param String
   * @param defaultValue O
   * @return O
   */
  public <O>O value(String param, O defaultValue) {
    TypeValue tv = get(param);
    if (tv == null || tv.valueType() == ValueType.SIMPLE)
      return defaultValue;
    ASObject obj = (ASObject) tv;
    return obj.<O>get("value", defaultValue);
  }
  
  /**
   * Method value.
   * @param param String
   * @return O
   */
  public <O>O value(String param) {
    return value(param,null);
  }
 
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    implements Serializable {
    private static final long serialVersionUID = -1975376657749952999L;
    private ImmutableMap<String,Object> map;
    SerializedForm(ParametersValue obj) {
      ImmutableMap.Builder<String,Object> builder = 
        ImmutableMap.builder();
      for (String key : obj)
        builder.put(key, obj.get(key));
      this.map = builder.build();
    }

    Object readResolve() 
      throws java.io.ObjectStreamException {
        ParametersValue.Builder builder = 
          ParametersValue.make();
        for (Map.Entry<String,Object> entry : map.entrySet())
          builder.set(entry.getKey(), (TypeValue) entry.getValue());
        return builder.get();
    }
  }
}
