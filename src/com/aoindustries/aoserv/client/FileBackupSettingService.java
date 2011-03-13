/*
 * Copyright 2003-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  FileBackupSettingTable
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.file_backup_settings)
public interface FileBackupSettingService extends AOServService<Integer,FileBackupSetting> {

    /* TODO
    int addFileBackupSetting(FailoverFileReplication replication, String path, boolean backupEnabled) throws IOException, SQLException {
        return connector.requestIntQueryIL(
            true,
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.FILE_BACKUP_SETTINGS,
            replication.pkey,
            path,
            backupEnabled
        );
    }

    FileBackupSetting getFileBackupSetting(FailoverFileReplication ffr, String path) throws IOException, SQLException {
        // Use index first
        for(FileBackupSetting fbs : getFileBackupSettings(ffr)) if(fbs.path.equals(path)) return fbs;
        return null;
    }

    List<FileBackupSetting> getFileBackupSettings(FailoverFileReplication ffr) throws IOException, SQLException {
        return getIndexedRows(FileBackupSetting.COLUMN_REPLICATION, ffr.pkey);
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_FILE_BACKUP_SETTING)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_FILE_BACKUP_SETTING, args, 3, err)) {
                out.println(
                    connector.getSimpleAOClient().addFileBackupSetting(
                        AOSH.parseInt(args[1], "replication"),
                        args[2],
                        AOSH.parseBoolean(args[3], "backup_enabled")
                    )
                );
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_FILE_BACKUP_SETTING)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_FILE_BACKUP_SETTING, args, 2, err)) {
                connector.getSimpleAOClient().removeFileBackupSetting(
                    AOSH.parseInt(args[1], "replication"),
                    args[2]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_FILE_BACKUP_SETTING)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_FILE_BACKUP_SETTING, args, 3, err)) {
                connector.getSimpleAOClient().setFileBackupSetting(
                    AOSH.parseInt(args[1], "replication"),
                    args[2],
                    AOSH.parseBoolean(args[3], "backup_enabled")
                );
            }
            return true;
        }
        return false;
    }

    void setFileBackupSettings(final FailoverFileReplication ffr, final List<String> paths, final List<Boolean> backupEnableds) throws IOException, SQLException {
        if(paths.size()!=backupEnableds.size()) throw new IllegalArgumentException("paths.size()!=backupEnableds.size(): "+paths.size()+"!="+backupEnableds.size());

        connector.requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.SET_FILE_BACKUP_SETTINGS_ALL_AT_ONCE.ordinal());
                    out.writeCompressedInt(ffr.getPkey());
                    int size = paths.size();
                    out.writeCompressedInt(size);
                    for(int c=0;c<size;c++) {
                        out.writeUTF(paths.get(c));
                        out.writeBoolean(backupEnableds.get(c));
                    }
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        invalidateList=AOServConnector.readInvalidateList(in);
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public void afterRelease() {
                    connector.tablesUpdated(invalidateList);
                }
            }
        );
    }
     */
}
