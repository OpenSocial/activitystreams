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

import com.ibm.common.activitystreams.ASObject;

/**
 * A simple non-GeoJSON Address object modelled after the 
 * legacy Activity Streams 1.0 Address object 
 * (see https://github.com/activitystreams/activity-schema/blob/master/activity-schema.md)
 * 
 * @author james
 *
 */
public final class Address 
  extends ASObject {

  public static final class Builder 
    extends ASObject.AbstractBuilder<Address, Builder> {

    Builder() {
      objectType("address");
    }
    
    /**
     * The full mailing address formatted for display or use 
     * with a printed mailing label.
     * @param formatted String
     * @return Builder
     */
    public Builder formatted(String formatted) {
      return set("formatted", formatted);
    }
    
    /**
     * The street address including house number, street name, P.O. Box, 
     * apartment or unit number and extended multi-line address information.
     * @param streetAddress String
     * @return Builder
     */
    public Builder streetAddress(String streetAddress) {
      return set("streetAddress", streetAddress);
    }
    
    /**
     * The city or locality
     * @param locality String
     * @return Builder
     */
    public Builder locality(String locality) {
      return set("locality", locality);
    }
    
    /**
     * The state or region
     * @param region String
     * @return Builder
     */
    public Builder region(String region) {
      return set("region", region);
    }
    
    /**
     * The zip or postal code
     * @param postalCode String
     * @return Builder
     */
    public Builder postalCode(String postalCode) {
      return set("postalCode", postalCode);
    }
    
    /**
     * The country name component
     * @param country String
     * @return Builder
     */
    public Builder country(String country) {
      return set("country", country);
    }
    
    /**
     * Get the completed Address object
     */
    @Override
    public Address get() {
      return new Address(this);
    }
    
  }
  
  private Address(Builder builder) {
    super(builder);
  }
  
  /**
   * The full mailing address formatted for display or use 
   * with a printed mailing label.
   * @return String
   */
  public String formatted() {
    return getString("formatted");
  }
  
  /**
   * The street address including house number, street name, P.O. Box, 
   * apartment or unit number and extended multi-line address information.
   * @return String
   */
  public String streetAddress() {
    return getString("streetAddress");
  }
  
  /**
   * The city or locality
   * @return String
   */
  public String locality() {
    return getString("locality");
  }
  
  /**
   * The state or region
   * @return String
   */
  public String region() {
    return getString("region");
  }
  
  /**
   * The zip or postal code
   * @return String
   */
  public String postalCode() {
    return getString("postalCode");
  }
  
  /**
   * The country name component
   * @return String
   */
  public String country() {
    return getString("country");
  }
  
  // Java Serialization Support
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<Address> {
    private static final long serialVersionUID = -2060301713159936285L;
    protected SerializedForm(Address obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return super.doReadResolve();
    }
    protected Address.Builder builder() {
      return GeoMakers.address();
    }
  }
}
