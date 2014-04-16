package com.ibm.common.activitystreams.examples;

import static com.ibm.common.activitystreams.Makers.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.ibm.common.activitystreams.Activity;

/**
 * @author james
 * @version $Revision: 1.0 $
 */
public final class Simple5 {
  
  private Simple5() {}
  
  /**
   * Method main.
   * @param args String[]
  
   * @throws Exception */
  public static void main(String... args) throws Exception {

    // Create the Activity... The API uses a Fluent Generator pattern
    Activity activity = 
      activity()
        .verb("post")
        .actor("acct:joe@example.org")
        .object("http://example.net/posts/1")
        .action("like", "http://example.org/actions/like")
        .updatedNow()
        .get();
    
    
    // The Activity object is immutable...
    System.out.println(activity.verb());
    System.out.println(activity.actor());
    System.out.println(activity.object());
    
    // Activity objects are serializable
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(out);
    oos.writeObject(activity);
    
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(in);
    
    activity = (Activity) ois.readObject();
    
    System.out.println(activity.verb());
    System.out.println(activity.actor());
    System.out.println(activity.object());
    System.out.println(activity.actions().get("like"));
    System.out.println(activity.updated());
  }
  
}
