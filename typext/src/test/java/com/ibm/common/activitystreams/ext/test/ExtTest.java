package com.ibm.common.activitystreams.ext.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.Future;

import org.junit.Test;

import static com.ibm.common.activitystreams.Makers.type;

import com.google.common.util.concurrent.Monitor;
import com.ibm.common.activitystreams.Collection;
import com.ibm.common.activitystreams.IO;
import com.ibm.common.activitystreams.Makers;
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
