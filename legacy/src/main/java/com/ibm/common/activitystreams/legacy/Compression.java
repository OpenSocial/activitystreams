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
import java.io.OutputStream;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public interface Compression<O extends OutputStream, I extends InputStream> {
  String label();
  O compressor(OutputStream wrap) throws IOException;
  I decompressor(InputStream in) throws IOException;  
  void finish(OutputStream out) throws IOException;

  public static final Compression<GZIPOutputStream,GZIPInputStream> GZipCompression =
    new Compression<GZIPOutputStream,GZIPInputStream>() {
    public String label() {
      return "gzip";
    }
    public GZIPOutputStream compressor(OutputStream wrap) throws IOException {
      return new GZIPOutputStream(wrap);
    }
    public GZIPInputStream decompressor(InputStream in) throws IOException {
      return new GZIPInputStream(in);
    }
    public void finish(OutputStream out) throws IOException {
      if (out instanceof GZIPOutputStream)
        ((GZIPOutputStream)out).finish();
    }
  };
  
  public static final Compression<DeflaterOutputStream, DeflaterInputStream> DeflateCompression = 
    new Compression<DeflaterOutputStream, DeflaterInputStream>() {
      public String label() {
        return "deflate";
      }
      public DeflaterOutputStream compressor(OutputStream wrap)
          throws IOException {
        return new DeflaterOutputStream(wrap);
      }
      public DeflaterInputStream decompressor(InputStream in)
          throws IOException {
        return new DeflaterInputStream(in);
      }
      public void finish(OutputStream out) throws IOException {
        if (out instanceof DeflaterOutputStream)
          ((DeflaterOutputStream)out).finish();
      }
  };
  
}
