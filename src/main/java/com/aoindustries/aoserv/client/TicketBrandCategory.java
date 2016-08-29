/*
 * Copyright 2009-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketBrandCategory extends CachedObjectIntegerKey<TicketBrandCategory> {

	static final int
		COLUMN_PKEY=0,
		COLUMN_BRAND=1,
		COLUMN_CATEGORY=2
	;
	static final String COLUMN_PKEY_name = "pkey";
	static final String COLUMN_BRAND_name = "brand";
	static final String COLUMN_CATEGORY_name = "category";

	private AccountingCode brand;
	private int category;
	private boolean enabled;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_BRAND: return brand;
			case COLUMN_CATEGORY: return category;
			case 3: return enabled;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public Brand getBrand() throws SQLException, IOException {
		Brand br = table.connector.getBrands().get(brand);
		if(br==null) throw new SQLException("Unable to find Brand: "+brand);
		return br;
	}

	public TicketCategory getCategory() throws IOException, SQLException {
		TicketCategory tc = table.connector.getTicketCategories().get(category);
		if(tc==null) throw new SQLException("Unable to find TicketCategory: "+category);
		return tc;
	}

	public boolean getEnabled() {
		return enabled;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.TICKET_BRAND_CATEGORIES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = result.getInt(1);
			brand = AccountingCode.valueOf(result.getString(2));
			category = result.getInt(3);
			enabled = result.getBoolean(4);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = in.readCompressedInt();
			brand = AccountingCode.valueOf(in.readUTF()).intern();
			category = in.readCompressedInt();
			enabled = in.readBoolean();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	String toStringImpl() {
		return brand+"|"+category+'|'+enabled;
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(brand.toString());
		out.writeCompressedInt(category);
		out.writeBoolean(enabled);
	}
}
