package com.captivatelabs.shortlink

import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import grails.web.mapping.LinkGenerator
import groovy.transform.CompileStatic
import org.springframework.http.HttpStatus

@CompileStatic
class ShortLinkController implements GrailsConfigurationAware {
    ShortLinkService shortLinkService
    LinkGenerator grailsLinkGenerator
    boolean redirectPermanent
    String defaultServerUrl

    def index() {
        if (!params.id) {
            log.debug "No id parameter in short link controller.  Redirecting to ${defaultServerUrl}..."
            redirect(url: defaultServerUrl, permanent: true)
            return
        }
        try {
            String url = shortLinkService.get((String) params.id, request)
            redirect(url: url, permanent: redirectPermanent)
        } catch (ShortLinkNotFoundException ex) {
            log.warn("Link code ${params.id} not found.", ex)
            redirect(url: "${defaultServerUrl}?msg=shortCode.notFound")
        }
    }

    def get() {
        if (!params.id) {
            notFound()
            return
        }
        try {
            String url = shortLinkService.get((String) params.id, request)
            render(text: url)
        } catch (ShortLinkNotFoundException ex) {
            log.warn("Link code ${params.id} not found.", ex)
            notFound()
        }
    }

    private void notFound() {
        render(text: "Not found", status: HttpStatus.NOT_FOUND)
    }

    @Override
    void setConfiguration(Config co) {
        redirectPermanent = co.getOrDefault('com.captivatelabs.shortlink.redirectPermanent', false)
        defaultServerUrl = grailsLinkGenerator.serverBaseURL
    }
}
