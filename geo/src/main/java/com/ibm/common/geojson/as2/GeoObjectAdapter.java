package com.ibm.common.geojson.as2;

import java.lang.reflect.Type;

import com.google.common.collect.ImmutableSet;
import com.ibm.common.activitystreams.ASObject.AbstractBuilder;
import com.ibm.common.activitystreams.internal.ASObjectAdapter;
import com.ibm.common.activitystreams.internal.Model;
import com.ibm.common.activitystreams.internal.Schema;
import com.ibm.common.geojson.AS1Position;
import com.ibm.common.geojson.Address;
import com.ibm.common.geojson.GeoMakers;
import com.ibm.common.geojson.Place;

@SuppressWarnings("deprecation")
public class GeoObjectAdapter 
  extends ASObjectAdapter {

  protected GeoObjectAdapter(Schema schema) {
    super(schema);
  }

  private static final ImmutableSet<? extends Type> knownTypes = 
    ImmutableSet.of(Address.class,AS1Position.class,Place.class);

  @Override
  protected boolean knowsType(Type type) {
    if (super.knowsType(type))
      return true;
    return knownTypes.contains(type);
  }

  @Override
  protected AbstractBuilder<?, ?> builderFor(Type type) {
    if (super.knowsType(type))
      return super.builderFor(type);
    if (type == Address.class) {
      return GeoMakers.address();
    } else if (type == AS1Position.class) {
      return GeoMakers.as1Position();
    } else if (type == Place.class) {
      return GeoMakers.place();
    } else return null;
  }

  @Override
  protected Model modelFor(Type type) {
    if (super.knowsType(type))
      return super.modelFor(type);
    if (type == Address.class) {
      return schema().forObjectClassOrType(
        Address.Builder.class,  
        "address");
    } else if (type == AS1Position.class) {
      return schema().forObjectClassOrType(
        AS1Position.Builder.class, 
        "position");
    } else if (type == Place.class) {
      return schema().forObjectClassOrType(
        Place.Builder.class, 
        "place");
    } else return null;
  }
}
