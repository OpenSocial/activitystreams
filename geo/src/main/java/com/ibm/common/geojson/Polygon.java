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

import static com.google.common.base.Preconditions.checkArgument;
import static com.ibm.common.geojson.BoundingBox.calculateBoundingBoxLineStrings;

import java.io.ObjectStreamException;
import java.util.Iterator;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.ibm.common.geojson.Geometry.CoordinateGeometry;

/**
 * A GeoJSON Polygon object
 * see http://geojson.org/geojson-spec.html#polygon
 * @author james
 *
 */
public final class Polygon 
  extends CoordinateGeometry<Polygon,LineString, Iterable<LineString>>  {

  public static final class Builder 
    extends CoordinateGeometry.Builder<LineString, Iterable<LineString>, Polygon, Builder> {

    private final ImmutableList.Builder<LineString> strings = 
      ImmutableList.builder();
    
    public Builder() {
      type(Type.POLYGON);
    }
    
    /**
     * Add one or more LineStrings
     * @param line LineString
     * @param lines LineString[] optional vararg
     * @return Builder
     */
    public Builder add(LineString line, LineString... lines) {
      checkArgument(line.linearRing(), "Polygon coordinates MUST be Linear Rings"); 
      // TODO: Check hole requirement
      this.strings.add(line);
      if (lines != null)
        for (LineString l : lines)
          add(l);
      return this;
    }
    
    /**
     * Add a LineString
     * @param line Supplier&lt;LineString>
     * @return Builder
     */
    public Builder add(Supplier<LineString> line) {
      return add(line.get());
    }
    
    /**
     * Add one or more LineStrings
     * @param lines Iterable&lt;LineString>
     * @return Builder
     */
    public Builder add(Iterable<LineString> lines) {
      this.strings.addAll(lines);
      return this;
    }
    
    public Polygon doGet() {
      return new Polygon(this);
    }

    @Override
    protected Iterable<LineString> coordinates() {
      return strings.build();
    }
    
  }
    
  protected Polygon(
    Builder builder) {
    super(builder);
  }

  @Override
  public Iterator<LineString> iterator() {
    return coordinates().iterator();
  }

  @Override
  protected Polygon makeWithBoundingBox() {
    return new Polygon.Builder()
      .from(this)
      .add(this)
      .boundingBox(
        calculateBoundingBoxLineStrings(this)).get();
  }

  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<Polygon,Builder> {
    private static final long serialVersionUID = -2060301713159936281L;
    protected SerializedForm(Polygon obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return doReadResolve();
    }
    @SuppressWarnings("unchecked")
    @Override
    protected boolean handle(Builder builder, String key, Object val) {
      if ("coordinates".equals(key)) {
        Iterable<LineString> list = (Iterable<LineString>) val;
        builder.strings.addAll(list);
        return true;
      }
      return false;
    }
    @Override
    protected Polygon.Builder builder() {
      return GeoMakers.polygon();
    }
  }
}
