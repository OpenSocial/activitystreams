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
package com.ibm.common.activitystreams.legacy;

import java.lang.reflect.Type;

import com.google.common.net.MediaType;
import com.ibm.common.activitystreams.Collection;
import com.ibm.common.activitystreams.IO;
import com.ibm.common.activitystreams.internal.Model;
import com.ibm.common.activitystreams.internal.Schema;
import com.ibm.common.activitystreams.internal.Schema.Builder;
import com.ibm.common.activitystreams.util.Module;

public class LegacyModule 
  implements Module {

  public static final Module instance = new LegacyModule();
  
  public static final Model binary = 
    Schema.object.template()
      .type(Binary.class, Binary.Builder.class)
      .string("compression", "md5", "data", "fileUrl")
      .as("mimeType", MediaType.class)
      .integer("length")
      .get();
  
  public static final Model audioVisual =
    Schema.object.template()
      .type(AudioVisual.class, AudioVisual.Builder.class)
      .string("embedCode")
      .as("stream", MediaLink.class)
      .get();

  public static final Model withImage =
    Schema.object.template()
      .type(WithImage.class, WithImage.Builder.class)
      .as("fullImage", MediaLink.class)
      .get();
  
  public static final Model bookmark =
    Schema.object.template()
      .type(Bookmark.class, Bookmark.Builder.class)
      .string("targetUrl")
      .get();
  
  public static final Model event =
    Schema.object.template()
      .type(Event.class, Event.Builder.class)
      .as("attendedBy", Collection.class)
      .as("attending", Collection.class)
      .as("invited", Collection.class)
      .as("maybeAttending", Collection.class)
      .as("notAttendedBy", Collection.class)
      .as("notAttending", Collection.class)
      .get();
  
  public static final Model membership =
    Schema.object.template()
      .type(Membership.class, Membership.Builder.class)
      .as("members", Collection.class)
      .get();
  
  public static final Model file =
    Schema.object.template()
      .type(File.class, File.Builder.class)
      .string("fileUrl")
      .as("mimeType", MediaType.class)
      .get();
  
  public static final Model issue = 
    Schema.object.template()
      .type(Issue.class, Issue.Builder.class)
      .string("types")
      .get();

  public static final Model question = 
    Schema.object.template()
      .type(Question.class, Question.Builder.class)
      .linkValue("options")
      .get();
  
  public static final Model task =
    Schema.object.template()
      .type(Task.class, Task.Builder.class)
      .object("actor", "object")
      .dateTime("by")
      .as("required", Boolean.class)
      .string("verb")
      .as("prerequisites", Task.class)
      .as("supersedes", Task.class)
      .get();
  
  public void apply(Builder builder) {
    builder.map("alert", Schema.object)
           .map("binary", binary)
           .map("application", Schema.object)
           .map("article", Schema.object)
           .map("audio", audioVisual)
           .map("badge", Schema.object)
           .map("bookmark",bookmark)
           .map("comment", Schema.object)
           .map("device", Schema.object)
           .map("event", event)
           .map("file", file)
           .map("game", Schema.object)
           .map("group", membership)
           .map("image", withImage)
           .map("issue", issue)
           .map("job", Schema.object)
           .map("note", Schema.object)
           .map("offer", Schema.object)
           .map("organization", Schema.object)
           .map("page", Schema.object)
           .map("person", Schema.object)
           .map("process", Schema.object)
           .map("product", Schema.object)
           .map("question", question)
           .map("review", Schema.object)
           .map("role", membership)
           .map("service", Schema.object)
           .map("team", membership)
           .map("video", audioVisual)
           ;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void apply(
    IO.Builder builder,
    Schema schema) {
      LegacyObjectAdapter base = 
        new LegacyObjectAdapter(schema);
      builder.adapter(MediaLink.class, new MediaLinkAdapter());
      for (Type type : LegacyObjectAdapter.knownTypes)
        builder.hierarchicalAdapter((Class)type, base);
  }

}
