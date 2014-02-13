package org.apache.coyote.http11;

import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.AbstractEndpoint.Handler.SocketState;
import org.apache.tomcat.util.net.JIoEndpoint;
import org.apache.tomcat.util.net.SocketWrapper;

/**
 * Shoots out some pre-http bytes to ensure availability on the client side.
 * 
 * @author Shopping24 GmbH, Torsten Bøgh Köster (@tboeghk)
 */
public class Http11PreAckProcessor extends Http11Processor {

   private static final Log log = LogFactory.getLog(Http11PreAckProcessor.class);
   
   private final static String ACK_STRING = "DEADDA7A";

   /**
    * Delegates arguments to superclass.
    */
   public Http11PreAckProcessor(int headerBufferSize, JIoEndpoint endpoint, int maxTrailerSize,
         int maxExtensionSize) {
      super(headerBufferSize, endpoint, maxTrailerSize, maxExtensionSize);
   }

   /**
    * On first access to the socket, pre-ack bytes are sent.
    */
   @Override
   public SocketState process(SocketWrapper<Socket> socketWrapper) throws IOException {
      // send bytes on first access only
      if (socketWrapper.getLastAccess() < 0) {
         if (log.isDebugEnabled()) {
            log.debug("Sending pre-ack bytes.");
         }
         
         // shoot out the ack bytes before handling stuff.
         socketWrapper.getSocket().getOutputStream().write(new BigInteger(ACK_STRING, 16).toByteArray());
      }

      return super.process(socketWrapper);
   }

}
