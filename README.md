# Activity Streams 2.0 Reference Implementation

## Getting Started

Maven:

Use Maven to build.

- mvn compile
- mvn install 
- mvn -f assembly assembly:assembly

```xml
<dependency>
  <groupId>com.ibm.common</groupId>
  <artifactId>activitystreams</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

Dependencies:

- gson 2.2.4
- guava 16.0.1
- joda-time 2.3

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

All Activity Stream objects support a variety of writeTo methods. These
serialize those objects as JSON to either an OutputStream, Writer or a
String. You can choose to use the default IO instance or pass in an IO.
Alternatively, you can use the write methods on the IO object itself to
serialize. 

## Makers and Builders

All objects use a fluent generator pattern. That is, you use a builder 
object to construct immutable instances of an object. You use Makers 
classes to create instances of the builders.

```java
  import com.ibm.common.activitystreams.Makers;
  import com.ibm.common.activitystreams.ASObject;
  
  //... the long way...
  
  ASObject.Builder builder = Makers.object();

  builder.id("urn:example:1")
         .displayName("foo")
         .objectType("thing");
         
  ASObject obj = builder.get();
```

By leveraging the fluent API pattern, the example above can be written as:

```java
  import static com.ibm.common.activitystreams.Makers.object;
  import com.ibm.common.activitystreams.ASObject;
  
  ASObject obj = 
    object("thing")
      .id("urn:example:1")
      .displayName("foo")
      .get();
```

Here's a slightly more complex example:

```java
  import static com.ibm.common.activitystreams.Makers.activity;
  import static com.ibm.common.activitystreams.Makers.object;
  import static com.ibm.common.activitystreams.Activity;

    Activity activity = 
      activity()
        .verb("post")
        .actor(
          object("person")
            .id("acct:joe@example.com")
            .displayName("Joe Smith")
        )
        .object(
          object("note")
            .id("http://example.net/posts/1")
            .title("This is the title"))
        .get();
```

Here, we first create an Activity.Builder object (using "activity()"). We 
then set the verb to "post" and set a new "person" object as the actor.
Following that, we create a new "note" object and set it as the object
of the activity. Finally, we call get() to create the finished Activity object.

## Using Modules

A Module is a package collection of extensions to the Activity Streams 2.0
data model. The reference implementation currently provides modules for 
Action Handlers, GeoJSON and Legacy Activity Streams 1.0 objectTypes.

Modules are registered when the IO object is created. For example, to 
use the Actions module:

```java
  import static com.ibm.common.activitystreams.actions.ActionMakers.*;
  import static com.ibm.common.activitystreams.Makers.*;
  import com.ibm.common.activitystreams.actions.ActionsModule;
  import com.ibm.common.activitystreams.IO;

  //...

  public static final IO io = 
    IO.makeDefaultPrettyPrint(ActionsModule.instance);
    
  //... create an extension object
  
  ASObject httpAction = 
    httpAction("http://example.org", METHOD_POST)
      .auth(
        "oauth2", 
        object()
          .set("scopes", 
            object()
              .set(
                "scope.key.1", 
                object().set("description", "foo"))))
      .expects(
        htmlForm()
          .parameter("foo", "http://example.org/FooProperty"))
      .expects(
        typedPayload("text/json")
          .schema("http://foo")
          .type("http://schema.org/Foo"))
      .returns(typedPayload("text/json"))
      .returns(typedPayload("text/html"))
      .requires(
        "urn:example:some:feature",
        "urn:example:some:other:feature")
      .prefers(
        "urn:example:some:optional-feature")
      .target(TARGET_DIALOG)
      .get();
    
    //... write the extension object out using the created IO object

    java.io.ByteArrayOutputStream out = 
      new java.io.ByteArrayOutputStream();
      
    // write using the IO object
    httpAction.writeTo(out, io);
    
    java.io.ByteArrayInputStream in = 
      new java.io.ByteArrayInputStream(out.toByteArray());
      
    httpAction = io.read(in);
     
```

The GeoJSON module provides an implementation of the GeoJSON format.

```java
  import com.ibm.common.geojson.as2.GeoModule;
  import com.ibm.common.activitystreams.IO;

  IO io = IO.makeDefault(GeoModule.instance);
```

The Legacy module provides implementations of legacy Activity Streams 1.0
objectTypes (see https://github.com/activitystreams/activity-schema/blob/master/activity-schema.md)

```java
  import com.ibm.common.activitystreams.legacy.LegacyModule;
  import com.ibm.common.activitystreams.IO;
  
  IO io = IO.makeDefault(LegacyModule.instance);
```

You can register multiple modules when the IO object is created:

```java
  import com.ibm.common.activitystreams.legacy.LegacyModule;
  import com.ibm.common.geojson.as2.GeoModule;
  import com.ibm.common.activitystreams.IO;
  
  IO io = IO.makeDefault(LegacyModule.instance, GeoModule.instance);
```

Each of the modules is provided as separate Maven artifacts.:

```xml
  <dependency>
    <groupId>com.ibm.common</groupId>
    <artifactId>activitystreams-actions</artifactId>
    <version>0.0.1-SNAPSHOT</version>
  </dependency>
  
  <dependency>
    <groupId>com.ibm.common</groupId>
    <artifactId>activitystreams-geo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
  </dependency>
  
  <dependency>
    <groupId>com.ibm.common</groupId>
    <artifactId>activitystreams-legacy</artifactId>
    <version>0.0.1-SNAPSHOT</version>
  </dependency>
```
