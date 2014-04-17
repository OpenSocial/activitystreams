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

import com.google.common.base.Supplier;
import com.ibm.common.activitystreams.ASObject;

/**
 * A legacy Activity Streams 1.0 Place object
 * see https://github.com/activitystreams/activity-schema/blob/master/activity-schema.md.
 * 
 * Place objects have additional address, position and geo properties that
 * describe the location.
 * @author james
 *
 */
public final class Place extends ASObject {

  public static final class Builder 
    extends ASObject.AbstractBuilder<Place, Builder> {

    Builder() {
      objectType("place");
    }
    
    /**
     * Set the address
     * @param address Address
     * @return Builder
     */
    public Builder address(Address address) {
      return set("address", address);
    }
    
    /**
     * Set the address
     * @param address Supplier&lt;Address>
     * @return Builder
     */
    public Builder address(Supplier<Address> address) {
      return address(address.get());
    }
    
    /**
     * Set the position 
     * @deprecated
     * @param position AS1Position
     * @return Builder
     */
    public Builder position(AS1Position position) {
      return set("position", position);
    }
    
    /**
     * Set the position
     * @deprecated 
     * @param position Supplier&lt;AS1Position>
     * @return Builder
     */
    public Builder position(Supplier<AS1Position> position) {
      return position(position.get());
    }
    
    /**
     * Set the position
     * @param latitude float
     * @param longitude float
     * @param altitude float
     * @return Builder
     * @deprecated
     */
    public Builder position(
      float latitude, 
      float longitude, 
      float altitude) {
      return position(GeoMakers.as1Position(latitude, longitude, altitude));
    }
    
    /**
     * Set the geo property
     * @param geo GeoObject&lt;?> Any GeoJSON object can be used
     * @return Builder
     */ 
    public Builder geo(GeoObject<?> geo) {
      return set("geo", geo);
    }
    
    /**
     * Set the geo property
     * @param geo Supplier&lt;? extends GeoObject&lt;?>> Any GeoJSON object can be used
     * @return Builder
     */ 
    public Builder geo(Supplier<? extends GeoObject<?>> geo) {
      return geo(geo.get());
    }
    
    @Override
    public Place get() {
      return new Place(this);
    }
    
  }
  
  private Place(Builder builder) {
    super(builder);
  }
  
  /**
   * Get the address
   * @return Address
   */
  public Address address() {
    return this.<Address>get("address");
  }
  
  /**
   * Get the position
   * @deprecated
   * @return AS1Position
   */
  public AS1Position position() {
    return this.<AS1Position>get("position");
  }
  
  /**
   * Get the geo property
   * @return &lt;G extends GeoObject&lt;?>>G
   */
  public <G extends GeoObject<?>>G geo() {
    return this.<G>get("geo");
  }
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<Place> {
    private static final long serialVersionUID = -2060301713159936285L;
    protected SerializedForm(Place obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return super.doReadResolve();
    }
    protected Place.Builder builder() {
      return GeoMakers.place();
    }
  }
}
