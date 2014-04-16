package com.ibm.common.activitystreams.util;

import com.ibm.common.activitystreams.IO;
import com.ibm.common.activitystreams.internal.Schema;

public interface Module {

  void apply(Schema.Builder builder);
  
  void apply(IO.Builder builder, Schema schema);
  
}
