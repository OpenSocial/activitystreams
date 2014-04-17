/**
 * Copyright 2013 OpenSocial Foundation
 * Copyright 2013 International Business Machines Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Utility library for working with Activity Streams Actions
 * Requires underscorejs.
 *
 * @author James M Snell (jasnell@us.ibm.com)
 */
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
