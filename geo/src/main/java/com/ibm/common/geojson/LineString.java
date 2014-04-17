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

import static com.ibm.common.geojson.BoundingBox.calculateBoundingBoxPositions;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Iterables.size;
import static java.lang.String.format;

import java.io.ObjectStreamException;
import java.util.Iterator;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.ibm.common.geojson.Geometry.CoordinateGeometry;

/**
 * A GeoJSON LineString object
 * See http://geojson.org/geojson-spec.html#linestring.
 * 
 * The LineString can optionally be a Linear Ring if there are at least four
 * positions and the first and last position are equivalent.
 * 
 * <pre>
 *   LineString regularLineString = 
 *     GeoMakers.linestring()
 *       .add(1,2)
 *       .add(2,3)
 *       .get();
 *   // includes points (1,2) and (2,3)
 *       
 *   LineString linearRing = 
 *     GeoMakers.linestring()
 *       .linearRing()
 *       .add(1,2)
 *       .add(2,3)
 *       .add(3,4)
 *       .get()
 *   // includes points (1,2), (2,3), (3,4) and (1,2)
 * </pre>
 * 
 * @author james
 *
 */
public final class LineString 
  extends CoordinateGeometry<LineString,Position,Iterable<Position>> {

    public static final class Builder 
      extends CoordinateGeometry.Builder<Position,Iterable<Position>,LineString, Builder> {

    private final ImmutableList.Builder<Position> positions =
      ImmutableList.builder();
    private boolean ring;
    private final boolean nocheck;
   
    public Builder() {
      nocheck = false;
      type(Type.LINESTRING);
    }
    
    private Builder(boolean nocheck) {
      this.nocheck = nocheck;
      type(Type.LINESTRING);
    }
    
    /**
     * Specify that this LineString is a linearring
     * @return Builder
     */
    public Builder linearRing() {
      return linearRing(true);
    }
    
    public Builder linearRing(boolean on) {
      this.ring = on;
      return this;
    }
    
    /**
     * Add one or more positions to this linestring
     * @param position Position
     * @param positions Position[] optional vararg 
     * @return Builder
     */
    public Builder add(Position position, Position... positions) {
      this.positions.add(position);
      if (positions != null) 
        for (Position pos : positions)
          add(pos);
      return this;
    }
    
    /**
     * Add a single position to this linestring
     * @param x float
     * @param y float
     * @return Builder
     */
    public Builder add(float x, float y) {
      return add(GeoObject.position(x, y));
    }
    
    /**
     * Add a single position to this linestring
     * @param position Supplier&lt;Position> 
     * @return Builder
     */
    public Builder add(Supplier<Position> position) {
      return add(position.get());
    }
    
    /**
     * Add one or more positions to this linestring
     * @param positions Iterable&lt;Position>
     * @return Builder
     */
    public Builder add(Iterable<Position> positions) {
      this.positions.addAll(positions);
      return this;
    }
    
    /**
     * Add a single position to this linestring 
     * @param x float
     * @param y float
     * @param z float
     * @return Builder
     */
    public Builder add(float x, float y, float z) {
      return add(GeoObject.position(x,y,z));
    }

    public LineString doGet() {
      return new LineString(this);
    }

    @Override
    protected Iterable<Position> coordinates() {
      return positions.build();
    }
    
  }
    
  private final boolean ring;
    
  protected LineString(
    Builder builder) {
    super(builder);
    this.ring = builder.ring;
    int min = ring?3:2;
    checkArgument(
      builder.nocheck ||
      size(coordinates()) >= min, 
      format(
        "A LineString that %s a LinearRing MUST consist of at least %d positions",
        ring?"is":"is not",
        min));
  }

  public boolean linearRing() {
    return ring;
  }

  @Override
  /**
   * Get this LineStrings positions
   * @return Iterable&lt;Position>
   */
  public Iterable<Position> coordinates() {
    Iterable<Position> pos = super.coordinates();
    if (!ring)
      return pos;
    else {
      return size(pos) > 0 ? 
        concat(pos,ImmutableList.of(getFirst(pos,null))) :
        ImmutableList.<Position>of();
    }
  }

  @Override
  public Iterator<Position> iterator() {
    return coordinates().iterator();
  }
  
  /**
   * Return a copy of this linestring with a calculated bounding box
   */
  @Override
  public LineString makeWithBoundingBox() {
    return new LineString.Builder()
      .from(this)
      .add(this)
      .boundingBox(calculateBoundingBoxPositions(this))
      .get();
  }
  
  // Java Serialization support

  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<LineString,Builder> {
    private static final long serialVersionUID = -2060301713159936281L;
    boolean ring;
    protected SerializedForm(LineString obj) {
      super(obj);
      this.ring = obj.ring;
    }
    Object readResolve() throws ObjectStreamException {
      return doReadResolve();
    }
    @SuppressWarnings("unchecked")
    @Override
    protected boolean handle(Builder builder, String key, Object val) {
      if ("coordinates".equals(key)) {
        Iterable<Position> list = (Iterable<Position>) val;
        builder.positions.addAll(list);
        return true;
      }
      return false;
    }
    @Override
    protected LineString.Builder builder() {
      Builder builder = new Builder(true);
      builder.ring = ring;
      return builder;
    }
  }
}
