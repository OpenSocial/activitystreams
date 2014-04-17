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
package com.ibm.common.activitystreams.util;

import com.google.common.base.Function;
import com.ibm.common.activitystreams.TypeValue;

/**
 * A TypeValue resolver is used to optionally replace TypeValue 
 * instances. Typically, this would be used to exchange simple
 * string TypeValue's with their object equivalents (if one is
 * available).
 * 
 * The replacement can be performed during parsing by setting a
 * TypeValueResolver on the IO.Builder. This should be done 
 * carefully, however, as the resolver could negatively impact
 * parsing performance depending on how it is implemented. 
 * 
 * @author james
 */
public interface TypeValueResolver
  extends Function<TypeValue,TypeValue> {

  public static final TypeValueResolver DEFAULT_INSTANCE = 
    new DefaultTypeValueResolver();
  
  public static final class DefaultTypeValueResolver 
    implements TypeValueResolver {
    public TypeValue apply(TypeValue tv) {
      return tv;
    }
  }
}
