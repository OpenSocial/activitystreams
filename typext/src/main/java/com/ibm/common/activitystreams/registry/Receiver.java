package com.ibm.common.activitystreams.registry;

public interface Receiver<T> {

  void receive(T t);
  
}
