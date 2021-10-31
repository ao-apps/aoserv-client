/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2021  AO Industries, Inc.
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
module com.aoindustries.aoserv.client {
	exports com.aoindustries.aoserv.client;
	exports com.aoindustries.aoserv.client.account;
	exports com.aoindustries.aoserv.client.accounting;
	exports com.aoindustries.aoserv.client.aosh;
	exports com.aoindustries.aoserv.client.backup;
	exports com.aoindustries.aoserv.client.billing;
	exports com.aoindustries.aoserv.client.distribution;
	exports com.aoindustries.aoserv.client.distribution.management;
	exports com.aoindustries.aoserv.client.dns;
	exports com.aoindustries.aoserv.client.dto;
	exports com.aoindustries.aoserv.client.email;
	exports com.aoindustries.aoserv.client.ftp;
	exports com.aoindustries.aoserv.client.infrastructure;
	exports com.aoindustries.aoserv.client.linux;
	exports com.aoindustries.aoserv.client.master;
	exports com.aoindustries.aoserv.client.monitoring;
	exports com.aoindustries.aoserv.client.mysql;
	exports com.aoindustries.aoserv.client.net;
	exports com.aoindustries.aoserv.client.net.monitoring;
	exports com.aoindustries.aoserv.client.net.reputation;
	exports com.aoindustries.aoserv.client.password;
	exports com.aoindustries.aoserv.client.payment;
	exports com.aoindustries.aoserv.client.pki;
	exports com.aoindustries.aoserv.client.postgresql;
	exports com.aoindustries.aoserv.client.reseller;
	exports com.aoindustries.aoserv.client.schema;
	exports com.aoindustries.aoserv.client.scm;
	exports com.aoindustries.aoserv.client.signup;
	exports com.aoindustries.aoserv.client.sql;
	exports com.aoindustries.aoserv.client.ticket;
	exports com.aoindustries.aoserv.client.util;
	exports com.aoindustries.aoserv.client.web;
	exports com.aoindustries.aoserv.client.web.jboss;
	exports com.aoindustries.aoserv.client.web.tomcat;
	// Direct
	requires com.aoapps.collections; // <groupId>com.aoapps</groupId><artifactId>ao-collections</artifactId>
	requires com.aoapps.hodgepodge; // <groupId>com.aoapps</groupId><artifactId>ao-hodgepodge</artifactId>
	requires com.aoapps.lang; // <groupId>com.aoapps</groupId><artifactId>ao-lang</artifactId>
	requires com.aoapps.net.types; // <groupId>com.aoapps</groupId><artifactId>ao-net-types</artifactId>
	requires com.aoapps.security; // <groupId>com.aoapps</groupId><artifactId>ao-security</artifactId>
	requires com.aoapps.sql; // <groupId>com.aoapps</groupId><artifactId>ao-sql</artifactId>
	requires com.aoapps.tlds; // <groupId>com.aoapps</groupId><artifactId>ao-tlds</artifactId>
	// Java SE
	requires java.desktop;
	requires java.logging;
	requires java.sql;
}
