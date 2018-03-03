package com.captivatelabs.shortlink


import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import grails.gorm.transactions.Transactional
import grails.web.mapping.LinkGenerator
import groovy.transform.CompileStatic
import org.springframework.transaction.annotation.Propagation

import javax.servlet.http.HttpServletRequest

@CompileStatic
class ShortLinkService implements GrailsConfigurationAware {
    LinkGenerator grailsLinkGenerator
    ShortCodeGenerator shortCodeGenerator
    ChecksumGenerator checksumGenerator
    ClickTracker clickTracker
    String baseUrl

    /**
     * Create a short link for the given target URL.
     *
     * @param targetUrl URL that short link will redirect to
     * @return
     */
    @Transactional(propagation = Propagation.NEVER)
    String create(String targetUrl) {
        ShortLink shortLink
        ShortLink.withTransaction {
            shortLink = new ShortLink(targetUrl: targetUrl)
            shortLink.save(flush: true, failOnError: true)
        }
        String shortCode = shortCodeGenerator.generate(shortLink.id)
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
        ShortLink shortLink
        ShortLink.withNewSession {
            shortLink = ShortLink.get(id)
            if (!shortLink) {
                throw new Exception("Short link not found for short code ${shortCode} which generated ID ${id}")
            }
        }

        if (trackClick) {
            clickTracker.track(shortLink, request)
        }

        return shortLink.targetUrl
    }

    @Override
    void setConfiguration(Config co) {
        baseUrl = co.getOrDefault('com.captivatelabs.shortlink.baseUrl', null)
        if (baseUrl && !baseUrl.endsWith('/')) {
            baseUrl = "${baseUrl}/"
        }
    }
}