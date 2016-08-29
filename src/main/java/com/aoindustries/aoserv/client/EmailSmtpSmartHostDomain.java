/*
 * Copyright 2009-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.DomainName;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides non-default per-domain smart host limits.
 *
 * @author  AO Industries, Inc.
 */
final public class EmailSmtpSmartHostDomain extends CachedObjectIntegerKey<EmailSmtpSmartHostDomain> {

	static final int
		COLUMN_PKEY = 0,
		COLUMN_SMART_HOST = 1
	;
	static final String COLUMN_SMART_HOST_name = "smart_host";
	static final String COLUMN_DOMAIN_name = "domain";

	private int smart_host;
	private DomainName domain;
	private int domain_out_burst;
	private float domain_out_rate;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_SMART_HOST: return smart_host;
			case 2: return domain;
			case 3: return domain_out_burst==-1 ? null : domain_out_burst;
			case 4: return Float.isNaN(domain_out_rate) ? null : domain_out_rate;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public EmailSmtpSmartHost getEmailSmtpSmartHost() throws SQLException, IOException {
		EmailSmtpSmartHost obj = table.connector.getEmailSmtpSmartHosts().get(smart_host);
		if(obj==null) throw new SQLException("Unable to find EmailSmtpSmartHost: "+smart_host);
		return obj;
	}

	public DomainName getDomain() {
		return domain;
	}

	/**
	 * Gets the domain-specific outbound burst limit for emails, the number of emails that may be sent before limiting occurs.
	 * A value of <code>-1</code> indicates unlimited.
	 */
	public int getDomainOutBurst() {
		return domain_out_burst;
	}

	/**
	 * Gets the domain-specific outbound sustained email rate in emails/second.
	 * A value of <code>Float.NaN</code> indicates unlimited.
	 */
	public float getDomainOutRate() {
		return domain_out_rate;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.EMAIL_SMTP_SMART_HOST_DOMAINS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			int pos = 1;
			pkey = result.getInt(pos++);
			smart_host = result.getInt(pos++);
			domain = DomainName.valueOf(result.getString(pos++));
			domain_out_burst=result.getInt(pos++);
			if(result.wasNull()) domain_out_burst = -1;
			domain_out_rate=result.getFloat(pos++);
			if(result.wasNull()) domain_out_rate = Float.NaN;
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = in.readCompressedInt();
			smart_host = in.readCompressedInt();
			domain = DomainName.valueOf(in.readUTF());
			domain_out_burst=in.readCompressedInt();
			domain_out_rate=in.readFloat();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(smart_host);
		out.writeCompressedInt(domain_out_burst);
		out.writeFloat(domain_out_rate);
	}
}
