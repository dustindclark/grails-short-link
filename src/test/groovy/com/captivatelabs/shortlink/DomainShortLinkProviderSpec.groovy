package com.captivatelabs.shortlink

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class DomainShortLinkProviderSpec extends Specification implements ServiceUnitTest<GormShortLinkProvider>, DataTest {

    GormShortLinkProvider provider

    def setup() {
        mockDomain(ShortLink)
        provider = new GormShortLinkProvider()
    }

    def cleanup() {
    }

    def "test short link creation"() {
        when:
        String targetUrl = 'https://www.captivatelabs.com'
        provider.getId(targetUrl)

        then:
        ShortLink.findByTargetUrl(targetUrl)
    }

    def "test id changes with duplicate target link"() {
        setup:
        String targetUrl = "http://www.captivatelabs.com/blah"
        ShortLink link = new ShortLink(targetUrl: targetUrl)
        link.save(flush: true)

        when:
        String urlFromService = provider.resolveShortLink(link.id)
        long idFromService = provider.getId(targetUrl)


        then:
        idFromService != link.id
        urlFromService == targetUrl
    }
}
