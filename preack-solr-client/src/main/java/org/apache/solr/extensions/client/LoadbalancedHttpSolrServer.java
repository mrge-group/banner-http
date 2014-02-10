package org.apache.solr.extensions.client;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.client.HttpClient;
import org.apache.solr.client.solrj.ResponseParser;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryResponseParser;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.LBHttpSolrServer;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SolrjNamedThreadFactory;
import org.apache.solr.extensions.client.HttpSolrServerWrapper.HttpSolrServerState;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * This is a software loadbalancer intended to be a extendable version of the @link
 * {@link LBHttpSolrServer}.
 * 
 * <ul>
 * <li>For optimization purposes, all loadbalanced solr servers share the same
 * {@linkplain HttpClient} instance. Configure your timeouts properly as
 * defaults may not meet your expectations!</li>
 * <li>The alive check executor checks the servers constantly for availability,
 * so they are moved to the zombie list asynchronously.</li>
 * </ul>
 * 
 * 
 * @author Shopping24 GmbH, Torsten Bøgh Köster (@tboeghk)
 */
public abstract class LoadbalancedHttpSolrServer extends SolrServer {

   private static final long serialVersionUID = 4222827702944890643L;

   // we use this http client across all configured servers
   protected final HttpClient httpClient;
   protected final ResponseParser responseParser;
   private int aliveCheckIntervalSeconds = 5;

   // this is list of configured servers
   private final List<HttpSolrServerWrapper> servers = Lists.newArrayList();

   // this is the index of servers to use
   private final AtomicInteger counter = new AtomicInteger(-1);

   private final ScheduledExecutorService aliveCheckExecutor;

   public LoadbalancedHttpSolrServer(HttpClient httpClient) {
      this(httpClient, new BinaryResponseParser());
   }

   public LoadbalancedHttpSolrServer(HttpClient httpClient, ResponseParser parser) {
      super();

      // preconditions
      Preconditions.checkNotNull(httpClient);
      Preconditions.checkNotNull(parser);

      this.httpClient = httpClient;
      this.responseParser = parser;

      this.aliveCheckExecutor = Executors
            .newSingleThreadScheduledExecutor(new SolrjNamedThreadFactory("aliveCheckExecutor"));
      this.aliveCheckExecutor.scheduleAtFixedRate(createAliveCheckRunner(servers), aliveCheckIntervalSeconds,
            aliveCheckIntervalSeconds, TimeUnit.SECONDS);
   }

   /**
    * Adds a solr endpoint url
    */
   public void addSolrServer(String url) {
      Preconditions.checkNotNull(url);

      servers.add(new HttpSolrServerWrapper(createNewSolrServer(url)));
   }

   /**
    * Sets a bunch of solr servers. Use in ioc containers.
    */
   public void setSolrServers(Collection<String> urls) {
      Preconditions.checkNotNull(urls);

      for (String url : urls) {
         addSolrServer(url);
      }
   }

   @Override
   public NamedList<Object> request(SolrRequest request) throws SolrServerException, IOException {
      return null;

      // find next alive solr server

      // execute request on it

      // on exception, report it.

      // Recover by calling the method again

   }

   @Override
   public void shutdown() {
      this.aliveCheckExecutor.shutdownNow();
      this.httpClient.getConnectionManager().shutdown();
   }

   // --- extension points --------------------------------------------

   protected abstract HttpSolrServerWrapper nextAliveSolrServer();

   protected abstract Runnable createAliveCheckRunner(final List<HttpSolrServerWrapper> servers);

   protected abstract HttpSolrServerState exceptionRaised(List<HttpSolrServerWrapper> servers,
         HttpSolrServerWrapper wrapper,
         Exception e);

   protected abstract HttpSolrServer createNewSolrServer(String url);

}
