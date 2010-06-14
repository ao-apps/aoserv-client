package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.DomainLabel;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

/**
 * AO Industries provides greater reliability through the use of multiple network locations.
 * Each location is represented by a <code>ServerFarm</code> object.
 *
 * @author  AO Industries, Inc.
 */
final public class ServerFarm extends AOServObjectDomainLabelKey<ServerFarm> implements BeanFactory<com.aoindustries.aoserv.client.beans.ServerFarm> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private String description;
    private AccountingCode owner;
    final private boolean useRestrictedSmtpPort;

    public ServerFarm(ServerFarmService<?,?> service, DomainLabel name, String description, AccountingCode owner, boolean useRestrictedSmtpPort) {
        super(service, name);
        this.description = description;
        this.owner = owner;
        this.useRestrictedSmtpPort = useRestrictedSmtpPort;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        description = intern(description);
        owner = intern(owner);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="name", index=IndexType.PRIMARY_KEY, description="the unique name of the farm")
    public DomainLabel getName() {
    	return getKey();
    }

    @SchemaColumn(order=1, name="description", description="a description of the farm")
    public String getDescription() {
    	return description;
    }

    /**
     * May be filtered.
     */
    static final String COLUMN_OWNER = "owner";
    @SchemaColumn(order=2, name=COLUMN_OWNER, index=IndexType.INDEXED, description="the business that owns of the farm")
    public Business getOwner() throws RemoteException {
        return getService().getConnector().getBusinesses().filterUnique(Business.COLUMN_ACCOUNTING, owner);
    }

    @SchemaColumn(order=3, name="use_restricted_smtp_port", description="outgoing servers should use restricted source ports (affects firewall rules)")
    public boolean useRestrictedSmtpPort() {
        return useRestrictedSmtpPort;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.ServerFarm getBean() {
        return new com.aoindustries.aoserv.client.beans.ServerFarm(getBean(getKey()), description, getBean(owner), useRestrictedSmtpPort);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getOwner()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getServers()
            // TODO: getRacks()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) {
    	return description;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    public List<Rack> getRacks() throws IOException, SQLException {
        return getService().getConnector().getRacks().getIndexedRows(Rack.COLUMN_FARM, pkey);
    }*/

    public IndexedSet<Server> getServers() throws RemoteException {
        return getService().getConnector().getServers().filterIndexed(Server.COLUMN_FARM, this);
    }
    // </editor-fold>
}
