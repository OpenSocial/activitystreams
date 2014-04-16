package com.ibm.common.geojson.as2;

import com.ibm.common.activitystreams.IO;
import com.ibm.common.activitystreams.internal.EnumAdapter;
import com.ibm.common.activitystreams.internal.Model;
import com.ibm.common.activitystreams.internal.Schema;
import com.ibm.common.activitystreams.internal.Schema.Builder;
import com.ibm.common.activitystreams.util.Module;
import com.ibm.common.geojson.AS1Position;
import com.ibm.common.geojson.Address;
import com.ibm.common.geojson.GeoObject;
import com.ibm.common.geojson.Place;

@SuppressWarnings("deprecation")
public final class GeoModule 
  implements Module {

  public static final Module instance = 
    new GeoModule();
  
  public static final Model place = 
    Schema.object.template()
    .type(Place.class, Place.Builder.class)
    .as("geo", GeoObject.class)
    .as("address", Address.class)
    .as("position", AS1Position.class)
    .get();
  
  public static final Model address = 
    Schema.object.template()
      .type(Address.class, Address.Builder.class)
      .string(
        "country", 
        "formatted", 
        "locality", 
        "postalCode", 
        "region", 
        "streetAddress")
      .get();
  
  public static final Model as1Position = 
    Schema.object.template()
      .type(AS1Position.class, AS1Position.Builder.class)
      .floatValue(
        "latitude", 
        "longitude", 
        "altitude")
      .get();
  
  @Override
  public void apply(
    Builder builder) {
      builder.map("place", place)
             .map("address", address)
             .map("position", as1Position);
  }

  @Override
  public void apply(
    IO.Builder builder,
    Schema schema) {
    final GeoObjectAdapter base = 
      new GeoObjectAdapter(schema);
    final GeoAdapter geo = 
      new GeoAdapter();
    builder.hierarchicalAdapter(Place.class, base)
           .hierarchicalAdapter(Address.class, base)
           .hierarchicalAdapter(AS1Position.class, base)
           .hierarchicalAdapter(GeoObject.class, geo)
           .hierarchicalAdapter(
             GeoObject.Type.class, 
             new EnumAdapter<GeoObject.Type>(GeoObject.Type.class));
  }

}
