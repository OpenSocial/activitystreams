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
package com.ibm.common.activitystreams;

/**
 * Used for TypeValue, LinkValue and NLV interfaces to distinguish
 * between the possible value options.
 * 
 * LinkValue.valueType() can return SIMPLE, OBJECT or ARRAY
 * TypeValue.valueType() can return SIMPLE or OBJECT
 * NLV.valueType() can return SIMPLE or OBJECT
 */
public enum ValueType {
  SIMPLE,
  OBJECT,
  ARRAY;
}
