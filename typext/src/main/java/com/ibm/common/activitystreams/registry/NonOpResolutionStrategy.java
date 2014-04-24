package com.ibm.common.activitystreams.registry;

import java.util.concurrent.Callable;

import com.ibm.common.activitystreams.TypeValue;

class NonOpResolutionStrategy 
  implements ResolutionStrategy {
  
  public Callable<TypeValue> resolverFor(final TypeValue tv) {
    return new Callable<TypeValue>() {
      public TypeValue call() throws Exception {
        return tv;
      }
    };
  }

  public Receiver<TypeValue> preloader() {
    return new Receiver<TypeValue>() {
      public void receive(TypeValue t) {} 
    };
  }

  public void shutdown() {}
}
