`0xDEADDA7A` – pre-acknowledged HTTP
===========

This project bundles server (Tomcat) and client (HttpClient, Solr) libraries to handle the Pre-Acknowledges HTTP protocol. The implementation is inspired by a talk [Etsys Gregg Donovan gave at Lucene Revolution in 2013](http://www.slideshare.net/greggny3/living-with-garbage-by-gregg-donovan-at-lucenesolr-revolution-2013).

## What to use for

In high throughput low latency compute environments, Java garbage collection can be hazardous. Main goal of this project is to prevent that request are being made to a host that is currently garbage collecting.

To achive that, the server sends a 5-byte pre-ack sequence to the client within a very short timeframe after connecting. The JVM will accept socket connections during Stop-the-world young generation garbage collection but will not execute Java code. So when the server is gc-ing, it is not able to send pre-ack byte sequence.

## How to use

This project includes a `Connector` for the popular [Tomcat Servlet engine](http://tomcat.apache.org/) and a thread pool implementation for the [`HttpClient`](http://hc.apache.org/httpcomponents-client-ga/) library.

### Enable `http-preack-bio` connector in Tomcat

The Tomcat connector wil send the `0xDEADDA7A` sequence to the client before reading any HTTP headers. Add the connector to your `server.xml`. We recommend to add it as an additional connector and not to replace th existing HTTP connector:

    <Connector port="8100"
       protocol="org.apache.coyote.http11.Http11PreAckProtocol" 
       connectionTimeout="20000" 
       URIEncoding="utf-8" />


### Configure `HttpClient`

The pre-ack connection manager waits for the server to send the pre-ack bytes. If the pre-ack bytes are not received during the configured timeout, a  `PreAckConnectTimeoutException` is thrown.

    // set timeout to 7 milliseconds
    BasicHttpParams httpParams = new BasicHttpParams();
    httpParams.setIntParameter("http.preack.timeout", 7);
    
    // inject connection manager into client
    DefaultHttpClient httpClient = new DefaultHttpClient(
        new PreAckPoolingClientConnectionManager(), 
        httpParams);

### Optional: Use in Solr

