package com.aoindustries.aoserv.client.command;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.BusinessAdministrator;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class GetMySQLSlaveStatusCommand extends RemoteCommand<GetMySQLSlaveStatusCommand.SlaveStatus> {

    final public static class SlaveStatus implements Serializable {

        private static final long serialVersionUID = 1L;

        final private String slaveIOState;
        final private String masterLogFile;
        final private String readMasterLogPos;
        final private String relayLogFile;
        final private String relayLogPos;
        final private String relayMasterLogFile;
        final private String slaveIORunning;
        final private String slaveSQLRunning;
        final private String lastErrno;
        final private String lastError;
        final private String skipCounter;
        final private String execMasterLogPos;
        final private String relayLogSpace;
        final private String secondsBehindMaster;

        public SlaveStatus(
            String slaveIOState,
            String masterLogFile,
            String readMasterLogPos,
            String relayLogFile,
            String relayLogPos,
            String relayMasterLogFile,
            String slaveIORunning,
            String slaveSQLRunning,
            String lastErrno,
            String lastError,
            String skipCounter,
            String execMasterLogPos,
            String relayLogSpace,
            String secondsBehindMaster
        ) {
            this.slaveIOState=slaveIOState;
            this.masterLogFile=masterLogFile;
            this.readMasterLogPos=readMasterLogPos;
            this.relayLogFile=relayLogFile;
            this.relayLogPos=relayLogPos;
            this.relayMasterLogFile=relayMasterLogFile;
            this.slaveIORunning=slaveIORunning;
            this.slaveSQLRunning=slaveSQLRunning;
            this.lastErrno=lastErrno;
            this.lastError=lastError;
            this.skipCounter=skipCounter;
            this.execMasterLogPos=execMasterLogPos;
            this.relayLogSpace=relayLogSpace;
            this.secondsBehindMaster=secondsBehindMaster;
        }

        public String getSlaveIOState() {
            return slaveIOState;
        }

        public String getMasterLogFile() {
            return masterLogFile;
        }

        public String getReadMasterLogPos() {
            return readMasterLogPos;
        }

        public String getRelayLogFile() {
            return relayLogFile;
        }

        public String getRelayLogPos() {
            return relayLogPos;
        }

        public String getRelayMasterLogFile() {
            return relayMasterLogFile;
        }

        public String getSlaveIORunning() {
            return slaveIORunning;
        }

        public String getSlaveSQLRunning() {
            return slaveSQLRunning;
        }

        public String getLastErrno() {
            return lastErrno;
        }

        public String getLastError() {
            return lastError;
        }

        public String getSkipCounter() {
            return skipCounter;
        }

        public String getExecMasterLogPos() {
            return execMasterLogPos;
        }

        public String getRelayLogSpace() {
            return relayLogSpace;
        }

        public String getSecondsBehindMaster() {
            return secondsBehindMaster;
        }
    }

    private static final long serialVersionUID = 1L;

    final private int pkey;

    public GetMySQLSlaveStatusCommand(
        @Param(name="pkey") int pkey
    ) {
        this.pkey = pkey;
    }

    public int getPkey() {
        return pkey;
    }

    @Override
    public boolean isReadOnlyCommand() {
        return true;
    }
    
    public Map<String, List<String>> validate(Locale locale, BusinessAdministrator connectedUser) throws RemoteException {
        // TODO
        return Collections.emptyMap();
    }
}
