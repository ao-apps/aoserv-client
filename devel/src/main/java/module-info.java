/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2021, 2022  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */
module com.aoindustries.aoserv.client.devel {
	exports com.aoindustries.aoserv.client.i18n;
	exports com.aoindustries.aoserv.client.account.i18n;
	exports com.aoindustries.aoserv.client.billing.i18n;
	exports com.aoindustries.aoserv.client.linux.i18n;
	exports com.aoindustries.aoserv.client.master.i18n;
	exports com.aoindustries.aoserv.client.monitoring.i18n;
	exports com.aoindustries.aoserv.client.mysql.i18n;
	exports com.aoindustries.aoserv.client.net.i18n;
	exports com.aoindustries.aoserv.client.password.i18n;
	exports com.aoindustries.aoserv.client.postgresql.i18n;
	exports com.aoindustries.aoserv.client.reseller.i18n;
	exports com.aoindustries.aoserv.client.ticket.i18n;
	// Direct
	requires com.aoapps.hodgepodge; // <groupId>com.aoapps</groupId><artifactId>ao-hodgepodge</artifactId>
	requires static jsr305; // <groupId>com.google.code.findbugs</groupId><artifactId>jsr305</artifactId>
	// Java SE
	requires java.logging;
}
