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

import static com.google.common.collect.Iterables.getFirst;
import static com.ibm.common.geojson.BoundingBox.calculateBoundingBox;

import com.ibm.common.geojson.Geometry.CoordinateGeometry;

public final class Point 
  extends CoordinateGeometry<Point,Position,Position> {

  public static final class Builder 
    extends CoordinateGeometry.Builder<Position,Position,Point,Builder> {

    protected Position position;
    
    public Builder() {
      type(GeoObject.Type.POINT);
    }
    
    public Builder position(float x, float y, float z) {
      this.position = GeoObject.position(x, y, z);
      return this;
    }
    
    public Builder position(float x, float y) {
      this.position = GeoObject.position(x,y);
      return this;
    }
    
    public Builder position(Position position) {
      this.position = position;
      return this;
    }
    
    public Builder position(Supplier<Position> position) {
      return position(position.get());
    }
    
    @Override
    protected Position coordinates() {
      return position;
    }
    
    @Override
    public Point doGet() {
      return new Point(this);
    }
    
  }
  
  Point(Builder builder) {
    super(builder);
  }

  @Override
  public Iterator<Position> iterator() {
    return coordinates().iterator();
  }


  @Override
  protected Point makeWithBoundingBox() {
    Position pos = getFirst(coordinates(),null);
    return new Point.Builder()
      .from(this)
      .position(pos)
      .boundingBox(calculateBoundingBox(pos))
      .get();
  }

  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<Point,Builder> {
    private static final long serialVersionUID = -2060301713159936281L;
    protected SerializedForm(Point obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return doReadResolve();
    }
    @Override
    protected boolean handle(Builder builder, String key, Object val) {
      if ("coordinates".equals(key)) {
        builder.position = (Position) val;
        return true;
      }
      return false;
    }
    @Override
    protected Point.Builder builder() {
      return GeoMakers.point();
    }
  }
}
