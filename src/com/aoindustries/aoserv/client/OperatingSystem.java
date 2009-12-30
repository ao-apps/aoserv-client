package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.Locale;

/**
 * One type of operating system.
 *
 * @see Server
 *
 * @author  AO Industries, Inc.
 */
final public class OperatingSystem extends AOServObjectStringKey<OperatingSystem> implements BeanFactory<com.aoindustries.aoserv.client.beans.OperatingSystem> {

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
    final private String display;
    final private boolean isUnix;

    public OperatingSystem(OperatingSystemService<?,?> service, String name, String display, boolean is_unix) {
        super(service, name);
        this.display = display;
        this.isUnix = is_unix;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="name", unique=true, description="the unique name of the operating system")
    public String getName() {
        return key;
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

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.OperatingSystem getBean() {
        return new com.aoindustries.aoserv.client.beans.OperatingSystem(key, display, isUnix);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) {
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