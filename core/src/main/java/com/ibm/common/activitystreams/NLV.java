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

import static com.google.common.collect.Maps.difference;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.ibm.common.activitystreams.util.AbstractWritable;


/**
 * An Activity Streams 2.0 Natural Language Value...
 * 
 * <p>The value is either a simple string or an object 
 * with multiple Language-Tag keys and values...</p>
 * 
 * <pre>
 *   {
 *     "objectType": "note",
 *     "displayName": {
 *       "en": "My Title In English",
 *       "fr": "Mon titre en français"
 *     }
 *   }
 * </pre>
 * 
 * <p>
 *   ASObject obj = ...
 *   System.out.println(obj.displayNameString("en"));
 *   System.out.println(obj.displayNameString("fr"));
 *   
 *   NLV nlv = obj.displayName();
 *   switch(nlv.valueType()) {
 *     case SIMPLE:
 *       SimpleNLV s = (SimpleNLV)nlv;
 *       ...
 *       break;
 *     case OBJECT:
 *       MapNLV m = (MapNLV)nlv;
 *       ...
 *       break;
 *   }
 * </p>
 * 
 * @author james
 * @version $Revision: 1.0 $
 */
public interface NLV
  extends Writable, Serializable {
  
  /**
   * Returns the value type. Either ValueType.SIMPLE or ValueType.OBJECT
   * @return ValueType
   */
  ValueType valueType();
  
  public static final class SimpleNLV 
    extends AbstractWritable
    implements NLV, Serializable {
    
    /**
     * Create a new builder
     * @return Builder */
    public static Builder make() {
      return new Builder();
    }
    
    /**
     * Create a new instance
     * @param value String
     * @return SimpleNLV */
    public static SimpleNLV make(String value) {
      return make().value(value).get();
    }
    
    private final String val;
    
    SimpleNLV(Builder builder) {
      super(builder);
      this.val = builder.val;
    }
    
    /**
     * Return the value
     * @return String 
     **/
    public String value() {
      return val;
    }
    
    @Override
    public int hashCode() {
      return Objects.hashCode(val);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      SimpleNLV other = (SimpleNLV) obj;
      return Objects.equal(val, other.val);
    }
    
    public String toString() {
      return val;
    }

    public static final class Builder 
      extends AbstractWritable.AbstractWritableBuilder<SimpleNLV,Builder> {

      private String val;

      public Builder value(String val) {
        this.val = val;
        return this;
      }
      
      public SimpleNLV get() {
        return new SimpleNLV(this);
      }
      
    }

    public ValueType valueType() {
      return ValueType.SIMPLE;
    }
    
    Object writeReplace() throws java.io.ObjectStreamException {
      return new SerializedForm(this);
    }
    
    private static class SerializedForm 
      implements Serializable {
      private static final long serialVersionUID = -1975376657749952999L;
      private String value;
      SerializedForm(SimpleNLV obj) {
        this.value = obj.val;
      }
      Object readResolve() 
        throws java.io.ObjectStreamException {
          return Makers.nlv(value);
      }
    }
    
  }
  
  public static final class MapNLV
    extends AbstractWritable
    implements NLV, Iterable<String> {

    public static Builder make() {
      return new Builder();
    }
    
    private final ImmutableMap<String,String> vals;
    private transient int hash = 1;
    
    MapNLV(Builder builder) {
      super(builder);
      this.vals = builder.vals.build();
    }
    
    public String value(String lang) {
      return vals.get(lang);
    }

    public boolean has(String lang) {
      return vals.containsKey(lang);
    }
    
    @Override
    public int hashCode() {
      if (hash == 1)
        hash = Objects.hashCode(vals);
      return hash;
    }

    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      MapNLV other = (MapNLV) obj;
      return 
        difference(vals, other.vals)
          .areEqual();
      
    }

    public Iterator<String> iterator() {
      return vals.keySet().iterator();
    }
    
    public static final class Builder 
      extends AbstractWritable.AbstractWritableBuilder<MapNLV,Builder> {

      private final ImmutableMap.Builder<String,String> vals = 
        ImmutableMap.builder();
      
      public Builder from(MapNLV other, String lang) {
        for (String l : other) 
          if (!l.equalsIgnoreCase(lang))
            set(l, other.value(l));
        return this;
      }
      
      public Builder set(String lang, String val) {
        vals.put(lang,val);
        return this;
      }
      
      public MapNLV get() {
        return new MapNLV(this);
      }
      
    }

    public ValueType valueType() {
      return ValueType.OBJECT;
    }
    
    public Map<String,String> toMap() {
      return vals;
    }
    
    Object writeReplace() throws java.io.ObjectStreamException {
      return new SerializedForm(this);
    }
    
    private static class SerializedForm 
      implements Serializable {
      private static final long serialVersionUID = -1975376657749952999L;
      private ImmutableMap<String,String> map;
      SerializedForm(MapNLV obj) {
        this.map = obj.vals;
      }
      Object readResolve() 
        throws java.io.ObjectStreamException {
          NLV.MapNLV.Builder builder = 
            Makers.nlv();
          for (Map.Entry<String,String> entry : map.entrySet())
            builder.set(entry.getKey(), entry.getValue());
          return builder.get();
      }
    }
  }

}
