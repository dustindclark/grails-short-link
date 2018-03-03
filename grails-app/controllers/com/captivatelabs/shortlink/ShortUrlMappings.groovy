package com.captivatelabs.shortlink

class ShortUrlMappings {

    static mappings = {
        "/l/$id?"(controller: 'shortLink', action: 'index')
    }
}
