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
package com.ibm.common.geojson;

import java.io.Serializable;
import java.util.Map;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Supplier;

import static com.google.common.collect.ImmutableMap.copyOf;
import static com.google.common.collect.Maps.newLinkedHashMap;

/**
 * The base class for all GeoJSON objects. The type of object is identified
 * by the type() property. 
 * @author james
 *
 * @param <G>
 */
@SuppressWarnings("unchecked")
public abstract class GeoObject<G extends GeoObject<G>>
  implements Serializable {

  private static final long serialVersionUID = 8852811044366213922L;

  public static enum Type {
    POINT,
    MULTIPOINT,
    LINESTRING,
    MULTILINESTRING,
    POLYGON,
    MULTIPOLYGON,
    GEOMETRYCOLLECTION,
    FEATURE,
    FEATURECOLLECTION
  }

  public static abstract class Builder
    <G extends GeoObject<G>, B extends Builder<G,B>> 
    implements Supplier<G> {
    
    protected boolean withBoundingBox = false;
    protected Type type;
    protected Map<String,Object> data = 
      newLinkedHashMap();
    
    /**
     * Auto-calculate the bounding box when the object is created
     * @return Builder
     */
    public B calculateBoundingBox() {
      this.withBoundingBox = true;
      return (B)this;
    }
    
    /**
     * Use the given object as a template when creating this one
     * @param geo GeObject&lt;?>
     * @return Builder
     */
    protected B from(GeoObject<?> geo) {
      data.putAll(geo.data);
      return (B)this;
    }
    
    /**
     * Set the object type
     * @param type Type
     * @return Builder
     */
    public B type(Type type) {
      this.type = type;
      return (B)this;
    }
    
    /**
     * Set the CRS
     * @param crs CRS
     * @return Builder
     */
    public B crs(CRS crs) {
      return set("crs", crs);
    }
    
    /**
     * Set the bounding box explicitly
     * @param bbox BoundingBox
     * @return Builder
     * @see GeoObject.Builder.calculateBoundingBox()
     */
    public B boundingBox(BoundingBox bbox) {
      return set("bbox", bbox);
    }
    
    /**
     * Set an additional property on this object
     * @param name String
     * @param val Object
     * @return Builder
     */
    public B set(String name, Object val) {
      if (val != null)
        this.data.put(name,val);
      else if (this.data.containsKey(name))
        this.data.remove(name);
      return (B)this;
    }
    
    /**
     * Get the built object
     */
    public final G get() {
      preGet();
      G g =  doGet();
      return withBoundingBox ? g.withBoundingBox() : g;
    }
    
    protected void preGet() {}
    
    protected abstract G doGet();
  }
  
  final Type type;
  final Map<String,Object> data;
  
  protected GeoObject(Builder<?,?> builder) {
    this.type = builder.type;
    this.data = copyOf(builder.data);
  }
  
  /**
   * Return the type of object
   * @return Type
   */
  public Type type() {
    return type;
  }
  
  public <T>T get(String name) {
    return (T)data.get(name);
  }
  
  public <T>T get(String name, T defaultValue) {
    T val = get(name);
    return val != null ? val : defaultValue;
  }
  
  public boolean has(String name) {
    return data.containsKey(name);
  }
  
  /**
   * Return the CRS for this object
   * @return CRS
   */
  public CRS crs() {
    return this.<CRS>get("crs", null);
  }
  
  /**
   * Return the bounding box for this object
   * @return BoundingBox
   */
  public BoundingBox boundingBox() {
    return this.<BoundingBox>get("bbox", null);
  }

  /**
   * Return a copy of this object with a calculated bounding box
   * @return G (a copy of this object)
   */
  public final G withBoundingBox() {
    return has("bbox") ? 
      (G)this : makeWithBoundingBox();
  }
  
  protected abstract G makeWithBoundingBox();
  
  public static final Position position(float x, float y) {
    return new Position.Builder()
      .northing(x)
      .easting(y)
      .get();
  }
  
  public static final Position position(float x, float y, float z) {
    return new Position.Builder()
      .northing(x)
      .easting(y)
      .altitude(z)
      .get();
  }
  
  public String toString() {
    return MoreObjects.toStringHelper(GeoObject.class)
      .add("type", type)
      .add("data", data)
      .toString();
  }
    
  protected static abstract class AbstractSerializedForm
    <G extends GeoObject<G>, B extends GeoObject.Builder<G,B>>
      implements Serializable {
    private static final long serialVersionUID = -1950126276150975248L;
    private Type type;
    private Map<String,Object> data;
    AbstractSerializedForm(G obj) {
      this.type = obj.type();
      this.data = obj.data;
    }
    protected Object doReadResolve() {
      B builder = builder();
      builder.type(type);
      for (Map.Entry<String,Object> entry : data.entrySet()) {
        String key = entry.getKey();
        Object val = entry.getValue();
        if (!handle(builder, key,val))
          builder.data.put(key,val);
      }
      return builder.get();
    }
    protected boolean handle(
      B builder, 
      String key, 
      Object val) {
        return false;
    }
    protected abstract B builder();
  }
}
