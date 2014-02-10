package org.apache.solr.extensions.client;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.LBHttpSolrServer;
import org.apache.solr.common.util.NamedList;

/**
 * This is a software loadbalancer intended to be a extendable version of the @link
 * {@link LBHttpSolrServer}.
 * 
 * <ul>
 * <li>For optimization purposes, all loadbalanced solr servers share the samen
 * {@linkplain HttpClient} instance. Configure your timeouts properly as
 * defaults may not meet your expectations!</li>
 * </ul>
 * 
 * 
 * @author Shopping24 GmbH, Torsten Bøgh Köster (@tboeghk)
 */
public class LoadbalancedHttpSolrServer extends SolrServer {

   private static final long serialVersionUID = 4222827702944890643L;

   private final HttpClient httpClient;

   public LoadbalancedHttpSolrServer(HttpClient httpClient) {
      this.httpClient = httpClient;
   }

   @Override
   public NamedList<Object> request(SolrRequest request) throws SolrServerException, IOException {
      return null;
   }

   @Override
   public void shutdown() {

   }

}
