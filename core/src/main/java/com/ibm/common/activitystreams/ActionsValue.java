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

import java.io.Serializable;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.ibm.common.activitystreams.util.AbstractDictionaryObject;

/**
 * <p>The value of the "actions" property... used to map potential verbs
 * with objects capable of "handling" those verbs. Each of the keys 
 * must be a valid verb (either a token or an absolute IRI). </p>
 * 
 * <p>Specifying Actions while creating AS Objects:</p>
 * 
 * <pre>
 * import static com.ibm.common.activitystreams.Makers.object;
 * import static com.ibm.common.activitystreams.Makers.actions;
 * import static com.ibm.common.activitystreams.actions.ActionMakers.intentAction;
 * ...
 * 
 * ASObject obj = object()
 *   .action("like", "http://example.org/like-action")
 *   .action("follow", intentAction())
 *   .get();
 * </pre>
 * 
 * <p>Accessing Actions when consuming AS Objects:</p>
 * 
 * <pre>
 * ActionsValue actions = obj.actions();
 * for (LinkValue lv : actions.get("like")) {
 *   ...
 * }
 * </pre>
 * 
 * <p>The Actions object itself is a JSON Dictonary that maps 
 * verb identifiers to a Link Value that contains one or more
 * Action Handlers. A call to the get(...) method returns an
 * Iterable of Simple or Object LinkValue instances representing 
 * each of the associated Action Handlers.</p>
 * 
 * <p>A Serialized Activity Stream object with Actions:
 * <pre>
 * {
 *   "objectType": "note",
 *   "displayName": "My Note",
 *   "actions": { 
 *     "like": "http://example.org/like-action",
 *     "follow": {
 *       "objectType": "IntentActionHandler",
 *       "displayName": "Follow updates to this note!"
 *     }
 *   }
 * }
 * </pre>
 * 
 * @author james
 * @version $Revision: 1.0 $
 */
public final class ActionsValue
  extends AbstractDictionaryObject<LinkValue>
  implements Serializable {

  /**
   * Builder for ActionsValue objects.
   * <pre>
   *   Makers.actions()
   *     .set("like", "http://example.org/like-action")
   *     .set("follow", ActionMakers.intentAction())
   *     .get();
   * </pre>
   */
  public static final class Builder 
    extends AbstractDictionaryObject.AbstractBuilder
      <LinkValue, ActionsValue, Builder> {

    protected Builder() {}
    
    /**
     * Add an action handler for the given verb.
     * Calling this multiple times results in 
     * multiple action handlers.
     * @param verb String
     * @param iri String   
     * @return Builder 
     */
    public Builder set(String verb, String iri) {
      return set(
        verb, 
        LinkValue.SimpleLinkValue.make(iri));
    }
        
    @Override
    public Builder set(String key, LinkValue x) {
      return super.link(key, x);
    }

    @Override
    public Builder set(String key, Supplier<? extends LinkValue> x) {
      return super.link(key, x);
    }

    /**
     * Get the built ActionsValue instance
     * @return ParametersValue 
     * @see com.google.common.base.Supplier#get() 
     **/
    public ActionsValue get() {
      return new ActionsValue(this);
    }
    
  }
  
  private ActionsValue(Builder builder) {
    super(builder);
  }

  /**
   * Get all Action Handlers associated with the given verb.
   * @param verb String
   * @return java.util.Iterable<LinkValue>
   */
  public Iterable<LinkValue> get(String verb) {
    return super.getIterable(verb);
  }

  /**
   * Get all Action Handlers associated with the given verb
   * that satisfy the given filter
   * @param verb String
   * @param filter Predicate<LinkValue>
   * @return java.util.Iterable<LinkValue>
   */
  public Iterable<LinkValue> get(
    String verb,
    Predicate<LinkValue> filter) {
    return super.getIterable(verb, filter);
  }

  /**
   * Get all Action Handlers associated with the given verb,
   * transformed using the given Transform function
   * @param verb String
   * @param transform Function<LinkValue,Y>
   * @return java.util.Iterable<Y>
   */
  public <Y> Iterable<Y> get(
    String verb,
    Function<LinkValue, Y> transform) {
    return super.getIterable(verb, transform);
  }

  /**
   * Get all Action Handlers associated with the given verb,
   * filtered using the given Predicate and transformed
   * using the given Transform function.
   * @param verb String
   * @param filter Predicate<LinkValue>
   * @param transform Function<LinkValue,Y>
   * @return java.util.Iterable<Y>
   */
  public <Y> Iterable<Y> get(
    String verb,
    Predicate<LinkValue> filter, 
    Function<LinkValue, Y> transform) {
    return super.getIterable(verb, filter, transform);
  }

  /**
   * True if there is at least one Action Handler associated
   * with the given verb
   * @param verb String
   * @return boolean
   */
  public boolean hasAtLeastOne(String verb) {
    return super.hasAtLeastOne(verb);
  }
  
  // Java serialization support
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    implements Serializable {
    private static final long serialVersionUID = -1975376657749952999L;
    private ImmutableMap<String,Object> map;
    SerializedForm(ActionsValue obj) {
      ImmutableMap.Builder<String,Object> builder = 
        ImmutableMap.builder();
      for (String key : obj)
        builder.put(key, obj.get(key));
      this.map = builder.build();
    }
    @SuppressWarnings("unchecked")
    Object readResolve() 
      throws java.io.ObjectStreamException {
        ActionsValue.Builder builder = 
          Makers.actions();
        for (Map.Entry<String,Object> entry : map.entrySet()) {
          Iterable<LinkValue> list = (Iterable<LinkValue>) entry.getValue();
          for (LinkValue link : list)
            builder.set(entry.getKey(), link);
        }
        return builder.get();
    }
  }
}
