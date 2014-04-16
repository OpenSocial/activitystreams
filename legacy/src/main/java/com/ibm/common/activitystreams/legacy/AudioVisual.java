package com.ibm.common.activitystreams.legacy;

import java.io.ObjectStreamException;

import com.google.common.base.Supplier;
import com.ibm.common.activitystreams.ASObject;

public final class AudioVisual 
  extends ASObject {

  public static final class Builder 
    extends ASObject.AbstractBuilder<AudioVisual,Builder> {

    public Builder embedCode(String embed) {
      return set("embedCode", embed);
    }
    
    public Builder stream(MediaLink mediaLink) {
      return set("stream", mediaLink);
    }
    
    public Builder stream(Supplier<? extends MediaLink> mediaLink) {
      return stream(mediaLink.get());
    }
    
    public AudioVisual get() {
      return new AudioVisual(this);
    }
    
  }
  
  private AudioVisual(Builder builder) {
    super(builder);
  }
  
  public String embedCode() {
    return getString("embedCode");
  }
  
  public MediaLink stream() {
    return this.<MediaLink>get("stream");
  }
  
  Object writeReplace() throws java.io.ObjectStreamException {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm 
    extends AbstractSerializedForm<AudioVisual> {
    private static final long serialVersionUID = -2060301713159936285L;
    protected SerializedForm(AudioVisual obj) {
      super(obj);
    }
    Object readResolve() throws ObjectStreamException {
      return super.doReadResolve();
    }
    protected AudioVisual.Builder builder() {
      return new Builder();
    }
  }
}
