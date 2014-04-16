package com.ibm.common.activitystreams;

import java.io.Serializable;

import com.ibm.common.activitystreams.util.AbstractWritable;

/**
 * In Activity Streams 2.0, the "objectType" and "verb" properties, 
 * as well as several other properties, are defined as "Type Values".
 * A "Type Value" can be either a simple token string, an absolute 
 * IRI string, or an ASObject.
 * 
 * <pre>
 *   {
 *     "verb": "post"
 *   }
 *   
 *   {
 *     "verb": "urn:example:verbs:foo"
 *   }
 *   
 *   {
 *     "verb": {
 *       "id": "urn:example:verbs:foo",
 *       "displayName": "Foo"
 *     }
 *   }
 * </pre>
 * 
 * <p>The TypeValue interface provides a minimal abstraction 
 * over these possible values. Developers should check valueType
 * to determine which type of TypeValue they are working with.</p>
 * 
 * <pre>
 *   Activity activity = ...
 *   TypeValue tv = activity.verb();
 *   
 *   System.out.println(tv.id());
 *   
 *   switch(tv.valueType()) {
 *   case SIMPLE:
 *     SimpleTypeValue s = (SimpleTypeValue)tv;
 *     ...
 *     break;
 *   case OBJECT:
 *     ASObject o (ASObject)tv;
 *     ...
 *     break;
 *   }
 * </pre>
 * 
 * @author james
 * @version $Revision: 1.0 $
 */
public interface TypeValue 
  extends Writable, Serializable {
  
  /**
   * Return the type value identifier
   * @return String
   */
  String id();
  
  ValueType valueType();
  
  public static final class SimpleTypeValue 
    extends AbstractWritable
    implements TypeValue, Serializable {
      
  public static Builder make() {
    return new SimpleTypeValue.Builder();
  }
    
  public static TypeValue make(String url) {
    return make().url(url).get();
  }
  
  public static final class Builder 
    extends AbstractWritable.AbstractWritableBuilder<SimpleTypeValue,Builder> {

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
      
      public SimpleTypeValue get() {
        return new SimpleTypeValue(this);
      }
      
    }
  
    private final String iri;
  
    SimpleTypeValue(SimpleTypeValue.Builder builder) {
      super(builder);
      this.iri = builder.iri;
    }
  
    /**
     * Return the type value identifier
     * @return String 
     * @see com.ibm.common.activitystreams.TypeValue#id() */
    public String id() {
      return iri;
    }
    
    public String toString() {
      return iri;
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
      private String iri;
      SerializedForm(SimpleTypeValue obj) {
        this.iri = obj.iri;
      }
      Object readResolve() 
        throws java.io.ObjectStreamException {
          return Makers.type(iri);
      }
    }
  }
  
}
