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

import static com.google.common.primitives.Floats.concat;
import static com.google.common.primitives.Floats.toArray;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Floats;

/**
 * A GeoJSON Position
 * see http://geojson.org/geojson-spec.html#positions. 
 * 
 * Position objects consist primarily of three distinct values:
 * a northing (latitude or x), easting (longitude or y) and altitude (z). 
 * 
 * @author james
 *
 */
public final class Position 
  implements Iterable<Float>, Serializable  {

  public static final class Builder 
    implements Supplier<Position> {
    
    private float x,y,z;
    private boolean hasz = false;
    private ImmutableList.Builder<Float> rest = 
        ImmutableList.builder();
    
    public Builder northing(float x) {
      this.x = x;
      return this;
    }
    
    public Builder easting(float y) {
      this.y = y;
      return this;
    }
    
    public Builder altitude(float z) {
      this.hasz = true;
      this.z = z;
      return this;
    }
    
    public Builder additional(float m) {
      this.rest.add(m);
      return this;
    }
    
    public Position get() {
      return new Position(this);
    }
    
  }
  
  private final float x,y,z;
  private final boolean hasz;
  private final float[] rest;
  
  Position(Builder builder) {
    this.x = builder.x;
    this.y = builder.y;
    this.z = builder.z;
    this.hasz = builder.hasz;
    this.rest = toArray(builder.rest.build());
  }

  public float northing() {
    return x;
  }
  
  public float easting() {
    return y;
  }
  
  public float altitude() {
    return z;
  }
  
  public boolean hasAltitude() {
    return hasz;
  }
  
  public int size() {
    return values().length;
  }
  
  private float[] values() {
    return hasz? 
      concat(new float[] {x,y,z}, rest) :
      concat(new float[] {x,y}, rest);
  }
  
  public String toString() {
    return Arrays.toString(values());
  }

  @Override
  public Iterator<Float> iterator() {
    return Floats.asList(values()).iterator();
  }

  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm implements Serializable {
    private static final long serialVersionUID = -2060301713159936285L;
    private float x,y,z;
    private boolean hasz;
    private float[] rest;
    protected SerializedForm(Position obj) {
     this.x = obj.x;
     this.y = obj.y;
     this.z = obj.z;
     this.hasz = obj.hasz;
     this.rest = obj.rest;
    }
    Object readResolve() throws ObjectStreamException {
      Position.Builder position = 
        new Position.Builder()
          .northing(x)
          .easting(y)
          .altitude(z);
      position.hasz = this.hasz;
      if (rest != null)
        for (float f : rest)
          position.additional(f);
      return position.get();
    }

  }
}
