package com.ibm.common.activitystreams.registry;

import com.ibm.common.activitystreams.IO;
import com.ibm.common.activitystreams.TypeValue;

/**
 * Preloads TypeValue instances.
 */
public interface PreloadStrategy {

  void load(IO io, Receiver<TypeValue> receiver);
  
}
