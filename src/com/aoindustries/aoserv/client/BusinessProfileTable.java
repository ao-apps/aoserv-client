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
 * @see  BusinessProfile
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class BusinessProfileTable extends CachedTableIntegerKey<BusinessProfile> {

    BusinessProfileTable(AOServConnector connector) {
	super(connector, BusinessProfile.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(BusinessProfile.COLUMN_ACCOUNTING_name, ASCENDING),
        new OrderBy(BusinessProfile.COLUMN_PRIORITY_name, DESCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addBusinessProfile(
	Business business,
	String name,
	boolean isPrivate,
	String phone,
	String fax,
	String address1,
	String address2,
	String city,
	String state,
	String country,
	String zip,
	boolean sendInvoice,
	String billingContact,
	String billingEmail,
	String technicalContact,
	String technicalEmail
    ) throws IOException, SQLException {
        // Create the new profile
        IntList invalidateList;
        AOServConnection connection=connector.getConnection();
        int pkey;
        try {
            CompressedDataOutputStream out=connection.getOutputStream();
            out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
            out.writeCompressedInt(SchemaTable.TableID.BUSINESS_PROFILES.ordinal());
            out.writeUTF(business.getAccounting());
            out.writeUTF(name);
            out.writeBoolean(isPrivate);
            out.writeUTF(phone);
            if(fax!=null && fax.length()==0) fax=null;
            out.writeBoolean(fax!=null);
            if(fax!=null) out.writeUTF(fax);
            out.writeUTF(address1);
            if(address2!=null && address2.length()==0) address2=null;
            out.writeBoolean(address2!=null);
            if(address2!=null) out.writeUTF(address2);
            out.writeUTF(city);
            if(state!=null && state.length()==0) state=null;
            out.writeBoolean(state!=null);
            if(state!=null) out.writeUTF(state);
            out.writeUTF(country);
            if(zip!=null && zip.length()==0) zip=null;
            out.writeBoolean(zip!=null);
            if(zip!=null) out.writeUTF(zip);
            out.writeBoolean(sendInvoice);
            out.writeUTF(billingContact);
            out.writeUTF(billingEmail);
            out.writeUTF(technicalContact);
            out.writeUTF(technicalEmail);
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

    public BusinessProfile get(Object pkey) {
        try {
            return get(((Integer)pkey).intValue());
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public BusinessProfile get(int pkey) throws IOException, SQLException {
	return getUniqueRow(BusinessProfile.COLUMN_PKEY, pkey);
    }

    /**
     * Gets the highest priority  <code>BusinessProfile</code> for
     * the provided <code>Business</code>.
     */
    BusinessProfile getBusinessProfile(Business business) throws IOException, SQLException {
	String accounting=business.getAccounting();
	List<BusinessProfile> cached=getRows();
	int size=cached.size();
	for(int c=0;c<size;c++) {
            BusinessProfile profile=cached.get(c);
            // Return first found because sorted highest priority first
            if(profile.accounting.equals(accounting)) return profile;
	}
	return null;
    }

    List<BusinessProfile> getBusinessProfiles(Business business) throws IOException, SQLException {
        return getIndexedRows(BusinessProfile.COLUMN_ACCOUNTING, business.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.BUSINESS_PROFILES;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_BUSINESS_PROFILE)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_BUSINESS_PROFILE, args, 16, err)) {
                try {
                    connector.simpleAOClient.addBusinessProfile(
                        args[1],
                        args[2],
                        AOSH.parseBoolean(args[3], "is_secure"),
                        args[4],
                        args[5],
                        args[6],
                        args[7],
                        args[8],
                        args[9],
                        args[10],
                        args[11],
                        AOSH.parseBoolean(args[12], "send_invoice"),
                        args[13],
                        args[14],
                        args[15],
                        args[16]
                    );
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.ADD_BUSINESS_PROFILE+": ");
                    err.println(iae.getMessage());
                    err.flush();
                } catch(IOException exc) {
                    err.print("aosh: "+AOSHCommand.ADD_BUSINESS_PROFILE+": ");
                    err.println(exc.getMessage());
                    err.flush();
                } catch(SQLException exc) {
                    err.print("aosh: "+AOSHCommand.ADD_BUSINESS_PROFILE+": ");
                    err.println(exc.getMessage());
                    err.flush();
                }
            }
            return true;
	} else return false;
    }
}