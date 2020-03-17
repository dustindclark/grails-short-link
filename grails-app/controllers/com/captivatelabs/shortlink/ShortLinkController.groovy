package com.captivatelabs.shortlink

import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import grails.web.mapping.LinkGenerator
import groovy.transform.CompileStatic
import org.springframework.http.HttpStatus

@CompileStatic
class ShortLinkController implements GrailsConfigurationAware {
    private static final String CONFIG_PREFIX = "com.captivatelabs.shortlink"

    ShortLinkService shortLinkService
    LinkGenerator grailsLinkGenerator

    private boolean redirectPermanent
    private String defaultServerUrl
    private String notFoundUrl

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
        if (notFoundUrl) {
            redirect(url: notFoundUrl)
        } else {
            render(text: "Not found", status: HttpStatus.NOT_FOUND)
        }
    }

    @Override
    void setConfiguration(Config co) {
        redirectPermanent = co.getOrDefault("${CONFIG_PREFIX}.redirectPermanent", false)
        notFoundUrl = co.get("${CONFIG_PREFIX}.notFoundUrl") ?: null
        defaultServerUrl = co.get("${CONFIG_PREFIX}.defaultUrl") ?: grailsLinkGenerator.serverBaseURL
    }
}
