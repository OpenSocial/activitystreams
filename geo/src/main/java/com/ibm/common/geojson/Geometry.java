package com.ibm.common.geojson;

import com.google.common.collect.ImmutableList;

public abstract class Geometry<G extends Geometry<G,M>, M>
  extends GeoObject<G>
  implements Iterable<M> {

  private static final long serialVersionUID = -6184360762496309625L;

  protected Geometry(Builder<?,?> builder) {
    super(builder);
  }
  
  public static abstract class CoordinateGeometry<G extends CoordinateGeometry<G,M,P>, M,P> 
    extends Geometry<G,M> {

    private static final long serialVersionUID = -6502745908455054826L;

    protected static abstract class Builder
      <M,P,C extends CoordinateGeometry<C,M,P>, B extends Builder<M,P,C,B>>
      extends GeoObject.Builder<C, B> {

      protected abstract P coordinates();
      
      public void preGet() {
        set("coordinates", coordinates());
      }
      
      protected abstract C doGet();
    }
    
    protected CoordinateGeometry(Builder<M,P,?, ?> builder) {
      super(builder);
    }
    
    @SuppressWarnings("unchecked")
    public Iterable<M> coordinates() {
      Object o = get("coordinates");
      if (o instanceof Iterable)
        return (Iterable<M>)o;
      else
        return ImmutableList.<M>of((M)o);
    }
    
  }
 
}
