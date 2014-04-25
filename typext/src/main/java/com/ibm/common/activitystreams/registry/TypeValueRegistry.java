package com.ibm.common.activitystreams.registry;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.util.concurrent.Futures.immediateFuture;
import static com.google.common.util.concurrent.Futures.immediateCancelledFuture;
import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.MoreExecutors.getExitingExecutorService;
import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static java.util.concurrent.Executors.newFixedThreadPool;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.Monitor;
import com.ibm.common.activitystreams.IO;
import com.ibm.common.activitystreams.Makers;
import com.ibm.common.activitystreams.TypeValue;
import com.ibm.common.activitystreams.ValueType;
import com.ibm.common.activitystreams.ext.ExtModule;

/**
 * Maintains a registry of resolved TypeValues. If a given TypeValue
 * is not currently known, an attempt will be made to resolve the 
 * value based on the ResolutionStrategy.
 */
public final class TypeValueRegistry 
  implements Function<TypeValue,Future<TypeValue>> {

  /**
   * Return a new TypeValueRegistry.Builder
   * @return Builder
   */
  public static Builder make () {
    return new Builder();
  }
  
  /**
   * Create and return a default TypeValueRegistry instance
   * @return TypeValueRegistry
   */
  public static TypeValueRegistry makeDefault() {
    return make().get();
  }
  
  /**
   * Create an return a default silent TypeValueRegistry instance.
   * Errors encountered during the resolve process will be silenced,
   * causing the process to abort and the original "simple" TypeValue
   * to be returned
   * @return TypeValueRegistry
   */
  public static TypeValueRegistry makeDefaultSilent() {
    return make()
      .resolver(DefaultResolutionStrategy.make().silentfail().get())
      .get();
  }
  
  /**
   * Create a default TypeValueRegistry instance using the 
   * given Activity Streams IO object
   * @param io
   * @return TypeValueRegistry
   */
  public static TypeValueRegistry makeDefault(final IO io) {
    return make()
      .io(io)
      .resolver(
        DefaultResolutionStrategy
          .make()
          .customizeFetcher(
            new Receiver<HttpFetch.Builder>() {
              public void receive(HttpFetch.Builder builder) {
                builder.io(io);
              }
            })
          .get())
      .get();
  }
  
  /**
   * Create a default silent TypeValueRegistry instance using 
   * the given Activity Streams IO object
   * @param io
   * @return TypeValueRegistry
   */
  public static TypeValueRegistry makeDefaultSilent(final IO io) {
    return make()
      .io(io)
      .resolver(
        DefaultResolutionStrategy.make()
          .silentfail()
          .customizeFetcher(
            new Receiver<HttpFetch.Builder>() {
            public void receive(HttpFetch.Builder builder) {
              builder.io(io);
            }})
          .get())
      .get();
  }
  
  public static enum Status {
    LOADING,
    READY,
    ERROR
  }
  
  public static final class Builder 
    implements Supplier<TypeValueRegistry> {

    private ExecutorService executor;
    private PreloadStrategy preloader = 
      ClasspathPreloader.instance;
    private ResolutionStrategy strategy = 
      DefaultResolutionStrategy.makeDefault();
    private IO io;

    /**
     * Set the IO object used
     * @param io
     * @return Builder
     */
    public Builder io(IO io) {
      this.io = io;
      return this;
    }
    
    /**
     * Set the ExecutorService used
     * @param executor
     * @return Builder
     */
    public Builder executor(ExecutorService executor) {
      this.executor = executor;
      return this;
    }
    
    /**
     * Set the PreloadStrategy to be used. By default the 
     * ClasspathPreloader is used.
     * @param strategy
     * @return Builder
     */
    public Builder preloader(PreloadStrategy strategy) {
      this.preloader = strategy != null ? 
        strategy : ClasspathPreloader.instance;
      return this;
    }
    
    /**
     * Set the ResolutionStrategy to use. By default, the
     * DefaultResolutionStrategy is used.
     * @param strategy
     * @return Builder
     */
    public Builder resolver(ResolutionStrategy strategy) {
      this.strategy = strategy != null ?
        strategy : 
        DefaultResolutionStrategy.makeDefault();
      return this;
    }
    
    public TypeValueRegistry get() {
      return new TypeValueRegistry(this);
    }
    
  }
  
  private final ResolutionStrategy strategy;
  private final ListeningExecutorService executor;
  private Status readyStatus = Status.LOADING;
  private Throwable loadError = null;
  private final ListenableFuture<?> loader;
  private final IO io;
  
  private final Monitor monitor = new Monitor();
  private final Monitor.Guard ready = 
    new Monitor.Guard(monitor) {
      @Override
      public boolean isSatisfied() {
        return readyStatus != Status.LOADING;
      }
    };
  
  private TypeValueRegistry(Builder builder) {
    this.strategy = builder.strategy;
    this.io = initIO(builder);
    this.executor = initExecutor(builder);
    this.loader = preload(builder);
  }
  
  private IO initIO(Builder builder) {
    if (builder.io != null)
      return builder.io;
    return IO.makeDefault(ExtModule.instance);
  }
  
  private ListenableFuture<?> preload(Builder builder) {
    final PreloadStrategy strategy = builder.preloader;
    final Receiver<TypeValue> receiver = this.strategy.preloader();
    ListenableFuture<?> future = 
      executor.submit(new Runnable() {
        public void run() {
          strategy.load(io,receiver);
        }
      });
    addCallback(
      future, 
      new FutureCallback<Object>() {
        public void onSuccess(Object result) {
          monitor.enter();
          try {
            readyStatus = Status.READY;
          } finally {
            monitor.leave();
          }
        }
        public void onFailure(Throwable t) {
          monitor.enter();
          try {
            readyStatus = Status.ERROR;
            loadError = t;
          } finally {
            monitor.leave();
          }
        }
      });
    return future;
  }
  
  public Status readyStatus() {
    return readyStatus;
  }
  
  public Throwable loadError() {
    return loadError;
  }
  
  /**
   * Block indefinitely until the preload process has completed
   * @throws InterruptedException
   * @throws ExecutionException
   */
  public void waitForPreloader() 
    throws InterruptedException, 
           ExecutionException {
    loader.get();
  }
  
  /**
   * Block up to the given period of time waiting for the preload
   * process to complete
   * @param duration
   * @param unit
   * @throws InterruptedException
   * @throws ExecutionException
   * @throws TimeoutException
   */
  public void waitForPreloader(long duration, TimeUnit unit) 
    throws InterruptedException, 
           ExecutionException, 
           TimeoutException {
    loader.get(duration,unit);
  }
  
  private ListeningExecutorService initExecutor(
    Builder builder) {
    if (builder.executor != null)
      return listeningDecorator(builder.executor);
    return listeningDecorator(
      getExitingExecutorService(
        (ThreadPoolExecutor)newFixedThreadPool(1)));
  }
  
  /**
   * Resolve the given ID without waiting for the preloader to finish
   * @param id
   * @return Future&lt;TypeValue>
   */
  public Future<TypeValue>resolveNoWait(String id) {
    return resolveNoWait(Makers.type(id));
  }
  
  /**
   * Resolve the given ID. Will wait for the preload process to complete
   * before returning
   * @param id
   * @return Future&lt;TypeValue>
   */
  public Future<TypeValue>resolve(String id) {
    return resolve(Makers.type(id));
  }
  
  /**
   * Resolve the given ID. Will wait the specified length of time for the 
   * preload process to complete before returning
   * @param id
   * @param duration
   * @param unit
   * @return Future&lt;TypeValue>
   */
  public Future<TypeValue>resolve(String id, long duration, TimeUnit unit) {
    return resolve(Makers.type(id),duration,unit);
  }
  
  /**
   * Resolve the given ID without waiting for the preload process to complete
   * @param tv
   * @return Future&lt;TypeValue>
   */
  public Future<TypeValue>resolveNoWait(TypeValue tv) {
    try {
      if (tv == null) return immediateCancelledFuture();
      return tv.valueType() == ValueType.OBJECT || isToken(tv) ?
        immediateFuture(tv) : 
        executor.submit(strategy.resolverFor(tv));
    } catch (Throwable t) {
      throw propagate(t);
    }
  }
  
  /**
   * Resolve the given ID. Will block indefinitely until the preload process
   * is complete
   * @param tv
   * @return Future&lt;TypeValue>
   */
  public Future<TypeValue> resolve(TypeValue tv) {
    try {
      if (tv == null) return immediateCancelledFuture();
      if (tv.valueType() == ValueType.OBJECT || isToken(tv))
        return immediateFuture(tv);
      monitor.enterWhen(ready);
      return executor.submit(strategy.resolverFor(tv));
    } catch (Throwable t) {
      throw propagate(t);
    } finally {
      monitor.leave();
    }
  }
  
  /**
   * Resolve the given ID. Will block for the given period of time until
   * the preload process is complete
   * @param tv
   * @param timeout
   * @param unit
   * @return Future&lt;TypeValue>
   */
  public Future<TypeValue> resolve(
    TypeValue tv, 
    long timeout, 
    TimeUnit unit) {
    try {
      if (tv == null) return immediateCancelledFuture();
      if (tv.valueType() == ValueType.OBJECT || isToken(tv))
        return immediateFuture(tv);
      if (monitor.enterWhen(ready, timeout, unit)) {
        return executor.submit(strategy.resolverFor(tv));
      } else throw new IllegalStateException();
    } catch (Throwable t) {
      throw propagate(t);
    } finally {
      monitor.leave();
    }
  }

  private boolean isToken(TypeValue value) {
    String id = value.id();
    return id != null ?
      id.matches("[A-Za-z0-9\\!\\#\\$\\%\\&\\'\\*\\+\\-\\.\\^\\_\\`\\|\\~]+") :
      false;
  }
  
  public Future<TypeValue> apply(TypeValue input) {
    return resolve(input);
  }
}
