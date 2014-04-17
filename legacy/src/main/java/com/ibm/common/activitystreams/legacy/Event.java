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

import com.google.common.base.Supplier;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.Collection;

public class Event 
  extends ASObject {

  public static final class Builder 
    extends ASObject.AbstractBuilder<Event, Builder> {

    Builder() {
      objectType("event");
    }
    
    public Builder attendedBy(Collection collection) {
      return set("attendedBy", collection);
    }
    
    public Builder attendedBy(Supplier<? extends Collection> collection) {
      return attendedBy(collection.get());
    }
    
    public Builder attending(Collection collection) {
      return set("attending", collection);
    }
    
    public Builder attending(Supplier<? extends Collection> collection) {
      return attending(collection.get());
    }
    
    public Builder invited(Collection collection) {
      return set("invited", collection);
    }
    
    public Builder invited(Supplier<? extends Collection> collection) {
      return invited(collection.get());
    }
    
    public Builder maybeAttending(Collection collection) {
      return set("maybeAttending", collection);
    }
    
    public Builder maybeAttending(Supplier<? extends Collection> collection) {
      return maybeAttending(collection.get());
    }
    
    public Builder notAttendedBy(Collection collection) {
      return set("notAttendedBy", collection);
    }
    
    public Builder notAttendedBy(Supplier<? extends Collection> collection) {
      return notAttendedBy(collection.get());
    }
    
    public Builder notAttending(Collection collection) {
      return set("notAttending", collection);
    }
    
    public Builder notAttending(Supplier<? extends Collection> collection) {
      return notAttending(collection.get());
    }
    
    public Event get() {
      return new Event(this);
    }
    
  }
  
  private Event(Builder builder) {
    super(builder);
  }
  
  public Collection attendedBy() {
    return this.<Collection>get("attendedBy");
  }
  
  public Collection attending() {
    return this.<Collection>get("attending");
  }
  
  public Collection invited() {
    return this.<Collection>get("invited");
  }
  
  public Collection maybeAttending() {
    return this.<Collection>get("maybeAttending");
  }
  
  public Collection notAttendedBy() {
    return this.<Collection>get("notAttendedBy");
  }
  
  public Collection notAttending() {
    return this.<Collection>get("notAttending");
  }
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<Event> {
    private static final long serialVersionUID = -2060301713159936285L;
    protected SerializedForm(Event obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return super.doReadResolve();
    }
    protected Event.Builder builder() {
      return new Builder();
    }
  }
}
