/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2009-2013, 2016  AO Industries, Inc.
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
import com.aoindustries.util.tree.Node;
import com.aoindustries.util.tree.Tree;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @see  Reseller
 *
 * @author  AO Industries, Inc.
 */
final public class ResellerTable extends CachedTableAccountingCodeKey<Reseller> {

	ResellerTable(AOServConnector connector) {
		super(connector, Reseller.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Reseller.COLUMN_ACCOUNTING_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	/**
	 * Gets a <code>Reseller</code> from the database.
	 */
	@Override
	public Reseller get(AccountingCode accounting) throws IOException, SQLException {
		return getUniqueRow(Reseller.COLUMN_ACCOUNTING, accounting);
	}

	/**
	 * Gets a <code>Reseller</code> given its brand.
	 */
	Reseller getReseller(Brand brand) throws IOException, SQLException {
		return getUniqueRow(Reseller.COLUMN_ACCOUNTING, brand.pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.RESELLERS;
	}
	/**
	 * Gets the list of all resellers that either have a null parent (the
	 * actual root of the business tree) or where the parent is inaccessible.
	 */
	public List<Reseller> getTopLevelResellers() throws IOException, SQLException {
		List<Reseller> matches=new ArrayList<>();
		for(Reseller reseller : getRows()) {
			if(reseller.getParentReseller()==null) matches.add(reseller);
		}
		return matches;
	}

	// <editor-fold defaultstate="collapsed" desc="Tree compatibility">
	private final Tree<Reseller> tree = new Tree<Reseller>() {
		@Override
		public List<Node<Reseller>> getRootNodes() throws IOException, SQLException {
			List<Reseller> topLevelResellers = getTopLevelResellers();
			int size = topLevelResellers.size();
			if(size==0) {
				return Collections.emptyList();
			} else if(size==1) {
				Node<Reseller> singleNode = new ResellerTreeNode(topLevelResellers.get(0));
				return Collections.singletonList(singleNode);
			} else {
				List<Node<Reseller>> rootNodes = new ArrayList<>(size);
				for(Reseller topLevelReseller : topLevelResellers) rootNodes.add(new ResellerTreeNode(topLevelReseller));
				return Collections.unmodifiableList(rootNodes);
			}
		}
	};

	static class ResellerTreeNode implements Node<Reseller> {

		private final Reseller reseller;

		ResellerTreeNode(Reseller reseller) {
			this.reseller = reseller;
		}

		/**
		 * The children of the reseller are any resellers that have their closest parent
		 * business (that is a reseller) equal to this one.
		 */
		@Override
		public List<Node<Reseller>> getChildren() throws IOException, SQLException {
			// Look for any existing children
			List<Reseller> children = reseller.getChildResellers();
			int size = children.size();
			if(size==0) {
				// Any reseller without children is rendered as not being able to have children.
				return null;
			} else if(size==1) {
				Node<Reseller> singleNode = new ResellerTreeNode(children.get(0));
				return Collections.singletonList(singleNode);
			} else {
				List<Node<Reseller>> childNodes = new ArrayList<>(size);
				for(Reseller child : children) childNodes.add(new ResellerTreeNode(child));
				return Collections.unmodifiableList(childNodes);
			}
		}

		@Override
		public Reseller getValue() {
			return reseller;
		}
	}

	/**
	 * Gets a Tree view of all the accessible resellers.
	 * All access to the tree read-through to the underlying storage
	 * and are thus subject to change at any time.  If you need a consistent
	 * snapshot of the tree, consider using TreeCopy.
	 *
	 * @see  TreeCopy
	 */
	public Tree<Reseller> getTree() {
		return tree;
	}
	// </editor-fold>
}
