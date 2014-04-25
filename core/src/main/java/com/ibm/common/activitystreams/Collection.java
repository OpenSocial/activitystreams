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
import static com.ibm.common.activitystreams.Makers.linkValue;
import static com.google.common.collect.Iterables.filter;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadablePeriod;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * An Activity Streams collection object
 * 
 * <pre>
 *   import static com.ibm.common.activitystreams.Makers.collection;
 *   import static com.ibm.common.activitystreams.Makers.object;
 *   ...
 * 
 *   Collection collection = collection()
 *     .items(object()...)
 *     .items(object()...)
 *     .get();
 * </pre>
 * 
 * @author james
 * @version $Revision: 1.0 $
 */
@SuppressWarnings("unchecked")
public class Collection
  extends ASObject
  implements Serializable {

  /** Standardized Paging Links **/
  public static enum Page {
    /** 
     * Reference to the first page in the logical set 
     **/
    FIRST,
    /** 
     * Reference to the next page in the logical set 
     **/
    NEXT,
    /** 
     * Reference to the final page in the logical set 
     * **/
    LAST,
    /** 
     * Reference to the previous page in the logical set 
     **/
    PREVIOUS,
    /** 
     * Reference to the previous page in the logical set (treated as an alias for "previous")
     * @deprecated Use Page.PREVIOUS instead
     **/
    PREV,
    /** 
     * Reference to the page containing the most recently published/updated 
     * items in the logical set 
     **/
    CURRENT,
    /** 
     * Reference to this page 
     **/
    SELF;
    
    private final String label;
    private Page() {
      this.label = name().toLowerCase();
    }
    
    private String checkPrev(Collection col, String or) {
      if (this == PREVIOUS) {
        if (!col.has(label) && col.has(PREV.label))
          return PREV.label;
      } else if (this == PREV) {
        if (!col.has(label) && col.has(PREVIOUS.label))
          return PREVIOUS.label;
      }
      return or;
    }
    
    private Iterable<LinkValue> links(Collection col) {
      return col.links(checkPrev(col,label));
    }
    
    private Iterable<LinkValue> links(
      Collection col, 
      Predicate<? super LinkValue> filter) {
      return col.links(checkPrev(col,label), filter);
    }
    
    private LinkValue first(Collection col) {
      return col.firstLink(checkPrev(col,label));
    }
    
    private LinkValue firstMatching(
      Collection col, 
      Predicate<? super LinkValue> filter) {
      return col.firstMatchingLink(checkPrev(col,label), filter);
    }
  }
  
  /**
   * @author james
   * @version $Revision: 1.0 $
   */
  public static final class Builder 
    extends Collection.AbstractBuilder<Collection,Collection.Builder> {

    /**
     * Method create. 
     * @return Collection */
    protected Collection create() {
      return new Collection(this);
    }
    
  }
  
  /**
   * @author james
   * @version $Revision: 1.0 $
   */
  public static abstract class AbstractBuilder
    <A extends Collection, B extends Collection.AbstractBuilder<A,B>>
      extends ASObject.AbstractBuilder<A, B> {
    
    protected final ImmutableList.Builder<ASObject> list = 
      ImmutableList.builder();
    
    /**
     * Method create.
     * @return A 
     **/
    protected abstract A create();
    
    @Override
    public B set(String key, Object value) {
      if (key.equals("items")) {
        if (value instanceof ArrayLinkValue) {
          ArrayLinkValue alv = (ArrayLinkValue) value;
          for (LinkValue lv : alv) {
            list.add((ASObject)lv);
          }
        } else if (value instanceof ASObject)
          list.add((ASObject) value);
        return (B)this;
      } else return super.set(key,value);
    }


    /**
     * Method get.
     * @return A 
     * @see com.google.common.base.Supplier#get() 
     **/
    public A get() {
      super.set("items", list.build());
      return create();
    }
    
    /**
     * Add items to this collection
     * @param objs java.util.Iterable<? extends ASObject>
     * @return B
     */
    public B items(Iterable<? extends ASObject> objs) {
      if (objs == null)
        return (B)this;
      for (ASObject obj : objs)
        items(obj);
      return (B)this;
    }
    
    /**
     * Add items to this collection
     * @param obj ASObject The object to add
     * @param objs ASObject[] Additional objects to add (vararg)
     * @return B 
     **/
    public B items(ASObject obj, ASObject... objs) {
      if (obj == null)
        return (B)this;
      list.add(obj);
      if (objs != null)
        for (ASObject o : objs)
          list.add(o);
      return (B)this;
    }
    
    /**
     * Add an item to this collection
     * @param obj com.google.common.base.Supplier<? extends ASObject>
     * @return B 
     **/
    public B items(Supplier<? extends ASObject> obj) {
      if (obj == null) 
        return (B)this;
      items(obj.get());
      return (B)this;
    }
        
    /**
     * Set the total number of items (must be non-negative)
     * @param i int
     * @return B 
     **/
    public B totalItems(int i) {
      checkArgument(i >= 0);
      set("totalItems", i);
      return (B)this;
    }
    
    /**
     * Set the number of items per page (must be non-negative)
     * @param i int
     * @return B 
     **/
    public B itemsPerPage(int i) {
      checkArgument(i >= 0);
      set("itemsPerPage", i);
      return (B)this;
    }
    
    /**
     * Set the starting index (must be non-negative)
     * @param i int
     * @return B 
     **/
    public B startIndex(int i) {
      checkArgument(i >= 0);
      set("startIndex", i);
      return (B)this;
    }
    
    /**
     * Specify that the collection contains items updated after the specified time
     * @param dt DateTime
     * @return B 
     **/
    public B itemsAfter(DateTime dt) {
      return _dt("itemsAfter", dt);
    }
    
    /**
     * Specify that the collection contains items updated after right now
     * @return B 
     */
    public B itemsAfterNow() {
      return _dtNow("itemsAfter");
    }
    
    /**
     * Specify that the collection contains items updated a specific duration after now
     * @param duration Duration
     * @return B 
     */
    public B itemsAfterFromNow(ReadableDuration duration) {
      return _dtFromNow("itemsAfter", duration);
    }
    
    /**
     * Specify that the collection contains items updated a specific period after now
     * @param period
     * @return B
     */
    public B itemsAfterFromNow(ReadablePeriod period) {
      return _dtFromNow("itemsAfter", period);
    }
    
    /**
     * Specify that the collection contains items updated a specific duration after now
     * @param v long
     * @param unit TimeUnit
     * @return B 
     **/
    public B itemsAfterFromNow(long v, TimeUnit unit) {
      return _dtFromNow("itemsAfter", v, unit);
    }
    
    /**
     * Specify that the collection contains items updated before a specific time
     * @param dt DateTime
     * @return B 
     **/
    public B itemsBefore(DateTime dt) {
      return _dt("itemsBefore", dt);
    }
    
    /**
     * Specify that the collection contains items updated before now
     * @return B 
     */
    public B itemsBeforeNow() {
      return _dtNow("itemsBefore");
    }
    
    /**
     * Specify that the collection contains items updated a specific duration
     * before now
     * @param duration Duration
     * @return B 
     */
    public B itemsBeforeFromNow(ReadableDuration duration) {
      return _dtFromNow("itemsBefore", duration);
    }
    
    /**
     * Specify that the collection contains items updated a specific period
     * before now
     * @param period
     * @return B
     */
    public B itemsBeforeFromNow(ReadablePeriod period) {
      return _dtFromNow("itemsBefore", period);
    }
    
    /**
     * Method itemsBeforeFromNow.
     * @param v long
     * @param unit TimeUnit
     * @return B 
     **/
    public B itemsBeforeFromNow(long v, TimeUnit unit) {
      return _dtFromNow("itemsBefore", v, unit);
    }
    
    /**
     * Adds a paging link
     * <pre>
     *   Collection collection = Makers.collection()
     *     .pageLink(Page.NEXT, "http://example.org")
     *     .get();
     * </pre>
     * @param page
     * @param url
     * @return B
     */
    public B pageLink(Page page, String url) {
      return link(page.label, linkValue(url));
    }
    
    /**
     * Adds a paging link
     * @param page
     * @param link
     * @return B
     */
    public B pageLink(Page page, LinkValue link) {
      return link(page.label, link);
    }
    
    /**
     * Adds a paging link
     * @param page
     * @param link
     * @return B
     */
    public B pageLink(Page page, Supplier<? extends LinkValue> link) {
      return link(page.label, link);
    }
        
  }
  
  /**
   * Constructor for Collection.
   * @param builder Collection.AbstractBuilder<?,?>
   */
  Collection(Collection.AbstractBuilder<?, ?> builder) {
    super(builder);
  }

  /**
   * Returns the total number of items
   * @return int 
   **/
  public int totalItems() {
    return getInt("totalItems");
  }
  
  /**
   * If not null, indicates that the collection only contains items
   * updated after the given instant 
   * @return DateTime 
   **/
  public DateTime itemsAfter() {
    return this.getDateTime("itemsAfter");
  }
  
  /**
   * If not null, indicates that the collection only contains items
   * updated before the given instant
   * @return DateTime 
   * */
  public DateTime itemsBefore() {
    return this.getDateTime("itemsBefore");
  }
  
  /**
   * Returns the number of items per page
   * @return int 
   **/
  public int itemsPerPage() {
    return this.getInt("itemsPerPage");
  }
  
  /**
   * Returns the start index for this page
   * @return int 
   **/
  public int startIndex() {
    return this.getInt("startIndex");
  }
  
  /**
   * Returns a listing of Paging links that 
   * exist on this collection
   * @return Iterable&lt;Page>
   */
  public Iterable<Page> pages() {
    ImmutableSet.Builder<Page> pages = 
      ImmutableSet.builder();
    for (Page page : Page.values())
      if (has(page.label))
        pages.add(page);
    return pages.build();
  }
  
  /**
   * Returns a listing of Paging LinkValues
   * @param page Page The type of paging link to return
   * @return Iterable&lt;LinkValue>
   */
  public Iterable<LinkValue> pageLink(
    Page page) {
      return page.links(this);
  }
  
  /**
   * Returns a listing of Paging LinkValues
   * @param page Page The type of paging link to return
   * @param filter Predicate<? super LinkValue> A filter
   * @return Iterable&lt;LinkValue>
   */
  public Iterable<LinkValue> pageLink(
    Page page, 
    Predicate<? super LinkValue> filter) {
    return page.links(this,filter);
  }
  
  /**
   * Returns the first matching paging LinkValue
   * @param page Page The type of paging link to return
   * @return LinkValue
   */
  public LinkValue firstPageLink(
    Page page) {
    return page.first(this);
  }
  
  /**
   * Returns the first matching paging LinkValue
   * @param page Page the type of paging link to return
   * @param test com.google.common.base.Predicate<? super LinkValue> a filter
   * @return LinkValue
   */
  public LinkValue firstMatchingPageLink(
    Page page, 
    Predicate<? super LinkValue> test) {
    return page.firstMatching(this,test);
  }
  
  /**
   * Returns the collection of items
   * @return java.util.Iterable<A> 
   **/
  public <A extends ASObject>Iterable<A> items() {
    return this.<Iterable<A>>get("items");
  }
  
  /**
   * Returns a filtered collection of items
   * @param filter com.google.common.base.Predicate<? super A> filter
   * @return java.util.Iterable<A>
   */
  public <A extends ASObject>Iterable<A> items(
    Predicate<? super A> filter) {
      return filter(this.<A>items(), filter);
  }
  
  // Java Serialization Support
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<Collection> {
    protected SerializedForm(Collection obj) {
      super(obj);
    }
    private static final long serialVersionUID = -1975376657749952999L;
    protected Collection.Builder builder() {
      return Makers.collection();
    }
    Object readResolve() throws ObjectStreamException {
      return super.doReadResolve();
    }
  }
}
