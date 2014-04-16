package com.ibm.common.geojson;

import static com.ibm.common.geojson.BoundingBox.calculateBoundingBoxPositions;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Iterables.size;
import static java.lang.String.format;

import java.io.ObjectStreamException;
import java.util.Iterator;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.ibm.common.geojson.Geometry.CoordinateGeometry;

public final class LineString 
  extends CoordinateGeometry<LineString,Position,Iterable<Position>> {

    public static final class Builder 
      extends CoordinateGeometry.Builder<Position,Iterable<Position>,LineString, Builder> {

    private final ImmutableList.Builder<Position> positions =
      ImmutableList.builder();
    private boolean ring;
    private final boolean nocheck;
   
    public Builder() {
      nocheck = false;
      type(Type.LINESTRING);
    }
    
    private Builder(boolean nocheck) {
      this.nocheck = nocheck;
      type(Type.LINESTRING);
    }
    
    public Builder linearRing() {
      return linearRing(true);
    }
    
    public Builder linearRing(boolean on) {
      this.ring = on;
      return this;
    }
    
    public Builder add(Position position, Position... positions) {
      this.positions.add(position);
      if (positions != null) 
        for (Position pos : positions)
          add(pos);
      return this;
    }
    
    public Builder add(float x, float y) {
      return add(GeoObject.position(x, y));
    }
    
    public Builder add(Supplier<Position> position) {
      return add(position.get());
    }
    
    public Builder add(Iterable<Position> positions) {
      this.positions.addAll(positions);
      return this;
    }
    
    public Builder add(float x, float y, float z) {
      return add(GeoObject.position(x,y,z));
    }

    public LineString doGet() {
      return new LineString(this);
    }

    @Override
    protected Iterable<Position> coordinates() {
      return positions.build();
    }
    
  }
    
  private final boolean ring;
    
  protected LineString(
    Builder builder) {
    super(builder);
    this.ring = builder.ring;
    int min = ring?3:2;
    checkArgument(
      builder.nocheck ||
      size(coordinates()) >= min, 
      format(
        "A LineString that %s a LinearRing MUST consist of at least %d positions",
        ring?"is":"is not",
        min));
  }

  public boolean linearRing() {
    return ring;
  }

  @Override
  public Iterable<Position> coordinates() {
    Iterable<Position> pos = super.coordinates();
    if (!ring)
      return pos;
    else {
      return size(pos) > 0 ? 
        concat(pos,ImmutableList.of(getFirst(pos,null))) :
        ImmutableList.<Position>of();
    }
  }

  @Override
  public Iterator<Position> iterator() {
    return coordinates().iterator();
  }
  
  @Override
  public LineString makeWithBoundingBox() {
    return new LineString.Builder()
      .from(this)
      .add(this)
      .boundingBox(calculateBoundingBoxPositions(this))
      .get();
  }

  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<LineString,Builder> {
    private static final long serialVersionUID = -2060301713159936281L;
    boolean ring;
    protected SerializedForm(LineString obj) {
      super(obj);
      this.ring = obj.ring;
    }
    Object readResolve() throws ObjectStreamException {
      return doReadResolve();
    }
    @SuppressWarnings("unchecked")
    @Override
    protected boolean handle(Builder builder, String key, Object val) {
      if ("coordinates".equals(key)) {
        Iterable<Position> list = (Iterable<Position>) val;
        builder.positions.addAll(list);
        return true;
      }
      return false;
    }
    @Override
    protected LineString.Builder builder() {
      Builder builder = new Builder(true);
      builder.ring = ring;
      return builder;
    }
  }
}
