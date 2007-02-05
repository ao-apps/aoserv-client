package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  EmailPipe
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class EmailPipeTable extends CachedTableIntegerKey<EmailPipe> {

    EmailPipeTable(AOServConnector connector) {
	super(connector, EmailPipe.class);
    }

    int addEmailPipe(AOServer ao, String path, Package packageObject) {
	int pkey=connector.requestIntQueryIL(
            AOServProtocol.ADD,
            SchemaTable.EMAIL_PIPES,
            ao.pkey,
            path,
            packageObject.name
	);
	return pkey;
    }

    public EmailPipe get(Object pkey) {
	return getUniqueRow(EmailPipe.COLUMN_PKEY, pkey);
    }

    public EmailPipe get(int pkey) {
	return getUniqueRow(EmailPipe.COLUMN_PKEY, pkey);
    }

    List<EmailPipe> getEmailPipes(Package pack) {
        return getIndexedRows(EmailPipe.COLUMN_PACKAGE, pack.name);
    }

    List<EmailPipe> getEmailPipes(AOServer ao) {
        return getIndexedRows(EmailPipe.COLUMN_AO_SERVER, ao.pkey);
    }

    int getTableID() {
	return SchemaTable.EMAIL_PIPES;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_EMAIL_PIPE)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_EMAIL_PIPE, args, 3, err)) {
                int pkey=connector.simpleAOClient.addEmailPipe(
                    args[1],
                    args[2],
                    args[3]
                );
                out.println(pkey);
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_EMAIL_PIPE)) {
            if(AOSH.checkParamCount(AOSHCommand.DISABLE_EMAIL_PIPE, args, 2, err)) {
                out.println(
                    connector.simpleAOClient.disableEmailPipe(
                        AOSH.parseInt(args[1], "pkey"),
                        args[2]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_EMAIL_PIPE)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_EMAIL_PIPE, args, 1, err)) {
                connector.simpleAOClient.enableEmailPipe(AOSH.parseInt(args[1], "pkey"));
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_EMAIL_PIPE)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_EMAIL_PIPE, args, 1, err)) {
                connector.simpleAOClient.removeEmailPipe(
                    AOSH.parseInt(args[1], "pkey")
                );
            }
            return true;
	}
	return false;
    }
}