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
