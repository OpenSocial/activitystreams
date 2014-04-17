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
package com.ibm.common.geojson.as2;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

import com.google.common.base.Enums;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.ibm.common.activitystreams.internal.Adapter;
import com.ibm.common.geojson.BoundingBox;
import com.ibm.common.geojson.CRS;
import com.ibm.common.geojson.Feature;
import com.ibm.common.geojson.FeatureCollection;
import com.ibm.common.geojson.GeoMakers;
import com.ibm.common.geojson.GeoObject;
import com.ibm.common.geojson.Geometry;
import com.ibm.common.geojson.Geometry.CoordinateGeometry;
import com.ibm.common.geojson.GeometryCollection;
import com.ibm.common.geojson.LineString;
import com.ibm.common.geojson.MultiLineString;
import com.ibm.common.geojson.MultiPoint;
import com.ibm.common.geojson.MultiPolygon;
import com.ibm.common.geojson.Point;
import com.ibm.common.geojson.Polygon;
import com.ibm.common.geojson.Position;

@SuppressWarnings("rawtypes")
public class GeoAdapter 
  extends Adapter<GeoObject> {

  @Override
  public JsonElement serialize(
    GeoObject geo, 
    Type type,
    JsonSerializationContext context) {
    
    JsonObject obj = new JsonObject();
    obj.add("type", context.serialize(geo.type(),GeoObject.Type.class));
    switch(geo.type()) {
    case POINT:
    case MULTIPOINT:
    case LINESTRING:
    case MULTILINESTRING:
    case MULTIPOLYGON:
    case POLYGON:
      CoordinateGeometry c = 
        (CoordinateGeometry) geo;
      obj.add(
        "coordinates", 
        context.serialize(
          c.coordinates(), 
          Iterable.class));
      break;
    case GEOMETRYCOLLECTION:
      GeometryCollection gc = 
        (GeometryCollection)geo;
      obj.add(
        "geometries",
        context.serialize(
          gc.geometries(),
          Iterable.class));
      break;
    case FEATURE:
      Feature feature = 
        (Feature) geo;
      if (feature.id() != null)
        obj.addProperty("id", feature.id());
      Geometry<?,?> geometry = feature.geometry();
      Map<String,Object> properties = feature.properties();
      if (geometry != null)
        obj.add(
          "geometry", 
          context.serialize(feature.geometry()));
      if (properties != null) 
        obj.add(
          "properties", 
          context.serialize(properties));
      break;
    case FEATURECOLLECTION:
      FeatureCollection fc = 
        (FeatureCollection)geo;
      obj.add(
        "features",
        context.serialize(
          fc.features(), 
          Iterable.class));
      break;
    default:
      break;
    
    }
    if (geo.boundingBox() != null) {
      BoundingBox bb = geo.boundingBox();
      obj.add("bbox", context.serialize(bb, Iterable.class));
    }
    if (geo.crs() != null) {
      CRS crs = geo.crs();
      JsonObject crsobj = new JsonObject();
      crsobj.addProperty("name", crs.type());
      if (crs.size() > 0)
        crsobj.add(
          "properties", 
          context.serialize(crs.properties()));
      obj.add("crs", crsobj);
    }
    return obj;
  }

  private Position toPosition(float[] pos) {
    Position.Builder b = 
      new Position.Builder();
    for (int n = 0; n < pos.length; n++) {
      if (n == 0) b.northing(pos[n]);
      else if (n == 1) b.easting(pos[n]);
      else if (n == 2) b.altitude(pos[n]);
      else b.additional(pos[n]);
    }
    return b.get();
  }
  
  @Override
  public GeoObject deserialize(
    JsonElement element, 
    Type type,
    JsonDeserializationContext context) 
      throws JsonParseException {
    GeoObject.Builder geo = null;
    checkArgument(element.isJsonObject());
    JsonObject obj = element.getAsJsonObject();
    checkArgument(obj.has("type"));
    GeoObject.Type et = 
      Enums.getIfPresent(
        GeoObject.Type.class, 
        obj.get("type").getAsString().toUpperCase()).orNull();
    checkArgument(et != null);
    switch(et) {
    case FEATURE:
      geo = GeoMakers.feature();
      break;
    case FEATURECOLLECTION:
      geo = GeoMakers.featureCollection();
      type = Feature.class;
      break;
    case GEOMETRYCOLLECTION:
      geo = GeoMakers.geometryCollection();
      type = Geometry.class;
      break;
    case LINESTRING:
      geo = GeoMakers.linestring();
      type = Position.class;
      break;
    case MULTILINESTRING:
      geo = GeoMakers.multiLineString();
      type = LineString.class;
      break;
    case MULTIPOINT:
      geo = GeoMakers.multipoint();
      type = Position.class;
      break;
    case MULTIPOLYGON:
      geo = GeoMakers.multiPolygon();
      type = Polygon.class;
      break;
    case POINT:
      geo = GeoMakers.point();
      type = null;
      break;
    case POLYGON:
      geo = GeoMakers.polygon();
      type = LineString.class;
      break;
    }
    
    for (Map.Entry<String,JsonElement> entry : obj.entrySet()) {
      JsonElement el = entry.getValue();
      String name = entry.getKey();
      if ("crs".equals(name)) {
        CRS.Builder cb = new CRS.Builder();
        JsonObject o = el.getAsJsonObject();
        if (o.has("type"))
          cb.type(o.get("type").getAsString());
        if (o.has("properties")) {
          JsonObject p = o.get("properties").getAsJsonObject();
          for (Map.Entry<String,JsonElement> e : p.entrySet()) {
            cb.set(e.getKey(), context.deserialize(e.getValue(), Object.class));
          }
        }
        geo.crs(cb.get());
      } else if ("properties".equals(name)) {
        geo.set(
          "properties", 
          context.deserialize(
            el, 
            Map.class));
      } else if ("bbox".equals(name)) {
        BoundingBox.Builder bb = 
          new BoundingBox.Builder();
        float[] points = context.deserialize(el, float[].class);
        bb.add(points);
        geo.boundingBox(bb.get());
      } else if ("features".equals(name)) {
        Feature[] features = context.deserialize(el, Feature[].class);
        FeatureCollection.Builder fcb = (FeatureCollection.Builder)geo;
        for (Feature f : features)
          fcb.add(f);
      } else if ("coordinates".equals(name)) {
        switch(et) {
        case LINESTRING: {
          LineString.Builder lsb = (LineString.Builder) geo;
          float[][] positions = context.deserialize(el, float[][].class);
          boolean ring = ring(positions);
          if (ring)
            lsb.linearRing();
          for (int n = 0; n < positions.length; n++) {
            if (!ring || (ring && n < positions.length - 1))
              lsb.add(toPosition(positions[n]));
          }
          break;
        } 
        case MULTIPOINT: {
          MultiPoint.Builder lsb = (MultiPoint.Builder) geo;
          float[][] positions = context.deserialize(el, float[][].class);
          for (float[] pos : positions)
            lsb.add(toPosition(pos));
          break;
        }
        case MULTILINESTRING: {
          MultiLineString.Builder mlb = (MultiLineString.Builder) geo;
          float[][][] positions = context.deserialize(el, float[][][].class);
          for (float[][] lines : positions) {
            LineString.Builder lsb = 
              GeoMakers.linestring();
            boolean ring = ring(lines);
            if (ring)
              lsb.linearRing();
            for (int n = 0; n < lines.length; n++) {
              if (!ring || (ring && n < lines.length - 1))
                lsb.add(toPosition(lines[n]));
            }
            for (float[] pos : lines)
              lsb.add(toPosition(pos));
            mlb.add(lsb);
          }
          break;
        }
        case POLYGON: {
          Polygon.Builder mlb = (Polygon.Builder) geo;
          float[][][] positions = context.deserialize(el, float[][][].class);
          for (float[][] lines : positions) {
            LineString.Builder lsb = 
              GeoMakers.linestring();
            for (float[] pos : lines)
              lsb.add(toPosition(pos));
            mlb.add(lsb);
          }
          break;
        }
        case MULTIPOLYGON: {
          MultiPolygon.Builder mpb = (MultiPolygon.Builder) geo;
          float[][][][] positions = context.deserialize(el, float[][][][].class);
          for (float[][][] polygons : positions) {
            Polygon.Builder pb = GeoMakers.polygon();
            for (float[][] lines : polygons) {
              LineString.Builder lsb = 
                GeoMakers.linestring();
              for (float[] pos : lines)
                lsb.add(toPosition(pos));
              pb.add(lsb);
            }
            mpb.add(pb);
          }
          break;
        }
        case POINT:
          Point.Builder pb = (Point.Builder)geo;
          float[] position = context.deserialize(el, float[].class);
          pb.position(toPosition(position));
          break;
        default:
          break;
        }
      } else if ("geometries".equals(name)) {
        Geometry[] geos = context.deserialize(el, Geometry[].class);
        GeometryCollection.Builder fcb = (GeometryCollection.Builder)geo;
        for (Geometry<?,?> g : geos)
          fcb.add(g);
      } else {
        if (el.isJsonArray()) {
          geo.set(name, context.deserialize(el, Object.class));
        } else if (el.isJsonObject()) {
          geo.set(name, context.deserialize(el, GeoObject.class));
        } else if (el.isJsonPrimitive()) {
          JsonPrimitive p = el.getAsJsonPrimitive();
          if (p.isBoolean())
            geo.set(name, p.getAsBoolean());
          else if (p.isNumber())
            geo.set(name, p.getAsNumber());
          else if (p.isString())
            geo.set(name, p.getAsString());
        }
      }
    }
    
    return geo.get();
  }

  private static boolean ring(float[][] line) {
    return ring(first(line),last(line));
  }
  
  private static boolean ring(float[] p1, float[] p2) {
    return Arrays.equals(p1,p2);
  }
  
  private static float[] first(float[][] line) {
    if (line.length == 0) return null;
    return line[0];
  }
  
  private static float[] last(float[][] line) {
    if (line.length == 0) return null;
    return line[line.length - 1];
  }
}
