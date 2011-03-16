/*
 * Copyright 2000-2011 by AO Industries, Inc.,
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
final public class ServerFarm extends Resource implements Comparable<ServerFarm>, DtoFactory<com.aoindustries.aoserv.client.dto.ServerFarm> {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = 3560486447117887495L;

    private DomainLabel name;
    final private String description;
    final private boolean useRestrictedSmtpPort;

    public ServerFarm(
        AOServConnector connector,
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        DomainLabel name,
        String description,
        boolean useRestrictedSmtpPort
    ) {
        super(connector, pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled);
        this.name = name;
        this.description = description;
        this.useRestrictedSmtpPort = useRestrictedSmtpPort;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        name = intern(name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(ServerFarm other) {
        return name.compareTo(other.name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=RESOURCE_LAST_COLUMN+1, index=IndexType.UNIQUE, description="the unique name of the farm")
    public DomainLabel getName() {
    	return name;
    }

    @SchemaColumn(order=RESOURCE_LAST_COLUMN+2, description="a description of the farm")
    public String getDescription() {
    	return description;
    }

    @SchemaColumn(order=RESOURCE_LAST_COLUMN+3, description="outgoing servers should use restricted source ports (affects firewall rules)")
    public boolean getUseRestrictedSmtpPort() {
        return useRestrictedSmtpPort;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public ServerFarm(AOServConnector connector, com.aoindustries.aoserv.client.dto.ServerFarm dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            dto.getResourceType(),
            getAccountingCode(dto.getAccounting()),
            getTimeMillis(dto.getCreated()),
            getUserId(dto.getCreatedBy()),
            dto.getDisableLog(),
            getTimeMillis(dto.getLastEnabled()),
            getDomainLabel(dto.getName()),
            dto.getDescription(),
            dto.isUseRestrictedSmtpPort()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.ServerFarm getDto() {
        return new com.aoindustries.aoserv.client.dto.ServerFarm(
            getKeyInt(),
            getResourceTypeName(),
            getDto(getAccounting()),
            created,
            getDto(getCreatedByUsername()),
            disableLog,
            lastEnabled,
            getDto(name),
            description,
            useRestrictedSmtpPort
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
    	return description;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<Server> getServers() throws RemoteException {
        return getConnector().getServers().filterIndexed(Server.COLUMN_FARM, this);
    }

    @DependentObjectSet
    public IndexedSet<Rack> getRacks() throws RemoteException {
        return getConnector().getRacks().filterIndexed(Rack.COLUMN_FARM, this);
    }
    // </editor-fold>
}
