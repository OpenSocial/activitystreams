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

import static com.ibm.common.activitystreams.actions.Adapters.AUTH;
import static com.ibm.common.activitystreams.actions.Adapters.STYLES;

import com.ibm.common.activitystreams.IO.Builder;
import com.ibm.common.activitystreams.internal.Model;
import com.ibm.common.activitystreams.internal.Schema;
import com.ibm.common.activitystreams.util.Module;

public final class ActionsModule 
  implements Module {

  public static final Module instance = new ActionsModule();
  
  @Override
  public void apply(Builder builder, Schema schema) {
    
    ActionObjectAdapter base = 
      new ActionObjectAdapter(schema);
    
    // Register Adapters
    builder.hierarchicalAdapter(ActionHandler.class,base)
           .hierarchicalAdapter(HttpActionHandler.class,base)
           .hierarchicalAdapter(IntentActionHandler.class,base)
           .hierarchicalAdapter(EmbedActionHandler.class,base)
           .hierarchicalAdapter(HtmlForm.class,base)
           .hierarchicalAdapter(ParametersValue.class, ParametersAdapter.instance)
           .hierarchicalAdapter(ParameterValue.class, ParameterAdapter.instance)
           .hierarchicalAdapter(UrlTemplate.class,base)
           .hierarchicalAdapter(TypedPayload.class,base)
           .hierarchicalAdapter(Authentication.class, AUTH)
           .hierarchicalAdapter(StylesValue.class, STYLES);
  }

  @Override
  public void apply(Schema.Builder builder) {
    builder.map("HtmlForm", withParameters.template(HtmlForm.class, HtmlForm.Builder.class))
      .map("TypedPayload", typedPayload)
      .map("UrlTemplate", withParameters.template(UrlTemplate.class, UrlTemplate.Builder.class))
      .map("HttpActionHandler", actionHandler.template(HttpActionHandler.class, HttpActionHandler.Builder.class))
      .map("IntentActionHandler", actionHandler.template(IntentActionHandler.class, IntentActionHandler.Builder.class))
      .map("EmbedActionHandler", actionHandler.template(EmbedActionHandler.class, EmbedActionHandler.Builder.class));
  }

  public final static Model actionHandler = 
      Model
        .make("object")
        .linkValue(
          "expects", 
          "returns",
          "requires",
          "prefers")
        .object("context")
        .as("auth", Authentication.class)
        .as("style", StylesValue.class)
        .get();
    
    
    public final static Model withParameters = 
      Model
        .make("object")
        .as("parameters", ParametersValue.class)
        .get();
    
    public final static Model typedPayload =
      Model
        .make("object")
        .type(TypedPayload.class, TypedPayload.Builder.class)
        .linkValue("schema")
        .typeValue("type")
        .get();
        

    
}
