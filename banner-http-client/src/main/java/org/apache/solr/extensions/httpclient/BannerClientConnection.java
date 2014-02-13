package org.apache.solr.extensions.httpclient;

import java.io.IOException;
import java.math.BigInteger;
import java.net.SocketTimeoutException;
import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.impl.SocketHttpClientConnection;
import org.apache.http.impl.conn.DefaultClientConnection;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.params.HttpParams;

/**
 * Implementation of a {@linkplain SocketHttpClientConnection} that expects a
 * banner byte sequence from the server.
 * 
 * @author Shopping24 GmbH, Torsten Bøgh Köster (@tboeghk)
 */
public class BannerClientConnection extends DefaultClientConnection {

   /**
    * Determines the timeout in milliseconds until the banner byte sequence is
    * received.
    * <p>
    * This parameter expects a value of type {@link Integer}.
    * </p>
    */
   public static final String BANNER_TIMEOUT = "http.banner.timeout";

   // this is the hexstring expected from the server
   private final static String BANNER_STRING = "DEADDA7A";

   private SessionInputBuffer inputBuffer;

   /**
    * Override this method to catch the input buffer. We need to read from it
    * below.
    */
   @Override
   protected HttpMessageParser<HttpResponse> createResponseParser(SessionInputBuffer buffer,
         HttpResponseFactory responseFactory, HttpParams params) {
      this.inputBuffer = buffer;

      return super.createResponseParser(buffer, responseFactory, params);
   }

   @Override
   public void openCompleted(boolean secure, HttpParams params) throws IOException {
      super.openCompleted(secure, params);

      int bannerTimeout = params.getIntParameter(BANNER_TIMEOUT, 5);

      // change socket timeut to preack timeout
      try {
         if (inputBuffer.isDataAvailable(bannerTimeout)) {

            // read buffer
            byte[] expected = new BigInteger(BANNER_STRING, 16).toByteArray();
            byte[] buffer = new byte[expected.length];
            inputBuffer.read(buffer);

            if (!Arrays.equals(buffer, expected)) {
               throw new BannerConnectTimeoutException(String.format("Received unexpected banner bytes %s.",
                     Arrays.toString(buffer)));
            }
         } else {
            throw new BannerConnectTimeoutException(String.format("Did not receive any banner bytes in %s ms.",
                  bannerTimeout));
         }
      } catch (SocketTimeoutException e) {

         // repackage thrown socket timeout exception
         throw new BannerConnectTimeoutException(String.format("Did not receive any banner bytes in %s ms: %s",
               bannerTimeout, e.getMessage()), e);
      }
   }
}
