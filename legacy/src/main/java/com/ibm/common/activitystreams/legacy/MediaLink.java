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

public final class MediaLink
  extends AbstractWritable
  implements Iterable<String>, Serializable {

  public static final class Builder
    extends AbstractWritableBuilder<MediaLink,Builder>
    implements Supplier<MediaLink> {

    private Map<String,Object> map = 
      Maps.newHashMap();
    
    public Builder duration(int duration) {
      map.put("duration", duration);
      return this;
    }
    
    public Builder height(int height) {
      map.put("height", height);
      return this;
    }
    
    public Builder width(int width) {
      map.put("width", width);
      return this;
    }
    
    public Builder url(String url) {
      map.put("url", url);
      return this;
    }
    
    public Builder set(String key, Object val) {
      map.put(key,val);
      return this;
    }
    
    public MediaLink get() {
      return new MediaLink(this);
    }
    
  }
  
  private final ImmutableMap<String,Object> map;
  
  private MediaLink(Builder builder) {
    super(builder);
    this.map = ImmutableMap.copyOf(builder.map);
  }
  
  public String url() {
    return (String)map.get("url");
  }
  
  public int duration() {
    return (Integer)map.get("duration");
  }
  
  public int height() {
    return (Integer)map.get("height");
  }
  
  public int width() {
    return (Integer)map.get("width");
  }
  
  @SuppressWarnings("unchecked")
  public <T>T get(String key) {
    return (T)map.get(key);
  }

  public Iterator<String> iterator() {
    return map.keySet().iterator();
  }
  
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
