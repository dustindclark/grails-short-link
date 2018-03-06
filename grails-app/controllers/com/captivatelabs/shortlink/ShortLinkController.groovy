package com.captivatelabs.shortlink

import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import groovy.transform.CompileStatic

@CompileStatic
class ShortLinkController implements GrailsConfigurationAware {
    ShortLinkService shortLinkService
    boolean redirectPermanent
    String defaultServerUrl

    def index() {
        if (!params.id) {
            log.debug "No id parameter in short link controller.  Redirecting to grails.serverURL..."
            if (defaultServerUrl) {
                redirect(url: defaultServerUrl, permanent: true)
                return
            } else {
                throw new Exception("No id parameter in short link controller and no config specified for grails.serverURL.  Not sure what to do...")
            }
        }
        try {
            String url = shortLinkService.get((String) params.id, request)
            redirect(url: url, permanent: redirectPermanent)
        } catch (Exception ex) {
            log.warn("Link code ${params.id} not found.", ex)
            redirect(url: "${defaultServerUrl}?msg=shortCode.notFound")
        }
    }

    @Override
    void setConfiguration(Config co) {
        redirectPermanent = co.getOrDefault('com.captivatelabs.shortlink.redirectPermanent', false)
        defaultServerUrl = co.getOrDefault('grails.serverURL', null)
    }
}
