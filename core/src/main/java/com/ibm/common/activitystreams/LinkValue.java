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

import java.io.Serializable;
import java.util.Iterator;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.ibm.common.activitystreams.util.AbstractWritable;

/**
 * An Activity Streams 2.0 Link Value.
 * 
 * <p>In Actvity Streams 2.0, Link Values can take one of three possible
 * forms:</p>
 * 
 * <ul>
 *   <li>A String containing an absolute IRI</li>
 *   <li>An Activity String object</li>
 *   <li>An Array containing a mix of Strings or Objects</li>
 * </ul>
 * 
 * <p>For instance, the following are all valid examples of Link Values:</p>
 * 
 * <pre>
 *   {
 *     "actor": "acct:joe@example.org"
 *   }
 *   
 *   {
 *     "actor": {
 *       "objectType": "person",
 *       "id": "acct:sally@example.org"
 *     }
 *   }
 *   
 *   {
 *     "actor": [
 *       "acct:joe@example.org",
 *       {
 *         "objectType": "person",
 *         "id": "acct:sally@example.org"
 *       }
 *     ]
 *   }
 * </pre>
 * 
 * <p>The LinkValue interface provides a minimal abstraction over these 
 * value options. Developers will have to check the ValueType of the LinkValue
 * (or do an instanceof check) in order to determine which kind of value 
 * they are working with.</p>
 * 
 * <pre>
 *   Activity activity = ...;
 *   Iterable<LinkValue> actors = activity.actor();
 *   for (LinkValue actor : actors) {
 *     switch(actor.valueType()) {
 *     case SIMPLE:
 *       SimpleLinkValue s = (SimpleLinkValue)actor;
 *       //...
 *       break;
 *     case OBJECT:
 *       ASObject obj = (ASObject)actor;
 *       //...
 *       break;
 *     }
 *   }
 * </pre>
 * 
 * <p>Methods that return Iterable<LinkValue> will never include an instance
 * of ArrayLinkValue as one of the Iterable values</p>
 * 
 * @author james
 * @version $Revision: 1.0 $
 */
public interface LinkValue 
  extends Writable, Serializable {
  
  /**
   * Returns the LinkValue type
   * @return ValueType
   */
  ValueType valueType();
  
  /**
   * A "Simple Link Value" is a string with a relative or absolute 
   * URI or IRI value.
   */
  public static final class SimpleLinkValue
    extends AbstractWritable
    implements LinkValue, Serializable {
    
    /**
     * Creates a new builder
     * @return Builder 
     **/
    public static Builder make() {
      return new Builder();
    }
    
    /**
     * Creates a new instance
     * @param url String
     * @return LinkValue 
     **/
    public static LinkValue make(String url) {
      return new SimpleLinkValue.Builder().url(url).get();
    }
    
    public static final class Builder 
      extends AbstractWritable.AbstractWritableBuilder<SimpleLinkValue,Builder> {

      private String iri;
      
      /**
       * Set the url
       * @param iri String
       * @return Builder 
       **/
      public Builder url(String iri) {
        this.iri = iri;
        return this;
      }
      
      /**
       * Method get.  
       * @return SimpleLinkValue 
       * @see com.google.common.base.Supplier#get() 
       **/
      public SimpleLinkValue get() {
        return new SimpleLinkValue(this);
      }
    }
    
    private final String iri;
    
    SimpleLinkValue(SimpleLinkValue.Builder builder) {
      super(builder);
      this.iri = builder.iri;
    }
    
    /**
     * Return the url
     * @return String 
     */
    public String url() {
      return iri;
    }
    
    public String toString() {
      return iri;
    }

    public ValueType valueType() {
      return ValueType.SIMPLE;
    }
    
    // Java Serialization Support
    
    Object writeReplace() throws java.io.ObjectStreamException {
      return new SerializedForm(this);
    }
    
    private static class SerializedForm 
      implements Serializable {
      private static final long serialVersionUID = -1975376657749952999L;
      private String iri;
      SerializedForm(SimpleLinkValue obj) {
        this.iri = obj.iri;
      }
      Object readResolve() 
        throws java.io.ObjectStreamException {
          return Makers.linkValue(iri);
      }
    }
    
  }
  
  /**
   * An Array Link value is a JSON Array of one or more Simple or Object
   * Link Values. Array Link Values MUST NOT contain nested arrays.
   */
  public static final class ArrayLinkValue
    extends AbstractWritable
    implements Iterable<LinkValue>, LinkValue, Serializable {

    /**
     * Create a new builder
     * @return ArrayLinkValue.Builder 
     **/
    public static ArrayLinkValue.Builder make() {
      return new ArrayLinkValue.Builder();
    }

    public static class Builder 
      extends AbstractWritable.AbstractWritableBuilder<ArrayLinkValue, Builder> {

      private final ImmutableList.Builder<LinkValue> links = 
        ImmutableList.builder();
      
      /**
       * Add one or more items
       * @param value String
       * @param values String[]
       * @return Builder */
      public Builder add(String value, String... values) {
        if (value != null) 
          add(SimpleLinkValue.make(value));
        if (values != null)
          for (String v : values)
            add(SimpleLinkValue.make(v));
        return this;
      }
      
      /**
       * Add one or more link values
       * @param links Iterable<LinkValue>
       * @return Builder */
      public Builder add(Iterable<LinkValue> links) {
        for (LinkValue l : links)
          add(l);
        return this;
      }
      
      /**
       * Add one or more link values
       * @param values LinkValue[]
       * @return Builder */
      public Builder add(LinkValue value, LinkValue... values) {
        if (value != null) {
          checkArgument(value.valueType() != ValueType.ARRAY);
          links.add(value);
        }
        if (values != null)
          for (LinkValue v : values) {
            checkArgument(v.valueType() != ValueType.ARRAY);
            links.add(v);
          }
        return this;
      }
      
      /**
       * Add a link value
       * @param value Supplier<? extends LinkValue>
       * @return Builder */
      public Builder add(Supplier<? extends LinkValue> value) {
        LinkValue val = value.get();
        checkArgument(val.valueType() != ValueType.ARRAY);
        links.add(val);
        return this;
      }
      
      /**
       * Method get.
       * @return ArrayLinkValue 
       * @see com.google.common.base.Supplier#get() 
       **/
      public ArrayLinkValue get() {
        return new ArrayLinkValue(this);
      }
      
    }
    
    private final ImmutableList<LinkValue> links;
    
    ArrayLinkValue(ArrayLinkValue.Builder builder) {
      super(builder);
      this.links = builder.links.build();
    }
    
    /**
     * Method iterator.
     * @return Iterator<LinkValue> 
     * @see java.lang.Iterable#iterator() 
     **/
    public Iterator<LinkValue> iterator() {
      return links.iterator();
    }

    public String toString() {
      return links.toString();
    }

    public ValueType valueType() {
      return ValueType.ARRAY;
    }
    
    // Java Serialization Support
    
    Object writeReplace() throws java.io.ObjectStreamException {
      return new SerializedForm(this);
    }
    
    private static class SerializedForm 
      implements Serializable {
      private static final long serialVersionUID = -1975376657749952999L;
      private ImmutableList<LinkValue> list;
      SerializedForm(ArrayLinkValue obj) {
        this.list = obj.links;
      }
      Object readResolve() 
        throws java.io.ObjectStreamException {
          return Makers.linkValues().add(list);
      }
    }
    
  }
}
