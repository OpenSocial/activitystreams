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

/**
 * Makers for the various GeoJSON object types
 * @author james
 *
 */
@SuppressWarnings("deprecation")
public final class GeoMakers {

  private GeoMakers() {}

  public static Place.Builder place() {
    return new Place.Builder();
  }
  
  /** @deprecated **/
  public static AS1Position.Builder as1Position() {
    return new AS1Position.Builder();
  }
  
  /** @deprecated **/
  public static AS1Position as1Position(
    float latitude, 
    float longitude, 
    float altitude) {
    return as1Position()
      .longitude(longitude)
      .latitude(latitude)
      .altitude(altitude)
      .get();
  }
  
  /** @deprecated **/
  public static AS1Position as1Position(
    Position position) {
      return as1Position(
        position.northing(),
        position.easting(),
        position.altitude());
  }
  
  public static Position position(
    AS1Position position) {
    return position(
      position.latitude(), 
      position.longitude(), 
      position.altitude());
  }
  
  public static Address.Builder address() {
    return new Address.Builder();
  }
  
  public static Position position(float x, float y) {
    return GeoObject.position(x,y);
  }
  
  public static Position position(float x, float y, float z) {
    return GeoObject.position(x, y, z);
  }
  
  public static Point point(float x, float y) {
    return point().position(x,y).get();
  }
  
  public static Point point(float x, float y, float z) {
    return point().position(x, y, z).get();
  }
  
  public static Point point(Position position) {
    return point().position(position).get();
  }
  
  public static Point.Builder point() {
    return new Point.Builder();
  }
  
  public static MultiPoint.Builder multipoint() {
    return new MultiPoint.Builder();
  }
  
  public static LineString.Builder linestring() {
    return new LineString.Builder();
  }
  
  public static LineString.Builder linearRing() {
    return linestring().linearRing();
  }
  
  public static Feature.Builder feature() {
    return new Feature.Builder();
  }
  
  public static FeatureCollection.Builder featureCollection() {
    return new FeatureCollection.Builder();
  }
  
  public static GeometryCollection.Builder geometryCollection() {
    return new GeometryCollection.Builder();
  }
  
  public static MultiLineString.Builder multiLineString() {
    return new MultiLineString.Builder();
  }
  
  public static MultiPolygon.Builder multiPolygon() {
    return new MultiPolygon.Builder();
  }
  
  public static Polygon.Builder polygon() {
    return new Polygon.Builder();
  }
}
