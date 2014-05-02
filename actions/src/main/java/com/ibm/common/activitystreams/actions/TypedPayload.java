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

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.net.MediaType;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.LinkValue;
import com.ibm.common.activitystreams.TypeValue;

public final class TypedPayload 
  extends ASObject
  implements ParameterValue {

  /**
   * Method makeTypedPayload.
   * @param mediaType MediaType
   * @return Builder
   */
  public static Builder makeTypedPayload(MediaType mediaType) {
    return new Builder().mediaType(mediaType);
  }
  
  /**
   * Method makeTypedPayload.
   * @param mediaType String
   * @return Builder
   */
  public static Builder makeTypedPayload(String mediaType) {
    return new Builder().mediaType(mediaType);
  }
  
  public static Builder make() {
    return new Builder();
  }
  
  /**
   */
  public static final class Builder 
    extends ASObject.AbstractBuilder<TypedPayload, Builder> {

    public Builder() {
      writeUsing(ActionMakers.io);
      objectType("TypedPayload");
    }

    /**
     * Method schema.
     * @param iri String
     * @return Builder
     */
    public Builder schema(String iri) {
      return schema(LinkValue.SimpleLinkValue.make(iri));
    }
    
    /**
     * Method schema.
     * @param lv LinkValue
     * @return Builder
     */
    public Builder schema(LinkValue lv) {
      return link("schema", lv);
    }
    
    /**
     * Method schema.
     * @param lv Supplier<? extends LinkValue>
     * @return Builder
     */
    public Builder schema(Supplier<? extends LinkValue> lv) {
      return schema(lv.get());
    }
    
    /**
     * Method type.
     * @param iri String
     * @return Builder
     */
    public Builder type(String iri) {
      return type(TypeValue.SimpleTypeValue.make(iri));
    }
    
    /**
     * Method type.
     * @param tv TypeValue
     * @return Builder
     */
    public Builder type(TypeValue tv) {
      return set("type", tv);
    }
    
    /**
     * Method type.
     * @param tv Supplier<? extends TypeValue>
     * @return Builder
     */
    public Builder type(Supplier<? extends TypeValue> tv) {
      return type(tv.get());
    }
    
    /**
     * Method get.
     * @return TypedPayload
     * @see com.google.common.base.Supplier#get()
     */
    public TypedPayload get() {
      return new TypedPayload(this);
    }
    
  }
  
  /**
   * Constructor for TypedPayload.
   * @param builder Builder
   */
  private TypedPayload(Builder builder) {
    super(builder);
  }
  
  /**
   * Method schema.
   * @return L
   */
  public Iterable<LinkValue> schema() {
    return this.links("schema");
  }
  
  public Iterable<LinkValue> schema(Predicate<? super LinkValue> filter) {
    return this.links("schema", filter);
  }
  
  /**
   * Method type.
   * @return TypeValue
   */
  public TypeValue type() {
    return this.<TypeValue>get("type");
  }
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<TypedPayload> {
    private static final long serialVersionUID = -2060301713159936285L;
    private String mediaType;
    protected SerializedForm(TypedPayload obj) {
      super(obj);
      this.mediaType = obj.mediaType().toString();
    }
    Object readResolve() throws ObjectStreamException {
      return super.doReadResolve();
    }
    protected TypedPayload.Builder builder() {
      return ActionMakers.typedPayload(mediaType);
    }
  }
}
