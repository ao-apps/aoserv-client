/*
 * Copyright 2001-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

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
