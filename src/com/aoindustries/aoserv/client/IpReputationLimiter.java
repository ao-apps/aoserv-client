/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * An <code>IpReputationLimiter</code> rate-limits traffic by class and type.
 *
 * @author  AO Industries, Inc.
 */
final public class IpReputationLimiter extends CachedObjectIntegerKey<IpReputationLimiter> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_NET_DEVICE=1
    ;

    static final String COLUMN_NET_DEVICE_name= "net_device";
    static final String COLUMN_IDENTIFIER_name= "identifier";

    int netDevice;
    private String identifier;
    private String description;

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.IP_REPUTATION_LIMITERS;
    }

    public void init(ResultSet result) throws SQLException {
        int pos = 1;
        pkey        = result.getInt(pos++);
        netDevice   = result.getInt(pos++);
        identifier  = result.getString(pos++);
        description = result.getString(pos++);
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(netDevice);
        out.writeUTF          (identifier);
        out.writeNullUTF      (description);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey        = in.readCompressedInt();
        netDevice   = in.readCompressedInt();
        identifier  = in.readUTF();
        description = in.readNullUTF();
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY :       return pkey;
            case COLUMN_NET_DEVICE : return netDevice;
            case 2 :                 return identifier;
            case 3 :                 return description;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public NetDevice getNetDevice() throws SQLException, IOException {
        NetDevice nd = table.connector.getNetDevices().get(netDevice);
        if(nd==null) throw new SQLException("Unable to find NetDevice: " + netDevice);
        return nd;
    }

    /**
     * Gets the per-net device unique identifier for this reputation limiter.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Gets the optional description of the limiter.
     */
    public String getDescription() {
        return description;
    }

    public List<IpReputationLimiterLimit> getLimits() throws IOException, SQLException {
        return table.connector.getIpReputationLimiterLimits().getLimits(this);
    }

    public List<IpReputationLimiterSet> getSets() throws IOException, SQLException {
        return table.connector.getIpReputationLimiterSets().getSets(this);
    }
}
