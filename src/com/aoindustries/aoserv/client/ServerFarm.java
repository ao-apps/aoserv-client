package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

/**
 * AO Industries provides greater reliability through the use of multiple network locations.
 * Each location is represented by a <code>ServerFarm</code> object.
 *
 * @author  AO Industries, Inc.
 */
final public class ServerFarm extends AOServObjectStringKey<ServerFarm> implements BeanFactory<com.aoindustries.aoserv.client.beans.ServerFarm> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private String description;
    final private String owner;
    final private boolean useRestrictedSmtpPort;

    public ServerFarm(ServerFarmService<?,?> service, String name, String description, String owner, boolean useRestrictedSmtpPort) {
        super(service, name);
        this.description = description;
        this.owner = owner.intern();
        this.useRestrictedSmtpPort = useRestrictedSmtpPort;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="name", unique=true, description="the unique name of the farm")
    public String getName() {
    	return key;
    }

    @SchemaColumn(order=1, name="description", description="a description of the farm")
    public String getDescription() {
    	return description;
    }

    /**
     * May be filtered.
     */
    @SchemaColumn(order=2, name="owner", description="the business that owns of the farm")
    public Business getOwner() throws RemoteException {
        return getService().getConnector().getBusinesses().get(owner);
    }

    @SchemaColumn(order=3, name="use_restricted_smtp_port", description="outgoing servers should use restricted source ports (affects firewall rules)")
    public boolean useRestrictedSmtpPort() {
        return useRestrictedSmtpPort;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.ServerFarm getBean() {
        return new com.aoindustries.aoserv.client.beans.ServerFarm(key, description, owner, useRestrictedSmtpPort);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return createDependencySet(
            getOwner()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return createDependencySet(
            // TODO: getServers(),
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
    public List<Server> getServers() throws IOException, SQLException {
        return getService().getConnector().getServers().getIndexedRows(Server.COLUMN_FARM, pkey);
    }

    public List<Rack> getRacks() throws IOException, SQLException {
        return getService().getConnector().getRacks().getIndexedRows(Rack.COLUMN_FARM, pkey);
    }*/
    // </editor-fold>
}
