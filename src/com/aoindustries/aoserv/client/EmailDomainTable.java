package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  EmailDomain
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class EmailDomainTable extends CachedTableIntegerKey<EmailDomain> {

    EmailDomainTable(AOServConnector connector) {
	super(connector, EmailDomain.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(EmailDomain.COLUMN_DOMAIN_name, ASCENDING),
        new OrderBy(EmailDomain.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addEmailDomain(String domain, AOServer ao, Package packageObject) throws SQLException, IOException {
	if (!EmailDomain.isValidFormat(domain)) throw new SQLException("Invalid domain format: " + domain);
	return connector.requestIntQueryIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.EMAIL_DOMAINS,
            domain,
            ao.pkey,
            packageObject.name
	);
    }

    public EmailDomain get(Object pkey) {
        try {
            return getUniqueRow(EmailDomain.COLUMN_PKEY, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public EmailDomain get(int pkey) throws IOException, SQLException {
	return getUniqueRow(EmailDomain.COLUMN_PKEY, pkey);
    }

    List<EmailDomain> getEmailDomains(Business owner) throws SQLException, IOException {
        String accounting=owner.pkey;

        List<EmailDomain> cached = getRows();
	int len = cached.size();
        List<EmailDomain> matches=new ArrayList<EmailDomain>(len);
	for (int c = 0; c < len; c++) {
            EmailDomain domain = cached.get(c);
            if (domain.getPackage().accounting.equals(accounting)) matches.add(domain);
	}
	return matches;
    }

    List<EmailDomain> getEmailDomains(Package pack) throws IOException, SQLException {
        return getIndexedRows(EmailDomain.COLUMN_PACKAGE, pack.name);
    }

    List<EmailDomain> getEmailDomains(AOServer ao) throws IOException, SQLException {
        return getIndexedRows(EmailDomain.COLUMN_AO_SERVER, ao.pkey);
    }

    EmailDomain getEmailDomain(AOServer ao, String domain) throws IOException, SQLException {
        // Use the index first
        List<EmailDomain> cached = getEmailDomains(ao);
	int len = cached.size();
	for (int c = 0; c < len; c++) {
            EmailDomain sd = cached.get(c);
            if(domain.equals(sd.domain)) return sd;
	}
	return null;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.EMAIL_DOMAINS;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_EMAIL_DOMAIN)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_EMAIL_DOMAIN, args, 3, err)) {
                out.println(
                    connector.simpleAOClient.addEmailDomain(
                        args[1],
                        args[2],
                        args[3]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_EMAIL_DOMAIN)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_EMAIL_DOMAIN, args, 1, err)) {
                try {
                    SimpleAOClient.checkEmailDomain(args[1]);
                    out.println("true");
                } catch(IllegalArgumentException iae) {
                    out.print("aosh: "+AOSHCommand.CHECK_EMAIL_DOMAIN+": ");
                    out.println(iae.getMessage());
                }
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.IS_EMAIL_DOMAIN_AVAILABLE)) {
            if(AOSH.checkParamCount(AOSHCommand.IS_EMAIL_DOMAIN_AVAILABLE, args, 2, err)) {
                try {
                    out.println(connector.simpleAOClient.isEmailDomainAvailable(args[1], args[2]));
                    out.flush();
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.IS_EMAIL_DOMAIN_AVAILABLE+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_EMAIL_DOMAIN)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_EMAIL_DOMAIN, args, 2, err)) {
                connector.simpleAOClient.removeEmailDomain(
                    args[1], args[2]
                );
            }
            return true;
	}
	return false;
    }

    boolean isEmailDomainAvailable(AOServer aoServer, String domain) throws SQLException, IOException {
	if(!EmailDomain.isValidFormat(domain)) throw new SQLException("Invalid EmailDomain: "+domain);
	return connector.requestBooleanQuery(AOServProtocol.CommandID.IS_EMAIL_DOMAIN_AVAILABLE, aoServer.pkey, domain);
    }
}