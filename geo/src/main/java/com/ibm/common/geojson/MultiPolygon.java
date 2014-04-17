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

import static com.ibm.common.geojson.BoundingBox.calculateBoundingBoxPolygons;

import java.io.ObjectStreamException;
import java.util.Iterator;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.ibm.common.geojson.Geometry.CoordinateGeometry;

/**
 * A GeoJSON MultiPolygon object.
 * see http://geojson.org/geojson-spec.html#multipolygon
 * @author james
 *
 */
public final class MultiPolygon 
  extends CoordinateGeometry<MultiPolygon, Polygon, Iterable<Polygon>>{

  public static final class Builder 
    extends CoordinateGeometry.Builder<Polygon,Iterable<Polygon>, MultiPolygon, Builder> {

    private final ImmutableList.Builder<Polygon> strings = 
      ImmutableList.builder();
    
    public Builder() {
      type(Type.MULTIPOLYGON);
    }
    
    /**
     * Add one or more Polygons
     * @param poly Polygon
     * @param polys Polygon[] optional vararg
     * @return Builder
     */
    public Builder add(Polygon poly, Polygon... polys) {
      // TODO: Check hole requirement
      this.strings.add(poly);
      if (polys != null)
        for (Polygon l : polys)
          add(l);
      return this;
    }
    
    /**
     * Add a Polygon
     * @param poly Supplier&lt;Polygon>
     * @return Builder
     */
    public Builder add(Supplier<Polygon> poly) {
      return add(poly.get());
    }
    
    /**
     * Add one ore more Polygons
     * @param polygons Iterable&lt;Polygon>
     * @return Builder
     */
    public Builder add(Iterable<Polygon> polygons) {
      this.strings.addAll(polygons);
      return this;
    }
    
    public MultiPolygon doGet() {
      return new MultiPolygon(this);
    }

    @Override
    protected Iterable<Polygon> coordinates() {
      return strings.build();
    }
    
  }
    
  protected MultiPolygon(
    Builder builder) {
    super(builder);
  }

  @Override
  public Iterator<Polygon> iterator() {
    return coordinates().iterator();
  }

  /**
   * Return a copy of this object with a calculated bounding box
   * @return MultiPolygon
   */
  @Override
  protected MultiPolygon makeWithBoundingBox() {
    return new MultiPolygon.Builder()
      .from(this)
      .add(this)
      .boundingBox(calculateBoundingBoxPolygons(this))
      .get();
  }

  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<MultiPolygon,Builder> {
    private static final long serialVersionUID = -2060301713159936281L;
    protected SerializedForm(MultiPolygon obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return doReadResolve();
    }
    @SuppressWarnings("unchecked")
    @Override
    protected boolean handle(Builder builder, String key, Object val) {
      if ("coordinates".equals(key)) {
        Iterable<Polygon> list = (Iterable<Polygon>) val;
        builder.strings.addAll(list);
        return true;
      }
      return false;
    }
    @Override
    protected MultiPolygon.Builder builder() {
      return GeoMakers.multiPolygon();
    }
  }
}
