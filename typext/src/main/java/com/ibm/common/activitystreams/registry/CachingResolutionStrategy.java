package com.ibm.common.activitystreams.registry;

import static com.google.common.base.Throwables.propagate;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ibm.common.activitystreams.Makers;
import com.ibm.common.activitystreams.TypeValue;
import com.ibm.common.activitystreams.ValueType;

public abstract class CachingResolutionStrategy 
  implements ResolutionStrategy {

  @SuppressWarnings("unchecked")
  public static abstract class AbstractBuilder
    <C extends CachingResolutionStrategy, B extends AbstractBuilder<C,B>> 
    implements Supplier<C> {

    private boolean silentfail = false;
    private final CacheBuilder<Object,Object> cache = 
      CacheBuilder.newBuilder()
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .maximumSize(100)
        .initialCapacity(50);

    public B silentfail() {
      this.silentfail = true;
      return (B)this;
    }
    
    public B customizeCache(Receiver<CacheBuilder<Object,Object>> receiver) {
      if (receiver != null)
        receiver.receive(cache);
      return (B)this;
    }

  }
  
  private final LoadingCache<TypeValue,TypeValue> cache;
  private final boolean silentfail;
  
  protected LoadingCache<TypeValue,TypeValue> cache() {
    return cache;
  }
  
  CachingResolutionStrategy(AbstractBuilder<?,?> builder) {
    this.cache = initCache(builder);
    this.silentfail = builder.silentfail;
  }
  
  protected boolean silentfail() {
    return silentfail;
  }
  
  private LoadingCache<TypeValue,TypeValue> initCache(AbstractBuilder<?,?> builder) {
    return builder.cache.build(loader());
  }
  
  public Callable<TypeValue> resolverFor(TypeValue tv) {
    return new Resolver(tv);
  }
  
  protected abstract CacheLoader<TypeValue,TypeValue> loader();

  public final class Resolver 
    implements Callable<TypeValue> {

    private final TypeValue input;
    
    Resolver(TypeValue input) {
      this.input = input;
    }
    
    public TypeValue call() throws Exception {
      try {
        if (input == null) return null;
        switch(input.valueType()) {
        case OBJECT:
          return input;
        case SIMPLE:
          return cache.get(input);
        default:
          throw new IllegalArgumentException();
        }
      } catch (Throwable t) {
        if (silentfail())
          return input;
        else throw propagate(t);
      }
    }
    
  }

  public Receiver<TypeValue> preloader() {
    return new CachePreloader(cache());
  }
 
  private static final class CachePreloader 
    implements Receiver<TypeValue> {
    
    private final LoadingCache<TypeValue,TypeValue> cache;
    
    CachePreloader(LoadingCache<TypeValue,TypeValue> cache) {
      this.cache = cache;
    }

    public void receive(final TypeValue t) {
      if (t.valueType() == ValueType.OBJECT && t.id() != null) {
        final TypeValue tv = Makers.type(t.id());
        cache.invalidate(tv);
        try {
          cache.get(tv, new Callable<TypeValue>() {
            public TypeValue call() {
              return t;
            }
          });
        } catch (Throwable e) {}
      }
    }
    
  }
}
