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
package com.ibm.common.activitystreams.legacy;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.ibm.common.activitystreams.util.AbstractWritable;

/**
 * An Activity Streams 1.0 Media Link object. 
 * @author james
 *
 */
public final class MediaLink
  extends AbstractWritable
  implements Iterable<String>, Serializable {

  public static final class Builder
    extends AbstractWritableBuilder<MediaLink,Builder>
    implements Supplier<MediaLink> {

    private Map<String,Object> map = 
      Maps.newHashMap();
    
    /**
     * When the media link refers to a video or audio resource,
     * the duration property indicates the total length in seconds
     * @param duration int
     * @return Builder
     */
    public Builder duration(int duration) {
      map.put("duration", duration);
      return this;
    }
    
    /**
     * When the media link refers to an object intended to be
     * displayed visually, such as a video or image, the 
     * height property specifies the display height in terms
     * of device independent pixels.
     * @param height int
     * @return Builder
     */
    public Builder height(int height) {
      map.put("height", height);
      return this;
    }
    
    /**
     * When the media link refers to an object intended to be
     * displayed visually, such as a video or image, the 
     * width property specifies the display width in terms
     * of device independent pixels.
     * @param height int
     * @return Builder
     */
    public Builder width(int width) {
      map.put("width", width);
      return this;
    }
    
    /**
     * The URL of the resource
     * @param url String
     * @return Builder
     */
    public Builder url(String url) {
      map.put("url", url);
      return this;
    }
    
    /**
     * Set an arbitrary property on the Media Link
     * @param key String
     * @param val Object
     * @return Builder
     */
    public Builder set(String key, Object val) {
      map.put(key,val);
      return this;
    }
    
    /**
     * Get the built MediaLink object
     */
    public MediaLink get() {
      return new MediaLink(this);
    }
    
  }
  
  private final ImmutableMap<String,Object> map;
  
  private MediaLink(Builder builder) {
    super(builder);
    this.map = ImmutableMap.copyOf(builder.map);
  }
  
  /**
   * Get the url property
   * @return
   */
  public String url() {
    return (String)map.get("url");
  }
  
  /**
   * When the media link refers to a video or audio resource,
   * the duration property indicates the total length in seconds
   * @return int
   */
  public int duration() {
    return (Integer)map.get("duration");
  }
  
  /**
   * When the media link refers to an object intended to be
   * displayed visually, such as a video or image, the 
   * height property specifies the display height in terms
   * of device independent pixels.
   * @return int
   */
  public int height() {
    return (Integer)map.get("height");
  }
  
  /**
   * When the media link refers to an object intended to be
   * displayed visually, such as a video or image, the 
   * width property specifies the display width in terms
   * of device independent pixels.
   * @return int
   */
  public int width() {
    return (Integer)map.get("width");
  }
  
  /**
   * Return the given property
   * @param key
   * @return &lt;T>T
   */
  @SuppressWarnings("unchecked")
  public <T>T get(String key) {
    return (T)map.get(key);
  }

  public Iterator<String> iterator() {
    return map.keySet().iterator();
  }
  
  // Java Serialization Support
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    implements Serializable {
    private static final long serialVersionUID = -2060301713159936285L;
    
    private ImmutableMap<String,Object> map;
    
    protected SerializedForm(MediaLink obj) {
      this.map = obj.map;
    }
    Object readResolve() throws ObjectStreamException {
      MediaLink.Builder builder = 
        new Builder();
      builder.map.putAll(map);
      return builder.get();
    }
  }
}
