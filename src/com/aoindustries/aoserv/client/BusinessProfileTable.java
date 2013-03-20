/*
 * Copyright 2001-2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.*;
import com.aoindustries.util.IntList;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  BusinessProfile
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
        final Business business,
        final String name,
        final boolean isPrivate,
        final String phone,
        String fax,
        final String address1,
        String address2,
        final String city,
        String state,
        final String country,
        String zip,
        final boolean sendInvoice,
        final String billingContact,
        final String billingEmail,
        final String technicalContact,
        final String technicalEmail
    ) throws IOException, SQLException {
        if(fax!=null && fax.length()==0) fax=null;
        final String finalFax = fax;
        if(address2!=null && address2.length()==0) address2=null;
        final String finalAddress2 = address2;
        if(state!=null && state.length()==0) state=null;
        final String finalState = state;
        if(zip!=null && zip.length()==0) zip=null;
        final String finalZip = zip;
        // Create the new profile
        return connector.requestResult(
            true,
            new AOServConnector.ResultRequest<Integer>() {
                int pkey;
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
                    out.writeCompressedInt(SchemaTable.TableID.BUSINESS_PROFILES.ordinal());
                    out.writeUTF(business.getAccounting());
                    out.writeUTF(name);
                    out.writeBoolean(isPrivate);
                    out.writeUTF(phone);
                    out.writeBoolean(finalFax!=null);
                    if(finalFax!=null) out.writeUTF(finalFax);
                    out.writeUTF(address1);
                    out.writeBoolean(finalAddress2!=null);
                    if(finalAddress2!=null) out.writeUTF(finalAddress2);
                    out.writeUTF(city);
                    out.writeBoolean(finalState!=null);
                    if(finalState!=null) out.writeUTF(finalState);
                    out.writeUTF(country);
                    out.writeBoolean(finalZip!=null);
                    if(finalZip!=null) out.writeUTF(finalZip);
                    out.writeBoolean(sendInvoice);
                    out.writeUTF(billingContact);
                    out.writeUTF(billingEmail);
                    out.writeUTF(technicalContact);
                    out.writeUTF(technicalEmail);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        pkey=in.readCompressedInt();
                        invalidateList=AOServConnector.readInvalidateList(in);
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public Integer afterRelease() {
                    connector.tablesUpdated(invalidateList);
                    return pkey;
                }
            }
        );
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
    boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_BUSINESS_PROFILE)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_BUSINESS_PROFILE, args, 16, err)) {
                try {
                    connector.getSimpleAOClient().addBusinessProfile(
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