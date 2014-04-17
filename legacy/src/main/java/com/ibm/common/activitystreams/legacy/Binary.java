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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingInputStream;
import com.google.common.hash.HashingOutputStream;
import com.google.common.io.BaseEncoding;
import com.google.common.net.MediaType;
import com.ibm.common.activitystreams.ASObject;

/**
 * The legacy "binary" objectType.
 * 
 * <pre>
 *   InputStream in = ...
 *   // will base64 encode, gzip compress and md5 sum the input data
 *   // will also set the length property accordingly
 *   Binary binary = 
 *     LegacyMakers.binary()
 *       .gzipData(in)
 *       .get();
 * </pre>
 * 
 * @author james
 *
 */
public final class Binary
  extends ASObject {

  public static final class Builder 
    extends ASObject.AbstractBuilder<Binary, Builder> {

    Builder() {
      objectType("binary");
    }
    
    /**
     * Set the input data without any compression. Will automatically
     * set calculate the md5 sum and length properties
     * @param in InputStream
     * @return Builder
     * @throws IOException
     */
    public Builder data(
      InputStream in) 
        throws IOException {
      return data(in,null);
    }
    
    /**
     * Set the input data with GZip compression. Will automatically
     * set calculate the md5 sum and length properties
     * @param in InputStream
     * @return Builder
     * @throws IOException
     */
    public Builder gzipData(InputStream in) throws IOException {
      return data(in, Compression.GZipCompression);
    }
    
    /**
     * Set the input data with Deflate compression. Will automatically
     * set calculate the md5 sum and length properties
     * @param in InputStream
     * @return Builder
     * @throws IOException
     */
    public Builder deflateData(InputStream in) throws IOException {
      return data(in, Compression.DeflateCompression);
    }
    
    /**
     * Set the input data the given Compression. Will automatically
     * set calculate the md5 sum and length properties
     * @param in InputStream
     * @return Builder
     * @throws IOException
     */
    public Builder data(
      InputStream in, 
      Compression<?,?> compression) 
        throws IOException {
      StringWriter writer = new StringWriter();
      OutputStream out = 
        BaseEncoding.base64Url().encodingStream(writer);
      if (compression != null)
        out = compression.compressor(out);
      HashingOutputStream hout = 
        new HashingOutputStream(
          Hashing.md5(), out);
      byte[] buf = new byte[1024];
      int r = -1;
      long size = 0;
      while((r = in.read(buf)) > -1) {
        hout.write(buf,0,r);
        size += r;
      }
      set("length", size);
      if (compression != null) {
        set("compression", compression.label());
        compression.finish(out);
      }
      hout.close();
      set("md5", hout.hash().toString());
      return set("data",writer.toString());
    }
    
    /**
     * Manually set the md5 properties (this is not recommended. calling the data
     * methods will automatically generate the md5 checksum for you)
     * 
     * @param md5 String
     * @return Builder
     */
    public Builder md5(String md5) {
      return set("md5", md5);
    }
    
    /**
     * Set the fileUrl property
     * @param fileUrl String 
     * @return Builder
     */
    public Builder fileUrl(String fileUrl) {
      return set("fileUrl", fileUrl);
    }
    
    /**
     * Set the MIME Media Type using the Legacy "mimeType" property name
     * rather than the AS 2.0 "mediaType" property name
     * @param mt MediaType
     * @return Builder
     */
    @Override
    public Builder mediaType(MediaType mt) {
      return set("mimeType", mt);
    }

    /**
     * Get the built Binary object
     */
    public Binary get() {
      return new Binary(this);
    }
    
  }
  
  private Binary(Builder builder) {
    super(builder);
  }
  
  /**
   * Get the fileUrl property
   * @return String
   */
  public String fileUrl() {
    return getString("fileUrl");
  }
  
  /**
   * Get the length property
   * @return long
   */
  public long length() {
    return getLong("length");
  }
  
  /**
   * Get the compression property value (typically "gzip" or "deflate")
   * @return String
   */
  public String compression() {
    return getString("compression");
  }
  
  /**
   * True if compression = gzip
   * @return boolean
   */
  public boolean isGzip() {
    return "gzip".equalsIgnoreCase(compression());
  }
  
  /**
   * True if compression = deflate
   * @return boolean
   */
  public boolean isDeflate() {
    return "deflate".equalsIgnoreCase(compression());
  }
  
  /**
   * Return the md5 checksum
   * @return String
   */
  public String md5() {
    return getString("md5");
  }
  
  /**
   * Return the literal string content of the data property.
   * This will be base64 encoded and optionally compressed
   * @return String
   */
  public String data() {
    return getString("data");
  }
  
  /**
   * Return an InputStream for reading the data. Will
   * decompress and base64 decode as necessary
   * @return InputStream
   * @throws IOException
   */
  public InputStream read() throws IOException {
    Compression<?,?> compression =
      isGzip() ? Compression.GZipCompression :
        isDeflate() ? Compression.DeflateCompression : 
          null;
    return read(compression);
  }
  
  /**
   * Return an InputStream for reading the data
   * @param compression Compression
   * @return InputStream
   * @throws IOException
   */
  public InputStream read(Compression<?,?> compression) throws IOException {
    StringReader reader = new StringReader(data());
    InputStream in = BaseEncoding.base64Url().decodingStream(reader);
    if (compression != null)
      in = compression.decompressor(in);
    if (has("md5"))
      in = new HashingInputStream(Hashing.md5(),in);
    return in;
  }

  /**
   * Return the MIME MediaType using the legacy "mimeType" property name
   * rather than the AS 2.0 "mediaType" name
   */
  @Override
  public MediaType mediaType() {
    return this.<MediaType>get("mimeType");
  }
  
  // Java Serialization Support
 
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<Binary> {
    private static final long serialVersionUID = -2060301713159936285L;
    protected SerializedForm(Binary obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return super.doReadResolve();
    }
    protected Binary.Builder builder() {
      return new Builder();
    }
  }
}
