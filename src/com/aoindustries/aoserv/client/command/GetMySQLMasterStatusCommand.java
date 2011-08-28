/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.*;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class GetMySQLMasterStatusCommand extends RemoteCommand<GetMySQLMasterStatusCommand.MasterStatus> {

    final public static class MasterStatus implements Serializable {

        private static final long serialVersionUID = 1214130854473981745L;

        final private String file;
        final private String position;

        public MasterStatus(
            String file,
            String position
        ) {
            this.file=file;
            this.position=position;
        }

        public String getFile() {
            return file;
        }

        public String getPosition() {
            return position;
        }
    }

    private static final long serialVersionUID = 1214130854473981745L;

    final private int pkey;

    public GetMySQLMasterStatusCommand(
        @Param(name="mysqlServer") MySQLServer mysqlServer
    ) {
        this.pkey = mysqlServer.getPkey();
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    public int getPkey() {
        return pkey;
    }

    @Override
    protected Map<String,List<String>> checkCommand(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // TODO
        return errors;
    }
}
