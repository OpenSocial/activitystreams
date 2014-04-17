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

import static com.ibm.common.activitystreams.Makers.type;
import static com.ibm.common.activitystreams.Makers.object;

import java.io.ObjectStreamException;

import com.google.common.base.Supplier;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.TypeValue;

/**
 */
public final class UrlTemplate 
  extends ASObject {

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
    
    private Builder() {
      objectType("UrlTemplate");
    }
    
    /**
     * Method template.
     * @param template String
     * @return Builder
     */
    public Builder template(String template) {
      set("template", template);
      return this;
    }

    /**
     * Method parameter.
     * @param name String
     * @param iri String
     * @return Builder
     */
    public Builder parameter(String name, String iri) {
      params.set(name, type(iri));
      return this;
    }
    
    /**
     * Method parameter.
     * @param name String
     * @param iri String
     * @param required boolean
     * @return Builder
     */
    public Builder parameter(
      String name, 
      String iri, 
      boolean required) {
      return parameter(
        name, 
        object()
          .id(iri)
          .set("required", required));
    }
    
    /**
     * Method parameter.
     * @param name String
     * @param iri String
     * @param required boolean
     * @param value Object
     * @return Builder
     */
    public Builder parameter(
      String name, 
      String iri, 
      boolean required, 
      Object value) {
      return parameter(
        name,
        object()
          .id(iri)
          .set("required", required)
          .set("value", value));
    }
    
    /**
     * Method parameter.
     * @param name String
     * @param lv TypeValue
     * @return Builder
     */
    public Builder parameter(
      String name, 
      TypeValue lv) {
      params.set(name, lv);
      return this;
    }
    
    /**
     * Method parameter.
     * @param name String
     * @param lv Supplier<? extends TypeValue>
     * @return Builder
     */
    public Builder parameter(
      String name, 
      Supplier<? extends TypeValue> lv) {
      return parameter(name, lv.get());
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
