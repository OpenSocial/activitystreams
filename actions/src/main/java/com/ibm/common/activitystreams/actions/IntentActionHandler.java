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
public class IntentActionHandler 
  extends ActionHandler {
  
  /**
   * Method makeIntentActionHandler.
   * @return Builder
   */
  public static Builder makeIntentActionHandler() {
    return new Builder();
  }
  
  /**
   */
  public static final class Builder 
    extends AbstractBuilder<IntentActionHandler, Builder> {

    public Builder() {
      super("IntentActionHandler");
    }
    
    /**
     * Method actualGet.
     * @return IntentActionHandler
     */
    protected IntentActionHandler actualGet() {
      return new IntentActionHandler(this);
    }
    
  }
  
  /**
   */
  public static abstract class AbstractBuilder
    <A extends IntentActionHandler, B extends AbstractBuilder<A,B>>
    extends ActionHandler.Builder<A,B> {
      /**
       * Constructor for AbstractBuilder.
       * @param objectType String
       */
      protected AbstractBuilder(String objectType) {
        objectType(objectType);
      }
  }
  
  /**
   * Constructor for IntentActionHandler.
   * @param builder AbstractBuilder<?,?>
   */
  protected IntentActionHandler(AbstractBuilder<?,?> builder) {
    super(builder);
  }

  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<IntentActionHandler> {
    private static final long serialVersionUID = -2060301713159936285L;
    protected SerializedForm(IntentActionHandler obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return super.doReadResolve();
    }
    protected IntentActionHandler.Builder builder() {
      return ActionMakers.intentAction();
    }
  }
}
