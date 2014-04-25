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

  public static Builder make () {
    return new Builder();
  }
  
  public static TypeValueRegistry makeDefault() {
    return make().get();
  }
  
  public static TypeValueRegistry makeDefaultSilent() {
    return make()
      .resolver(DefaultResolutionStrategy.make().silentfail().get())
      .get();
  }
  
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

    public Builder io(IO io) {
      this.io = io;
      return this;
    }
    
    public Builder executor(ExecutorService executor) {
      this.executor = executor;
      return this;
    }
    
    public Builder preloader(PreloadStrategy strategy) {
      this.preloader = strategy != null ? 
        strategy : ClasspathPreloader.instance;
      return this;
    }
    
    public Builder resolver(ResolutionStrategy strategy) {
      this.strategy = strategy != null ?
        strategy : ResolutionStrategy.nonop;
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
          readyStatus = Status.READY;
        }
        public void onFailure(Throwable t) {
          readyStatus = Status.ERROR;
          loadError = t;
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
  
  public void waitForPreloader() 
    throws InterruptedException, 
           ExecutionException {
    loader.get();
  }
  
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
