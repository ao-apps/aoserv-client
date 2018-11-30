/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.password;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Classes that are <code>PasswordProtected</code> provide mechanisms for
 * checking password strength.
 *
 * @author  AO Industries, Inc.
 */
public interface PasswordProtected {

	/**
	 * Indicates that none of the passwords are set.
	 */
	int NONE=0;

	/**
	 * Indicates that some of the passwords are set.
	 */
	int SOME=1;

	/**
	 * Indicates that all of the passwords are set.
	 */
	int ALL=2;

	List<PasswordChecker.Result> checkPassword(String password) throws IOException, SQLException;

	//String checkPasswordDescribe(String password);

	boolean canSetPassword() throws IOException, SQLException;

	int arePasswordsSet() throws IOException, SQLException;

	void setPassword(String password) throws IOException, SQLException;
}
