package com.captivatelabs.shortlink

import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import groovy.transform.CompileStatic

@CompileStatic
abstract class ShortCodeGenerator implements GrailsConfigurationAware {
    public static final String DEFAULT_DICTIONARY = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'
    
    abstract String generate(long id)
    abstract long getId(String shortCode)

    protected char[] dictionary
    protected Map<Character, Integer> characterValues


    @Override
    void setConfiguration(Config co) {
        ConfigObject shortLinkConfig = (ConfigObject) co.get('com.captivatelabs.shortlink')
        String dictionaryString = shortLinkConfig.getOrDefault('dictionary', DEFAULT_DICTIONARY).toString()

        dictionary = dictionaryString.toCharArray()
        if (dictionary.size() <= 10) {
            throw new Exception ("Dictionary must have at least 10 characters to choose from...otherwise I'd be lengthening...")
        }
        characterValues = new HashMap<Character, Integer>()
        dictionary.eachWithIndex { char c, int i ->
            characterValues.put(c, i)
        }
    }
}
