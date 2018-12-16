/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.distribution;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * A <code>TechnologyName</code> represents one piece of software installed in
 * the system.
 *
 * @author  AO Industries, Inc.
 */
final public class Software extends GlobalObjectStringKey<Software> {

	static final int COLUMN_NAME = 0;
	static final String COLUMN_NAME_name = "name";

	public static final String MYSQL = "MySQL";

	public static final String PHP = "php";

	private String image_filename;
	private int image_width;
	private int image_height;
	private String image_alt;
	private String home_page_url;

	@Override
	protected Object getColumnImpl(int i) {
		if(i == COLUMN_NAME) return pkey;
		if(i == 1) return image_filename;
		if(i == 2) return image_width==-1?null:image_width;
		if(i == 3) return image_height==-1?null:image_height;
		if(i == 4) return image_alt;
		if(i == 5) return home_page_url;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public String getHomePageURL() {
		return home_page_url;
	}

	public String getImageAlt() {
		return image_alt;
	}

	public String getImageFilename() {
		return image_filename;
	}

	public int getImageHeight() {
		return image_height;
	}

	public int getImageWidth() {
		return image_width;
	}

	public String getName() {
		return pkey;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.TECHNOLOGY_NAMES;
	}

	public List<SoftwareCategorization> getTechnologies(AOServConnector connector) throws IOException, SQLException {
		return connector.getDistribution().getSoftwareCategorization().getTechnologies(this);
	}

	public SoftwareVersion getTechnologyVersion(AOServConnector connector, String version, OperatingSystemVersion osv) throws IOException, SQLException {
		return connector.getDistribution().getSoftwareVersion().getTechnologyVersion(this, version, osv);
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getString(1);
		image_filename=result.getString(2);
		image_width=result.getInt(3);
		if(result.wasNull()) image_width=-1;
		image_height=result.getInt(4);
		if(result.wasNull()) image_height=-1;
		image_alt=result.getString(5);
		home_page_url=result.getString(6);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF().intern();
		image_filename=in.readNullUTF();
		image_width=in.readCompressedInt();
		image_height=in.readCompressedInt();
		image_alt=in.readNullUTF();
		home_page_url=in.readNullUTF();
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey);
		out.writeNullUTF(image_filename);
		out.writeCompressedInt(image_width);
		out.writeCompressedInt(image_height);
		out.writeNullUTF(image_alt);
		out.writeNullUTF(home_page_url);
	}
}
