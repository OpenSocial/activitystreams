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

import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Maps.difference;
import static com.ibm.common.activitystreams.Makers.linkValue;
import static com.ibm.common.activitystreams.Makers.linkValues;

import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.LinkValue;
import com.ibm.common.activitystreams.ValueType;
import com.ibm.common.activitystreams.LinkValue.ArrayLinkValue;

/**
 * Utility abstract base used for objects that are JSON
 * dictionaries as opposed to full Activity Stream objects.
 * @author james
 * @version $Revision: 1.0 $
 */
public abstract class AbstractDictionaryObject<X>
  extends AbstractWritable
  implements Iterable<String> {

  public static abstract class AbstractBuilder
    <X, D extends AbstractDictionaryObject<X>, B extends AbstractBuilder<X,D,B>> 
    extends AbstractWritable.AbstractWritableBuilder<D,B> {

    protected final Map<String, X> map = 
      Maps.newHashMap();
    private boolean isempty = true;
    
    /**
     * Returns true if items have been added
     * @return boolean
     */
    public boolean notEmpty() {
      return !isempty;
    }
    
    /**
     * Sets an item in this dictionary object
     * @param key String
     * @param x X
     * @return B
     */
    @SuppressWarnings("unchecked")
    public B set(String key, X x) {
      if (x != null) {
        map.put(key,x);
        isempty = false;
      }
      return (B)this;
    }
    
    /**
     * Sets an item in this dictionary object
     * @param key String
     * @param x Supplier<? extends X>
     * @return B
     */
    public B set(
      String key, 
      Supplier<? extends X> x) {
        return set(key, x.get());
    }
    
    /**
     * Add a link
     * @param name String
     * @param url String
     * @return B 
     **/
    protected B link(String name, String url) {
      return link(name, linkValue(url));
    }
    
    /**
     * Add a link
     * @param name String
     * @param link LinkValue   
     * @return B 
     **/
    @SuppressWarnings("unchecked")
    protected B link(String name, LinkValue link) {
      if (link == null)
        return (B)this;
      isempty = false;
      Object obj = map.get(name);
      if (link.valueType() != ValueType.ARRAY) {
        if (obj instanceof LinkValue)
          link = 
            ((LinkValue) obj).valueType() == ValueType.ARRAY ?
              linkValues()
                .add((LinkValue.ArrayLinkValue)obj)
                .add(link)
                .get() :
              linkValues()
                .add((LinkValue)obj, link)
                .get();
        map.put(name, (X) link);
      } else map.put(name, (X)link);
      return (B)this;
    }
  
    /**
     * Add a link
     * @param name String
     * @param link Supplier<? extends LinkValue>
     * @return B 
     **/
    public B link(String name, Supplier<? extends LinkValue> link) {
      return link(name,link.get());
    }

    /**
     * Add a link
     * @param name String
     * @param links Object[]
     * @return B 
     **/
    @SuppressWarnings("unchecked")
    protected B link(String name, Object... links) {
      if (links == null) return (B)this;
      ArrayLinkValue.Builder b = 
        ArrayLinkValue.make();
      for (Object obj : links)
        _add(b, obj);
      return link(name,b.get());
    }
    
    /**
     * Method _add.
     * @param builder ArrayLinkValue.Builder
     * @param obj Object
     */
    private void _add(ArrayLinkValue.Builder builder, Object obj) {
      if (obj == null)
        return;
      else if (obj instanceof String)
        builder.add((String)obj);
      else if (obj instanceof ASObject)
        builder.add((ASObject)obj);
      else if (obj instanceof Supplier)
        _add(builder,((Supplier<?>)obj).get());
      else throw new IllegalArgumentException();
    }
    
  }
  
  private final ImmutableMap<String,X> map;
  private transient int hash = 1;
  
  /**
   * @param builder AbstractBuilder<X,?,?>
   */
  protected AbstractDictionaryObject(AbstractBuilder<X,?,?> builder) {
    super(builder);
    this.map = ImmutableMap.copyOf(builder.map);
  }
  
  /**
   * Method getSingle.
   * @param key String
   * @return X
   */
  protected X getSingle(String key) {
    return map.get(key);
  }
  
  /**
   * Method getSingle.
   * @param key String
   * @param defaultValue X
   * @return X
   */
  protected X getSingle(String key, X defaultValue) {
    X ret = getSingle(key);
    return ret != null ? ret : defaultValue;
  }
  
  /**
   * Method getIterable.
   * @param key String
   * @return Iterable<X>
   */
  @SuppressWarnings("unchecked")
  protected Iterable<X> getIterable(String key) {
    X x = map.get(key);
    return x instanceof Iterable ?
      (Iterable<X>)x : 
      of(x);
  }
  
  /**
   * Method getIterable.
   * @param key String
   * @param filter Predicate<X>
   * @return Iterable<X>
   */
  protected Iterable<X> getIterable(
    String key, 
    Predicate<X> filter) {
      return filter(getIterable(key), filter);
  }
  
  /**
   * Method getIterable.
   * @param key String
   * @param transform Function<X,Y>
   * @return Iterable<Y>
   */
  protected <Y>Iterable<Y> getIterable(
    String key,
    Function<X,Y> transform) {
      return transform(getIterable(key), transform);
  }
  
  /**
   * Method getIterable.
   * @param key String
   * @param filter Predicate<X>
   * @param transform Function<X,Y>
   * @return Iterable<Y>
   */
  protected <Y>Iterable<Y> getIterable(
    String key,
    Predicate<X> filter,
    Function<X,Y> transform) {
      return transform(filter(getIterable(key),filter),transform);
  }
  
  public Iterator<String> iterator() {
    return map.keySet().iterator();
  }

  /**
   * Returns true if the verb appears in the ActionsValue object
   * @param key String
   * @return boolean */
  public boolean has(String key) {
    return map.containsKey(key);
  }
  
  /**
   * Returns true if the verb appears in the ActionsValue object
   * and there is at least one handler in the value.
   * @param key String
   * @return boolean
   */
  protected boolean hasAtLeastOne(String key) {
    return has(key) && size(getIterable(key)) > 0;
  }
  
  public int hashCode() {
    if (hash == 1)
      hash = map.hashCode();
    return hash;
  }
  

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AbstractDictionaryObject other = 
      (AbstractDictionaryObject) obj;
    return difference(map,other.map).areEqual();
  }

}
