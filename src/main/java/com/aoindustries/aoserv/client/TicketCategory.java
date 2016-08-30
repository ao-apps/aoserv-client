/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2009, 2016  AO Industries, Inc.
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

import static com.aoindustries.aoserv.client.ApplicationResources.accessor;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketCategory extends CachedObjectIntegerKey<TicketCategory> {

	static final int
		COLUMN_PKEY=0,
		COLUMN_PARENT=1,
		COLUMN_NAME=2
	;
	static final String COLUMN_PKEY_name = "pkey";
	static final String COLUMN_PARENT_name = "parent";
	static final String COLUMN_NAME_name = "name";

	/**
	 * Some conveniences constants for specific categories.
	 */
	public static final int AOSERV_MASTER_PKEY = 110;

	int parent;
	String name;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_PARENT: return parent==-1 ? null : parent;
			case COLUMN_NAME: return name;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	/**
	 * Gets the parent category or <code>null</code> if this is a top-level category.
	 */
	public TicketCategory getParent() throws IOException, SQLException {
		if(parent==-1) return null;
		TicketCategory tc = table.connector.getTicketCategories().get(parent);
		if(tc==null) throw new SQLException("Unable to find TicketCategory: "+parent);
		return tc;
	}

	public String getName() {
		return name;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.TICKET_CATEGORIES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getInt(1);
		parent = result.getInt(2);
		if(result.wasNull()) parent = -1;
		name = result.getString(3);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey = in.readCompressedInt();
		parent = in.readCompressedInt();
		name = in.readUTF().intern();
	}

	private String slashPath = null;
	synchronized public String getSlashPath() throws IOException, SQLException {
		if(slashPath==null) slashPath = parent==-1 ? name : (getParent().getSlashPath()+'/'+name);
		return slashPath;
	}

	private String dotPath = null;
	synchronized public String getDotPath() throws IOException, SQLException {
		if(dotPath==null) dotPath = parent==-1 ? name : (getParent().getDotPath()+'.'+name);
		return dotPath;
	}

	@Override
	String toStringImpl() throws IOException, SQLException {
		return accessor.getMessage("TicketCategory."+getDotPath()+".toString");
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(parent);
		out.writeUTF(name);
	}

	public List<TicketBrandCategory> getTicketBrandCategorys() throws IOException, SQLException {
		return table.connector.getTicketBrandCategories().getTicketBrandCategories(this);
	}

	public List<TicketCategory> getChildrenCategories() throws IOException, SQLException {
		return table.connector.getTicketCategories().getChildrenCategories(this);
	}
}
