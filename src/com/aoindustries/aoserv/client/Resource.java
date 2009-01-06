package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.StringUtility;
import java.io.*;
import java.sql.*;

/**
 * A <code>Resource</code> is a measurable hardware resource.  A <code>Package</code>
 * comes with a set of resources, and when those <code>PackageDefinitionLimit</code>s are exceeded,
 * an additional amount is charged to the <code>Business</code>.
 *
 * @see  Package
 *
 * @version  1.0a
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
        FAILOVER="failover",
        HARDWARE_DISK_ATA_7200_120="hardware_disk_ata_7200_120",
        HTTPD="httpd",
        IP="ip",
        JAVAVM="javavm",
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

    private String
        singular_display_unit,
        plural_display_unit,
        per_unit,
        description
    ;


    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_NAME: return pkey;
            case 1: return singular_display_unit;
            case 2: return plural_display_unit;
            case 3: return per_unit;
            case 4: return description;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getDescription() {
	return description;
    }

    public String getName() {
	return pkey;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.RESOURCES;
    }

    public String getSingularDisplayUnit() {
	return singular_display_unit;
    }

    public String getPluralDisplayUnit() {
	return plural_display_unit;
    }

    public String getPerUnit() {
	return per_unit;
    }

    public void init(ResultSet result) throws SQLException {
	pkey = result.getString(1);
	singular_display_unit = result.getString(2);
	plural_display_unit = result.getString(3);
        per_unit = result.getString(4);
	description = result.getString(5);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF().intern();
	singular_display_unit=in.readUTF().intern();
	plural_display_unit=in.readUTF().intern();
        per_unit=in.readUTF().intern();
	description=in.readUTF();
    }

    String toStringImpl() {
	return description;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeUTF(pkey);
	out.writeUTF(singular_display_unit);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_123)>=0) {
            out.writeUTF(plural_display_unit);
            out.writeUTF(per_unit);
        }
	out.writeUTF(description);
    }
}