/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2005-2013, 2016, 2017, 2018  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.IntList;
import com.aoindustries.util.InternUtils;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>PackageDefinition</code> stores one unique set of resources, limits, and prices.
 *
 * @author  AO Industries, Inc.
 */
public final class PackageDefinition extends CachedObjectIntegerKey<PackageDefinition> implements Removable {

	static final int COLUMN_PKEY=0;
	static final String COLUMN_ACCOUNTING_name = "accounting";
	static final String COLUMN_CATEGORY_name = "category";
	static final String COLUMN_MONTHLY_RATE_name = "monthly_rate";
	static final String COLUMN_NAME_name = "name";
	static final String COLUMN_VERSION_name = "version";

	Account.Name accounting;
	String category;
	String name;
	String version;
	private String display;
	private String description;
	private int setup_fee;
	private String setup_fee_transaction_type;
	private int monthly_rate;
	private String monthly_rate_transaction_type;
	private boolean active;
	private boolean approved;

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case 1: return accounting;
			case 2: return category;
			case 3: return name;
			case 4: return version;
			case 5: return display;
			case 6: return description;
			case 7: return setup_fee==-1 ? null : setup_fee;
			case 8: return setup_fee_transaction_type;
			case 9: return monthly_rate==-1 ? null : monthly_rate;
			case 10: return monthly_rate_transaction_type;
			case 11: return active;
			case 12: return approved;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	/**
	 * May be null if filtered.
	 */
	public Account getBusiness() throws IOException, SQLException {
		return table.getConnector().getAccount().getAccount().get(accounting);
	}

	public PackageCategory getPackageCategory() throws SQLException, IOException {
		PackageCategory pc=table.getConnector().getBilling().getPackageCategory().get(category);
		if(pc==null) throw new SQLException("Unable to find PackageCategory: "+category);
		return pc;
	}

	/**
	 * Gets the list of packages using this definition.
	 */
	public List<Package> getPackages() throws IOException, SQLException {
		return table.getConnector().getBilling().getPackage().getPackages(this);
	}

	public PackageDefinitionLimit getLimit(Resource resource) throws IOException, SQLException {
		if(resource==null) throw new AssertionError("resource is null");
		return table.getConnector().getBilling().getPackageDefinitionLimit().getPackageDefinitionLimit(this, resource);
	}

	public List<PackageDefinitionLimit> getLimits() throws IOException, SQLException {
		return table.getConnector().getBilling().getPackageDefinitionLimit().getPackageDefinitionLimits(this);
	}

	public void setLimits(final PackageDefinitionLimit[] limits) throws IOException, SQLException {
		table.getConnector().requestUpdate(true,
			AoservProtocol.CommandID.SET_PACKAGE_DEFINITION_LIMITS,
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeCompressedInt(limits.length);
					for(PackageDefinitionLimit limit : limits) {
						out.writeUTF(limit.resource);
						out.writeCompressedInt(limit.soft_limit);
						out.writeCompressedInt(limit.hard_limit);
						out.writeCompressedInt(limit.additional_rate);
						out.writeBoolean(limit.additional_transaction_type!=null);
						if(limit.additional_transaction_type!=null) out.writeUTF(limit.additional_transaction_type);
					}
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						invalidateList=AOServConnector.readInvalidateList(in);
					} else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unknown response code: "+code);
					}
				}

				@Override
				public void afterRelease() {
					table.getConnector().tablesUpdated(invalidateList);
				}
			}
		);
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getDisplay() {
		return display;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Gets the setup fee or <code>null</code> for none.
	 */
	public BigDecimal getSetupFee() {
		if(setup_fee==-1) return null;
		return BigDecimal.valueOf(setup_fee, 2);
	}

	public TransactionType getSetupFeeTransactionType() throws SQLException, IOException {
		if(setup_fee_transaction_type==null) return null;
		TransactionType tt=table.getConnector().getBilling().getTransactionType().get(setup_fee_transaction_type);
		if(tt==null) throw new SQLException("Unable to find TransactionType: "+setup_fee_transaction_type);
		return tt;
	}

	public BigDecimal getMonthlyRate() {
		return BigDecimal.valueOf(monthly_rate, 2);
	}

	public TransactionType getMonthlyRateTransactionType() throws SQLException, IOException {
		if(monthly_rate_transaction_type==null) return null;
		TransactionType tt=table.getConnector().getBilling().getTransactionType().get(monthly_rate_transaction_type);
		if(tt==null) throw new SQLException("Unable to find TransactionType: "+monthly_rate_transaction_type);
		return tt;
	}

	public boolean isActive() {
		return active;
	}

	public int copy() throws IOException, SQLException {
		return table.getConnector().requestIntQueryIL(true, AoservProtocol.CommandID.COPY_PACKAGE_DEFINITION, pkey);
	}

	public void setActive(boolean active) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_PACKAGE_DEFINITION_ACTIVE, pkey, active);
	}

	public boolean isApproved() {
		return approved;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.PACKAGE_DEFINITIONS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey=result.getInt(1);
			accounting=Account.Name.valueOf(result.getString(2));
			category=result.getString(3);
			name=result.getString(4);
			version=result.getString(5);
			display=result.getString(6);
			description=result.getString(7);
			String S=result.getString(8);
			setup_fee=S==null ? -1 : SQLUtility.getPennies(S);
			setup_fee_transaction_type=result.getString(9);
			monthly_rate=SQLUtility.getPennies(result.getString(10));
			monthly_rate_transaction_type=result.getString(11);
			active=result.getBoolean(12);
			approved=result.getBoolean(13);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey=in.readCompressedInt();
			accounting=Account.Name.valueOf(in.readUTF()).intern();
			category=in.readUTF().intern();
			name=in.readUTF();
			version=in.readUTF();
			display=in.readUTF();
			description=in.readUTF();
			setup_fee=in.readCompressedInt();
			setup_fee_transaction_type=InternUtils.intern(in.readNullUTF());
			monthly_rate=in.readCompressedInt();
			monthly_rate_transaction_type=InternUtils.intern(in.readNullUTF());
			active=in.readBoolean();
			approved=in.readBoolean();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public String toStringImpl() {
		return display;
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version aoservVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(accounting.toString());
		out.writeUTF(category);
		out.writeUTF(name);
		out.writeUTF(version);
		out.writeUTF(display);
		out.writeUTF(description);
		out.writeCompressedInt(setup_fee);
		out.writeNullUTF(setup_fee_transaction_type);
		out.writeCompressedInt(monthly_rate);
		out.writeNullUTF(monthly_rate_transaction_type);
		out.writeBoolean(active);
		out.writeBoolean(approved);
	}

	@Override
	public List<CannotRemoveReason<Package>> getCannotRemoveReasons() throws IOException, SQLException {
		List<CannotRemoveReason<Package>> reasons=new ArrayList<>(1);
		List<Package> packs=getPackages();
		if(!packs.isEmpty()) reasons.add(new CannotRemoveReason<>("Used by "+packs.size()+" "+(packs.size()==1?"package":"packages"), packs));
		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true,
			AoservProtocol.CommandID.REMOVE,
			Table.TableID.PACKAGE_DEFINITIONS,
			pkey
		);
	}

	public void update(
		final Account business,
		final PackageCategory category,
		final String name,
		final String version,
		final String display,
		final String description,
		final int setupFee,
		final TransactionType setupFeeTransactionType,
		final int monthlyRate,
		final TransactionType monthlyRateTransactionType
	) throws IOException, SQLException {
		table.getConnector().requestUpdate(true,
			AoservProtocol.CommandID.UPDATE_PACKAGE_DEFINITION,
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeUTF(business.getName().toString());
					out.writeUTF(category.getName());
					out.writeUTF(name);
					out.writeUTF(version);
					out.writeUTF(display);
					out.writeUTF(description);
					out.writeCompressedInt(setupFee);
					out.writeBoolean(setupFeeTransactionType!=null);
					if(setupFeeTransactionType!=null) out.writeUTF(setupFeeTransactionType.getName());
					out.writeCompressedInt(monthlyRate);
					out.writeUTF(monthlyRateTransactionType.getName());
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						invalidateList=AOServConnector.readInvalidateList(in);
					} else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unknown response code: "+code);
					}
				}

				@Override
				public void afterRelease() {
					table.getConnector().tablesUpdated(invalidateList);
				}
			}
		);
	}
}
