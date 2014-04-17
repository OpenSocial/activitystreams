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

import static com.google.common.collect.Iterables.transform;

import java.io.ObjectStreamException;

import org.joda.time.DateTime;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadablePeriod;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.LinkValue;

public class Task 
  extends ASObject {

  public static final class Builder 
    extends ASObject.AbstractBuilder<Task, Builder> {

    Builder() {
      objectType("task");
    }
    
    public Builder actor(ASObject actor) {
      return set("actor", actor);
    }
    
    public Builder actor(Supplier<? extends ASObject> actor) {
      return actor(actor.get());
    }
    
    public Builder object(ASObject object) {
      return set("object", object);
    }
    
    public Builder object(Supplier<? extends ASObject> object) {
      return object(object.get());
    }
    
    public Builder prerequisites(Task task, Task... tasks) {
      if (task != null)
        link("prerequisites",task);
      if (tasks != null)
        for (Task t : tasks)
          link("prerequisites", t);
      return this;
    }
    
    public Builder prerequisites(Supplier<? extends Task> task) {
      return prerequisites(task.get());
    }
    
    public Builder supersedes(Task task, Task... tasks) {
      if (task != null)
        link("supersedes",task);
      if (tasks != null)
        for (Task t : tasks)
          link("supersedes", t);
      return this;
    }
    
    public Builder supersedes(Supplier<? extends Task> task) {
      return supersedes(task.get());
    }
    
    public Builder required(boolean on) {
      return set("required", on);
    }
    
    public Builder required() {
      return required(true);
    }
    
    public Builder verb(String verb) {
      return set("verb", verb);
    }
    
    public Builder by(DateTime dt) {
      return this._dt("by", dt);
    }
    
    public Builder byNow() {
      return this._dtNow("by");
    }
    
    public Builder byFromNow(ReadableDuration duration) {
      return this._dtFromNow("by", duration);
    }
    
    public Builder byFromNow(ReadablePeriod period) {
      return this._dtFromNow("by", period);
    }
    
    public Builder by(DateTime dt, ReadableDuration duration) {
      return this._dtFrom("by", dt, duration);
    }
    
    public Builder by(DateTime dt, ReadablePeriod period) {
      return this._dtFrom("by", dt, period);
    }
    
    public Task get() {
      return new Task(this);
    }
    
  }
  
  private Task(Builder builder) {
    super(builder);
  }
  
  public <A extends ASObject>A actor() {
    return this.<A>get("actor");
  }
  
  public <A extends ASObject>A object() {
    return this.<A>get("object");
  }
  
  public DateTime by() {
    return getDateTime("by");
  }
  
  public String verb() {
    return getString("verb");
  }
  
  public boolean required() {
    return getBoolean("required");
  }
  
  public Iterable<Task> supersedes() {
    return transform(links("supersedes",filter), transformer);
  }
  
  public Iterable<Task> prerequisites() {
    return transform(links("prerequisites",filter), transformer);
  }
  
  private static final Predicate<LinkValue> filter = 
    new Predicate<LinkValue>() {
      public boolean apply(LinkValue input) {
        return input instanceof Task;
      }
  };
  
  private static final Function<LinkValue,Task> transformer = 
    new Function<LinkValue,Task>() {
      public Task apply(LinkValue input) {
        return (Task)input;
      }
    
  };
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<Task> {
    private static final long serialVersionUID = -2060301713159936285L;
    protected SerializedForm(Task obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return super.doReadResolve();
    }
    protected Task.Builder builder() {
      return new Builder();
    }
  }
}
