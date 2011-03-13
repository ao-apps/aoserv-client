/*
 * Copyright 2008-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;

/**
 * All of the types of processors.
 *
 * @author  AO Industries, Inc.
 */
final public class ProcessorType extends AOServObjectStringKey implements Comparable<ProcessorType>, DtoFactory<com.aoindustries.aoserv.client.dto.ProcessorType> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private short sortOrder;

    public ProcessorType(AOServConnector connector, String type, short sortOrder) {
        super(connector, type);
        this.sortOrder = sortOrder;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(ProcessorType other) {
        return AOServObjectUtils.compare(sortOrder, other.sortOrder);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="type", index=IndexType.PRIMARY_KEY, description="the unique name of the type of processor")
    public String getType() {
        return getKey();
    }

    @SchemaColumn(order=1, name="sort_order", index=IndexType.UNIQUE, description="the sort order of the processor, those sorted higher may be substituted for those sorted lower")
    public short getSortOrder() {
        return sortOrder;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public ProcessorType(AOServConnector connector, com.aoindustries.aoserv.client.dto.ProcessorType dto) {
        this(connector, dto.getType(), dto.getSortOrder());
    }

    @Override
    public com.aoindustries.aoserv.client.dto.ProcessorType getDto() {
        return new com.aoindustries.aoserv.client.dto.ProcessorType(getKey(), sortOrder);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<VirtualServer> getVirtualServersByMinimumProcessorType() throws RemoteException {
        return getConnector().getVirtualServers().filterIndexed(VirtualServer.COLUMN_MINIMUM_PROCESSOR_TYPE, this);
    }
    // </editor-fold>
}