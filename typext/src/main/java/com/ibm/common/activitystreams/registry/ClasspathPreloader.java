package com.ibm.common.activitystreams.registry;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.Enumeration;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.Collection;
import com.ibm.common.activitystreams.IO;
import com.ibm.common.activitystreams.TypeValue;

public final class ClasspathPreloader 
  implements PreloadStrategy {

  public static final class Builder  
    implements Supplier<ClasspathPreloader> {

    private ClassLoader loader = 
      Thread.currentThread().getContextClassLoader();
    private boolean avoidDuplicates = false;
    
    public Builder avoidDuplicates() {
      this.avoidDuplicates = true;
      return this;
    }
    
    public Builder classLoader(ClassLoader loader) {
      this.loader = loader != null ?
        loader : Thread.currentThread().getContextClassLoader();
      return this;
    }
    
    public ClasspathPreloader get() {
      return new ClasspathPreloader(this);
    }
    
  }
  
  private final ClassLoader loader;
  private final boolean avoidDuplicates;
  
  private ClasspathPreloader(Builder builder) {
    this.loader = builder.loader;
    this.avoidDuplicates = builder.avoidDuplicates;
  }
  
  public void load(IO io, Receiver<TypeValue> receiver) {

    final BloomFilter<CharSequence> filter = 
      avoidDuplicates ?
        BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), 1000) : null;
    
    try {
      for (InputStream in : streams.apply(loader.getResources("typeValues.bin"))) {
        try {
          ObjectInputStream oin = new ObjectInputStream(in);
          Collection col = (Collection) oin.readObject();
          load(col, receiver, filter);
        } catch (Throwable t) {}
      }
    
      for (InputStream in : streams.apply(loader.getResources("typeValues.json"))) {
        try {
          load(io.readAsCollection(in), receiver, filter);
        } catch (Throwable t) {}
      }
      
    } catch (Throwable t) {
      throw Throwables.propagate(t);
    }
    
  }
  
  private void load(
    Collection col, 
    Receiver<TypeValue> receiver, 
    BloomFilter<CharSequence> filter) {
    if (col != null && receiver != null)
      for (ASObject obj : col.items())
        if (obj.id() != null && (filter == null || filter.put(obj.id()))) {
          try {
            receiver.receive(obj);
          } catch (Throwable t) {}
        }
  }
  
  private static Function<Enumeration<URL>,Iterable<InputStream>> streams = 
    new Function<Enumeration<URL>,Iterable<InputStream>> () {
      public Iterable<InputStream> apply(Enumeration<URL> input) {
        ImmutableList.Builder<InputStream> list = 
          ImmutableList.builder();
        while(input.hasMoreElements()) {
          try {
            list.add(input.nextElement().openStream());
          } catch (Throwable t) {}
        }
        return list.build();
      }
  };

  public static final PreloadStrategy instance = 
    new Builder().get();
}
