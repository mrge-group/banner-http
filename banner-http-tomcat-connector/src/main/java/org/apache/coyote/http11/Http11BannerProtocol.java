package org.apache.coyote.http11;

import org.apache.tomcat.util.net.JIoEndpoint;

/**
 * Enables the HTTP echo protocol.
 * 
 * @author Shopping24 GmbH, Torsten Bøgh Köster (@tboeghk)
 */
public class Http11BannerProtocol extends Http11Protocol {

   public Http11BannerProtocol() {
      super();

      // install alternate handler
      cHandler = new Http11BannerConnectionHandler(this, (JIoEndpoint) endpoint);
      
      // register alternate handler
      ((JIoEndpoint) endpoint).setHandler(cHandler);
   }

   @Override
   protected String getNamePrefix() {
      return "http-banner-bio";
   }

   /**
    * Inner class to inject a {@linkplain Http11BannerProcessor} into the
    * processor chain.
    */
   protected static class Http11BannerConnectionHandler extends Http11ConnectionHandler {

      private final JIoEndpoint endpoint;

      Http11BannerConnectionHandler(Http11Protocol proto, JIoEndpoint endpoint) {
         super(proto);

         this.endpoint = endpoint;
      }

      /**
       * 
       */
      @Override
      protected Http11Processor createProcessor() {
         Http11Processor processor = new Http11BannerProcessor(
               proto.getMaxHttpHeaderSize(), endpoint,
               proto.getMaxTrailerSize(), proto.getMaxExtensionSize());
         processor.setAdapter(proto.getAdapter());
         processor.setMaxKeepAliveRequests(proto.getMaxKeepAliveRequests());
         processor.setKeepAliveTimeout(proto.getKeepAliveTimeout());
         processor.setConnectionUploadTimeout(
               proto.getConnectionUploadTimeout());
         processor.setDisableUploadTimeout(proto.getDisableUploadTimeout());
         processor.setCompressionMinSize(proto.getCompressionMinSize());
         processor.setCompression(proto.getCompression());
         processor.setNoCompressionUserAgents(proto.getNoCompressionUserAgents());
         processor.setCompressableMimeTypes(proto.getCompressableMimeTypes());
         processor.setRestrictedUserAgents(proto.getRestrictedUserAgents());
         processor.setSocketBuffer(proto.getSocketBuffer());
         processor.setMaxSavePostSize(proto.getMaxSavePostSize());
         processor.setServer(proto.getServer());
         processor.setDisableKeepAlivePercentage(
               proto.getDisableKeepAlivePercentage());
         register(processor);

         return processor;
      }

   }
}
