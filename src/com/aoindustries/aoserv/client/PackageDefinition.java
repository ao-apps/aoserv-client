package com.aoindustries.aoserv.client;

/*
 * Copyright 2005-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.IntList;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>PackageDefinition</code> stores one unique set of resources, limits, and prices.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class PackageDefinition extends CachedObjectIntegerKey<PackageDefinition> implements Removable {

    static final int COLUMN_PKEY=0;
    static final String COLUMN_ACCOUNTING_name = "accounting";
    static final String COLUMN_CATEGORY_name = "category";
    static final String COLUMN_MONTHLY_RATE_name = "monthly_rate";
    static final String COLUMN_NAME_name = "name";
    static final String COLUMN_VERSION_name = "version";

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

    Object getColumnImpl(int i) {
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

    /**
     * May be null if filtered.
     */
    public Business getBusiness() throws IOException, SQLException {
        return table.connector.getBusinesses().get(accounting);
    }

    public PackageCategory getPackageCategory() throws SQLException, IOException {
        PackageCategory pc=table.connector.getPackageCategories().get(category);
        if(pc==null) throw new SQLException("Unable to find PackageCategory: "+category);
        return pc;
    }

    /**
     * Gets the list of packages using this definition.
     */
    public List<Package> getPackages() throws IOException, SQLException {
        return table.connector.getPackages().getPackages(this);
    }

    public PackageDefinitionLimit getLimit(Resource resource) throws IOException, SQLException {
        if(resource==null) throw new AssertionError("resource is null");
        return table.connector.getPackageDefinitionLimits().getPackageDefinitionLimit(this, resource);
    }

    public List<PackageDefinitionLimit> getLimits() throws IOException, SQLException {
        return table.connector.getPackageDefinitionLimits().getPackageDefinitionLimits(this);
    }

    public void setLimits(final PackageDefinitionLimit[] limits) throws IOException, SQLException {
        table.connector.requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.SET_PACKAGE_DEFINITION_LIMITS.ordinal());
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
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        invalidateList=AOServConnector.readInvalidateList(in);
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unknown response code: "+code);
                    }
                }

                public void afterRelease() {
                    table.connector.tablesUpdated(invalidateList);
                }
            }
        );
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
    
    public TransactionType getSetupFeeTransactionType() throws SQLException, IOException {
        if(setup_fee_transaction_type==null) return null;
        TransactionType tt=table.connector.getTransactionTypes().get(setup_fee_transaction_type);
        if(tt==null) throw new SQLException("Unable to find TransactionType: "+setup_fee_transaction_type);
        return tt;
    }
    
    public int getMonthlyRate() {
        return monthly_rate;
    }
    
    public TransactionType getMonthlyRateTransactionType() throws SQLException, IOException {
        if(monthly_rate_transaction_type==null) return null;
        TransactionType tt=table.connector.getTransactionTypes().get(monthly_rate_transaction_type);
        if(tt==null) throw new SQLException("Unable to find TransactionType: "+monthly_rate_transaction_type);
        return tt;
    }

    public boolean isActive() {
        return active;
    }
    
    public int copy() throws IOException, SQLException {
        return table.connector.requestIntQueryIL(true, AOServProtocol.CommandID.COPY_PACKAGE_DEFINITION, pkey);
    }

    public void setActive(boolean active) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_PACKAGE_DEFINITION_ACTIVE, pkey, active);
    }

    public boolean isApproved() {
        return approved;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.PACKAGE_DEFINITIONS;
    }

    public void init(ResultSet result) throws SQLException {
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
        accounting=in.readUTF().intern();
        category=in.readUTF().intern();
        name=in.readUTF();
        version=in.readUTF();
        display=in.readUTF();
        description=in.readUTF();
        setup_fee=in.readCompressedInt();
        setup_fee_transaction_type=StringUtility.intern(in.readNullUTF());
        monthly_rate=in.readCompressedInt();
        monthly_rate_transaction_type=StringUtility.intern(in.readNullUTF());
        active=in.readBoolean();
        approved=in.readBoolean();
    }

    @Override
    String toStringImpl() {
        return display;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version aoservVersion) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeUTF(accounting);
        out.writeUTF(category);
        out.writeUTF(name);
        out.writeUTF(version);
        out.writeUTF(display);
        out.writeUTF(description);
        out.writeCompressedInt(setup_fee);
        out.writeNullUTF(setup_fee_transaction_type);
        out.writeCompressedInt(monthly_rate);
        out.writeNullUTF(monthly_rate_transaction_type);
        out.writeBoolean(active);
        out.writeBoolean(approved);
    }
    
    public List<CannotRemoveReason> getCannotRemoveReasons() throws IOException, SQLException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>(1);
        List<Package> packs=getPackages();
        if(!packs.isEmpty()) reasons.add(new CannotRemoveReason<Package>("Used by "+packs.size()+" "+(packs.size()==1?"package":"packages"), packs));
        return reasons;
    }
    
    public void remove() throws IOException, SQLException {
        table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.PACKAGE_DEFINITIONS,
            pkey
    	);
    }

    public void update(
        final Business business,
        final PackageCategory category,
        final String name,
        final String version,
        final String display,
        final String description,
        final int setupFee,
        final TransactionType setupFeeTransactionType,
        final int monthlyRate,
        final TransactionType monthlyRateTransactionType
    ) throws IOException, SQLException {
        table.connector.requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.UPDATE_PACKAGE_DEFINITION.ordinal());
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
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        invalidateList=AOServConnector.readInvalidateList(in);
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unknown response code: "+code);
                    }
                }

                public void afterRelease() {
                    table.connector.tablesUpdated(invalidateList);
                }
            }
        );
    }
}