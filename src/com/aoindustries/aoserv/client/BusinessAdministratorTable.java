package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  BusinessAdministrator
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class BusinessAdministratorTable extends CachedTableStringKey<BusinessAdministrator> {

    BusinessAdministratorTable(AOServConnector connector) {
        super(connector, BusinessAdministrator.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(BusinessAdministrator.COLUMN_USERNAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    void addBusinessAdministrator(
        Username username,
        String name,
        String title,
        long birthday,
        boolean isPrivate,
        String workPhone,
        String homePhone,
        String cellPhone,
        String fax,
        String email,
        String address1,
        String address2,
        String city,
        String state,
        String country,
        String zip
    ) {
        try {
            // Create the new profile
            IntList invalidateList;
            AOServConnection connection=connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
                out.writeCompressedInt(SchemaTable.TableID.BUSINESS_ADMINISTRATORS.ordinal());
                out.writeUTF(username.pkey);
                out.writeUTF(name);
                if(title!=null && title.length()==0) title=null;
                out.writeBoolean(title!=null); if(title!=null) out.writeUTF(title);
                out.writeLong(birthday);
                out.writeBoolean(isPrivate);
                out.writeUTF(workPhone);
                if(homePhone!=null && homePhone.length()==0) homePhone=null;
                out.writeBoolean(homePhone!=null); if(homePhone!=null) out.writeUTF(homePhone);
                if(cellPhone!=null && cellPhone.length()==0) cellPhone=null;
                out.writeBoolean(cellPhone!=null); if(cellPhone!=null) out.writeUTF(cellPhone);
                if(fax!=null && fax.length()==0) fax=null;
                out.writeBoolean(fax!=null); if(fax!=null) out.writeUTF(fax);
                out.writeUTF(email);
                if(address1!=null && address1.length()==0) address1=null;
                out.writeBoolean(address1!=null); if(address1!=null) out.writeUTF(address1);
                if(address2!=null && address2.length()==0) address2=null;
                out.writeBoolean(address2!=null); if(address2!=null) out.writeUTF(address2);
                if(city!=null && city.length()==0) city=null;
                out.writeBoolean(city!=null); if(city!=null) out.writeUTF(city);
                if(state!=null && state.length()==0) state=null;
                out.writeBoolean(state!=null); if(state!=null) out.writeUTF(state);
                if(country!=null && country.length()==0) country=null;
                out.writeBoolean(country!=null); if(country!=null) out.writeUTF(country);
                if(zip!=null && zip.length()==0) zip=null;
                out.writeBoolean(zip!=null); if(zip!=null) out.writeUTF(zip);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
                else {
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
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Gets one BusinessAdministrator from the database.
     */
    public BusinessAdministrator get(Object username) {
        return getUniqueRow(BusinessAdministrator.COLUMN_USERNAME, username);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.BUSINESS_ADMINISTRATORS;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_BUSINESS_ADMINISTRATOR)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_BUSINESS_ADMINISTRATOR, args, 16, err)) {
                connector.simpleAOClient.addBusinessAdministrator(
                    args[1],
                    args[2],
                    args[3],
                    args[4].length()==0?-1:AOSH.parseDate(args[4], "birthday"),
                    AOSH.parseBoolean(args[5], "is_private"),
                    args[6],
                    args[7],
                    args[8],
                    args[9],
                    args[10],
                    args[11],
                    args[12],
                    args[13],
                    args[14],
                    args[15],
                    args[16]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CHECK_BUSINESS_ADMINISTRATOR_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_BUSINESS_ADMINISTRATOR_PASSWORD, args, 2, err)) {
                PasswordChecker.Result[] results = SimpleAOClient.checkBusinessAdministratorPassword(
                    args[1],
                    args[2]
                );
                if(PasswordChecker.hasResults(Locale.getDefault(), results)) {
                    PasswordChecker.printResults(results, out);
                    out.flush();
                }
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CHECK_BUSINESS_ADMINISTRATOR_USERNAME)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_BUSINESS_ADMINISTRATOR_USERNAME, args, 1, err)) {
                SimpleAOClient.checkBusinessAdministratorUsername(args[1]);
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_BUSINESS_ADMINISTRATOR)) {
            if(AOSH.checkParamCount(AOSHCommand.DISABLE_BUSINESS_ADMINISTRATOR, args, 2, err)) {
                out.println(
                    connector.simpleAOClient.disableBusinessAdministrator(
                        args[1],
                        args[2]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_BUSINESS_ADMINISTRATOR)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_BUSINESS_ADMINISTRATOR, args, 1, err)) {
                connector.simpleAOClient.enableBusinessAdministrator(args[1]);
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.IS_BUSINESS_ADMINISTRATOR_PASSWORD_SET)) {
            if(AOSH.checkParamCount(AOSHCommand.IS_BUSINESS_ADMINISTRATOR_PASSWORD_SET, args, 1, err)) {
                out.println(
                    connector.simpleAOClient.isBusinessAdministratorPasswordSet(
                        args[1]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_BUSINESS_ADMINISTRATOR)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_BUSINESS_ADMINISTRATOR, args, 1, err)) {
                connector.simpleAOClient.removeBusinessAdministrator(args[1]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_BUSINESS_ADMINISTRATOR_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_BUSINESS_ADMINISTRATOR_PASSWORD, args, 2, err)) {
                connector.simpleAOClient.setBusinessAdministratorPassword(
                    args[1],
                    args[2]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_BUSINESS_ADMINISTRATOR_PROFILE)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_BUSINESS_ADMINISTRATOR_PROFILE, args, 16, err)) {
                connector.simpleAOClient.setBusinessAdministratorProfile(
                    args[1],
                    args[2],
                    args[3],
                    AOSH.parseDate(args[4], "birthday"),
                    AOSH.parseBoolean(args[5], "is_private"),
                    args[6],
                    args[7],
                    args[8],
                    args[9],
                    args[10],
                    args[11],
                    args[12],
                    args[13],
                    args[14],
                    args[15],
                    args[16]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CRYPT)) {
            if(AOSH.checkRangeParamCount(AOSHCommand.CRYPT, args, 1, 2, err)) {
                String encrypted=SimpleAOClient.crypt(
                    args[1],
                    args.length==3?args[2]:null
                );
                out.println(encrypted);
            }
            return true;
        }
        return false;
    }
}