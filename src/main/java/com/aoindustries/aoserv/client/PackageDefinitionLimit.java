/*
 * Copyright 2005-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.InternUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>PackageDefinitionLimit</code> stores one limit that is part of a <code>PackageDefinition</code>.
 *
 * @see  PackageDefinition
 *
 * @author  AO Industries, Inc.
 */
public final class PackageDefinitionLimit extends CachedObjectIntegerKey<PackageDefinitionLimit> {

	static final int
		COLUMN_PKEY=0,
		COLUMN_PACKAGE_DEFINITION=1
	;
	static final String COLUMN_RESOURCE_name = "resource";
	static final String COLUMN_PACKAGE_DEFINITION_name = "package_definition";

	/**
	 * Indicates a particular value is unlimited.
	 */
	public static final int UNLIMITED=-1;

	int package_definition;
	String resource;
	int soft_limit;
	int hard_limit;
	int additional_rate;
	String additional_transaction_type;

	public PackageDefinitionLimit() {
	}

	public PackageDefinitionLimit(
		PackageDefinition package_definition,
		Resource resource,
		int soft_limit,
		int hard_limit,
		int additional_rate,
		TransactionType additional_transaction_type
	) {
		this.pkey=-1;
		this.package_definition=package_definition.pkey;
		this.resource=resource.pkey;
		this.soft_limit=soft_limit;
		this.hard_limit=hard_limit;
		this.additional_rate=additional_rate;
		this.additional_transaction_type=additional_transaction_type==null ? null : additional_transaction_type.pkey;

		// The table is set from the connector of the package definition
		setTable(package_definition.table.connector.getPackageDefinitionLimits());
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_PACKAGE_DEFINITION: return package_definition;
			case 2: return resource;
			case 3: return soft_limit==UNLIMITED ? null : soft_limit;
			case 4: return hard_limit==UNLIMITED ? null : hard_limit;
			case 5: return additional_rate==-1 ? null : additional_rate;
			case 6: return additional_transaction_type;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public PackageDefinition getPackageDefinition() throws IOException, SQLException {
		PackageDefinition pd = table.connector.getPackageDefinitions().get(package_definition);
		if(pd == null) throw new SQLException("Unable to find PackageDefinition: " + package_definition);
		return pd;
	}

	public Resource getResource() throws SQLException, IOException {
		Resource r=table.connector.getResources().get(resource);
		if(r==null) throw new SQLException("Unable to find Resource: "+resource);
		return r;
	}

	public int getSoftLimit() {
		return soft_limit;
	}

	/**
	 * Gets the soft limit and unit or <code>null</code> if there is none.
	 */
	public String getSoftLimitDisplayUnit() throws IOException, SQLException {
		return soft_limit==-1 ? null : getResource().getDisplayUnit(soft_limit);
	}

	public int getHardLimit() {
		return hard_limit;
	}

	/**
	 * Gets the hard limit and unit or <code>null</code> if there is none.
	 */
	public String getHardLimitDisplayUnit() throws IOException, SQLException {
		return hard_limit==-1 ? null : getResource().getDisplayUnit(hard_limit);
	}

	/**
	 * Gets the additional rate or <code>null</code> if there is none.
	 */
	public BigDecimal getAdditionalRate() {
		return additional_rate==-1 ? null : BigDecimal.valueOf(additional_rate, 2);
	}

	/**
	 * Gets the additional rate per unit or <code>null</code> if there is none.
	 */
	public String getAdditionalRatePerUnit() throws IOException, SQLException {
		return additional_rate==-1 ? null : '$'+getResource().getPerUnit(BigDecimal.valueOf(additional_rate, 2));
	}

	public TransactionType getAdditionalTransactionType() throws SQLException, IOException {
		if(additional_transaction_type==null) return null;
		TransactionType tt=table.connector.getTransactionTypes().get(additional_transaction_type);
		if(tt==null) throw new SQLException("Unable to find TransactionType: "+additional_transaction_type);
		return tt;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.PACKAGE_DEFINITION_LIMITS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		package_definition=result.getInt(2);
		resource=result.getString(3);
		soft_limit=result.getInt(4);
		if(result.wasNull()) soft_limit=UNLIMITED;
		hard_limit=result.getInt(5);
		if(result.wasNull()) hard_limit=UNLIMITED;
		String S=result.getString(6);
		additional_rate=S==null ? -1 : SQLUtility.getPennies(S);
		additional_transaction_type=result.getString(7);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		package_definition=in.readCompressedInt();
		resource=in.readUTF().intern();
		soft_limit=in.readCompressedInt();
		hard_limit=in.readCompressedInt();
		additional_rate=in.readCompressedInt();
		additional_transaction_type=InternUtils.intern(in.readNullUTF());
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(package_definition);
		out.writeUTF(resource);
		out.writeCompressedInt(soft_limit);
		out.writeCompressedInt(hard_limit);
		out.writeCompressedInt(additional_rate);
		out.writeNullUTF(additional_transaction_type);
	}
}