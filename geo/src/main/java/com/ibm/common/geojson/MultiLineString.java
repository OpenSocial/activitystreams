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

import static com.ibm.common.geojson.BoundingBox.calculateBoundingBoxLineStrings;

import java.io.ObjectStreamException;
import java.util.Iterator;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.ibm.common.geojson.Geometry.CoordinateGeometry;

/**
 * A GeoJSON MultiLineString object
 * see http://geojson.org/geojson-spec.html#multilinestring
 * @author james
 *
 */
public final class MultiLineString 
  extends CoordinateGeometry<MultiLineString,LineString,Iterable<LineString>> {

  public static final class Builder 
    extends CoordinateGeometry.Builder<LineString,Iterable<LineString>, MultiLineString, Builder> {

    private final ImmutableList.Builder<LineString> strings = 
      ImmutableList.builder();
    
    public Builder() {
      type(Type.MULTILINESTRING);
    }
    
    /**
     * Add one or more LineStrings
     * @param line LineString
     * @param lines LineString[] optional vararg
     * @return Builder
     */
    public Builder add(LineString line, LineString... lines) {
      this.strings.add(line);
      if (lines != null)
        for (LineString l : lines)
          add(l);
      return this;
    }
    
    /**
     * Add a single LineString
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
    
    public MultiLineString doGet() {
      return new MultiLineString(this);
    }

    /**
     * Get this objects collection of LineStrings
     * @return Iterable&lt;LineString>
     */
    @Override
    protected Iterable<LineString> coordinates() {
      return strings.build();
    }
    
  }
    
  protected MultiLineString(
    Builder builder) {
    super(builder);
  }

  @Override
  public Iterator<LineString> iterator() {
    return coordinates().iterator();
  }

  /** 
   * Copy this object with a calculated bounding box
   * @return MultiLineString
   */
  @Override
  protected MultiLineString makeWithBoundingBox() {
    return new MultiLineString.Builder()
      .from(this)
      .add(this)
      .boundingBox(
        calculateBoundingBoxLineStrings(this)).get();
  }

  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<MultiLineString,Builder> {
    private static final long serialVersionUID = -2060301713159936281L;
    protected SerializedForm(MultiLineString obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return doReadResolve();
    }
    @SuppressWarnings("unchecked")
    protected boolean handle(Builder builder, String key, Object val) {
      if ("coordinates".equals(key)) {
        Iterable<LineString> list = (Iterable<LineString>) val;
        builder.strings.addAll(list);
        return true;
      }
      return false;
    }
    @Override
    protected MultiLineString.Builder builder() {
      return GeoMakers.multiLineString();
    }
  }
}
