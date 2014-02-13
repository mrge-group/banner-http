`0xDEADDA7A`
===========
**The "banner" HTTP protocol**


This project bundles server (Tomcat) and client (HttpClient) libraries to handle the "banner" HTTP protocol. The implementation is inspired by a talk Etsys [Gregg Donovan](https://twitter.com/greggdonovan) gave at [Lucene Revolution in 2013](http://www.slideshare.net/greggny3/living-with-garbage-by-gregg-donovan-at-lucenesolr-revolution-2013).

## What to use for

In high throughput low latency compute environments, Java garbage collection can be hazardous. Main goal of this project is to prevent that requests are being made to a host that is currently garbage collecting.

To achive that, the server sends a 5-byte banner sequence to the client within a very short timeframe after connecting and before reading and HTTP headers. The JVM will accept socket connections during Stop-the-world young generation garbage collection but will not execute Java code. So when the server is gc-ing, it will not able to send the banner byte sequence to the client.

On the client side, a `BannerConnectTimeoutException` is thrown and the client can direct the request to another server instance.

## How to use

This project includes a `Connector` for the popular [Tomcat Servlet engine](http://tomcat.apache.org/) and a thread pool implementation for the [`HttpClient`](http://hc.apache.org/httpcomponents-client-ga/) library.

### Enable `http-banner-bio` connector in Tomcat

The Tomcat connector wil send the `0xDEADDA7A` sequence to the client before reading any HTTP headers. Add the connector to your `server.xml`. We recommend to add it as an additional connector and not to replace th existing HTTP connector:

    <Connector port="8100"
       protocol="org.apache.coyote.http11.Http11BannerProtocol" 
       connectionTimeout="20000" 
       URIEncoding="utf-8" />


### Configure `HttpClient`

The pre-ack connection manager waits for the server to send the banner bytes. If the pre-ack bytes are not received during the configured timeout, a  `BannerConnectTimeoutException` is thrown.

    // set banner receive timeout to 7 milliseconds
    BasicHttpParams httpParams = new BasicHttpParams();
    httpParams.setIntParameter("http.banner.timeout", 7);
    
    // inject connection manager into client
    DefaultHttpClient httpClient = new DefaultHttpClient(
        new BannerPoolingClientConnectionManager(), 
        httpParams);

## Building

You need to haven Maven installed. Check out the project, run

    mvn clean verify
    
You'll find the client and tomcat `jar` in the `target` directory of the subproject.

## Issueing a release

In order to do a release we have to prepare the release

    $ mvn release:prepare
    
The parameter `developmentVersion` can be used to set the new version of your local working copy. Afterwards we perform the release

    $ mvn release:perform

## License

[Apache License](LICENSE)
