package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.IntList;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  EmailSmtpRelay
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class EmailSmtpRelayTable extends CachedTableIntegerKey<EmailSmtpRelay> {

    EmailSmtpRelayTable(AOServConnector connector) {
	super(connector, EmailSmtpRelay.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(EmailSmtpRelay.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
        new OrderBy(EmailSmtpRelay.COLUMN_HOST_name, ASCENDING),
        new OrderBy(EmailSmtpRelay.COLUMN_PACKAGE_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addEmailSmtpRelay(Package pack, AOServer aoServer, String host, EmailSmtpRelayType type, long duration) throws IOException, SQLException {
        int pkey;
        IntList invalidateList;
        AOServConnection connection=connector.getConnection();
        try {
            CompressedDataOutputStream out=connection.getOutputStream();
            out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
            out.writeCompressedInt(SchemaTable.TableID.EMAIL_SMTP_RELAYS.ordinal());
            out.writeUTF(pack.name);
            out.writeCompressedInt(aoServer==null?-1:aoServer.pkey);
            out.writeUTF(host);
            out.writeUTF(type.pkey);
            out.writeLong(duration);
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
    }

    public EmailSmtpRelay get(int pkey) throws IOException, SQLException {
    	return getUniqueRow(EmailSmtpRelay.COLUMN_PKEY, pkey);
    }

    EmailSmtpRelay getEmailSmtpRelay(Package pk, AOServer ao, String host) throws IOException, SQLException {
	String packageName=pk.name;
        int aoPKey=ao.pkey;

	List<EmailSmtpRelay> cached = getRows();
	int len = cached.size();
	for (int c = 0; c < len; c++) {
            EmailSmtpRelay relay=cached.get(c);
            if(
                packageName.equals(relay.packageName)
                && (relay.ao_server==-1 || relay.ao_server==aoPKey)
                && host.equals(relay.host)
            ) return relay;
	}
	return null;
    }

    List<EmailSmtpRelay> getEmailSmtpRelays(Package pk) throws IOException, SQLException {
        return getIndexedRows(EmailSmtpRelay.COLUMN_PACKAGE, pk.name);
    }

    List<EmailSmtpRelay> getEmailSmtpRelays(AOServer ao) throws IOException, SQLException {
        int aoPKey=ao.pkey;

	List<EmailSmtpRelay> cached = getRows();
	int len = cached.size();
        List<EmailSmtpRelay> matches=new ArrayList<EmailSmtpRelay>(len);
	for (int c = 0; c < len; c++) {
            EmailSmtpRelay relay = cached.get(c);
            if (relay.ao_server==-1 || relay.ao_server==aoPKey) matches.add(relay);
	}
	return matches;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.EMAIL_SMTP_RELAYS;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_EMAIL_SMTP_RELAY)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_EMAIL_SMTP_RELAY, args, 5, err)) {
                String S=args[5].trim();
                int pkey=connector.getSimpleAOClient().addEmailSmtpRelay(
                    args[1],
                    args[2],
                    args[3],
                    args[4],
                    S.length()==0?EmailSmtpRelay.NO_EXPIRATION:AOSH.parseLong(S, "duration")
                );
                out.println(pkey);
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_EMAIL_SMTP_RELAY)) {
            if(AOSH.checkParamCount(AOSHCommand.DISABLE_EMAIL_SMTP_RELAY, args, 2, err)) {
                out.println(
                    connector.getSimpleAOClient().disableEmailSmtpRelay(
                        AOSH.parseInt(args[1], "pkey"),
                        args[2]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_EMAIL_SMTP_RELAY)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_EMAIL_SMTP_RELAY, args, 1, err)) {
                connector.getSimpleAOClient().enableEmailSmtpRelay(AOSH.parseInt(args[1], "pkey"));
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REFRESH_EMAIL_SMTP_RELAY)) {
            if(AOSH.checkParamCount(AOSHCommand.REFRESH_EMAIL_SMTP_RELAY, args, 1, err)) {
                connector.getSimpleAOClient().refreshEmailSmtpRelay(
                    AOSH.parseInt(args[1], "pkey"),
                    args[2].trim().length()==0?EmailSmtpRelay.NO_EXPIRATION:AOSH.parseLong(args[2], "min_duration")
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_EMAIL_SMTP_RELAY)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_EMAIL_SMTP_RELAY, args, 1, err)) {
                connector.getSimpleAOClient().removeEmailSmtpRelay(AOSH.parseInt(args[1], "pkey"));
            }
            return true;
	}
	return false;
    }
}