package com.ibm.common.activitystreams.actions;

import java.io.Serializable;

import com.google.common.base.Objects;
import com.ibm.common.activitystreams.IO;
import com.ibm.common.activitystreams.ValueType;
import com.ibm.common.activitystreams.Writable;
import com.ibm.common.activitystreams.util.AbstractWritable;

public interface ParameterValue
  extends Writable, Serializable {

  ValueType valueType();
  
  String toString(IO io);
  
  public static final class SimpleParameterValue 
    extends AbstractWritable 
    implements ParameterValue {

    static final class Builder 
      extends AbstractWritable.AbstractWritableBuilder<SimpleParameterValue,Builder> {

      private String type;
      
      public Builder() {
        writeUsing(ActionMakers.io);
      }
      
      public Builder type(String type) {
        this.type = type;
        return this;
      }
      
      @Override
      public SimpleParameterValue get() {
        return new SimpleParameterValue(this);
      }
      
    }
    
    private final String type;
    
    private SimpleParameterValue(Builder b) {
      super(b);
      this.type = b.type;
    }
    
    public ValueType valueType() {
      return ValueType.SIMPLE;
    }
    
    public String type() {
      return type;
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(type);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      SimpleParameterValue other = (SimpleParameterValue) obj;
      return Objects.equal(type,other.type);
    }
    
    public String toString(IO io) {
      return super.toString(io);
    }
    
    Object writeReplace() throws java.io.ObjectStreamException {
      return new SerializedForm(this);
    }
    
    private static class SerializedForm 
      implements Serializable {
      private static final long serialVersionUID = -1975376657749952999L;
      private String type;
      SerializedForm(SimpleParameterValue obj) {
        this.type = obj.type;
      }
    
      Object readResolve() 
        throws java.io.ObjectStreamException {
          return new SimpleParameterValue.Builder().type(type).get();
      }
    } 

  }
  
}
