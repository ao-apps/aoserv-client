/*
 * Copyright 2003-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;

/**
 * One type of operating system.
 *
 * @see Server
 *
 * @author  AO Industries, Inc.
 */
final public class OperatingSystem extends AOServObjectStringKey implements Comparable<OperatingSystem>, DtoFactory<com.aoindustries.aoserv.client.dto.OperatingSystem> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final String
        CENTOS="centos",
        DEBIAN="debian",
        GENTOO="gentoo",
        MANDRAKE="mandrake",
        MANDRIVA="mandriva",
        REDHAT="redhat",
        WINDOWS="windows"
    ;
    
    public static final String DEFAULT_OPERATING_SYSTEM=CENTOS;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private String display;
    final private boolean isUnix;

    public OperatingSystem(AOServConnector connector, String name, String display, boolean is_unix) {
        super(connector, name);
        this.display = display;
        this.isUnix = is_unix;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        display = intern(display);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(OperatingSystem other) {
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(getKey(), other.getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="name", index=IndexType.PRIMARY_KEY, description="the unique name of the operating system")
    public String getName() {
        return getKey();
    }

    @SchemaColumn(order=1, name="display", description="the display version of the name")
    public String getDisplay() {
        return display;
    }

    @SchemaColumn(order=2, name="is_unix", description="indicates that this is a Unix-based OS")
    public boolean isUnix() {
        return isUnix;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public OperatingSystem(AOServConnector connector, com.aoindustries.aoserv.client.dto.OperatingSystem dto) {
        this(connector, dto.getName(), dto.getDisplay(), dto.isIsUnix());
    }

    @Override
    public com.aoindustries.aoserv.client.dto.OperatingSystem getDto() {
        return new com.aoindustries.aoserv.client.dto.OperatingSystem(getKey(), display, isUnix);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<OperatingSystemVersion> getOperatingSystemVersions() throws RemoteException {
        return getConnector().getOperatingSystemVersions().filterIndexed(OperatingSystemVersion.COLUMN_OPERATING_SYSTEM, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return display;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public OperatingSystemVersion getOperatingSystemVersion(AOServConnector conn, String version, Architecture architecture) throws IOException, SQLException {
        return conn.getOperatingSystemVersions().getOperatingSystemVersion(this, version, architecture);
    }*/
    // </editor-fold>
}