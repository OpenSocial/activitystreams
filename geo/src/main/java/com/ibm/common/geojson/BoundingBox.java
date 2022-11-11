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
import java.io.Serializable;
import java.util.Iterator;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;

/**
 * A GeoJSON Bounding Box (see http://geojson.org/geojson-spec.html#bounding-boxes)
 * @author james
 */
public final class BoundingBox
  implements Iterable<Float>, Serializable {

  public static final class Builder 
    implements Supplier<BoundingBox> {

    private final ImmutableList.Builder<Float> bounds = 
      ImmutableList.builder();
    
    public Builder add(float value) {
      this.bounds.add(value);
      return this;
    }
    
    public Builder add(float... values) {
      if (values != null) 
        for (float v : values)
          this.bounds.add(v);
      return this;
    }
    
    @Override
    public BoundingBox get() {
      return new BoundingBox(this);
    }
    
  }
  
  private final ImmutableList<Float> bounds;
  
  BoundingBox(Builder builder) {
    this.bounds = builder.bounds.build();
  }

  @Override
  public Iterator<Float> iterator() {
    return bounds.iterator();
  }
  
  public String toString() {
    return MoreObjects.toStringHelper(BoundingBox.class)
      .addValue(bounds)
      .toString();
  }
  
  private static BoundingBox buildBoundingBox(
    ImmutableSortedSet<Float> xs,
    ImmutableSortedSet<Float> ys,
    ImmutableSortedSet<Float> zs) {
    BoundingBox.Builder bbox = 
      new BoundingBox.Builder()
        .add(xs.first())
        .add(ys.first());
    if (!zs.isEmpty())
      bbox.add(zs.first());
    bbox.add(xs.last());
    bbox.add(ys.last());
    if (!zs.isEmpty())
      bbox.add(zs.last());
    return bbox.get();
  }
  
  protected static BoundingBox calculateBoundingBoxLineStrings(Iterable<LineString> lineStrings) {
    ImmutableSortedSet.Builder<Float> xset = 
      ImmutableSortedSet.naturalOrder();
    ImmutableSortedSet.Builder<Float> yset = 
        ImmutableSortedSet.naturalOrder();
    ImmutableSortedSet.Builder<Float> zset = 
        ImmutableSortedSet.naturalOrder();
    for (LineString ls : lineStrings) {
      for (Position p : ls) {
        xset.add(p.northing());
        yset.add(p.easting());
        if (p.hasAltitude())
          zset.add(p.altitude());
      }
    }
    return buildBoundingBox(
      xset.build(), 
      yset.build(), 
      zset.build());
  }
  
  /**
   * Calculate the Bounding Box for a collection of Polygon objects
   * @param polygons Iterable&ltPolygon>
   * @return BoundingBox
   */
  public static BoundingBox calculateBoundingBoxPolygons(Iterable<Polygon> polygons) {
    ImmutableSortedSet.Builder<Float> xset = 
      ImmutableSortedSet.naturalOrder();
    ImmutableSortedSet.Builder<Float> yset = 
        ImmutableSortedSet.naturalOrder();
    ImmutableSortedSet.Builder<Float> zset = 
        ImmutableSortedSet.naturalOrder();  
    for (Polygon polygon : polygons) {
      for (LineString line : polygon) {
        for (Position pos : line) {
          xset.add(pos.northing());
          yset.add(pos.easting());
          if (pos.hasAltitude())
            zset.add(pos.altitude());          
        }
      }
    }
    return buildBoundingBox(
        xset.build(), 
        yset.build(), 
        zset.build());
  }
  
  protected static BoundingBox calculateBoundingBoxPositions(Iterable<Position> positions) {
    ImmutableSortedSet.Builder<Float> xset = 
      ImmutableSortedSet.naturalOrder();
    ImmutableSortedSet.Builder<Float> yset = 
        ImmutableSortedSet.naturalOrder();
    ImmutableSortedSet.Builder<Float> zset = 
        ImmutableSortedSet.naturalOrder();
    for (Position pos : positions) {
      xset.add(pos.northing());
      yset.add(pos.easting());
      if (pos.hasAltitude())
        zset.add(pos.altitude());
    }
    return buildBoundingBox(
      xset.build(), 
      yset.build(), 
      zset.build());
  }

  protected static BoundingBox calculateBoundingBox(Position position) {
    BoundingBox.Builder bbox = 
      new BoundingBox.Builder();
    bbox.add(position.northing());
    bbox.add(position.easting());
    if (position.hasAltitude())
      bbox.add(position.altitude());
    return bbox.get();    
  }
  
  private static void addValues(
    ImmutableSortedSet.Builder<Float> xset,
    ImmutableSortedSet.Builder<Float> yset,
    ImmutableSortedSet.Builder<Float> zset,
    Iterable<Position> positions) {
    for (Position position : positions) {
      xset.add(position.northing());
      yset.add(position.easting());
      if (position.hasAltitude())
        zset.add(position.altitude());
    }
  }
  
  private static void addValuesLineString(
      ImmutableSortedSet.Builder<Float> xset,
      ImmutableSortedSet.Builder<Float> yset,
      ImmutableSortedSet.Builder<Float> zset,
      Iterable<LineString> lines) {
    for (LineString ls : lines) {
      addValues(xset,yset,zset,ls);
    }
  }
  
  private static void addValuesPolygon(
      ImmutableSortedSet.Builder<Float> xset,
      ImmutableSortedSet.Builder<Float> yset,
      ImmutableSortedSet.Builder<Float> zset,
      Iterable<Polygon> polygons) {
    for (Polygon poly : polygons) {
      addValuesLineString(xset,yset,zset,poly);
    }
  }
  
  protected static BoundingBox calculateBoundingBox(Geometry<?,?> geometry) {
    return calculateBoundingBoxGeometries(
      ImmutableList.<Geometry<?,?>>of(geometry));
  }
  
  @SuppressWarnings("unchecked")
  protected static BoundingBox calculateBoundingBoxGeometries(
    Iterable<Geometry<?,?>> geometries) {
      ImmutableSortedSet.Builder<Float> xset = 
        ImmutableSortedSet.naturalOrder();
      ImmutableSortedSet.Builder<Float> yset = 
          ImmutableSortedSet.naturalOrder();
      ImmutableSortedSet.Builder<Float> zset = 
          ImmutableSortedSet.naturalOrder();
      for (Geometry<?,?> geo : geometries) {
        switch(geo.type()) {
        case POINT:
          Point point = (Point) geo;
          Position position = Iterables.getFirst(point.coordinates(),null);
          xset.add(position.northing());
          yset.add(position.easting());
          if (position.hasAltitude())
            zset.add(position.altitude());
          break;
        case LINESTRING:
        case MULTIPOINT:
          addValues(xset,yset,zset,(Iterable<Position>)geo);
          break;
        case MULTILINESTRING:
        case POLYGON:
          addValuesLineString(xset,yset,zset,(Iterable<LineString>)geo);
          break;
        case MULTIPOLYGON:
          addValuesPolygon(xset,yset,zset,(Iterable<Polygon>)geo);
          break;
        default:
          break; 
        }
      }
      return buildBoundingBox(
        xset.build(),
        yset.build(),
        zset.build());
  }
  
  protected static BoundingBox calculateBoundingBoxFeatures(Iterable<Feature> features) {
    ImmutableList.Builder<Geometry<?,?>> list = 
      ImmutableList.builder();
    for (Feature feature : features) {
      Geometry<?,?> geometry = feature.geometry();
      if (geometry != null) 
        list.add(geometry);
    }
    return calculateBoundingBoxGeometries(list.build());
  }
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm implements Serializable {
    private static final long serialVersionUID = -2060301713159936285L;
    private ImmutableList<Float> bounds;
    protected SerializedForm(BoundingBox obj) {
      this.bounds = obj.bounds;
    }
    Object readResolve() throws ObjectStreamException {
      BoundingBox.Builder builder = 
        new BoundingBox.Builder();
      builder.bounds.addAll(bounds);
      return builder.get();
    }
  }
}
