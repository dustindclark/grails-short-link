package com.captivatelabs.shortlink


import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import grails.web.mapping.LinkGenerator
import groovy.transform.CompileStatic

import javax.servlet.http.HttpServletRequest

@CompileStatic
class ShortLinkService implements GrailsConfigurationAware {
    LinkGenerator grailsLinkGenerator
    ShortCodeGenerator shortCodeGenerator
    ShortLinkProvider shortLinkProvider
    ChecksumGenerator checksumGenerator
    ClickTracker clickTracker
    String baseUrl

    /**
     * Create a short link for the given target URL.
     *
     * @param targetUrl URL that short link will redirect to
     * @return
     */
    String create(String targetUrl) {
        long id = shortLinkProvider.create(targetUrl)
        return getShortUrl(id)
    }

    String getShortUrl(long id) {
        String shortCode = shortCodeGenerator.generate(id)
        if (baseUrl) {
            return "${baseUrl}${shortCode}"
        }
        return grailsLinkGenerator.link(controller: 'shortLink', id: shortCode, absolute: true)
    }

    /**
     * Get the target URL for a given short link code.
     *
     * @param shortCode Short link code
     * @return
     */
    String get(String shortCode, HttpServletRequest request, boolean trackClick = true) {
        short checksumLength = checksumGenerator.checksumLength()
        String codeWithoutChecksum = shortCode
        String checksum = null
        if (checksumLength > 0) {
            if (shortCode.length() <= checksumLength) {
                throw new Exception("Short code length cannot be <= checksum length")
            }
            int shortCodeLength = shortCode.length() - checksumLength
            codeWithoutChecksum = shortCode[0..shortCodeLength - 1]
            checksum = shortCode[shortCodeLength]
        }
        Long id = shortCodeGenerator.getId(codeWithoutChecksum)
        if (checksumLength > 0) {
            String generatedChecksum = checksumGenerator.generate(id)
            if (checksum != generatedChecksum) {
                throw new Exception("Checksum generated (${generatedChecksum}) != checksum in short code (${checksum}) for link id ${id} and short code ${shortCode}.")
            }
        }
        try {
            String targetUrl = shortLinkProvider.resolve(id)

            if (trackClick) {
                clickTracker.track(id, targetUrl, request)
            }

            return targetUrl
        } catch (Exception ex) {
            log.error("Error resolving short link for code: ${shortCode}")
            throw ex
        }
    }

    @Override
    void setConfiguration(Config co) {
        baseUrl = co.getOrDefault('com.captivatelabs.shortlink.baseUrl', null)
        if (baseUrl && !baseUrl.endsWith('/')) {
            baseUrl = "${baseUrl}/"
        }
    }
}