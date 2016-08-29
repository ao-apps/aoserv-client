/*
 * Copyright 2000-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The system can process several different <code>PaymentType</code>s.
 * Once processed, the amount paid is subtracted from the
 * <code>Business</code>' account as a new <code>Transaction</code>.
 *
 * @author  AO Industries, Inc.
 */
final public class PaymentType extends GlobalObjectStringKey<PaymentType> {

	static final int COLUMN_NAME=0;
	static final String COLUMN_NAME_name = "name";

	/**
	 * The system supported payment types, not all of which can
	 * be processed by AO Industries.
	 */
	public static final String
		AMEX = "amex",
		CASH = "cash",
		CHECK = "check",
		DISCOVER = "discover",
		MASTERCARD = "mastercard",
		MONEY_ORDER = "money_order",
		PAYPAL = "paypal",
		VISA = "visa",
		WIRE = "wire"
	;

	String description;

	private boolean isActive;

	private boolean allowWeb;

	public boolean allowWeb() {
		return allowWeb;
	}

	@Override
	Object getColumnImpl(int i) {
		if(i==COLUMN_NAME) return pkey;
		if(i==1) return description;
		if(i==2) return isActive;
		if(i==3) return allowWeb;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return pkey;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.PAYMENT_TYPES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getString(1);
		description = result.getString(2);
		isActive = result.getBoolean(3);
		allowWeb = result.getBoolean(4);
	}

	public boolean isActive() {
		return isActive;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF().intern();
		description=in.readUTF();
		isActive=in.readBoolean();
		allowWeb=in.readBoolean();
	}

	@Override
	String toStringImpl() {
		return description;
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey);
		out.writeUTF(description);
		out.writeBoolean(isActive);
		out.writeBoolean(allowWeb);
	}
}
