package com.ibm.common.activitystreams.registry;

import static com.google.common.util.concurrent.MoreExecutors.platformThreadFactory;
import static com.google.common.base.Throwables.propagate;

import java.util.Set;
import java.util.concurrent.Callable;

import com.google.common.base.Objects;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableSet;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.Collection;
import com.ibm.common.activitystreams.Makers;
import com.ibm.common.activitystreams.TypeValue;

public final class DefaultResolutionStrategy 
  extends CachingResolutionStrategy {

  public static Builder make() {
    return new Builder();
  }
  
  public static DefaultResolutionStrategy makeDefault() {
    return make().get();
  }
  
  public static final class Builder 
    extends CachingResolutionStrategy.AbstractBuilder<DefaultResolutionStrategy, Builder> {

    private boolean proactive = false;
    private final HttpFetch.Builder fetcherBuilder = 
      new HttpFetch.Builder();
    private final ImmutableSet.Builder<String> proactiveTypes = 
      ImmutableSet.<String>builder()
        .add("verb")
        .add("objectType");
    
    public Builder customizeFetcher(Receiver<HttpFetch.Builder> receiver) {
      if (receiver != null)  
        receiver.receive(fetcherBuilder);
      return this;
    }
    
    /**
     * Tells the loader to proactively cache additional typevalue 
     * identifiers that happen to be discovered when attempting to
     * resolve a given typevalue.
     * @return
     */
    public Builder proactiveCaching() {
      this.proactive = true;
      return this;
    }
    
    /**
     * Specifies additional objectType identifiers to watch for when 
     * proactiveCaching is enabled.
     * @param typeValueId
     * @return
     */
    public Builder typeValueObjectType(String typeValueId) {
      proactiveTypes.add(typeValueId);
      return this;
    }
    
    public DefaultResolutionStrategy get() {
      return new DefaultResolutionStrategy(this);
    }
    
  }
  
  private final boolean proactiveCaching;
  private final ImmutableSet<String> proactiveTypes;
  private final HttpFetch fetcher;
  
  private DefaultResolutionStrategy(Builder builder) {
    super(builder);
    this.proactiveCaching = builder.proactive;
    this.proactiveTypes = builder.proactiveTypes.build();
    this.fetcher = initFetcher(builder);
    ensureAlwaysShutdown(this);
  }
  
  private HttpFetch initFetcher(Builder builder) {
    return builder.fetcherBuilder.get();
  }
  
  @Override
  protected CacheLoader<TypeValue, TypeValue> loader() {
    return new DefaultCacheLoader();
  }

  private final class DefaultCacheLoader 
    extends CacheLoader<TypeValue,TypeValue> {

    @Override
    public TypeValue load(TypeValue key) throws Exception {
      try {
        if (key == null)
          throw new IllegalArgumentException();
        switch(key.valueType()) {
        case OBJECT:
          return key; // type is already resolved
        case SIMPLE:
          String id = key.id();
          ASObject obj = fetcher.fetch(id); // attempt to fetch an object
          ImmutableSet.Builder<TypeValue> additional = 
            ImmutableSet.builder();
          if (obj instanceof Collection) {
            Collection col = (Collection) obj;
            ASObject matching = 
              processItems(
                col.items(), 
                id, 
                proactiveCaching, 
                proactiveTypes, 
                additional);
            if (matching != null)
              return matching;
          } else if (obj.has("items")) {
            Iterable<ASObject> items = 
              obj.<Iterable<ASObject>>get("items");
            ASObject matching = 
              processItems(
                items, 
                id, 
                proactiveCaching, 
                proactiveTypes, 
                additional);
            if (matching != null)
              return matching;
          } else if (Objects.equal(id, obj.id())) {
            return obj; 
          } 
        default:
          break;      
        }
      } catch (Throwable t) {
        if (silentfail())
          return key;
        else propagate(t);
      }
      throw new UncacheableResponseException();
    }
  }
  
  private ASObject processItems(
    Iterable<ASObject> items, 
    String lookingFor, 
    boolean proactive, 
    Set<String> proactiveTypes,
    ImmutableSet.Builder<TypeValue> additional) {
    ASObject matching = null;
    for (final ASObject obj : items) {
      if (Objects.equal(lookingFor, obj.id())) {
        matching = obj;
        if (!proactive) break;
      } else if (proactive) {
        TypeValue objectType = obj.objectType();
        String id = obj.id();
        if (objectType != null && id != null && !Objects.equal(lookingFor,id)) {
          String otid = objectType.id();
          if (proactiveTypes.contains(otid)) {
            try {
              cache().get(
                Makers.type(id), 
                new Callable<TypeValue>() {
                  public TypeValue call() throws Exception {
                    return obj;
                  }
                });
            } catch (Throwable t) {}
          }
        }
      }
    }
    return matching;
  }

  private static void ensureAlwaysShutdown(
    final ResolutionStrategy strategy) {
      Thread shutdownThread = 
        platformThreadFactory()
          .newThread(new Runnable() {
            public void run() {
              try {
                strategy.shutdown();
              } catch (Throwable t) {}
            }
          });
      Runtime.getRuntime()
             .addShutdownHook(shutdownThread);
  }

  public void shutdown() {
    try {
      fetcher.shutdown();
    } catch (Throwable t) {}
  }
}
