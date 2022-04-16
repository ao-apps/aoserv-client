/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2002-2009, 2016, 2017, 2018, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.account.DisableLog;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Classes that are <code>Disablable</code> can be disable and enabled.
 *
 * @author  AO Industries, Inc.
 */
public interface Disablable {

	/**
	 * Checks if this object is disabled.  This should execute very quickly (not
	 * incur any round-trip to any database) and thus does not throw any checked
	 * exceptions.
	 */
	boolean isDisabled();

	DisableLog getDisableLog() throws IOException, SQLException;

	boolean canDisable() throws IOException, SQLException;

	boolean canEnable() throws IOException, SQLException;

	void disable(DisableLog dl) throws IOException, SQLException;

	void enable() throws IOException, SQLException;
}
