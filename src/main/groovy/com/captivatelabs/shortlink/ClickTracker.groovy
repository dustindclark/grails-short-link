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

import groovy.transform.CompileStatic

import javax.servlet.http.HttpServletRequest

@CompileStatic
interface ClickTracker {
    void track(ShortLink shortLink, HttpServletRequest request)
}
