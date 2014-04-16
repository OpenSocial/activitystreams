package com.ibm.common.geojson;

import java.io.ObjectStreamException;
import java.util.Iterator;

import com.google.common.base.Supplier;

import static com.google.common.collect.Iterables.getFirst;
import static com.ibm.common.geojson.BoundingBox.calculateBoundingBox;

import com.ibm.common.geojson.Geometry.CoordinateGeometry;

public final class Point 
  extends CoordinateGeometry<Point,Position,Position> {

  public static final class Builder 
    extends CoordinateGeometry.Builder<Position,Position,Point,Builder> {

    protected Position position;
    
    public Builder() {
      type(GeoObject.Type.POINT);
    }
    
    public Builder position(float x, float y, float z) {
      this.position = GeoObject.position(x, y, z);
      return this;
    }
    
    public Builder position(float x, float y) {
      this.position = GeoObject.position(x,y);
      return this;
    }
    
    public Builder position(Position position) {
      this.position = position;
      return this;
    }
    
    public Builder position(Supplier<Position> position) {
      return position(position.get());
    }
    
    @Override
    protected Position coordinates() {
      return position;
    }
    
    @Override
    public Point doGet() {
      return new Point(this);
    }
    
  }
  
  Point(Builder builder) {
    super(builder);
  }

  @Override
  public Iterator<Position> iterator() {
    return coordinates().iterator();
  }


  @Override
  protected Point makeWithBoundingBox() {
    Position pos = getFirst(coordinates(),null);
    return new Point.Builder()
      .from(this)
      .position(pos)
      .boundingBox(calculateBoundingBox(pos))
      .get();
  }

  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<Point,Builder> {
    private static final long serialVersionUID = -2060301713159936281L;
    protected SerializedForm(Point obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return doReadResolve();
    }
    @Override
    protected boolean handle(Builder builder, String key, Object val) {
      if ("coordinates".equals(key)) {
        builder.position = (Position) val;
        return true;
      }
      return false;
    }
    @Override
    protected Point.Builder builder() {
      return GeoMakers.point();
    }
  }
}
