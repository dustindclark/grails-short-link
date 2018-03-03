# Grails Short Link Plugin

This plugin provides a service for generating short links and
a controller for resolving target urls from a given short link.  The default implementation simply
translates the database ID (a long) into a short code using the configured dictionary
(or the default dictionary if one is not configured).

Example config:

```yaml
com:
    captivatelabs:
        shortlink:
            baseUrl: http://xyz.com 
            dictionary: abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789
            checksumDictionary: 0987654321ZYXWVUTSRQPONMLKJIHGFEDCBAzyxwvutsrqponmlkjihgfedcba
            redirectPermanent: false #if true, a permanent redirect (301) redirect will be used.  Permanent redirects should not be used where click tracking is desired.
``` 

| Config                | Default                                                        | Description |
| --------------------- | -------------------------------------------------------------- | ----- |
| baseUrl               | *null*                                                         | If specified, short links will generate as *baseUrl* + *shortCode*.  Note that you will need to set up a web server to forward the path `/l` as necessary.  If no value is specified, links will be generated as `${grails.serverUrl}/l/${shortCode}`.  To change `/l` to a different path, use UrlMappings as necessary for ShortLinkController. |
| dictionary            | abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 | Short codes will be generated from characters in this list.  Consider removing vowels to eliminate the possibility of any obscene language generation. |
| checksumDictionary    | 0987654321ZYXWVUTSRQPONMLKJIHGFEDCBAzyxwvutsrqponmlkjihgfedcba | This dictionary is provided to any class that implements ChecksumGenerator. |
| redirectPermanent     | false                                                          | If true, a permanent redirect (301) will be used when a short link is accessed.  Permanent redirects should not be used where click tracking is desired. |


The default dependency injections are shown below.  Note that no `ClickTracker` or `ChecksumGenerator` is
provided by default.  If checksum generation and/or click tracking is desired, implement and inject these
classes (and keep your checksum implementation private).

```groovy
shortCodeGenerator(DefaultShortCodeGenerator)
checksumGenerator(NoChecksumGenerator)
clickTracker(NoClickTracker)
```

__WARNING: Once you start generating links, be very wary of changing your implementations and/or dictionary configuration.  This will break existing links because the short codes are generated, not stored.__

Note that `UrlMappings` have been used to map `/l` to `ShortLinkController`.  If this conflicts with your existing controllers,
you may override this behavior in your application's `UrlMappings`.  If you'd like to use a custom host name (bypassing `/l` altogether),
set the *baseUrl* configuration as mentioned above.  You must also configure your web server to route your short link traffic appropriately.
An Apache example is below:

```xml
<VirtualHost *:80>
   ServerName short.co
   RequestHeader set Host "www.longdomainname.com"
   ProxyPass / http://localhost:8080/l/ <!-- Or other machine name as necessary -->

   ErrorLog /var/log/httpd/short.co-error_log
   ProxyPreserveHost On
</VirtualHost>
```

