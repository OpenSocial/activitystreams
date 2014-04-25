package com.ibm.common.activitystreams.ext;

import java.util.concurrent.Future;

import static com.ibm.common.activitystreams.Makers.type;
import com.ibm.common.activitystreams.TypeValue;
import com.ibm.common.activitystreams.registry.TypeValueRegistry;

public class Test {

  public static void main(String... args) throws Exception {
  
    TypeValueRegistry reg = 
      TypeValueRegistry
        .makeDefaultSilent();
    
    Future<TypeValue> tv = 
      reg.resolveNoWait(type("post"));
    System.out.println(tv.get().valueType());
    
  }
  
}
