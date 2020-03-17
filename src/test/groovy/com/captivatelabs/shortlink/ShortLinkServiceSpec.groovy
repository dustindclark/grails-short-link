package com.captivatelabs.shortlink

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import grails.web.mapping.LinkGenerator
import org.springframework.mock.web.MockHttpServletRequest
import spock.lang.Specification

class ShortLinkServiceSpec extends Specification implements ServiceUnitTest<ShortLinkService>, DataTest {
    private static final long MOCK_ID = 89234
    private static final String MOCK_CODE = "15C92"

    def setup() {
    }

    def cleanup() {
    }

    def "test short link creation"() {
        setup:
        service.grailsLinkGenerator = Mock(LinkGenerator)
        service.shortLinkProvider = Mock(ShortLinkProvider)
        service.shortCodeGenerator = Mock(ShortCodeGenerator)

        when:
        String targetUrl = 'https://www.captivatelabs.com'
        String shortUrl = service.create(targetUrl)

        then:
        shortUrl.endsWith(MOCK_CODE)
        1 * service.shortCodeGenerator.generate(_) >> MOCK_CODE
        1 * service.grailsLinkGenerator.link(_) >> { Map attrs -> return "http://cl.co/${attrs.id}" }
        1 * service.shortLinkProvider.create(_) >> MOCK_ID
    }

    def "test short link creation with base url"() {
        setup:
        String baseUrl = "http://b.co"
        grailsApplication.config.com.captivatelabs.shortlink.baseUrl = baseUrl
        service.shortCodeGenerator = Mock(ShortCodeGenerator)
        service.shortLinkProvider = Mock(ShortLinkProvider)

        when:
        String targetUrl = 'https://www.captivatelabs.com'
        String shortUrl = service.create(targetUrl)

        then:
        shortUrl == "${baseUrl}/${MOCK_CODE}"
        1 * service.shortCodeGenerator.generate(_) >> MOCK_CODE
        1 * service.shortLinkProvider.create(_) >> MOCK_ID
    }

    def "test get target url for short link code no checksum"() {
        setup:
        String targetUrl = "http://www.captivatelabs.com/blah"
        service.checksumGenerator = Mock(ChecksumGenerator)
        service.shortLinkProvider = Mock(ShortLinkProvider)
        service.shortCodeGenerator = Mock(ShortCodeGenerator)

        when:
        String urlFromService = service.get(MOCK_CODE, new MockHttpServletRequest(), false)

        then:
        urlFromService == targetUrl
        notThrown Exception
        1 * service.checksumGenerator.checksumLength() >> 0
        1 * service.shortCodeGenerator.getId(_) >> MOCK_ID
        1 * service.shortLinkProvider.resolve(_) >> targetUrl
    }

    def "test checksum matching"() {
        setup:
        String targetUrl = "http://www.captivatelabs.com/blah"
        service.checksumGenerator = Mock(ChecksumGenerator)
        service.shortLinkProvider = Mock(ShortLinkProvider)
        service.shortCodeGenerator = Mock(ShortCodeGenerator)

        when:
        String shortCode = 'aaa'
        String urlChecksum = 'x'
        String generatedChecksum = 'x'
        service.checksumGenerator.checksumLength() >> 1
        service.checksumGenerator.generate(_) >> { Long id -> return generatedChecksum }
        service.shortCodeGenerator.getId(_) >> { String parsedCode -> return (shortCode == parsedCode ? MOCK_ID : null) } //Awkward way to test short code parsing.
        service.shortLinkProvider.resolve(_) >> targetUrl
        String result = service.get("${shortCode}${urlChecksum}", new MockHttpServletRequest(), false)

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
        service.clickTracker = Mock(ClickTracker)
        service.checksumGenerator = Mock(ChecksumGenerator)
        service.shortCodeGenerator = Mock(ShortCodeGenerator)
        service.shortLinkProvider = Mock(ShortLinkProvider)

        when:
        service.get('xyx', new MockHttpServletRequest())

        then:
        1 * service.clickTracker.track(_, _, _)
        1 * service.checksumGenerator.checksumLength() >> 0
        1 * service.shortCodeGenerator.getId(_) >> MOCK_ID
        1 * service.shortLinkProvider.resolve(_) >> targetUrl
    }
}
