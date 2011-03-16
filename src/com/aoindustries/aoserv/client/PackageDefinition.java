/*
 * Copyright 2005-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import com.aoindustries.util.i18n.Money;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;

/**
 * A <code>PackageDefinition</code> stores one unique set of resource types, limits, and prices.
 *
 * @author  AO Industries, Inc.
 */
final public class PackageDefinition extends AOServObjectIntegerKey implements Comparable<PackageDefinition>, DtoFactory<com.aoindustries.aoserv.client.dto.PackageDefinition> /*TODO:, Removable */ {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = -6314674051475407184L;

    private String category;
    final private String name;
    final private String version;
    final private Money setupFee;
    private String setupFeeTransactionType;
    final private Money monthlyRate;
    private String monthlyRateTransactionType;
    final private boolean approved;

    public PackageDefinition(
        AOServConnector connector,
        int pkey,
        String category,
        String name,
        String version,
        Money setupFee,
        String setupFeeTransactionType,
        Money monthlyRate,
        String monthlyRateTransactionType,
        boolean approved
    ) {
        super(connector, pkey);
        this.category = category;
        this.name = name;
        this.version = version;
        this.setupFee = setupFee;
        this.setupFeeTransactionType = setupFeeTransactionType;
        this.monthlyRate = monthlyRate;
        this.monthlyRateTransactionType = monthlyRateTransactionType;
        this.approved = approved;
        intern();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        category = intern(category);
        setupFeeTransactionType = intern(setupFeeTransactionType);
        monthlyRateTransactionType = intern(monthlyRateTransactionType);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(PackageDefinition other) {
        try {
            int diff = category==other.category ? 0 : getCategory().compareTo(other.getCategory());
            if(diff!=0) return diff;
            diff = monthlyRate.compareTo(other.monthlyRate);
            if(diff!=0) return diff;
            diff = compareIgnoreCaseConsistentWithEquals(name, other.name);
            if(diff!=0) return diff;
            return compareIgnoreCaseConsistentWithEquals(version, other.version);
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    public static final MethodColumn COLUMN_PKEY = getMethodColumn(PackageDefinition.class, "pkey");
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="the unique ID of the package definition")
    public int getPkey() {
        return getKeyInt();
    }

    public static final MethodColumn COLUMN_CATEGORY = getMethodColumn(PackageDefinition.class, "category");
    @DependencySingleton
    @SchemaColumn(order=1, index=IndexType.INDEXED, description="the package category")
    public PackageCategory getCategory() throws RemoteException {
        return getConnector().getPackageCategories().get(category);
    }

    @SchemaColumn(order=2, description="the name of the package")
    public String getName() {
        return name;
    }

    @SchemaColumn(order=3, description="the unique version of this package")
    public String getVersion() {
        return version;
    }

    /**
     * Gets the setup fee or <code>null</code> for none.
     */
    @SchemaColumn(order=4, description="the setup fee for this package definition")
    public Money getSetupFee() {
        return setupFee;
    }

    public static final MethodColumn COLUMN_SETUP_FEE_TRANSACTION_TYPE = getMethodColumn(PackageDefinition.class, "setupFeeTransactionType");
    @DependencySingleton
    @SchemaColumn(order=5, index=IndexType.INDEXED, description="the type of transaction of the setup fee")
    public TransactionType getSetupFeeTransactionType() throws RemoteException {
        if(setupFeeTransactionType==null) return null;
        return getConnector().getTransactionTypes().get(setupFeeTransactionType);
    }

    @SchemaColumn(order=6, description="the default monthly charge for this package")
    public Money getMonthlyRate() {
        return monthlyRate;
    }

    public static final MethodColumn COLUMN_MONTHLY_RATE_TRANSACTION_TYPE = getMethodColumn(PackageDefinition.class, "monthlyRateTransactionType");
    @DependencySingleton
    @SchemaColumn(order=7, index=IndexType.INDEXED, description="the type of transaction for the monthly fee")
    public TransactionType getMonthlyRateTransactionType() throws RemoteException {
        if(monthlyRateTransactionType==null) return null;
        return getConnector().getTransactionTypes().get(monthlyRateTransactionType);
    }

    @SchemaColumn(order=8, description="once approved a definition may be used for businesses, but may not be modified")
    public boolean isApproved() {
        return approved;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public PackageDefinition(AOServConnector connector, com.aoindustries.aoserv.client.dto.PackageDefinition dto) {
        this(
            connector,
            dto.getPkey(),
            dto.getCategory(),
            dto.getName(),
            dto.getVersion(),
            getMoney(dto.getSetupFee()),
            dto.getSetupFeeTransactionType(),
            getMoney(dto.getMonthlyRate()),
            dto.getMonthlyRateTransactionType(),
            dto.isApproved()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.PackageDefinition getDto() {
        return new com.aoindustries.aoserv.client.dto.PackageDefinition(
            getKeyInt(),
            category,
            name,
            version,
            getDto(setupFee),
            setupFeeTransactionType,
            getDto(monthlyRate),
            monthlyRateTransactionType,
            approved
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return category+"|"+name+"|"+version;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /**
     * Gets the list of businesses using this definition.
     */
    @DependentObjectSet
    public IndexedSet<Business> getBusinesses() throws RemoteException {
        return getConnector().getBusinesses().filterIndexed(Business.COLUMN_PACKAGE_DEFINITION, this);
    }

    @DependentObjectSet
    public IndexedSet<PackageDefinitionLimit> getLimits() throws RemoteException {
        return getConnector().getPackageDefinitionLimits().filterIndexed(PackageDefinitionLimit.COLUMN_PACKAGE_DEFINITION, this);
    }

    @DependentObjectSet
    public IndexedSet<PackageDefinitionBusiness> getPackageDefinitionBusinesses() throws RemoteException {
        return getConnector().getPackageDefinitionBusinesses().filterIndexed(PackageDefinitionBusiness.COLUMN_PACKAGE_DEFINITION, this);
    }

    /* TODO
    public PackageDefinitionLimit getLimit(ResourceType resourceType) throws RemoteException {
        if(resourceType==null) throw new AssertionError("resourceType is null");
        return getConnector().getPackageDefinitionLimits().getPackageDefinitionLimit(this, resourceType);
    }

    @DependentObjectSet
    public List<SignupRequest> getSignupRequests() throws RemoteException {
        return getConnector().getSignupRequests().getIndexedRows(SignupRequest.COLUMN_PACKAGE_DEFINITION, pkey);
    }
    */
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public void setLimits(final PackageDefinitionLimit[] limits) throws RemoteException {
        getConnector().requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.SET_PACKAGE_DEFINITION_LIMITS.ordinal());
                    out.writeCompressedInt(pkey);
                    out.writeCompressedInt(limits.length);
                    for(int c=0;c<limits.length;c++) {
                        PackageDefinitionLimit limit=limits[c];
                        out.writeUTF(limit.resourceType);
                        out.writeCompressedInt(limit.soft_limit);
                        out.writeCompressedInt(limit.hard_limit);
                        out.writeCompressedInt(limit.additional_rate);
                        out.writeBoolean(limit.additional_transaction_type!=null);
                        if(limit.additional_transaction_type!=null) out.writeUTF(limit.additional_transaction_type);
                    }
                }

                public void readResponse(CompressedDataInputStream in) throws RemoteException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        invalidateList=AOServConnector.readInvalidateList(in);
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unknown response code: "+code);
                    }
                }

                public void afterRelease() {
                    getConnector().tablesUpdated(invalidateList);
                }
            }
        );
    }

    public int copy() throws RemoteException {
        return getConnector().requestIntQueryIL(true, AOServProtocol.CommandID.COPY_PACKAGE_DEFINITION, pkey);
    }

    public void setActive(boolean active) throws RemoteException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_PACKAGE_DEFINITION_ACTIVE, pkey, active);
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() throws RemoteException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>(1);
        List<Business> bus=getBusinesses();
        if(!bus.isEmpty()) reasons.add(new CannotRemoveReason<Business>("Used by "+bus.size()+" "+(bus.size()==1?"business":"businesses"), bus));
        return reasons;
    }

    public void remove() throws RemoteException {
        getConnector().requestUpdateIL(
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
    ) throws RemoteException {
        getConnector().requestUpdate(
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

                public void readResponse(CompressedDataInputStream in) throws RemoteException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        invalidateList=AOServConnector.readInvalidateList(in);
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unknown response code: "+code);
                    }
                }

                public void afterRelease() {
                    getConnector().tablesUpdated(invalidateList);
                }
            }
        );
    }
     */
    // </editor-fold>
}