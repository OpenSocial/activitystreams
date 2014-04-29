package com.ibm.common.activitystreams.registry;

import static com.google.common.base.Throwables.propagate;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.ibm.common.activitystreams.ASObject;
import com.ibm.common.activitystreams.IO;
import com.ibm.common.activitystreams.ext.ExtModule;

public final class HttpFetch
  extends CacheLoader<String,ASObject> {

  static final class Builder 
    implements Supplier<HttpFetch> {

    private final RegistryBuilder<ConnectionSocketFactory> csfr = 
      RegistryBuilder.<ConnectionSocketFactory>create()
        .register("http", PlainConnectionSocketFactory.INSTANCE);
    private ConnectionConfig defaultConnectionConfig;
    private SocketConfig defaultSocketConfig;
    private final ImmutableMap.Builder<HttpHost,ConnectionConfig> connectionConfigs = 
      ImmutableMap.builder();
    private final ImmutableMap.Builder<HttpHost,SocketConfig> socketConfigs = 
      ImmutableMap.builder();
    private int maxConnectionsPerRoute = 2;
    private int maxConnections = 20;
    private final ImmutableSet.Builder<Header> defaultHeaders = 
      ImmutableSet.builder();
    private String userAgent = null;
    private IO io = null;
    private HttpClientBuilder builder = 
      HttpClients.custom();
    
    private final CacheBuilder<Object,Object> cache = 
      CacheBuilder.newBuilder()
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .maximumSize(50)
        .initialCapacity(50);
    private HttpClientConnectionManager manager;
    
    public Builder customizeCache(
      Receiver<CacheBuilder<Object,Object>> receiver) {
      if (receiver != null)
        receiver.receive(cache);
      return this;
    }
    
    public Builder customizeClientBuilder(
      Receiver<HttpClientBuilder> receiver) {
        receiver.receive(builder);
        return this;
    }
    
    public Builder io(IO io) {
      this.io = io;
      return this;
    }
    
    public Builder useragent(String ua) {
      this.userAgent = ua;
      return this;
    }
    
    public Builder defaultHeader(String name, String value) {
      defaultHeaders.add(new BasicHeader(name,value));
      return this;
    }
    
    public Builder maxConnectionsPerRoute(int max) {
      this.maxConnectionsPerRoute = max;
      return this;
    }
    
    public Builder maxConnections(int max) {
      this.maxConnections = max;
      return this;
    }
    
    public Builder connectionConfig(ConnectionConfig config) {
      defaultConnectionConfig = config;
      return this;
    }
    
    public Builder connectionConfig(HttpHost host, ConnectionConfig config) {
      connectionConfigs.put(host,config);
      return this;
    }
    
    public Builder socketConfig(SocketConfig config) {
      defaultSocketConfig = config;
      return this;
    }
    
    public Builder socketConfig(HttpHost host, SocketConfig config) {
      socketConfigs.put(host, config);
      return this;
    }
    
    public Builder registerConnectionSocketFactory(
      String id, 
      ConnectionSocketFactory csf) {
      csfr.register(id, csf);
      return this;
    }

    public Builder manager(HttpClientConnectionManager manager) {
      this.manager = manager;
      return this;
    }
    
    public HttpFetch get() {
      return new HttpFetch(this);
    }
    
  }
  
  private final LoadingCache<String,ASObject> cache;
  private final HttpClientConnectionManager manager;
  private final IO io;
  private final CloseableHttpClient client;

  HttpFetch(Builder builder) {
    this.cache = initCache(builder);
    this.manager = initManager(builder);
    this.io = initIO(builder);
    this.client = initClient(builder);
  }
  
  public void shutdown() {
    try {
      manager.shutdown();
    } catch (Throwable t) {}
  }
  
  private IO initIO(Builder builder) {
    if (builder.io != null)
      return builder.io;
    return IO.makeDefault(ExtModule.instance);
  }
  
  private CloseableHttpClient initClient(Builder builder) {
    HttpClientBuilder b = builder.builder;
    b.setConnectionManager(manager);
    ImmutableSet<Header> headers = 
      builder.defaultHeaders.build();
    if (!headers.isEmpty())
      b.setDefaultHeaders(headers);
    if (builder.userAgent != null)
      b.setUserAgent(builder.userAgent);
    return b.build();
  }
  
  private HttpClientConnectionManager initManager(Builder builder) {
    if (builder.manager != null)
      return builder.manager;
    PoolingHttpClientConnectionManager pm = 
      new PoolingHttpClientConnectionManager(builder.csfr.build());
    for (Map.Entry<HttpHost,ConnectionConfig> entry : builder.connectionConfigs.build().entrySet())
      pm.setConnectionConfig(entry.getKey(), entry.getValue());
    if (builder.defaultConnectionConfig != null)
      pm.setDefaultConnectionConfig(builder.defaultConnectionConfig);
    for (Map.Entry<HttpHost,SocketConfig> entry : builder.socketConfigs.build().entrySet())
      pm.setSocketConfig(entry.getKey(), entry.getValue());
    if (builder.defaultSocketConfig != null)
      pm.setDefaultSocketConfig(builder.defaultSocketConfig);
    pm.setDefaultMaxPerRoute(builder.maxConnectionsPerRoute);
    pm.setMaxTotal(builder.maxConnections);
    return pm;
  }
  
  private LoadingCache<String,ASObject> initCache(Builder builder) {
    return builder.cache.build(this);
  }

  public ASObject fetch(String uri) {
    try {
      return cache.get(uri);
    } catch (Throwable t) {
      throw propagate(t);
    }
  }
  
  @Override
  public ASObject load(String key) throws Exception {
    HttpContext context = new HttpClientContext();
    HttpGet get = new HttpGet(key);
    HttpResponse resp = client.execute(get, context);
    StatusLine status = resp.getStatusLine();
    int code = status.getStatusCode();
    if (code >= 200 && code < 300) {
      HttpEntity entity = resp.getEntity();
      if (entity != null) {
        // attempt parse
        Optional<ASObject> parsed = 
          parse(entity.getContent());
        if (parsed.isPresent())
          return parsed.get();
      }
    }
    throw new UncacheableResponseException();
  }
  
  private Optional<ASObject> parse(InputStream in) {
    try {
      return Optional.<ASObject>of(io.read(in));
    } catch (Throwable t) {
      return Optional.<ASObject>absent();
    }
  }
}
