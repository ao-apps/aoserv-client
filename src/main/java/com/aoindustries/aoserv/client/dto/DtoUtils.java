/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2010, 2011, 2016, 2017, 2019  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.dto;

import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * @author  AO Industries, Inc.
 */
class DtoUtils {

	private DtoUtils() { }

	/**
	 * @param timeZone  The time zone to use or {@code null} to use the default time zone
	 */
	static GregorianCalendar getCalendar(long time, TimeZone timeZone) {
		GregorianCalendar gcal = timeZone == null ? new GregorianCalendar() : new GregorianCalendar(timeZone);
		gcal.setTimeInMillis(time);
		return gcal;
	}

	/**
	 * @param timeZone  The time zone to use or {@code null} to use the default time zone
	 */
	static GregorianCalendar getCalendar(Long time, TimeZone timeZone) {
		if(time == null) return null;
		return getCalendar(time.longValue(), timeZone);
	}
}
