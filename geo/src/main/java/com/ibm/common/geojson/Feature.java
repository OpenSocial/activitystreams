package com.ibm.common.geojson;

import static com.ibm.common.geojson.BoundingBox.calculateBoundingBox;

import java.io.ObjectStreamException;
import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;

public final class Feature 
  extends GeoObject<Feature>
  implements Iterable<String> {

  public static final class Builder
    extends GeoObject.Builder<Feature,Builder> {

    private final ImmutableMap.Builder<String,Object> properties = 
      ImmutableMap.builder();
    
    public Builder() {
      type(GeoObject.Type.FEATURE);
    }

    public Builder geometry(Geometry<?,?> geometry) {
      return set("geometry", geometry);
    }
    
    public Builder geometry(Supplier<? extends Geometry<?,?>> geometry) {
      return geometry(geometry.get());
    }
    
    public Builder property(String name, Object value) {
      this.properties.put(name, value);
      return this;
    }
    
    protected Builder properties(Map<String,Object> properties) {
      properties.putAll(properties);
      return this;
    }
    
    public Builder id(String id) {
      return set("id", id);
    }
    
    @Override
    public Feature doGet() {
      set("properties", properties.build());
      return new Feature(this);
    }
    
  }
  
  Feature(Builder builder) {
    super(builder);
  }
  
  public <G extends Geometry<?,?>>G geometry() {
    return this.<G>get("geometry");
  }
  
  public Map<String,Object> properties() {
    return this.<Map<String,Object>>get(
      "properties", 
      ImmutableMap.<String,Object>of());
  }
  
  public String id() {
    return this.<String>get("id");
  }

  @SuppressWarnings("unchecked")
  public <T>T getProperty(String name) {
    return (T) properties().get(name);
  }
  
  public <T>T getProperty(String name, T defaultValue) {
    T t = this.<T>getProperty(name);
    return t != null ? t : defaultValue;
  }
  
  public boolean has(String name) {
    return properties().containsKey(name);
  }

  @Override
  public Iterator<String> iterator() {
    return properties().keySet().iterator();
  }

  @Override
  protected Feature makeWithBoundingBox() {
    return new Feature.Builder()
      .from(this)
      .properties(this.properties())
      .boundingBox(
        calculateBoundingBox(geometry())).get();
  }
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<Feature,Feature.Builder> {
    private static final long serialVersionUID = -2060301713159936281L;
    protected SerializedForm(Feature obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return doReadResolve();
    } 
    @SuppressWarnings("unchecked")
    @Override
    protected boolean handle(
      Builder builder, 
      String key, 
      Object val) {
      if ("properties".equals(key)) {
        Map<String,Object> props = (Map<String, Object>) val;
        builder.properties.putAll(props);
        return true;
      }
      return false;
    }
    @Override
    protected Builder builder() {
      return new Feature.Builder();
    }
  }
}
