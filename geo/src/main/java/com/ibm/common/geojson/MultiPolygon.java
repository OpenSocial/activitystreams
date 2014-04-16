package com.ibm.common.geojson;

import static com.ibm.common.geojson.BoundingBox.calculateBoundingBoxPolygons;

import java.io.ObjectStreamException;
import java.util.Iterator;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.ibm.common.geojson.Geometry.CoordinateGeometry;

public final class MultiPolygon 
  extends CoordinateGeometry<MultiPolygon, Polygon, Iterable<Polygon>>{

  public static final class Builder 
    extends CoordinateGeometry.Builder<Polygon,Iterable<Polygon>, MultiPolygon, Builder> {

    private final ImmutableList.Builder<Polygon> strings = 
      ImmutableList.builder();
    
    public Builder() {
      type(Type.MULTIPOLYGON);
    }
    
    public Builder add(Polygon poly, Polygon... polys) {
      // TODO: Check hole requirement
      this.strings.add(poly);
      if (polys != null)
        for (Polygon l : polys)
          add(l);
      return this;
    }
    
    public Builder add(Supplier<Polygon> poly) {
      return add(poly.get());
    }
    
    public Builder add(Iterable<Polygon> polygons) {
      this.strings.addAll(polygons);
      return this;
    }
    
    public MultiPolygon doGet() {
      return new MultiPolygon(this);
    }

    @Override
    protected Iterable<Polygon> coordinates() {
      return strings.build();
    }
    
  }
    
  protected MultiPolygon(
    Builder builder) {
    super(builder);
  }

  @Override
  public Iterator<Polygon> iterator() {
    return coordinates().iterator();
  }

  @Override
  protected MultiPolygon makeWithBoundingBox() {
    return new MultiPolygon.Builder()
      .from(this)
      .add(this)
      .boundingBox(calculateBoundingBoxPolygons(this))
      .get();
  }

  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<MultiPolygon,Builder> {
    private static final long serialVersionUID = -2060301713159936281L;
    protected SerializedForm(MultiPolygon obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return doReadResolve();
    }
    @SuppressWarnings("unchecked")
    @Override
    protected boolean handle(Builder builder, String key, Object val) {
      if ("coordinates".equals(key)) {
        Iterable<Polygon> list = (Iterable<Polygon>) val;
        builder.strings.addAll(list);
        return true;
      }
      return false;
    }
    @Override
    protected MultiPolygon.Builder builder() {
      return GeoMakers.multiPolygon();
    }
  }
}
