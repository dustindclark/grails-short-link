package com.captivatelabs.shortlink

import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic

@CompileStatic
class GormShortLinkProvider implements ShortLinkProvider {
    @Override
    long create(String targetUrl) {
        ShortLink link = saveShortLink(targetUrl)
        return link.id.toLong()
    }

    @Override
    String resolve(long id) throws ShortLinkNotFoundException {
        ShortLink shortLink
        ShortLink.withNewSession {
            shortLink = ShortLink.get(id)
            if (!shortLink) {
                throw new ShortLinkNotFoundException("Short link not found for id ${id}")
            }
        }
        return shortLink.targetUrl
    }

    @Transactional
    private ShortLink saveShortLink(String targetUrl) {
        ShortLink shortLink = new ShortLink(targetUrl: targetUrl)
        shortLink.save(flush: true, failOnError: true)
        return shortLink
    }
}
