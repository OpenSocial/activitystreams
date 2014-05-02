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
package com.ibm.common.activitystreams.actions;

import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.actions.StylesValue.Builder;
import com.ibm.common.activitystreams.internal.Adapter;
import com.ibm.common.activitystreams.util.AbstractDictionaryObjectAdapter;

final class Adapters {

  private Adapters() {}
  
  static final Adapter<Authentication> AUTH =
      new AbstractDictionaryObjectAdapter
      <ASObject,
       Authentication,
       Authentication.Builder>(ASObject.class) {
      @Override
      protected Authentication.Builder builder() {
        return Authentication.make();
      }
    };
    
    static final Adapter<StylesValue> STYLES  =
      new AbstractDictionaryObjectAdapter
        <String,
         StylesValue,
         StylesValue.Builder>(String.class) {  
      @Override
      protected Builder builder() {
        return StylesValue.make();
      }
    };
    
}
