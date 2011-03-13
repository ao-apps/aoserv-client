/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;

/**
 * AO Industries provides greater reliability through the use of multiple network locations.
 * Each location is represented by a <code>ServerFarm</code> object.
 *
 * @author  AO Industries, Inc.
 */
final public class ServerFarm extends AOServObjectDomainLabelKey implements Comparable<ServerFarm>, DtoFactory<com.aoindustries.aoserv.client.dto.ServerFarm> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private String description;
    private AccountingCode owner;
    final private boolean useRestrictedSmtpPort;

    public ServerFarm(AOServConnector connector, DomainLabel name, String description, AccountingCode owner, boolean useRestrictedSmtpPort) {
        super(connector, name);
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

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(ServerFarm other) {
        return getKey().compareTo(other.getKey());
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
    @DependencySingleton
    @SchemaColumn(order=2, name=COLUMN_OWNER, index=IndexType.INDEXED, description="the business that owns of the farm")
    public Business getOwner() throws RemoteException {
        return getConnector().getBusinesses().filterUnique(Business.COLUMN_ACCOUNTING, owner);
    }

    @SchemaColumn(order=3, name="use_restricted_smtp_port", description="outgoing servers should use restricted source ports (affects firewall rules)")
    public boolean useRestrictedSmtpPort() {
        return useRestrictedSmtpPort;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public ServerFarm(AOServConnector connector, com.aoindustries.aoserv.client.dto.ServerFarm dto) throws ValidationException {
        this(
            connector,
            getDomainLabel(dto.getName()),
            dto.getDescription(),
            getAccountingCode(dto.getOwner()),
            dto.isUseRestrictedSmtpPort()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.ServerFarm getDto() {
        return new com.aoindustries.aoserv.client.dto.ServerFarm(getDto(getKey()), description, getDto(owner), useRestrictedSmtpPort);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
    	return description;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    @DependentObjectSet
    public List<Rack> getRacks() throws IOException, SQLException {
        return getConnector().getRacks().getIndexedRows(Rack.COLUMN_FARM, pkey);
    }*/

    @DependentObjectSet
    public IndexedSet<Server> getServers() throws RemoteException {
        return getConnector().getServers().filterIndexed(Server.COLUMN_FARM, this);
    }
    // </editor-fold>
}
