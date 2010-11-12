/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.command.AOServCommand;
import java.util.List;

/**
 * Classes that are <code>PasswordProtected</code> provide mechanisms for
 * checking password strength and setting passwords.
 *
 * @author  AO Industries, Inc.
 */
public interface PasswordProtected {

    /**
     * Indicates that none of the passwords are set.
     */
    //int NONE=0;

    /**
     * Indicates that some of the passwords are set.
     */
    //int SOME=1;
    
    /**
     * Indicates that all of the passwords are set.
     */
    //int ALL=2;

    AOServCommand<List<PasswordChecker.Result>> getCheckPasswordCommand(String password);

    //String checkPasswordDescribe(String password);
    //boolean canSetPassword() throws IOException, SQLException;
    //int arePasswordsSet() throws IOException, SQLException;
    //void setPassword(String password) throws IOException, SQLException;

    AOServCommand<Void> getSetPasswordCommand(String plaintext);
}
