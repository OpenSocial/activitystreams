package com.ibm.common.activitystreams.ext.test;

import java.util.concurrent.Future;

import org.junit.Test;

import com.ibm.common.activitystreams.IO;
import com.ibm.common.activitystreams.TypeValue;
import com.ibm.common.activitystreams.ext.ExtModule;
import com.ibm.common.activitystreams.registry.TypeValueRegistry;

public class ExtTest {

  private static final IO io = IO.makeDefault(ExtModule.instance);
  
  @Test
  public void extTest() throws Exception {
    
    TypeValueRegistry tvr = 
      TypeValueRegistry
        .makeDefaultSilent(io);
    
    Future<TypeValue> object = tvr.resolve("urn:example:verbs:foo");
    
    System.out.println(object.get().valueType());
  }
  
}
