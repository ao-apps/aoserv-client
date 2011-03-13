/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

import java.util.Calendar;

/**
 * @author  AO Industries, Inc.
 */
class DtoUtils {

    private DtoUtils() { }

    static Calendar getCalendar(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return cal;
    }

    static Calendar getCalendar(Long time) {
        if(time==null) return null;
        return getCalendar(time.longValue());
    }
}
