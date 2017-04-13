/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2009, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.util.tree.Node;
import com.aoindustries.util.tree.Tree;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @see TicketCategory
 * @see Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketCategoryTable extends CachedTableIntegerKey<TicketCategory> {

	TicketCategoryTable(AOServConnector connector) {
		super(connector, TicketCategory.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(TicketCategory.COLUMN_PARENT_name, ASCENDING),
		new OrderBy(TicketCategory.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public TicketCategory get(int pkey) throws IOException, SQLException {
		return getUniqueRow(TicketCategory.COLUMN_PKEY, pkey);
	}

	/**
	 * Gets the list of all top-level categories that have a null parent.
	 */
	public List<TicketCategory> getTopLevelCategories() throws IOException, SQLException {
		List<TicketCategory> cached=getRows();
		List<TicketCategory> matches=new ArrayList<>();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			TicketCategory tc=cached.get(c);
			if(tc.parent==-1) matches.add(tc);
		}
		return matches;
	}

	List<TicketCategory> getChildrenCategories(TicketCategory parent) throws IOException, SQLException {
		return getIndexedRows(TicketCategory.COLUMN_PARENT, parent.pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.TICKET_CATEGORIES;
	}

	/**
	 * Gets the category with the provided parent and name.
	 * Parent may be <code>null</code> for top-level categories.
	 */
	public TicketCategory getTicketCategory(TicketCategory parent, String name) throws IOException, SQLException {
		if(parent==null) {
			// Search all top-level
			for(TicketCategory tc : getRows()) {
				if(tc.parent==-1 && tc.name.equals(name)) return tc;
			}
		} else {
			// Use indexing from above
			for(TicketCategory child : getChildrenCategories(parent)) {
				if(child.name.equals(name)) return child;
			}
		}
		return null;
	}

	/**
	 * Gets a ticket category given its dot-separated path or <code>null</code> if not found.
	 */
	public TicketCategory getTicketCategoryByDotPath(String dotPath) throws IOException, SQLException {
		for(TicketCategory category : getRows()) {
			if(category.getDotPath().equals(dotPath)) return category;
		}
		return null;
	}

	// <editor-fold defaultstate="collapsed" desc="Tree compatibility">
	private final Tree<TicketCategory> tree = new Tree<TicketCategory>() {
		@Override
		public List<Node<TicketCategory>> getRootNodes() throws IOException, SQLException {
			List<TicketCategory> topLevelCategories = getTopLevelCategories();
			int size = topLevelCategories.size();
			if(size==0) {
				return Collections.emptyList();
			} else if(size==1) {
				Node<TicketCategory> singleNode = new TicketCategoryTreeNode(topLevelCategories.get(0));
				return Collections.singletonList(singleNode);
			} else {
				List<Node<TicketCategory>> rootNodes = new ArrayList<>(size);
				for(TicketCategory topLevelCategory : topLevelCategories) rootNodes.add(new TicketCategoryTreeNode(topLevelCategory));
				return Collections.unmodifiableList(rootNodes);
			}
		}
	};

	static class TicketCategoryTreeNode implements Node<TicketCategory> {

		private final TicketCategory ticketCategory;

		TicketCategoryTreeNode(TicketCategory ticketCategory) {
			this.ticketCategory = ticketCategory;
		}

		@Override
		public List<Node<TicketCategory>> getChildren() throws IOException, SQLException {
			// Look for any existing children
			List<TicketCategory> children = ticketCategory.getChildrenCategories();
			int size = children.size();
			if(size==0) {
				// Any empty is rendered as not allowed to have children
				return null;
			} else if(size==1) {
				Node<TicketCategory> singleNode = new TicketCategoryTreeNode(children.get(0));
				return Collections.singletonList(singleNode);
			} else {
				List<Node<TicketCategory>> childNodes = new ArrayList<>(size);
				for(TicketCategory child : children) childNodes.add(new TicketCategoryTreeNode(child));
				return Collections.unmodifiableList(childNodes);
			}
		}

		@Override
		public TicketCategory getValue() {
			return ticketCategory;
		}
	}

	/**
	 * Gets a Tree view of all the accessible categories.
	 * All access to the tree read-through to the underlying storage
	 * and are thus subject to change at any time.  If you need a consistent
	 * snapshot of the tree, consider using TreeCopy.
	 *
	 * @see  TreeCopy
	 */
	public Tree<TicketCategory> getTree() {
		return tree;
	}
	// </editor-fold>
}
