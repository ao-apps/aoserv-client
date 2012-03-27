package com.aoindustries.aoserv.client;

/*
 * Copyright 2008-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * A <code>VirtualServer</code> consumes physical resources within the
 * virtualization layer.
 *
 * @author  AO Industries, Inc.
 */
final public class VirtualServer extends CachedObjectIntegerKey<VirtualServer> {

    static final int COLUMN_SERVER=0;

    static final String COLUMN_SERVER_name = "server";

    private int primaryRam;
    private int primaryRamTarget;
    private int secondaryRam;
    private int secondaryRamTarget;
    private String minimumProcessorType;
    private String minimumProcessorArchitecture;
    private int minimumProcessorSpeed;
    private int minimumProcessorSpeedTarget;
    private short processorCores;
    private short processorCoresTarget;
    private short processorWeight;
    private short processorWeightTarget;
    private boolean primaryPhysicalServerLocked;
    private boolean secondaryPhysicalServerLocked;
    private boolean requires_hvm;
    private String vnc_password;

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_SERVER: return Integer.valueOf(pkey);
            case 1 : return primaryRam;
            case 2 : return primaryRamTarget;
            case 3 : return secondaryRam==-1 ? null : Integer.valueOf(secondaryRam);
            case 4 : return secondaryRamTarget==-1 ? null : Integer.valueOf(secondaryRamTarget);
            case 5 : return minimumProcessorType;
            case 6 : return minimumProcessorArchitecture;
            case 7 : return minimumProcessorSpeed==-1 ? null : Integer.valueOf(minimumProcessorSpeed);
            case 8 : return minimumProcessorSpeedTarget==-1 ? null : Integer.valueOf(minimumProcessorSpeedTarget);
            case 9 : return processorCores;
            case 10 : return processorCoresTarget;
            case 11 : return processorWeight;
            case 12 : return processorWeightTarget;
            case 13 : return primaryPhysicalServerLocked;
            case 14 : return secondaryPhysicalServerLocked;
            case 15 : return requires_hvm;
            case 16 : return vnc_password;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public Server getServer() throws SQLException, IOException {
        Server se=table.connector.getServers().get(pkey);
        if(se==null) new SQLException("Unable to find Server: "+pkey);
        return se;
    }

    public int getPrimaryRam() {
        return primaryRam;
    }

    public int getPrimaryRamTarget() {
        return primaryRamTarget;
    }

    /**
     * Gets the secondary RAM allocation or <code>-1</code> if no secondary required.
     * When RAM allocation is <code>-1</code>, the VM will not be able to run on the
     * secondary server - it only provides block device replication.  Therefore,
     * other things like processor type, speed, architecture, processor cores and
     * processor weights will also not be allocated.
     */
    public int getSecondaryRam() {
        return secondaryRam;
    }
    
    public int getSecondaryRamTarget() {
        return secondaryRamTarget;
    }

    /**
     * Gets the minimum processor type or <code>null</code> if none.
     */
    public ProcessorType getMinimumProcessorType() throws IOException, SQLException {
        if(minimumProcessorType==null) return null;
        ProcessorType pt = table.connector.getProcessorTypes().get(minimumProcessorType);
        if(pt==null) new SQLException("Unable to find ProcessorType: "+minimumProcessorType);
        return pt;
    }

    /**
     * Gets the minimum processor architecture.
     */
    public Architecture getMinimumProcessorArchitecture() throws IOException, SQLException {
        Architecture a = table.connector.getArchitectures().get(minimumProcessorArchitecture);
        if(a==null) new SQLException("Unable to find Architecture: "+minimumProcessorArchitecture);
        return a;
    }

    /**
     * Gets the minimum processor speed or <code>-1</code> for none.
     */
    public int getMinimumProcessorSpeed() {
        return minimumProcessorSpeed;
    }

    /**
     * Gets the minimum processor speed target or <code>-1</code> for none.
     */
    public int getMinimumProcessorSpeedTarget() {
        return minimumProcessorSpeedTarget;
    }

    /**
     * Gets the processor cores.
     */
    public short getProcessorCores() {
        return processorCores;
    }

    public short getProcessorCoresTarget() {
        return processorCoresTarget;
    }

    /**
     * Gets the processor weight.
     */
    public short getProcessorWeight() {
        return processorWeight;
    }

    public short getProcessorWeightTarget() {
        return processorWeightTarget;
    }

    /**
     * Gets if the primary server is locked (manually set).
     */
    public boolean isPrimaryPhysicalServerLocked() {
        return primaryPhysicalServerLocked;
    }
    
    /**
     * Gets if the secondary server is locked (manually set).
     */
    public boolean isSecondaryPhysicalServerLocked() {
        return secondaryPhysicalServerLocked;
    }
    
    /**
     * Gets if this virtual requires full hardware virtualization support.
     */
    public boolean getRequiresHvm() {
        return requires_hvm;
    }

    /**
     * Gets the VNC password for this virtual server or <code>null</code> if VNC is disabled.
     * The password must be unique between virtual servers because the password is used
     * behind the scenes to resolve the actual IP and port for VNC proxying.
     */
    public String getVncPassword() {
        return vnc_password;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.VIRTUAL_SERVERS;
    }

    public void init(ResultSet result) throws SQLException {
        int pos = 1;
        pkey = result.getInt(pos++);
        primaryRam = result.getInt(pos++);
        primaryRamTarget = result.getInt(pos++);
        secondaryRam = result.getInt(pos++);
        if(result.wasNull()) secondaryRam = -1;
        secondaryRamTarget = result.getInt(pos++);
        if(result.wasNull()) secondaryRamTarget = -1;
        minimumProcessorType = result.getString(pos++);
        minimumProcessorArchitecture = result.getString(pos++);
        minimumProcessorSpeed = result.getInt(pos++);
        if(result.wasNull()) minimumProcessorSpeed = -1;
        minimumProcessorSpeedTarget = result.getInt(pos++);
        if(result.wasNull()) minimumProcessorSpeedTarget = -1;
        processorCores = result.getShort(pos++);
        processorCoresTarget = result.getShort(pos++);
        processorWeight = result.getShort(pos++);
        processorWeightTarget = result.getShort(pos++);
        primaryPhysicalServerLocked = result.getBoolean(pos++);
        secondaryPhysicalServerLocked = result.getBoolean(pos++);
        requires_hvm = result.getBoolean(pos++);
        vnc_password = result.getString(pos++);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey = in.readCompressedInt();
        primaryRam = in.readCompressedInt();
        primaryRamTarget = in.readCompressedInt();
        secondaryRam = in.readCompressedInt();
        secondaryRamTarget = in.readCompressedInt();
        minimumProcessorType = StringUtility.intern(in.readNullUTF());
        minimumProcessorArchitecture = in.readUTF().intern();
        minimumProcessorSpeed = in.readCompressedInt();
        minimumProcessorSpeedTarget = in.readCompressedInt();
        processorCores = in.readShort();
        processorCoresTarget = in.readShort();
        processorWeight = in.readShort();
        processorWeightTarget = in.readShort();
        primaryPhysicalServerLocked = in.readBoolean();
        secondaryPhysicalServerLocked = in.readBoolean();
        requires_hvm = in.readBoolean();
        vnc_password = in.readNullUTF();
    }

    @Override
    protected String toStringImpl() throws SQLException, IOException {
        return getServer().toStringImpl();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(primaryRam);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_43)>=0) out.writeCompressedInt(primaryRamTarget);
        out.writeCompressedInt(secondaryRam);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_43)>=0) out.writeCompressedInt(secondaryRamTarget);
        out.writeNullUTF(minimumProcessorType);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_40)<=0) out.writeNullUTF(secondaryRam==-1 ? null : minimumProcessorType);
        out.writeUTF(minimumProcessorArchitecture);
        out.writeCompressedInt(minimumProcessorSpeed);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_43)>=0) out.writeCompressedInt(minimumProcessorSpeedTarget);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_40)<=0) out.writeCompressedInt(secondaryRam==-1 ? -1 : minimumProcessorSpeed);
        out.writeShort(processorCores);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_43)>=0) out.writeShort(processorCoresTarget);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_40)<=0) out.writeShort(secondaryRam==-1 ? -1 : processorCores);
        out.writeShort(processorWeight);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_43)>=0) out.writeShort(processorWeightTarget);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_40)<=0) out.writeShort(secondaryRam==-1 ? -1 : processorWeight);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_40)<=0) out.writeCompressedInt(-1);
        out.writeBoolean(primaryPhysicalServerLocked);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_40)<=0) out.writeCompressedInt(-1);
        out.writeBoolean(secondaryPhysicalServerLocked);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_37)>=0) out.writeBoolean(requires_hvm);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_51)>=0) out.writeNullUTF(vnc_password);
    }

    public List<VirtualDisk> getVirtualDisks() throws IOException, SQLException {
        return table.connector.getVirtualDisks().getVirtualDisks(this);
    }
    
    /**
     * Gets the virtual disk for this virtual server and the provided device
     * name.
     * @param device should be <code>xvd[a-z]</code>
     * @return the disk or <code>null</code> if not found
     */
    public VirtualDisk getVirtualDisk(String device) throws IOException, SQLException {
        for(VirtualDisk vd : getVirtualDisks()) {
            if(vd.getDevice().equals(device)) return vd;
        }
        return null;
    }

    public AOServer.DaemonAccess requestVncConsoleAccess() throws IOException, SQLException {
        return table.connector.requestResult(
            true,
            new AOServConnector.ResultRequest<AOServer.DaemonAccess>() {
                private AOServer.DaemonAccess daemonAccess;
                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.REQUEST_VNC_CONSOLE_DAEMON_ACCESS.ordinal());
                    out.writeCompressedInt(pkey);
                }
                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        daemonAccess = new AOServer.DaemonAccess(
                            in.readUTF(),
                            in.readUTF(),
                            in.readCompressedInt(),
                            in.readLong()
                        );
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }
                public AOServer.DaemonAccess afterRelease() {
                    return daemonAccess;
                }
            }
        );
    }
}
