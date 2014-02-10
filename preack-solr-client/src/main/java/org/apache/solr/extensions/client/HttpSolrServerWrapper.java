package org.apache.solr.extensions.client;

import org.apache.solr.client.solrj.impl.HttpSolrServer;

/**
 * Wraps a HttpSolrServer and stores meta information.
 */
public class HttpSolrServerWrapper {

   public enum HttpSolrServerState {
      ALIVE,
      ZOMBIE,
      DEAD,
   }

   private final HttpSolrServer server;
   private HttpSolrServerState state = HttpSolrServerState.ALIVE;
   private int errorCount = 0;

   public HttpSolrServerWrapper(HttpSolrServer solrServer) {
      this.server = solrServer;
   }

}