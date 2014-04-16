package com.ibm.common.geojson;

import java.io.ObjectStreamException;

import com.ibm.common.activitystreams.ASObject;

public final class Address 
  extends ASObject {

  public static final class Builder 
    extends ASObject.AbstractBuilder<Address, Builder> {

    Builder() {
      objectType("address");
    }
    
    public Builder formatted(String formatted) {
      return set("formatted", formatted);
    }
    
    public Builder streetAddress(String streetAddress) {
      return set("streetAddress", streetAddress);
    }
    
    public Builder locality(String locality) {
      return set("locality", locality);
    }
    
    public Builder region(String region) {
      return set("region", region);
    }
    
    public Builder postalCode(String postalCode) {
      return set("postalCode", postalCode);
    }
    
    public Builder country(String country) {
      return set("country", country);
    }
    
    @Override
    public Address get() {
      return new Address(this);
    }
    
  }
  
  private Address(Builder builder) {
    super(builder);
  }
  
  public String formatted() {
    return getString("formatted");
  }
  
  public String streetAddress() {
    return getString("streetAddress");
  }
  
  public String locality() {
    return getString("locality");
  }
  
  public String region() {
    return getString("region");
  }
  
  public String postalCode() {
    return getString("postalCode");
  }
  
  public String country() {
    return getString("country");
  }
  
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
