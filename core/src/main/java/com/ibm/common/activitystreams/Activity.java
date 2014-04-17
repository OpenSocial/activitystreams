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

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.floor;
import static java.lang.Math.ceil;
import static com.ibm.common.activitystreams.Makers.linkValue;
import static com.ibm.common.activitystreams.Makers.type;

import java.io.ObjectStreamException;
import java.io.Serializable;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;


/**
 * An Activity Streams 2.0 Activity
 * 
 * <p>Creating an Activity:</p>
 * <pre>
 *   import static com.ibm.common.activitystreams.Makers.activity;
 *   import static com.ibm.common.activitystreams.Makers.object;
 *   ...
 * 
 *   Activity activity = activity()
 *     .actor("acct:joe@example.org")
 *     .verb("post")
 *     .object(object().content("This is a note"))
 *     .get();
 * </pre>
 * 
 * <p>Consuming an Activity:</p>
 * <pre>
 *   Activity activity = IO.makeDefault().readAsActivity(...);
 *   
 *   System.out.println(activity.verbAsString());   
 *   ASObject actor = activity.firstActor();
 *   ASObject object = activity.firstObject();
 * </pre>
 * 
 * @author james
 * @version $Revision: 1.0 $
 */
public class Activity
  extends ASObject
  implements Serializable {

  public static enum Audience {
    /** 
     * Primary public audience 
     **/
    TO,
    /** 
     * Primary private audience 
     **/
    BTO,
    /** 
     * Secondary public audience 
     **/
    CC,
    /** 
     * Secondary private audience 
     **/
    BCC,
    /** 
     * Public origin 
     **/
    FROM,
    /** 
     * Private origin 
     **/
    BFROM;
    
    private final String label;
    
    private Audience() {
      this.label = name().toLowerCase();
    }
    
    /**
     * Returns the "from" target(s)
     * @return java.util.Iterable&lt;LinkValue>*/
    private Iterable<LinkValue> get(
      Activity activity) {
        return activity.links(label);
    }
    
    private Iterable<LinkValue> get(
      Activity activity, 
      Predicate<? super LinkValue> filter) {
        return activity.links(label, filter);
    }
    
    private LinkValue first(Activity activity) {
      return activity.firstLink(label);
    }
    
    private LinkValue firstMatching(
      Activity activity, 
      Predicate<? super LinkValue> test) {
      return activity.firstMatchingLink(label, test);
    }
  }
  
  public static enum Status {
    /** The Activity is tentatively scheduled to begin **/
    TENTATIVE,
    /** The Activity is scheduled to begin **/
    PENDING,
    /** The Activity has been completed **/
    COMPLETED,
    /** The Activity has been canceled or aborted **/
    CANCELED,
    /** The Activity is currently active **/
    ACTIVE,
    /** The Activity has been voided. **/
    VOIDED,
    OTHER
  }
  
  /**
   * 
   * Builder for concrete Activity object instances.
   * 
   * @author james
   * @version $Revision: 1.0 $
   */
  public static final class Builder 
    extends Activity.AbstractBuilder<Activity,Activity.Builder> {

    /**
     * Get the built Activity object
     * @return Activity 
     * @see com.google.common.base.Supplier#get() 
     */
    public Activity get() {
      return new Activity(this);
    }
    
  }

  /**
   * Ensures that the priority value is within the acceptable range (0.0-1.0)
   * @param d double
   * @return double
   */
  private static double checkPriority(double d) {
    checkArgument(floor(d) >= 0 && ceil(d) >= 1);
    return d;
  }
  
  /**
   * Abstract builder for Activity classes. This is defined this way
   * to make it easier to build extensions of the Activity class.
   * @author james
   * @version $Revision: 1.0 $
   */
  @SuppressWarnings("unchecked")
  public static abstract class AbstractBuilder
    <A extends Activity, B extends AbstractBuilder<A,B>>
    extends ASObject.AbstractBuilder<A, B> {

    /**
     * Set the Activity status property.
     * @param status Status
     * @return B
     */
    public B status(Status status) {
      if (status != null)  
        set("status", status);
      return (B)this;
    }

    /**
     * Mark the status of this activity as being "voided"
     * @return B
     */
    public B voided() {
      return status(Status.VOIDED);
    }
    
    /**
     * Mark the status of this activity as being "tentative"
     * @return B
     */
    public B tentative() {
      return status(Status.TENTATIVE);
    }
    
    /**
     * Mark the status of this activity as being "pending"
     * @return B
     */
    public B pending() {
      return status(Status.PENDING);
    }
    
    /**
     * Mark the status of this activity as being "active"
     * @return B
     */
    public B active() {
      return status(Status.ACTIVE);
    }
    
    /**
     * Mark the status of this activity as being "canceled"
     * @return B
     */
    public B canceled() {
      return status(Status.CANCELED);
    }
    
    /**
     * Mark the status of this activity as being "completed"
     * @return B
     */
    public B completed() {
      return status(Status.COMPLETED);
    }
    
    /**
     * Set the verb for this activity
     * @param iri String
     * @return B 
     */
    public B verb(String iri) {
      set("verb", type(iri));
      return (B)this;
    }

    /**
     * Set the verb for this activity
     * @param tv TypeValue
     * @return B 
     */
    public B verb(TypeValue tv) {
      set("verb", tv);
      return (B)this;
    }
    
    /**
     * Set the verb for this activity
     * @param tv Supplier<? extends TypeValue>
     * @return B 
     */
    public B verb(Supplier<? extends TypeValue> tv) {
      return verb(tv.get());
    }

    /**
     * Set a participant that indirectly contributed to the activity.
     * @param url String
     * @return B 
     **/
    public B participant(String url, String... urls) {
      if (url != null)
        link("participant", linkValue(url));
      if (urls != null)
        for (String u : urls)
          link("participant", linkValue(u));
      return (B)this;
    }
  
    /**
     * Set a participant that indirectly contributed to the activity
     * @param link LinkValue
     * @return B 
     */
    public B participant(LinkValue link, LinkValue... links) {
      if (link != null)
        link("participant", link);
      if (links != null)
        for (LinkValue l : links)
          link("participant", l);
      return (B)this;
    }
  
    /**
     * Set a participant that indirectly contributed to the activity
     * @param link Supplier<? extends LinkValue>
     * @return B 
     */
    public B participant(Supplier<? extends LinkValue> link) {
      return link("participant", link.get());
    }
    
    /**
     * Set the instrument used to complete the activity
     * @param url String
     * @return B 
     */
    public B instrument(String url, String... urls) {
      if (url != null)
        link("instrument", linkValue(url));
      if (urls != null)
        for (String u : urls)
          link("instument", linkValue(u));
      return (B)this;
    }
  
    /**
     * Set the instrument used to complete the activity
     * @param link LinkValue
     * @return B 
     */
    public B instrument(LinkValue link, LinkValue... links) {
      if (link != null)
        link("instrument", link);
      if (links != null)
        for (LinkValue l : links)
          link("instrument", l);
      return (B)this;
    }
  
    /**
     * Set the instrument used to complete the activity
     * @param link Supplier<? extends LinkValue>
     * @return B 
     */
    public B instrument(Supplier<? extends LinkValue> link) {
      return link("instrument", link.get());
    }
    
    /**
     * Set the primary actor/agent for this activity 
     * @param url String
     * @return B 
     */
    public B actor(String url, String... urls) {
      if (url != null)
        link("actor", linkValue(url));
      if (urls != null)
        for (String u : urls)
          link("actor", linkValue(u));
      return (B)this;
    }
  
    /**
     * Set the primary actor/agent for this activity 
     * @param link LinkValue
     * @return B 
     */
    public B actor(LinkValue link, LinkValue... links) {
      if (link != null)
        link("actor", link);
      if (links != null)
        for (LinkValue l : links)
          link("actor", l);
      return (B)this;
    }
  
    /**
     * Set the actor
     * @param link Supplier<? extends LinkValue>
     * @return B 
     */
    public B actor(Supplier<? extends LinkValue> link) {
      return link("actor", link.get());
    }
    
    /**
     * Set the direct object of this activity (the object that has been
     * or will be acted upon)
     * @param url String  
     * @return B 
     */
    public B object(String url, String... urls) {
      if (url != null)
        link("object", linkValue(url));
      if (urls != null)
        for (String u : urls)
          link("object", linkValue(u));
      return (B)this;
    }
  
    /**
     * Set the direct object of this activity (the object that has been
     * or will be acted upon)
     * @param link LinkValue
     * @return B 
     */
    public B object(LinkValue link, LinkValue... links) {
      if (link != null)
        link("object", link);
      if (links != null)
        for (LinkValue l : links)
          link("object", l);
      return (B)this;
    }
  
    /**
     * Set the direct object of this activity (the object that has been
     * or will be acted upon)
     * @param link Supplier<? extends LinkValue>
     * @return B 
     */
    public B object(Supplier<? extends LinkValue> link) {
      return link("object", link.get());
    }
    
    /**
     * Set an object that is indirectly affected by the activity
     * @param url String
     * @return B 
     */
    public B target(String url, String... urls) {
      if (url != null)
        link("target", linkValue(url));
      if (urls != null)
        for (String u : urls)
          link("target", linkValue(u));
      return (B)this;
    }
  
    /**
     * Set an object that is indirectly affected by the activity
     * @param link LinkValue
     * @return B 
     */
    public B target(LinkValue link, LinkValue... links) {
      if (link != null)
        link("target", link);
      if (links != null)
        for (LinkValue l : links)
          link("target", l);
      return (B)this;
    }
  
    /**
     * Set an object that is indirectly affected by the activity
     * @param link Supplier<? extends LinkValue>
     * @return B 
     */
    public B target(Supplier<? extends LinkValue> link) {
      return link("target", link.get());
    }
    
    /**
     * Set a URI that describes the result of this activity
     * @param url String
     * @return B 
     */
    public B result(String url, String... urls) {
      if (url != null)
        link("result", linkValue(url));
      if (urls != null)
        for (String u : urls)
          link("result", linkValue(u));
      return (B)this;
    }
  
    /**
     * Set an object that describes the result of this activity
     * @param link LinkValue
     * @return B 
     */
    public B result(LinkValue link, LinkValue... links) {
      if (link != null)
        link("result", link);
      if (links != null)
        for (LinkValue l : links)
          link("result", l);
      return (B)this;
    }
  
    /**
     * Set an object that describes the result of this activity
     * @param link Supplier<? extends LinkValue>
     * @return B 
     */
    public B result(Supplier<? extends LinkValue> link) {
      return link("result", link.get());
    }
    
    /**
     * Set the audience for this activity
     * @param audience
     * @param url
     * @param urls
     * @return B
     */
    public B audience(Audience audience, String url, String... urls) {
      link(audience.label, linkValue(url));
      for (String u : urls)
        link(audience.label, u);
      return (B)this;
    }
    
    /**
     * Set the audience for this activity
     * @param audience
     * @param link
     * @param links
     * @return B
     */
    public B audience(Audience audience, LinkValue link, LinkValue... links) {
      link(audience.label, link);
      for (LinkValue lv : links)
        link(audience.label, lv);
      return (B)this;
    }
    
    /**
     * Set the audience for this activity
     * @param audience
     * @param link
     * @return B
     */
    public B audience(Audience audience, Supplier<? extends LinkValue> link) {
      return link(audience.label, link);
    }
        
    /**
     * Set the priority as value in the range 0.00 to 1.00.
     * Value will be checked to ensure it is within the valid range.
     * The value SHOULD be limited to two decimal places but the 
     * number of decimals will not be checked.
     * @param d double
     * @return B 
     * @throws IllegalArgumentException if the value is not within 0.00-1.00 
     */
    public B priority(double d) {
      return set("priority", checkPriority(d));
    }
  }
  
  Activity(Activity.AbstractBuilder<?, ?> builder) {
    super(builder);
  }

  /**
   * returns the verb as TypeValue object
   * @return T 
   **/
  public <T extends TypeValue>T verb() {
    return this.<T>get("verb");
  }
  
  /**
   * Returns the verb as a string
   * @return String 
   **/
  public String verbString() {
    return typeValueAsString("verb");
  }
  
  /**
   * Return the participant(s)
   * @return java.util.Iterable&lt;LinkValue>
   **/
  public Iterable<LinkValue> participant() {
    return links("participant");
  }
  
  /**
   * Return the matching participant(s)
   * @param filter
   * @return java.util.Iterable<LinkValue>
   */
  public Iterable<LinkValue> participant(Predicate<? super LinkValue> filter) {
    return links("participant", filter);
  }
  
  /**
   * Return the first paticipant
   * @return LinkValue
   */
  public LinkValue firstParticipant() {
    return firstLink("participant");
  }
  
  /**
   * Return the first matching participant
   * @param test
   * @return LinkValue
   */
  public LinkValue firstMatchingParticipant(Predicate<? super LinkValue> test) {
    return firstMatchingLink("participan", test);
  }
  
  /**
   * Return the instrument(s)
   * @return java.util.Iterable&lt;LinkValue>
   **/
  public Iterable<LinkValue> instrument() {
    return links("instrument");
  }
  
  /**
   * Return the matching instrument(s)
   * @param filter
   * @return java.util.Iterable<LinkValue>
   */
  public Iterable<LinkValue> instrument(Predicate<? super LinkValue> filter) {
    return links("instrument", filter);
  }
  
  /**
   * Return the first instrument
   * @return LinkValue
   */
  public LinkValue firstInstrument() {
    return firstLink("instrument");
  }
  
  /**
   * Return the first matching instrument
   * @param test
   * @return LinkValue
   */
  public LinkValue firstMatchingInstrument(Predicate<? super LinkValue> test) {
    return firstMatchingLink("instrument", test);
  }
  
  /**
   * Returns the actor(s)
   * @return java.util.Iterable&lt;LinkValue>
   **/
  public Iterable<LinkValue> actor() {
    return links("actor");
  }
  
  /**
   * Return the matching actor(s)
   * @param filter
   * @return java.util.Iterable<LinkValue>
   */
  public Iterable<LinkValue> actor(Predicate<? super LinkValue> filter) {
    return links("actor", filter);
  }
  
  /**
   * Return the first actor
   * @return LinkValue
   */
  public LinkValue firstActor() {
    return firstLink("actor");
  }
  
  /**
   * Return the first matching actor
   * @param test
   * @return LinkValue
   */
  public LinkValue firstMatchingActor(Predicate<? super LinkValue> test) {
    return firstMatchingLink("actor", test);
  }
  
  /**
   * Returns the object(s)
   * @return java.util.Iterable&lt;LinkValue>
   **/
  public Iterable<LinkValue> object() {
    return links("object");
  }
  
  /**
   * Return the matching object(s)
   * @param filter
   * @return java.util.Iterable<LinkValue>
   */
  public Iterable<LinkValue> object(Predicate<? super LinkValue> filter) {
    return links("object", filter);
  }
  
  /**
   * Return the first object
   * @return LinkValue
   */
  public LinkValue firstObject() {
    return firstLink("object");
  }
  
  /**
   * Return the first matching object
   * @param test
   * @return LinkValue
   */
  public LinkValue firstMatchingObject(Predicate<? super LinkValue> test) {
    return firstMatchingLink("object", test);
  }
  
  /**
   * Returns the target(s)
   * @return java.util.Iterable&lt;LinkValue>
   **/
  public Iterable<LinkValue> target() {
    return links("target");
  }
  
  /**
   * Return the matching target(s)
   * @param filter
   * @return java.util.Iterable<LinkValue>
   */
  public Iterable<LinkValue> target(Predicate<? super LinkValue> filter) {
    return links("target", filter);
  }
  
  /**
   * Return the first target
   * @return LinkValue
   */
  public LinkValue firstTarget() {
    return firstLink("target");
  }
  
  /**
   * Return the first matching target
   * @param test
   * @return LinkValue
   */
  public LinkValue firstMatchingTarget(Predicate<? super LinkValue> test) {
    return firstMatchingLink("target", test);
  }
  
  /**
   * Returns the result(s)
   * @return java.util.Iterable&lt;LinkValue>
   **/
  public Iterable<LinkValue> result() {
    return links("result");
  }
  
  /**
   * Returns the matching result(s)
   * @param filter
   * @return java.util.Iterable&lt;LinkValue>
   */
  public Iterable<LinkValue> result(Predicate<? super LinkValue> filter) {
    return links("result", filter);
  }
  
  /**
   * Return the first result
   * @return LinkValue
   */
  public LinkValue firstResult() {
    return firstLink("result");
  }
  
  /**
   * Return the first matching result
   * @param test
   * @return LinkValue
   */
  public LinkValue firstMatchingResult(Predicate<? super LinkValue> test) {
    return firstMatchingLink("result", test);
  }
  
  /**
   * Returns the priority.
   * @return double 
   * @throws IllegalArgumentException if the priority is not within 
   *         the range 0.00-1.00 
   **/
  public double priority() {
    return checkPriority(getDouble("priority"));
  }
  
  /**
   * Return the audience for this activity
   * @param audience
   * @return java.util.Iterable&lt;LinkValue>
   */
  public Iterable<LinkValue> audience(
    Audience audience) {
      return audience.get(this);
  }
  
  /**
   * Return the audience for this activity
   * @param audience
   * @param filter
   * @return java.util.Iterable&lt;LinkValue>
   */
  public Iterable<LinkValue> audience(
    Audience audience, 
    Predicate<? super LinkValue> filter) {
      return audience.get(this,filter);
  }
  
  /**
   * Return the first audience for this activity
   * @param audience
   * @return LinkValue
   */
  public LinkValue firstAudience(Audience audience) {
    return audience.first(this);
  }
  
  /**
   * Return the first matching audience for this activity
   * @param audience
   * @param test
   * @return LinkValue
   */
  public LinkValue firstMatchingAudience(
    Audience audience, 
    Predicate<? super LinkValue> test) {
      return audience.firstMatching(this, test);
  }
  
  /**
   * Return the status of this activity
   * @return Status
   */
  public Status status() {
    return get("status");
  }
  
  // Java Serialization Support

  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<Activity> {
    protected SerializedForm(Activity obj) {
      super(obj);
    }
    private static final long serialVersionUID = -1975376657749952999L;
    protected Activity.Builder builder() {
      return Makers.activity();
    }
    Object readResolve() throws ObjectStreamException {
      return super.doReadResolve();
    }
  }
}
