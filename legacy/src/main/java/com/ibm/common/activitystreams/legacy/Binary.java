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

public final class Binary
  extends ASObject {

  public static final class Builder 
    extends ASObject.AbstractBuilder<Binary, Builder> {

    Builder() {
      objectType("binary");
    }
    
    public Builder data(
      InputStream in) 
        throws IOException {
      return data(in,null);
    }
    
    public Builder gzipData(InputStream in) throws IOException {
      return data(in, Compression.GZipCompression);
    }
    
    public Builder deflateData(InputStream in) throws IOException {
      return data(in, Compression.DeflateCompression);
    }
    
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
    
    public Builder md5(String md5) {
      return set("md5", md5);
    }
    
    public Builder fileUrl(String fileUrl) {
      return set("fileUrl", fileUrl);
    }
    
    @Override
    public Builder mediaType(MediaType mt) {
      return set("mimeType", mt);
    }

    public Binary get() {
      return new Binary(this);
    }
    
  }
  
  private Binary(Builder builder) {
    super(builder);
  }
  
  public String fileUrl() {
    return getString("fileUrl");
  }
  
  public long length() {
    return getLong("length");
  }
  
  public String compression() {
    return getString("compression");
  }
  
  public boolean isGzip() {
    return "gzip".equalsIgnoreCase(compression());
  }
  
  public boolean isDeflate() {
    return "deflate".equalsIgnoreCase(compression());
  }
  
  public String md5() {
    return getString("md5");
  }
  
  public String data() {
    return getString("data");
  }
  
  public InputStream read() throws IOException {
    Compression<?,?> compression =
      isGzip() ? Compression.GZipCompression :
        isDeflate() ? Compression.DeflateCompression : 
          null;
    return read(compression);
  }
  
  public InputStream read(Compression<?,?> compression) throws IOException {
    StringReader reader = new StringReader(data());
    InputStream in = BaseEncoding.base64Url().decodingStream(reader);
    if (compression != null)
      in = compression.decompressor(in);
    if (has("md5"))
      in = new HashingInputStream(Hashing.md5(),in);
    return in;
  }

  @Override
  public MediaType mediaType() {
    return this.<MediaType>get("mimeType");
  }
 
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
