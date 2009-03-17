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
    ) throws IOException, SQLException {
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

    public int addNetBind(
        Package pk,
        IPAddress ia,
        NetPort netPort,
        NetProtocol netProtocol,
        Protocol appProtocol,
        boolean openFirewall,
        boolean monitoringEnabled
    ) throws IOException, SQLException {
        return table.connector.netBinds.addNetBind(
            this,
            pk,
            ia,
            netPort,
            netProtocol,
            appProtocol,
            openFirewall,
            monitoringEnabled
        );
    }

    public AOServer getAOServer() throws IOException, SQLException {
        return table.connector.aoServers.get(pkey);
    }

    public PhysicalServer getPhysicalServer() {
        return table.connector.physicalServers.get(pkey);
    }

    public VirtualServer getVirtualServer() {
        return table.connector.virtualServers.get(pkey);
    }

    public List<Business> getBusinesses() throws IOException, SQLException {
	return table.connector.businessServers.getBusinesses(this);
    }

    Object getColumnImpl(int i) {
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

    public OperatingSystemVersion getOperatingSystemVersion() throws SQLException, IOException {
        if(operating_system_version==-1) return null;
        OperatingSystemVersion osv=table.connector.operatingSystemVersions.get(operating_system_version);
        if(osv==null) new SQLException("Unable to find OperatingSystemVersion: "+operating_system_version);
        return osv;
    }
    
    /**
     * May be filtered.
     */
    public Package getPackage() throws IOException, SQLException {
        return table.connector.packages.get(packageId);
    }

    public String getName() {
        return name;
    }

    public boolean isMonitoringEnabled() {
        return monitoring_enabled;
    }

    public ServerFarm getServerFarm() throws SQLException {
	ServerFarm sf=table.connector.serverFarms.get(farm);
	if(sf==null) throw new SQLException("Unable to find ServerFarm: "+farm);
	return sf;
    }

    public String getDescription() {
        return description;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SERVERS;
    }

    public void init(ResultSet result) throws SQLException {
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
    protected String toStringImpl() throws IOException, SQLException {
        AOServer aoServer = getAOServer();
        if(aoServer!=null) return aoServer.toStringImpl();
        Package pk = getPackage();
        if(pk!=null) return pk.getName()+'/'+name;
        return Integer.toString(pkey);
    }

    @Override
    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
            out.writeUTF(name); // hostname
        }
	out.writeUTF(farm);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
            out.writeUTF("AOINDUSTRIES"); // owner
            out.writeUTF("orion"); // administrator
        }
        out.writeUTF(description);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_107)<=0) out.writeUTF(Architecture.I686);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
            out.writeCompressedInt(0); // backup_hour
            out.writeLong(-1); // last_backup_time
        }
        if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
            out.writeCompressedInt(operating_system_version==-1 ? OperatingSystemVersion.MANDRIVA_2006_0_I586 : operating_system_version);
        } else {
            out.writeCompressedInt(operating_system_version);
        }
        if(
            version.compareTo(AOServProtocol.Version.VERSION_1_0_A_108)>=0
            && version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0
        ) {
            out.writeNullUTF(null); // asset_label
        }
        if(
            version.compareTo(AOServProtocol.Version.VERSION_1_16)>=0
            && version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0
        ) {
            out.writeFloat(Float.NaN); // minimum_power
            out.writeFloat(Float.NaN); // maximum_power
        }
        if(version.compareTo(AOServProtocol.Version.VERSION_1_31)>=0) {
            out.writeCompressedInt(packageId);
            out.writeUTF(name);
        }
        if(version.compareTo(AOServProtocol.Version.VERSION_1_32)>=0) {
            out.writeBoolean(monitoring_enabled);
        }
    }

    /**
     * Gets the list of all replications coming from this server.
     */
    public List<FailoverFileReplication> getFailoverFileReplications() throws IOException, SQLException {
        return table.connector.failoverFileReplications.getFailoverFileReplications(this);
    }

    public NetBind getNetBind(
        IPAddress ipAddress,
        NetPort port,
        NetProtocol netProtocol
    ) throws IOException, SQLException {
        return table.connector.netBinds.getNetBind(this, ipAddress, port, netProtocol);
    }

    public List<NetBind> getNetBinds() throws IOException, SQLException {
	return table.connector.netBinds.getNetBinds(this);
    }

    public List<NetBind> getNetBinds(IPAddress ipAddress) throws IOException, SQLException {
	return table.connector.netBinds.getNetBinds(this, ipAddress);
    }

    public List<NetBind> getNetBinds(Protocol protocol) throws IOException, SQLException {
	return table.connector.netBinds.getNetBinds(this, protocol);
    }

    public NetDevice getNetDevice(String deviceID) throws IOException, SQLException {
	return table.connector.netDevices.getNetDevice(this, deviceID);
    }

    public List<NetDevice> getNetDevices() throws IOException, SQLException {
	return table.connector.netDevices.getNetDevices(this);
    }

    public List<IPAddress> getIPAddresses() throws IOException, SQLException {
	return table.connector.ipAddresses.getIPAddresses(this);
    }

    public IPAddress getAvailableIPAddress() throws SQLException, IOException {
	for(IPAddress ip : getIPAddresses()) {
            if(
                ip.isAvailable()
                && ip.isAlias()
                && !ip.getNetDevice().getNetDeviceID().isLoopback()
            ) return ip;
	}
	return null;
    }
}
