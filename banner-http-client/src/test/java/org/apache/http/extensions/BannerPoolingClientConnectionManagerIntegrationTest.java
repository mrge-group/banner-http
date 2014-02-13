package org.apache.http.extensions;

import static org.junit.Assert.assertEquals;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BannerPoolingClientConnectionManagerIntegrationTest {

   private BannerPoolingClientConnectionManager manager;
   private DefaultHttpClient httpClient;
   private BasicHttpParams httpParams;

   @Before
   public void setUp() throws Exception {
      manager = new BannerPoolingClientConnectionManager();
      httpParams = new BasicHttpParams();
      httpClient = new DefaultHttpClient(manager, httpParams);
   }

   @After
   public void tearDown() {
      httpClient.getConnectionManager().shutdown();
   }

   @Test
   public void testBannerSuccessConnection() throws Exception {
      httpParams.setIntParameter("http.banner.timeout", 200);
      HttpGet request = new HttpGet("http://localhost:53278/");
      HttpResponse response = httpClient.execute(request);
      assertEquals(404, response.getStatusLine().getStatusCode());
   }

   @Test(expected = BannerConnectTimeoutException.class)
   public void testBannerFailesConnection() throws Exception {
      httpParams.setIntParameter("http.banner.timeout", 1);
      HttpGet request = new HttpGet("http://localhost:53278/");
      httpClient.execute(request);
   }

}
