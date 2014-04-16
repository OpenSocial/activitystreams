package com.ibm.common.geojson;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;

public final class CRS
  implements Iterable<String>, Serializable {

  public static final class Builder implements Supplier<CRS> {
    
    private String type;
    private ImmutableMap.Builder<String,Object> properties = 
      ImmutableMap.builder();
    
    public Builder type(String type) {
      this.type = type;
      return this;
    }
    
    public Builder set(String name, Object value) {
      this.properties.put(name,value);
      return this;
    }
    
    public Builder set(Map<String,Object> properties) {
      this.properties.putAll(properties);
      return this;
    }
    
    public CRS get() {
      return new CRS(this);
    }
    
  }
  
  private final String type;
  private final Map<String,Object> properties;
  
  CRS(Builder builder) {
    this.type = builder.type;
    this.properties = builder.properties.build();
  }
  
  public String toString() {
    return Objects.toStringHelper(CRS.class)
      .addValue(type)
      .addValue(properties)
      .toString();
  }
  
  public int size() {
    return properties.size();
  }
  
  public String type() {
    return type;
  }
  
  public Map<String,Object> properties() {
    return properties;
  }
  
  @SuppressWarnings("unchecked")
  public <T>T get(String name) {
    return (T)properties.get(name);
  }
  
  public <T>T get(String name, T defaultValue) {
    T t = this.<T>get(name);
    return t != null ? t : defaultValue;
  }
  
  public boolean has(String name) {
    return properties.containsKey(name);
  }

  @Override
  public Iterator<String> iterator() {
    return properties.keySet().iterator();
  }
  
  public static final CRS named(String name) {
    return new CRS.Builder()
      .type("name")
      .set("name",name)
      .get();
  }
  
  public static final CRS linked(String href, String type) {
    return new CRS.Builder()
      .type("link")
      .set("href", href)
      .set("type", type)
      .get();
  }
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm implements Serializable {
    private static final long serialVersionUID = -2060301713159936285L;
    private String type;
    private Map<String,Object> properties;
    protected SerializedForm(CRS obj) {
      this.type = obj.type;
      this.properties = obj.properties;
    }
    Object readResolve() throws ObjectStreamException {
      CRS.Builder builder = new CRS.Builder();
      builder.type(type);
      builder.properties.putAll(properties);
      return builder.get();
    }
  }
}
