/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2005-2013, 2016, 2017, 2018, 2019  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.billing;

import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.math.SafeMath;
import com.aoindustries.util.InternUtils;
import com.aoindustries.util.i18n.Money;
import java.io.IOException;
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

	private int package_definition;
	private String resource;
	private int soft_limit;
	private int hard_limit;
	private Money additionalRate;
	String additional_transaction_type;

	public PackageDefinitionLimit() {
	}

	public PackageDefinitionLimit(
		PackageDefinition package_definition,
		Resource resource,
		int soft_limit,
		int hard_limit,
		Money additionalRate,
		TransactionType additional_transaction_type
	) {
		this.pkey = -1;
		this.package_definition = package_definition.getPkey();
		this.resource = resource.getName();
		this.soft_limit = soft_limit;
		this.hard_limit = hard_limit;
		this.additionalRate = additionalRate;
		this.additional_transaction_type = additional_transaction_type == null ? null : additional_transaction_type.getName();

		// The table is set from the connector of the package definition
		setTable(package_definition.getTable().getConnector().getBilling().getPackageDefinitionLimit());
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_PACKAGE_DEFINITION: return package_definition;
			case 2: return resource;
			case 3: return soft_limit==UNLIMITED ? null : soft_limit;
			case 4: return hard_limit==UNLIMITED ? null : hard_limit;
			case 5: return additionalRate;
			case 6: return additional_transaction_type;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public int getPackageDefinition_id() {
		return package_definition;
	}

	public PackageDefinition getPackageDefinition() throws IOException, SQLException {
		PackageDefinition pd = table.getConnector().getBilling().getPackageDefinition().get(package_definition);
		if(pd == null) throw new SQLException("Unable to find PackageDefinition: " + package_definition);
		return pd;
	}

	public String getResource_name() {
		return resource;
	}

	public Resource getResource() throws SQLException, IOException {
		Resource r=table.getConnector().getBilling().getResource().get(resource);
		if(r==null) throw new SQLException("Unable to find Resource: "+resource);
		return r;
	}

	/**
	 * Gets the soft limit or {@code null} of there is none.
	 */
	public int getSoftLimit() {
		return soft_limit;
	}

	/**
	 * Gets the soft limit and unit or {@code null} if there is none.
	 */
	public String getSoftLimitDisplayUnit() throws IOException, SQLException {
		return soft_limit==-1 ? null : getResource().getDisplayUnit(soft_limit);
	}

	/**
	 * Gets the hard limit or {@code null} of there is none.
	 */
	public int getHardLimit() {
		return hard_limit;
	}

	/**
	 * Gets the hard limit and unit or {@code null} if there is none.
	 */
	public String getHardLimitDisplayUnit() throws IOException, SQLException {
		return hard_limit==-1 ? null : getResource().getDisplayUnit(hard_limit);
	}

	/**
	 * Gets the additional rate or {@code null} if there is none.
	 */
	public Money getAdditionalRate() {
		return additionalRate;
	}

	/**
	 * Gets the additional rate per unit or {@code null} if there is none.
	 */
	public String getAdditionalRatePerUnit() throws IOException, SQLException {
		return additionalRate == null ? null : getResource().getPerUnit(additionalRate);
	}

	public TransactionType getAdditionalTransactionType() throws SQLException, IOException {
		if(additional_transaction_type==null) return null;
		TransactionType tt=table.getConnector().getBilling().getTransactionType().get(additional_transaction_type);
		if(tt==null) throw new SQLException("Unable to find TransactionType: "+additional_transaction_type);
		return tt;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.PACKAGE_DEFINITION_LIMITS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getInt("id");
		package_definition = result.getInt("package_definition");
		resource = result.getString("resource");
		soft_limit = result.getInt("soft_limit");
		if(result.wasNull()) soft_limit = UNLIMITED;
		hard_limit = result.getInt("hard_limit");
		if(result.wasNull()) hard_limit = UNLIMITED;
		additionalRate = MoneyUtil.getMoney(result, "additionalRate.currency", "additionalRate.value");
		additional_transaction_type = result.getString("additional_transaction_type");
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey = in.readCompressedInt();
		package_definition = in.readCompressedInt();
		resource = in.readUTF().intern();
		soft_limit = in.readCompressedInt();
		hard_limit = in.readCompressedInt();
		additionalRate = MoneyUtil.readNullMoney(in);
		additional_transaction_type = InternUtils.intern(in.readNullUTF());
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(package_definition);
		out.writeUTF(resource);
		out.writeCompressedInt(soft_limit);
		out.writeCompressedInt(hard_limit);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
			if(additionalRate != null && additionalRate.getCurrency() == Currency.USD && additionalRate.getScale() == 2) {
				out.writeCompressedInt(SafeMath.castInt(additionalRate.getUnscaledValue()));
			} else {
				out.writeCompressedInt(-1);
			}
		} else {
			MoneyUtil.writeNullMoney(additionalRate, out);
		}
		out.writeNullUTF(additional_transaction_type);
	}
}
