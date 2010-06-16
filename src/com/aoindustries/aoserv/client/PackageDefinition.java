/*
 * Copyright 2005-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.i18n.Money;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * A <code>PackageDefinition</code> stores one unique set of resource types, limits, and prices.
 *
 * @author  AO Industries, Inc.
 */
final public class PackageDefinition extends AOServObjectIntegerKey<PackageDefinition> implements BeanFactory<com.aoindustries.aoserv.client.beans.PackageDefinition> /*TODO:, Removable */ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private String category;
    final private String name;
    final private String version;
    final private String display;
    final private String description;
    final private Money setupFee;
    private String setupFeeTransactionType;
    final private Money monthlyRate;
    private String monthlyRateTransactionType;
    final private boolean active;
    final private boolean approved;

    public PackageDefinition(
        PackageDefinitionService<?,?> service,
        int pkey,
        String category,
        String name,
        String version,
        String display,
        String description,
        Money setupFee,
        String setupFeeTransactionType,
        Money monthlyRate,
        String monthlyRateTransactionType,
        boolean active,
        boolean approved
    ) {
        super(service, pkey);
        this.category = category;
        this.name = name;
        this.version = version;
        this.display = display;
        this.description = description;
        this.setupFee = setupFee;
        this.setupFeeTransactionType = setupFeeTransactionType;
        this.monthlyRate = monthlyRate;
        this.monthlyRateTransactionType = monthlyRateTransactionType;
        this.active = active;
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
    protected int compareToImpl(PackageDefinition other) throws RemoteException {
        int diff = category==other.category ? 0 : getCategory().compareTo(other.getCategory());
        if(diff!=0) return diff;
        diff = monthlyRate.compareTo(other.monthlyRate);
        if(diff!=0) return diff;
        diff = AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(name, other.name);
        if(diff!=0) return diff;
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(version, other.version);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="the unique ID of the package definition")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_CATEGORY = "category";
    @SchemaColumn(order=1, name=COLUMN_CATEGORY, index=IndexType.INDEXED, description="the package category")
    public PackageCategory getCategory() throws RemoteException {
        return getService().getConnector().getPackageCategories().get(category);
    }

    @SchemaColumn(order=2, name="name", description="the name of the package")
    public String getName() {
        return name;
    }

    @SchemaColumn(order=3, name="version", description="the unique version of this package")
    public String getVersion() {
        return version;
    }

    @SchemaColumn(order=4, name="display", description="a short description for display use")
    public String getDisplay() {
        return display;
    }

    @SchemaColumn(order=5, name="description", description="a description of the package definition")
    public String getDescription() {
        return description;
    }

    /**
     * Gets the setup fee or <code>null</code> for none.
     */
    @SchemaColumn(order=6, name="setup_fee", description="the setup fee for this package definition")
    public Money getSetupFee() {
        return setupFee;
    }

    static final String COLUMN_SETUP_FEE_TRANSACTION_TYPE = "setup_fee_transaction_type";
    @SchemaColumn(order=7, name=COLUMN_SETUP_FEE_TRANSACTION_TYPE, index=IndexType.INDEXED, description="the type of transaction of the setup fee")
    public TransactionType getSetupFeeTransactionType() throws RemoteException {
        if(setupFeeTransactionType==null) return null;
        return getService().getConnector().getTransactionTypes().get(setupFeeTransactionType);
    }

    @SchemaColumn(order=8, name="monthly_rate", description="the default monthly charge for this package")
    public Money getMonthlyRate() {
        return monthlyRate;
    }

    static final String COLUMN_MONTHLY_RATE_TRANSACTION_TYPE = "monthly_rate_transaction_type";
    @SchemaColumn(order=9, name=COLUMN_MONTHLY_RATE_TRANSACTION_TYPE, index=IndexType.INDEXED, description="the type of transaction for the monthly fee")
    public TransactionType getMonthlyRateTransactionType() throws RemoteException {
        if(monthlyRateTransactionType==null) return null;
        return getService().getConnector().getTransactionTypes().get(monthlyRateTransactionType);
    }

    @SchemaColumn(order=10, name="active", description="allows new accounts for this package")
    public boolean isActive() {
        return active;
    }

    @SchemaColumn(order=11, name="approved", description="once approved a definition may be used for packages, but may not be modified")
    public boolean isApproved() {
        return approved;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    @Override
    public com.aoindustries.aoserv.client.beans.PackageDefinition getBean() {
        return new com.aoindustries.aoserv.client.beans.PackageDefinition(
            key,
            category,
            name,
            version,
            display,
            description,
            getBean(setupFee),
            setupFeeTransactionType,
            getBean(monthlyRate),
            monthlyRateTransactionType,
            active,
            approved
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getCategory(),
            getSetupFeeTransactionType(),
            getMonthlyRateTransactionType()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getBusinesses()
            // TODO: getLimits(),
            // TODO: getSignupRequests()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return display;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /**
     * Gets the list of businesses using this definition.
     */
    public IndexedSet<Business> getBusinesses() throws RemoteException {
        return getService().getConnector().getBusinesses().filterIndexed(Business.COLUMN_PACKAGE_DEFINITION, this);
    }
    /* TODO
    public PackageDefinitionLimit getLimit(ResourceType resourceType) throws RemoteException {
        if(resourceType==null) throw new AssertionError("resourceType is null");
        return getService().getConnector().getPackageDefinitionLimits().getPackageDefinitionLimit(this, resourceType);
    }

    public List<PackageDefinitionLimit> getLimits() throws RemoteException {
        return getService().getConnector().getPackageDefinitionLimits().getPackageDefinitionLimits(this);
    }

    public List<SignupRequest> getSignupRequests() throws RemoteException {
        return getService().getConnector().getSignupRequests().getIndexedRows(SignupRequest.COLUMN_PACKAGE_DEFINITION, pkey);
    }
    */
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public void setLimits(final PackageDefinitionLimit[] limits) throws RemoteException {
        getService().getConnector().requestUpdate(
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
                    getService().getConnector().tablesUpdated(invalidateList);
                }
            }
        );
    }

    public int copy() throws RemoteException {
        return getService().getConnector().requestIntQueryIL(true, AOServProtocol.CommandID.COPY_PACKAGE_DEFINITION, pkey);
    }

    public void setActive(boolean active) throws RemoteException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_PACKAGE_DEFINITION_ACTIVE, pkey, active);
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() throws RemoteException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>(1);
        List<Business> bus=getBusinesses();
        if(!bus.isEmpty()) reasons.add(new CannotRemoveReason<Business>("Used by "+bus.size()+" "+(bus.size()==1?"business":"businesses"), bus));
        return reasons;
    }

    public void remove() throws RemoteException {
        getService().getConnector().requestUpdateIL(
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
        getService().getConnector().requestUpdate(
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
                    getService().getConnector().tablesUpdated(invalidateList);
                }
            }
        );
    }
     */
    // </editor-fold>
}