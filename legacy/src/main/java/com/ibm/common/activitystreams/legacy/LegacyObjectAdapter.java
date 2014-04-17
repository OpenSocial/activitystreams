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

import com.google.common.collect.ImmutableSet;
import com.ibm.common.activitystreams.ASObject.AbstractBuilder;
import com.ibm.common.activitystreams.internal.ASObjectAdapter;
import com.ibm.common.activitystreams.internal.Model;
import com.ibm.common.activitystreams.internal.Schema;
import static com.ibm.common.activitystreams.Makers.object;

public class LegacyObjectAdapter 
  extends ASObjectAdapter {

  protected LegacyObjectAdapter(Schema schema) {
    super(schema);
  }

  @SuppressWarnings("unchecked")
  public static final ImmutableSet<? extends Type> knownTypes = 
    ImmutableSet.of(
      Binary.class,
      AudioVisual.class,
      Bookmark.class,
      Event.class,
      File.class,
      Issue.class,
      Membership.class,
      Question.class,
      Task.class,
      WithImage.class);
  
  @Override
  protected boolean knowsType(Type type) {
    if (super.knowsType(type))
      return true;
    return knownTypes.contains(type);
  }

  @Override
  protected AbstractBuilder<?, ?> builderFor(Type type) {
    if (super.knowsType(type))
      return super.builderFor(type);
    if (knowsType(type)) {
      if (type == AudioVisual.class)
        return LegacyMakers.audioVisual();
      else if (type == Binary.class)
        return LegacyMakers.binary();
      else if (type == Bookmark.class)
        return LegacyMakers.bookmark();
      else if (type == Event.class)
        return LegacyMakers.event();
      else if (type == File.class)
        return LegacyMakers.file();
      else if (type == Issue.class)
        return LegacyMakers.issue();
      else if (type == Membership.class)
        return LegacyMakers.membership();
      else if (type == Question.class)
        return LegacyMakers.question();
      else if (type == Task.class)
        return LegacyMakers.task();
      else if (type == WithImage.class)
        return LegacyMakers.withImage();
      else return object();
    } else return null;
  }

  @Override
  protected Model modelFor(Type type) {
    if (super.knowsType(type))
      return super.modelFor(type);
    if (knowsType(type)) {
      if (type == AudioVisual.class)
        return LegacyModule.audioVisual;
      else if (type == Bookmark.class)
        return LegacyModule.bookmark;
      else if (type == Binary.class)
        return LegacyModule.binary;
      else if (type == Event.class)
        return LegacyModule.event;
      else if (type == File.class)
        return LegacyModule.file;
      else if (type == Issue.class)
        return LegacyModule.issue;
      else if (type == Membership.class)
        return LegacyModule.membership;
      else if (type == Question.class)
        return LegacyModule.question;
      else if (type == Task.class)
        return LegacyModule.task;
      else if (type == WithImage.class)
        return LegacyModule.withImage;
      else return Schema.object;
    } else return null;
  }

}
