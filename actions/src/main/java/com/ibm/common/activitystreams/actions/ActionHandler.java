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

import java.io.Serializable;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.LinkValue;

/**
 */
public abstract class ActionHandler 
  extends ASObject
  implements Serializable {

  /**
   */
  @SuppressWarnings("unchecked")
  public static abstract class Builder<A extends ActionHandler, B extends Builder<A,B>> 
    extends ASObject.AbstractBuilder<A, B> {
    
    private final Authentication.Builder auth = 
      Authentication.make();
    
    public Builder() {
      writeUsing(ActionMakers.io);
    }
    
    /**
     * Method confirm.
     * @return B
     */
    public B confirm() {
      return confirm(true);
    }
    
    /**
     * Method confirm.
     * @param on boolean
     * @return B
     */
    public B confirm(boolean on) {
      set("confirm", on);
      return (B)this;
    }
    
    /**
     * Method context.
     * @param obj ASObject
     * @return B
     */
    public B context(ASObject obj) {
      set("context", obj);
      return (B)this;
    }
    
    /**
     * Method context.
     * @param obj Supplier<? extends ASObject>
     * @return B
     */
    public B context(Supplier<? extends ASObject> obj) {
      return context(obj.get());
    }
    
    /**
     * Method requires.
     * @param iri String
     * @return B
     */
    public B requires(String iri, String... iris) {
      requires(LinkValue.SimpleLinkValue.make(iri));
      if (iris != null) 
        for (String i : iris)
          requires(i);
      return (B)this;
    }
    
    /**
     * Method requires.
     * @param lv LinkValue
     * @return B
     */
    public B requires(LinkValue lv, LinkValue... lvs) {
      link("requires", lv);
      if (lvs != null)
        for (LinkValue i : lvs)
          requires(i);
      return (B)this;
    }
    
    /**
     * Method requires.
     * @param lv Supplier<? extends LinkValue>
     * @return B
     */
    public B requires(Supplier<? extends LinkValue> lv) {
      return requires(lv.get());
    }
    
    /**
     * Method prefers.
     * @param iri String
     * @return B
     */
    public B prefers(String iri, String... iris) {
      prefers(LinkValue.SimpleLinkValue.make(iri));
      if (iris != null)
        for (String i : iris)
          prefers(i);
      return (B) this;
    }
    
    /**
     * Method prefers.
     * @param lv LinkValue
     * @return B
     */
    public B prefers(LinkValue lv, LinkValue... lvs) {
      link("prefers", lv);
      if (lvs != null)
        for (LinkValue l : lvs)
          prefers(l);
      return (B)this;
    }
    
    /**
     * Method prefers.
     * @param lv Supplier<? extends LinkValue>
     * @return B
     */
    public B prefers(Supplier<? extends LinkValue> lv) {
      return prefers(lv.get());
    }
    
    /**
     * Method expects.
     * @param iri String
     * @return B
     */
    public B expects(String iri, String... iris) {
      expects(LinkValue.SimpleLinkValue.make(iri));
      if (iris != null) 
        for (String i : iris)
          expects(i);
      return (B)this;
    }
    
    /**
     * Method expects.
     * @param tv LinkValue
     * @return B
     */
    public B expects(LinkValue tv, LinkValue... lvs) {
      link("expects", tv);
      if (lvs != null)
        for (LinkValue lv : lvs)
          expects(lv);
      return (B)this;
    }
    
    /**
     * Method expects.
     * @param tv Supplier<? extends LinkValue>
     * @return B
     */
    public B expects(Supplier<? extends LinkValue> tv) {
      return expects(tv.get());
    }
    
    /**
     * Method returns.
     * @param iri String
     * @return B
     */
    public B returns(String iri, String... iris) {
      returns(LinkValue.SimpleLinkValue.make(iri));
      if (iris != null)
        for (String i : iris)
          returns(i);
      return (B)this;
    }
    
    /**
     * Method returns.
     * @param tv LinkValue
     * @return B
     */
    public B returns(LinkValue tv, LinkValue... lvs) {
      link("returns", tv);
      if (lvs != null)
        for (LinkValue lv : lvs)
          returns(lv);
      return (B)this;
    }
    
    /**
     * Method returns.
     * @param tv Supplier<? extends LinkValue>
     * @return B
     */
    public B returns(Supplier<? extends LinkValue> tv) {
      return returns(tv.get());
    }
    
    /**
     * Method auth.
     * @param key String
     * @param obj ASObject
     * @return B
     */
    public B auth(String key, ASObject obj) {
      auth.set(key, obj);
      return (B)this;
    }
    
    /**
     * Method auth.
     * @param key String
     * @param obj Supplier<? extends ASObject>
     * @return B
     */
    public B auth(String key, Supplier<? extends ASObject> obj) {
      return auth(key, obj.get());
    }

    /**
     * Method get.
     * @return A
     * @see com.google.common.base.Supplier#get()
     */
    public A get() {
      if (auth.notEmpty())
        set("auth", auth.get());
      return actualGet();
    }
    
    /**
     * Method actualGet.
     * @return A
     */
    protected abstract A actualGet();
  }
  
  /**
   * Constructor for ActionHandler.
   * @param builder Builder<?,?>
   */
  protected ActionHandler(Builder<?,?> builder) {
    super(builder);
  }
  
  /**
   * Method confirm.
   * @return boolean
   */
  public boolean confirm() {
    return this.getBoolean("confirm");
  }
  
  /**
   * Method context.
   * @return A
   */
  public <A extends ASObject>A context() {
    return this.<A>get("context");
  }
  
  /**
   * Method expects.
   * @return Iterable<LinkValue>
   */
  public Iterable<LinkValue> expects() {
    return this.links("expects");
  }
  
  public Iterable<LinkValue> expects(Predicate<? super LinkValue> filter) {
    return this.links("expects", filter);
  }
  
  /**
   * Method requires.
   * @return Iterable<LinkValue>
   */
  public Iterable<LinkValue> requires() {
    return this.links("requires");
  }
  
  public Iterable<LinkValue> requires(Predicate<? super LinkValue> filter) {
    return this.links("requires", filter);
  }
  
  /**
   * Method prefers.
   * @return Iterable<LinkValue>
   */
  public Iterable<LinkValue> prefers() {
    return this.links("prefers");
  }
  
  public Iterable<LinkValue> prefers(Predicate<? super LinkValue> filter) {
    return this.links("prefers", filter);
  }
  
  /**
   * Method returns.
   * @return Iterable<LinkValue>
   */
  public Iterable<LinkValue> returns() {
    return this.links("returns");
  }
  
  public Iterable<LinkValue> returns(Predicate<? super LinkValue> filter) {
    return this.links("returns", filter);
  }
    
  /**
   * Method auth.
   * @return Authentication
   */
  public Authentication auth() {
    return this.<Authentication>get("auth");
  }
  
  /**
   * Method hasAuth.
   * @param key String
   * @return boolean
   */
  public boolean hasAuth(String key) {
    Authentication auth = auth();
    return auth != null ?
      auth.has(key) : false;
  }
  
  /**
   * Method auth.
   * @param key String
   * @return A
   */
  @SuppressWarnings("unchecked")
  public <A extends ASObject>A auth(String key) {
    Authentication auth = auth();
    return auth != null ?
      (A)auth.get(key) : null;
  }

}
