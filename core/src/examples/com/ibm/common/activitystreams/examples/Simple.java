package com.ibm.common.activitystreams.examples;

import static com.ibm.common.activitystreams.IO.makeDefaultPrettyPrint;
import static com.ibm.common.activitystreams.Makers.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.ibm.common.activitystreams.Activity;
import com.ibm.common.activitystreams.IO;

/**
 * @author james
 * @version $Revision: 1.0 $
 */
public final class Simple {
  
  // The IO object handles all of the reading and writing of the object
  private static final IO io = makeDefaultPrettyPrint();
  
  private Simple() {}
  
  /**
   * Method main.
   * @param args String[]
  
   * @throws Exception */
  public static void main(String... args) throws Exception {
    
    // Demonstrates the creation and parsing of a simple Activity Object
    

    // Create the Activity... The API uses a Fluent Generator pattern
    Activity activity = 
      activity()
        .verb("post")
        .actor("acct:joe@example.org")
        .object("http://example.net/posts/1")
        .get();
    
    
    // The Activity object is immutable...
    System.out.println(activity.verb());
    System.out.println(activity.actor());
    System.out.println(activity.object());
    
    // let's write it out to our outputstream
    ByteArrayOutputStream out = 
      new ByteArrayOutputStream();
    activity.writeTo(out, io);
    
    // now let's parse it back in
    ByteArrayInputStream in = 
      new ByteArrayInputStream(
        out.toByteArray());
    
    activity = io.readAsActivity(in);
    
    // We get back the same thing...
    System.out.println(activity.verb());
    System.out.println(activity.actor());
    System.out.println(activity.object());
    
    // If you want to see what was serialized, 
    // simply write out to stdout...
    activity.writeTo(System.out, io);
    
    
  }
  
}
