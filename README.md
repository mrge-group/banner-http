Pre-Acknowledged HTTP
===========

This project bundles server (Tomcat) and client (HttpClient, Solr) libraries to handle the Pre-Acknowledges HTTP protocol. The implementation is inspired by a talk [Etsys Gregg Donovan gave at Lucene Revolution in 2013](http://www.slideshare.net/greggny3/living-with-garbage-by-gregg-donovan-at-lucenesolr-revolution-2013).

## What to use for

In high throughput low latency environments ...

## How to use

### Enable `http-preack-bio` connector in Tomcat

Add connector to `server.xml`

    <Connector port="8100"
       protocol="org.apache.coyote.http11.Http11PreAckProtocol" 
       connectionTimeout="20000" 
       URIEncoding="utf-8"/>

Use as additional connector on a different port

### Configure `HttpClient`



### Optional: Use in Solr

