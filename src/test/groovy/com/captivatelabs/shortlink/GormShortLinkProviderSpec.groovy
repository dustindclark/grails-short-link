package com.captivatelabs.shortlink

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class GormShortLinkProviderSpec extends Specification implements ServiceUnitTest<GormShortLinkProvider>, DataTest {

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
        provider.create(targetUrl)

        then:
        ShortLink.findByTargetUrl(targetUrl)
    }

    def "test id changes with duplicate target link"() {
        setup:
        String targetUrl = "http://www.captivatelabs.com/blah"
        ShortLink link = new ShortLink(targetUrl: targetUrl)
        link.save(flush: true)

        when:
        String urlFromService = provider.resolve(link.id)
        long idFromService = provider.create(targetUrl)


        then:
        idFromService != link.id
        urlFromService == targetUrl
    }
}
