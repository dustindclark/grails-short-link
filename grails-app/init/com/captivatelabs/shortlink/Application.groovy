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

import grails.boot.*
import grails.boot.config.GrailsAutoConfiguration
import grails.plugins.metadata.*

@PluginSource
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(com.captivatelabs.shortlink.Application, args)
    }
}