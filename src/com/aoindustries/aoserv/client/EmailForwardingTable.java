package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An <code>Architecture</code> wraps all the data for a single supported
 * computer architecture.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class EmailForwardingTable extends CachedTableIntegerKey<EmailForwarding> {

    EmailForwardingTable(AOServConnector connector) {
	super(connector, EmailForwarding.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(EmailForwarding.COLUMN_EMAIL_ADDRESS_name+'.'+EmailAddress.COLUMN_DOMAIN_name+'.'+EmailDomain.COLUMN_DOMAIN_name, ASCENDING),
        new OrderBy(EmailForwarding.COLUMN_EMAIL_ADDRESS_name+'.'+EmailAddress.COLUMN_DOMAIN_name+'.'+EmailDomain.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
        new OrderBy(EmailForwarding.COLUMN_EMAIL_ADDRESS_name+'.'+EmailAddress.COLUMN_ADDRESS_name, ASCENDING),
        new OrderBy(EmailForwarding.COLUMN_DESTINATION_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addEmailForwarding(EmailAddress emailAddressObject, String destination) throws IOException, SQLException {
	if (!EmailAddress.isValidEmailAddress(destination)) throw new SQLException("Invalid destination: " + destination);
	return connector.requestIntQueryIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.EMAIL_FORWARDING,
            emailAddressObject.pkey,
            destination
	);
    }

    public EmailForwarding get(Object pkey) {
        try {
            return getUniqueRow(EmailForwarding.COLUMN_PKEY, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public EmailForwarding get(int pkey) throws SQLException, IOException {
	return getUniqueRow(EmailForwarding.COLUMN_PKEY, pkey);
    }

    List<EmailForwarding> getEmailForwarding(Business business) throws SQLException, IOException {
	List<EmailForwarding> cached = getRows();
	int len = cached.size();
        List<EmailForwarding> matches=new ArrayList<EmailForwarding>(len);
	for (int c = 0; c < len; c++) {
            EmailForwarding forward = cached.get(c);
            if (forward
                .getEmailAddress()
                .getDomain()
                .getPackage()
                .getBusiness()
                .equals(business)
            ) matches.add(forward);
	}
	return matches;
    }

    List<EmailForwarding> getEmailForwardings(EmailAddress ea) throws IOException, SQLException {
        return getIndexedRows(EmailForwarding.COLUMN_EMAIL_ADDRESS, ea.pkey);
    }

    List<EmailForwarding> getEnabledEmailForwardings(EmailAddress ea) throws SQLException, IOException {
        if(ea.getDomain().getPackage().disable_log==-1) return getEmailForwardings(ea);
        else return Collections.emptyList();
    }

    EmailForwarding getEmailForwarding(EmailAddress ea, String destination) throws IOException, SQLException {
        // Use index first
	List<EmailForwarding> cached=getEmailForwardings(ea);
	int len=cached.size();
	for (int c=0;c<len;c++) {
            EmailForwarding forward=cached.get(c);
            if(forward.destination.equals(destination)) return forward;
	}
        return null;
    }

    List<EmailForwarding> getEmailForwarding(AOServer ao) throws SQLException, IOException {
        int aoPKey=ao.pkey;
	List<EmailForwarding> cached = getRows();
	int len = cached.size();
        List<EmailForwarding> matches=new ArrayList<EmailForwarding>(len);
	for (int c = 0; c < len; c++) {
            EmailForwarding forward = cached.get(c);
            if (forward.getEmailAddress().getDomain().ao_server==aoPKey) matches.add(forward);
	}
	return matches;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.EMAIL_FORWARDING;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_EMAIL_FORWARDING)) {
            if(AOSH.checkMinParamCount(AOSHCommand.ADD_EMAIL_FORWARDING, args, 3, err)) {
                if((args.length%3)!=1) {
                    err.println("aosh: "+AOSHCommand.ADD_EMAIL_FORWARDING+": must have multiple of three number of parameters");
                    err.flush();
                } else {
                    for(int c=1;c<args.length;c+=3) {
                        String addr=args[c];
                        int pos=addr.indexOf('@');
                        if(pos==-1) {
                            err.print("aosh: "+AOSHCommand.ADD_EMAIL_FORWARDING+": invalid email address: ");
                            err.println(addr);
                            err.flush();
                        } else {
                            out.println(
                                connector.getSimpleAOClient().addEmailForwarding(
                                    addr.substring(0, pos),
                                    addr.substring(pos+1),
                                    args[c+1],
                                    args[c+2]
                                )
                            );
                            out.flush();
                        }
                    }
                }
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_EMAIL_FORWARDING)) {
            if(AOSH.checkMinParamCount(AOSHCommand.CHECK_EMAIL_FORWARDING, args, 2, err)) {
                if((args.length&1)==0) {
                    err.println("aosh: "+AOSHCommand.CHECK_EMAIL_FORWARDING+": must have even number of parameters");
                    err.flush();
                } else {
                    for(int c=1;c<args.length;c+=2) {
                        String addr=args[c];
                        int pos=addr.indexOf('@');
                        if(pos==-1) {
                            if(args.length>3) {
                                out.print(addr);
                                out.print(": ");
                            }
                            out.print("invalid email address: ");
                            out.println(addr);
                        } else {
                            try {
                                SimpleAOClient.checkEmailForwarding(
                                    addr.substring(0, pos),
                                    addr.substring(pos+1),
                                    args[c+1]
                                );
                                if(args.length>3) {
                                    out.print(addr);
                                    out.print(": ");
                                }
                                out.println("true");
                            } catch(IllegalArgumentException ia) {
                                if(args.length>3) {
                                    out.print(addr);
                                    out.print(": ");
                                }
                                out.println(ia.getMessage());
                            }
                        }
                        out.flush();
                    }
                }
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_EMAIL_FORWARDING)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_EMAIL_FORWARDING, args, 3, err)) {
                String addr=args[1];
                int pos=addr.indexOf('@');
                if(pos==-1) {
                    err.print("aosh: "+AOSHCommand.REMOVE_EMAIL_FORWARDING+": invalid email address: ");
                    err.println(addr);
                    err.flush();
                } else {
                    connector.getSimpleAOClient().removeEmailForwarding(
                        addr.substring(0, pos),
                        addr.substring(pos+1),
                        args[2],
                        args[3]
                    );
                }
            }
            return true;
	} else return false;
    }
}