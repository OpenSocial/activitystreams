package com.ibm.common.activitystreams;

/**
 * Used for TypeValue, LinkValue and NLV interfaces to distinguish
 * between the possible value options.
 * 
 * LinkValue.valueType() can return SIMPLE, OBJECT or ARRAY
 * TypeValue.valueType() can return SIMPLE or OBJECT
 * NLV.valueType() can return SIMPLE or OBJECT
 */
public enum ValueType {
  SIMPLE,
  OBJECT,
  ARRAY;
}
