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

import static com.google.common.base.Enums.getIfPresent;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Maps.difference;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.net.MediaType.parse;
import static com.ibm.common.activitystreams.Makers.linkValue;
import static com.ibm.common.activitystreams.Makers.linkValues;
import static com.ibm.common.activitystreams.Makers.nlv;
import static com.ibm.common.activitystreams.Makers.type;
import static com.ibm.common.activitystreams.util.Converters.toBoolean;
import static com.ibm.common.activitystreams.util.Converters.toDateTime;
import static com.ibm.common.activitystreams.util.Converters.toDouble;
import static com.ibm.common.activitystreams.util.Converters.toDuration;
import static com.ibm.common.activitystreams.util.Converters.toFloat;
import static com.ibm.common.activitystreams.util.Converters.toInt;
import static com.ibm.common.activitystreams.util.Converters.toInterval;
import static com.ibm.common.activitystreams.util.Converters.toLong;
import static com.ibm.common.activitystreams.util.Converters.toPeriod;
import static com.ibm.common.activitystreams.util.Converters.toShort;
import static com.ibm.common.activitystreams.util.Util.DEFAULT_LOCALE;
import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import static org.joda.time.DateTimeZone.UTC;
import static org.joda.time.Duration.standardSeconds;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadablePeriod;

import com.google.common.base.Converter;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.net.MediaType;
import com.ibm.common.activitystreams.NLV.MapNLV;
import com.ibm.common.activitystreams.NLV.SimpleNLV;
import com.ibm.common.activitystreams.util.AbstractWritable;

/**
 * The Base for all Activity Streams objects.
 * 
 * <p>Creating an object:</p>
 * <pre>
 *   import static com.ibm.common.activitystreams.Makers.object;
 * 
 *   ASObject obj = object()
 *     .id("urn:example:object:1")
 *     .displayName("My Note")
 *     .objectType("note")
 *     .content("en", "This is my note in english")
 *     .get();
 * </pre>
 * @author james
 * @version $Revision: 1.0 $
 */
@SuppressWarnings("unchecked")
public class ASObject
  extends AbstractWritable
  implements Iterable<String>, LinkValue, TypeValue, Serializable {

  /**
   * Builder for concrete ASObject instances
   */
  public static class Builder 
    extends AbstractBuilder<ASObject, Builder> {
    /**
     * Returns the built ASObject instance    
     * @return ASObject 
     * @see com.google.common.base.Supplier#get() 
     **/
    public ASObject get() {
      return new ASObject(this);
    }
  }
  
  /**
   * Verifies that the rating value is within the proper range
   * @param d double
   * @return double 
   **/
  private static double checkRating(double d) {
    checkArgument(floor(d) >= 1 && ceil(d) <= 5);
    return d;
  }
  
  /**
   * Verifies that the value is non-negative
   * @param i int
   * @return int 
   **/
  private static int checkNotNegative(int i) {
    checkArgument(i >= 0);
    return i;
  }
  
  /**
   * Method checkNotNegative.
   * @param l long
   * @return long
   */
  private static long checkNotNegative(long l) {
    checkArgument(l >= 0);
    return l;
  }
    
  /**
   * Abstract base builder for ASObject instances.
   */
  public static abstract class AbstractBuilder
    <A extends ASObject, B extends AbstractBuilder<A,B>>
      extends AbstractWritable.AbstractWritableBuilder<A,B>
      implements Supplier<A>, Writable {
  
    private final Map<String,Object> map = 
      newLinkedHashMap();
    private final ActionsValue.Builder actions = 
      Makers.actions();
      
    /**
     * Method _dt.
     * @param key String
     * @param dt DateTime
     * @return B 
     **/
    protected B _dt(String key, DateTime dt) {
      return set(key, dt);
    }
    
    /**
     * Method _dt.
     * @param key String
     * @param dt String
     * @return B 
     **/
    protected B _dt(String key, String dt) {
      return _dt(key, DateTime.parse(dt));
    }
    
    /**
     * Method _dtNow.
     * @param key String
     * @return B 
     **/
    protected B _dtNow(String key) {
      return _dt(key, DateTime.now(UTC));
    }
    
    /**
     * Method _dtFromNow.
     * @param key String
     * @param duration Duration
     * @return B 
     **/
    protected B _dtFromNow(String key, ReadableDuration duration) {
      return _dt(key, DateTime.now(UTC).plus(duration));
    }
    
    /**
     * Method _dtFromNow.
     * @param key String
     * @param period ReadablePeriod
     * @return B 
     **/
    protected B _dtFromNow(String key, ReadablePeriod period) {
      return _dt(key, DateTime.now(UTC).plus(period));
    }
    
    /**
     * Method _dtFromNow.
     * @param key String
     * @param v long
     * @param unit TimeUnit
     * @return B 
     **/
    protected B _dtFromNow(String key, long v, TimeUnit unit) {
      return _dtFromNow(key, toDuration(v,unit));
    }
    
    /**
     * Method _dtFrom.
     * @param key String
     * @param dt DateTime
     * @param duration Duration
     * @return B 
     **/
    protected B _dtFrom(String key, DateTime dt, ReadableDuration duration) {
      return _dt(key, dt.plus(duration));
    }
    
    /**
     * Method _dtFrom.
     * @param key String
     * @param dt DateTime
     * @param period ReadablePeriod
     * @return B
     */
    protected B _dtFrom(String key, DateTime dt, ReadablePeriod period) {
      return _dt(key, dt.plus(period));
    }
    
    /**
     * Method _dtFrom.
     * @param key String
     * @param dt DateTime
     * @param v long
     * @param unit TimeUnit
     * @return B 
     **/
    protected B _dtFrom(String key, DateTime dt, long v, TimeUnit unit) {
      return _dtFrom(key, dt, toDuration(v,unit));
    }
    
    /**
     * Set the published timestamp
     * @param dt DateTime
     * @return B 
     **/
    public B published(DateTime dt) {
      return _dt("published", dt);
    }
    
    /**
     * Set the published timestamp equal to the current time
     * @return B 
     **/
    public B publishedNow() {
      return _dtNow("published");
    }
    
    /**
     * Set the published timestamp as a given duration from the current time.
     * For instance:
     * <pre>
     * // Published one day ago
     * ASObject obj = Makers.object()
     *   .publishedFromNow(Duration.standardDays(-1))
     *   .get();
     * <pre>
     * @param duration Duration
     * @return B 
     **/
    public B publishedFromNow(ReadableDuration duration) {
      return _dtFromNow("published", duration);
    }
    
    /**
     * Set the published timestamp as a given period of time from the current time
     * <pre>
     * // Published one day ago
     * ASObject obj = Makers.object()
     *   .publishedFromNow(Period.days(-1))
     *   .get();
     * <pre>
     * @param period ReadablePeriod
     * @return B 
     **/
    public B publishedFromNow(ReadablePeriod period) {
      return _dtFromNow("published", period);
    }
    
    /**
     * Set the published timestamp as a given period of time from the current time
     * @param v long
     * @param unit TimeUnit
     * @return B 
     **/
    public B publishedFromNow(long v, TimeUnit unit) {
      return _dtFromNow("published", v, unit);
    }
    
    /**
     * Set the updated timestamp
     * @param dt DateTime
     * @return B 
     **/
    public B updated(DateTime dt) {
      return _dt("updated", dt);
    }
    
    /**
     * Set the updated timestamp equal to the current time
     * @return B 
     **/
    public B updatedNow() {
      return _dtNow("updated");
    }
    
    /**
     * Set the updated timestamp as a given duration from the current time.
     * For instance:
     * <pre>
     * // Published one day ago
     * ASObject obj = Makers.object()
     *   .updatedFromNow(Duration.standardDays(-1))
     *   .get();
     * <pre>
     * @param duration Duration
     * @return B
     **/
    public B updatedFromNow(ReadableDuration duration) {
      return _dtFromNow("updated", duration);
    }
    
    /**
     * Set the updated timestamp as a given period from the current time.
     * For instance:
     * <pre>
     * // Published one day ago
     * ASObject obj = Makers.object()
     *   .updatedFromNow(Period.days(-1))
     *   .get();
     * <pre>
     * @param period ReadablePeriod
     * @return B
     **/
    public B updatedFromNow(ReadablePeriod period) {
      return _dtFromNow("updated", period);
    }
    
    /**
     * Set the updated timestamp as a given period from the current time
     * @param v long
     * @param unit TimeUnit
     * @return B 
     **/
    public B updatedFromNow(long v, TimeUnit unit) {
      return _dtFromNow("updated", v, unit);
    }
    
    /**
     * Set the start time
     * @param dt DateTime
     * @return B 
     **/
    public B startTime(DateTime dt) {
      return _dt("startTime", dt);
    }
    
    /**
     * Set the startTime timestamp as a given duration from the current time.
     * For instance:
     * <pre>
     * // Published one day ago
     * ASObject obj = Makers.object()
     *   .startTimeFromNow(Duration.standardDays(-1))
     *   .get();
     * <pre>
     * @param duration Duration
     * @return B 
     **/
    public B startTimeFromNow(ReadableDuration duration) {
      return _dtFromNow("startTime", duration);
    }
    
    /**
     * Set the startTime timestamp as a given period from the current time.
     * For instance:
     * <pre>
     * // Published one day ago
     * ASObject obj = Makers.object()
     *   .startTimeFromNow(Period.days(-1))
     *   .get();
     * <pre>
     * @param period ReadablePeriod
     * @return B 
     **/
    public B startTimeFromNow(ReadablePeriod period) {
      return _dtFromNow("startTime", period);
    }
    
    /**
     * Set the startTime timestamp as a given period from the current time.
     * @param v long
     * @param unit TimeUnit
     * @return B 
     **/
    public B startTimeFromNow(long v, TimeUnit unit) {
      return _dtFromNow("startTime", v, unit);
    }
    
    /**
     * Set the start time to the current time
     * @return B 
     **/
    public B startTimeNow() {
      return _dtNow("startTime");
    }
    
    /**
     * Set the end time
     * @param dt DateTime
     * @return B
     **/
    public B endTime(DateTime dt) {
      return _dt("endTime", dt);
    }
    
    /**
     * Set the endTime timestamp as a given duration from the current time.
     * For instance:
     * <pre>
     * // Published one day ago
     * ASObject obj = Makers.object()
     *   .startTimeFromNow(Duration.standardDays(-1))
     *   .get();
     * <pre>
     * @param duration Duration
     * @return B 
     **/
    public B endTimeFromNow(ReadableDuration duration) {
      return _dtFromNow("endTime", duration);
    }
    
    /**
     * Set the endTime timestamp as a given period from the current time.
     * For instance:
     * <pre>
     * // Published one day ago
     * ASObject obj = Makers.object()
     *   .startTimeFromNow(Period.days(-1))
     *   .get();
     * <pre>
     * @param period ReadablePeriod
     * @return B
     **/
    public B endTimeFromNow(ReadablePeriod period) {
      return _dtFromNow("endTime", period);
    }
    
    /**
     * Set the endTime timestamp as a given period from the current time
     * @param v long
     * @param unit TimeUnit
     * @return B 
     **/
    public B endTimeFromNow(long v, TimeUnit unit) {
      return _dtFromNow("endTime", v, unit);
    }
    
    /**
     * Set the end time 
     * @return B
     **/
    public B endTimeNow() {
      return _dtNow("endTimeNow");
    }
    
    /**
     * Set the rating as a value in the range 0.00-1.00;
     * @param d double
     * @return B 
     * @throws IllegalArgumentException if the value is not 
     *         within the proper range 
     **/
    public B rating(double d) {
      return set("rating", checkRating(d));
    }
    
    /**
     * Set the duration
     * @param s int
     * @return B 
     * @throws IllegalArgumentException if the value 
     *         is less than zero 
     **/
    public B duration(long s) {
      return duration(standardSeconds(checkNotNegative(s)));
    }
    
    /**
     * Set the duration as a Joda-Time Duration
     * @param d Duration
     * @return B 
     **/
    public B duration(ReadableDuration d) {
      return set("duration", d);
    }
    
    /**
     * Set the duration as a Joda-Time Period
     * @param period ReadablePeriod
     * @return B
     */
    public B duration(ReadablePeriod period) {
      return duration(period.toPeriod().toStandardDuration());
    }
    
    /**
     * Set the duration
     * @param v long
     * @param unit TimeUnit
     * @return B 
     **/
    public B duration(long v, TimeUnit unit) {
      return duration(toDuration(v,unit));
    }
    
    /**
     * Set the duration as a given period of time from the given reference
     * @param p ReadablePeriod
     * @param dt DateTime
     * @return B
     */
    public B durationFrom(ReadablePeriod p, DateTime dt) {
      return duration(p.toPeriod().toDurationFrom(dt));
    }
    
    /**
     * Set the duration as a given period of time from the current time
     * @param p ReadablePeriod
     * @return B
     */
    public B durationFromNow(ReadablePeriod p) {
      return duration(p.toPeriod().toDurationFrom(DateTime.now(UTC)));
    }
    
    /**
     * Set the height of the object in terms of number of device-independent
     * pixels.
     * @param h int
     * @return B 
     **/
    public B height(int h) {
      return set("height", checkNotNegative(h));
    }
    
    /**
     * Set the width of the object in terms of number of device-independent
     * pixels.
     * @param h int
     * @return B 
     **/
    public B width(int h) {
      return set("width", checkNotNegative(h));
    }
    
    /**
     * Set the MIME Media Type of the object
     * @param mt String
     * @return B 
     **/
    public B mediaType(String mt) {
      return mediaType(parse(mt));
    }
    
    /**
     * Set the MIME Media Type of the object
     * @param mt com.google.common.net.MediaType
     * @return B 
     **/
    public B mediaType(MediaType mt) {
      return set("mediaType", mt);
    }
    
    /**
     * Set the link relation
     * @param rel String
     * @return B 
     **/
    public B rel(String rel) {
      return set("rel", rel);
    }
    
    /**
     * An objects alias is an alternative to it's "id".
     * @param iri String
     * @return B 
     **/
    public B alias(String iri) {
      return set("alias", iri);
    }
    
    /**
     * Add an attachment
     * @param url String
     * @return B 
     **/
    public B attachments(String url, String... urls) {
      if (url != null)
        link("attachments", linkValue(url));
      if (urls != null)
        for (String u : urls)
          link("attachments", linkValue(u));
      return (B)this;
    }
  
    /**
     * Add an attachment.
     * @param link LinkValue
     * @return B 
     **/
    public B attachments(LinkValue link, LinkValue... links) {
      if (link != null)
        link("attachments", link);
      if (links != null) 
        for (LinkValue l : links)
          link("attachments", l);
      return (B)this;
    }
    
    /**
     * Add an attachment
     * @param link Supplier<? extends LinkValue>
     * @return B 
     **/
    public B attachments(Supplier<? extends LinkValue> link) {
      return link("attachments", link.get());
    }
    
    /**
     * Set the author
     * @param url String
     * @return B 
     **/
    public B author(String url, String... urls) {
      if (url != null)
        link("author", linkValue(url));
      if (urls != null) 
        for (String u : urls)
          link("author", linkValue(u));
      return (B)this;
    }
  
    /**
     * Set the author
     * @param link LinkValue
     * @return B 
     **/
    public B author(LinkValue link, LinkValue... links) {
      if (link != null)
        link("author", link);
      if (links != null)
        for (LinkValue l : links)
          link("author", l);
      return (B)this;
    }
  
    /**
     * Set the author
     * @param link Supplier<? extends LinkValue>
     * @return B 
     **/
    public B author(Supplier<? extends LinkValue> link) {
      return link("author", link.get());
    }

    /**
     * Add a duplicate
     * @param url String
     * @return B 
     **/
    public B duplicates(String url, String... urls) {
      if (url != null)
        link("duplicates", linkValue(url));
      if (urls != null)
        for (String u : urls)
          link("duplicates", linkValue(u));
      return (B)this;
    }
  
    /**
     * Add a duplicate
     * @param link LinkValue
     * @return B 
     **/
    public B duplicates(LinkValue link, LinkValue... links) {
      if (link != null)
        link("duplicates", link);
      if (links != null)
        for (LinkValue l : links)
          link("duplicates", l);
      return (B)this;
    }
  
    /**
     * Add a duplicate
     * @param link Supplier<? extends LinkValue>
     * @return B 
     **/
    public B duplicates(Supplier<? extends LinkValue> link) {
      return link("duplicates", link.get());
    }

    /**
     * Set the icon
     * @param url String
     * @return B
     **/
    public B icon(String url, String... urls) {
      if (url != null)
        link("icon", linkValue(url));
      if (urls != null)
        for (String u : urls)
          link("icon", linkValue(u));
      return (B)this;
    }
  
    /**
     * Set the icon
     * @param link LinkValue
     * @return B */
    public B icon(LinkValue link, LinkValue... links) {
      if (link != null)
        link("icon", link);
      if (links != null)
        for (LinkValue l : links)
          link("icon", l);
      return (B)this;
    }
  
    /**
     * Set the icon
     * @param link Supplier<? extends LinkValue>
     * @return B */
    public B icon(Supplier<? extends LinkValue> link) {
      return link("icon", link.get());
    }

    /**
     * Set the image
     * @param url String   
     * @return B */
    public B image(String url, String... urls) {
      if (url != null)
        link("image", linkValue(url));
      if (urls != null)
        for (String u : urls)
          link("image", linkValue(u));
      return (B)this;
    }
  
    /**
     * Set the image.
     * @param link LinkValue 
     * @return B */
    public B image(LinkValue link, LinkValue... links) {
      if (link != null)
        link("image", link);
      if (links != null)
        for (LinkValue l : links)
          link("image", l);
      return (B)this;
    }
  
    /**
     * Set the image
     * @param link Supplier<? extends LinkValue> 
     * @return B */
    public B image(Supplier<? extends LinkValue> link) {
      return link("image", link.get());
    }
    
    /**
     * Set the location
     * @param url String
     * @return B */
    public B location(String url, String... urls) {
      if (url != null)
        link("location", linkValue(url));
      if (urls != null)
        for (String u : urls)
          link("location", linkValue(u));
      return (B)this;
    }
  
    /**
     * Set the location
     * @param link LinkValue  
     * @return B */
    public B location(LinkValue link, LinkValue... links) {
      if (link != null)
        link("location", link);
      if (links != null)
        for (LinkValue u : links)
          link("location", u);
      return (B)this;
    }
  
    /**
     * Set the location
     * @param link Supplier<? extends LinkValue>  
     * @return B */
    public B location(Supplier<? extends LinkValue> link) {
      return link("location", link.get());
    }
    
    /**
     * Set the generator
     * @param url String  
     * @return B */
    public B generator(String url, String... urls) {
      if (url != null)
        link("generator", linkValue(url));
      if (urls != null)
        for (String u : urls)
          link("generator", linkValue(u));
      return (B)this;
    }
  
    /**
     * Set the generator
     * @param link LinkValue 
     * @return B */
    public B generator(LinkValue link, LinkValue... links) {
      if (link != null)
        link("generator", link);
      if (links != null)
        for (LinkValue u : links)
          link("generator", u);
      return (B)this;
    }
  
    /**
     * Set the generator.
     * @param link Supplier<? extends LinkValue>  
     * @return B */
    public B generator(Supplier<? extends LinkValue> link) {
      return link("generator", link.get());
    }
    
    /**
     * Set the provider
     * @param url String   
     * @return B */
    public B provider(String url, String... urls) {
      if (url != null)
        link("provider", linkValue(url));
      if (urls != null)
        for (String u : urls)
          link("provider", linkValue(u));
      return (B)this;
    }
  
    /**
     * Set the provider
     * @param link LinkValue  
     * @return B */
    public B provider(LinkValue link, LinkValue... links) {
      if (link != null)
        link("provider", link);
      if (links != null)
        for (LinkValue l : links)
          link("provider",l);
      return (B)this;
    }
  
    /**
     * Set the provider
     * @param link Supplier<? extends LinkValue> 
     * @return B */
    public B provider(Supplier<? extends LinkValue> link) {
      return link("provider", link.get());
    }
    
    /**
     * Add a tag
     * @param url String 
     * @return B */
    public B tags(String url, String... urls) {
      if (url != null)
        link("tags", linkValue(url));
      if (urls != null)
        for (String u : urls)
          link("tags", linkValue(u));
      return (B)this;
    }
  
    /**
     * Add a tag
     * @param link LinkValue  
     * @return B */
    public B tags(LinkValue link, LinkValue... links) {
      if (link != null)
        link("tags", link);
      if (links != null)
        for (LinkValue l : links)
          link("tags", l);
      return (B)this;
    }
  
    /**
     * Add a tag
     * @param link Supplier<? extends LinkValue>  
     * @return B */
    public B tags(Supplier<? extends LinkValue> link) {
      return link("tags", link.get());
    }

    /**
     * Add in-reply-to
     * @param url String  
     * @return B 
     **/
    public B inReplyTo(String url, String... urls) {
      if (url != null)
        link("inReplyTo", linkValue(url));
      if (urls != null)
        for (String u : urls)
          link("inReplyTo", linkValue(u));
      return (B)this;
    }
  
    /**
     * Add in-reply-to
     * @param link LinkValue   
     * @return B */
    public B inReplyTo(LinkValue link, LinkValue... links) {
      if (link != null)
        link("inReplyTo", link);
      if (links != null)
        for (LinkValue l : links)
          link("inReplyTo", l);
      return (B)this;
    }
  
    /**
     * Add in-reply-to
     * @param link Supplier<? extends LinkValue> 
     * @return B 
     **/
    public B inReplyTo(Supplier<? extends LinkValue> link) {
      return link("inReplyTo", link.get());
    }
    
    /**
     * Add a replies collection
     * @param collection Collection 
     * @return B */
    public B replies(Collection collection) {
      return set("replies", collection);
    }
    
    /**
     * Add a replies collection
     * @param collection Supplier<? extends Collection>
     * @return B */
    public B replies(Supplier<? extends Collection> collection) {
      return set("replies", collection.get());
    }
     
    /**
     * Set the ID
     * @param iri String
     * @return B 
     **/
    public B id(String iri) {
      return set("id", iri);
    }
  
    /**
     * Set the objectType
     * @param iri String
     * @return B 
     **/
    public B objectType(String iri) {
      return set("objectType", type(iri));
    }
    
    /**
     * Set the objectType
     * @param tv TypeValue
     * @return B 
     **/
    public B objectType(TypeValue tv) {
      return set("objectType", tv);
    }
    
    /**
     * Set the objectType
     * @param tv Supplier<? extends TypeValue>
     * @return B 
     **/
    public B objectType(Supplier<? extends TypeValue> tv) {
      return objectType(tv.get());
    }
  
    /**
     * Set the language
     * @param lang String 
     * @return B 
     **/
    public B language(String lang) {
      return set("language", lang);
    }
  
    /**
     * Set the displayName
     * @param name String 
     * @return B 
     **/
    public B displayName(String name) {
      return _nlv("displayName", name);
    }
  
    /**
     * Set the displayName
     * @param nlv NLV  
     * @return B 
     **/
    public B displayName(NLV nlv) {
      return _nlv("displayName", nlv);
    }
  
    /**
     * Set the displayName
     * @param nlv Supplier<NLV> 
     * @return B 
     **/
    public B displayName(Supplier<NLV> nlv) {
      return _nlv("displayName", nlv);
    }
  
    /**
     * Set the displayName
     * @param lang String
     * @param name String   
     * @return B 
     **/
    public B displayName(String lang, String name) {
      return _nlv("displayName",lang, name);
    }
  
    /**
     * Set the displayName
     * @param map Map<String,String>   
     * @return B 
     **/
    public B displayName(Map<String,String> map) {
      return _nlv("displayName", map);
    }

    /**
     * Set the content
     * @param name String    
     * @return B 
     **/
    public B content(String name) {
      return _nlv("content", name);
    }
  
    /**
     * Set the content
     * @param nlv NLV 
     * @return B 
     **/
    public B content(NLV nlv) {
      return _nlv("content", nlv);
    }
  
    /**
     * Set the content
     * @param nlv Supplier<NLV>
     * @return B 
     **/
    public B content(Supplier<NLV> nlv) {
      return _nlv("content", nlv);
    }
  
    /**
     * Set the content
     * @param lang String
     * @param name String
     * @return B 
     **/
    public B content(String lang, String name) {
      return _nlv("content",lang, name);
    }
  
    /**
     * Set the content
     * @param map Map<String,String>
     * @return B 
     **/
    public B content(Map<String,String> map) {
      return _nlv("content", map);
    }

    /**
     * Set the summary
     * @param name String
     * @return B 
     **/
    public B summary(String name) {
      return _nlv("summary", name);
    }
  
    /**
     * Set the summary
     * @param nlv NLV
     * @return B 
     **/
    public B summary(NLV nlv) {
      return _nlv("summary", nlv);
    }
  
    /**
     * Set the summary
     * @param nlv Supplier<NLV>
     * @return B 
     **/
    public B summary(Supplier<NLV> nlv) {
      return _nlv("summary", nlv);
    }
  
    /**
     * Set the summary
     * @param lang String
     * @param name String
     * @return B 
     **/
    public B summary(String lang, String name) {
      return _nlv("summary",lang, name);
    }
    
    /**
     * Set the summary
     * @param map Map<String,String>
     * @return B 
     **/
    public B summary(Map<String,String> map) {
      return _nlv("summary", map);
    }

    /**
     * Set the title
     * @param name String
     * @return B 
     **/
    public B title(String name) {
      return _nlv("title", name);
    }
  
    /**
     * Set the title
     * @param nlv NLV
     * @return B 
     **/
    public B title(NLV nlv) {
      return _nlv("title", nlv);
    }
  
    /**
     * Set the title
     * @param nlv Supplier<N>
     * @return B 
     **/
    public <N extends NLV>B title(Supplier<N> nlv) {
      return _nlv("title", nlv);
    }
  
    /**
     * Set the title
     * @param lang String
     * @param value String
     * @return B 
     **/
    public B title(String lang, String value) {
      return _nlv("title",lang, value);
    }
  
    /**
     * Set the title
     * @param map Map<String,String>
     * @return B 
     **/
    public B title(Map<String,String> map) {
      return _nlv("title", map);
    }
    
    /**
     * Add an action handler
     * @param verb String
     * @param iri String
     * @return B
     */
    public B action(String verb, String iri, String... iris) {
      if (iri != null)
        actions.set(verb, iri);
      if (iris != null)
        for (String i : iris)
          actions.set(verb, i);
      return (B)this;
    }
    
    /**
     * Add an action handler
     * @param verb String
     * @param lv LinkValue
     * @return B
     */
    public B action(String verb, LinkValue lv, LinkValue... links) {
      if (lv != null)
        actions.set(verb, lv);
      if (links != null)
        for (LinkValue l : links)
          actions.set(verb, l);
      return (B)this;
    }
    
    /**
     * Add an action handler
     * @param verb String
     * @param lv Supplier<? extends LinkValue>
     * @return B
     */
    public B action(String verb, Supplier<? extends LinkValue> lv) {
      return action(verb, lv.get());
    }
        
    /**
     * Method _nlv.
     * @param key String
     * @param value String
     * @return B
     **/
    protected B _nlv(String key, String value) {
      return set(key, nlv(value));
    }
  
    /**
     * Method _nlv.
     * @param key String
     * @param nlv NLV
     * @return B 
     **/
    protected B _nlv(String key, NLV nlv) {
      return set(key, nlv);
    }
  
    /**
     * Method _nlv.
     * @param key String
     * @param nlv Supplier<? extends NLV>
     * @return B 
     **/
    protected B _nlv(String key, Supplier<? extends NLV> nlv) {
      return set(key, nlv.get());
    }
  
    /**
     * Method _nlv.
     * @param key String
     * @param map Map<String,String>
     * @return B 
     **/
    protected B _nlv(String key, Map<String,String> map) {
      for (Map.Entry<String,String> entry : map.entrySet())
        _nlv(key,entry.getKey(),entry.getValue());
      return (B)this;
    }
    
    /**
     * Method _nlv.
     * @param key String
     * @param lang String
     * @param value String
     * @return B 
     **/
    protected B _nlv(String key, String lang, String value) {
      if (map.containsKey(key)) {
        Object obj = map.get(key);
        if (obj instanceof NLV) {
          NLV nlv = (NLV) obj;
          switch(nlv.valueType()) {
          case SIMPLE:
            String l = (String) map.get("language");
            if (l == null)
              l = DEFAULT_LOCALE;
            NLV.MapNLV.Builder b = 
              Makers.nlv();
            if (lang.equals(l))
              b.set(lang, value);
            else
              b.set(l, ((NLV.SimpleNLV)obj).value())
                 .set(lang, value);
            return set(key, b);
          case OBJECT:
            return set(key, 
              Makers.nlv()
                .from((NLV.MapNLV)obj, lang)
                .set(lang, value)); 
          default:
            throw new IllegalArgumentException();
          }
        } else if (obj instanceof NLV.MapNLV.Builder) {
          ((NLV.MapNLV.Builder) obj).set(lang, value);
          return (B)this;
        }
      }
      set(key, Makers.nlv().set(lang,value));      
      return (B)this;
    }
    
    /**
     * Set the "url"
     * @param url String
     * @return B 
     **/
    public B url(String url, String... urls) {
      if (url != null)
        link("url", linkValue(url));
      if (urls != null)
        for (String u : urls)
          link("url", linkValue(u));
      return (B)this;
    }
    
    /**
     * Set the "url"
     * @param link Supplier<LinkValue>
     * @return B 
     **/
    public B url(Supplier<? extends LinkValue> link) {
      return url(link.get());
    }
  
    /**
     * Set the "url"
     * @param link LinkValue
     * @return B 
     **/
    public B url(LinkValue link, LinkValue... links) {
      if (link != null)
        link("url", link);
      if (links != null)
        for (LinkValue l : links)
          link("url", l);
      return (B)this;
    }
    
    /**
     * Add a link
     * @param name String
     * @param url String
     * @return B 
     **/
    public B link(String name, String url) {
      return link(name, linkValue(url));
    }
    
    /**
     * Add a link
     * @param name String
     * @param link LinkValue   
     * @return B 
     **/
    public B link(String name, LinkValue link) {
      if (link == null)
        return (B)this;
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
        map.put(name, link);
      } else set(name, link);
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
  
    /**
     * Set a property
     * @param key String
     * @param value V
     * @return B 
     **/
    public B set(String key, Object value) {
      if (value == null) 
        return (B)this;
      if (value instanceof Supplier)
        map.put(key, ((Supplier<?>)value).get());
      else
        map.put(key, value);
      return (B)this;
    }
    
    /**
     * Set a property
     * @param key String
     * @param value Supplier<V>
     * @return B 
     **/
    public B set(String key, Supplier<?> value) {
      try {
        return value == null ?
          (B)this : set(key,value.get());
      } catch (Throwable t) {
        throw propagate(t);
      }
    }
    
    /**
     * Set a property from a given callable
     * @param key String
     * @param value Callable<V>
     * @return B 
     **/
    public B set(String key, Callable<?> value) {
      try {
        return value == null ?
          (B)this : 
          set(key,value.call());
      } catch (Throwable t) {
        throw propagate(t);
      }
    }
    
    /**
     * Set the scope
     * @param url String
     * @return B 
     **/
    public B scope(String url, String... urls) {
      if (url != null)
        link("scope", linkValue(url));
      if (urls != null)
        for (String u : urls)
          link("scope", linkValue(u));
      return (B)this;
    }
  
    /**
     * Set the scope
     * @param link LinkValue
     * @return B 
     **/
    public B scope(LinkValue link, LinkValue... links) {
      if (link != null)
        link("scope", link);
      if (links != null)
        for (LinkValue l : links)
          link("scope", l);
      return (B)this;
    }
  
    /**
     * Set the scope
     * @param link Supplier<? extends LinkValue>
    
     * @return B */
    public B scope(Supplier<? extends LinkValue> link) {
      return link("scope", link.get());
    }   
  }
  
  protected final ImmutableMap<String,Object> map;
  private transient int hash = 1;
  
  /**
   * Constructor for ASObject.
   * @param builder ASObject.AbstractBuilder<?,?>
   */
  public ASObject(ASObject.AbstractBuilder<?,?> builder) {
    super(builder);
    if (builder.actions.notEmpty())
      builder.map.put("actions", builder.actions.get());
    this.map = ImmutableMap.copyOf(builder.map);
  }
  
  /**
   * Returns true if the given property exists, does not 
   * determine if the value is non-null
   * @param key String
   * @return boolean 
   **/
  public boolean has(String key) {
    return map.containsKey(key);
  }
  
  /**
   * Return the value of the property if it exists, null otherwise
   * @param key String
   * @return V 
   **/
  public <V>V get(String key) {
    return this.<V>_get(key).orNull();
  }
  
  /**
   * Return the value of the property if it exists, casting to a DateTime
   * object.
   * @param key String
   * @return DateTime
   */
  public DateTime getDateTime(String key) {
    return this.get(key,toDateTime,Optional.<DateTime>absent()).orNull();
  }
  
  /**
   * Return the value of the property if it exists, converting the value
   * to the given Enum class. 
   * @param key String
   * @param _enumClass Class<E>
   * @return E
   */
  public <E extends Enum<E>>E getEnum(String key, Class<E> _enumClass) {
    return getEnum(key, _enumClass, (E)null);
  }
  
  /**
   * Method getEnum.
   * @param key String
   * @param _enumClass Class<E>
   * @param or E
   * @return E
   */
  public <E extends Enum<E>>E getEnum(String key, Class<E> _enumClass, E or) {
    String val = getString(key);
    Optional<E> op = getIfPresent(_enumClass, val);
    return or != null ?
      op.or(or) : op.orNull();
  }
  
  /**
   * Return the value of the property if it exists, casting to a String
   * object.
   * @param key String
   * @return String
   */
  public String getString(String key) {
    return getString(key, null);
  }
  
  /**
   * Method getDuration.
   * @param key String
   * @return Duration
   */
  public Duration getDuration(String key) {
    return this.get(key, toDuration, Optional.<Duration>absent()).orNull();
  }
  
  /**
   * Method getPeriod.
   * @param key String
   * @return Period
   */
  public Period getPeriod(String key) {
    return this.get(key, toPeriod, Optional.<Period>absent()).orNull();
  }
  
  /**
   * Method getInterval.
   * @param key String
   * @return Interval
   */
  public Interval getInterval(String key) {
    return this.get(key, toInterval, Optional.<Interval>absent()).orNull();
  }
  
  /**
   * Return the value of the property as a string if it exists or defaultValue if 
   * it does not. 
   * @param key String
   * @param defaultValue String
   * @return String
   */
  public String getString(String key, String defaultValue) {
    return defaultValue != null ?
      this.<String>_get(key).or(defaultValue) :
      this.<String>_get(key).orNull();
  }
  
  /**
   * Return the value of the property as an int if it exists or 0 if it does not.
   * @param key String
   * @return int
   */
  public int getInt(String key) {
    return getInt(key, 0);
  }
  
  /**
   * Return the value of the property as an int if it exists or defaultValue if
   * it does not
   * @param key String
   * @param defaultValue int
   * @return int
   */
  public int getInt(String key, int defaultValue) {
    return get(key, toInt, Optional.<Integer>absent()).or(defaultValue);
  }
  
  /**
   * Return the value of the property as a long if it exists or 0 if it does not
   * @param key String
   * @return long
   */
  public long getLong(String key) {
    return getLong(key,0L);
  }
  
  /**
   * Return the value of the property as a long if it exists or defaultValue if
   * it does not
   * @param key String
   * @param defaultValue long
   * @return long
   */
  public long getLong(String key, long defaultValue) {
    return get(key, toLong, Optional.<Long>absent()).or(defaultValue);
  }
  
  /**
   * Return the value of the property as a double if it exists, or 0.0 if it
   * does not
   * @param key String
   * @return double
   */
  public double getDouble(String key) {
    return getDouble(key, 0.0);
  }
  
  /**
   * Return the value of the property as a double if it exists or defaultValue
   * if it does not
   * @param key String
   * @param defaultValue double
   * @return double
   */
  public double getDouble(String key, double defaultValue) {
    return get(key, toDouble, Optional.<Double>absent()).or(defaultValue);
  }
  
  /**
   * Return the value of the property as a float if it exists or 0f if it 
   * does not
   * @param key String
   * @return float
   */
  public float getFloat(String key) {
    return getFloat(key, 0f);
  }
  
  /**
   * Return the value of the property as a float if it exists or defaultValue
   * if it does not
   * @param key String
   * @param defaultValue float
   * @return float
   */
  public float getFloat(String key, float defaultValue) {
    return get(key, toFloat, Optional.<Float>absent()).or(defaultValue);
  }
  
  /**
   * Return the value of the property as a short if it exists or 0 if it 
   * does not
   * @param key String
   * @return short
   */
  public short getShort(String key) {
    return getShort(key,(short)0);
  }
  
  /**
   * Return the value of the property as a short if it exists or defaultValue
   * if it does not
   * @param key String
   * @param defaultValue short
   * @return short
   */
  public short getShort(String key, short defaultValue) {
    return get(key, toShort, Optional.<Short>absent()).or(defaultValue);
  }
  
  /**
   * return the value of the property as a boolean if it exists or false 
   * if it does not
   * @param key String
   * @return boolean
   */
  public boolean getBoolean(String key) {
    return getBoolean(key,false);
  }
  
  /**
   * Return the value of the property as a boolean if it exists or defaultValue
   * if it does not
   * @param key String
   * @param defaultValue boolean
   * @return boolean
   */
  public boolean getBoolean(
    String key, 
    boolean defaultValue) {
      return get(key, toBoolean, Optional.<Boolean>absent()).or(defaultValue);
  }
  
  /**
   * Method _get.
   * @param key String
   * @return Optional<V>
   */
  private <V>Optional<V> _get(String key) {
    return Optional.fromNullable((V)map.get(key));
  }
  
  /**
   * Method _get.
   * @param key String
   * @param transform Function<V1,V2>
   * @return Optional<V2>
   */
  private <V1,V2>Optional<V2> _get(String key, Function<V1,V2> transform) {
    return this.<V1>_get(key).transform(transform);
  }
  
  /**
   * Return the value if it exists or defaultValue if it does not
   * @param key String
   * @param defaultValue V
   * @return V 
   **/
  public <V>V get(String key, V defaultValue) {
    return defaultValue != null ?
      this.<V>_get(key).or(defaultValue) :
      this.<V>_get(key).orNull();
  }
  
  /**
   * Returns the value of the property, transformed using the given function
   * @param key String
   * @param transform Function<V1,V2>
   * @return V2 
   **/
  public <V1,V2>V2 get(String key, Function<V1,V2> transform, V2 defaultValue) {
    return this.<V1,V2>_get(key,transform).or(defaultValue);
  }
  
  /**
   * Returns the value of the given property converted using the given converter
   * @param key String
   * @param converter Converter<V1,V2>
   * @return V2
   */
  public <V1,V2>V2 get(String key, Converter<V1,V2> converter) {
    return this.<V1,V2>_get(key,converter).orNull();
  }
  
  /**
   * Returns the value of the property if it exists or defaultValue if it
   * does not
   * @param key String
   * @param def V2
   * @param transform Function<V1,V2>
   * @return V2 
   **/
  public <V1,V2>V2 get(String key, V2 def, Function<V1,V2> transform) {
    return this.<V1,V2>_get(key,transform).or(def);
  }

  /**
   * Returns an iterator listing all of the properties defined on this object
   * @return Iterator<String> 
   * @see java.lang.Iterable#iterator() 
   **/
  public Iterator<String> iterator() {
    return map.keySet().iterator();
  }
  
  /**
   * Return this objects identifier
   * @return String 
   * @see com.ibm.common.activitystreams.TypeValue#id() 
   **/
  public String id() {
    return this.getString("id");
  }
  
  /**
   * Return the objectType
   * @return T 
   **/
  public <T extends TypeValue>T objectType() {
    return this.<T>get("objectType");
  }
  
  /**
   * Return this object's type identifier as a string
   * @return String 
   **/
  public String objectTypeString() {
    return typeValueAsString("objectType");
  }
  
  /**
   * Method typeValueAsString.
   * @param name String
   * @return String 
   **/
  protected String typeValueAsString(String name) {
    TypeValue tv = this.<TypeValue>get(name);
    return tv != null ? tv.id() : null;
  }
  
  /**
   * Return this object's language context
   * @return String 
   **/
  public String language() {
    return this.getString("language");
  }
  
  /**
   * Return this objects displayName as an NLV object
   * @return NLV 
   **/
  public NLV displayName() {
    return this.<NLV>get("displayName");
  }
  
  /**
   * Return this objects displayName
   * @return String 
   **/
  public String displayNameString() {
    return _nlv("displayName");
  }
  
  /**
   * Method displayNameString.
   * @param lang String
   * @return String 
   **/
  public String displayNameString(String lang) {
    return _nlv("displayName", lang);
  }
  
  /**
   * Method _nlv.
   * @param key String
   * @return String 
   **/
  protected String _nlv(String key) {
    String lang = language();
    return _nlv(key, lang != null ? lang : DEFAULT_LOCALE);
  }
  
  /**
   * Method _nlv.
   * @param key String
   * @param lang String
   * @return String 
   **/
  protected String _nlv(String key, String lang) {
    NLV nlv = 
      this.<NLV>get(key);
    switch(nlv.valueType()) {
    case SIMPLE:
      NLV.SimpleNLV sim = 
        (SimpleNLV) nlv;
      String l = language();
      return l == null || Objects.equal(l,lang) ? sim.value() : null;
    case OBJECT:
      NLV.MapNLV map = 
       (MapNLV) nlv;
      return map.value(lang);
    default:
      return null;
    } 
  }
  
  /**
   * Return this objects URL LinkValues
   * @return java.util.Iterable&lt;LinkValue> 
   **/
  public Iterable<LinkValue> url() {
    return links("url");
  }
  
  /**
   * Return the matching URL LinkValues
   * @return java.util.Iterable&lt;LinkValue>
   **/
  public Iterable<LinkValue> url(Predicate<? super LinkValue> test) {
    return links("url", test);
  }
  
  /**
   * Return this objects first URL LinkValue
   * @return LinkValue
   */
  public LinkValue firstUrl() {
    return firstLink("url");
  }
  
  /**
   * Return this objects first matching LinkValue
   * @param test
   * @return LinkValue
   */
  public LinkValue firstMatchingUrl(Predicate<? super LinkValue> test) {
    return firstMatchingLink("url", test);
  }
  
  /**
   * Return this object's mediaType
   * @return MediaType 
   **/
  public MediaType mediaType() {
    return this.<MediaType>get("mediaType");
  }
  
  /**
   * Returns the value of this objects's rel property
   * @return String 
   **/
  public String rel() {
    return this.getString("rel");
  }
  
  /**
   * Returns this value of this object's alias property
   * @return String 
   **/
  public String alias() {
    return this.getString("alias");
  }
  
  /**
   * Return the given set of links
   * @param name String
   * @return java.util.Iterable<LinkValue> 
   **/
  protected Iterable<LinkValue> links(String name) {
    LinkValue lv = this.<LinkValue>get(name);
    if (lv == null) return of();
    return lv.valueType() == ValueType.ARRAY ?
      (Iterable<LinkValue>)lv : of(lv);
  }
  
  protected Iterable<LinkValue> links(
    String name, 
    Predicate<? super LinkValue> test) {
      return filter(links(name), test);
  }
  
  protected LinkValue firstLink(String name) {
    return getFirst(links(name), null);
  }
  
  protected LinkValue firstMatchingLink(
    String name, 
    Predicate<? super LinkValue> test) {
      return getFirst(links(name,test), null);
  }
  
  /**
   * Return the set of attachments
   * @return java.util.Iterable<LinkValue> 
   **/
  public Iterable<LinkValue> attachments() {
    return links("attachments");
  }
  
  /**
   * Return the matching set of attachments;
   * @param test
   * @return java.util.Iterable&lt;LinkValue>
   */
  public Iterable<LinkValue> attachments(Predicate<? super LinkValue> test) {
    return links("attachments", test);
  }
  
  /**
   * Return the first attachment for this object
   * @return LinkValue
   */
  public LinkValue firstAttachment() {
    return firstLink("attachments");
  }
  
  /**
   * Return the first matching attachment
   * @param test
   * @return LinkValue
   */
  public LinkValue firstMatchingAttachment(Predicate<? super LinkValue> test) {
    return firstMatchingLink("attachments", test);
  }
  
  /**
   * Return this authors of this object
   * @return java.util.Iterable<LinkValue> 
   **/
  public Iterable<LinkValue> author() {
    return links("author");
  }
  
  /**
   * Return the matching authors of this object
   * @return java.util.Iterable<LinkValue>
   */
  public Iterable<LinkValue> author(Predicate<? super LinkValue> test) {
    return links("author", test);
  }
  
  /**
   * Return the first author
   * @return LinkValue
   */
  public LinkValue firstAuthor() {
    return firstLink("author");
  }
  
  /**
   * Return the first matching author
   * @param test
   * @return LinkValue
   */
  public LinkValue firstMatchingAuthor(Predicate<? super LinkValue> test) {
    return firstMatchingLink("author", test);
  }
  
  /**
   * Return the collection of duplicates for this object
   * @return java.util.Iterable<LinkValue> 
   **/
  public Iterable<LinkValue> duplicates() {
    return links("duplicates");
  }
  
  /**
   * Return the matching duplicates
   * @param test
   * @return java.util.Iterable&lt;LinkValue>
   */
  public Iterable<LinkValue> duplicates(Predicate<? super LinkValue> test) {
    return links("duplicates", test);
  }
  
  /**
   * Return the first duplicate
   * @return LinkValue
   */
  public LinkValue firstDuplicate() {
    return firstLink("duplicates");
  }
  
  /**
   * Return the first matching duplicate
   * @param test
   * @return LinkValue
   */
  public LinkValue firstMatchingDuplicate(Predicate<? super LinkValue> test) {
    return firstMatchingLink("duplicates", test);
  }
  
  /**
   * Return the icons for this object
   * @return java.util.Iterable<LinkValue> */
  public Iterable<LinkValue> icon() {
    return links("icon");
  }
  
  /**
   * Return the matching icons for this object
   * @return java.util.Iterable<LinkValue>
   */
  public Iterable<LinkValue> icon(Predicate<? super LinkValue> test) {
    return links("icon", test);
  }
  
  /**
   * Return the first icon
   * @return LinkValue
   */
  public LinkValue firstIcon() {
    return firstLink("icon");
  }
  
  /**
   * Return thie first matching icon
   * @param test
   * @return LinkValue
   */
  public LinkValue firstMatchingIcon(Predicate<? super LinkValue> test) {
    return firstMatchingLink("icon", test);
  }
  
  /**
   * Return the image for this object
   * @return java.util.Iterable<LinkValue>
   **/
  public Iterable<LinkValue> image() {
    return links("image");
  }
  
  /**
   * Return the matching images for this object
   * @param test
   * @return java.util.Iterable<LinkValue>
   */
  public Iterable<LinkValue> image(Predicate<? super LinkValue> test) {
    return links("image", test);
  }
  
  /**
   * Return the first image
   * @return LinkValue
   */
  public LinkValue firstImage() {
    return firstLink("image");
  }
  
  /**
   * Return the first matching image
   * @param test
   * @return LinkValue
   */
  public LinkValue firstMatchingImage(Predicate<? super LinkValue> test) {
    return firstMatchingLink("image", test);
  }
  
  /**
   * Return the location associated with this object
   * @return java.util.Iterable<LinkValue> 
   **/
  public Iterable<LinkValue> location() {
    return links("location");
  }
  
  /**
   * Return the matching locations associated with this object
   * @param test
   * @return java.util.Iterable<LinkValue>
   */
  public Iterable<LinkValue> location(Predicate<? super LinkValue> test) {
    return links("location", test);
  }
  
  /**
   * Return the first location associated with this object
   * @return LinkValue
   */
  public LinkValue firstLocation() {
    return firstLink("location");
  }
  
  /**
   * Return the first matching location associated with this object
   * @param test
   * @return LinkValue
   */
  public LinkValue firstMatchingLocation(Predicate<? super LinkValue> test) {
    return firstMatchingLink("location", test);
  }
  
  /**
   * Return the generators for this object
   * @return java.util.Iterable<LinkValue> 
   **/
  public Iterable<LinkValue> generator() {
    return links("generator");
  }
  
  /**
   * Return the matching generators for this object
   * @return java.util.Iterable<LinkValue>
   */
  public Iterable<LinkValue> generator(Predicate<? super LinkValue> test) {
    return links("generator", test);
  }
  
  /**
   * Return the first generator for this object
   * @return LinkValue
   */
  public LinkValue firstGenerator() {
    return firstLink("generator");
  }
  
  /**
   * Return the first matching generator for this object
   * @param test
   * @return LinkValue
   */
  public LinkValue firstMatchingGenerator(Predicate<? super LinkValue> test) {
    return firstMatchingLink("generator", test);
  }
  
  /**
   * Return the providers for this object
   * @return java.util.Iterable<LinkValue> 
   **/
  public Iterable<LinkValue> provider() {
    return links("provider");
  }
  
  /**
   * Return the matching providers for this object
   * @return java.util.Iterable<LinkValue>
   */
  public Iterable<LinkValue> provider(Predicate<? super LinkValue> test) {
    return links("provider", test);
  }
  
  /**
   * Return the first provider for this object
   * @return LinkValue
   */
  public LinkValue firstProvider() {
    return firstLink("provider");
  }
  
  /**
   * Return the first matching providers for this object
   * @param test
   * @return LinkValue
   */
  public LinkValue firstMatchingProvider(Predicate<? super LinkValue> test) {
    return firstMatchingLink("provider", test);
  }
  
  /**
   * Return the tags for this object
   * @return java.util.Iterable<LinkValue> 
   **/
  public Iterable<LinkValue> tags() {
    return links("tags");
  }
  
  /**
   * Return the matching tags for this object
   * @return java.util.Iterable<LinkValue>
   */
  public Iterable<LinkValue> tags(Predicate<? super LinkValue> test) {
    return links("tags", test);
  }
  
  /**
   * Return the first tag for this object
   * @return LinkValue
   */
  public LinkValue firstTag() {
    return firstLink("tags");
  }
  
  /**
   * Return this first matching tag for this object
   * @param test
   * @return LinkValue
   */
  public LinkValue firstMatchingTag(Predicate<? super LinkValue> test) {
    return firstMatchingLink("tags", test);
  }
  
  /**
   * Return the inReplyTo links for this object
   * @return java.util.Iterable<LinkValue> 
   **/
  public Iterable<LinkValue> inReplyTo() {
    return links("inReplyTo");
  }
  
  /**
   * Return the matching inReplyTo links for this object
   * @return java.util.Iterable<LinkValue>
   */
  public Iterable<LinkValue> inReplyTo(Predicate<? super LinkValue> test) {
    return links("inReplyTo", test);
  }
  
  /**
   * Return the first inReplyTo link for this object
   * @return LinkValue
   */
  public LinkValue firstInReplyTo() {
    return firstLink("inReplyTo");
  }
  
  /**
   * Return the first matching inReplyTo link for this object
   * @param test
   * @return LinkValue
   */
  public LinkValue firstMatchingInReplyTo(Predicate<? super LinkValue> test) {
    return firstMatchingLink("inReplyTo", test);
  }
  
  /**
   * Return the content of this object
   * @return NLV 
   **/
  public NLV content() {
    return this.<NLV>get("content");
  }
  
  /**
   * Return the content of this object
   * @return String 
   **/
  public String contentString() {
    return _nlv("content");
  }
  
  /**
   * Return the content of this object for the given language
   * @param lang String
   * @return String */
  public String contentString(String lang) {
    return _nlv("content", lang);
  }
  
  /**
   * Return the title of this object
   * @return NLV 
   **/
  public NLV title() {
    return this.<NLV>get("title");
  }
  
  /**
   * Return the title of this object
   * @return String 
   **/
  public String titleString() {
    return _nlv("title");
  }
  
  /**
   * Return the title of this object for the given language
   * @param lang String
   * @return String 
   **/
  public String titleString(String lang) {
    return _nlv("title", lang);
  }
  
  /**
   * Return the summary of this object
   * @return NLV 
   **/
  public NLV summary() {
    return this.<NLV>get("summary");
  }
  
  /**
   * Return the summary of this object
   * @return String 
   **/
  public String summaryString() {
    return _nlv("summary");
  }
  
  /**
   * Return the summary of this object for the given language
   * @param lang String
   * @return String */
  public String summaryString(String lang) {
    return _nlv("summary", lang);
  }
  
  /**
   * Return the published timestamp for this object
   * @return DateTime */
  public DateTime published() {
    return this.getDateTime("published");
  }
  
  /**
   * Return the updated timestamp for this object
   * @return DateTime 
   **/
  public DateTime updated() {
    return this.getDateTime("updated");
  }
  
  /**
   * Return the startTime timestamp for this object
   * @return DateTime 
   **/
  public DateTime startTime() {
    return this.getDateTime("startTime");
  }
  
  /**
   * Return the endTime timestamp for this object
   * @return DateTime 
   **/
  public DateTime endTime() {
    return this.getDateTime("endTime");
  }
  
  /**
   * Return the rating property for this object
   * @return double 
   **/
  public double rating() {
    return checkRating(getDouble("rating"));
  }
  
  /**
   * Return the duration of this object
   * @return Duration 
   **/
  public Duration duration() {
    return this.get("duration", toDuration, Optional.<Duration>absent()).orNull();
  }
  
  /**
   * Return the height of this object in device independent pixels
   * @return int 
   **/
  public int height() {
    return checkNotNegative(getInt("height"));
  }
  
  /**
   * Return the width of this object in device independent pixels
   * @return int 
   **/
  public int width() {
    return checkNotNegative(getInt("width"));
  }
  
  /**
   * Return the replies collection for this object
   * @return Collection */
  public Collection replies() {
    return this.<Collection>get("replies");
  }
  
  /**
   * Return the actions collection for this object
   * @return ActionsValue 
   */
  public ActionsValue actions() {
    return this.<ActionsValue>get("actions");
  }
  
  /**
   * Return the scope
   * @return java.util.Iterable<LinkValue> 
   **/
  public Iterable<LinkValue> scope() {
    return links("scope");
  }
  
  /**
   * Return the matching scope items
   * @return java.util.Iterable<LinkValue>
   */
  public Iterable<LinkValue> scope(Predicate<? super LinkValue> test) {
    return links("scope", test);
  }
  
  /**
   * Return the first scope item
   * @return LinkValue
   */
  public LinkValue firstScope() {
    return firstLink("scope");
  }
  
  /**
   * Return the first matching scope item
   * @param test
   * @return LinkValue
   */
  public LinkValue firstMatchingScope(Predicate<? super LinkValue> test) {
    return firstMatchingLink("scope", test);
  }

  @Override
  public int hashCode() {
    if (hash == 1)
      hash = Objects.hashCode(map);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ASObject other = (ASObject) obj;
    return 
      difference(map, other.map)
        .areEqual();
  }
  
  /**
   * Return the valueType of this object (always returns ValueType.OBJECT)
   * @return ValueType
   * @see com.ibm.common.activitystreams.LinkValue#valueType()
   */
  public ValueType valueType() {
    return ValueType.OBJECT;
  }
  
  
  // Java Serialization support... 
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<ASObject> {
    protected SerializedForm(ASObject obj) {
      super(obj);
    }
    private static final long serialVersionUID = -1975376657749952999L;
    protected ASObject.Builder builder() {
      return Makers.object();
    }
    Object readResolve() throws ObjectStreamException {
      return super.doReadResolve();
    }
  }
  
  protected static abstract class AbstractSerializedForm<A extends ASObject> 
    implements Serializable {
    private static final long serialVersionUID = -801787904013409277L;
    private ImmutableMap<String,Object> map;
    protected AbstractSerializedForm(A obj) {      
      this.map = ImmutableMap.copyOf(Maps.transformValues(obj.map, SerializableTransform));
    }
    protected abstract ASObject.AbstractBuilder<?, ?> builder();
    protected Object doReadResolve() 
      throws java.io.ObjectStreamException {
        ASObject.AbstractBuilder<?,?> builder = builder();
        for (Map.Entry<String,Object> entry : map.entrySet()) {
          Object obj = entry.getValue();
          if (obj instanceof SerializableMediaType)
            obj = ((SerializableMediaType)obj).mediaType();
          builder.set(entry.getKey(), obj);
        }
        return builder.get();
    }
  }
  
  private static final Function<Object,Object> SerializableTransform = 
    new Function<Object,Object>() {
      public Object apply(Object input) {
        if (input instanceof MediaType)
          input = new SerializableMediaType((MediaType) input);
        return input;
      }
    };
  
  private static final class SerializableMediaType 
    implements Serializable {
    private static final long serialVersionUID = -3162545492169619570L;
    private String mediaType;
    SerializableMediaType(MediaType mt) {
      this.mediaType = mt.toString();
    }
    MediaType mediaType() {
      return MediaType.parse(mediaType);
    }
  }
}
