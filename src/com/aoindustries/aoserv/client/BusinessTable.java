package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.IntList;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  Business
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class BusinessTable extends CachedTableStringKey<Business> {

    private String rootAccounting;

    BusinessTable(AOServConnector connector) {
	super(connector, Business.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(Business.COLUMN_ACCOUNTING_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    void addBusiness(
	String accounting,
	String contractNumber,
	Server defaultServer,
	String parent,
        boolean canAddBackupServers,
	boolean canAddBusinesses,
        boolean canSeePrices,
        boolean billParent
    ) throws IOException, SQLException {
        IntList invalidateList;
        AOServConnection connection=connector.getConnection();
        try {
            CompressedDataOutputStream out=connection.getOutputStream();
            out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
            out.writeCompressedInt(SchemaTable.TableID.BUSINESSES.ordinal());
            out.writeUTF(accounting);
            out.writeBoolean(contractNumber!=null);
            if(contractNumber!=null) out.writeUTF(contractNumber);
            out.writeCompressedInt(defaultServer.pkey);
            out.writeUTF(parent);
            out.writeBoolean(canAddBackupServers);
            out.writeBoolean(canAddBusinesses);
            out.writeBoolean(canSeePrices);
            out.writeBoolean(billParent);
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
    }

    @Override
    public void clearCache() {
        super.clearCache();
        synchronized(this) {
            rootAccounting=null;
        }
    }

    public String generateAccountingCode(String template) throws IOException, SQLException {
	return connector.requestStringQuery(AOServProtocol.CommandID.GENERATE_ACCOUNTING_CODE, template);
    }

    /**
     * Gets one <code>Business</code> from the database.
     */
    public Business get(Object accounting) {
        try {
            return getUniqueRow(Business.COLUMN_ACCOUNTING, accounting);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    List<Business> getChildBusinesses(Business business) throws IOException, SQLException {
	String accounting=business.pkey;

	List<Business> cached=getRows();
	List<Business> matches=new ArrayList<Business>();
	int size=cached.size();
	for(int c=0;c<size;c++) {
            Business bu=cached.get(c);
            if(accounting.equals(bu.parent)) matches.add(bu);
	}
	return matches;
    }

    synchronized public String getRootAccounting() throws IOException, SQLException {
        if(rootAccounting==null) rootAccounting=connector.requestStringQuery(AOServProtocol.CommandID.GET_ROOT_BUSINESS);
        return rootAccounting;
    }

    public Business getRootBusiness() throws IOException, SQLException {
        String accounting=getRootAccounting();
        Business bu=get(accounting);
        if(bu==null) throw new SQLException("Unable to find Business: "+accounting);
        return bu;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.BUSINESSES;
    }

    public List<Business> getTopLevelBusinesses() throws IOException, SQLException {
	List<Business> cached=getRows();
	List<Business> matches=new ArrayList<Business>();
	int size=cached.size();
	for(int c=0;c<size;c++) {
            Business bu=cached.get(c);
            if(bu.parent==null || getUniqueRow(Business.COLUMN_ACCOUNTING, bu.parent)==null) matches.add(bu);
	}
	return matches;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_BUSINESS)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_BUSINESS, args, 8, err)) {
                try {
                    connector.simpleAOClient.addBusiness(
                        args[1],
                        args[2],
                        args[3],
                        args[4],
                        AOSH.parseBoolean(args[5], "can_add_backup_servers"),
                        AOSH.parseBoolean(args[6], "can_add_businesses"),
                        AOSH.parseBoolean(args[7], "can_see_prices"),
                        AOSH.parseBoolean(args[8], "bill_parent")
                    );
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.ADD_BUSINESS+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.CANCEL_BUSINESS)) {
            if(AOSH.checkParamCount(AOSHCommand.CANCEL_BUSINESS, args, 2, err)) {
                connector.simpleAOClient.cancelBusiness(args[1], args[2]);
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_ACCOUNTING)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_ACCOUNTING, args, 1, err)) {
                try {
                    SimpleAOClient.checkAccounting(args[1]);
                    out.println("true");
                } catch(IllegalArgumentException iae) {
                    out.print("aosh: "+AOSHCommand.CHECK_ACCOUNTING+": ");
                    out.println(iae.getMessage());
                }
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_BUSINESS)) {
            if(AOSH.checkParamCount(AOSHCommand.DISABLE_BUSINESS, args, 2, err)) {
                out.println(
                    connector.simpleAOClient.disableBusiness(
                        args[1],
                        args[2]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_BUSINESS)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_BUSINESS, args, 1, err)) {
                connector.simpleAOClient.enableBusiness(args[1]);
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.GENERATE_ACCOUNTING)) {
            if(AOSH.checkParamCount(AOSHCommand.GENERATE_ACCOUNTING, args, 1, err)) {
                out.println(connector.simpleAOClient.generateAccountingCode(args[1]));
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.GET_ROOT_BUSINESS)) {
            if(AOSH.checkParamCount(AOSHCommand.GET_ROOT_BUSINESS, args, 0, err)) {
                out.println(connector.simpleAOClient.getRootBusiness());
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.IS_ACCOUNTING_AVAILABLE)) {
            if(AOSH.checkParamCount(AOSHCommand.IS_ACCOUNTING_AVAILABLE, args, 1, err)) {
                try {
                    out.println(connector.simpleAOClient.isAccountingAvailable(args[1]));
                    out.flush();
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.IS_ACCOUNTING_AVAILABLE+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.MOVE_BUSINESS)) {
            if(AOSH.checkParamCount(AOSHCommand.MOVE_BUSINESS, args, 3, err)) {
                connector.simpleAOClient.moveBusiness(
                    args[1],
                    args[2],
                    args[3],
                    isInteractive?out:null
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.SET_BUSINESS_ACCOUNTING)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_BUSINESS_ACCOUNTING, args, 2, err)) {
                connector.simpleAOClient.setBusinessAccounting(args[1], args[2]);
            }
            return true;
	} else return false;
    }

    public boolean isAccountingAvailable(String accounting) throws SQLException, IOException {
	if(!Business.isValidAccounting(accounting)) throw new SQLException("Invalid accounting code: "+accounting);
	return connector.requestBooleanQuery(AOServProtocol.CommandID.IS_ACCOUNTING_AVAILABLE, accounting);
    }
}