/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2009-2013, 2016, 2017, 2018, 2019, 2020  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.reseller;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.CachedTableAccountNameKey;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.util.tree.Node;
import com.aoindustries.util.tree.Tree;
import com.aoindustries.util.tree.TreeCopy;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @see  Brand
 *
 * @author  AO Industries, Inc.
 */
final public class BrandTable extends CachedTableAccountNameKey<Brand> {

	BrandTable(AOServConnector connector) {
		super(connector, Brand.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Brand.COLUMN_ACCOUNTING_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	/**
	 * Gets a <code>Brand</code> from the database.
	 */
	@Override
	public Brand get(Account.Name accounting) throws IOException, SQLException {
		return getUniqueRow(Brand.COLUMN_ACCOUNTING, accounting);
	}

	/**
	 * Gets a <code>Brand</code> given its business.
	 */
	public Brand getBrand(Account business) throws IOException, SQLException {
		return getUniqueRow(Brand.COLUMN_ACCOUNTING, business.getName());
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.BRANDS;
	}

	/**
	 * Gets the list of all brands that either have a null parent (the
	 * actual root of the business tree) or where the parent is inaccessible.
	 */
	public List<Brand> getTopLevelBrands() throws IOException, SQLException {
		List<Brand> matches=new ArrayList<>();
		for(Brand brand : getRows()) {
			if(brand.getParentBrand()==null) matches.add(brand);
		}
		return matches;
	}

	// <editor-fold defaultstate="collapsed" desc="Tree compatibility">
	private final Tree<Brand> tree = () -> {
		List<Brand> topLevelBrands = getTopLevelBrands();
		int size = topLevelBrands.size();
		if(size==0) {
			return Collections.emptyList();
		} else if(size==1) {
			Node<Brand> singleNode = new BrandTreeNode(topLevelBrands.get(0));
			return Collections.singletonList(singleNode);
		} else {
			List<Node<Brand>> rootNodes = new ArrayList<>(size);
			for(Brand topLevelBrand : topLevelBrands) rootNodes.add(new BrandTreeNode(topLevelBrand));
			return Collections.unmodifiableList(rootNodes);
		}
	};

	static class BrandTreeNode implements Node<Brand> {

		private final Brand brand;

		BrandTreeNode(Brand brand) {
			this.brand = brand;
		}

		/**
		 * The children of the brand are any brands that have their closest parent
		 * business (that is a brand) equal to this one.
		 */
		@Override
		public List<Node<Brand>> getChildren() throws IOException, SQLException {
			// Look for any existing children
			List<Brand> children = brand.getChildBrands();
			int size = children.size();
			if(size==0) {
				// Any brand without children is rendered as not being able to have children.
				return null;
			} else if(size==1) {
				Node<Brand> singleNode = new BrandTreeNode(children.get(0));
				return Collections.singletonList(singleNode);
			} else {
				List<Node<Brand>> childNodes = new ArrayList<>(size);
				for(Brand child : children) childNodes.add(new BrandTreeNode(child));
				return Collections.unmodifiableList(childNodes);
			}
		}

		@Override
		public Brand getValue() {
			return brand;
		}
	}

	/**
	 * Gets a Tree view of all the accessible brands.
	 * All access to the tree read-through to the underlying storage
	 * and are thus subject to change at any time.  If you need a consistent
	 * snapshot of the tree, consider using TreeCopy.
	 *
	 * @see  TreeCopy
	 */
	public Tree<Brand> getTree() {
		return tree;
	}
	// </editor-fold>
}
