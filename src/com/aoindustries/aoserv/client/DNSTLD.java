package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.StringUtility;
import java.io.*;
import java.sql.*;

/**
 * A <code>DNSTLD</code> is a name server top level domain.  A top level domain
 * is a domain that is one level above that which is controlled by AO Industries'
 * name servers.  Some common examples include <code>com</code>, <code>net</code>,
 * <code>org</code>, <code>co.uk</code>, <code>aq</code> (OK - not so common), and
 * <code>med.pro</code>.  The domains added to the name servers must be in the
 * format <code>subdomain</code>.<code>dns_tld</code>, where <code>subdomain</code>
 * is a word without dots (<code>.</code>), and <code>dns_tld</code> is one of
 * the top level domains in the database.  If a top level domain does not exist
 * that properly should, please contact AO Industries to have it added.
 *
 * @see  DNSZone
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class DNSTLD extends GlobalObjectStringKey<DNSTLD> {

    static final int COLUMN_DOMAIN=0;
    static final String COLUMN_DOMAIN_name = "domain";

    private String description;

    public Object getColumn(int i) {
	if(i==COLUMN_DOMAIN) return pkey;
	if(i==1) return description;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public String getDescription() {
	return description;
    }

    public String getDomain() {
	return pkey;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.DNS_TLDS;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getString(1);
	description=result.getString(2);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF().intern();
	description=in.readUTF();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeUTF(pkey);
	out.writeUTF(description);
    }
}