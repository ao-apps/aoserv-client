package com.aoindustries.aoserv.client;

/*
 * Copyright 2005-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * A <code>PackageDefinition</code> stores one unique set of resources, limits, and prices.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class PackageDefinition extends CachedObjectIntegerKey<PackageDefinition> implements Removable {

    static final int COLUMN_PKEY=0;

    String accounting;
    String category;
    String name;
    String version;
    private String display;
    private String description;
    private int setup_fee;
    private String setup_fee_transaction_type;
    private int monthly_rate;
    private String monthly_rate_transaction_type;
    private boolean active;
    private boolean approved;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return accounting;
            case 2: return category;
            case 3: return name;
            case 4: return version;
            case 5: return display;
            case 6: return description;
            case 7: return setup_fee==-1 ? null : Integer.valueOf(setup_fee);
            case 8: return setup_fee_transaction_type;
            case 9: return monthly_rate==-1 ? null : Integer.valueOf(monthly_rate);
            case 10: return monthly_rate_transaction_type;
            case 11: return active ? Boolean.TRUE : Boolean.FALSE;
            case 12: return approved ? Boolean.TRUE : Boolean.FALSE;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public Business getBusiness() {
        // May be filtered
        return table.connector.businesses.get(accounting);
    }
    
    public PackageCategory getPackageCategory() {
        PackageCategory pc=table.connector.packageCategories.get(category);
        if(pc==null) throw new WrappedException(new SQLException("Unable to find PackageCategory: "+category));
        return pc;
    }

    /**
     * Gets the list of packages using this definition.
     */
    public List<Package> getPackages() {
        return table.connector.packages.getPackages(this);
    }

    public PackageDefinitionLimit getLimit(Resource resource) {
        return table.connector.packageDefinitionLimits.getPackageDefinitionLimit(this, resource);
    }

    public List<PackageDefinitionLimit> getLimits() {
        return table.connector.packageDefinitionLimits.getPackageDefinitionLimits(this);
    }

    public void setLimits(PackageDefinitionLimit[] limits) {
        try {
            IntList invalidateList;
            AOServConnection connection=table.connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.SET_PACKAGE_DEFINITION_LIMITS);
                out.writeCompressedInt(pkey);
                out.writeCompressedInt(limits.length);
                for(int c=0;c<limits.length;c++) {
                    PackageDefinitionLimit limit=limits[c];
                    out.writeUTF(limit.resource);
                    out.writeCompressedInt(limit.soft_limit);
                    out.writeCompressedInt(limit.hard_limit);
                    out.writeCompressedInt(limit.additional_rate);
                    out.writeBoolean(limit.additional_transaction_type!=null);
                    if(limit.additional_transaction_type!=null) out.writeUTF(limit.additional_transaction_type);
                }
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) {
                    invalidateList=AOServConnector.readInvalidateList(in);
                } else {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unknown response code: "+code);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                table.connector.releaseConnection(connection);
            }
            table.connector.tablesUpdated(invalidateList);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public String getName() {
        return name;
    }
    
    public String getVersion() {
        return version;
    }
    
    public String getDisplay() {
        return display;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getSetupFee() {
        return setup_fee;
    }
    
    public TransactionType getSetupFeeTransactionType() {
        if(setup_fee_transaction_type==null) return null;
        TransactionType tt=table.connector.transactionTypes.get(setup_fee_transaction_type);
        if(tt==null) throw new WrappedException(new SQLException("Unable to find TransactionType: "+setup_fee_transaction_type));
        return tt;
    }
    
    public int getMonthlyRate() {
        return monthly_rate;
    }
    
    public TransactionType getMonthlyRateTransactionType() {
        if(monthly_rate_transaction_type==null) return null;
        TransactionType tt=table.connector.transactionTypes.get(monthly_rate_transaction_type);
        if(tt==null) throw new WrappedException(new SQLException("Unable to find TransactionType: "+monthly_rate_transaction_type));
        return tt;
    }

    public boolean isActive() {
        return active;
    }
    
    public int copy() {
        return table.connector.requestIntQueryIL(AOServProtocol.COPY_PACKAGE_DEFINITION, pkey);
    }

    public void setActive(boolean active) {
        table.connector.requestUpdateIL(AOServProtocol.SET_PACKAGE_DEFINITION_ACTIVE, pkey, active);
    }

    public boolean isApproved() {
        return approved;
    }

    protected int getTableIDImpl() {
	return SchemaTable.PACKAGE_DEFINITIONS;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        accounting=result.getString(2);
        category=result.getString(3);
        name=result.getString(4);
        version=result.getString(5);
        display=result.getString(6);
        description=result.getString(7);
        String S=result.getString(8);
        setup_fee=S==null ? -1 : SQLUtility.getPennies(S);
        setup_fee_transaction_type=result.getString(9);
        monthly_rate=SQLUtility.getPennies(result.getString(10));
        monthly_rate_transaction_type=result.getString(11);
        active=result.getBoolean(12);
        approved=result.getBoolean(13);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        accounting=in.readUTF();
        category=in.readUTF();
        name=in.readUTF();
        version=in.readUTF();
        display=in.readUTF();
        description=in.readUTF();
        setup_fee=in.readCompressedInt();
        setup_fee_transaction_type=readNullUTF(in);
        monthly_rate=in.readCompressedInt();
        monthly_rate_transaction_type=readNullUTF(in);
        active=in.readBoolean();
        approved=in.readBoolean();
    }

    String toStringImpl() {
        return display;
    }

    public void write(CompressedDataOutputStream out, String aoservVersion) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeUTF(accounting);
        out.writeUTF(category);
        out.writeUTF(name);
        out.writeUTF(version);
        out.writeUTF(display);
        out.writeUTF(description);
        out.writeCompressedInt(setup_fee);
        writeNullUTF(out, setup_fee_transaction_type);
        out.writeCompressedInt(monthly_rate);
        writeNullUTF(out, monthly_rate_transaction_type);
        out.writeBoolean(active);
        out.writeBoolean(approved);
    }
    
    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>(1);
        List<Package> packs=getPackages();
        if(!packs.isEmpty()) reasons.add(new CannotRemoveReason<Package>("Used by "+packs.size()+" "+(packs.size()==1?"package":"packages"), packs));
        return reasons;
    }
    
    public void remove() {
	table.connector.requestUpdateIL(
            AOServProtocol.REMOVE,
            SchemaTable.PACKAGE_DEFINITIONS,
            pkey
	);
    }

    public void update(
        Business business,
        PackageCategory category,
        String name,
        String version,
        String display,
        String description,
        int setupFee,
        TransactionType setupFeeTransactionType,
        int monthlyRate,
        TransactionType monthlyRateTransactionType
    ) {
        try {
            IntList invalidateList;
            AOServConnection connection=table.connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.UPDATE_PACKAGE_DEFINITION);
                out.writeCompressedInt(pkey);
                out.writeUTF(business.pkey);
                out.writeUTF(category.pkey);
                out.writeUTF(name);
                out.writeUTF(version);
                out.writeUTF(display);
                out.writeUTF(description);
                out.writeCompressedInt(setupFee);
                out.writeBoolean(setupFeeTransactionType!=null);
                if(setupFeeTransactionType!=null) out.writeUTF(setupFeeTransactionType.pkey);
                out.writeCompressedInt(monthlyRate);
                out.writeUTF(monthlyRateTransactionType.pkey);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) {
                    invalidateList=AOServConnector.readInvalidateList(in);
                } else {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unknown response code: "+code);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                table.connector.releaseConnection(connection);
            }
            table.connector.tablesUpdated(invalidateList);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }
}