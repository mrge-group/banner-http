package org.apache.solr.extensions.httpclient;

import static org.junit.Assert.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.solr.extensions.httpclient.PreAckPoolingClientConnectionManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PreAckPoolingClientConnectionManagerTest {

   private PreAckPoolingClientConnectionManager manager;
   private DefaultHttpClient httpClient;
   private BasicHttpParams httpParams;
   
   @Before
   public void setUp() throws Exception {
      manager = new PreAckPoolingClientConnectionManager();
      httpParams = new BasicHttpParams();
      httpClient = new DefaultHttpClient(manager, httpParams);
   }
   
   @After
   public void tearDown() {
      httpClient.getConnectionManager().shutdown();
   }
   
   @Test
   public void testPreAckConnection() throws Exception {
      HttpGet request = new HttpGet("http://localhost:8080/");
      HttpResponse response = httpClient.execute(request);
      assertEquals(404, response.getStatusLine().getStatusCode());
   }

}
