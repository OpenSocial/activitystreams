# Type Value Extension Support

This module provides two basic pieces of functionality relating to the 
use of extension Type Value definitions:

1. Verb and ObjectType objects

2. A TypeValueRegistry that is used to resolve information about extension type value identifiers

## The TypeValueRegistry

Suppose we have the following Activity object:

```json
{ 
  "actor": "acct:john@example.org",
  "verb": "http://example.org/verbs/like",
  "object": "http://example.com/notes/1"
}
```

The verb "http://example.org/verbs/like" is an "extension verb". The first 
time an application encounters an extension verb, it may not have any idea 
what to do with it, or what exactly it means. The TypeValueRegistry is 
intended to provide a partial solution to that problem by allowing an 
application to resolve simple TypeValue identifiers into rich object 
identifiers.

For example:

```java
TypeValueRegistry tvr = 
  TypeValueRegistry
    .makeDefaultSilent(io);
    
TypeValue simple = Makers.type("http://example.org/verbs/like");
    
Future<TypeValue> object = tvr.resolve(simple);
    
ASObject obj = (ASObject)object.get();
```

By default, the TypeValueRegistry above does three things:

1. When created, the TypeValueRegistry checks the java classpath for Activity
   Stream 2.0 Collection documents that contain TypeValue definitions. If 
   found, these are preloaded into an in-memory cache.
   
2. When asked to resolve a simple TypeValue, the TypeValueRegistry will first  
   check to see if a resolved object TypeValue already exists in memory. If
   it finds one, it returns it.
   
3. If there currently is not a resolved object TypeValue in memory, the
   TypeValueRegistry will attempt to do an HTTP fetch on the IRI given by
   the TypeValue ID. If the ID is not an HTTP URL, this request will fail.
   If the GET returns an Activity Streams 2.0 document, the document is 
   parsed and is examined for information about the given ID. If found,
   this information is cached and a resolved object TypeValue is 
   returned. 
   
In other words, let's say that the URL "http://example.org/verbs/like" 
points to the following Activity Streams 2.0 collection document:

```json
{
  "objectType": "collection",
  "items": [
    {
      "objectType": "verb",
      "id": "http://example.org/verbs/like",
      "displayName": "like"
    },
    {
      "objectType": "objectType",
      "id": "http://example.org/objects/note",
      "displayName": "note"
    }
  ]
}
```

The TypeValueRegistry will, by default, find and cache both of the TypeValue
definitions included in the document. Then, it will return the object that 
contains "id": "http://example.org/verbs/like".

Because I used the "makeDefaultSilent" method, if there are any errors 
encountered throughout this process, the process will be aborted and the 
original simple TypeValue will be returned. 

### Customizing the TypeValueRegistry

The TypeValueRegistry implementation can be customized by specifying your
own ResolutionStrategy and PreloadStrategy implementations using the 
TypeValueRegistry.Builder. You can also provide your own ExecutorService 
implementation. Refer to the Javadocs for details.
