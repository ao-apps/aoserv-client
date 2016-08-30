/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2013, 2016  AO Industries, Inc.
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
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.aoserv.client.validator.ValidationResult;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.util.IntList;
import com.aoindustries.util.tree.Node;
import com.aoindustries.util.tree.Tree;
import com.aoindustries.util.tree.TreeCopy;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @see  Business
 *
 * @author  AO Industries, Inc.
 */
final public class BusinessTable extends CachedTableAccountingCodeKey<Business> {

	private AccountingCode rootAccounting;

	BusinessTable(AOServConnector connector) {
		super(connector, Business.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Business.COLUMN_ACCOUNTING_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	void addBusiness(
		final AccountingCode accounting,
		final String contractNumber,
		final Server defaultServer,
		final AccountingCode parent,
		final boolean canAddBackupServers,
		final boolean canAddBusinesses,
		final boolean canSeePrices,
		final boolean billParent
	) throws IOException, SQLException {
		connector.requestUpdate(
			true,
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
					out.writeCompressedInt(SchemaTable.TableID.BUSINESSES.ordinal());
					out.writeUTF(accounting.toString());
					out.writeBoolean(contractNumber!=null);
					if(contractNumber!=null) out.writeUTF(contractNumber);
					out.writeCompressedInt(defaultServer.pkey);
					out.writeUTF(parent.toString());
					out.writeBoolean(canAddBackupServers);
					out.writeBoolean(canAddBusinesses);
					out.writeBoolean(canSeePrices);
					out.writeBoolean(billParent);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
					else {
						AOServProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public void afterRelease() {
					connector.tablesUpdated(invalidateList);
				}
			}
		);
	}

	@Override
	public void clearCache() {
		super.clearCache();
		synchronized(this) {
			rootAccounting=null;
		}
	}

	public AccountingCode generateAccountingCode(AccountingCode template) throws IOException, SQLException {
		try {
			return AccountingCode.valueOf(
				connector.requestStringQuery(
					true,
					AOServProtocol.CommandID.GENERATE_ACCOUNTING_CODE,
					template.toString()
				)
			);
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Gets one <code>Business</code> from the database.
	 */
	@Override
	public Business get(AccountingCode accounting) throws IOException, SQLException {
		return getUniqueRow(Business.COLUMN_ACCOUNTING, accounting);
	}

	List<Business> getChildBusinesses(Business business) throws IOException, SQLException {
		AccountingCode accounting=business.pkey;

		List<Business> cached=getRows();
		List<Business> matches=new ArrayList<>();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			Business bu=cached.get(c);
			if(accounting.equals(bu.parent)) matches.add(bu);
		}
		return matches;
	}

	synchronized public AccountingCode getRootAccounting() throws IOException, SQLException {
		if(rootAccounting==null) {
			try {
			   rootAccounting=AccountingCode.valueOf(connector.requestStringQuery(true, AOServProtocol.CommandID.GET_ROOT_BUSINESS));
			} catch(ValidationException e) {
				throw new IOException(e);
			}
		}
		return rootAccounting;
	}

	public Business getRootBusiness() throws IOException, SQLException {
		AccountingCode accounting=getRootAccounting();
		Business bu=get(accounting);
		if(bu==null) throw new SQLException("Unable to find Business: "+accounting);
		return bu;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.BUSINESSES;
	}

	/**
	 * Gets the list of all businesses that either have a null parent (the
	 * actual root of the business tree) or where the parent is inaccessible.
	 */
	public List<Business> getTopLevelBusinesses() throws IOException, SQLException {
		List<Business> cached=getRows();
		List<Business> matches=new ArrayList<>();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			Business bu=cached.get(c);
			if(bu.parent==null || getUniqueRow(Business.COLUMN_ACCOUNTING, bu.parent)==null) matches.add(bu);
		}
		return matches;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_BUSINESS)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_BUSINESS, args, 8, err)) {
				try {
					connector.getSimpleAOClient().addBusiness(
						AOSH.parseAccountingCode(args[1], "accounting_code"),
						args[2],
						args[3],
						AOSH.parseAccountingCode(args[4], "parent_business"),
						AOSH.parseBoolean(args[5], "can_add_backup_servers"),
						AOSH.parseBoolean(args[6], "can_add_businesses"),
						AOSH.parseBoolean(args[7], "can_see_prices"),
						AOSH.parseBoolean(args[8], "bill_parent")
					);
				} catch(IllegalArgumentException iae) {
					err.print("aosh: "+AOSHCommand.ADD_BUSINESS+": ");
					err.println(iae.getMessage());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.CANCEL_BUSINESS)) {
			if(AOSH.checkParamCount(AOSHCommand.CANCEL_BUSINESS, args, 2, err)) {
				connector.getSimpleAOClient().cancelBusiness(
					AOSH.parseAccountingCode(args[1], "accounting_code"),
					args[2]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_ACCOUNTING)) {
			if(AOSH.checkParamCount(AOSHCommand.CHECK_ACCOUNTING, args, 1, err)) {
				ValidationResult validationResult = AccountingCode.validate(args[1]);
				out.println(validationResult.isValid());
				out.flush();
				if(!validationResult.isValid()) {
					err.print("aosh: "+AOSHCommand.CHECK_ACCOUNTING+": ");
					err.println(validationResult.toString());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_BUSINESS)) {
			if(AOSH.checkParamCount(AOSHCommand.DISABLE_BUSINESS, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().disableBusiness(
						AOSH.parseAccountingCode(args[1], "accounting"),
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_BUSINESS)) {
			if(AOSH.checkParamCount(AOSHCommand.ENABLE_BUSINESS, args, 1, err)) {
				connector.getSimpleAOClient().enableBusiness(
					AOSH.parseAccountingCode(args[1], "accounting")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.GENERATE_ACCOUNTING)) {
			if(AOSH.checkParamCount(AOSHCommand.GENERATE_ACCOUNTING, args, 1, err)) {
				out.println(
					connector.getSimpleAOClient().generateAccountingCode(
						AOSH.parseAccountingCode(args[1], "template")
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.GET_ROOT_BUSINESS)) {
			if(AOSH.checkParamCount(AOSHCommand.GET_ROOT_BUSINESS, args, 0, err)) {
				out.println(connector.getSimpleAOClient().getRootBusiness());
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.IS_ACCOUNTING_AVAILABLE)) {
			if(AOSH.checkParamCount(AOSHCommand.IS_ACCOUNTING_AVAILABLE, args, 1, err)) {
				try {
					out.println(
						connector.getSimpleAOClient().isAccountingAvailable(
							AOSH.parseAccountingCode(args[1], "accounting_code")
						)
					);
					out.flush();
				} catch(IllegalArgumentException iae) {
					err.print("aosh: "+AOSHCommand.IS_ACCOUNTING_AVAILABLE+": ");
					err.println(iae.getMessage());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.MOVE_BUSINESS)) {
			if(AOSH.checkParamCount(AOSHCommand.MOVE_BUSINESS, args, 3, err)) {
				connector.getSimpleAOClient().moveBusiness(
					AOSH.parseAccountingCode(args[1], "business"),
					args[2],
					args[3],
					isInteractive?out:null
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_BUSINESS_ACCOUNTING)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_BUSINESS_ACCOUNTING, args, 2, err)) {
				connector.getSimpleAOClient().setBusinessAccounting(
					AOSH.parseAccountingCode(args[1], "old_accounting"),
					AOSH.parseAccountingCode(args[2], "new_accounting")
				);
			}
			return true;
		} else return false;
	}

	public boolean isAccountingAvailable(AccountingCode accounting) throws SQLException, IOException {
		return connector.requestBooleanQuery(
			true,
			AOServProtocol.CommandID.IS_ACCOUNTING_AVAILABLE,
			accounting.toString()
		);
	}

	// <editor-fold defaultstate="collapsed" desc="Tree compatibility">
	private final Tree<Business> tree = new Tree<Business>() {
		@Override
		public List<Node<Business>> getRootNodes() throws IOException, SQLException {
			List<Business> topLevelBusinesses = getTopLevelBusinesses();
			int size = topLevelBusinesses.size();
			if(size==0) {
				return Collections.emptyList();
			} else if(size==1) {
				Node<Business> singleNode = new BusinessTreeNode(topLevelBusinesses.get(0));
				return Collections.singletonList(singleNode);
			} else {
				List<Node<Business>> rootNodes = new ArrayList<>(size);
				for(Business topLevelBusiness : topLevelBusinesses) rootNodes.add(new BusinessTreeNode(topLevelBusiness));
				return Collections.unmodifiableList(rootNodes);
			}
		}
	};

	static class BusinessTreeNode implements Node<Business> {

		private final Business business;

		BusinessTreeNode(Business business) {
			this.business = business;
		}

		@Override
		public List<Node<Business>> getChildren() throws IOException, SQLException {
			// Look for any existing children
			List<Business> children = business.getChildBusinesses();
			int size = children.size();
			if(size==0) {
				if(business.canAddBusinesses()) {
					// Can have children but empty
					return Collections.emptyList();
				} else {
					// Not allowed to have children
					return null;
				}
			} else if(size==1) {
				Node<Business> singleNode = new BusinessTreeNode(children.get(0));
				return Collections.singletonList(singleNode);
			} else {
				List<Node<Business>> childNodes = new ArrayList<>(size);
				for(Business child : children) childNodes.add(new BusinessTreeNode(child));
				return Collections.unmodifiableList(childNodes);
			}
		}

		@Override
		public Business getValue() {
			return business;
		}
	}

	/**
	 * Gets a Tree view of all the accessible businesses.
	 * All access to the tree read-through to the underlying storage
	 * and are thus subject to change at any time.  If you need a consistent
	 * snapshot of the tree, consider using TreeCopy.
	 *
	 * @see  TreeCopy
	 */
	public Tree<Business> getTree() {
		return tree;
	}
	// </editor-fold>
}
