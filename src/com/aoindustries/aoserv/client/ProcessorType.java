/*
 * Copyright 2008-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

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

    public String getType() {
        return key;
    }

    public short getSortOrder() {
        return sortOrder;
    }
}