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
package com.ibm.common.activitystreams;

import java.io.OutputStream;
import java.io.Writer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Base interface for all objects that serialize to IO object instances
 * @author james
 * @version $Revision: 1.0 $
 */
public interface Writable {

  /**
   * Write the object to the output stream using the default IO instance
   * @param out OutputStream
   */
  void writeTo(OutputStream out);
  
  /**
   * Asynchronously write the object to the output stream using the 
   * default IO instance
   * @param out
   * @param executor
   * @return Future&lt;?>
   */
  Future<?> writeTo(OutputStream out, ExecutorService executor);
  
  /**
   * Write the object to the Writer using the default IO instance
   * @param out Writer
   */
  void writeTo(Writer out);
  
  /**
   * Asynchronously write the object to the writer using the default IO instance
   * @param out
   * @param executor
   * @return Future&lt;?>
   */
  Future<?> writeTo(Writer out, ExecutorService executor);
  
  /**
   * Write the object to the output stream using the given IO instance
   * @param out OutputStream
   * @param io IO
   */
  void writeTo(OutputStream out, IO io);
  
  /**
   * Asynchronously write the object to the output stream using the given
   * IO instance.
   * @param out
   * @param io
   * @param executor
   * @return Future&lt;?>
   */
  Future<?> writeTo(OutputStream out, IO io, ExecutorService executor);
  
  /**
   * Write the object to the writer using the given IO instance
   * @param out Writer
   * @param io IO
   */
  void writeTo(Writer out, IO io);
  
  /**
   * Asynchronously write the object to the writer using the given IO instance
   * @param out
   * @param io
   * @param executor
   * @return Future&lt;?>
   */
  Future<?> writeTo(Writer out, IO io, ExecutorService executor);
  
}
