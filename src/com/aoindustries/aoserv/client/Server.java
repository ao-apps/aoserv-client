package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.List;

/**
 * A <code>Server</code> stores the details about a single, physical server.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class Server extends CachedObjectIntegerKey<Server> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_PACKAGE=4
    ;
    static final String COLUMN_PACKAGE_name = "package";
    static final String COLUMN_NAME_name = "name";

    /**
     * The daemon key is only available to <code>MasterUser</code>s.  This value is used
     * in place of the key when not accessible.
     */
    public static final String HIDDEN_PASSWORD="*";

    String
        farm
    ;
    private String description;
    private int operating_system_version;
    private int packageId;
    private String name;
    private boolean monitoring_enabled;

    public void addBusiness(
        String accounting,
        String contractVersion,
        Business parent,
        boolean can_add_backup_servers,
        boolean can_add_businesses,
        boolean can_see_prices,
        boolean billParent
    ) {
	table.connector.businesses.addBusiness(
            accounting,
            contractVersion,
            this,
            parent.pkey,
            can_add_backup_servers,
            can_add_businesses,
            can_see_prices,
            billParent
        );
    }

    public AOServer getAOServer() {
        return table.connector.aoServers.get(pkey);
    }

    public List<Business> getBusinesses() {
	return table.connector.businessServers.getBusinesses(this);
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return farm;
            case 2: return description;
            case 3: return operating_system_version==-1 ? null : Integer.valueOf(operating_system_version);
            case COLUMN_PACKAGE: return packageId;
            case 5: return name;
            case 6: return monitoring_enabled;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public OperatingSystemVersion getOperatingSystemVersion() {
        if(operating_system_version==-1) return null;
        OperatingSystemVersion osv=table.connector.operatingSystemVersions.get(operating_system_version);
        if(osv==null) throw new WrappedException(new SQLException("Unable to find OperatingSystemVersion: "+operating_system_version));
        return osv;
    }
    
    /**
     * May be filtered.
     */
    public Package getPackage() {
        return table.connector.packages.get(packageId);
    }

    public String getName() {
        return name;
    }

    public boolean isMonitoringEnabled() {
        return monitoring_enabled;
    }

    public ServerFarm getServerFarm() {
	ServerFarm sf=table.connector.serverFarms.get(farm);
	if(sf==null) throw new WrappedException(new SQLException("Unable to find ServerFarm: "+farm));
	return sf;
    }

    public String getDescription() {
        return description;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SERVERS;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey = result.getInt(1);
	farm = result.getString(2);
        description = result.getString(3);
        operating_system_version=result.getInt(4);
        if(result.wasNull()) operating_system_version = -1;
        packageId = result.getInt(5);
        name = result.getString(6);
        monitoring_enabled = result.getBoolean(7);
    }

    @Override
    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
	farm=in.readUTF().intern();
        description = in.readUTF();
        operating_system_version=in.readCompressedInt();
        packageId = in.readCompressedInt();
        name = in.readUTF();
        monitoring_enabled = in.readBoolean();
    }

    @Override
    protected String toStringImpl() {
        AOServer aoServer = getAOServer();
        if(aoServer!=null) return aoServer.toStringImpl();
        Package pk = getPackage();
        if(pk!=null) return pk.getName()+'/'+name;
        return Integer.toString(pkey);
    }

    @Override
    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_30)<=0) {
            out.writeUTF(name); // hostname
        }
	out.writeUTF(farm);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_30)<=0) {
            out.writeUTF("AOINDUSTRIES"); // owner
            out.writeUTF("orion"); // administrator
        }
        out.writeUTF(description);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_107)<=0) out.writeUTF(Architecture.I686);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_30)<=0) {
            out.writeCompressedInt(0); // backup_hour
            out.writeLong(-1); // last_backup_time
        }
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_30)<=0) {
            out.writeCompressedInt(operating_system_version==-1 ? OperatingSystemVersion.MANDRIVA_2006_0_I586 : operating_system_version);
        } else {
            out.writeCompressedInt(operating_system_version);
        }
        if(
            AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_108)>=0
            && AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_30)<=0
        ) {
            out.writeNullUTF(null); // asset_label
        }
        if(
            AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_16)>=0
            && AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_30)<=0
        ) {
            out.writeFloat(Float.NaN); // minimum_power
            out.writeFloat(Float.NaN); // maximum_power
        }
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_31)>=0) {
            out.writeCompressedInt(packageId);
            out.writeUTF(name);
        }
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_32)>=0) {
            out.writeBoolean(monitoring_enabled);
        }
    }

    /**
     * Gets the list of all replications coming from this server.
     */
    public List<FailoverFileReplication> getFailoverFileReplications() {
        return table.connector.failoverFileReplications.getFailoverFileReplications(this);
    }
}
