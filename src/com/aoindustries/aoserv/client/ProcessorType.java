/*
 * Copyright 2008-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;

/**
 * All of the types of processors.
 *
 * @author  AO Industries, Inc.
 */
final public class ProcessorType extends AOServObjectStringKey<ProcessorType> implements BeanFactory<com.aoindustries.aoserv.client.beans.ProcessorType> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private short sortOrder;

    public ProcessorType(ProcessorTypeService<?,?> service, String type, short sortOrder) {
        super(service, type);
        this.sortOrder = sortOrder;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(ProcessorType other) {
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

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    @Override
    public com.aoindustries.aoserv.client.beans.ProcessorType getBean() {
        return new com.aoindustries.aoserv.client.beans.ProcessorType(getKey(), sortOrder);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getPhysicalServers());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getVirtualServersByMinimumProcessorType());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<VirtualServer> getVirtualServersByMinimumProcessorType() throws RemoteException {
        return getService().getConnector().getVirtualServers().filterIndexed(VirtualServer.COLUMN_MINIMUM_PROCESSOR_TYPE, this);
    }
    // </editor-fold>
}