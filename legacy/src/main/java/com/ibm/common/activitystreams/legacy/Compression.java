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
