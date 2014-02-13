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
 * pre-ack byte sequence from the server.
 * 
 * @author Shopping24 GmbH, Torsten Bøgh Köster (@tboeghk)
 */
public class PreAckClientConnection extends DefaultClientConnection {

   /**
    * Determines the timeout in milliseconds until the pre-ack byte sequence is
    * received.
    * <p>
    * This parameter expects a value of type {@link Integer}.
    * </p>
    */
   public static final String PRE_ACK_TIMEOUT = "http.preack.timeout";

   // this is the hexstring expected from the server
   private final static String ACK_STRING = "DEADDA7A";

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

      int preAckTimeout = params.getIntParameter(PRE_ACK_TIMEOUT, 5);

      // change socket timeut to preack timeout
      try {
         if (inputBuffer.isDataAvailable(preAckTimeout)) {

            // read buffer
            byte[] expected = new BigInteger(ACK_STRING, 16).toByteArray();
            byte[] buffer = new byte[expected.length];
            inputBuffer.read(buffer);

            if (!Arrays.equals(buffer, expected)) {
               throw new PreAckConnectTimeoutException(String.format("Received unexpected pre-ack bytes %s.",
                     Arrays.toString(buffer)));
            }
         } else {
            throw new PreAckConnectTimeoutException(String.format("Did not receive any pre-ack bytes in %s ms.",
                  preAckTimeout));
         }
      } catch (SocketTimeoutException e) {

         // repackage thrown socket timeout exception
         throw new PreAckConnectTimeoutException(String.format("Did not receive any pre-ack bytes in %s ms: %s",
               preAckTimeout, e.getMessage()), e);
      }
   }
}
