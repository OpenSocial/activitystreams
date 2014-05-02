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
import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.ibm.common.activitystreams.util.AbstractWritable;

/**
 * The value of the "parameters" property... 
 * @author james
 * @version $Revision: 1.0 $
 */
public final class ParametersValue
  extends AbstractWritable
  implements Serializable, Iterable<String> {

  public static Builder make() {
    return new Builder();
  }
 
  /**
   * @author james
   * @version $Revision: 1.0 $
   */
  public static final class Builder 
    extends AbstractWritable.AbstractWritableBuilder
      <ParametersValue,Builder> {

    private final Map<String,Object> params = 
      Maps.newHashMap();
    
    public Builder() {
      writeUsing(ActionMakers.io);
    }
    
    public boolean notEmpty() {
      return !params.isEmpty();
    }
    
    public Builder param(String param, String iri) {
      params.put(param, ActionMakers.parameter(iri));
      return this;
    }
    
    public Builder param(
      String param, 
      ParameterValue parameter) {
      params.put(param, parameter);
      return this;
    }
    
    public Builder param(
      String param, 
      Supplier<? extends ParameterValue> parameter) {
      params.put(param, parameter);
      return this;
    }
    
    public ParametersValue get() {
      return new ParametersValue(this);
    }
    
  }
  
  private final ImmutableMap<String,Object> params;
  
  /**
   * Constructor for ParametersValue.
   * @param builder Builder
   */
  ParametersValue(Builder builder) {
    super(builder);
    this.params = ImmutableMap.copyOf(builder.params);
  }
  
  public Iterator<String> iterator() {
    return params.keySet().iterator();
  }

  @SuppressWarnings("unchecked")
  public <O extends ParameterValue>O get(String param) {
    return (O)params.get(param);
  }
  
  public boolean has(String param) {
    return params.containsKey(param);
  }
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    implements Serializable {
    private static final long serialVersionUID = -1975376657749952999L;
    private ImmutableMap<String,Object> map;
    SerializedForm(ParametersValue obj) {
      map = obj.params;
    }

    Object readResolve() 
      throws java.io.ObjectStreamException {
        ParametersValue.Builder builder = 
          ParametersValue.make();
        builder.params.putAll(map);
        return builder.get();
    }
  }
}
