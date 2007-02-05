package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.IntList;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  Server
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class ServerTable extends CachedTableIntegerKey<Server> {

    ServerTable(AOServConnector connector) {
	super(connector, Server.class);
    }

    public int addBackupServer(
        String hostname,
        ServerFarm farm,
        Package owner,
        String description,
        int backup_hour,
        OperatingSystemVersion os_version,
        String username,
        String password,
        String contact_phone,
        String contact_email
    ) {
        try {
            // Create the new profile
            IntList invalidateList;
            AOServConnection connection=connector.getConnection();
            int pkey;
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.ADD_BACKUP_SERVER);
                out.writeUTF(hostname);
                out.writeUTF(farm.getName());
                out.writeCompressedInt(owner.getPKey());
                out.writeUTF(description);
                out.writeCompressedInt(backup_hour);
                out.writeCompressedInt(os_version.getPKey());
                out.writeUTF(username);
                out.writeUTF(password);
                out.writeUTF(contact_phone);
                out.writeUTF(contact_email);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) {
                    pkey=in.readCompressedInt();
                    invalidateList=AOServConnector.readInvalidateList(in);
                } else {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unexpected response code: "+code);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                connector.releaseConnection(connection);
            }
            connector.tablesUpdated(invalidateList);
            return pkey;
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public Server get(Object pkey) {
        if(pkey instanceof Integer) return get(((Integer)pkey).intValue());
        else if(pkey instanceof String) return get((String)pkey);
        else throw new IllegalArgumentException("Must be an Integer or a String");
    }

    public Server get(int pkey) {
	return getUniqueRow(Server.COLUMN_PKEY, pkey);
    }

    public Server get(String hostname) {
	return getUniqueRow(Server.COLUMN_HOSTNAME, hostname);
    }

    int getTableID() {
	return SchemaTable.SERVERS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_BACKUP_SERVER)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_BACKUP_SERVER, args, 3, err)) {
                out.println(
                    connector.simpleAOClient.addBackupServer(
                        args[1],
                        args[2],
                        args[3],
                        args[4],
                        AOSH.parseInt(args[5], "backup_hour"),
                        args[6],
                        args[7],
                        args[8],
                        args[9],
                        args[10],
                        args[11],
                        args[12]
                    )
                );
                out.flush();
            }
            return true;
	}
	return false;
    }
}