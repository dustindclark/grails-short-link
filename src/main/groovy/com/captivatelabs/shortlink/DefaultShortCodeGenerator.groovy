package com.captivatelabs.shortlink

import groovy.transform.CompileStatic

@CompileStatic
class DefaultShortCodeGenerator extends ShortCodeGenerator {

    @Override
    String generate(long id) {
        if (id < 0) {
            throw new Exception("Id must be greater than 0.")
        }
        if (id == 0) {
            return dictionary[0] //Because negative infinity...
        }
        int baseNumber = dictionary.length
        int digitsInNewBase = (int) (Math.log(id) / Math.log(baseNumber)) + 1
        char[] result = new char[digitsInNewBase]

        int dictionaryIndex
        long remaining = id
        (digitsInNewBase - 1..0).each { i ->
            dictionaryIndex = (int) remaining % baseNumber
            result[i] = dictionary[dictionaryIndex]
            remaining = (remaining / baseNumber).longValue()
        }
        return new String(result)
    }

    @Override
    long getId(String shortCode) {
        if (!shortCode) {
            throw new Exception("Short code cannot be null.")
        }
        int dictionaryLength = dictionary.length
        char[] codeArray = shortCode.toCharArray()
        long total = 0
        long placeValue
        (0..codeArray.length - 1).each { i ->
            placeValue = characterValues[codeArray[i]] //Digit
            if (placeValue == null) {
                throw new Exception("Could not find character ${codeArray[i]} in the short link character dictionary.")
            }
            placeValue *= (long) Math.pow(dictionaryLength, codeArray.length - i - 1) //Times its place value (i.e. base 32, third place value = 32 ^ 32)
            total += placeValue
        }
        return total
    }
}
