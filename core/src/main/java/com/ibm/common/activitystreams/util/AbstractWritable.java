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
package com.ibm.common.activitystreams.util;

import static com.ibm.common.activitystreams.IO.makeDefault;

import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.google.common.base.Supplier;
import com.ibm.common.activitystreams.IO;
import com.ibm.common.activitystreams.Writable;

/**
 * Utility base class for Writable instances.
 * @author james
 * @version $Revision: 1.0 $
 */
public abstract class AbstractWritable
  implements Writable {

  public static abstract class AbstractWritableBuilder
    <X extends Writable, B extends AbstractWritableBuilder<X,B>>
    implements Supplier<X> {
    
    private IO io = null;

    /**
     * Set the IO instance that should be used 
     * by default for writing instances of this
     * object.
     */
    @SuppressWarnings("unchecked")
    public B writeUsing(IO io) {
      this.io = io;
      return (B)this;
    }

    public void writeTo(OutputStream out) {
      get().writeTo(out);
    }
    
    public Future<?> writeTo(OutputStream out, ExecutorService executor) {
      return get().writeTo(out,executor);
    }

    public void writeTo(Writer out) {
      get().writeTo(out);
    }
    
    public Future<?> writeTo(Writer out, ExecutorService executor) {
      return get().writeTo(out,executor);
    }


    public void writeTo(OutputStream out, IO io) {
      get().writeTo(out,io);
    }
    
    public Future<?> writeTo(OutputStream out, IO io, ExecutorService executor) {
      return get().writeTo(out, io, executor);
    }

    public void writeTo(Writer out, IO io) {
      get().writeTo(out,io);
    }
    
    public Future<?> writeTo(Writer out, IO io, ExecutorService executor) {
      return get().writeTo(out, io, executor);
    }
    
  }
  
  private final IO io;

  protected AbstractWritable(AbstractWritableBuilder<?,?> b) {
    this.io = b.io != null ? b.io : makeDefault();
  }
  
  public void writeTo(OutputStream out) {
    writeTo(out,io);
  }
  
  public Future<?> writeTo(OutputStream out, ExecutorService executor) {
    return writeTo(out, io, executor);
  }

  public void writeTo(OutputStream out, IO io) {
    io.write(this,out);
  }
  
  public Future<?> writeTo(OutputStream out, IO io, ExecutorService executor) {
    return io.write(this,out,executor);
  }

  public void writeTo(Writer out) {
    writeTo(out, io);
  }
  
  public Future<?> writeTo(Writer out, ExecutorService executor) {
    return writeTo(out, io, executor);
  }

  public void writeTo(Writer out, IO io) {
    io.write(this,out);
  }
  
  public Future<?> writeTo(Writer out, IO io, ExecutorService executor) {
    return io.write(this, out, executor);
  }
  
  public String toString(IO io) {
    StringWriter sw = 
      new StringWriter();
    io.write(this, sw);
    return sw.toString();    
  }
  
  public Future<String> toString(ExecutorService executor) {
    return io.write(this, executor);
  }
  
  public Future<String> toString(IO io, ExecutorService executor) {
    return io.write(this, executor);
  }
  
  public String toString() {
    return toString(io);
  }

}
