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

import java.io.ObjectStreamException;

import com.google.common.base.Supplier;
import com.ibm.common.activitystreams.ASObject;

/**
 */
public final class UrlTemplate 
  extends ASObject
  implements ParameterValue {

  /**
   * Method makeUrlTemplate.
   * @return Builder
   */
  public static Builder makeUrlTemplate() {
    return new Builder();
  }
  
  /**
   */
  public static final class Builder 
    extends ASObject.AbstractBuilder<UrlTemplate, Builder> {

    private final ParametersValue.Builder params = 
      ParametersValue.make();
    
    public Builder() {
      writeUsing(ActionMakers.io);
      objectType("UrlTemplate");
    }
    
    public Builder template(String template) {
      set("template", template);
      return this;
    }

    public Builder parameter(String name, String iri) {
      params.param(name, iri);
      return this;
    }
    
    public Builder parameter(
      String name, 
      ParameterValue parameter) {
        params.param(name, parameter);
        return this;
    }
    
    public Builder parameter(
      String name, 
      Supplier<? extends ParameterValue> parameter) {
        return parameter(name, parameter.get());
    }
        
    /**
     * Method get.
     * @return UrlTemplate
     * @see com.google.common.base.Supplier#get()
     */
    public UrlTemplate get() {
      if (params.notEmpty())
        set("parameters", params.get());
      return new UrlTemplate(this);
    }
    
  }
  
  /**
   * Constructor for UrlTemplate.
   * @param builder Builder
   */
  private UrlTemplate(Builder builder) {
    super(builder);
  }
  
  /**
   * Method parameters.
   * @return ParametersValue
   */
  public ParametersValue parameters() {
    return this.<ParametersValue>get("parameters");
  }
  
  /**
   * Method template.
   * @return String
   */
  public String template() {
    return this.getString("template");
  }
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<UrlTemplate> {
    private static final long serialVersionUID = -2060301713159936285L;
    protected SerializedForm(UrlTemplate obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return super.doReadResolve();
    }
    protected UrlTemplate.Builder builder() {
      return ActionMakers.urlTemplate();
    }
  }
}
