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


/**
 */
public class HttpActionHandler 
  extends ActionHandler {
  
  /**
   * Method makeHttpActionHandler.
   * @return Builder
   */
  public static Builder makeHttpActionHandler() {
    return new Builder();
  }
  
  /**
   */
  public static final class Builder 
    extends AbstractBuilder<HttpActionHandler, Builder> {

    public Builder() {
      super("HttpActionHandler");
    }
    
    /**
     * Method actualGet.
     * @return HttpActionHandler
     */
    protected HttpActionHandler actualGet() {
      return new HttpActionHandler(this);
    }
    
  }
  
  /**
   */
  @SuppressWarnings("unchecked")
  public static abstract class AbstractBuilder
    <A extends HttpActionHandler, B extends AbstractBuilder<A,B>>
    extends ActionHandler.Builder<A,B> {
      /**
       * Constructor for AbstractBuilder.
       * @param objectType String
       */
      protected AbstractBuilder(String objectType) {
        objectType(objectType);
      }
      
      /**
       * Method method.
       * @param method String
       * @return B
       */
      public B method(String method) {
        set("method", method);
        return (B)this;
      }
      
      /**
       * Method target.
       * @param target String
       * @return B
       */
      public B target(String target) {
        set("target", target);
        return (B)this;
      }
  }
  
  /**
   * Constructor for HttpActionHandler.
   * @param builder AbstractBuilder<?,?>
   */
  protected HttpActionHandler(AbstractBuilder<?,?> builder) {
    super(builder);
  }
  
  /**
   * Method method.
   * @return String
   */
  public String method() {
    return this.getString("method");
  }
  
  /**
   * Method target.
   * @return String
   */
  public String target() {
    return has("target") ? 
      this.<String>get("target") : 
      ActionMakers.TARGET_DEFAULT;
  }
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<HttpActionHandler> {
    private static final long serialVersionUID = -2060301713159936285L;
    protected SerializedForm(HttpActionHandler obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return super.doReadResolve();
    }
    protected HttpActionHandler.Builder builder() {
      return ActionMakers.httpAction();
    }
  }
}
