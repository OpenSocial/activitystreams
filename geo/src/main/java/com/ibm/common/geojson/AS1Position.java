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

import static com.google.common.primitives.Floats.max;
import static com.google.common.primitives.Floats.min;

import java.io.ObjectStreamException;

import com.ibm.common.activitystreams.ASObject;

/**
 * Represents an Activity Streams 1.0 style position object
 * see (https://github.com/activitystreams/activity-schema/blob/master/activity-schema.md)
 * 
 * Use of the AS1Position object is deprecated. Use the GeoJSON mechanisms
 * instead
 * 
 * @author james
 * @deprecated Use Position
 */
public final class AS1Position 
  extends ASObject {

  public static final class Builder 
    extends ASObject.AbstractBuilder<AS1Position, Builder> {

    Builder() {
      objectType("position");
    }
    
    /**
     * Set the latitude
     * @param latitude float
     * @return Builder
     */
    public Builder latitude(float latitude) {
      return set("latitude", max(0f,min(90.0f,latitude)));
    }
    
    /**
     * Set the longitude
     * @param longitude float
     * @return Builder
     */
    public Builder longitude(float longitude) {
      return set("longitude", max(-180.0f,min(180.0f,longitude)));
    }
    
    /**
     * Set the altitude
     * @param altitude float
     * @return Builder
     */
    public Builder altitude(float altitude) {
      return set("altitude", altitude);
    }
    
    @Override
    public AS1Position get() {
      return new AS1Position(this);
    }
    
  }
  
  private AS1Position(Builder builder) {
    super(builder);
  }
  
  /**
   * Get the latitude 
   * @return float
   */
  public float latitude() {
    return max(0f,min(90.0f,getFloat("latitude")));
  }
  
  /**
   * Get the longitude
   * @return float
   */
  public float longitude() {
    return max(-180.0f,min(180.0f,getFloat("longitude")));
  }
  
  /**
   * Get the altitude. If the altitude property is not set, this 
   * will return Float.MIN_VALUE;
   * @return float
   */
  public float altitude() {
    return getFloat("altitude", Float.MIN_VALUE);
  }
  
  // Java Serialization Support
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<AS1Position> {
    private static final long serialVersionUID = -2060301713159936285L;
    protected SerializedForm(AS1Position obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return super.doReadResolve();
    }
    protected AS1Position.Builder builder() {
      return GeoMakers.as1Position();
    }
  }
}
