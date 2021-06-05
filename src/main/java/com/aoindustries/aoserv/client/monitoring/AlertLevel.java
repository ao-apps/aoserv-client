/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2014, 2016, 2017, 2018, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.monitoring;

import com.aoapps.lang.i18n.Resources;

/**
 * The different alert levels in the system.
 *
 * @author  AO Industries, Inc.
 */
// Matches enum com.aoindustries.noc.monitor.common.AlertLevel
// Matches aoserv-master-db/aoindustries/monitoring/AlertLevel-type.sql
// TODO: Once a separate project exists for aoserv-client-monitoring, remove this duplicate enum and depend on noc-common
public enum AlertLevel {
	NONE,
	LOW,
	MEDIUM,
	HIGH,
	CRITICAL,
	UNKNOWN;

	private static final Resources RESOURCES = Resources.getResources(AlertLevel.class);

	private static final AlertLevel[] alertLevels = values();

	/**
	 * Gets the alert level from ordinal without the overhead of a call
	 * to <code>values</code>.
	 */
	public static AlertLevel fromOrdinal(int ordinal) {
		return alertLevels[ordinal];
	}

	@Override
	public String toString() {
		return RESOURCES.getMessage(name() + ".toString");
	}
}
