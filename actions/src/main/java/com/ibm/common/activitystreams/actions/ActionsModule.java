package com.ibm.common.activitystreams.actions;

import static com.ibm.common.activitystreams.actions.Adapters.AUTH;
import static com.ibm.common.activitystreams.actions.Adapters.PARAMETERS;
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
           .hierarchicalAdapter(UrlTemplate.class,base)
           .hierarchicalAdapter(TypedPayload.class,base)
           .hierarchicalAdapter(Parameter.class,base)
           .hierarchicalAdapter(ParametersValue.class, PARAMETERS)
           .hierarchicalAdapter(Authentication.class, AUTH)
           .hierarchicalAdapter(StylesValue.class, STYLES);
  }

  @Override
  public void apply(Schema.Builder builder) {
    builder.map("HtmlForm", withParameters.template(HtmlForm.class, HtmlForm.Builder.class))
      .map("parameter", parameter)
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
        .linkValue("schema")
        .typeValue("type")
        .get();
        
    public final static Model parameter = 
      Model
        .make("object")
        .typeValue("type")
        .type(Parameter.class, Parameter.Builder.class)
        .get();
    
}
