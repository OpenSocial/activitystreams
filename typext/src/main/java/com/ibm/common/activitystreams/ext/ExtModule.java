package com.ibm.common.activitystreams.ext;

import com.ibm.common.activitystreams.IO;
import com.ibm.common.activitystreams.internal.Model;
import com.ibm.common.activitystreams.internal.Schema;
import com.ibm.common.activitystreams.internal.Schema.Builder;
import com.ibm.common.activitystreams.util.Module;

public class ExtModule implements Module {

  public static Module instance = new ExtModule();
  
  public static final Model verb = 
    Schema.object.template()
      .type(Verb.class, Verb.Builder.class)
      .get();
  
  public static final Model objectType = 
    Schema.object.template()
      .type(ObjectType.class, ObjectType.Builder.class)
      .get();
  
  public void apply(Builder builder) {
    builder.map("verb", verb);
    builder.map("objectType", objectType);
  }

  public void apply(
    IO.Builder builder,
    Schema schema) {
      ExtAdapter base = new ExtAdapter(schema);
      builder.adapter(Verb.class, base);
      builder.adapter(ObjectType.class, base);
  }

}
