package com.captivatelabs.shortlink

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import grails.web.mapping.LinkGenerator
import org.springframework.mock.web.MockHttpServletRequest
import spock.lang.Specification

class ShortLinkServiceSpec extends Specification implements ServiceUnitTest<ShortLinkService>, DataTest {

    def setup() {
        mockDomain ShortLink
    }

    def cleanup() {
    }

    def "test short link creation"() {
        setup:
        service.shortCodeGenerator = Mock(ShortCodeGenerator)
        service.grailsLinkGenerator = Mock(LinkGenerator)
        String mockCode = 'xyz'

        when:
        String targetUrl = 'https://www.captivatelabs.com'
        String shortUrl = service.create(targetUrl)

        then:
        shortUrl.endsWith('xyz')
        ShortLink.findByTargetUrl(targetUrl)
        1 * service.shortCodeGenerator.generate(_) >> mockCode
        1 * service.grailsLinkGenerator.link(_) >> { Map attrs -> return "http://cl.co/${attrs.id}" }
    }

    def "test short link creation with base url"() {
        setup:
        String baseUrl = "http://b.co"
        config.com.captivatelabs.shortlink.baseUrl = baseUrl
        service.shortCodeGenerator = Mock(ShortCodeGenerator)
        String mockCode = 'abc'

        when:
        String targetUrl = 'https://www.captivatelabs.com'
        String shortUrl = service.create(targetUrl)

        then:
        shortUrl == "${baseUrl}/${mockCode}"
        1 * service.shortCodeGenerator.generate(_) >> mockCode
    }

    def "test get target url for short link code no checksum"() {
        setup:
        String targetUrl = "http://www.captivatelabs.com/blah"
        ShortLink link = new ShortLink(targetUrl: targetUrl)
        link.save(flush: true)
        service.checksumGenerator = Mock(ChecksumGenerator)
        service.shortCodeGenerator = Mock(ShortCodeGenerator)

        when:
        String urlFromService = service.get('xyx', new MockHttpServletRequest(),false)

        then:
        urlFromService == targetUrl
        notThrown Exception
        1 * service.checksumGenerator.checksumLength() >> 0
        1 * service.shortCodeGenerator.getId(_) >> link.id
    }

    def "test checksum matching"() {
        setup:
        String targetUrl = "http://www.captivatelabs.com/blah"
        ShortLink link = new ShortLink(targetUrl: targetUrl)
        link.save(flush: true)
        service.checksumGenerator = Mock(ChecksumGenerator)
        service.shortCodeGenerator = Mock(ShortCodeGenerator)

        when:
        String shortCode = 'aaa'
        String urlChecksum = 'x'
        String generatedChecksum = 'x'
        service.checksumGenerator.checksumLength() >> 1
        service.checksumGenerator.generate(_) >> { Long id -> return generatedChecksum }
        service.shortCodeGenerator.getId(_) >> { String parsedCode -> return (shortCode == parsedCode ? link.id : null) } //Awkward way to test short code parsing.
        String result = service.get("${shortCode}${urlChecksum}", new MockHttpServletRequest(),false)

        then:
        notThrown(Exception)
        result == targetUrl

        when:
        urlChecksum = 'x'
        generatedChecksum = 'y'
        service.get("aaa${urlChecksum}", false)

        then:
        thrown(Exception)
    }

    def "test click tracking"() {
        setup:
        String targetUrl = "http://www.captivatelabs.com/blah"
        ShortLink link = new ShortLink(targetUrl: targetUrl)
        link.save(flush: true)
        service.checksumGenerator = Mock(ChecksumGenerator)
        service.shortCodeGenerator = Mock(ShortCodeGenerator)
        service.clickTracker = Mock(ClickTracker)

        when:
        service.get('xyx', new MockHttpServletRequest())

        then:
        1 * service.clickTracker.track(_, _)
        1 * service.checksumGenerator.checksumLength() >> 0
        1 * service.shortCodeGenerator.getId(_) >> link.id
    }
}
