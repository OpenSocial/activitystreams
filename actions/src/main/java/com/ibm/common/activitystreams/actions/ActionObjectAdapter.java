package com.ibm.common.activitystreams.actions;

import java.lang.reflect.Type;

import com.google.common.collect.ImmutableSet;
import com.ibm.common.activitystreams.ASObject.AbstractBuilder;
import com.ibm.common.activitystreams.internal.ASObjectAdapter;
import com.ibm.common.activitystreams.internal.Model;
import com.ibm.common.activitystreams.internal.Schema;

public final class ActionObjectAdapter 
  extends ASObjectAdapter {

  ActionObjectAdapter(Schema schema) {
    super(schema);
  }

  private static final ImmutableSet<Class<?>> knownTypes = 
    ImmutableSet.<Class<?>>of(
      HttpActionHandler.class,
      IntentActionHandler.class,
      EmbedActionHandler.class,
      HtmlForm.class,
      UrlTemplate.class,
      TypedPayload.class
    );

  @Override
  protected boolean knowsType(Type type) {
    if (super.knowsType(type))
      return true;
    return knownTypes.contains(type);
  }

  @Override
  protected AbstractBuilder<?, ?> builderFor(Type type) {
    if (super.knowsType(type))
      return super.builderFor(type);
    if (type == HttpActionHandler.class)
      return ActionMakers.httpAction();
    else if (type == IntentActionHandler.class)
      return ActionMakers.intentAction();
    else if (type == EmbedActionHandler.class)
      return ActionMakers.embedAction();
    else if (type == HtmlForm.class)
      return ActionMakers.htmlForm();
    else if (type == UrlTemplate.class)
      return ActionMakers.urlTemplate();
    else if (type == TypedPayload.class)
      return ActionMakers.typedPayload();
    else return null;
  }

  @Override
  protected Model modelFor(Type type) {
    if (super.knowsType(type))
      return super.modelFor(type);
    if (type == HttpActionHandler.class)
      return schema().forObjectClassOrType(
        HttpActionHandler.Builder.class,  
        "HttpActionHandler");
    else if (type == IntentActionHandler.class)
      return schema().forObjectClassOrType(
        IntentActionHandler.Builder.class,  
        "IntentActionHandler");
    else if (type == EmbedActionHandler.class)
      return schema().forObjectClassOrType(
        EmbedActionHandler.Builder.class,  
        "EmbedActionHandler");
    else if (type == HtmlForm.class)
      return schema().forObjectClassOrType(
        HtmlForm.Builder.class, 
        "HtmlForm");
    else if (type == UrlTemplate.class)
      return schema().forObjectClassOrType(
        UrlTemplate.Builder.class, 
        "UrlTemplate");
    else if (type == TypedPayload.class)
      return schema().forObjectClassOrType(
        TypedPayload.Builder.class,
        "TypedPayload");
    else return null;
  }
  
}
