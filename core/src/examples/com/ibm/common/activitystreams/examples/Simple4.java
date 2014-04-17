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

/**
 * The Makers class includes a bunch of static generator 
 * methods that are easiest to use when imported statically
 */
import static com.ibm.common.activitystreams.Activity.Audience.CC;
import static com.ibm.common.activitystreams.Activity.Audience.TO;
import static com.ibm.common.activitystreams.Makers.activity;
import static com.ibm.common.activitystreams.Makers.nlv;
import static com.ibm.common.activitystreams.Makers.object;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.ibm.common.activitystreams.Activity;
import com.ibm.common.activitystreams.IO;

/**
 * @author james
 * @version $Revision: 1.0 $
 */
public final class Simple4 {

  private Simple4() {}
  
  /**
   * Method main.
   * @param args String[]
  
   * @throws Exception */
  public static void main(String... args) throws Exception {
    
    // Demonstrates the creation and parsing of a simple Activity Object
    
    // (we'll use this to store the output...)
    ByteArrayOutputStream out = 
      new ByteArrayOutputStream();
    
    // The IO object handles all of the reading and writing of the object
    IO io = IO.make().prettyPrint().get();
    
    // Create the Activity... The API uses a Fluent Generator pattern
    Activity activity = 
      activity()
        .verb("post")
        .actor(
          object()
            .objectType(
              object()
                .id("http://schema.example.net/Person")
                .displayName("Person")
                .alias("person"))
            .id("acct:joe@example.com")
            .displayName("Joe Smith")
        )
        .object(
          object("note")
            .id("http://example.net/posts/1")
            .title(
              nlv()
                .set("en", "This is the title")
                .set("fr", "C'est le titre"))
          )
        .audience(TO, 
          "urn:social:everyone", 
          "acct:mary@example.net")
        .audience(CC, 
          "urn:social:extended")
        .action("embed", "http://xml.example.org/foo")
        .pending()
        .get();
    
    // The Activity object is immutable...
    System.out.println(activity.verb());
    System.out.println(activity.actor());
    System.out.println(activity.object());
    System.out.println(activity.status());
    
    // let's write it out to our outputstream
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
    System.out.println(activity.status());
    
    // If you want to see what was serialized, 
    // simply write out to stdout...
    activity.writeTo(System.out, io);
  }
  
}
