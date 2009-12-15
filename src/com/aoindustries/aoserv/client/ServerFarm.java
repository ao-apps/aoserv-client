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
 * AO Industries provides greater reliability through the use of multiple network locations.
 * Each location is represented by a <code>ServerFarm</code> object.
 *
 * @author  AO Industries, Inc.
 */
final public class ServerFarm extends CachedObjectStringKey<ServerFarm> {

    static final int COLUMN_NAME=0;
    static final String COLUMN_NAME_name = "name";

    private String description;
    private String owner;
    private boolean use_restricted_smtp_port;

    @Override
    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_NAME: return pkey;
            case 1: return description;
            case 2: return owner;
            case 3: return use_restricted_smtp_port;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    /**
     * May be filtered.
     */
    public Business getOwner() throws IOException, SQLException {
        return table.connector.getBusinesses().get(owner);
    }

    public boolean useRestrictedSmtpPort() {
        return use_restricted_smtp_port;
    }

    public String getDescription() {
    	return description;
    }

    public String getName() {
    	return pkey;
    }

    @Override
    public SchemaTable.TableID getTableID() {
    	return SchemaTable.TableID.SERVER_FARMS;
    }

    @Override
    public void init(ResultSet result) throws SQLException {
        pkey = result.getString(1);
    	description = result.getString(2);
        owner = result.getString(3);
        use_restricted_smtp_port = result.getBoolean(4);
    }

    @Override
    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readUTF().intern();
        description=in.readUTF();
        owner=in.readUTF().intern();
        use_restricted_smtp_port = in.readBoolean();
    }

    @Override
    String toStringImpl(Locale userLocale) {
    	return description;
    }

    @Override
    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(pkey);
        out.writeUTF(description);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
            out.writeUTF("192.168.0.0/16");
            out.writeBoolean(false);
            out.writeUTF("mob");
        }
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_102)>=0 && version.compareTo(AOServProtocol.Version.VERSION_1_61)<=0) out.writeCompressedInt(308); // owner (package)
        if(version.compareTo(AOServProtocol.Version.VERSION_1_62)>=0) out.writeUTF(owner);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_26)>=0) out.writeBoolean(use_restricted_smtp_port);
    }
}
