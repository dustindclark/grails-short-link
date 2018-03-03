package com.captivatelabs.shortlink

class ShortLink {
    String targetUrl
    Date dateCreated

    static constraints = {
        targetUrl url: true, size: 1..2083
    }

    static mapping = {
        version false
    }
}
