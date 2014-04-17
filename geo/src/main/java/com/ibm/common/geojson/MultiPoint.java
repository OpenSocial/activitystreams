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

import java.io.ObjectStreamException;
import java.util.Iterator;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.ibm.common.geojson.Geometry.CoordinateGeometry;

import static com.ibm.common.geojson.BoundingBox.calculateBoundingBoxPositions;

public final class MultiPoint 
  extends CoordinateGeometry<MultiPoint,Position,Iterable<Position>> {

  public static final class Builder 
    extends CoordinateGeometry.Builder<Position,Iterable<Position>,MultiPoint,Builder> {

    protected ImmutableList.Builder<Position> list = 
      ImmutableList.builder();
    
    public Builder() {
      type(GeoObject.Type.MULTIPOINT);
    }
    
    public Builder add(Position position, Position... positions) {
      list.add(position);
      if (positions != null)
        list.add(positions);
      return this;
    }
    
    public Builder add(Iterable<Position> positions) {
      list.addAll(positions);
      return this;
    }
    
    public Builder add(Supplier<Position> pos) {
      return add(pos.get());
    }
    
    public Builder add(float x, float y) {
      return add(GeoObject.position(x, y));
    }
    
    public Builder add(float x, float y, float z) {
      return add(GeoObject.position(x,y,z));
    }
    
    @Override
    protected Iterable<Position> coordinates() {
      return list.build();
    }
    
    @Override
    public MultiPoint doGet() {
      return new MultiPoint(this);
    }
    
  }

  protected MultiPoint(
    Builder builder) {
    super(builder);
  }

  @Override
  public Iterator<Position> iterator() {
    return coordinates().iterator();
  }

  @Override
  protected MultiPoint makeWithBoundingBox() {
    return new MultiPoint.Builder()
      .from(this)
      .add(this)
      .boundingBox(calculateBoundingBoxPositions(this)).get();
  }

  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<MultiPoint,Builder> {
    private static final long serialVersionUID = -2060301713159936281L;
    protected SerializedForm(MultiPoint obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return doReadResolve();
    }
    @SuppressWarnings("unchecked")
    @Override
    protected boolean handle(Builder builder, String key, Object val) {
      if ("coordinates".equals(key)) {
        Iterable<Position> list = (Iterable<Position>) val;
        builder.list.addAll(list);
        return true;
      }
      return false;
    }
    @Override
    protected MultiPoint.Builder builder() {
      return GeoMakers.multipoint();
    }
  }
}
