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

import static com.ibm.common.geojson.BoundingBox.calculateBoundingBoxFeatures;

import java.io.ObjectStreamException;
import java.util.Iterator;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * A GeoJSON FeatureCollection object
 * see http://geojson.org/geojson-spec.html#feature-collection-objects
 * @author james
 *
 */
public final class FeatureCollection 
  extends GeoObject<FeatureCollection>
  implements Iterable<Feature> {

  public static final class Builder 
    extends GeoObject.Builder<FeatureCollection, Builder> {

    private final ImmutableList.Builder<Feature> list =
      ImmutableList.builder();
    
    public Builder() {
      type(GeoObject.Type.FEATURECOLLECTION);
    }
    
    public Builder add(Iterable<Feature> features) {
      list.addAll(features);
      return this;
    }
    
    public Builder add(Feature feature, Feature... features) {
      list.add(feature);
      if (features != null)
        list.add(features);
      return this;
    }
    
    public Builder add(Supplier<Feature> feature) {
      return add(feature.get());
    }
    
    @Override
    public void preGet() {
      set("features", list.build());
    }
    
    @Override
    public FeatureCollection doGet() {
      return new FeatureCollection(this);
    }
    
  }
  
  FeatureCollection(Builder builder) {
    super(builder);
  }

  public Iterable<Feature> features() {
    return this.<Iterable<Feature>>get(
      "features",
      ImmutableList.<Feature>of());
  }
  
  public int size() {
    return Iterables.size(features());
  }
  
  public Feature get(int idx) {
    return Iterables.get(features(),idx);
  }
  
  public Iterator<Feature> iterator() {
    return features().iterator();
  }

  @Override
  protected FeatureCollection makeWithBoundingBox() {
    return new FeatureCollection.Builder()
      .from(this)
      .add(this)
      .boundingBox(calculateBoundingBoxFeatures(this))
      .get();
  }
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<FeatureCollection,FeatureCollection.Builder> {
    private static final long serialVersionUID = -2060301713159936281L;
    protected SerializedForm(FeatureCollection obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return doReadResolve();
    }
    @Override
    protected FeatureCollection.Builder builder() {
      return GeoMakers.featureCollection();
    }
  }
}
