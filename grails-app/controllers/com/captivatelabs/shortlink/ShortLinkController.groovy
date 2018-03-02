package com.captivatelabs.shortlink

import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import groovy.transform.CompileStatic

@CompileStatic
class ShortLinkController implements GrailsConfigurationAware{
    ShortLinkService shortLinkService
    boolean redirectPermanent

    def index() {
        String url = shortLinkService.get((String) params.id)
        redirect(url: url, permanent: redirectPermanent)
    }

    @Override
    void setConfiguration(Config co) {
        redirectPermanent = co.getOrDefault('com.captivatelabs.shortlink.redirectPermanent', false)
    }
}
