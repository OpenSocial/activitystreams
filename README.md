# Activity Streams 2.0 Reference Implementation

## Getting Started

Maven:
```xml
<dependency>
  <groupId>com.ibm.common</groupId>
  <artifactId>activitystreams</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

Dependencies:

* gson 2.2.4
* guava 16.0.1
* joda-time 2.3

## Creating an Activity statement

```java
import static com.ibm.common.activitystreams.Makers.activity;
import static com.ibm.common.activitystreams.Makers.object;

public class Example {

  public static void main(String... args) {

    Activity activity = 
     activity()
       .actor(object("person").id("acct:joe@example.org"))
       .object(object("note").content("my note"))
       .verb("post")
       .get();
   
  }
 
}
```

The library uses a consistent fluent generator pattern to construct all 
object types. Once created, objects are immutable.

## Serializing and Parsing objects

The library has one job: to make it easy to create and parse Activity 
Stream objects that are conformant to the Activity Streams 2.0 
specification.

The IO object is used to serialize and parse Activity Stream objects. 
IO objects are threadsafe and immutable so they can be safely created 
once and stored as a static final constant. 

```java
package com.ibm.common.activitystreams;

import static com.ibm.common.activitystreams.IO.makeDefaultPrettyPrint;
import static com.ibm.common.activitystreams.Makers.activity;
import static com.ibm.common.activitystreams.Makers.object;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Test {

  private static final IO io = makeDefaultPrettyPrint();
  
  public static void main(String... args) {

    Activity activity = 
     activity()
       .actor(object("person").id("acct:joe@example.org"))
       .object(object("note").content("my note"))
       .verb("post")
       .get();
    
    ByteArrayOutputStream out = 
      new ByteArrayOutputStream();
    
    // Write it out
    activity.writeTo(out, io);
    
    ByteArrayInputStream in = 
      new ByteArrayInputStream(out.toByteArray());
   
    // Read it in
    activity = io.readAsActivity(in);

    // Access the properties
    TypeValue tv = activity.verb();
    System.out.println(tv.id()); // "post"

    ASObject actor = activity.firstActor();
    System.out.println(actor.id()); // "acct:joe@example.org"

    for (ASObject object : activity.object())
      System.out.println(object.objectType().id()) // "note"
      
    // iterate all properties
    for (String key : activity)
      System.out.println(activity.get(key));
    
  }
 
}

```

