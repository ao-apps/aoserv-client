package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;

/**
 * Classes that are <code>PasswordProtected</code> provide mechanisms for
 * checking password strength.
 *
 * @version  1.0a
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

    PasswordChecker.Result[] checkPassword(String password) throws IOException, SQLException;
    //String checkPasswordDescribe(String password);
    boolean canSetPassword() throws IOException, SQLException;
    int arePasswordsSet() throws IOException, SQLException;
    void setPassword(String password) throws IOException, SQLException;
}
