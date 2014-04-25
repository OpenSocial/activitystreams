package com.ibm.common.activitystreams.registry;

import java.util.concurrent.Callable;

import com.ibm.common.activitystreams.TypeValue;

/**
 * Implements a strategy for resolving TypeValue identifiers.
 */
public interface ResolutionStrategy {

  /**
   * Returns the Receiver instance that is used to 
   * process preloaded TypeValue instances
   * @return Receiver&lt;TypeValue>
   */
  Receiver<TypeValue> preloader();
  
  /**
   * Returns a Callable that implements TypeValue resolution 
   * for the given TypeValue.
   */
  Callable<TypeValue> resolverFor(TypeValue tv);
  
  /**
   * Shutdown and cleanup any resources
   */
  void shutdown();
  
  public static final ResolutionStrategy nonop = 
    new NonOpResolutionStrategy();
}
