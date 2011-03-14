/*
 * Copyright 2008-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

/**
 * A <code>Rack</code> houses <code>PhysicalServer</code>s.
 *
 * @author  AO Industries, Inc.
 */
final public class Rack extends Resource implements Comparable<Rack>, DtoFactory<com.aoindustries.aoserv.client.dto.Rack>{

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = -2729086832214700368L;

    final private int farm;
    final private String name;
    final private Float maxPower;
    final private Integer totalRackUnits;

    public Rack(
        AOServConnector connector,
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        int farm,
        String name,
        Float maxPower,
        Integer totalRackUnits
    ) {
        super(connector, pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled);
        this.farm = farm;
        this.name = name;
        this.maxPower = maxPower;
        this.totalRackUnits = totalRackUnits;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(Rack other) {
        try {
            int diff = farm==other.farm ? 0 : getFarm().compareTo(other.getFarm());
            if(diff!=0) return diff;
            return compareIgnoreCaseConsistentWithEquals(name, other.name);
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    public static final MethodColumn COLUMN_FARM = getMethodColumn(Rack.class, "farm");
    @DependencySingleton
    @SchemaColumn(order=RESOURCE_LAST_COLUMN+1, index=IndexType.INDEXED, description="the server_farm housing the rack")
    public ServerFarm getFarm() throws RemoteException {
        return getConnector().getServerFarms().get(farm);
    }

    @SchemaColumn(order=RESOURCE_LAST_COLUMN+2, description="the per-farm unique name")
    public String getName() {
        return name;
    }

    @SchemaColumn(order=RESOURCE_LAST_COLUMN+3, description="the maximum electrical load supported by the rack")
    public Float getMaxPower() {
        return maxPower;
    }

    @SchemaColumn(order=RESOURCE_LAST_COLUMN+4, description="the number of rack units of physical space")
    public int getTotalRackUnits() {
        return totalRackUnits;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public Rack(AOServConnector connector, com.aoindustries.aoserv.client.dto.Rack dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            dto.getResourceType(),
            getAccountingCode(dto.getAccounting()),
            getTimeMillis(dto.getCreated()),
            getUserId(dto.getCreatedBy()),
            dto.getDisableLog(),
            getTimeMillis(dto.getLastEnabled()),
            dto.getFarm(),
            dto.getName(),
            dto.getMaxPower(),
            dto.getTotalRackUnits()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.Rack getDto() {
        return new com.aoindustries.aoserv.client.dto.Rack(
            key,
            getResourceTypeName(),
            getDto(getAccounting()),
            created,
            getDto(getCreatedByUsername()),
            disableLog,
            lastEnabled,
            farm,
            name,
            maxPower,
            totalRackUnits
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return name;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<PhysicalServer> getPhysicalServers() throws RemoteException {
        return getConnector().getPhysicalServers().filterIndexed(PhysicalServer.COLUMN_RACK, this);
    }
    // </editor-fold>
}
