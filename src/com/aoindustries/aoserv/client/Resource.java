package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

/**
 * A <code>Resource</code> is a measurable hardware resource.  A <code>Package</code>
 * comes with a set of resources, and when those <code>PackageDefinitionLimit</code>s are exceeded,
 * an additional amount is charged to the <code>Business</code>.
 *
 * @see  Package
 *
 * @author  AO Industries, Inc.
 */
final public class Resource extends GlobalObjectStringKey<Resource> {

    static final int COLUMN_NAME=0;
    static final String COLUMN_NAME_name = "name";

    public static final String
        AOSERV_DAEMON="aoserv_daemon",
        AOSERV_MASTER="aoserv_master",
        BANDWIDTH="bandwidth",
        CONSULTING="consulting",
        DISK="disk",
        DISTRIBUTION_SCAN="distribution_scan",
        DRUPAL="drupal",
        FAILOVER="failover",
        HARDWARE_DISK_ATA_7200_120="hardware_disk_ata_7200_120",
        HTTPD="httpd",
        IP="ip",
        JAVAVM="javavm",
        JOOMLA="joomla",
        MYSQL_REPLICATION="mysql_replication",
        POP="pop",
        RACK="rack",
        SERVER_DATABASE="server_database",
        SERVER_ENTERPRISE="server_enterprise",
        SERVER_P4="server_p4",
        SERVER_SCSI="server_scsi",
        SERVER_XEON="server_xeon",
        SITE="site",
        SYSADMIN="sysadmin",
        USER="user"
    ;

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_NAME: return pkey;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    /**
     * Gets the unique name of this resource.
     */
    public String getName() {
        return pkey;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.RESOURCES;
    }

    public String getDisplayUnit(Locale userLocale, int quantity) {
        if(quantity==1) return ApplicationResources.getMessage(userLocale, "Resource."+pkey+".singularDisplayUnit", quantity);
        else return ApplicationResources.getMessage(userLocale, "Resource."+pkey+".pluralDisplayUnit", quantity);
    }

    public String getPerUnit(Locale userLocale, Object amount) {
        return ApplicationResources.getMessage(userLocale, "Resource."+pkey+".perUnit", amount);
    }

    public void init(ResultSet result) throws SQLException {
        pkey = result.getString(1);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readUTF().intern();
    }

    @Override
    String toStringImpl(Locale userLocale) {
        return ApplicationResources.getMessage(userLocale, "Resource."+pkey+".toString");
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(pkey);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_60)<=0) {
            out.writeUTF(ApplicationResources.getMessage(Locale.getDefault(), "Resource."+pkey+".singularDisplayUnit", ""));
        }
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_123)>=0 && version.compareTo(AOServProtocol.Version.VERSION_1_60)<=0) {
            out.writeUTF(ApplicationResources.getMessage(Locale.getDefault(), "Resource."+pkey+".pluralDisplayUnit", ""));
            out.writeUTF(getPerUnit(Locale.getDefault(), ""));
        }
        if(version.compareTo(AOServProtocol.Version.VERSION_1_60)<=0) out.writeUTF(toString(Locale.getDefault())); // description
    }
}
