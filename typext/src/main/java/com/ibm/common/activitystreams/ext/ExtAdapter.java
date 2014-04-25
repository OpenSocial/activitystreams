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
package com.ibm.common.activitystreams.ext;

import java.lang.reflect.Type;

import com.google.common.collect.ImmutableSet;
import com.ibm.common.activitystreams.ASObject.AbstractBuilder;
import com.ibm.common.activitystreams.internal.ASObjectAdapter;
import com.ibm.common.activitystreams.internal.Model;
import com.ibm.common.activitystreams.internal.Schema;

public class ExtAdapter 
  extends ASObjectAdapter {

  protected ExtAdapter(Schema schema) {
    super(schema);
  }

  private static final ImmutableSet<? extends Type> knownTypes = 
    ImmutableSet.of(Verb.class,ObjectType.class);

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
    if (type == Verb.class) {
      return ExtMakers.verb();
    } else if (type == ObjectType.class) {
      return ExtMakers.objectType();
    } else return null;
  }

  @Override
  protected Model modelFor(Type type) {
    if (super.knowsType(type))
      return super.modelFor(type);
    if (type == Verb.class) {
      return schema().forObjectClassOrType(
        Verb.Builder.class,  
        "verb");
    } else if (type == ObjectType.class) {
      return schema().forObjectClassOrType(
        ObjectType.Builder.class, 
        "objectType");
    } else return null;
  }
}
