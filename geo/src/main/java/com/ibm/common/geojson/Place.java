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

public final class Place extends ASObject {

  public static final class Builder 
    extends ASObject.AbstractBuilder<Place, Builder> {

    Builder() {
      objectType("place");
    }
    
    public Builder address(Address address) {
      return set("address", address);
    }
    
    public Builder address(Supplier<Address> address) {
      return address(address.get());
    }
    
    @SuppressWarnings("deprecation") 
    public Builder position(AS1Position position) {
      return set("position", position);
    }
    
    @SuppressWarnings("deprecation")
    public Builder position(Supplier<AS1Position> position) {
      return position(position.get());
    }
    
    @SuppressWarnings("deprecation")
    public Builder position(
      float latitude, 
      float longitude, 
      float altitude) {
      return position(GeoMakers.as1Position(latitude, longitude, altitude));
    }
    
    public Builder geo(GeoObject<?> geo) {
      return set("geo", geo);
    }
    
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
  
  public Address address() {
    return this.<Address>get("address");
  }
  
  @SuppressWarnings("deprecation")
  public AS1Position position() {
    return this.<AS1Position>get("position");
  }
  
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
