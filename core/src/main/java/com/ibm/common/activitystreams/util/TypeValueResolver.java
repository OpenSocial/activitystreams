package com.ibm.common.activitystreams.util;

import com.google.common.base.Function;
import com.ibm.common.activitystreams.TypeValue;

/**
 * A TypeValue resolver is used to optionally replace TypeValue 
 * instances. Typically, this would be used to exchange simple
 * string TypeValue's with their object equivalents (if one is
 * available).
 * 
 * The replacement can be performed during parsing by setting a
 * TypeValueResolver on the IO.Builder. This should be done 
 * carefully, however, as the resolver could negatively impact
 * parsing performance depending on how it is implemented. 
 * 
 * @author james
 */
public interface TypeValueResolver
  extends Function<TypeValue,TypeValue> {

  public static final TypeValueResolver DEFAULT_INSTANCE = 
    new DefaultTypeValueResolver();
  
  public static final class DefaultTypeValueResolver 
    implements TypeValueResolver {
    public TypeValue apply(TypeValue tv) {
      return tv;
    }
  }
}
