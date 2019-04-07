package com.captivatelabs.shortlink

import grails.core.GrailsApplication
import grails.testing.mixin.integration.Integration
import grails.util.GrailsWebMockUtil
import grails.web.mapping.LinkGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.request.RequestContextHolder
import spock.lang.Specification

@Integration
class ShortLinkControllerSpec extends Specification {
    GrailsApplication grailsApplication
    ShortLinkService shortLinkService
    LinkGenerator grailsLinkGenerator

    @Autowired
    WebApplicationContext ctx

    ShortLinkController controller = new ShortLinkController()

    def setup() {
        GrailsWebMockUtil.bindMockWebRequest(ctx)
        controller.shortLinkService = shortLinkService
        controller.grailsLinkGenerator = grailsLinkGenerator
    }

    def cleanup() {
        RequestContextHolder.resetRequestAttributes()
    }

    void "test short link redirect"() {
        when:
        String targetUrl = 'https://www.captivatelabs.com?ref=t'
        String shortUrl = shortLinkService.create(targetUrl)
        String shortCode = shortUrl.substring(shortUrl.lastIndexOf('/') + 1)
        controller.params.id = shortCode
        controller.index()

        then:
        controller.response.status == HttpStatus.FOUND.value()
        controller.response.redirectedUrl == targetUrl
    }

    void "test permanent redirect"() {
        setup:
        grailsApplication.config.com.captivatelabs.shortlink.redirectPermanent = true
        controller.setConfiguration(grailsApplication.config)

        when:
        String targetUrl = 'https://www.captivatelabs.com?ref=t2'
        String shortUrl = shortLinkService.create(targetUrl)
        String shortCode = shortUrl.substring(shortUrl.lastIndexOf('/') + 1)
        controller.params.id = shortCode
        controller.index()

        then:
        controller.response.status == HttpStatus.MOVED_PERMANENTLY.value()
        controller.response.redirectedUrl == targetUrl
    }
}
