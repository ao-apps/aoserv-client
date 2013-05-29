package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.StringUtility;
import java.io.*;
import java.sql.*;

/**
 * A <code>DNSForbiddenZone</code> is a zone that may not be hosted by
 * AO Industries' name servers.  These domains are forbidden to protect
 * systems that require accurate name resolution for security or reliability.
 *
 * @see  DNSZone
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class DNSForbiddenZone extends GlobalObjectStringKey<DNSForbiddenZone> {

    static final int COLUMN_ZONE=0;
    static final String COLUMN_ZONE_name = "zone";

    Object getColumnImpl(int i) {
	if(i==COLUMN_ZONE) return pkey;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.DNS_FORBIDDEN_ZONES;
    }

    public String getZone() {
	return pkey;
    }

    public void init(ResultSet result) throws SQLException {
	pkey=result.getString(1);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF().intern();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeUTF(pkey);
    }
}