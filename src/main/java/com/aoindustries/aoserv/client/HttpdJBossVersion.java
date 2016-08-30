/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2009, 2016  AO Industries, Inc.
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

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An <code>HttpdJBossVersion</code> flags which
 * <code>TechnologyVersion</code>s are a version of the JBoss
 * EJB Container.  Sites configured to use JBoss are called
 * HttpdJBossSites.
 * 
 * @see  HttpdJBossSite
 * @see  TechnologyVersion
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdJBossVersion extends GlobalObjectIntegerKey<HttpdJBossVersion> {

	static final int COLUMN_VERSION=0;
	static final String COLUMN_VERSION_name = "version";

	private int tomcatVersion;
	private String templateDir;
	public static final String TECHNOLOGY_NAME="JBoss";

	public static final String
		VERSION_2_2_2="2.2.2"
	;

	public static final String DEFAULT_VERSION=VERSION_2_2_2;

	@Override
	Object getColumnImpl(int i) {
		if(i==COLUMN_VERSION) return pkey;
		if(i==1) return tomcatVersion;
		if(i==2) return templateDir;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public HttpdTomcatVersion getHttpdTomcatVersion(AOServConnector connector) throws SQLException, IOException {
		HttpdTomcatVersion obj=connector.getHttpdTomcatVersions().get(tomcatVersion);
		if(obj==null) throw new SQLException("Unable to find HttpdTomcatVersion: "+tomcatVersion);
		return obj;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_JBOSS_VERSIONS;
	}

	public TechnologyVersion getTechnologyVersion(AOServConnector connector) throws SQLException, IOException {
		TechnologyVersion obj=connector.getTechnologyVersions().get(pkey);
		if(obj==null) throw new SQLException("Unable to find TechnologyVersion: "+pkey);
		return obj;
	}

	public String getTemplateDirectory() {
		return templateDir;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		tomcatVersion=result.getInt(2);
		templateDir=result.getString(3);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		tomcatVersion=in.readCompressedInt();
		templateDir=in.readUTF();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(tomcatVersion);
		out.writeUTF(templateDir);
	}
}
