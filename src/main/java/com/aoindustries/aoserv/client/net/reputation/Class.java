/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2012, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.net.reputation;

/**
 * The set of possible classes
 *
 * @author  AO Industries, Inc.
 */
// Matches aoserv-master-db/aoindustries/net/reputation/Class-type.sql
public enum Class {
	/**
	 * Manual Good
	 */
	gm,

	/**
	 * Manual Bad
	 */
	bm,

	/**
	 * Definite Bad
	 */
	bd,

	/**
	 * Uncertain Bad
	 */
	bu,

	/**
	 * Uncertain Good
	 */
	gu,

	/**
	 * Definite Good
	 */
	gd,

	/**
	 * Network Good
	 */
	gn,

	/**
	 * Unknown
	 */
	uu
}
