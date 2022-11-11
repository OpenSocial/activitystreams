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
import java.time.Duration;
import java.time.ZonedDateTime;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.LinkValue;

/**
 * The Legacy "task" objectType. Task objects in AS 1.0 are essentially
 * pending Activity statements.
 * @author james
 *
 */
public class Task 
  extends ASObject {

  public static final class Builder 
    extends ASObject.AbstractBuilder<Task, Builder> {

    Builder() {
      objectType("task");
    }
    
    /**
     * Set the actor
     * @param actor ASObject
     * @return Builder
     */
    public Builder actor(ASObject actor) {
      return set("actor", actor);
    }
    
    /**
     * Set the actor
     * @param actor Supplier&lt;? extends ASObject>
     * @return Builder
     */
    public Builder actor(Supplier<? extends ASObject> actor) {
      return actor(actor.get());
    }
    
    /**
     * Set the object
     * @param object ASObject
     * @return Builder
     */
    public Builder object(ASObject object) {
      return set("object", object);
    }
    
    /**
     * Set the object
     * @param object Supplier&lt;? extends ASObject>
     * @return Builder
     */
    public Builder object(Supplier<? extends ASObject> object) {
      return object(object.get());
    }
    
    /**
     * Set one or more prerequisite taskss
     * @param task Task
     * @param tasks Task[] optional vararg
     * @return Builder
     */
    public Builder prerequisites(Task task, Task... tasks) {
      if (task != null)
        link("prerequisites",task);
      if (tasks != null)
        for (Task t : tasks)
          link("prerequisites", t);
      return this;
    }
    
    /**
     * Set one or more prerequisite tasks
     * @param tasks Iterable&lt;Task>
     * @return Builder
     */
    public Builder prerequisites(Iterable<Task> tasks) {
      if (tasks != null)
        for (Task task : tasks)
          link("prerequisites", task);
      return this;
    }
    
    /**
     * Set an optional prerequisite
     * @param task Supplier&lt;? extends Task>
     * @return Builder
     */
    public Builder prerequisites(Supplier<? extends Task> task) {
      return prerequisites(task.get());
    }
    
    /**
     * Specify one or more other tasks that this task supersedes
     * @param task Task
     * @param tasks Task[] optional vararg
     * @return Builder
     */
    public Builder supersedes(Task task, Task... tasks) {
      if (task != null)
        link("supersedes",task);
      if (tasks != null)
        for (Task t : tasks)
          link("supersedes", t);
      return this;
    }
    
    /**
     * Specify one or more other tasks that this task supersedes
     * @param tasks Iterable&lt;Task>
     * @return Builder
     */
    public Builder supersedes(Iterable<Task> tasks) {
      if (tasks != null)
        for (Task task : tasks)
          link("supersedes", task);
      return this;
    }
    
    /**
     * Specify a task that this task supersedes
     * @param task Supplier&lt;? extends Task>
     * @return Builder
     */
    public Builder supersedes(Supplier<? extends Task> task) {
      return supersedes(task.get());
    }
    
    /**
     * Indicates whether this task is required or not
     * @param on boolean
     * @return Builder
     */
    public Builder required(boolean on) {
      return set("required", on);
    }
    
    /**
     * Indicates that this task is required
     * @return Builder
     */
    public Builder required() {
      return required(true);
    }
    
    /**
     * Specifies the verb for this task
     * @param verb String
     * @return Builder
     */
    public Builder verb(String verb) {
      return set("verb", verb);
    }
    
    /**
     * Specifies the due date for this task
     * @param dt DateTime
     * @return Builder
     */
    public Builder by(ZonedDateTime dt) {
      return this._dt("by", dt);
    }
    
    /**
     * Specifies that this task is due right now
     * @return Builder
     */
    public Builder byNow() {
      return this._dtNow("by");
    }
    
    /**
     * Specifies the due date for this task in terms of a specific
     * duration of time from right now
     * @param duration ReadableDuration
     * @return Builder
     */
    public Builder byFromNow(Duration duration) {
      return this._dtFromNow("by", duration);
    }
    
    /**
     * Specifies the due date for this task in terms of a specific
     * duration of time from the given instant;
     * @param dt DateTime
     * @param duration ReadableDuration
     * @return Builder
     */
    public Builder by(ZonedDateTime dt, Duration duration) {
      return this._dtFrom("by", dt, duration);
    }
    
    /**
     * Get the completed Task object
     */
    public Task get() {
      return new Task(this);
    }
    
  }
  
  private Task(Builder builder) {
    super(builder);
  }
  
  /**
   * Get the actor
   * @return &lt;A extends ASObject>A
   */
  public <A extends ASObject>A actor() {
    return this.<A>get("actor");
  }
  
  /**
   * Get the object
   * @return &lt;A extends ASObject>A
   */
  public <A extends ASObject>A object() {
    return this.<A>get("object");
  }
  
  /**
   * Get the due date
   * @return DateTime
   */
  public ZonedDateTime by() {
    return getDateTime("by");
  }
  
  /**
   * Get the verb
   * @return String
   */
  public String verb() {
    return getString("verb");
  }
  
  /**
   * Return true if this task is required
   * @return boolean
   */
  public boolean required() {
    return getBoolean("required");
  }
  
  /**
   * Return the listing of other tasks superseded by this one
   * (will return an empty iterable if there are no superseded
   * tasks)
   * @return Iterable&lt;Task>
   */
  public Iterable<Task> supersedes() {
    return transform(links("supersedes",filter), transformer);
  }
  
  /**
   * Return the listing of other tasks upon which this task depends.
   * (will return an empty iterable if there are no prerequisite
   * tasks)
   * @return Iterable&lt;Task>
   */
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
  
  // Java Serialization Support
  
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
