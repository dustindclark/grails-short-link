/*
 *  Copyright (c) 2018 Captivate Labs, Inc.  All Rights Reserved.
 *
 *  CONFIDENTIAL
 *  __________________
 *
 *  NOTICE:  All information contained herein is, and remains the property of Captivate Labs, Inc and its suppliers,
 *  if any.  The intellectual and technical concepts contained herein are proprietary to Captivate Labs, Inc and its
 *  suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade
 *  secret or copyright law.  Dissemination of this information or reproduction of this material is strictly forbidden
 *  unless prior written permission is obtained from Captivate Labs, Inc.
 */

package com.captivatelabs.shortlink

import org.grails.testing.GrailsUnitTest
import spock.lang.Specification

class ShortCodeGeneratorTest extends Specification implements GrailsUnitTest {
    ShortCodeGenerator gen

    def setup() {
        gen = new DefaultShortCodeGenerator()
        config.com.captivatelabs.shortlink.dictionary = ShortCodeGenerator.DEFAULT_DICTIONARY
        gen.setConfiguration(config)
    }


    void "test basic short code generation and inverse"() {
        setup:
        config.com.captivatelabs.shortlink.dictionary = "0123456789ABCDEF" //HEX for easy testing
        gen.setConfiguration(config)
        
        expect:
        gen.generate(id) == shortCode
        gen.getId(shortCode) == id

        where:
        id                | shortCode
        0                 | '0'
        1                 | '1'
        5                 | '5'
        11                | 'B'
        15                | 'F'
        16                | '10'
        17                | '11'
        18                | '12'
        89234             | '15C92'
        234234            | '392FA'
        16777215          | 'FFFFFF'
        23984728394789234 | '5535FBC0017172'
        Long.MAX_VALUE    | "7FFFFFFFFFFFFFFF"
    }

    void "test unique (and matching) codes up to some high number"() {
        given:
        boolean matches = true
        Map<String, Long> values = [:]
        List<String> dups = []
        int someExcessivelyHighValueThatWillMakeTestsSlow = 10000
        (0..someExcessivelyHighValueThatWillMakeTestsSlow).each { i ->
            String code = gen.generate(i)
            if (values.containsKey(code)) {
                dups << code
            }
            values[code] = i
            matches = matches && (gen.generate(i) == code)
        }

        expect:
        dups.size() == 0
        values.size() == someExcessivelyHighValueThatWillMakeTestsSlow + 1
        matches
    }
}
