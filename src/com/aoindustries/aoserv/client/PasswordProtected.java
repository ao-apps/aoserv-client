package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;

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

    PasswordChecker.Result[] checkPassword(Locale userLocale, String password);
    //String checkPasswordDescribe(String password);
    boolean canSetPassword();
    int arePasswordsSet();
    void setPassword(String password);
}